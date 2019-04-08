package com.dlsra.assistenciatecnica707;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by AlScoundrel on 29/03/2017.
 */
public class DBAssistencia {
    private Context contexto;
    private DBHelper dbHelper;
    private static final String DATABASE_NAME = "assistencia707.db";
    private static final int DATABASE_VERSION = 41;
    private String nomeTabela = "";
    private ArrayList<HashMap<String, String>> listaDados;

    DBAssistencia(Context context, String categoria, String tabela){
        contexto = context;
        String[] scriptSQLCreate = new String[] {
                "create table utilizador    ( id integer primary key autoincrement, codigo integer not null default 0, contacto integer not null default 0, confirmado TINYINT not null default 0, ultimo_acesso text not null);",
                "create table tarefa        ( id integer primary key, id_utilizador integer not null default 0, n_pessoa integer not null default 0, data text not null, hora text not null, n_obra integer not null default 0, cod_obra text not null, ano_obra integer not null default 0,  localidade text not null, nota text not null, confirmado TINYINT not null default 0);",
                "create table processo      ( id integer primary key, id_utilizador integer not null default 0, dia_tarefa text not null, n_obra integer not null default 0, cod_obra text not null, ano_obra integer not null default 0, categoria text, estado text, descricao text, mediador text, referencia text, franquia real, franquia_pago real, danos_esteticos text, tipo_base integer not null default 0)",
                "create table seguro        ( id integer primary key, id_utilizador integer not null default 0, dia_tarefa text not null, n_obra integer not null default 0, cod_obra text not null, ano_obra integer not null default 0, companhia text, referencias text, apolice text, n_sinistro txt, produto, contacto)",
                "create table pessoa        ( id integer primary key, id_utilizador integer not null default 0, dia_tarefa text not null, n_obra integer not null default 0, cod_obra text not null, ano_obra integer not null default 0, titulo text, id_pessoa integer, nome text, morada text, contactos text, outros_contactos text, contribuinte integer, gps text, companhia text, enviado integer not null default 0)",
                "create table morada_obra   ( id integer primary key, id_utilizador integer not null default 0, dia_tarefa text not null, n_obra integer not null default 0, cod_obra text not null, ano_obra integer not null default 0, morada text)",
                "create table fotografia    ( id integer primary key, id_utilizador integer not null default 0, dia_tarefa text not null, n_obra integer not null default 0, cod_obra text not null, ano_obra integer not null default 0, nome_ficheiro text, nome_temporario text, pasta text, tipologia text, descricao text, por text, origem Integer not null default 0, data_registo text, gps text, enviado integer not null default 0, para_enviar Integer not null default 1, data blob)",
                "create table ficheiro      ( id integer primary key, id_utilizador integer not null default 0, dia_tarefa text not null, n_obra integer not null default 0, cod_obra text not null, ano_obra integer not null default 0, nome_ficheiro text, tipologia text, descricao text, por text, origem Integer not null default 0, data_registo text, data blob)",
                "create table evolucao      ( id integer primary key, id_utilizador integer not null default 0, dia_tarefa text not null, n_obra integer not null default 0, cod_obra text not null, ano_obra integer not null default 0, texto text, enviado integer not null default 0, para_enviar Integer not null default 1)",
                "create table trabalhos     ( id integer primary key, id_utilizador integer not null default 0, dia_tarefa text not null, n_obra integer not null default 0, cod_obra text not null, ano_obra integer not null default 0, descricao text)",
                "create table coordenadas   ( id integer primary key, id_utilizador integer not null default 0, dia_tarefa text not null, n_obra integer not null default 0, cod_obra text not null, ano_obra integer not null default 0, id_pessoa integer not null default 0, gps text, enviado integer not null default 0, data_registo text, para_enviar Integer not null default 1)",
                "create table pica_ponto    ( id integer primary key, id_utilizador integer not null default 0, dia_tarefa text not null, numero integer not null default 0, codigo integer not null default 0, origem text, data text, hora text, maquina text, enviado integer not null default 0, para_enviar Integer not null default 1, gps text)",
                "create table evolucao_obra ( id integer primary key, id_utilizador integer not null default 0, dia_tarefa text not null, n_obra integer not null default 0, cod_obra text not null, ano_obra integer not null default 0, data_registo text, por integer not null default 0, texto text, enviado integer not null default 0, para_enviar Integer not null default 1)",
                "create table assistencia   ( id integer primary key, id_utilizador integer not null default 0, dia_tarefa text not null, n_obra integer not null default 0, cod_obra text not null, ano_obra integer not null default 0, artigo text, codigo text, contacto)",
                "create table levantamento_tecnico (" +
                        " id integer primary key, id_utilizador integer not null default 0, dia_tarefa text not null, n_levantamento integer not null default 0," +
                        " n_obra integer not null default 0, cod_obra text not null, ano_obra integer not null default 0," +
                        " data text, hora text, data_registo text, por text, n_pessoa integer not null default 0," +
                        " pessoa_presente text, pessoa_contacto text," +
                        " ano_construcao integer not null default 0, n_pisos integer not null default 0, n_fraccoes integer not null default 0," +
                        " area_piso text, estado_edificio integer not null default 0, canalizacao text," +
                        " administrador text, administrador_contacto text, data_sinistro text, descricao_sinistro text," +
                        " orcamento_segurado text, n_sinistros integer not null default 0, n_fraccoes_infectadas integer not null default 0," +
                        " pesquisa integer not null default 0, pesquisa_duracao text, pesquisa_material text," +
                        " teste_pressao integer not null default 0, teste_esgoto integer not null default 0, teste_humidade integer not null default 0," +
                        " observacoes text, validado integer not null default 0, validado_por text, validado_data text," +
                        " enviado integer not null default 0, para_enviar Integer not null default 0" +
                        ")",
                "create table levantamento_topografica (" +
                        " id integer primary key, id_utilizador integer not null default 0, dia_tarefa text not null, n_topografico integer not null default 0, n_levantamento integer not null default 0," +
                        " n_obra integer not null default 0, cod_obra text not null, ano_obra integer not null default 0," +
                        " n_fraccao  integer not null default 0, compartimento text, " +
                        " compartimento_largura real, compartimento_comprimento real, compartimento_altura real, compartimento_largura2 real, compartimento_comprimento2 real," +
                        " data_registo text, por_registo text, tecto_cor text, tecto_material integer not null default 0, tecto_outro text," +
                        " moldura_cor text, moldura_material integer not null default 0, moldura_outro text," +
                        " tecto_area_afectada real, tecto_area_pintar real, moldura_area_afectada real," +
                        " paredes_cor text, paredes_material integer not null default 0, paredes_outro text, paredes_medidas text, paredes_n_afectadas integer not null default 0, paredes_area_afectada real, paredes_area_pintar real," +
                        " pavimento_cor text, pavimento_material integer not null default 0, pavimento_outro text, pavimento_medidas text, pavimento_area_afectada real, pavimento_area_reparar real, " +
                        " rodape_cor text, rodape_material integer not null default 0, rodape_outro text, rodape_medidas text, rodape_area_afectada real, rodape_area_reparar real," +
                        " portas_cor text, portas_material integer not null default 0, portas_outro text, portas_medidas text, portas_n_substituir integer not null default 0, portas_n_reparar integer not null default 0," +
                        " aros_n_substituir integer not null default 0, aros_n_reparar integer not null default 0," +
                        " outros_danos text, observacoes text," +
                        " desenho blob," +
                        " enviado integer not null default 0, para_enviar Integer not null default 0" +
                        ")",
                "create table levantamento_fraccao (" +
                        " id integer primary key, id_utilizador integer not null default 0, dia_tarefa text not null, n_fraccao  integer not null default 0, n_levantamento integer not null default 0," +
                        " n_obra integer not null default 0, cod_obra text not null, ano_obra integer not null default 0," +
                        " identificacao text, relacao text, nome text, contacto text, nif text, data_nascimento text," +
                        " n_assoalhadas integer not null default 0, area text, estado real," +
                        " enviado integer not null default 0, para_enviar Integer not null default 0" +
                        ")",
                "create table levantamento_evolucao (" +
                        " id integer primary key, id_utilizador integer not null default 0, dia_tarefa text not null, id_evolucao integer not null default 0," +
                        " n_obra integer not null default 0, cod_obra text not null, ano_obra integer not null default 0," +
                        " n_fraccao integer not null default 0, data_registo text, por_registo text" +
                        " trabalhos_executados text, finalizar_estimativa text, trabalhos_falta text," +
                        " enviado integer not null default 0, para_enviar Integer not null default 0" +
                        ")",
                "create table levantamento_evolucao_material (" +
                        " id integer primary key, id_utilizador integer not null default 0, dia_tarefa text not null, id_material integer not null default 0, id_evolucao integer not null default 0," +
                        " n_obra integer not null default 0, cod_obra text not null, ano_obra integer not null default 0," +
                        " situacao integer not null default 0, designacao text, quantidade real, valor real," +
                        " enviado integer not null default 0, para_enviar Integer not null default 0" +
                        ")"

        };
        //origem 1: do servidor; 2: do smartphone - nao enviado; 3: do smartphone enviado
        String[] scriptSQLDelete  = new String[] {
                "DROP TABLE IF EXISTS utilizador",
                "DROP TABLE IF EXISTS tarefa",
                "DROP TABLE IF EXISTS processo",
                "DROP TABLE IF EXISTS seguro",
                "DROP TABLE IF EXISTS pessoa",
                "DROP TABLE IF EXISTS morada_obra",
                "DROP TABLE IF EXISTS fotografia",
                "DROP TABLE IF EXISTS ficheiro",
                "DROP TABLE IF EXISTS evolucao",
                "DROP TABLE IF EXISTS trabalhos",
                "DROP TABLE IF EXISTS coordenadas"
        };

        this.nomeTabela = tabela;
        dbHelper = new DBHelper(contexto, scriptSQLCreate, scriptSQLDelete, categoria, DATABASE_NAME, DATABASE_VERSION);
    }

