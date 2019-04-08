package com.dlsra.assistenciatecnica707;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by AlScoundrel on 29/02/2016.
 */
public class ClasseInternalStorage {
    static final int READ_BLOCK_SIZE = 16384;//8192, 4096

    Context context = null;
    File dirRaiz = null;
    String raiz = "";
    String _FileNameUtilizador;
    String _FileNameEndereco;
    String _FileDadosNavegacao;
    private String pastaDepositoFotografias = "";
    //declaracao de classes
    private DBAssistencia classAssistenciaDB = null;

    ClasseInternalStorage(Context contexto){
        this.context = contexto;
        this.dirRaiz = contexto.getFilesDir();
        this.raiz = dirRaiz.getPath();

        //ficheiro guarda utilizador
        _FileNameUtilizador = contexto.getString(R.string.ficheiro_credenciais_utilizador);
        //ficheiro guarda endereco servidor
        _FileNameEndereco = contexto.getString(R.string.ficheiro_endereco_electronico);
        //ficheiro guarda dados navegacao
        _FileDadosNavegacao = contexto.getString(R.string.ficheiro_dados_navegacao);
        this.pastaDepositoFotografias = contexto.getString(R.string.pasta_deposito_fotografias);

        //inicializa a classe tarefa
        classAssistenciaDB = new DBAssistencia(contexto, "TAREFA", "fotografia");
    }

    /**
     * Abre directorio no armazem interno, caso nao exista
     * @param nome
     * @return
     */
    public boolean abre_directorio(String nome){
        boolean abre = false;
        try{
            File dir= this.context.getDir(nome, Context.MODE_PRIVATE);
            abre = dir.exists();
        }
        catch(Exception e){ Log.e("Abrir directorio", e.toString());}
        return abre;
    }

    /**
     *
     * @param nome
     * @return
     */
    public boolean abre_subDirectorio(String nome){
        return abre_subDirectorio(this.context.getFilesDir(), nome);
    }

    public boolean abre_subDirectorio(File onde, String nome){
        boolean abre = false;
        try{
            //cria File onde passo no nome da pasta a criar
            File subdir= new File(onde, nome);
            //cria o directorio, caso nao exista
            subdir.mkdir();
            abre = subdir.exists() && subdir.isDirectory();
        }
        catch(Exception e){ Log.e("Abrir directorio", e.toString());}
        return abre;
    }

    /**
     * Verifica se existe um directorio
     * @param dir_path
     * @return
     */
    public boolean dir_exists(String dir_path) {
        boolean ret = false;
        File dir = new File(this.context.getFilesDir(), dir_path);
        if(dir.exists() && dir.isDirectory()){ ret = true;}
        return ret;
    }

    /**
     * Verifica se existe o ficheiro
     * @param file_path
     * @return
     */
    public boolean file_exists(String file_path) {
        boolean ret = false;
        File file = new File(this.context.getFilesDir(), file_path);
        if(file.exists() && file.isFile()){ ret = true;}
        return ret;
    }


    /**
     * Elimina diretorio de modo recursivo
     * @param dir_path pelo caminho
     */
    public void elimina_recursivo(String dir_path){
        File dir = new File(this.context.getFilesDir(), dir_path);
        elimina_recursivo(dir);
    }

    /**
     * Elimina diretorio de modo recursivo
     * @param file objecto File
     */
    public void elimina_recursivo(File file){
        if(file.exists()){
            String nome = file.getName();
            //se directorio
            if(file.isDirectory()) {
                //testa se estamos no deposito das fotografias
                if(nome.equals(this.pastaDepositoFotografias)){
                    //vai fazer uma eliminacao selectiva
                    //elimina_fotografiasDeposito(file);
                    return;
                }

                File[] lista = file.listFiles();
                if(null!=lista) {
                    for (File caminho : lista) {
                        elimina_recursivo(caminho);
                    }
                }
            }
            try {
                //nao pode eliminar os ficheiros armazenam dados em cache
                // || nome.equals(_FileDadosNavegacao)
                if(nome.equals(_FileNameEndereco) || nome.equals(_FileNameUtilizador) || nome.equals("files")) {}
                else{
                    boolean apaga = file.delete();
                    Log.i("Elimina", (apaga ? "Eliminou: " : "Não Eliminou: ") + file.getPath());
                }
            }
            catch (Exception e){ Log.e("Elimina", e.toString());}
        }
    }
/*
    //elimina selectivamente as fotografias
    private void elimina_fotografiasDeposito(File file){
        ArrayList<ClasseFotografias> fotografias = classAssistenciaDB.get_fotografias(0, "", 0, false);
        //percorre as fotofrafias
        for(ClasseFotografias foto:fotografias){
            int origem  = foto.getOrigem();
            String nome = foto.getNome_temporario();
            if(2==origem){ }
            else{
                //elimina
                try {
                    File aponta = new File(file, nome);
                    boolean apaga = aponta.delete();
                    Log.i("Elimina", (apaga ? "Eliminou: " : "Não Eliminou: ") + aponta.getPath());
                }
                catch (Exception e){ Log.e("Elimina", e.toString());}
            }
        }

    }
*/
    /**
     * Verifica se o directorio esta vazio
     * @param caminho_dir pelo caminho
     * @return
     */
    public boolean dirVazio(String caminho_dir){
        File dir = new File(this.context.getFilesDir(), caminho_dir);
        return dirVazio(dir);
    }

