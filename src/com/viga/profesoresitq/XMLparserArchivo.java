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

public class XMLparserArchivo {
	private URL url;
	Context contextor;
	
	public XMLparserArchivo(String url,Context contexto ) {
		contextor=contexto;
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Archivo> parse() {
		
		ArrayList<Archivo> archivos = new ArrayList<Archivo>();
		Archivo archivoActual;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		try {
		
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(this.url.openConnection().getInputStream());
			 Element root = dom.getDocumentElement();
			
			NodeList items = root.getElementsByTagName("archivo"); 
			for (int i=0;i<items.getLength();i++){
				archivoActual = new Archivo();				
				Node item = items.item(i);
				NodeList properties = item.getChildNodes();
				for (int j=0;j<properties.getLength();j++){
					Node property = properties.item(j);
					String name = property.getNodeName();
					if (name.equalsIgnoreCase("descripcion")){
						archivoActual.setDescripcion(property.getFirstChild().getNodeValue());
					}else if (name.equalsIgnoreCase("imagen")){
						archivoActual.setImagen(property.getFirstChild().getNodeValue());
					}else if (name.equalsIgnoreCase("fecha")){
						archivoActual.setFecha(property.getFirstChild().getNodeValue());
					}
					
				}
				archivos.add(archivoActual);
				Log.i("Parsher", "archivo:"+i);
			}
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 

		return archivos;
	}
	
}