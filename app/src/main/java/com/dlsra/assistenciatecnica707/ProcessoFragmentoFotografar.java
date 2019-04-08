package com.dlsra.assistenciatecnica707;


import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;


public class ProcessoFragmentoFotografar extends Fragment {
    private static final int TAMANHO_IMAGE_LATERAL = 2048;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int BANDABYTE = 4096;

    private static final String mPastaTemporaria = "TempDirFotosAT";
    private File mDirTemporaria = null;
    private File mTempCameraPhotoFile = null;

    //declaracao de classes
    private DBAssistencia classAssistenciaDB = null;
    private ClasseDadosNavegacao classDadosNavegacao = null;
    private ClasseInternetConectividade classConeccao = null;
    private ClasseFicheiroCache classFicheiroCacheUtilizador = null;
    private ClasseEliminaDadosBD classEliminaDados = null;
    //variaveis
    private int mIdUtilizador;
    private int mNUtilizador;
    private int mIdMenu;
    private int mNObra;
    private String mCodObra;
    private int mAnoObra;
    private int mIdTarefa;
    private String _FaseObra = "";
    private String _DescricaoFoto = "";
    private String _GPSFoto = "";

    private String mPastaDepositoFotografias = "";
    private String mFileNameUtilizador = "";

    //elementos visuais
    private View janela = null;
    private EditText etDescricao   = null;
    private Spinner spFase         = null;
    private ImageButton ibBotao    = null;
    private CardView cvFotografia  = null;
    private TextView tvInfo        = null;
    private ImageView ivFotografia = null;

    //elementos sistema
    private Context mContexto = null;
    private Activity mActividade = null;

