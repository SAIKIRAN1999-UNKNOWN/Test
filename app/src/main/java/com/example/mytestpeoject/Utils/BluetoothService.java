package com.example.mytestpeoject.Utils;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.mytestpeoject.MainActivity;
import com.example.mytestpeoject.USBPrinting;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothService {

    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    public static final int EXIT_CMD = -1;
    public static final int VOL_UP = 1;
    public static final int VOL_DOWN = 2;
    public static final int MOUSE_MOVE = 3;

    public BluetoothService(Context context, Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
    }

    private synchronized void setState(int state) {
        mState = state;
        mHandler.obtainMessage(USBPrinting.MESSAGE_STATE_CHANGE, state, -1)
                .sendToTarget();
    }

    public synchronized int getState() {
        return mState;
    }

    public synchronized void start() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        setState(STATE_LISTEN);
    }

    public synchronized void connect(BluetoothDevice device) {
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    public BluetoothSocket getBluetoothSocket(BluetoothDevice device) {
        BluetoothSocket socket = null;
        try {

            socket = device
                    .createRfcommSocketToServiceRecord(Constants.MY_UUID);
            Log.d("UUID", Constants.MY_UUID.toString());
        } catch (IOException e) {
        }

        return socket;
    }

    public synchronized void connected(BluetoothSocket socket,
                                       BluetoothDevice device) {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
        Message msg = mHandler.obtainMessage(USBPrinting.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();

        bundle.putString(USBPrinting.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        setState(STATE_CONNECTED);
    }

    public synchronized void stop() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        setState(STATE_NONE);
    }

    public boolean write(byte[] out) {
        ConnectedThread r;
        synchronized (this) {
            if (mState != STATE_CONNECTED)
                return false;
            r = mConnectedThread;
        }
        r.write(out);
        return true;
    }

    public boolean write(int out) {
        ConnectedThread r;
        synchronized (this) {
            if (mState != STATE_CONNECTED)
                return false;
            r = mConnectedThread;
        }
        r.write(out);
        return true;
    }

    public boolean isConnected() {
        synchronized (this) {
            if (mState != STATE_CONNECTED)
                return false;
        }
        return true;
    }

    private void connectionFailed() {
        setState(STATE_LISTEN);
        Message msg = mHandler.obtainMessage(USBPrinting.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(USBPrinting.TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    private void connectionLost() {
        // mConnectionLostCount++;
        // if (mConnectionLostCount < 3) {
        // // Send a reconnect message back to the Activity
        // Message msg = mHandler.obtainMessage(GatiMain.MESSAGE_TOAST);
        // Bundle bundle = new Bundle();
        // bundle.putString(GatiMain.TOAST,
        // "Device connection was lost. Reconnecting...");
        // msg.setData(bundle);
        // mHandler.sendMessage(msg);
        //
        // connect(mSavedDevice);
        // } else {
        setState(STATE_LISTEN);
        Message msg = mHandler.obtainMessage(USBPrinting.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(USBPrinting.TOAST, "Device connection was lost");
        msg.setData(bundle);
//        mHandler.sendMessage(msg);
        // }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            // BluetoothSocket tmp = null;
            // try {
            // tmp = device
            // .createRfcommSocketToServiceRecord(Constants.MY_UUID);
            // } catch (IOException e) {
            // }
            // mmSocket = tmp;
            mmSocket = getBluetoothSocket(device);
        }

        @Override
        public void run() {
            setName("ConnectThread");

            mAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
            } catch (IOException e) {
                connectionFailed();
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(Constants.LOG_TAG,
                            "unable to close() socket during connection failure",
                            e2);
                }
                BluetoothService.this.start();
                return;
            }

            synchronized (BluetoothService.this) {
                mConnectThread = null;
            }
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(Constants.LOG_TAG, "close() of connect socket failed", e);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(Constants.LOG_TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(Constants.LOG_TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        @Override
        public void run() {
            Log.i(Constants.LOG_TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            while (true) {
                try {
                    int bytes = mmInStream.read(buffer);
                    mHandler.obtainMessage(USBPrinting.MESSAGE_READ, bytes, -1,
                            buffer).sendToTarget();
                } catch (IOException e) {
                    Log.e(Constants.LOG_TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
            } catch (IOException e) {
                Log.e(Constants.LOG_TAG, "Exception during write", e);
            }
        }

        public void write(int out) {
            try {
                mmOutStream.write(out);
            } catch (IOException e) {
                Log.e(Constants.LOG_TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmOutStream.write(EXIT_CMD);
                mmSocket.close();
            } catch (IOException e) {
                Log.e(Constants.LOG_TAG, "close() of connect socket failed", e);
            }
        }
    }
}