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
 * Created by AlScoundrel on 11/06/2017.
 */
public class ClasseEnviarCoordenadas {
    private static final int BANDABYTE = 4096;
    //variaveis...
    private ArrayList<ClasseCoordenadas> dados_enviar = null;
    private ArrayList<EnviarCoordenadaTask> _EnviarCoordenadasTask = null;
    //
    private Context _contexto = null;

    //elementos
    private ProgressBar progressoEnvio = null;
    private TextView    mensagemEnvio  = null;

    ClasseEnviarCoordenadas(Context contexto){
        this._contexto = contexto;
    }

    public void setElementos(View progressBar, View textView){
        this.progressoEnvio = (ProgressBar) progressBar;
        this.mensagemEnvio  = (TextView) textView;
    }

    public void setDados(ArrayList<ClasseCoordenadas> coordenadas){ this.dados_enviar = coordenadas;}

    public void enviar() {
        if(null != progressoEnvio) {
            progressoEnvio.setMax(dados_enviar.size());
            progressoEnvio.setVisibility(View.VISIBLE);
        }
        if(null != mensagemEnvio){ mensagemEnvio.setVisibility(View.VISIBLE);}
        for(ClasseCoordenadas coordenada:dados_enviar){
            enviar(coordenada);
        }
    }

    public void enviar(int posicao){
        //se passar -1 é a ultima posicao a enviar
        if(null != dados_enviar) {
            ClasseCoordenadas coordenada = -1 == posicao ? dados_enviar.get(dados_enviar.size() - 1) : dados_enviar.get(posicao);
            enviar(coordenada);
        }
    }

    public void enviar(ClasseCoordenadas coordenada){
        if(null==_EnviarCoordenadasTask){ _EnviarCoordenadasTask = new ArrayList<>();}
        if(null!=coordenada){
            EnviarCoordenadaTask envia = new EnviarCoordenadaTask(coordenada);
            envia.execute();

            _EnviarCoordenadasTask.add(envia);
        }
    }

    public void cancelar(){
        if(null!=_EnviarCoordenadasTask){
            for(EnviarCoordenadaTask pedido:_EnviarCoordenadasTask){
                pedido.cancel(true);
            }
            _EnviarCoordenadasTask = null;
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

    //classe de segundo plano para envio de coordenadas
    private class EnviarCoordenadaTask extends AsyncTask<Void, Integer, String> {
        private int id_coordenada = 0;
        private int n_obra = 0;
        private String cod_obra = "";
        private int ano_obra = 0;
        private int id_pessoa = 0;
        private String latitude = "";
        private String longitude = "";
        private String por = "";
        private String gps = "";

        private String enderecoURL;
        private String pasta_servidor;
        private String endereco;
        private String fileNameEndereco;
        //declaracao de classes
        private DBAssistencia classAssistenciaDB = null;
        private ClasseFicheiroCache classFicheiroCacheEndereco = null;


        EnviarCoordenadaTask(ClasseCoordenadas coordenada) {
            this.id_coordenada = coordenada.getId();
            this.n_obra = coordenada.getN_obra();
            this.cod_obra = coordenada.getCod_obra();
            this.ano_obra = coordenada.getAno_obra();
            this.id_pessoa = coordenada.getId_pessoa();
            this.por = new ClasseFicheiroCache(_contexto, _contexto.getString(R.string.ficheiro_credenciais_utilizador)).getTextoFicheiro();
            this.gps = coordenada.getGps();

            String[] partes = gps.split(":");
            if(2==partes.length){ this.latitude = partes[0]; this.longitude = partes[1];}

            //ficheiro guarda endereco servidor
            fileNameEndereco = _contexto.getString(R.string.ficheiro_endereco_electronico);
            classFicheiroCacheEndereco = new ClasseFicheiroCache(_contexto, fileNameEndereco);
            //inicializa a classe tarefa
            classAssistenciaDB = new DBAssistencia(_contexto, "TAREFA", "coordenada");
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
                endereco = enderecoURL+"/"+pasta_servidor+"/processos/coordenadas_upload.php";
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
                client.addFormPart("n_obra", "" + n_obra);
                client.addFormPart("cod_obra", cod_obra);
                client.addFormPart("ano_obra", "" + ano_obra);
                client.addFormPart("id_pessoa", "" + id_pessoa);
                client.addFormPart("latitude", latitude);
                client.addFormPart("longitude", longitude);
                client.addFormPart("por", por);
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
                    boolean sucesso = classAssistenciaDB.updateDados(valores, "id", ""+this.id_coordenada, "coordenadas");
                    if(sucesso && null == progressoEnvio) {
                        //Log.i("Coordenada", "Actualizada com sucesso!");
                        Toast.makeText(_contexto, "Coordenada da obra '"+n_obra+" "+cod_obra+"/"+ano_obra+"' enviada para o servidor com sucesso!", Toast.LENGTH_SHORT).show();
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
                    float progresso = progressoEnvio.getProgress() * 100 / progressoEnvio.getMax();//progressoEnvioCoordenadas.getProgress() + "/" + progressoEnvioCoordenadas.getMax()
                    mensagemEnvio.setText(Math.round(progresso) + "% Enviar coordenada da obra:" + +n_obra+" "+cod_obra+"/"+ano_obra);
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
            String descricao = "O pedido de upload coordenada foi interrompido!";
            Toast.makeText(_contexto, descricao, Toast.LENGTH_LONG).show();
            if(null != progressoEnvio) { progressoEnvio.incrementProgressBy(1);}
        }
    }
}