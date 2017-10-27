package com.commonapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.api.ApiService;
import com.api.response.CommonResponse;
import com.squareup.picasso.Picasso;
import com.utils.AppFlags;
import com.utils.CircularImageView;
import com.utils.PreferencesKeys;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BaseActivity extends FragmentActivity {


    protected View headerview;
    protected ListView lvMenuList;
    protected BaseMenuAdapter baseMenuAdapter;
    //protected  NavigationView navigationView;

    protected ImageView ivNotificationBase;
    protected TextView tvUsernameBase, tvUseremailBase, tvReqCounter;
    protected CircularImageView ivUserPhotoBase;
    protected RelativeLayout rlBaseMenuHeader;
    protected RelativeLayout rlLogout;


    String TAG = "==my- BaseActivity==";

    // for the Base full screen view and with sub screen view with header.
    protected LinearLayout llContainerMain, llContainerSub;

    // for the menu and back arrow
    protected RelativeLayout rlBaseMainHeader, rlMenu, rlBack;

    // for the text view header of the screen
    protected TextView tvTitle, tvTitle2;
    protected ImageView tvTitleImage;
    protected TextView tvSubTitle /*, tvExitMessage, tvCancel, tvOK*/;

    // for the main data in drawer layouts
    RelativeLayout left_drawer;
    // for the drawer layout
    protected DrawerLayout drawer;


    // for the Menu icons show at right side side
    protected RelativeLayout rlMenu4, rlMenu3, rlMenu2, rlMenu1;
    // for the menu images icons
    protected ImageView ivMenu4, ivMenu3, ivMenu2, ivMenu1;

    //for the app common class
    protected App app;
    protected PowerManager.WakeLock mWakeLock;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (App.blnFullscreenActvitity == true) {

/*getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
*/

/*


            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            View decorView = getWindow().getDecorView();

            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);*/

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            App.blnFullscreenActvitity = false;
        }


        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.act_baseactivity);

            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

           /* size = App.sharePrefrences.getArraySize(getApplicationContext(), PreferencesKeys.arrayAert);
            helper = new DatabaseHelper(this);*/

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            initialization();
            //setBaseLabelLanguage();
            setDrawerListAdapter();
            setBaseClickEvents();
            setFonts();

            final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
            //this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");

            App.getInstance().trackScreenView(getString(R.string.scrn_BaseActivity));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFonts() {
        tvTitle.setTypeface(App.getFont_Regular());
        tvTitle2.setTypeface(App.getFont_Regular());
        tvUsernameBase.setTypeface(App.getFont_Regular());
        tvUseremailBase.setTypeface(App.getFont_Regular());
        tvReqCounter.setTypeface(App.getFont_Regular());
    }


    private void setBaseClickEvents() {
        try {
            rlMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    if (drawer.isDrawerOpen(left_drawer)) {
                        drawer.closeDrawers();
                    } else {
                        drawer.openDrawer(left_drawer);
                    }
                }
            });
            rlBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    onBackPressed();
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
                        app.hideKeyBoard(v);
                    }
                    return true;
                }
            });
            rlBaseMenuHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (drawer.isDrawerOpen(left_drawer)) {
                        drawer.closeDrawers();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(BaseActivity.this, ActEditProfile.class);
                            intent.putExtra(AppFlags.tagFrom, "BaseActivity");
                            startActivity(intent);
                            animStartActivity();
                        }
                    }, 280);
                }
            });

            ivNotificationBase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (drawer.isDrawerOpen(left_drawer)) {
                        drawer.closeDrawers();
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(BaseActivity.this, ActNotification.class);
                            intent.putExtra(AppFlags.tagFrom, "BaseActivity");
                            startActivity(intent);
                            animStartActivity();
                        }
                    }, 280);
                }
            });
            rlLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // for the logout click


                    if (drawer.isDrawerOpen(left_drawer)) {
                        drawer.closeDrawers();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                           setLogout();
                        }
                    }, 280);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void initialization() {
        try {
            app = (App) getApplicationContext();

            lvMenuList = (ListView) findViewById(R.id.lvMenuList);
            LayoutInflater inflater = getLayoutInflater();
            headerview = inflater.inflate(R.layout.nav_header_act_home, null, false);
            lvMenuList.addHeaderView(headerview, null, false);

            rlBaseMainHeader = (RelativeLayout) findViewById(R.id.rlBaseMainHeader);
            llContainerMain = (LinearLayout) findViewById(R.id.llContainerMain);
            llContainerSub = (LinearLayout) findViewById(R.id.llContainerSub);
            rlMenu = (RelativeLayout) findViewById(R.id.rlMenu);

            rlBack = (RelativeLayout) findViewById(R.id.rlBack);
            tvTitle = (TextView) findViewById(R.id.tvTitle);
            tvTitle2 = (TextView) findViewById(R.id.tvTitle2);
            tvTitleImage = (ImageView) findViewById(R.id.tvTitleImage);
            tvSubTitle = (TextView) findViewById(R.id.tvSubTitle);
            // for the handel right side menu click 
            rlMenu4 = (RelativeLayout) findViewById(R.id.rlMenu4);
            rlMenu3 = (RelativeLayout) findViewById(R.id.rlMenu3);
            rlMenu2 = (RelativeLayout) findViewById(R.id.rlMenu2);
            rlMenu1 = (RelativeLayout) findViewById(R.id.rlMenu1);
            // for the right side menu images
            ivMenu4 = (ImageView) findViewById(R.id.ivMenu4);
            ivMenu3 = (ImageView) findViewById(R.id.ivMenu3);
            ivMenu2 = (ImageView) findViewById(R.id.ivMenu2);
            ivMenu1 = (ImageView) findViewById(R.id.ivMenu1);

            tvReqCounter = (TextView) findViewById(R.id.tvReqCounter);

            left_drawer = (RelativeLayout) findViewById(R.id.left_drawer);
            rlLogout = (RelativeLayout) findViewById(R.id.rlLogout);
            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            rlBaseMenuHeader = (RelativeLayout) headerview.findViewById(R.id.rlBaseMenuHeader);
            ivUserPhotoBase = (CircularImageView) headerview.findViewById(R.id.ivUserPhotoBase);
            tvUsernameBase = (TextView) headerview.findViewById(R.id.tvUsernameBase);
            tvUseremailBase = (TextView) headerview.findViewById(R.id.tvUseremailBase);

           ivNotificationBase = (ImageView) headerview.findViewById(R.id.ivNotificationBase);

            initDrawer();
            setDrawerListAdapter();

            tvTitle.setText(R.string.app_name);
            setBaseMenuDrawerHeaderData();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }


    protected void setDrawerListAdapter()
    {
        try{

            baseMenuAdapter = new BaseMenuAdapter(BaseActivity.this, getBaseMenuListData());
            lvMenuList.setAdapter(baseMenuAdapter);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public void setBaseMenuDrawerHeaderData() {
        try {

            if (tvUsernameBase != null && tvUseremailBase != null) {
                if (App.sharePrefrences.getStringPref(PreferencesKeys.strUserFullName) != null
                        && App.sharePrefrences.getStringPref(PreferencesKeys.strUserFullName).length() > 1) {
                    String strFullName = App.sharePrefrences.getStringPref(PreferencesKeys.strUserFullName);
                    tvUsernameBase.setText(strFullName);
                }
                if (App.sharePrefrences.getStringPref(PreferencesKeys.strUserEmail) != null && App.sharePrefrences.getStringPref(PreferencesKeys.strUserEmail).length() > 1) {
                    String strEmailId = App.sharePrefrences.getStringPref(PreferencesKeys.strUserEmail);
                    tvUseremailBase.setText(strEmailId);
                }
            }

            if(ivUserPhotoBase !=null)
            {
                if (App.sharePrefrences.getStringPref(PreferencesKeys.strUserImage) != null
                        && App.sharePrefrences.getStringPref(PreferencesKeys.strUserImage).length() > 1) {
                    Picasso.with(getApplicationContext())
                            .load(App.strBaseUploadedPicUrl + App.sharePrefrences.getStringPref(PreferencesKeys.strUserImage))
                            .fit().centerCrop()
                            .into(ivUserPhotoBase);

                    ivUserPhotoBase.setBorderWidth(2);
                    //ivUserPhotoBase.setBorderColor(Color.WHITE);
                    ivUserPhotoBase.setBorderColor(Color.GRAY);
                //    ivUserPhotoBase.setShadow("#000000");

                }
            }


            /*if (tvUsernameBase != null && ivUserPhotoBase != null) {
                if (App.sharePrefrences.getStringPref(PreferencesKeys.strUserFullName) != null && App.sharePrefrences.getStringPref(PreferencesKeys.strUserFullName).length() > 1) {
                    String strFullName = App.sharePrefrences.getStringPref(PreferencesKeys.strUserFullName);
                    tvUsernameBase.setText(strFullName);
                }
                if (App.sharePrefrences.getStringPref(PreferencesKeys.strUserImageurl) != null && App.sharePrefrences.getStringPref(PreferencesKeys.strUserImageurl).length() > 1) {
                    String strUserImageurl = App.sharePrefrences.getStringPref(PreferencesKeys.strUserImageurl);
                    Picasso.with(getApplicationContext())
                            .load(strUserImageurl)
                            .placeholder(R.drawable.profile_ph)
                            .fit().centerCrop()
                            .into(ivUserPhotoBase);
                }
            }*/
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }


    private void initDrawer() {
        try {
            left_drawer.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {// TODO Auto-generated method stub
                    // TODO Auto-generated method stub
                    App.showLog("==base menu act==", "===on touch==");

                    if (v instanceof EditText) {
                        App.showLog("==base menu act==", "=touch no hide edittext==2=");
                    } else {
                        App.showLog("==base menu act==", "===on touch hide=2=");
                        //   v.clearFocus();
                        App.hideSoftKeyboardMy(BaseActivity.this);
                    }
                    return true;
                }
            });


            drawer.setDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                }

                @Override
                public void onDrawerOpened(View drawerView) {

                    try {

                        App.showLog("==onDrawerOpened===");

                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }


                }

                @Override
                public void onDrawerClosed(View drawerView) {

                    App.showLog("==onDrawerClosed===");

                    if (drawerView instanceof EditText) {
                        App.showLog("==base menu act==", "=touch no hide edittext==2=");
                    } else {
                        App.showLog("==base menu act==", "===on touch hide=2=");
                        //   v.clearFocus();
                        App.hideSoftKeyboardMy(BaseActivity.this);
                    }
                }

                @Override
                public void onDrawerStateChanged(int newState) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setEnableDrawer(boolean blnEnable) {
        if (blnEnable == true) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }

    }


    public static void overrideFonts(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child);
                }
            } else if (v instanceof TextView) {
            }
        } catch (Exception e) {
        }
    }


    public ArrayList<MenuItems> getBaseMenuListData() {
        ArrayList<MenuItems> arrayListMenuItems = new ArrayList<>();

        try {
            /*if (arrayDBLabels != null && arrayDBLabels.length > 7) {
                arrayListMenuItems.add(new MenuItems(App.setLabelText(arrayDBLabels[1], "dashboard"), R.drawable.dash, 0));
                arrayListMenuItems.add(new MenuItems(App.setLabelText(arrayDBLabels[2], "notification"), R.drawable.notification, 1));
                arrayListMenuItems.add(new MenuItems(App.setLabelText(arrayDBLabels[5], "settings"), R.drawable.settings, 5));
                arrayListMenuItems.add(new MenuItems(App.setLabelText(arrayDBLabels[6], "Logout"), R.drawable.logout, 6));
            } else {
                arrayListMenuItems.add(new MenuItems("dashboard", R.drawable.dash, 0));
                arrayListMenuItems.add(new MenuItems("notifications", R.drawable.notification, 1));
                arrayListMenuItems.add(new MenuItems("settings", R.drawable.settings, 5));
                arrayListMenuItems.add(new MenuItems("Logout", R.drawable.logout, 6));
            }*/

            arrayListMenuItems.add(new MenuItems("Map", R.drawable.user, 0));
            arrayListMenuItems.add(new MenuItems("Support", R.drawable.user, 1));
            arrayListMenuItems.add(new MenuItems("About Us", R.drawable.user, 2));
            arrayListMenuItems.add(new MenuItems("Notifications", R.drawable.user, 3));


        } catch (Exception e) { e.printStackTrace(); }
        return arrayListMenuItems;
    }


    public class MenuItems {
        String strMenuName;
        int intImageDrawable, intMenuId;

        public MenuItems(String name, int id) {
            strMenuName = name;
            intMenuId = id;
        }

        public MenuItems(String name, int image, int id) {
            strMenuName = name;
            intImageDrawable = image;
            intMenuId = id;
        }
    }


    public class BaseMenuAdapter extends BaseAdapter {
        Context mContext;
        ArrayList<MenuItems> mArrayListMenuItems;
        LayoutInflater inflater;

        public BaseMenuAdapter(Context context, ArrayList<MenuItems> arrayListMenuItems) {
            inflater = LayoutInflater.from(context);
            mArrayListMenuItems = arrayListMenuItems;
            mContext = context;
        }

        @Override
        public int getCount() {
            return mArrayListMenuItems.size();
        }

        @Override
        public Object getItem(int i) {
            return mArrayListMenuItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return mArrayListMenuItems.get(i).intMenuId;
        }

        @Override
        public View getView(final int i, View convertView, ViewGroup viewGroup) {
            View rowView = convertView;
            final ViewHolder viewHolder;
            // reuse views
            if (rowView == null) {
                viewHolder = new ViewHolder();
                rowView = inflater.inflate(R.layout.row_menulist, null);
                // configure view holder


                viewHolder.ivMenuIcon = (ImageView) rowView.findViewById(R.id.ivMenuIcon);
                viewHolder.tvMenuItem = (TextView) rowView.findViewById(R.id.tvMenuItem);
                viewHolder.rlMenuItem = (RelativeLayout) rowView.findViewById(R.id.rlMenuItem);

                viewHolder.tvMenuItem.setTypeface(App.getFont_Regular());

                rowView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) rowView.getTag();
            }


            final String menu_selected_id = App.sharePrefrences.getStringPref(PreferencesKeys.strMenuSelectedId);
            if (menu_selected_id != null && menu_selected_id.length() > 0) {
              /*  try {

                    int intMenu = 0;
                    intMenu = Integer.parseInt(menu_selected_id);

                    if (mArrayListMenuItems.get(i).intMenuId == intMenu) {
                        viewHolder.ivMenuIcon.setColorFilter(Color.argb(255, 255, 255, 255)); // White Tint
                        viewHolder.tvMenuItem.setTextColor(0xffffffff);
                    } else {
                        viewHolder.ivMenuIcon.setColorFilter(Color.argb(255, 255, 219, 000)); // Yellow Tint
                        viewHolder.tvMenuItem.setTextColor(0x40ffffff);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }*/

            } else {
                App.sharePrefrences.setPref(PreferencesKeys.strMenuSelectedId, "0");
            }

            viewHolder.ivMenuIcon.setImageResource(mArrayListMenuItems.get(i).intImageDrawable);
            viewHolder.tvMenuItem.setText(mArrayListMenuItems.get(i).strMenuName);

            viewHolder.rlMenuItem.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NewApi")
                @Override
                public void onClick(View view) {

                    //viewHolder.ivMenuIcon.setColorFilter(Color.argb(255, 255, 255, 255)); // White Tint


                    drawer.closeDrawer(GravityCompat.START);
                    App.sharePrefrences.setPref(PreferencesKeys.strMenuSelectedId, "" + mArrayListMenuItems.get(i).intMenuId);

                    if (mArrayListMenuItems.get(i).intMenuId == 0) {
                        if (!menu_selected_id.equalsIgnoreCase("0")) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(BaseActivity.this, ActDashboard.class);
                                    intent.putExtra(AppFlags.tagFrom, "BaseActivity");
                                    startActivity(intent);
                                    animStartActivity();
                                }
                            }, 280);
                        } else {
                            drawer.closeDrawers();
                        }
                    }
                    else if (mArrayListMenuItems.get(i).intMenuId == 1) {
                        if (!menu_selected_id.equalsIgnoreCase("1")) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    Intent intent = new Intent(BaseActivity.this, WebviewContentActivity.class);
                                    intent.putExtra(AppFlags.tagFrom, "BaseActivity");
                                    intent.putExtra(AppFlags.tagTitle, "Support");
                                    startActivity(intent);
                                    animStartActivity();
                                }
                            }, 280);
                        } else {
                            drawer.closeDrawers();
                        }
                    }
                    else if (mArrayListMenuItems.get(i).intMenuId == 4) {
                        if (!menu_selected_id.equalsIgnoreCase("1")) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    Intent intent = new Intent(BaseActivity.this, WebviewContentActivity.class);
                                    //Intent intent = new Intent(BaseActivity.this, NavigationActivity.class);
                                    intent.putExtra(AppFlags.tagFrom, "BaseActivity");
                                    intent.putExtra(AppFlags.tagTitle, "About Us");
                                    startActivity(intent);
                                    animStartActivity();
                                }
                            }, 280);
                        } else {
                            drawer.closeDrawers();
                        }
                    }
                    else if (mArrayListMenuItems.get(i).intMenuId == 5) {
                        if (!menu_selected_id.equalsIgnoreCase("5")) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    //Intent intent = new Intent(BaseActivity.this, WebviewContentActivity.class);
                                    Intent intent = new Intent(BaseActivity.this, ActNotification.class);
                                    intent.putExtra(AppFlags.tagFrom, "BaseActivity");
                                    intent.putExtra(AppFlags.tagTitle, "Notifications");
                                    startActivity(intent);
                                    animStartActivity();
                                }
                            }, 280);
                        } else {
                            drawer.closeDrawers();
                        }
                    }
                     else {
                        App.showLog("=======Menu item not found====");
                    }

                }
            });
            return rowView;
        }

        public class ViewHolder {
            ImageView ivMenuIcon;
            TextView tvMenuItem;
            RelativeLayout rlMenuItem;
        }
    }




