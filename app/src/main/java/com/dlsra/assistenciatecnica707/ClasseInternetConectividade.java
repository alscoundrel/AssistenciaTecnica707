package com.dlsra.assistenciatecnica707;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Utilizador on 29/03/2017.
 */

public class ClasseInternetConectividade {
    //variaveis
    private Activity minhaActividade = null;
    private boolean ligado = false;

    //lanca a classe
    public ClasseInternetConectividade(Activity actividade){
        minhaActividade = actividade;
        refaz();
    }

    public boolean isLigado(){
        return ligado;
    }

    public void refaz(){
        ligado = temLigacaoInternet();
    }

    private boolean temLigacaoInternet(){
        boolean conectado=false;
        boolean avaliado =false;
        ConnectivityManager coneccaoManager = (ConnectivityManager) minhaActividade.getSystemService(Context.CONNECTIVITY_SERVICE);
        //Crio a variável informacao que recebe as informações da Rede
        NetworkInfo informacao = coneccaoManager.getActiveNetworkInfo();
        if(null!=informacao) {
            conectado = informacao.isConnectedOrConnecting();
            avaliado  = informacao.isAvailable();
        }
        //Se o objeto for nulo ou nao tem conectividade retorna false
        return conectado && avaliado;
    }
}
