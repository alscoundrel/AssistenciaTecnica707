package com.dlsra.assistenciatecnica707;

import android.graphics.Bitmap;

/**
 * Created by Utilizador on 19/02/2016.
 */
public class ClasseDocumentos {
    private int id;
    private int n_obra;
    private String cod_obra;
    private int ano_obra;
    private String nome_ficheiro;
    private String tipologia;
    private String descricao;
    private String por;
    private int origem;
    private String data_registo;
    private Bitmap data;

    ClasseDocumentos(){}

    public int getId(){ return this.id;}
    public int getN_obra(){ return this.n_obra;}
    public String getCod_obra(){ return this.cod_obra;}
    public int getAno_obra(){ return this.ano_obra;}
    public String getNome_ficheiro(){ return this.nome_ficheiro;}
    public String getTipologia(){ return this.tipologia;}
    public String getDescricao(){ return this.descricao;}
    public String getPor(){ return this.por;}
    public int getOrigem(){ return this.origem;}
    public String getData_registo(){ return this.data_registo;}
    public Bitmap getData(){ return this.data;}

    public void setId(int id){ this.id=id;}
    public void setN_obra(int n_obra){ this.n_obra=n_obra;}
    public void setCod_obra(String cod_obra){ this.cod_obra=cod_obra;}
    public void setAno_obra(int ano_obra){ this.ano_obra=ano_obra;}
    public void setNome_ficheiro(String nome_ficheiro){ this.nome_ficheiro=nome_ficheiro;}
    public void setTipologia(String tipologia){ this.tipologia=tipologia;}
    public void setDescricao(String descricao){ this.descricao=descricao;}
    public void setPor(String por){ this.por=por;}
    public void setOrigem(int origem){ this.origem=origem;}
    public void setData_registo(String data_registo){ this.data_registo=data_registo;}
    public void setData(Bitmap data){ this.data=data;}

    public void put(String campo, String valor){
        switch (campo){
            case "id": setId(Integer.parseInt(valor)); break;
            case "n_obra": setN_obra(Integer.parseInt(valor)); break;
            case "cod_obra": setCod_obra(valor); break;
            case "ano_obra": setAno_obra(Integer.parseInt(valor)); break;
            case "nome_ficheiro": setNome_ficheiro(valor); break;
            case "tipologia": setTipologia(valor); break;
            case "descricao": setDescricao(valor); break;
            case "por": setPor(valor); break;
            case "data_registo": setData_registo(valor); break;
            case "origem": setOrigem(Integer.parseInt(valor)); break;
        }
    }

    public void put(String campo, int valor){
        switch (campo){
            case "id": setId(valor); break;
            case "n_obra": setN_obra(valor);break;
            case "ano_obra": setAno_obra(valor); break;
            case "origem": setOrigem(valor); break;
        }
    }

    public void put(String campo, Bitmap valor){
        switch (campo){
            case "data": setData(valor); break;
        }
    }

    public String retreat(String campo){
        String valor = "";

        switch (campo){
            case "id": valor=""+this.id; break;
            case "n_obra": valor=""+this.n_obra; break;
            case "cod_obra": valor=this.cod_obra; break;
            case "ano_obra": valor=""+this.ano_obra; break;
            case "nome_ficheiro": valor=this.nome_ficheiro; break;
            case "tipologia": valor=this.tipologia; break;
            case "descricao": valor=this.descricao; break;
            case "por": valor=this.por; break;
            case "data_registo": valor=this.data_registo; break;
            case "origem": valor=""+this.origem; break;
        }
        return valor;
    }
}
