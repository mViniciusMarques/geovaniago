package com.m2brcorp.geostatus;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.m2brcorp.geostatus.Adapter.StatusAdapter;
import com.m2brcorp.geostatus.Core.ParseAuthUtils;
import com.m2brcorp.geostatus.Domain.Status;
import com.m2brcorp.geostatus.Util.DataHoraUtils;
import com.m2brcorp.geostatus.Util.NetworkUtils;
import com.m2brcorp.geostatus.Util.ReferenceFB;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bolts.Continuation;
import bolts.Task;
import universum.studios.android.transition.WindowTransitions;

public class MasculinoActivity extends AppCompatActivity {

    public static final String FLAG_TO_EDITOR = "geovania@stefanini.com";
    private ProgressDialog progressDialog;
    protected RecyclerView recyclerView;
    protected List<Status> statuses;
    Activity activity = null;
    private ReferenceFB fire;
    Boolean isLimpando = false;
    Status status1;

    Button limpando;
    Button limpo;
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

        limpando = findViewById(R.id.imageButton3);
        limpo = findViewById(R.id.imageButton2);

        recyclerView = findViewById(R.id.recyclevieww);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        botaoTeste = findViewById(R.id.button2);
        botaoTeste3 = findViewById(R.id.button3);

        getAutenticateUser();
        recuperarStatusBotao();
       // blockButtons();
       // recuperarStatus();
        //hselectorBotoes(isLimpando);

      //  testeTrigger();
       // testeTrigger2();
        esconderEmbaixoDoTapete();

        acionarBotaoLimpando();
       // acionarBotaoLimpo();
        acionarAcaoBanheiroLimpando();
        obterMovimentacoesBanheiro();
        desabilitarBotoesParaUsuariosComuns();
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
                if (NetworkUtils.isOnline(v.getContext())) {
                    fire.getFirebaseContextReference().child("Masculino1").setValue(Boolean.FALSE);
                    fire.getFirebaseContextReference().child("Masc").child("Status").push().setValue(persistirStatus("Banheiro Limpo"));
                    isLimpando = Boolean.FALSE;
                    selectorBotoes(isLimpando);
                    recyclerView.invalidate();
                    recyclerView.setAdapter(new StatusAdapter(getApplicationContext(), statuses));
                    statuses.clear();
                    recuperarStatus();
                }else{
                    Toast.makeText(v.getContext(),"Verifique sua conexão com a internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void testeTrigger2(){
        botaoTeste3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.isOnline(v.getContext())) {
                    fire.getFirebaseContextReference().child("Masculino1").setValue(Boolean.TRUE);
                    fire.getFirebaseContextReference().child("Masc").child("Status").push().setValue(persistirStatus("Banheiro Limpando"));
                    isLimpando = Boolean.TRUE;
                    selectorBotoes(isLimpando);
                    recyclerView.invalidate();
                    recyclerView.setAdapter(new StatusAdapter(getApplicationContext(), statuses));
                    statuses.clear();
                    recuperarStatus();
                }else{
                    Toast.makeText(v.getContext(),"Verifique sua conexão com a internet", Toast.LENGTH_SHORT).show();
                }
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
        if (NetworkUtils.isOnline(this)) {
         return ParseAuthUtils.obterUsuarioLogado().getEmail();
        }
        return "";
    }

    public Boolean hasPermissionToEdit(){
        return getAutenticateUser().equalsIgnoreCase(FLAG_TO_EDITOR);
    }

    /*usuario geovania bloqueado temporariamente
            para estes botoes*/
    public void blockButtons(){
        if(!hasPermissionToEdit() || getAutenticateUser().equalsIgnoreCase("geovania@stefanini.com")){
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
                        String status = (String) ds.child("status").getValue();
                        String data = (String) ds.child("data").getValue();
                        String hora = (String) ds.child("hora").getValue();
                        inserirRegistroNaLista(status, data, hora);
                    progressDialog.dismiss();
                }
            }

