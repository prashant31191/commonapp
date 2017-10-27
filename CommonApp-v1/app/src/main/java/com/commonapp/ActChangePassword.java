package com.commonapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.api.ApiService;
import com.api.response.ChangePasswordResponse;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.utils.CustomProgressDialog;
import com.utils.PreferencesKeys;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActChangePassword extends BaseActivity {

    String TAG = "==ActChangePassword==";


    TextView tvSubmit;
    MaterialEditText etCurrentPassword, etNewPassword, etConfPassword;
    String strCurrentPassword = "", strNewPassword = "", strConfirmPassword = "";

    // for the api call
    Retrofit retrofitApiCall;
    ApiService apiService;
    Call callApiMethod;
    CustomProgressDialog customProgressDialog;


    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup.inflate(this, R.layout.act_change_password, llContainerSub);

        try {
            initialization();

            setClickEvent();
            setApiData();
            setFonts();

            App.getInstance().trackScreenView(getString(R.string.scrn_ActChangePassword));
        } catch (Exception e) {
            // TODO: handle exceptione.
            e.printStackTrace();
        }
    }


    private void initialization() {
        rlBack.setVisibility(View.VISIBLE);
        setEnableDrawer(false);
        tvTitle.setText("Change Password");

        tvSubmit = (TextView) findViewById(R.id.tvSubmit);

        etCurrentPassword = (MaterialEditText) findViewById(R.id.etCurrentPassword);
        etNewPassword = (MaterialEditText) findViewById(R.id.etNewPassword);
        etConfPassword = (MaterialEditText) findViewById(R.id.etConfPassword);

    }



    private void setClickEvent() {
        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        etConfPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    tvSubmit.performClick();
                    return true;
                }
                return false;
            }
        });

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                App.hideSoftKeyboardMy(ActChangePassword.this);

                strCurrentPassword = etCurrentPassword.getText().toString();
                strNewPassword = etNewPassword.getText().toString();
                strConfirmPassword = etConfPassword.getText().toString();

                setValidation(strCurrentPassword, strNewPassword, strConfirmPassword);
            }
        });
    }


    private void setFonts() {
        etCurrentPassword.setTypeface(App.getFont_Regular());
        etNewPassword.setTypeface(App.getFont_Regular());
        etConfPassword.setTypeface(App.getFont_Regular());

        tvSubmit.setTypeface(App.getFont_Regular());
    }


    private void setValidation(String password, String newPassword, String newConfirmpassword) {
        if (password == null || password.length() == 0) {

                App.showSnackBar(etCurrentPassword, "Please enter current password.");


            etCurrentPassword.requestFocus();
        } else if (newPassword == null || newPassword.length() == 0 || newPassword.length() <= 5) {
            if (newPassword.length() == 0) {
                    App.showSnackBar(etCurrentPassword, "Plesae enter new password.");
                etNewPassword.requestFocus();
            } else {
                    App.showSnackBar(etCurrentPassword, "New password must has 6 characters.");
                etNewPassword.requestFocus();
            }
        } else if (newPassword.equals(password)) {
                App.showSnackBar(etCurrentPassword, "New password must be different from current password.");
            etNewPassword.requestFocus();
        } else if (newConfirmpassword == null || newConfirmpassword.length() == 0 || newConfirmpassword.length() <= 5) {
            if (newConfirmpassword.length() == 0) {

                    App.showSnackBar(etCurrentPassword, "Please re-type your new password.");
                etConfPassword.requestFocus();
            } else {
                    App.showSnackBar(etCurrentPassword, "Re-typed password must has 6 characters.");
                etConfPassword.requestFocus();
            }
        } else if (!newPassword.equals(newConfirmpassword)) {
                App.showSnackBar(etCurrentPassword, "Passwords do not match. Please check and try again.");
            etConfPassword.requestFocus();
        } else {
            if (App.isInternetAvail(ActChangePassword.this)) {
                asyncChangePassword();
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
            customProgressDialog = new CustomProgressDialog(ActChangePassword.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // http://webzzserverurl.com/foldernm/ws.foldernm.php?op=LoginSocial&fbid=123123&device_id=123123&platform=1
    public void asyncChangePassword() {
        try {
            customProgressDialog.show();
            callApiMethod = apiService.setChangePassword(App.OP_CHANGE_PWD,
                    App.sharePrefrences.getStringPref(PreferencesKeys.strUserId),
                    strCurrentPassword,
                    strNewPassword,
                    App.APP_PLATFORM,
                    App.APP_MODE,
                    App.sharePrefrences.getStringPref(PreferencesKeys.strAccessToken)
            );

            App.showLogApi("OP_CHANGE_PWD--" + App.OP_CHANGE_PWD
                    + "&uid=" + App.sharePrefrences.getStringPref(PreferencesKeys.strUserId)
                    + "&cpwd=" + strCurrentPassword
                    + "&npwd=" + strNewPassword
                    + "&platform=" + App.APP_PLATFORM
                    + "&app_mode=" + App.APP_MODE
                    + "&accessToken=" + App.sharePrefrences.getStringPref(PreferencesKeys.strAccessToken)
            );

            callApiMethod.enqueue(new Callback<ChangePasswordResponse>() {
                @Override
                public void onResponse(Call<ChangePasswordResponse> call, Response<ChangePasswordResponse> response) {
                    customProgressDialog.dismiss();
                    final ChangePasswordResponse model = response.body();

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
                        App.showLog("===Response==" + response.body().toString());
                        App.showLog("===Success==**==asyncChangePassword==>", new Gson().toJson(response.body()));

                        if(model.msg !=null && model.msg.length() > 0)
                        {
                            App.showSnackBar(tvTitle, model.msg);
                        }

                        if (model.status.equalsIgnoreCase("1")) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    ///// Store password for Login session //////
                                    App.sharePrefrences.setPref(PreferencesKeys.strUserPassword, strNewPassword);
                                    /////////////////////////////////////////////


                                    if (model.data !=null && model.data.accessToken != null && model.data.accessToken.length() > 0) {
                                        App.showLog(TAG+"====set token====="+ model.data.accessToken);
                                        App.sharePrefrences.setPref(PreferencesKeys.strAccessToken, model.data.accessToken);
                                    }


                                    onBackPressed();
                                }
                            }, 2500);
                        }/* else if (model.status.equalsIgnoreCase(App.APP_LOGOUTSTATUST)) {
                            asyncLogout();
                        } */else {

                                App.showSnackBar(tvTitle, getString(R.string.strSomethingWentwrong));

                        }
                    }
                }

                @Override
                public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                    t.printStackTrace();
                    customProgressDialog.dismiss();

                        App.showSnackBar(tvTitle, getString(R.string.strSomethingWentwrong));

                }
            });
        } catch (Exception e) {e.printStackTrace();}
    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        animFinishActivity();
    }

}