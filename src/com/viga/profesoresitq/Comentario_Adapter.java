package com.viga.profesoresitq;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Comentario_Adapter extends ArrayAdapter<Object> {
	
	Context context;
	private ArrayList<Comentario> comentarios;
	
	public Comentario_Adapter(Context context, ArrayList<Comentario> comentarios) {
		super(context, R.layout.item_calificacion);
		this.context = context;
		this.comentarios = comentarios;
	}
	
	public int getCount() {
		return comentarios.size();
	}
	
	private static class PlaceHolder {

		TextView materia;
		TextView facilidad;
		TextView ayuda;
		TextView claridad;
		TextView comentario;
		TextView fecha;
		ImageView recomendado;

		public static PlaceHolder generate(View convertView) {
			PlaceHolder placeHolder = new PlaceHolder();
			placeHolder.materia = (TextView) convertView
					.findViewById(R.id.comentario_textview_materia);
			
			placeHolder.facilidad = (TextView) convertView
					.findViewById(R.id.comentario_textview_facilidad);
			
			placeHolder.ayuda = (TextView) convertView
					.findViewById(R.id.comentario_textview_ayuda);
			
			placeHolder.claridad = (TextView) convertView
					.findViewById(R.id.comentario_textview_claridad);
			
			placeHolder.comentario = (TextView) convertView
					.findViewById(R.id.comentario_textview_comentario);
			
			placeHolder.fecha = (TextView) convertView
					.findViewById(R.id.comentario_textview_fecha);
			
			placeHolder.recomendado = (ImageView) convertView
					.findViewById(R.id.comentario_imageview_recomendado);
			return placeHolder;
		}

	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		PlaceHolder placeHolder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_calificacion, null);
			placeHolder = PlaceHolder.generate(convertView);
			convertView.setTag(placeHolder);
		} else {
			placeHolder = (PlaceHolder) convertView.getTag();
		}
		placeHolder.materia.setText(comentarios.get(position).getMateria());
		
		placeHolder.facilidad.setText(comentarios.get(position).getFacilidad());
		
		placeHolder.ayuda.setText(comentarios.get(position).getAyuda());
		
		placeHolder.claridad.setText(comentarios.get(position).getClaridad());
		
		placeHolder.comentario.setText(comentarios.get(position).getComentario());
		
		placeHolder.fecha.setText(comentarios.get(position).getFecha());
		
		if (comentarios.get(position).getRecomendado().equals("si")){
			placeHolder.recomendado.setImageResource(R.drawable.recomendado);
		}
		else {
			placeHolder.recomendado.setImageResource(R.drawable.no_recomendado);
		}
	
		return (convertView);
	}
	

}
