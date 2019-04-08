package com.dlsra.assistenciatecnica707;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * realizar acesso por codigo e nº de contacto
 */
public class InicioAcesso extends AppCompatActivity {
    /**
     * traca segundo plano para realizar acesso
     */
    private UtilizadorAcessoTask mAuthTask = null;
    // classes referencia
    private ClasseInternetConectividade classeInternetConectividade;
    // instanciar a classe utilizadorDB
    private DBAssistencia classAssistenciaDB = null;

    // instanciar a classe FicheiroCache
    private ClasseFicheiroCache classeFicheiroCacheUtilizador = null;
    private ClasseFicheiroCache classeFicheiroCacheEndereco = null;
    //dados para o utilizador
    private ArrayList<HashMap<String, String>> listaUtilizadores;
    private int nUtilizadores;
    private int mIdUtilizador;
    private int mNUtilizador;
    private String _FileNameUtilizador;

    //dados para endereco do servidor
    private String _FileNameEndereco;
    // UI referencia
    private AutoCompleteTextView mCodigoView;
    private EditText mContactoView;
    private Button mBotaoEntrar;
    private View mProgressView;
    private View mLoginFormView;
    private TextView mLigacaoInternet;
    private Button mEnderecoSite;
    //dados gerais
    private final String[] mCamposUtilizador = new String[]{"id", "codigo", "contacto", "confirmado", "ultimo_acesso"};
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio_acesso_activity);
        //carrega valores predefinidos
        aponta_predefinidos();
        //carrega classes
        aponta_classes();
        //carrega elementos
        aponta_elementos();
        //lanca eventos
        implementa_inventos();

        //ligacao internet
        mostra_temLigacaoInternet();
        //eliminacao de dados...
        testa_eliminacaoDados();
    }

    @Override
    protected void onPause(){
        super.onPause();
        regista_acessoUtilizador();
    }

    /*carrega dados predefinidos (string)*/
    private void aponta_predefinidos(){
        //ficheiro guarda utilizador
        _FileNameUtilizador = getString(R.string.ficheiro_credenciais_utilizador);
        //ficheiro guarda endereco servidor
        _FileNameEndereco = getString(R.string.ficheiro_endereco_electronico);
    }
    /*carrega classes*/
    private void aponta_classes(){
        classeInternetConectividade = new ClasseInternetConectividade(this);

        //inicializa class ligacao ficheiro cache
        classeFicheiroCacheUtilizador = new ClasseFicheiroCache(this, _FileNameUtilizador);
        classeFicheiroCacheEndereco = new ClasseFicheiroCache(this, _FileNameEndereco);

        //inicializa class base de dados do utilizador
        classAssistenciaDB = new DBAssistencia(this, "UTILIZADOR", "utilizador");
        classAssistenciaDB.preencheDados(mCamposUtilizador, "", "", "codigo ASC");
        listaUtilizadores = classAssistenciaDB.getDados();
        nUtilizadores = classAssistenciaDB.nElementos();
    }
    /*carrega elementos*/
    private void aponta_elementos(){
        mCodigoView = (AutoCompleteTextView) findViewById(R.id.actvCodigo);
        mContactoView = (EditText) findViewById(R.id.etContacto);
        mBotaoEntrar = (Button) findViewById(R.id.btEntrar);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mLigacaoInternet = (TextView) findViewById(R.id.tvLigacaoInternet);
        mEnderecoSite = (Button) findViewById(R.id.btEnderecoSite);
    }
    /*lanca eventos*/
    private void implementa_inventos(){
        mCodigoView.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                testaCampoCodigo();
            }
        });
        mContactoView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });


        mBotaoEntrar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mEnderecoSite.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //muda para a pagina alteracao endereco electronico da empresa
                navega2endereco();
            }
        });
    }
    //metodo ao alterar campo codigo do utilizador
    private void testaCampoCodigo() {
        String mCodigo = mCodigoView.getText().toString().trim();
        HashMap<String, String> utilizador;
        String contacto;
        String confirmado;

        if (6 == mCodigo.length()) {
            int codigo = Integer.parseInt(mCodigo);
            try {
                utilizador = classAssistenciaDB.getDadosBy("codigo", mCodigo, mCamposUtilizador);
                contacto = utilizador.get("contacto");
                confirmado = utilizador.get("confirmado");

                if (confirmado.equals("1") && null != contacto) {
                    mContactoView.setText(contacto);
                }
            } catch (Exception e) {
                Log.e("Passa Contacto", e.toString());
            }
            mContactoView.requestFocus();
        }

    }

    private void mostra_temLigacaoInternet(){
        mLigacaoInternet.setText((classeInternetConectividade.isLigado()?"":"Não ")+"Tem Ligação à Internet");
    }
    /**
     * testa os valores passados e verifica a sua veracidade
     */
    private void attemptLogin() {
        if (mAuthTask != null) { return;}

        // zera os erros.
        mCodigoView.setError(null);
        mContactoView.setError(null);

        // aloja os valores credenciados
        String codigo = mCodigoView.getText().toString();
        String contacto = mContactoView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // testa por um contacto valido.
        if (TextUtils.isEmpty(contacto) || !eContactoValido(contacto)) {
            mContactoView.setError(getString(R.string.error_invalid_contacto));
            focusView = mContactoView;
            cancel = true;
        }

        // testa e valida codigo pessoal.
        if (TextUtils.isEmpty(codigo) || !eCodigoValido(codigo)) {
            mCodigoView.setError(getString(R.string.error_field_required));
            focusView = mCodigoView;
            cancel = true;
        }

        if (cancel) {
            // se devolve erro, nao aceite, nao faz o login
            // torna evidente o objecto com erro
            focusView.requestFocus();
        } else {
            HashMap<String, String> utilizador = classAssistenciaDB.getDadosBy("codigo", codigo.toString(), mCamposUtilizador);

            //so vai autenticar ao servidor ocasionalmente, se confirmado utilizador, caso contrario sempre
            classeInternetConectividade.refaz();
            boolean autenticaOnline = classeInternetConectividade.isLigado();
            mostra_temLigacaoInternet();

            boolean existe_utilizador = 0 < utilizador.size();
            if (autenticaOnline && existe_utilizador) {
                int n = 10;
                if (utilizador.get("confirmado").equals("1")) {
                    //tira à sorte um inteiro
                    Random rand = new Random();
                    n = rand.nextInt(2000); // retorna um inteiro entre 0 <= n < 20
                }
                autenticaOnline = 10 == n;
            }

            // Mostra a roda de progresso, em segundo plano testa se existe as credenciais
            if (autenticaOnline) {
                showProgress(true);
                mAuthTask = new UtilizadorAcessoTask(this, codigo, contacto);
                mAuthTask.execute((Void) null);
            } else if (existe_utilizador) {
                showProgress(false);
                Log.i("Troca Tela", "Tarefas");
                registaNumeroUtilizador(codigo);
                navega2tarefas();
            } else {
                mostraMensagemTela("Ligue-se à internet...", Toast.LENGTH_LONG);
            }
        }
    }

    private boolean eCodigoValido(String codigo) {
        boolean valido = false;
        String parteCodigo;
        String fCodigo;
        int numero;
        ClasseCodigoPessoal codigoPessoal = null;

        if (6 == codigo.length()) {
            parteCodigo = codigo.substring(1, codigo.length() - 1);
            numero = Integer.parseInt(parteCodigo);

            codigoPessoal = new ClasseCodigoPessoal(numero);
            fCodigo = codigoPessoal.getCodigo();

            //Log.i("Codigo", codigo + " > " + numero + " » " + fCodigo);
            valido = 0 == codigo.compareTo(fCodigo);
        }
        return valido;
    }

    private boolean eContactoValido(String contacto) {
        //se tem 9 digitos!
        return 9 == contacto.length();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /* regista valores */
    private void registaNumeroUtilizador(Integer mCodigo) {
        registaNumeroUtilizador(mCodigo.toString());
    }

    private void registaNumeroUtilizador(String codigo) {
        //regista n do utilizador
        String parteCodigo = codigo.substring(1, codigo.length() - 1);
        Integer numero = Integer.parseInt(parteCodigo);
        classeFicheiroCacheUtilizador.setTextoFicheiro(""+numero);
        mNUtilizador = numero;
    }

    /*muda de tela*/
    private void navega2tarefas(){
        Log.i("Troca Tela", "Tarefas");

        carrega_idUtilizador();

        Bundle args = new Bundle();
        args.putInt("_id_utilizador", mIdUtilizador);
        args.putInt("_n_utilizador", mNUtilizador);
        startActivity(new Intent(this, TarefasDia.class).putExtras(args));
    }

    private void navega2endereco(){
        Log.i("Troca Tela", "Endereço Electrónico");
        startActivity(new Intent(this, InicioEndereco.class));
    }

    private void carrega_idUtilizador(){
        String mCodigo = mCodigoView.getText().toString().trim();
        HashMap<String, String> utilizador = classAssistenciaDB.getDadosBy("codigo", mCodigo, mCamposUtilizador);
        mIdUtilizador = Integer.parseInt(utilizador.get("id"));
    }

    private void testa_eliminacaoDados(){
        /*
        ClasseEliminaDadosBD classeEliminaDadosBD = new ClasseEliminaDadosBD(this, mIdUtilizador);
        ClasseDataTempo classeDataTempo = new ClasseDataTempo();
        String mCodigo = mCodigoView.getText().toString().trim();
        HashMap<String, String> utilizador = classAssistenciaDB.getDadosBy("codigo", mCodigo, mCamposUtilizador);
        String ultimo_acesso = utilizador.get("ultimo_acesso");
        String hoje          = classeDataTempo.data_hoje();
        */
        ClasseEliminaDadosBD classeEliminaDadosBD = new ClasseEliminaDadosBD(this, mIdUtilizador);
        ClasseDataTempo classeDataTempo           = new ClasseDataTempo();
        String hoje = classeDataTempo.data_hoje();
        int nivel   = 3; //elimina tudo na memoria utilizado pela app (relacionados com tarefas/processos)
        for(HashMap<String, String> utilizador:listaUtilizadores){
            String ultimo_acesso = utilizador.get("ultimo_acesso");
            if(ultimo_acesso.equals(hoje)){ nivel=0;} //nao elimina nada
        }
        classeEliminaDadosBD.elimina_dados(nivel);
    }

    private void regista_acessoUtilizador(){
        ClasseDataTempo classeDataTempo = new ClasseDataTempo();
        String mCodigo = mCodigoView.getText().toString().trim();
        ContentValues valores = new ContentValues();
        String hoje          = classeDataTempo.data_hoje();
        valores.put("ultimo_acesso", hoje);
        classAssistenciaDB.updateDados(valores, "codigo", mCodigo);
    }


    //mostra mensagem na tela
    private void mostraMensagemTela(String texto, int duracao) {
        Context contexto = getApplicationContext();
        Toast toast = Toast.makeText(contexto, texto, duracao);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }


    /**
     * Sincronizador para autenticacao/registo, task usado para autenticacao do utilizador
     */
    public class UtilizadorAcessoTask extends AsyncTask<Void, Void, Boolean> {
        private final Activity mActividade;
        private final Integer mCodigo;
        private final Integer mContacto;

        private String enderecoURL;
        private String pasta_servidor;
        private HttpURLConnection connection;
        private URL url;

        UtilizadorAcessoTask(Activity actividade, String codigo, String contacto) {
            mActividade = actividade;
            mCodigo = Integer.parseInt(codigo);
            mContacto = Integer.parseInt(contacto);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                enderecoURL = classeFicheiroCacheEndereco.getTextoFicheiro();

                //no caso de nao ter uma url valida, regista a url por defeito
                if ("" == enderecoURL) {
                    enderecoURL = getString(R.string.endereco_electronico);
                    classeFicheiroCacheEndereco.setTextoFicheiro(enderecoURL);
                }
                pasta_servidor = getString(R.string.pasta_no_servidor);

                //cria a ligacao
                url = new URL(enderecoURL + "/" + pasta_servidor + "/utilizadores/credenciais.php?codigo=" + mCodigo + "&contacto=" + mContacto);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                //traca a ligacao
                connection = (HttpURLConnection) url.openConnection();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            classeInternetConectividade.refaz();
            boolean temLigacao = classeInternetConectividade.isLigado();
            HashMap<String, String> utilizador = classAssistenciaDB.getDadosBy("codigo", mCodigo.toString(), mCamposUtilizador);

            if (temLigacao) {
                //no caso de nao existir cria um registo do utilizador
                if (utilizador.isEmpty()) {
                    ContentValues valores = new ContentValues();
                    valores.put("codigo", mCodigo);
                    valores.put("contacto", mContacto);
                    valores.put("ultimo_acesso", "");
                    classAssistenciaDB.insert(valores);
                }
                //busca a confirmacao numa ligacao ao servidor
                try {
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        Log.i("Fez Ligação", "Página existe!");

                        String resultado = "";
                        InputStream in = new BufferedInputStream(connection.getInputStream());
                        if (in != null) {
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                            String line = "";

                            while ((line = bufferedReader.readLine()) != null) {
                                resultado += line;
                            }
                            resultado = resultado.trim();
                        }
                        in.close();

                        Log.i("Fez Ligação", resultado);
                        if (resultado.equals("232")) { return true;}
                    } else if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                        Log.i("Fez Ligação", "Página não existe!");
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            } else {
                //nao faz registo na base de dados por estar offline
                try {
                    // Simula acesso à internet
                    Thread.sleep(2000);

                    //se existe registo
                    if (!utilizador.isEmpty()) {
                        // verifica se tem o mesmo contacto e se e só se confirmado
                        if (utilizador.get("contacto").equals("" + mContacto) && utilizador.get("confirmado").equals("1")) { return true;}
                    }

                } catch (InterruptedException e) {
                    Log.i("Logar offline", e.toString());
                }
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {

                //actualiza o contacto e a confirmacao
                ContentValues valores = new ContentValues();
                valores.put("contacto", mContacto);
                valores.put("confirmado", 1);
                classAssistenciaDB.updateDados(valores, "codigo", mCodigo.toString());

                try {

                    String texto = "Logado com sucesso!";
                    int duracao = Toast.LENGTH_SHORT;
                    mostraMensagemTela(texto, duracao);

                    registaNumeroUtilizador(mCodigo);
                    // Simula acesso à internet
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    Log.i("Sucesso", e.toString());
                }

                Log.i("Troca Tela", "Tarefas");
                navega2tarefas();
            } else {
                mContactoView.setError("Dados Utilizador errados!");
                mContactoView.requestFocus();

                ContentValues valores = new ContentValues();
                valores.put("confirmado", 0);
                classAssistenciaDB.updateDados(valores, "codigo", mCodigo.toString());
            }
        }

        /**
         * É chamado quando a execucao é cancelada
         */
        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

