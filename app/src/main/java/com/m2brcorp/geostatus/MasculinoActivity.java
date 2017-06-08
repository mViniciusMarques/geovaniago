package com.m2brcorp.geostatus;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
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
import com.m2brcorp.geostatus.Util.ReferenceFB;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import universum.studios.android.transition.WindowTransitions;

public class MasculinoActivity extends AppCompatActivity {

    public static final String FLAG_TO_EDITOR = "geovania@stefanini.com";
    private ProgressDialog progressDialog;
    Button limpando;
    Button limpo;
    protected RecyclerView recyclerView;
    protected List<Status> statuses;
    Activity activity = null;
    private ReferenceFB fire;
    Boolean isLimpando = false;
    Status status1;
    Button botaoTeste;
    Button botaoTeste3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masculino);
        activity = this;
        fire = new ReferenceFB();
        fire.getFirebaseInstance(this);
        statuses = new ArrayList<>();
        status1 = new Status();
        progressDialog = new ProgressDialog(this);

        getSupportActionBar().hide();

        limpando = (Button) findViewById(R.id.imageButton3);
        limpo = (Button) findViewById(R.id.imageButton2);

        recyclerView = (RecyclerView) findViewById(R.id.recyclevieww);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        botaoTeste = (Button) findViewById(R.id.button2);
        botaoTeste3 = (Button) findViewById(R.id.button3);

        getAutenticateUser();
        recuperarStatusBotao();
        blockButtons();
        recuperarStatus();
        //hselectorBotoes(isLimpando);

        testeTrigger();
        testeTrigger2();

        acionarBotaoLimpando();
        acionarBotaoLimpo();

    }

    private void acionarBotaoLimpo() {
        limpo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    fire.setReferencedSon("Banheiro");
                    fire.getFirebaseContextReference().child("Masculino1").setValue(Boolean.FALSE);
                    fire.getFirebaseContextReference().child("Masc").child("Status").push().setValue(persistirStatus("Banheiro Limpo"));
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
                if(event.getAction() == MotionEvent.ACTION_UP){
                    fire.setReferencedSon("Banheiro");
                    fire.getFirebaseContextReference().child("Masculino1").setValue(Boolean.TRUE);
                    fire.getFirebaseContextReference().child("Masc").child("Status").push().setValue(persistirStatus("Banheiro Limpando"));
                    isLimpando = Boolean.FALSE;
                    selectorBotoes(isLimpando);
                    return true;
                }
                return false;
            }
        });
    }

    public void testeTrigger(){
        botaoTeste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fire.getFirebaseContextReference().child("Masculino1").setValue(Boolean.FALSE);
            }
        });
    }
    public void testeTrigger2(){
        botaoTeste3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fire.getFirebaseContextReference().child("Masculino1").setValue(Boolean.TRUE);
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
        Log.i("SIMPSONS",user.getEmail());
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
        fire.getFirebaseContextReference().child("Masc").child("Status").addListenerForSingleValueEvent(new ValueEventListener() {
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
            limpo.setVisibility(View.GONE);
        }else{
            limpando.setVisibility(View.GONE);
            limpo.setVisibility(View.VISIBLE);
        }
    }

    public void recuperarStatusBotao(){
        fire.setReferencedSon("Banheiro");
        fire.getFirebaseContextReference().child("Masculino").addValueEventListener(new ValueEventListener() {
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

    public void gerarNotificacao(){
        long[] padrao = {0, 100, 1000,100,1000};
        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.men_p)
                        .setVibrate(padrao)
                        .setContentTitle("Atenção")
                        .setContentText("Alteração no status do banheiro masculino");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(00, builder.build());
    }

}
