package com.viga.profesoresitq;

public class Archivo {
	
	private String descripcion;
	private String imagen;
	private String fecha;
	
	public void setDescripcion(String des) {
		this.descripcion = des.toUpperCase();
	}
	
	public String getDescripcion() {
		return this.descripcion;
	}
	
	public void setImagen(String pic) {
		this.imagen = pic;
	}
	
	public String getImagen() {
		return this.imagen;
	}
	
	public void setFecha(String date) {
		this.fecha = date;
	}
	
	public String getFecha() {
		return this.fecha;
	}
	
	

}

