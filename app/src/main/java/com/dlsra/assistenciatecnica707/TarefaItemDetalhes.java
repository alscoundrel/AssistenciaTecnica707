package com.dlsra.assistenciatecnica707;

/**
 * Created by AlScoundrel on 03/02/2016.
 */
public class TarefaItemDetalhes {
    private int _id;
    private int id_utilizador;
    private int n_pessoa;
    private String data;
    private String hora;
    private int n_obra;
    private String cod_obra;
    private int ano_obra;
    private String localidade;
    private String nota;

    public int get_id(){ return _id;}
    public int getId_utilizador() {return id_utilizador;}
    public int getN_pessoa() {return n_pessoa;}
    public String getData(){ return data;}
    public String getHora(){ return hora;}
    public int getN_obra(){ return n_obra;}
    public String getCod_obra(){ return cod_obra;}
    public int getAno_obra(){ return ano_obra;}
    public String getLocalidade(){ return localidade;}
    public String getNota(){ return nota;}

    public void set_id(int _id) { this._id=_id;}
    public void setId_utilizador(int id_utilizador) {this.id_utilizador = id_utilizador;}
    public void setN_pessoa(int n_pessoa) {this.n_pessoa = n_pessoa;}
    public void setData(String data) { this.data=data;}
    public void setHora(String hora) { this.hora = hora;}
    public void setN_obra(int n_obra) { this.n_obra = n_obra;}
    public void setAno_obra(int ano_obra) { this.ano_obra = ano_obra;}
    public void setCod_obra(String cod_obra) { this.cod_obra = cod_obra;}
    public void setLocalidade(String localidade) { this.localidade = localidade;}
    public void setNota(String nota) { this.nota = nota;}
}
