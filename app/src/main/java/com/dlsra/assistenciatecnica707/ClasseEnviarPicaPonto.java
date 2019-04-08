package com.dlsra.assistenciatecnica707;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by AlScoundrel on 11/03/2016.
 */
public class ClasseEnviarPicaPonto {
    private static final int BANDABYTE = 4096;
    //variaveis...
    private ArrayList<ClassePicaPonto> dados_enviar = null;
    private ArrayList<EnviarPicaPontoTask> _EnviarPicaPontoTask = null;
    //
    private Context _contexto = null;

    //elementos
    private ProgressBar progressoEnvio = null;
    private TextView    mensagemEnvio  = null;

    ClasseEnviarPicaPonto(Context contexto){
        this._contexto = contexto;
    }

    public void setElementos(View progressBar, View textView){
        this.progressoEnvio = (ProgressBar) progressBar;
        this.mensagemEnvio  = (TextView) textView;
    }

    public void setDados(ArrayList<ClassePicaPonto> picas){ this.dados_enviar = picas;}

    public void enviar() {
        if(null != progressoEnvio) {
            progressoEnvio.setMax(dados_enviar.size());
            progressoEnvio.setVisibility(View.VISIBLE);
        }
        if(null != mensagemEnvio){ mensagemEnvio.setVisibility(View.VISIBLE);}
        for(ClassePicaPonto pica:dados_enviar){
            enviar(pica);
        }
    }

    public void enviar(int posicao){
        //se passar -1 é a ultima posicao a enviar
        if(null != dados_enviar) {
            ClassePicaPonto pica = -1 == posicao ? dados_enviar.get(dados_enviar.size() - 1) : dados_enviar.get(posicao);
            enviar(pica);
        }
    }

    public void enviar(ClassePicaPonto pica){
        if(null==_EnviarPicaPontoTask){ _EnviarPicaPontoTask = new ArrayList<>();}
        if(null!=pica){
            EnviarPicaPontoTask envia = new EnviarPicaPontoTask(pica);
            envia.execute();

            _EnviarPicaPontoTask.add(envia);
        }
    }

    public void cancelar(){
        if(null!=_EnviarPicaPontoTask){
            for(EnviarPicaPontoTask pedido:_EnviarPicaPontoTask){
                pedido.cancel(true);
            }
            _EnviarPicaPontoTask = null;
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

    //classe de segundo plano para envio de picaponto
    private class EnviarPicaPontoTask extends AsyncTask<Void, Integer, String> {
        private int id;
        private int numero;
        private int codigo;
        private String origem;
        private String data;
        private String hora;
        private String maquina;
        private String gps;

        private String enderecoURL;
        private String pasta_servidor;
        private String endereco;
        private String fileNameEndereco;
        //declaracao de classes
        private DBAssistencia classAssistenciaDB = null;
        private ClasseFicheiroCache classFicheiroCacheEndereco = null;


        EnviarPicaPontoTask(ClassePicaPonto pica) {
            this.id      = pica.getId();
            this.numero  = pica.getNumero();
            this.codigo  = pica.getCodigo();
            this.origem  = pica.getOrigem();
            this.data    = pica.getData();
            this.hora    = pica.getHora();
            this.maquina = pica.getMaquina();
            this.gps     = pica.getGps();

            //ficheiro guarda endereco servidor
            fileNameEndereco = _contexto.getString(R.string.ficheiro_endereco_electronico);
            classFicheiroCacheEndereco = new ClasseFicheiroCache(_contexto, fileNameEndereco);
            //inicializa a classe tarefa
            classAssistenciaDB = new DBAssistencia(_contexto, "PICA", "pica_ponto");
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
                endereco = enderecoURL+"/"+pasta_servidor+"/pica_ponto/registo.php";
                Log.i("Endereço", endereco);
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            publishProgress(1);
            String responseString = "";

            HttpClient client = new HttpClient(endereco);

            try {
                client.connectForMultipart();
                client.addFormPart("numero", "" + numero);
                client.addFormPart("codigo", "" + codigo);
                client.addFormPart("origem", URLEncoder.encode(origem, "UTF8"));
                client.addFormPart("data", URLEncoder.encode(data, "UTF8"));
                client.addFormPart("hora", hora);
                client.addFormPart("maquina", URLEncoder.encode(maquina, "UTF8"));
                client.addFormPart("gps", gps.replace(" ","+"));
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
                    boolean sucesso = classAssistenciaDB.updateDados(valores, "id", ""+this.id, "pica_ponto");
                    if(sucesso && null == progressoEnvio) {
                        //Log.i("PicaPonto", "Actualizada com sucesso!");
                        Toast.makeText(_contexto, "Pica das '"+this.hora.substring(0,5)+"' enviado para o servidor com sucesso!", Toast.LENGTH_SHORT).show();
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
                    float progresso = progressoEnvio.getProgress() * 100 / progressoEnvio.getMax();//progressoEnvio.getProgress() + "/" + progressoEnvio.getMax()
                    mensagemEnvio.setText(Math.round(progresso) + "% Enviar pica:" + this.hora.substring(0,5));
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
            String descricao = "O pedido de upload pica foi interrompido!";
            Toast.makeText(_contexto, descricao, Toast.LENGTH_LONG).show();
            if(null != progressoEnvio) { progressoEnvio.incrementProgressBy(1);}
        }
    }
}