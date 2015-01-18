package com.viga.profesoresitq;

public class Profesor {
	
	private String id;
	private String nombre;
	private String foto;
	private String karma;
	
	public void setNombre(String name) {
		this.nombre = name.toUpperCase();
	}
	
	public String getNombre() {
		return this.nombre;
	}
	
	public void setFoto(String photo) {
		this.foto = photo;
	}
	
	public String getFoto() {
		return this.foto;
	}
	
	public void setId(String id) {
		this.id = id;
	}
		
	public String getId() {
		return this.id;
	}
	
	public void setKarma(String karma) {
		this.karma = karma;
	}
		
	public String getKarma() {
		return this.karma;
	}

}

