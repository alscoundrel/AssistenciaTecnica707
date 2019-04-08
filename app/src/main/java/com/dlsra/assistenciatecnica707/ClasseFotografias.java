package com.dlsra.assistenciatecnica707;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Utilizador on 18/02/2016.
 */
public class ClasseFotografias {
    private int id;
    private int n_obra;
    private String cod_obra;
    private int ano_obra;
    private String nome_ficheiro;
    private String nome_temporario;
    private String pasta;
    private String tipologia;
    private String descricao;
    private String por;
    private int origem;
    private String data_registo;
    private String gps;
    private Bitmap data;
    private boolean para_enviar = false;
    private boolean enviado = false;

    ClasseFotografias(){}

    public int getId(){ return this.id;}
    public int getN_obra(){ return this.n_obra;}
    public String getCod_obra(){ return this.cod_obra;}
    public int getAno_obra(){ return this.ano_obra;}
    public String getNome_ficheiro(){ return this.nome_ficheiro;}
    public String getNome_temporario(){ return this.nome_temporario;}
    public String getPasta(){ return this.pasta;}
    public String getTipologia(){ return this.tipologia;}
    public String getDescricao(){ return this.descricao;}
    public String getPor(){ return this.por;}
    public int getOrigem(){ return this.origem;}
    public String getData_registo(){ return this.data_registo;}
    public String getGps(){ return gps;}
    public Bitmap getData(){ return this.data;}
    public boolean isPara_enviar() {return para_enviar;}
    public boolean isEnviado(){ return enviado;}

    public void setId(int id){ this.id=id;}
    public void setN_obra(int n_obra){ this.n_obra=n_obra;}
    public void setCod_obra(String cod_obra){ this.cod_obra=cod_obra;}
    public void setAno_obra(int ano_obra){ this.ano_obra=ano_obra;}
    public void setNome_ficheiro(String nome_ficheiro){ this.nome_ficheiro=nome_ficheiro;}
    public void setNome_temporario(String nome_temporario) {this.nome_temporario = nome_temporario;}
    public void setPasta(String pasta){ this.pasta=pasta;}
    public void setTipologia(String tipologia){ this.tipologia=tipologia;}
    public void setDescricao(String descricao){ this.descricao=descricao;}
    public void setPor(String por){ this.por=por;}
    public void setOrigem(int origem){ this.origem=origem;}
    public void setData_registo(String data_registo){ this.data_registo=data_registo;}
    public void setGps(String gps) { this.gps = gps;}
    public void setData(Bitmap data){ this.data=data;}
    public void setPara_enviar(boolean para_enviar) {this.para_enviar = para_enviar;}
    public void setEnviado(boolean enviado){ this.enviado = enviado;}

    public void put(String campo, String valor){
        switch (campo){
            case "id": setId(Integer.parseInt(valor)); break;
            case "n_obra": setN_obra(Integer.parseInt(valor)); break;
            case "cod_obra": setCod_obra(valor); break;
            case "ano_obra": setAno_obra(Integer.parseInt(valor)); break;
            case "nome_ficheiro": setNome_ficheiro(valor); break;
            case "nome_temporario": setNome_temporario(valor);break;
            case "pasta": setPasta(valor); break;
            case "tipologia": setTipologia(valor); break;
            case "descricao": setDescricao(valor); break;
            case "por": setPor(valor); break;
            case "data_registo": setData_registo(valor); break;
            case "gps": setGps(valor); break;
            case "para_enviar": setPara_enviar(1 == Integer.parseInt(valor)); break;
            case "origem": setOrigem(Integer.parseInt(valor)); break;
            case "enviado": setEnviado(1 == Integer.parseInt(valor)); break;
        }
    }

    public void put(String campo, int valor){
        switch (campo){
            case "id": setId(valor); break;
            case "n_obra": setN_obra(valor);break;
            case "ano_obra": setAno_obra(valor); break;
            case "origem": setOrigem(valor); break;
            case "para_enviar": setPara_enviar(1==valor);
            case "enviado": setEnviado(1==valor); break;
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
            case "nome_temporario": valor=this.nome_temporario; break;
            case "pasta": valor=this.pasta; break;
            case "tipologia": valor=this.tipologia; break;
            case "descricao": valor=this.descricao; break;
            case "por": valor=this.por; break;
            case "data_registo": valor=this.data_registo; break;
            case "gps": valor=this.gps; break;
            case "origem": valor=""+this.origem; break;
            case "para_enviar": valor=this.isPara_enviar()?"1":"0";
            case "enviado": valor=this.isEnviado()?"1":"0";
        }
        return valor;
    }

    public static Bitmap decodeFile(File f,int WIDTH,int HIGHT){
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);

            //The new size we want to scale to
            final int REQUIRED_WIDTH=WIDTH;
            final int REQUIRED_HIGHT=HIGHT;
            //Find the correct scale value. It should be the power of 2.
            int scale=1;
            while(o.outWidth/scale/2>=REQUIRED_WIDTH && o.outHeight/scale/2>=REQUIRED_HIGHT)
                scale*=2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        }
        catch (FileNotFoundException e) {}
        catch (OutOfMemoryError e){ return textAsBitmap("Falta de memória para alocar...", 56.0F, Color.RED);}
        return null;
    }

    private static Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }
}
