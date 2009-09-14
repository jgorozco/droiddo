package cu.practica2.calendario;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Layout.Alignment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TableRow.LayoutParams;
import android.widget.TimePicker.OnTimeChangedListener;
public class calendario extends Activity {
 
	/** Called when the activity is first created. */
    private TableLayout tabla;
    private dialogo dial;
    private dialogoConfig conf;
    private Validacion validacion;
    //private Button cancelar;
    /*los del cuadro de dialogo*/
    // date and time
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    private int mSecond;
    private String datos;
    private String nombre;
    private String user="defecto";
    private String pass="defecto";
    private int prioridad;
    private TratarXml docu;
    private BaseDatos bbdd;
    private int Ipser[]={ 138,100,217,38};

    
    private transmision envio = new transmision(Ipser[0],Ipser[1],Ipser[2],Ipser[3],5000);
    private Calendar c = Calendar.getInstance();
    
    public  ArrayAdapter listaSpiner = new ArrayAdapter(this,
        android.R.layout.simple_spinner_dropdown_item,
            new String[] { "muy baja","baja", "media", "alta","muy alta" }); 
    
    private int colocacion=1;
    private int otr =0;
    
    
    
    @Override
    public void onCreate(Bundle icicle)  {
        super.onCreate(icicle);
        setContentView(R.layout.main);


		
//aqui delclaramos todas las cosas que usamos mejor        
        try {
		//	bbdd=new BaseDatos(this.createDatabase("bdatos.sql", 1, MODE_PRIVATE, null));
        	bbdd=new BaseDatos(this.openOrCreateDatabase("bdatos.sql",  MODE_PRIVATE, null));
        } catch (FileNotFoundException e1) {Log.d("SQL",e1.getMessage());}
        tabla =(TableLayout)findViewById(R.id.tabla);
        Button pickDate = (Button) findViewById(R.id.boton1);
        Button pickTime = (Button) findViewById(R.id.boton2);
        Button configurar = (Button)findViewById(R.id.configurar);
        Button porNombre = (Button)findViewById(R.id.pornombre);
        Button porPrioridad = (Button)findViewById(R.id.porprioridad);
        Button porFecha = (Button)findViewById(R.id.porfecha);
        updateDisplay();
        configurar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            configuracioon();
            }
        });
        
        
        //validacion del usuario
        validacion();

               
        porFecha.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            poner(1);                        
            }
        });

        porNombre.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            poner(3);
            }
        });
        
        porPrioridad.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            poner(2);
            }
        });
        
        //      clicker del boton1       
        pickDate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                new DatePickerDialog(calendario.this,
                        mDateSetListener,
                        mYear, mMonth, mDay, Calendar.MONDAY).show();
                  otr=0;
            }
        });
        
//cliker del boton2
       pickTime.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
            	EnviarArchivo();
            	//recibirFichero();
            }
        });


       //fin de la aplicacion ;)


    }
//usamos los milisegundos deeeee clave para la tabla
    
    ///funcion que actualiza la gui, esta actualizando datos ;) ;)
    public void updateDisplay() {
       
    	Calendar c =Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        mSecond = c.get(Calendar.SECOND);
        c.set(mYear, mMonth, mDay, mHour, mMinute,mSecond);
    	
    }
    //funcion que usa la base de datos
    private void inserBase(){    	
    	 c.set(mYear, mMonth, mDay, mHour, mMinute,Calendar.SECOND);
         String[] cositas ={nombre,String.valueOf(prioridad),datos};
    	 bbdd.inserBase(c, cositas);
		}
 

  //añade a una tabla con unsolo elemento devuelve falso si no hay nada en la linea  
 @SuppressWarnings("deprecation")
