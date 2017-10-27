package com.commonapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.api.ApiService;
import com.api.response.CommonResponse;
import com.utils.CustomProgressDialog;
import com.utils.EmailValidator;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ActForgotPassword extends BaseActivity {



    String TAG = "=ActForgotPassword=";
    TextView tvSubmit,
    tvLogin;
    EditText etEmail;
    String strEmail = "";

    // for the api call
    Retrofit retrofitApiCall;
    ApiService apiService;
    Call callApiMethod;
    CustomProgressDialog customProgressDialog;


    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            ViewGroup.inflate(this, R.layout.act_forgot_password, llContainerSub);
            setApiData();
            initialization();
            setClickEvents();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            App.getInstance().trackScreenView(getString(R.string.scrn_ActForgotPassword));
        } catch (Exception e) {
            // TODO: handle exceptione.
            e.printStackTrace();
        }
    }


    private void initialization() {
        try {

            rlBaseMainHeader.setVisibility(View.GONE);
            setEnableDrawer(false);
            tvSubmit = (TextView) findViewById(R.id.tvSubmit);
            tvLogin = (TextView) findViewById(R.id.tvLogin);
            etEmail = (EditText) findViewById(R.id.etEmail);

            etEmail.setTypeface(App.getFont_Regular());
            tvSubmit.setTypeface(App.getFont_Regular());
            tvLogin.setTypeface(App.getFont_Regular());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setClickEvents() {
        try {
            etEmail.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        tvSubmit.performClick();
                        return true;
                    }
                    return false;
                }
            });

            tvLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            tvSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    App.hideSoftKeyboardMy(ActForgotPassword.this);
                    strEmail = etEmail.getText().toString().trim();
                    emailValidator(strEmail);
                }
            });
            llContainerSub.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // TODO Auto-generated method stub
                    System.out.println("===on touch=2=");
                    if (v instanceof EditText) {
                        System.out.println("=touch no hide edittext==2=");
                    } else {
                        System.out.println("===on touch hide=2=");
                        //App.hideKeyBoard(v);
                        App.hideSoftKeyboardMy(ActForgotPassword.this);
                    }
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Email validator.
     *
     * @param email the email
     */
    public void emailValidator(String email) {
        EmailValidator emailValidator = new EmailValidator();
        boolean valid = emailValidator.validate(email);

        if (email == null || email.length() == 0) {
            App.showSnackBar(etEmail, "Please enter your registered email address.");
            etEmail.requestFocus();
        } else if (email == null || email.length() == 0 || valid == false) {
            App.showSnackBar(etEmail, "Please enter a valid email address.");
            etEmail.requestFocus();
        } else {
            if (App.isInternetAvail(ActForgotPassword.this)) {
                asyncForgotPassword();
            } else {
                    App.showSnackBar(tvTitle, getString(R.string.strNetError));
            }
        }
    }


    private void setApiData() {
        try {
            retrofitApiCall = new Retrofit.Builder()
                    .baseUrl(App.strBaseHostUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofitApiCall.create(ApiService.class);
            customProgressDialog = new CustomProgressDialog(ActForgotPassword.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Async forgot password.
     */
    // http://webzzserverurl.com/foldernm/ws.foldernm.php?op=LoginSocial&fbid=123123&device_id=123123&platform=1
    public void asyncForgotPassword() {
        customProgressDialog.show();

        callApiMethod = apiService.getForgotPassword(App.OP_FORGOT_PWD,
                strEmail
        );

        App.showLogApi("OP_FORGOT_PWD--" + App.OP_FORGOT_PWD
                + "&email=" + strEmail
        );

        callApiMethod.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                customProgressDialog.dismiss();
                CommonResponse model = response.body();

                if (model == null) {
                    //404 or the response cannot be converted to User.
                    App.showLog("Test---null response--", "==Something wrong=");
                    ResponseBody responseBody = response.errorBody();
                    if (responseBody != null) {
                        try {
                            App.showLog("Test---error-", "" + responseBody.string());
                                App.showSnackBar(tvTitle, getString(R.string.strSomethingWentwrong));
                            //tv.setText("responseBody = "+responseBody.string());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    //200 sucess
                    App.showLogApiRespose(TAG + App.OP_FORGOT_PWD,response);
                    {
                        if (model.msg != null) {
                            //App.showSnackBar(etEmail, model.msg);
                            App.showLog("====MSG===", "" + model.msg);
                        }
                    }

                    if (model!=null&&model.status !=null && model.status.equalsIgnoreCase("1")) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                App.myFinishActivity(ActForgotPassword.this);
                            }
                        }, 2500);
                    }
                    else {
                        if (model.msg != null) {
                            App.showSnackBar(etEmail, model.msg);
                            App.showLog("====MSG===", "" + model.msg);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                t.printStackTrace();
                customProgressDialog.dismiss();

                App.showSnackBar(tvTitle, getString(R.string.strSomethingWentwrong));
            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
        //overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}