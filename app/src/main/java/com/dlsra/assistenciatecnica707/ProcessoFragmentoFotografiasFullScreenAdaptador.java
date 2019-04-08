package com.dlsra.assistenciatecnica707;

/**
 * Created by AlScoundrel on 26/02/2016.
 */
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class ProcessoFragmentoFotografiasFullScreenAdaptador extends PagerAdapter {
    private String pastaDepositoFotografias = "";
    private int mIdUtilizador;
    //declaracao de classes
    private DBAssistencia classAssistenciaDB = null;

    private Activity _activity;
    private ArrayList<ClasseFotografias> _images;
    private LayoutInflater inflater;

    // constructor
    public ProcessoFragmentoFotografiasFullScreenAdaptador(Activity activity, ArrayList<ClasseFotografias> images, int id_utilizador) {
        this._activity     = activity;
        this._images       = images;
        this.mIdUtilizador = id_utilizador;

        //inicializa a classe ligacao à bd
        classAssistenciaDB = new DBAssistencia(activity, "TAREFA", "fotografia");
        this.pastaDepositoFotografias = activity.getString(R.string.pasta_deposito_fotografias)+"_"+id_utilizador;
    }

    @Override
    public int getCount() {
        return this._images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imgDisplay;
        Button btnClose;
        TextView tvPastaFotografiaFullScreen;
        TextView tvDescricaoFotografiaFullScreen;

        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.processo_fragmento_fotografias_fullscreen_image, container,
                false);

        imgDisplay = (TouchImageView) viewLayout.findViewById(R.id.imgDisplay);
        btnClose = (Button) viewLayout.findViewById(R.id.btnClose);
        tvPastaFotografiaFullScreen = (TextView) viewLayout.findViewById(R.id.tvPastaFotografiaFullScreen);
        tvDescricaoFotografiaFullScreen = (TextView) viewLayout.findViewById(R.id.tvDescricaoFotografiaFullScreen);

        ClasseFotografias fotografia = this._images.get(position);
        int id_foto = fotografia.getId();
        String pasta = fotografia.getPasta();
        String descricao = fotografia.getDescricao();
        String nome_temporario = fotografia.getNome_temporario();

        boolean pela_pasta = false;
        try {
            File raiz = new File(this._activity.getFilesDir(), this.pastaDepositoFotografias);
            if (null != raiz) {
                File foto = new File(raiz, nome_temporario);
                if (null != foto) {
                    int tamanho = imgDisplay.getMaxWidth();
                    Bitmap bm = fotografia.decodeFile(foto, tamanho, tamanho);
                    if(null != bm){
                        imgDisplay.setImageBitmap(bm);
                        pela_pasta = true;
                    }
                    foto = null;
                }
                raiz = null;
            }
        }
        catch(Exception e){ Log.e("Montar fotografia", e.toString());}

        tvPastaFotografiaFullScreen.setText(pasta);
        tvDescricaoFotografiaFullScreen.setText(descricao);

        tvPastaFotografiaFullScreen.setVisibility(pasta.equals("") ? View.INVISIBLE : View.VISIBLE);
        tvDescricaoFotografiaFullScreen.setVisibility(descricao.equals("") ? View.INVISIBLE : View.VISIBLE);

        // close button click event
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _activity.finish();
            }
        });

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}
