package com.m2brcorp.geostatus;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fondesa.lyra.Lyra;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.m2brcorp.geostatus.Enum.GeneroEnum;
import com.m2brcorp.geostatus.Util.DataHoraUtils;
import com.m2brcorp.geostatus.Util.NetworkUtils;
import com.m2brcorp.geostatus.Util.ReferenceFB;
import com.parse.ParseUser;

import java.util.Date;

import universum.studios.android.transition.WindowTransitions;

public class MainActivity extends AppCompatActivity {

    public static final String FLAG_TO_EDITOR = "geovania@stefanini.com";

    private String generoSelecionado;
    private ProgressDialog progressDialog;

    TextView lblAviso;
    ImageButton masculino;
    ImageButton feminino;
    ImageButton sair;

    Spinner spinner;
    Button salvar;
    EditText campoAviso;
    TextView txtCafe;
    ImageView btnCafe;

    private ReferenceFB fire;
    Activity activity = null;
    SharedPreferences.Editor editor;
    SharedPreferences pref;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_inicial);
        Lyra.with(getApplication()).build();
        Lyra.instance().restoreState(this, savedInstanceState);
        this.fire = new ReferenceFB();
        progressDialog = new ProgressDialog(this);

        getSupportActionBar().hide();

        activity = this;
        pref = getApplicationContext().getSharedPreferences("Autenticator", MODE_PRIVATE);

        lblAviso = (TextView) findViewById(R.id.lblAviso);
        txtCafe = (TextView) findViewById(R.id.textView6);

        masculino = (ImageButton) findViewById(R.id.imageView3);
        feminino = (ImageButton) findViewById(R.id.imageView4);
        sair = (ImageButton) findViewById(R.id.imageButtonSair);
        btnCafe = (ImageView) findViewById(R.id.imageView8);

        spinner = (Spinner) findViewById(R.id.spinner);
        salvar = (Button) findViewById(R.id.button4);
        campoAviso = (EditText) findViewById(R.id.editText4);

        verificarVisibilidade();
        getGeneroDescricao();
        selectItem();
        persistirAviso();
        exibicaoCaixaAviso();

        navegarEntreTelas();
        atualizarStatusCafe();
        recuperarStatusTextoCafe();

    }

    private void navegarEntreTelas() {
        masculino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(),MasculinoActivity.class));
                WindowTransitions.SLIDE_TO_BOTTOM.overrideStart(activity);
            }
        });

        feminino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(),FemininoActivity.class));
                WindowTransitions.SLIDE_TO_BOTTOM.overrideStart(activity);
            }
        });

        sair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fire.getFirebaseAuthReference().signOut();
                encerrarSessao();
                startActivity(new Intent(v.getContext(),LoginActivity.class));
            }
        });
    }

    private void exibicaoCaixaAviso() {
        if(hasPermissionToEdit()){
            recuperarAvisoPorGenero();
        }else{
            recuperarAvisoUsuarioComum();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Boolean isLogado = pref.getBoolean("isLogado", false);
        if(isLogado != null && isLogado == true){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }

    public void encerrarSessao(){
        editor = pref.edit();
        editor.putBoolean("isLogado",false);
        editor.commit();
    }

    public void getGeneroDescricao(){
        spinner.setAdapter(new ArrayAdapter<String>(this,
                                                    R.layout.support_simple_spinner_dropdown_item,
                                                    GeneroEnum.getGeneroDescricao()));

    }

    private void verificarVisibilidade() {
        if(!hasPermissionToEdit()){
            spinner.setVisibility(View.GONE);
            salvar.setVisibility(View.GONE);
            campoAviso.setEnabled(Boolean.FALSE);
            lblAviso.setVisibility(View.VISIBLE);
            btnCafe.setEnabled(Boolean.FALSE);
        }else{
            lblAviso.setVisibility(View.GONE);
            txtCafe.setY(100F);
            btnCafe.setY(100F);
        }
    }

    private String getAutenticateUser(){
        if(NetworkUtils.isOnline(this)) {
//            FirebaseUser user = fire.getFirebaseAuthReference().getCurrentUser();
//            Log.i("SIMPSONS", user.getEmail());
//            return user.getEmail();

            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser != null) {
               return currentUser.getEmail();
            } else {
                // show the signup or login screen
            }

        }
        return "";
    }

    public Boolean hasPermissionToEdit(){
        return getAutenticateUser().equalsIgnoreCase(FLAG_TO_EDITOR);
    }

    //metodo resposavel por popular combo de genero
    public void selectItem(){
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(),parent.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();
                generoSelecionado = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getApplicationContext(),"Of Monster and men",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void persistirAviso(){
        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fire.setReferencedSon("Aviso");
                if(generoSelecionado.equalsIgnoreCase("feminino") && NetworkUtils.isOnline(v.getContext())) {
                    fire.getFirebaseContextReference().child("AvisoFeminino").setValue(campoAviso.getText().toString());
                    Toast.makeText(v.getContext(),"Meninas avisadas com sucesso",Toast.LENGTH_SHORT).show();
                }else if(generoSelecionado.equalsIgnoreCase("masculino") && NetworkUtils.isOnline(v.getContext())){
                    fire.getFirebaseContextReference().child("AvisoMasculino").setValue(campoAviso.getText().toString());
                    Toast.makeText(v.getContext(),"Meninos avisados com sucesso",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Você esqueceu de selecionar Homens ou Mulheres",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void recuperarAvisoPorGenero(){
        progressDialog.setMessage("Por favor aguarde...");
        progressDialog.show();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                generoSelecionado = parent.getItemAtPosition(position).toString();
                if(generoSelecionado.equalsIgnoreCase("feminino") && NetworkUtils.isOnline(view.getContext())) {
                    fire.setReferencedSon("Aviso");
                    fire.getFirebaseContextReference().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String s = dataSnapshot.child("AvisoFeminino").getValue().toString();
                            campoAviso.setText(s);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(),"Verifique sua conexão com a internet",Toast.LENGTH_SHORT).show();
                        }
                    });

                }else if(generoSelecionado.equalsIgnoreCase("masculino") && NetworkUtils.isOnline(view.getContext())){
                    fire.setReferencedSon("Aviso");
                    fire.getFirebaseContextReference().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String s = dataSnapshot.child("AvisoMasculino").getValue().toString();
                            campoAviso.setText(s);
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(),"Verifique sua conexão com a internet",Toast.LENGTH_SHORT).show();
                        }
                    });

                }else{
                    Toast.makeText(getApplicationContext(),"Masculino ou Feminino ?",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void recuperarAvisoUsuarioComum() {
        progressDialog.setMessage("Por favor aguarde...");
        progressDialog.show();

        if (getAutenticateUser().equalsIgnoreCase("mulheres@stefanini.com") && NetworkUtils.isOnline(this)) {
            fire.setReferencedSon("Aviso");
            fire.getFirebaseContextReference().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String s = dataSnapshot.child("AvisoFeminino").getValue().toString();
                    campoAviso.setText(s);
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(),"Verifique sua conexão com a internet",Toast.LENGTH_SHORT).show();
                }
            });

        } else if (getAutenticateUser().equals("homens@stefanini.com") && NetworkUtils.isOnline(this)) {
            fire.setReferencedSon("Aviso");
            fire.getFirebaseContextReference().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String s = dataSnapshot.child("AvisoMasculino").getValue().toString();
                    campoAviso.setText(s);
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(),"Verifique sua conexão com a internet",Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void atualizarStatusCafe(){
        btnCafe.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(NetworkUtils.isOnline(getApplicationContext())) {
                    fire.setReferencedSon("Cafe");
                    fire.getFirebaseContextReference().child("Status_Cafe").setValue(DataHoraUtils.dataHoraFormatada(new Date()));
                    txtCafe.setText(DataHoraUtils.dataHoraFormatada(new Date()));
                    return true;
                }else{
                    Toast.makeText(getApplicationContext(),"Sem conexão com a internet",Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        });
    }

    private void recuperarStatusTextoCafe(){
        fire.setReferencedSon("Cafe");
        fire.getFirebaseContextReference().child("Status_Cafe").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(NetworkUtils.isOnline(getApplicationContext())) {
                    txtCafe.setText(dataSnapshot.getValue().toString());
                }else{
                    Toast.makeText(getApplicationContext(),"Sem conexão com a internet",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


//    public void testezim(){
    //https://android-arsenal.com/details/1/5806
//        Sneaker.with(this)
//                .setTitle("Title", R.color.wallet_bright_foreground_holo_dark) // Title and title color
//                .setMessage("Meninas avisadas com sucesso.",) // Message and message color
//                .setDuration(4000) // Time duration to show
//                .autoHide(true) // Auto hide Sneaker view
//                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT) // Height of the Sneaker layout
//                .setIcon(R.drawable.warning_icon, R.color.wallet_bright_foreground_holo_dark, false) // Icon, icon tint color and circular icon view
//                .sneak(R.color.colorAccent);
//    }
}
