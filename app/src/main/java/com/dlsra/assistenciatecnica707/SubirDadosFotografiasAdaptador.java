package com.dlsra.assistenciatecnica707;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
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
public class SubirDadosFotografiasAdaptador extends RecyclerView.Adapter<SubirDadosFotografiasAdaptador.ViewHolder>{

    private final ArrayList<ClasseFotografias> mValores;
    private Context mContexto;
    private int _id_utilizador = 0;
    private int _n_utilizador = 0;

    public SubirDadosFotografiasAdaptador(ArrayList<ClasseFotografias> items, Context contexto, int id_utilizador, int n_utilizador) {
        mValores = items;
        mContexto = contexto;
        _id_utilizador = id_utilizador;
        _n_utilizador  = n_utilizador;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subir_dados_fotografias_items_detalhes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ClasseFotografias fotografia = mValores.get(position);
        String pasta           = fotografia.getPasta();
        String descricao       = fotografia.getDescricao();
        int origem             = fotografia.getOrigem();
        int n_obra             = fotografia.getN_obra();
        String cod_obra        = fotografia.getCod_obra();
        int ano_obra           = fotografia.getAno_obra();
        String processo        = n_obra + " "+cod_obra+"/"+ano_obra;
        Bitmap bitmap          = fotografia.getData();
        final int id_foto      = fotografia.getId();
        boolean enviar         = fotografia.isPara_enviar();
        boolean enviado        = fotografia.isEnviado();
        //String nome_temporario = fotografia.getNome_temporario();

        holder.txt_itemPastaFoto.setText(pasta);
        holder.txt_itemProcesso.setText(0<n_obra?n_obra+" "+cod_obra+"/"+ano_obra:"");
        holder.txt_itemDescricaoFoto.setText(descricao);
        holder.txt_itemPorEnviar.setText(!enviado?"Para Enviar":"Enviada");
        holder.cb_itemParaEnviar.setChecked(enviar);
        holder.cb_itemParaEnviar.setVisibility(View.GONE);//nao mostra esta opcao
        try {
            holder.img_ivFotografia.setImageBitmap(bitmap);
        }
        catch (Exception e){ Log.e("Enviar Foto", e.toString());}
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
                DBAssistencia classAssistenciaDB = new DBAssistencia(mContexto, "FOTO", "fotografia");
                ContentValues valores = new ContentValues();
                valores.put("para_enviar", isChecked?1:0);
                boolean sucesso = classAssistenciaDB.updateDados(valores, "id", ""+id_foto, "fotografia");
                Toast.makeText(mContexto, "Fotografia '"+id_foto+"' estado enviar alterado com "+(sucesso?"sucesso!":"insucesso!"), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValores.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View janela;
        ImageView img_ivFotografia;
        ImageView img_ivOkEnviado;
        TextView  txt_itemPastaFoto;
        TextView  txt_itemProcesso;
        TextView  txt_itemDescricaoFoto;
        TextView  txt_itemPorEnviar;
        Button bt_itemEliminar;
        CheckBox cb_itemParaEnviar;

        public ViewHolder(View view) {
            super(view);
            janela = view;
            img_ivFotografia = (ImageView) janela.findViewById(R.id.ivFotografia);
            img_ivOkEnviado = (ImageView) janela.findViewById(R.id.ivOkEnviado);
            txt_itemPastaFoto = (TextView) janela.findViewById(R.id.itemTipologiaDocumento);
            txt_itemProcesso = (TextView) janela.findViewById(R.id.itemProcessoDocumento);
            txt_itemDescricaoFoto = (TextView) janela.findViewById(R.id.itemDescricaoFoto);
            txt_itemPorEnviar = (TextView) janela.findViewById(R.id.itemPorEnviar);
            bt_itemEliminar   = (Button) janela.findViewById(R.id.btEliminarDado);
            cb_itemParaEnviar = (CheckBox) janela.findViewById(R.id.cbParaEnviar);
        }

        @Override
        public String toString() {
            return super.toString();// + " '" + mTVHora.getText() + "', '"+mTVProcesso.getText()+ "', '"+mTVLocalidade.getText();
        }
    }
}
