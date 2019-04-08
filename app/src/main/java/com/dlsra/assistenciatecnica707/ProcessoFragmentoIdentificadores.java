package com.dlsra.assistenciatecnica707;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProcessoFragmentoIdentificadores.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProcessoFragmentoIdentificadores#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProcessoFragmentoIdentificadores extends Fragment {
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
    private View janela         = null;

    //elementos sistema
    private Context mContexto = null;

    private OnFragmentInteractionListener mListener;

    public ProcessoFragmentoIdentificadores() {
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
        janela = inflater.inflate(R.layout.processo_fragmento_identificadores, container, false);
        return janela;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle){
        super.onViewCreated(view, bundle);
        carrega_classes();
        carrega_elementos();
        esconde_quadros();
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
    }

    private void esconde_quadros(){
    }

    private void carrega_classes(){
        //inicializa a classe tarefa
        classAssistenciaDB = new DBAssistencia(mContexto, "TAREFA", "tarefa");
    }
    // ---------------------

    private void preenche_dadosJanela(){
        HashMap<String, String> processo = get_processo(mNObra, mCodObra, mAnoObra);
        HashMap<String, String> seguro = get_seguro(mNObra, mCodObra, mAnoObra);
        HashMap<String, String> morada = get_moradaObra(mNObra, mCodObra, mAnoObra);
        ArrayList<HashMap<String, String>> pessoas = get_pessoas(mNObra, mCodObra, mAnoObra);
        HashMap<String, String> assistencia = get_assistencia(mNObra, mCodObra, mAnoObra);

        int tipo_base = Integer.parseInt(processo.get("tipo_base"));
        preenche_cliente(pessoas.get(0), tipo_base);
        preenche_seguro(seguro);
        preenche_processo(processo);
        preenche_lesados(pessoas);
        preenche_perito(pessoas);
        HashMap<String,String> cfinal = 1<pessoas.size()?pessoas.get(1):new HashMap<String, String>();
        preenche_clienteFinal(cfinal, tipo_base);
        preenche_assistencia(assistencia);
        preenche_moradaObra(morada);
    }

    private void preenche_cliente(HashMap<String, String> pessoa, int tipo_base){
        CardView cvCliente = (CardView) janela.findViewById(R.id.cvCliente);
        if (pessoa.isEmpty()) {
            cvCliente.setVisibility(View.GONE);
        } else {
            DecimalFormat DF = new DecimalFormat("###0.00");

            String nome             = pessoa.get("nome");
            String morada           = pessoa.get("morada");
            String contactos        = pessoa.get("contactos");
            String outros_contactos = pessoa.get("outros_contactos");
            String contribuinte     = pessoa.get("contribuinte");
            String gps              = pessoa.get("gps");

            //elementos
            TextView tvTitulo                = (TextView) janela.findViewById(R.id.tvProcessoTituloCliente);
            RelativeLayout rlNome            = (RelativeLayout) janela.findViewById(R.id.rlProcessoClienteNome);
            TextView tvNome                  = (TextView) janela.findViewById(R.id.tvProcessoValorClienteNome);
            RelativeLayout rlMorada          = (RelativeLayout) janela.findViewById(R.id.rlProcessoClienteMorada);
            TextView tvMorada                = (TextView) janela.findViewById(R.id.tvProcessoValorClienteMorada);
            RelativeLayout rlContactos       = (RelativeLayout) janela.findViewById(R.id.rlProcessoClienteContactos);
            TextView tvContactos             = (TextView) janela.findViewById(R.id.tvProcessoValorClienteContactos);
            RelativeLayout rlOutrosContactos = (RelativeLayout) janela.findViewById(R.id.rlProcessoClienteOutrosContactos);
            TextView tvOutrosContactos       = (TextView) janela.findViewById(R.id.tvProcessoValorClienteOutrosContactos);
            RelativeLayout rlContribuinte    = (RelativeLayout) janela.findViewById(R.id.rlProcessoClienteContribuinte);
            TextView tvContribuinte          = (TextView) janela.findViewById(R.id.tvProcessoValorClienteContribuinte);
            RelativeLayout rlCoordenadas     = (RelativeLayout) janela.findViewById(R.id.rlProcessoClienteCoordenadas);
            TextView tvCoordenadas           = (TextView) janela.findViewById(R.id.tvProcessoValorClienteCoordenadas);

            tvTitulo.setText(1==tipo_base?"Segurado":"Cliente");
            cvCliente.setVisibility(View.VISIBLE);
            tvNome.setText(nome);
            tvMorada.setText(morada);
            tvContactos.setText(contactos);
            if(!outros_contactos.equals("")){
                rlOutrosContactos.setVisibility(View.VISIBLE);
                tvOutrosContactos.setText(outros_contactos);
            } else { rlOutrosContactos.setVisibility(View.GONE);}
            if(!contribuinte.equals("")){
                rlContribuinte.setVisibility(View.VISIBLE);
                tvContribuinte.setText(contribuinte);
            } else { rlContribuinte.setVisibility(View.GONE);}
            if(!gps.equals("") && !gps.equals(":")){
                rlCoordenadas.setVisibility(View.VISIBLE);
                tvCoordenadas.setText(gps);
            } else { rlCoordenadas.setVisibility(View.GONE);}
        }
    }

    private void preenche_seguro(HashMap<String, String> seguro) {
        CardView cvSeguro = (CardView) janela.findViewById(R.id.cvSeguro);
        if (seguro.isEmpty()) {
            cvSeguro.setVisibility(View.GONE);
        } else {
            DecimalFormat DF = new DecimalFormat("###0.00");

            String companhia   = seguro.get("companhia");
            String produto     = seguro.get("produto");
            String apolice     = seguro.get("apolice");
            String sinistro    = seguro.get("n_sinistro");
            String contacto    = seguro.get("contacto");
            String referencias = seguro.get("referencias");

            //elementos
            RelativeLayout rlCompanhia   = (RelativeLayout) janela.findViewById(R.id.rlProcessoCompanhia);
            TextView tvCompanhia         = (TextView) janela.findViewById(R.id.tvProcessoValorCompanhia);
            RelativeLayout rlProduto     = (RelativeLayout) janela.findViewById(R.id.rlProcessoProduto);
            TextView tvProduto           = (TextView) janela.findViewById(R.id.tvProcessoValorProduto);
            RelativeLayout rlApolice     = (RelativeLayout) janela.findViewById(R.id.rlProcessoApolice);
            TextView tvApolice           = (TextView) janela.findViewById(R.id.tvProcessoValorApolice);
            RelativeLayout rlSinistro    = (RelativeLayout) janela.findViewById(R.id.rlProcessoSinistro);
            TextView tvSinistro          = (TextView) janela.findViewById(R.id.tvProcessoValorSinistro);
            RelativeLayout rlContacto    = (RelativeLayout) janela.findViewById(R.id.rlProcessoContactoSeguro);
            TextView tvContacto          = (TextView) janela.findViewById(R.id.tvProcessoValorContactoSeguro);
            RelativeLayout rlReferencias = (RelativeLayout) janela.findViewById(R.id.rlProcessoReferencias);
            TextView tvReferencias       = (TextView) janela.findViewById(R.id.tvProcessoValorReferencias);

            cvSeguro.setVisibility(View.VISIBLE);
            tvCompanhia.setText(companhia);
            if(!produto.equals("")){
                rlProduto.setVisibility(View.VISIBLE);
                tvProduto.setText(produto);
            } else { rlProduto.setVisibility(View.GONE);}
            tvApolice.setText(apolice);
            if(!sinistro.equals("")){
                rlSinistro.setVisibility(View.VISIBLE);
                tvSinistro.setText(sinistro);
            } else { rlSinistro.setVisibility(View.GONE);}
            /*
            if(!contacto.equals("")){
                rlContacto.setVisibility(View.VISIBLE);
                tvContacto.setText(contacto);
            } else { rlContacto.setVisibility(View.GONE);}
            */
            rlContacto.setVisibility(View.GONE);
            tvReferencias.setText(referencias);
        }
    }

    private void preenche_processo(HashMap<String, String> processo) {
        CardView cvProcesso = (CardView) janela.findViewById(R.id.cvProcesso);
        if (processo.isEmpty()) {
            cvProcesso.setVisibility(View.GONE);
        } else {
            DecimalFormat DF = new DecimalFormat("###0.00");

            String categoria       = processo.get("categoria");
            String estado          = processo.get("estado");
            String descricao       = processo.get("descricao");
            String mediador        = processo.get("mediador");
            String referencia      = processo.get("referencia");
            Double franquia        = Double.parseDouble(processo.get("franquia"));
            Double franquia_pago   = Double.parseDouble(processo.get("franquia_pago"));
            String danos_esteticos = processo.get("danos_esteticos");
            int tipo_base          = Integer.parseInt(processo.get("tipo_base"));

            //elementos
            RelativeLayout rlCategoria  = (RelativeLayout) janela.findViewById(R.id.rlProcessoCategoria);
            TextView tvCategoria        = (TextView) janela.findViewById(R.id.tvProcessoValorCategoria);
            RelativeLayout rlEstado     = (RelativeLayout) janela.findViewById(R.id.rlProcessoEstado);
            TextView tvEstado           = (TextView) janela.findViewById(R.id.tvProcessoValorEstado);
            RelativeLayout rlDescricao  = (RelativeLayout) janela.findViewById(R.id.rlProcessoDescricao);
            TextView tvDescricao        = (TextView) janela.findViewById(R.id.tvProcessoValorDescricao);
            RelativeLayout rlFranquia   = (RelativeLayout) janela.findViewById(R.id.rlProcessoFranquia);
            TextView tvFranquia         = (TextView) janela.findViewById(R.id.tvProcessoValorFranquia);
            RelativeLayout rlReferencia = (RelativeLayout) janela.findViewById(R.id.rlProcessoReferencia);
            TextView tvReferencia       = (TextView) janela.findViewById(R.id.tvProcessoValorReferencia);
            RelativeLayout rlMediador   = (RelativeLayout) janela.findViewById(R.id.rlProcessoMediador);
            TextView tvMediador         = (TextView) janela.findViewById(R.id.tvProcessoValorMediador);
            RelativeLayout rlDanos      = (RelativeLayout) janela.findViewById(R.id.rlProcessoDanos);
            TextView tvDanos            = (TextView) janela.findViewById(R.id.tvProcessoValorDanos);

            cvProcesso.setVisibility(View.VISIBLE);
            tvCategoria.setText(categoria);
            tvEstado.setText(estado);
            if(1==tipo_base && 0<franquia){
                Double franquia_falta = franquia-franquia_pago;
                String t = DF.format(franquia)+"€";
                if(0 < franquia_falta){ t+=" (Falta: "+DF.format(franquia_falta)+"€)";}
                rlFranquia.setVisibility(View.VISIBLE);
                tvFranquia.setText(t);
            } else { rlFranquia.setVisibility(View.GONE);}
            if(!referencia.equals("")){
                rlReferencia.setVisibility(View.VISIBLE);
                tvReferencia.setText(referencia);
            } else { rlReferencia.setVisibility(View.GONE);}
            if(!mediador.equals("")){
                rlMediador.setVisibility(View.VISIBLE);
                tvMediador.setText(mediador);
            } else { rlMediador.setVisibility(View.GONE);}
            tvDescricao.setText(descricao);
            if(!danos_esteticos.equals("")) {
                rlDanos.setVisibility(View.VISIBLE);
                tvDanos.setText(danos_esteticos);
            } else { rlDanos.setVisibility(View.GONE);}
        }
    }

    private void preenche_lesados(ArrayList<HashMap<String, String>> pessoas){
        CardView cvLesados = (CardView) janela.findViewById(R.id.cvLesados);
        String texto = "";
        for(int i = 0; i < pessoas.size(); i++){
            HashMap<String, String> pessoa = pessoas.get(i);
            String titulo = pessoa.get("titulo");
            if(titulo.equals("Lesado")){
                String nome             = pessoa.get("nome");
                String morada           = pessoa.get("morada");
                String contactos        = pessoa.get("contactos");
                String outros_contactos = pessoa.get("outros_contactos");
                String contribuinte     = pessoa.get("contribuinte");
                String gps              = pessoa.get("gps");

                if(!texto.equals("")){ texto += "\r\n\r\n";}
                texto += nome;
                texto += "\n\r"+morada;
                if(!contactos.equals("")){ texto += "\r\n"+contactos;}
                if(!outros_contactos.equals("")){ texto += "\r\n"+outros_contactos;}
                if(!contribuinte.equals("")){ texto += "\r\n"+contribuinte;}
            }
        }
        if(texto.equals("")){ cvLesados.setVisibility(View.GONE);}
        else{
            TextView tvLesados = (TextView) janela.findViewById(R.id.tvProcessoValorLesados);
            cvLesados.setVisibility(View.VISIBLE);
            tvLesados.setText(texto);
        }
    }

    private void preenche_perito(ArrayList<HashMap<String, String>> pessoas){
        CardView cvPerito = (CardView) janela.findViewById(R.id.cvPerito);
        String texto = "";
        for(int i = 0; i < pessoas.size(); i++){
            HashMap<String, String> pessoa = pessoas.get(i);
            String titulo = pessoa.get("titulo");
            if(titulo.equals("Perito")){
                String nome      = pessoa.get("nome");
                String contactos = pessoa.get("contactos");
                String companhia = pessoa.get("companhia");

                if(!texto.equals("")){ texto += "\r\n\r\n";}
                texto += nome;
                if(!contactos.equals("")){ texto += "\r\n"+contactos;}
                if(!companhia.equals("")){ texto += "\r\n"+companhia;}
            }
        }
        if(texto.equals("")){ cvPerito.setVisibility(View.GONE);}
        else{
            TextView tvPerito = (TextView) janela.findViewById(R.id.tvProcessoValorPerito);
            cvPerito.setVisibility(View.VISIBLE);
            tvPerito.setText(texto);
        }
    }

    private void preenche_clienteFinal(HashMap<String, String> pessoa, int tipo_base){
        CardView cvCliente = (CardView) janela.findViewById(R.id.cvClienteFinal);
        if (pessoa.isEmpty()) {
            cvCliente.setVisibility(View.GONE);
        } else {
            DecimalFormat DF = new DecimalFormat("###0.00");

            String titulo           = pessoa.get("titulo");
            String nome             = pessoa.get("nome");
            String morada           = pessoa.get("morada");
            String contactos        = pessoa.get("contactos");
            String outros_contactos = pessoa.get("outros_contactos");
            String contribuinte     = pessoa.get("contribuinte");
            String gps              = "";//pessoa.get("gps");

            //elementos
            TextView tvTitulo                = (TextView) janela.findViewById(R.id.tvProcessoTituloClienteFinal);
            RelativeLayout rlNome            = (RelativeLayout) janela.findViewById(R.id.rlProcessoClienteFinalNome);
            TextView tvNome                  = (TextView) janela.findViewById(R.id.tvProcessoValorClienteFinalNome);
            RelativeLayout rlMorada          = (RelativeLayout) janela.findViewById(R.id.rlProcessoClienteFinalMorada);
            TextView tvMorada                = (TextView) janela.findViewById(R.id.tvProcessoValorClienteFinalMorada);
            RelativeLayout rlContactos       = (RelativeLayout) janela.findViewById(R.id.rlProcessoClienteFinalContactos);
            TextView tvContactos             = (TextView) janela.findViewById(R.id.tvProcessoValorClienteFinalContactos);
            RelativeLayout rlOutrosContactos = (RelativeLayout) janela.findViewById(R.id.rlProcessoClienteFinalOutrosContactos);
            TextView tvOutrosContactos       = (TextView) janela.findViewById(R.id.tvProcessoValorClienteFinalOutrosContactos);
            RelativeLayout rlContribuinte    = (RelativeLayout) janela.findViewById(R.id.rlProcessoClienteFinalContribuinte);
            TextView tvContribuinte          = (TextView) janela.findViewById(R.id.tvProcessoValorClienteFinalContribuinte);
            RelativeLayout rlCoordenadas     = (RelativeLayout) janela.findViewById(R.id.rlProcessoClienteFinalCoordenadas);
            TextView tvCoordenadas           = (TextView) janela.findViewById(R.id.tvProcessoValorClienteFinalCoordenadas);

            if(!titulo.equals("Cliente Final")){ cvCliente.setVisibility(View.GONE);}
            else{ cvCliente.setVisibility(View.VISIBLE);}
            tvTitulo.setText(titulo);
            tvNome.setText(nome);
            tvMorada.setText(morada);
            tvContactos.setText(contactos);
            if(!outros_contactos.equals("")){
                rlOutrosContactos.setVisibility(View.VISIBLE);
                tvOutrosContactos.setText(outros_contactos);
            } else { rlOutrosContactos.setVisibility(View.GONE);}
            if(!contribuinte.equals("")){
                rlContribuinte.setVisibility(View.VISIBLE);
                tvContribuinte.setText(contribuinte);
            } else { rlContribuinte.setVisibility(View.GONE);}
            if(!gps.equals("")){
                rlCoordenadas.setVisibility(View.VISIBLE);
                tvCoordenadas.setText(gps);
            } else { rlCoordenadas.setVisibility(View.GONE);}
        }
    }

    private void preenche_assistencia(HashMap<String, String> assistencia) {
        CardView cvAssistencia = (CardView) janela.findViewById(R.id.cvAssistencia);
        if (assistencia.isEmpty()) {
            cvAssistencia.setVisibility(View.GONE);
        } else {
            String codigo   = assistencia.get("codigo");
            String artigo   = assistencia.get("artigo");
            String contacto = assistencia.get("contacto");

            //elementos
            RelativeLayout rlCodigo   = (RelativeLayout) janela.findViewById(R.id.rlProcessoCodigo);
            TextView tvCodigo         = (TextView) janela.findViewById(R.id.tvProcessoValorCodigo);
            RelativeLayout rlArtigo    = (RelativeLayout) janela.findViewById(R.id.rlProcessoArtigo);
            TextView tvArtigo           = (TextView) janela.findViewById(R.id.tvProcessoValorArtigo);
            RelativeLayout rlContacto    = (RelativeLayout) janela.findViewById(R.id.rlProcessoContactoAssistencia);
            TextView tvContacto          = (TextView) janela.findViewById(R.id.tvProcessoValorContactoAssistencia);

            if(codigo.equals("") && artigo.equals("")){ cvAssistencia.setVisibility(View.GONE);}
            else{ cvAssistencia.setVisibility(View.VISIBLE);}
            tvCodigo.setText(codigo);
            tvArtigo.setText(artigo);
            tvContacto.setText(contacto);
        }
    }

    private void preenche_moradaObra(HashMap<String, String> morada){
        CardView cvMorada = (CardView) janela.findViewById(R.id.cvMoradaObra);
        String texto = "";

        if (morada.isEmpty()) {
            cvMorada.setVisibility(View.GONE);
        } else {
            String endereco = morada.get("morada");

            //elementos
            RelativeLayout rlMorada = (RelativeLayout) janela.findViewById(R.id.rlProcessoMoradaObra);
            TextView tvMorada       = (TextView) janela.findViewById(R.id.tvProcessoValorMoradaObra);

            if(endereco.equals("")){ cvMorada.setVisibility(View.GONE);}
            else{ cvMorada.setVisibility(View.VISIBLE);}
            tvMorada.setText(endereco);
        }
    }
    /**
     * GET's
     */
    //processo
    private HashMap<String, String> get_processo(int n_obra, String cod_obra, int ano_obra){
        final String[] camposProcesso = new String[] {"id","n_obra","cod_obra","ano_obra","categoria","estado","descricao","mediador","referencia","franquia","franquia_pago","danos_esteticos","tipo_base"};
        classAssistenciaDB.setNomeTabela("processo");
        String onde = "n_obra=? AND cod_obra=? AND ano_obra=?";
        String[] valores = new String[]{""+n_obra, cod_obra, ""+ano_obra};
        HashMap<String, String> processo = classAssistenciaDB.getDadosBy(onde, valores, camposProcesso);
        return processo;
    }
    //seguro
    private HashMap<String, String> get_seguro(int n_obra, String cod_obra, int ano_obra){
        final String[] camposSeguro = new String[] {"id","n_obra","cod_obra","ano_obra","companhia","referencias","apolice","n_sinistro","produto","contacto"};
        classAssistenciaDB.setNomeTabela("seguro");
        String onde = "n_obra=? AND cod_obra=? AND ano_obra=?";
        String[] valores = new String[]{""+n_obra, cod_obra, ""+ano_obra};
        HashMap<String, String> seguro = classAssistenciaDB.getDadosBy(onde, valores, camposSeguro);
        return seguro;
    }
    //morada_obra
    private HashMap<String, String> get_moradaObra(int n_obra, String cod_obra, int ano_obra){
        final String[] camposMorada = new String[] {"id","n_obra","cod_obra","ano_obra","morada"};
        classAssistenciaDB.setNomeTabela("morada_obra");
        String onde = "n_obra=? AND cod_obra=? AND ano_obra=?";
        String[] valores = new String[]{""+n_obra, cod_obra, ""+ano_obra};
        HashMap<String, String> morada = classAssistenciaDB.getDadosBy(onde, valores, camposMorada);
        return morada;
    }
    //pessoas
    private ArrayList<HashMap<String, String>> get_pessoas(int n_obra, String cod_obra, int ano_obra){
        final String[] camposPessoa = new String[] {"id","n_obra","cod_obra","ano_obra","titulo","id_pessoa","nome","morada","contactos","outros_contactos","contribuinte","gps","companhia","enviado"};
        classAssistenciaDB.setNomeTabela("pessoa");
        String onde = "n_obra=? AND cod_obra=? AND ano_obra=?";
        String[] valores = new String[]{""+n_obra, cod_obra, ""+ano_obra};
        classAssistenciaDB.preencheDados(camposPessoa, onde, valores, "id ASC");
        ArrayList<HashMap<String, String>> pessoas = classAssistenciaDB.getDados();
        return pessoas;
    }
    //morada_obra
    private HashMap<String, String> get_assistencia(int n_obra, String cod_obra, int ano_obra){
        final String[] camposMorada = new String[] {"id","n_obra","cod_obra","ano_obra","codigo","artigo","contacto"};
        classAssistenciaDB.setNomeTabela("assistencia");
        String onde = "n_obra=? AND cod_obra=? AND ano_obra=?";
        String[] valores = new String[]{""+n_obra, cod_obra, ""+ano_obra};
        HashMap<String, String> assistencia = classAssistenciaDB.getDadosBy(onde, valores, camposMorada);
        return assistencia;
    }
}
