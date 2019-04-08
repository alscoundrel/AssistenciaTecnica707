package com.dlsra.assistenciatecnica707;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by AlScoundrel on 29/03/2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    private String CATEGORIA = "";
    // Database Name
    private static String DATABASE_NAME;
    private String NOME_TABELA;
    private String[] scriptSQLCreate;
    private String[] scriptSQLDelete;

    //version number to upgrade database version
    //each time if you Add, Edit table, you need to change the
    //version number.
    private static int DATABASE_VERSION;

    /**
     *  construtor classe
     * @param context recebe a actividade
     * @param scriptSQLCreate o sql para criacao da tabela/dados
     * @param scriptSQLDelete o sql para eliminar a tabela
     * @param categoria guarda a categoria do pedido da classe
     */
    public DBHelper(Context context, String[] scriptSQLCreate, String[] scriptSQLDelete, String categoria, String db_nome, Integer db_versao) {
        super(context, db_nome, null, db_versao);

        this.DATABASE_NAME = db_nome;
        this.DATABASE_VERSION = db_versao;
        this.CATEGORIA = categoria;
        this.scriptSQLCreate = scriptSQLCreate;
        this.scriptSQLDelete = scriptSQLDelete;
    }


    @Override
    // Criar novo banco...
    public void onCreate(SQLiteDatabase db) {
        Log.i(CATEGORIA, "Criar banco com sql");
        int qtdeScripts = scriptSQLCreate.length;
        // Executa cada sql passado como parâmetro
        try {
            for (int i = 0; i < qtdeScripts; i++) {
                String sql = scriptSQLCreate[i];
                Log.i(CATEGORIA, sql);
                // Cria o banco de dados executando o script de criação
                db.execSQL(sql);
            }
        }
        catch (Exception e) {
            Log.e(CATEGORIA + " onCreator", e.toString());
        }
    }

    @Override
    // Mudou a versão...
    public void onUpgrade(SQLiteDatabase db, int versaoAntiga, int novaVersao) {
        Log.w(CATEGORIA, "Atualizando da versão " + versaoAntiga + " para " + novaVersao + ". Todos os registros serão apagados.");

        try {
            // apaga as tabelas...
            for(String sql : scriptSQLDelete) {
                db.execSQL(sql);
            }
            // Cria novamente...
            onCreate(db);

        } catch (Exception e){
            Log.e(CATEGORIA+" onUpgrade", e.toString());
        }
    }
}
