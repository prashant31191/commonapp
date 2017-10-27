package com.commonapp;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.api.ApiService;
import com.api.model.WebviewContentListModel;
import com.api.response.WebviewContentResponse;
import com.utils.AppFlags;
import com.utils.CustomProgressDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class WebviewContentActivity extends BaseActivity {


    WebView webview;
    Retrofit retrofitApiCall;
    ApiService apiService;
    Call callApiMethod;
    CustomProgressDialog customProgressDialog;
    String strTag="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup.inflate(this, R.layout.webview_content_activity, llContainerSub);

        try {

            rlBack.setVisibility(View.VISIBLE);
            setEnableDrawer(false);
            setApiData();

            strTag = getIntent().getStringExtra(AppFlags.tagTitle);
            tvTitle.setText(strTag);


            rlBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    App.myFinishActivity(WebviewContentActivity.this);
                }
            });
            webview = (WebView) findViewById(R.id.webview);

            WebviewContant();

            if(strTag.equalsIgnoreCase("Support")){
                App.getInstance().trackScreenView(getString(R.string.scrn_WebviewContentActivity_Support));
            }else {
                App.getInstance().trackScreenView(getString(R.string.scrn_WebviewContentActivity_About));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        }

    private void setApiData() {
        retrofitApiCall = new Retrofit.Builder()
                .baseUrl(App.strBaseHostUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofitApiCall.create(ApiService.class);
        customProgressDialog = new CustomProgressDialog(this);
    }
    public void WebviewContant() {
        if (App.isInternetAvail(getApplicationContext())) {
            customProgressDialog.show();
            final Call<WebviewContentResponse> countrylist = apiService.getPageContent("getCMS", App.sharePrefrences.getStringPref("uid"));
            App.showLog("===========?op=List_ContentPage&uid=" + App.sharePrefrences.getStringPref("uid"));
            countrylist.enqueue(new Callback<WebviewContentResponse>() {
                @Override
                public void onResponse(Call<WebviewContentResponse> call, Response<WebviewContentResponse> response) {
                    customProgressDialog.dismiss();
                    final String mimeType = "text/html";
                    final String encoding = "UTF-8";
                    String str = response.body().status;
                    if (str.equals("1")) {
                        if (response.body().arrayListWebviewContentListModel != null && response.body().arrayListWebviewContentListModel.size() > 0) {
                            App.showLog("=======arrayListWebviewContentListModel======" + response.body().arrayListWebviewContentListModel.size());
                            for (WebviewContentListModel webviewContentListModel : response.body().arrayListWebviewContentListModel) {
                                if(webviewContentListModel.content !=null && webviewContentListModel.content.length() > 0)
                                {
                                    if(webviewContentListModel.name !=null && webviewContentListModel.name.equalsIgnoreCase(strTag))
                                    {
                                        if(webviewContentListModel.name !=null && webviewContentListModel.name.length() > 0) {
                                            tvTitle.setText(webviewContentListModel.name);
                                        }
                                        StringBuilder sb = new StringBuilder();
                                        sb.append("<HTML><body>");
                                        sb.append(String.valueOf(Html.fromHtml(webviewContentListModel.content)));
                                        sb.append("</body></HTML>");
                                        webview.loadData(sb.toString(), "text/html; charset=UTF-8", null);
                                    }
                                   /* if(webviewContentListModel.name !=null && webviewContentListModel.name.equalsIgnoreCase(strTag))
                                    {
                                        if(webviewContentListModel.name !=null && webviewContentListModel.name.length() > 0) {
                                            tvTitle.setText(webviewContentListModel.name);
                                        }
                                        StringBuilder sb = new StringBuilder();
                                        sb.append("<HTML><body>");
                                        sb.append(String.valueOf(Html.fromHtml(webviewContentListModel.content)));
                                        sb.append("</body></HTML>");
                                        webview.loadData(sb.toString(), "text/html; charset=UTF-8", null);
                                    }*/
                                }
                            }
                        } else {
                            App.showLog("=======arrayListWebviewContentListModel===null===");
                            App.showSnackBar(rlBack, getString(R.string.strSomethingWentwrong));
                        }
                    }
                    else {
                        App.showSnackBar(rlBack, getString(R.string.strSomethingWentwrong));
                       }
                }
                @Override
                public void onFailure(Call<WebviewContentResponse> call, Throwable t) {
                    customProgressDialog.dismiss();
                    App.showSnackBar(rlBack, getString(R.string.strSomethingWentwrong));
                    t.printStackTrace();
                }
            });
        } else {
            App.showSnackBar(rlMenu, getString(R.string.strNetError));
        }
    }
}
