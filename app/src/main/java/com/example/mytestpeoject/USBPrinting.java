package com.example.mytestpeoject;//

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mytestpeoject.Utils.BluetoothService;
import com.example.mytestpeoject.Utils.Constants;
import com.example.mytestpeoject.Utils.DataSource;
import com.example.mytestpeoject.Utils.Globals;
import com.example.mytestpeoject.Utils.Utils;
import com.example.tscdll.TSCActivity;
import com.example.tscdll.TSCUSBActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class USBPrinting extends AppCompatActivity {
    Button connect, reprint;
    public static Intent findDeviceList = null;
    LinearLayout pieceNoLayout;
    TSCUSBActivity TscUsb = new TSCUSBActivity();
    AlertDialog alert;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    UsbManager usbManager;
    UsbDevice usbDevice;
    PendingIntent mPermissionIntent;
    EditText connumber, origin, destination, pieces, fromnumber;
    public static TSCActivity TscDll = new TSCActivity();
    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;
    boolean executed = false, isSpecific = false;
    int time;
    private String conNoCommand, totalPcsCommand;
    Context context;
    RadioButton otg, bluetooth;
    Handler handler1;
    boolean tscConnected;
    TextView deviceMacAddress;
    EditText fromNumEt, toNumEt;
    boolean isOTG, isZebra, isBluetooth, isBranchValid;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    private boolean isPrinting = false;

    private static final String OTG = "OTG";
    private static final String TSC = "TSC";
    private static final String DEVICE = "bluetoothdevice";
    private DataSource dataSource;
    Timer timer;
    Handler handler;
    TimerTask timerTask;
    ProgressDialog progressDialog;

    private BluetoothService mCommandService = null;
    private String mConnectedDeviceName = null;
    private BluetoothAdapter bluetoothAdapter;
    TextView tv_printer_type;


    private Button refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usbprinting);
        context = USBPrinting.this;
        dataSource = new DataSource(context);
        otg = (RadioButton) findViewById(R.id.otg);
        Globals.printerArr = getResources().getStringArray(R.array.whichPrinterArr);
        bluetooth = (RadioButton) findViewById(R.id.bluetooth);
        refresh = (Button) findViewById(R.id.refresh);
        refresh.setVisibility(View.GONE);
        fromNumEt = (EditText) findViewById(R.id.from_piece_num);
        toNumEt = (EditText) findViewById(R.id.to_piece_num);
        deviceMacAddress = (TextView) findViewById(R.id.deviceMac);
        tv_printer_type = (TextView) findViewById(R.id.tv_type);

        if (dataSource.shardPreferences.getValue(DEVICE).length() != 0) {
            String device = dataSource.shardPreferences.getValue(DEVICE);
            Gson gson = new Gson();
            Type type = new TypeToken<BluetoothDevice>() {
            }.getType();
            BluetoothDevice bluetoothDevice = gson.fromJson(device, type);
            BluetoothDevice btDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(bluetoothDevice.getAddress());
            deviceMacAddress.setText(btDevice.getAddress());
        } else if (Boolean.parseBoolean(dataSource.shardPreferences.getValue(OTG))) {
            String name = "";
            if (Boolean.parseBoolean(dataSource.shardPreferences.getValue(TSC))) {
                name += TSC;
            } else {
                name += "Zebra";
            }
            deviceMacAddress.setText(name + "-" + OTG);
        }



        tv_printer_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isPrinting = false;
                if (dataSource.shardPreferences.getValue(DEVICE).length() != 0 || Boolean.parseBoolean(dataSource.shardPreferences.getValue(OTG))) {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle(getResources().getString(R.string.whichPrinterTitel));
                    alertDialog.setCancelable(true);
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.selected_device, null);
                    TextView deviceName, deviceMac;
                    Button chooseDevice;
                    deviceName = (TextView) view.findViewById(R.id.devicename);
                    deviceMac = (TextView) view.findViewById(R.id.devicemac);
                    chooseDevice = (Button) view.findViewById(R.id.choosedevice);
                    if (dataSource.shardPreferences.getValue(DEVICE) != null && dataSource.shardPreferences.getValue(DEVICE).length() != 0) {
                        String device = dataSource.shardPreferences.getValue(DEVICE);
                        Gson gson = new Gson();
                        Type type = new TypeToken<BluetoothDevice>() {
                        }.getType();
                        BluetoothDevice bluetoothDevice = gson.fromJson(device, type);
                        BluetoothDevice btDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(bluetoothDevice.getAddress());
                        deviceName.setText(btDevice.getName());
                        deviceMac.setText(btDevice.getAddress());
                        deviceMacAddress.setText(btDevice.getAddress());
                    } else if (Boolean.parseBoolean(dataSource.shardPreferences.getValue(OTG))) {
                        String name = "";
                        if (Boolean.parseBoolean(dataSource.shardPreferences.getValue(TSC))) {
                            name += TSC;
                        } else {
                            name += "Zebra";
                        }
                        deviceName.setText(name + "-" + OTG);
                        deviceMacAddress.setText(name + "-" + OTG);
                    }
                    alertDialog.setView(view);
                    chooseDevice.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.dismiss();
                            showPrinterOption();
                        }
                    });
                    alert = alertDialog.show();
                } else {
                    showPrinterOption();
                }

            }
        });

        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mEditor = mPrefs.edit();

        connect = (Button) findViewById(R.id.connect);
        reprint = (Button) findViewById(R.id.reprint);
        pieceNoLayout = (LinearLayout) findViewById(R.id.ll_piece_numbers);
        connumber = (EditText) findViewById(R.id.connumber);
        origin = (EditText) findViewById(R.id.origin);

        destination = (EditText) findViewById(R.id.destination);
        pieces = (EditText) findViewById(R.id.noofpcs);
        fromnumber = (EditText) findViewById(R.id.fromnumber);
        this.handler = new Handler();

        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        usbDevice = (UsbDevice) getIntent().getParcelableExtra(UsbManager.EXTRA_DEVICE);
        while (deviceIterator.hasNext()) {
            usbDevice = deviceIterator.next();
        }

        mPermissionIntent = PendingIntent.getBroadcast(USBPrinting.this, 0,
                new Intent(ACTION_USB_PERMISSION), 0);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tscConnected = false;
                isPrinting = true;
                executed = false;

                    if (isSpecific) {

                                proceedAfterValidation();

                    }

                    else {
                        proceedAfterValidation();
                    }

            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.getConnectivityStatus(context)) {

                }
            }
        });

        reprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pieceNoLayout.getVisibility() == View.GONE) {
                    pieceNoLayout.setVisibility(View.VISIBLE);
                    reprint.setText("Print Whole Series");
                    isSpecific = true;
                } else {
                    pieceNoLayout.setVisibility(View.GONE);
                    reprint.setText("Print Specific Labels");
                    fromNumEt.setText("");
                    toNumEt.setText("");
                    isSpecific = false;
                }
            }
        });


    }

    private void proceedAfterValidation() {
        if (!Boolean.parseBoolean(dataSource.shardPreferences.getValue(OTG))) {
//                            if(Globals.selectedDevice!=null) {
            String device = dataSource.shardPreferences.getValue(DEVICE);
            Gson gson = new Gson();
            Type type = new TypeToken<BluetoothDevice>() {
            }.getType();
            BluetoothDevice bluetoothDevice = gson.fromJson(device, type);
            if (bluetoothDevice != null) {
                BluetoothDevice btDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(bluetoothDevice.getAddress());
                Globals.selectedDevice = btDevice;
                Log.d("tv_test_printer", btDevice.toString());
                if (mCommandService != null && !Boolean.parseBoolean(dataSource.shardPreferences.getValue(TSC))) {
                    mCommandService.connect(Globals.selectedDevice);
                }
                checkBTState();
            }
            else {
                new AlertDialog.Builder(USBPrinting.this)
                        .setTitle(R.string.app_name)
                        .setMessage("please select a printer")
                        .setPositiveButton("OK", null).show();
            }

        } else {
            if (Boolean.parseBoolean(dataSource.shardPreferences.getValue(TSC))) {
                printOTG();
            }
        }
    }

    public void printOTG() {
        this.handler1 = new Handler();
        Log.d("conNo", StringUtils.center(connumber.getText().toString().trim(), 7));
        this.handler1.postDelayed(update, 3000);
//        progressDialog = ProgressDialog.show(context, "",
//                "Connecting to Printer");
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        if (deviceList != null) {
            Toast.makeText(context, "Connected Devices: " + deviceList.size(), Toast.LENGTH_SHORT).show();
        }
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        usbDevice = (UsbDevice) getIntent().getParcelableExtra(UsbManager.EXTRA_DEVICE);
        while (deviceIterator.hasNext()) {
//            Toast.makeText(USBPrinting.this,"in while",Toast.LENGTH_SHORT).show();
            usbDevice = deviceIterator.next();
        }
        if (usbDevice != null) {
            Toast.makeText(context, "Device detected", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Ensure that the printer is connected", Toast.LENGTH_SHORT).show();
        }

        mPermissionIntent = PendingIntent.getBroadcast(context, 0,
                new Intent(ACTION_USB_PERMISSION), 0);
        if (usbDevice != null && usbManager != null) {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (usbManager.hasPermission(usbDevice)) {
                if (!executed) {
                    executed = true;
                    TscUsb.openport(usbManager, usbDevice);
                    if (!isSpecific) {
                        for (int i = 0; i < Integer.parseInt(pieces.getText().toString()); i++) {
                            TscUsb.clearbuffer();
                            if(connumber.getText().toString().trim().length() == 2 || connumber.getText().toString().trim().length() == 4 || connumber.getText().toString().trim().length() == 6||connumber.getText().toString().trim().length() == 7) {
                                conNoCommand = "TEXT 47,100,\"ROMAN.TTF\",0,63,63,\"" + StringUtils.center(connumber.getText().toString().trim(), 7) + "\"\n";
                            } else {
                                if(connumber.getText().toString().trim().length() == 5) {
                                    conNoCommand = "TEXT 105,100,\"ROMAN.TTF\",0,63,63,\"" + StringUtils.center(connumber.getText().toString().trim(), 7) + "\"\n";
                                } else if(connumber.getText().toString().trim().length() == 3) {
                                    conNoCommand = "TEXT 155,100,\"ROMAN.TTF\",0,63,63,\"" + StringUtils.center(connumber.getText().toString().trim(), 7) + "\"\n";
                                } else {
                                    conNoCommand = "TEXT 195,100,\"ROMAN.TTF\",0,63,63,\"" + StringUtils.center(connumber.getText().toString().trim(),7 ) + "\"\n";
                                }
                            }
                            if(pieces.getText().toString().trim().length() == 3 || pieces.getText().toString().trim().length() == 1) {
                                totalPcsCommand = "TEXT 310,310,\"ROMAN.TTF\",0,12,12,\"" + StringUtils.center("" + (i + 1), 4) + "\"\n";
                            } else {
                                totalPcsCommand = "TEXT 295,310,\"ROMAN.TTF\",0,12,12,\"" + StringUtils.center("" + (i + 1), 4) + "\"\n";
                            }

                            TscUsb.sendcommandUTF8("SIZE 75 mm, 50 mm\n" +
                                    "DIRECTION 1\n" +
                                    "SPEED 10\n" +
                                    conNoCommand +
                                    "TEXT 40,295,\"ROMAN.TTF\",0,35,35,\"" + StringUtils.rightPad(origin.getText().toString().trim().toUpperCase(), 4) + "\"\n" +
                                    totalPcsCommand +
                                    "TEXT 390,295,\"ROMAN.TTF\",0,30,30,\"" + StringUtils.leftPad(pieces.getText().toString(), 4) + "\"\n" +
                                    "PRINT 1,1\n"
                            );
                        }
                    } else {
                        int fromNo = Integer.parseInt(fromNumEt.getText().toString());
                        int toNo = Integer.parseInt(toNumEt.getText().toString());
                        for (int i = fromNo; i <= toNo; i++) {
                            TscUsb.clearbuffer();
                            if(connumber.getText().toString().trim().length() == 2 || connumber.getText().toString().trim().length() == 4 || connumber.getText().toString().trim().length() == 6||connumber.getText().toString().trim().length() == 7) {
                                conNoCommand = "TEXT 47,100,\"ROMAN.TTF\",0,63,63,\"" + StringUtils.center(connumber.getText().toString().trim(), 7) + "\"\n";
                            } else {
                                if(connumber.getText().toString().trim().length() == 5) {
                                    conNoCommand = "TEXT 175,100,\"ROMAN.TTF\",0,63,63,\"" + StringUtils.center(connumber.getText().toString().trim(), 7) + "\"\n";
                                } else if(connumber.getText().toString().trim().length() == 3) {
                                    conNoCommand = "TEXT 175,100,\"ROMAN.TTF\",0,63,63,\"" + StringUtils.center(connumber.getText().toString().trim(), 7) + "\"\n";
                                } else {
                                    conNoCommand = "TEXT 195,100,\"ROMAN.TTF\",0,63,63,\"" + StringUtils.center(connumber.getText().toString().trim(), 7) + "\"\n";
                                }
                            }
                            if(pieces.getText().toString().trim().length() == 3 || pieces.getText().toString().trim().length() == 1) {
                                totalPcsCommand = "TEXT 310,310,\"ROMAN.TTF\",0,12,12,\"" + StringUtils.center("" + (i), 4) + "\"\n";
                            } else {
                                totalPcsCommand = "TEXT 295,310,\"ROMAN.TTF\",0,12,12,\"" + StringUtils.center("" + (i), 4) + "\"\n";
                            }

                            TscUsb.sendcommandUTF8("SIZE 75 mm, 50 mm\n" +
                                    "DIRECTION 1\n" +
                                    "SPEED 10\n" +
                                    conNoCommand +
                                    "TEXT 40,295,\"ROMAN.TTF\",0,35,35,\"" + StringUtils.rightPad(origin.getText().toString().trim().toUpperCase(), 4) + "\"\n" +
                                    totalPcsCommand +
                                    "TEXT 390,295,\"ROMAN.TTF\",0,30,30,\"" + StringUtils.leftPad(pieces.getText().toString(), 4) + "\"\n" +
                                    "PRINT 1,1\n"
                            );
                        }

                    }
                    pieceNoLayout.setVisibility(View.GONE);
                    reprint.setText("Print Specific Labels");
                    fromNumEt.setText("");
                    toNumEt.setText("");
                    isSpecific = false;
                    TscUsb.closeport();
//                    if(progressDialog != null) {
//                        progressDialog.dismiss();
//                        progressDialog = null;
//                    }
                }
            } else {
//                new android.app.AlertDialog.Builder(context)
//                        .setTitle(R.string.app_name)
//                        .setMessage(
//                                "Please grant permission to connect to the printer")
//                        .setNeutralButton("OK", null).show();

            }
        }
    }


    public void checkBTState() {
        String state = getResources().getString(R.string.connecting_to_printer);
        Globals.BLUETOOTH_STATE = state;
        if (isBluetoothEnabled()) {
            if (!isBluetoothDiscovering()) {

                progressDialog = ProgressDialog.show(context, "",
                        "Connecting to Printer");
                if (mCommandService != null && !Boolean.parseBoolean(dataSource.shardPreferences.getValue(TSC))) {
                    if (!mCommandService.isConnected()) {
                        Log.d("CheckBTState", "mCommandServie != null");
                        mCommandService.connect(Globals.selectedDevice);
                    }
                }
//                tscConnected=false;
                timer = new Timer();
                handler = new Handler() {
                };
                time = Constants.connAttemptsTym;
                timerTask = new TimerTask() {

                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                if (!Globals.BLUETOOTH_STATE
                                        .equalsIgnoreCase("Connecting to Printer")) {
                                    boolean deviceConnected = isDeviceConnected();
                                    Log.d("deviceConnected", "" + deviceConnected);
                                    if (deviceConnected) {
//                                        if (!tscConnected) {
//                                                if (Globals.isZebraSelected) {
                                        tscConnected = true;
                                        Globals.selectedPrint = true;
                                        Globals.isPrint = true;
                                        Globals.isPause = true;
//                                                if(Globals.isTSCSelected) {
//                                                }
                                        if (isPrinting) {
                                            testPrintLabel();
                                            isPrinting = false;
                                        }
                                        timerTask.cancel();
                                        timer.cancel();
                                        timer.purge();
//                                                }
//                                        }
                                        time += Constants.maxConnAttemptsTym;
                                    }

                                    if (time > Constants.maxConnAttemptsTym) {
                                        if (progressDialog != null) {
                                            progressDialog.dismiss();
                                            progressDialog = null;
                                        }
                                        if (mCommandService != null) {
                                            mCommandService.stop();
                                            mCommandService = null;
                                        }
                                        timer.cancel();

                                    } else
                                        time = time * 2;

                                }

                            }
                        });

                    }

                };
                timer.schedule(timerTask, Constants.PRINT_SLEEP_TIME, time);
                Globals.BLUETOOTH_STATE = context.getResources()
                        .getString(R.string.connect_devices);
            }
        }
    }

    public void testPrintLabel() {
        if (Boolean.parseBoolean(dataSource.shardPreferences.getValue(TSC)))
            printTSCLabel();
        else {
            printZebraLabel();
            if (mCommandService != null) {
                if (mCommandService.isConnected()) {
                    mCommandService.stop();
                    mCommandService = null;
                }
            }
        }

    }

    public void printTVSLabel() {
        TscDll.clearbuffer();
        TscDll.sendcommandUTF8("SIZE 70 mm, 65 mm\n" +
                "DIRECTION 1\n" +
                "SPEED 10\n" +
                "TEXT 40,50,\"3\",0,1,1,\"SPOTON LOGISTICS PRIVATE LTD\"\n" +
                "BLOCK 40,100,550,200,\"ROMAN.TTF\",0,1,1,20,2,\"Toll-Free: 1800 " +
                "200 1414, Visit us at www.spoton.co.in\"\n" +
                "BARCODE 30,300,\"128\",90,2,0,3,3,\"" + 123456789 + "\"\n" +
                "PRINT 1,1\n"
        );
        String closeport = TscDll.closeport();
        Log.d("closeport", closeport);
    }
    public void printTSCLabel() {

//        cmd = "TEXT 410,270,\"ROMAN.TTF\",0,10,10,\"" + s1 + "\"\n";
//            else
//        cmd = "TEXT 410,270,\"ROMAN.TTF\",0,15,15,\"" + s1 + "\"\n";


        TscDll.clearbuffer();
     /*  String label = "SIZE 70 mm, 65 mm\n" +
                "DIRECTION 1\n" +
                "SPEED 10\n" +
                "BOX 10,90,550,450,4,20\n" +//whole box
                "TEXT 15,430,\"ROMAN.TTF\",270,12,12,\"PACK. NO\"\n" +
                "TEXT 15,310,\"ROMAN.TTF\",270,12,12,\"" +"123456789111 " + "\"\n" +
                "BARCODE 50,410,\"128\",45,2,270,3,3,\"" + "267627768"  + "\"\n" +//vertical barcode
                "TEXT 260,320,\"ROMAN.TTF\",0,7,7,\"DELHIVERY\"\n" +
                "BARCODE 170,340,\"128\",45,2,0,3,3,\"" + "1111111" + "\"\n" +//vertical barcode
                "BAR 130,90,3,360\n" +//vertical bar beside vertical barcode
                "BARCODE 220,115,\"128\",75,2,0,3,3,\"" + "123456789012" + "\"\n" +//HORIZONTAL BARCODE
                "TEXT 140,140,\"ROMAN.TTF\",0,8,8,\"SPOTON\"\n" +
                "TEXT 140,240,\"ROMAN.TTF\",0,6,6,\"FROM\"\n" +
                "TEXT 270,240,\"ROMAN.TTF\",0,6,6,\"TO\"\n" +
                "TEXT 420,240,\"ROMAN.TTF\",0,6,6,\"PIECES\"\n" +
                "TEXT 150,270,\"ROMAN.TTF\",0,15,15,\"" + "BWDB" + "\"\n" +
                "TEXT 280,270,\"ROMAN.TTF\",0,15,15,\"" + "HYDO" + "\"\n" +
                "TEXT 410,270,\"ROMAN.TTF\",0,10,10,\"" + "con123" + "\"\n" +
//                    "TEXT 140,350,\"ROMAN.TTF\",0,6,6,\"CON. NO\"\n" +
//                    "TEXT 170,370,\"ROMAN.TTF\",0,15,15,\"" + Globals.conNumber + "\"\n" +
                "TEXT 470,370,\"ROMAN.TTF\",0,15,15,\"" + "11" + "\"\n" +
                "BAR 130,220,420,3\n" +//horizontal bar top
                "BAR 130,310,420,3\n" +//horizontal bar 2
                "BAR 260,220,3,90\n" +//vertical bar left
                "BAR 400,220,3,90\n" +//vertical bar right
                "BAR 130,410,420,3\n" +//horizontal bar bottom
                "TEXT 140,420,\"ROMAN.TTF\",0,6,6,\"Toll Free - 1800 102 1414       contactus@spoton.co.in\"\n" +
                "PRINT 1,1\n";
       */
//        String label ="! 0 200 200 500 1\r\n" +
//                "B QR 10 M 2 U 10\r\n" +
//                "MA,QR code ABC123\r\n" +
//                "ENDQR\r\n" +
//                "T 4 0 10 400 QR code ABC123\r\n" +
//                "FORM\r\n" +
//                "PRINT\r\n";
        String label="SIZE 66.5 mm, 67.9 mm\n" +
                "DIRECTION 0,0\n" +
                "REFERENCE 0,0\n" +
                "OFFSET 0 mm\n" +
                "SET PEEL OFF\n" +
                "SET CUTTER OFF\n" +
                "SET PARTIAL_CUTTER OFF\n" +
                "SET TEAR ON\n" +
                "CLS\n" +
                "CODEPAGE 1252\n" +
                "TEXT 16,105,\"0\",0,11,8,\"Shipper Detail\"\n" +
                "BOX 10,14,526,103,2\n" +

                "TEXT 16,264,\"0\",0,9,8,\"Receiver Details\"\n" +
                "BAR 7,261, 377, 2\n" +
                "BAR 5,403, 379, 2\n" +
                "TEXT 13,433,\"0\",0,7,8,\"No of Packages \"\n" +
                "TEXT 13,458,\"ROMAN.TTF\",0,1,8,\"Actual Weight\"\n" +
                "TEXT 13,481,\"ROMAN.TTF\",0,1,9,\"Cargo Value\"\n" +

                "TEXT 233,72,\"0\",0,11,9,\"DKT No-\"\n" +
                "BAR 389,112, 2, 423\n" +
                "QRCODE 400,402,L,3,A,0,M2,S7,\"https://www.gati.com/QRDktTrack.php?p=cc2ebc3565481eba1d0481ba62b0640a123456789cc2ebc3565481eba1d0481ba62b0640a\"\n" +
                "TEXT 13,407,\"ROMAN.TTF\",0,1,8,\"Basis\"\n" +
                "TEXT 164,409,\"ROMAN.TTF\",0,1,9,\":\"\n" +
                "TEXT 16,374,\"ROMAN.TTF\",0,1,8,\"T-\"\n" +
                "TEXT 12,229,\"ROMAN.TTF\",0,1,8,\"T-\"\n" +
                "TEXT 16,505,\"ROMAN.TTF\",0,1,8,\"COD\"\n" +

                "TEXT 164,455,\"ROMAN.TTF\",0,1,9,\":\"\n" +
                "TEXT 164,481,\"ROMAN.TTF\",0,1,9,\":\"\n" +
                "TEXT 165,502,\"ROMAN.TTF\",0,1,9,\":\"\n" +
                "PUTBMP 10,30,\"GH.BMP\"\n" +
                "PRINT 1,1\n";
        TscDll.sendcommandUTF8(label);
                  //  TscDll.openport(Globals.selectedDevice.getAddress());
     //   int i = 0;
//        for (int i = 0; i < 10; i++) {//Integer.parseInt(pieces.getText().toString())
//            TscDll.clearbuffer();
//         //   TscDll.sendcommandUTF8(
//
///*
//            String gati30C="SIZE 72 mm, 73.1 mm\n" +
//                    "DIRECTION 0,0\n" +
//                    "REFERENCE 0,0\n" +
//                    "OFFSET 0 mm\n" +
//                    "SET PEEL OFF\n" +
//                    "SET CUTTER OFF\n" +
//                    "SET PARTIAL_CUTTER OFF\n" +
//                    "SET TEAR ON\n" +
//                    "CLS\n" +
//                    "BITMAP 429,517,18,32,1,ÿÿÿÿÿÿÿçÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿ\u0007ÿÿÿÿÿÿÿ \u000Fÿð >\u0007\u0081Àþ\u0007ð\u0003ð\u0004\u0001þ   ÿà >\u0007\u0001À~\u000Fø\u0003ø\u0006\u0001ÿ\b  ?à <\u0006\u0001à~\u000Fø\u0007ø\u0006\u0001þ\b\u0007 ?à 8\u0006\u0001ð<\u000Fø\u0007ð\u000F\u0001€\u0018\u000Fà\u000Fÿøx\u0006\u0001ð<\u000Fð\u0007ð\u000F  0\u000Fà\u000Fÿðx\u0004 ø\u001C\u001Fð\u000Fà\u001F  p\u000Fð\u000Fà p\u0004 ø\u0018\u001Fð\u000Fà\u001F€ðp\u000Fð\u000FÀ p  ü\b\u001Fð\u001Fà\u001F€`ð\u000Fà\u000FÀ `  ü ?à\u001FÀ?€Aÿÿà\u001FÀ à€ þ ?à\u001FÀ?€CÿÿÀ?ÿàÁÀ`þ ?À\u001FÀ\u007FÀ\u0007ÿÿ€\u007FÿàÁÀ`ü \u007FÀ7€}À\u0007ïÿ ÿ€\u0001\u0081Ààø \u007FÀ  \u0001À\u000FÀ \u0003ÿ€\u0001ƒÀàð \u007F€  \u0003À\u001FÀ \u0007ÿ \u0001ƒÁàÀ ÿ€  \u0007à\u000FÀ ?ÿ \u0001‡Áó€Àÿÿÿÿÿÿÿü\u0007ÿÿÿÿÿÿü\u0001Àÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿð\u0007ÁÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÀ\u000Fƒÿÿÿÿÿÿÿÿÿÿÿÿù\u007Fà ?\u008Fÿÿÿÿÿÿÿÿÿÿÿÿü   ÿŸÿÿÿÿÿÿÿÿÿÿÿÿÿ€ \u0007ÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿø ?ÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿ\n" +
//                    "CODEPAGE 1252\n" +
//                    "TEXT 563,420,\"ROMAN.TTF\",180,1,10,\"Shipper Detail\"\n" +
//                    "TEXT 573,379,\"ROMAN.TTF\",180,1,8,\" Welpsun India P ltd- Inds estate 112\"\n" +
//                    "TEXT 573,352,\"ROMAN.TTF\",180,1,8,\" Versamedi- Anjar- Gandhidam Gujrat\"\n" +
//                    "TEXT 573,325,\"ROMAN.TTF\",180,1,8,\" Gandhidham-370201- State Gujrat\"\n" +
//                    "TEXT 573,298,\"ROMAN.TTF\",180,1,8,\" T+91 8008559787\"\n" +
//                    "TEXT 571,268,\"ROMAN.TTF\",180,1,10,\" Recevier  Detail\"\n" +
//                    "TEXT 573,235,\"ROMAN.TTF\",180,1,8,\" Welpsun India P ltd- Inds estate 112\"\n" +
//                    "TEXT 573,208,\"ROMAN.TTF\",180,1,8,\" Versamedi- Anjar- Gandhidam Gujrat\"\n" +
//                    "TEXT 573,181,\"ROMAN.TTF\",180,1,8,\" BHUJ -377201- State Gujrat\"\n" +
//                    "TEXT 573,154,\"ROMAN.TTF\",180,1,8,\" T+91 8008559787\"\n" +
//                    "TEXT 571,115,\"ROMAN.TTF\",180,1,8,\" No of Packages: 100000\"\n" +
//                    "TEXT 571,88,\"ROMAN.TTF\",180,1,8,\" Actual Weight   : 100000\"\n" +
//                    "TEXT 571,61,\"ROMAN.TTF\",180,1,8,\" Cargo Value      : 100000\"\n" +
//                    "TEXT 571,34,\"ROMAN.TTF\",180,1,8,\" \"\n" +
//                    "BAR 124,127, 452, 3\n" +
//                    "BARCODE 403,551,\"128M\",89,0,180,5,10,\"!10512345678\"\n" +
//                    "TEXT 262,456,\"ROMAN.TTF\",180,1,10,\"12345678\"\n" +
//                    "BAR 8,431, 565, 3\n" +
//                    "BAR 570,431, 3, 129\n" +
//                    "BAR 1,558, 570, 3\n" +
//                    "BAR 3,460, 3, 100\n" +
//                    "QRCODE 123,144,L,4,A,180,M2,S7,\"https://www.gati.com/track-by-docket/\"\n" +
//                    "TEXT 91,175,\"0\",90,18,24,\"SE-BHJ-GJ\"\n" +
//                    "BAR 123,0, 3, 432\n" +
//                    "BAR 3,12, 3, 451\n" +
//                    "TEXT 323,109,\"ROMAN.TTF\",180,1,7,\"FOD      :  1000\"\n" +
//                    "TEXT 323,87,\"ROMAN.TTF\",180,1,7,\"COD     : 100000\"\n" +
//                    "PRINT 1,1\n";
//            */
//
//            // String l=
////                            "BAR 20,170,450,3\n" +//HORIZONTAL LINE1
////                            "BAR 20,280,450,3\n"+ //HORIZONTAL LINE2
////                            "BAR 20,420,450,3\n" +//HORIZONTAL LINE3
////                            "BAR 250,170,3,115\n"+//VERTICAL LINE2
////                            "TEXT 30,180,\"ROMAN.TTF\",0,2,1,\"FROM:\"\n"+
////                            "TEXT 270,180,\"ROMAN.TTF\",0,2,1,\"TO:\"\n"+
////                            "TEXT 30,230,\"ROMAN.TTF\",0,2,2,\"MAAN\"\n"+
////                            "TEXT 270,230,\"ROMAN.TTF\",0,2,2,\"HYDN\"\n"+
//                          //  "BARCODE 480,450,\"128\",70,2,270,3,4,\""+"12354856"+"\"\n"+
////                            "TEXT 30,290,\"ROMAN.TTF\",0,1,1,\"DKT No:-\"\n"+
////                            "BAR 320,280,3,140\n"+//VERTICAL LINE2
//                         //   "BARCODE 30,330,\"128\",30,1,0,6,2, \""+"12354856"+"\"\n"+
////                            "TEXT 30,370,\"ROMAN.TTF\",0,1,1,\"1234567895\"\n"+
////                            "TEXT 335,290,\"ROMAN.TTF\",0,1,1,\"No of Pkgs:\"\n"+
////                            "TEXT 345,330,\"ROMAN.TTF\",0,1,1,\"1/2\"\n"+
////                            "TEXT 30,430 ,\"ROMAN.TTF\",0,1,1,\"Customer Pkg No:\"\n"+
////                          "TEXT 30,470 ,\"ROMAN.TTF\",0,1,1,\"1234568254\"\n"+
//
//
//
//
//
//            TscDll.sendcommandUTF8("SIZE 70 mm, 40 mm\n" +
//                    "DIRECTION 1\n" +
//                    "SPEED 10\n" +
//                    "BARCODE 100,55,\"128\",80,2,0,4,4,\""+1234856+i+"\"\n"+
//                    "PRINT 1,1\n"
//            );
//
//
//
//           // TscDll.sendcommandUTF8(l);
//
///*
//            String l1=
//                    "SIZE 76 mm, 101.6 mm\n" +
//                            "DIRECTION 0,0\n" +
//                            "REFERENCE 0,0\n" +
//                            "OFFSET 0 mm\n" +
//                            "SET REWIND OFF\n" +
//                            "SET PEEL OFF\n" +
//                            "SET CUTTER OFF\n" +
//                            "SET PARTIAL_CUTTER OFF\n" +
//                            "SET TEAR ON\n" +
//                            "CLS\n" +
//                            "CODEPAGE 1252\n"+
//                            "BOX 10,50,560,800,4,10\n"+
//                            "BAR 10,170,550,3 \n"+//H-L-1
//                            "BAR 10,290,550,3 \n"+//H-L-2
//                            "BAR 10,330,550,3 \n"+//H-L-3
//                            "BAR 10,660,550,3 \n"+//H-L-4
//                            //"BAR 10,570,550,3 \n"+//H-L-5
//                            "BAR 180,50,3,120 \n"+
//                            "BAR 350,50,3,120 \n"+
//                            "BAR 180,110,380,3 \n"+//H-L-1.1
//                            "BAR 290,290,3,40 \n"+//H-V-1.1
//                            "TEXT 200,70,\"ROMAN.TTF\",0,12,12,\"" +  "ORIGIN"+ "\"\n"+
//                            "TEXT 360,70,\"ROMAN.TTF\",0,12,12,\"" +  "DESTINATION"+ "\"\n"+
//                            "TEXT 200,120,\"ROMAN.TTF\",0,12,12,\"" +  "O-VAL"+ "\"\n"+
//                            "TEXT 360,120,\"ROMAN.TTF\",0,12,12,\"" +  "DES-VAL"+ "\"\n"+
//                            "BARCODE 65,185,\"128\",70,2,0,3,3,\"" + "*2106882816*" + "\"\n" +
//                            "TEXT 20,300,\"ROMAN.TTF\",0,8,8,\"" +  "REF. No :- "+ "\"\n"+
//                            "TEXT 110,300,\"ROMAN.TTF\",0,8,8,\"" +  "IJMPO2019_04"+ "\"\n"+
//                            "TEXT 300,300,\"ROMAN.TTF\",0,8,8,\"" +  "AWB No - "+ "\"\n"+
//                            "TEXT 400,300,\"ROMAN.TTF\",0,8,8,\"" +  "2106882816"+ "\"\n"+
//                            "TEXT 20,450,\"ROMAN.TTF\",0,10,10,\"" +  "TO: -"+ "\"\n"+
//                            "TEXT 20,520,\"ROMAN.TTF\",0,10,10,\"" +  "Dr pavan raghava reddy,"+ "\"\n"+
//                            "TEXT 20,560,\"ROMAN.TTF\",0,10,10,\"" +  "1-2-54 jkc nagar gujjana gundla,"+ "\"\n"+
//                            "TEXT 20,600,\"ROMAN.TTF\",0,10,10,\"" +  "GUNTUR - 522001,ANDHRA PRADESH"+ "\"\n"+
//                            "TEXT 20,700,\"ROMAN.TTF\",0,10,10,\"" +  "Ph : "+ "\"\n"+
//                            "TEXT 90,700,\"ROMAN.TTF\",0,10,10,\"" +  "7338716640"+ "\"\n"+
//                            "TEXT 270,700,\"ROMAN.TTF\",0,10,10,\"" +  "Pc.s -"+ "\"\n"+
//                            "TEXT 320,700,\"ROMAN.TTF\",0,10,10,\"" +  "1"+ "\"\n"+
//                            "TEXT 370,700,\"ROMAN.TTF\",0,10,10,\"" +  "Weight -"+ "\"\n"+
//                            "TEXT 480,700,\"ROMAN.TTF\",0,10,10,\"" +  "0.440"+ "\"\n"+
//                            "TEXT 20,750,\"ROMAN.TTF\",0,10,10,\"" +  "Email: "+ "\"\n"+
//                            "TEXT 100,750,\"ROMAN.TTF\",0,10,10,\"" +  "0"+ "\"\n"+
//                            "TEXT 320,750,\"ROMAN.TTF\",0,10,10,\"" +  "Declared Value: "+ "\"\n"+
//                            "TEXT 530,750,\"ROMAN.TTF\",0,10,10,\"" +  "1"+ "\"\n"+
////                            "TEXT 20,590,\"ROMAN.TTF\",0,10,10,\"" +  "FROM/RETURN Details :"+ "\"\n"+
////                            "TEXT 20,630,\"ROMAN.TTF\",0,10,10,\"" +  "MR CHETAN DARNE"+ "\"\n"+
////                            "TEXT 20,660,\"ROMAN.TTF\",0,10,10,\"" +  "Wolterskluwer pvt limited, "+ "\"\n"+
////                            "TEXT 20,690,\"ROMAN.TTF\",0,10,10,\"" +  "A/202, 2ND FLOOR THE QUBE,MAROL VILLAGE"+ "\"\n"+
////                            "TEXT 20,720,\"ROMAN.TTF\",0,10,10,\"" +  "ANDHERI (E) "+ "\"\n"+
////                            "TEXT 20,750,\"ROMAN.TTF\",0,10,10,\"" +  "MUMBAI - 400059,MAHARASHTR "+ "\"\n"+
//
//
//
//
//
//
//
//
//
//
//
//*/
//
//
//
////                            "LINE 10 15 10 900 3\r\n"+//Vertical Line 1
////                    "LINE 150 15 150 135 3\r\n" + //vertical line beside origin
////                    "LINE 370 15 370 135 3\r\n" +//vertical line beside destination
////                    "LINE 300 260 300 300 3\r\n"+//vertical line 3
////                    "LINE 10 15 550 15 3\r\n"+//horizontal ba top
////                    "LINE 10 135 550 135 3\r\n" +//horizontal bar 1
////                    "LINE 10 260 550 260 3\r\n" +//horizontal bar 2
////                    "LINE 10 300 550 300 3\r\n" +//horizontal bar 3
////                    "LINE 10 500 550 500 3\r\n" +//horizontal bar 4
////                    "LINE 10 600 550 600 3\r\n" +//horizontal bar 5
////                    "LINE 10 900 550 900 3\r\n" +//horizontal bar 6
////                    "LINE 150 70 550 70 3\r\n"+//horizontal line below origin and destination
////                    "BT 0 3 8\r\nB 128 2 0 75 75 150 " + "300002401"+ "\r\nBT OFF\r\n" + //horizontal barcode//
////                    "T 5 0 220 20 ORIGIN\r\n" +//Q
////                    "T 5 0 380 20 DESTINATION\r\n" +//Q
////                    "T 7 0 220 90 MUMBAI \r\n" +//A
////                    "T 7 0 390 90 GUNTUR \r\n" +//A
////                    "T 5 0 12 265 REF. No :- \r\n" +//Q
////                    "T 7 0 150 265 IJMPO2019_04 :- \r\n" +//A
////                    "T 5 0 305 265 AWB.No:\r\n" +//Q
////                    "T 7 0 420 265 2106882816\r\n" +//A
////                    "T 5 0 12 320 TO: -\r\n"+//Q
////                    "T 7 0 14 350"+"Dr pavan raghava reddy,"+"\r\n"+//A
////                    "T 7 0 14 400 1-2-54 jkc nagar gujjana gundla, "+"\r\n"+//A
////                    "T 7 0 14 450"+" GUNTUR - 522001,ANDHRA PRADESH"+"\r\n"+//A
////
////                    "T 5 0 12 530 Ph : \r\n" +//Q
////                    "T 7 0 70 530 7338716640\r\n" +//A
////                    "T 5 0 350 530 Weight - \r\n" +//Q
////                    "T 7 0 450 530 0.440 \r\n" +//A
////
////                    "T 5 0 230 530 Pc.s -\r\n" +//Q
////                    "T 7 0 300 530 1\r\n" +//A
////                    "T 5 0 12 570 Email: \r\n" +//Q
////                    "T 7 0 100 570 0\r\n" +//A
////                    "T 5 0 250 570 Declared Value :  \r\n" +//Q
////                    "T 7 0 450 570 1 \r\n" +//A
////                    "T 5 0 12 615 FROM/RETURN Details :\r\n"+//Q
////
////                    "T 7 0 14 655"+"MR CHETAN DARNE"+"\r\n"+//A
////                    "T 7 0 14 705"+"Wolterskluwer pvt limited, "+"\r\n"+//A
////                    "T 7 0 14 755"+"A/202, 2ND FLOOR THE QUBE,MAROL VILLAGE "+"\r\n"+//A
////                    "T 7 0 14 805"+"ANDHERI (E) "+"\r\n"+//A
////                    "T 7 0 14 855"+"MUMBAI - 400059,MAHARASHTRA"+"\r\n"+//A
////
//  //                          "PRINT 1,1\n";
////"T 5 0 12 630"+toadd+"\r\n"+//A
//            // "T 7 0 12 570 Ph : \r\n" +//Q
//            // "ML 47 TEXT 0 12 345 "+toadd+"\r\nEND ML\r\n"+//TODO
//
//
//          //  print(l.getBytes(StandardCharsets.UTF_8));
//
////                "TEXT 10,10,\"ROMAN.TTF\",0,8,8,\"INVOICE\"\n" +
////                "TEXT 480,10,\"ROMAN.TTF\",0,8,8,\"Invoice Dt: " + "31/03/18" + "\"\n" +
////                "TEXT 10,30,\"ROMAN.TTF\",0,10,10,\"SPOTON LOGISTICS PVT LTD\"\n" +
////                "TEXT 400,30,\"ROMAN.TTF\",0,8,8,\"Invoice No: " + "1796009867" + "\"\n" +
////                "TEXT 10,60,\"ROMAN.TTF\",0,8,8,\"SPOTON GSTIN-STATE:" + "36AAQCS5845Q1Z9/TS-LOS\"\n" +
////                "BOX 10,90,130,150,3\n" +
////                "TEXT 20,100,\"ROMAN.TTF\",0,12,12,\"TS\"\n" +
////                "BOX 45,90,45,150,3\n" +
////                "TEXT 70,140,\"ROMAN.TTF\",0,10,10,\"to\"\n" +
////                "BOX 90,90,90,150,3\n" +
////                "TEXT 100,140,\"ROMAN.TTF\",0,10,10,\"TN\"\n" +
////                "TEXT 140,140,\"ROMAN.TTF\",0,10,10,\"" + "30/03/18" + "\"\n" +
////                "TEXT 230,140,\"ROMAN.TTF\",0,10,10,\"Amt: " + "887.31" + "\"\n"+
////                            "PRINT 1,1\n"
////                        +
////                "BOX 10,160,600,"+(160+(noOfCons*50))+",3\n" +
////                consPrint
//       // );
////
////            String label = "! 0 200 200 525 1\r\nON-OUT-OF-PAPER WAIT 400\r\nPW 551\r\n";
////            label += "TONE 0\r\nSPEED 5\r\nON-FEED IGNORE\r\nNO-PACE\r\n";
////            label += "LABEL\r\nCONTRAST 0\r\nPCX 2 29 !<GLOGO.pcx";
////            label += "BAR-SENSE\r\nLINE 2 176 440 176 3\r\nLINE 2 382 440 382 3\r\nLINE 2 267 440 267 3\r\n";
////
////            label += "B 128 2 0 100 235 42 " +430232237//BT 0 3 8
////                    + "\r\nBT OFF\r\n";
////            label+="T 0 3 310 150 "+430232237+" \r\n";
////
////            label += "LINE 219 176 219 266 3 \r\n";
////            label+= "T 5 0 4 181 FROM:\r\nT 4 1 26 185 " +"MAAN"
////                    + "\r\n";
////            label += "T 5 0 228 181 TO:\r\nT 4 1 279 185 " + "LKO"
////                    + "\r\n";
////
////            label += "VB 128 2 0 70 448 478 " +  430232237//BT 0 3 5
////                    + "\r\nBT OFF\r\n";
////            label+="VT 0 3 520 390 "+430232237+" \r\n";
////
////            label += "T 5 0 4 274 Dkt No:\r\n" +//4 1 12 294
////                    "B 128 1 0 25 12 310 " + 200874145//BT 0 5 8
////                    + "\r\nBT OFF\n";
////            label+="T 0 3 40 350 "+200874145+" \r\n";
////
////            label += "T 5 0 295 274 No of Pkgs:\r\nT 4 1 340 294 "
////                    + 1+ "\r\n";//docketListDatum.getNoOfPkgs()
////        /*label += "T 5 0 6 389 Add:\r\nT 4 1 12 402 "
////                + singleDocketDetails.getAssuredDlyDate() + "\r\n";
////        */
////            //        label += "T 5 0 6 389 Customer Pkg No:\r\nT 4 1 12 402 "
//////                + packageDetailsDB.getCustPkgNo() + "\r\n";
////            label += "T 5 0 6 389 Customer Pkg No:\r\nT 0 5 25 420" +
////                    " "
////                    + 200874145+"\r\n";//""+singleDocketDetails.getCustPkgNo()+
////            label += "LINE 280 268 280 382 3\r\n" ;
////            //  + "LINE 227 381 227 487 3\r\n";
//////        label += "T 5 0 234 389 Type:\r\nT 4 1 336 400 " + type
//////                + "\r\n";
////
////            label += "PRINT\r\n";
////
////
////            String label = "! 0 200 200 600 1\r\nON-OUT-OF-PAPER WAIT 400\r\nPW 551\r\n";
////            label += "TONE 0\r\nSPEED 5\r\nON-FEED IGNORE\r\nNO-PACE\r\n";
////            label += "LABEL\r\nCONTRAST 0\r\nPCX 2 29 !<GLOGO.pcx";
////            label += "BAR-SENSE\r\nLINE 2 176 440 176 3\r\nLINE 2 382 440 382 3\r\nLINE 2 267 440 267 3\r\n";
////            label += "B 128 2 0 100 200 42 " + 2003787289//BT 0 3 8
////                    + "\r\nBT OFF\r\n";
////            label+="T 0 3 280 150 "+2003787289+" \r\n";
////
////
////
////
////            label += "LINE 219 176 219 266 3 \r\n";
////            label+= "T 5 0 4 181 FROM:\r\nT 4 1 26 185 " +"maan"
////                    + "\r\n";
////            label += "T 5 0 228 181 TO:\r\nT 4 1 279 185 " + "hyd"
////                    + "\r\n";
////            label += "VB 128 2 0 70 448 458 " +  2003787289//BT 0 3 5
////                    + "\r\nBT OFF\r\n";
////            label+="VT 0 3 525 380 "+2003787289+" \r\n";
////            label += "T 5 0 4 274 Dkt No:\r\n" +//4 1 12 294
////                    "B 128 1 0 25 12 310 " + 2003787289//BT 0 5 8
////                    + "\r\nBT OFF\n";
////            label+="T 0 3 40 350 "+2003787289+" \r\n";
////            label += "T 5 0 295 274 No of Pkgs:\r\nT 4 1 340 294 "
////                    + i+ "\r\n";//docketListDatum.getNoOfPkgs()
////        /*label += "T 5 0 6 389 Add:\r\nT 4 1 12 402 "
////                + singleDocketDetails.getAssuredDlyDate() + "\r\n";
////        */
////            //        label += "T 5 0 6 389 Customer Pkg No:\r\nT 4 1 12 402 "
//////                + packageDetailsDB.getCustPkgNo() + "\r\n";
////            label += "T 5 0 6 389 Customer Pkg No:\r\nT 0 5 25 420" +
////                    " "
////                    + 200874145+"\r\n";//""+singleDocketDetails.getCustPkgNo()+
////            label += "LINE 280 268 280 390 3\r\n" ;
////            //  + "LINE 227 381 227 487 3\r\n";
////            label += "T 4 1 336 450 "
////                    + "\r\n";
////
////
////
////             label +="SETFF 25 2.5 \r\n";
////            label += "SET-TOF 0 \r\n";
////            label += "PRINT\r\n";
//
////
////            String label1 = "! 0 200 200 600 1\r\nON-OUT-OF-PAPER WAIT 400\r\nPW 551\r\n";
////            label1 += "TONE 0\r\nSPEED 5\r\nON-FEED IGNORE\r\nNO-PACE\r\n";
////            label1 += "LABEL\r\nCONTRAST 0\r\nPCX 2 29 !<GLOGO.pcx";
////            label1 += "BAR-SENSE\r\n" ;
////                 //   "LINE 2 176 440 176 3\r\nLINE 2 382 440 382 3\r\nLINE 2 267 440 267 3\r\n";
////            label1 += "B 128 2 0 100 200 42 " + 2003944299//BT 0 3 8
////                    + "\r\nBT OFF\r\n";
//
//            //label1+="T 0 3 280 150 "+2003944299+" \r\n";
//////            label1 += "BT 128 2 0 200 10 42 " + 2003944299//BT 0 3 8
//////                    + "\r\nBT OFF\r\n";
////
////
////
////            label += "LINE 219 176 219 266 3 \r\n";
////            label+= "T 5 0 4 181 FROM:\r\nT 4 1 26 185 " +"maan"
////                    + "\r\n";
////            label += "T 5 0 228 181 TO:\r\nT 4 1 279 185 " + "hyd"
////                    + "\r\n";
////            label += "VB 128 2 0 70 448 458 " +  2003787289//BT 0 3 5
////                    + "\r\nBT OFF\r\n";
////            label+="VT 0 3 525 380 "+2003787289+" \r\n";
////            label += "T 5 0 4 274 Dkt No:\r\n" +//4 1 12 294
////                    "B 128 1 0 25 12 310 " + 2003787289//BT 0 5 8
////                    + "\r\nBT OFF\n";
////            label+="T 0 3 40 350 "+2003787289+" \r\n";
////            label += "T 5 0 295 274 No of Pkgs:\r\nT 4 1 340 294 "
////                    + i+ "\r\n";//docketListDatum.getNoOfPkgs()
////        /*label += "T 5 0 6 389 Add:\r\nT 4 1 12 402 "
////                + singleDocketDetails.getAssuredDlyDate() + "\r\n";
////        */
////            //        label += "T 5 0 6 389 Customer Pkg No:\r\nT 4 1 12 402 "
//////                + packageDetailsDB.getCustPkgNo() + "\r\n";
////            label += "T 5 0 6 389 Customer Pkg No:\r\nT 0 5 25 420" +
////                    " "
////                    + 200874145+"\r\n";//""+singleDocketDetails.getCustPkgNo()+
////            label += "LINE 280 268 280 390 3\r\n" ;
////            //  + "LINE 227 381 227 487 3\r\n";
////            label += "T 4 1 336 450 "
////                    + "\r\n";
////
////
////
////            label1 +="SETFF 25 2.5 \r\n";
////            label1 += "SET-TOF 0 \r\n";
////            label1 += "PRINT\r\n";
//
///*
//            String label1 = "! 0 200 200 545 1\r\nON-OUT-OF-PAPER WAIT 400\r\nPW 551\r\n";
//            label += "TONE 0\r\nSPEED 5\r\nON-FEED IGNORE\r\nNO-PACE\r\n";
//            label += "LABEL\r\nCONTRAST 0\r\nPCX 2 29 !<GLOGO.pcx";
//            label += "BAR-SENSE\r\nLINE 2 126 440 126 3\r\nLINE 2 332 440 332 3\r\nLINE 2 217 440 217 3\r\n";
//
//            label += "B 128 2 0 70 200 20 " + 430232237//BT 0 3 8
//                    + "\r\nBT OFF\r\n";
//            label+="T 0 3 310 100 "+430232237+" \r\n";
//
//            label += "LINE 219 126 219 216 3 \r\n";
//            label+= "T 5 0 4 131 FROM:\r\nT 4 1 26 135 " +"MAAN"
//                    + "\r\n";
//            label += "T 5 0 228 131 TO:\r\nT 4 1 279 135 " + "CRU"
//                    + "\r\n";
//
//            label += "VB 128 2 0 70 448 428 " +  430232237//BT 0 3 5
//                    + "\r\nBT OFF\r\n";
//            label+="VT 0 3 520 340 "+430232237+" \r\n";
//
//            label += "T 5 0 4 224 Dkt No:\r\n" +//4 1 12 294
//                    "B 128 1 0 25 12 260 " +430232237//BT 0 5 8
//                    + "\r\nBT OFF\n";
//            label+="T 0 3 40 300 "+430232237+" \r\n";
//
//            label += "T 5 0 295 224 No of Pkgs:\r\nT 4 1 340 244 "
//
//            label += "T 5 0 6 339 Customer Pkg No:\r\nT 0 5 25 370" +
//                    " "
//                    + 430232237+"\r\n";//""+singleDocketDetails.getCustPkgNo()+
//            label += "LINE 280 218 280 352 3\r\n" ;
//
//*/
//
///*********************************************kranthi original todo**********************************************************************/
////            TscDll.sendcommandUTF8("SIZE 70 mm, 65 mm\n" +
////                    "DIRECTION 1\n" +
////                    "SPEED 10\n" +
////                    "BOX 10,100,563,630,4,20\n" +//whole box
////                    "TEXT 140,140,\"ROMAN.TTF\",0,8,8,\"" + Constants.LOG_TAG + "\"\n" +
////                    "TEXT 140,240,\"ROMAN.TTF\",0,6,6,\"FROM\"\n" +
////                    "TEXT 270,240,\"ROMAN.TTF\",0,6,6,\"TO\"\n" +
////                    "TEXT 420,240,\"ROMAN.TTF\",0,6,6,\"PIECES\"\n" +
////                    "BAR 130,100,3,350\n" +//vertical bar beside vertical barcode
////                    "TEXT 135,350,\"ROMAN.TTF\",0,6,6,\"CON. NO\"\n" +
////                    "BAR 130,220,420,3\n" +//horizontal bar top
////                    "BAR 130,340,420,3\n" +//horizontal bar 2
////                    "BAR 260,220,3,120\n" +//vertical bar left
////                    "BAR 400,220,3,120\n" +//vertical bar right
////                    "BAR 130,410,420,3\n" +//horizontal bar bottom
////                    "BARCODE 30,430,\"128\",70,2,270,3,3,\"" + (123469) + "\"\n" +//vertical barcode//Integer.parseInt(fromnumber.getText().toString()) + i
////                    "BARCODE 220,115,\"128\",75,2,\"ROMAN.TTF\",3,3,\"" + (12345679) + "\"\n" +//HORIZONTAL BARCODE//Integer.parseInt(fromnumber.getText().toString()) + i
////                    "TEXT 160,280,\"ROMAN.TTF\",0,15,15,\"" +  "hydh"+ "\"\n" +//origin.getText().toString().trim().toUpperCase()
////                    "TEXT 280,280,\"ROMAN.TTF\",0,15,15,\"" +"ibp"  + "\"\n" +//destination.getText().toString().trim()
////                    "TEXT 420,280,\"ROMAN.TTF\",0,15,15,\"" + 1 + "\"\n" +//(i + 1) + "/" + Integer.parseInt(pieces.getText().toString())
////                    "TEXT 170,370,\"ROMAN.TTF\",0,12,12,\"" + "conn" + "\"\n" +//connumber.getText().toString().trim()
//////                "BAR 220,100,3,120\n" +//vertical bar beside spoton text
////                    "TEXT 140,420,\"ROMAN.TTF\",0,6,6,\"Toll Free - 18002001414       contactus@kranti.co.in\"\n" +
////                    "PRINT 1,1\n");
//
//            /*------------------PUD INVOICE SAMPLE-----------------------*/
//            //        int noOfCons = 5;
////        String consPrint = "TEXT 60,170,\"ROMAN.TTF\",0,10,10,\"Con No\"\n" +
////                "TEXT 160,170,\"ROMAN.TTF\",0,10,10,\"Wt\"\n" +
////                "TEXT 220,170,\"ROMAN.TTF\",0,10,10,\"FRate\"\n";
////        Random rand = new Random();
////        for (int k = 1; k <= noOfCons; k++) {
////            consPrint += "TEXT 20," + (170 + (k * 50)) + ",\"ROMAN.TTF\",0,10,10\""
////                    + (rand.nextInt(900000000) + 100000000) + "\"\n" +
////                    "TEXT 150," + (170 + (k * 50)) + ",\"ROMAN.TTF\",0,10,10,\"" + (rand.nextInt(90) + 10) + "\"\n" +
////                    "TEXT 210," + (170 + (k * 50)) + ",\"ROMAN.TTF\",0,10,10," + "\"2.5\"\n";
////        }
////        String command = "SIZE 70 mm, 65 mm\n" +
////                "DIRECTION 0\n" +
////                "SPEED 10\n" +
////                "TEXT 10,10,\"ROMAN.TTF\",0,8,8,\"INVOICE\"\n" +
////                "TEXT 360,10,\"ROMAN.TTF\",0,8,8,\"Invoice Dt: " + "31/03/18" + "\"\n" +
////                "TEXT 10,30,\"ROMAN.TTF\",0,10,10,\"SPOTON LOGISTICS PVT LTD\"\n" +
////                "TEXT 360,30,\"ROMAN.TTF\",0,8,8,\"Invoice No: " + "1796009867" + "\"\n" +
////                "TEXT 10,60,\"ROMAN.TTF\",0,8,8,\"SPOTON GSTIN-STATE: " + "36AAQCS5845Q1Z9/TS-LOS\"\n" +
////                "BOX 10,90,140,150,3\n" +
////                "TEXT 20,100,\"ROMAN.TTF\",0,12,12,\"TS\"\n" +
////                "BOX 50,90,48,150,3\n" +
////                "TEXT 65,100,\"ROMAN.TTF\",0,10,10,\"to\"\n" +
////                "BOX 90,90,93,150,3\n" +
////                "TEXT 100,100,\"ROMAN.TTF\",0,12,12,\"TN\"\n" +
////                "TEXT 195,100,\"ROMAN.TTF\",0,10,10,\"" + "30/03/18" + "\"\n" +
////                "TEXT 300,100,\"ROMAN.TTF\",0,10,10,\"Amt: " + "887.31" + "\"\n" +
////                "BOX 10,160,550," + (160 + (noOfCons * 50)) + ",3\n" +
////                consPrint +
////                "PRINT 1,1\n";
////        Log.d("Command",command);
//      //  TscDll.sendcommandUTF8(l);
//
//
//
//
//
//
//            try {
//                Thread.sleep(Constants.PRINT_SLEEP_TIME);
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
            //        }
        String closeport = TscDll.closeport();
        Log.d("closeport", closeport);
    }
    
    private void printZebraLabel() {
     //   for (int i = 0; i < Integer.parseInt(pieces.getText().toString()); i++) {
            //            String label = "! 0 200 200 515 1\r\nON-OUT-OF-PAPER WAIT 400\r\nPW 551\r\n" +
//                    "TONE 0\r\nSPEED 5\r\nON-FEED IGNORE\r\nNO-PACE\r\n" +
//                    "LABEL\r\nCONTRAST 0\r\nJOURNAL\r\n" +
//                    "BOX 10 15 550 365 4 20\r\n" +//whole box
//                    "T 7 0 140 55 " + Constants.LOG_TAG + "\r\n" +
////                    "LINE 220 100 220 220 1\r\n" +//vertical bar beside spoton text
//                    "LINE 130 135 550 135 1\r\n" +//horizontal bar top
//                    "LINE 130 255 550 255 1\r\n" +//horizontal bar 2
//                    "LINE 260 135 260 255 1\r\n" +//vertical bar left
//                    "LINE 400 135 400 255 1\r\n" +//vertical bar right
//                    "LINE 130 325 550 325 1\r\n" +
//
//                    "T 7 0 140 265 CON. No\r\n" + //replace with con number
//                    "T 5 2 170 285 " + connumber.getText().toString().trim() + "\r\n" + //Globals.conNumber
//                    "LINE 130 15 130 365 1\r\n" + //vertical line beside vertical barcode
//                    "T 7 0 140 155 FROM\r\n" +
//                    "T 5 2 140 195 " + origin.getText().toString().trim().toUpperCase() + "\r\n" + //replace with origin Globals.origin
//                    "T 7 0 270 155 To\r\n" +
//                    "T 5 2 270 195 " + destination.getText().toString().trim() + "\r\n" + //replace with dest Globals.destination
//                    "T 7 0 420 155 PIECES\r\n" +
//                    "T 5 2 420 195 " + (i + 1) + "/" + Integer.parseInt(pieces.getText().toString()) + "\r\n" + //replace with piece/no. of pieces //(i + 1) + "/" + numOfPackages
//                    "BT 0 3 8\r\nB 128 2 0 75 220 30 " + (Integer.parseInt(fromnumber.getText().toString()) + i) + "\r\nBT OFF\r\n" + //horizontal barcode //(d.getFromNo() + i)
//                    "T 0 2 140 335 Toll-Free:18002001414 contactus @www.kranti.co.in\r\n" +
//                    "BT 0 3 8\r\nVB 128 2 0 70 30 345 " + (Integer.parseInt(fromnumber.getText().toString()) + i) + "\r\nBT OFF\r\n" + //vertical barcode //(d.getFromNo() + i)
//                    "PRINT\r\n";
//            Log.d(Constants.LOG_TAG, label);
//            print(label.getBytes());

/*******************************************gati label***********************************************************************/

//            String label = "! 0 200 200 525 1\r\nON-OUT-OF-PAPER WAIT 400\r\nPW 551\r\n";
//            label += "TONE 0\r\nSPEED 5\r\nON-FEED IGNORE\r\nNO-PACE\r\n";
//            label += "LABEL\r\nCONTRAST 0\r\nPCX 2 29 !<GLOGO.pcx\r\n";
//            label+=  "BOX 10 15 550 365 4 20\\r\\n ";
//            label += "BAR-SENSE\r\nLINE 2 176 440 176 3\r\nLINE 2 382 440 382 3\r\nLINE 2 267 440 267 3\r\n";
//            label += "BT 0 3 8\r\nB 128 2 0 100 235 42 " + 439204769
//                    + "\r\nBT OFF\r\n";
//            label += "LINE 219 176 219 266 3 \r\n";
//            label+= "T 5 0 4 181 FROM:\r\nT 4 1 26 185 " + "MAAN"
//                    + "\r\n";
//            label += "T 5 0 228 181 TO:\r\nT 4 1 279 185 " + "HYAN"
//                    + "\r\n";
//            label += "BT 0 3 5\r\nVB 128 2 0 70 448 478 " + 439204769
//                    + "\r\nBT OFF\r\n";
//            label += "T 5 0 4 274 Dkt No:\r\n" +//4 1 12 294
//                    "BT 0 5 8\r\nB 128 1 0 25 12 310 " + 200788930
//                    + "\r\nBT OFF\n";
//            label += "T 5 0 295 274 No of Pkgs:\r\nT 4 1 340 294 "
//                    + 2+ "\r\n";
//        /*label += "T 5 0 6 389 Add:\r\nT 4 1 12 402 "
//                + singleDocketDetails.getAssuredDlyDate() + "\r\n";
//        */
//            //        label += "T 5 0 6 389 Customer Pkg No:\r\nT 4 1 12 402 "
////                + packageDetailsDB.getCustPkgNo() + "\r\n";
//            label += "T 5 0 6 389 Customer Pkg No:\r\nT 0 5 25 420" +
//                    " "
//                    + 2003690235 +"\r\n";//""+singleDocketDetails.getCustPkgNo()+
//            label += "LINE 280 268 280 382 3\r\n" ;
//            //  + "LINE 227 381 227 487 3\r\n";
////        label += "T 5 0 234 389 Type:\r\nT 4 1 336 400 " + type
////                + "\r\n";
//            label += "PRINT\r\n";
/******************************************************************TrackOn label************************************************************************************************/
        String toadd="Dr pavan raghava reddy,1-2-54 jkc nagar gujjana gundla, GUNTUR - 522001,ANDHRA PRADESH";
        String str[]=toadd.split(",");
        //todo need to use ml(multi line)
      String  label1 = "! 0 200 200 970 1\r\nON-OUT-OF-PAPER WAIT 400\r\nPW 600\r\n" +
                "TONE 0\r\nSPEED 5\r\nON-FEED IGNORE\r\nNO-PACE\r\n" +
                "LABEL\r\nCONTRAST 0\r\nJOURNAL\r\n" +
              //  "BOX 10 15 550 365 4 20\r\n" +//whole box
             //   "T 7 0 140 55 SPOTON\r\n" +
             // "LINE "+
              "LINE 10 15 10 600 3\r\n"+//Vertical Line 1 - starting
              "LINE 570 15 570 600 3\r\n"+//Vertical Line 1 - ending
              "LINE 150 15 150 135 3\r\n" + //vertical line beside origin
              "LINE 370 15 370 135 3\r\n" +//vertical line beside destination
              "LINE 300 260 300 300 3\r\n"+//vertical line 3
              "LINE 10 15 570 15 3\r\n"+//horizontal ba top
              "LINE 10 135 570 135 3\r\n" +//horizontal bar 1
              "LINE 10 260 570 260 3\r\n" +//horizontal bar 2
              "LINE 10 300 570 300 3\r\n" +//horizontal bar 3
              "LINE 10 500 570 500 3\r\n" +//horizontal bar 4
              "LINE 10 600 570 600 3\r\n" +//horizontal bar 5
            //  "LINE 10 900 570 900 3\r\n" +//horizontal bar 6
              "LINE 150 70 570 70 3\r\n"+//horizontal line below origin and destination
              "BT 0 3 8\r\nB 128 2 0 70 75 150 " + "300002401"+ "\r\nBT OFF\r\n" + //horizontal barcode//
              "T 5 0 220 20 ORIGIN\r\n" +//Q
              "T 5 0 380 20 DESTINATION\r\n" +//Q
              "T 7 0 220 90 MUMBAI \r\n" +//A
              "T 7 0 390 90 GUNTUR \r\n" +//A
              "T 5 0 12 265 REF. No :- \r\n" +//Q
              "T 7 0 130 265 IJMPO2019_04 \r\n" +//A
              "T 5 0 310 265 AWB.No:\r\n" +//Q
              "T 7 0 400 265 2106882816\r\n" +//A
              "T 5 0 12 320 TO: -\r\n"+//Q
              "T 7 0 14 350"+"Dr pavan raghava reddy,"+"\r\n"+//A
              "T 7 0 14 400 1-2-54 jkc nagar gujjana gundla, "+"\r\n"+//A
              "T 7 0 14 450"+" GUNTUR - 522001,ANDHRA PRADESH"+"\r\n"+//A
             // "ML 47 TEXT 0 12 345 "+toadd+"\r\nEND ML\r\n"+//TODO
              "T 5 0 12 530 Ph : \r\n" +//Q
              "T 7 0 70 530 7338716640\r\n" +//A
              "T 5 0 350 530 Weight - \r\n" +//Q
              "T 7 0 450 530 0.440 \r\n" +//A
             // "T 7 0 12 570 Ph : \r\n" +//Q
              "T 5 0 230 530 Pc.s -\r\n" +//Q
              "T 7 0 300 530 1\r\n" +//A
              "T 5 0 12 570 Email: \r\n" +//Q
              "T 7 0 100 570 0\r\n" +//A
              "T 5 0 270 570 Declared Value :  \r\n" +//Q
              "T 7 0 470 570 1 \r\n" +//A
//              "T 5 0 12 615 FROM/RETURN Details :\r\n"+//Q
//              //"T 5 0 12 630"+toadd+"\r\n"+//A
//              "T 7 0 14 655"+"MR CHETAN DARNE"+"\r\n"+//A
//              "T 7 0 14 705"+"Wolterskluwer pvt limited, "+"\r\n"+//A
//              "T 7 0 14 755"+"A/202, 2ND FLOOR THE QUBE,MAROL VILLAGE "+"\r\n"+//A
//              "T 7 0 14 805"+"ANDHERI (E) "+"\r\n"+//A
//              "T 7 0 14 855"+"MUMBAI - 400059,MAHARASHTRA"+"\r\n"+//A

//              "ML 47 \r\n" +
//              " TEXT 5 0 12 615 \r\n" +
//              "MR CHETAN DARNE \r\n"+
//              "Wolterskluwer pvt limited, \r\n"+
//              "A/202, 2ND FLOOR THE QUBE,MAROL VILLAGE \r\n"+
//              "ANDHERI (E) \r\n"+
//              "MUMBAI - 400059,MAHARASHTRA"
//              +"\r\nEND ML\r\n"+//TODO


              //ML 47
              //TEXT 4 0 10 20
              //1st line of text
              //2nd line of text
              //:
              //Nth line of text
              //ENDML


                "PRINT\r\n";
/*
       for(int i=0;i<50;i++) {
            String label = "! 0 200 200 515 1\r\nON-OUT-OF-PAPER WAIT 400\r\nPW 551\r\n" +
                    "TONE 0\r\nSPEED 5\r\nON-FEED IGNORE\r\nNO-PACE\r\n" +
                    "LABEL\r\nCONTRAST 0\r\nGAP-SENSE\r\nSET-TOF 20\r\n" +
                    "BOX 10 40 550 445 4 20\r\n" +//whole box
                    "T 7 0 140 135 SPOTON\r\n" +

                    "T 5 0 140 65 " + "Box No: " + 300002401 + "\r\n" +
                    "LINE 10 100 550 100 2\r\n" + //topmost horizontal bar

                    "LINE 130 215 550 215 1\r\n" +//horizontal bar top
                    "LINE 130 335 550 335 1\r\n" +//horizontal bar 2
                    "LINE 260 215 260 335 1\r\n" +//vertical bar left
                    "LINE 400 215 400 335 1\r\n" +//vertical bar right
                    "LINE 130 405 550 405 1\r\n" +

                    "T 0 1 140 340 DELHIVERY\r\n" + //replace with con number

                    "T 0 1 300 340 " + 300002401 + "\r\n" +//wstPrintedPieceInfo.getConNumber()
                    "B 128 1 0 35 140 360 " + 300002401 + "\r\nBT OFF\r\n" + //horizontal barcode
                    "T 5 2 500 285 " + "" + "\r\n" +
                    "T 5 2 490 415 " + "**" + "\r\n" + //to indicate duplicate label

                    "LINE 130 105 130 445 1\r\n" + //vertical line beside vertical barcode
                    "T 7 0 140 225 FROM\r\n" +
                    "T 5 2 135 275 " + "org" + "\r\n" + //replace with origin

                    "T 7 0 270 225 To\r\n" +

                    "T 5 2 270 275 " + "dest" + "\r\n" + //replace with dest

                    "T 7 0 420 225 PIECES\r\n" +

                    "BT 0 3 8\r\nB 128 2 0 75 220 110 " + 1 + "\r\nBT OFF\r\n" + //horizontal barcode
                    "T 0 2 140 415 Toll-Free:18001021414 contactus @www.spoton.co.in\r\n" +
                    "BT 0 3 8\r\nVB 128 1 0 60 45 370 " + 300002401 + "\r\nBT OFF\r\n" + //vertical barcode
                    "VT 7 0 15 350 CON. No\r\n" +
                    "PRINT\r\n";
*/


        String label = "! 0 200 200 600 1\r\nON-OUT-OF-PAPER WAIT 400\r\nPW 551\r\n";
        label += "TONE 0\r\nSPEED 5\r\nON-FEED IGNORE\r\nNO-PACE\r\n";
        label += "LABEL\r\nCONTRAST 0\r\nPCX 2 29 !<GLOGO.pcx";
        label += "BAR-SENSE\r\nLINE 2 176 440 176 3\r\nLINE 2 382 440 382 3\r\nLINE 2 267 440 267 3\r\n";
        label += "B 128 2 0 100 200 42 " + 12345645//BT 0 3 8
                + "\r\nBT OFF\r\n";
//        label+="T 0 3 280 150 "+2003787289+" \r\n";
//        label += "LINE 219 176 219 266 3 \r\n";
//        label+= "T 5 0 4 181 FROM:\r\nT 4 1 26 185 " +"maan"
//                + "\r\n";
//        label += "T 5 0 228 181 TO:\r\nT 4 1 279 185 " + "hyd"
//                + "\r\n";
        label += "VB 128 2 0 70 448 458 " +  12345645//BT 0 3 5
                + "\r\nBT OFF\r\n";
      //  label+="VT 0 3 525 380 "+2003787289+" \r\n";
        label +=
               // "T 5 0 4 274 Dkt No:\r\n"+ //4 1 12 294
                "B 128 1 0 25 12 310 " + 2003787289//BT 0 5 8
                + "\r\nBT OFF\n";
//        label+="T 0 3 40 350 "+2003787289+" \r\n";
//        label += "T 5 0 295 274 No of Pkgs:\r\nT 4 1 340 294 "
//                + "1/1"+ "\r\n";
//        label += "T 5 0 6 389 Customer Pkg No:\r\nT 0 5 25 420"+ 200874145+"\r\n";
//
//        label += "LINE 280 268 280 390 3\r\n" ;
//        label += "T 4 1 336 450 "
//                + "\r\n";
         label +="SETFF 25 2.5 \r\n";
        label += "SET-TOF 0 \r\n";
        label += "PRINT\r\n";


        String gato30C ="SIZE 72 mm, 73.1 mm\n" +
                "DIRECTION 0,0\n" +
                "REFERENCE 0,0\n" +
                "OFFSET 0 mm\n" +
                "SET PEEL OFF\n" +
                "SET CUTTER OFF\n" +
                "SET PARTIAL_CUTTER OFF\n" +
                "SET TEAR ON\n" +
                "CLS\n" +
                "BITMAP 429,517,18,32,1,ÿÿÿÿÿÿÿçÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿ\u0007ÿÿÿÿÿÿÿ \u000Fÿð >\u0007\u0081Àþ\u0007ð\u0003ð\u0004\u0001þ   ÿà >\u0007\u0001À~\u000Fø\u0003ø\u0006\u0001ÿ\b  ?à <\u0006\u0001à~\u000Fø\u0007ø\u0006\u0001þ\b\u0007 ?à 8\u0006\u0001ð<\u000Fø\u0007ð\u000F\u0001€\u0018\u000Fà\u000Fÿøx\u0006\u0001ð<\u000Fð\u0007ð\u000F  0\u000Fà\u000Fÿðx\u0004 ø\u001C\u001Fð\u000Fà\u001F  p\u000Fð\u000Fà p\u0004 ø\u0018\u001Fð\u000Fà\u001F€ðp\u000Fð\u000FÀ p  ü\b\u001Fð\u001Fà\u001F€`ð\u000Fà\u000FÀ `  ü ?à\u001FÀ?€Aÿÿà\u001FÀ à€ þ ?à\u001FÀ?€CÿÿÀ?ÿàÁÀ`þ ?À\u001FÀ\u007FÀ\u0007ÿÿ€\u007FÿàÁÀ`ü \u007FÀ7€}À\u0007ïÿ ÿ€\u0001\u0081Ààø \u007FÀ  \u0001À\u000FÀ \u0003ÿ€\u0001ƒÀàð \u007F€  \u0003À\u001FÀ \u0007ÿ \u0001ƒÁàÀ ÿ€  \u0007à\u000FÀ ?ÿ \u0001‡Áó€Àÿÿÿÿÿÿÿü\u0007ÿÿÿÿÿÿü\u0001Àÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿð\u0007ÁÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÀ\u000Fƒÿÿÿÿÿÿÿÿÿÿÿÿù\u007Fà ?\u008Fÿÿÿÿÿÿÿÿÿÿÿÿü   ÿŸÿÿÿÿÿÿÿÿÿÿÿÿÿ€ \u0007ÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿø ?ÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿÿ\n" +
                "CODEPAGE 1252\n" +
                "TEXT 563,420,\"ROMAN.TTF\",180,1,10,\"Shipper Detail\"\n" +
                "TEXT 573,379,\"ROMAN.TTF\",180,1,8,\" Welpsun India P ltd- Inds estate 112\"\n" +
                "TEXT 573,352,\"ROMAN.TTF\",180,1,8,\" Versamedi- Anjar- Gandhidam Gujrat\"\n" +
                "TEXT 573,325,\"ROMAN.TTF\",180,1,8,\" Gandhidham-370201- State Gujrat\"\n" +
                "TEXT 573,298,\"ROMAN.TTF\",180,1,8,\" T+91 8008559787\"\n" +
                "TEXT 571,268,\"ROMAN.TTF\",180,1,10,\" Recevier  Detail\"\n" +
                "TEXT 573,235,\"ROMAN.TTF\",180,1,8,\" Welpsun India P ltd- Inds estate 112\"\n" +
                "TEXT 573,208,\"ROMAN.TTF\",180,1,8,\" Versamedi- Anjar- Gandhidam Gujrat\"\n" +
                "TEXT 573,181,\"ROMAN.TTF\",180,1,8,\" BHUJ -377201- State Gujrat\"\n" +
                "TEXT 573,154,\"ROMAN.TTF\",180,1,8,\" T+91 8008559787\"\n" +
                "TEXT 571,115,\"ROMAN.TTF\",180,1,8,\" No of Packages: 100000\"\n" +
                "TEXT 571,88,\"ROMAN.TTF\",180,1,8,\" Actual Weight   : 100000\"\n" +
                "TEXT 571,61,\"ROMAN.TTF\",180,1,8,\" Cargo Value      : 100000\"\n" +
                "TEXT 571,34,\"ROMAN.TTF\",180,1,8,\" \"\n" +
                "BAR 124,127, 452, 3\n" +
                "BARCODE 403,551,\"128M\",89,0,180,5,10,\"!10512345678\"\n" +
                "TEXT 262,456,\"ROMAN.TTF\",180,1,10,\"12345678\"\n" +
                "BAR 8,431, 565, 3\n" +
                "BAR 570,431, 3, 129\n" +
                "BAR 1,558, 570, 3\n" +
                "BAR 3,460, 3, 100\n" +
                "QRCODE 123,144,L,4,A,180,M2,S7,\"https://www.gati.com/track-by-docket/\"\n" +
//                "\t\t\t\t\t\"QRCODE 400,402,L,3,A,0,M2,S7,\\\"https://www.gati.com/QRDktTrack.php?p=cc2ebc3565481eba1d0481ba62b0640a\"+d.getDocketno()+\"cc2ebc3565481eba1d0481ba62b0640a\\\"\\n\" +\n"+
//                "TEXT 91,175,\"0\",90,18,24,\"SE-BHJ-GJ\"\n" +
//                "BAR 123,0, 3, 432\n" +
//                "BAR 3,12, 3, 451\n" +
//                "TEXT 323,109,\"ROMAN.TTF\",180,1,7,\"FOD      :  1000\"\n" +
//                "TEXT 323,87,\"ROMAN.TTF\",180,1,7,\"COD     : 100000\"\n" +
                "PRINT 1,1\n";




        String str1="\u0010CT~~CD,~CC^~CT~\n" +
                "^XA~TA000~JSN^LT0^MNM^MTD^PON^PMN^LH0,0^JMA^PR4,4~SD19^JUS^LRN^CI0^XZ\n" +
                "^XA\n" +
                "^MMT\n" +
                "^PW574\n" +
                "^LL0599\n" +
                "^LS0\n" +
                "^FO128,0^GFA,01792,01792,00004,:Z64:\n" +
                "eJxjYKAV4BnFo3gUj+JRPIopxGwMtAIAUMIS6w==:B942\n" +
                "^FO128,128^GFA,01792,01792,00056,:Z64:\n" +
                "eJxjYBgFOMB/kgFl+kbBKBgFo2AU0BEAAOE4Y50=:DA58\n" +
                "^FO256,416^GFA,01024,01024,00016,:Z64:\n" +
                "eJxjYBgFo4AkYM/ceACIHtQ3QPj/Dx8E8R/+h/AZ/z9+eIC54cFjGP8hlN8M4TMf+PwRyC94zgzlNxRvBPILkfg7gXxrJP5MIN8eiT8XyJdH4vcC+XJw/oHP/UC+zPNmuP3tQL4FzH6g+9oP2P+ogLkP6P5mIP8DzP1g/9n/fPAAxh/WAAAXhFXK:A753\n" +
                "^FO352,384^GFA,01792,01792,00028,:Z64:\n" +
                "eJzt0rFqwzAQBuATGrxZHTMI+xU6ulDQKyl7qVQ8aGteScFDX0PGe1Do4lIR9WTJtCl0LgHf+pnz/b8NcKsjwf2fCWr4PDPNAtSVbeTjOPT6ki0OhoUgnEBrvfAKrY86ERknw5qgvEITs/IRzUS7mB1MXfN76TnUu076Ee1AszmaDDqHBtydj2ivZDmIBLSGw4PNtrfJyrGxN/UTVx/JRMh2KBkU3ul/mSkZGd6puVx2dmWnCdnIZPA91/YSs9kp3dnt0RqeM7xpVbLj+3gUZ8vJpc3Zo2Z27awKsR0dGls6O37qnD11TX2sLNqJutS1fTckG1DczQGws3Wo6a9NhW8r3yE9BbDD3asRS9cMi91hBz9MP//5X2yzzTa3Ol9i77K2:FDB2\n" +
                "^FO352,224^GFA,01792,01792,00028,:Z64:\n" +
                "eJzt0TFugzAUBuDnevBm1gwoXKGjBxqu5O5RQ4RUtuYIXMURQ8YcobYysDIyRLz+JqhJ1COUf7HQJ6P3fhMtWfLPory9NJnoBZfCDVq4pNzQsBYOlg02cFNfFawdYYXnKp6wPLfh8n0YE9iJtfS7nk+cTGZWNhzbT72WuKdgr++hVep2j6KVKoU5+aUsGedlvY9WlDa0MHM38rI6TrP0NpyrA0c7MWw3UMf7yRLMOcpf0+ZmIZq42tDjX5NJmDXUyZKjEXZwd0tTa1wnq8lWaZyz1jCJI00NdqjVZLnB7k03woqWYUXP7bw7OvPcXK457DhqozPP+7kzdO0/Mj9g9ze3hSm3oe3NiCyRLAmdPbxO8mTo+vHleDZPMn5F03/tBR08mZhtyZKn/ADvfrFQ:ABE2\n" +
                "^FO288,320^GFA,03456,03456,00036,:Z64:\n" +
                "eJzt1T1r20AYAOD3OLhbVKvjBQT6CycMQYVg96ecEdhLBmfLUGIZgdzBZPbQH+FuHU8YnCVk9la5GboUKtNFBaHrneVPOXTKUKhfZJDxI53u/ZAB/rWwadFyf5UuTgFlBBgmcEexz+Q1mrJBuDbuQzHQpj3KgBaVURg7LDVGVYZJFoCwvHsfGg4BixA0iyKLZV3ML5LKcMGTUDLxyMzv5oMhiRqVGW6NSIYhF49AfxBCf5aEwnJG2aKnDoxMIhBrA4R+L/TNxAxPFkG5N50wiUGKJwAKBIhZUAToy2NAONs8j/8MyRiF4iEEnBLAzBiF8qfOuJ1v9/4Nko8IhLkk35iOgnx+YOwuJBMM4kGfOxsTRGgx64w5W1YGNmauTy0CaKr39YzRZCYWu+fhcyuZNImY6/xUBsMM40mS7o0/ZwELLG9U5Vkbk2fK1mazlhvlA3tZtCNdr1xlkKpMIdVgMu3zbU1tlLWozF2UAs4qcwfKYVJo0w5fpbf+l1AhTYFDzi9U7EtLePdEvAllzbg5cFz6lel78anBcOUAp40rhoy59UaEX9QMAW4ObMy1MdGpsdzp2lxW5tqL4vbvmmHuFJtb7cwwdlc1w6Ed2RkOm5XpeuGceTUjtHEzlDUnKp5Lq+cNS6ujjs17vTWfwwevMoEXnRr9DXMODb5AxnQ+j4jVCevmLdGG7ExMSFIzSehrQwi/RaNI12L1gpmB4/g6zzszOjUDsHM3wwV/p9bmJirxsGZaQDM7Q6buI2TqPjw15/hbYOWmDKQLbo7itsSF8MK039RddWIGsjK06GvDj40eHaHNUoJuVZANPRfDqV/r1cpIURlrber9fGyYmYup46mXDOxNxCy++R88Mv2d6WpDeHJokAJxBbKlG22wNj0vtuwsgFNjh1sTaOMeG7NWFySS27X0XFg+PzHf9fwcmLEek8ua6cxCKeU2P2L1otEdvs+zWH2i/rHBCgL4Wg70C09pQwtxM8Fu5tRMD1TZhsqYuk+wfWzOcY5znOM14w8f3pfx:4D9F\n" +
                "^FO512,288^GFA,00256,00256,00008,:Z64:\n" +
                "eJxjYCAb8OCl5RtQaULqcdKM/x+g0NQFAHFmBZs=:28AD\n" +
                "^FO416,288^GFA,00384,00384,00012,:Z64:\n" +
                "eJxjYKAFYG/+J8//8z9/4wcGBv7H/+z7f/6XP/yDgYEngcewwYBN4rgMA4OEgUTCgQQegzQeENsg4cABCYM0BhA7IeFggwGUfSDhMEOCQTqQLVPYkHCM4YBB8gGgmR8ZEo4wNAA1Ae3azJBwhrHBILkBqMgYygbpTWZLOCPZbJAMNF8mmcewx7BN4vAcoN6Df+zbP/yTPwh0D3vDD3nmBzX8DR9oEgxEAAC36zk3:312F\n" +
                "^FO256,160^GFA,03840,03840,00040,:Z64:\n" +
                "eJzt1TFr3DAUB/AnBPKinFcVzPkrKGTxUOp+FB2B6xKooEs2O2ToEujaj9GxW95huFvyATI6ZMjSwd1cOKw+2fFdiB1nyVDKPTgw9g8/S/93COBQ/1SFF9WHAOuQrnINcamkAAlGz0HJkDuWsyvhXVzUWXi3TXNIS02/3mW5kmmxd8m9OpWLq2MES86a3t2hkrbkO6eXcvX9RBikZxpM746RXGmeONG5EL3DncvpC9BydvFn4OCJo1crCXrvkkdHV6Ou7zvvnEWIW/c+bGDeungLx3693xrvROdShACnXNS5OQKU3i2p78lIX9U5IGe8eyD3Jd+vd+e6/WM57R29ZFHQ7Tsc7l/U5UGuzWNxQbcRh3mE+3w3Lk/v4Ybe/dn1+abbRxd080Iu/ury+AF+CZ5nrp+XnTvUm5XTIr1hP5VDRwOhWWkgKI2Wz/aZkctu2HflVq0LKgNhZQeOX9KErHmkoLj0TiQGouR86DhN3JpLctw7+c7lWg6d4PEWN26mYN055q7t7GzEhTUWjdg5WFwbMeakMoXge3cKRixHnNDp6iqoszVnZVbN1JLcJx02oy7sXVB7dzrq7Ooq8n25NprfnpFb/Bj25cbgrfIuSOjPp87BzoaO71zBZxE5bUFLUw3z4MbibZuHlJ2LkqFjjmGKNqwpXyX6fI197oBcjDaoaV6SwLuPMDYvh3qLSn83LJesYo05wk3jd12+7IItOUkufMnRQUZuFoHQqkRBUzHlKFNyBoV63aW1RcLTjsY7rvSrLiCntJpwcSU4Sl6CkCYtGq2yYvuyg9q7bMJRX2EkRN6V0983s5KOdCEQp12USNm7qf1Tqs1D4FQeaSX0kXugddBx2Oa7GV0HORu6+8q7tJ2XcXeoQx3qv6u/Spx4zw==:BDC1\n" +
                "^FO480,96^GFA,00384,00384,00012,:Z64:\n" +
                "eJxjYBgwwPiwva3C/gCYzfyx//CfegibTXJGcs8DKJtxBjPPAQibvXEes80BhPofUDZDZePjAwlQtmRDMoM9whwemPk/+x//KTgAs7fxA0w9CCCzoWoYGBsYYOaA2fJIaqgJAPsUKqA=:6419\n" +
                "^FO416,64^GFA,01280,01280,00020,:Z64:\n" +
                "eJztz7EKwjAQBuALhWYJZL1B9BUiXRzEvkqLg7NbNwVBl4JrBsGH8AUqBV2kro4FB9eASwYRL1ZUqA8g6D8d3/DfHcDPxdaJXevmTT9YTZo8TwYLZfaAbFIyk0UAoTxewl3bjsbIZsazJVms+2mnCPSQbKo8NGQRBL7aBiImS9EXzhKyIFdi8LCD62ud/XBixAhexpdnv8uMSLi9246MaerzViKSlRVub9pPaYeOF9Xejeub030F3aeRn+i+PKr+aDWU6SHyI/3hrIoCQGRjmtblmzU+mLTSWfZu3Eo3Zc++f/75ntwACKVXew==:C1FC\n" +
                "^FO416,32^GFA,01280,01280,00020,:Z64:\n" +
                "eJzt0D0KwjAUB/CEQOuQtmvFSyRk8TgVVw/gZkGwS7FrXPQK3sBCwanWK0R6AYtLBUFfTa1inZ36H3883hdCXf4Votpmlm2z3bbRdhU7nIJYDfEqn6rCZ5U57FhECiy63IqrNsIyLkfbCV6KUMjY05b2fQ4WCirot10oo3f1sk0BFuVgs/htMsURAfMak8Ld45BAP+4/jWc8HFcGM6Q2Zwf75b0F7EevZW1ruCOxFjhIaHke6E8haEvAoIKhDzOs+Zdh3zCJNrv+zNtQY126/MoDuttVeQ==:9197\n" +
                "^FO416,0^GFA,01280,01280,00020,:Z64:\n" +
                "eJxjYBgFQx3wN2CKyT/AFLNIwBQzQGLbSR9Lb3jAUGPecOaDHFSsXv6Z/YEHDP/sDxz/UQ+157BMmsXBDQzHLA4cl3h4ACLGOCPN4OAGxmaDhGOSBxBixlCxygaoe/7PSLNvlgCJHf/AkAAXs2GGiDXAxA4CzWPjAetlgIklS6QZHOMB2nvwmARMrF4izR4o9s/+/PEfMDE7iTR5HjagP/oOP4TZy8DAxgAUAzsL5j6QmAw7hPUQSUyCGWp2A6YYLFxGwfAAAP+vSFY=:1636\n" +
                "^FO352,96^GFA,00128,00128,00004,:Z64:\n" +
                "eJxjYCAW8EAxpWpQAQAM4AAx:8194\n" +
                "^FO352,64^GFA,00256,00256,00004,:Z64:\n" +
                "eJxjYKA24IFiStXQBwAAHeAAMQ==:B3AB\n" +
                "^FO352,32^GFA,00256,00256,00004,:Z64:\n" +
                "eJxjYKAV4IFiStXQFgAAHGAAMQ==:9421\n" +
                "^FO352,0^GFA,00128,00128,00004,:Z64:\n" +
                "eJxjYCAF8EAxpWoQAAAMIAAx:01D8\n" +
                "^FO192,96^GFA,00768,00768,00024,:Z64:\n" +
                "eJxjYBhcgPEACvcHjMGMIs74D8ZiR1HO3AZjsaGKw1l8jO0HHGfUmDdIJH5g7jGukOeRYIaI9z94OOOf/QGJ5B/MZ4z/2PdAxPkZZyQ8nNlmARSXYDhj3JMIFZdmbDDcOIPZIEEijQcozsZwBiIu2YhLfP7HjTP/w8XroeKGB+d/lsQibnBwhrHkzGa4eMIZif9g8cM9yZIzjgHd8xjonuIewx4LsHhCff8zXpD75R//YH7/+Y997w+wOQm17cd4Qf7lB/r3+eYKed4biMCQYDAA0wcMUAMJhzjjDCRxHsb/GOIMqOJDAQAAAsphCw==:2AC1\n" +
                "^FO320,64^GFA,00512,00512,00008,:Z64:\n" +
                "eJxjYBhgwPjBDkwz/6gH0+wyyWCajedgAzaaXQZCM/9ApWHimOqTUcyH2TcKwAAAr5QUPg==:A6D3\n" +
                "^FO224,32^GFA,01280,01280,00020,:Z64:\n" +
                "eJxjYBgFQxMwfrBjP/BDnvlBDX9jgwFEjPlHPf/DP/btH/7JH4SLSSTzJPYYthkck4CLsfEcnGHMlnBGstkg/d//A8SIJbYZYIr1GDZgiM0wxrADq9gGM0yxBzaYYgcsMP0BF8PiX+RwYVCgWnCPgmEJAB48VG0=:4A62\n" +
                "^FO160,0^GFA,01792,01792,00028,:Z64:\n" +
                "eJxjYBgFo4D2gAXOOiABJBSQ5ThQ5QSQ5SRQ5SSQ5QRQ5TiQ5Qz4GeobeBgMfsgdvjkj4QePPPODGv4GsDoGA/sD/4Fyhn/sj/+ckfyPx779wz/5AxC5hIKHB4FyxjyGxyxnpPOxGbYZHJOAyT1IbASZyZZwRnJGGjOIajZIgModKGbEKcdgDJIz/geWY8CQa/4DlUtuwJBrB8qBzUz8gCHXx8NQ2ANy54MaFDkDoFwPD/NHkP/mP+DjQZEDuhMotwEULv0JbFC5G9Lg8AT6r4cHGoBpbBwQuR/y4HgAhksfTC6dTwISLg9kwPEHDE+gO0GAsSH5jwByeLIA46EZJpf4QwE5HkbBKBhoAAAm7YXo:0CC2\n" +
                "^FO32,160^GFA,01792,01792,00008,:Z64:\n" +
                "eJzVk0tuAyEMhkEsWHIEjsLRMr0ZuQlHYMkC8cfGeNpFH1RNowRp9Ikx/v0CY55t+UOYDt+YyGkQHDJAJj8skInNjkQM1fVYiMW1MOmVVRiUhc/5GsuF/Xu6sp4DriB9Un3jOOYybGXGupcuIPl8RYoe/5WUat5I1Pa/8XF118kWG8/J9YQ1J8icRpI59SBzKhvF88J0MwGYHiQr9wvTYJE96zvazHtGxpTf+yt36OdPz6u/6qm+xjvjaz4Gnzf6l32f0nlVmpcH0531dvkr4fxQ3RV+2c/z6k9lfdTfyeP7Qo4l9hrv/NHvZ7ZG+Nxz3K6Hp3M/3invG124zpQ=:39CF\n" +
                "^FO512,128^GFA,00512,00512,00008,:Z64:\n" +
                "eJxjYBi8IAEv/QeNJqQeJ83/H5UeUQAAZUQI9Q==:CA2E\n" +
                "^FO416,128^GFA,01024,01024,00016,:Z64:\n" +
                "eJxjYBgFJAIb+Q8/6s//qOFvgPDr5T/+AfL/yR+A8JMZEnsYGwyOWTyA8A82HGZjZkhsNkiA8hub2dgYDjYbQM072MzMxsPYiOC3MbNJMDM2W0D5j3uY2QzYGJvloPz6DmY2BRbGZjYo33YGM1sCD2OzDJTPCOVLQPmHJYzZEs5INktA7X8skdiT2GNwTAbqvjr+h3/s2z/844e634b9wA955gc17A0UBNIoGBoAADURNUc=:4526\n" +
                "^FO320,448^GFA,03072,03072,00032,:Z64:\n" +
                "eJztkr9qwzAQxk+iguJAiCBaOhWPGvwAnRxIdwWk9xHulr6EyWSukLn4aUqm0MHP0Du3qdRS3K4Ff0MkdP7lu38As2bNmvUXdel6HX+Iu3QNPt2H4aU6DbKVZ7CtxD2CRcQ2fRtqs6HvhQMNwog1KLHSq894j9E2PXSyJR6wQSiKrsBLWBAfmFc1mFp44WHN5yUuyyoO5I/yZfSXWFo+UwUKwv07T/5OOO10LXapgEV8ekTABZlHSRfsOI+MF7v7NWyJD6A8/dShFuHTH4/NaTiCrRiivPHQ5v6U/8hrA+SvVqAFv+rE980B76DYR+Y76kDk1wuvQlAbT00LQEUrB0Exn9e/PA0VLI+cPPP78V9TfYZ4M/Jk7mDHrRfZMO3iobM0NOa5ct6DIlsGbwTxZizeUBMYpfNDBQ5Vc64AKzKlybeWUijT/FUg3hnwnvyN1kCj19n4i76/k883TdeTKS1OZB6TvQneE69ctnLbbZa9LUt5K5ftkN6khO8aV3BCGLvJuM879lW3nMUrrd+EtBNT4YKaHyfiV9uJILUAcSr+u6a8Z82a9S/1BvqWhxg=:681B\n" +
                "^FO20,428^GB536,118,2^FS\n" +
                "^BY3,3,58^FT332,474^BCI,,Y,N\n" +
                "^FD>;12345678>69^FS\n" +
                "^FT10,169^BQN,2,6\n" +
                "^FH\\^FDLA,SE-465725901-MH^FS\n" +
                "^FO151,284^GB401,0,2^FS\n" +
                "^PQ1,0,1,Y^XZ\n";

        String str3= "! 0 200 200 525 1\r\nON-OUT-OF-PAPER WAIT 400\r\nPW 551\r\n";
        str3 += "TONE 0\r\nSPEED 5\r\nON-FEED IGNORE\r\nNO-PACE\r\n";
        str3 += "LABEL\r\nCONTRAST 0\r\nPCX 2 29 !<glogo1.pcx";
        str3 += "BAR-SENSE\r\nLINE 2 176 440 176 3\r\nLINE 2 382 440 382 3\r\nLINE 2 267 440 267 3\r\n";
        str3+="T 0 3 280 150 ";



        String s1="\u0010CT~~CD,~CC^~CT~\n" +
                "^XA\n" +
                "^PW574\n" +
                "^FO352,448^GFA,00896,00896,00028,:Z64:\n" +
                "eJxjYMAN/uMAHwjIjYJRMAqIAwB7VTGx:8544\n" +
                "^FO128,256^GFA,01792,01792,00056,:Z64:\n" +
                "eJxjYBgFo2AUUAqY/5MBfpCvbxSMglFAOQAA+Ttpjw==:5C20\n" +
                "^FO128,128^GFA,01792,01792,00056,:Z64:\n" +
                "eJxjYBgFo2AUDDXA+J8cQL6+UTAKhiMAAOU7aZk=:EB87\n" +
                "^FO128,0^GFA,01664,01664,00004,:Z64:\n" +
                "eJyTYWBgkBnFo3gEY75RPIpHMRjTGwAAmJIfxw==:CA45\n" +
                "^FO320,416^GFA,04096,04096,00032,:Z64:\n" +
                "eJztk71uE0EUhe9ksMYiEkOTDjGI0rwD44KIlgKXCNFS0SBRrNiVUlBQ7BsQiQolBY+QfQMeISPRRK423U0Y5nDXzv4IbFMj9hReaz6d2XN/lmjUqFGj/gc9+At/s/l4evPcv5C/U5H8nF4XLZ4cHtKTyZzIBFrMJ4vJczp4CsxbbgGCZR9sIASNMpEtAW65ARQMu8pUlFcaJpJzP1/Ggf8IJdsbf/qQyB9ziaLjScGxqW3jN8kkuu9qjda/9/C9hmfDwlGYpNMsecnRFUCZEb+uXaWER408+lqh6niyOGPNvhBuhUOKGfLMnN/LVHSkQCYqqDqvei757LfHTMkXEsqyhg75wO8Q3XEWhTd+x9KMavFMdfm88JIj4azQacVLQX3+xm/fZZTnJKU7SW7kVHX1z07ZW/H71HBfE77KqVzVSrF3tfijhF/xT81U4oCnxg+Zm/CLKRqr5Z7XmRX/25U/D81FkqpusbQr2SBtFz+/xjLI6Jd7CC3PcRldyNSVzJ1zoMqlhPMuvvivZPjJ/mDyDKCAtBDXA34po83cK6aDhYia5vXrR+nk85fbxcx/7xPT7IR+V35U/3G20nr99/GRN/O1TDRb/GvJAoYdWNbBFNugAFn3O1v5XWp6fmvr5XY5+Nw2vfwFsAMTPdppHzVq1KhRo/4x/QIqNR8J:B188\n" +
                "^BY3,3,66^FT330,434^BCI,,Y,N\n" +
                "^FD>;12345678>69^FS\n" +
                "^FO331,1^GB0,390,2^FS\n" +
                "^BY3,3,77^FT43,58^BCR,,Y,N\n" +
                "^FD>;12345678>69^FS\n" +
                "^FT536,408^A@I,39,38,TT0003M_^FH\\^CI17^F8^FD{EXP}^FS^CI0\n" +
                "^FT544,353^A@I,34,24,TT0003M_^FH\\^CI17^F8^FDFROM:^FS^CI0\n" +
                "^FT325,353^A@I,34,33,TT0003M_^FH\\^CI17^F8^FDTO:^FS^CI0\n" +
                "^FT540,237^A@I,34,24,TT0003M_^FH\\^CI17^F8^FDDktNo:^FS^CI0\n" +
                "^FT325,237^A@I,34,22,TT0003M_^FH\\^CI17^F8^FDSeq./No.ofPkgs^FS^CI0\n" +
                "^FT325,110^A@I,34,22,TT0003M_^FH\\^CI17^F8^FDType:^FS^CI0\n" +
                "^FT544,105^A@I,34,22,TT0003M_^FH\\^CI17^F8^FDAdd:^FS^CI0\n" +
                "^FT497,298^A@I,61,42,TT0003M_^FH\\^CI17^F8^FD{HYDN}^FS^CI0\n" +
                "^FT284,298^A@I,61,33,TT0003M_^FH\\^CI17^F8^FD{BOMS}^FS^CI0\n" +
                "^FT540,171^A@I,61,33,TT0003M_^FH\\^CI17^F8^FD{465725901}^FS^CI0\n" +
                "^FT252,171^A@I,61,33,TT0003M_^FH\\^CI17^F8^FD{/3}^FS^CI0\n" +
                "^FT298,171^A@I,61,33,TT0003M_^FH\\^CI17^F8^FD{1}^FS^CI0\n" +
                "^FT540,40^A@I,61,33,TT0003M_^FH\\^CI17^F8^FD{10-08-2022}^FS^CI0\n" +
                "^FT279,40^A@I,61,33,TT0003M_^FH\\^CI17^F8^FD{CB}^FS^CI0\n" +
                "^FO11,391^GB553,118,2^FS\n" +
                "^PQ";

        String sss3="! 0 200 200 500 1\r\n" +
                "B QR 10 M 2 U 10\r\n" +
                "MA,QR code ABC123\r\n" +
                "ENDQR\r\n" +
                "T 4 0 10 400 QR code ABC123\r\n" +
                "FORM\r\n" +
                "PRINT\r\n";
        print(gato30C.getBytes());
       // print(label1.getBytes());
            try {
                Thread.sleep(Constants.PRINT_SLEEP_TIME);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    }

    protected boolean isDeviceConnected() {
        if (Boolean.parseBoolean(dataSource.shardPreferences.getValue(TSC))) {
            if (!Boolean.parseBoolean(dataSource.shardPreferences.getValue(OTG))) {
                if (!tscConnected) {
                    String openport = TscDll.openport(Globals.selectedDevice.toString());
                    Log.d("openport", openport);
                    if (openport.equals("-1")) {
                        return false;
                    } else {
                        return true;
                    }
                }
            }
        } else {
            if (mCommandService != null) {
                if (mCommandService.isConnected())
                    return true;
                else
                    return false;
            } else {
                return false;
            }
        }
        return false;
    }


    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String state = "";
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            // mTitle.setText(R.string.title_connected_to);
                            // mTitle.append(mConnectedDeviceName);
                            state = getApplicationContext().getResources().getString(
                                    R.string.title_connected_to);

                            break;
                        case BluetoothService.STATE_CONNECTING:
                            // mTitle.setText(R.string.title_connecting);
                            state = getApplicationContext().getResources().getString(
                                    R.string.title_connecting);

                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
//                            state = getApplicationContext().getResources().getString(
//                                    R.string.title_not_connected);
//
//                            Toast.makeText(getApplicationContext(),
//                                    state + mConnectedDeviceName, Toast.LENGTH_SHORT)
//                                    .show();
                            break;
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    state = getApplicationContext().getResources().getString(
                            R.string.title_connected_to);

                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(),
                            state + mConnectedDeviceName, Toast.LENGTH_SHORT)
                            .show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(),
                            msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
                            .show();
                    break;
            }

            Globals.BLUETOOTH_STATE = state;
        }
    };

    protected boolean print(byte[] data) {

        Log.d(Constants.LOG_TAG, "Print try");

        if (mCommandService.getState() != BluetoothService.STATE_CONNECTED
                && mCommandService.getState() != BluetoothService.STATE_CONNECTING) {
        } else {
            Log.d(Constants.LOG_TAG, "Printer was already connected");
        }
        if (mCommandService.write(data))
            return true;
        else
            return false;

    }

    public void setSelectedDevice(BluetoothAdapter mBtAdapter) {

        if (Globals.selectedDevice == null) {
            String dev = "Zebra";

            Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device != null) {
                        if (device.getAddress().equals(dev))
                            Globals.selectedDevice = device;
                    }
                }
            }

        }

    }

    public void showPrinterOption() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getResources().getString(R.string.whichPrinterTitel));
        builder.setCancelable(true);
        builder.setSingleChoiceItems(Globals.printerArr,
                Globals.selectedPrinterPos,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int pos) {
                        Globals.selectedPrinterPos = pos;
                        if (pos == 0) {
                            dataSource.shardPreferences.set(TSC, "true");
                            dataSource.shardPreferences.set(OTG, "false");
                            startPrinting();
                        } else if (pos == 1) {
                            dataSource.shardPreferences.set(TSC, "false");
                            dataSource.shardPreferences.set(OTG, "false");
                            startPrinting();
                        } else if (pos == 2) {
                            dataSource.shardPreferences.set(TSC, "true");
                            dataSource.shardPreferences.set(OTG, "true");
                            dataSource.shardPreferences.set(DEVICE, "");
                            deviceMacAddress.setText(TSC + "-" + OTG);
                            Globals.selectedDevice = null;
                        } else if (pos == 3) {
                            dataSource.shardPreferences.set(TSC, "false");
                            dataSource.shardPreferences.set(OTG, "true");
                            dataSource.shardPreferences.set(DEVICE, "");
                            deviceMacAddress.setText("Zebra" + "-" + OTG);
                            Globals.selectedDevice = null;
                        }
                        dialog.dismiss();
                    }
                });

        builder.show();

    }

    private void startPrinting() {
        Globals.BLUETOOTH_STATE = "";
        checkBluetoothState();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_ENABLE_BT:
                    checkBluetoothState();
                    break;
                case Constants.REQUEST_PAIRED_DEVICE:
                    if (Globals.selectedDevice != null) {
                        Utils.logD("Globals.selectedDevice != null");
                        Log.d(Constants.LOG_TAG, Globals.selectedDevice.toString());
                        if (mCommandService != null && !Boolean.parseBoolean(dataSource.shardPreferences.getValue(TSC))) {
                            mCommandService.connect(Globals.selectedDevice);
                        }
                        Gson gson = new Gson();
                        String json = gson.toJson(Globals.selectedDevice);
                        dataSource.shardPreferences.set(DEVICE, json);
                        deviceMacAddress.setText(Globals.selectedDevice.toString());
                        Log.d("BTDevice", json.toString());
                    } else {
                        String state = getApplicationContext().getResources()
                                .getString(R.string.title_not_connected);
                        Globals.BLUETOOTH_STATE = state;
                    }


                    break;
                case Constants.REQUEST_DISCOVERABLE_BT:
                    checkBluetoothState();
                    break;

                case Constants.REQUEST_REPRINT_DOCKET:
//                    checkAndPrint(null);
                    break;

                default:
                    break;
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void checkBluetoothState() {
        String state = getResources().getString(R.string.connecting_to_printer);
        Globals.BLUETOOTH_STATE = state;
        if (isBluetoothEnabled()) {
            if (!isBluetoothDiscovering()) {
                startDeviceList();
                Globals.BLUETOOTH_STATE = context.getResources()
                        .getString(R.string.connect_devices);
            }
        }
    }

    public boolean isBluetoothEnabled() {
        boolean isEnabled = false;

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mCommandService == null) {
            mCommandService = new BluetoothService(context, mHandler);
        } else if (mCommandService != null) {
            if (mCommandService.getState() == BluetoothService.STATE_NONE) {
                mCommandService.start();
            }
        }
        setSelectedDevice(bluetoothAdapter);

        if (bluetoothAdapter == null) {
            Toast.makeText(context,
                    getResources().getString(R.string.bt_not_support),
                    Toast.LENGTH_LONG).show();
        } else if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, Constants.REQUEST_ENABLE_BT);
        } else
            isEnabled = true;

        return isEnabled;
    }

    public boolean isBluetoothDiscovering() {
        boolean isDiscovering = false;

        if (bluetoothAdapter.isDiscovering()) {
            Toast.makeText(context,
                    getResources().getString(R.string.bt_enabled),
                    Toast.LENGTH_SHORT).show();
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startActivityForResult(enableBtIntent,
                    Constants.REQUEST_DISCOVERABLE_BT);
            isDiscovering = true;
        }

        return isDiscovering;

    }


    public void startDeviceList() {
        Log.d("MainActivity", "startDeviceList");
        deviceMacAddress.setText(null);
        Intent findDeviceList = new Intent(context, DeviceList.class);
        startActivityForResult(findDeviceList,
                Constants.REQUEST_PAIRED_DEVICE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("test", "in onRequestPermissionsResult");
        if (!executed) {
            TscUsb.openport(usbManager, usbDevice);
            if (!isSpecific) {
                for (int i = 0; i < Integer.parseInt(pieces.getText().toString()); i++) {
                    TscUsb.clearbuffer();
                    if(connumber.getText().toString().trim().length() == 2 || connumber.getText().toString().trim().length() == 4 || connumber.getText().toString().trim().length() == 6||connumber.getText().toString().trim().length() == 7) {
                        conNoCommand = "TEXT 47,100,\"ROMAN.TTF\",0,63,63,\"" + StringUtils.center(connumber.getText().toString().trim(), 7) + "\"\n";
                    } else {
                        if(connumber.getText().toString().trim().length() == 5) {
                            conNoCommand = "TEXT 175,100,\"ROMAN.TTF\",0,63,63,\"" + StringUtils.center(connumber.getText().toString().trim(), 7) + "\"\n";
                        } else if(connumber.getText().toString().trim().length() == 3) {
                            conNoCommand = "TEXT 175,100,\"ROMAN.TTF\",0,63,63,\"" + StringUtils.center(connumber.getText().toString().trim(), 7) + "\"\n";
                        } else {
                            conNoCommand = "TEXT 195,100,\"ROMAN.TTF\",0,63,63,\"" + StringUtils.center(connumber.getText().toString().trim(), 7) + "\"\n";
                        }
                    }
                    if(pieces.getText().toString().trim().length() == 3 || pieces.getText().toString().trim().length() == 1) {
                        totalPcsCommand = "TEXT 310,310,\"ROMAN.TTF\",0,12,12,\"" + StringUtils.center("" + (i + 1), 4) + "\"\n";
                    } else {
                        totalPcsCommand = "TEXT 295,310,\"ROMAN.TTF\",0,12,12,\"" + StringUtils.center("" + (i + 1), 4) + "\"\n";
                    }

                    TscUsb.sendcommandUTF8("SIZE 75 mm, 50 mm\n" +
                            "DIRECTION 1\n" +
                            "SPEED 10\n" +
                            conNoCommand +
                            "TEXT 40,295,\"ROMAN.TTF\",0,35,35,\"" + StringUtils.rightPad(origin.getText().toString().trim().toUpperCase(), 4) + "\"\n" +
                            totalPcsCommand +
                            "TEXT 390,295,\"ROMAN.TTF\",0,30,30,\"" + StringUtils.leftPad(pieces.getText().toString(), 4) + "\"\n" +
                            "PRINT 1,1\n"
                    );
                }
            } else {
                int fromNo = Integer.parseInt(fromNumEt.getText().toString());
                int toNo = Integer.parseInt(toNumEt.getText().toString());

                for (int i = fromNo; i <= toNo; i++) {
                    TscUsb.clearbuffer();
                    if(connumber.getText().toString().trim().length() == 2 || connumber.getText().toString().trim().length() == 4 || connumber.getText().toString().trim().length() == 6||connumber.getText().toString().trim().length() == 7) {
                        conNoCommand = "TEXT 47,100,\"ROMAN.TTF\",0,63,63,\"" + StringUtils.center(connumber.getText().toString().trim(), 7) + "\"\n";
                    } else {
                        if(connumber.getText().toString().trim().length() == 5) {
                            conNoCommand = "TEXT 105,100,\"ROMAN.TTF\",0,63,63,\"" + StringUtils.center(connumber.getText().toString().trim(), 7) + "\"\n";
                        } else if(connumber.getText().toString().trim().length() == 3) {
                            conNoCommand = "TEXT 155,100,\"ROMAN.TTF\",0,63,63,\"" + StringUtils.center(connumber.getText().toString().trim(), 7) + "\"\n";
                        } else {
                            conNoCommand = "TEXT 195,100,\"ROMAN.TTF\",0,63,63,\"" + StringUtils.center(connumber.getText().toString().trim(), 7) + "\"\n";
                        }
                    }
                    if(pieces.getText().toString().trim().length() == 3 || pieces.getText().toString().trim().length() == 1) {
                        totalPcsCommand = "TEXT 310,310,\"ROMAN.TTF\",0,12,12,\"" + StringUtils.center("" + (i), 4) + "\"\n";
                    } else {
                        totalPcsCommand = "TEXT 295,310,\"ROMAN.TTF\",0,12,12,\"" + StringUtils.center("" + (i), 4) + "\"\n";
                    }

                    TscUsb.sendcommandUTF8("SIZE 75 mm, 50 mm\n" +
                            "DIRECTION 1\n" +
                            "SPEED 10\n" +
                            conNoCommand +
                            "TEXT 40,295,\"ROMAN.TTF\",0,35,35,\"" + StringUtils.rightPad(origin.getText().toString().trim().toUpperCase(), 4) + "\"\n" +
                            totalPcsCommand +
                            "TEXT 390,295,\"ROMAN.TTF\",0,30,30,\"" + StringUtils.leftPad(pieces.getText().toString(), 4) + "\"\n" +
                            "PRINT 1,1\n"
                    );
                }

            }
            pieceNoLayout.setVisibility(View.GONE);
            reprint.setText("Print Specific Labels");
            isSpecific = false;
            fromNumEt.setText("");
            toNumEt.setText("");
            TscUsb.closeport();
//            if(progressDialog != null) {
//                progressDialog.dismiss();
//                progressDialog = null;
//            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private final Runnable update = new Runnable() {
        public void run() {
            usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
            HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
//            if(deviceList!=null) {
//                Toast.makeText(USBPrinting.this, "Connected Devices: " + deviceList.size(), Toast.LENGTH_SHORT).show();
//            }
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
            usbDevice = (UsbDevice) getIntent().getParcelableExtra(UsbManager.EXTRA_DEVICE);
            while (deviceIterator.hasNext()) {
//            Toast.makeText(USBPrinting.this,"in while",Toast.LENGTH_SHORT).show();
                usbDevice = deviceIterator.next();
            }
            if (usbManager != null && usbDevice != null) {
                if (!usbManager.hasPermission(usbDevice)) {
                    usbManager.requestPermission(usbDevice, mPermissionIntent);
                } else {
                    if (!executed) {
                        executed = true;
                    }
                }
            }
            USBPrinting.this.handler.postDelayed(update, 3000);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(update);
        try {
            if (mCommandService != null)
                mCommandService.stop();
        } catch (Exception e) {
        }
        super.onDestroy();
    }
}
