package com.viga.profesoresitq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class Activity_Publicidad extends Activity{
	
	Button b_cerrar;
	ImageView view;
	private String statusRespuesta;
	private String img_url;
	private URLs urls = new URLs();
	private String sitio_url;
	private boolean con_sitio= true;
	public String URL = urls.status_publicidad2;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_publicidad);
		
		view = (ImageView) findViewById(R.id.imageViewPublicidad);
		view.setVisibility(View.INVISIBLE);
		b_cerrar = (Button) findViewById(R.id.buttonPublicidad);
		b_cerrar.setVisibility(View.INVISIBLE);
		
		b_cerrar.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				cerrarVentana();
			}
		});
		
		new PublicidadStatus(getBaseContext(), URL).execute();
		
	}
	
	private void cerrarVentana(){
		
		finish();
		Intent intent = new Intent(Activity_Publicidad.this,
				Activity_Principal.class);
		startActivity(intent);
		
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
						
						img_url = respuesta[1];
						if (respuesta[2].equalsIgnoreCase("ninguno")) {
						con_sitio = false;
						}else{
						sitio_url = respuesta[2];
						}
						putImg();
						
					} else {

						cerrarVentana();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {

				cerrarVentana();
			}
		}
	}
	
	public void putImg(){
		
		view.setVisibility(View.VISIBLE);
        ImageLoader imgLoader = new ImageLoader(getBaseContext());
        imgLoader.DisplayImage3(img_url, view);
        b_cerrar.setVisibility(View.VISIBLE);
        
        if(con_sitio){
    		view.setOnClickListener(new View.OnClickListener(){
    		    public void onClick(View v){
    		        Intent intent = new Intent();
    		        intent.setAction(Intent.ACTION_VIEW);
    		        intent.addCategory(Intent.CATEGORY_BROWSABLE);
    		        intent.setData(Uri.parse(sitio_url));
    		        startActivity(intent);
    		    }
    		});
        }
	}
	
	
}
