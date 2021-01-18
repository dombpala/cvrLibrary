/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cvr.dataprocessing;

import cvr.models.GeoData;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;

/**
 * Clase de métodos estáticos auxiliares que se utilizan en el análisis de las ventanas deslizantes.
 * 
 * @author Domenica Barreiro
 */
public class UtilsRW {
    
    /**
     * Calcula la media aritmética de los datos recibidos por parámetro.
     * 
     * @param window colección tipo Queue de java que contiene objetos tipo GeoData
     * de los que se obtendrá la media aritmética.
     * @return valor tipo double que representa la media aritmética de los datos de la 
     * colección. Se utiliza el campo {data} del objeto GeoData para realizar el cálculo. 
     */
    public static double getMovingMean(Queue<GeoData> window){

        int length = 0;
        double acum = 0;

        for(GeoData value: window){
            acum += value.data;
            length++;
        }
        return acum/(length);
    }
    
    /**
     * Calcula la mediana de los datos recibidos por parámetro.
     * 
     * @param window colección tipo Queue de java que contiene objetos tipo GeoData
     * de los que se obtendrá la mediana.
     * @return valor tipo double que representa la mediana de los datos de la colección.
     * Se utiliza el campo {data} del objeto GeoData para realizar el cálculo.
     */
    public static double getMovingMedian(Queue<GeoData> window){
        
        ArrayList<Double> data = new ArrayList<>();
        
        int length = 0;

        for(GeoData value: window){
            data.add(value.data);
            length++;
        }
        
        Collections.sort(data);
        
        double median;
        
        if(length%2 == 0){
            median = (data.get((length/2)-1) + data.get(length/2))/2;
        }else{
            median = data.get(length/2);
        }
        return median;
    }
    
    /**
     * Calcula la desviación estándar de los datos recibidos por parámetro.
     * 
     * @param window colección tipo Queue de java que contiene objetos tipo GeoData
     * de los que se obtendrá la desviación estándar.
     * @return valor tipo double que representa la desviación estándar de los datos de 
     * la colección. Se utiliza el campo {data} del objeto GeoData para realizar el cálculo.
     */
    public static double getMovingStd(Queue<GeoData> window){

        int length = 0;
        double acum = 0;
        double std = 0.0;

        for(GeoData value : window) {
            acum += value.data;
            length++;
        }

        double mean = acum/length;

        for(GeoData value: window) {
            std += Math.pow(value.data - mean, 2);
        }

        return Math.sqrt(std/length);
    }   
    
    /**
     * Calcula la media aritmética de un arreglo de datos tipo double.
     * 
     * @param numArray arreglo de datos tipo double al que se le calculará la media.
     * @return valor tipo double que representa la media de los datos del arreglo.
     */
    public static double calculateMean(double numArray[]){
        double sum = 0.0;
        int length = numArray.length;

        for(double num : numArray) {
            sum += num;
        }
        return sum/length;
    }
    
    /**
     * Verifica que el intervalo de tiempo almacenado en el arreglo time_difference cumple con el umbral
     * establecido en base a los parámetros time y unit.
     * 
     * @param time_difference arreglo de cuatro datos tipo long que representan un intervalo de tiempo
     * de la siguiente forma:
     * [dias, horas, minutos, segundos]
     * @param time valor entero que representa el umbral de tiempo.
     * @param unit objeto tipo ChronoUnit de la librería time.temporal de java, representa la unidad
     * de tiempo del umbral (HOURS, MINUTES, SECONDS).
     * @return valor entero que indica si el intervalo de tiempo del arreglo time_difference cumple o
     * no con el umbral de tiempo.
     *      0: si el intervalo time_difference cumple con el umbral de tiempo en cantidad y unidad.
     *      1: si el intervalo time_difference no cumple y supera el umbral de tiempo.
     *      -1: si el intervalo time_difference no cumple y es menor que el umbral de tiempo.
     * 
     */
    public static int meetsTimeThreshold(long[] time_difference, int time, ChronoUnit unit){
        long acum = 0;
        
        switch(unit){
            case HOURS:
                acum = (time_difference[0]*24) + time_difference[1];
                break;
            case MINUTES:
                acum = (time_difference[0]*1440) + (time_difference[1]*60) + time_difference[2];
                break;
            case SECONDS:
                acum = (time_difference[0]*86400) + (time_difference[1]*3600)+(time_difference[2]*60) + time_difference[3];
                break;
        }
       
        if(acum > time){
            return 1;
        } else if (acum < time){
            return -1;
        }
        return 0;
    }
    
    /**
     * Calcula la diferencia entre dos objetos LocalDateTime de la librería time de java, en cuatro unidades 
     * de tiempo: días, horas, minutos y segundos.
     * 
     * @param first objeto LocalDateTime utilizado como base para realizar el cálculo de la diferencia. Debe 
     * ser temporalmente menor que el parámetro current.
     * @param current objeto LocalDateTime con el que se calcula la diferencia de tiempo en base al parámetro
     * first. Debe ser temporalmente mayor que el parámetro first.
     * @return un arreglo de cuatro datos tipo long que representan la diferencia entre las dos fechas:
     * [dias, horas, minutos, segundos].
     */
    public static long[] timestampDifference(LocalDateTime first, LocalDateTime current){
    
        LocalDateTime temp = LocalDateTime.from(first);
        
        long days = temp.until(current, ChronoUnit.DAYS );
        temp = temp.plusDays( days );

        long hours = temp.until(current, ChronoUnit.HOURS );
        temp = temp.plusHours( hours );

        long minutes = temp.until(current, ChronoUnit.MINUTES );
        temp = temp.plusMinutes( minutes );

        long seconds = temp.until( current, ChronoUnit.SECONDS );
        
        return new long[]{days,hours,minutes,seconds};
    } 
    
}
