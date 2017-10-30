package com.commonapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.api.ApiService;
import com.api.response.RegisterResponse;
import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;
import com.utils.AppFlags;
import com.utils.CustomProgressDialog;
import com.utils.EmailValidator;
import com.utils.PreferencesKeys;

import org.json.JSONObject;

import java.net.URL;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * The type Act login.
 */
public class ActLogin extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {

    String TAG = "==ActLogin==";
    boolean blnRetry = false;

    @BindView(R.id.etEmail)
    MaterialEditText etEmail;

    @BindView(R.id.etPassword)
    MaterialEditText etPassword;

    @BindView(R.id.tvForgot)
    TextView tvForgot;

    @BindView(R.id.tvLogin)
    TextView tvLogin;

    @BindView(R.id.tvRegister)
    TextView tvRegister;

    @BindView(R.id.tvSubmit)
    TextView tvSubmit;

    @BindView(R.id.tvTag1)
    TextView tvTag1;

    @BindView(R.id.tvTag2)
    TextView tvTag2;

    @BindView(R.id.tvFacebook)
    TextView tvFacebook;

    @BindView(R.id.tvTwitter)
    TextView tvTwitter;

    @BindView(R.id.rlFbData)
    RelativeLayout rlFbData;

    @BindView(R.id.fabNext)
    FloatingActionButton fabNext;

    @BindView(R.id.llLogin)
    LinearLayout llLogin;




    TwitterLoginButton twitterLoginButton;
    String strEmail = "", strPassword = "";
    String strFName = "", strLName = "", strFBid = "", strGPid = "", strSocialImageUrl = "", strTwtrid = "";

    Retrofit retrofitApiCall;
    ApiService apiService;
    Call callApiMethod;
    CustomProgressDialog customProgressDialog;

    // for the google and facebook login
    CallbackManager callbackManager;
    AccessToken accessToken;
    GraphRequest request;
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;


    Bitmap photoBitmap;

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            App.showLog(TAG);
            ViewGroup.inflate(this, R.layout.act_login, llContainerSub);
            ButterKnife.bind(this);

            App.GenerateKeyHash();

