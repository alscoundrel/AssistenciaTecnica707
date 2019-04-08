package com.dlsra.assistenciatecnica707;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import static android.view.WindowManager.*;

public class ProcessoFragmentoDocumentosVerTXT extends Activity {
    //declaracao de classes
    private DBAssistencia classAssistenciaDB = null;

    //valores passados
    private int _n_obra;
    private String _cod_obra;
    private int _ano_obra;
    private int _posicao_file;
    private String _nome_ficheiro;

    private ProcessoFragmentoDocumentosVerTXTAdaptador adapter;
    private ViewPager viewPager;

    static final int READ_BLOCK_SIZE = 16384;//8192, 4096

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //pedir para retirar o titulo da aplicação
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.processo_fragmento_documentos_ver_txt);

        //pedir para retirar o titulo da aplicação

        //carrega valores passados
        Intent intent = getIntent();
        try {
            Bundle bundle = intent.getExtras();
            _n_obra = bundle.getInt("n_obra");
            _cod_obra = bundle.getString("cod_obra");
            _ano_obra = bundle.getInt("ano_obra");
            _posicao_file = bundle.getInt("posicao_file");
            _nome_ficheiro = bundle.getString("nome_ficheiro");
        } catch (Exception e){ Log.e("Valores Passados", e.toString());}

        try {
            String conteudo = ReadFicheiro();

            if(null != conteudo) {
                adapter = new ProcessoFragmentoDocumentosVerTXTAdaptador(ProcessoFragmentoDocumentosVerTXT.this, conteudo);

                viewPager = (ViewPager) findViewById(R.id.vpVerTXT);
                viewPager.setAdapter(adapter);

                // displaying selected image first
                viewPager.setCurrentItem(_posicao_file);
            }
            else{ Log.e("Lançar ViewPager", "Erro ao abrir ficheiro "+_nome_ficheiro);}
        }
        catch (Exception e){ Log.e("Lançar ViewPager", e.toString());}
    }

    // ler texto do ficheiro txt
    public String ReadFicheiro() {
        String pasta = _ano_obra + _cod_obra + _n_obra;
        //reading text from file
        try {
            File raiz = new File(getApplicationContext().getFilesDir(), pasta);
            File file = new File(raiz, _nome_ficheiro);
            boolean v1 = raiz.isDirectory();
            boolean v2 = file.isFile();

            InputStreamReader InputRead = new InputStreamReader(new FileInputStream(file),"ISO-8859-1");

            char[] inputBuffer= new char[READ_BLOCK_SIZE];
            String s="";
            int charRead;
            Log.i("Tamanho", ""+file.length());

            while (0< (charRead=InputRead.read(inputBuffer))) {
                // char to string conversion
                String readstring=String.copyValueOf(inputBuffer,0,charRead);
                s +=readstring;
            }
            InputRead.close();
            //Toast.makeText(getBaseContext(), s, Toast.LENGTH_SHORT).show();

            return s;
            /*
            byte[] tmp2 = Base64.decode(s, Base64.DEFAULT);
            String s2 = new String(tmp2, "UTF-8");
            return s2;
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
