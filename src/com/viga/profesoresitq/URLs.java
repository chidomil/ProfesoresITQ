package com.viga.profesoresitq;

public class URLs {
	
	private String dominio = "http://calavera3.vzpla.net/api/";
	//http://192.168.1.105:8088/profesoresitq/api/
	//http://www.profesoresitq.co.nf/api/
	//"http://profesoresitq.vzpla.net/api/";
	
	String Lista_maestros = dominio + "lista_profesores.php";
	String Lista_archivos = dominio + "lista_archivos_profesor.php?id_profesor=";
	String Lista_calificaciones = dominio + "lista_comentarios_profesor.php?id_profesor=";
	String agregar_calificacion = dominio + "agregar_comentario.php";
	String agregar_archivo = dominio + "agrega_archivo.php";
	String status_archivo = dominio + "consultar_archivo_agregado.php?nombre_archivo=";
	String agregar_maestro = dominio + "agrega_profesor.php?nombre_maestro=";
	String status_maestro = dominio + "consultar_profesor_agregado.php?nombre_maestro=";
	String status_publicidad = dominio + "status_publicidad.php";
	String status_publicidad2 = dominio + "status_publicidad_dos.php";
	String ruta_chat = dominio + "chat/";
	
}
