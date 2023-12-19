package com.example.mytestpeoject.Utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataSource {

    private SQLiteDatabase database;
    private DataHelper dbHelper;

    public ShardPreferences shardPreferences;

    public BranchCodes branchCodes;


    public DataSource(Context context) {
        try {
            dbHelper = new DataHelper(context);
            shardPreferences = new ShardPreferences();
            branchCodes = new BranchCodes();
        } catch (SQLiteException e) {
            Utils.logE(e.toString());
        } catch (VerifyError e) {
            Utils.logE(e.toString());
        }
    }

    @SuppressLint("NewApi")
    public void open() throws SQLException {

        if (database != null) {
            while (database.isOpen()) {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {

                }
            }
        }
        if ((database != null && !database.isOpen()) || database == null) {
            try {
                database = dbHelper.getWritableDatabase();
                // isDatabaseOpen = true;
            }
            // catch (SQLiteDatabaseLockedException e) {
            // Utils.logE(e.toString());
            // }
            catch (Exception e) {
                Utils.logE(e.toString());
            }
        }
    }

    public void openRead() throws SQLException {
        if ((database != null && !database.isOpen()) || database == null) {
            database = dbHelper.getReadableDatabase();
            // isDatabaseOpen = true;
        }
    }

    public void close() {
        if ((database != null && database.isOpen())) {
            dbHelper.close();
            // isDatabaseOpen = false;
        }
    }

    public void dropAllTables() {
        open();
        if (database != null) {
//            database.execSQL("DROP TABLE IF EXISTS " + DataHelper.SHARED_PREF_TABLE_NAME);
//            database.execSQL("DROP TABLE IF EXISTS " + DataHelper.PINCODE_TABLE_NAME);
            database.execSQL("DROP TABLE IF EXISTS " + DataHelper.PUD_TABLE_NAME);
            database.execSQL("DROP TABLE IF EXISTS " + DataHelper.PIECE_TABLE_NAME);
            database.execSQL("DROP TABLE IF EXISTS " + DataHelper.PAYMENTMODE_TABLE_NAME);
            database.execSQL("DROP TABLE IF EXISTS " + DataHelper.CANCELSTATUS_TABLE_NAME);
            database.execSQL("DROP TABLE IF EXISTS " + DataHelper.CANCELREASON_TABLE_NAME);
            database.execSQL("DROP TABLE IF EXISTS " + DataHelper.DKTMASTER_TABLE_NAME);
//            database.execSQL("DROP TABLE IF EXISTS " + DataHelper.CON_TABLE_NAME);
            database.execSQL("DROP TABLE IF EXISTS " + DataHelper.DELUNDEL_CODE_TABLE_NAME);
            database.execSQL("DROP TABLE IF EXISTS " + DataHelper.RELATIONSHIP_TABLE_NAME);
            database.execSQL("DROP TABLE IF EXISTS " + DataHelper.DEL_TABLE_NAME);
            DataHelper.createTables(database);
        }
        close();
//        Utils.logD("Tables Dropped");
    }

    public void resetAllTables() {
        open();
        if (database != null) {
//            database.execSQL("DROP TABLE IF EXISTS " + DataHelper.SHARED_PREF_TABLE_NAME);
            database.execSQL("DROP TABLE IF EXISTS " + DataHelper.PINCODE_TABLE_NAME);
            database.execSQL("DROP TABLE IF EXISTS " + DataHelper.PUD_TABLE_NAME);
            database.execSQL("DROP TABLE IF EXISTS " + DataHelper.PIECE_TABLE_NAME);
            database.execSQL("DROP TABLE IF EXISTS " + DataHelper.PAYMENTMODE_TABLE_NAME);
            database.execSQL("DROP TABLE IF EXISTS " + DataHelper.CANCELSTATUS_TABLE_NAME);
            database.execSQL("DROP TABLE IF EXISTS " + DataHelper.CANCELREASON_TABLE_NAME);
            database.execSQL("DROP TABLE IF EXISTS " + DataHelper.DKTMASTER_TABLE_NAME);
//            database.execSQL("DROP TABLE IF EXISTS " + DataHelper.CON_TABLE_NAME);
            database.execSQL("DROP TABLE IF EXISTS " + DataHelper.DELUNDEL_CODE_TABLE_NAME);
            database.execSQL("DROP TABLE IF EXISTS " + DataHelper.RELATIONSHIP_TABLE_NAME);
            database.execSQL("DROP TABLE IF EXISTS " + DataHelper.DEL_TABLE_NAME);
            DataHelper.createTablesonReset(database);
        }
        close();
//        Utils.logD("Reset Tables Dropped");
    }

    // ************** SHARED_PREFERENCES **************//

    public class ShardPreferences {

        public void set(String key, String value) {
            open();
            try {
                if (!Utils.isValidString(value))
                    value = "";

                if (Utils.isValidString(key)) {
                    int id = exists(key);
                    if (id == -1) {
                        create(key, value);
                    } else {
                        update(key, value);
                    }
                }
            } catch (Exception e) {
                Utils.logE(e.toString());
            } finally {
                close();
            }
        }

        public void update(String key, String value) {
            open();
            try {
                ContentValues values = new ContentValues();
                values.put(DataHelper.SHARED_PREF_COLUMN_VALUE, value);

                if (database.update(DataHelper.SHARED_PREF_TABLE_NAME, values,
                        DataHelper.SHARED_PREF_COLUMN_KEY + " = '" + key + "'",
                        null) > 0) {
                } else {
                }
            } catch (Exception e) {
                Utils.logE(e.toString());
            } finally {
                close();
            }

        }

        public void create(String key, String value) {
            open();
            try {
                ContentValues values = new ContentValues();
                values.put(DataHelper.SHARED_PREF_COLUMN_KEY, key);
                values.put(DataHelper.SHARED_PREF_COLUMN_VALUE, value);

                if (database.insert(DataHelper.SHARED_PREF_TABLE_NAME, null,
                        values) > 0) {
                } else {
                }
            } catch (Exception e) {
                Utils.logE(e.toString());
            } finally {
                close();
            }
        }

        public void delete(String key) {
            open();
            try {
                if (database.delete(DataHelper.SHARED_PREF_TABLE_NAME,
                        DataHelper.SHARED_PREF_COLUMN_KEY + " = '" + key + "'",
                        null) > 0) {
                } else {
                }
            } catch (Exception e) {
                Utils.logE(e.toString());
            } finally {
                close();
            }
        }

        public List<SharedPreferenceItem> getAll() {
            openRead();
            List<SharedPreferenceItem> items = new ArrayList<SharedPreferenceItem>();
            Cursor cursor = null;
            try {

                cursor = database.query(DataHelper.SHARED_PREF_TABLE_NAME,
                        DataHelper.SHARED_PREF_COLUMNS, null, null, null, null,
                        null);

                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    SharedPreferenceItem item = cursorToItem(cursor);
                    items.add(item);
                    cursor.moveToNext();
                }

            } catch (Exception e) {
                Utils.logE(e.toString());
                cursor = null;
            } finally {
                if (cursor != null)
                    cursor.close();
                close();
            }
            return items;
        }

        public String getValue(String key) {
            openRead();

            Cursor cursor = null;
            String value = "";

            try {
                String selectQuery = "SELECT  "
                        + DataHelper.SHARED_PREF_COLUMN_VALUE + " FROM "
                        + DataHelper.SHARED_PREF_TABLE_NAME + " WHERE "
                        + DataHelper.SHARED_PREF_COLUMN_KEY + " = '" + key
                        + "'";

                cursor = database.rawQuery(selectQuery, null);

                if (cursor.moveToFirst()) {
                    value = cursor.getString(0);
                }

            } catch (Exception e) {
                Utils.logE(e.toString());
                cursor = null;
            } finally {
                if (cursor != null)
                    cursor.close();
                close();
            }
            return value;

        }

        public int exists(String key) {
            openRead();
            int id = -1;
            Cursor cursor = null;
            try {
                String selectQuery = "SELECT  " + DataHelper.SHARED_PREF_KEY_ID
                        + " FROM " + DataHelper.SHARED_PREF_TABLE_NAME
                        + " WHERE " + DataHelper.SHARED_PREF_COLUMN_KEY
                        + " = '" + key + "'";

                cursor = database.rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    id = cursor.getInt(0);
                }
            } catch (Exception e) {
                Utils.logE(e.toString());
            } finally {
                if (cursor != null)
                    cursor.close();
                close();
            }

            return id;
        }

        private SharedPreferenceItem cursorToItem(Cursor cursor) {
            return new SharedPreferenceItem(cursor.getString(0),
                    cursor.getString(1));
        }
    }


    public int getVersion() {
        openRead();
        int version = 0;
        Cursor cursor = null;
        String query = "SELECT MAX(" + DataHelper.PINCODE_COLUMN_VERSION + ") FROM " + DataHelper.PINCODE_TABLE_NAME;
        Utils.logD("SQL Query " + query);
        try {
            cursor = database.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                version = cursor.getInt(0);
            }
        } catch (Exception e) {
            Utils.logE(e.toString());
        } finally {
            if (cursor != null)
                cursor.close();
            close();
        }


        return version;
    }

