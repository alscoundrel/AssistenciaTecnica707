package com.dlsra.assistenciatecnica707;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Utilizador on 04/04/2017.
 */

public class ClasseDataTempo {
    //variaveis
    Calendar calendario = Calendar.getInstance();

    public ClasseDataTempo(){}

    public String data_hoje(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(calendario.getTime());
    }
    public String ano_hoje(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        return sdf.format(calendario.getTime());
    }

    public String mes_hoje() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        return sdf.format(calendario.getTime());
    }

    public String dia_hoje(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        return sdf.format(calendario.getTime());
    }

    public String horas_agora(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(calendario.getTime());
    }

    public String hora_agora(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        return sdf.format(calendario.getTime());
    }

    public String minutos_agora(){
        SimpleDateFormat sdf = new SimpleDateFormat("mm");
        return sdf.format(calendario.getTime());
    }

    /**************************************************************************************
     * Procedimentos
     */
    /**
     *
     * @param horaInicial
     * @param horaFinal
     * @return em segundos a diferenca
     */
    public long diferenca_horas(String horaInicial, String horaFinal) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Calendar calInicial = Calendar.getInstance();
            Calendar calFinal   = Calendar.getInstance();
            calInicial.setTime(sdf.parse(horaInicial));
            calFinal.setTime(sdf.parse(horaFinal));
            long diferenca = Math.abs(calFinal.getTimeInMillis()-calInicial.getTimeInMillis())/1000;
            return diferenca;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    
}

