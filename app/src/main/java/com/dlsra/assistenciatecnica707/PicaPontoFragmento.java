package com.dlsra.assistenciatecnica707;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * create an instance of this fragment.
 */
public class PicaPontoFragmento extends Fragment {
    //elementos seccao
    private Context mContexto = null;
    private Activity mActividade = null;
    //constantes

    //classes
    private ClasseDataTempo classeDataTempo = null;
    private DBAssistencia dbAssistencia = null;
    private ClasseInternetConectividade classConeccao = null;
    //variaveis locais
    private String mDataHoje = "";
    private int mNUtilizador  = 0;
    private int mIdUtilizador = 0;
    private String mLocalizacao = "";
    //elementos
    View mView = null;
    Button btPicarPonto = null;
    RecyclerView mRecyclerView = null;
    CardView cvPicaPonto = null;

    public PicaPontoFragmento() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContexto = this.getContext();
        mActividade = this.getActivity();

        try {
            Intent intent = getActivity().getIntent();
            Bundle bundle = intent.getExtras();
            mIdUtilizador = bundle.getInt("_id_utilizador");
            mNUtilizador  = bundle.getInt("_n_utilizador");
        } catch (Exception e){ Log.e("Valores Passados", e.toString());}

        carrega_classes();

        mDataHoje = classeDataTempo.data_hoje();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.pica_ponto_fragmento, container, false);

        carrega_elementos();
        lanca_eventos();
        monta_listaPicaPonto();

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        /**
         * da-me gps localizacao
         */
        //monta o localizador da posicao do aparelho
        LocationManager locationManager = (LocationManager) mContexto.getSystemService(Context.LOCATION_SERVICE);
        //o localizador da ultima posicao valida
        Location localizacaoUltima = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(null != localizacaoUltima) { mLocalizacao = "" + localizacaoUltima.getLatitude() + ":" + localizacaoUltima.getLongitude();}

        //actualizar sempre que se altere
        if(23 < Build.VERSION.SDK_INT
                && ContextCompat.checkSelfPermission(mContexto, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(mContexto, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ){
            Log.i("Localização", "Não Entrou!!!!");}
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
                        if (location != null) {
                            mLocalizacao = "" + location.getLatitude() + ":" + location.getLongitude();
                        }
                    }
                }, null);
            } catch (SecurityException e) {
            } catch (Exception e) {}
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
        // TODO: Update argument type and name
        void onListFragmentInteraction(ClassePicaPonto item);
    }

    /**
     * Classes
     */
    private void carrega_classes(){
        classeDataTempo = new ClasseDataTempo();
        dbAssistencia   = new DBAssistencia(mContexto, "Pica Ponto", "pica_ponto");
        classConeccao   = new ClasseInternetConectividade(mActividade);
    }
    /**
     * carrega elementos
     */
    private void carrega_elementos(){
        btPicarPonto  = (Button) mView.findViewById(R.id.btPicarPonto);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.listaPicaPonto);
        cvPicaPonto   = (CardView) mView.findViewById(R.id.cvPicaPontoLista);

    }
    private void lanca_eventos(){
        btPicarPonto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Put up the Yes/No message box
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder
                        .setTitle("Pica-Ponto")
                        .setMessage("Pretende prosseguir com o registo?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                regista_picaponto();
                            }
                        })
                        .setNegativeButton("Não", null)                        //Nao faz nada no no
                        .show();
            }
        });
    }


    /*************************************************************
     * procedimentos
     */
    private void regista_picaponto(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dataAgora = sdf.format(c.getTime());
        sdf = new SimpleDateFormat("HH:mm:ss");
        String horaAgora = sdf.format(c.getTime());

        Boolean regista = true;
        ArrayList<ClassePicaPonto> registosPP = dbAssistencia.get_picaPonto(mIdUtilizador);
        int nElementos = registosPP.size();

        if(0<nElementos){
            ClassePicaPonto ultimo = registosPP.get(nElementos-1);
            String data = ultimo.getData();
            String hora = ultimo.getHora();
            if(data.equals(dataAgora)){
                long diferenca = classeDataTempo.diferenca_horas(hora, horaAgora);
                regista = 60 < diferenca;
            }
        }

        if(regista){
            passa_ao_registo(dataAgora, horaAgora);
            monta_listaPicaPonto();
        }
    }

    public void passa_ao_registo(String data, String hora){
        String origem = "APP";
        String maquina = Settings.Secure.getString(mContexto.getContentResolver(), Settings.Secure.ANDROID_ID);
        int enviado = 0;
        int para_enviar = 1;

        //busca o proximo id para o pica ponto
        dbAssistencia.setNomeTabela("pica_ponto");
        final int id = dbAssistencia.get_proxID();
        ClasseCodigoPessoal CP = new ClasseCodigoPessoal(mNUtilizador);
        String codigo = CP.getCodigo();

        ContentValues valores = new ContentValues();
        valores.put("id", id);
        valores.put("id_utilizador", mIdUtilizador);
        valores.put("numero", mNUtilizador);
        valores.put("codigo", codigo);
        valores.put("origem", origem);
        valores.put("data", data);
        valores.put("hora", hora);
        valores.put("maquina", maquina);
        valores.put("enviado", enviado);
        valores.put("para_enviar", para_enviar);
        valores.put("gps", mLocalizacao);
        valores.put("dia_tarefa", data);

        if(dbAssistencia.insert(valores, "pica_ponto")) {
            Toast.makeText(mContexto, "Pica-Ponto '" + data + " " + hora + "' registado com sucesso!", Toast.LENGTH_SHORT).show();
            classConeccao.refaz();
            if(classConeccao.isLigado()) {
                ClasseEnviarPicaPonto cepp = new ClasseEnviarPicaPonto(mContexto);
                ArrayList<ClassePicaPonto> picas = dbAssistencia.get_picaPonto();
                cepp.setDados(picas);
                cepp.enviar(picas.size()-1);

                //faz_chamadaActualizar(6000);
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                Log.i("tag", "This'll run 6000 milliseconds later");
                                monta_listaPicaPonto();
                            }
                        },
                        6000);
            }
        }
    }

    private void faz_chamadaActualizar(int intervalo){
        Runnable runnable = new Runnable() {
            public void run() {
                monta_listaPicaPonto();
            }
        };
        Handler handler = new android.os.Handler();
        handler.postDelayed(runnable, intervalo);

        //handler.removeCallbacks(runnable);
    }

    /**
     * Lanca a lista do pica ponto
     */
    private void monta_listaPicaPonto(){
        if(mRecyclerView != null){
            mRecyclerView.removeAllViewsInLayout();
            mRecyclerView.setVisibility(View.VISIBLE);
            ArrayList<ClassePicaPonto>  lista = dbAssistencia.get_picaPonto(this.mIdUtilizador);
            int nElementos = lista.size();
            if(0==nElementos){
                cvPicaPonto.setVisibility(View.GONE);
            }
            else {
                cvPicaPonto.setVisibility(View.VISIBLE);
                RecyclerView.LayoutManager layout = new LinearLayoutManager(this.mContexto, LinearLayoutManager.VERTICAL, false);
                mRecyclerView.setLayoutManager(layout);

                mRecyclerView.setAdapter(new PicaPontoAdaptador(lista));
            }
        }
    }

}
