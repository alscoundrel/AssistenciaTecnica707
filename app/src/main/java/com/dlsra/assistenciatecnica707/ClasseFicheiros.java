package com.dlsra.assistenciatecnica707;

import java.util.ArrayList;

/**
 * Created by Utilizador on 19/02/2016.
 */
public class ClasseFicheiros {
    private ArrayList<ClasseFotografias> fotografias;
    private ArrayList<ClasseDocumentos> documentos;

    ClasseFicheiros(){}

    public void put(ArrayList<ClasseFotografias> fotografias, ArrayList<ClasseDocumentos> documentos){
        this.fotografias = fotografias;
        this.documentos =documentos;
    }

    public ArrayList<ClasseFotografias> getFotografias(){ return this.fotografias;}
    public ArrayList<ClasseDocumentos> getDocumentos(){ return this.documentos;}

    public void setFotografias(ArrayList<ClasseFotografias> fotografias){ this.fotografias=fotografias;}
    public void setDocumentos(ArrayList<ClasseDocumentos> documentos){ this.documentos=documentos;}

    public int size(){ return this.documentos.size()+this.fotografias.size();}
}
