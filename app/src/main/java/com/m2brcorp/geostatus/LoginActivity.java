package com.m2brcorp.geostatus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.m2brcorp.geostatus.Util.NetworkUtils;
import com.m2brcorp.geostatus.Util.ReferenceFB;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @NotEmpty
    @Email(message = "Preencha o campo E-mail ")
    EditText login;

    @Password(message = "Preencha o campo senha")
    EditText senha;
    ImageButton btnEntrar;
    private ReferenceFB fire;
    private ProgressDialog progressDialog;
    SharedPreferences.Editor editor;
    SharedPreferences pref;
    NetworkUtils networkUtils;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        this.fire = new ReferenceFB();
        networkUtils = new NetworkUtils();
        progressDialog = new ProgressDialog(this);

        getSupportActionBar().hide();

        pref = getApplicationContext().getSharedPreferences("Autenticator", MODE_PRIVATE);

        btnEntrar = (ImageButton) findViewById(R.id.imageButton45);
        login = (EditText) findViewById(R.id.editText2);
        senha  = (EditText) findViewById(R.id.editText3);
        recuperarSessao();
        doLogin();
    }

    public void doLogin() {
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Por favor aguarde...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                if (NetworkUtils.isOnline(v.getContext())){
                    if (!TextUtils.isEmpty(login.getText().toString().trim()) || !TextUtils.isEmpty(senha.getText().toString().trim())) {
                        persistirSessao();
                        fire.getFirebaseAuthReference().signInWithEmailAndPassword(login.getText().toString().trim(), senha.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            progressDialog.dismiss();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Senha ou usuários inválidos", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(v.getContext(), "Preencha os campos usuários e senha", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }else{
                    Toast.makeText(v.getContext(), "Sem conexaõ com a Internet",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }


    public void persistirSessao(){
        editor = pref.edit();
        editor.putBoolean("isLogado",true);
        editor.commit();
    }

    public void recuperarSessao(){
        Boolean isLogado = pref.getBoolean("isLogado", false);
        if(isLogado != null && isLogado == true){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        Log.i("mimimimimimimimi",isLogado.toString());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, LoginActivity.class));
    }
}
