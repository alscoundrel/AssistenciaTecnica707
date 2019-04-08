package com.dlsra.assistenciatecnica707;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProcessoFragmentoTrabalhos extends Fragment {

    //declaracao de classes
    private DBAssistencia classAssistenciaDB = null;
    //variaveis
    private int mIdUtilizador;
    private int mNUtilizador;
    private int mIdMenu;
    private int mNObra;
    private String mCodObra;
    private int mAnoObra;
    private int mIdTarefa;
    //elementos sistema
    private Context mContexto = null;
    //elementos visuais
    private View janela = null;

    public ProcessoFragmentoTrabalhos() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Intent intent = getActivity().getIntent();
            Bundle bundle = intent.getExtras();
            mIdUtilizador = bundle.getInt("_id_utilizador");
            mNUtilizador  = bundle.getInt("_n_utilizador");
            mIdMenu       = bundle.getInt("_id_menu");
            mNObra        = bundle.getInt("_n_obra");
            mCodObra      = bundle.getString("_cod_obra");
            mAnoObra      = bundle.getInt("_ano_obra");
            mIdTarefa     = bundle.getInt("_id_tarefa");
        } catch (Exception e){ Log.e("Valores Passados", e.toString());}

        mContexto = this.getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        janela = inflater.inflate(R.layout.processo_fragmento_trabalhos, container, false);
        return janela;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle){
        super.onViewCreated(view, bundle);
        carrega_classes();
        carrega_elementos();
        preenche_dadosJanela();
    }
    /**
     *
     */
    private void carrega_elementos(){
    }

    private void carrega_classes(){
        //inicializa a classe tarefa
        classAssistenciaDB = new DBAssistencia(mContexto, "TAREFA", "tarefa");
    }

    private void preenche_dadosJanela(){
        WebView wvTrabalhos = (WebView) janela.findViewById(R.id.wvTrabalhos);
        HashMap<String, String> trabalhos = get_trabalhos(mNObra, mCodObra, mAnoObra);

        String textoHTML = montaHTML(trabalhos);

        WebSettings webDefinicoes = wvTrabalhos.getSettings();
        webDefinicoes.setDefaultTextEncodingName("utf-8");//"iso-885-1"

        wvTrabalhos.loadData(Uri.encode(textoHTML), "text/html; charset=utf-8", null);//iso-8859-1

        wvTrabalhos.getSettings().setBuiltInZoomControls(true);
        wvTrabalhos.getSettings().setDisplayZoomControls(false);
    }

    private String montaHTML(HashMap<String, String> trabalhos){
        String html = "";
        html+="<html>";
        html+="<head>";
        html+="<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">";//iso-8859-1
        html+="<meta name=\"Language\" content=\"pt-PT\">";
        html+="<title>Trabalhos</title>";
        html+=carrega_estilos();
        html+="</head>";
        html+="<body>";
        html+=mNObra+" "+mCodObra+"/"+mAnoObra+"<br><br>";
        html+=preenche_trabalhos(trabalhos);
        html+="</body>";
        html+="</html>";
        return html;
    }

    private String preenche_trabalhos(HashMap<String, String> trabalhos){
        String corpo = "";
        if(trabalhos.isEmpty()){}
        else{
            String descricao = trabalhos.get("descricao");
            corpo += descricao.equals("")?"Sem trabalhos...":descricao;
        }

        return corpo;
    }

    private String carrega_estilos(){
        String estilos="";
        estilos+="<style type=\"text/css\">";
        estilos+="html{margin:0;}";
        estilos+="i{}";
        estilos+="";
        estilos+="";
        estilos+="</style>";
        return estilos;
    }

    /**
     * GET's
     */
    //processo
    private HashMap<String, String> get_trabalhos(int n_obra, String cod_obra, int ano_obra){
        final String[] camposTrabalhos = new String[] {"id","n_obra","cod_obra","ano_obra","descricao"};
        classAssistenciaDB.setNomeTabela("trabalhos");
        String onde = "n_obra=? AND cod_obra=? AND ano_obra=?";
        String[] valores = new String[]{""+n_obra, cod_obra, ""+ano_obra};
        HashMap<String, String> trabalhos = classAssistenciaDB.getDadosBy(onde, valores, camposTrabalhos);
        return trabalhos;
    }
}
