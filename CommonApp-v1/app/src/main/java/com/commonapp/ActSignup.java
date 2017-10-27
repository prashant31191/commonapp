package com.commonapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.api.ApiService;
import com.api.response.RegisterResponse;
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
import com.soundcloud.android.crop.Crop;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;
import com.utils.CustomProgressDialog;
import com.utils.EmailValidator;
import com.utils.PreferencesKeys;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActSignup extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {

    String TAG = "==ActSignup==";
    boolean blnRetry = false;

    @BindView(R.id.etFirstName)
    MaterialEditText etFirstName;

    @BindView(R.id.etLastName)
    MaterialEditText etLastName;

    @BindView(R.id.etEmail)
    MaterialEditText etEmail;

    @BindView(R.id.etPassword)
    MaterialEditText etPassword;


    @BindView(R.id.tvSubmit)
    TextView tvSubmit;

    @BindView(R.id.tvRegister)
    TextView tvRegister;

    @BindView(R.id.tvLogin)
    TextView tvLogin;

    @BindView(R.id.tvPromo)
    TextView tvPromo;



    @BindView(R.id.tvTag1)
    TextView tvTag1;

    @BindView(R.id.tvTag2)
    TextView tvTag2;

    @BindView(R.id.fabNext)
    FloatingActionButton fabNext;

    @BindView(R.id.rlFbData)
    RelativeLayout rlFbData;

    @BindView(R.id.ivProfPic)
    ImageView ivProfPic;

    @BindView(R.id.ivProfPicPlusSign)
    ImageView ivProfPicPlusSign;


    @BindView(R.id.ivFacebook)
    ImageView ivFacebook;


    @BindView(R.id.ivGPlus)
    ImageView ivGPlus;


    @BindView(R.id.ivTwitter)
    ImageView ivTwitter;

    @BindView(R.id.llLogin)
    LinearLayout llLogin;



    TwitterLoginButton twitterLoginButton;

    String strFName = "", strLName = "", strEmail = "", strPassword = "", strConfirmPassword = "";
    String strFBid = "", strGPid = "", strTwtrid = "", strSocialImageUrl = "";

    String sdCardPath;
    Uri mImageCaptureUri;
    int CAMERA_REQESTCODE = 104;

    // for the google and facebook login
    CallbackManager callbackManager;
    AccessToken accessToken;
    GraphRequest request;
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;


    // for the api call
    Retrofit retrofitApiCall;
    ApiService apiService;
    Call callApiMethod;

    CustomProgressDialog customProgressDialog;
    Bitmap photoBitmap;

    String strTagChooseImage = "choose image";
    String strTagGallery = "Gallery";
    String strTagCamera = "Camera";


    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            App.showLog(TAG);
            ViewGroup.inflate(this, R.layout.act_signup, llContainerSub);

            ButterKnife.bind(this);

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            FacebookSdk.sdkInitialize(getApplicationContext());
            callbackManager = CallbackManager.Factory.create();

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(ActSignup.this, ActSignup.this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

            getIntentData();
            setApiData();
            initialization();
            setClickEvents();
            // setLabelLanguage();

            sdCardPath = Environment.getExternalStorageDirectory().toString();
            App.getInstance().trackScreenView(getString(R.string.scrn_ActSignup));

        } catch (Exception e) {
            // TODO: handle exceptione.
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        App.showLog(TAG, "====onStop==");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.showLog(TAG, "====onDestroy==");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }


    private void getIntentData() {
        try {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                if (bundle.getString("email") != null && bundle.getString("email").length() > 0) {
                    strEmail = bundle.getString("email");
                }
                if (bundle.getString("flname") != null && bundle.getString("flname").length() > 0) {
                    strFName = bundle.getString("flname");
                }
                if (bundle.getString("lname") != null && bundle.getString("lname").length() > 0) {
                    strLName = bundle.getString("lname");
                }
                if (bundle.getString("fbid") != null && bundle.getString("fbid").length() > 0) {
                    strFBid = bundle.getString("fbid");
                }
                if (bundle.getString("tid") != null && bundle.getString("tid").length() > 0) {
                    strTwtrid = bundle.getString("tid");
                }

                if (bundle.getString("gpid") != null && bundle.getString("gpid").length() > 0) {
                    strGPid = bundle.getString("gpid");
                }

                if (bundle.getString("image_url") != null && bundle.getString("image_url").length() > 0) {
                    strSocialImageUrl = bundle.getString("image_url");
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initialization() {
        try {
            Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_up);
            llLogin.startAnimation(slide_up);


            rlBaseMainHeader.setVisibility(View.VISIBLE);
            rlBack.setVisibility(View.VISIBLE);
            tvTitle.setText("");

            setEnableDrawer(false);

           /* etFirstName = (MaterialEditText) findViewById(R.id.etFirstName);
            etLastName = (MaterialEditText) findViewById(R.id.etLastName);
            etEmail = (MaterialEditText) findViewById(R.id.etEmail);
            etPassword = (MaterialEditText) findViewById(R.id.etPassword);

            tvSubmit = (TextView) findViewById(R.id.tvSubmit);
            tvLogin = (TextView) findViewById(R.id.tvLogin);

            ivProfPic = (ImageView) findViewById(R.id.ivProfPic);
            ivProfPicPlusSign = (ImageView) findViewById(R.id.ivProfPicPlusSign);
            ivFacebook = (ImageView) findViewById(R.id.ivFacebook);
            ivGPlus = (ImageView) findViewById(R.id.ivGPlus);
            ivTwitter = (ImageView) findViewById(R.id.ivTwitter);*/

            //Initializing twitter login button
            twitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitterLogin);

            /*****************************************************/
            //////////// Set External fonts to labels /////////////
            /*****************************************************/
            tvRegister.setTypeface(App.getFont_Regular());
            tvLogin.setTypeface(App.getFont_Regular());

            etFirstName.setTypeface(App.getFont_Regular());
            etEmail.setTypeface(App.getFont_Regular());
            etPassword.setTypeface(App.getFont_Regular());

            tvSubmit.setTypeface(App.getFont_Regular());
            tvPromo.setTypeface(App.getFont_Regular());
            tvTag1.setTypeface(App.getFont_Regular());
            tvTag2.setTypeface(App.getFont_Regular());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setClickEvents() {
        try {

            llContainerSub.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // TODO Auto-generated method stub
                    System.out.println("===on touch=2=");
                    if (v instanceof EditText) {
                        System.out.println("=touch no hide edittext==2=");
                    } else {
                        System.out.println("===on touch hide=2=");
                        //app.hideKeyBoard(v);
                        App.hideSoftKeyboardMy(ActSignup.this);
                    }
                    return true;
                }
            });
            tvLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ActSignup.this, ActLogin.class);
                   // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            });

            rlBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            // SUBMIT click event
            fabNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvSubmit.performClick();
                }
            });
            tvSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(ActSignup.this, ActAddAddress.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);

                    /*
                    App.hideSoftKeyboardMy(ActSignup.this);

                    strFName = etFirstName.getText().toString();
                    strLName = etLastName.getText().toString();
                    strEmail = etEmail.getText().toString();
                    strPassword = etPassword.getText().toString();
                    strConfirmPassword = etPassword.getText().toString();
                    checkSignupValidation(strFName, strLName, strEmail, strPassword, strConfirmPassword);*/

                }
            });
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

            // Profile Pic image click event
            ivProfPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openBottomSheetPhoto();
                }
            });

            // PROFILE pic PLUS click event
            ivProfPicPlusSign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openBottomSheetPhoto();
                }
            });


            // FACEBOOK click event
            rlFbData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivFacebook.performClick();
                }
            });
            ivFacebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (App.isInternetAvail(ActSignup.this)) {
                        strFBid = "";
                        strGPid = "";
                        strTwtrid = "";
                        setFacebookLoginWithPermission();
                    } else {
                        App.showSnackBar(tvTitle, getString(R.string.strNetError));

                    }

                }
            });

            // G PLUS click event
            ivGPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (App.isInternetAvail(ActSignup.this)) {
                        strFBid = "";
                        strGPid = "";
                        strTwtrid = "";
                        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                        startActivityForResult(signInIntent, RC_SIGN_IN);

                    } else {

                        App.showSnackBar(tvTitle, getString(R.string.strNetError));
                    }

                }
            });

            ivTwitter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    strFBid = "";
                    strGPid = "";
                    strTwtrid = "";
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


            try {


                if (strFName != null && strFName.length() > 0) {
                    etFirstName.setText(strFName);
                }
                if (strLName != null && strLName.length() > 0) {
                    etLastName.setText(strLName);
                }
                if (strEmail != null && strEmail.length() > 0) {
                    etEmail.setText(strEmail);
                    etEmail.setEnabled(false);
                }


                if (strSocialImageUrl != null && strSocialImageUrl.length() > 0) {
                    App.showLog("==get social image bitmap===URL=" + strSocialImageUrl);

                    DownloadImage downloadImage = new DownloadImage();
                    downloadImage.execute(strSocialImageUrl);

                    photoBitmap = getBitmapFromURL(strSocialImageUrl);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //The login function accepting the result object
    public void getTwiterData(Result<TwitterSession> result) {
        try {
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

                        String tid = Long.toString(user.id);

                        socialLogin("", user.name, "", "", tid);
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    App.showLog("=onFailure===t=" + t.toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    ////////////// VALIDATION CHECK ON SUBMIT CLICK ///////////////
    private void checkSignupValidation(String fname, String lname, String email, String password, String confirmpassword) {
        EmailValidator emailValidator = new EmailValidator();
        boolean valid = emailValidator.validate(email);

        if (fname == null || fname.length() == 0) {

            App.showSnackBar(tvTitle, "Please enter first name");
            etFirstName.requestFocus();
        } else if (fname == null || fname.length() == 0 || fname.length() <= 2) {
            App.showSnackBar(tvTitle, "Please enter valid first name");
            etFirstName.requestFocus();
        } else if (lname == null || lname.length() == 0) {

            App.showSnackBar(tvTitle, "Please enter last name");
            etLastName.requestFocus();
        } else if (lname == null || lname.length() == 0 || lname.length() <= 2) {

            App.showSnackBar(tvTitle, "Please enter valid last name");
            etLastName.requestFocus();
        } else if (email == null || email.length() == 0) {

            App.showSnackBar(tvTitle, "Please enter email id.");
            etEmail.setEnabled(true);
            etEmail.requestFocus();
        } else if (email == null || email.length() == 0 || valid == false) {

            App.showSnackBar(tvTitle, "Please enter valid email id.");
            etEmail.setEnabled(true);
            etEmail.requestFocus();
        } else if (password == null || password.length() == 0) {


            App.showSnackBar(tvTitle, "Please enter password.");
            etPassword.requestFocus();
        } else if (password == null || password.length() < 6) {

            etPassword.requestFocus();
            App.showSnackBar(tvTitle, "Password must has 6 characters.");
            etPassword.requestFocus();
        } else if (confirmpassword == null || confirmpassword.length() == 0) {


            App.showSnackBar(tvTitle, "Please enter confirm password.");
            etPassword.requestFocus();
        } else if (confirmpassword == null || confirmpassword.length() < 6) {

            App.showSnackBar(tvTitle, "Confirm password must has 6 characters.");

            etPassword.requestFocus();
        } else if (!password.equalsIgnoreCase(confirmpassword)) {

            App.showSnackBar(tvTitle, "Both password should match. Please enter both passwords same.");

            etPassword.requestFocus();
        } else {
            if (App.isInternetAvail(ActSignup.this)) {
                asyncSignup();
            } else {
                App.showSnackBar(tvTitle, getString(R.string.strNetError));
            }
        }
    }

    ///////////// PROFILE PIC - Select option - GALLERY/CAMERA ////////////
    private void openBottomSheetPhoto() {
        try {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.bottom_sheet_getimage, null, false);

            TextView header = (TextView) view.findViewById(R.id.header);
            header.setText(strTagChooseImage);

            TextView tvGallery = (TextView) view.findViewById(R.id.tvGallery);
            TextView tvCamera = (TextView) view.findViewById(R.id.tvCamera);

            tvGallery.setText(strTagGallery);
            tvCamera.setText(strTagCamera);


            header.setTypeface(App.getFont_Regular());
            tvGallery.setTypeface(App.getFont_Regular());
            tvCamera.setTypeface(App.getFont_Regular());

            final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(ActSignup.this, R.style.BottomSheetDialog);
            mBottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mBottomSheetDialog.setContentView(view);
            mBottomSheetDialog.setCancelable(true);

            mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
            mBottomSheetDialog.show();
            tvGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBottomSheetDialog.dismiss();

                    ivProfPic.setImageDrawable(null);
                    Crop.pickImage(ActSignup.this);
                }
            });

            tvCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBottomSheetDialog.dismiss();

                    ivProfPic.setImageDrawable(null);
                    getImageFromCamera();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /////////// CAPTURE PROFILE PIC FROM CAMERA - FUNCTION //////////
    private void getImageFromCamera() {
        App.showLog(TAG, "From CAMERA Setting Capture TRUE");

        try {


            File file2 = new File(sdCardPath + "/" + App.APP_FOLDERNAME + "");
            if (!file2.exists()) {
                if (!file2.mkdirs()) {
                    //System.out.println("==Create Directory "+App.APP_FOLDERNAME+"====");
                } else {
                    //System.out.println("==No--1Create Directory "+App.APP_FOLDERNAME+"====");
                }
            } else {
                //System.out.println("== already created---No--2Create Directory "+App.APP_FOLDERNAME+"====");
            }


            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().toString(), "/"+App.APP_FOLDERNAME+"/tempimage.jpg"));
            mImageCaptureUri = Uri.fromFile(new File(sdCardPath, "/" + App.APP_FOLDERNAME + "/ProfPhoto.jpg"));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            intent.putExtra("return-data", true);

            photoBitmap = null;
            startActivityForResult(intent, CAMERA_REQESTCODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            ivProfPic.setImageURI(Crop.getOutput(result));

            try {
                photoBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Crop.getOutput(result));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    ///////////// FACEBOOK LOGIN //////////////
    private void setFacebookLoginWithPermission() {
        try {
            LoginManager.getInstance().logInWithReadPermissions(
                    ActSignup.this,
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
                                        ivFacebook.performClick();
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
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            callbackManager.onActivityResult(requestCode, resultCode, data);

            //Adding the login result back to the button
            twitterLoginButton.onActivityResult(requestCode, resultCode, data);

            if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            }


            { // Image set to profile code
                if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {

                    App.showLog("===requestCode==Crop.REQUEST_PICK==onActivityResult====" + requestCode);
                    beginCrop(data.getData());

                } else if (requestCode == Crop.REQUEST_CROP) {

                    App.showLog("===Crop.REQUEST_CROP==onActivityResult====" + requestCode);
                    handleCrop(resultCode, data);

                } else if (requestCode == CAMERA_REQESTCODE && resultCode == RESULT_OK) {

                    App.showLog("===CAMERA_REQESTCODE==onActivityResult====" + requestCode);
                    beginCrop(mImageCaptureUri);

                } else {
                    App.showLog("===ELSE==onActivityResult====" + requestCode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {
        try {
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
                // Signed out, show unauthenticated UI.
                App.showSnackBar(tvTitle, getString(R.string.strSomethingWentwrong));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void socialLogin(final String fid, final String fname, final String gplusid, final String email, String twiter_id) {
        try {
            App.showLog("FB socialLogin url", "==fid==" + fid);
            App.showLog("FB socialLogin url", "==fname==" + fname);
            App.showLog("G Plus socialLogin url", "==gplusid==" + gplusid);
            App.showLog("FB socialLogin url", "==email==" + email);
            App.showLog("Twitter socialLogin url", "==email==" + twiter_id);

            try {
                if (strSocialImageUrl != null && strSocialImageUrl.length() > 0) {
                    App.showLog("==get social image bitmap===URL=" + strSocialImageUrl);

                    DownloadImage downloadImage = new DownloadImage();
                    downloadImage.execute(strSocialImageUrl);

                    //photoBitmap =  getBitmapFromURL(strSocialImageUrl);
                } else {
                    App.showLog("==strSocialImageUrl===null====URL=" + strSocialImageUrl);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            strEmail = email;
            strFName = fname;

            if (email != null && email.length() > 3) {
                etEmail.setText(email);
                etEmail.setEnabled(false);
            }
            if (fname != null && fname.length() > 3) {
                etFirstName.setText(fname);
            }

            strFBid = fid;
            strGPid = gplusid;
            strTwtrid = twiter_id;


            if (App.isInternetAvail(ActSignup.this)) {
                asyncSigninSocial();
            } else {
                App.showSnackBar(tvTitle, getString(R.string.strNetError));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setApiData() {
        try {
            retrofitApiCall = new Retrofit.Builder()
                    .baseUrl(App.strBaseHostUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofitApiCall.create(ApiService.class);
            customProgressDialog = new CustomProgressDialog(ActSignup.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /////////// SIGN UP API CALL FUNCTION ////////////
    public void asyncSignup() {

        customProgressDialog.show();
        String isEmail = "1";
        if (etEmail.isEnabled() == false) {
            isEmail = "0";
        }

        callApiMethod = apiService.setRegisterNewUser(App.OP_REGISTER,
                strFName,
                strLName,
                strEmail,
                strPassword,
                App.sharePrefrences.getStringPref(PreferencesKeys.strDeviceId),
                App.APP_PLATFORM,
                App.APP_MODE,
                App.sharePrefrences.getStringPref(PreferencesKeys.strDeviceId),
                isEmail,
                strFBid,
                strGPid,
                strTwtrid);

        App.showLogApi("OP_REGISTER--" + App.OP_REGISTER
                + "&fname=" + strFName
                + "&lname=" + strLName
                + "&email=" + strEmail
                + "&pwd=" + strPassword
                + "&device_id=" + App.sharePrefrences.getStringPref(PreferencesKeys.strDeviceId)
                + "&platform=" + App.APP_PLATFORM
                + "&app_mode=" + App.APP_MODE
                + "&fbid=" + strFBid
                + "&isEmail=" + isEmail
                + "&gid=" + strGPid
                + "&tid=" + strTwtrid
        );

        File userimagefile = new File("");
        if (photoBitmap != null) {
            FileOutputStream out = null;
            try {
                String path = Environment.getExternalStorageDirectory().toString();
                userimagefile = new File(path, "photo.png");
                out = new FileOutputStream(userimagefile);

                if (photoBitmap.getHeight() > 800) {
                    App.showLog("==Resize to ===800===");
                    photoBitmap = App.getResizedBitmap(photoBitmap, 800);
                    photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                } else {
                    App.showLog("===else==100=");
                    photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else {
            userimagefile = null;
        }

        if (userimagefile != null) {

            // create RequestBody instance from file
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), userimagefile);
            // MultipartBody.Part is used to send also the actual file name
            MultipartBody.Part bodys = MultipartBody.Part.createFormData("avatar", userimagefile.getName(), requestFile);

           /* @Query("op") String op,
            @Query("fullname") String fnm,
            @Query("email") String email,
            @Query("pass") String pass,
            @Query("mobile") String mobile,
            @Query("civil_id") String civil_id,
            @Query("device_id") String device_id,
            @Query("platform") String platform,
            @Query("refer_code") String refer_code,
            @Query("app_mode") String app_mode,
            @Query("user_type") String user_type,
            @Query("otp") String otp,
            @Part MultipartBody.Part filePhoto*/

            callApiMethod = apiService.setRegisterNewUser(App.OP_REGISTER,
                    strFName,
                    strLName,
                    strEmail,
                    strPassword,
                    App.sharePrefrences.getStringPref(PreferencesKeys.strDeviceId),
                    App.APP_PLATFORM,
                    App.APP_MODE,
                    App.sharePrefrences.getStringPref(PreferencesKeys.strDeviceId),
                    isEmail,
                    strFBid,
                    strGPid,
                    strTwtrid,
                    bodys);

            App.showLogApi("OP_REGISTER--" + App.OP_REGISTER
                    + "&fname=" + strFName
                    + "&fname=" + strLName
                    + "&email=" + strEmail
                    + "&pwd=" + strPassword
                    + "&device_id=" + App.sharePrefrences.getStringPref(PreferencesKeys.strDeviceId)
                    + "&platform=" + App.APP_PLATFORM
                    + "&app_mode=" + App.APP_MODE

                    + "&isEmail=" + isEmail
                    + "&fbid=" + strFBid
                    + "&gid=" + strGPid
                    + "&tid=" + strTwtrid
                    + "&avatar====pass image=" + strTwtrid
            );
        } else {
            App.showLog("============No Profile image add=========");
            //   App.showSnackBar(tvTitle, "Please upload image for register your profile.");
        }


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
                                App.showLog("Test---register simple----error-", "" + responseBody.string());
                                App.showSnackBar(tvTitle, getString(R.string.strSomethingWentwrong));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        //200 sucess
                        App.showLogApiRespose(TAG + App.OP_REGISTER, response);

                        if (model != null && model.status != null && model.status.equalsIgnoreCase("1")) {

                            App.showLog(TAG + "======register simple Main Success======1111111==");
                            // set for the user is login
                            App.sharePrefrences.setPref(PreferencesKeys.strLogin, "0");

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
                                if (model.data.uimg != null && model.data.uimg.length() > 0) {
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
                            }

                            if (etEmail.isEnabled() == false) {
                                Intent intent = new Intent(ActSignup.this, ActDashboard.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                App.myStartActivity(ActSignup.this, intent);
                            } else {

                                //You have successfully registered, please verify your email address
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                        ActSignup.this);
                                alertDialogBuilder
                                        .setMessage("You have successfully registered, please verify your email address")
                                        .setCancelable(false)
                                        .setPositiveButton("ok",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        Intent login = new Intent(ActSignup.this, ActLogin.class);
                                                        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(login);
                                                        animFinishActivity();
                                                        finish();
                                                    }
                                                });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }

                        } else {
                            App.showLog(TAG + "======register simple else========");

                            if (model != null && model.msg != null && model.msg.length() > 0) {
                                App.showSnackBar(tvTitle, model.msg);
                            } else {
                                App.showSnackBar(tvTitle, getString(R.string.strSomethingWentwrong));

                            }
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

    }


    // http://webzzserverurl.com/foldernm/ws.foldernm.php?op=LoginSocial&fbid=123123&device_id=123123&platform=1
    public void asyncSigninSocial() {
        customProgressDialog.show();
        callApiMethod = apiService.setCheckSocialUser(App.OP_LOGINSOCIAL,
                strFBid,
                strGPid,
                strTwtrid,
                strEmail,
                App.APP_MODE,
                App.APP_PLATFORM,
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
                        //  Log.e("Test---status-", "" + model.status);
                        App.showLogApiRespose(TAG + App.OP_LOGINSOCIAL, response);

                        if (model != null && model.status != null) {
                            if (model.status.equalsIgnoreCase("1")) {
                                App.showLog(TAG + "======register social Main Success======1111111==");
                                // set for the user is login
                                App.sharePrefrences.setPref(PreferencesKeys.strLogin, "1");

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
                                }

                                Intent login = new Intent(ActSignup.this, ActDashboard.class);
                                login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                App.myStartActivity(ActSignup.this, login);
                            } else {
                                tvSubmit.performClick();
                            }
                        } else {
                            App.showLog(TAG + "======signup==fail======");
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

    }


    class DownloadImage extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            customProgressDialog.show();
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
            customProgressDialog.dismiss();
            if (photoBitmap != null) {
                ivProfPic.setImageBitmap(photoBitmap);
                App.showLog("===getting image sucessfully===");
            }
            super.onPostExecute(result);
        }

    }


    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            App.showLog("==social image loaded sucess==");
            return myBitmap;
        } catch (Exception e) {
            // Log exception
            e.printStackTrace();
            App.showLog("==social image loaded fail==");
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}