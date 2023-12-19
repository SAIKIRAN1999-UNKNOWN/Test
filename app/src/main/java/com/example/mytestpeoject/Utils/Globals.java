package com.example.mytestpeoject.Utils;

import android.bluetooth.BluetoothDevice;

public class Globals {

	public static boolean isHardCodeData = false;

	public static String lastErrMsg;

	public static int screenWidth;
	public static int screenHeight;

	public static String username;
	public static String password;

//	public static DocketData docketData;

	public static String custCode;
	public static String custVentCode;

//	public static ArrayList<DocketInfo> selectedDocketList = new ArrayList<DocketInfo>();

	public static BluetoothDevice selectedDevice;

	public static int selectedPrinterPos = -1;
	public static String[] printerArr = null;

	public static boolean isPrint = false;
	public static boolean isPause = false;
	public static boolean cancelPrint = false;
	public static boolean pausePrint = false;
	public static boolean resumePrint = false;
	public static boolean selectedPrint = false;
	public static String conNumber, origin, destination;
	public static int pausePrintAt = 0;
	public static boolean isZebraSelected, isTSCSelected;

	public static String BLUETOOTH_STATE;


//	public static SaveDocketInfo reprintSaveDocketInfo;

//	public static ArrayList<PrintDocketInfo> printDocketInfos = new ArrayList<PrintDocketInfo>();
}
