package com.viga.profesoresitq;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.util.Log;

public class XMLparserComentario {
	private URL url;
	Context contextor;
	
	public XMLparserComentario(String url,Context contexto ) {
		contextor=contexto;
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Comentario> parse() {
		
		ArrayList<Comentario> comentarios = new ArrayList<Comentario>();
		Comentario comentarioActual;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		try {
		
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(this.url.openConnection().getInputStream());
			 Element root = dom.getDocumentElement();
			
			NodeList items = root.getElementsByTagName("evaluacion"); 
			for (int i=0;i<items.getLength();i++){
				comentarioActual = new Comentario();				
				Node item = items.item(i);
				NodeList properties = item.getChildNodes();
				for (int j=0;j<properties.getLength();j++){
					Node property = properties.item(j);
					String name = property.getNodeName();
					if (name.equalsIgnoreCase("materia")){
						comentarioActual.setMateria(property.getFirstChild().getNodeValue());
					}else if (name.equalsIgnoreCase("facilidad")){
						comentarioActual.setFacilidad(property.getFirstChild().getNodeValue());
					}else if (name.equalsIgnoreCase("ayuda")){
					comentarioActual.setAyuda(property.getFirstChild().getNodeValue());
					}else if (name.equalsIgnoreCase("claridad")){
						comentarioActual.setClaridad(property.getFirstChild().getNodeValue());
					}else if (name.equalsIgnoreCase("comentario")){
						comentarioActual.setComentario(property.getFirstChild().getNodeValue());
					}else if (name.equalsIgnoreCase("recomendado")){
						comentarioActual.setRecomendado(property.getFirstChild().getNodeValue());
					}else if (name.equalsIgnoreCase("fecha")){
						comentarioActual.setFecha(property.getFirstChild().getNodeValue());
					}
					
				}
				comentarios.add(comentarioActual);
				Log.i("Parsher", "comentario:"+i);
			}
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 

		return comentarios;
	}
	
}