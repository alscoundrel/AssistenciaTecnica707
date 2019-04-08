package com.dlsra.assistenciatecnica707;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.dlsra.assistenciatecnica707.TarefasDiaFragmentoTarefas.OnListFragmentInteractionListener;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * {@link RecyclerView.Adapter} that can display a and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class TarefasDiaAdaptadorTarefas extends RecyclerView.Adapter<TarefasDiaAdaptadorTarefas.ViewHolder>{

    private final ArrayList<TarefaItemDetalhes> mValores;
    private final OnListFragmentInteractionListener mLista;
    private Context mContexto;
    private int _id_utilizador = 0;
    private int _n_utilizador = 0;

    public TarefasDiaAdaptadorTarefas(ArrayList<TarefaItemDetalhes> items, OnListFragmentInteractionListener listener, Context contexto, int id_utilizador, int n_utilizador) {
        mValores = items;
        mLista = listener;
        mContexto = contexto;
        _id_utilizador = id_utilizador;
        _n_utilizador  = n_utilizador;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tarefas_dia_fragmento_tarefas, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        int n_obra = mValores.get(position).getN_obra();
        String cod_obra = mValores.get(position).getCod_obra();
        int ano_obra = mValores.get(position).getAno_obra();
        String nota = mValores.get(position).getNota();
        int nCaracteres = nota.length();
        int maximo = 140;
        if(maximo < nCaracteres){ nota = nota.substring(0, maximo-2)+"...";}
        holder.mItem = mValores.get(position);
        holder.mTVHora.setText(mValores.get(position).getHora());
        holder.mTVProcesso.setText(0<n_obra?n_obra+" "+cod_obra+" "+ano_obra:"");
        holder.mTVLocalidade.setText(mValores.get(position).getLocalidade());
        holder.mTVNota.setText(nota);


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mLista) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mLista.onListFragmentInteraction(holder.mItem);
                }
                int n_obra      = mValores.get(position).getN_obra();
                String cod_obra = mValores.get(position).getCod_obra();
                int ano_obra    = mValores.get(position).getAno_obra();
                int id_tarefa   = mValores.get(position).get_id();

                //carrega os argumentos para passar
                Bundle args = new Bundle();
                args.putInt("_id_utilizador", _id_utilizador);
                args.putInt("_n_utilizador", _n_utilizador);
                args.putInt("_n_obra", n_obra);
                args.putInt("_ano_obra", ano_obra);
                args.putString("_cod_obra", cod_obra);
                args.putInt("_id_tarefa", id_tarefa);

                //abre nova actividade....
                mContexto.startActivity(new Intent(mContexto, ProcessoJanela.class).putExtras(args));
                String processo = 0<n_obra?n_obra+"/"+cod_obra+"/"+ano_obra:"";
                Toast.makeText(mContexto, "Abre Processo: ("+processo+")", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValores.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public final View mView;
        public final TextView mTVHora;
        public final TextView mTVProcesso;
        public final TextView mTVLocalidade;
        public final TextView mTVNota;
        public TarefaItemDetalhes mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTVHora = (TextView) view.findViewById(R.id.tvHoraListaTarefas);
            mTVProcesso = (TextView) view.findViewById(R.id.tvProcessoListaTarefas);
            mTVLocalidade = (TextView) view.findViewById(R.id.tvLocalidadeListaTarefas);
            mTVNota = (TextView) view.findViewById(R.id.tvNotaListaTarefas);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTVHora.getText() + "', '"+mTVProcesso.getText()+ "', '"+mTVLocalidade.getText();
        }
    }
}
