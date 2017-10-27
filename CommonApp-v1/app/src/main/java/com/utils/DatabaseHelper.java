package com.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.commonapp.App;


public class DatabaseHelper extends SQLiteOpenHelper {

    String TAG = "==DatabaseHelper==";

    //public static final String DATABASE_NAME = "/storage/emulated/0/Android/data/elite.gift_x/files/Pictures" + File.separator + "gift_x.db";

    public SQLiteDatabase myDataBase;
    public SQLiteStatement statement;
    private static final int DATABASE_VERSION = 1;


    public static final String TABLE_COUNTRY = "Country";
    public static final String TABLE_STATE = "State";
    public static final String TABLE_CITY = "City";
    private static final String COUNTRY_NAME = "countryname";


    public static final String TABLE_ALERT = "TBL_ALERT";
    public static final String TABLE_LABEL = "TBL_LABEL";

    //Screen title name on api key names english names
    // http://webzzserverurl.com/GiftX/ws.giftx.php?op=List_AppLabels&lid=1

    public static final String STR_TR_USER_MAIN_SCREEN= "TrUserMainScreen";
    public static final String STR_TR_COMPLETE_PAYMENT_SCREEN= "TrCompletePaymentScreen";
    public static final String STR_TR_FORGOT_PASSWORD_SCREEN= "TrForgotPasswordScreen";
    public static final String STR_TR_LOGIN_SCREEN= "TrLoginScreen";
    public static final String STR_TR_MY_LOYALTY_POINTS_SCREEN= "TrMyLoyaltyPointsScreen";
    public static final String STR_TR_PAY_BY_CREDIT_CARD_SCREEN= "TrPayByCreditCardScreen";
    public static final String STR_TR_PAYMENT_RECEIVED_SCREEN= "TrPaymentReceivedScreen";
    public static final String STR_TR_PREVIOUS_BOOKING_DETAIL_SCREEN= "TrPreviousBookingDetailScreen";
    public static final String STR_TR_REGISTER_SCREEN= "TrRegisterScreen";
    public static final String STR_TR_SETTING_SCREEN= "TrSettingsScreen";
    public static final String STR_TR_SLIDE_MENU_SCREEN= "TrSliderMenuScreen";
    public static final String STR_TR_WALLET_SCREEN= "TrWalletScreen";
    public static final String STR_TR_MY_BOOKING_SCREEN= "TrMyBookingScreen";
    public static final String STR_TR_DASHBOARD_SCREEN= "TrDashboardScreen";
    public static final String STR_TR_PAYMENT_HISTORY_SCREEN= "TrPaymentHistory";
    public static final String STR_TR_CHANGE_PASSWORD_SCREEN= "TrChangePassword";
    public static final String STR_TR_ADD_MONEY_SCREEN= "TrAddMoney";
    public static final String STR_ACTSTARTNAVIGATIONORRIDE = "ActStartNavigationOrRide";
    public static final String STR_DASHBOARDSCREEN = "DashboardScreen";



    public static final String STR_LOGINSCREEN = "LoginScreen";
    public static final String STR_MYEARNINGSCREEN = "MyEarningsScreen";
    public static final String STR_MYTRIPSCREEN = "MyTripsScreen";
    public static final String STR_REGISTERSCREEN = "RegisterScreen";
    public static final String STR_TOTALFARESCREEN = "TotalFareScreen";