    public ProcessoFragmentoFotografar() {
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
        mActividade = this.getActivity();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        janela = inflater.inflate(R.layout.processo_fragmento_fotografar, container, false);
        return janela;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle){
        super.onViewCreated(view, bundle);

        mPastaDepositoFotografias = getString(R.string.pasta_deposito_fotografias)+"_"+mIdUtilizador;
        mFileNameUtilizador       = getString(R.string.ficheiro_credenciais_utilizador);
        mDirTemporaria            = new File(Environment.getExternalStorageDirectory(), mPastaTemporaria);

        carrega_classes();

        //se existe ficheiro dados cache utilizador
        boolean a = classFicheiroCacheUtilizador.criaFicheiro();
        //elimina o conteudo da pasta temporaria das fotografias...
        if (mDirTemporaria.isDirectory()){ classEliminaDados.elimina_recursivo(mDirTemporaria);}
        if(!mDirTemporaria.exists()){ boolean b = mDirTemporaria.mkdirs();}

        carrega_elementos();
        preenche_dadosJanela();
        //por causa do onchange
        aponta_eventos();

        /**
         * se nao tem obra np fotografica
         */
        if(0==mNObra){
            ibBotao.setVisibility(View.GONE);
            spFase.setVisibility(View.GONE);
            cvFotografia.setVisibility(View.GONE);
        }
        else{
            ibBotao.setVisibility(View.VISIBLE);
            spFase.setVisibility(View.VISIBLE);
            cvFotografia.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String filePath = "";
    }
    /**
     *
     */

    private void carrega_classes(){
        //inicializa a classe
        classAssistenciaDB           = new DBAssistencia(mContexto, "TAREFA", "tarefa");
        classDadosNavegacao          = new ClasseDadosNavegacao(mContexto);
        classConeccao                = new ClasseInternetConectividade(mActividade);
        classFicheiroCacheUtilizador = new ClasseFicheiroCache(mContexto, mFileNameUtilizador);
        classEliminaDados            = new ClasseEliminaDadosBD(mContexto, mIdUtilizador);
    }

    private void carrega_elementos(){
        etDescricao   = (EditText)    janela.findViewById(R.id.etFotografarDescricao);
        spFase        = (Spinner)     janela.findViewById(R.id.spFotografarFases);
        ibBotao       = (ImageButton) janela.findViewById(R.id.ibFotografarBotao);
        cvFotografia  = (CardView)    janela.findViewById(R.id.cvFotografarFotografia);
        tvInfo        = (TextView)    janela.findViewById(R.id.tvFotografarInfo);
        ivFotografia  = (ImageView)   janela.findViewById(R.id.ivFotografarFotografia);
    }

    private void aponta_eventos(){
        /**/
        //ao alterar valores, guarda
        etDescricao.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String descricao = etDescricao.getText().toString();
                classDadosNavegacao.atribui_elemento("fotografar_descricao", descricao);
            }
        });

        etDescricao.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    final boolean b = ((InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                            etDescricao.getWindowToken(), 0);
                }
            }
        });
        /**/
        spFase.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = spFase.getItemAtPosition(position);
                _FaseObra = item.toString();
                classDadosNavegacao.atribui_elemento("fotografar_fase", _FaseObra);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        ibBotao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //testa os campos
                _FaseObra = String.valueOf(spFase.getSelectedItem());
                _DescricaoFoto = etDescricao.getText().toString();

                Log.i("Dados", "Fase: " + _FaseObra + "\n" + "Descricao: " + _DescricaoFoto);
                if (_FaseObra.equals("") || _DescricaoFoto.equals("")) {
                    String erros = "";
                    if (_FaseObra.equals("")) {
                        erros += (erros.equals("") ? "" : "\n") + "Campo 'Fase do Processo' é obrigatório!";
                    }
                    if (_DescricaoFoto.equals("")) {
                        erros += (erros.equals("") ? "" : "\n") + "Campo 'Descrição/Elemento' é obrigatório!";
                    }
                    Toast.makeText(getContext(), erros, Toast.LENGTH_SHORT).show();
                } else {
                    //disparaModoFotografar();
                    tiraPelaCamera();
                }
            }
        });

        /**
         * da-me gps localizacao
         */
        //monta o localizador da posicao do aparelho
        LocationManager locationManager = (LocationManager) mContexto.getSystemService(Context.LOCATION_SERVICE);
        //o localizador da ultima posicao valida
        Location localizacaoUltima = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(null != localizacaoUltima) { _GPSFoto = "" + localizacaoUltima.getLatitude() + ":" + localizacaoUltima.getLongitude();}

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
                        if (location != null) {
                            _GPSFoto = "" + location.getLatitude() + ":" + location.getLongitude();
                        }
                    }
                }, null);
            }
            catch (SecurityException e) {}
            catch (Exception e) {}
        }
    }

    // ---------------------
    private void preenche_dadosJanela(){
        //atribui valores guardados
        String descricao = classDadosNavegacao.get_valor("fotografar_descricao");

        String fase = classDadosNavegacao.get_valor("fotografar_fase");
        ArrayAdapter myAdap = (ArrayAdapter) spFase.getAdapter(); //cast to an ArrayAdapter
        try {
            etDescricao.setText(descricao);
            int spinnerPosition = myAdap.getPosition(fase);
            //set the default according to value
            spFase.setSelection(spinnerPosition);
        }
        catch (Exception e){ Log.e("Preenche", e.getMessage());}
    }

    /* TIRAR FOTOGRAFIA */
    private void tiraPelaCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            mTempCameraPhotoFile = new File(mDirTemporaria, "/" + UUID.randomUUID().toString().replaceAll("-", "") + ".jpg");
            Log.d("image", "/" + UUID.randomUUID().toString().replaceAll("-", "") + ".jpg");
            //pega a imagem directamente e guarda no ficheiro provisorio
            //já que pelo Intent retorna uma mini imagem...
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTempCameraPhotoFile));
            mActividade.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void trata_fotografia(){
        String filePath = "";

        try {
            filePath = mTempCameraPhotoFile.getAbsolutePath();
        }
        catch(RuntimeException te){ Log.e("Fotografar", te.toString());}

        Bitmap imageBitmap = null;
        InputStream input = null;
        OutputStream output = null;
        BufferedInputStream bis = null;

        File raiz = null;
        File ficheiro = null;

        int id_foto = classAssistenciaDB.get_proxID("fotografia");
        String nomeTemporario = id_foto+".jpg";

        try {
            URLConnection conn = new URL("file:" + filePath).openConnection();

            conn.connect();
            input = conn.getInputStream();
            //1- por ficheiro
            // abre/cria o directorio para deposito, relacionado com o processo
            raiz = new File(mContexto.getFilesDir(), mPastaDepositoFotografias);
            raiz.getParentFile().mkdirs();//caso nao exista cria
            if(null != raiz) {
                //abre/cria o ficheiro no directorio aberto
                ficheiro = new File(raiz, nomeTemporario); //
                ficheiro.getParentFile().mkdirs();
                if(null != ficheiro) {
                    //escreve no ficheiro
                    output = new FileOutputStream(ficheiro);
                    byte dataR[] = new byte[BANDABYTE];//
                    int count;
                    while ((count = input.read(dataR)) != -1) {
                        output.write(dataR, 0, count);
                    }
                }
                else{ Log.e("Directório", "Não abre ficheiro!");}
            }
            else{ Log.e("Directório", "Não abre a raiz!");}

            //2- por Bitmap
                /*
                bis = new BufferedInputStream(input);
                imageBitmap = BitmapFactory.decodeStream(bis);
                */
            // Decode image to get smaller image to save memoria
            //tamanho da imagem
            int sampleSize = calculaSampleSize(ficheiro.getAbsolutePath());

            //descodifica imagem
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = sampleSize;
            if(null != ficheiro) {
                imageBitmap = BitmapFactory.decodeFile(ficheiro.getPath(), options);
                Log.i("tamanho", ""+imageBitmap.getByteCount());
            }
            //mostra a fotografia
            this.ivFotografia.setVisibility(View.VISIBLE);
            this.ivFotografia.setImageBitmap(imageBitmap);
            //regista a fotografia no processo
            Toast.makeText(getContext(), "Fotografia tirada com sucesso!", Toast.LENGTH_SHORT).show();
        }
        catch (OutOfMemoryError e){ Log.e("TRATA", e.toString());}
        catch (MalformedURLException me){ Log.e("TRATA", me.toString());}
        catch (Exception e) { Log.e("TRATA", e.toString());}
        finally {
            try {
                if (null != output){ output.close(); output = null;}
                if (null != input){ input.close(); input = null;}
                if (null != raiz){ raiz = null;}
                if (null != ficheiro){ ficheiro = null;}
                if (null != bis){ bis.close(); bis = null;}
            } catch (IOException ignora) {}
        }

        registaFotografiaDB(imageBitmap, nomeTemporario);
    }

    private int calculaSampleSize(String caminho){
        int sampleSize = 1;

        int maxTamanho = 2048;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 1;
        final Bitmap imageBitmap = BitmapFactory.decodeFile(caminho, options);
        int height = imageBitmap.getHeight();
        int width  = imageBitmap.getWidth();
        float tamanhoMax = Math.max(height, width);
        while(0<tamanhoMax && maxTamanho < tamanhoMax){
            tamanhoMax /= 2;
            sampleSize *= 2;
        }
        Log.i("Tamanho", tamanhoMax+" ("+height+", "+width+") > "+sampleSize);
        return sampleSize;
    }


    private void registaFotografiaDB(Bitmap bitmap, String nomeTemporario){
        final Integer nPessoa = Integer.parseInt(classFicheiroCacheUtilizador.getTextoFicheiro());
        classAssistenciaDB.setNomeTabela("tarefa");
        HashMap<String, String> dados = classAssistenciaDB.getDadosBy("id", ""+mIdTarefa, new String []{"data"});
        String dia_tarefa = dados.get("data");

        // nome e pasta
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = null;
        // -  Usa a data para nomear o ficheiro
        sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.UK);
        final String strDateFoto = sdf.format(c.getTime());
        final String nome_ficheiro = "foto_"+strDateFoto+".jpg";

        // - compoe a pasta
        sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        final String strDatePasta = sdf.format(c.getTime());
        final String pasta = _FaseObra+" "+strDatePasta;
        // - tempo agora
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
        final String data_registo = sdf.format(c.getTime());

        //busca o proximo id para a fotografia
        classAssistenciaDB.setNomeTabela("fotografia");
        final int id = classAssistenciaDB.get_proxID();

        try {
            //comprime o bitmap para guardar na base de dados
            //bitmap = getResizedBitmap(bitmap, TAMANHO_IMAGE_LATERAL);
            int limite = TAMANHO_IMAGE_LATERAL * 100;
            int largura = Math.max(bitmap.getWidth(), bitmap.getHeight());
            float relacao = Math.round(limite / largura);
            int percentagem = Math.min(100, Math.round(relacao));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, percentagem, out);
            final byte[] buffer = out.toByteArray();

            //Bitmap comprimida = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
            //Log.i("Comprimida", comprimida.getWidth() + "x" + comprimida.getHeight());

            ContentValues valores = new ContentValues();
            valores.put("id", id);
            valores.put("n_obra", mNObra);
            valores.put("cod_obra", mCodObra);
            valores.put("ano_obra", mAnoObra);
            valores.put("nome_ficheiro", nome_ficheiro);
            valores.put("pasta", pasta);
            valores.put("tipologia", "");
            valores.put("descricao", _DescricaoFoto);
            valores.put("por", nPessoa);
            valores.put("origem", 2);
            valores.put("enviado", 0);
            valores.put("data_registo", data_registo);
            valores.put("gps", _GPSFoto);
            valores.put("data", buffer);
            valores.put("nome_temporario", nomeTemporario);
            valores.put("dia_tarefa", dia_tarefa);
            //int tam = buffer.length;
            if (classAssistenciaDB.insert(valores, "fotografia")) {
                Toast.makeText(getContext(), "Fotografia '" + nome_ficheiro + "' alojada com sucesso!", Toast.LENGTH_SHORT).show();
            }

            classConeccao.refaz();
            if (classConeccao.isLigado()) {
                ClasseEnviarFotografias cef = new ClasseEnviarFotografias(mContexto);
                cef.setDados(get_fotosAEnviar());
                cef.enviar(0);//sao devolvidas por data DESC...
            }
        }
        catch (OutOfMemoryError e){ Log.e("RegFoto", e.getMessage());}
    }

    private ArrayList<ClasseFotografias> get_fotosAEnviar(){
        ArrayList<ClasseFotografias> fotografiasAEnviar = new ArrayList<>();

        ArrayList<ClasseFotografias> fotografias = classAssistenciaDB.get_fotografias(true);
        for(ClasseFotografias foto: fotografias){
            int origem     = foto.getOrigem();
            boolean enviar = !foto.isEnviado();
            if(2==origem && enviar){
                fotografiasAEnviar.add(foto);
            }
        }
        return fotografiasAEnviar;
    }
}
