package com.dlsra.assistenciatecnica707;

/**
 * Created by Utilizador on 29/01/2016.
 */
public class ClasseCodigoPessoal {
    private int numero;
    private String codigo;
    private int nControlo;
    private int[] pesos = {7,9,5,2,3};

    /** construtor da classe
     *
     * @param numero recebe o valor do colaborador
     * gera o codigo pessoal
     */
    ClasseCodigoPessoal(int numero){
        this.numero=numero;

        calculaCodigo();
    }

    //retorna valores
    public int getNumero(){
        return this.numero;
    }

    public String getCodigo(){
        return this.codigo;
    }

    public int getnControlo(){
        return this.nControlo;
    }

    //zona de calculos
    private void calculaCodigo(){
        int completo;
        int ultimo;

        completo = (10000+this.numero)*10;
        this.nControlo = fazNControlo(completo);
        ultimo = completo + this.nControlo;
        this.codigo = ""+ultimo;
    }

    private int fazNControlo(int controlar){
        String sControlar=""+controlar;
        int n1 = Integer.parseInt(sControlar.substring(0,1));
        int n2 = Integer.parseInt(sControlar.substring(1,2));
        int n3 = Integer.parseInt(sControlar.substring(2,3));
        int n4 = Integer.parseInt(sControlar.substring(3,4));
        int n5 = Integer.parseInt(sControlar.substring(4,5));

        int produto=n1*this.pesos[0]+n2*this.pesos[1]+n3*this.pesos[2]+n4*this.pesos[3]+n5*this.pesos[4];
        int modulo = produto % 11;
        int resultado = 11-modulo;
        if(9 < resultado){ resultado -=10;}
        return resultado;
    }
}