    public static final String STR_ABOUTUSSCREEN = "AboutUsScreen";
    public static final String STR_BOTTOMMENU = "BottomMenu";
    public static final String STR_CHAGEPASSWORDSCREEN= "ChagePasswordScreen";
    public static final String STR_COMDEALDETAILSCREEN = "ComDealDetailScreen";
    public static final String STR_CONTACTINFOSCREEN = "ContactInfoScreen";
    public static final String STR_CREATEADEALSCREEN = "CreateaDealScreen";
    public static final String STR_CREATEPROFILESCREEN = "CreateProfileScreen";
    public static final String STR_DEALREQUESTSCREEN = "DealRequestScreen";
    public static final String STR_DISSCUSIONSCREEN = "DisscusionScreen";
    public static final String STR_FORGOTPASSWORDSCREEN = "ForgotPasswordScreen";
    public static final String STR_IMPROVEOURAPPSCREEN = "ImproveOurAppScreen";
    public static final String STR_MAKEPAYMENTSCREEN = "MakePaymentScreen";
    public static final String STR_MORESCREEN = "MoreScreen";
    public static final String STR_MYDEALSSCREEN = "MyDealsScreen";
    public static final String STR_MYGXSCREEN = "MyGXScreen";
    public static final String STR_MYPROFILEMOREBUTTONSCREEN = "MyProfileMoreButtonScreen";
    public static final String STR_PAYMENTFAILEDSCREEN = "PaymentFailedScreen";
    public static final String STR_PAYMENTSUCESSSCREEN = "PaymentSucessScreen";
    public static final String STR_RECENTLYSEARCHESSCREEN = "RecentlySearchesScreen";
    public static final String STR_REQUESTCHATSCREEN = "RequestChatScreen";
    public static final String STR_REVIEWSHOWSCREEN = "ReviewShowScreen";
    public static final String STR_SCHEDULETRIPSCREEN = "ScheduleTripScreen";
    public static final String STR_SEARCHRESULTSCREEN = "SearchResultScreen";
    public static final String STR_SEARCHSCREEN = "SearchScreen";
    public static final String STR_SETTINGSSCREEN = "SettingsScreen";
    public static final String STR_SIGNUP = "Signup";
    public static final String STR_STATUSLABELS = "StatusLabels";
    public static final String STR_TOPMENU = "TopMenu";
    public static final String STR_TRAVELLERDETAIL = "TravellerDetail";
    public static final String STR_TRAVELLERDETAILSCREEN  = "TravellerDetailScreen";
    public static final String STR_USERDEALDEATILSCREEN = "UserDealDeatilScreen";
    public static final String STR_USERRATINGSCREEN = "UserRatingScreen";
    public static final String STR_VARIFYOTPSCREEN = "VarifyOTPScreen";

    //http://webzzserverurl.com/GiftX/ws.giftx.php?op=List_Alerts&lid=2&user_type=1
    // for the app alert messages text


    static Context mContext;
    SQLiteDatabase db;


    public DatabaseHelper(Context context) {
        //super(context, App.DB_PATH + App.DB_NAME, null, DATABASE_VERSION);

        // set on folder super(context,App.DB_PATH +"/" + App.DB_NAME, null, DATABASE_VERSION);

        super(context, App.DB_NAME, null, DATABASE_VERSION);


        //super(context, DATABASE_NAME, null, 1);
        mContext = context;
    }


    public void open() throws SQLException {
        db = this.getWritableDatabase();
    }

    @Override
    public synchronized void close() {

        if (db != null)
            db.close();
        super.close();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        App.showLog(TAG+"===============onCreate=======Create tables=====DATABSE================");

        db.execSQL("create table TBL_ALERT "
                + "(id integer primary key autoincrement, name text,message text)");

        db.execSQL("create table TBL_LABEL "
                + "(id integer primary key autoincrement, name text, label text,message text)");

        App.showLog(TAG+"...............came in table creation............");


        db.execSQL("CREATE TABLE IF NOT EXISTS Country(id INTEGER,countryname TEXT,countryid TEXT,countrycode TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS State(id INTEGER,statename TEXT,stateid TEXT,countryid TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS City(id INTEGER,cityname TEXT,countryid TEXT,stateid TEXT);");

        App.showLog(TAG+"===========table created================");
    }

    // public void createdatabase() {
    //
    // }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

        db.execSQL("DROP TABLE IF EXISTS TBL_ALERT");
        db.execSQL("DROP TABLE IF EXISTS TBL_LABEL");
        App.showLog(TAG+"==================Upgrade Called===============");

