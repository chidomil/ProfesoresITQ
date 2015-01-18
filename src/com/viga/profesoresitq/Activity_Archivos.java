package com.viga.profesoresitq;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Activity_Archivos extends Activity implements OnClickListener {

	public ArrayList<Archivo> array_Archivos = new ArrayList<Archivo>();
	private Archivo_Adapter adapter;

	public String ID;
	public String status;
	ListView lista;
	ListView aux;

	TextView nombre;
	ProgressBar progreso;
	Button bt_addarchivo;

	private URLs urls = new URLs();
	public String URL = urls.Lista_archivos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_archivos);

		aux = (ListView) findViewById(R.id.archivos_listview);
		nombre = (TextView) findViewById(R.id.archivos_nombre_maestro);
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

		rellenarArchivos();

		aux.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Archivo item;
				item = array_Archivos.get(position);

				Intent newActivity = new Intent(Activity_Archivos.this,
						Activity_ZoomArchivo.class);
				newActivity.putExtra("url", item.getImagen());
				startActivity(newActivity);

			}
		});

	}

	private void inicializarListView() {
		lista = (ListView) findViewById(R.id.archivos_listview);
		adapter = new Archivo_Adapter(this, array_Archivos);
		lista.setAdapter(adapter);
		progreso.setVisibility(View.INVISIBLE);
		bt_addarchivo.setVisibility(View.VISIBLE);

	}

	private void rellenarArchivos() {
		if (isOnline()) {
			new DescargarArchivos(getBaseContext(), URL).execute();
		} else {
			Toast.makeText(getBaseContext(), "Sin acceso a Internet",
					Toast.LENGTH_LONG).show();
		}

	}

	private class DescargarArchivos extends AsyncTask<String, Void, Boolean> {

		private String feedUrl;
		private Context ctx;

		// ProgressDialog pDialog;

		public DescargarArchivos(Context c, String url) {
			this.feedUrl = url;
			this.ctx = c;
		}

		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Boolean doInBackground(final String... args) {
			XMLparserArchivo parser = new XMLparserArchivo(feedUrl,
					getBaseContext());
			array_Archivos = parser.parse();
			return true;
		}

		@Override
		protected void onPostExecute(Boolean success) {
			if (success) {
				try {

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
		case R.id.bt_addarchivo:
			finish();
			Intent newActivity = new Intent(Activity_Archivos.this,
					Activity_NuevoArchivo.class);
			newActivity.putExtra("nombre", nombre.getText());
			newActivity.putExtra("ID", ID);
			startActivity(newActivity);
			break;
		}
	}

	private void initilize() {

		bt_addarchivo = (Button) findViewById(R.id.bt_addarchivo);
		bt_addarchivo.setOnClickListener(this);
		bt_addarchivo.setVisibility(View.INVISIBLE);
	}

	public void customToast_ok() {

		Context context = getApplicationContext();
		CharSequence text = "Archivo Agregado";
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

	/*
	 * @Override protected void onPause() { super.onPause(); finish(); }
	 */

}