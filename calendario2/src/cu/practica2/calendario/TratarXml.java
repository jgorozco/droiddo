package cu.practica2.calendario;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.kxml2.io.KXmlSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import android.database.Cursor;
import android.util.Log;
public class TratarXml {

	private org.kxml2.kdom.Document peticion;
	private org.kxml2.kdom.Element tipo;
	private org.kxml2.kdom.Element eventos;
	private String fichero;

	TratarXml(String usuario,String pass){
		peticion = new org.kxml2.kdom.Document();
		tipo = peticion.createElement(null, "envio");
		peticion.setEncoding("utf-8");
		//añade el nombre
		org.kxml2.kdom.Element nombre= tipo.createElement(null, "user");
		// nombre.setName("nombre");
		//elem.setAttribute(null, "argu2", "argu3");
		//elem.setPrefix("aaa", "bbb");
		nombre.addChild(peticion.TEXT,usuario);
		tipo.addChild(peticion.ELEMENT, nombre);
		//añade el pass
		nombre= tipo.createElement(null, "password");

		nombre.addChild(peticion.TEXT,pass);
		tipo.addChild(peticion.ELEMENT, nombre);

		//añado el padre de eventos
		eventos= tipo.createElement(null, "eventos");     
	}


	public String getFichero(){
		return fichero;
	}

	public void setFichero(String nom){
		fichero=nom;
	}

	public void generarXml(BaseDatos bbdd,String[] consulta){
		Cursor cur = bbdd.getConsulta(consulta, "fecha");
		Log.d("XMl","cursor obtenido");
		int i=0;
		while(cur.moveToNext()){
			Log.d("XMl","elemento"+i);
			org.kxml2.kdom.Element evento= eventos.createElement(null, "evento");
			org.kxml2.kdom.Element dato= null;
			dato=evento.createElement(null, "id");				
			dato.addChild(dato.TEXT,String.valueOf(cur.getInt(0)));
			evento.addChild(dato.ELEMENT, dato);
			dato=evento.createElement(null, "fecha");		
			dato.addChild(dato.TEXT,String.valueOf(cur.getLong(1)));
			evento.addChild(dato.ELEMENT, dato);
			dato=evento.createElement(null, "titulo");				
			dato.addChild(dato.TEXT,cur.getString(2));
			evento.addChild(dato.ELEMENT, dato);
			dato=evento.createElement(null, "prioridad");				
			dato.addChild(dato.TEXT,String.valueOf(cur.getString(3)));
			evento.addChild(dato.ELEMENT, dato);
			dato=evento.createElement(null, "datos");				
			dato.addChild(dato.TEXT,cur.getString(4));
			evento.addChild(dato.ELEMENT, dato);			
			eventos.addChild(eventos.ELEMENT, evento);		
		}

		tipo.addChild(peticion.ELEMENT, eventos);
		bbdd.close();
	}

	public void GenerarArchivo(FileOutputStream fOut){
		try {
			peticion.addChild(peticion.ELEMENT, tipo);
			OutputStreamWriter osw = new OutputStreamWriter(fOut);
			//por ahora solo escribe bien el encabezado, lo deja en /data/data/cu.practica2.calendario/files
			XmlSerializer serializer = new KXmlSerializer(); 
			serializer.setOutput(osw);
			peticion.write(serializer); 
			osw.close();

		} catch (IOException e1) {
			// 
			e1.printStackTrace();
			Log.d("XML","no esta el archivo, ha sucedido alguna desgracia");
		}


	}

	public static Vector <String> tratarDocumento(String datos) {

		try {
			DocumentBuilderFactory constructor = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser;
			parser = constructor.newDocumentBuilder();
			Document doc = parser
			.parse(new InputSource(new StringReader(datos)));
			return	tratarNodo(doc, "principal");
		} catch (ParserConfigurationException e) {
			System.out.println(e.getMessage());return null;
		} catch (SAXException e) {
			System.out.println(e.getMessage());return null;
		} catch (IOException e) {
			System.out.println(e.getMessage());return null;
		}

	}

