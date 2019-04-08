package com.dlsra.assistenciatecnica707;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class ProcessoJanela extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final static String mIdMenu = "id_menu_processo";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    //classes
    private ClasseDadosNavegacao classDadosNavegacao = null;

    //elementos
    private Toolbar mBarraFerramentas = null;
    private DrawerLayout mJanela = null;
    private NavigationView mNavegador = null;
    private Fragment _Fragmento = null;
    //elementos sistema
    private Context mContexto = null;
    //valores passados
    private int _id_menu = 0;
    private int _id_utilizador = 0;
    private int _n_utilizador  = 0;
    private int _n_obra        = 0;
    private String _cod_obra   = "";
    private int _ano_obra      = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.processo_janela_activity);

        mContexto = getBaseContext();

        try {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            _id_utilizador = bundle.getInt("_id_utilizador");
            _n_utilizador  = bundle.getInt("_n_utilizador");
            _id_menu       = bundle.getInt("_id_menu");
            _n_obra        = bundle.getInt("_n_obra");
            _cod_obra      = bundle.getString("_cod_obra");
            _ano_obra      = bundle.getInt("_ano_obra");
        } catch (Exception e){ Log.e("Valores Passados", e.toString());}

        //carrega valores predefinidos
        aponta_predefinidos();
        //carrega classes
        aponta_classes();
        //carrega elementos
        aponta_elementos();
        //lanca eventos
        implementa_inventos();

        setSupportActionBar(mBarraFerramentas);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mJanela, mBarraFerramentas, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mJanela.addDrawerListener(toggle);
        toggle.syncState();

        mNavegador.setNavigationItemSelectedListener(this);
        //mostra conteudo por menu seleccionado
        String id = classDadosNavegacao.get_valor(this.mIdMenu);
        if(!id.equals("")){ _id_menu = Integer.parseInt(id);}
        //(R.id.nav_sair_aplicacao==_id_menu || 0==_id_menu){ _id_menu = R.id.nav_tarefas_dia;}
        //vai sempre para a lista de tarefas
        _id_menu = R.id.nav_tarefa;
        mostra_parte_menu();
    }

    @Override
    public void onBackPressed() {
        if (mJanela.isDrawerOpen(GravityCompat.START)) {
            mJanela.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    private void aponta_predefinidos(){
        mBarraFerramentas = (Toolbar) findViewById(R.id.barra_ferramentas_processo);
        mJanela = (DrawerLayout) findViewById(R.id.janela_processo);
        mNavegador = (NavigationView) findViewById(R.id.navegador_processo);
    }

    private void aponta_classes(){
        classDadosNavegacao = new ClasseDadosNavegacao(this);
    }

    private void aponta_elementos(){}

    private void implementa_inventos(){}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        _id_menu = item.getItemId();
        mostra_parte_menu();
        return true;
    }

    private void mostra_parte_menu() {
        String titulo = "";
        String etiqueta = "";
        String processo = "";
        if (0 == _n_obra || 0 == _ano_obra || _cod_obra.equals("")) {
        } else {
            processo = "(" + _n_obra + " " + _cod_obra + "/" + _ano_obra + ")";
        }

        Bundle args = new Bundle();
        args.putInt("id_menu", _id_menu);
        args.putInt("_id_utilizador", _id_utilizador);
        args.putInt("_n_utilizador", _n_utilizador);
        args.putInt("_n_obra", _n_obra);
        args.putString("_cod_obra", _cod_obra);
        args.putInt("_ano_obra", _ano_obra);

        // direcciona a aplicacao para a opecao seleccionada
        if (0 == _id_menu || _id_menu == R.id.nav_tarefa) {
            titulo = "Dados Tarefa " + processo;
            etiqueta = "frag_tarefa";
            _Fragmento = new ProcessoFragmentoTarefa();
        } else if (_id_menu == R.id.nav_identificadores) {
            titulo = "Identificadores " + processo;
            etiqueta = "frag_identificadores";
            _Fragmento = new ProcessoFragmentoIdentificadores();
        } else if (_id_menu == R.id.nav_trabalhos) {
            titulo = "Trabalhos " + processo;
            etiqueta = "frag_trabalhos";
            _Fragmento = new ProcessoFragmentoTrabalhos();
        } else if (_id_menu == R.id.nav_fotografias) {
            titulo = "Fotografias " + processo;
            etiqueta = "frag_fotografias";
            _Fragmento = new ProcessoFragmentoFotografias();
        } else if (_id_menu == R.id.nav_fotografar) {
            titulo = "Fotografar " + processo;
            etiqueta = "frag_fotografar";
            _Fragmento = new ProcessoFragmentoFotografar();
        } else if (_id_menu == R.id.nav_documentos) {
            titulo = "Documentos " + processo;
            etiqueta = "frag_documentos";
            _Fragmento = new ProcessoFragmentoDocumentos();
        } else if (_id_menu == R.id.nav_localizacao) {
            titulo = "Localização " + processo;
            etiqueta = "frag_localizacao";
            _Fragmento = new ProcessoFragmentoLocalizacao();
        } else if(_id_menu == R.id.nav_voltar_tarefas){
            _id_menu = 0;
            finish();
        } else if (_id_menu == R.id.nav_sair_aplicacao_processo){
            _id_menu = 0;
            moveTaskToBack(true);
            finish();
            System.exit(0);
        }

        if(null!=_Fragmento){
            //destroi o fragmento com a mesma etiqueta
            try {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                Fragment fragmento = fm.findFragmentByTag(etiqueta);
                if(null!=fragmento){ fragmentTransaction.remove(fragmento).commit();}
                
                _Fragmento.setArguments(args);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.ProcessoFrame, _Fragmento, etiqueta);
                ft.commit();
            }
            catch (Exception e){}

            getSupportActionBar().setTitle(titulo);

            classDadosNavegacao.atribui_elemento(this.mIdMenu, ""+_id_menu);
            mJanela.closeDrawer(GravityCompat.START);
        }
        else if(0 < _id_menu){
            _id_menu = R.id.nav_tarefa;
            mostra_parte_menu();
        }
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * Faz parte do fotografar
     * ao tirar foto salta para aqui, e depois gera a foto
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String filePath = "";
        //se fotografia
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            ProcessoFragmentoFotografar fragment= (ProcessoFragmentoFotografar) _Fragmento;///always Null
            fragment.trata_fotografia();
        }
    }
}
