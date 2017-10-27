package com.commonapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.api.ApiService;
import com.api.model.NotificationListModel;
import com.api.response.CommonResponse;
import com.api.response.NotificationListResponse;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.utils.AppFlags;
import com.utils.CircularImageView;
import com.utils.CustomProgressDialog;
import com.utils.PreferencesKeys;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.text.format.DateUtils.getRelativeTimeSpanString;

public class ActNotification extends BaseActivity {

    String TAG = "=ActNotification=";

    RecyclerView recyclerView;
    MaterialRefreshLayout materialRefreshLayout;
    NotificationAdapter notificationAdapter;
    TextView tvNodataTag;
    LinearLayout llNodataTag;


    Retrofit retrofitApiCall;
    ApiService apiService;
    Call callApiMethod;
    CustomProgressDialog customProgressDialog;

    String strFrom = "", strTitle = "Notifications";
    int page = 0;
    String strTotalResult = "0";
    public ArrayList<NotificationListModel> arrayListAllNotificationListModel;

    private Paint p = new Paint();


    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup.inflate(this, R.layout.act_notification, llContainerSub);

        try {

            getIntentData();
            initialization();
            setApiData();
            setClickEvent();

            App.sharePrefrences.setPref(PreferencesKeys.strMenuSelectedId, "5");

            if (App.isInternetAvail(ActNotification.this)) {

                page = 0;
                arrayListAllNotificationListModel = new ArrayList<>();
                //111 open api call for listing
                asyncGetNotificationList();

            } else {
                App.showSnackBar(tvTitle, getString(R.string.strNetError));
            }

            tvNodataTag.setTypeface(App.getFont_Regular());
            App.getInstance().trackScreenView(getString(R.string.scrn_ActNotification));
        } catch (Exception e) {
            // TODO: handle exceptione.
            e.printStackTrace();
        }
    }

    private void initialization() {
        try {
            rlMenu.setVisibility(View.VISIBLE);
            tvTitle.setText(strTitle);

            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            tvNodataTag = (TextView) findViewById(R.id.tvNodataTag);
            llNodataTag = (LinearLayout) findViewById(R.id.llNodataTag);
            materialRefreshLayout = (MaterialRefreshLayout) findViewById(R.id.refresh);
            materialRefreshLayout.setIsOverLay(true);
            materialRefreshLayout.setWaveShow(true);
            materialRefreshLayout.setWaveColor(0x55ffffff);

            //tvNodataTag.setVisibility(View.GONE);
            llNodataTag.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            materialRefreshLayout.setLoadMore(true);


            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ActNotification.this);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setHasFixedSize(true);


            initSwipe();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {
                    notificationAdapter.removeItem(position);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX < 0) {


                        /*p.setColor(Color.RED);
                        c.drawRect(background,p);*/

                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        p.setColor(Color.GRAY);
                        p.setTextSize(35);
                        c.drawText("will be removed", background.centerX(), background.centerY(), p);
                        //versionViewHolder.tvName.setTypeface(App.getFont_Regular());

                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    private void setApiData() {
        try {
            retrofitApiCall = new Retrofit.Builder()
                    .baseUrl(App.strBaseHostUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofitApiCall.create(ApiService.class);

            customProgressDialog = new CustomProgressDialog(ActNotification.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getIntentData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString(AppFlags.tagTitle) != null && bundle.getString(AppFlags.tagTitle).length() > 0) {
                strTitle = bundle.getString(AppFlags.tagTitle);
            }
            if (bundle.getString(AppFlags.tagFrom) != null && bundle.getString(AppFlags.tagFrom).length() > 0) {
                strFrom = bundle.getString(AppFlags.tagFrom);
            }
        }

    }


    private void setClickEvent() {
        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                //refreshing...
                if (App.isInternetAvail(ActNotification.this)) {
                    //asyncGetNotificationList();

                    page = 0;
                    arrayListAllNotificationListModel = new ArrayList<>();

                    asyncGetNotificationList();

                } else {
                    App.showSnackBar(tvTitle, getString(R.string.strNetError));
                }
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                try {


                    if (App.isInternetAvail(ActNotification.this)) {

                        if (arrayListAllNotificationListModel != null && strTotalResult.equalsIgnoreCase("" + arrayListAllNotificationListModel.size())) {
                            if (arrayListAllNotificationListModel.size() >= 20) {
                                App.showSnackBar(rlMenu, "No more notification found.");
                            }
                            materialRefreshLayout.finishRefresh();
                            // load more refresh complete
                            materialRefreshLayout.finishRefreshLoadMore();
                        } else {
                            page = page + 1;
                            asyncGetNotificationList();
                        }
                    } else {
                        App.showSnackBar(tvTitle, getString(R.string.strNetError));

                        materialRefreshLayout.finishRefresh();
                        // load more refresh complete
                        materialRefreshLayout.finishRefreshLoadMore();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    //http://serverpath/foldernm/ws.halacab.php?op=noti_list&uid=116&user_type=0
    public void asyncGetNotificationList() {

        try {
            customProgressDialog.show();
            callApiMethod = apiService.getNotificationList(
                    App.OP_NOTI_LIST,
                    App.sharePrefrences.getStringPref(PreferencesKeys.strUserId),
                    "" + page,

                    App.APP_PLATFORM,
                    App.APP_MODE,
                    App.APP_USERTYPE,
                    App.sharePrefrences.getStringPref(PreferencesKeys.strAccessToken)
            );

            App.showLogApi("OP_NOTI_LIST--" + App.OP_NOTI_LIST
                    + "&uid=" + App.sharePrefrences.getStringPref(PreferencesKeys.strUserId)
                    + "&page=" + page

                    + "&platform=" + App.APP_PLATFORM
                    + "&app_mode=" + App.APP_MODE
                    + "&user_type=" + App.APP_USERTYPE
                    + "&accessToken=" + App.sharePrefrences.getStringPref(PreferencesKeys.strAccessToken)
            );

            callApiMethod.enqueue(new Callback<NotificationListResponse>() {
                @Override
                public void onResponse(Call<NotificationListResponse> call, Response<NotificationListResponse> response) {
                    try {
                        customProgressDialog.dismiss();
                        // refresh complete
                        materialRefreshLayout.finishRefresh();
                        // load more refresh complete
                        materialRefreshLayout.finishRefreshLoadMore();


                        NotificationListResponse model = response.body();
                        if (model == null) {
                            //404 or the response cannot be converted to User.
                            App.showLog("---null response--", "==Something wrong=");
                            ResponseBody responseBody = response.errorBody();
                            if (responseBody != null) {
                                try {
                                    App.showLog("---error-", "" + responseBody.string());
                                    App.showSnackBar(tvTitle, getString(R.string.strSomethingWentwrong));

                                    //tv.setText("responseBody = "+responseBody.string());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            //200 sucess
                            App.showLog("===Response== " + response.body().toString());
                            App.showLog("==**==Success==**==asyncGetNotificationList==> ", new Gson().toJson(response.body()));

                            if (model.arrayListNotificationListModel != null && model.arrayListNotificationListModel.size() > 0) {
                                arrayListAllNotificationListModel.addAll(model.arrayListNotificationListModel);


                                if (page == 0) {
                                    notificationAdapter = new NotificationAdapter(ActNotification.this, arrayListAllNotificationListModel);
                                    recyclerView.setAdapter(notificationAdapter);
                                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                                    recyclerView.setVisibility(View.VISIBLE);
                                    //tvNodataTag.setVisibility(View.GONE);
                                    llNodataTag.setVisibility(View.GONE);
                                } else {
                                    if (notificationAdapter != null) {
                                        notificationAdapter.notifyDataSetChanged();
                                    }
                                }

                            } else {
                                App.showLog("=====model.arrayListNotificationListModel====Null model=");
                            }

                            if (model.t != null) {
                                strTotalResult = model.t;
                            }

                            if (model.status.equalsIgnoreCase("1")) {

                            }/* else if (model.status.equalsIgnoreCase(App.APP_LOGOUTSTATUST)) {
                                asyncLogout();
                            } */ else {
                                if (model.t != null && model.t.equalsIgnoreCase("0")) {
                                    strTotalResult = model.t;
                                    arrayListAllNotificationListModel = new ArrayList<NotificationListModel>();

                                    notificationAdapter = new NotificationAdapter(ActNotification.this, arrayListAllNotificationListModel);
                                    recyclerView.setAdapter(notificationAdapter);
                                    recyclerView.setItemAnimator(new DefaultItemAnimator());

                                    recyclerView.setVisibility(View.GONE);
                                    //tvNodataTag.setVisibility(View.VISIBLE);
                                    llNodataTag.setVisibility(View.VISIBLE);
                                    tvNodataTag.setText("no notifications found.");

                                } else {
                                    if (model.msg != null) {
                                        App.showSnackBar(rlMenu, model.msg);
                                        // tvNodataTag.setText(model.msg);
                                    }

                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        customProgressDialog.dismiss();
                        // refresh complete
                        materialRefreshLayout.finishRefresh();
                        // load more refresh complete
                        materialRefreshLayout.finishRefreshLoadMore();
                    }
                }

                @Override
                public void onFailure(Call<NotificationListResponse> call, Throwable t) {

                    t.printStackTrace();
                    customProgressDialog.dismiss();
                    // refresh complete
                    materialRefreshLayout.finishRefresh();
                    // load more refresh complete
                    materialRefreshLayout.finishRefreshLoadMore();

                    App.showSnackBar(tvTitle, getString(R.string.strSomethingWentwrong));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.VersionViewHolder> {
        ArrayList<NotificationListModel> mArrListNotificationListModel;
        Context mContext;


        public NotificationAdapter(Context context, ArrayList<NotificationListModel> arrayListFollowers) {
            mArrListNotificationListModel = arrayListFollowers;
            mContext = context;
        }

        @Override
        public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_notification, viewGroup, false);
            VersionViewHolder viewHolder = new VersionViewHolder(view);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(final VersionViewHolder versionViewHolder, final int i) {
            try {
                NotificationListModel notificationListModel = mArrListNotificationListModel.get(i);


         /*   if (i == (mArrListNotificationListModel.size() - 1)) {
                versionViewHolder.vDividerLine.setVisibility(View.GONE);
            } else {
                versionViewHolder.vDividerLine.setVisibility(View.VISIBLE);
            }*/

                versionViewHolder.tvName.setTypeface(App.getFont_Regular());
                versionViewHolder.tvDateTime.setTypeface(App.getFont_Regular());

                if (notificationListModel.message != null && notificationListModel.isRead != null && notificationListModel.isRead.equalsIgnoreCase("0")) {
                    versionViewHolder.tvName.setText(Html.fromHtml("<b>" + notificationListModel.message + "</b>"));
                    versionViewHolder.tvName.setTextColor(Color.parseColor("#111111"));
                    int color = versionViewHolder.cardItemLayout.getContext().getResources().getColor(R.color.clrCardbgUnRead);
                    versionViewHolder.cardItemLayout.setCardBackgroundColor(color);
                } else {
                    versionViewHolder.tvName.setText(Html.fromHtml("<b>" + notificationListModel.message + "</b>"));
                    versionViewHolder.tvName.setTextColor(Color.parseColor("#111111"));

                    int color = versionViewHolder.cardItemLayout.getContext().getResources().getColor(R.color.clrCardbgRead);
                    versionViewHolder.cardItemLayout.setCardBackgroundColor(color);
                    versionViewHolder.rlMain.setAlpha(0.6f);
                }

                if (notificationListModel.createddate != null && notificationListModel.createddate.length() > 1) {
                    //111--versionViewHolder.tvDateTime.setText(App.getddMMMyy(notificationListModel.createddate));
                    //versionViewHolder.tvDateTime.setText(notificationListModel.total_time+" mins ago");


// it comes out like this 2013-08-31 15:55:22 so adjust the date format
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = df.parse(notificationListModel.createddate); //createdDate: "2017-05-20 19:12:15",

                    long epoch = date.getTime();

                    String timePassedString = (String) getRelativeTimeSpanString(epoch, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

                    versionViewHolder.tvDateTime.setText(timePassedString);
                }

                if (notificationListModel.profilepic != null && notificationListModel.profilepic.length() > 1) {
                    Picasso.with(getApplicationContext())
                            .load(App.strBaseUploadedPicUrl + notificationListModel.profilepic)
                            .fit().centerCrop()
                            .into(versionViewHolder.ivUserPhoto);
                }


                versionViewHolder.cardItemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            mArrListNotificationListModel.get(i).isRead = "1";
                            notificationAdapter.notifyDataSetChanged();

                            if (App.isInternetAvail(ActNotification.this)) {
                                if (mArrListNotificationListModel.get(i).noti_id != null) {
                                    // 1- App.OP_NOTI_READ
                                    // 2- App.OP_NOTI_DELETE
                                    asyncReadDeleteNotification(App.OP_NOTI_READ, mArrListNotificationListModel.get(i).noti_id);
                                }
                            }


                        /*    if(mArrListNotificationListModel.get(i).rid !=null)
                            {
                                Intent intent = new Intent(ActNotification.this, ActRouteDetailNavigation.class);
                                intent.putExtra(AppFlags.tagFrom, "ActNotification");
                                intent.putExtra(App.ITAG_RID, mArrListNotificationListModel.get(i).rid );
                                App.myStartActivity(ActNotification.this,intent);
                            }
*/

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mArrListNotificationListModel.size();
        }


        public void removeItem(int position) {

            if (App.isInternetAvail(ActNotification.this)) {
                if (mArrListNotificationListModel.get(position).noti_id != null) {
                    // 1- App.OP_NOTI_READ
                    // 2- App.OP_NOTI_DELETE
                    asyncReadDeleteNotification(App.OP_NOTI_DELETE, mArrListNotificationListModel.get(position).noti_id);
                }
                mArrListNotificationListModel.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mArrListNotificationListModel.size());

            } else {
                App.showSnackBar(tvTitle, getString(R.string.strNetError));
            }


        }


        class VersionViewHolder extends RecyclerView.ViewHolder {
            CardView cardItemLayout;
            TextView tvName, tvDateTime;
            CircularImageView ivUserPhoto;
            RelativeLayout rlMain;
            View vDividerLine;

            public VersionViewHolder(View itemView) {
                super(itemView);

                cardItemLayout = (CardView) itemView.findViewById(R.id.cardlist_item);
                rlMain = (RelativeLayout) itemView.findViewById(R.id.rlMain);
                tvName = (TextView) itemView.findViewById(R.id.tvName);
                ivUserPhoto = (CircularImageView) itemView.findViewById(R.id.ivUserPhoto);
                tvDateTime = (TextView) itemView.findViewById(R.id.tvDateTime);
                vDividerLine = itemView.findViewById(R.id.vDividerLine);
            }

        }
    }

    public void asyncReadDeleteNotification(String strOP, String strNotiId) {
        try {
            // 1- App.OP_NOTI_READ
            // 2- App.OP_NOTI_DELETE
            callApiMethod = apiService.setReadDeleteNotification(strOP,
                    App.sharePrefrences.getStringPref(PreferencesKeys.strUserId),
                    strNotiId,
                    App.APP_PLATFORM,
                    App.APP_MODE,
                    App.sharePrefrences.getStringPref(PreferencesKeys.strAccessToken)
            );

            App.showLogApi("strOP--op=" + strOP
                    + "&uid=" + App.sharePrefrences.getStringPref(PreferencesKeys.strUserId)
                    + "&noti_id=" + strNotiId

                    + "&platform=" + App.APP_PLATFORM
                    + "&app_mode=" + App.APP_MODE
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
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        //200 sucess
                      /*  App.showLog("===Response== " + response.body().toString());
                        App.showLog("==**==Success==**==asyncReadNotification==> ", new Gson().toJson(response.body()));
*/
                        if (model.status != null && model.status.length() > 0) {
                            if (model.status != null && model.status.length() > 0
                                    && model.status.equalsIgnoreCase("1")) {
                                App.showLog("====NOTI READ API====SUCESSS==");
                            } /*else if (model.status.equalsIgnoreCase(App.APP_LOGOUTSTATUST)) {
                                asyncLogout();
                            }*/
                        }
                    }
                }

                @Override
                public void onFailure(Call<CommonResponse> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        showExitDialog();
        /*super.onBackPressed();
        animFinishActivity();*/
    }

}