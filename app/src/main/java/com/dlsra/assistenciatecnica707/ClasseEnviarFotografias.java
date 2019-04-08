package com.dlsra.assistenciatecnica707;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by AlScoundrel on 11/06/2017.
 */
public class ClasseEnviarFotografias {
    private static final int BANDABYTE = 4096;
    //variaveis...
    private ArrayList<ClasseFotografias> dados_enviar = null;
    private ArrayList<EnviarFotografiaTask> _EnviarFotografiasTask = null;
    //
    private Context _contexto = null;

    //elementos
    private ProgressBar progressoEnvio = null;
    private TextView    mensagemEnvio  = null;

    ClasseEnviarFotografias(Context contexto){
        this._contexto = contexto;
    }

    public void setElementos(View progressBar, View textView){
        this.progressoEnvio = (ProgressBar) progressBar;
        this.mensagemEnvio  = (TextView) textView;
    }

    public void setDados(ArrayList<ClasseFotografias> fotos){ this.dados_enviar = fotos;}

    public void enviar() {
        if(null != progressoEnvio) {
            progressoEnvio.setMax(dados_enviar.size());
            progressoEnvio.setVisibility(View.VISIBLE);
        }
        if(null != mensagemEnvio){ mensagemEnvio.setVisibility(View.VISIBLE);}
        for(ClasseFotografias foto:dados_enviar){
            enviar(foto);
        }
    }

    public void enviar(int posicao){
        //se passar -1 é a ultima posicao a enviar
        if(null != dados_enviar) {
            ClasseFotografias foto = -1 == posicao ? dados_enviar.get(dados_enviar.size() - 1) : dados_enviar.get(posicao);
            enviar(foto);
        }
    }

    public void enviar(ClasseFotografias foto){
        if(null==_EnviarFotografiasTask){ _EnviarFotografiasTask = new ArrayList<>();}
        if(null!=foto){
            EnviarFotografiaTask envia = new EnviarFotografiaTask(foto);
            envia.execute();

            _EnviarFotografiasTask.add(envia);
        }
    }

    public void cancelar(){
        if(null!=_EnviarFotografiasTask){
            for(EnviarFotografiaTask pedido:_EnviarFotografiasTask){
                pedido.cancel(true);
            }
            _EnviarFotografiasTask = null;
        }
    }

    private void se_finalizado(){
        if(null != progressoEnvio && null != mensagemEnvio){
            if(progressoEnvio.getMax() == progressoEnvio.getProgress()){
                progressoEnvio.setVisibility(View.GONE);
                mensagemEnvio.setVisibility(View.GONE);
            }
        }
    }

    //classe de segundo plano para envio de fotografias
    private class EnviarFotografiaTask extends AsyncTask<Void, Integer, String> {
        private int id_foto = 0;
        private int n_obra = 0;
        private String cod_obra = "";
        private int ano_obra = 0;
        private String pasta = "";
        private String nome_ficheiro = "";
        private String descricao = "";
        private int origem = 0;
        private String tipologia = "";
        private String data_registo = "";
        private String por = "";
        private String gps = "";
        private Bitmap dataImg = null;
        private String nome_temporario;

        private String enderecoURL;
        private String pasta_servidor;
        private String endereco;
        private String fileNameEndereco;
        //declaracao de classes
        private DBAssistencia classAssistenciaDB = null;
        private ClasseFicheiroCache classFicheiroCacheEndereco = null;


