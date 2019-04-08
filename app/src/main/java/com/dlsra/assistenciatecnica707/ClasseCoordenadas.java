package com.dlsra.assistenciatecnica707;

/**
 * Created by Utilizador on 27/07/2017.
 */
public class ClasseCoordenadas {
    private int id;
    private int n_obra;
    private String cod_obra;
    private int ano_obra;
    private int id_pessoa;
    private String data_registo;
    private String gps;
    private int enviado;
    private boolean para_enviar = false;

    ClasseCoordenadas(){}

    public int getId(){ return this.id;}
    public int getN_obra(){ return this.n_obra;}
    public String getCod_obra(){ return this.cod_obra;}
    public int getAno_obra(){ return this.ano_obra;}
    public int getId_pessoa(){ return this.id_pessoa;}
    public String getData_registo(){ return this.data_registo;}
    public String getGps(){ return this.gps;}
    public int getEnviado(){ return this.enviado;}
    public boolean isPara_enviar() {return this.para_enviar;}
    public boolean isEnviado(){ return 1==this.enviado;}

    public void setId(int id){ this.id=id;}
    public void setN_obra(int n_obra){ this.n_obra=n_obra;}
    public void setCod_obra(String cod_obra){ this.cod_obra=cod_obra;}
    public void setAno_obra(int ano_obra){ this.ano_obra=ano_obra;}
    public void setId_pessoa(int id_pessoa){ this.id_pessoa=id_pessoa;}
    public void setData_registo(String data_registo){ this.data_registo=data_registo;}
    public void setGps(String gps) { this.gps = gps;}
    public void setEnviado(int enviado) { this.enviado = enviado;}
    public void setPara_enviar(boolean para_enviar) {this.para_enviar = para_enviar;}

    public void put(String campo, String valor){
        switch (campo){
            case "id": setId(Integer.parseInt(valor)); break;
            case "n_obra": setN_obra(Integer.parseInt(valor)); break;
            case "cod_obra": setCod_obra(valor); break;
            case "ano_obra": setAno_obra(Integer.parseInt(valor)); break;
            case "id_pessoa": setId_pessoa(Integer.parseInt(valor)); break;
            case "data_registo": setData_registo(valor); break;
            case "gps": setGps(valor); break;
            case "enviado": setEnviado(Integer.parseInt(valor)); break;
            case "para_enviar": setPara_enviar(1 == Integer.parseInt(valor)); break;
        }
    }

    public void put(String campo, int valor){
        switch (campo){
            case "id": setId(valor); break;
            case "n_obra": setN_obra(valor);break;
            case "ano_obra": setAno_obra(valor); break;
            case "id_pessoa": setId_pessoa(valor); break;
            case "enviado": setEnviado(valor); break;
            case "para_enviar": setPara_enviar(1 == valor);
        }
    }

    public String retreat(String campo){
        String valor = "";

        switch (campo){
            case "id": valor=""+this.id; break;
            case "n_obra": valor=""+this.n_obra; break;
            case "cod_obra": valor=this.cod_obra; break;
            case "ano_obra": valor=""+this.ano_obra; break;
            case "id_pessoa": valor=""+this.id_pessoa; break;
            case "data_registo": valor=this.data_registo; break;
            case "gps": valor=this.gps; break;
            case "enviado": valor=""+this.enviado; break;
            case "para_enviar": valor=this.isPara_enviar()?"1":"0";
        }
        return valor;
    }
}