public boolean insertabla(){
	 //lo paso a texto lo de la 2º linea
	 CharSequence linea =nombre;

     
	 if ((linea.length()<=0)&(otr==0)) return false;
	 else {
	     //creo un tablerow, una fila de la tabla
		
		 if (otr==0)inserBase();
		// if (otr==1)inserBase();
		//inserBase();
		 tabla.removeAllViews();
	 String[] consulta = {"id","fecha","nombre","prioridad","datos"};
		 String coloc ="";
		 if (colocacion==1) coloc="fecha";
		 if (colocacion==2) coloc="prioridad";
		 if (colocacion==3) coloc="nombre";
		 //aqui me genero el cursor con la consulta que me interesa, lo organizo por fecha\/
		// Cursor cur = baseDatos.query("principal2", consulta, null, null, null, null, coloc);
		 Cursor cur = bbdd.getConsulta(consulta, coloc);
		Log.d("TBL","el cursor esta bien");
		 
		 //trato el cursor, lo meto en variables
		int ide=0;
		
		 while(cur.moveToNext()){
			  ide =cur.getInt(0);
			 c.setTimeInMillis(cur.getLong(1));
			String nomb = cur.getString(2);
			String prior = cur.getString(3);
			final String dats = cur.getString(4);
		
			//pongo la caja de texto a vacio
		
			 GridView  fil = new GridView(this);
			 
			 fil.setLayoutParams(new LayoutParams(
                     LayoutParams.FILL_PARENT,
                     LayoutParams.WRAP_CONTENT));
			 
	         TableRow tr = new TableRow(this);
	         //los parametos para q sea toda la columna
	         tr.setLayoutParams(new LayoutParams(
	                        LayoutParams.FILL_PARENT,
	                        LayoutParams.WRAP_CONTENT));
	         //un textview
	         
	         TextView b = new TextView(this);
	         Date fec= c.getTime();
	         String ttxt = pad(fec.getDate())+"/"+pad(fec.getMonth()+1)+"  "+pad(fec.getHours())+" : "+pad(fec.getMinutes());
	       //  b.setText(fec.toGMTString());
	         b.setText(ttxt);
	         b.setTextSize(12);
	         b.setWidth(90);
	         //b.setAlignment(Alignment.ALIGN_CENTER);
	         //que ocupa toda la linea
	         b.setLayoutParams(new LayoutParams(
	                 LayoutParams.FILL_PARENT,
	                LayoutParams.WRAP_CONTENT
	                ));
           //lo añado a el tablerow
	         tr.addView(b,0);
	       //  fil.addView(b, 0);
	         b = new TextView(this);
	         b.setTextSize(12);
	         int prior1 = new Integer(prior).intValue();
	         if (prior1==0)b.setText("muy baja");else
	         if (prior1==1)b.setText("baja");else
	         if (prior1==2)b.setText("media");else
	         if (prior1==3)b.setText("alta");else
	         if (prior1==4)b.setText("muy alta");else b.setText(prior); 
	         b.setWidth(58);
	        // b.setAlignment(Alignment.ALIGN_CENTER);
	         //que ocupa toda la linea
	         b.setLayoutParams(new LayoutParams(
	                 LayoutParams.FILL_PARENT,
	                LayoutParams.WRAP_CONTENT
	                ));
	         
            //genero el cuadro y lo añado a la lista
	         tr.addView(b,1);
	    
	         b = new TextView(this);
	         //b.setAl(Alignment.ALIGN_OPPOSITE);
	         b.setWidth(145);
	         b.setTextSize(12);
	         b.setText(nomb);
	         //que ocupa toda la linea
	         b.setLayoutParams(new LayoutParams(
	                 LayoutParams.FILL_PARENT,
	                LayoutParams.WRAP_CONTENT
	                ));
	         
            //genero el cuadro y lo añado a la lista
	         tr.addView(b,2);
	         
	         tr.setClickable(true);
	         tr.setOnClickListener(new View.OnClickListener() {
	             public void onClick(View v) {
	            	    Dialog d = new Dialog(calendario.this);
		                 d.setCancelable(true);
		                 TextView ba = new TextView(calendario.this);
		                 ba.setText(dats);
		                 d.setTitle("contenido");
		                 d.addContentView(ba, new LayoutParams(
		                 LayoutParams.FILL_PARENT,
		                LayoutParams.WRAP_CONTENT
		                ));
		               d.show();
	                 }
	         });
	         //añado el tablerow
	         //fil.addView(b,1);
	         //tr.addView(fil);
	         tabla.addView(tr,new TableLayout.LayoutParams(
	                 LayoutParams.FILL_PARENT,
	                 LayoutParams.WRAP_CONTENT));
	         
	         
	         
		}
		bbdd.close();
    
	 return true;
	 }
 }
    
 //funcion que genera el xml
 //esta todavia en pruebas ;)
 public  void generarXml(){
	    Log.d("GEN","fichero"+user+pass+" generado");
	    pass="pepito";
		docu = new TratarXml(user,pass);
		Log.d("GEN","fichero generado");
		String[] consulta = {"id","fecha","nombre","prioridad","datos"};
		docu.generarXml(bbdd, consulta);
		Log.d("GEN","fichero fin de generado");
		FileOutputStream fSalida;
		try {
			fSalida = openFileOutput("f.xml", MODE_WORLD_WRITEABLE);
			docu.setFichero("f.xml");
			docu.GenerarArchivo(fSalida);
		} catch (FileNotFoundException e) {
			Log.d("FIC",e.getMessage());
		    }
		
        }

 //   funcion que envia por sockets el archivo
 
 public void EnviarArchivo() {
	 try {
		generarXml();
		Log.d("XML","fichero generado");
		FileInputStream fInt = openFileInput(docu.getFichero());
		envio.transmitir(fInt, 5000);
	 } catch (FileNotFoundException e) {Log.d("FIC",e.getMessage());}
 }
 
 public void recibirFichero(){
	 Vector <String> consultas = envio.recibir(5000);
	 for(int i=0;i<=consultas.size();i++)
	 bbdd.inserBase(c, consultas.get(i));
	 bbdd.close();	 
	 insertabla();
 }

