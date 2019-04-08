package com.dlsra.assistenciatecnica707;


import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.dlsra.assistenciatecnica707.ClasseFotografias;
import com.dlsra.assistenciatecnica707.DBAssistencia;
import com.dlsra.assistenciatecnica707.ProcessoFragmentoFotografiasAdaptador;
import com.dlsra.assistenciatecnica707.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;

import static android.R.attr.type;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProcessoFragmentoDocumentos extends Fragment {
    //declaracao de classes
    private DBAssistencia classAssistenciaDB = null;
    private ClasseInternalStorage classInternalStorage = null;
    //variaveis
    private int mIdUtilizador;
    private int mNUtilizador;
    private int mIdMenu;
    private int mNObra;
    private String mCodObra;
    private int mAnoObra;
    private int mIdTarefa;
    private String pastaProcesso;

    //elementos visuais
    private View janela = null;
    private ListView lvDocumentos  = null;
    private LinearLayout llSemDocs = null;
    private LinearLayout llComDocs = null;
    //elementos sistema
    private Context mContexto = null;

    public ProcessoFragmentoDocumentos() {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        janela = inflater.inflate(R.layout.processo_fragmento_documentos, container, false);
        return janela;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle){
        super.onViewCreated(view, bundle);
        this.pastaProcesso = mAnoObra+mCodObra+mNObra;

        carrega_classes();
        carrega_elementos();
        preenche_dadosJanela();
    }

    /**
     *
     */
    private void carrega_elementos(){
        lvDocumentos = (ListView) janela.findViewById(R.id.lvDocumentos);
        llSemDocs    = (LinearLayout) janela.findViewById(R.id.llDocumentosSemDocs);
        llComDocs    = (LinearLayout) janela.findViewById(R.id.llDocumentosComDocs);
    }

    private void carrega_classes(){
        //inicializa a classe tarefa
        classAssistenciaDB = new DBAssistencia(mContexto, "TAREFA", "tarefa");
        classInternalStorage = new ClasseInternalStorage(mContexto);
    }

    // ---------------------
    private void preenche_dadosJanela(){
        classAssistenciaDB.setNomeTabela("ficheiro");
        final ArrayList<ClasseDocumentos> documentosDB = classAssistenciaDB.get_documentos(mNObra, mCodObra, mAnoObra);
        ArrayList<String> documentosDIR = classInternalStorage.get_listaFicheiros(this.pastaProcesso);

        if(0==documentosDB.size()){
            llSemDocs.setVisibility(View.VISIBLE);
            llComDocs.setVisibility(View.GONE);
        }
        else {
            llSemDocs.setVisibility(View.GONE);
            llComDocs.setVisibility(View.VISIBLE);

            /* carrega o adaptador */
            ProcessoFragmentoDocumentosAdaptador adaptador = new ProcessoFragmentoDocumentosAdaptador(mContexto, documentosDB, documentosDIR, mIdUtilizador);
            lvDocumentos.setAdapter(adaptador);

            /*lanca evento*/
            lvDocumentos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ClasseDocumentos documento = documentosDB.get(position);
                    String nome_ficheiro       = documento.getNome_ficheiro();
                    abreJanelaComDocumento(position, nome_ficheiro);
                }
            });
        }
    }

    /**
     *
     */
    private void abreJanelaComDocumento(int posicao, String nome_ficheiro){
        String extensao = extensao_file(nome_ficheiro);

        Bundle bundle = new Bundle();
        bundle.putInt("_id", mIdTarefa);
        bundle.putInt("n_obra", mNObra);
        bundle.putString("cod_obra", mCodObra);
        bundle.putInt("ano_obra", mAnoObra);
        bundle.putInt("posicao_file", posicao);
        bundle.putString("nome_ficheiro", nome_ficheiro);

        //aponta para o ficheiro original

        String pasta = mAnoObra + mCodObra + mNObra;
        File raiz = new File(getContext().getFilesDir(), pasta);
        File file = new File(raiz, nome_ficheiro);
        file.setReadable(true);

        Uri uri = null;
        String type = "";

        //copiar o ficheiro para o sdcard
        AssetManager assetManager = getActivity().getAssets();
        InputStream in = null;
        OutputStream out = null;
        File caminho = null;
        File temporario = null;
        String nome_temporario = "temporario_at."+extensao;
        try{
            //caminho para o sdcard
            caminho = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            caminho.mkdir();
            temporario = new File(caminho, nome_temporario);

            in = new FileInputStream(file);//assetManager.open(file.getAbsolutePath());
            out = new FileOutputStream(temporario);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;

            //Uri uri = FileProvider.getUriForFile(getContext(), "com.dlsra.assistenciatecnica707", file);
        }
        catch (IOException ioe){ Log.e("SDCARD", ioe.toString());}

        try{
            temporario = new File(caminho, nome_temporario);
            uri = Uri.fromFile(temporario);//file sem permissoes para app's externas
            type = getMimeType(uri.toString());
        }
        catch (Exception e){ Log.e("SDCARD", e.toString());}

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, type);

        String textoErro = "";
        if(nome_ficheiro.endsWith(".txt")){
            //é um ficheiro txt
            intent = new Intent(getContext(), ProcessoFragmentoDocumentosVerTXT.class);
            intent.putExtras(bundle);
            textoErro = "Mostrar ficheiro txt!";
        }
        else if(nome_ficheiro.endsWith(".eml")){
            //é um ficheiro txt
            textoErro = "Não encontrada a aplicação para ver eml";
        }
        //nome_ficheiro.endsWith(".eml")
        else if(nome_ficheiro.endsWith(".html")){
            //é um ficheiro eml/html
            intent = new Intent(getContext(), ProcessoFragmentoDocumentosVerHTML.class);
            intent.putExtras(bundle);
            textoErro = "Mostrar ficheiro html!";
        }
        else if(nome_ficheiro.endsWith(".pdf")){
            //é um ficheiro PDF
            //intent.setType("application/pdf");
            textoErro = "Não encontrada a aplicação para ver PDF";
            /*
            intent = new Intent(getContext(), ProcessoDocumentoPdfFullScreenActivity.class);
            intent.putExtras(bundle);
            textoErro = "Mostrar ficheiro pdf!";
            */
        }
        else if(nome_ficheiro.endsWith(".doc") || nome_ficheiro.endsWith(".docx")){
            //intent.setType("application/msword");
            textoErro = "Não encontrada a aplicação para ver Word";
        }
        else if(nome_ficheiro.endsWith(".xls") || nome_ficheiro.endsWith(".xlsx")){
            //intent.setType("application/vnd.ms-excel");
            textoErro = "Não encontrada a aplicação para ver Excel";
        }
        else if(nome_ficheiro.endsWith(".rtf")){
            //intent.setType("application/rft");
            textoErro = "Não encontrada a aplicação para ver Rich Text Format";
        }

        try { startActivity(intent);}
        catch (Exception e) {
            Toast.makeText(getContext(), textoErro+"\n"+e.toString(), Toast.LENGTH_LONG).show();
            Log.e("Ficheiro", textoErro+"\n"+e.toString());
        }
    }

    private String extensao_file(String file){
        String extensao = "";
        int posicao = file.lastIndexOf(".");
        extensao = file.substring(posicao+1);
        return extensao;
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);

        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }

        return type;
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[2048];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
}
