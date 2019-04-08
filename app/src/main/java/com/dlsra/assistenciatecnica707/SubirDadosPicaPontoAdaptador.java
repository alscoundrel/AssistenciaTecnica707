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
public class SubirDadosPicaPontoAdaptador extends RecyclerView.Adapter<SubirDadosPicaPontoAdaptador.ViewHolder>{

    private final ArrayList<ClassePicaPonto> mValores;
    private Context mContexto;
    private int _id_utilizador = 0;
    private int _n_utilizador = 0;

    public SubirDadosPicaPontoAdaptador(ArrayList<ClassePicaPonto> items, Context contexto, int id_utilizador, int n_utilizador) {
        mValores = items;
        mContexto = contexto;
        _id_utilizador = id_utilizador;
        _n_utilizador  = n_utilizador;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subir_dados_picaponto_items_detalhes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ClassePicaPonto pica = mValores.get(position);
        final int id_pica    = pica.getId();
        int numero           = pica.getNumero();
        int codigo           = pica.getCodigo();
        String origem        = pica.getOrigem();
        String data          = pica.getData();
        String hora          = pica.getHora();
        String maquina       = pica.getMaquina();
        String gps           = pica.getGps();
        boolean enviar       = pica.isPara_enviar();
        boolean enviado      = pica.isEnviado();

        holder.txt_itemDataPica.setText(data);
        holder.txt_itemHoraPica.setText(hora.substring(0,5));
        holder.txt_itemNumeroPessoa.setText(numero+(gps.equals("")?"":" em "+gps));
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
                DBAssistencia classAssistenciaDB = new DBAssistencia(mContexto, "PICA", "pica_ponto");
                ContentValues valores = new ContentValues();
                valores.put("para_enviar", isChecked?1:0);
                boolean sucesso = classAssistenciaDB.updateDados(valores, "id", ""+id_pica, "pica_ponto");
                Toast.makeText(mContexto, "Pica '"+id_pica+"' estado enviar alterado com "+(sucesso?"sucesso!":"insucesso!"), Toast.LENGTH_SHORT).show();
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
        TextView  txt_itemDataPica;
        TextView  txt_itemHoraPica;
        TextView  txt_itemNumeroPessoa;
        TextView  txt_itemPorEnviar;
        Button bt_itemEliminar;
        CheckBox cb_itemParaEnviar;

        public ViewHolder(View view) {
            super(view);
            janela = view;
            img_ivOkEnviado      = (ImageView) janela.findViewById(R.id.ivOkEnviado);
            txt_itemDataPica     = (TextView) janela.findViewById(R.id.itemDataPicaPonto);
            txt_itemHoraPica     = (TextView) janela.findViewById(R.id.itemHoraPicaPonto);
            txt_itemNumeroPessoa = (TextView) janela.findViewById(R.id.itemNumeroPessoa);
            txt_itemPorEnviar    = (TextView) janela.findViewById(R.id.itemPorEnviar);
            bt_itemEliminar      = (Button) janela.findViewById(R.id.btEliminarDado);
            cb_itemParaEnviar    = (CheckBox) janela.findViewById(R.id.cbParaEnviar);
        }

        @Override
        public String toString() {
            return super.toString();// + " '" + mTVHora.getText() + "', '"+mTVProcesso.getText()+ "', '"+mTVLocalidade.getText();
        }
    }
}
