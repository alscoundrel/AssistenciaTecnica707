package com.dlsra.assistenciatecnica707;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Utilizador on 23/03/2016.
 */
public class ClasseDadosNavegacao {
    private final String mNoPai = "navegacao";
    private final String mNoFilhos = "dado";
    private final String mNoIdentificacao = "identificacao";
    private final String mNoValor = "valor";
    private Context mContexto = null;
    private Document documento = null;
    private Element elementoPai = null;
    private String _FileDadosNavegacao = "";
    private boolean _CarregouXML = false;
    //classes
    ClasseInternalStorage cInternalStorage = null;

    ClasseDadosNavegacao(Context contexto){
        this.mContexto = contexto;

        //ficheiro guarda dados navegacao
        _FileDadosNavegacao = contexto.getString(R.string.ficheiro_dados_navegacao);
        //inicializacao classes
        cInternalStorage = new ClasseInternalStorage(contexto);
    }

    public void carrega_xml() {
        //testa se existe ficheiro no cache...
        File fileDados = new File(this.mContexto.getFilesDir(), this._FileDadosNavegacao);
        if(!fileDados.exists()){
            Log.i("XML", "ficheiro não existe!");
            cria_xml();
            fileDados = new File(this.mContexto.getFilesDir(), this._FileDadosNavegacao);
        }

        try {
            /*
            InputStream is = new FileInputStream(fileDados);
            String temp = "";
            int c=-1;
            while ((c = is.read()) != -1) {
                temp = temp + Character.toString((char) c);
            }
            */
            String conteudo = cInternalStorage.ler_arquivo(this._FileDadosNavegacao);
            InputStream is = new ByteArrayInputStream(conteudo.getBytes("UTF-8"));

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            this.documento = dBuilder.parse(is);

            this.elementoPai = documento.getDocumentElement();
            this.elementoPai.normalize();
            _CarregouXML = true;
        }
        catch (ParserConfigurationException pce){ Log.e("XML", pce.getMessage());}
        catch (SAXException se){ Log.e("XML", se.getMessage());}
        catch (IOException ioe) { Log.e("XML", ioe.getMessage());}
    }

