package com.dlsra.assistenciatecnica707;


import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProcessoFragmentoLocalizacao extends Fragment {
    //declaracao de classes
    private DBAssistencia classAssistenciaDB = null;
    private ClasseInternetConectividade classConeccao = null;
    //variaveis
    private int mIdUtilizador;
    private int mNUtilizador;
    private int mIdMenu;
    private int mNObra;
    private String mCodObra;
    private int mAnoObra;
    private int mIdTarefa;
    private int mIdPessoa;

    //elementos visuais
    private View janela = null;
    private TextView tvLatitude    = null;
    private TextView tvLongitude   = null;
    private TextView tvActualizado = null;
    private TextView tvPrecisao    = null;
    private TextView tvProvador    = null;
    private TextView tvCoordenadas = null;
    private Button btMarcarPosicao = null;

    //elementos sistema
    private Context mContexto = null;
    private LocationManager locationManager = null;

    public ProcessoFragmentoLocalizacao() {
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
        janela = inflater.inflate(R.layout.processo_fragmento_localizacao, container, false);
        return janela;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle){
        super.onViewCreated(view, bundle);
        //monta o localizador da posicao do aparelho
        locationManager = (LocationManager) mContexto.getSystemService(Context.LOCATION_SERVICE);

        carrega_classes();
        carrega_elementos();
        preenche_dadosJanela();
        aponta_eventos();
    }


    @Override
    public void onDestroyView(){
        super.onDestroyView();
        //if(null!=CoordenadasAsyncTask){ CoordenadasAsyncTask.cancel(true);}
    }

    /**
     *
     */
    private void carrega_elementos(){
        tvLatitude    = (TextView) janela.findViewById(R.id.tvLatitude);
        tvLongitude   = (TextView) janela.findViewById(R.id.tvLongitude);
        tvActualizado = (TextView) janela.findViewById(R.id.tvActualizado);
        tvPrecisao    = (TextView) janela.findViewById(R.id.tvPrecisao);
        tvProvador    = (TextView) janela.findViewById(R.id.tvProvador);
        tvCoordenadas = (TextView) janela.findViewById(R.id.tvCoordenadasObra);
        btMarcarPosicao = (Button)   janela.findViewById(R.id.btMarcarPosicao);
    }

    private void aponta_eventos(){
        btMarcarPosicao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latitude  = (String) tvLatitude.getText();
                String longitude = (String) tvLongitude.getText();

                /*latitude = "40.137963"; longitude = "-7.5010773";*/
                if(latitude.equals("-") || longitude.equals("-")){
                    Toast.makeText(mContexto, "As coordenadas a armazenar não estão correctas!", Toast.LENGTH_SHORT).show();
                }
                else if(0 < mIdUtilizador){
                    registaCoordenadasGPS(mIdPessoa, latitude, longitude, 0);
                }
            }
        });

        tvCoordenadas.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String localizacao = tvCoordenadas.getText().toString();
                if(!localizacao.equals("")){
                    Double latitude  = 39.8197871;
                    Double longitude = -8.0921217;

                    String[] partes = localizacao.split(":");
                    if(2==partes.length){
                        latitude  = Double.parseDouble(partes[0]);
                        longitude = Double.parseDouble(partes[1]);
                    }
                    //abrir numa nova app
                    String uri    = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                }
                return false;
            }
        });

        //actualizar sempre que se altere
        if(23 < Build.VERSION.SDK_INT
                && ContextCompat.checkSelfPermission(mContexto, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(mContexto, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ){Log.i("Localização", "Não Entrou!!!!");}
        else {

            //sempre que altere actualiza localização...
            try {
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                    @Override
                    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
                        Toast.makeText(mContexto, "Status Localização Alterado!", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onProviderEnabled(String arg0) {
                        Toast.makeText(mContexto, "Localização Habilitado!", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onProviderDisabled(String arg0) {
                        Toast.makeText(mContexto, "Localização Desabilitado!", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onLocationChanged(Location location) {
                        preenche_coordenadas();
                    }
                }, null);
            }
            catch (SecurityException e) {}
            catch (Exception e) {}
        }
    }

    private void carrega_classes(){
        //inicializa a classe tarefa
        classAssistenciaDB = new DBAssistencia(mContexto, "TAREFA", "tarefa");
        classConeccao      = new ClasseInternetConectividade(getActivity());
    }

    /* ---------------------
        SET's
     */
    private void preenche_dadosJanela(){
        preenche_coordenadas();
        preenche_coordenadasObra();
    }

    private void preenche_coordenadasObra(){
        /**
         * Localizacao armazenada nos dados do processo
         */
        int id_p = 0;
        ArrayList<HashMap<String, String>> pessoas = get_pessoas(mNObra, mCodObra, mAnoObra);
        if(0 < pessoas.size()) {
            HashMap<String, String> pessoa = pessoas.get(0);
            final String gps = pessoa.get("gps");
            tvCoordenadas.setText(gps.equals(":")?"":gps);

            mIdPessoa = Integer.parseInt(pessoa.get("id_pessoa"));
        }
    }

    private void preenche_coordenadas(){
        //o localizador da ultima posicao valida
        Location localizacaoUltima = null;
        try {
            localizacaoUltima = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (SecurityException e ){}

        if(null != localizacaoUltima) {
            tvLatitude.setText(String.valueOf(localizacaoUltima.getLatitude()));
            tvLongitude.setText(String.valueOf(localizacaoUltima.getLongitude()));
            tvPrecisao.setText(String.valueOf(localizacaoUltima.getAccuracy()));
            tvProvador.setText(localizacaoUltima.getProvider());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.UK);
            tvActualizado.setText(sdf.format(localizacaoUltima.getTime()));
        }
        else{
            tvLatitude.setText("-");
            tvLongitude.setText("-");
            tvPrecisao.setText("-");
            tvProvador.setText("-");
            tvActualizado.setText("-");
        }
    }

    //regista coordenadas
    private void registaCoordenadasGPS(int id_pessoa, String latitude, String longitude, int enviado){
        String gps = latitude + ":" + longitude;
        final TextView tvCoordenadas = (TextView) getActivity().findViewById(R.id.tvCoordenadasObra);

        classAssistenciaDB.setNomeTabela("coordenadas");
        ArrayList<HashMap<String, String>> registo = get_coordenadas(id_pessoa, mCodObra);
        boolean sucesso;
        if(0 == registo.size()){
            //novo registo
            sucesso = inserirCoordenadasBD(id_pessoa, gps, enviado);
        }
        else{
            //regista a actualizacao na base de dados
            ContentValues valores = new ContentValues();
            valores.put("gps", gps);
            valores.put("enviado", enviado);

            //actualiza registo
            classAssistenciaDB.setNomeTabela("coordenadas");
            sucesso = classAssistenciaDB.updateDados(valores, "id_pessoa = ? AND cod_obra = ?", new String[]{"" + id_pessoa, mCodObra});
        }
        if(sucesso){
            //altera na tabela pessoa
            ContentValues valores = new ContentValues();
            valores.put("gps", gps);

            classAssistenciaDB.setNomeTabela("pessoa");
            if(classAssistenciaDB.updateDados(valores, "id_pessoa", "" + id_pessoa)) {

                Toast.makeText(mContexto, "Registado com sucesso!!", Toast.LENGTH_SHORT).show();
                tvCoordenadas.setText(gps);
            }

            //envia para o servidor
            classConeccao.refaz();
            if (classConeccao.isLigado()) {
                ClasseEnviarCoordenadas cec = new ClasseEnviarCoordenadas(mContexto);
                ArrayList<ClasseCoordenadas> coords = classAssistenciaDB.get_coordenadas();
                cec.setDados(coords);
                cec.enviar(coords.size()-1);
            }
        }
        else{ Toast.makeText(mContexto, "Não registado na base de dados!!", Toast.LENGTH_SHORT).show();}
    }

    private boolean inserirCoordenadasBD(int id_pessoa, String gps, int enviado){
        classAssistenciaDB.setNomeTabela("tarefa");
        HashMap<String, String> dados = classAssistenciaDB.getDadosBy("id", ""+mIdTarefa, new String []{"data"});
        String dia_tarefa = dados.get("data");

        classAssistenciaDB.setNomeTabela("coordenadas");
        final int id = classAssistenciaDB.get_proxID();

        ContentValues valores = new ContentValues();
        valores.put("id", id);
        valores.put("n_obra", mNObra);
        valores.put("cod_obra", mCodObra);
        valores.put("ano_obra", mAnoObra);
        valores.put("id_pessoa", id_pessoa);
        valores.put("gps", gps);
        valores.put("enviado", enviado);
        valores.put("dia_tarefa", dia_tarefa);
        final boolean sucesso = classAssistenciaDB.insert(valores, "coordenadas");
        if(!sucesso){ Toast.makeText(mContexto, "GPS não registado na base de dados!!", Toast.LENGTH_SHORT).show();}
        return sucesso;
    }
    /*
        GET's
     */
    private ArrayList<HashMap<String, String>> get_pessoas(int n_obra, String cod_obra, int ano_obra){
        final String[] camposPessoa = new String[] {"id", "gps", "id_pessoa"};
        classAssistenciaDB.setNomeTabela("pessoa");
        String onde = "n_obra=? AND cod_obra=? AND ano_obra=?";
        String[] valores = new String[]{""+n_obra, cod_obra, ""+ano_obra};
        classAssistenciaDB.preencheDados(camposPessoa, onde, valores, "id ASC");
        ArrayList<HashMap<String, String>> pessoas = classAssistenciaDB.getDados();
        return pessoas;
    }

    //coordenadas
    private ArrayList<HashMap<String, String>> get_coordenadas(int id_pessoa, String cod_obra){
        final String[] camposCoordenadas = new String[] {"id","n_obra","cod_obra","ano_obra","id_pessoa","gps","enviado"};
        classAssistenciaDB.setNomeTabela("coordenadas");
        String onde = "id_pessoa=? AND cod_obra=?";
        String[] valores = new String[]{""+id_pessoa, cod_obra};
        classAssistenciaDB.preencheDados(camposCoordenadas, onde, valores, "id ASC");
        ArrayList<HashMap<String, String>> coordenadas = classAssistenciaDB.getDados();
        return coordenadas;
    }
}
