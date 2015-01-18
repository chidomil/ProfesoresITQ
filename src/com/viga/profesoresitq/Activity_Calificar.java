package com.viga.profesoresitq;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


public class Activity_Calificar extends Activity implements OnClickListener{

	TextView nombre;
	TextView text_recomendado;
	EditText clase;
	EditText comentario;
	CheckBox chb_recomendado;
	RatingBar r_claridad;
	RatingBar r_ayuda;
	RatingBar r_facilidad;
	Button enviar;
	private PopupWindow pwindo;
	
	private URLs urls = new URLs();
	public String URL = urls.agregar_calificacion;
		
	public String statusRespuesta;
	public String ID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calificar);
	
		inicializador();
		
		nombre = (TextView) findViewById(R.id.calificar_nombre_maestro);
		
		Bundle extras = this.getIntent().getExtras();
		nombre.setText(extras.getString("nombre")); 
		ID = extras.getString("ID");
	
	}
	
	private void enviaComentario() {
		if (isOnline()) {
			new DescargarStatus(getBaseContext(), URL).execute();
		}
		else{
			Toast.makeText(getBaseContext(), "Sin acceso a Internet", Toast.LENGTH_LONG)
			.show();
		}
	}
	
	private class DescargarStatus extends AsyncTask<String, Void, Boolean> {

		private String feedUrl;
		private Context ctx;

		public DescargarStatus(Context c, String url) {
			this.feedUrl = url;
			this.ctx = c;
		}
		
		protected void onPreExecute() {
			super.onPreExecute();
			initiatePopupWindow();
		}

		@Override
		protected Boolean doInBackground(final String... args) {
			XMLparserStandar parser = new XMLparserStandar(feedUrl, getBaseContext());
			statusRespuesta = parser.parse();
			return true;
		}

	@Override
		protected void onPostExecute(Boolean success) {
			if (success) {
				try {
					pwindo.dismiss();
										
					Intent newActivity = new Intent(Activity_Calificar.this, Activity_Calificaciones.class);
					newActivity.putExtra("nombre", nombre.getText()); 
					newActivity.putExtra("ID", ID);
					newActivity.putExtra("status", statusRespuesta);
		            startActivity(newActivity);
					
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				Toast.makeText(ctx, "Error en la lectura", Toast.LENGTH_LONG)
						.show();
			}
		}
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.checkbox_status:
			
			if (((CheckBox)v).isChecked()) {
				text_recomendado.setText("SI");
            } else {
            	text_recomendado.setText("NO");
            }
			break;
		case R.id.calificar_bt_listo:
			
			int claridad = (int) (r_claridad.getRating() *2);
			int ayuda = (int) (r_ayuda.getRating() *2);
			int facilidad = (int) (r_facilidad.getRating() *2);
			String clase_;
			String comentario_;
			String status = "0";
			
			if (chb_recomendado.isChecked()) {
				status = "1";
            } else {
            	status = "0";
            }
			
			clase_ = "" + clase.getText();
			clase_ = "" + clase.getText();
			clase_ = "" + clase.getText();
			clase_ = clase_.replace('&','.');
			clase_ = clase_.replace('=','.');
			clase_ = clase_.replace('+','.');
			
			comentario_ = "" + comentario.getText();
			comentario_ = "" + comentario.getText();
			comentario_ = "" + comentario.getText();
			comentario_ = comentario_.replace('\n',' ');
			comentario_ = comentario_.replace('&','.');
			comentario_ = comentario_.replace('=','.');
			comentario_ = comentario_.replace('+','.');
			
			if (comentario.getText().length() <= 1){
				comentario_ = "Sin comentario...";
			}
			
			if (clase_.length() <= 4){

				customToast_aviso();
				
			}else {
				URL = URL + "?id_maestro="+ID+"&ayuda="+ayuda+"&claridad="+claridad+"&facilidad="+facilidad+"&materia="+clase_+"&comentario="+comentario_+"&recomendado="+status;	
				URL = URL.replace(' ','+');
				enviaComentario();

			}
			
			break;
			
		}
	}
	
	public void inicializador(){
		
		clase =  ( EditText ) findViewById( R.id.calificar_materia );
		
		comentario =  ( EditText ) findViewById( R.id.calificar_comentario );
		if (comentario != null) {
			comentario.setHorizontallyScrolling(false);
			comentario.setLines(14);
		}
		
		r_claridad = (RatingBar) findViewById(R.id.ratingBarClaridad);

		r_ayuda = (RatingBar) findViewById(R.id.ratingBarAyuda);
		
		r_facilidad = (RatingBar) findViewById(R.id.ratingBarFacilidad);
		
		chb_recomendado  = ( CheckBox ) findViewById( R.id.checkbox_status );
		chb_recomendado.setOnClickListener(this);
		
		text_recomendado = (TextView) findViewById(R.id.textView_status);
		
		enviar = (Button) findViewById(R.id.calificar_bt_listo);
		enviar.setOnClickListener(this);
		
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
	
	public void customToast_aviso(){
		
		Context context = getApplicationContext();
		CharSequence text = "Por favor agrega una Clase valida";
		int duration = Toast.LENGTH_LONG;
		 
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.custom_toast_aviso,
		        (ViewGroup) findViewById(R.id.toast_layout_aviso));
		 
		TextView textToast = (TextView) layout.findViewById(R.id.text_toast);
		textToast.setText(text);
		 
		Toast toast = new Toast(context);
		toast.setDuration(duration);
		toast.setGravity(Gravity.CENTER,0,0);
		toast.setView(layout);
		toast.show();
		
	}
	
	
	private void initiatePopupWindow() {
	try {

	LayoutInflater inflater = (LayoutInflater) Activity_Calificar.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	View layout = inflater.inflate(R.layout.custom_popup_aviso,(ViewGroup) findViewById(R.id.popup_layout_aviso));
	layout.setAnimation(AnimationUtils.loadAnimation(this, R.drawable.popupanim));
	pwindo = new PopupWindow(layout, 320, 140, true);
	pwindo.setBackgroundDrawable(new BitmapDrawable());
	pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
	
	TextView textToast = (TextView) layout.findViewById(R.id.text_toast);
	textToast.setText("Registrando calificación, Espere.");


	} catch (Exception e) {
	e.printStackTrace();
	}
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}
	
	
}
