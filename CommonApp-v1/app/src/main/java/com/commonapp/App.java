package com.commonapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.utils.AnalyticsTrackers;
import com.utils.SharePrefrences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.fabric.sdk.android.Fabric;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;

public class App extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
     private static final String TWITTER_KEY = "asdasdasd";
    private static final String TWITTER_SECRET = "asdasdasdasdasd";

    private static App mInstance;
    private static final String TAG = App.class.getSimpleName();

    // fullscreen
    public static boolean blnFullscreenActvitity = false;

    public static String DB_NAME = "seeka.db";
   /* public static String DB_PATH = "/data/data/" + "com.commonapp" + "/databases/";*/

    // app folder name
    public static String APP_FOLDERNAME = ".seeka";

    // share pref name
    //public String PREF_NAME = "halacab_driver_app";
    public static String PREF_NAME = "seeka_app";

    // class for the share pref keys and valyes get set
    public static SharePrefrences sharePrefrences;

    // for the app context
    static Context mContext;

    // for the set app fontface or type face
    static Typeface tf_Regular, tf_Bold;


    //Live
    public static String strBaseHostUrl = "http://webserverurl.com/";
    public static String strBaseUploadedPicUrl = "http://webserverurl.com/upload/user/";
    public static String strBaseUploadedPicRouteUrl = "http://webserverurl.com/upload/route/";


    public static String APP_USERTYPE = "0"; // user_type = 0 for customer/traveller & user_type = 1 for driver
    public static String APP_MODE = "2"; // app_mode 1 = Live   2=testing
    public static String APP_PLATFORM = "2"; // platform = 2   passing app platefrom  2 for android


    public static String OP_REGISTER = "Register";
    public static String OP_LOGIN = "Login"; //done
    public static String OP_LOGINSOCIAL = "LoginSocial";
    public static String OP_VIEW_PROFILE = "viewprofile";
    public static String OP_EDITPROFILE = "editprofile";
    public static String OP_NOTI_LIST = "noti_list";
    public static String OP_NOTI_READ = "noti_read";
    public static String OP_NOTI_DELETE = "noti_delete";
    public static String OP_CHANGE_PWD = "change_password";
    public static String OP_FORGOT_PWD = "forgot_password";
    public static String OP_UPDATEDEVICEINFO = "update_info";

    public static String OP_GETCOUNTRY = "GetCountry";
    public static String OP_GETSTATE = "GetState";
    public static String OP_GETCITY = "GetCity";

    public static String OP_LOGOUT = "Logout";


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    // application on create methode for the create and int base values
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        /* Twitter login - Fabric
        */
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        try {
            MultiDex.install(this);
            mContext = getApplicationContext();
            sharePrefrences = new SharePrefrences(App.this);
            getFont_Regular();
            getFont_Bold();
            createAppFolder();


            // for the google anylytics
            AnalyticsTrackers.initialize(this);
            AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);

            /*
            * Crashlytics - Fabric
            * */
            Fabric.with(this, new Crashlytics());
            //startService(new Intent(this, LoginSessionService.class));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    private void createAppFolder() {
        try {
            String sdCardPath = Environment.getExternalStorageDirectory().toString();
            //File file2 = new File(sdCardPath + "/" + App.APP_FOLDERNAME + "");
            File file2 = new File(sdCardPath + "/" + App.APP_FOLDERNAME + "/AppLog2");
            if (!file2.exists()) {
                if (!file2.mkdirs()) {
                    System.out.println("==Create Directory " + App.APP_FOLDERNAME + "====");
                } else {
                    System.out.println("==No--1Create Directory " + App.APP_FOLDERNAME + "====");
                }
            } else {
                System.out.println("== already created---No--2Create Directory " + App.APP_FOLDERNAME + "====");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static Typeface getFont_Regular() {
        tf_Regular = Typeface.createFromAsset(mContext.getAssets(), "fonts/Montserrat-Regular.ttf");
        return tf_Regular;
    }

    public static Typeface getFont_Bold() {
        tf_Bold = Typeface.createFromAsset(mContext.getAssets(), "fonts/Montserrat-Bold.ttf");
        return tf_Bold;
    }


    public static String setLabelText(String newString, String defaultString) {
        if (newString != null) {
            return newString;
        } else {
            showLog("==setLabelText====LABEL===null===newString====set default text==");
            return defaultString;
        }
    }


    public static String setAlertText(String newString, String defaultString) {
        if (newString != null) {
            return newString;
        } else {
            showLog("==setAlertText===null===newString====set default text==");
            return defaultString;
        }
    }


    public static String getddMMMyy(String convert_date_string) {
        String final_date = "";
        String date1 = "";
        if (convert_date_string != null) {

            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM ''yy h:mm a");
                String inputDateStr = convert_date_string;
                Date date = null;
                date = inputFormat.parse(inputDateStr);
                //String outputDateStr = outputFormat.format(date);
                date1 = outputFormat.format(date);
                final_date = date1.toLowerCase();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return final_date;
    }

    public static String getHHmmddMMMyy(String convert_date_string) {
        String final_date = "";
        String date1 = "";
        if (convert_date_string != null) {

            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat outputFormat = new SimpleDateFormat("h:mm a dd-MM-yyyy");
                String inputDateStr = convert_date_string;
                Date date = null;
                date = inputFormat.parse(inputDateStr);
                //String outputDateStr = outputFormat.format(date);
                date1 = outputFormat.format(date);
                final_date = date1.toLowerCase();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return final_date;
    }

    public static String convertSecondToHHMMString(int secondtTime) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(tz);
        String time = df.format(new Date(secondtTime * 1000L));

        return time;
    }


    public static boolean isCheckReachLocation(int rangeMeter, double sDLat, double sDLon, double eDLat, double eDLon) {
        float distanceInMeters = 0;

        Location startLocation = new Location("Start");
        startLocation.setLatitude(sDLat);
        startLocation.setLongitude(sDLon);

        Location targetLocation = new Location("Ending");
        targetLocation.setLatitude(eDLat);
        targetLocation.setLongitude(eDLon);

        distanceInMeters = (targetLocation.distanceTo(startLocation));


        // distance = locationA.distanceTo(locationB);   // in meters
        //  distance = locationA.distanceTo(locationB)/1000;   // in km

        String strCalMeters = String.format("%.02f", distanceInMeters);

        App.showLog("===checkReachLocation====strCalMeters====meters====" + strCalMeters);

        if (distanceInMeters > rangeMeter) {
            return false;
        } else {
            App.showLog("====-----REACHED----=====checkReachLocation====strCalMeters====meters====" + strCalMeters);
            return true;
        }
    }


    static String strPrvCalKM = "";

    public static String getDistanceInKM(double sDLat, double sDLon, double eDLat, double eDLon) {
        float distanceInMeters = 0;

        Location startLocation = new Location("Start");
        startLocation.setLatitude(sDLat);
        startLocation.setLongitude(sDLon);

        Location targetLocation = new Location("Ending");
        targetLocation.setLatitude(eDLat);
        targetLocation.setLongitude(eDLon);

        distanceInMeters = (targetLocation.distanceTo(startLocation) / 1000);
        /*
            distanceInMeters = (targetLocation.distanceTo(startLocation));
            distanceInMeters = (distanceInMeters / 1000);
        */

        // distance = locationA.distanceTo(locationB);   // in meters
        //  distance = locationA.distanceTo(locationB)/1000;   // in km

        String strCalKM = String.format("%.02f", distanceInMeters);

        if (strPrvCalKM.equalsIgnoreCase(strCalKM)) {

        } else {
            App.showLog("======KM====" + strCalKM);
            strPrvCalKM = strCalKM;
            setJsonArrayRoute(10, eDLat, eDLon);
        }
        return strCalKM;
    }

    public static JSONArray jsonArrayRoute = new JSONArray();

    public static void setJsonArrayRoute(float meters, double eDLat, double eDLon) {
        try {
            //if (meters % 10 == 0) // 10 meter insert data
            {
                JSONObject jsonObjectRoute = new JSONObject();
                jsonObjectRoute.put("latlon", "" + eDLat + "," + eDLon);
                jsonArrayRoute.put(jsonObjectRoute);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String getDistanceInKMtoMeter(float KMS) {
        float distanceInMeters = KMS;


        distanceInMeters = ((distanceInMeters) * 1000);


        // distance = locationA.distanceTo(locationB);   // in meters
        //  distance = locationA.distanceTo(locationB)/1000;   // in km

        String strCalKMtoMETER = String.format("%.02f", distanceInMeters);

        App.showLog("======METER====" + strCalKMtoMETER);

        return strCalKMtoMETER;
    }


    public static String convertMeterToKMString(float meters) {
        float distanceInMeters = 0;

        distanceInMeters = meters / 1000;
        String strCalKM = "0";
        strCalKM = String.format("%.02f", distanceInMeters);

        App.showLog("======KM====" + strCalKM);

        return strCalKM;
    }


    public static String getDistanceInMeter(double sDLat, double sDLon, double eDLat, double eDLon) {
        float distanceInMeters = 0;

        Location startLocation = new Location("Start");
        startLocation.setLatitude(sDLat);
        startLocation.setLongitude(sDLon);

        Location targetLocation = new Location("Ending");
        targetLocation.setLatitude(eDLat);
        targetLocation.setLongitude(eDLon);

        distanceInMeters = (targetLocation.distanceTo(startLocation));


        // distance = locationA.distanceTo(locationB);   // in meters
        //  distance = locationA.distanceTo(locationB)/1000;   // in km

        String strCalMeter = String.format("%.02f", distanceInMeters);

        App.showLog("======METER====" + strCalMeter);

        return strCalMeter;
    }


    public static String getCurrentDateTime() {
        String current_date = "";
        Calendar c = Calendar.getInstance();
        //System.out.println("Current time => " + c.getTime());

        SimpleDateFormat postFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        current_date = postFormater.format(c.getTime());

        return current_date;
    }


    public static void showToastLong(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }


    public static void showToastShort(Context context, String strMessage) {
        Toast.makeText(context, strMessage, Toast.LENGTH_SHORT).show();
    }


    public static void showSnackBar(View view, String strMessage) {
        Snackbar snackbar = Snackbar.make(view, strMessage, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.BLACK);
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }


    public static void showSnackBarLong(View view, String strMessage) {
        Snackbar.make(view, strMessage, Snackbar.LENGTH_LONG).show();
    }


    public static void showLog(String strMessage) {
        Log.v("==App==", "--strMessage--" + strMessage);
    }


    public static void showLogApi(String strMessage) {
        //Log.v("==App==", "--strMessage--" + strMessage);
        System.out.println("--API-MESSAGE--" + strMessage);

        //  appendLogApi("c_api", strMessage);
    }

    public static void showLogApi(String strOP, String strMessage) {
        //Log.v("==App==", "--strMessage--" + strMessage);
        System.out.println("--API-strOP--" + strOP);
        System.out.println("--API-MESSAGE--" + strMessage);

        // appendLogApi(strOP + "_c_api", strMessage);
    }

    public static void showLogApiRespose(String op, Response response) {
        //Log.w("=op==>" + op, "response==>");
        String strResponse = new Gson().toJson(response.body());
        Log.i("=op==>" + op, "response==>" + strResponse);
        // appendLogApi(op + "_r_api", strResponse);
    }


    public static void showLogResponce(String strTag, String strResponse) {
        Log.i("==App==strTag==" + strTag, "--strResponse--" + strResponse);
        //appendLogApi(strTag + "_r_api", strResponse);
    }

    public static void appendLogApi(String fileName, String text) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyMM_dd_HHmmss");
            String currentDateandTime = sdf.format(new Date());

            String sdCardPath = sdCardPath = Environment.getExternalStorageDirectory().toString();

            File logFile = new File(sdCardPath, "/" + App.APP_FOLDERNAME + "/AppLog2/" + fileName + "_" + currentDateandTime + "_lg.txt");
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (Exception e) {

            Log.e(TAG, e.getMessage(), e);
        }
    }


    public static void showLog(String strTag, String strMessage) {
        Log.v("==App==strTag==" + strTag, "--strMessage--" + strMessage);
    }


    public static void setTaskBarColored(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        }
    }


    public static boolean isInternetAvail(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }


    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    public static String getOnlyDigits(String s) {
        Pattern pattern = Pattern.compile("[^0-9]");
        Matcher matcher = pattern.matcher(s);
        String number = matcher.replaceAll("");
        return number;
    }


    public static String getOnlyStrings(String s) {
        Pattern pattern = Pattern.compile("[^a-z A-Z]");
        Matcher matcher = pattern.matcher(s);
        String number = matcher.replaceAll("");
        return number;
    }


    public static String getOnlyAlfaNumeric(String s) {
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
        Matcher matcher = pattern.matcher(s);
        String number = matcher.replaceAll(" ");
        return number;
    }


    public void hideKeyBoard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public static void hideSoftKeyboardMy(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }


    @SuppressLint("NewApi")
    public static void myStartActivity(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    public static void myFinishActivityRefresh(Activity activity, Intent intent) {
        activity.finish();
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    public static void myFinishActivity(Activity activity) {
        activity.finish();
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    public static void GenerateKeyHash() {
        try {
            PackageInfo info = mContext.getPackageManager().getPackageInfo(mContext.getApplicationContext().getPackageName(),
                    PackageManager.GET_SIGNATURES); //GypUQe9I2FJr2sVzdm1ExpuWc4U= android pc -2 key
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                App.showLog("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void myFinishStartActivity(Activity activity) {
        activity.finish();
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }


    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public static String convertTo24Hour(String Time) {
        DateFormat f1 = new SimpleDateFormat("hh:mm a"); //11:00 pm
        Date d = null;
        try {
            d = f1.parse(Time);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        DateFormat f2 = new SimpleDateFormat("HH:mm:ss");
        String x = f2.format(d); // "23:00"

        return x;
    }


    public static String convertTo12Hour(String Time) {
        DateFormat f1 = new SimpleDateFormat("HH:mm:ss"); // "23:00:00"
        Date d = null;
        try {
            d = f1.parse(Time);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        DateFormat f2 = new SimpleDateFormat("hh:mm a");
        String x = f2.format(d); //11:00 pm

        return x;
    }


    public static void expand(final View v) {
        //v.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT
        v.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        //? WindowManager.LayoutParams.WRAP_CONTENT //WRAP_CONTENT
                        ? WindowManager.LayoutParams.MATCH_PARENT //WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }


    public static void expandWRAP_CONTENT(final View v) {
        //v.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT); //WRAP_CONTENT
        v.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        //? WindowManager.LayoutParams.WRAP_CONTENT //WRAP_CONTENT
                        ? WindowManager.LayoutParams.MATCH_PARENT //WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }


    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }


    public static RequestBody createPartFromString(String value) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), value);
    }

    private static RequestBody createPartFromFile(File file) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), file);
    }


    public static void saveBitmapSdcard(String filename, Bitmap bitmap) {
        FileOutputStream out = null;
        try {

            String directoryPath = Environment.getExternalStorageDirectory() + File.separator + App.APP_FOLDERNAME;

            File appDir = new File(directoryPath);

            if (!appDir.exists() && !appDir.isDirectory()) {
                // create empty directory
                if (appDir.mkdirs()) {
                    Log.d("===CreateDir===", "App dir created");
                } else {
                    Log.d("===CreateDir===", "Unable to create app dir!");
                }
            } else {
                //Log.d("===CreateDir===","App dir already exists");
            }


            out = new FileOutputStream(directoryPath + File.separator + filename);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /*start goole analytycs*/
    public static synchronized App getInstance() {
        return mInstance;
    }

    public synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
    }

    /***
     * Tracking screen view
     *
     * @param screenName screen name to be displayed on GA dashboard
     */
    public void trackScreenView(String screenName) {
        Tracker t = getGoogleAnalyticsTracker();

        // Set screen name.
        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }

    /***
     * Tracking exception
     *
     * @param e exception to be tracked
     */
    public void trackException(Exception e) {
        if (e != null) {
            Tracker t = getGoogleAnalyticsTracker();

            t.send(new HitBuilders.ExceptionBuilder()
                    .setDescription(
                            new StandardExceptionParser(this, null)
                                    .getDescription(Thread.currentThread().getName(), e))
                    .setFatal(false)
                    .build()
            );
        }
    }

    /***
     * Tracking event
     *
     * @param category event category
     * @param action   action of the event
     * @param label    label
     */
    public void trackEvent(String category, String action, String label) {
        Tracker t = getGoogleAnalyticsTracker();

        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }

    /*end goole analytycs*/


}
