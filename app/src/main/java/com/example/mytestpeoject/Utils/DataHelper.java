package com.example.mytestpeoject.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataHelper extends SQLiteOpenHelper {

    public final static int DATABASE_VERSION = 2; //changed on 18/04/18 when Branchcodes validation is implemented
    public final static String DATABASE_NAME = "pud";

    /**
     * ************* SHARED_PREFERENCES **************
     **/
    public static final String SHARED_PREF_TABLE_NAME = "sharedpreferences";
    public final static String SHARED_PREF_KEY_ID = "SHARED_PREF_KEY_ID";
    public static final String SHARED_PREF_COLUMN_KEY = "KEY";
    public static final String SHARED_PREF_COLUMN_VALUE = "VALUE";

    public static final String SHARED_PREF_DATABASE_CREATE = "create table "
            + SHARED_PREF_TABLE_NAME + "(" + SHARED_PREF_KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + SHARED_PREF_COLUMN_KEY
            + " text not null, " + SHARED_PREF_COLUMN_VALUE
            + " text not null);";

    public static final String[] SHARED_PREF_COLUMNS = {
            SHARED_PREF_COLUMN_KEY, SHARED_PREF_COLUMN_VALUE};

    /***
     * ***********************BRANCH CODES *********************************
     * **/

    public static final String BRANCH_CODES_TABLE_NAME = "branchCodes";
    public static final String BRANCH_CODES_KEY_ID = "KEY_ID";
    public static final String BRANCH_CODES_BRANCH_CODE = "BRANCH_CODE";
    public static final String BRANCH_CODES_ACTIVE = "ACTIVE";
    public static final String BRANCH_CODES_VERSION = "VERSION";

    public static final String BRANCH_CODES_DATABASE_CREATE = "create table " +
            BRANCH_CODES_TABLE_NAME + "(" + BRANCH_CODES_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            BRANCH_CODES_BRANCH_CODE + " TEXT NOT NULL, " + BRANCH_CODES_ACTIVE + " INTEGER, " + BRANCH_CODES_VERSION + " INTEGER" + ")";

    /**
     * ************* Pincodes Data **************
     **/
    public final static String PINCODE_TABLE_NAME = "pincodeData";

    public final static String PINCODE_COLUMN_KEY_ID = "PINCODE_KEY_ID";
    public final static String PINCODE_COLUMN_PINCODE = "PINCODE_PINCODE";
    public final static String PINCODE_COLUMN_SERVICABLE = "PINCODE_SERVICABLE";
    public final static String PINCODE_COLUMN_BRANCHCODE = "PINCODE_BRANCHCODE";
    public final static String PINCODE_COLUMN_LOCATIONNAME = "PINCODE_LOCATIONNAME";
    public final static String PINCODE_COLUMN_ACTIVE = "PINCODE_ACTIVE";
    public final static String PINCODE_COLUMN_ACTIVESTATUS = "PINCODE_ACTIVESTATUS";
    public final static String PINCODE_COLUMN_SERVICETYPE = "PINCODE_SERVICETYPE";
    public final static String PINCODE_COLUMN_VERSION = "PINCODE_VERSION";

    public final static String PINCODE_DATABASE_CREATE = "create table "
            + PINCODE_TABLE_NAME + "(" + PINCODE_COLUMN_KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + PINCODE_COLUMN_PINCODE
            + " INTEGER, " + PINCODE_COLUMN_SERVICABLE + " TEXT, " + PINCODE_COLUMN_BRANCHCODE
            + " TEXT, " + PINCODE_COLUMN_LOCATIONNAME + " TEXT, "
            + PINCODE_COLUMN_ACTIVE + " TEXT, "
            + PINCODE_COLUMN_ACTIVESTATUS + " TEXT, "
            + PINCODE_COLUMN_SERVICETYPE + " TEXT, " + PINCODE_COLUMN_VERSION
            + " INTEGER)";

    public static final String[] PINCODE_COLUMNS = {PINCODE_COLUMN_PINCODE,
            PINCODE_COLUMN_SERVICABLE, PINCODE_COLUMN_BRANCHCODE, PINCODE_COLUMN_LOCATIONNAME,
            PINCODE_COLUMN_ACTIVE, PINCODE_COLUMN_ACTIVESTATUS,
            PINCODE_COLUMN_SERVICETYPE, PINCODE_COLUMN_VERSION};


    /**
     * ************* PUD Data **************
     **/
    public final static String PUD_TABLE_NAME = "pud_data";

    public final static String PUD_COLUMN_KEY_ID = "PUD_KEY_ID";
    public final static String PUD_COLUMN_ID = "PUD_ID";
    public final static String PUD_COLUMN_CFT = "PUD_CFT";
    public final static String PUD_COLUMN_CONTACTMOBILE = "PUD_CONTACTMOBILE";
    public final static String PUD_COLUMN_CONTACTPERSON = "PUD_CONTACTPERSON";
    public final static String PUD_COLUMN_CUSTOMERCODE = "PUD_CUSTOMERCODE";
    public final static String PUD_COLUMN_CUSTOMERNAME = "PUD_CUSTOMERNAME";
    public final static String PUD_COLUMN_CUSTOMERREFNUMBER = "PUD_CUSTOMERREFNUMBER";
    public final static String PUD_COLUMN_DELIVERYADDRESS = "PUD_DELIVERYADDRESS";
    public final static String PUD_COLUMN_DELIVERYCITY = "PUD_DELIVERYCITY";
    public final static String PUD_COLUMN_DELIVERYPINCODE = "PUD_DELIVERYPINCODE";
    public final static String PUD_COLUMN_MINCFGWEIGHT = "PUD_MINCFGWEIGHT";
    public final static String PUD_COLUMN_PICKUPADDRESS = "PUD_PICKUPADDRESS";
    public final static String PUD_COLUMN_PICKUPCITY = "PUD_PICKUPCITY";
    public final static String PUD_COLUMN_PICKUPDATE = "PUD_PICKUPDATE";
    public final static String PUD_COLUMN_PICKUPORDERNO = "PUD_PICKUPORDERNO";
    public final static String PUD_COLUMN_PICKUPPINCODE = "PUD_PICKUPPINCODE";
    public final static String PUD_COLUMN_PICKUPREGISTERID = "PUD_PICKUPREGISTERID";
    public final static String PUD_COLUMN_PICKUPSCHEDULEID = "PUD_PICKUPSCHEDULEID";
    public final static String PUD_COLUMN_PIECES = "PUD_PIECES";
    public final static String PUD_COLUMN_WEIGHT = "PUD_WEIGHT";
    public final static String PUD_COLUMN_USERID = "PUD_USERID";

    public final static String PUD_COLUMN_STATUS = "PUD_STATUS";

    public final static String PUD_COLUMN_NO_OF_CONS = "PUD_NO_OF_CONS";
    public final static String PUD_COLUMN_CANCEL_REASON = "PUD_CANCEL_REASON";

    public final static String PUD_COLUMN_CON_LIST_JSON = "PUD_COLUMN_CON_LIST_JSON";

    public final static String PUD_DATABASE_CREATE = "create table "
            + PUD_TABLE_NAME + "(" + PUD_COLUMN_KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + PUD_COLUMN_ID + " INTEGER, "
            + PUD_COLUMN_CFT + " REAL, "
            + PUD_COLUMN_CONTACTMOBILE + " TEXT, "
            + PUD_COLUMN_CONTACTPERSON + " TEXT, "
            + PUD_COLUMN_CUSTOMERCODE + " TEXT, "
            + PUD_COLUMN_CUSTOMERNAME + " TEXT, "
            + PUD_COLUMN_CUSTOMERREFNUMBER + " TEXT, "
            + PUD_COLUMN_DELIVERYADDRESS + " TEXT, "
            + PUD_COLUMN_DELIVERYCITY + " TEXT, "
            + PUD_COLUMN_DELIVERYPINCODE + " INTEGER, "
            + PUD_COLUMN_MINCFGWEIGHT + " REAL, "
            + PUD_COLUMN_PICKUPADDRESS + " TEXT, "
            + PUD_COLUMN_PICKUPCITY + " TEXT, "
            + PUD_COLUMN_PICKUPDATE + " TEXT, "
            + PUD_COLUMN_PICKUPORDERNO + " TEXT, "
            + PUD_COLUMN_PICKUPPINCODE + " INTEGER, "
            + PUD_COLUMN_PICKUPREGISTERID + " INTEGER, "
            + PUD_COLUMN_PICKUPSCHEDULEID + " INTEGER, "
            + PUD_COLUMN_PIECES + " INTEGER, "
            + PUD_COLUMN_WEIGHT + " REAL, "
            + PUD_COLUMN_USERID + " TEXT, "
            + PUD_COLUMN_STATUS + " INTEGER, "
            + PUD_COLUMN_NO_OF_CONS + " INTEGER, "
            + PUD_COLUMN_CANCEL_REASON + " TEXT, "
            + PUD_COLUMN_CON_LIST_JSON
            + " CLOB)";

    public static final String[] PUD_COLUMNS = {
            PUD_COLUMN_ID, PUD_COLUMN_CFT,
            PUD_COLUMN_CONTACTMOBILE, PUD_COLUMN_CONTACTPERSON,
            PUD_COLUMN_CUSTOMERCODE, PUD_COLUMN_CUSTOMERNAME,
            PUD_COLUMN_CUSTOMERREFNUMBER, PUD_COLUMN_DELIVERYADDRESS,
            PUD_COLUMN_DELIVERYCITY, PUD_COLUMN_DELIVERYPINCODE,
            PUD_COLUMN_MINCFGWEIGHT, PUD_COLUMN_PICKUPADDRESS,
            PUD_COLUMN_PICKUPCITY, PUD_COLUMN_PICKUPDATE,
            PUD_COLUMN_PICKUPORDERNO, PUD_COLUMN_PICKUPPINCODE,
            PUD_COLUMN_PICKUPREGISTERID, PUD_COLUMN_PICKUPSCHEDULEID,
            PUD_COLUMN_PIECES, PUD_COLUMN_WEIGHT,
            PUD_COLUMN_USERID, PUD_COLUMN_STATUS,
            PUD_COLUMN_NO_OF_CONS, PUD_COLUMN_CANCEL_REASON,
            PUD_COLUMN_CON_LIST_JSON
    };


    /**
     * ************* Piece Data **************
     **/
    public final static String PIECE_TABLE_NAME = "pieceData";

    public final static String PIECE_COLUMN_KEY_ID = "PIECE_KEY_ID";
    public final static String PIECE_COLUMN_ID = "PIECE_ID";
    public final static String PIECE_COLUMN_COMPLETE = "PIECE_COMPLETE";
    public final static String PIECE_COLUMN_ENDNO = "PIECE_ENDNO";
    public final static String PIECE_COLUMN_LASTPIECENO = "PIECE_LASTPIECENO";
    public final static String PIECE_COLUMN_PKEY = "PIECE_PKEY";
    public final static String PIECE_COLUMN_SERIESTYPE = "PIECE_SERIESTYPE";
    public final static String PIECE_COLUMN_STARTNO = "PIECE_STARTNO";
    public final static String PIECE_COLUMN_TOTALISSSUED = "PIECE_TOTALISSSUED";
    public final static String PIECE_COLUMN_TOTALUSED = "PIECE_TOTALUSED";
    public final static String PIECE_COLUMN_USERID = "PIECE_USERID";

    public final static String PIECE_DATABASE_CREATE = "create table "
            + PIECE_TABLE_NAME + "(" + PIECE_COLUMN_KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + PIECE_COLUMN_ID
            + " INTEGER, " + PIECE_COLUMN_COMPLETE + " TEXT, " + PIECE_COLUMN_ENDNO
            + " TEXT, " + PIECE_COLUMN_LASTPIECENO + " TEXT, "
            + PIECE_COLUMN_PKEY + " INTEGER, "
            + PIECE_COLUMN_SERIESTYPE + " TEXT, "
            + PIECE_COLUMN_STARTNO + " TEXT, "
            + PIECE_COLUMN_TOTALISSSUED + " TEXT, "
            + PIECE_COLUMN_TOTALUSED + " TEXT, "
            + PIECE_COLUMN_USERID
            + " TEXT)";

    public static final String[] PIECE_COLUMNS = {PIECE_COLUMN_ID,
            PIECE_COLUMN_COMPLETE, PIECE_COLUMN_ENDNO, PIECE_COLUMN_LASTPIECENO,
            PIECE_COLUMN_PKEY, PIECE_COLUMN_SERIESTYPE,
            PIECE_COLUMN_STARTNO, PIECE_COLUMN_TOTALISSSUED, PIECE_COLUMN_TOTALUSED, PIECE_COLUMN_USERID};


    /**
     * ************* PaymentMode Data **************
     **/
    public final static String PAYMENTMODE_TABLE_NAME = "paymentMode";

    public final static String PAYMENTMODE_COLUMN_KEY_ID = "PAYMENTMODE_KEY_ID";
    public final static String PAYMENTMODE_COLUMN_ID = "PAYMENTMODE_ID";
    public final static String PAYMENTMODE_COLUMN_PAYMENTMODE = "PAYMENTMODE_PAYMENTMODE";

    public final static String PAYMENTMODE_DATABASE_CREATE = "create table "
            + PAYMENTMODE_TABLE_NAME + "(" + PAYMENTMODE_COLUMN_KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + PAYMENTMODE_COLUMN_ID
            + " INTEGER, " + PAYMENTMODE_COLUMN_PAYMENTMODE
            + " TEXT)";

    public static final String[] PAYMENTMODE_COLUMNS = {PAYMENTMODE_COLUMN_ID,
            PAYMENTMODE_COLUMN_PAYMENTMODE};


    /**
     * ************* CancelStatus Data **************
     **/
    public final static String CANCELSTATUS_TABLE_NAME = "cancelStatus";

    public final static String CANCELSTATUS_COLUMN_KEY_ID = "CANCELSTATUS_KEY_ID";
    public final static String CANCELSTATUS_COLUMN_ID = "CANCELSTATUS_ID";
    public final static String CANCELSTATUS_COLUMN_STATUS = "CANCELSTATUS_STATUS";
    public final static String CANCELSTATUS_COLUMN_STATUSID = "CANCELSTATUS_STATUSID";

    public final static String CANCELSTATUS_DATABASE_CREATE = "create table "
            + CANCELSTATUS_TABLE_NAME + "(" + CANCELSTATUS_COLUMN_KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + CANCELSTATUS_COLUMN_ID
            + " INTEGER, " + CANCELSTATUS_COLUMN_STATUS + " TEXT, " + CANCELSTATUS_COLUMN_STATUSID
            + " INTEGER)";

    public static final String[] CANCELSTATUS_COLUMNS = {CANCELSTATUS_COLUMN_ID,
            CANCELSTATUS_COLUMN_STATUS, CANCELSTATUS_COLUMN_STATUSID};


    /**
     * ************* CancelReason Data **************
     **/
    public final static String CANCELREASON_TABLE_NAME = "cancelReason";

    public final static String CANCELREASON_COLUMN_KEY_ID = "CANCELREASON_KEY_ID";
    public final static String CANCELREASON_COLUMN_ID = "CANCELREASON_ID";
    public final static String CANCELREASON_COLUMN_ACTIVE_STATUS = "CANCELREASON_ACTIVE_STATUS";
    public final static String CANCELREASON_COLUMN_FAILURE_CAT = "CANCELREASON_FAILURE_CAT";
    public final static String CANCELREASON_COLUMN_PICKUP_STATUSID = "CANCELREASON_PICKUP_STATUSID";
    public final static String CANCELREASON_COLUMN_REASONCODE = "CANCELREASON_REASONCODE";
    public final static String CANCELREASON_COLUMN_REASONDESC = "CANCELREASON_REASONDESC";
    public final static String CANCELREASON_COLUMN_UPDATEDBY = "CANCELREASON_UPDATEDBY";
    public final static String CANCELREASON_COLUMN_UPDATEDON = "CANCELREASON_UPDATEDON";

    public final static String CANCELREASON_DATABASE_CREATE = "create table "
            + CANCELREASON_TABLE_NAME + "(" + CANCELREASON_COLUMN_KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + CANCELREASON_COLUMN_ID
            + " INTEGER, " + CANCELREASON_COLUMN_ACTIVE_STATUS + " INTEGER, " + CANCELREASON_COLUMN_FAILURE_CAT
            + " TEXT, " + CANCELREASON_COLUMN_PICKUP_STATUSID + " INTEGER, "
            + CANCELREASON_COLUMN_REASONCODE + " TEXT, "
            + CANCELREASON_COLUMN_REASONDESC + " TEXT, "
            + CANCELREASON_COLUMN_UPDATEDBY + " TEXT, " + CANCELREASON_COLUMN_UPDATEDON
            + " TEXT)";

    public static final String[] CANCELREASON_COLUMNS = {CANCELREASON_COLUMN_ID,
            CANCELREASON_COLUMN_ACTIVE_STATUS, CANCELREASON_COLUMN_FAILURE_CAT, CANCELREASON_COLUMN_PICKUP_STATUSID,
            CANCELREASON_COLUMN_REASONCODE, CANCELREASON_COLUMN_REASONDESC,
            CANCELREASON_COLUMN_UPDATEDBY, CANCELREASON_COLUMN_UPDATEDON};

    /**
     * ************* DocketMasters Data **************
     **/

    public final static String DKTMASTER_TABLE_NAME = "docketMastersData";

    public final static String DKTMASTER_COLUMN_KEY_ID = "DKTMASTER_KEY_ID";
    public final static String DKTMASTER_COLUMN_ID = "DKTMASTER_ID";
    public final static String DKTMASTER_COLUMN_DKT_USERKEY = "DKTMASTER_DKT_USERKEY";
    public final static String DKTMASTER_COLUMN_BRANCHCODE = "DKTMASTER_BRANCHCODE";
    public final static String DKTMASTER_COLUMN_DKT_KEY = "DKTMASTER_DKT_KEY";
    public final static String DKTMASTER_COLUMN_FROM = "DKTMASTER_FROM";
    public final static String DKTMASTER_COLUMN_LASTSR = "DKTMASTER_LASTSR";
    public final static String DKTMASTER_COLUMN_TO = "DKTMASTER_TO";
    public final static String DKTMASTER_COLUMN_TOTALLEAF = "DKTMASTER_TOTALLEAF";
    public final static String DKTMASTER_COLUMN_USERID = "DKTMASTER_USERID";
    public final static String DKTMASTER_COLUMN_USERNAME = "DKTMASTER_USERNAME";
    public final static String DKTMASTER_COLUMN_VENDORCODE = "DKTMASTER_VENDORCODE";
    public final static String DKTMASTER_COLUMN_TIMESTAMP = "DKTMASTER_TIMESTAMP";

    public final static String DKTMASTER_DATABASE_CREATE = "create table "
            + DKTMASTER_TABLE_NAME + "(" + DKTMASTER_COLUMN_KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + DKTMASTER_COLUMN_ID
            + " INTEGER, " + DKTMASTER_COLUMN_DKT_USERKEY + " TEXT, " + DKTMASTER_COLUMN_BRANCHCODE
            + " TEXT, " + DKTMASTER_COLUMN_DKT_KEY + " INTEGER, "
            + DKTMASTER_COLUMN_FROM + " TEXT, "
            + DKTMASTER_COLUMN_LASTSR + " TEXT, "
            + DKTMASTER_COLUMN_TO + " TEXT, "
            + DKTMASTER_COLUMN_TOTALLEAF + " TEXT, "
            + DKTMASTER_COLUMN_USERID + " INTEGER, "
            + DKTMASTER_COLUMN_USERNAME + " TEXT, "
            + DKTMASTER_COLUMN_VENDORCODE + " TEXT, " + DKTMASTER_COLUMN_TIMESTAMP
            + " LONG)";

    public static final String[] DKTMASTER_COLUMNS = {DKTMASTER_COLUMN_ID,
            DKTMASTER_COLUMN_DKT_USERKEY, DKTMASTER_COLUMN_BRANCHCODE, DKTMASTER_COLUMN_DKT_KEY,
            DKTMASTER_COLUMN_FROM, DKTMASTER_COLUMN_LASTSR,
            DKTMASTER_COLUMN_TO, DKTMASTER_COLUMN_TOTALLEAF,
            DKTMASTER_COLUMN_USERID, DKTMASTER_COLUMN_USERNAME,
            DKTMASTER_COLUMN_VENDORCODE, DKTMASTER_COLUMN_TIMESTAMP
    };


    /**
     * ************* Con Data **************
     **/
    public final static String CON_TABLE_NAME = "conData";

    public final static String CON_COLUMN_KEY_ID = "CON_KEY_ID";
    public final static String CON_COLUMN_CONNUMBER = "CON_NUMBER";
    public final static String CON_COLUMN_ORIGIN_BRANCH = "CON_ORIGIN_BRANCH";
    public final static String CON_COLUMN_DEST_BRANCH = "CON_DEST_BRANCH";
    public final static String CON_COLUMN_FROMNO = "CON_FROMNO";
    public final static String CON_COLUMN_TONO = "CON_TONO";
    public final static String CON_COLUMN_NOOFPCS = "CON_NOOFPCS";
    public final static String CON_COLUMN_USERNAME = "CON_USERNAME";
    public final static String CON_COLUMN_CUSTNAME = "CON_CUSTNAME";

    public final static String CON_DATABASE_CREATE = "create table "
            + CON_TABLE_NAME + "(" + CON_COLUMN_KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + CON_COLUMN_CONNUMBER
            + " TEXT, " + CON_COLUMN_ORIGIN_BRANCH + " TEXT, "
            + CON_COLUMN_DEST_BRANCH + " TEXT, "
            + CON_COLUMN_FROMNO + " INTEGER, "
            + CON_COLUMN_TONO + " INTEGER, "
            + CON_COLUMN_NOOFPCS + " INTEGER, "
            + CON_COLUMN_USERNAME + " INTEGER, "
            + CON_COLUMN_CUSTNAME + " TEXT)";

    public static final String[] CON_COLUMNS = {CON_COLUMN_CONNUMBER,
            CON_COLUMN_ORIGIN_BRANCH, CON_COLUMN_DEST_BRANCH, CON_COLUMN_FROMNO,
            CON_COLUMN_TONO, CON_COLUMN_NOOFPCS, CON_COLUMN_USERNAME, CON_COLUMN_CUSTNAME};

    // ************** OFFLINE REQUESTS **************//
    public final static String OFFLINE_REQ_TABLE_NAME = "offlineRequests";

    public final static String OFFLINE_REQ_COLUMN_KEY_ID = "OFFLINE_REQ_KEY_ID";
    public final static String OFFLINE_REQ_COLUMN_URL = "OFFLINE_REQ_URL";
    public final static String OFFLINE_REQ_COLUMN_QUERY = "OFFLINE_REQ_QUERY";
    public final static String OFFLINE_REQ_COLUMN_USERNAME = "OFFLINE_REQ_USERNAME";
    public final static String OFFLINE_REQ_COLUMN_ERR_MSG = "OFFLINE_REQ_ERR_MSG";

    public final static String OFFLINE_REQ_DATABASE_CREATE = "create table "
            + OFFLINE_REQ_TABLE_NAME + "(" + OFFLINE_REQ_COLUMN_KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + OFFLINE_REQ_COLUMN_URL
            + " TEXT," + OFFLINE_REQ_COLUMN_QUERY + " TEXT,"
            + OFFLINE_REQ_COLUMN_USERNAME + " TEXT,"
            + OFFLINE_REQ_COLUMN_ERR_MSG + " TEXT )";

    public static final String[] OFFLINE_REQ_COLUMNS = {
            OFFLINE_REQ_COLUMN_URL, OFFLINE_REQ_COLUMN_QUERY,
            OFFLINE_REQ_COLUMN_USERNAME, OFFLINE_REQ_COLUMN_ERR_MSG};

    /************** DelUndelCode Data ****************/

    public final static String DELUNDEL_CODE_TABLE_NAME = "delUndelCodeData";

    public final static String DELUNDEL_CODE_COLUMN_KEY_ID = "DELUNDEL_CODE_KEY_ID";
    public final static String DELUNDEL_CODE_COLUMN_DELUNDEL_CODE = "DELUNDEL_CODE";
    public final static String DELUNDEL_CODE_COLUMN_CODE = "DELUNDEL_CODE_CODE";
    public final static String DELUNDEL_CODE_COLUMN_SHORT_CODE = "DELUNDEL_CODE_SHORT_CODE";
    public final static String DELUNDEL_CODE_COLUMN_DESCRIPTION = "DELUNDEL_CODE_DESCRIPTION";

    public final static String DELUNDEL_CODE_DATABASE_CREATE = "create table "
            + DELUNDEL_CODE_TABLE_NAME + "(" + DELUNDEL_CODE_COLUMN_KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + DELUNDEL_CODE_COLUMN_DELUNDEL_CODE
            + " INTEGER, " + DELUNDEL_CODE_COLUMN_CODE + " INTEGER, "
            + DELUNDEL_CODE_COLUMN_SHORT_CODE + " TEXT, " + DELUNDEL_CODE_COLUMN_DESCRIPTION
            + " TEXT)";

    public static final String[] DELUNDEL_CODE_COLUMNS = {DELUNDEL_CODE_COLUMN_DELUNDEL_CODE,
            DELUNDEL_CODE_COLUMN_CODE, DELUNDEL_CODE_COLUMN_SHORT_CODE, DELUNDEL_CODE_COLUMN_DESCRIPTION};

    /**
     * ************* Relationship Data **************
     **/
    public final static String RELATIONSHIP_TABLE_NAME = "relationship";

    public final static String RELATIONSHIP_COLUMN_KEY_ID = "RELATIONSHIP_KEY_ID";
    public final static String RELATIONSHIP_COLUMN_CODE = "RELATIONSHIP_CODE";
    public final static String RELATIONSHIP_COLUMN_DESCRIPTION = "RELATIONSHIP_DESCRIPTION";

    public final static String RELATIONSHIP_DATABASE_CREATE = "create table "
            + RELATIONSHIP_TABLE_NAME + "(" + RELATIONSHIP_COLUMN_KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + RELATIONSHIP_COLUMN_CODE
            + " TEXT, " + RELATIONSHIP_COLUMN_DESCRIPTION
            + " TEXT)";

    public static final String[] RELATIONSHIP_COLUMNS = {RELATIONSHIP_COLUMN_CODE,
            RELATIONSHIP_COLUMN_DESCRIPTION};

    /**
     * ************* Del Data **************
     **/
    public final static String DEL_TABLE_NAME = "del_data";

    public final static String DEL_COLUMN_KEY_ID = "DEL_KEY_ID";
    public final static String DEL_COLUMN_AWBNUMBER = "DEL_AWBNUMBER";
    public final static String DEL_COLUMN_PDCNUMBER = "DEL_PDCNUMBER";
    public final static String DEL_COLUMN_REFERENCENO1 = "DEL_REFERENCENO1";
    public final static String DEL_COLUMN_REFERENCENO2 = "DEL_REFERENCENO2";
    public final static String DEL_COLUMN_NUMPCS = "DEL_NUMPCS";
    public final static String DEL_COLUMN_CONWEIGHT = "DEL_CONWEIGHT";
    public final static String DEL_COLUMN_CONSIGNEENAME = "DEL_CONSIGNEENAME";
    public final static String DEL_COLUMN_CONSIGNEECOMPANY = "DEL_CONSIGNEECOMPANY";
    public final static String DEL_COLUMN_CONSIGNEEADDRESS = "DEL_CONSIGNEEADDRESS";
    public final static String DEL_COLUMN_CONSIGNEECITY = "DEL_CONSIGNEECITY";
    public final static String DEL_COLUMN_CONSIGNEEPINCODE = "DEL_CONSIGNEEPINCODE";
    public final static String DEL_COLUMN_CONSIGNEEMOBILENO = "DEL_CONSIGNEEMOBILENO";
    public final static String DEL_COLUMN_CONSIGNEEEMAILID = "DEL_CONSIGNEEEMAILID";
    public final static String DEL_COLUMN_CONSIGNORNAME = "DEL_CONSIGNORNAME";
    public final static String DEL_COLUMN_CONSIGNORCITY = "DEL_CONSIGNORCITY";
    public final static String DEL_COLUMN_CONSIGNORMOBILENO = "DEL_CONSIGNORMOBILENO";
    public final static String DEL_COLUMN_CONSIGNOREMAILID = "DEL_CONSIGNOREMAILID";

    public final static String DEL_DATABASE_CREATE = "create table "
            + DEL_TABLE_NAME + "(" + DEL_COLUMN_KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + DEL_COLUMN_AWBNUMBER + " TEXT, "
            + DEL_COLUMN_PDCNUMBER + " TEXT, "
            + DEL_COLUMN_REFERENCENO1 + " TEXT, "
            + DEL_COLUMN_REFERENCENO2 + " TEXT, "
            + DEL_COLUMN_NUMPCS + " TEXT, "
            + DEL_COLUMN_CONWEIGHT + " REAL, "
            + DEL_COLUMN_CONSIGNEENAME + " TEXT, "
            + DEL_COLUMN_CONSIGNEECOMPANY + " TEXT, "
            + DEL_COLUMN_CONSIGNEEADDRESS + " TEXT, "
            + DEL_COLUMN_CONSIGNEECITY + " TEXT, "
            + DEL_COLUMN_CONSIGNEEPINCODE + " TEXT, "
            + DEL_COLUMN_CONSIGNEEMOBILENO + " TEXT, "
            + DEL_COLUMN_CONSIGNEEEMAILID + " TEXT, "
            + DEL_COLUMN_CONSIGNORNAME + " TEXT, "
            + DEL_COLUMN_CONSIGNORCITY + " TEXT, "
            + DEL_COLUMN_CONSIGNORMOBILENO + " TEXT, "
            + DEL_COLUMN_CONSIGNOREMAILID
            + " TEXT)";

    public static final String[] DEL_COLUMNS = {
            DEL_COLUMN_AWBNUMBER, DEL_COLUMN_PDCNUMBER,
            DEL_COLUMN_REFERENCENO1, DEL_COLUMN_REFERENCENO2,
            DEL_COLUMN_NUMPCS, DEL_COLUMN_CONWEIGHT,
            DEL_COLUMN_CONSIGNEENAME, DEL_COLUMN_CONSIGNEECOMPANY,
            DEL_COLUMN_CONSIGNEEADDRESS, DEL_COLUMN_CONSIGNEECITY,
            DEL_COLUMN_CONSIGNEEPINCODE, DEL_COLUMN_CONSIGNEEMOBILENO,
            DEL_COLUMN_CONSIGNEEEMAILID, DEL_COLUMN_CONSIGNORNAME,
            DEL_COLUMN_CONSIGNORCITY, DEL_COLUMN_CONSIGNORMOBILENO,
            DEL_COLUMN_CONSIGNOREMAILID
    };


//    /**
//     * ************* ConDetails Data **************
//     **/
//    public final static String CON_DETAILS_TABLE_NAME = "con_details_data";
//
//    public final static String CON_DETAILS_COLUMN_KEY_ID = "CON_DETAILS_KEY_ID";
//
//    public final static String CON_DETAILS_COLUMN_CONENTRYID = "CON_DETAILS_CONENTRYID";
//    public final static String CON_DETAILS_COLUMN_CONNUMBER = "CON_DETAILS_CONNUMBER";
//    public final static String CON_DETAILS_COLUMN_SCANNED = "CON_DETAILS_SCANNED";
//    public final static String CON_DETAILS_COLUMN_REFNUMBER = "CON_DETAILS_REFNUMBER";
//    public final static String CON_DETAILS_COLUMN_PICKUPDATE = "CON_DETAILS_PICKUPDATE";
//    public final static String CON_DETAILS_COLUMN_PRODUCT = "CON_DETAILS_PRODUCT";
//    public final static String CON_DETAILS_COLUMN_ORIGINPINCODE = "CON_DETAILS_ORIGINPINCODE";
//    public final static String CON_DETAILS_COLUMN_DESTINATIONPINCODE = "CON_DETAILS_DESTINATIONPINCODE";
//    public final static String CON_DETAILS_COLUMN_PAYMENTBASIS = "CON_DETAILS_PAYMENTBASIS";
//    public final static String CON_DETAILS_COLUMN_CUSTOMERCODE = "CON_DETAILS_CUSTOMERCODE";
//    public final static String CON_DETAILS_COLUMN_NOOFPACKAGE = "CON_DETAILS_NOOFPACKAGE";
//    public final static String CON_DETAILS_COLUMN_ACTUALWEIGHT = "CON_DETAILS_ACTUALWEIGHT";
//    public final static String CON_DETAILS_COLUMN_APPLYVTV = "CON_DETAILS_APPLYVTV";
//    public final static String CON_DETAILS_COLUMN_VTCAMOUNT = "CON_DETAILS_VTCAMOUNT";
//    public final static String CON_DETAILS_COLUMN_APPLYDC = "CON_DETAILS_APPLYDC";
//    public final static String CON_DETAILS_COLUMN_APPLYNFFORM = "CON_DETAILS_APPLYNFFORM";
//    public final static String CON_DETAILS_COLUMN_RISKTYPE = "CON_DETAILS_RISKTYPE";
//    public final static String CON_DETAILS_COLUMN_DECLAREDVALUE = "CON_DETAILS_DECLAREDVALUE";
//    public final static String CON_DETAILS_COLUMN_SPL_INS = "CON_DETAILS_SPL_INS";
//    public final static String CON_DETAILS_COLUMN_LATVALUE = "CON_DETAILS_LATVALUE";
//    public final static String CON_DETAILS_COLUMN_LONGVALUE = "CON_DETAILS_LONGVALUE";
//    public final static String CON_DETAILS_COLUMN_USERID = "CON_DETAILS_USERID";
//    public final static String CON_DETAILS_COLUMN_ORDERNO = "CON_DETAILS_ORDERNO";
//    public final static String CON_DETAILS_COLUMN_CRMSCHEDULEID = "CON_DETAILS_CRMSCHEDULEID";
//    public final static String CON_DETAILS_COLUMN_USERNAME = "CON_DETAILS_USERNAME";
//    public final static String CON_DETAILS_COLUMN_RECEIVERNAME = "CON_DETAILS_RECEIVERNAME";
//    public final static String CON_DETAILS_COLUMN_RECEIVERPHONENO = "CON_DETAILS_RECEIVERPHONENO";
//    public final static String CON_DETAILS_COLUMN_PACKAGETYPE = "CON_DETAILS_PACKAGETYPE";
//    public final static String CON_DETAILS_COLUMN_CONSIGNMENTTYPE = "CON_DETAILS_CONSIGNMENTTYPE";
//    public final static String CON_DETAILS_COLUMN_TOTALVOLWEIGHT = "CON_DETAILS_TOTALVOLWEIGHT";
//    public final static String CON_DETAILS_COLUMN_VOLTYPE = "CON_DETAILS_VOLTYPE";
//    public final static String CON_DETAILS_COLUMN_TINNO = "CON_DETAILS_TINNO";
//    public final static String CON_DETAILS_COLUMN_PANNO = "CON_DETAILS_PANNO";
//    public final static String CON_DETAILS_COLUMN_IMAGE1URL = "CON_DETAILS_IMAGE1URL";
//    public final static String CON_DETAILS_COLUMN_IMAGE2URL = "CON_DETAILS_IMAGE2URL";
//    public final static String CON_DETAILS_COLUMN_SHIPMENTIMAGEURL = "CON_DETAILS_SHIPMENTIMAGEURL";
//    public final static String CON_DETAILS_COLUMN_GATEPASSTIME = "CON_DETAILS_GATEPASSTIME";
//    public final static String CON_DETAILS_COLUMN_PIECES = "CON_DETAILS_PIECES";
//    public final static String CON_DETAILS_COLUMN_PIECEENTRY = "CON_DETAILS_PIECEENTRY";
//    public final static String CON_DETAILS_COLUMN_PIECEVOLUME = "CON_DETAILS_PIECEVOLUME";
//    public final static String CON_DETAILS_COLUMN_PIECESIMAGES1 = "CON_DETAILS_PIECESIMAGES1";
//    public final static String CON_DETAILS_COLUMN_PIECESIMAGES2 = "CON_DETAILS_PIECESIMAGES2";
//    public final static String CON_DETAILS_COLUMN_PIECESIMAGES3 = "CON_DETAILS_PIECESIMAGES3";
//    public final static String CON_DETAILS_COLUMN_PIECESIMAGES4 = "CON_DETAILS_PIECESIMAGES4";
//    public final static String CON_DETAILS_COLUMN_PIECESIMAGES5 = "CON_DETAILS_PIECESIMAGES5";
//
//
//    public final static String CON_DETAILS_DATABASE_CREATE = "create table "
//            + CON_DETAILS_TABLE_NAME + "(" + CON_DETAILS_COLUMN_KEY_ID
//            + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//            + CON_DETAILS_COLUMN_CONENTRYID + " INTEGER, "
//            + CON_DETAILS_COLUMN_CONNUMBER + " TEXT, "
//            + CON_DETAILS_COLUMN_SCANNED + " TEXT, "
//            + CON_DETAILS_COLUMN_REFNUMBER + " TEXT, "
//            + CON_DETAILS_COLUMN_PICKUPDATE + " TEXT, "
//            + CON_DETAILS_COLUMN_PRODUCT + " TEXT, "
//            + CON_DETAILS_COLUMN_ORIGINPINCODE + " TEXT, " + CON_DETAILS_COLUMN_DESTINATIONPINCODE + " TEXT, "
//            + CON_DETAILS_COLUMN_PAYMENTBASIS + " TEXT, " + CON_DETAILS_COLUMN_CUSTOMERCODE + " TEXT, "
//            + CON_DETAILS_COLUMN_NOOFPACKAGE + " TEXT, " + CON_DETAILS_COLUMN_ACTUALWEIGHT + " TEXT, "
//            + CON_DETAILS_COLUMN_APPLYVTV + " TEXT, " + CON_DETAILS_COLUMN_VTCAMOUNT + " TEXT, "
//            + CON_DETAILS_COLUMN_APPLYDC + " TEXT, " + CON_DETAILS_COLUMN_APPLYNFFORM + " TEXT, "
//            + CON_DETAILS_COLUMN_RISKTYPE + " TEXT, " + CON_DETAILS_COLUMN_DECLAREDVALUE + " TEXT, "
//            + CON_DETAILS_COLUMN_SPL_INS + " TEXT, " + CON_DETAILS_COLUMN_LATVALUE + " TEXT, "
//            + CON_DETAILS_COLUMN_LONGVALUE + " TEXT, " + CON_DETAILS_COLUMN_USERID + " TEXT, "
//            + CON_DETAILS_COLUMN_ORDERNO + " TEXT, " + CON_DETAILS_COLUMN_CRMSCHEDULEID + " TEXT, "
//            + DEL_COLUMN_CONSIGNORMOBILENO + " TEXT, "
//            + DEL_COLUMN_CONSIGNOREMAILID
//            + " TEXT)";
//
//    public static final String[] DEL_COLUMNS = {
//            DEL_COLUMN_AWBNUMBER, DEL_COLUMN_PDCNUMBER,
//            DEL_COLUMN_REFERENCENO1, DEL_COLUMN_REFERENCENO2,
//            DEL_COLUMN_NUMPCS, DEL_COLUMN_CONWEIGHT,
//            DEL_COLUMN_CONSIGNEENAME, DEL_COLUMN_CONSIGNEECOMPANY,
//            DEL_COLUMN_CONSIGNEEADDRESS, DEL_COLUMN_CONSIGNEECITY,
//            DEL_COLUMN_CONSIGNEEPINCODE, DEL_COLUMN_CONSIGNEEMOBILENO,
//            DEL_COLUMN_CONSIGNEEEMAILID, DEL_COLUMN_CONSIGNORNAME,
//            DEL_COLUMN_CONSIGNORCITY, DEL_COLUMN_CONSIGNORMOBILENO,
//            DEL_COLUMN_CONSIGNOREMAILID
//    };



    public DataHelper(Context context) {
        super(context, DataHelper.DATABASE_NAME, null,
                DataHelper.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DataHelper.SHARED_PREF_DATABASE_CREATE);
        database.execSQL(DataHelper.BRANCH_CODES_DATABASE_CREATE);
        database.execSQL(DataHelper.PINCODE_DATABASE_CREATE);
        database.execSQL(DataHelper.PUD_DATABASE_CREATE);
        database.execSQL(DataHelper.PIECE_DATABASE_CREATE);
        database.execSQL(DataHelper.PAYMENTMODE_DATABASE_CREATE);
        database.execSQL(DataHelper.CANCELSTATUS_DATABASE_CREATE);
        database.execSQL(DataHelper.CANCELREASON_DATABASE_CREATE);
        database.execSQL(DataHelper.DKTMASTER_DATABASE_CREATE);
        database.execSQL(DataHelper.CON_DATABASE_CREATE);
        database.execSQL(DataHelper.OFFLINE_REQ_DATABASE_CREATE);
        database.execSQL(DataHelper.DELUNDEL_CODE_DATABASE_CREATE);
        database.execSQL(DataHelper.RELATIONSHIP_DATABASE_CREATE);
        database.execSQL(DataHelper.DEL_DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        Utils.logI("Upgrading database from version " + oldVersion + " to "
//                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + DataHelper.BRANCH_CODES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataHelper.PINCODE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataHelper.PUD_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataHelper.PIECE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataHelper.PAYMENTMODE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataHelper.CANCELSTATUS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataHelper.CANCELREASON_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataHelper.DKTMASTER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataHelper.CON_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataHelper.DELUNDEL_CODE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataHelper.RELATIONSHIP_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataHelper.DEL_TABLE_NAME);
        onCreateTables(db);
    }

    public void onCreateTables(SQLiteDatabase database) {
        database.execSQL(DataHelper.BRANCH_CODES_DATABASE_CREATE);
        database.execSQL(DataHelper.PINCODE_DATABASE_CREATE);
        database.execSQL(DataHelper.PUD_DATABASE_CREATE);
        database.execSQL(DataHelper.PIECE_DATABASE_CREATE);
        database.execSQL(DataHelper.PAYMENTMODE_DATABASE_CREATE);
        database.execSQL(DataHelper.CANCELSTATUS_DATABASE_CREATE);
        database.execSQL(DataHelper.CANCELREASON_DATABASE_CREATE);
        database.execSQL(DataHelper.DKTMASTER_DATABASE_CREATE);
        database.execSQL(DataHelper.CON_DATABASE_CREATE);
        database.execSQL(DataHelper.DELUNDEL_CODE_DATABASE_CREATE);
        database.execSQL(DataHelper.RELATIONSHIP_DATABASE_CREATE);
        database.execSQL(DataHelper.DEL_DATABASE_CREATE);
    }

    public static void createTables(SQLiteDatabase database) {
        database.execSQL(DataHelper.PUD_DATABASE_CREATE);
        database.execSQL(DataHelper.PIECE_DATABASE_CREATE);
        database.execSQL(DataHelper.PAYMENTMODE_DATABASE_CREATE);
        database.execSQL(DataHelper.CANCELSTATUS_DATABASE_CREATE);
        database.execSQL(DataHelper.CANCELREASON_DATABASE_CREATE);
        database.execSQL(DataHelper.DKTMASTER_DATABASE_CREATE);
        database.execSQL(DataHelper.DELUNDEL_CODE_DATABASE_CREATE);
        database.execSQL(DataHelper.RELATIONSHIP_DATABASE_CREATE);
        database.execSQL(DataHelper.DEL_DATABASE_CREATE);
    }

    public static void createTablesonReset(SQLiteDatabase database) {
        database.execSQL(DataHelper.PINCODE_DATABASE_CREATE);
        database.execSQL(DataHelper.PUD_DATABASE_CREATE);
        database.execSQL(DataHelper.PIECE_DATABASE_CREATE);
        database.execSQL(DataHelper.PAYMENTMODE_DATABASE_CREATE);
        database.execSQL(DataHelper.CANCELSTATUS_DATABASE_CREATE);
        database.execSQL(DataHelper.CANCELREASON_DATABASE_CREATE);
        database.execSQL(DataHelper.DKTMASTER_DATABASE_CREATE);
        database.execSQL(DataHelper.DELUNDEL_CODE_DATABASE_CREATE);
        database.execSQL(DataHelper.RELATIONSHIP_DATABASE_CREATE);
        database.execSQL(DataHelper.DEL_DATABASE_CREATE);
    }


    public void dropAllTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + DataHelper.SHARED_PREF_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataHelper.PINCODE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataHelper.PUD_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataHelper.PIECE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataHelper.PAYMENTMODE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataHelper.CANCELSTATUS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataHelper.CANCELREASON_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataHelper.DKTMASTER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataHelper.CON_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataHelper.DELUNDEL_CODE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataHelper.RELATIONSHIP_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DataHelper.DEL_TABLE_NAME);
    }



}
