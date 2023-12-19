package com.example.mytestpeoject;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mytestpeoject.Utils.Globals;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class DeviceList extends Activity {
	private Context context;

	private BluetoothAdapter mBtAdapter;
	private ArrayAdapter<String> mPairedDevicesArrayAdapter;
	private ArrayAdapter<String> mNewDevicesArrayAdapter;
	private ArrayList<BluetoothDevice> mPairedDevicesArray = new ArrayList<BluetoothDevice>();
	private ArrayList<BluetoothDevice> mNewDevicesArray = new ArrayList<BluetoothDevice>();

	private ListView pairedListView, newDevicesListView;
	private Button scanBtn, closeBtn;
	private String noDevicePaired, noDevicesFound;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.device_list);

		context = DeviceList.this;


		setResult(Activity.RESULT_CANCELED);

		Globals.selectedDevice = null;

		noDevicePaired = getResources().getText(R.string.none_paired)
				.toString();
		noDevicesFound = getResources().getText(R.string.none_found).toString();

		mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.device_name);
		mNewDevicesArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.device_name);

		pairedListView = (ListView) findViewById(R.id.paired_devices);
		pairedListView.setAdapter(mPairedDevicesArrayAdapter);
		pairedListView.setOnItemClickListener(mDeviceClickListener);

		newDevicesListView = (ListView) findViewById(R.id.new_devices);
		newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
		newDevicesListView.setOnItemClickListener(mDeviceClickListener);

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(mReceiver, filter);

		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(mReceiver, filter);

		mBtAdapter = BluetoothAdapter.getDefaultAdapter();

		Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

		if (pairedDevices.size() > 0) {
			mPairedDevicesArrayAdapter.clear();
			mPairedDevicesArray.clear();
			findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
			for (BluetoothDevice device : pairedDevices) {
				if (device != null) {
//					if(device.getBluetoothClass().getMajorDeviceClass()==1536) {//major class number representing imaging devices (printers)
						mPairedDevicesArray.add(device);
						mPairedDevicesArrayAdapter.add(device.getName() + "\n"
								+ device.getAddress());
//					}
				}
			}
		} else {
			mPairedDevicesArray.add(null);
			mPairedDevicesArrayAdapter.add(noDevicePaired);
		}

		scanBtn = (Button) findViewById(R.id.scan_btn);
		scanBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doDiscovery();
				// v.setVisibility(View.GONE);
			}
		});
		closeBtn = (Button) findViewById(R.id.close_btn);
		closeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				close();
			}
		});
	}

	private void close() {
		if (USBPrinting.findDeviceList != null)
			USBPrinting.findDeviceList = null;

		Intent intent = new Intent();
		Globals.selectedDevice = null;
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (USBPrinting.findDeviceList != null)
			USBPrinting.findDeviceList = null;

		if (mBtAdapter != null) {
			mBtAdapter.cancelDiscovery();
		}

		this.unregisterReceiver(mReceiver);
	}

	private void doDiscovery() {
		mNewDevicesArrayAdapter.add(null);
		mNewDevicesArrayAdapter.clear();
		mNewDevicesArray.clear();
		setProgressBarIndeterminateVisibility(true);
		setTitle(R.string.scanning);

		findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

		if (mBtAdapter.isDiscovering()) {
			mBtAdapter.cancelDiscovery();
		}

		mBtAdapter.startDiscovery();
	}

	private final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
				final int state        = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
				final int prevState    = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

				if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
					showToast("Paired");
				} else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
					showToast("Unpaired");
				}

			}
		}
	};

	public void showToast(String s) {
		Toast.makeText(context,s,Toast.LENGTH_SHORT).show();
	}

	private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> listView, View v, int index,
								long arg3) {

			if (listView == pairedListView) {
				selectedDevice = mPairedDevicesArray.get(index);
//				String openport=MainActivity.TscDll.openport(selectedDevice.toString());
//				Log.d("openport",openport);
//				MainActivity.TscDll.sendcommandUTF8("SIZE 70 mm, 65 mm\n"+
//						"DIRECTION 1\n"+
//						"PUTBMP 10,10,\"spton1.BMP\"\n");
			} else if (listView == newDevicesListView) {
				selectedDevice = mNewDevicesArray.get(index);
				//testing
				try {
					Method method = selectedDevice.getClass().getMethod("createBond", (Class[]) null);
					method.invoke(selectedDevice, (Object[]) null);
					IntentFilter intent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
					registerReceiver(mPairReceiver, intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (selectedDevice != null) {

				Globals.selectedDevice = selectedDevice;
				String address = selectedDevice.getAddress();
				mBtAdapter.cancelDiscovery();
				Intent intent = new Intent();
				setResult(Activity.RESULT_OK, intent);

				if (USBPrinting.findDeviceList != null)
					USBPrinting.findDeviceList = null;

				finish();
			}
		}
	};
	private BluetoothDevice selectedDevice = null;

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device != null) {
					if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
						Log.d("class number",device.getBluetoothClass().getMajorDeviceClass()+"");
//						if(device.getBluetoothClass().getMajorDeviceClass()==1536) { //major class number representing imaging devices (printers)
							mNewDevicesArray.add(device);
							mNewDevicesArrayAdapter.add(device.getName() + "\n"
									+ device.getAddress());
//						}
					}
				}
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				setProgressBarIndeterminateVisibility(false);
				setTitle(R.string.select_device);
				if (mNewDevicesArrayAdapter.getCount() == 0) {
					mNewDevicesArray.add(null);
					mNewDevicesArrayAdapter.add(noDevicesFound);
				}
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			close();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

}