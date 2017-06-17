package com.m2brcorp.geostatus.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vinic on 30/10/2016.
 */

public class DataHoraUtils {

    public static String dataFormatada(Date data){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(data);
    }

    public static String horaFormatada(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        return sdf.format(date);
    }

    public static String dataHoraFormatada(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM" +" - "+ "hh:mm");
        return sdf.format(date);
    }

}
