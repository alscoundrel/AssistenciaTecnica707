package com.dlsra.assistenciatecnica707;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Utilizador on 03/02/2016.
 */
public class ProcessoFragmentoFotografiasAdaptador extends BaseAdapter{
    //declaracao
    private DBAssistencia classAssistenciaDB = null;
    private static ArrayList<ClasseFotografias> cFotografias;
    private LayoutInflater l_Inflater;
    private Context contexto;
    private String pastaDepositoFotografias = "";

    public ProcessoFragmentoFotografiasAdaptador(Context context, ArrayList<ClasseFotografias> results, int id_utilizador) {
        cFotografias = results;
        l_Inflater = LayoutInflater.from(context);
        //inicializa a classe tarefa
        classAssistenciaDB = new DBAssistencia(context, "TAREFA", "fotografia");

        this.contexto = context;
        this.pastaDepositoFotografias = context.getString(R.string.pasta_deposito_fotografias)+"_"+id_utilizador;
    }

    public int getCount() { return cFotografias.size();}

    public Object getItem(int position) {
        return cFotografias.get(position);
    }

    public long getItemId(int position) {return position;}

    public long getItemIdFoto(int position) {
        ClasseFotografias foto = (ClasseFotografias) getItem(position);
        return foto.getId();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = l_Inflater.inflate(R.layout.processo_fragmento_fotografias_items_detalhes, null);
            holder = new ViewHolder();
            holder.img_ivFotografia = (ImageView) convertView.findViewById(R.id.ivFotografia);
            holder.txt_itemPastaFoto = (TextView) convertView.findViewById(R.id.itemTipologiaDocumento);
            holder.txt_itemDescricaoFoto = (TextView) convertView.findViewById(R.id.itemDescricaoFoto);
            holder.txt_itemPorEnviar = (TextView) convertView.findViewById(R.id.itemPorEnviar);
            holder.txt_itemIdFotografia = (TextView) convertView.findViewById(R.id.itemIdFotografia);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ClasseFotografias fotografia = (ClasseFotografias) getItem(position);
        //cFotografias.get(position).getData();
        int id_foto      = fotografia.getId();
        String pasta     = fotografia.getPasta();
        String descricao = fotografia.getDescricao();
        int origem       = fotografia.getOrigem();
        String imgPasta  = fotografia.getNome_temporario();
        boolean enviado  = fotografia.isEnviado();

        //mostra em que ponto se encontra a fotografia tirada pelo tecnico
        String txtPorEnviar = "";
        if(2==origem){ txtPorEnviar = enviado?"Enviada...":"Por Enviar...";}

        try {
            File raiz = new File(this.contexto.getFilesDir(), this.pastaDepositoFotografias);
            if (raiz.exists()) {
                File foto = new File(raiz, imgPasta);
                if (foto.exists()) {
                    int tamanho = holder.img_ivFotografia.getMaxWidth();
                    Bitmap bm = fotografia.decodeFile(foto, tamanho, tamanho);

                    if(null != bm){
                        holder.img_ivFotografia.setImageBitmap(bm);
                    }
                    foto = null;
                }
                raiz = null;
            }
        }
        catch(Exception e){ Log.e("Montar fotografia", e.toString());}

        holder.txt_itemPastaFoto.setText(pasta);
        holder.txt_itemDescricaoFoto.setText(descricao);
        holder.txt_itemPorEnviar.setText(txtPorEnviar);
        holder.txt_itemIdFotografia.setText(""+id_foto);

        fotografia = null;
        return convertView;
    }

    static class ViewHolder {
        ImageView img_ivFotografia;
        TextView txt_itemPastaFoto;
        TextView txt_itemDescricaoFoto;
        TextView txt_itemPorEnviar;
        TextView txt_itemIdFotografia;
    }
}
