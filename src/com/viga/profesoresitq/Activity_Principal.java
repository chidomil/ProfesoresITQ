package com.viga.profesoresitq;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Activity_Principal extends Activity implements OnClickListener {

	public ArrayList<Profesor> array_Profesores = new ArrayList<Profesor>();
	public ArrayList<Profesor> array_sort = new ArrayList<Profesor>();
	private Profesor_Adapter adapter;

	private URLs urls = new URLs();
	private String URL = urls.Lista_maestros;

	ListView lista;
	ListView aux;
	Boolean nuevaLista = false;
	Boolean banderaBusqueda = false;

	EditText inputSearch;
	int textlength = 0;
	Button bt_addProfesor;
	Button bt_ir;
	Button irAcalif;
	Button irAarchi;
	ProgressBar progreso;
	int posicionlist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_principal);

		aux = (ListView) findViewById(R.id.profesores_listview);
		progreso = (ProgressBar) findViewById(R.id.progressBar);

		inputSearch = (EditText) findViewById(R.id.buscador);
		inputSearch.setVisibility(View.INVISIBLE);

		initilize();
		rellenarProfesores();

		aux.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				posicionlist = position;
				initiatePopupWindow();

			}
		});

	}

	private PopupWindow pwindo;

	private void initiatePopupWindow() {
		try {

			LayoutInflater inflater = (LayoutInflater) Activity_Principal.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.screen_popup,
					(ViewGroup) findViewById(R.id.popup_element));
			layout.setAnimation(AnimationUtils.loadAnimation(this,
					R.drawable.popupanim));
			pwindo = new PopupWindow(layout, 280, 170, true);
			pwindo.setBackgroundDrawable(new BitmapDrawable());
			pwindo.showAtLocation(layout, Gravity.CENTER, 0, 30);

			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(
					inputSearch.getWindowToken(), 0);

			irAcalif = (Button) layout.findViewById(R.id.btn_calif_popup);
			irAcalif.setOnClickListener(calificaciones_button_click_listener);

			irAarchi = (Button) layout.findViewById(R.id.btn_archi_popup);
			irAarchi.setOnClickListener(archivos_button_click_listener);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private OnClickListener archivos_button_click_listener = new OnClickListener() {
		public void onClick(View v) {

			Profesor itemP;
			if (nuevaLista) {
				itemP = array_sort.get(posicionlist);
			} else {
				itemP = array_Profesores.get(posicionlist);
			}

			pwindo.dismiss();
			Intent newActivity2 = new Intent(Activity_Principal.this,
					Activity_Archivos.class);
			newActivity2.putExtra("ID", itemP.getId());
			newActivity2.putExtra("nombre", itemP.getNombre());
			newActivity2.putExtra("status", "inactivo");
			startActivity(newActivity2);

		}
	};

	private OnClickListener calificaciones_button_click_listener = new OnClickListener() {
		public void onClick(View v) {

			Profesor itemP;
			if (nuevaLista) {
				itemP = array_sort.get(posicionlist);
			} else {
				itemP = array_Profesores.get(posicionlist);
			}
			pwindo.dismiss();
			Intent newActivity = new Intent(Activity_Principal.this,
					Activity_Calificaciones.class);
			newActivity.putExtra("ID", itemP.getId());
			newActivity.putExtra("nombre", itemP.getNombre());
			newActivity.putExtra("status", "inactivo");
			startActivity(newActivity);

		}
	};

	private void inicializarListView() {
		lista = (ListView) findViewById(R.id.profesores_listview);
		adapter = new Profesor_Adapter(this, array_Profesores);
		lista.setAdapter(adapter);

		inputSearch.setVisibility(View.VISIBLE);
		bt_addProfesor.setVisibility(View.VISIBLE);
		bt_ir.setVisibility(View.VISIBLE);
		progreso.setVisibility(View.INVISIBLE);

	}

	private void inicializarListViewBusqueda() {

		lista = (ListView) findViewById(R.id.profesores_listview);
		adapter = new Profesor_Adapter(this, array_sort);
		lista.setAdapter(adapter);
		banderaBusqueda = false;

	}

	private void rellenarProfesores() {
		if (isOnline()) {
			new DescargarProfesores(getBaseContext(), URL).execute();
		} else {
			Toast.makeText(getBaseContext(), "Sin acceso a Internet",
					Toast.LENGTH_LONG).show();
		}
	}

	private class DescargarProfesores extends AsyncTask<String, Void, Boolean> {

		private String feedUrl;
		private Context ctx;

		public DescargarProfesores(Context c, String url) {
			this.feedUrl = url;
			this.ctx = c;
		}

		@Override
		protected Boolean doInBackground(final String... args) {
			try {
				XMLparserProfesor parser = new XMLparserProfesor(feedUrl,
						getBaseContext());
				array_Profesores = parser.parse();
				return true;
			} catch (Exception e) {
				return false;
			}
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
				Toast.makeText(ctx, "Error en la lectura, Verifica tu versión",
						Toast.LENGTH_LONG).show();
				finalizar();
			}
		}
	}

	private void finalizar() {
		Thread timer = new Thread() {
			public void run() {
				try {
					sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					finish();
				}
			}
		};

		timer.start();
	}

	private class actualizaList extends AsyncTask<String, Void, Boolean> {

		private Context ctx;

		public actualizaList(Context c) {
			this.ctx = c;
		}

		@Override
		protected Boolean doInBackground(final String... args) {
			nuevaLista = true;
			array_sort.clear();
			for (int i = 0; i < array_Profesores.size(); i++) {

				Pattern pat = Pattern.compile(inputSearch.getText().toString()
						.toUpperCase());
				Matcher mat = pat.matcher((String) array_Profesores.get(i)
						.getNombre());
				if (mat.find()) {
					array_sort.add(array_Profesores.get(i));
				}
			}

			return true;
		}

		@Override
		protected void onPostExecute(Boolean success) {
			if (success) {
				try {

					inicializarListViewBusqueda();

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

	private void initilize() {
		bt_addProfesor = (Button) findViewById(R.id.bt_addprofesor);
		bt_addProfesor.setOnClickListener(this);
		bt_addProfesor.setVisibility(View.INVISIBLE);

		bt_ir = (Button) findViewById(R.id.button_buscar);
		bt_ir.setOnClickListener(this);
		bt_ir.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_addprofesor:
			startActivity(new Intent(this, Activity_NuevoMaestro.class));
			break;
		case R.id.button_buscar:

			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(
					inputSearch.getWindowToken(), 0);

			if (banderaBusqueda) {
			} else {
				new actualizaList(getBaseContext()).execute();
				banderaBusqueda = true;
			}

		}
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
			finish();
			startActivity(new Intent(this, Activity_Chat.class));
			return true;
			
		case R.id.inicio:
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
