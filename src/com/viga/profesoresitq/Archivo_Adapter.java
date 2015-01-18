package com.viga.profesoresitq;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Archivo_Adapter extends ArrayAdapter<Object> {
	
	Context context;
	private ArrayList<Archivo> archivos;
	
	public Archivo_Adapter(Context context, ArrayList<Archivo> archivos) {
		super(context, R.layout.item_archivo);
		this.context = context;
		this.archivos = archivos;
	}
	
	public int getCount() {
		return archivos.size();
	}
	
	private static class PlaceHolder {

		TextView descripcion;
		TextView fecha;
		ImageView imagen;

		public static PlaceHolder generate(View convertView) {
			PlaceHolder placeHolder = new PlaceHolder();
			
			placeHolder.descripcion = (TextView) convertView
					.findViewById(R.id.archivo_textview_desc);
			
			placeHolder.fecha = (TextView) convertView
					.findViewById(R.id.archivo_textview_fecha);
			
			placeHolder.imagen = (ImageView) convertView
					.findViewById(R.id.archivo_imageView);
			
			return placeHolder;
		}

	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		PlaceHolder placeHolder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_archivo, null);
			placeHolder = PlaceHolder.generate(convertView);
			convertView.setTag(placeHolder);
		} else {
			placeHolder = (PlaceHolder) convertView.getTag();
		}
		placeHolder.descripcion.setText(archivos.get(position).getDescripcion());
		
		placeHolder.fecha.setText(archivos.get(position).getFecha());
		
		ImageLoader imgLoader = new ImageLoader(context);
        imgLoader.DisplayImage2(archivos.get(position).getImagen(), placeHolder.imagen);
		
		
		return (convertView);
	}
	

}
