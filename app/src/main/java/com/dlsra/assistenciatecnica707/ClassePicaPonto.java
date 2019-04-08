package com.dlsra.assistenciatecnica707;

/**
 * Created by Utilizador on 22/04/2016.
 */
public class ClassePicaPonto {
    private int id;
    private int numero;
    private int codigo;
    private String origem;
    private String data;
    private String hora;
    private String maquina;
    private String gps;
    private int enviado;
    private boolean para_enviar = false;

    ClassePicaPonto(){}

    public int getId(){ return this.id;}
    public int getNumero(){ return this.numero;}
    public int getCodigo(){ return this.codigo;}
    public String getOrigem(){ return this.origem;}
    public String getData(){ return this.data;}
    public String getHora(){ return this.hora;}
    public String getMaquina(){ return this.maquina;}
    public String getGps(){ return this.gps;}
    public int getEnviado(){ return this.enviado;}
    public boolean isPara_enviar() {return para_enviar;}
    public boolean isEnviado(){ return 1==this.enviado;}

    public void setId(int id){ this.id=id;}
    public void setNumero(int numero){ this.numero=numero;}
    public void setCodigo(int codigo){ this.codigo=codigo;}
    public void setOrigem(String origem){ this.origem=origem;}
    public void setData(String data){ this.data=data;}
    public void setHora(String hora){ this.hora=hora;}
    public void setMaquina(String maquina) { this.maquina = maquina;}
    public void setGps(String gps){ this.gps = gps;}
    public void setEnviado(int enviado) { this.enviado = enviado;}
    public void setPara_enviar(boolean para_enviar) {this.para_enviar = para_enviar;}

    public void put(String campo, String valor){
        switch (campo){
            case "id": setId(Integer.parseInt(valor)); break;
            case "numero": setNumero(Integer.parseInt(valor)); break;
            case "codigo": setCodigo(Integer.parseInt(valor)); break;
            case "origem": setOrigem(valor); break;
            case "data": setData(valor); break;
            case "hora": setHora(valor); break;
            case "maquina": setMaquina(valor); break;
            case "gps": setGps(valor);break;
            case "enviado": setEnviado(Integer.parseInt(valor)); break;
            case "para_enviar": setPara_enviar(1 == Integer.parseInt(valor)); break;
        }
    }

    public void put(String campo, int valor){
        switch (campo){
            case "id": setId(valor); break;
            case "numero": setNumero(valor);break;
            case "codigo": setCodigo(valor); break;
            case "enviado": setEnviado(valor); break;
            case "para_enviar": setPara_enviar(1 == valor);
        }
    }

    public String retreat(String campo){
        String valor = "";

        switch (campo){
            case "id": valor=""+this.id; break;
            case "numero": valor=""+this.numero; break;
            case "codigo": valor=""+this.codigo; break;
            case "origem": valor=""+this.origem; break;
            case "data": valor=this.data; break;
            case "hora": valor=this.hora; break;
            case "maquina": valor=this.maquina; break;
            case "gps": valor=this.gps;break;
            case "enviado": valor=""+this.enviado; break;
            case "para_enviar": valor=this.isPara_enviar()?"1":"0";
        }
        return valor;
    }
}
