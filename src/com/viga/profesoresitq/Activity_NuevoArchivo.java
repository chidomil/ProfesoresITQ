package com.viga.profesoresitq;

import java.io.File;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_NuevoArchivo extends Activity implements OnClickListener {

	TextView nombre;
	EditText clase;
	EditText nombre_archivo;
	Button botonGaleria;
	Button botonFoto;
	Button botonListo;
	ImageView imagenArchivo;
	private PopupWindow pwindo;

	public String sinespacios;
	private int tipo_img = 0; // 1 para foto, 2 para galeria, 0 sin nada
	double aleatorio = 0;
	private String ruta_foto;
	private String ruta_imagen = "";
	private int TAKE_PICTURE = 1;
	private int SELECCIONAR_IMAGEN = 2;
	private URLs urls = new URLs();
	public String URL = urls.agregar_archivo;
	public String URL_status = urls.status_archivo;
	public String statusRespuesta;
	public String ID;
	public String nombre_maestro;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nuevo_archivo);

		aleatorio = new Double(Math.random() * 100).intValue();
		ruta_foto = Environment.getExternalStorageDirectory() + "/ITQ"
				+ aleatorio + ".jpg";

		clase = (EditText) findViewById(R.id.archivo_materia);
		nombre_archivo = (EditText) findViewById(R.id.archivo_nombre);
		botonGaleria = (Button) findViewById(R.id.archivo_bt_galeria);
		botonFoto = (Button) findViewById(R.id.archivo_bt_foto);
		botonListo = (Button) findViewById(R.id.archivo_bt_listo);
		nombre = (TextView) findViewById(R.id.archivo_nombre_maestro);
		imagenArchivo = (ImageView) findViewById(R.id.imagenArchivo);
		Bundle extras = this.getIntent().getExtras();
		nombre.setText(extras.getString("nombre"));
		ID = extras.getString("ID");
		nombre_maestro = extras.getString("nombre");

		botonGaleria.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ventanaImagen();
			}
		});
		botonFoto.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				camaraImagen();
			}
		});
		botonListo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				enviartodo();
			}
		});

	}

	private void ventanaImagen() {

		Intent intentSeleccionarImagen = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
		intentSeleccionarImagen.setType("image/*");
		startActivityForResult(intentSeleccionarImagen, SELECCIONAR_IMAGEN);
	}

	private void camaraImagen() {

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Uri output = Uri.fromFile(new File(ruta_foto));
		intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
		startActivityForResult(intent, TAKE_PICTURE);
	}

	private void enviartodo() {

		if (tipo_img == 0) {

			customToast_aviso("No has agregado un Archivo", 1);

		} else {

			if (clase.getText().length() <= 4) {

				customToast_aviso("Clase no Valida", 1);

			} else {

				if (nombre_archivo.getText().length() <= 5) {

					customToast_aviso("Nombre no Valido", 1);

				} else {
					URL = URL + "?id_maestro=" + ID + "&materia="
							+ clase.getText() + "&nombre_archivo="
							+ nombre_archivo.getText();
					URL = URL.replace(' ', '+');

					sinespacios = "" + nombre_archivo.getText();
					sinespacios = sinespacios.replace(' ', '_');
					URL_status = URL_status + sinespacios;
					URL_status = URL_status.replace(' ', '+');

					if (isOnline()) {

						if (tipo_img == 1) {
							AgregaArchivo(ruta_foto);
						} else if (tipo_img == 2) {
							AgregaArchivo(ruta_imagen);
						}

					} else {
						customToast_aviso("No hay conexión a Internet", 1);
					}

				}
			}
		}

	}

	Bitmap ShrinkBitmap(String file, int width, int height) {

		BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
		bmpFactoryOptions.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

		int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight
				/ (float) height);
		int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth
				/ (float) width);

		if (heightRatio > 1 || widthRatio > 1) {
			if (heightRatio > widthRatio) {
				bmpFactoryOptions.inSampleSize = heightRatio;
			} else {
				bmpFactoryOptions.inSampleSize = widthRatio;
			}
		}

		bmpFactoryOptions.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
		return bitmap;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			if (requestCode == SELECCIONAR_IMAGEN) {
				if (resultCode == Activity.RESULT_OK) {
					Uri selectedImage = data.getData();

					ruta_imagen = obtieneRuta(selectedImage);
					// Bitmap bitmap = getBitmap(ruta_imagen);
					Bitmap bitmap = ShrinkBitmap(ruta_imagen, 150, 100);

					/*
					 * if (bitmap.getHeight() >= 1200 || bitmap.getWidth() >=
					 * 1200) { bitmap = Bitmap.createScaledBitmap( bitmap,
					 * (bitmap.getHeight() >= 1200) ? 1200 : bitmap
					 * .getHeight(), (bitmap.getWidth() >= 1200) ? 1200 : bitmap
					 * .getWidth(), true); imagenArchivo.setImageBitmap(bitmap);
					 * bitmap.recycle(); bitmap = null;
					 * 
					 * tipo_img = 2;
					 * 
					 * } else {
					 * 
					 * imagenArchivo.setImageBitmap(BitmapFactory
					 * .decodeFile(ruta_imagen)); tipo_img = 2; }
					 */

					imagenArchivo.setImageBitmap(bitmap);
					tipo_img = 2;
				}
			} else if (requestCode == TAKE_PICTURE) {

				File file = new File(ruta_foto);
				if (file.exists()) {

					Bitmap bitmap = ShrinkBitmap(ruta_foto, 150, 100);
					imagenArchivo.setImageBitmap(bitmap);
					// imagenArchivo.setImageBitmap(BitmapFactory
					// .decodeFile(ruta_foto));

					tipo_img = 1;
				}

			}

		} catch (Exception e) {
		}
	}

	/*
	 * private Bitmap getBitmap(String ruta_imagen) { File imagenArchivo = new
	 * File(ruta_imagen); Bitmap bitmap = null;
	 * 
	 * if (imagenArchivo.exists()) { bitmap =
	 * BitmapFactory.decodeFile(imagenArchivo.getAbsolutePath()); } return
	 * bitmap; }
	 */

	private String obtieneRuta(Uri uri) {
		String[] projection = { android.provider.MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public void AgregaArchivo(String s) {
		UploaderFoto nuevaTarea = new UploaderFoto();
		nuevaTarea.execute(s);
	}

	class UploaderFoto extends AsyncTask<String, Void, Void> {

		String miFoto = "";

		@Override
		protected Void doInBackground(String... params) {
			miFoto = params[0];
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpContext localContext = new BasicHttpContext();
				HttpPost httppost = new HttpPost(URL);

				MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				//mpEntity.addPart("FotoNom", new StringBody("" + nombre_archivo.getText()));
				mpEntity.addPart("fotoUp", new FileBody(new File(miFoto), getMimeType(miFoto)));
				httppost.setEntity(mpEntity);
				
		        HttpResponse response1 = httpclient.execute(httppost,localContext);
		        HttpEntity resEntity = response1.getEntity();
		        String Response=EntityUtils.toString(resEntity);
		        Log.d("Response:", Response);
				httpclient.getConnectionManager().shutdown();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPreExecute() {
			super.onPreExecute();

			initiatePopupWindow();

		}

		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			consultaStatus();

		}

	}
	
	public static String getMimeType(String filePath) {
        String type = null;
        String extension = getFileExtensionFromUrl(filePath);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }
	
	public static String getFileExtensionFromUrl(String url) {
        int dotPos = url.lastIndexOf('.');
        if (0 <= dotPos) {
            return (url.substring(dotPos + 1)).toLowerCase();
        }

        return "";
    }

	public void consultaStatus() {

		new DescargarStatus(getBaseContext(), URL_status).execute();

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
						pwindo.dismiss();
						customToast_aviso("Archivo Agregado", 2);
						finish();

						Intent newActivity = new Intent(
								Activity_NuevoArchivo.this,
								Activity_Archivos.class);
						newActivity.putExtra("nombre", nombre_maestro);
						newActivity.putExtra("ID", respuesta[1]);
						newActivity.putExtra("status", "desactivado");
						startActivity(newActivity);

					} else{
						pwindo.dismiss();
						customToast_aviso("Error al Agregar", 3);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				Toast.makeText(ctx, "Error en la lectura", Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	public void customToast_aviso(String texto, int tipo) {

		if (tipo == 1) {

			Context context = getApplicationContext();
			CharSequence text = texto;
			int duration = Toast.LENGTH_LONG;

			LayoutInflater inflater = getLayoutInflater();
			View layout = inflater.inflate(R.layout.custom_toast_aviso,
					(ViewGroup) findViewById(R.id.toast_layout_aviso));

			TextView textToast = (TextView) layout
					.findViewById(R.id.text_toast);
			textToast.setText(text);

			Toast toast = new Toast(context);
			toast.setDuration(duration);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.setView(layout);
			toast.show();
		}

		else if (tipo == 2) {

			Context context = getApplicationContext();
			CharSequence text = texto;
			int duration = Toast.LENGTH_LONG;

			LayoutInflater inflater = getLayoutInflater();
			View layout = inflater.inflate(R.layout.custom_toast_ok,
					(ViewGroup) findViewById(R.id.toast_layout_ok));

			TextView textToast = (TextView) layout
					.findViewById(R.id.text_toast);
			textToast.setText(text);

			Toast toast = new Toast(context);
			toast.setDuration(duration);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.setView(layout);
			toast.show();
		}

		else if (tipo == 3) {

			Context context = getApplicationContext();
			CharSequence text = texto;
			int duration = Toast.LENGTH_LONG;

			LayoutInflater inflater = getLayoutInflater();
			View layout = inflater.inflate(R.layout.custom_toast_fallo,
					(ViewGroup) findViewById(R.id.toast_layout_fallo));

			TextView textToast = (TextView) layout
					.findViewById(R.id.text_toast);
			textToast.setText(text);

			Toast toast = new Toast(context);
			toast.setDuration(duration);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.setView(layout);
			toast.show();
		}

	}

	private void initiatePopupWindow() {
		try {

			LayoutInflater inflater = (LayoutInflater) Activity_NuevoArchivo.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.custom_popup_aviso,
					(ViewGroup) findViewById(R.id.popup_layout_aviso));
			layout.setAnimation(AnimationUtils.loadAnimation(this,
					R.drawable.popupanim));
			pwindo = new PopupWindow(layout, 320, 140, true);
			pwindo.setBackgroundDrawable(new BitmapDrawable());
			pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);

			TextView textToast = (TextView) layout
					.findViewById(R.id.text_toast);
			textToast.setText("Agregando Archivo, Espere.");

		} catch (Exception e) {
			e.printStackTrace();
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
		// TODO Auto-generated method stub

	}
}