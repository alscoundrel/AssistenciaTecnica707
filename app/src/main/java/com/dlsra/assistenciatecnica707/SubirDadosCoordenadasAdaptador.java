package com.dlsra.assistenciatecnica707;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dlsra.assistenciatecnica707.TarefasDiaFragmentoTarefas.OnListFragmentInteractionListener;

import java.util.ArrayList;

/**
 * {@link RecyclerView.Adapter} that can display a and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class SubirDadosCoordenadasAdaptador extends RecyclerView.Adapter<SubirDadosCoordenadasAdaptador.ViewHolder>{

    private final ArrayList<ClasseCoordenadas> mValores;
    private Context mContexto;
    private int _id_utilizador = 0;
    private int _n_utilizador = 0;

    public SubirDadosCoordenadasAdaptador(ArrayList<ClasseCoordenadas> items, Context contexto, int id_utilizador, int n_utilizador) {
        mValores = items;
        mContexto = contexto;
        _id_utilizador = id_utilizador;
        _n_utilizador  = n_utilizador;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subir_dados_coordenadas_items_detalhes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ClasseCoordenadas coordenada = mValores.get(position);
        String gps              = coordenada.getGps();
        String data_registo     = coordenada.getData_registo();
        int id_pessoa           = coordenada.getId_pessoa();
        int n_obra              = coordenada.getN_obra();
        String cod_obra         = coordenada.getCod_obra();
        int ano_obra            = coordenada.getAno_obra();
        final String processo         = n_obra + " "+cod_obra+"/"+ano_obra;
        final int id_coordenada = coordenada.getId();
        boolean enviar          = coordenada.isPara_enviar();
        boolean enviado         = coordenada.isEnviado();

        holder.txt_itemProcessoCoordenadas.setText(processo);
        holder.txt_itemGPSCoordenadas.setText(gps);
        holder.txt_itemDataCoordenadas.setText("TESTE");//data_registo.substring(0, 16)
        holder.txt_itemPorEnviar.setText(!enviado?"Para Enviar":"Enviada");
        holder.cb_itemParaEnviar.setChecked(enviar);
        holder.cb_itemParaEnviar.setVisibility(View.GONE);//nao mostra esta opcao

        if(enviar){
            holder.img_ivOkEnviado.setVisibility(View.GONE);
            holder.bt_itemEliminar.setVisibility(View.GONE);//nao permite eliminar fotos
        }
        else{
            holder.img_ivOkEnviado.setVisibility(View.VISIBLE);
            holder.bt_itemEliminar.setVisibility(View.GONE);
        }

        holder.cb_itemParaEnviar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DBAssistencia classAssistenciaDB = new DBAssistencia(mContexto, "OBRA", "coordenada");
                ContentValues valores = new ContentValues();
                valores.put("para_enviar", isChecked?1:0);
                boolean sucesso = classAssistenciaDB.updateDados(valores, "id", ""+id_coordenada, "coordenada");
                Toast.makeText(mContexto, "Coordenada '"+processo+"' estado enviar alterado com "+(sucesso?"sucesso!":"insucesso!"), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValores.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View janela;
        ImageView img_ivOkEnviado;
        TextView  txt_itemProcessoCoordenadas;
        TextView  txt_itemGPSCoordenadas;
        TextView  txt_itemDataCoordenadas;
        TextView  txt_itemPorEnviar;
        Button bt_itemEliminar;
        CheckBox cb_itemParaEnviar;

        public ViewHolder(View view) {
            super(view);
            janela                      = view;
            img_ivOkEnviado             = (ImageView) janela.findViewById(R.id.ivOkEnviado);
            txt_itemProcessoCoordenadas = (TextView) janela.findViewById(R.id.itemProcessoCoordenadas);
            txt_itemGPSCoordenadas      = (TextView) janela.findViewById(R.id.itemGPSCoordenadas);
            txt_itemDataCoordenadas     = (TextView) janela.findViewById(R.id.itemDataCoordenadasr);
            txt_itemPorEnviar           = (TextView) janela.findViewById(R.id.itemPorEnviar);
            bt_itemEliminar             = (Button) janela.findViewById(R.id.btEliminarDado);
            cb_itemParaEnviar           = (CheckBox) janela.findViewById(R.id.cbParaEnviar);
        }

        @Override
        public String toString() {
            return super.toString();// + " '" + mTVHora.getText() + "', '"+mTVProcesso.getText()+ "', '"+mTVLocalidade.getText();
        }
    }
}
