package cu.practica2.calendario;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class dialogoConfig extends Dialog implements android.view.View.OnClickListener {

	private calendario papi;
	private EditText primer;
	private EditText segun;
	private EditText tercer;
	private EditText ulti;
	private EditText nservidor;
	private EditText usrConf;
	private EditText passConf;
	private Button acept;
	private Button cancel;
	private Button sync;
	
	public dialogoConfig(calendario calend) {
		super(calend);
		papi=calend;
	}

	public void onClick(View arg0) {
	}

	  protected void onStart() {
		  super.onStart();
	        setContentView(R.layout.config);
	        getWindow().setFlags(4, 4);
	        setTitle("añadir datos");
		  	 primer = (EditText) findViewById(R.id.primero);
		 segun = (EditText) findViewById(R.id.segundo);
		 tercer = (EditText) findViewById(R.id.tercero);
		 ulti = (EditText) findViewById(R.id.cuarto);
		 nservidor =(EditText) findViewById(R.id.nservidor);
		 usrConf = (EditText) findViewById(R.id.userConf);
		 passConf = (EditText) findViewById(R.id.passConf);
		 acept =(Button) findViewById(R.id.aceptConf);
		 cancel =(Button) findViewById(R.id.cancelConf);
		 sync =(Button) findViewById(R.id.sync);
		 
		 nservidor.setText(papi.getEnvio().getHostName());
		 primer.setText(String.valueOf((byte)(papi.getEnvio().getIpServ()[0])));
		 segun.setText(String.valueOf((byte)(papi.getEnvio().getIpServ()[1])));
		 tercer.setText(String.valueOf((byte)(papi.getEnvio().getIpServ()[2])));
		 ulti.setText(String.valueOf((byte)(papi.getEnvio().getIpServ()[3])));
		 usrConf.setText(papi.getUser());
		 passConf.setText(papi.getPass());
         
		 
		 
		 cancel.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                 dialogoConfig.this.dismiss();
                 }
         });
		 
		 sync.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                 papi.setEnvio(new transmision(String.valueOf(nservidor.getText()),papi.getEnvio().getPort()));
                 primer.setText(String.valueOf((int)(papi.getEnvio().getIpServ()[0])));
        		 segun.setText(String.valueOf((int)(papi.getEnvio().getIpServ()[1])));
        		 tercer.setText(String.valueOf((int)(papi.getEnvio().getIpServ()[2])));
        		 ulti.setText(String.valueOf((int)(papi.getEnvio().getIpServ()[3])));
        		 if (papi.getEnvio().prueba()==false) {
        			                                                   Dialog d =new Dialog(papi);
        			                                                   
        			                                                   d.setTitle("error");
        		                                                        d.show();
        		                                                         }
                 }
         });
         
         acept.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                     
                if ((validar(primer.getText().toString())==true)&
            			 (validar(segun.getText().toString())==true)&
            			 (validar(tercer.getText().toString())==true)&
            			 (validar(ulti.getText().toString())==true)&
            			 (usrConf.getText().length()>0)&
            			 (passConf.getText().length()>0)
            			 ){
                	
                	papi.getEnvio().setIpAddress(Integer.parseInt(primer.getText().toString()),
                    		                   Integer.parseInt(primer.getText().toString()), 
                    		                   Integer.parseInt(primer.getText().toString()), 
                    		                   Integer.parseInt(primer.getText().toString()));
                     papi.setUser(usrConf.getText().toString());
                     papi.setPass(passConf.getText().toString());
                    dialogoConfig.this.dismiss();
                     }
                else {
                	Dialog d = new Dialog(papi);
                	d.setTitle("faltan datos por introducir");
                	d.show();
                }
                 }
         });
         
	  }
	  
         public boolean validar (String dato){
        	 
        	 int num = Integer.parseInt(dato);
        	 if ((num<255)&(num>0))
        		   return true;
        	 else return false;
             }
         
         
	  }