	/** trata el nodo, es una funcion recursiva, 
	 * @param nodo Node el nodo a tratar
	 * @param padre String el padre del nodo, para saber que tipo es
	 */
	public static Vector <String> tratarNodo(Node nodo, String padre) {
		Vector <String> consulta=null;
		// si tiene atributos, los tratamos, son tambien nodos
		if (nodo.hasAttributes()) {
			NamedNodeMap atributos = nodo.getAttributes();
			for (int i = 0; i < atributos.getLength(); i++)
				tratarNodo(atributos.item(i), "padre");
		}

		// recoje los nodos de antes, los de has atributes

		switch (nodo.getNodeType()) {

		case Node.ATTRIBUTE_NODE:
			System.out.print(" " + nodo.getNodeName() + "=\""
					+ nodo.getNodeValue() + "\"");
			break;

			// escribe los nodo tipo texto
		case Node.TEXT_NODE:

			break;

			// en teoria se podria meter un documento, pero bueno, lo uso
			// por para pasarle directamente un nodo de tipo documento
		case Node.DOCUMENT_NODE:
			System.out.println("entramos en docu");
			Document doc = (Document) nodo;
			tratarNodo(doc.getDocumentElement(), "principal");
			break;

			// este trata un elemento, y si tiene hijos los trata
		case Node.ELEMENT_NODE:
			String nombre = nodo.getNodeName();
			System.out.print("<" + nombre);
			System.out.println(">");
			NodeList hijos = nodo.getChildNodes();

			//lo ideal seria usar mysql

			if (nombre == "eventos")


				for (int i = 0; i < hijos.getLength(); i++) {

					Vector<String> resultado = tratarEventos(hijos.item(i));
					long feccha = new Long(resultado.get(1)).longValue();
					consulta.add( "INSERT INTO principal2 ('fecha','titulo,'prioridad,'datos')VALUES("
							+ feccha
							+ ","
							+ resultado.get(2)
							+ ","
							+ resultado.get(3) + "," + resultado.get(4) + ");");
					//	System.out.println(consulta);

				}
			else

				if (hijos != null) {
					for (int i = 0; i < hijos.getLength(); i++) {
						tratarNodo(hijos.item(i), nombre);

					}
				}
			System.out.println("</" + nombre + ">");

			break;

			/*----------------------------------------------------------------------------------------------	          
			 * 		  este tambien contiene texto, como TEXT node, lo que esta dentro no se interpreta
			 *       con el parser 
			 *---------------------------------------------------------------------------------------------- */

		case Node.CDATA_SECTION_NODE:
			System.out.print("nombre de nodo-" + nodo.getNodeName()
					+ "-valor de nodo-" + nodo.getNodeValue());
			break;
			/*----------------------------------------------------------------------------------------------
	 este tambien contiene texto
			 *----------------------------------------------------------------------------------------------*/
		case Node.PROCESSING_INSTRUCTION_NODE:
			System.out.print("procesing intruccion node" + nodo.getNodeValue());
			break;
			/*----------------------------------------------------------------------------------------------
			 *
			 *----------------------------------------------------------------------------------------------*/

		case Node.ENTITY_REFERENCE_NODE:
			System.out.print("entity reference node" + nodo.getNodeValue());
			break;
			/*----------------------------------------------------------------------------------------------
			 *
			 *----------------------------------------------------------------------------------------------*/

		case Node.DOCUMENT_TYPE_NODE:
			System.out.print("document type node" + nodo.getNodeValue());
			break;

		}
		return consulta;

	}


	/**tratarEventos devuelve los enventos en un vector
	 * @param nodo el nodo que recibe los eventos
	 * @return Vector<String> devuelve los strings de los eventos
	 */
	public static Vector<String> tratarEventos(Node nodo) {
		String id = "";
		String fecha = "";
		String titulo = "";
		String prioridad = "";
		String datos = "";
		Vector<String> vAux = new Vector();
		System.out.println(nodo.getNodeName());
		if (nodo.getNodeName() == "evento") {
			NodeList hijos = nodo.getChildNodes();
			String nm;
			for (int i = 0; i < hijos.getLength(); i++) {
				Node nod = hijos.item(i);
				nm = nod.getNodeName();

				NodeList nietos = nod.getChildNodes();

				if (nm == "id")
					id = nietos.item(0).getNodeValue();
				if (nm == "fecha")
					fecha = nietos.item(0).getNodeValue();
				if (nm == "titulo")
					titulo = nietos.item(0).getNodeValue();
				if (nm == "prioridad")
					prioridad = nietos.item(0).getNodeValue();
				if (nm == "datos")
					datos = nietos.item(0).getNodeValue();
				// System.out.println( id+"--TITULO:"+titulo+"
				// PRIORIDAD:"+prioridad+" FECHA:"+fecha+" DATOS:"+datos);
			}

		}
		vAux.add(id);
		vAux.add(fecha);
		vAux.add(titulo);
		vAux.add(prioridad);
		vAux.add(datos);
		return vAux;
	}

}