        EnviarFotografiaTask(ClasseFotografias fotografia) {
            this.id_foto = fotografia.getId();
            this.n_obra = fotografia.getN_obra();
            this.cod_obra = fotografia.getCod_obra();
            this.ano_obra = fotografia.getAno_obra();
            this.pasta = fotografia.getPasta();
            this.nome_ficheiro = fotografia.getNome_ficheiro();
            this.descricao = fotografia.getDescricao();
            this.origem = fotografia.getOrigem();
            this.tipologia = fotografia.getTipologia();
            this.data_registo = fotografia.getData_registo();
            this.por = fotografia.getPor();
            this.gps = fotografia.getGps();
            this.dataImg = fotografia.getData();
            this.nome_temporario = fotografia.getNome_temporario();

            //ficheiro guarda endereco servidor
            fileNameEndereco = _contexto.getString(R.string.ficheiro_endereco_electronico);
            classFicheiroCacheEndereco = new ClasseFicheiroCache(_contexto, fileNameEndereco);
            //inicializa a classe tarefa
            classAssistenciaDB = new DBAssistencia(_contexto, "TAREFA", "fotografia");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            enderecoURL = classFicheiroCacheEndereco.getTextoFicheiro();

            //no caso de nao ter uma url valida, regista a url por defeito
            if(""==enderecoURL){}
            else{
                pasta_servidor = _contexto.getString(R.string.pasta_no_servidor);
                //cria a ligacao
                endereco = enderecoURL+"/"+pasta_servidor+"/processos/foto_upload.php";
                Log.i("Endereço", endereco);
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            publishProgress(1);
            String responseString = "";
            byte[] byteArray = null;

            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                dataImg.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byteArray = stream.toByteArray();
            }catch (NullPointerException e){ Log.e("UPLOAD #0", e.toString());}

            if(null != byteArray) {
                HttpClient client = new HttpClient(endereco);

                try {
                    client.connectForMultipart();
                    client.addFormPart("n_obra", "" + n_obra);
                    client.addFormPart("cod_obra", cod_obra);
                    client.addFormPart("ano_obra", "" + ano_obra);
                    client.addFormPart("pasta", URLEncoder.encode(pasta, "UTF8"));
                    client.addFormPart("nome_ficheiro", URLEncoder.encode(nome_ficheiro, "UTF8"));
                    client.addFormPart("tipologia", URLEncoder.encode(tipologia, "UTF8"));
                    client.addFormPart("descricao", URLEncoder.encode(descricao, "UTF8"));
                    client.addFormPart("por", por);
                    client.addFormPart("data_registo", data_registo.replace(" ", "+"));
                    client.addFormPart("gps", gps.replace(" ","+"));
                    client.addFilePart("minha_fotografia", nome_ficheiro, byteArray);
                    client.finishMultipart();

                    if (!client.fezLigacao()) {
                        Log.e("Ligação", "Sem sucesso!");
                    } else {
                        responseString = client.getResponse();
                    }
                } catch (RuntimeException e) {
                    Log.e("UPLOAD #1", e.toString());
                } catch (Throwable t) {
                    Log.e("UPLOAD #2", t.toString());
                }
            }
            publishProgress(2);
            return responseString;
        }

        @Override
        protected void onPostExecute(final String resultado) {
            //Log.i("Upload", resultado);
            if(0 < resultado.indexOf("SUCESSO!")) {

                try {
                    ContentValues valores = new ContentValues();
                    valores.put("enviado", 1);
                    boolean sucesso = classAssistenciaDB.updateDados(valores, "id", ""+this.id_foto, "fotografia");
                    if(sucesso && null == progressoEnvio) {
                        //Log.i("Fotografia", "Actualizada com sucesso!");
                        Toast.makeText(_contexto, "Fotografia '"+this.nome_ficheiro+"' enviada para o servidor com sucesso!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("Upload", e.toString());
                }


            }
            else{ Log.i("Upload", "insucesso!");}

            se_finalizado();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            int qual = progress[0];
            if(null != progressoEnvio && null != mensagemEnvio) {
                if (1 == qual) {//com sucesso
                    float progresso = progressoEnvio.getProgress() * 100 / progressoEnvio.getMax();//progressoEnvioFotos.getProgress() + "/" + progressoEnvioFotos.getMax()
                    mensagemEnvio.setText(Math.round(progresso) + "% Enviar fotografia:" + this.nome_ficheiro);
                } else if (2 == qual) { //insucesso
                    progressoEnvio.incrementProgressBy(1);
                }
            }
        }


        /**
         * É chamado quando a execucao é cancelada
         */
        @Override
        protected void onCancelled() {
            String descricao = "O pedido de upload fotografia foi interrompido!";
            Toast.makeText(_contexto, descricao, Toast.LENGTH_LONG).show();
            if(null != progressoEnvio) { progressoEnvio.incrementProgressBy(1);}
        }
    }
}