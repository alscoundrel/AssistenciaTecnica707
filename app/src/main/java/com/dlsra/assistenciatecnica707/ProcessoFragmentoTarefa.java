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
import android.widget.TextView;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProcessoFragmentoTarefa.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProcessoFragmentoTarefa#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProcessoFragmentoTarefa extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "_id_utilizador";
    private static final String ARG_PARAM2 = "_n_utilizador";
    private static final String ARG_PARAM3 = "_id_menu";
    private static final String ARG_PARAM4 = "_n_obra";
    private static final String ARG_PARAM5 = "_cod_obra";
    private static final String ARG_PARAM6 = "_ano_obra";
    private static final String ARG_PARAM7 = "_id_tarefa";
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
    private TextView tvDescricao = null;
    private TextView tvNota = null;
    private TextView tvHora = null;
    private TextView tvProcesso = null;
    //elementos sistema
    private Context mContexto = null;

    private OnFragmentInteractionListener mListener;

    public ProcessoFragmentoTarefa() {
        // Required empty public constructor
    }

    public static ProcessoFragmentoTarefa newInstance(String param1, String param2, String param3, String param4, String param5, String param6, String param7) {
        ProcessoFragmentoTarefa fragment = new ProcessoFragmentoTarefa();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        args.putString(ARG_PARAM5, param5);
        args.putString(ARG_PARAM6, param6);
        args.putString(ARG_PARAM7, param7);
        fragment.setArguments(args);
        return fragment;
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
        janela = inflater.inflate(R.layout.processo_fragmento_tarefa, container, false);
        return janela;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle){
        super.onViewCreated(view, bundle);
        carrega_classes();
        carrega_elementos();
        preenche_dadosJanela();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    /**
     *
     */
    private void carrega_elementos(){
        tvDescricao = (TextView) janela.findViewById(R.id.tvDescricaoTarefa);
        tvNota = (TextView) janela.findViewById(R.id.tvNotaTarefa);
        tvHora = (TextView) janela.findViewById(R.id.tvHorasTarefa);
    }

    private void carrega_classes() {
        //inicializa a classe tarefa
        classAssistenciaDB = new DBAssistencia(mContexto, "TAREFA", "tarefa");
    }

    // ---------------------
    private void preenche_dadosJanela(){
        TarefaItemDetalhes tarefa = get_tarefa(mIdTarefa);
        String descricao = get_descricaoProcesso(mNObra, mCodObra, mAnoObra);

        String nota = "-";
        String hora = "";
        if(null!=tarefa){
            nota = tarefa.getNota();
            hora = tarefa.getHora();
        }
        tvNota.setText(nota.replace("&euro", "€"));
        tvHora.setText(hora);
        tvDescricao.setText(descricao);

        //mostra obra
    }
    /**
     * GET's
     */
    private TarefaItemDetalhes get_tarefa(int id_tarefa){
        TarefaItemDetalhes tarefa = null;

        final String[] camposTarefa = new String[] {"id","data","hora","n_obra","cod_obra","ano_obra","localidade","nota"};
        classAssistenciaDB.setNomeTabela("tarefa");
        HashMap<String, String> dados = classAssistenciaDB.getDadosBy("id", ""+id_tarefa, camposTarefa);

        if(dados.isEmpty()){}
        else{
            tarefa = new TarefaItemDetalhes();

            tarefa.set_id(Integer.parseInt(dados.get("id")));
            tarefa.setData(dados.get("data"));
            tarefa.setHora(dados.get("hora"));
            tarefa.setN_obra(Integer.parseInt(dados.get("n_obra")));
            tarefa.setCod_obra(dados.get("cod_obra"));
            tarefa.setAno_obra(Integer.parseInt(dados.get("ano_obra")));
            tarefa.setLocalidade(dados.get("localidade"));
            tarefa.setNota(dados.get("nota"));
        }

        return tarefa;
    }

    private String get_descricaoProcesso(int n_obra, String cod_obra, int ano_obra){
        String descricao = "";

        final String[] camposProcesso = new String[] {"descricao"};
        classAssistenciaDB.setNomeTabela("processo");
        String onde = "n_obra=? AND cod_obra=? AND ano_obra=?";
        String[] valores = new String[]{""+n_obra, cod_obra, ""+ano_obra};
        HashMap<String, String> dados = classAssistenciaDB.getDadosBy(onde, valores, camposProcesso);

        if(dados.isEmpty()){}
        else{
            descricao = dados.get("descricao");
        }

        return descricao;
    }
}
