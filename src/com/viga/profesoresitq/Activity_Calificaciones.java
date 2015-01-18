package com.viga.profesoresitq;

import java.util.ArrayList;

import android.app.Activity;
//import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
//import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
//import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_Calificaciones extends Activity implements
		OnClickListener {

	TextView nombre;
	Button bt_calificar;
	ProgressBar progreso;

	public ArrayList<Comentario> array_Comentarios = new ArrayList<Comentario>();
	private Comentario_Adapter adapter;

	private URLs urls = new URLs();
	public String URL = urls.Lista_calificaciones;

	public String ID;
	public String status;
	ListView lista;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calificaciones);

		nombre = (TextView) findViewById(R.id.calificaciones_nombre_maestro);
		progreso = (ProgressBar) findViewById(R.id.progressBar);

		Bundle extras = this.getIntent().getExtras();
		nombre.setText(extras.getString("nombre"));
		ID = extras.getString("ID");
		URL = URL + extras.getString("ID");
		status = extras.getString("status");

		if (extras.getString("status").equalsIgnoreCase("exito")) {

			customToast_ok();

		} else if (extras.getString("status").equalsIgnoreCase("fallo")) {

			customToast_fallo();
		}

		initilize();

		rellenarComentarios();

	}

	private void inicializarListView() {
		lista = (ListView) findViewById(R.id.calificaciones_listview);
		adapter = new Comentario_Adapter(this, array_Comentarios);
		lista.setAdapter(adapter);
		bt_calificar.setVisibility(View.VISIBLE);
		progreso.setVisibility(View.INVISIBLE);
	}

	private void rellenarComentarios() {
		if (isOnline()) {
			new DescargarCalificaciones(getBaseContext(), URL).execute();
		} else {
			Toast.makeText(getBaseContext(), "Sin acceso a Internet",
					Toast.LENGTH_LONG).show();
		}

	}

	private class DescargarCalificaciones extends
			AsyncTask<String, Void, Boolean> {

		private String feedUrl;
		private Context ctx;

		// ProgressDialog pDialog;

		public DescargarCalificaciones(Context c, String url) {
			this.feedUrl = url;
			this.ctx = c;
		}

		protected void onPreExecute() {
			super.onPreExecute();

			// pDialog = new ProgressDialog(Activity_Calificaciones.this);
			// pDialog.setMessage("Cargando Calificaciones, espere." );
			// pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			// pDialog.setCancelable(true);

			// if (status.equalsIgnoreCase("exito") ||
			// status.equalsIgnoreCase("fallo") ){

			// }else{

			// pDialog.show();

			// }
		}

		@Override
		protected Boolean doInBackground(final String... args) {
			XMLparserComentario parser = new XMLparserComentario(feedUrl,
					getBaseContext());
			array_Comentarios = parser.parse();
			return true;
		}

		@Override
		protected void onPostExecute(Boolean success) {
			if (success) {
				try {

					// pDialog.dismiss();

					inicializarListView();
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				Toast.makeText(ctx, "Error en la lectura", Toast.LENGTH_LONG)
						.show();
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_addcalificacion:
			Intent newActivity = new Intent(Activity_Calificaciones.this,
					Activity_Calificar.class);
			newActivity.putExtra("nombre", nombre.getText());
			newActivity.putExtra("ID", ID);
			startActivity(newActivity);
			break;
		}
	}

	private void initilize() {
		bt_calificar = (Button) findViewById(R.id.bt_addcalificacion);
		bt_calificar.setOnClickListener(this);
		bt_calificar.setVisibility(View.INVISIBLE);
	}

	public void customToast_ok() {

		Context context = getApplicationContext();
		CharSequence text = "Calificación Agregada";
		int duration = Toast.LENGTH_LONG;

		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.custom_toast_ok,
				(ViewGroup) findViewById(R.id.toast_layout_ok));

		TextView textToast = (TextView) layout.findViewById(R.id.text_toast);
		textToast.setText(text);

		Toast toast = new Toast(context);
		toast.setDuration(duration);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setView(layout);
		toast.show();

	}

	public void customToast_fallo() {

		Context context = getApplicationContext();
		CharSequence text = "Ocurrio un Problema";
		int duration = Toast.LENGTH_LONG;

		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.custom_toast_fallo,
				(ViewGroup) findViewById(R.id.toast_layout_fallo));

		TextView textToast = (TextView) layout.findViewById(R.id.text_toast);
		textToast.setText(text);

		Toast toast = new Toast(context);
		toast.setDuration(duration);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setView(layout);
		toast.show();
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

}