//    public int exists(int pin) {
//        int id = -1;
//        Cursor cursor = null;
//        try {
//            String selectQuery = "SELECT  " + DataHelper.PINCODE_COLUMN_KEY_ID
//                    + " FROM " + DataHelper.PINCODE_TABLE_NAME + " WHERE "
//                    + DataHelper.PINCODE_COLUMN_PINCODE + " = " + pin;
//
//            cursor = database.rawQuery(selectQuery, null);
//            if (cursor.moveToFirst()) {
//                id = cursor.getInt(0);
//            }
//        } catch (Exception e) {
//            Utils.logE(e.toString());
//        } finally {
//            if (cursor != null)
//                cursor.close();
//        }
//
//        return id;
//    }

    public class BranchCodes {

        public int isBranchActive(String branch) {
            openRead();
            int id = -1;
            Cursor cursor = null;
            try {
                String selectQuery = "SELECT  " + DataHelper.BRANCH_CODES_ACTIVE
                        + " FROM " + DataHelper.BRANCH_CODES_TABLE_NAME
                        + " WHERE " + DataHelper.BRANCH_CODES_BRANCH_CODE
                        + " = '" + branch + "'";

                cursor = database.rawQuery(selectQuery, null);
                int idxActive = cursor
                        .getColumnIndex(DataHelper.BRANCH_CODES_ACTIVE);
                if (cursor.moveToFirst()) {
                    id = cursor.getInt(idxActive);
                }
            } catch (Exception e) {
                Utils.logE(e.toString());
            } finally {
                if (cursor != null)
                    cursor.close();
                close();
            }

            return id;
        }




        public int exists(String key) {
            openRead();
            int id = -1;
            Cursor cursor = null;
            try {
                String selectQuery = "SELECT  " + DataHelper.BRANCH_CODES_KEY_ID
                        + " FROM " + DataHelper.BRANCH_CODES_TABLE_NAME
                        + " WHERE " + DataHelper.BRANCH_CODES_BRANCH_CODE
                        + " = '" + key + "'";

                cursor = database.rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    id = cursor.getInt(0);
                }
            } catch (Exception e) {
                Utils.logE(e.toString());
            } finally {
                if (cursor != null)
                    cursor.close();
                close();
            }

            return id;
        }

    }
}



