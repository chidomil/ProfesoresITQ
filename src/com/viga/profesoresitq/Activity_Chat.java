package com.viga.profesoresitq;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
//import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
 
@SuppressLint("SetJavaScriptEnabled")
public class Activity_Chat extends Activity {
 
	private URLs urls = new URLs();
	private String URL = urls.ruta_chat;
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_chat);

 

        WebView browser = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = browser.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false); 
        webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36");
        //browser.setWebChromeClient(new WebChromeClient());
        browser.loadUrl(URL);
        
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_principal, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.chat:
			
			return true;
			
		case R.id.inicio:
			finish();
			startActivity(new Intent(this, Activity_Principal.class));
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
    

 
}