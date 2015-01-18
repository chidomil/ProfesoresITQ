package com.viga.profesoresitq;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Profesor_Adapter extends ArrayAdapter<Object> {
	
	Context context;
	private ArrayList<Profesor> profesores;
	
	public Profesor_Adapter(Context context, ArrayList<Profesor> profesores) {
		super(context, R.layout.item_profesor);
		this.context = context;
		this.profesores = profesores;
	}
	
	public int getCount() {
		return profesores.size();
	}
	
	private static class PlaceHolder {

		TextView nombre;
		TextView karma;
		TextView id;
		ImageView foto;

		public static PlaceHolder generate(View convertView) {
			PlaceHolder placeHolder = new PlaceHolder();
			placeHolder.nombre = (TextView) convertView
					.findViewById(R.id.profesor_textview_nombre);
			
			placeHolder.karma = (TextView) convertView
					.findViewById(R.id.profesor_textview_karma);
			
			placeHolder.id = (TextView) convertView
					.findViewById(R.id.profesor_textview_id);
			
			placeHolder.foto = (ImageView) convertView
					.findViewById(R.id.profesor_imageView);
			return placeHolder;
		}

	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		PlaceHolder placeHolder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_profesor, null);
			placeHolder = PlaceHolder.generate(convertView);
			convertView.setTag(placeHolder);
		} else {
			placeHolder = (PlaceHolder) convertView.getTag();
		}
		placeHolder.nombre.setText(profesores.get(position).getNombre());
		
		placeHolder.karma.setText(profesores.get(position).getKarma());
		
		placeHolder.id.setText(profesores.get(position).getId());
		
        ImageLoader imgLoader = new ImageLoader(context);
        imgLoader.DisplayImage(profesores.get(position).getFoto(), placeHolder.foto);
		
		
		return (convertView);
	}
	

}
