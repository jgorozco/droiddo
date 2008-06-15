package cu.practica2.calendario;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Vector;

import android.util.Log;

public class transmision {
	
	private byte[] IpServ;
	private String nombreServ;
	private int puerto;
	private InetAddress Servidor;
	private DatagramSocket sockt;
	
transmision(int a,int b,int c,int d,int puer){
	setIpAddress(a, b, c, d);
	puerto=puer;
	try {
		Servidor=InetAddress.getByAddress(IpServ);
		nombreServ=Servidor.getHostName();
		   sockt = new DatagramSocket();
	} catch (UnknownHostException e) {Log.d("RED",e.getMessage());} 
	catch (SocketException e) {Log.d("RED",e.getMessage());}
}	

transmision(String nomSer,int puer){
	puerto=puer;
	try {
		nombreServ=nomSer;
		Servidor=InetAddress.getByName(nomSer);
		IpServ=Servidor.getAddress();
        sockt = new DatagramSocket();
	} catch (UnknownHostException e) {Log.d("RED",e.getMessage());} 
	catch (SocketException e) {Log.d("RED",e.getMessage());}
}		

public boolean prueba(){
	try {
		return Servidor.isReachable(5);
	} catch (IOException e) {
         return false;
	}
	
}


public void transmitir(FileInputStream fInt,int tamanio){
	
	try {
		 byte[] datos= new byte[tamanio];
		 fInt.read(datos);
		 fInt.close();
		 DatagramPacket enviar = new DatagramPacket(datos,datos.length, Servidor, puerto );
      
	     sockt.send( enviar );
         sockt.close();
	  } catch (IOException e) {Log.d("RED",e.getMessage());	}
    
    
     //creo un paquete datagrama de un tamaño
	
}

public Vector <String> recibir(int taman){
  byte datos[] = new byte[taman];  
 DatagramPacket recibirPaquete = new DatagramPacket( datos, datos.length );
 String linea = new String(recibirPaquete.getData(),0, recibirPaquete.getLength() );
 return TratarXml.tratarDocumento(linea);
} 	     


public String getHostName(){return nombreServ;}
public void setHostName(String d){nombreServ=d;}
public byte[] getIpServ(){return IpServ;}
public void setIpServ(byte[] d){IpServ=d;}
public int getPort(){return puerto;}
public void setIpAddress(int a,int b,int c,int d){
	 byte[] aux={(byte) a,(byte) b,(byte) c,(byte) d};
    IpServ=aux;         
 }

}
