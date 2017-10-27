package com.commonapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.api.ApiService;
import com.api.model.CityListModel;
import com.api.model.CountryListModel;
import com.api.model.StateListModel;
import com.api.response.CityListResponse;
import com.api.response.CountryListResponse;
import com.api.response.StateListResponse;
import com.api.response.ViewProfileResponse;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;
import com.utils.AppFlags;
import com.utils.CircularImageView;
import com.utils.CustomProgressDialog;
import com.utils.PreferencesKeys;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActEditProfile extends BaseActivity {

    String TAG = "==ActEditProfile==";

    EditText etFirstName, etLastName, etEmail,
            etWeb,
            etBio,
            etAge,
            etHeight,
            etWeight,

    etCompany,
            etClgUni;

    TextView tvChangePassword, etCountry, etState, etCity, etFacebook, etTwitter;


    TextView tvSubmit;
    CircularImageView ivProfPic;

    String strFName = "", strLName = "", strEmail = "";
    String strUrl = "", strAbout = "", strCollege = "", strCompany = "";
    String strAge = "", strWeight = "", strHeight = "";
    String strCity = "", strState = "", strCountry = "";
    String strCountryId = "", strStateId = "", strCityId = "";

    String sdCardPath;
    Uri mImageCaptureUri;
    int CAMERA_REQESTCODE = 104;


    // for the api call
    Retrofit retrofitApiCall;
    ApiService apiService;
    Call callApiMethod;

    CustomProgressDialog customProgressDialog;
    Bitmap photoBitmap;

    ViewProfileResponse myProfileResponse;
    String strTagChooseImage = "choose image";
    String strTagGallery = "Gallery";
    String strTagCamera = "Camera";


    CountryListResponse countryListResponse;
    StateListResponse stateListResponse;
    CityListResponse cityListResponse;


    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            App.showLog(TAG);
            ViewGroup.inflate(this, R.layout.act_edit_profile, llContainerSub);

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            initialization();
            setClickEvents();
            setApiData();


            if (App.isInternetAvail(getApplicationContext())) {
                asyncGetMyProfile();
            } else {
                App.showSnackBar(tvTitle, getString(R.string.strNetError));
            }

            sdCardPath = Environment.getExternalStorageDirectory().toString();

            ivProfPic.setBorderWidth(2);
            ivProfPic.setBorderColor(Color.WHITE);

            App.getInstance().trackScreenView(getString(R.string.scrn_ActEditProfile));
        } catch (Exception e) {
            // TODO: handle exceptione.
            e.printStackTrace();
        }
    }

    private void initialization() {
        try {

            rlBack.setVisibility(View.VISIBLE);
            setEnableDrawer(false);
            tvTitle.setText("Edit Profile");

            etFirstName = (EditText) findViewById(R.id.etFirstName);
            etLastName = (EditText) findViewById(R.id.etLastName);
            etEmail = (EditText) findViewById(R.id.etEmail);
            etWeb = (EditText) findViewById(R.id.etWeb);
            etBio = (EditText) findViewById(R.id.etBio);
            etAge = (EditText) findViewById(R.id.etAge);
            etHeight = (EditText) findViewById(R.id.etHeight);
            etWeight = (EditText) findViewById(R.id.etWeight);

            etCompany = (EditText) findViewById(R.id.etCompany);
            etClgUni = (EditText) findViewById(R.id.etClgUni);

            tvChangePassword = (TextView) findViewById(R.id.tvChangePassword);
            etCountry = (TextView) findViewById(R.id.etCountry);
            etState = (TextView) findViewById(R.id.etState);
            etCity = (TextView) findViewById(R.id.etCity);
            etFacebook = (TextView) findViewById(R.id.etFacebook);
            etTwitter = (TextView) findViewById(R.id.etTwitter);

            tvSubmit = (TextView) findViewById(R.id.tvSubmit);
            ivProfPic = (CircularImageView) findViewById(R.id.ivProfPic);

            /*****************************************************/
            //////////// Set External fonts to labels /////////////
            /*****************************************************/
            tvTitle.setTypeface(App.getFont_Regular());

            etFirstName.setTypeface(App.getFont_Regular());
            etLastName.setTypeface(App.getFont_Regular());
            etEmail.setTypeface(App.getFont_Regular());

            etWeb.setTypeface(App.getFont_Regular());
            etBio.setTypeface(App.getFont_Regular());
            etAge.setTypeface(App.getFont_Regular());
            etHeight.setTypeface(App.getFont_Regular());
            etWeight.setTypeface(App.getFont_Regular());
            etCity.setTypeface(App.getFont_Regular());
            etCompany.setTypeface(App.getFont_Regular());
            etClgUni.setTypeface(App.getFont_Regular());

            tvChangePassword.setTypeface(App.getFont_Regular());
            etCountry.setTypeface(App.getFont_Regular());
            etState.setTypeface(App.getFont_Regular());
            etFacebook.setTypeface(App.getFont_Regular());
            etTwitter.setTypeface(App.getFont_Regular());

            tvSubmit.setTypeface(App.getFont_Regular());

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
                    if (v instanceof MaterialEditText) {
                        System.out.println("=touch no hide edittext==2=");
                    } else {
                        System.out.println("===on touch hide=2=");
                        //app.hideKeyBoard(v);
                        App.hideSoftKeyboardMy(ActEditProfile.this);
                    }
                    return true;
                }
            });

            rlBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            // SUBMIT click event
            tvSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    strFName = etFirstName.getText().toString().trim();
                    strLName = etLastName.getText().toString().trim();
                    strEmail = etEmail.getText().toString().trim();
                    strUrl = etWeb.getText().toString().trim();
                    strAbout = etBio.getText().toString().trim();

                    strAge = etAge.getText().toString().trim();
                    strHeight = etHeight.getText().toString().trim();
                    strWeight = etWeight.getText().toString().trim();

                    strCompany = etCompany.getText().toString().trim();
                    strCollege = etClgUni.getText().toString().trim();

                    checkSignupValidation(strFName, strLName);

                }
            });

            tvChangePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ActEditProfile.this, ActChangePassword.class);
                    intent.putExtra(AppFlags.tagFrom, "ActEditProfile");
                    startActivity(intent);
                    animStartActivity();
                }
            });

            // Profile Pic image click event
            ivProfPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openBottomSheetPhoto();
                }
            });


            etCountry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (countryListResponse != null && countryListResponse.arrayListCountryListModel != null && countryListResponse.arrayListCountryListModel.size() > 0) {
                        openCountryBottomSheet(etCountry);
                    } else {
                        if (App.isInternetAvail(getApplicationContext())) {
                            asyncGetCountryList();
                       /* strCountryId = "";
                        strStateId = "";
                        strCityId = "";*/
                        } else {
                            App.showSnackBar(etEmail, getResources().getString(R.string.strNetError));
                        }
                    }
                }
            });

            etState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (stateListResponse != null && stateListResponse.arrayListStateListModel != null && stateListResponse.arrayListStateListModel.size() > 0) {
                        openStateBottomSheet(etState);
                    } else {
                        if (strCountryId != null && strCountryId.length() > 0) {
                            if (App.isInternetAvail(getApplicationContext())) {
                                asyncGetStateList();
                       /* strCountryId = "";
                        strStateId = "";
                        strCityId = "";*/
                            } else {
                                App.showSnackBar(etEmail, getResources().getString(R.string.strNetError));
                            }
                        } else {
                            App.showSnackBar(tvSubmit, "Please select country.");
                        }
                    }
                }
            });

            etCity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cityListResponse != null && cityListResponse.arrayListCityListModel != null && cityListResponse.arrayListCityListModel.size() > 0) {
                        openCityBottomSheet(etCity);
                    } else {
                        if (strCountryId != null && strCountryId.length() > 0) {
                            if (strStateId != null && strStateId.length() > 0) {
                                if (App.isInternetAvail(getApplicationContext())) {
                                    asyncGetCityList();
                       /* strCountryId = "";
                        strStateId = "";
                        strCityId = "";*/
                                } else {
                                    App.showSnackBar(etEmail, getResources().getString(R.string.strNetError));
                                }
                            } else {
                                App.showSnackBar(tvSubmit, "Please select state.");
                            }
                        } else {
                            App.showSnackBar(tvSubmit, "Please select country.");
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    ////////////// VALIDATION CHECK ON SUBMIT CLICK ///////////////
    private void checkSignupValidation(String fname, String lname) {

        if (fname == null || fname.length() == 0) {
            App.showSnackBar(tvTitle, "Please enter first name");
            etFirstName.requestFocus();
        } else if (fname == null || fname.length() == 0 || fname.length() <= 2) {
            App.showSnackBar(tvTitle, "Please enter valid first name");
            etFirstName.requestFocus();
        } else if (lname == null || lname.length() == 0) {
            App.showSnackBar(tvTitle, "Please enter last name");
            etLastName.requestFocus();
        } else if (fname == null || fname.length() == 0 || fname.length() <= 2) {
            App.showSnackBar(tvTitle, "Please enter valid last name");

            etLastName.requestFocus();
        } else {
            if (App.isInternetAvail(ActEditProfile.this)) {
                asyncEditProfile();
            } else {
                App.showSnackBar(tvTitle, getString(R.string.strNetError));
            }
        }
    }


    ///////////// PROFILE PIC - Select option - GALLERY/CAMERA ////////////
    private void openBottomSheetPhoto() {

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

        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(ActEditProfile.this, R.style.BottomSheetDialog);
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
                Crop.pickImage(ActEditProfile.this);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


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

    }


    private void setApiData() {
        try {
            retrofitApiCall = new Retrofit.Builder()
                    .baseUrl(App.strBaseHostUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofitApiCall.create(ApiService.class);
            customProgressDialog = new CustomProgressDialog(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /////////// EDIR PROFILE API CALL FUNCTION ////////////
    // http://serverpath/foldernm/ws.halacab.php?op=EditProfile&uid=116&fullname=xyz abcd
    public void asyncEditProfile() {
        try {
            customProgressDialog.show();


            File userimagefile = null;
            if (photoBitmap != null) {
                FileOutputStream out = null;
                try {
                    userimagefile = new File(sdCardPath, "/" + App.APP_FOLDERNAME + "/ProfPhoto.jpg");
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

                MultipartBody.Part bodys = MultipartBody.Part.createFormData("avatar", userimagefile.getName(), requestFile);

                callApiMethod = apiService.setUpdateUserProfile(
                        App.OP_EDITPROFILE,
                        App.sharePrefrences.getStringPref(PreferencesKeys.strUserId),

                        strFName,
                        strLName,
                        strEmail,

                        strAge,
                        strWeight,
                        strHeight,

                        "",


                        strCityId,
                        strStateId,
                        strCountryId,

                        "",
                        "",
                        strAbout,
                        strCollege,
                        strCompany,
                        strUrl,

                        App.APP_PLATFORM,
                        App.APP_MODE,
                        App.sharePrefrences.getStringPref(PreferencesKeys.strDeviceId),
                        App.sharePrefrences.getStringPref(PreferencesKeys.strDeviceId),
                        App.sharePrefrences.getStringPref(PreferencesKeys.strAccessToken),
                        bodys);

                App.showLog("============Yes Profile image add=========");
            } else {
                callApiMethod = apiService.setUpdateUserProfile(App.OP_EDITPROFILE,
                        App.sharePrefrences.getStringPref(PreferencesKeys.strUserId),

                        strFName,
                        strLName,
                        strEmail,

                        strAge,
                        strWeight,
                        strHeight,

                        "",


                        strCityId,
                        strStateId,
                        strCountryId,

                        "",
                        "",
                        strAbout,
                        strCollege,
                        strCompany,
                        strUrl,

                        App.APP_PLATFORM,
                        App.APP_MODE,
                        App.sharePrefrences.getStringPref(PreferencesKeys.strDeviceId),
                        App.sharePrefrences.getStringPref(PreferencesKeys.strDeviceId),
                        App.sharePrefrences.getStringPref(PreferencesKeys.strAccessToken));
                App.showLog("============No Profile image add=========");
            }


            App.showLogApi("OP_EDITPROFILE--" + App.OP_EDITPROFILE
                    + "=uid=" + App.sharePrefrences.getStringPref(PreferencesKeys.strUserId)
                    + "&fnm=" + strFName
                    + "&email=" + strEmail

                    + "&ctrid=" + strCountryId
                    + "&sid=" + strStateId
                    + "&ctid=" + strCityId

                    + "&platform=" + App.APP_PLATFORM
                    + "&app_mode=" + App.APP_MODE
                    + "&device_id=" + App.sharePrefrences.getStringPref(PreferencesKeys.strDeviceId)
                    + "&android_token=" + App.sharePrefrences.getStringPref(PreferencesKeys.strDeviceId)
                    + "&accessToken=" + App.sharePrefrences.getStringPref(PreferencesKeys.strAccessToken)
            );


            callApiMethod.enqueue(new Callback<ViewProfileResponse>() {
                @Override
                public void onResponse(Call<ViewProfileResponse> call, Response<ViewProfileResponse> response) {
                    try {
                        customProgressDialog.dismiss();
                        ViewProfileResponse model = response.body();

                        if (model == null) {
                            //404 or the response cannot be converted to User.
                            App.showLog("Test---null response--", "==Something wrong=");
                            ResponseBody responseBody = response.errorBody();
                            if (responseBody != null) {
                                try {
                                        App.showSnackBar(tvTitle, getString(R.string.strSomethingWentwrong));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            //200 sucess
                            App.showLogApiRespose(App.OP_EDITPROFILE, response);

                            if (model.status != null && model.status.length() > 0) {
                                if (model.status.equalsIgnoreCase("1")) {
                                    App.sharePrefrences.setPref(PreferencesKeys.strLogin, "1");

                                    if (model.data != null) {
                                        if (model.data.uid != null) {
                                            App.showLog("===ViewProfileResponse=uid=" + model.data.uid);
                                            App.sharePrefrences.setPref(PreferencesKeys.strUserId, model.data.uid);
                                        }

                                        if (model.data.name != null) {
                                            App.sharePrefrences.setPref(PreferencesKeys.strUserFullName, model.data.name);
                                            tvUsernameBase.setText(model.data.name);
                                        }
                                        if (model.data.fname != null) {
                                            App.sharePrefrences.setPref(PreferencesKeys.strUserFirstName, model.data.fname);
                                        }
                                        if (model.data.lname != null) {
                                            App.sharePrefrences.setPref(PreferencesKeys.strUserLastName, model.data.lname);
                                        }

                                        if (model.data.email != null) {
                                            App.showLog("===ViewProfileResponse=email=" + model.data.email);
                                            App.sharePrefrences.setPref(PreferencesKeys.strUserEmail, model.data.email);
                                        }


                                        if (model.data.uimg != null) {
                                            App.sharePrefrences.setPref(PreferencesKeys.strUserImage, model.data.uimg);

                                            Picasso.with(getApplicationContext())
                                                    .load(App.strBaseUploadedPicUrl + App.sharePrefrences.getStringPref(PreferencesKeys.strUserImage))
                                                    .fit().centerCrop()
                                                    .into(ivProfPic);

                                            Picasso.with(getApplicationContext())
                                                    .load(App.strBaseUploadedPicUrl + App.sharePrefrences.getStringPref(PreferencesKeys.strUserImage))
                                                    .fit().centerCrop()
                                                    .into(ivUserPhotoBase);
                                        }
                                        if (model.data.city != null) {
                                            App.sharePrefrences.setPref(PreferencesKeys.strUserCity, model.data.city);
                                        }
                                        if (model.data.state != null) {
                                            App.sharePrefrences.setPref(PreferencesKeys.strUserState, model.data.state);
                                        }
                                        if (model.data.cntry != null) {
                                            App.sharePrefrences.setPref(PreferencesKeys.strUserCountry, model.data.cntry);
                                        }

                                    }


                                } /*else if (model.status.equalsIgnoreCase(App.APP_LOGOUTSTATUST)) {
                                    asyncLogout();
                                } */

                                {
                                    if (model.msg != null && model.msg.length() > 0) {
                                        App.showSnackBar(etEmail, model.msg);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        customProgressDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<ViewProfileResponse> call, Throwable t) {
                    t.printStackTrace();
                    customProgressDialog.dismiss();
                        App.showSnackBar(etEmail, getString(R.string.strSomethingWentwrong));

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /////////// My Profile Info Detail API CALL //////////
    // http://serverpath/foldernm/ws.halacab.php?op=getinfo&uid=116&user_type=0&platform=2&app_mode=2&device_id=cngwfT5EQDY:
    public void asyncGetMyProfile() {

        try {
            customProgressDialog.show();
            callApiMethod = apiService.getMyProfile(
                    App.OP_VIEW_PROFILE,
                    App.sharePrefrences.getStringPref(PreferencesKeys.strUserId),

                    App.APP_PLATFORM,
                    App.APP_MODE,
                    App.APP_USERTYPE,
                    App.sharePrefrences.getStringPref(PreferencesKeys.strAccessToken)
            );

            App.showLogApi("--OP_GET_MY_PROFILE_INFO--" + App.OP_VIEW_PROFILE
                    + "&uid=" + App.sharePrefrences.getStringPref(PreferencesKeys.strUserId)

                    + "&platform=" + App.APP_PLATFORM
                    + "&app_mode=" + App.APP_MODE
                    + "&user_type=" + App.APP_USERTYPE
                    + "&accessToken=" + App.sharePrefrences.getStringPref(PreferencesKeys.strAccessToken)
            );

            callApiMethod.enqueue(new Callback<ViewProfileResponse>() {
                @Override
                public void onResponse(Call<ViewProfileResponse> call, Response<ViewProfileResponse> response) {
                    customProgressDialog.dismiss();
                    myProfileResponse = response.body();

                    if (myProfileResponse == null) {
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
                        App.showLogApiRespose(App.OP_VIEW_PROFILE, response);


                        if (myProfileResponse.status != null && myProfileResponse.status.length() > 0) {
                            if (myProfileResponse.status.equalsIgnoreCase("1")) {

                                AppFlags.isEditProfile = true;
                                AppFlags.isEditProfileBase = true;

                                if (myProfileResponse != null && myProfileResponse.data != null) {
                                    App.showLog("==set profile data==");
                                    setProfileData();
                                }
                            } else {
                                if (myProfileResponse.msg != null) {
                                    App.showSnackBar(rlMenu, myProfileResponse.msg);
                                }
                            }

                            /*else if (myProfileResponse.status.equalsIgnoreCase(App.APP_LOGOUTSTATUST)) {
                                asyncLogout();
                            }*/

                        }
                    }
                }

                @Override
                public void onFailure(Call<ViewProfileResponse> call, Throwable t) {
                    t.printStackTrace();
                    customProgressDialog.dismiss();

                        App.showSnackBar(tvTitle, getString(R.string.strSomethingWentwrong));

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /////// GET PROFILE INFO OF USER ////////////
    private void setProfileData() {
        try {
            if (myProfileResponse != null && myProfileResponse.data != null) {
                ViewProfileResponse.Data dataProfile = myProfileResponse.data;

                if (dataProfile.fname != null && dataProfile.fname.length() > 0) {
                    strFName = dataProfile.fname;
                    App.sharePrefrences.setPref(PreferencesKeys.strUserFirstName, strFName);
                }
                if (dataProfile.lname != null && dataProfile.lname.length() > 0) {
                    strLName = dataProfile.lname;
                    App.sharePrefrences.setPref(PreferencesKeys.strUserLastName, strLName);
                }

                if (strFName != null && strLName != null && strFName.length() > 0) {
                    App.sharePrefrences.setPref(PreferencesKeys.strUserFullName, strFName + " " + strLName);
                }

                if (dataProfile.ctid != null && dataProfile.ctid.length() > 0) {
                    strCityId = dataProfile.ctid;
                    App.sharePrefrences.setPref(PreferencesKeys.strUserCityId, strCityId);
                }
                if (dataProfile.sid != null && dataProfile.sid.length() > 0) {
                    strStateId = dataProfile.sid;
                    App.sharePrefrences.setPref(PreferencesKeys.strUserStateId, strStateId);
                }
                if (dataProfile.ctrid != null && dataProfile.ctrid.length() > 0) {
                    strCountryId = dataProfile.ctrid;
                    App.sharePrefrences.setPref(PreferencesKeys.strUserCountryId, strCountryId);
                }


                if (dataProfile.email != null && dataProfile.email.length() > 1) {
                    strEmail = dataProfile.email;
                    App.sharePrefrences.setPref(PreferencesKeys.strUserEmail, strEmail);
                    etEmail.setText(strEmail);
                    etEmail.setEnabled(false);
                }

                if (dataProfile.uimg != null && dataProfile.uimg.length() > 1) {
                    App.sharePrefrences.setPref(PreferencesKeys.strUserImage, dataProfile.uimg);

                    Picasso.with(getApplicationContext())
                            .load(App.strBaseUploadedPicUrl + App.sharePrefrences.getStringPref(PreferencesKeys.strUserImage))
                            .fit().centerCrop()
                            .into(ivProfPic);

                    Picasso.with(getApplicationContext())
                            .load(App.strBaseUploadedPicUrl + App.sharePrefrences.getStringPref(PreferencesKeys.strUserImage))
                            .fit().centerCrop()
                            .into(ivUserPhotoBase);
                }

                etFirstName.setText(strFName);
                etLastName.setText(strLName);
                etEmail.setText(strEmail);

                if (dataProfile.age != null && dataProfile.age.length() > 1) {
                    strAge = dataProfile.age;
                    etAge.setText(strAge);
                }

                if (dataProfile.url != null && dataProfile.url.length() > 1) {
                    strUrl = dataProfile.url;
                    etWeb.setText(strUrl);
                }

                if (dataProfile.height != null && dataProfile.height.length() > 1) {
                    strHeight = dataProfile.height;
                    etHeight.setText(strHeight);
                }

                if (dataProfile.weight != null && dataProfile.weight.length() > 1) {
                    strWeight = dataProfile.weight;
                    etWeight.setText(strWeight);
                }

                if (dataProfile.college != null && dataProfile.college.length() > 1) {
                    strCollege = dataProfile.college;
                    etClgUni.setText(strCollege);
                }

                if (dataProfile.cntry != null && dataProfile.cntry.length() > 1) {
                    strCountry = dataProfile.cntry;
                    etCountry.setText(strCountry);
                }

                if (dataProfile.company != null && dataProfile.company.length() > 1) {
                    strCompany = dataProfile.company;
                    etCompany.setText(strCompany);
                }

                if (dataProfile.about != null && dataProfile.about.length() > 1) {
                    strAbout = dataProfile.about;
                    etBio.setText(strAbout);
                }


                if (dataProfile.state != null && dataProfile.state.length() > 1) {
                    strState = dataProfile.state;
                    etState.setText(strState);
                }

                if (dataProfile.city != null && dataProfile.city.length() > 1) {
                    strCity = dataProfile.city;
                    etCity.setText(strCity);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void asyncGetCountryList() {

        customProgressDialog.show();
        callApiMethod = apiService.getCountryList(
                App.OP_GETCOUNTRY,
                App.sharePrefrences.getStringPref(PreferencesKeys.strUserId));

        App.showLogApi("OP_GETCOUNTRY--" + App.OP_GETCOUNTRY
                + "---uid---" + App.sharePrefrences.getStringPref(PreferencesKeys.strUserId)
        );

        callApiMethod.enqueue(new Callback<CountryListResponse>() {
            @Override
            public void onResponse(Call<CountryListResponse> call, Response<CountryListResponse> response) {
                customProgressDialog.dismiss();
                countryListResponse = response.body();
                if (countryListResponse == null) {
                    //404 or the response cannot be converted to User.
                    App.showLog("Test---null response--", "==Something wrong=");
                    ResponseBody responseBody = response.errorBody();
                    if (responseBody != null) {
                        try {
                            App.showLog("Test---error-", "" + responseBody.string());
                            App.showSnackBar(rlMenu, getString(R.string.strSomethingWentwrong));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    //200 sucess
                    //  Log.e("Test---status-", "" + model.status);
                    if (countryListResponse != null && countryListResponse.arrayListCountryListModel != null && countryListResponse.arrayListCountryListModel.size() > 0) {
                        openCountryBottomSheet(etCountry);
                    }

                    if (countryListResponse.status.equalsIgnoreCase("1")) {

                    } else {
                        if (countryListResponse.msg != null)
                            App.showSnackBar(rlMenu, countryListResponse.msg);
                    }
                }
            }

            @Override
            public void onFailure(Call<CountryListResponse> call, Throwable t) {
                t.printStackTrace();
                customProgressDialog.dismiss();
                App.showSnackBar(rlMenu, getString(R.string.strSomethingWentwrong));
            }
        });

    }

    public void asyncGetStateList() {
        customProgressDialog.show();
        callApiMethod = apiService.getStateList(
                App.OP_GETSTATE,
                App.sharePrefrences.getStringPref(PreferencesKeys.strUserId),
                strCountryId
        );

        App.showLogApi("OP_GETSTATE--" + App.OP_GETSTATE
                + "---uid---" + App.sharePrefrences.getStringPref(PreferencesKeys.strUserId)
                + "--ctrid--" + strCountryId
        );

        callApiMethod.enqueue(new Callback<StateListResponse>() {
            @Override
            public void onResponse(Call<StateListResponse> call, Response<StateListResponse> response) {
                customProgressDialog.dismiss();
                stateListResponse = response.body();
                if (stateListResponse == null) {
                    //404 or the response cannot be converted to User.
                    App.showLog("Test---null response--", "==Something wrong=");
                    ResponseBody responseBody = response.errorBody();
                    if (responseBody != null) {
                        try {
                            App.showLog("Test---error-", "" + responseBody.string());
                            App.showSnackBar(rlMenu, getString(R.string.strSomethingWentwrong));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    //200 sucess
                    //  Log.e("Test---status-", "" + model.status);

                    if (stateListResponse.arrayListStateListModel != null && stateListResponse.arrayListStateListModel.size() > 0) {
                        openStateBottomSheet(etState);
                    }

                    if (stateListResponse.status.equalsIgnoreCase("1")) {

                    } else {
                        if (stateListResponse.msg != null)
                            App.showSnackBar(rlMenu, stateListResponse.msg);
                    }
                }
            }

            @Override
            public void onFailure(Call<StateListResponse> call, Throwable t) {
                t.printStackTrace();
                customProgressDialog.dismiss();
                App.showSnackBar(rlMenu, getString(R.string.strSomethingWentwrong));
            }
        });

    }

    public void asyncGetCityList() {
        customProgressDialog.show();
        callApiMethod = apiService.getCityList(
                App.OP_GETCITY,
                App.sharePrefrences.getStringPref(PreferencesKeys.strUserId),
                strCountryId,
                strStateId
        );

        App.showLogApi("OP_GETCITY--" + App.OP_GETCITY
                + "---uid---" + App.sharePrefrences.getStringPref(PreferencesKeys.strUserId)
                + "---ctrid---" + strCountryId
                + "---sid---" + strStateId

        );

        callApiMethod.enqueue(new Callback<CityListResponse>() {
            @Override
            public void onResponse(Call<CityListResponse> call, Response<CityListResponse> response) {
                customProgressDialog.dismiss();
                cityListResponse = response.body();
                if (cityListResponse == null) {
                    //404 or the response cannot be converted to User.
                    App.showLog("Test---null response--", "==Something wrong=");
                    ResponseBody responseBody = response.errorBody();
                    if (responseBody != null) {
                        try {
                            App.showLog("Test---error-", "" + responseBody.string());
                            App.showSnackBar(rlMenu, getString(R.string.strSomethingWentwrong));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    //200 sucess
                    //  Log.e("Test---status-", "" + model.status);

                    if (cityListResponse.arrayListCityListModel != null && cityListResponse.arrayListCityListModel.size() > 0) {
                        openCityBottomSheet(etCity);
                    }

                    if (cityListResponse.status.equalsIgnoreCase("1")) {

                    } else {
                        if (cityListResponse.msg != null)
                            App.showSnackBar(rlMenu, cityListResponse.msg);
                    }
                }
            }

            @Override
            public void onFailure(Call<CityListResponse> call, Throwable t) {
                t.printStackTrace();
                customProgressDialog.dismiss();
                App.showSnackBar(rlMenu, getString(R.string.strSomethingWentwrong));
            }
        });

    }


    private void openCountryBottomSheet(final TextView editText) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.bottom_sheet_list, null, false);

        TextView header = (TextView) view.findViewById(R.id.header);
        header.setText("Select Country");
        header.setTypeface(App.getFont_Bold());

        //LinearLayout ll_main = (LinearLayout) view.findViewById(R.id.ll_main);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        TextView cancle = (TextView) view.findViewById(R.id.cancle);
        TextView done = (TextView) view.findViewById(R.id.done);


        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(ActEditProfile.this, R.style.BottomSheetDialog);
        mBottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);

        CountryListAdapter adapter = new CountryListAdapter(ActEditProfile.this, countryListResponse.arrayListCountryListModel, editText, mBottomSheetDialog);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
            }
        });
    }


    public class CountryListAdapter extends RecyclerView.Adapter<CountryListAdapter.VersionViewHolder> {
        Context mContext;
        ArrayList<CountryListModel> arrListCountryListModels;
        TextView editText;

        BottomSheetDialog mBottomSheetDialog;


        public CountryListAdapter(Context context, ArrayList<CountryListModel> data, TextView editText, BottomSheetDialog mBottomSheetDialog) {
            this.mContext = context;
            this.arrListCountryListModels = data;
            this.editText = editText;
            this.mBottomSheetDialog = mBottomSheetDialog;
        }

        @Override
        public CountryListAdapter.VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_bottom_sheetlist, viewGroup, false);
            CountryListAdapter.VersionViewHolder viewHolder = new CountryListAdapter.VersionViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final CountryListAdapter.VersionViewHolder versionViewHolder, final int position) {

            versionViewHolder.tvTitle.setText(arrListCountryListModels.get(position).ctrnm);

            versionViewHolder.rlMainRaw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (strCountry.equalsIgnoreCase(arrListCountryListModels.get(position).ctrnm)) {
                        // No changes in country
                    } else {
                        if (arrListCountryListModels.get(position).ctrnm != null) {
                            strCountry = arrListCountryListModels.get(position).ctrnm;
                            editText.setText(strCountry);
                        }
                        if (arrListCountryListModels.get(position).ctrid != null && arrListCountryListModels.get(position).ctrid.length() > 0) {
                            strCountryId = arrListCountryListModels.get(position).ctrid;
                        }
                        { // set null other data when select new country

                            stateListResponse = null;

                            strState = "";
                            strCity = "";

                            strStateId = "";
                            strCityId = "";

                            etState.setText("");
                            etCity.setText("");
                        }
                    }

                    mBottomSheetDialog.dismiss();
                }
            });

        }

        @Override
        public int getItemCount() {
            return arrListCountryListModels.size();
        }


        class VersionViewHolder extends RecyclerView.ViewHolder {

            TextView tvTitle;
            RelativeLayout rlMainRaw;

            public VersionViewHolder(View itemView) {
                super(itemView);
                tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
                rlMainRaw = (RelativeLayout) itemView.findViewById(R.id.rlMainRaw);

                tvTitle.setTypeface(App.getFont_Regular());
            }

        }

    }

//##################################  country finish ####################


    private void openStateBottomSheet(final TextView editText) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.bottom_sheet_list, null, false);

        TextView header = (TextView) view.findViewById(R.id.header);
        header.setText("Select State");

        header.setTypeface(App.getFont_Bold());

        // LinearLayout ll_main = (LinearLayout) view.findViewById(R.id.ll_main);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        TextView cancle = (TextView) view.findViewById(R.id.cancle);
        TextView done = (TextView) view.findViewById(R.id.done);


        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(ActEditProfile.this, R.style.BottomSheetDialog);
        mBottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);

        StateListAdapter adapter = new StateListAdapter(ActEditProfile.this, stateListResponse.arrayListStateListModel, editText, mBottomSheetDialog);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
            }
        });
    }


    public class StateListAdapter extends RecyclerView.Adapter<StateListAdapter.VersionViewHolder> {
        Context mContext;
        ArrayList<StateListModel> arrListStateListModel;
        TextView editText;

        BottomSheetDialog mBottomSheetDialog;


        public StateListAdapter(Context context, ArrayList<StateListModel> data, TextView editText, BottomSheetDialog mBottomSheetDialog) {
            this.mContext = context;
            this.arrListStateListModel = data;
            this.editText = editText;
            this.mBottomSheetDialog = mBottomSheetDialog;
        }

        @Override
        public StateListAdapter.VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_bottom_sheetlist, viewGroup, false);
            StateListAdapter.VersionViewHolder viewHolder = new StateListAdapter.VersionViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final StateListAdapter.VersionViewHolder versionViewHolder, final int position) {

            versionViewHolder.tvTitle.setText(arrListStateListModel.get(position).snm);

            versionViewHolder.rlMainRaw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (strState.equalsIgnoreCase(arrListStateListModel.get(position).snm)) {
                        // No changes in country
                    } else {


                        if (arrListStateListModel.get(position).snm != null) {
                            strState = arrListStateListModel.get(position).snm;
                            editText.setText(strState);
                        }
                        if (arrListStateListModel.get(position).sid != null && arrListStateListModel.get(position).sid.length() > 0) {
                            strStateId = arrListStateListModel.get(position).sid;
                        }

                        if (arrListStateListModel.get(position).ctrid != null && arrListStateListModel.get(position).ctrid.length() > 0) {
                            strCountryId = arrListStateListModel.get(position).ctrid;
                        }


                        { // set null other data when select new country
                            cityListResponse = null;

                            strCity = "";
                            strCityId = "";
                            etCity.setText("");
                        }
                    }

                    mBottomSheetDialog.dismiss();

                }
            });

        }

        @Override
        public int getItemCount() {
            return arrListStateListModel.size();
        }


        class VersionViewHolder extends RecyclerView.ViewHolder {

            TextView tvTitle;
            RelativeLayout rlMainRaw;

            public VersionViewHolder(View itemView) {
                super(itemView);
                tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
                rlMainRaw = (RelativeLayout) itemView.findViewById(R.id.rlMainRaw);

                tvTitle.setTypeface(App.getFont_Regular());
            }

        }

    }


//##################################  State finish ####################


    private void openCityBottomSheet(final TextView editText) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.bottom_sheet_list, null, false);

        TextView header = (TextView) view.findViewById(R.id.header);
        header.setText("Select City");

        header.setTypeface(App.getFont_Bold());

        //LinearLayout ll_main = (LinearLayout) view.findViewById(R.id.ll_main);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        TextView cancle = (TextView) view.findViewById(R.id.cancle);
        TextView done = (TextView) view.findViewById(R.id.done);


        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(ActEditProfile.this, R.style.BottomSheetDialog);
        mBottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);

        CityListAdapter adapter = new CityListAdapter(ActEditProfile.this, cityListResponse.arrayListCityListModel, editText, mBottomSheetDialog);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
            }
        });
    }

    public class CityListAdapter extends RecyclerView.Adapter<CityListAdapter.VersionViewHolder> {
        Context mContext;
        ArrayList<CityListModel> arrListCityListModel;
        TextView editText;

        BottomSheetDialog mBottomSheetDialog;


        public CityListAdapter(Context context, ArrayList<CityListModel> data, TextView editText, BottomSheetDialog mBottomSheetDialog) {
            this.mContext = context;
            this.arrListCityListModel = data;
            this.editText = editText;
            this.mBottomSheetDialog = mBottomSheetDialog;
        }

        @Override
        public CityListAdapter.VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_bottom_sheetlist, viewGroup, false);
            CityListAdapter.VersionViewHolder viewHolder = new CityListAdapter.VersionViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final CityListAdapter.VersionViewHolder versionViewHolder, final int position) {

            versionViewHolder.tvTitle.setText(arrListCityListModel.get(position).ctnm);

            versionViewHolder.rlMainRaw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (strCity.equalsIgnoreCase(arrListCityListModel.get(position).ctnm)) {
                        // No changes in country
                    } else {
                        if (arrListCityListModel.get(position).ctnm != null) {
                            strCity = arrListCityListModel.get(position).ctnm;
                            editText.setText(strCity);
                        }


                        if (arrListCityListModel.get(position).ctrid != null && arrListCityListModel.get(position).ctrid.length() > 0) {
                            strCountryId = arrListCityListModel.get(position).ctrid;
                        }
                        if (arrListCityListModel.get(position).sid != null && arrListCityListModel.get(position).sid.length() > 0) {
                            strStateId = arrListCityListModel.get(position).sid;
                        }
                        if (arrListCityListModel.get(position).ctid != null && arrListCityListModel.get(position).ctid.length() > 0) {
                            strCityId = arrListCityListModel.get(position).ctid;
                        }
                    }

                    mBottomSheetDialog.dismiss();
                }
            });

        }

        @Override
        public int getItemCount() {
            return arrListCityListModel.size();
        }


        class VersionViewHolder extends RecyclerView.ViewHolder {

            TextView tvTitle;
            RelativeLayout rlMainRaw;

            public VersionViewHolder(View itemView) {
                super(itemView);
                tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
                rlMainRaw = (RelativeLayout) itemView.findViewById(R.id.rlMainRaw);

                tvTitle.setTypeface(App.getFont_Regular());
            }

        }

    }


//##################################  city finish ####################


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        App.myFinishActivity(ActEditProfile.this);
    }
}