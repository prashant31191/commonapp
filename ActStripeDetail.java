package com.appname.tradie;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.api.response.CommonResponse;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.appname.App;
import com.appname.R;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.utils.AppFlags;
import com.utils.FourDigitCardFormatWatcher;
import com.utils.PreferencesKeys;
import com.utils.TwoDigitCardFormatWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;


public class ActPayAsYouGo extends TradieBaseActivity {

    private static String TAG = "==ActPayAsYouGo==";

    @BindView(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;

    @BindView(R.id.rlMenuSub)
    RelativeLayout rlMenuSub;

    @BindView(R.id.rlMenu4Sub)
    RelativeLayout rlMenu4Sub;

    @BindView(R.id.rlAutoRenew)
    RelativeLayout rlAutoRenew;


    @BindView(R.id.tvTitleSub)
    TextView tvTitleSub;


    @BindView(R.id.tvTag1)
    TextView tvTag1;

    @BindView(R.id.tvTag2)
    TextView tvTag2;

    @BindView(R.id.tvTag3)
    TextView tvTag3;

    @BindView(R.id.tvTag4)
    TextView tvTag4;

    @BindView(R.id.tvTag5)
    TextView tvTag5;

    @BindView(R.id.tvTag6)
    TextView tvTag6;

    @BindView(R.id.ivTick)
    ImageView ivTick;

    @BindView(R.id.llBottom)
    LinearLayout llBottom;


    @BindView(R.id.etCardNo)
    MaterialEditText etCardNo;

    @BindView(R.id.etMMYY)
    MaterialEditText etMMYY;

    @BindView(R.id.etCVV)
    MaterialEditText etCVV;


    @BindView(R.id.fabNext)
    FloatingActionButton fabNext;


    String strFrom = "";
    String strTitle = "Pay as You Go";

    // for the stripe payment generate token for the plan
    Stripe stripePayment = null;

    String strCardNumber = "";
    String strPlanId = "";
    String strCardExpDate = "";
    String strCardCVV = "";
    int intExpMonth = 0;
    int intExpYear = 0;

    Call callApiMethod;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            App.showLog(TAG);
            ViewGroup.inflate(this, R.layout.act_t_pay_as_you_go, llContainerSub);
            ButterKnife.bind(this);

            getIntentData();
            initialization();
            setClickEvents();
            setListData();

            App.getInstance().trackScreenView(getString(R.string.scrn_ActPayAsYouGo));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getIntentData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
           /* if (bundle.getString(AppFlags.tagTitle) != null && bundle.getString(AppFlags.tagTitle).length() > 0) {
                strTitle = bundle.getString(AppFlags.tagTitle);
            }*/
            if (bundle.getString(AppFlags.tagFrom) != null && bundle.getString(AppFlags.tagFrom).length() > 0) {
                strFrom = bundle.getString(AppFlags.tagFrom);
            }
        }

    }

    private void initialization() {
        try {

            Animation slide_up;// = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_up);
            //  llLogin.startAnimation(slide_up);

            slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_zoomin);
            fabNext.startAnimation(slide_up);

            rlBaseMainHeader.setVisibility(View.GONE);
            setEnableDrawer(false);


            tvTitleSub.setText(strTitle);
            tvTag5.setText(App.sharePrefrences.getStringPref(PreferencesKeys.strCurrencySymbol) + " " + App.sharePrefrences.getStringPref(PreferencesKeys.STR_USER_data11_SINGLE_LEAD_PRICE));


            etCVV.setTypeface(App.getFont_Regular());
            etMMYY.setTypeface(App.getFont_Regular());
            etCardNo.setTypeface(App.getFont_Regular());

            etCVV.setAccentTypeface(App.getFont_Regular());
            etMMYY.setAccentTypeface(App.getFont_Regular());
            etCardNo.setAccentTypeface(App.getFont_Regular());

            tvTitleSub.setTypeface(App.getFont_Regular());
            tvTag1.setTypeface(App.getFont_Regular());
            tvTag2.setTypeface(App.getFont_Regular());
            tvTag3.setTypeface(App.getFont_Regular());
            tvTag4.setTypeface(App.getFont_Regular());
            tvTag5.setTypeface(App.getFont_Regular());
            tvTag6.setTypeface(App.getFont_Regular());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void setClickEvents() {
        try {
            // Register Label Click event
            rlMenuSub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

          /*  fabNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent iv = new Intent(ActStripeDetail.this, Actdata11Dashboard.class);
                    iv.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    iv.putExtra(AppFlags.tagFrom, "ActStripeDetail");
                    App.myStartActivity(ActStripeDetail.this, iv);
                }
            });*/

            fabNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    strCardNumber = etCardNo.getText().toString().trim();
                    strCardExpDate = etMMYY.getText().toString().trim();
                    strCardCVV = etCVV.getText().toString().trim();


                    checkValidation(strCardNumber, strCardExpDate, strCardCVV);



                   /* Intent iv = new Intent(ActSubscriptionPacks.this, Actdata11Dashboard.class);
                    iv.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    iv.putExtra(AppFlags.tagFrom, "ActSubscriptionPacks");
                    App.myStartActivity(ActSubscriptionPacks.this, iv);*/
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setListData() {
        try {

            etCardNo.addTextChangedListener(new FourDigitCardFormatWatcher());
            etMMYY.addTextChangedListener(new TwoDigitCardFormatWatcher());

            ivTick.setSelected(true);

            rlAutoRenew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ivTick.performClick();
                }
            });

            ivTick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ivTick.isSelected() == true) {
                        ivTick.setSelected(false);
                    } else {
                        ivTick.setSelected(true);
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void checkValidation(String strCNo, String strCExp, String strCCVV) {
        try {

            if (strCNo != null && strCNo.length() >= 19) {
                if (strCExp != null && strCExp.length() >= 5) {
                    if (strCExp.contains("/") && strCExp.split("/")[0] != null && strCExp.split("/")[1] != null && (Integer.parseInt(strCExp.split("/")[0]) <= 12) && (Integer.parseInt(strCExp.split("/")[0]) >= 1) && (Integer.parseInt(strCExp.split("/")[1]) >= 17)) {

                        intExpMonth = (Integer.parseInt(strCExp.split("/")[0]));
                        intExpYear = (Integer.parseInt(strCExp.split("/")[1]));

                        if (strCCVV != null && strCCVV.length() >= 3) {

                            setStripePaymentData();
                        } else {
                            etCVV.requestFocus();
                            //etCVV.setError("Please enter valid Card CVV");
                            App.showSnackBar(tvTitle, "Please enter valid Card CVV");
                        }
                    } else {
                        etMMYY.requestFocus();
                        //etMMYY.setError("Please enter valid Card Expire Date");
                        App.showSnackBar(tvTitle, "Please check Card Expire Date");
                    }
                } else {
                    etMMYY.requestFocus();
                    //etMMYY.setError("Please enter valid Card Expire Date");
                    App.showSnackBar(tvTitle, "Please enter valid Card Expire Date");
                }


            } else {
                etCardNo.requestFocus();
                //etCardNo.setError("Please enter valid Card Number");
                App.showSnackBar(tvTitle, "Please enter valid Card Number");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setStripePaymentData() {
        try {
            customProgressDialog.show();


            if (stripePayment == null) {
                stripePayment = new Stripe((ActStripeDetail.this));//.setDefaultPublishableKey(App.STR_STRIPE_PUBLISHABLE_KEY));
                stripePayment.setDefaultPublishableKey(App.STR_STRIPE_PUBLISHABLE_KEY);
            }

            stripePayment.createToken(
                    new Card(strCardNumber, intExpMonth, intExpYear, strCardCVV),
                    new TokenCallback() {
                        public void onSuccess(Token token) {
                            // Send token to your own web service

                            //MyServer.chargeToken(token);
                            customProgressDialog.dismiss();

                            //App.showSnackBar(tvTitle, "Token generated Successfully.");

                            asyncSetPlanJob(strPlanId, token.getId());

                            //Toast.makeText(ActSubscriptionPacks.this,token.getId(),Toast.LENGTH_SHORT).show();

                            App.showLog("======token id=====" + token.getId());
                        }

                        public void onError(Exception error) {

                            App.showLog("====onError======stripePayment==createToken======");
                            error.printStackTrace();

                            customProgressDialog.dismiss();

                            Toast.makeText(ActStripeDetail.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * set requested for the job
     */
    public void asyncSetPlanJob(String strPlanId, String strToken) {

        String isAutoRenew = "0";
      /*  if (ivTick.isSelected() == true) {
            isAutoRenew = "1";
        }*/
        strCardNumber = strCardNumber.replaceAll(" ","");
        customProgressDialog.show();

        callApiMethod = App.getRetrofitApiService().setSubscriptionPlan(App.OP_MAKE_PAYMENT,
                App.sharePrefrences.getStringPref(PreferencesKeys.strUserId),
                strCardNumber,
                "" + intExpMonth,
                "" + intExpYear,
                "" + strCardCVV,
                "" + strToken,
                "" + isAutoRenew,
                "1",
                "" + strPlanId,


                App.sharePrefrences.getStringPref(PreferencesKeys.strUserType),
                App.APP_PLATFORM,
                App.APP_MODE,
                App.sharePrefrences.getStringPref(PreferencesKeys.strAccessToken)
        );

        App.showLogApi("OP_MAKE_PAYMENT --" + App.OP_MAKE_PAYMENT
                + "&uid=" + App.sharePrefrences.getStringPref(PreferencesKeys.strUserId)

                + "&card_number="+strCardNumber
                + "&expmonth=" + intExpMonth
                + "&expyear=" + intExpYear
                + "&cvv=" + strCardCVV
                + "&token=" + strToken
                + "&is_auto_renew=" + isAutoRenew
                + "&payasyougo=" + 1
                + "&planid=" + strPlanId

                + "&user_type=" + App.sharePrefrences.getStringPref(PreferencesKeys.strUserType)
                + "&platform=" + App.APP_PLATFORM
                + "&appmode=" + App.APP_MODE
                + "&accesstoken=" + App.sharePrefrences.getStringPref(PreferencesKeys.strAccessToken)
        );

        callApiMethod.enqueue(new retrofit2.Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                try {
                    customProgressDialog.dismiss();

                    CommonResponse model = response.body();

                    if (model == null) {
                        //404 or the response cannot be converted to User.
                        App.showLog("Test---null response--", "==Something wrong=");
                        ResponseBody responseBody = response.errorBody();
                        if (responseBody != null) {
                            try {
                                App.showLog("Test---error-", "" + responseBody.string());
                                App.showSnackBar(tvTitle, AppFlags.NO_DATA_ERROR_MESSAGE);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        //200 sucess
                        App.showLogApiRespose(TAG + App.OP_MAKE_PAYMENT, response);

                        if (model.msg != null && model.msg.length() > 0) {
                            App.showSnackBar(tvTitle, model.msg);
                        }
                        if (model.status != null && model.status.equalsIgnoreCase("1")) {
                            //111 new screen
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent iv = new Intent(ActStripeDetail.this, Actdata11Dashboard.class);
                                    iv.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    iv.putExtra(AppFlags.tagFrom, "ActStripeDetail");
                                    App.myStartActivity(ActStripeDetail.this, iv);
                                }
                            }, 1800);

                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                t.printStackTrace();
                customProgressDialog.dismiss();
                App.showSnackBar(tvTitle, AppFlags.NO_DATA_ERROR_MESSAGE);

            }
        });

    }


    @SuppressLint("NewApi")
    @Override
    public void onBackPressed() {
        try {
            App.showLog(TAG + "=====onBackPressed=====");
            super.onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
