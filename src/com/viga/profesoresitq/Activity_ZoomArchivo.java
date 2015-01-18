package com.viga.profesoresitq;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.util.ByteArrayBuffer;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.BitmapAjaxCallback;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.FloatMath;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_ZoomArchivo extends Activity implements OnTouchListener 
{
    private static final String TAG = "Touch";
    @SuppressWarnings("unused")
    private static final float MIN_ZOOM = 1f,MAX_ZOOM = 1f;

    // These matrices will be used to scale points of the image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // The 3 states (events) which the user is trying to perform
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // these PointF objects are used to record the point(s) the user is touching
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;
    
    private String ruta_img;
    Bitmap myBitmap;
    private PopupWindow pwindo;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {    	
		Bundle extras = this.getIntent().getExtras();
		ruta_img = extras.getString("url");
    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);
        ImageView view = (ImageView) findViewById(R.id.imageViewZoom);
        
        //ImageLoaderZoom imgLoader = new ImageLoaderZoom(getBaseContext());
        //imgLoader.DisplayImage3(ruta_img, view);
        
        AQuery aq = new AQuery(view);
        aq.id(R.id.imageViewZoom).image(ruta_img);
        
        
        Button download_image = (Button) findViewById(R.id.bt_svimg);
        download_image.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {	
            	          	
            	if (isOnline()) {
        			new ImagendeURL(getBaseContext()).execute();
        		}
        		else{
        			customToast_aviso("Sin Acceso a Internet", 1);
        		}
            	
            }
        });
        
        
        
        view.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) 
    {
        ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;

        dumpEvent(event);
        // Handle touch events here...

        switch (event.getAction() & MotionEvent.ACTION_MASK) 
        {
            case MotionEvent.ACTION_DOWN:   // first finger down only
                                                savedMatrix.set(matrix);
                                                start.set(event.getX(), event.getY());
                                                Log.d(TAG, "mode=DRAG"); // write to LogCat
                                                mode = DRAG;
                                                break;

            case MotionEvent.ACTION_UP: // first finger lifted

            case MotionEvent.ACTION_POINTER_UP: // second finger lifted

                                                mode = NONE;
                                                Log.d(TAG, "mode=NONE");
                                                break;

            case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down

                                                oldDist = spacing(event);
                                                Log.d(TAG, "oldDist=" + oldDist);
                                                if (oldDist > 5f) {
                                                    savedMatrix.set(matrix);
                                                    midPoint(mid, event);
                                                    mode = ZOOM;
                                                    Log.d(TAG, "mode=ZOOM");
                                                }
                                                break;

            case MotionEvent.ACTION_MOVE:

                                                if (mode == DRAG) 
                                                { 
                                                    matrix.set(savedMatrix);
                                                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y); // create the transformation in the matrix  of points
                                                } 
                                                else if (mode == ZOOM) 
                                                { 
                                                    // pinch zooming
                                                    float newDist = spacing(event);
                                                    Log.d(TAG, "newDist=" + newDist);
                                                    if (newDist > 5f) 
                                                    {
                                                        matrix.set(savedMatrix);
                                                        scale = newDist / oldDist; // setting the scaling of the
                                                                                    // matrix...if scale > 1 means
                                                                                    // zoom in...if scale < 1 means
                                                                                    // zoom out
                                                        matrix.postScale(scale, scale, mid.x, mid.y);
                                                    }
                                                }
                                                break;
        }

        view.setImageMatrix(matrix); // display the transformation on screen

        return true; // indicate event was handled
    }

    /*
     * --------------------------------------------------------------------------
     * Method: spacing Parameters: MotionEvent Returns: float Description:
     * checks the spacing between the two fingers on touch
     * ----------------------------------------------------
     */

    private float spacing(MotionEvent event) 
    {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    /*
     * --------------------------------------------------------------------------
     * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
     * Description: calculates the midpoint between the two fingers
     * ------------------------------------------------------------
     */

    private void midPoint(PointF point, MotionEvent event) 
    {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /** Show an event in the LogCat view, for debugging */
    private void dumpEvent(MotionEvent event) 
    {
        String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE","POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);

        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) 
        {
            sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }

        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++) 
        {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }

        sb.append("]");
        Log.d("Touch Events ---------", sb.toString());
    }
    
    public void customToast_aviso(String texto, int tipo){
    	
    	if (tipo == 1){

    	
    	Context context = getApplicationContext();
    	CharSequence text = texto;
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
    	
    	else if (tipo == 2){

    		
    		Context context = getApplicationContext();
    		CharSequence text = texto;
    		int duration = Toast.LENGTH_LONG;
    		 
    		LayoutInflater inflater = getLayoutInflater();
    		View layout = inflater.inflate(R.layout.custom_toast_ok,
    		        (ViewGroup) findViewById(R.id.toast_layout_ok));
    		 
    		TextView textToast = (TextView) layout.findViewById(R.id.text_toast);
    		textToast.setText(text);
    		 
    		Toast toast = new Toast(context);
    		toast.setDuration(duration);
    		toast.setGravity(Gravity.CENTER,0,0);
    		toast.setView(layout);
    		toast.show();
    		}
    	
    	else if (tipo == 3){

    		
    		Context context = getApplicationContext();
    		CharSequence text = texto;
    		int duration = Toast.LENGTH_LONG;
    		 
    		LayoutInflater inflater = getLayoutInflater();
    		View layout = inflater.inflate(R.layout.custom_toast_fallo,
    		        (ViewGroup) findViewById(R.id.toast_layout_fallo));
    		 
    		TextView textToast = (TextView) layout.findViewById(R.id.text_toast);
    		textToast.setText(text);
    		 
    		Toast toast = new Toast(context);
    		toast.setDuration(duration);
    		toast.setGravity(Gravity.CENTER,0,0);
    		toast.setView(layout);
    		toast.show();
    		}
    	
    }
    
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    } 
    
    @Override
	protected void onPause() {
		super.onPause();
		finish();
	}
    
    private class ImagendeURL extends AsyncTask<String, Void, Boolean> {

		public ImagendeURL(Context c) {
		}

		@Override
		protected Boolean doInBackground(final String... args) {
			try{
				
				String url = ruta_img;
				String fileName = url.substring( url.lastIndexOf('/')+1, url.length() );
				String fileNameWithoutExtn = fileName.substring(0, fileName.lastIndexOf('.'));
				DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
				request.setDescription("imagen de ProfesoresITQ");
				request.setTitle(fileNameWithoutExtn);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				    request.allowScanningByMediaScanner();
				    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
				}
				request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileNameWithoutExtn + System.currentTimeMillis() +".jpg");

				DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
				manager.enqueue(request);

	       
	            return true;
			} catch (Exception e) {
				return false;
			}
		}
		
		protected void onPreExecute() {
			super.onPreExecute();
			
			initiatePopupWindow();
			
		}

	@Override
		protected void onPostExecute(Boolean success) {
		pwindo.dismiss();
			if (success) {
				customToast_aviso("Imagen Guardada", 2);
			} else {
				customToast_aviso("Imposible guardar Imagen", 3);
			}
		}
	}
    
    
	private void initiatePopupWindow() {
	try {

	LayoutInflater inflater = (LayoutInflater) Activity_ZoomArchivo.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	View layout = inflater.inflate(R.layout.custom_popup_aviso,(ViewGroup) findViewById(R.id.popup_layout_aviso));
	layout.setAnimation(AnimationUtils.loadAnimation(this, R.drawable.popupanim));
	pwindo = new PopupWindow(layout, 320, 140, true);
	pwindo.setBackgroundDrawable(new BitmapDrawable());
	pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
	
	TextView textToast = (TextView) layout.findViewById(R.id.text_toast);
	textToast.setText("Guardando Imagen, Espere.");


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
    
}