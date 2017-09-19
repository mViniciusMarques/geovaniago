package com.m2brcorp.geostatus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.m2brcorp.geostatus.Adapter.StatusAdapter;
import com.m2brcorp.geostatus.Domain.Status;
import com.m2brcorp.geostatus.Util.DataHoraUtils;
import com.m2brcorp.geostatus.Util.NetworkUtils;
import com.m2brcorp.geostatus.Util.ReferenceFB;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import universum.studios.android.transition.WindowTransitions;

public class FemininoActivity extends AppCompatActivity {

    public static final String FLAG_TO_EDITOR = "geovania@stefanini.com";
    private ProgressDialog progressDialog;
    ImageButton limpando;
    ImageButton limpo;
    protected RecyclerView recyclerView;
    protected List<Status> statuses;
    Activity activity = null;
    private ReferenceFB fire;
    Boolean isLimpando = false;
    Status status1;

    NetworkUtils networkUtils;
    private Button limpoMulheres;
    private Button limpandoMulheres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feminino);
        activity = this;

        getSupportActionBar().hide();

        fire = new ReferenceFB();
        statuses = new ArrayList<>();
        status1 = new Status();
        progressDialog = new ProgressDialog(this);

        limpando = (ImageButton) findViewById(R.id.imageButtonLimpando);
        limpo = (ImageButton) findViewById(R.id.imageButtonLiberado);

        limpoMulheres = (Button) findViewById(R.id.btn_limpo_mulheres);
        limpandoMulheres = (Button) findViewById(R.id.btn_limpando_mulheres);

        recyclerView = (RecyclerView) findViewById(R.id.recycleviewFeminino);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getAutenticateUser();
        recuperarStatusBotao();
        blockButtons();
        recuperarStatus();
        //hselectorBotoes(isLimpando);

       // acionarBotaoLimpando();
       // acionarBotaoLimpo();

        acionarLimpoMulheres();
        acionarLimpandoMulheres();
        esconderEmbaixoDoTapete();

    }

    private void acionarBotaoLimpo() {
        limpo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    fire.setReferencedSon("Banheiro");
                    fire.getFirebaseContextReference().child("Feminino").setValue(Boolean.FALSE);
                    fire.getFirebaseContextReference().child("Feminino1").setValue(Boolean.FALSE);
                    fire.getFirebaseContextReference().child("Fem").child("Status").push().setValue(persistirStatus("Banheiro Limpo"));
                    isLimpando = Boolean.TRUE;
                    selectorBotoes(isLimpando);
                    return true;
                }
                return false;
            }
        });
    }

    private void acionarBotaoLimpando() {
        limpando.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    fire.setReferencedSon("Banheiro");
                    fire.getFirebaseContextReference().child("Feminino").setValue(Boolean.TRUE);
                    fire.getFirebaseContextReference().child("Feminino1").setValue(Boolean.TRUE);
                    fire.getFirebaseContextReference().child("Fem").child("Status").push().setValue(persistirStatus("Banheiro Limpando"));
                    isLimpando = Boolean.FALSE;
                    selectorBotoes(isLimpando);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        WindowTransitions.SLIDE_TO_TOP.overrideStart(activity);
    }

    public Status persistirStatus(String tipoStatus){
        Status status;
        String data = DataHoraUtils.dataFormatada(new Date());
        String hora = DataHoraUtils.horaFormatada(new Date());
        status = new Status(tipoStatus,data,hora);
        return status;
    }

    private String getAutenticateUser(){
        FirebaseUser user = fire.getFirebaseAuthReference().getCurrentUser();
        Log.i("MARGE",user.getEmail());
        return user.getEmail();
    }

    public Boolean hasPermissionToEdit(){
        return getAutenticateUser().equalsIgnoreCase(FLAG_TO_EDITOR);
    }

    public void blockButtons(){
        if(!hasPermissionToEdit()){
            limpo.setEnabled(Boolean.FALSE);
            limpando.setEnabled(Boolean.FALSE);
        }
    }

    public void recuperarStatus(){
        progressDialog.setMessage("Por favor aguarde...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        fire.setReferencedSon("Banheiro");
        fire.getFirebaseContextReference().child("Fem").child("Status").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds :dataSnapshot.getChildren()) {
                    Log.i("GEMMA", ds.getValue().toString());
                    String status = (String) ds.child("status").getValue();
                    String data = (String) ds.child("data").getValue();
                    String hora = (String) ds.child("hora").getValue();
                    statuses.add(new Status(status,data,hora));
                    recyclerView.setAdapter(new StatusAdapter(getApplicationContext(), statuses));
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void selectorBotoes(Boolean isLimpando){
        if(isLimpando){
            limpando.setVisibility(View.VISIBLE);
            limpo.setVisibility(View.INVISIBLE);
        }else{
            limpando.setVisibility(View.INVISIBLE);
            limpo.setVisibility(View.VISIBLE);
        }
    }

    public void recuperarStatusBotao(){
        fire.setReferencedSon("Banheiro");
        fire.getFirebaseContextReference().child("Feminino1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("BAVARIO", dataSnapshot.getValue().toString());
                boolean b = (Boolean) dataSnapshot.getValue();
                selectorBotoes(b);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void esconderEmbaixoDoTapete(){
        if(!getAutenticateUser().equalsIgnoreCase("geovania@stefanini.com")){
            limpoMulheres.setVisibility(View.GONE);
            limpandoMulheres.setVisibility(View.GONE);
        }
    }

    private void acionarLimpoMulheres(){
        limpoMulheres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fire.getFirebaseContextReference().child("Feminino1").setValue(Boolean.FALSE);
                fire.getFirebaseContextReference().child("Fem").child("Status").push().setValue(persistirStatus("Banheiro Limpo"));

                recyclerView.invalidate();
                recyclerView.setAdapter(new StatusAdapter(getApplicationContext(), statuses));
                statuses.clear();
                recuperarStatus();

                isLimpando = Boolean.FALSE;
                selectorBotoes(isLimpando);
            }
        });
    }

    private void acionarLimpandoMulheres(){
        limpandoMulheres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fire.getFirebaseContextReference().child("Feminino1").setValue(Boolean.TRUE);
                fire.getFirebaseContextReference().child("Fem").child("Status").push().setValue(persistirStatus("Banheiro Limpando"));

                recyclerView.invalidate();
                recyclerView.setAdapter(new StatusAdapter(getApplicationContext(), statuses));
                statuses.clear();
                recuperarStatus();

                isLimpando = Boolean.TRUE;
                selectorBotoes(isLimpando);
            }
        });
    }
}
