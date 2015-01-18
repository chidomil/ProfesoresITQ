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

public class XMLparserProfesor {
	private URL url;
	Context contextor;
	
	public XMLparserProfesor(String url,Context contexto ) {
		contextor=contexto;
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Profesor> parse() {
		
		ArrayList<Profesor> profesores = new ArrayList<Profesor>();
		Profesor profesoractual;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		try {
		
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(this.url.openConnection().getInputStream());
			 Element root = dom.getDocumentElement();
			
			NodeList items = root.getElementsByTagName("maestro"); 
			for (int i=0;i<items.getLength();i++){
				profesoractual = new Profesor();				
				Node item = items.item(i);
				NodeList properties = item.getChildNodes();
				for (int j=0;j<properties.getLength();j++){
					Node property = properties.item(j);
					String name = property.getNodeName();
					if (name.equalsIgnoreCase("id")){
						profesoractual.setId(property.getFirstChild().getNodeValue());
					}else if (name.equalsIgnoreCase("nombre")){
						profesoractual.setNombre(property.getFirstChild().getNodeValue());
					}else if (name.equalsIgnoreCase("foto")){
					profesoractual.setFoto(property.getFirstChild().getNodeValue());
					}else if (name.equalsIgnoreCase("karma")){
						profesoractual.setKarma(property.getFirstChild().getNodeValue());
						}
					
				}
				profesores.add(profesoractual);
				Log.i("Parsher", "profesor:"+i);
			}
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 

		return profesores;
	}
	
}