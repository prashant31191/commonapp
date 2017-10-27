package com.commonapp;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.utils.AppFlags;
import com.utils.CustomProgressDialog;

@SuppressLint("SetJavaScriptEnabled")
public class ActWebUrls extends BaseActivity {
	/** Called when the activity is first created. */

	WebView web;
	CustomProgressDialog progressBar;

	String from="",strUrl="",strTag="";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//    setContentView(R.layout.act_webview_urls);

		ViewGroup.inflate(this, R.layout.act_webview_urls,llContainerSub);

		try {

			rlBack.setVisibility(View.VISIBLE);
			setEnableDrawer(false);
			tvTitle.setText("About us");
			
			rlBack.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					App.myFinishActivity(ActWebUrls.this);

					/*if(from !=null && from.equalsIgnoreCase("ActSignUp"))
					{
						app.myFinishActivity(ActWebUrls.this);
					}
					else
					{
					
				//	Intent intentSignin = new Intent(ActWebUrls.this,ViewMyProfile.class); //final - CreateSongActivity
				//	startActivity(intentSignin);
					app.myFinishActivity(ActWebUrls.this);
					}*/
				}
			});


			System.out.println("===start ActWebUrls.java==");


			web = (WebView) findViewById(R.id.webview01);
			web.setBackgroundColor(Color.parseColor("#ffffff"));
			
			progressBar = new CustomProgressDialog(ActWebUrls.this);
/*---
			web.setWebViewClient(new myWebClient());
			web.getSettings().setJavaScriptEnabled(true);
*/
			from = getIntent().getStringExtra(AppFlags.tagFrom);
			strTag = getIntent().getStringExtra(AppFlags.tagTitle);
			
			/*EasyTracker	easyTracker = EasyTracker.getInstance(getApplicationContext());
			easyTracker.send(MapBuilder.createAppView().set(Fields.SCREEN_NAME, "URL - "+from ).build());
			//from - ActSetting  tag-screenName  url-Load any url
			*/
			
			String ua = "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0";
			web.getSettings().setUserAgentString(ua);//"Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/20 Safari/537.31");
			web.getSettings().setJavaScriptEnabled(true);
			web.getSettings().setUseWideViewPort(true);
			web.getSettings().setLoadWithOverviewMode(true);
		//	web.getSettings().setUserAgent("1");//for desktop 1 or mobil 0. 
			
			web.getSettings().setUserAgentString("my-user-agent");
			web.getSettings().setSupportZoom(true);
			web.getSettings().setBuiltInZoomControls(true);


			web.getSettings().setAppCacheMaxSize( 10 * 1024 * 1024 ); // 10MB
			if(from.equalsIgnoreCase("BaseActivity") || strUrl !=null)
			{
				strUrl = getIntent().getStringExtra(AppFlags.tagUrl);
				System.out.println("=====You url is==="+strUrl);

				if(strTag != null && strTag != "" && strTag.length() > 0)
					tvTitle.setText(strTag);

				web.loadUrl(strUrl);        	
			}
			else
			{
				web.loadUrl("http://www.google.com");
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public class myWebClient extends WebViewClient
	{
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			progressBar.show();
			view.loadUrl(url);
			return true;

		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);

			progressBar.dismiss();
		}
	}

	// To handle "Back" key press event for WebView to go back to previous screen.
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{

		if ((keyCode == KeyEvent.KEYCODE_BACK) && web.canGoBack()) {
			web.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() 
	{
		System.out.println("==back press==");
		progressBar.setCancelable(true);
		progressBar.cancel();
		if (web.canGoBack()) 
		{
			web.goBack();    
		}
		else
		{
			App.myFinishActivity(ActWebUrls.this);

			/*if(from !=null && from.equalsIgnoreCase("ActSignUp"))
			{
				app.myFinishActivity(ActWebUrls.this);
			}
			else
			{

			//Intent intentSignin = new Intent(ActWebUrls.this,ViewMyProfile.class); //final - CreateSongActivity
			//startActivity(intentSignin);
			app.myFinishActivity(ActWebUrls.this);
			}
			*/
		}

	}

	/*@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}*/
}
