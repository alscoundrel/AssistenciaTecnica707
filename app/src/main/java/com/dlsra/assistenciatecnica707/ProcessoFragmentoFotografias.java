package com.dlsra.assistenciatecnica707;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProcessoFragmentoFotografias extends Fragment {
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

    //elementos visuais
    private View janela = null;
    private ListView lvFotografias = null;
    private LinearLayout llSemFotos = null;
    private LinearLayout llComFotos = null;
    //elementos sistema
    private Context mContexto = null;

    public ProcessoFragmentoFotografias() {
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
        janela = inflater.inflate(R.layout.processo_fragmento_fotografias, container, false);
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
        lvFotografias = (ListView) janela.findViewById(R.id.lvFotografias);
        llSemFotos    = (LinearLayout) janela.findViewById(R.id.llFotografiasSemFotos);
        llComFotos    = (LinearLayout) janela.findViewById(R.id.llFotografiasComFotos);
    }

    private void carrega_classes(){
        //inicializa a classe tarefa
        classAssistenciaDB = new DBAssistencia(mContexto, "TAREFA", "tarefa");
    }

    // ---------------------
    private void preenche_dadosJanela(){
        ArrayList<ClasseFotografias> fotografias = new ArrayList<>();
        if(0 < mNObra){ fotografias = classAssistenciaDB.get_fotografias(mNObra, mCodObra, mAnoObra, false);}

        if(0==fotografias.size()){
            llSemFotos.setVisibility(View.VISIBLE);
            llComFotos.setVisibility(View.GONE);
        }
        else{
            llSemFotos.setVisibility(View.GONE);
            llComFotos.setVisibility(View.VISIBLE);

            /* carrega o adaptador */
            ProcessoFragmentoFotografiasAdaptador adaptador = new ProcessoFragmentoFotografiasAdaptador(mContexto, fotografias, mIdUtilizador);
            lvFotografias.setAdapter(adaptador);

            /* adiciona o invento */
            lvFotografias.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    abreFullImage(position);
                }
            });
        }
    }

    public void abreFullImage(int posicao){
        Bundle bundle = new Bundle();
        bundle.putInt("_id", mIdTarefa);
        bundle.putInt("_id_utilizador", mIdUtilizador);
        bundle.putInt("_n_obra", mNObra);
        bundle.putString("_cod_obra", mCodObra);
        bundle.putInt("_ano_obra", mAnoObra);
        bundle.putInt("_posicao_img", posicao);

        Intent i = new Intent(getContext(), ProcessoFragmentoFotografiasFullScreen.class);
        i.putExtras(bundle);
        startActivity(i);
    }
}
