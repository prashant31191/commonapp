package com.commonapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.api.model.AddressListModel;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.utils.AppFlags;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by prashant.patel on 9/20/2017.
 */

public class ActSelectAddress extends BaseActivity {
    @BindView(R.id.llBottom)
    LinearLayout llBottom;

    @BindView(R.id.etAddrTitle)
    MaterialEditText etAddrTitle;

    @BindView(R.id.etAddrDetail)
    MaterialEditText etAddrDetail;



    @BindView(R.id.fabNext)
    FloatingActionButton fabNext;

    @BindView(R.id.rlSelAdd_Back)
    RelativeLayout rlSelAdd_Back;

    AddressListModel addressListModel = null;

    String strTitle="",strDetail="",strLat="",strLng="";

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            App.showLog(TAG);
            ViewGroup.inflate(this, R.layout.act_select_address, llContainerSub);
            ButterKnife.bind(this);
            initialization();
            setClickEvents();

            // for the full screen view
            App.getInstance().trackScreenView(getString(R.string.scrn_ActSelectAddress));

          //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        } catch (Exception e) {
            // TODO: handle exceptione.
            e.printStackTrace();
        }
    }

    private void initialization() {

        try {
            Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
            llBottom.startAnimation(slide_up);

            rlBaseMainHeader.setVisibility(View.GONE);
            setEnableDrawer(false);

            etAddrTitle.setTypeface(App.getFont_Regular());
            etAddrDetail.setTypeface(App.getFont_Regular());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setClickEvents() {
        try {
            // Register Label Click event
            rlSelAdd_Back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            fabNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    strTitle = etAddrTitle.getText().toString().trim();
                    strDetail = etAddrDetail.getText().toString().trim();

                    addressListModel = new AddressListModel();
                    addressListModel.addr_title = strTitle;
                    addressListModel.addr_detail = strDetail;


                    if(strTitle == null || strTitle.length() == 0)
                    {
                        App.showSnackBar(tvTitle,"Please enter location name");
                        etAddrTitle.requestFocus();
                    }
                    else if(strDetail == null || strDetail.length() == 0)
                    {
                        App.showSnackBar(tvTitle,"Please enter address");
                        etAddrDetail.requestFocus();
                    }
                    else
                    {
                        Intent intent = new Intent();
                        intent.putExtra(AppFlags.tagFrom,"ActSelectAddress");
                        intent.putExtra(AppFlags.model_AddressListModel,addressListModel);
                        setResult(Activity.RESULT_OK,intent);

                        overridePendingTransition(0,0);
                        finish();
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onBackPressed() {
       /*
        overridePendingTransition(0, 0);*/
        super.onBackPressed();

       Intent intent = new Intent();
        intent.putExtra(AppFlags.tagFrom,"ActSelectAddress");
        setResult(Activity.RESULT_CANCELED,intent);
        overridePendingTransition(0,0);
        finish();

    }
}
