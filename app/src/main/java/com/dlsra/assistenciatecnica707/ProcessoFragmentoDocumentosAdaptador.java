package com.dlsra.assistenciatecnica707;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Utilizador on 25/05/2017.
 */
public class ProcessoFragmentoDocumentosAdaptador extends BaseAdapter{
    private static ArrayList<ClasseDocumentos> cDocumentos;
    private static ArrayList<String> lDocumentos;
    private LayoutInflater l_Inflater;
    private int mIdUtilizador = 0;

    public ProcessoFragmentoDocumentosAdaptador(Context context, ArrayList<ClasseDocumentos> documentosDB, ArrayList<String> documentosDIR, int id_utilizador) {
        this.cDocumentos   = documentosDB;
        this.lDocumentos   = documentosDIR;
        this.mIdUtilizador = id_utilizador;
        l_Inflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return cDocumentos.size();
    }

    public Object getItem(int position) {
        return cDocumentos.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = l_Inflater.inflate(R.layout.processo_fragmento_documentos_items_detalhes, null);
            holder = new ViewHolder();
            holder.img_ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
            holder.txt_itemTipologiaDocumento = (TextView) convertView.findViewById(R.id.itemTipologiaDocumento);
            holder.txt_itemNomeDocumento = (TextView) convertView.findViewById(R.id.itemNomeDocumento);
            holder.txt_itemDescricaoDocumento = (TextView) convertView.findViewById(R.id.itemDescricaoDocumento);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //Bitmap bitmap = cDocumentos.get(position).getData();
        String nome_ficheiro = cDocumentos.get(position).getNome_ficheiro();
        String tipologia = cDocumentos.get(position).getTipologia();
        String descricao = cDocumentos.get(position).getDescricao();
        Bitmap bitmap = bitmapIcon(nome_ficheiro);

        holder.img_ivIcon.setImageBitmap(bitmap);
        holder.txt_itemTipologiaDocumento.setText(tipologia);
        holder.txt_itemNomeDocumento.setText(nome_ficheiro);
        holder.txt_itemDescricaoDocumento.setText(descricao);

        return convertView;
    }

    static class ViewHolder {
        ImageView img_ivIcon;
        TextView txt_itemTipologiaDocumento;
        TextView txt_itemNomeDocumento;
        TextView txt_itemDescricaoDocumento;
    }

    private Bitmap bitmapIcon(String nome){
        int idLogo = R.drawable.logo_all;

        if(nome.endsWith(".txt")){ idLogo = R.drawable.logo_txt;}
        if(nome.endsWith(".rtf")){ idLogo = R.drawable.logo_rtf;}
        if(nome.endsWith(".doc")){ idLogo = R.drawable.logo_doc;}
        if(nome.endsWith(".docx")){ idLogo = R.drawable.logo_docx;}
        if(nome.endsWith(".xls")){ idLogo = R.drawable.logo_xls;}
        if(nome.endsWith(".xlsx")){ idLogo = R.drawable.logo_xlsx;}
        if(nome.endsWith(".pdf")){ idLogo = R.drawable.logo_pdf;}
        if(nome.endsWith(".tiff")){ idLogo = R.drawable.logo_tiff;}

        return BitmapFactory.decodeResource(l_Inflater.getContext().getResources(), idLogo);
    }
}
