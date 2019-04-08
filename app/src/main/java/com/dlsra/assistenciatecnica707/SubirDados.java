package com.dlsra.assistenciatecnica707;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class SubirDados extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final static String mIdMenu = "id_menu_subir";
    //classes
    private ClasseDadosNavegacao classDadosNavegacao = null;
    //elementos sistema
    private Context mContexto = null;
    //elementos
    Toolbar mBarraFerramentas = null;
    DrawerLayout mJanela = null;
    NavigationView mNavegador = null;
    //valores passados
    private int _id_menu = 0;
    private int _id_utilizador = 0;
    private int _n_utilizador = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subir_dados);

        try {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            _id_utilizador = bundle.getInt("_id_utilizador");
            _n_utilizador = bundle.getInt("_n_utilizador");
        } catch (Exception e){ Log.e("Valores Passados", e.toString());}

        //dados visuais
        aponta_predefinidos();
        //carrega classes
        aponta_classes();

        setSupportActionBar(mBarraFerramentas);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mJanela, mBarraFerramentas, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mJanela.addDrawerListener(toggle);
        toggle.syncState();

        mNavegador.setNavigationItemSelectedListener(this);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.tarefas_dia, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) { return true;}

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

    private void mostra_parte_menu(){
        Fragment fragment =  null;
        String titulo = "";
        Bundle args = new Bundle();
        args.putInt("id_menu", _id_menu);
        args.putInt("_id_utilizador", _id_utilizador);
        args.putInt("_n_utilizador", _n_utilizador);

        // direcciona a aplicacao para a opecao seleccionada

        if (0==_id_menu || _id_menu == R.id.nav_sd_fotografias) {
            titulo = "Fotografias";
            fragment = new SubirDadosFotografiasFragmento();
        } else if (_id_menu == R.id.nav_sd_picaponto) {
            titulo = "Pica Ponto";
            fragment = new SubirDadosPicaPontoFragmento();
        } else if (_id_menu == R.id.nav_sd_coordenadas) {
            titulo = "Coordenadas";
            fragment = new SubirDadosCoordenadasFragmento();
        } else if (_id_menu == R.id.nav_sd_voltar_tarefas) {
            _id_menu = 0;
            finish();
        } else if (_id_menu == R.id.nav_sd_sair_aplicacao_processo){
            _id_menu = 0;
            moveTaskToBack(true);
            finish();
            System.exit(0);
        }
        if(null!=fragment){
            fragment.setArguments(args);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.SubirDadosFrame, fragment);
            ft.commit();

            getSupportActionBar().setTitle(titulo);

            classDadosNavegacao.atribui_elemento(this.mIdMenu, ""+_id_menu);
            mJanela.closeDrawer(GravityCompat.START);
        }
        else if(0 < _id_menu){
            _id_menu = R.id.nav_tarefas_dia;
            mostra_parte_menu();
        }
    }

    private void aponta_predefinidos(){
        mBarraFerramentas = (Toolbar) findViewById(R.id.barra_ferramentas_subir);
        mJanela = (DrawerLayout) findViewById(R.id.janela_subir);
        mNavegador = (NavigationView) findViewById(R.id.navegador_subir);
    }

    private void aponta_classes(){
        classDadosNavegacao = new ClasseDadosNavegacao(this);
    }
}
