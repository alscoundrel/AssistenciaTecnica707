package com.dlsra.assistenciatecnica707;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by AlScoundrel on 29/01/2016.
 */
public class ClasseFicheiroCache {
    Context context = null;
    String NomeFicheiro = "";

    /** construtor
     * @param contexto recebo o contexto
     * @param nome recebe o nome do ficheiro
     */
    ClasseFicheiroCache(Context contexto, String nome){
        this.context = contexto;
        this.NomeFicheiro = nome;
    }

    public boolean setTextoFicheiro(String string){
        FileOutputStream outputStream;
        boolean sucesso=false;

        try {
            outputStream = context.openFileOutput(this.NomeFicheiro, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
            sucesso = true;
        }
        catch (FileNotFoundException fe){ Log.e("Cache-escrever", fe.toString());}
        catch (Exception e) { Log.e("Cache-escrever", e.toString());}
        return sucesso;
    }

    public String getTextoFicheiro(){
        String temp="";
        try{
            FileInputStream fin = context.openFileInput(NomeFicheiro);
            if(null != fin) {
                int c;

                while ((c = fin.read()) != -1) {
                    temp = temp + Character.toString((char) c);
                }
            }
            //Log.i("Ficheiro " + NomeFicheiro, temp);
        }
        catch (FileNotFoundException fe){ Log.e("Cache-ler", fe.toString());}
        catch(Exception e){ Log.e("Cache-ler", e.toString());}
        return temp;
    }

    private String caminhoFicheiro(){
        return context.getFilesDir().getAbsolutePath() + "/" + NomeFicheiro;
    }

    public boolean seExisteFicheiro() {
        String caminho = caminhoFicheiro();
        File file = new File(caminho);
        return file.exists();
    }

    public boolean criaFicheiro(){
        if(seExisteFicheiro()){ return true;}
        String caminho = caminhoFicheiro();
        File file = new File(caminho);
        return file.mkdirs();
    }
}
