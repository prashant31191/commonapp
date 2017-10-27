package com.commonapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by prashant.patel on 9/20/2017.
 */

public class ActRegisterMain extends BaseActivity {
    @BindView(R.id.tvRegister)
    TextView tvRegister;

    @BindView(R.id.tvLogin)
    TextView tvLogin;

    @BindView(R.id.tvCustomer)
    TextView tvCustomer;

    @BindView(R.id.tvTradie)
    TextView tvTradie;

    @BindView(R.id.llLogin)
    LinearLayout llLogin;


    @BindView(R.id.rlCustomer)
    RelativeLayout rlCustomer;

    @BindView(R.id.rlTradie)
    RelativeLayout rlTradie;



    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            App.showLog(TAG);
            ViewGroup.inflate(this, R.layout.act_register_main, llContainerSub);
            ButterKnife.bind(this);
            initialization();
            setClickEvents();
            // for the full screen view
            App.getInstance().trackScreenView(getString(R.string.scrn_ActRegisterMain));

        } catch (Exception e) {
            // TODO: handle exceptione.
            e.printStackTrace();
        }
    }


    private void initialization() {

        try {
            Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
            llLogin.startAnimation(slide_up);

            rlBaseMainHeader.setVisibility(View.VISIBLE);
            rlBack.setVisibility(View.VISIBLE);
            tvTitle.setText("");

            setEnableDrawer(false);

            tvRegister.setTypeface(App.getFont_Regular());
            tvLogin.setTypeface(App.getFont_Regular());

            tvCustomer.setTypeface(App.getFont_Regular());
            tvTradie.setTypeface(App.getFont_Regular());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setClickEvents() {
        try {
            // Register Label Click event
            rlCustomer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intActSignup = new Intent(ActRegisterMain.this, ActSignup.class);
                    startActivity(intActSignup);
                    overridePendingTransition(0, 0);
                }
            });
            rlTradie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    App.showSnackBar(tvTitle, getString(R.string.strComingSoon));
                }
            });

            rlBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            tvLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}