    /**
     * Verifica se o directorio esta vazio
     * @param dir pelo File
     * @return
     */
    public boolean dirVazio(File dir){
        boolean vazio = true;
        if(dir.exists() && dir.isDirectory()){
            File[] lista = dir.listFiles();
            return 0==lista.length;
        }
        return vazio;
    }

    public String constroiCaminho(String directorio){
        return this.context.getDir(directorio, Context.MODE_PRIVATE).getAbsolutePath();
    }

    public String constroiCaminho(String directorio, String ficheiro){
        String caminho = constroiCaminho(directorio);
        return caminho+"/"+ficheiro;
    }

    /**
     *
     * @param nome
     * @param conteudo
     * @return
     */
    public boolean criaFicheiro(String nome, String conteudo) {
        byte[] bytes = conteudo.getBytes();
        return criaFicheiro(nome, bytes);
    }

    public boolean criaFicheiro(String nome, byte[] conteudo) {
        try{
            FileOutputStream fos = this.context.openFileOutput(nome, Context.MODE_PRIVATE);
            fos.write(conteudo);
            fos.close();
            return true;
        }
        catch (Exception e){ Log.i("Falha criar ficheiro", e.toString());}
        return false;
    }

    public ArrayList<String> get_listaFicheiros(String pasta){
        File onde = new File(this.context.getFilesDir(), pasta);
        return get_listaFicheiros(onde);
    }

    public ArrayList<String> get_listaFicheiros(File file){
        ArrayList<String> lista = new ArrayList<String>();
        if(file.exists()){
            //se directorio
            if(file.isDirectory()) {
                File[] files = file.listFiles();
                if(null != lista) {
                    for (File caminho : files) {
                        if(caminho.isFile()) {
                            lista.add(caminho.getName());
                        }
                    }
                }
            }
        }
        return lista;
    }

    public String ler_arquivo(String nome_ficheiro){
        File file = new File(this.context.getFilesDir(), nome_ficheiro);
        return ler_arquivo(file);
    }

    public String ler_arquivo(String pasta, String nome_ficheiro){
        File raiz = new File(this.context.getFilesDir(), pasta);
        File file = new File(raiz, nome_ficheiro);
        return ler_arquivo(file);
    }

    public String ler_arquivo(File file){
        String s = "";
        try {
            InputStreamReader InputRead = new InputStreamReader(new FileInputStream(file), "ISO-8859-1");

            char[] inputBuffer = new char[READ_BLOCK_SIZE];
            int charRead;
            Log.i("Tamanho", "" + file.length());

            while (0 < (charRead = InputRead.read(inputBuffer))) {
                // char to string conversion
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                s += readstring;
            }
            InputRead.close();
        }
        catch (FileNotFoundException fnfe){ Log.e("InternalStorage", fnfe.toString());
        } catch (IOException ioe){ Log.e("InternalStorage", ioe.toString());}
        //Toast.makeText(getBaseContext(), s, Toast.LENGTH_SHORT).show();
        return s;
    }

    /**
     * devolve a extensao do ficheiro
     */
    public String extensao(String nome_ficheiro){
        char ch;
        int len;
        if(nome_ficheiro==null ||
                (len = nome_ficheiro.length())==0 ||
                (ch = nome_ficheiro.charAt(len-1))=='/' || ch=='\\' || //in the case of a directory
                ch=='.' ) //in the case of . or ..
            return "";
        int dotInd = nome_ficheiro.lastIndexOf('.'),
                sepInd = Math.max(nome_ficheiro.lastIndexOf('/'), nome_ficheiro.lastIndexOf('\\'));
        if( dotInd<=sepInd )
            return "";
        else
            return nome_ficheiro.substring(dotInd+1);
    }
}