    public String get_valor(String identificacao){
        carrega_xml();
        String valor = "";
        if(null != documento) {
            NodeList nList = documento.getElementsByTagName(this.mNoFilhos);
            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element elemento = (Element) node;

                    NodeList filhos = elemento.getChildNodes();
                    Node nIdent = filhos.item(0);
                    Node nValor = filhos.item(1);
                    String sIdent = getElementoValor(nIdent);
                    String sValor = getElementoValor(nValor);

                    if(null != sIdent && sIdent.equals(identificacao)){ valor = sValor;}
                }
            }
        }
        return valor;
    }

    private String getElementoValor( Node elem ) {
        Node kid;
        if( elem != null){
            if (elem.hasChildNodes()){
                for( kid = elem.getFirstChild(); kid != null; kid = kid.getNextSibling() ){
                    if( kid.getNodeType() == Node.TEXT_NODE  ){
                        return kid.getNodeValue();
                    }
                }
            }
        }
        return "";
    }

    public void atribui_elemento(String identificacao, String valor){
        this.carrega_xml();

        boolean v = false;

        if(null!=this.documento) {
            //nos filhos
            NodeList lista = this.documento.getElementsByTagName(this.mNoFilhos);
            for (int i = 0; i < lista.getLength(); i++) {
                Node dado = lista.item(i);
                if (dado.hasChildNodes()) {
                    //elementos NOS dos filhos
                    NodeList elementos = dado.getChildNodes();
                    //procura os NOS identificacao
                    Node neto1 = elementos.item(0);
                    Node neto2 = elementos.item(1);
                    if (null != neto1 && null != neto2) {
                        String v1 = getElementoValor(neto1);
                        if (v1.equals(identificacao)) {
                            v = true;
                            break;
                        }
                    }
                }
            }
        }

        if(v){ actualiza_elemento(identificacao, valor);}
        else{ insere_elemento(identificacao, valor);}
    }

    private void insere_elemento(String identificacao, String valor){
        try {
            //devolve no raiz do documento
            Node raiz = this.documento.getFirstChild();
            //cria mais um elemento filho
            Element filho = this.documento.createElement(this.mNoFilhos);
            //atribui o no à raiz do documento
            raiz.appendChild(filho);
            
            //cria os elementos identificativos
            Element netoIdent = this.documento.createElement(this.mNoIdentificacao);
            Element netoValor = this.documento.createElement(this.mNoValor);
            //atribui valores
            netoIdent.appendChild(this.documento.createTextNode(identificacao));
            netoValor.appendChild(this.documento.createTextNode(valor));
            //atribui o no ao dado
            filho.appendChild(netoIdent);
            filho.appendChild(netoValor);
        }
        catch (Exception e){ Log.e("XML", e.toString());}
        salva_xml(documento);
    }

    private void actualiza_elemento(String identificacao, String valor){
        try {
            //devolve no raiz do documento
            Node raiz = this.documento.getFirstChild();
            // percorre os nos da raiz
            NodeList lista = raiz.getChildNodes();
            for (int i = 0; i < lista.getLength(); i++) {
                Node dado = lista.item(i);
                if(dado.hasChildNodes()) {
                    //elementos NOS dos filhos
                    NodeList elementos = dado.getChildNodes();
                    //procura os NOS identificacao
                    Node neto1 = elementos.item(0);
                    Node neto2 = elementos.item(1);
                    if(null != neto1 && null != neto2){
                        String v1 = getElementoValor(neto1);
                        if(v1.equals(identificacao)){
                            neto2.setTextContent(valor);
                            break;
                        }
                    }
                }
            }
        }
        catch (Exception e){ Log.e("XML", e.toString());}
        salva_xml(this.documento);
    }

    private void elimina_elemento(String identificacao){
        try {
            //devolve no raiz do documento
            Node raiz = this.documento.getFirstChild();
            // percorre os nos da raiz
            NodeList lista = raiz.getChildNodes();
            for (int i = 0; i < lista.getLength(); i++) {
                Node dado = lista.item(i);
                NodeList elementos = dado.getChildNodes();

                if (elementos.item(0).getNodeValue().equals(identificacao)) {
                    raiz.removeChild(dado);
                }
            }
        }
        catch (Exception e){ Log.e("XML", e.toString());}
        salva_xml(documento);
    }


    private void cria_xml(){
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element pai = doc.createElement(this.mNoPai);
            doc.appendChild(pai);

            Element filho = doc.createElement(this.mNoFilhos);
            pai.appendChild(filho);

            Element netoIdent = doc.createElement(this.mNoIdentificacao);
            Element netoValor = doc.createElement(this.mNoValor);
            //atribui valores
            netoIdent.appendChild(doc.createTextNode("identificacao"));
            netoValor.appendChild(doc.createTextNode("valor"));
            //atribui o no ao dado
            filho.appendChild(netoIdent);
            filho.appendChild(netoValor);

            try {
                FileOutputStream fos = this.mContexto.openFileOutput(this._FileDadosNavegacao, Context.MODE_PRIVATE);
                fos.write("".getBytes());
                fos.close();
            }
            catch (IOException e) { Log.e("Exception", "File write failed: " + e.toString());}
            salva_xml(doc);
        }
        catch (ParserConfigurationException pce) { Log.e("XML", pce.toString());}
    }

    private void salva_xml(Document doc){
        String conteudo = "";
        conteudo = cInternalStorage.ler_arquivo(this._FileDadosNavegacao);

        if(null!=doc) {
            try {
                File ficheiro = new File(this.mContexto.getFilesDir(), this._FileDadosNavegacao);
                //boolean existe1 = ficheiro.exists() && ficheiro.isFile();
                // escreve conteudo no ficheiro xml
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                Result output = new StreamResult(ficheiro);
                Source input = new DOMSource(doc);

                transformer.transform(input, output);

                conteudo = cInternalStorage.ler_arquivo(this._FileDadosNavegacao);
                //Toast.makeText(this.mContexto, conteudo, Toast.LENGTH_LONG).show();
                Log.i("XML", "Ficheiro Salvo!");
            } catch (TransformerException tfe) {
                Log.e("XML", tfe.toString());
            } catch (Exception e) {
                Log.e("XML", e.toString());
            }
        }
    }
}
