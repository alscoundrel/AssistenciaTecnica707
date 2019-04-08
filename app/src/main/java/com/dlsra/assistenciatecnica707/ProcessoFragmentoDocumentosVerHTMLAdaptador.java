package com.dlsra.assistenciatecnica707;

/**
 * Created by AlScoundrel on 26/02/2016.
 */
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class ProcessoFragmentoDocumentosVerHTMLAdaptador extends PagerAdapter {

    private Activity _activity;
    private String _conteudo;
    private LayoutInflater inflater;

    // constructor
    public ProcessoFragmentoDocumentosVerHTMLAdaptador(Activity activity, String conteudo) {
        this._activity = activity;
        this._conteudo = conteudo;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Button btnClose;

        TextView tvConteudoTxt;

        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.processo_fragmento_documentos_ver_txt_adaptador, container,
                false);

        btnClose = (Button) viewLayout.findViewById(R.id.btnClose);
        tvConteudoTxt = (TextView) viewLayout.findViewById(R.id.tvVerTxtFull);
        tvConteudoTxt.setText(_conteudo);

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
