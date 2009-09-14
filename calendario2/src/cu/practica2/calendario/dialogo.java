package cu.practica2.calendario;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;


public class dialogo extends Dialog implements android.view.View.OnClickListener {

	private Button cancelar;
	private Button aceptar;
	private TextView nombre;
	private TextView datos;
	private Spinner prioridad;
	private calendario papi;
	private int fin=0;
	public dialogo(calendario calenda) {

		super(calenda);papi=calenda;
	}


	public void onClick(View arg0) {

	}

	protected void onStart() {
		super.onStart();
		setContentView(R.layout.dialogo1);
		getWindow().setFlags(4, 4);
		setTitle("añadir datos");

		cancelar =(Button) findViewById(R.id.cancelar);
		aceptar =(Button) findViewById(R.id.aceptar);
		nombre =(TextView) findViewById(R.id.nombre);
		datos =(TextView) findViewById(R.id.datos);
		prioridad =(Spinner) findViewById(R.id.prioridad);

		prioridad.setAdapter(papi.listaSpiner);

		datos.setText("");
		nombre.setText("");
		cancelar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				fin=2; 

				dialogo.this.dismiss();

			}
		});
		aceptar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				fin=1; 
				papi.setDatos(getDatos());
				papi.setNombre(getNombre());
				papi.setPrioridad(getPrioridad());
				papi.insertabla();
				papi.updateDisplay();
				dialogo.this.cancel();
			}
		});
	}


	public int isEnd(){
		return fin;
	}

	public String getNombre(){
		return nombre.getText().toString();
	}

	public String getDatos(){
		return datos.getText().toString();
	}	  

	public int getPrioridad(){
		return prioridad.getSelectedItemPosition();
	}

}