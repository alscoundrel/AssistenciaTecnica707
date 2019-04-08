package com.dlsra.assistenciatecnica707;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class InicioEndereco extends AppCompatActivity {
    //variaveis
    String mFicheiro = null;
    //classes
    private ClasseFicheiroCache ficheiroCache = null;
    //elementos
    private EditText mEnderecoElectronico = null;
    private Button mRegistarBT = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio_endereco_activity);

        //carrega valores predefinidos
        aponta_predefinidos();
        //carrega classes
        aponta_classes();
        //carrega elementos
        aponta_elementos();
        //lanca eventos
        implementa_inventos();

        mEnderecoElectronico.setText(carregaEnderecoGuardado());
    }

    private void aponta_predefinidos(){
        mFicheiro = getString(R.string.ficheiro_endereco_electronico);
    }

    private void aponta_classes(){
        ficheiroCache = new ClasseFicheiroCache(this, mFicheiro);
    }

    private void aponta_elementos(){
        mEnderecoElectronico = (EditText) findViewById(R.id.etEnderecoElectronico);
        mRegistarBT = (Button) findViewById(R.id.btRegistarEndereco);
    }

    private void implementa_inventos(){
        mRegistarBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testaEregistaEndereco();
            }
        });
    }
    /**
     * carrega numa String o endereco da empresa, se nao tem registo carrega o por defeito
     * @return endereco da empresa
     */
    private String carregaEnderecoGuardado(){
        String endereco = getString(R.string.endereco_electronico);

        if(ficheiroCache.seExisteFicheiro()){
            endereco = ficheiroCache.getTextoFicheiro();
        }

        return endereco;
    }

    /**
     * testa o endereco e regista
     */
    private void testaEregistaEndereco(){
        // elimina errors.
        mEnderecoElectronico.setError(null);

        // carrega os valores a testar no login
        String endereco = mEnderecoElectronico.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // testa contacto
        boolean eContactoValido = isEnderecoValid(endereco);
        if (!eContactoValido) {
            mEnderecoElectronico.setError("Endereco inválido!");
            focusView = mEnderecoElectronico;
            cancel = true;
        }

        if (cancel) {
            // Se devolve erro;
            // form field com o erro.
            focusView.requestFocus();
        } else {
            //regista endereco
            ficheiroCache.setTextoFicheiro(endereco);

            String texto = "Endereço guardado com sucesso!";
            int duracao = Toast.LENGTH_LONG;
            mostraMensagemTela(texto, duracao);
        }
    }

    //valida endereco electronico
    private boolean isEnderecoValid(String endereco){
        boolean valido = false;
        int tamanho = endereco.length();
        if(12 < tamanho) {

            String http = endereco.substring(0, 7);
            String https = endereco.substring(0, 8);
            String ultimo = endereco.substring(tamanho-1);
            int posBarras = endereco.indexOf("//", 8);

            if(0==http.compareTo("http://")){ valido=true;}
            if(0==https.compareTo("https://")){ valido=true;}

            if(ultimo.equals("/")){ valido=false;}
            if(-1 < posBarras){ valido=false;}
            Log.i("Endereco ", http+" : "+https);
        }
        return valido;
    }
    /*************************
        mostra mensagem na tela
    **************************/
    private void mostraMensagemTela(String texto, int duracao){
        Context contexto = getApplicationContext();
        Toast toast = Toast.makeText(contexto, texto, duracao);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }
}