    public void setNomeTabela(String tabela){
        this.nomeTabela = tabela;
    }

    public String getNomeTabela(){
        return nomeTabela;
    }

    //verifica se existe tabela
    public boolean existeTabela(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql ="SELECT * FROM "+nomeTabela;
        try {
            //executa pedido
            Cursor c =db.rawQuery(sql, null);
            c.moveToFirst();

            c.close();
        } catch (Exception e){
            Log.e("ExisteTabela", e.toString());
            return false;
        } finally{
            if(null!=db && db.isOpen()){ db.close();}// desliga db coneccao
        }

        return true;
    }

    /**
     *
     * @return o proximo id
     */
    public int get_proxID(){
        return get_proxID(nomeTabela);
    }
    public int get_proxID(String nome_tabela){
        int id = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql ="SELECT max(id) AS total FROM "+nome_tabela;
        try {
            //executa pedido
            Cursor c =db.rawQuery(sql, null);
            if (c.moveToFirst()) {
                id = c.getInt(0)+1;
            }
            else{ id = 1;}
            c.close();
        } catch (Exception e){
            Log.e("proxID", e.toString());
        } finally{
            if(null!=db && db.isOpen()){ db.close();}// desliga db coneccao
        }
        return id;
    }

    /**
     * Insere dados na tabela
     * @param valores
     * @return sucesso! se inseriu os valores
     */
    public boolean insert(ContentValues valores){
        return insert(valores, nomeTabela);
    }
    public boolean insert(ContentValues valores, String nome_tabela){
        boolean inseriu = false;

        //abre ligacao em escrita da bd
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            // Insere linha
            long i = db.insert(nome_tabela, null, valores);
            if(0 < i){ inseriu = true;}
        } catch (Exception e){
            Log.e("Insert", e.toString());
        } finally{
            if(null!=db && db.isOpen()){ db.close();}// desliga db coneccao
        }

