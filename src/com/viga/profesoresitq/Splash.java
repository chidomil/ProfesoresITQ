package com.viga.profesoresitq;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.InterstitialAd;

public class Splash extends Activity implements AdListener {

	private InterstitialAd interstitialAds = null;
	private String statusRespuesta;
	private URLs urls = new URLs();
	public String URL = urls.status_publicidad;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		this.interstitialAds = new InterstitialAd(this, "ca-app-pub-5832816311152771/7624227647");
		this.interstitialAds.setAdListener(this);
		
	
		lanzarThread();
	}

	private void lanzarThread() {
		Thread timer = new Thread() {

			public void run() {
				if (isOnline()) {
					try {
						sleep(400);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						new PublicidadStatus(getBaseContext(), URL).execute();
					}

				} else {
					try {
						sleep(1500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						finish();
						Intent intent = new Intent(Splash.this,
								Activity_Principal.class);
						startActivity(intent);
					}
				}

			}
		};

		timer.start();
	}

	private class PublicidadStatus extends AsyncTask<String, Void, Boolean> {

		private String feedUrl;

		public PublicidadStatus(Context c, String url) {
			this.feedUrl = url;
		}

		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(final String... args) {
			XMLparserStandar parser = new XMLparserStandar(feedUrl,
					getBaseContext());
			statusRespuesta = parser.parse();

			return true;
		}

		@Override
		protected void onPostExecute(Boolean success) {
			if (success) {
				try {

					String[] respuesta = statusRespuesta.split("#");

					if (respuesta[0].equalsIgnoreCase("exito")) {
						if (respuesta[1].equalsIgnoreCase("0")) {

							finish();

							Intent newActivity = new Intent(Splash.this,
									Activity_Principal.class);
							startActivity(newActivity);

						} else if (respuesta[1].equalsIgnoreCase("1")) {

							AdRequest adr = new AdRequest();
							interstitialAds.loadAd(adr);

						}else if (respuesta[1].equalsIgnoreCase("2")) {

							finish();

							Intent newActivity = new Intent(Splash.this,
									Activity_Publicidad.class);
							startActivity(newActivity);

						}
						
					} else {

						finish();

						Intent newActivity = new Intent(Splash.this,
								Activity_Principal.class);
						startActivity(newActivity);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {

				finish();

				Intent newActivity = new Intent(Splash.this,
						Activity_Principal.class);
				startActivity(newActivity);
			}
		}
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getApplication()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		}
		return false;
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

	@Override
	public void onDismissScreen(Ad arg0) {
		// TODO Auto-generated method stub
		finish();
		
		Intent newActivity = new Intent(Splash.this,
				Activity_Principal.class);
		startActivity(newActivity);
	}

	@Override
	public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
		// TODO Auto-generated method stub
		finish();

		Intent newActivity = new Intent(Splash.this,
				Activity_Principal.class);
		startActivity(newActivity);
	}

	@Override
	public void onLeaveApplication(Ad arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPresentScreen(Ad arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReceiveAd(Ad arg0) {
		// TODO Auto-generated method stub
		if (interstitialAds.isReady()) {
			interstitialAds.show();
		} else {
			finish();

			Intent newActivity = new Intent(Splash.this,
					Activity_Principal.class);
			startActivity(newActivity);
		}
	}

}