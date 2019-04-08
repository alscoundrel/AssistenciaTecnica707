package com.dlsra.assistenciatecnica707;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;


public class ProcessoFragmentoFotografiasFullScreen extends Activity {
    //declaracao de classes
    private DBAssistencia classAssistenciaDB = null;

    //valores passados
    private int mIdUtilizador;
    private int mNObra;
    private String mCodObra;
    private int mAnoObra;
    private int mPosicaoImg;

    private ProcessoFragmentoFotografiasFullScreenAdaptador adapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //pedir para retirar o titulo da aplicação
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.processo_fragmento_fotografias_full_screen);

        //pedir para retirar o titulo da aplicação

        //carrega valores passados
        Intent intent = getIntent();
        try {
            Bundle bundle = intent.getExtras();
            mIdUtilizador = bundle.getInt("_id_utilizador");
            mNObra        = bundle.getInt("_n_obra");
            mCodObra      = bundle.getString("_cod_obra");
            mAnoObra      = bundle.getInt("_ano_obra");
            mPosicaoImg   = bundle.getInt("_posicao_img");
        } catch (Exception e){ Log.e("Valores Passados", e.toString());}

        //inicializa a classe tarefa
        classAssistenciaDB = new DBAssistencia(this, "Fotografia", "fotografia");
        ArrayList<ClasseFotografias> fotografias = classAssistenciaDB.get_fotografias(mNObra, mCodObra, mAnoObra, false);

        try {
            adapter = new ProcessoFragmentoFotografiasFullScreenAdaptador(ProcessoFragmentoFotografiasFullScreen.this, fotografias, mIdUtilizador);

            viewPager = (ViewPager) findViewById(R.id.pagerFullscreen);
            viewPager.setAdapter(adapter);

            // displaying selected image first
            viewPager.setCurrentItem(mPosicaoImg);
        }
        catch (Exception e){ Log.e("Lançar ViewPager", e.toString());}
    }
}
