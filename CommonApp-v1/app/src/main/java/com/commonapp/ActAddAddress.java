package com.commonapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.api.model.AddressListModel;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.utils.AppFlags;
import com.utils.StaticDataList;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by prashant.patel on 9/20/2017.
 */

public class ActAddAddress extends BaseActivity {
    @BindView(R.id.tvName)
    TextView tvName;

    @BindView(R.id.llBottom)
    LinearLayout llBottom;

    @BindView(R.id.etContactNo)
    MaterialEditText etContactNo;

    @BindView(R.id.tvVerify)
    TextView tvVerify;

    @BindView(R.id.tvAddMoreAddress)
    TextView tvAddMoreAddress;


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.fabNext)
    FloatingActionButton fabNext;

    int REQ_CODE_SELECT_ADDRESS= 1200;


    DataListAdapter dataListAdapter;
    private ArrayList<AddressListModel> arrayListAllAddressListModel = new ArrayList<>();

    String strName = "Michael";

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            App.showLog(TAG);
            ViewGroup.inflate(this, R.layout.act_add_address, llContainerSub);
            ButterKnife.bind(this);
            initialization();
            setClickEvents();

            setListData();

            // for the full screen view
            App.getInstance().trackScreenView(getString(R.string.scrn_ActAddAddress));

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



            tvName.setText(Html.fromHtml("Hi <b>"+strName ));

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ActAddAddress.this);
            recyclerView.setLayoutManager(linearLayoutManager);
            //recyclerView.setHasFixedSize(true);
            recyclerView.getLayoutManager().setAutoMeasureEnabled(true);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setHasFixedSize(false);


            etContactNo.setTypeface(App.getFont_Regular());
            tvAddMoreAddress.setTypeface(App.getFont_Regular());

            tvVerify.setTypeface(App.getFont_Regular());
            tvName.setTypeface(App.getFont_Regular());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setClickEvents() {
        try {
            // Register Label Click event
            fabNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intActSignup = new Intent(ActAddAddress.this, ActSignup.class);
                    startActivity(intActSignup);
                    overridePendingTransition(0, 0);
                }
            });

            tvVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    App.showSnackBar(tvTitle, getString(R.string.strComingSoon));
                }
            });


            tvAddMoreAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent=new Intent(ActAddAddress.this,ActSelectAddress.class);
                    startActivityForResult(intent, REQ_CODE_SELECT_ADDRESS);// Activity is started with requestCode 2

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Call Back method  to get the Message form other Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        App.showLog("=======onActivityResult=====");

        // check if the request code is same as what is passed  here it is 2
        if(resultCode == Activity.RESULT_OK) {
            App.showLog("=====result==OK==requestCode=="+requestCode);

            if (requestCode == REQ_CODE_SELECT_ADDRESS)
            {
                App.showLog("=====result==OK===="+REQ_CODE_SELECT_ADDRESS);

                if(data !=null) {
                    Bundle bundle = data.getExtras();
                    String from = data.getStringExtra(AppFlags.tagFrom);
                    App.showLog("==from==="+from);

                    AddressListModel addressListModel = (AddressListModel) bundle.getSerializable(AppFlags.model_AddressListModel);
                    arrayListAllAddressListModel.add(addressListModel);
                    dataListAdapter.notifyDataSetChanged();

                    recyclerView.smoothScrollToPosition(dataListAdapter.getItemCount()+1);
                }
                else
                {
                    App.showLog("===act - data - null===");
                }



            }
        }
        else
        {
            App.showLog("=====result==cancel====");
            App.showLog("=====result==cancel==requestCode=="+requestCode);
        }
    }




    private void setListData(){
        try
        {
            arrayListAllAddressListModel = StaticDataList.getStaticData();
            dataListAdapter = new DataListAdapter(ActAddAddress.this, arrayListAllAddressListModel);
            recyclerView.setAdapter(dataListAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }





    public class DataListAdapter extends RecyclerView.Adapter<DataListAdapter.VersionViewHolder> {
        ArrayList<AddressListModel> mArrListAddressListModel;
        Context mContext;


        public DataListAdapter(Context context, ArrayList<AddressListModel> arrayListFollowers) {
            mArrListAddressListModel = arrayListFollowers;
            mContext = context;
        }

        @Override
        public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_add_address_list, viewGroup, false);
            VersionViewHolder viewHolder = new VersionViewHolder(view);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(final VersionViewHolder versionViewHolder, final int i) {
            try {
                AddressListModel addressListModel = mArrListAddressListModel.get(i);

                versionViewHolder.tvTitle.setText(addressListModel.addr_title);
                versionViewHolder.tvDetail.setText(addressListModel.addr_detail);

                versionViewHolder.cardItemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            
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
            return mArrListAddressListModel.size();
        }


        public void removeItem(int position) {

            if (App.isInternetAvail(ActAddAddress.this)) {
                if (mArrListAddressListModel.get(position).addr_id != null) {
                    // 1- App.OP_NOTI_READ
                    // 2- App.OP_NOTI_DELETE
                   // asyncReadDeleteNotification(App.OP_NOTI_DELETE, mArrListAddressListModel.get(position).addr_id);
                }
                mArrListAddressListModel.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mArrListAddressListModel.size());

            } else {
                App.showSnackBar(tvTitle, getString(R.string.strNetError));
            }


        }


        class VersionViewHolder extends RecyclerView.ViewHolder {
            CardView cardItemLayout;
            TextView tvTitle, tvDetail;
            View vDividerLine;

            public VersionViewHolder(View itemView) {
                super(itemView);

                cardItemLayout = (CardView) itemView.findViewById(R.id.cardlist_item);
                tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
                tvDetail = (TextView) itemView.findViewById(R.id.tvDetail);
                vDividerLine = itemView.findViewById(R.id.vDividerLine);

                tvDetail.setTypeface(App.getFont_Regular());
                tvTitle.setTypeface(App.getFont_Regular());

            }

        }
    }









    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}