//listener de el de la fecha
    
    private OnDateChangedListener mDateSetListener =
            new OnDateChangedListener() {

                public void dateSet(DatePicker view, int year, int monthOfYear,
                        int dayOfMonth) {
                    setMYear(year);
                    setMMonth(monthOfYear);
                    setMDay(dayOfMonth);
                    otr=0;
                    new TimePickerDialog(calendario.this.getBaseContext(),
                    		mTimeSetListener,
                    		"que hora",mHour,mMinute,false);
                    /*new TimePickerDialog(calendario.this,
                            mTimeSetListener, "elige la hora",
                            mHour, mMinute, false).show();
                    */
                }

				@Override
				public void onDateChanged(DatePicker view, int year,
						int monthOfYear, int dayOfMonth) {
					// TODO Auto-generated method stub
					
				}
            };
            
//listener de el de la hora
private OnTimeChangedListener mTimeSetListener =
            new OnTimeChangedListener() {

public void timeSet(TimePicker view, int hourOfDay, int minute) {
                     setMHour(hourOfDay);
                    setMMinute(minute);
                   setMSecond(c.get(Calendar.SECOND));
                  //  inserBase();
                    otr=0;
                    dialogo1();
                    
               
                }

@Override
public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
	// TODO Auto-generated method stub
	
}
            };

            // pasamos a string
 private void poner (int num){
            	colocacion=num;
            	otr=1;
               insertabla();
             	
            }
            
private void configuracioon(){
              conf = new dialogoConfig(this);   
              conf.show();
        	  
          }  
            
private void dialogo1(){
        	  dial = new dialogo(this);
        	  dial.show();
          }  

 private static String pad(long c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);      
    }
 
 private void validacion(){
	  validacion = new Validacion(this);
	  validacion.show();    	  
 }
 
 /*metodos get y set*/
 public  String getDatos(){
	 return datos;
 }
 
 public void setDatos(String d){
	 datos=d;
 }
 public  String getNombre(){
	 return nombre;
 }
 
 public void setNombre(String d){
	 nombre=d;
 }
 
 public  String getUser(){
	 return user;
 }
 
 public void setUser(String d){
	 user=d;
 }
 
 public  String getPass(){
	 return pass;
 }
 
 public void setPass(String d){
	 pass=d;
 }
 
 public  int getPrioridad(){
	 return prioridad;
 }
 
 public void setPrioridad(int d){
	 prioridad=d;
 }
 
 public  TratarXml getDocu(){
	 return docu;
 }
 
 public void setDocu(TratarXml d){
	 docu=d;
 }
 
 public  BaseDatos getBbdd(){
	 return bbdd;
 }
 
 public void setBbdd(BaseDatos d){
	 bbdd=d;
 }
 
 public  transmision getEnvio(){
	 return envio;
 }
 
 public void setEnvio(transmision d){
	 envio=d;
 }
 
 public  int getMYear(){
	 return mYear;
 }
 
 public void setMYear(int d){
	 mYear=d;
 }
 
 public  int getMMonth(){
	 return mMonth;
 }
 
 public void setMMonth(int d){
	 mMonth=d;
 }
 
 public  int getMDay(){
	 return mDay;
 }
 
 public void setMDay(int d){
	 mDay=d;
 }
 
 public  int getMHour(){
	 return mHour;
 }
 
 public void setMHour(int d){
	 mHour=d;
 }
 
 public  int getMMinute(){
	 return mMinute;
 }
 
 public void setMMinute(int d){
	 mMinute=d;
 }
 
 public  int getMSecond(){
	 return mSecond;
 }
 
 public void setMSecond(int d){
	 mSecond=d;
 }
 
 public void setUsuario(String d){
	 user = d;
 }
 
 public void setPassword(String d){
	 pass = d;
 } 
 
 public int corrector (int i){
	 if (i>=0)return i;
	 else return 256+i;
 }
 

 
}