            Fabric.with(this, new Crashlytics());

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            FacebookSdk.sdkInitialize(getApplicationContext());
            callbackManager = CallbackManager.Factory.create();

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(ActLogin.this, ActLogin.this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

            initialization();
            setClickEvents();
            setApiData();
            // for the full screen view
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


            App.getInstance().trackScreenView(getString(R.string.scrn_ActLogin));

        } catch (Exception e) {
            // TODO: handle exceptione.
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void initialization() {

        try {
            Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_up);
            llLogin.startAnimation(slide_up);

            rlBaseMainHeader.setVisibility(View.GONE);
            setEnableDrawer(false);

            /*etEmail = (MaterialEditText) findViewById(R.id.etEmail);
            etPassword = (MaterialEditText) findViewById(R.id.etPassword);
            tvSubmit = (TextView) findViewById(R.id.tvSubmit);
            tvRegister = (TextView) findViewById(R.id.tvRegister);
            tvForgot = (TextView) findViewById(R.id.tvForgot);

            tvFacebook = (TextView) findViewById(R.id.tvFacebook);
            tvTwitter = (TextView) findViewById(R.id.tvTwitter);*/

            //Initializing twitter login button
            twitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitterLogin);

            /*****************************************************/
            //////////// Set External fonts to labels /////////////
            /*****************************************************/

            tvTitle.setTypeface(App.getFont_Regular());

            etEmail.setTypeface(App.getFont_Regular());
            etPassword.setTypeface(App.getFont_Regular());

            tvForgot.setTypeface(App.getFont_Regular());
            tvLogin.setTypeface(App.getFont_Regular());
            tvTag1.setTypeface(App.getFont_Regular());
            tvTag2.setTypeface(App.getFont_Regular());

            tvSubmit.setTypeface(App.getFont_Regular());
            tvRegister.setTypeface(App.getFont_Regular());
            tvFacebook.setTypeface(App.getFont_Regular());
            tvTwitter.setTypeface(App.getFont_Regular());

            etEmail.setAccentTypeface(App.getFont_Regular());
            etPassword.setAccentTypeface(App.getFont_Regular());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setClickEvents() {
        try {

            etPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        tvSubmit.performClick();
                        return true;
                    }
                    return false;
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
                        App.hideSoftKeyboardMy(ActLogin.this);
                        //app.hideKeyBoard(v);
                    }
                    return true;
                }
            });

            // Forgot Label Click event
            tvForgot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intActForgotPassword = new Intent(ActLogin.this, ActForgotPassword.class);
                    startActivity(intActForgotPassword);
                    overridePendingTransition(0, 0);
                }
            });

            // Register Label Click event
            tvRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intActSignup = new Intent(ActLogin.this, ActRegisterMain.class);
                    startActivity(intActSignup);
                    overridePendingTransition(0, 0);
                }
            });


            // Submit button click listener
            fabNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvSubmit.performClick();
                }
            });
            tvSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    App.hideSoftKeyboardMy(ActLogin.this);

                    strEmail = etEmail.getText().toString();
                    strPassword = etPassword.getText().toString();

                    loginValidator(strEmail, strPassword);
                }
            });

            // FACEBOOK click event
            rlFbData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvFacebook.performClick();
                }
            });
            tvFacebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (App.isInternetAvail(ActLogin.this)) {
                        setFacebookLoginWithPermission();
                    } else {
                        App.showSnackBar(tvTitle, getString(R.string.strNetError));
                    }

                }
            });

            // G PLUS click event
           /* ivGPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (App.isInternetAvail(ActLogin.this)) {
                        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                        startActivityForResult(signInIntent, RC_SIGN_IN);

                    } else {
                        if (size > 0 && size >= 0) {
                            App.showSnackBar(tvTitle, App.setAlertText(App.sharePrefrences.loadArrayValue(PreferencesKeys.arrayAert, 0),"We can't detect an internet connection. please check and try again."));
                        } else {
                            App.showSnackBar(tvTitle, getString(R.string.strNetError));
                        }
                    }
                }
            });
*/
            tvTwitter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    twitterLoginButton.performClick();
                }
            });

            twitterLoginButton.setCallback(new com.twitter.sdk.android.core.Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    getTwiterData(result);
                }

                @Override
                public void failure(TwitterException exception) {
                    App.showLog("TwitterKit", "Login with Twitter failure==" + exception.toString());
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //The login function accepting the result object
    public void getTwiterData(Result<TwitterSession> result) {

        //Creating a twitter session with result's data
        TwitterSession session = result.data;
        //Getting the username from session
        final String username = session.getUserName();

        Twitter.getApiClient(session).getAccountService().verifyCredentials(true, false).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> userResult) {

                App.showLog("======onResponse====");
                //If it succeeds creating a User object from userResult.data
                User user = userResult.body();

                if (user != null) {

                    //Getting the profile image url
                    String profileImage = user.profileImageUrl.replace("_normal", "");

                    App.showLog("=====profileImage======" + profileImage);
                    App.showLog("=====name======" + user.name);
                    App.showLog("=====id======" + user.id);
                    App.showLog("=====email======" + user.email);
                    strSocialImageUrl = profileImage;
                    String tid = Long.toString(user.id);

                    socialLogin("", user.name, "", "", tid);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                App.showLog("=onFailure===t=" + t.toString());
            }
        });
    }

    ////////////// VALIDATION CHECK ON SUBMIT CLICK ///////////////
    public void loginValidator(String email, String password) {
        EmailValidator emailValidator = new EmailValidator();
        boolean valid = emailValidator.validate(email);

        if (email == null || email.length() == 0) {
            App.showSnackBar(tvTitle, "Please enter email address.");
            etEmail.requestFocus();
        } else if (email == null || email.length() == 0 || valid == false) {
            App.showSnackBar(tvTitle, "Please enter a valid email address.");
            etEmail.requestFocus();
        } else if (password == null || password.length() == 0) {
            App.showSnackBar(tvTitle, "Please enter password.");
            etPassword.requestFocus();
        } else if (password == null || password.length() < 6) {
            App.showSnackBar(tvTitle, "Please enter valid password. It should contains minimum 6 characters.");
            etPassword.requestFocus();
        } else {
            if (App.isInternetAvail(ActLogin.this)) { // Check inter net condition
                App.showLog(TAG, "=========SUBMIT Click==========");
                asyncSignin();
            } else {
                App.showSnackBar(tvTitle, getString(R.string.strNetError));

            }
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
            customProgressDialog = new CustomProgressDialog(ActLogin.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Login API
    public void asyncSignin() {

        try {
            customProgressDialog.show();
            callApiMethod = apiService.setLoginUser(
                    App.OP_LOGIN,
                    strEmail,
                    strPassword,
                    App.APP_MODE,
                    App.APP_PLATFORM,
                    App.sharePrefrences.getStringPref(PreferencesKeys.strDeviceId),
                    App.sharePrefrences.getStringPref(PreferencesKeys.strDeviceId));

            App.showLogApi("--OP_LOGIN--" + App.OP_LOGIN
                    + "&email=" + strEmail
                    + "&pwd=" + strPassword
                    + "&app_mode=" + App.APP_MODE
                    + "&platform=" + App.APP_PLATFORM
                    + "&android_token=" + App.sharePrefrences.getStringPref(PreferencesKeys.strDeviceId)
                    + "&device_id=" + App.sharePrefrences.getStringPref(PreferencesKeys.strDeviceId)

            );

            callApiMethod.enqueue(new Callback<RegisterResponse>() {
                @Override
                public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                    customProgressDialog.dismiss();
                    RegisterResponse model = response.body();

                    if (model == null) {

                        App.showLog("Test---null response--", "==Something wrong=");
                        ResponseBody responseBody = response.errorBody();
                        if (responseBody != null) {
                            try {
                                App.showLog("Login---error-", " -/- " + responseBody.string());
                                    App.showSnackBar(tvTitle, getString(R.string.strSomethingWentwrong));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        //200 sucess
                        App.showLogApiRespose(TAG + App.OP_LOGIN, response);

                        if (model != null && model.status != null) {
                            if (model.status.equalsIgnoreCase("1")) {
                                App.showLog(TAG + "======login Main Success======1111111==");
                                // set for the user is login
                                App.sharePrefrences.setPref(PreferencesKeys.strLogin, "1");
                                App.sharePrefrences.setPref(PreferencesKeys.strSocialLogin, "0");

                                if (model.data != null) {

                                    ///// Store password for Login session //////
                                    App.sharePrefrences.setPref(PreferencesKeys.strUserPassword, strPassword);
                                    /////////////////////////////////////////////

                                    if (model.data.uid != null) {
                                        App.sharePrefrences.setPref(PreferencesKeys.strUserId, model.data.uid);
                                    }

                                    String strFullName = "";
                                    if (model.data.fname != null && model.data.fname.length() > 0) {
                                        App.sharePrefrences.setPref(PreferencesKeys.strUserFirstName, model.data.fname);
                                        strFullName = model.data.fname + " ";
                                    }
                                    if (model.data.lname != null && model.data.lname.length() > 0) {
                                        App.sharePrefrences.setPref(PreferencesKeys.strUserLastName, model.data.lname);
                                        strFullName = strFullName + model.data.lname;
                                    }
                                    if (strFullName != null && strFullName.length() > 0) {
                                        App.sharePrefrences.setPref(PreferencesKeys.strUserFullName, strFullName);
                                    }

                                    if (model.data.uimg != null && model.data.uimg.length() > 0) {
                                        App.sharePrefrences.setPref(PreferencesKeys.strUserImage, model.data.uimg);
                                    }

                                    if (model.data.email != null) {
                                        App.sharePrefrences.setPref(PreferencesKeys.strUserEmail, model.data.email);
                                    }


                                    if (model.data.badge != null) {
                                        App.sharePrefrences.setPref(PreferencesKeys.strBadge, model.data.badge);
                                    }


                                    if (model.data.accessToken != null && model.data.accessToken.length() > 0) {
                                        App.showLog(TAG + "====set token=====" + model.data.accessToken);
                                        App.sharePrefrences.setPref(PreferencesKeys.strAccessToken, model.data.accessToken);
                                    }

                                    Intent login = new Intent(ActLogin.this, ActDashboard.class);
                                    login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    App.myStartActivity(ActLogin.this, login);

                                }
                                if (model.msg != null)
                                    App.showLog(TAG + model.msg);

                            } else {
                                App.showLog(TAG + "======login else========");
                                if (model.msg != null) {
                                    App.showLog(TAG + model.msg);
                                    App.showSnackBar(tvTitle, model.msg);
                                }
                            }
                        } else {
                            App.showLog(TAG + "======login else========");
                                App.showSnackBar(tvTitle, getString(R.string.strSomethingWentwrong));
                        }
                    }
                }

                @Override
                public void onFailure(Call<RegisterResponse> call, Throwable t) {
                    t.printStackTrace();
                    customProgressDialog.dismiss();
                    App.showSnackBar(tvTitle, getString(R.string.strSomethingWentwrong));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    ///////////// FACEBOOK LOGIN //////////////
    private void setFacebookLoginWithPermission() {
        try {
            LoginManager.getInstance().logInWithReadPermissions(
                    ActLogin.this,
                    Arrays.asList("public_profile", "user_friends", "email"));

            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {

                        @Override
                        public void onSuccess(LoginResult result) {
                            // TODO Auto-generated method stub
                            accessToken = AccessToken
                                    .getCurrentAccessToken();

                            request = GraphRequest
                                    .newMeRequest(
                                            accessToken,
                                            new GraphRequest.GraphJSONObjectCallback() {
                                                @Override
                                                public void onCompleted(
                                                        JSONObject object,
                                                        GraphResponse response) {

                                                    try {

                                                        App.showLog("Response", object.toString());
                                                        String email = "", fname = "", lname = "", gender = "";
                                                        String fid = object.getString("id").toString();

                                                        if (object.getString("first_name") != null) {
                                                            fname = object.getString("first_name").toString();
                                                        }

                                                        if (object.getString("last_name") != null) {
                                                            lname = object.getString("last_name").toString();

                                                            fname = fname + " " + lname;
                                                        }

                                                        if (object.getString("gender") != null) {
                                                            gender = object.getString("gender").toString();
                                                        }

                                                        /*if (object.has("email") && object.getString("email") != null) {
                                                            email = object.getString("email").toString();
                                                        }*/
                                                        if (object.has("email") && object.getString("email") != null) {
                                                            email = object.getString("email").toString();
                                                        }

                                                        JSONObject picobject = object.getJSONObject("picture");
                                                        JSONObject dataobject = picobject.getJSONObject("data");
                                                        String url = dataobject.getString("url").toString();

                                                        //String picture = object.getString("gender").toString();
                                                        App.showLog("==fb photo url==" + url);
                                                        strSocialImageUrl = url;
                                                        try {
                                                            URL urlFb = new URL("https://graph.facebook.com/" + fid + "/picture?width=300&height=300");
                                                            strSocialImageUrl = urlFb.toString();
                                                            App.showLog("==strSocialImageUrl==" + strSocialImageUrl);
                                                        } catch (Exception e) {
                                                            // TODO Auto-generated catch block
                                                            e.printStackTrace();
                                                        }
                                                        socialLogin(fid, fname, "", email, "");

                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });

                            Bundle parameters = new Bundle();
                            parameters.putString("fields",
                                    "id,name,first_name,last_name,age_range,about,picture,gender,email");
                            request.setParameters(parameters);
                            request.executeAsync();
                        }

                        @Override
                        public void onCancel() {
                            // TODO Auto-generated method stub
                            App.showLog("Cancel", "cancel");
                        }

                        @Override
                        public void onError(FacebookException error) {
                            // TODO Auto-generated method stub
                            App.showLog("error", error.getMessage());

                            if (error instanceof FacebookAuthorizationException) {

                                if (AccessToken.getCurrentAccessToken() != null) {
                                    LoginManager.getInstance().logOut();
                                    if (blnRetry == false) {
                                        blnRetry = true;
                                        tvFacebook.performClick();
                                    }
                                }
                            }

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        //Adding the login result back to the button
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

    }


    private void handleSignInResult(GoogleSignInResult result) {
        App.showLog(TAG, "handleSignInResult:" + result.isSuccess());
        App.showLog(TAG, "result getStatus :" + result.getStatus());

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
//            mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
//            updateUI(true);


            if (acct != null && acct.getPhotoUrl() != null && acct.getPhotoUrl().toString() != null) {
                App.showLog("==gp photo url==" + acct.getPhotoUrl().toString());

                strSocialImageUrl = acct.getPhotoUrl().toString();
            }

            socialLogin("", acct.getDisplayName(), acct.getId(), acct.getEmail(), "");

        } else {
            App.showSnackBar(tvTitle, getString(R.string.strSomethingWentwrong));
        }
    }


    public void socialLogin(final String fid, final String fname, final String gplusid, final String email, String twiter_id) {

        App.showLog("FB socialLogin url", "==fid==" + fid);
        App.showLog("FB socialLogin url", "==fname==" + fname);
        App.showLog("G Plus socialLogin url", "==gplusid==" + gplusid);
        App.showLog("FB socialLogin url", "==email==" + email);
        App.showLog("Twitter socialLogin url", "==email==" + twiter_id);

        strEmail = email;

        //strFName = fname;

        if (fname.trim().contains(" ")) {
            String sArrName[] = null;

            sArrName = fname.split(" ");

            if (sArrName != null && sArrName.length >= 0 && sArrName[0] != null) {
                strFName = sArrName[0];
                strLName = "";
            }
            if (sArrName != null && sArrName.length >= 1 && sArrName[1] != null) {
                strFName = sArrName[0];
                strLName = sArrName[1];
            }

        } else {
            strFName = fname;
            strLName = "";
        }


        /*if (email != null && email.length() > 3) {
            etEmail.setText(email);
        }
        if (fname != null && fname.length() > 3) {
            etFirstName.setText(fname);
        }*/

        strFBid = fid;
        strGPid = gplusid;
        strTwtrid = twiter_id;


        if (App.isInternetAvail(ActLogin.this)) {
            asyncSigninSocial();
        } else {
                App.showSnackBar(tvTitle, getString(R.string.strNetError));
            }
    }


    public void asyncSigninSocial() {
        try {
            customProgressDialog.show();
            callApiMethod = apiService.setCheckSocialUser(App.OP_LOGINSOCIAL,
                    strFBid,
                    strGPid,
                    strTwtrid,
                    strEmail,
                    App.APP_PLATFORM,
                    App.APP_MODE,
                    App.sharePrefrences.getStringPref(PreferencesKeys.strDeviceId),
                    App.sharePrefrences.getStringPref(PreferencesKeys.strDeviceId));

            App.showLogApi("OP_LOGINSOCIAL--" + App.OP_LOGINSOCIAL
                    + "&fbid=" + strFBid
                    + "&gid=" + strGPid
                    + "&tid=" + strTwtrid
                    + "&email=" + strEmail
                    + "&platform=" + App.APP_PLATFORM
                    + "&app_mode=" + App.APP_MODE
                    + "&device_id=" + App.sharePrefrences.getStringPref(PreferencesKeys.strDeviceId)
                    + "&android_token=" + App.sharePrefrences.getStringPref(PreferencesKeys.strDeviceId)
            );

            callApiMethod.enqueue(new Callback<RegisterResponse>() {
                @Override
                public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                    try {
                        customProgressDialog.dismiss();
                        RegisterResponse model = response.body();

                        if (model == null) {
                            //404 or the response cannot be converted to User.
                            App.showLog("Test---null response--", "==Something wrong=");
                            ResponseBody responseBody = response.errorBody();
                            if (responseBody != null) {
                                try {
                                    App.showLog("Test---error-", "" + responseBody.string());
                                        App.showSnackBar(tvTitle, getString(R.string.strSomethingWentwrong));
                                    } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            //200 sucess

                            App.showLogApiRespose(TAG + App.OP_LOGINSOCIAL, response);


                            if (model != null && model.status != null) {
                                if (model.status.equalsIgnoreCase("1")) {
                                    App.showLog(TAG + "======login social Success======1111111==");
                                    // set for the user is login
                                    App.sharePrefrences.setPref(PreferencesKeys.strLogin, "1");
                                    App.sharePrefrences.setPref(PreferencesKeys.strSocialLogin, "1");

                                    if (model.data != null) {

                                        if (model.data.uid != null) {
                                            App.sharePrefrences.setPref(PreferencesKeys.strUserId, model.data.uid);
                                        }

                                        String strFullName = "";
                                        if (model.data.fname != null && model.data.fname.length() > 0) {
                                            App.sharePrefrences.setPref(PreferencesKeys.strUserFirstName, model.data.fname);
                                            strFullName = model.data.fname + " ";
                                        }
                                        if (model.data.lname != null && model.data.lname.length() > 0) {
                                            App.sharePrefrences.setPref(PreferencesKeys.strUserLastName, model.data.lname);
                                            strFullName = strFullName + model.data.lname;
                                        }
                                        if (strFullName != null && strFullName.length() > 0) {
                                            App.sharePrefrences.setPref(PreferencesKeys.strUserFullName, strFullName);
                                        }
                                        if (model.data.uimg != null) {
                                            App.sharePrefrences.setPref(PreferencesKeys.strUserImage, model.data.uimg);
                                        }
                                        if (model.data.city != null) {
                                            App.sharePrefrences.setPref(PreferencesKeys.strUserCity, model.data.city);
                                        }
                                        if (model.data.state != null) {
                                            App.sharePrefrences.setPref(PreferencesKeys.strUserState, model.data.state);
                                        }
                                        if (model.data.country != null) {
                                            App.sharePrefrences.setPref(PreferencesKeys.strUserCountry, model.data.country);
                                        }

                                        if (model.data.email != null) {
                                            App.sharePrefrences.setPref(PreferencesKeys.strUserEmail, model.data.email);
                                        }

                                        if (model.data.badge != null) {
                                            App.sharePrefrences.setPref(PreferencesKeys.strBadge, model.data.badge);
                                        }


                                        if (model.data.accessToken != null && model.data.accessToken.length() > 0) {
                                            App.showLog(TAG + "====set token=====" + model.data.accessToken);
                                            App.sharePrefrences.setPref(PreferencesKeys.strAccessToken, model.data.accessToken);
                                        }

                                        Intent login = new Intent(ActLogin.this, ActDashboard.class);
                                        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        App.myStartActivity(ActLogin.this, login);

                                    }
                                    if (model.msg != null)
                                        App.showLog(TAG + model.msg);

                                } else if (model.status.equalsIgnoreCase("0")) {

                                    Intent iv = new Intent(ActLogin.this, ActSignup.class);
                                    //iv = new Intent(ActSplash.this, SPActMapDemo.class);
                                    iv.putExtra(AppFlags.tagFrom, "ActLogin");
                                    iv.putExtra("email", strEmail);
                                    iv.putExtra("flname", strFName);
                                    iv.putExtra("lname", strLName);
                                    iv.putExtra("image_url", strSocialImageUrl);
                                    iv.putExtra("fbid", strFBid);
                                    iv.putExtra("gpid", strGPid);
                                    iv.putExtra("tid", strTwtrid);

                                    App.myStartActivity(ActLogin.this, iv);
                                } else {
                                    App.showLog(TAG + "======login else========");
                                    if (model.msg != null) {
                                        App.showLog(TAG + model.msg);
                                        App.showSnackBar(tvTitle, model.msg);
                                    }
                                }
                            } else {
                                App.showLog(TAG + "======login social else========");
                                    App.showSnackBar(tvTitle, getString(R.string.strSomethingWentwrong));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        customProgressDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<RegisterResponse> call, Throwable t) {
                    t.printStackTrace();
                    customProgressDialog.dismiss();
                        App.showSnackBar(tvTitle, getString(R.string.strSomethingWentwrong));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class DownloadImage extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            try {
                URL url = new URL(params[0]);
                photoBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            if (photoBitmap != null) {
                App.showLog("===getting image sucessfully===");
            }
            super.onPostExecute(result);
        }

    }


    boolean doubleBackToExitPressedOnce = false;


    @SuppressLint("NewApi")
    @Override
    public void onBackPressed() {

        try {

            final Dialog dialog = new Dialog(ActLogin.this);
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
                    App.myFinishActivity(ActLogin.this);
                    finishAffinity();
                    onBackPressed();

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
