package com.dlsra.assistenciatecnica707;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.HashMap;

/**
 * Created by Utilizador on 12/04/2017.
 */

public class ClasseEliminaDadosBD {
    private Context mContexto;
    private int idUtilizador;
    private String hoje;
    private String pastaDepositoFotografias = "";
    //classes
    private DBAssistencia classeAssistenciaDB = null;
    private ClasseDataTempo classeDataTempo = null;

    public ClasseEliminaDadosBD(Context context, int id_utilizador){
        mContexto=context;
        idUtilizador = id_utilizador;

        classeAssistenciaDB = new DBAssistencia(context, "UTILIZADOR", "utilizador");
        classeDataTempo = new ClasseDataTempo();

        hoje = classeDataTempo.data_hoje();
        this.pastaDepositoFotografias = mContexto.getString(R.string.pasta_deposito_fotografias)+"_"+this.idUtilizador;
    }

    /**
     *
     * eliminacao por nivel
     * @param nivel
     * 3- tudo
     * 2- do utilizador
     * 1- do utilizador (exepto ficheiros nas pastas)
     */
    public void elimina_dados(int nivel){
        if(3==nivel){ elimina_recursivo(this.mContexto.getFilesDir());}
        else if(2==nivel){ elimina_recursivo(new File(this.mContexto.getFilesDir(), this.pastaDepositoFotografias));}
        if(0<nivel) {
            elimina_tarefas(nivel);
            elimina_tabela("processo", false, nivel);
            elimina_tabela("seguro", false, nivel);
            elimina_tabela("pessoa", false, nivel);
            elimina_tabela("morada_obra", false, nivel);
            elimina_fotografias(nivel);
            if(1<nivel){ elimina_tabela("ficheiro", false, nivel);}
            elimina_tabela("evolucao", true, nivel);
            elimina_tabela("trabalhos", false, nivel);
            elimina_tabela("coordenadas", true, nivel);
            elimina_tabela("pica_ponto", true, nivel);
            elimina_tabela("evolucao_obra", true, nivel);
            elimina_tabela("levantamento_tecnico", true, nivel);
            elimina_tabela("levantamento_topografica", true, nivel);
            elimina_tabela("levantamento_fraccao", true, nivel);
            elimina_tabela("levantamento_evolucao", true, nivel);
            elimina_tabela("levantamento_evolucao_material", true, nivel);
        }
    }

    public void elimina_utilizadores(){
        classeAssistenciaDB.delete("utilizador");
    }

    private void elimina_tarefas(int nivel){
        if(3==nivel){ classeAssistenciaDB.delete("tarefa");}
        else{ classeAssistenciaDB.delete("(id_utilizador=? AND data=?) OR data<>?", new String[]{""+idUtilizador, hoje, hoje}, "tarefa");}
    }


    private void elimina_tabela(String tabela, boolean enviar, int nivel){
        if(3==nivel){ classeAssistenciaDB.delete(tabela);}
        else {
            if (enviar){ classeAssistenciaDB.delete("?<enviado AND ((id_utilizador=? AND dia_tarefa=?) OR dia_tarefa<>?)", new String[]{"" + 0, "" + idUtilizador, hoje, hoje}, tabela);}
            else{ classeAssistenciaDB.delete("(id_utilizador=? AND dia_tarefa=?) OR dia_tarefa<>?", new String[]{""+idUtilizador, hoje, hoje}, tabela);}
        }
    }

    private void elimina_fotografias(int nivel){
        String tabela = "fotografia";
        if(3==nivel){ classeAssistenciaDB.delete("?<enviado", "0", tabela);} /*nao deve eliminar os para enviar*/
        else { elimina_tabela(tabela, true, nivel);}/*incorpora elementos de envio*/
    }
    /**
     *
     * @param file
     */
    public void elimina_recursivo(File file){
        if(file.exists()){
            //se directorio
            if(file.isDirectory()) {
                File[] lista = file.listFiles();
                if(null!=lista) {
                    for (File caminho : lista) {
                        elimina_recursivo(caminho);
                    }
                }
            }
            String nome = file.getName();
            /* retira da eliminacao ficheiros usados pelo sistema */
            if(nome.equals("files")
                    || nome.equals(mContexto.getString(R.string.ficheiro_credenciais_utilizador))
                    || nome.equals(mContexto.getString(R.string.ficheiro_dados_navegacao))
                    || nome.equals(mContexto.getString(R.string.ficheiro_endereco_electronico))) { }
            else{
                try {
                    boolean apaga = file.delete();
                    Log.i("Elimina", (apaga ? "Eliminou: " : "Não Eliminou: ") + file.getPath());
                } catch (Exception e) {
                    Log.e("Elimina", e.toString());
                }
            }
        }
    }
}
