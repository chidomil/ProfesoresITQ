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

import org.apache.http.conn.ClientConnectionManager;
//import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_NuevoMaestro extends Activity {

	private ImageView imagenProfesor;
	private Button botonGuardar;
	private Button botonImagenProfesor;
	private EditText editTextNombre;
	private EditText editTextApellido;
	private URLs urls = new URLs();
	public String URL = urls.agregar_maestro;
	public String URL_status = urls.status_maestro;
	public String sinespacio;
	private String ruta_imagen = "";
	private int SELECCIONAR_IMAGEN = 237487;
	boolean sinimagen;
	public String statusRespuesta;
	public String nombre_completo;
	private PopupWindow pwindo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nuevo_maestro);

		inicializar();

		botonImagenProfesor.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ventanaImagen();
			}
		});

		botonGuardar.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (isOnline()) {

					if (editTextApellido.getText().length() <= 4) {

						customToast_aviso("Apellido no Valido", 1);

					} else {

						if (editTextNombre.getText().length() <= 4) {

							customToast_aviso("Nombre no Valido", 1);

						} else {

							nombre_completo = "";
							nombre_completo = "" + editTextApellido.getText()
									+ " " + editTextNombre.getText();
							URL = URL + nombre_completo;
							URL = URL.replace(' ', '+');

							URL_status = URL_status + nombre_completo;
							URL_status = URL_status.replace(' ', '+');

							sinespacio = "" + editTextApellido.getText();
							sinespacio = sinespacio.replace(' ', '_');

							if (ruta_imagen.equals("")) {
								URL = URL + "&imagen=0";
								sinimagen = true;
								ejecuta(ruta_imagen);

							} else {
								URL = URL + "&imagen=1";
								sinimagen = false;
								ejecuta(ruta_imagen);
							}

						}
					}
				} else {
					customToast_aviso("No hay conexión a Internet", 1);
				}

			}
		});

	}

	private void inicializar() {

		botonGuardar = (Button) findViewById(R.id.maestro_bt_listo);
		editTextNombre = (EditText) findViewById(R.id.maestro_nombre);
		editTextApellido = (EditText) findViewById(R.id.maestro_apellidos);
		botonImagenProfesor = (Button) findViewById(R.id.maestro_bt_foto);
		imagenProfesor = (ImageView) findViewById(R.id.imagenProfesor);
		imagenProfesor.setScaleType(ScaleType.FIT_XY);

	}

	private void ventanaImagen() {

		Intent intentSeleccionarImagen = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
		intentSeleccionarImagen.setType("image/*");
		startActivityForResult(intentSeleccionarImagen, SELECCIONAR_IMAGEN);
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

					Bitmap bitmap = ShrinkBitmap(ruta_imagen, 150, 150);
					imagenProfesor.setImageBitmap(bitmap);

					/*
					 * Bitmap bitmap = getBitmap(ruta_imagen);
					 * 
					 * if (bitmap.getHeight() >= 1200 || bitmap.getWidth() >=
					 * 1200) { bitmap = Bitmap.createScaledBitmap( bitmap,
					 * (bitmap.getHeight() >= 1200) ? 1200 : bitmap
					 * .getHeight(), (bitmap.getWidth() >= 1200) ? 1200 : bitmap
					 * .getWidth(), true);
					 * imagenProfesor.setImageBitmap(bitmap);
					 * 
					 * } else { //imagenProfesor.setImageURI(selectedImage);
					 * imagenProfesor
					 * .setImageBitmap(BitmapFactory.decodeFile(ruta_imagen)); }
					 */
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

	class UploaderFoto extends AsyncTask<String, Void, Void> {

		String miFoto = "";

		@Override
		protected Void doInBackground(String... params) {
			miFoto = params[0];
				
				HttpClient httpclient = new DefaultHttpClient();
				HttpContext localContext = new BasicHttpContext();
				HttpPost httppost = new HttpPost(URL);


				if (!sinimagen) {
					MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
					mpEntity.addPart("fotoUp", new FileBody(new File(miFoto), getMimeType(miFoto)));
					httppost.setEntity(mpEntity);
				}

					try {
						
				        HttpResponse response1 = httpclient.execute(httppost,localContext);
				        HttpEntity resEntity = response1.getEntity();
				        String Response=EntityUtils.toString(resEntity);
				        Log.d("Response:", Response);
						
						
						
					} catch (Exception e) {
						e.printStackTrace();Log.i("BitmapFactory", "status:"+ e.getMessage());
						customToast_aviso("Error inesperado", 3);
					}
				httpclient.getConnectionManager().shutdown();

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
						customToast_aviso("Profesor Agregado", 2);
						finish();

						Intent newActivity = new Intent(
								Activity_NuevoMaestro.this,
								Activity_Calificar.class);
						newActivity.putExtra("nombre",
								nombre_completo.toUpperCase());
						newActivity.putExtra("ID", respuesta[1]);
						startActivity(newActivity);

					} else {
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

	public void ejecuta(String s) {

		UploaderFoto nuevaTarea = new UploaderFoto();
		nuevaTarea.execute(s);

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

			LayoutInflater inflater = (LayoutInflater) Activity_NuevoMaestro.this
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
			textToast.setText("Agregando Profesor, Espere.");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	   public static HttpClient wrapClient(HttpClient base) {
		    try {
		        SSLContext ctx = SSLContext.getInstance("TLS");
		        X509TrustManager tm = new X509TrustManager() {
		            public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException { }

		            public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException { }

		            public X509Certificate[] getAcceptedIssuers() {
		                return null;
		            }
		        };
		        ctx.init(null, new TrustManager[]{tm}, null);
		        MySSLSocketFactory ssf = new MySSLSocketFactory(ctx);
		        ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	        
		        ClientConnectionManager ccm = base.getConnectionManager();
		        SchemeRegistry sr = ccm.getSchemeRegistry();
		        sr.register(new Scheme("http", ssf, 80));
		        sr.register(new Scheme("https", ssf, 443));
		        return new DefaultHttpClient(ccm, base.getParams());
		    } catch (Exception ex) {
		        return null;
		    }
		}
	

}