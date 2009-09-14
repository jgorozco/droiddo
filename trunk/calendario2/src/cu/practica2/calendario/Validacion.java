package cu.practica2.calendario;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class Validacion extends Dialog implements android.view.View.OnClickListener {

	private Button aceptar;
	private TextView tituloUsuario;
	private TextView tituloPassword;
	private EditText usuario;
	private EditText password;
	private ImageView logo;
	private calendario padre;


	public Validacion (calendario calenda) {	
		super(calenda);padre=calenda;
	}


	protected void onStart() {
		super.onStart();
		setContentView(R.layout.validacion);
		getWindow().setFlags(4, 4);
		setTitle("Introduzca Usuario y password");
		aceptar =(Button) findViewById(R.id.aceptar);
		usuario  =(EditText) findViewById(R.id.usuario);
		tituloUsuario =(TextView) findViewById(R.id.tituloUsuario);
		password=(EditText) findViewById(R.id.password);
		tituloPassword=(TextView) findViewById(R.id.tituloPassword);
		aceptar.setOnClickListener(this);
		logo =(ImageView)findViewById(R.id.imagen);
	}




	public void onClick(View v) {
		padre.setUsuario(getUsuario());
		padre.setPassword(getPassword());
		this.cancel();
	}



	public String getUsuario(){
		return usuario.getText().toString();
	}

	public String getPassword(){
		return password.getText().toString();
	}	



}