        return inseriu;
    }


    /**
     * actualiza registo por uma referencia
     * @param valores
     * @param oQue
     * @param queContem
     * @return
     */
    public boolean updateDados(ContentValues valores, String oQue, String queContem){
        return updateDados(valores, oQue, queContem, nomeTabela);
    }
    public boolean updateDados(ContentValues valores, String oQue, String queContem, String nome_tabela){
        String[] conteudos = new String[] { String.valueOf(queContem) };
        if(""!=oQue){ oQue+="= ?";}
        else{ conteudos=null;}
        return updateDados(valores, oQue, conteudos, nome_tabela);
    }
    public boolean updateDados(ContentValues valores, String oQue, String[] queContem){
        return updateDados(valores, oQue, queContem, nomeTabela);
    }
    public boolean updateDados(ContentValues valores, String oQue, String[] queContem, String nome_tabela){
        boolean actualizou = false;

        //abre ligacao em escrita da bd
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            // actualiza registo
            // e boa pratica usar o ?, instanciado pelos valores passados em string
            long i = db.update(nome_tabela, valores, oQue, queContem);
            if(0 < i){ actualizou = true;}
        } catch (Exception e){
            Log.e("Update", e.toString());
        } finally{
            if(null!=db && db.isOpen()){ db.close();}// desliga db coneccao
        }

        return actualizou;
    }

    /**
     * elimina registo por uma referencia
     * @param oQue
     * @param queContem
     * @return sucesso
     */
    public boolean delete(String oQue, String queContem){
        return delete(oQue, queContem, nomeTabela);
    }
    public boolean delete(String oQue, String queContem, String nome_tabela){
        String[] conteudos = new String[] { String.valueOf(queContem) };
        if(""!=oQue){ oQue+="= ?";}
        else{ conteudos=null;}
        return delete(oQue, conteudos, nome_tabela);
    }
    public boolean delete(String oQue, String[] queContem){
        return delete(oQue, queContem, nomeTabela);
    }
    public boolean delete(String oQue, String[] queContem, String nome_tabela){
        boolean eliminou = false;

        //abre ligacao em escrita da bd
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            // elimina registo
            // e boa pratica usar o ?, instanciado pelos valores passados em string
            int d = db.delete(nome_tabela, oQue, queContem);
            if(-1 < d){ eliminou = true;}
        } catch (Exception e){
            Log.e("Delete", e.toString());
        } finally{
            if(null!=db && db.isOpen()){ db.close();}// desliga db coneccao
        }

        return eliminou;
    }
    public boolean delete(){
        return delete(nomeTabela);
    }
    public boolean delete(String nome_tabela){
        boolean eliminou;

        //abre ligacao em escrita da bd
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            // elimina registo
            // e boa pratica usar o ?, instanciado pelos valores passados em string
            int d = db.delete(nome_tabela, null, null);
            eliminou = true;
        } catch (Exception e){
            Log.e("Delete", e.toString());
            eliminou = false;
        } finally{
            if(null!=db && db.isOpen()){ db.close();}// desliga db coneccao
        }

        return eliminou;
    }

    /**
     * retorna o n de elementos da tabela
     * @return n de linhas numa tabela
     */
    public int nElementos(){
        return nElementos(nomeTabela);
    }
    public int nElementos(String nome_tabela){
        SQLiteDatabase db = null;
        String sql = "SELECT * FROM "+nome_tabela;
        int conta =0;
        try{
            db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);

            if(cursor.moveToFirst()){
                conta = cursor.getCount();
            }

        } catch (Exception e){
            Log.e("nElementos", e.toString());
        } finally{
            if(null!=db && db.isOpen()){ db.close();}
        }
        return conta;
    }

    public byte[] get_dataTabela(int id, String nome_tabela){
        SQLiteDatabase db = null;
        String sql = "SELECT data FROM " + nome_tabela + " WHERE id="+id;
        byte[] resultado = null;
        try {
            db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery(sql, null);
            if(cursor.moveToFirst()){
                resultado = cursor.getBlob(cursor.getColumnIndex("data"));
            }
        } catch (Exception e){
            Log.e("nElementos", e.toString());
        } finally{
            if(null!=db && db.isOpen()){ db.close();}
        }

        return resultado;
    }

    /**
     * @param oQue
     * @param queContem
     * @param campos
     * @return linha da tabela por
     */
    public HashMap<String, String> getDadosBy(String oQue, String queContem, String[] campos) {
        return getDadosBy(oQue, queContem, campos, nomeTabela);
    }
    public HashMap<String, String> getDadosBy(String oQue, String queContem, String[] campos, String nome_tabela) {
        String[] conteudos = new String[] { String.valueOf(queContem) };
        if(""!=oQue){ oQue+="= ?";}
        else{ conteudos=null;}
        return getDadosBy(oQue, conteudos, campos, nome_tabela);
    }
    public HashMap<String, String> getDadosBy(String oQue, String[] queContem, String[] campos){
        return getDadosBy(oQue, queContem, campos, nomeTabela);
    }
    public HashMap<String, String> getDadosBy(String oQue, String[] queContem, String[] campos, String nome_tabela){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql ="SELECT * FROM " + nome_tabela + " WHERE " + oQue;

        HashMap<String, String> utilizador = new HashMap<String, String>();

        try {
            //executa pedido
            Cursor c =db.rawQuery(sql, queContem);
            if(c.moveToFirst() && 0 < c.getCount()){
                //ciclo para carragar os dados
                do{
                    HashMap<String, String> u = new HashMap<String, String>();
                    String valor;
                    for(String campo : campos) {
                        valor = c.getString(c.getColumnIndex(campo));
                        u.put(campo, valor);
                    }
                    utilizador=u;
                }while(c.moveToNext());
            }

            c.close();
        } catch (Exception e){
            Log.e("getDadosBy", e.toString());
        } finally{
            if(null!=db && db.isOpen()){ db.close();}
        }
        return utilizador;
    }

    //retorna valores da tabela
    public ArrayList<HashMap<String, String>> getDados() {
        return listaDados;
    }

    public ArrayList<HashMap<String, String>> preencheDados(String[] campos, String oQue, String queContem, String ordem){
        return preencheDados(campos, oQue, queContem, ordem, nomeTabela);
    }
    public ArrayList<HashMap<String, String>> preencheDados(String[] campos, String oQue, String queContem, String ordem, String nome_tabela){
        String[] conteudos = new String[] { String.valueOf(queContem) };
        if(""!=oQue){ oQue+="= ?";}
        else{ conteudos=null;}
        return preencheDados(campos, oQue, conteudos, ordem, nome_tabela);
    }
    public ArrayList<HashMap<String, String>> preencheDados(String[] campos, String oQue, String[] queContem, String ordem){
        return preencheDados(campos, oQue, queContem, ordem, nomeTabela);
    }
    public ArrayList<HashMap<String, String>> preencheDados(String[] campos, String oQue, String[] queContem, String ordem, String nome_tabela){
        SQLiteDatabase db = null;
        try{
            db = dbHelper.getReadableDatabase();
        }catch (NullPointerException npe){ Log.e("monta helper", npe.toString());}

        //lista em array dos dados
        ArrayList<HashMap<String, String>> lista = new ArrayList<>();

        try {
            //executa pedido
            Cursor c = db.query(nome_tabela, campos, oQue, queContem, null, null, ordem, null);
            if(c.moveToFirst()){
                //ciclo para carragar os dados
                do{
                    HashMap<String, String> u = new HashMap<String, String>();
                    String valor;
                    for(String campo : campos) {
                        valor = c.getString(c.getColumnIndex(campo));
                        u.put(campo, valor);
                    }
                    lista.add(u);
                }while(c.moveToNext());
            }

            c.close();
        } catch (Exception e){
            Log.e("PreeDados", e.toString());
        } finally{
            if(null!=db && db.isOpen()){ db.close();}
        }
        listaDados=lista;
        return lista;
    }

    /**
     *
     * @param incorporaDadosFotografia
     * @return
     */
    public ArrayList<ClasseFotografias> get_fotografias(boolean incorporaDadosFotografia){
        return get_fotografias(0, "", 0, incorporaDadosFotografia);
    }
    /**
     *
     * @param n_obra
     * @param cod_obra
     * @param ano_obra
     * @return
     */
    public ArrayList<ClasseFotografias> get_fotografias(int n_obra, String cod_obra, int ano_obra, boolean incorporaDadosFotografia){
        ArrayList<ClasseFotografias> fotografias = new ArrayList<>();
        String tabela = "fotografia";
        final String[] campos = new String[] {"id","id_utilizador","dia_tarefa","n_obra","cod_obra","ano_obra","nome_ficheiro","pasta","tipologia","descricao","por","origem","data_registo","data","gps","enviado","para_enviar","nome_temporario"};
        String[] valores = null;
        String onde = "";
        if(0==n_obra || cod_obra.equals("") || 0==ano_obra){}
        else {
            valores = new String[]{"" + n_obra, cod_obra, "" + ano_obra};
            onde = "n_obra=? AND cod_obra=? AND ano_obra=?";
        }
        String ordem = "data_registo DESC, id DESC";

        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        try {
            //executa pedido
            Cursor c = db.query(tabela, campos, onde, valores, null, null, ordem, null);
            if(c.moveToFirst()){
                //ciclo para carragar os dados
                do{
                    ClasseFotografias foto = new ClasseFotografias();
                    String valor;
                    for(String campo : campos) {
                        if(campo.equals("data")){
                            if(incorporaDadosFotografia) {
                                try {
                                    byte[] byteArray = c.getBlob(c.getColumnIndex("data"));
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                                    foto.setData(bitmap);
                                }
                                catch (OutOfMemoryError oome){Log.e("Fotografias", oome.toString());}
                            }
                        }
                        else {
                            valor = c.getString(c.getColumnIndex(campo));
                            foto.put(campo, valor);
                        }
                    }
                    fotografias.add(foto);
                }while(c.moveToNext());
            }

            c.close();
        } catch (Exception e){
            Log.e("getFotos", "Erro# "+e.getMessage());
        } finally{
            if(null!=db && db.isOpen()){ db.close();}
        }
        return fotografias;
    }

    /**
     *
     * @return
     */
    public ArrayList<ClasseDocumentos> get_documentos(){
        return get_documentos(0, "", 0);
    }

    public ArrayList<ClasseDocumentos> get_documentos(int n_obra, String cod_obra, int ano_obra){
        ArrayList<ClasseDocumentos> documentos = new ArrayList<>();
        String tabela = "ficheiro";
        final String[] campos = new String[] {"id","n_obra","cod_obra","ano_obra","nome_ficheiro","tipologia","descricao","por","origem","data_registo"};
        String[] valores = null;
        String onde = null;
        if(0==n_obra || cod_obra.equals("") || 0==ano_obra){ }
        else {
            valores = new String[]{"" + n_obra, cod_obra, "" + ano_obra};
            onde = "n_obra=? AND cod_obra=? AND ano_obra=?";
        }

        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        try {
            //executa pedido
            Cursor c = db.query(tabela, campos, onde, valores, null, null, "data_registo DESC", null);
            if(c.moveToFirst()){
                //ciclo para carragar os dados
                do{
                    ClasseDocumentos file = new ClasseDocumentos();
                    String valor;
                    for(String campo : campos) {
                        valor = c.getString(c.getColumnIndex(campo));
                        file.put(campo, valor);
                    }
                    documentos.add(file);
                }while(c.moveToNext());
            }

            c.close();
        } catch (Exception e){
            Log.e("getDocumentos", e.toString());
        } finally{
            if(null!=db && db.isOpen()){ db.close();}
        }
        return documentos;
    }

    /**
     *
     * @return
     */
    public ArrayList<ClassePicaPonto> get_picaPonto(){
        return get_picaPonto(0);
    }

    public ArrayList<ClassePicaPonto> get_picaPonto(int id_utilizador){
        ArrayList<ClassePicaPonto> pica_ponto = new ArrayList<>();
        String tabela = "pica_ponto";
        final String[] campos = new String[] {"id","id_utilizador","numero","codigo","origem","data","hora","maquina","enviado","para_enviar", "gps"};
        String[] valores = null;
        String onde = null;
        if(0<id_utilizador){
            valores = new String[]{""+id_utilizador};
            onde    = "id_utilizador=?";
        }

        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        try {
            //executa pedido
            Cursor c = db.query(tabela, campos, onde, valores, null, null, "id ASC", null);
            if(c.moveToFirst()){
                //ciclo para carragar os dados
                do{
                    ClassePicaPonto linha = new ClassePicaPonto();
                    String valor;
                    for(String campo : campos) {
                        valor = c.getString(c.getColumnIndex(campo));
                        linha.put(campo, valor);
                    }
                    pica_ponto.add(linha);
                }while(c.moveToNext());
            }

            c.close();
        } catch (Exception e){
            Log.e("get_picaPonto", e.toString());
        } finally{
            if(null!=db && db.isOpen()){ db.close();}
        }
        return pica_ponto;
    }

    /**
     *
     * @return
     */
    public ArrayList<ClasseCoordenadas> get_coordenadas(){
        return get_coordenadas(0);
    }

    public ArrayList<ClasseCoordenadas> get_coordenadas(int id_utilizador){
        ArrayList<ClasseCoordenadas> coordenadas = new ArrayList<>();
        String tabela = "coordenadas";
        final String[] campos = new String[] {"id","n_obra","cod_obra","ano_obra","id_pessoa","data_registo","enviado","para_enviar","gps"};
        String[] valores = null;
        String onde = null;
        if(0<id_utilizador){
            valores = new String[]{""+id_utilizador};
            onde    = "id_utilizador=?";
        }

        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        try {
            //executa pedido
            Cursor c = db.query(tabela, campos, onde, valores, null, null, "id ASC", null);
            if(c.moveToFirst()){
                //ciclo para carragar os dados
                do{
                    ClasseCoordenadas linha = new ClasseCoordenadas();
                    String valor;
                    for(String campo : campos) {
                        valor = c.getString(c.getColumnIndex(campo));
                        linha.put(campo, valor);
                    }
                    coordenadas.add(linha);
                }while(c.moveToNext());
            }

            c.close();
        } catch (Exception e){
            Log.e("get_coordenadas", e.toString());
        } finally{
            if(null!=db && db.isOpen()){ db.close();}
        }
        return coordenadas;
    }
}

