package com.m2brcorp.geostatus.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.m2brcorp.geostatus.Domain.Status;
import com.m2brcorp.geostatus.R;

import java.util.List;

/**
 * Created by vinic on 04/05/2017.
 */

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusViewHolder>{

    private final Context context;
    private final List<Status> statuses;

    public StatusAdapter(Context context, List<Status> statuses) {
        this.context = context;
        this.statuses = statuses;
    }


    //Cria o ViewHolder
    //Define o layout
    @Override
    public StatusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.status_lista_layout, parent, false);
        return new StatusViewHolder(view);
    }

    //Recebe o position e atualiza as views no ViewHolder
    @Override
    public void onBindViewHolder(StatusViewHolder holder, int position) {
        Status status = statuses.get(position);

        holder.statusLimpeza.setText(status.getStatus() );
        holder.dataLimpeza.setText(status.getData());
        holder.horaLimpeza.setText(status.getHora());
    }

    @Override
    public int getItemCount() {
        return this.statuses != null ? this.statuses.size() : 0;
    }

    public static class StatusViewHolder extends RecyclerView.ViewHolder {

        public TextView statusLimpeza;
        public TextView dataLimpeza;
        public TextView horaLimpeza;
        public TextView as_;

        public StatusViewHolder(View statusView) {
            super(statusView);

//            nome = (TextView) itemView.findViewById(R.id.nome);
//            partido = (TextView) itemView.findViewById(R.id.partido);
//            numero = (TextView) itemView.findViewById(R.id.numero);
//            imgPerfil = (ImageView) itemView.findViewById(R.id.imageView);

            statusLimpeza = (TextView) statusView.findViewById(R.id.statusLimpeza);
            dataLimpeza = (TextView) statusView.findViewById(R.id.dataLimpeza);
            horaLimpeza = (TextView) statusView.findViewById(R.id.horaLimpeza);

        }
    }
}
