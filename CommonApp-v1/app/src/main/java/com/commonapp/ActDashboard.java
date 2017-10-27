package com.commonapp;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.api.ApiService;
import com.utils.CustomProgressDialog;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActDashboard extends BaseActivity {

    private static String TAG = "==ActDashboard==";

    TextView mLocationMarkerText;


    ImageView ivMenu4_tmp;
    RelativeLayout rlMenu4_tmp, rlMenu_tmp;





    Retrofit retrofitApiCall;
    ApiService apiService;
    Call callApiMethod;
    CustomProgressDialog customProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.showLog(TAG + "=====onCreate=====");
        //setContentView(R.layout.activity_maps);
        ViewGroup.inflate(this, R.layout.act_dashboard, llContainerSub);

        try {
            setApiData();
            initialization();
            setClickEvents();

            App.getInstance().trackScreenView(getString(R.string.scrn_ActDashboard));
        } catch (Exception e) {e.printStackTrace();}

    }


    private void initialization() {
        try {
            rlBaseMainHeader.setVisibility(View.GONE);
            setEnableDrawer(true);

            mLocationMarkerText = (TextView) findViewById(R.id.locationMarkertext);

            ivMenu4_tmp = (ImageView) findViewById(R.id.ivMenu4_tmp);
            rlMenu4_tmp = (RelativeLayout) findViewById(R.id.rlMenu4_tmp);
            rlMenu_tmp = (RelativeLayout) findViewById(R.id.rlMenu_tmp);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void setClickEvents() {
        try {

            rlMenu_tmp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rlMenu.performClick();
                }
            });


            rlMenu4_tmp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });

        } catch (Exception e) {e.printStackTrace();}
    }


    private void setApiData() {
        try {
            retrofitApiCall = new Retrofit.Builder()
                    .baseUrl(App.strBaseHostUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofitApiCall.create(ApiService.class);
            customProgressDialog = new CustomProgressDialog(ActDashboard.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("NewApi")
    @Override
    public void onBackPressed() {
        App.showLog(TAG + "=====onBackPressed=====");
        try {
            if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawers();
            } else {

                final Dialog dialog = new Dialog(ActDashboard.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                // Include dialog.xml file
                dialog.setContentView(R.layout.popup_exit);

                // set values for custom dialog components - text, image and button
                TextView tvExitMessage = (TextView) dialog.findViewById(R.id.tvMessage);
                TextView tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);
                TextView tvOK = (TextView) dialog.findViewById(R.id.tvOk);

                String strAlertMessageExit = "Are you sure you want to exit ?";

                String strYes = "YES";

                String strNo = "NO";

                tvExitMessage.setText(strAlertMessageExit);
                tvCancel.setText(strNo);
                tvOK.setText(strYes);

                tvExitMessage.setTypeface(App.getFont_Regular());
                tvCancel.setTypeface(App.getFont_Regular());
                tvOK.setTypeface(App.getFont_Regular());

                dialog.show();

                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                tvOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        App.myFinishActivity(ActDashboard.this);
                        finishAffinity();
                        onBackPressed();

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