        db.execSQL("DROP TABLE IF EXISTS 'Country'");
        db.execSQL("DROP TABLE IF EXISTS 'State'");
        db.execSQL("DROP TABLE IF EXISTS 'City'");
        System.out.println(TAG+"==============Upgrade Called====================");
        // Create tables again
        onCreate(db);
    }









    public boolean insertAlert(String name, String message) {

        SQLiteDatabase  db = this.getReadableDatabase();
        db = this.getWritableDatabase();
        Cursor c = db.rawQuery(
                "select DISTINCT name from TBL_ALERT where name = '" + name
                        + "'", null);
        if (c.getCount() > 0) {
            db.execSQL("delete from " + DatabaseHelper.TABLE_ALERT);
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", name);
            contentValues.put("message", message);

            db.insert(DatabaseHelper.TABLE_ALERT, null, contentValues);
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", name);
            contentValues.put("message", message);

            db.insert(DatabaseHelper.TABLE_ALERT, null, contentValues);

            //	Log.e("data", "inserted successfully");
        }

        return true;
    }


    public boolean insertLabels(String name, String label,String message) {

        SQLiteDatabase db = this.getReadableDatabase();
        db = this.getWritableDatabase();
        Cursor c = db.rawQuery(
                "select  label from "+ DatabaseHelper.TABLE_LABEL+" where label = '" + label
                        + "'", null);

        if (c.getCount() > 0) {

            db.execSQL("delete from " + DatabaseHelper.TABLE_LABEL);


            ContentValues contentValues = new ContentValues();
            contentValues.put("name", name);
            contentValues.put("label", label);
            contentValues.put("message", message);

            db.insert(DatabaseHelper.TABLE_LABEL, null, contentValues);

        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", name);
            contentValues.put("label", label);
            contentValues.put("message", message);

            db.insert(DatabaseHelper.TABLE_LABEL, null, contentValues);

            //Log.e("data", "inserted successfully");
        }
        return true;
    }




    public boolean insertLabelsAll(String name, String label,String message) {
        SQLiteDatabase db = this.getReadableDatabase();
        db = this.getWritableDatabase();


        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("label", label);
        contentValues.put("message", message);

        db.insert(DatabaseHelper.TABLE_LABEL, null, contentValues);

        return true;
    }



    public boolean insertAlertAll(String name, String message) {
        SQLiteDatabase db = this.getReadableDatabase();
        db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("message", message);

        db.insert(DatabaseHelper.TABLE_ALERT, null, contentValues);

        return true;
    }

    public Cursor getAlert() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select message from " + DatabaseHelper.TABLE_ALERT, null);

        //Log.d("data", "fetched successfully ");
        return res;
    }



    public Cursor getLabels(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select message from " + DatabaseHelper.TABLE_LABEL + " where name='"+name+"'", null);

        return res;
    }





    public Cursor getAllLabels() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select message from " + DatabaseHelper.TABLE_LABEL, null);

        return res;
    }

    public Cursor deleteTable(String name) {
        /*try {

        } catch (Exception e) {
            e.printStackTrace();
        }*/
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("DELETE FROM " + name, null);



        //Log.d("data", "deleted successfully ");
        return res;
    }


    public boolean deleteTableData(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ name);
        return true;
    }






    public void DropTable(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS '" + name + "'");
        db.close();
    }


    /*public void InsertCountry(CountryModel country) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("countryid", country.ctrid);
            values.put("countrycode", country.ctrcode);
            values.put("countryname", country.ctrname);
            db.insert(TABLE_COUNTRY, null, values);
            db.close(); // Closing database connection
        } catch (Exception e) {

        }
    }

    public void InsertState(StateModel country) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("stateid", country.sid);
            values.put("countryid", country.ctrid);
            values.put("statename", country.sname);
            db.insert(TABLE_STATE, null, values);
            db.close(); // Closing database connection
        } catch (Exception e) {

        }
    }

    public void InsertCity(CityModel country) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("stateid", country.sid);
            values.put("countryid", country.ctyid);
            values.put("cityname", country.ctyname);
            db.insert(TABLE_CITY, null, values);
            db.close(); // Closing database connection
        } catch (Exception e) {

        }
    }*/


    public void DeleteTableData(String strTableName) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(strTableName, null, null);
            System.out.println("=====Deleted Table data is===" + strTableName);
            db.close();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }


    /*public ArrayList<CountryModel> getAllCountry() {
        ArrayList<CountryModel> data = new ArrayList<CountryModel>();
        try {
            String name, id, code;

            String selectQuery = "SELECT  * FROM " + TABLE_COUNTRY + " DESC";
            SQLiteDatabase dbs = this.getReadableDatabase();
            Cursor cursor = null;

            cursor = dbs.rawQuery(selectQuery, null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.moveToFirst()) {
                    do {

                        CountryModel countryModel = new CountryModel();

                        name = cursor.getString(cursor
                                .getColumnIndex("countryname"));
                        id = cursor.getString(cursor
                                .getColumnIndex("countryid"));
                        code = cursor.getString(cursor
                                .getColumnIndex("countrycode"));


                        try {
                            countryModel.ctrname = name;
                            countryModel.ctrcode = code;
                            countryModel.ctrid = id;

                            data.add(countryModel);
                            // /data.put(name + "+" + room_full_name, image);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
                dbs.close(); // Closing database connection

            }
        } catch (Exception e) {

        }
        return data;

    }

    public ArrayList<StateModel> getAllState(String userstate) {
*//*
         values.put("stateid", country.sid);
            values.put("countryid", country.ctrid);
            values.put("statename", country.sname);*//*

        ArrayList<StateModel> data = new ArrayList<StateModel>();
        try {
            String stateid, countryid, statename;

            String selectQuery = "SELECT  * FROM " + TABLE_STATE + " WHERE countryid=" + userstate;
            SQLiteDatabase dbs = this.getReadableDatabase();
            Cursor cursor = null;

            cursor = dbs.rawQuery(selectQuery, null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.moveToFirst()) {
                    do {

                        StateModel countryModel = new StateModel();

                        stateid = cursor.getString(cursor
                                .getColumnIndex("stateid"));
                        countryid = cursor.getString(cursor
                                .getColumnIndex("countryid"));
                        statename = cursor.getString(cursor
                                .getColumnIndex("statename"));


                        try {
                            countryModel.ctrid = countryid;
                            countryModel.sid = stateid;
                            countryModel.sname = statename;

                            data.add(countryModel);
                            // /data.put(name + "+" + room_full_name, image);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
                dbs.close(); // Closing database connection

            }
        } catch (Exception e) {

        }
        return data;

    }

    public ArrayList<CityModel> getAllCity(String userstate) {
*//*
        values.put("stateid", country.sid);
            values.put("countryid", country.ctyid);
            values.put("cityname", country.ctyname);*//*

        ArrayList<CityModel> data = new ArrayList<CityModel>();
        try {
            String stateid, countryid, cityname;

            String selectQuery = "SELECT  * FROM " + TABLE_CITY + " WHERE stateid="+userstate;
            SQLiteDatabase dbs = this.getReadableDatabase();
            Cursor cursor = null;

            cursor = dbs.rawQuery(selectQuery, null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.moveToFirst()) {
                    do {

                        CityModel countryModel = new CityModel();

                        stateid = cursor.getString(cursor
                                .getColumnIndex("stateid"));
                        countryid = cursor.getString(cursor
                                .getColumnIndex("countryid"));
                        cityname = cursor.getString(cursor
                                .getColumnIndex("cityname"));


                        try {
                            countryModel.sid = stateid;
                            countryModel.ctyid = countryid;
                            countryModel.ctyname = cityname;

                            data.add(countryModel);
                            // /data.put(name + "+" + room_full_name, image);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
                dbs.close(); // Closing database connection

            }
        } catch (Exception e) {

        }
        return data;

    }*/


}
