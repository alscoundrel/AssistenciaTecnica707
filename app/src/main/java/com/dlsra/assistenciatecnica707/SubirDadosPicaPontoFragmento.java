package com.dlsra.assistenciatecnica707;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class SubirDadosPicaPontoFragmento extends Fragment {
    //declaracao de classes
    private DBAssistencia classAssistenciaDB = null;
    //elementos visuais
    private View janela = null;
    private ProgressBar pbFicheirosEnviar = null;
    private TextView tvDadosAEnviar = null;
    private RecyclerView listaDados = null;
    private TextView tvSemDadosSubir = null;
    private Button btSubirDados = null;
    //elementos sistema
    private Context mContexto = null;
    private Activity mActividade = null;
    //variaveis
    private int mIdUtilizador;
    private int mNUtilizador;
    private ArrayList<ClassePicaPonto> picapontoAEnviar;
    //classe internas
    private RelogioEnvio relogioEnvio;
    final Handler myHandler = new Handler();

    public SubirDadosPicaPontoFragmento() {
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
        } catch (Exception e){ Log.e("Valores Passados", e.toString());}

        mContexto = this.getContext();
        mActividade = this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        janela = inflater.inflate(R.layout.subir_dados_picaponto_fragmento, container, false);
        return janela;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle){
        super.onViewCreated(view, bundle);
        carrega_classes();
        carrega_elementos();
        carrega_dados();
        esconde_quadros();
        preenche_dadosJanela();

        montaFragmento();

        //implementa evento subir dados
        btSubirDados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carrega_dadosAEnviar();
            }
        });
    }

    /**
     *
     */
    private void carrega_elementos(){
    }

    private void esconde_quadros(){
    }

    private void carrega_classes(){
        //inicializa a classe bd
        classAssistenciaDB = new DBAssistencia(mContexto, "PICAPONTO", "pica_ponto");
        //classe interna
        relogioEnvio = new RelogioEnvio();
    }

    private void carrega_dados(){
        picapontoAEnviar = new ArrayList<>();

        ArrayList<ClassePicaPonto> picas = classAssistenciaDB.get_picaPonto();
        for(ClassePicaPonto pica: picas){
            boolean enviar = !pica.isEnviado();
            if(enviar){
                picapontoAEnviar.add(pica);
            }
        }
    }
    // ---------------------

    private void preenche_dadosJanela(){
        pbFicheirosEnviar = (ProgressBar) janela.findViewById(R.id.pbFicheirosEnviar);
        tvDadosAEnviar    = (TextView) janela.findViewById(R.id.tvDadosAEnviar);
        listaDados        = (RecyclerView) janela.findViewById(R.id.listaDados);
        tvSemDadosSubir   = (TextView) janela.findViewById(R.id.tvSemDadosSubir);
        btSubirDados      = (Button) janela.findViewById(R.id.btSubirDados);
    }


    private void montaFragmento(){
        Toast.makeText(getContext(), "Nº Dados: "+picapontoAEnviar.size(), Toast.LENGTH_SHORT).show();

        pbFicheirosEnviar.setVisibility(View.GONE);
        tvDadosAEnviar.setVisibility(View.GONE);
        listaDados.setVisibility(View.GONE);
        tvSemDadosSubir.setVisibility(View.GONE);
        btSubirDados.setVisibility(View.GONE);

        // carrega adaptador
        if (listaDados!=null) {
            carrega_dados();
            recarrega_fragmentoPagina();

            RecyclerView.LayoutManager layout = new LinearLayoutManager(mContexto, LinearLayoutManager.VERTICAL, false);
            listaDados.setLayoutManager(layout);
        }
    }

    private void recarrega_fragmentoPagina(){
        if(tvSemDadosSubir!= null && listaDados != null) {
            listaDados.removeAllViewsInLayout();//removes all the views
            if (0 == picapontoAEnviar.size()) {
                tvSemDadosSubir.setVisibility(View.VISIBLE);
            } else {
                btSubirDados.setVisibility(View.VISIBLE);
                RecyclerView.Adapter adapter = new SubirDadosPicaPontoAdaptador(picapontoAEnviar, mContexto, mIdUtilizador, mNUtilizador);
                adapter.notifyDataSetChanged();
                listaDados.setVisibility(View.VISIBLE);
                //listaDados.setAdapter(adapter);
                listaDados.invalidate();
                listaDados.swapAdapter(adapter, true);
            }
        }
    }

    /**
     * Enviar dados
     */
    private void carrega_dadosAEnviar(){
        ClasseInternetConectividade cic = new ClasseInternetConectividade(mActividade);
        if(cic.isLigado()) {
            ArrayList<ClassePicaPonto> pAEnviar = new ArrayList<>();

            int c = listaDados.getChildCount();
            int conta = 0;
            for (ClassePicaPonto pica : picapontoAEnviar) {
                if(pica.isPara_enviar()){ pAEnviar.add(pica);}
                conta++;
            }

            if (0 == pAEnviar.size()) {
                Toast.makeText(mContexto, "Sem dados para enviar!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContexto, "A enviar " + pAEnviar.size() + "!", Toast.LENGTH_SHORT).show();
                ClasseEnviarPicaPonto cepp = new ClasseEnviarPicaPonto(mContexto);
                cepp.setElementos(pbFicheirosEnviar, tvDadosAEnviar);
                cepp.setDados(pAEnviar);
                cepp.enviar();

                //chama a classe relogioEnvio para controlar se o envio findou
                if(relogioEnvio.seInicializada()){ relogioEnvio.continua();}
                else{ relogioEnvio.inicializa();}
            }
        }
        else{ Toast.makeText(mContexto, "Sem ligação à internet!", Toast.LENGTH_LONG).show();}
    }

    final Runnable myRunnable = new Runnable() {
        public void run() {
            montaFragmento();//recarrega_fragmentoPagina();
        }
    };

    class RelogioEnvio{
        private static final int TEMPOMAXENVIO = 2*1000;
        private boolean pausa = true;
        private boolean inicializada = false;
        private Timer mTimer = new Timer();

        private int mNPassos = 0;
        private TimerTask mTask = new TimerTask() {
            @Override
            public void run() {
                mNPassos++;
                if(sePausa()){}
                else if(seInicializada() && View.GONE==pbFicheirosEnviar.getVisibility()){
                    //recarrega_fragmentoPagina();
                    myHandler.post(myRunnable);
                    pausa();
                }
            }
        };

        public void inicializa() {
            mTimer.scheduleAtFixedRate(mTask, TEMPOMAXENVIO, TEMPOMAXENVIO);
            pausa = false;
            inicializada = true;
        }

        public boolean seInicializada() {
            return inicializada;
        }

        public boolean sePausa() {
            return pausa;
        }

        public void pausa(){
            pausa = true;
        }

        public void continua(){
            pausa = false;
        }
    }
}