            private void inserirRegistroNaLista(String status, String data, String hora) {
                statuses.add(new Status(status,data,hora));
                recyclerView.invalidate();
                recyclerView.setAdapter(new StatusAdapter(getApplicationContext(), statuses));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

   /* Metodo responsavel por alterar a visibilidade
    *intercalando os botões sobrepostos
    */
    public void selectorBotoes(Boolean isLimpando){
        recyclerView.setAdapter(new StatusAdapter(getApplicationContext(), statuses));
        if(isLimpando){
            limpando.setVisibility(View.VISIBLE);
            limpo.setVisibility(View.GONE);
        }else{
            limpando.setVisibility(View.GONE);
            limpo.setVisibility(View.VISIBLE);
        }
    }

    private void esconderEmbaixoDoTapete(){
        if(!getAutenticateUser().equalsIgnoreCase("geovania@stefanini.com")){
            botaoTeste.setVisibility(View.GONE);
            botaoTeste3.setVisibility(View.GONE);
        }
    }

    public void recuperarStatusBotao(){
        fire.setReferencedSon("Banheiro");
        fire.getFirebaseContextReference().child("Masculino1").addValueEventListener(new ValueEventListener() {
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
                new NotificationCompat.Builder(this)
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

    private void cadastrarAcaoBanheiro(String descricao, final boolean estado) {
        ParseObject banheiroParse = new ParseObject("Banheiro");
        banheiroParse.put("grupo", "M".trim());
        banheiroParse.put("descricao", descricao);
        banheiroParse.put("data", new Date());
        banheiroParse.put("ativo", false);
        banheiroParse.saveInBackground().onSuccess(new Continuation<Void, Object>() {
            @Override
            public Object then(Task<Void> task) {
                mudarEstadoBanheiro(estado);
                return null;
            }
        });
    }

    private void mudarEstadoBanheiro(final Boolean estado) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Estado");
        query.getInBackground("AJ2HmyA2Yq", new GetCallback<ParseObject>() {
            public void done(ParseObject gameScore, ParseException e) {
                if (e == null) {
                    gameScore.put("ativo", estado);
                    gameScore.saveInBackground();
                }
            }
        });
    }

    private void recuperarEstadoBanheiro() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Estado");
        query.getInBackground("AJ2HmyA2Yq", new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    selectorBotoes(object.getBoolean("ativo"));
                } else {
                   Log.e("Estado_dos_botoes", e.getMessage());
                }
            }
        });
    }

    private void desabilitarBotoesParaUsuariosComuns() {
        if(!ParseAuthUtils.obterUsuarioLogado().getEmail().equalsIgnoreCase(getString(R.string.EMAIL_ADMINISTRADOR))){
            botaoTeste.setVisibility(View.GONE);
            botaoTeste3.setVisibility(View.GONE);
        }
    }

    private void obterMovimentacoesBanheiro() {
        progressDialog.setMessage(getString(R.string.MSG_AGUARDE));
        progressDialog.setCancelable(false);
        progressDialog.show();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Banheiro");
          query.whereEqualTo("grupo", "M");
          query.orderByDescending("createdAt");
        query.setLimit(10);
        query.findInBackground(new FindCallback<ParseObject>() {
                 @Override
                 public void done(List<ParseObject> objects, ParseException e) {
                     for (ParseObject retorno : objects ) {
                         Status estadoAtual = new Status();
                         DateFormat dataFormatada = new SimpleDateFormat("dd/MM/yyyy");
                         DateFormat horaFormatada = new SimpleDateFormat("HH:mm:ss");


                         estadoAtual.setStatus(retorno.getString("descricao"));
                         estadoAtual.setData( dataFormatada.format(retorno.getCreatedAt()) );
                         estadoAtual.setHora( horaFormatada.format(retorno.getCreatedAt()) );

                         statuses.add(estadoAtual);
                         recyclerView.invalidate();
                         recyclerView.setAdapter(new StatusAdapter(getApplicationContext(), statuses));
                         Log.d("CHORAO", String.valueOf(retorno));
                     }
                     progressDialog.dismiss();
                 }
             });

    }

    private void acionarAcaoBanheiroLimpando() {
        botaoTeste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cadastrarAcaoBanheiro("Limpando banheiro masculino", false);
            }
        });
    }

    private void acionarAcaoBanheiroLimpo() {
        botaoTeste3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cadastrarAcaoBanheiro("Banheiro masculino limpo.", true);
            }
        });
    }

}
