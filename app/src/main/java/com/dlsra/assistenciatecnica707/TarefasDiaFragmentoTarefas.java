package com.dlsra.assistenciatecnica707;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class TarefasDiaFragmentoTarefas extends Fragment {
    //elementos seccao
    private Context mContexto = null;
    private Activity mActividade = null;
    // constantes
    private String[] mCamposTarefa = null;
    private static final int mNChamadasServidor = 5;
    private static final int mTempoLancaFimChamadas = 60*1000;//segundos
    private static final int BANDABYTE = 4096;
    //elementos
    private View mView = null;
    private LinearLayout mLLSemtarefas = null;
    private SwipeRefreshLayout mSwipeRefreshLayout = null;
    private RecyclerView listaTarefasDia;
    private TextView tvSemTarefas;
    private ProgressBar pbFicheiros;
    private TextView tvDadoAReceber;
    //classes
    private ClasseInternetConectividade classeInternetConectividade;
    private ClasseDataTempo classeDataTempo = null;
    private DBAssistencia classeAssistenciaDB           = null;
    private ClasseEliminaDadosBD classeEliminaDadosBD   = null;
    // classe FicheiroCache
    private ClasseFicheiroCache classeFicheiroCacheUtilizador = null;
    private ClasseFicheiroCache classeFicheiroCacheEndereco   = null;
    //variaveis
    private LayoutInflater mLayoutInflater;
    private ViewGroup mContainer;
    private String mDataHoje = "";
    private int mNUtilizador  = 0;
    private int mIdUtilizador = 0;
    private String _FileNameUtilizador = "";
    private String _FileNameEndereco = "";
    private ArrayList<TarefaItemDetalhes> mListaItemDetalhes = null;
    private int mSomaNChamadasServidorActivas = 0;
    private int mSomaNChamadasServidorFinalizadas = 0;
    private Long mTempoInicioChamadas = (long) 0.0;
    private String pastaDepositoFotografias = "";
    private int mContaFotosCarregadas = 0;
    private int mContaFotosACarregadar = 0;
    //valores passados

    //classes em 2 plano
    private ProgressDialog progressoDialogo;
    private TarefasTask mTarefasTask     = null;
    private ProcessosTask mProcessosTask = null;
    private FicheirosTask mFicheirosTask = null;
    private ArrayList<DataFotografiaTask> mDataFotografiaTask = null;
    private ArrayList<DataDocumentoTask> mDataDocumentoTask   = null;

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 3;
    private OnListFragmentInteractionListener mLista;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TarefasDiaFragmentoTarefas() {
    }

    @SuppressWarnings("unused")
    public static TarefasDiaFragmentoTarefas newInstance(int columnCount) {
        TarefasDiaFragmentoTarefas fragment = new TarefasDiaFragmentoTarefas();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContexto = this.getContext();
        mActividade = this.getActivity();

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        try {
            Intent intent = getActivity().getIntent();
            Bundle bundle = intent.getExtras();
            mIdUtilizador = bundle.getInt("_id_utilizador");
            mNUtilizador  = bundle.getInt("_n_utilizador");
        } catch (Exception e){ Log.e("Valores Passados", e.toString());}

        carrega_constantes();
        //inicializa valores guardados
        carrega_valoresGuardados();
        //inicializa classes
        carrega_classes();
        //carrega valores nos ficheiros
        carrega_valoresFicheiros();
        //1º - data de hoje
        mDataHoje = classeDataTempo.data_hoje();
        pastaDepositoFotografias = mContexto.getString(R.string.pasta_deposito_fotografias)+"_"+mIdUtilizador;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mLayoutInflater = inflater;
        mContainer = container;

        //2º - carrega tarefas guardadas
        carrega_tarefas();

        //monta fragmento da pagina
        monta_fragmentoPagina();
        return mView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mLista = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(TarefaItemDetalhes item);
    }

    /**
     * carregamento dos objectos
     */
    private void carrega_elementos(){
    }

    private void implementa_inventos(){
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                mata2plano();
                refreshItems();
            }
        });
    }
    /**
     * carregamentos iniciais
     */
    private void carrega_constantes(){
        mCamposTarefa = new String[] {"id", "id_utilizador","n_pessoa","data","hora","n_obra","cod_obra","ano_obra","localidade","nota"};

    }
    private void carrega_classes(){
        classeInternetConectividade = new ClasseInternetConectividade(mActividade);
        classeDataTempo = new ClasseDataTempo();
        classeFicheiroCacheUtilizador = new ClasseFicheiroCache(mContexto, _FileNameUtilizador);
        classeFicheiroCacheEndereco = new ClasseFicheiroCache(mContexto, _FileNameEndereco);
        classeAssistenciaDB = new DBAssistencia(mContexto, "TAREFA", "tarefa");
        classeEliminaDadosBD = new ClasseEliminaDadosBD(mContexto, mIdUtilizador);
    }
    /**
     * carregamento de valores guardados
     */
    private void carrega_valoresGuardados() {
        //ficheiro guarda utilizador
        _FileNameUtilizador = getString(R.string.ficheiro_credenciais_utilizador);
        _FileNameEndereco = getString(R.string.ficheiro_endereco_electronico);
    }

    private void carrega_valoresFicheiros(){

    }
    /**
     * procedimentos
     */
    private void monta_fragmentoPagina(){
        mView               = mLayoutInflater.inflate(R.layout.tarefas_dia_fragmento_tarefas_list, mContainer, false);
        mLLSemtarefas       = (LinearLayout) mView.findViewById(R.id.llSemtarefas);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.RefreshTarefasDia2);
        listaTarefasDia     = (RecyclerView) mView.findViewById(R.id.listaTarefasDia);
        tvSemTarefas        = (TextView) mView.findViewById(R.id.tvSemtarefas);
        pbFicheiros         = (ProgressBar) mView.findViewById(R.id.pbFicheirosReceber);
        tvDadoAReceber      = (TextView) mView.findViewById(R.id.tvDadoAReceber);

        Toast.makeText(getContext(), "Nº Tarefas: "+mListaItemDetalhes.size(), Toast.LENGTH_SHORT).show();
        // carrega adaptador
        if (listaTarefasDia!=null) {
            recarrega_fragmentoPagina();

            RecyclerView.LayoutManager layout = new LinearLayoutManager(mContexto, LinearLayoutManager.VERTICAL, false);
            listaTarefasDia.setLayoutManager(layout);
        }
        implementa_inventos();
        pbFicheiros.setVisibility(View.GONE);
        tvDadoAReceber.setVisibility(View.GONE);
    }

    private void recarrega_fragmentoPagina(){
        if(tvSemTarefas != null && listaTarefasDia != null) {
            listaTarefasDia.removeAllViewsInLayout();//removes all the views
            if (0 == mListaItemDetalhes.size()) {
                tvSemTarefas.setVisibility(View.VISIBLE);
                tvSemTarefas.setText("Sem tarefas...");
                listaTarefasDia.setVisibility(View.GONE);
                listaTarefasDia.setAdapter(new TarefasDiaAdaptadorTarefas(new ArrayList<TarefaItemDetalhes>(), null, mContexto, mIdUtilizador, mNUtilizador));
            } else {
                RecyclerView.Adapter adapter = new TarefasDiaAdaptadorTarefas(mListaItemDetalhes, mLista, mContexto, mIdUtilizador, mNUtilizador);
                tvSemTarefas.setVisibility(View.GONE);
                tvSemTarefas.setText("");
                listaTarefasDia.setVisibility(View.VISIBLE);
                listaTarefasDia.setAdapter(adapter);

                // incluir o evento ao clicar tarefa
                // mostra dados da tarefa e processo
                listaTarefasDia.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int posicao = listaTarefasDia.indexOfChild(v);
                        TarefaItemDetalhes tarefa = mListaItemDetalhes.get(posicao);

                        Toast.makeText(mActividade, "Posição: "+posicao+" ("+tarefa.getN_obra()+"/"+tarefa.getCod_obra()+"/"+tarefa.getAno_obra()+")", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    /**
     *
     */
    private void refreshItems() {
        classeInternetConectividade.refaz();
        if(classeInternetConectividade.isLigado()) {
            // Load items
            mSwipeRefreshLayout.setRefreshing(true);
            // ...
            //1- realiza as chamadas ao servidor
            cria_chamadasServidor();
        }
        else{
            Toast.makeText(mActividade, "Não tem ligação à internet válida!", Toast.LENGTH_SHORT).show();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // ...
        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void finalizadaChamadasServidor(){
        if(mSomaNChamadasServidorActivas==mSomaNChamadasServidorFinalizadas){ mata2plano();}
    }

    private void refresca_dadosAApresentar(){
        carrega_tarefas();
        recarrega_fragmentoPagina();
    }

    private void carrega_tarefas() {
        mListaItemDetalhes = getTarefas();
    }

    private ArrayList<TarefaItemDetalhes> getTarefas(){
        ArrayList<TarefaItemDetalhes> resultado = new ArrayList<TarefaItemDetalhes>();

        String[] valores = new String[]{mDataHoje, ""+mNUtilizador};

        ArrayList<HashMap<String, String>> ListaTarefas = classeAssistenciaDB.preencheDados(mCamposTarefa, "data=? AND n_pessoa=?", valores, "hora ASC", "tarefa");

        if(ListaTarefas.isEmpty()){}
        else{
            TarefaItemDetalhes item_details;
            //for (int i = 0; i < ListaTarefas.size(); i++) {
            //HashMap<String, String> t = ListaTarefas.get(i);
            for(HashMap<String, String> t : ListaTarefas){
                item_details = new TarefaItemDetalhes();

                item_details.set_id(Integer.parseInt(t.get("id")));
                item_details.setData(t.get("data"));
                item_details.setHora(t.get("hora"));
                item_details.setN_obra(Integer.parseInt(t.get("n_obra")));
                item_details.setCod_obra(t.get("cod_obra"));
                item_details.setAno_obra(Integer.parseInt(t.get("ano_obra")));
                item_details.setLocalidade(t.get("localidade"));
                item_details.setNota(t.get("nota"));
                resultado.add(item_details);
            }
        }

        /*/
        TarefaItemDetalhes item_details = new TarefaItemDetalhes();
        item_details.set_id(1);
        item_details.setData("2016-02-04");
        item_details.setHora("08:50");
        item_details.setN_obra(23);
        item_details.setCod_obra("CMAE");
        item_details.setAno_obra(2016);
        item_details.setLocalidade("Agueda");
        item_details.setNota("Teste nota 1 \nlinha 2");
        resultado.add(item_details);

        item_details = new TarefaItemDetalhes();
        item_details.set_id(2);
        item_details.setData("2016-02-04");
        item_details.setHora("10:30");
        item_details.setN_obra(45);
        item_details.setCod_obra("EPAE");
        item_details.setAno_obra(2016);
        item_details.setLocalidade("Guarda");
        item_details.setNota("Teste nota 2 \nPara finalizar, é possivel identificar o item da lista que foi clicado. Para isso deve ser incluido no projeto a classe RecyclerItemClickListener. Na classe MainActivity.java adicionamos o seguinte método, que irá exibir um toast com o nome do país que foi clicado.");
        resultado.add(item_details);
        /**/
        return resultado;
    }

    /**
     * *****************************************************************************************************************************************
     * Parte
     *
     * criacao chamadas ao servidor
     *
     * baixar dados
     * *****************************************************************************************************************************************
     */
    private void cria_chamadasServidor(){
        cria_dialogo();
        mTempoInicioChamadas = System.currentTimeMillis()/1000;
        mSomaNChamadasServidorActivas = 0;
        mSomaNChamadasServidorFinalizadas = 0;

        //passa a baixar dados das tarefas
        if(null!=mTarefasTask){ mTarefasTask.cancel(true); mTarefasTask=null;}
        mTarefasTask = new TarefasTask(mActividade, mIdUtilizador, mNUtilizador, mDataHoje, progressoDialogo);
        mTarefasTask.execute((Void) null);

        cria_setTimeOut();
    }

    private void cria_dialogo(){
        // mostra um dialogo da progressao do carregamento das tarefa
        progressoDialogo = new ProgressDialog(mContexto);
        progressoDialogo.setCancelable(true);
        progressoDialogo.setMessage("Descarregar Tarefas");
        progressoDialogo.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //progressoDialogo.setIndeterminate(true);
        progressoDialogo.setProgress(0);
        progressoDialogo.setMax(7);
    }

    private void cria_setTimeOut(){
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        Log.i("tag","Lança chamada depois de 60 segundos...");
                        if(progressoDialogo.isShowing()) {
                            Toast.makeText(mActividade, "Ligação ao servidor interrompida!", Toast.LENGTH_SHORT).show();
                            mata2plano();
                        }
                    }
                }, mTempoLancaFimChamadas);
    }

    private void mata2plano(){
        if(null!=mTarefasTask){ mTarefasTask.cancel(true);}
        if(null!=progressoDialogo && progressoDialogo.isShowing()) {
            try{
                progressoDialogo.dismiss();
            }
            catch (Exception e){ Log.e("Erro: ", e.toString());}
        }
        if(null!=mSwipeRefreshLayout){ mSwipeRefreshLayout.setRefreshing(false);}
    }

    /**
     * Sincronizador para actualizar a lista das tarefas
     * chama o ProcessosTask e FicheirosTask
     */
    private class TarefasTask extends AsyncTask<Void, Void, String> {
        private Activity mActividade;
        private int _mIdUtilizador;
        private int _mNumero;
        private String mData;

        private String enderecoURL;
        private String pasta_servidor;
        private HttpURLConnection connection;
        private URL url;
        private ProgressDialog dialogo;

        TarefasTask(Activity actividade, int id_utilizador, int numero, String data, ProgressDialog dialogo) {
            this.mActividade = actividade;
            this._mIdUtilizador = id_utilizador;
            this._mNumero = numero;
            this.mData = data;
            this.dialogo = dialogo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                //inicializa o dialogo progresso
                if (null!=this.dialogo) { this.dialogo.show();}
                this.dialogo.setMessage("(4) A colectar tarefas...");

                enderecoURL = classeFicheiroCacheEndereco.getTextoFicheiro();

                //no caso de nao ter uma url valida, regista a url por defeito
                if(enderecoURL.equals("")){}
                else{
                    pasta_servidor = getString(R.string.pasta_no_servidor);

                    //cria a ligacao
                    String endereco = enderecoURL+"/"+pasta_servidor+"/tarefas/tarefas.php?numero="+_mNumero+"&data="+this.mData;
                    url = new URL(endereco);
                    Log.i("Endereço", endereco);
                }
            }
            catch (MalformedURLException e) { e.printStackTrace();}

            try {
                //traca a ligacao
                connection = (HttpURLConnection)url.openConnection();
                mSomaNChamadasServidorActivas++;
            } catch (IOException e1) { e1.printStackTrace();}
        }

        @Override
        protected String doInBackground(Void... params) {
            String resultadoUrl = "";

            //busca a confirmacao numa ligacao ao servidor
            try {
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK ) {
                    Log.i("Fez Ligação", "Página tarefas existe!");

                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    if (in != null) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                        String line = "";
                        while ((line = bufferedReader.readLine()) != null) {
                            resultadoUrl += line;
                        }
                        resultadoUrl = resultadoUrl.trim();
                    }
                    in.close();

                }
                else if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND ) {
                    Log.i("Fez Ligação", "Página tarefas não existe!");
                }
            } catch (IOException e1) { e1.printStackTrace();}

            return resultadoUrl;
        }

        @Override
        protected void onPostExecute(final String resultado) {
            mSomaNChamadasServidorFinalizadas++;
            Log.i("Tarefas", "passa joson");

            // ok - obteve ligacao ao servidor, pode eliminar os dados na bd
            //1- elimina dados existentes
            classeEliminaDadosBD.elimina_dados(1);

            try {
                this.dialogo.incrementProgressBy(1);

                JSONObject jsonRootObject = new JSONObject(resultado);
                //Get the instance of JSONArray that contains JSONObjects
                JSONArray jsonArray = jsonRootObject.optJSONArray("tarefas");

                if(null!=jsonArray && 0<jsonArray.length()) {
                    int id = classeAssistenciaDB.get_proxID("tarefa");
                    //Iterate the jsonArray and print the info of JSONObjects
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String data = jsonObject.optString("data");
                        String hora = jsonObject.optString("hora");
                        String obra = jsonObject.optString("cod_proc");
                        String localidade = jsonObject.optString("localidade");
                        String nota = jsonObject.optString("nota");

                        int n_obra = 0;
                        String cod_obra = "";
                        int ano_obra = 0;

                        String[] partes = obra.split(" ");
                        if (3 == partes.length) {
                            n_obra = Integer.parseInt(partes[0]);
                            cod_obra = partes[1];
                            ano_obra = Integer.parseInt(partes[2]);
                        }

                        ContentValues valores = new ContentValues();
                        valores.put("id", id+i);
                        valores.put("id_utilizador", _mIdUtilizador);
                        valores.put("n_pessoa", _mNumero);
                        valores.put("data", data);
                        valores.put("hora", hora);
                        valores.put("n_obra", n_obra);
                        valores.put("cod_obra", cod_obra);
                        valores.put("ano_obra", ano_obra);
                        valores.put("localidade", localidade);
                        valores.put("nota", nota);
                        classeAssistenciaDB.insert(valores, "tarefa");
                    }

                    prepara_chamadaDadosProcessos();
                }
                else{
                    finalizadaChamadasServidor();
                    Toast.makeText(this.mActividade, "Sem Tarefas agendadas/a baixar!", Toast.LENGTH_SHORT).show();
                }

                //refresca os dados a apresentar
                refresca_dadosAApresentar();

                this.dialogo.incrementProgressBy(1);
            }
            catch (JSONException e) { Log.e("Tarefas (JSON)", e.toString());}
            catch (Exception e) { Log.e("Tarefas", e.toString());}
        }


        /**
         * É chamado quando a execucao é cancelada
         */
        @Override
        protected void onCancelled() {
            String descricao = "O pedido de sincronização das tarefas foi interrompido!";
            //Snackbar.make(srlTarefas, descricao, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            Toast.makeText(this.mActividade, descricao, Toast.LENGTH_SHORT).show();
            // desliga e esconde o dialogo
            if (null!=this.dialogo) { this.dialogo.dismiss();}
            mSomaNChamadasServidorFinalizadas++;
        }
    }

    /**
     * Dados dos processos
     */
    private void prepara_chamadaDadosProcessos(){
        //passa para descarregar dados dos processos
        if(null!=mProcessosTask){ mProcessosTask.cancel(true); mProcessosTask=null;}
        mProcessosTask = new ProcessosTask(this.mActividade, this.mIdUtilizador, this.mNUtilizador, this.mDataHoje, progressoDialogo);
        mProcessosTask.execute((Void) null);
    }

    /**
     * Sincronizador para actualizar a lista dos processos
     * não chama AsyncTask's
     */
    private class ProcessosTask extends AsyncTask<Void, Void, String> {
        private Activity mActividade;
        private int mIdUtilizador;
        private int mNumero;
        private String mData;

        private String enderecoURL;
        private String pasta_servidor;
        private HttpURLConnection connection;
        private URL url;
        private ProgressDialog dialogo;

        ProcessosTask(Activity actividade, int id_utilizador, int numero, String data, ProgressDialog dialogo) {
            this.mActividade = actividade;
            this.mIdUtilizador = id_utilizador;
            this.mNumero = numero;
            this.mData = data;
            this.dialogo = dialogo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                //inicializa o dialogo progresso
                if (null!=this.dialogo) { this.dialogo.show();}
                this.dialogo.setMessage("(3) A colectar processos...");

                enderecoURL = classeFicheiroCacheEndereco.getTextoFicheiro();

                //no caso de nao ter uma url valida, regista a url por defeito
                if(enderecoURL.equals("")){}
                else{
                    pasta_servidor = getString(R.string.pasta_no_servidor);

                    //cria a ligacao
                    String endereco = enderecoURL+"/"+pasta_servidor+"/tarefas/processos.php?numero="+this.mNumero+"&data="+this.mData;
                    url = new URL(endereco);
                    Log.i("Endereço", endereco);
                }
            } catch (MalformedURLException e) { e.printStackTrace();}

            try {
                //traca a ligacao
                connection = (HttpURLConnection)url.openConnection();
                mSomaNChamadasServidorActivas++;
            }
            catch (IOException e1) { e1.printStackTrace();}
        }

        @Override
        protected String doInBackground(Void... params) {
            String resultadoUrl = "";

            //busca lista com dados dos processo
            try {
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK ) {
                    Log.i("Fez Ligação", "Página processo existe!");

                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    if (in != null) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                        String line = "";

                        while ((line = bufferedReader.readLine()) != null) {
                            resultadoUrl += line;
                        }
                        resultadoUrl = resultadoUrl.trim();
                    }
                    in.close();

                }
                else if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND ) {
                    Log.i("Fez Ligação", "Página processos não existe!");
                }
            }
            catch (IOException e1) { e1.printStackTrace();}

            return resultadoUrl;
        }

        @Override
        protected void onPostExecute(final String resultado) {
            mSomaNChamadasServidorFinalizadas++;
            Log.i("Processos", "passa json");

            try {
                this.dialogo.incrementProgressBy(1);
                String tabela;
                String[] campos;
                //processo
                tabela = "processo";
                campos = new String[]{"n_obra", "cod_obra", "ano_obra", "categoria", "estado", "descricao", "mediador", "referencia", "franquia", "franquia_pago", "danos_esteticos", "tipo_base"};
                passaValoresTabelas(resultado, tabela, campos);
                //seguro
                tabela = "seguro";
                campos = new String[]{"n_obra", "cod_obra", "ano_obra", "companhia", "apolice", "n_sinistro", "referencias", "produto", "contacto"};
                passaValoresTabelas(resultado, tabela, campos);
                //seguro
                tabela = "pessoa";
                campos = new String[]{"n_obra", "cod_obra", "ano_obra", "titulo", "id_pessoa", "nome", "morada", "contactos", "outros_contactos", "contribuinte", "gps", "companhia"};
                passaValoresTabelas(resultado, tabela, campos);
                //seguro
                tabela = "morada_obra";
                campos = new String[]{"n_obra", "cod_obra", "ano_obra", "morada"};
                passaValoresTabelas(resultado, tabela, campos);
                //trabalhos
                tabela = "trabalhos";
                campos = new String[]{"n_obra", "cod_obra", "ano_obra", "descricao"};
                passaValoresTabelas(resultado, tabela, campos);
                //assistencia
                tabela = "assistencia";
                campos = new String[]{"n_obra", "cod_obra", "ano_obra", "codigo", "artigo", "contacto"};
                passaValoresTabelas(resultado, tabela, campos);

                this.dialogo.incrementProgressBy(1);

                prepara_chamadaDadosFicheiros();
            } catch (Exception e){ Log.e("Registar Processos", e.toString());}
        }

        /**
         * É chamado quando a execucao é cancelada
         */
        @Override
        protected void onCancelled() {
            mSomaNChamadasServidorFinalizadas++;

            // desliga e esconde o dialogo
            if (null!=this.dialogo) { this.dialogo.dismiss();}
        }

        private void passaValoresTabelas(String resultado, String tabela, String[] campos){
            try {
                JSONObject jsonRootObject = new JSONObject(resultado);
                //Get the instance of JSONArray that contains JSONObjects
                JSONArray jsonArray = jsonRootObject.optJSONArray(tabela);

                if(null != jsonArray) {
                    int id = classeAssistenciaDB.get_proxID(tabela);
                    //Iterate the jsonArray and print the info of JSONObjects
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        ContentValues valores = new ContentValues();
                        valores.put("id", id+i);
                        valores.put("id_utilizador", this.mIdUtilizador);
                        valores.put("dia_tarefa", this.mData);
                        for (String campo : campos) {
                            String valor = jsonObject.optString(campo);
                            valores.put(campo, valor);
                        }

                        classeAssistenciaDB.insert(valores, tabela);
                    }
                }
            } catch (JSONException e) { Log.e("Processos "+tabela, e.toString());}
            catch (Exception e) { Log.e("Processos "+tabela, e.toString());}
        }
    }

    /**
     * Dados dos ficheiros
     */
    private void prepara_chamadaDadosFicheiros(){
        //passa a descarregar dados dos ficheiros armazenados
        if(null!=mFicheirosTask){mFicheirosTask.cancel(true); mFicheirosTask=null;}
        mFicheirosTask =  new FicheirosTask(this.mActividade, this.mIdUtilizador, this.mNUtilizador, this.mDataHoje, progressoDialogo);
        mFicheirosTask.execute((Void) null);
    }

    /**
     * Sincronizador para devolver os ficheiros dos processos
     * chama varios DataFotografiaTask, varios DataDocumentoTask
     */
    private class FicheirosTask extends AsyncTask<Void, Void, String> {
        private Activity mActividade;
        private int mIdUtilizador;
        private int mNumero;
        private String mData;

        private String enderecoURL;
        private String pasta_servidor;
        private HttpURLConnection connection;
        private URL url;
        private ProgressDialog dialogo;

        FicheirosTask(Activity actividade, int id_utilizador, int numero, String data, ProgressDialog dialogo) {
            this.mActividade = actividade;
            this.mIdUtilizador = id_utilizador;
            this.mNumero = numero;
            this.mData = data;
            this.dialogo = dialogo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                //inicializa o dialogo progresso
                if (null != this.dialogo) {
                    this.dialogo.show();
                }
                this.dialogo.setMessage("(2) Lista de ficheiros...");

                enderecoURL = classeFicheiroCacheEndereco.getTextoFicheiro();

                //no caso de nao ter uma url valida, regista a url por defeito
                if (enderecoURL.equals("")) {}
                else {
                    pasta_servidor = getString(R.string.pasta_no_servidor);

                    //cria a ligacao
                    String endereco = enderecoURL + "/" + pasta_servidor + "/processos/ficheiros.php?opt=lista&numero=" + this.mNumero + "&data=" + this.mData;
                    url = new URL(endereco);
                    Log.i("Endereço", endereco);
                }
            }
            catch (MalformedURLException e) { e.printStackTrace();}
            catch (NullPointerException e){ e.printStackTrace();}

            try {
                //tranca a ligacao
                connection = (HttpURLConnection) url.openConnection();
                mSomaNChamadasServidorActivas++;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            String resultadoURL = "";

            try {
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Log.i("Fez Ligação", "Página ficheiros existe!");

                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                    String line = "";

                    while ((line = bufferedReader.readLine()) != null) {
                        resultadoURL += line;
                    }
                    in.close();

                } else if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                    Log.i("Fez Ligação", "Página ficheiros não existe!");
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return resultadoURL;
        }

        @Override
        protected void onPostExecute(final String resultadoURL) {
            mSomaNChamadasServidorFinalizadas++;
            Log.i("Ficheiros", "passa json");

            ArrayList<ClasseFotografias> fotografias = null;
            ArrayList<ClasseDocumentos> documentos = null;

            try {
                this.dialogo.setMessage("(1) A finalizar a colecta...");
                this.dialogo.incrementProgressBy(1);

                fotografias = dame_listaFotografias(resultadoURL);
                this.dialogo.incrementProgressBy(1);

                documentos = dame_listaDocumentos(resultadoURL);
                this.dialogo.incrementProgressBy(1);

                Log.i("Fotografias", "São " + fotografias.size());
                Log.i("Documentos", "São " + documentos.size());

                ClasseFicheiros cFicheiros = new ClasseFicheiros();
                cFicheiros.setDocumentos(documentos);
                cFicheiros.setFotografias(fotografias);
                // desliga e esconde o dialogo
                if (null != this.dialogo) { this.dialogo.dismiss();}

                prepara_chamadasConteudosFicheiros(cFicheiros);
            } catch (Exception e) {
                Log.e("Obter Ficheiros", e.toString());
            }
        }

        /**
         * É chamado quando a execucao é cancelada
         */
        @Override
        protected void onCancelled() {
            mSomaNChamadasServidorFinalizadas++;
            // desliga e esconde o dialogo
            if (null != this.dialogo) {
                this.dialogo.dismiss();
            }
        }

        /**
         * procedimentos
         */
        private ArrayList<ClasseFotografias> dame_listaFotografias(String resultadoUrl) {
            ArrayList<ClasseFotografias> fotografias = new ArrayList<>();
            if(!resultadoUrl.equals("")) {
                try {
                    JSONObject jsonRootObject = new JSONObject(resultadoUrl);
                    //Get the instance of JSONArray that contains JSONObjects
                    JSONArray jsonArray = jsonRootObject.optJSONArray("fotografias");

                    if(null != jsonArray) {
                        String[] campos = new String[]{"n_obra","cod_obra","ano_obra","nome_ficheiro","pasta","tipologia","descricao","por","data_registo"};

                        //Iterate the jsonArray and print the info of JSONObjects
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            ClasseFotografias fotografia = new ClasseFotografias();
                            fotografia.put("id", 0);
                            fotografia.put("origem", 1);
                            fotografia.put("id_utilizador", mIdUtilizador);
                            fotografia.put("dia_tarefa", mDataHoje);
                            for (String campo : campos) {
                                String valor = jsonObject.optString(campo);
                                fotografia.put(campo, valor);
                            }

                            fotografias.add(fotografia);
                        }
                    }
                }
                catch (JSONException e) { Log.e("Fotografias ", e.toString());}
                catch (Exception e) { Log.e("Fotografias ", e.toString());}
            }
            return fotografias;
        }

        private ArrayList<ClasseDocumentos> dame_listaDocumentos(String resultadoUrl) {
            ArrayList<ClasseDocumentos> documentos = new ArrayList<>();
            if(!resultadoUrl.equals("")) {
                try {
                    JSONObject jsonRootObject = new JSONObject(resultadoUrl);
                    //Get the instance of JSONArray that contains JSONObjects
                    JSONArray jsonArray = jsonRootObject.optJSONArray("documentos");

                    if(null != jsonArray) {
                        String[] campos = new String[]{"n_obra","cod_obra","ano_obra","nome_ficheiro","tipologia","descricao","por","data_registo"};

                        //Iterate the jsonArray and print the info of JSONObjects
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            ClasseDocumentos documento = new ClasseDocumentos();
                            documento.put("id", 0);
                            documento.put("id_utilizador", mIdUtilizador);
                            documento.put("dia_tarefa", mDataHoje);
                            documento.put("origem", 1);
                            for (String campo : campos) {
                                String valor = jsonObject.optString(campo).toString();
                                documento.put(campo, valor);
                            }

                            documentos.add(documento);
                        }

                    }
                } catch (JSONException e) {
                    Log.e("Documentos ", e.toString());
                } catch (Exception e) {
                    Log.e("Documentos ", e.toString());
                }
            }
            return documentos;
        }
    }

    /**
     * conteudo dos ficheiros
     */
    private void prepara_chamadasConteudosFicheiros(ClasseFicheiros classeFicheiros){
        int nFicheirosBaixar = classeFicheiros.size();
        //se tem ficheiros a descarregar
        if(0==nFicheirosBaixar){ mata2plano();}
        else{
            //chegou aqui correu tudo bem
            // vamos zerar as contagens
            if(null!=mSwipeRefreshLayout){ mSwipeRefreshLayout.setRefreshing(false);}
            mSomaNChamadasServidorActivas = 0;
            mSomaNChamadasServidorFinalizadas = 0;
            mDataFotografiaTask = new ArrayList<>();
            mDataDocumentoTask  = new ArrayList<>();

            //1- percorre as fotografias
            mContaFotosCarregadas = 0;
            mContaFotosACarregadar = 0;
            //1.1 - obtem lista dos dados armazenados na bd
            ArrayList<ClasseFotografias> fotografiasBD = classeAssistenciaDB.get_fotografias(0, "", 0, false);

            //1.1.1 -
            ArrayList<ClasseFotografias> cFotografias = classeFicheiros.getFotografias();
            ClasseFotografias cFotografia;
            int nFotografias = cFotografias.size();
            for (int conta = 0; conta < nFotografias; conta++) {
                cFotografia            = cFotografias.get(conta);
                String pasta           = cFotografia.retreat("pasta");
                String n_obra          = cFotografia.retreat("n_obra");
                String cod_obra        = cFotografia.retreat("cod_obra");
                String ano_obra        = cFotografia.retreat("ano_obra");
                String nome_ficheiro   = cFotografia.retreat("nome_ficheiro");

                try {
                    boolean para_descarregar = true;

                    //vefifica se tem registos...
                    if(fotografiasBD.isEmpty() || 0==fotografiasBD.size()){ /* ignora */}
                    else{
                        //verifica se existe registo desta fotografia, retorna o id do registo
                        ClasseFotografias existeRegistoBD = verificaRegistoDBFotografia(Integer.parseInt(n_obra), cod_obra, Integer.parseInt(ano_obra), pasta, nome_ficheiro, fotografiasBD);
                        //actualiza o objecto fotografia com o id
                        if (null != existeRegistoBD) {
                            cFotografia.setId(existeRegistoBD.getId());
                            //verifica se existe o ficheiro na pasta
                            String nome_temporario = existeRegistoBD.getNome_temporario();
                            boolean existeFicheiro = verificaExisteFicheiro(nome_temporario, pastaDepositoFotografias);
                            if (existeFicheiro) { para_descarregar = false;}
                        }
                    }

                    if(para_descarregar){
                        DataFotografiaTask tarefaD = new DataFotografiaTask(cFotografia, conta+1, this.mActividade);
                        //tarefaD.execute(pastaDepositoFotografias);
                        mDataFotografiaTask.add(tarefaD);

                        mContaFotosACarregadar ++;
                    }
                    else{
                        //Log.i("Fotografia", nome_ficheiro+": Existe!");
                    }
                } catch (Exception e) { Log.e("Erro descarga", "Descarregar fotografia "+conta+", "+e.toString());}
            }
            fotografiasBD = null;

            //1.2 - obtem lista dos dados armazenados
            ArrayList<ClasseDocumentos> documentos = classeAssistenciaDB.get_documentos(0, "", 0);
            //1.2.1- percorre os documentos
            ArrayList<ClasseDocumentos> cDocumentos = classeFicheiros.getDocumentos();
            ClasseDocumentos cDocumento;
            int nDocumentos = cDocumentos.size();
            for (int conta = 0; conta < nDocumentos; conta++) {

                cDocumento = cDocumentos.get(conta);
                String n_obra = cDocumento.retreat("n_obra");
                String cod_obra = cDocumento.retreat("cod_obra");
                String ano_obra = cDocumento.retreat("ano_obra");
                String nome_ficheiro = cDocumento.retreat("nome_ficheiro");

                try {
                    //verifica se existe registo deste documento
                    boolean existeRegisto = verificaRegistoDBDocumentos(Integer.parseInt(n_obra), cod_obra, Integer.parseInt(ano_obra), nome_ficheiro, documentos);
                    //File raiz = new File(this.mActividade.getFilesDir(), pasta);
                    //File file = new File(raiz, nome_ficheiro);

                    //if(existeRegisto && file.isFile()){
                    if(existeRegisto){
                        Log.i("Documento", nome_ficheiro + ": Existe!");
                    }
                    else {
                        //parteURL = parteURL + "/" + nome_ficheiro.replace(" ", "%20");//URLEncoder.encode(nome_ficheiro, "utf-8");
                        DataDocumentoTask tarefaD = new DataDocumentoTask(cDocumento, this.mActividade, conta+1);
                        //tarefaD.execute();
                        mDataDocumentoTask.add(tarefaD);
                    }
                } catch (Exception e) { Log.e("Erro descarga", "Descarregar documento "+conta+", "+e.toString());}
            }
            documentos = null;

            //1.3 - realiza as tarefas
            int aBaixar = mDataFotografiaTask.size()+mDataDocumentoTask.size();
            //1.3.1 - monta um novo dialogo
            pbFicheiros.setVisibility(View.VISIBLE);
            pbFicheiros.setMax(aBaixar);
            tvDadoAReceber.setVisibility(View.VISIBLE);
            tvDadoAReceber.setText("A baixar ficheiros. Em espera de ligação ao servidor...");

            //1.3.2 - fotografias
            for(DataFotografiaTask tarefa:mDataFotografiaTask){
                tarefa.execute(pastaDepositoFotografias);
            }
            //1.3.3 - documentos
            for(DataDocumentoTask tarefa:mDataDocumentoTask){
                tarefa.execute();
            }


            fazExcondeDialogo();
        }
    }

    /**
     * Verificacao da existencia de registos
     */
    private ClasseFotografias verificaRegistoDBFotografia(int n_obra, String cod_obra, int ano_obra, String pasta, String nome_ficheiro, ArrayList<ClasseFotografias> fotografias){
        if(!fotografias.isEmpty()){
            for(ClasseFotografias fotografia:fotografias){
                if(n_obra == fotografia.getN_obra() && ano_obra == fotografia.getAno_obra()){
                    if(fotografia.getNome_ficheiro().equals(nome_ficheiro)) {
                        if (fotografia.getPasta().equals(pasta)) {
                            if (fotografia.getCod_obra().equals(cod_obra)) {
                                return fotografia;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private boolean verificaRegistoDBDocumentos(int n_obra, String cod_obra, int ano_obra, String nome_ficheiro, ArrayList<ClasseDocumentos> documentos){
        boolean existe = false;
        if(!documentos.isEmpty()){
            for(ClasseDocumentos documento:documentos){
                if(n_obra == documento.getN_obra() && ano_obra == documento.getAno_obra()) {
                    if (documento.getNome_ficheiro().equals(nome_ficheiro)) {
                        if (documento.getCod_obra().equals(cod_obra)) {
                            existe = true;
                            break;
                        }
                    }
                }
            }
        }
        return existe;
    }

    private boolean verificaExisteFicheiro(String nome_ficheiro, String pasta){
        boolean existe = false;

        File raiz = new File(this.mActividade.getFilesDir(), pasta);
        if(null != raiz && raiz.isDirectory()) {
            //abre/cria o ficheiro no directorio aberto
            File ficheiro = new File(raiz, nome_ficheiro);
            existe = null != ficheiro && ficheiro.exists();

        }
        return existe;
    }

    private void fazExcondeDialogo(){
        if(mSomaNChamadasServidorFinalizadas == pbFicheiros.getMax() || pbFicheiros.getProgress() == pbFicheiros.getMax()){
            pbFicheiros.setVisibility(View.GONE);
            tvDadoAReceber.setVisibility(View.GONE);
            int falta = this.mContaFotosACarregadar - this.mContaFotosCarregadas;
            if(0 < falta){
                Toast.makeText(this.mActividade, "Falta descarregar " + falta + " fotografias!", Toast.LENGTH_SHORT).show();
            }
        }
        /*
        if (null!=progressoDialogo) {
            int nTotal = progressoDialogo.getMax();
            if (progressoDialogo.getProgress() == nTotal || mSomaNChamadasServidorFinalizadas == nTotal) {
                progressoDialogo.dismiss();

                int falta = this.mContaFotosACarregadar - this.mContaFotosCarregadas;
                if(0 < falta){
                    Toast.makeText(this.mActividade, "Falta descarregar " + falta + " fotografias!", Toast.LENGTH_SHORT).show();
                }
            }
        }
        */
    }

    /**
     * Sincronizador para descarregar fotografias do servidor
     * não chama AsyncTask's
     */
    private class DataFotografiaTask extends AsyncTask<String, Void, Integer> {
        private int mIdFoto = 0;
        private String mNomeFicheiro = "";
        private String mNomeTemporario = "";
        private ClasseFotografias cFotografia = null;
        private int ordem = 0;
        private Context contexto = null;

        private String enderecoURL;

        DataFotografiaTask(ClasseFotografias cFotografia, int ordem, Context contexto) {
            this.cFotografia = cFotografia;
            this.ordem = ordem;
            this.contexto = contexto;

            if(null!=cFotografia){
                this.mIdFoto         = cFotografia.getId();
                this.mNomeFicheiro   = cFotografia.getNome_ficheiro();
                this.mNomeTemporario = cFotografia.getNome_temporario();
                Boolean tem_registo  = true;
                //Log.i("Foto 1", this.mIdFoto+"/"+this.mNomeFicheiro+"/"+mNomeTemporario);

                String extensao = this.mNomeFicheiro.substring(this.mNomeFicheiro.length() - 3);
                /* se tem registo id, se 0 faz registo do dados*/
                if(0==this.mIdFoto){
                    this.mIdFoto = classeAssistenciaDB.get_proxID("fotografia");
                    this.mNomeTemporario = this.mIdFoto+"."+extensao;
                    tem_registo = registaFotografiaDB(this.cFotografia, this.mIdFoto, this.mNomeTemporario);
                }else if(this.mNomeTemporario.equals("")){ this.mNomeTemporario = this.mIdFoto+"."+extensao;}
                //Log.i("Foto 2", this.mIdFoto+"/"+this.mNomeFicheiro+"/"+mNomeTemporario);
                /* caso nao registou, anula o id dado, para nao realizar o download */
                if(!tem_registo){ this.mIdFoto = 0;}

                //Log.i("Foto 3", this.mIdFoto+"/"+this.mNomeFicheiro+"/"+mNomeTemporario);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            enderecoURL = classeFicheiroCacheEndereco.getTextoFicheiro();

            //no caso de nao ter uma url valida, regista a url por defeito
            if(enderecoURL.equals("")){}
            else {
                /* regista fotografia na base de dados */
                try {
                    //cria a ligacao
                    enderecoURL += "/android_extranet";
                    enderecoURL += "/processos/ficheiro.php?";
                    //enderecoURL += "opt=img";
                    enderecoURL += "&ordem=" + this.ordem;
                    enderecoURL += "&n_obra=" + cFotografia.getN_obra();
                    enderecoURL += "&ano_obra=" + cFotografia.getAno_obra();
                    enderecoURL += "&cod_obra=" + cFotografia.getCod_obra();
                    enderecoURL += "&nome=" + URLEncoder.encode(this.mNomeFicheiro, "UTF8");
                    enderecoURL += "&pasta=" + URLEncoder.encode(cFotografia.getPasta(), "UTF8");
                    enderecoURL += "&rnd=" + Math.random();
                } catch (UnsupportedEncodingException ignora) {}
            }
            mSomaNChamadasServidorActivas++;

        }
        @Override
        protected Integer doInBackground(String... params) {
            Integer sucesso = 0;
            InputStream input = null;
            OutputStream output = null;

            File raiz = null;
            File ficheiro = null;

            if(0 < this.mIdFoto) {
                try {
                    //pede para fazer copia da imagem em outra pasta (temporaria)
                    URL url = new URL(enderecoURL);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    // just want to do an HTTP GET here
                    urlConnection.setRequestMethod("GET");
                    // uncomment this if you want to write output to this url
                    //connection.setDoOutput(true);
                    //urlConnection.setDoInput(true);
                    // give it 1 seconds to respond
                    urlConnection.setReadTimeout(1 * 1000);
                    urlConnection.connect();
                    int statusCode = urlConnection.getResponseCode();
                    if (statusCode == HttpURLConnection.HTTP_OK) {
                        input = urlConnection.getInputStream();

                        //1- numa pasta
                        // abre/cria o directorio para deposito, relacionado com o processo
                        raiz = new File(this.contexto.getFilesDir(), params[0]);
                        if(!raiz.exists()){ boolean b = raiz.mkdirs();}
                        //raiz.getParentFile().mkdirs();//caso nao exista cria
                        if (raiz.exists()){
                            //abre/cria o ficheiro no directorio aberto
                            ficheiro = new File(raiz, this.mNomeTemporario);
                            if(!ficheiro.exists()){ boolean c = ficheiro.createNewFile();}
                            if(ficheiro.exists()) {
                                //escreve no ficheiro
                                output = new FileOutputStream(ficheiro);
                                byte data[] = new byte[BANDABYTE];
                                int count;
                                while ((count = input.read(data)) != -1) {
                                    output.write(data, 0, count);
                                    sucesso = 1;
                                }
                            } else {
                                Log.e("Directório", "Não abre ficheiro!");
                                sucesso = 0;
                            }
                        } else {
                            Log.e("Directório", "Não abre a raiz!");
                            sucesso = 0;
                        }
                    }
                    urlConnection = null;
                    url = null;
                } catch (IOException e) {
                    Log.e("Baixar fotografia", e.toString());
                } catch (Exception e) {
                    Log.e("Baixar fotografia", e.toString());
                } finally {
                    try {
                        if (null != output) {
                            output.close();
                            output = null;
                        }
                        if (null != input) {
                            input.close();
                            input = null;
                        }
                        if (null != raiz) {
                            raiz = null;
                        }
                        if (null != ficheiro) {
                            ficheiro = null;
                        }
                    } catch (IOException ignora) {
                    }
                }
            }
            return sucesso;
        }

        @Override
        protected void onPostExecute(Integer resultado) {
            mSomaNChamadasServidorFinalizadas++;
            //mostra no dialogo o nome do ficheiro
            //progressoDialogo.setMessage(this.mNomeFicheiro);
            //progressoDialogo.incrementProgressBy(1);
            pbFicheiros.incrementProgressBy(1);
            tvDadoAReceber.setText("("+mSomaNChamadasServidorFinalizadas+" de "+pbFicheiros.getMax()+") "+mNomeFicheiro);

            try {
                //caso deu erro ao criar o ficheiro, elimina os dados da fotografia
                if(0 < resultado){
                    //actualiza
                    mContaFotosCarregadas++;
                }
                //actualiza registo da fotografia
                ContentValues valores = new ContentValues();
                valores.put("nome_temporario", this.mNomeTemporario);
                classeAssistenciaDB.updateDados(valores, "id", ""+this.mIdFoto, "fotografia");

                // desliga e esconde o dialogo
                fazExcondeDialogo();
            }
            catch (WindowManager.BadTokenException be){}
            catch (Exception e){}
        }

        /**
         * É chamado quando a execucao é cancelada
         */
        @Override
        protected void onCancelled() {
            //String descricao = "O pedido de sincronização dos ficheiros foi interrompido!";
            //Toast.makeText(this.contexto, descricao, Toast.LENGTH_SHORT).show();
            // desliga e esconde o dialogo
            mSomaNChamadasServidorFinalizadas++;
            //mostra no dialogo o nome do ficheiro
            //progressoDialogo.setMessage(this.mNomeFicheiro);
            //progressoDialogo.incrementProgressBy(1);
            pbFicheiros.incrementProgressBy(1);
            fazExcondeDialogo();
        }

        /**
         *
         * @param mFotografia ClasseFotografia
         */
        private boolean registaFotografiaDB(ClasseFotografias mFotografia, int id_foto, String nome_temporario){
            //procede a um registo dos dados
            ContentValues valores = new ContentValues();
            valores.put("id", id_foto);
            valores.put("id_utilizador", mIdUtilizador);
            valores.put("dia_tarefa", mDataHoje);
            valores.put("nome_temporario", nome_temporario);
            valores.put("enviado", 0);

            String[] campos = new String[]{"n_obra","cod_obra","ano_obra","nome_ficheiro","pasta","tipologia","descricao","por","origem","data_registo"};
            String valor = "";
            for(String campo:campos){
                valor = mFotografia.retreat(campo);
                valores.put(campo, valor);
            }
            return classeAssistenciaDB.insert(valores, "fotografia");
        }
    }


    /**
     * Sincronizador para descarregar ficheiro do servidor
     * não chama AsyncTask's
     */
    private class DataDocumentoTask extends AsyncTask<String, Void, String> {
        private String mNomeFicheiro;
        private String mPastaFicheiro;
        private ClasseDocumentos cDocumento;
        private Context contexto;
        private int ordem;

        private String enderecoURL;

        DataDocumentoTask(ClasseDocumentos cDocumento, Context contexto, int ordem) {
            this.cDocumento = cDocumento;
            this.contexto = contexto;
            this.ordem = ordem;

            this.mNomeFicheiro = "";
            if(null!=cDocumento){
                this.mNomeFicheiro  = cDocumento.getNome_ficheiro();
                this.mPastaFicheiro = cDocumento.getAno_obra()+cDocumento.getCod_obra()+cDocumento.getN_obra();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            enderecoURL = classeFicheiroCacheEndereco.getTextoFicheiro();

            if(enderecoURL.equals("")){}
            else {
                try {
                    //cria a ligacao
                    enderecoURL += "/android_extranet";
                    enderecoURL += "/processos/ficheiro.php?";
                    //enderecoURL += "opt=img";
                    enderecoURL += "&ordem=" + this.ordem;
                    enderecoURL += "&n_obra=" + cDocumento.getN_obra();
                    enderecoURL += "&ano_obra=" + cDocumento.getAno_obra();
                    enderecoURL += "&cod_obra=" + cDocumento.getCod_obra();
                    enderecoURL += "&nome=" + URLEncoder.encode(cDocumento.getNome_ficheiro(), "UTF8");
                    enderecoURL += "&rnd=" + Math.random();
                }
                catch (UnsupportedEncodingException uee){}
            }
            mSomaNChamadasServidorActivas++;
        }

        @Override
        protected String doInBackground(String... params) {
            String resultado = null;

            /* pede para dar conteudo (stream) do ficheiro */
            InputStream input = null;
            OutputStream output = null;

            File raiz = null;
            File ficheiro = null;
            try {
                //pede para fazer copia da imagem em outra pasta (temporaria)
                URL url = new URL(enderecoURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                // just want to do an HTTP GET here
                urlConnection.setRequestMethod("GET");
                // uncomment this if you want to write output to this url
                //connection.setDoOutput(true);
                // give it 1 seconds to respond
                urlConnection.setReadTimeout(1000);
                urlConnection.connect();
                int statusCode = urlConnection.getResponseCode();
                if (statusCode == HttpURLConnection.HTTP_OK) {
                    input = urlConnection.getInputStream();

                    //abre/cria o directorio para deposito, relacionado com o processo
                    raiz = new File(this.contexto.getFilesDir(), this.mPastaFicheiro);
                    if(!raiz.exists()){ boolean b = raiz.mkdirs();}
                    //raiz.getParentFile().mkdirs();//caso nao exista cria
                    if(raiz.exists()) {
                        //abre/cria o ficheiro no directorio aberto
                        ficheiro = new File(raiz, this.mNomeFicheiro);
                        if(!ficheiro.exists()){ boolean c = ficheiro.createNewFile();}
                        //ficheiro.getParentFile().mkdirs();
                        if(ficheiro.exists()) {
                            //escreve no ficheiro
                            output = new FileOutputStream(ficheiro);
                            byte data[] = new byte[BANDABYTE];
                            int count;
                            while ((count = input.read(data)) != -1) {
                                output.write(data, 0, count);
                            }
                        }
                        else{ Log.e("Directório", "Não abre ficheiro!");}
                    }
                    else{ Log.e("Directório", "Não abre a raiz!");}
                }
            }
            catch (MalformedURLException e){ resultado = " ficheiro #1 - "+e.toString();}
            catch (IOException e){ resultado = " ficheiro #2 - "+e.toString();}
            catch (Exception e){ resultado = " ficheiro #3 - "+e.toString();}
            finally {
                try {
                    if (null != output){ output.close();}
                    if (null != input){ input.close();}
                    if (null != raiz){ raiz = null;}
                    if (null != ficheiro){ ficheiro = null;}
                } catch (IOException ignored) {}
            }
            return resultado;
        }

        @Override
        protected void onPostExecute(String resultado) {
            mSomaNChamadasServidorFinalizadas++;
            //mostra no dialogo o nome do ficheiro
            //.setMessage(this.mNomeFicheiro);
            //progressoDialogo.incrementProgressBy(1);
            pbFicheiros.incrementProgressBy(1);
            tvDadoAReceber.setText("("+mSomaNChamadasServidorFinalizadas+" de "+pbFicheiros.getMax()+") "+mNomeFicheiro);

            try {
                if (null != resultado) {
                    Toast.makeText(this.contexto, "Erro ao Baixar: " + resultado, Toast.LENGTH_SHORT).show();
                    Log.e("Baixar Documento", resultado);
                } else {
                    registaDocumentoDB(cDocumento);
                }

                // desliga e esconde o dialogo
                fazExcondeDialogo();
                //internalStorage = null;
            }
            catch (WindowManager.BadTokenException be){}
            catch (Exception e){}
        }

        /**
         * É chamado quando a execucao é cancelada
         */
        @Override
        protected void onCancelled() {
            //String descricao = "O pedido de sincronização dos ficheiros foi interrompido!";
            //Toast.makeText(this.contexto, descricao, Toast.LENGTH_SHORT).show();
            // desliga e esconde o dialogo
            mSomaNChamadasServidorFinalizadas++;
            //mostra no dialogo o nome do ficheiro
            //progressoDialogo.setMessage(this.mNomeFicheiro);
            //progressoDialogo.incrementProgressBy(1);
            pbFicheiros.incrementProgressBy(1);
            fazExcondeDialogo();
            //internalStorage = null;
        }

        /**
         *
         * @param mDocumento ClasseDocumento
         */
        private void registaDocumentoDB(ClasseDocumentos mDocumento){
            if(null!=mDocumento){
                String[] campos = new String[]{"n_obra","cod_obra","ano_obra","nome_ficheiro","tipologia","descricao","por","origem","data_registo"};
                int id = classeAssistenciaDB.get_proxID("ficheiro");

                ContentValues valores = new ContentValues();

                valores.put("id", id);
                valores.put("id_utilizador", mIdUtilizador);
                valores.put("dia_tarefa", mDataHoje);
                String valor = "";
                for(String campo:campos){
                    valor = mDocumento.retreat(campo);
                    valores.put(campo, valor);
                }
                classeAssistenciaDB.insert(valores, "ficheiro");
            }
        }
    }
}
