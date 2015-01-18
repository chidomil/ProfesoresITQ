package com.viga.profesoresitq;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.util.Log;

public class XMLparserStandar {
	private URL url;
	Context contextor;
	
	public XMLparserStandar(String url,Context contexto ) {
		contextor=contexto;
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public String parse() {
		
		String estatus = new String();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		try {
		
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(this.url.openConnection().getInputStream());
			 Element root = dom.getDocumentElement();
			
			NodeList items = root.getElementsByTagName("padre"); 
			for (int i=0;i<items.getLength();i++){
							
				Node item = items.item(i);
				NodeList properties = item.getChildNodes();
				for (int j=0;j<properties.getLength();j++){
					Node property = properties.item(j);
					String name = property.getNodeName();
					if (name.equalsIgnoreCase("status")){
						estatus = property.getFirstChild().getNodeValue();
					}
				}
				Log.i("Parsher", "status:"+i);
			}
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 

		return estatus;
	}
	
}