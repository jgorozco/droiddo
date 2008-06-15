package cu.practica2.calendario;

import java.io.FileNotFoundException;
import java.util.Calendar;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BaseDatos {
    private SQLiteDatabase baseDatos = null;
	private String nombreDb;
	
    BaseDatos(SQLiteDatabase bD){
    nombreDb=bD.getPath();	
    baseDatos =bD;
    
    try{
    	  String crear = "CREATE TABLE IF NOT EXISTS principal2 (id INTEGER PRIMARY KEY AUTOINCREMENT,fecha INTEGER, nombre VARCHAR,prioridad VARCHAR,datos VARCHAR);";
    	  Log.d("SQL","crear");  
    	  baseDatos.execSQL(crear);
    	  Log.d("SQL","cerrar"); 
    	  baseDatos.close();
    }catch(SQLException sq){
    	Log.d("SQL","error sql-"+sq.getMessage());
    	}
	}
	
   public SQLiteDatabase getDb(){
	   return baseDatos;
   }
    
	
	
    public boolean inserBase(Calendar c,String[] cositas){
      try{
    	Log.d("SQL","abrir");
    	baseDatos = SQLiteDatabase.open(nombreDb, null);	
    	Log.d("SQL","añadir:"+cositas[0]+"',"+c.getTimeInMillis()+","+cositas[1]+",'"+cositas[2]);
		baseDatos.execSQL("INSERT INTO principal2 (nombre,fecha,prioridad,datos)VALUES('"+cositas[0]+"',"+c.getTimeInMillis()+","+cositas[1]+",'"+cositas[2]+"');");
		Log.d("SQL","cerrar");
		baseDatos.close();
    	return true;
    	}catch(SQLException e){
    	   Log.d("SQL", e.getMessage());
    	   return false;
    	   }
        }
    public boolean inserBase(Calendar c,String cositas){
        try{
      	Log.d("SQL","abrir");
      	baseDatos = SQLiteDatabase.open(nombreDb, null);	
      	Log.d("SQL",cositas);
  		baseDatos.execSQL(cositas);
  		Log.d("SQL","cerrar");
  		baseDatos.close();
      	return true;
      	}catch(SQLException e){
      	   Log.d("SQL", e.getMessage());
      	   return false;
      	   }
          }
    public Cursor getConsulta(String[] consult,String Colocacion){
    	Log.d("SQL","abrir");
    	baseDatos = SQLiteDatabase.open(nombreDb, null);
    	Log.d("SQL","consultar");
    	Cursor cur =baseDatos.query("principal2", consult, null, null, null, null, Colocacion);
    	Log.d("SQL",cur.getColumnName(2));
    	
    	 return   	cur;
    }
    
    public void close(){
    	baseDatos.close();
    }
    
}
