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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class TarefasDia extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final static String mIdMenu = "id_menu_tarefas";
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
        setContentView(R.layout.tarefas_dia_activity);

        try {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            _id_utilizador = bundle.getInt("_id_utilizador");
            _n_utilizador = bundle.getInt("_n_utilizador");
        } catch (Exception e){ Log.e("Valores Passados", e.toString());}

        mContexto = getBaseContext();

        /* verifica se entra em novo dia */
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
        _id_menu = R.id.nav_tarefas_dia;
        mostra_parte_menu();
    }

    @Override
    protected void onResume(){
        super.onResume();
        //envia os dados sempre que passa por esta actividade
        envia_fotografias();
        envia_picaponto();
        envia_coordenadas();
    }

    private void aponta_predefinidos(){
        mBarraFerramentas = (Toolbar) findViewById(R.id.barra_ferramentas_tarefas);
        mJanela = (DrawerLayout) findViewById(R.id.janela_tarefas);
        mNavegador = (NavigationView) findViewById(R.id.navegador_tarefas);
    }

    private void aponta_classes(){
        classDadosNavegacao = new ClasseDadosNavegacao(this);
    }

    private void aponta_elementos(){}

    private void implementa_inventos(){}

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
        if (0==_id_menu || _id_menu == R.id.nav_tarefas_dia) {
            titulo = "Lista Tarefas";
            fragment = new TarefasDiaFragmentoTarefas();
        } else if (_id_menu == R.id.nav_picaponto) {
            titulo = "Pica Ponto";
            fragment = new PicaPontoFragmento();
        } else if (_id_menu == R.id.nav_subir_dados) {
            _id_menu = 0;
            mJanela.closeDrawer(GravityCompat.START);
            abre_janelaSubirDados();
        } else if (_id_menu == R.id.nav_sair_aplicacao){
            _id_menu = 0;
            moveTaskToBack(true);
            finish();
            System.exit(0);
        }
        if(null!=fragment){
            fragment.setArguments(args);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.TarefasFrame, fragment);
            ft.commit();

            getSupportActionBar().setTitle(titulo);

            classDadosNavegacao.atribui_elemento(mIdMenu, ""+_id_menu);
            mJanela.closeDrawer(GravityCompat.START);
        }
        else if(0 < _id_menu){
            _id_menu = R.id.nav_tarefas_dia;
            mostra_parte_menu();
        }
    }

    private void abre_janelaSubirDados(){
        //carrega os argumentos para passar
        Bundle args = new Bundle();
        args.putInt("_id_utilizador", _id_utilizador);
        args.putInt("_n_utilizador", _n_utilizador);

        //abre nova actividade....
        //mContexto.startActivity(new Intent(mContexto, SubirDados.class).putExtras(args));
        startActivity(new Intent(this, SubirDados.class).putExtras(args));
        Toast.makeText(mContexto, "Abre Subir Dados...", Toast.LENGTH_SHORT).show();
    }

    /**
     * Envia dados
     */
    private void envia_fotografias(){
        DBAssistencia classAssistenciaDB = new DBAssistencia(mContexto, "FOTOGRAFIA", "fotografia");
        ArrayList<ClasseFotografias> fotografiasAEnviar = new ArrayList<>();

        ArrayList<ClasseFotografias> fotografias = classAssistenciaDB.get_fotografias(true);
        for(ClasseFotografias foto: fotografias){
            int origem     = foto.getOrigem();
            boolean enviar = !foto.isEnviado();
            if(2==origem && enviar){
                fotografiasAEnviar.add(foto);
            }
        }

        if(0 < fotografiasAEnviar.size()){
            ClasseEnviarFotografias cef = new ClasseEnviarFotografias(mContexto);
            cef.setDados(fotografiasAEnviar);
            cef.enviar();
        }
    }

    private void envia_picaponto(){
        DBAssistencia classAssistenciaDB = new DBAssistencia(mContexto, "PICA", "pica_ponto");
        ArrayList<ClassePicaPonto> picapontoAEnviar = new ArrayList<>();
        ArrayList<ClassePicaPonto> picas = classAssistenciaDB.get_picaPonto();
        for(ClassePicaPonto pica: picas){
            boolean enviar = !pica.isEnviado();
            if(enviar){
                picapontoAEnviar.add(pica);
            }
        }

        if(0 < picapontoAEnviar.size()){
            ClasseEnviarPicaPonto cepp = new ClasseEnviarPicaPonto(mContexto);
            cepp.setDados(picapontoAEnviar);
            cepp.enviar();
        }
    }

    private void envia_coordenadas(){
        DBAssistencia classAssistenciaDB = new DBAssistencia(mContexto, "COORDS", "coordenadas");
        ArrayList<ClasseCoordenadas> coordenadasAEnviar = new ArrayList<>();
        ArrayList<ClasseCoordenadas> coords = classAssistenciaDB.get_coordenadas();
        for(ClasseCoordenadas coord: coords){
            boolean enviar = !coord.isEnviado();
            if(enviar){
                coordenadasAEnviar.add(coord);
            }
        }

        if(0 < coordenadasAEnviar.size()){
            ClasseEnviarCoordenadas cec = new ClasseEnviarCoordenadas(mContexto);
            cec.setDados(coordenadasAEnviar);
            cec.enviar();
        }
    }
}