/*

    private void openBottomSheetShare() {

        String strTagShareIntent = "Share";
        String strTagFacebook = "Facebook";
        String strTagTwitter = "Twitter";
        String strTagOther = "Other";

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.bottom_sheet_share_intent, null, false);

        TextView header = (TextView) view.findViewById(R.id.header);
        header.setText(strTagShareIntent);

        TextView tvFacebook = (TextView) view.findViewById(R.id.tvFacebook);
        TextView tvTwitter = (TextView) view.findViewById(R.id.tvTwitter);
        TextView tvOther = (TextView) view.findViewById(R.id.tvOther);

        tvFacebook.setText(strTagFacebook);
        tvTwitter.setText(strTagTwitter);
        tvOther.setText(strTagOther);

        header.setTypeface(App.getFont_Regular());
        tvFacebook.setTypeface(App.getFont_Regular());
        tvTwitter.setTypeface(App.getFont_Regular());
        tvOther.setTypeface(App.getFont_Regular());

        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(BaseActivity.this, R.style.BottomSheetDialog);
        mBottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);

        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();

        tvFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();

                Intent i1 = new Intent(BaseActivity.this, ActShareIntent.class);
                i1.putExtra(AppFlags.tagFrom, "Facebook");
                startActivity(i1);
            }
        });

        tvTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
                Intent i1 = new Intent(BaseActivity.this, ActShareIntent.class);
                i1.putExtra(AppFlags.tagFrom, "Twitter");
                startActivity(i1);
            }
        });

        tvOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();

                Intent i1 = new Intent(BaseActivity.this, ActShareIntent.class);
                i1.putExtra(AppFlags.tagFrom, "other");
                startActivity(i1);
            }
        });
    }

*/

    private void setLogout()
    {
        {
            if (App.isInternetAvail(BaseActivity.this)) {
                {
                    final Dialog dialog = new Dialog(BaseActivity.this);
                    // Include dialog.xml file
                    // Include dialog.xml file
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.prograss_bg); //temp removed
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                    dialog.setContentView(R.layout.popup_exit);

                    // set values for custom dialog components - text, image and button
                    // set values for custom dialog components - text, image and button
                    TextView    tvExitMessage = (TextView) dialog.findViewById(R.id.tvMessage);
                    TextView   tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);
                    TextView   tvOK = (TextView) dialog.findViewById(R.id.tvOk);

                    String strAlertMessage = "Are you sure you want to logout ?";

                    String strYes = "yes";

                    String strNo = "no";


                    App.showLog("==al-msg====strAlertMessage===="+strAlertMessage);
                    App.showLog("==al-0=====strYes==="+strYes);
                    App.showLog("==al-1====strNo===="+strNo);

                    tvExitMessage.setText(strAlertMessage);
                    tvCancel.setText(strNo);
                    tvOK.setText(strYes);


                    tvExitMessage.setTypeface(App.getFont_Regular());
                    tvCancel.setTypeface(App.getFont_Regular());
                    tvOK.setTypeface(App.getFont_Regular());

                    dialog.show();

                    tvCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 800);
                        }
                    });

                    tvOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    asyncLogout();
                                }
                            }, 800);

                        }
                    });
                    //asyncLogout(App.sharePrefrences.getStringPref(PreferencesKeys.strUserId));
                }
            } else {
                    App.showSnackBar(tvTitle, getString(R.string.strNetError));

            }
        }
    }


    public void animStartActivity() {
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void animFinishActivity() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onResume() {
        try {

            if (AppFlags.isEditProfileBase == true) {
                App.showLog("==base onResume==isEditProfileBase=");
                AppFlags.isEditProfileBase = false;
                setBaseMenuDrawerHeaderData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();

    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        //	setCloseDrawerMenu(true);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(llContainerMain.getWindowToken(), 0);
        super.onPause();
    }


    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        System.out.println("=====Base onStop====");
        //	setCloseDrawerMenu(true);
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub

        System.out.println("=====Base onDestroy====");
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
        } else {
            try {
                super.onBackPressed();
                animFinishActivity();
            } catch (Exception e) {
                e.printStackTrace();
                App.showLog("==Exception on base back click====");
            }
        }
    }



    //http://serverpath/foldernm/ws.halacab.php?op=Logout&uid=76
    protected void asyncLogout() {
        try {
            Retrofit retrofitApiCall;
            ApiService apiService;
            Call callApiMethod;
            retrofitApiCall = new Retrofit.Builder()
                    .baseUrl(App.strBaseHostUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiService = retrofitApiCall.create(ApiService.class);
            callApiMethod = apiService.setUserLogout(
                    App.OP_LOGOUT,
                    App.sharePrefrences.getStringPref(PreferencesKeys.strUserId),

                    App.APP_PLATFORM,
                    App.APP_MODE,
                    App.APP_USERTYPE,
                    App.sharePrefrences.getStringPref(PreferencesKeys.strAccessToken)
            );

            App.showLogApi("--OP_LOGOUT--" + App.OP_LOGOUT
                    + "&uid=" + App.sharePrefrences.getStringPref(PreferencesKeys.strUserId)

                    + "&platform=" + App.APP_PLATFORM
                    + "&app_mode=" + App.APP_MODE
                    + "&user_type=" + App.APP_USERTYPE
                    + "&accessToken=" + App.sharePrefrences.getStringPref(PreferencesKeys.strAccessToken)
            );
            callApiMethod.enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                    CommonResponse model = response.body();

                    if (model == null) {
                        //404 or the response cannot be converted to User.
                        App.showLog("Test---null response--", "==Something wrong=");
                        ResponseBody responseBody = response.errorBody();
                        if (responseBody != null) {
                            try {
                                App.showLog("Test---error-", "" + responseBody.string());
                                logoutFinishAllActivity();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        //200 sucess
                        try {
                            logoutFinishAllActivity();
                        } catch (Exception e) {
                            App.showLog("=====Logout crash===");
                            e.printStackTrace();

                            logoutFinishAllActivity();
                        }
                    }
                }

                @Override
                public void onFailure(Call<CommonResponse> call, Throwable t) {
                    t.printStackTrace();
                    logoutFinishAllActivity();
                }
            });
        } catch (Exception e) {e.printStackTrace();}
    }


    protected void logoutFinishAllActivity() {
        try {
            App.showLog("=====Logout err===");
            String strDeviceId = App.sharePrefrences.getStringPref(PreferencesKeys.strDeviceId);

            String password = App.sharePrefrences.getStringPref(PreferencesKeys.strUserPassword);

            App.sharePrefrences.clearPrefValues();


            App.sharePrefrences.setPref(PreferencesKeys.strDeviceId, strDeviceId);

            //App.sharePrefrences.setPref(PreferencesKeys.strUserPassword, password);
            App.sharePrefrences.setPref(PreferencesKeys.strLogin, "0"); // isLogin = 0-Logout/1-Login
            App.sharePrefrences.setPref(PreferencesKeys.strSocialLogin, "0"); // isSocialLogin = 0-Logout-Social/1-Login-Social

            NotificationManager notifManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notifManager.cancelAll();

            Intent iv = new Intent(BaseActivity.this, ActLogin.class);
            iv.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            //iv.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            iv.putExtra(AppFlags.tagFrom, "BaseActivity");
            startActivity(iv);
            animStartActivity();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            }
            else
            {
                finish();
            }
            //android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    ////// COMMON EXIT DIALOG FOR ALL ACTIVITY //////
    public void showExitDialog() {

        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
        } else {
            final Dialog dialog = new Dialog(BaseActivity.this);
            // Include dialog.xml file
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
            dialog.setContentView(R.layout.popup_exit);

            // set values for custom dialog components - text, image and button
            TextView   tvExitMessage = (TextView) dialog.findViewById(R.id.tvMessage);
            TextView    tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);
            TextView    tvOK = (TextView) dialog.findViewById(R.id.tvOk);

            String strAlertMessageExit = "Are you sure you want to exit ?";

            String strYes = "YES";

            String strNo = "NO";

            App.showLog("==al-msg====strAlertMessageExit===="+strAlertMessageExit);
            App.showLog("==al-0=====strYes==="+strYes);
            App.showLog("==al-1====strNo===="+strNo);

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
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    }, 800);
                }
            });

            tvOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            App.myFinishActivity(BaseActivity.this);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                finishAffinity();
                            }
                            else
                            {
                                finish();
                            }
                            onBackPressed();
                            return;
                        }
                    }, 800);

                }
            });
        }
    }
}
