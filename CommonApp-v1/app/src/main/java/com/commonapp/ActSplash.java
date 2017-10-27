package com.commonapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.api.ApiService;
import com.api.response.CommonResponse;
import com.api.response.DeviceInfoResponse;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.iid.FirebaseInstanceId;
import com.utils.AppFlags;
import com.utils.CustomProgressDialog;
import com.utils.PreferencesKeys;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ActSplash extends Activity

{


    String TAG = "==ActSplacescreen==";
    private String mapResourcesDirPath;

    private static int TIME = 5000;

    Retrofit retrofitApiCall;
    ApiService apiService;
    Call callApiMethod;
    CustomProgressDialog customProgressDialog;
    DeviceInfoResponse model;

    App app;


    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mFirebaseAuth;
    String strAccessToken = "";

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        try {
            app = (App) getApplicationContext();
            App.showLog(TAG);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.act_splash);

            strAccessToken = App.sharePrefrences.getStringPref(PreferencesKeys.strAccessToken);

            // Initialize FirebaseAuth
            mFirebaseAuth = FirebaseAuth.getInstance();
            // Obtain the FirebaseAnalytics instance.
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

            initialization();

            String refreshedToken = App.sharePrefrences.getStringPref(PreferencesKeys.strDeviceId);
            App.sharePrefrences.setPref(PreferencesKeys.strMenuSelectedId, "0");
            App.showLog("====refreshedToken===device token===" + refreshedToken);
            if (refreshedToken != null && refreshedToken.length() > 5) {
                App.sharePrefrences.setPref(PreferencesKeys.strDeviceId, refreshedToken);
                TIME = 2000;
            } else {
                getDeviceToken();
            }

            if (strAccessToken != null && strAccessToken.length() > 0) {
            } else {
                App.showLog("==strAccessToken not found==");
                App.sharePrefrences.setPref(PreferencesKeys.strAccessToken, "123456789");
            }

            String strCurrencySymbol = App.sharePrefrences.getStringPref(PreferencesKeys.strCurrencySymbol);

            if (strCurrencySymbol != null && strCurrencySymbol.length() > 0) {
            } else {
                App.sharePrefrences.setPref(PreferencesKeys.strCurrencySymbol, "fils");
            }


            String strUserImageurl = App.sharePrefrences.getStringPref(PreferencesKeys.strUserImage);
            if (strUserImageurl != null && strUserImageurl.length() > 5) {

            } else {

                strUserImageurl = "https://lh3.googleusercontent.com/-a6GhihAwokA/V31Ke1CptjI/AAAAAAAABvo/8pL8UhP6Q_0/Grey-Damon-dp-profile-pics-Facebook-whatsapp-300.jpg";
                App.sharePrefrences.setPref(PreferencesKeys.strUserImage, strUserImageurl);
            }

            setApiData();
            setSendDataAnalytics();
            setSendCrashData();
            displaySplash();

            if (App.sharePrefrences.getStringPref(PreferencesKeys.strLid) != null && App.sharePrefrences.getStringPref(PreferencesKeys.strLid).length() > 0) {

            } else {
                App.sharePrefrences.setPref(PreferencesKeys.strLid, "1");
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    // Initialize local variables
    private void initialization() {
        try {
            TextView tvTag = (TextView) findViewById(R.id.tvTag);
            tvTag.setTypeface(App.getFont_Regular());

            if(App.sharePrefrences.getStringPref(PreferencesKeys.strUserId) !=null && App.sharePrefrences.getStringPref(PreferencesKeys.strUserId).length() > 0) {
                asyncUpdateDeviceInfo();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void asyncUpdateDeviceInfo() {

        if (App.isInternetAvail(ActSplash.this)) {

            if( App.sharePrefrences.getStringPref(PreferencesKeys.strUserId) != null &&  App.sharePrefrences.getStringPref(PreferencesKeys.strUserId).length() > 0){
                callApiMethod = apiService.updateDeviceInfo(
                        App.OP_UPDATEDEVICEINFO,
                        App.sharePrefrences.getStringPref(PreferencesKeys.strUserId),
                        App.sharePrefrences.getStringPref(PreferencesKeys.strDeviceId),
                        App.APP_PLATFORM,
                        App.APP_MODE,
                        App.sharePrefrences.getStringPref(PreferencesKeys.strAccessToken)
                );

                App.showLogApi("OP_UPDATEDEVICEINFO--" + App.OP_UPDATEDEVICEINFO
                        + "&uid=" + App.sharePrefrences.getStringPref(PreferencesKeys.strUserId)
                        + "&device_id=" + App.sharePrefrences.getStringPref(PreferencesKeys.strDeviceId)
                        + "&platform=" + App.APP_PLATFORM
                        + "&app_mode=" + App.APP_MODE
                        + "&user_type=" + App.APP_USERTYPE
                        + "&accessToken=" + App.sharePrefrences.getStringPref(PreferencesKeys.strAccessToken)
                );

                callApiMethod.enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                    }

                    @Override
                    public void onFailure(Call<CommonResponse> call, Throwable t) {
                    }
                });
            }
        }
    }

    // Get Device token - Function
    private void getDeviceToken() {
        try {

            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            Log.d(TAG, "Refreshed token: " + refreshedToken);
            // [START subscribe_topics]
            //FirebaseMessaging.getInstance().subscribeToTopic("news");
            Log.d(TAG, "Subscribed to news topic");
            // [END subscribe_topics]
            Log.d(TAG, "InstanceID token: " + FirebaseInstanceId.getInstance().getToken());

            if (refreshedToken != null && refreshedToken.length() > 5) {
                App.sharePrefrences.setPref(PreferencesKeys.strDeviceId, refreshedToken);

                TIME = 2000;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Define retrofit parameters
    private void setApiData() {
        try {
            retrofitApiCall = new Retrofit.Builder()
                    .baseUrl(App.strBaseHostUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofitApiCall.create(ApiService.class);
            customProgressDialog = new CustomProgressDialog(ActSplash.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // splash screen set with timing
    private void displaySplash() {
        // TODO Auto-generated method stub b

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub

                Intent iv;
                if (App.sharePrefrences.getStringPref(PreferencesKeys.strLogin).equalsIgnoreCase("1")) {
                    iv = new Intent(ActSplash.this, ActDashboard.class);
                    //iv = new Intent(ActSplash.this, ActDashboard.class);
                    iv.putExtra(AppFlags.tagFrom, "ActSplash");
                } else {
                    iv = new Intent(ActSplash.this, ActLogin.class);
                    //iv = new Intent(ActSplash.this, ActMapDemo.class);
                    iv.putExtra(AppFlags.tagFrom, "ActSplash");
                }
                startActivity(iv);
                finish();

            }
        }, TIME);
    }

    private void setSendDataAnalytics() {
        try {
            Log.d(TAG, "---FirebaseAnalytics.Event.SELECT_CONTENT------");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "111");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Prince");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            Log.d(TAG, "---FirebaseAnalytics.Event.SHARE------");
            Bundle bundle2 = new Bundle();
            bundle2.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "prince article");
            bundle2.putString(FirebaseAnalytics.Param.ITEM_ID, "p786");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle);
        } catch (Exception e) {
            Log.d(TAG, "---setSendDataAnalytics-Error send analytics--");
            e.printStackTrace();
        }
    }

    private void setSendCrashData() {
        Log.d(TAG, "---setSendCrashData--");
        FirebaseCrash.logcat(Log.ERROR, TAG, "crash caused");
        FirebaseCrash.report(new Exception("My first Android non-fatal error"));
        FirebaseCrash.log("Activity created");

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id_a311");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "name_prince");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }


}
