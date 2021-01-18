/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cvr.dataprocessing;

import cvr.models.TemporalGeoData;
import cvr.models.GeoData;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Clase con métodos estáticos que realizan el análisis de las ventanas deslizantes.
 * Existen dos implementaciones:
 * 1. rolling: Ventana deslizante por número de datos.
 * 2. rollingTemporal: Ventana deslizante temporal (umbral de tiempo).
 * 
 * @author Domenica Barreiro
 */
public class RollingWindow {
    
    /**
     * Calcula una métrica y el error de datos utilizando ventanas deslizantes dado un número de datos por ventana.
     * 
     * @param window_k valor entero que representa el número de datos que debe tener una ventana para calcular las métricas.
     * @param window_x valor entero que representa el número de ventanas que se utilizan para el cálculo del valor final.
     * @param min_accuracy valor flotante que representa la precisión mínima que debe tener un dato para ser considerado válido dentro del análisis.
     * Debe estar entre 0 y 1, siendo 0 la precisión mínima y 1 la precisión máxima.
     * @param metric valor tipo RollingMetric que define cuál métrica se va a calcular en las ventanas.
     * @param data debe ser una colección que implemente la interfaz Collection cuyo contenido sean elementos tipo GeoData pertenecientes a 
     * esta librería con los atributos: 
     * data, accuracy
     * donde el campo accuracy debe ser un valor decimal entre 0 y 1 que represente la calidad o precisión del dato, siendo 0 la precisión mínima
     * y 1 la precisión máxima.
     * 
     * @return arreglo de 3 números decimales con los siguientes campos:
     * [promedio_final, error, codigo_salida]
     * donde promedio_final es el promedio de los resultados obtenidos de las {window_x} ventanas cuya desviación estándar sea mínima,
     * error es el promedio de las desviaciones estándar de las {window_x} ventanas escogidas, el código_salida indica si el algoritmo
     * culminó correctamente o tuvo algún error.
     * 
     * Códigos de salida:
     *   1    Algoritmo culminado sin errores.
     *  -1    Si la colección {data} está vacía. 
     *  -2    Si el número asignado de elementos a una ventana {window_k}, es mayor que el número total de datos en la colección {data}.
     *  -3    Si el argumento {min_accuracy} no está en el rango establecido [0,1].
     *  -4    Si el número de ventanas a considerar para calcular el resultado final {window_x}, es mayor que el número de ventanas
     *        que se pueden formar de la colección {data} con {window_k} elementos.
     * 
     */
    public static double[] rolling(int window_k, int window_x, double min_accuracy, RollingMetric metric, Collection<GeoData> data){
        
        if(data.isEmpty()){ 
            return new double[]{0,0,-1}; 
        } 
        
        else if(min_accuracy < 0 || min_accuracy > 1){ 
            return new double[]{0,0,-3}; 
        }
        
        else if(window_k > data.size()){  
            return new double[]{0,0,-2}; 
        }
        
        else if(window_x > (data.size() - window_k + 1)){ 
            return new double[]{0,0,-4}; 
        } 
        
        
        PriorityQueue<double[]> minHeap = new PriorityQueue<>(new Comparator<double[]>() {
            @Override
            public int compare(double[] o1, double[] o2) {
                return ((Double)o1[1]).compareTo(o2[1]);
            }
        });
        
        Queue<GeoData> window = new LinkedList<>();
        Iterator<GeoData> iterator = data.iterator();
        int cont_data = 0;
        
        while(iterator.hasNext()){
            
            GeoData value = iterator.next();
            
            if(value.accuracy >= min_accuracy){
                window.offer(value);
                cont_data++;
                
                if(cont_data >= window_k){
                
                    double result_metric;
                    switch(metric){
                        case MEAN:
                            result_metric = UtilsRW.getMovingMean(window);
                            break;
                        case MEDIAN:
                            result_metric = UtilsRW.getMovingMedian(window);
                            break;
                        default:
                            result_metric = UtilsRW.getMovingMean(window);
                    }

                    double std = UtilsRW.getMovingStd(window);
                    minHeap.add(new double[]{result_metric, std});
                    window.poll();
                }
            }
        }
        
        
        double[] results_metric = new double[window_x];
        double[] stds = new double[window_x];
        int i = 0;

        while(i < window_x){
            double[] arreglo = minHeap.poll();
            results_metric[i] = arreglo[0];
            stds[i] = arreglo[1];
            i++;
        }

        double error = UtilsRW.calculateMean(stds);
        double promedio_final = UtilsRW.calculateMean(results_metric);
       
        return new double[]{promedio_final, error, 1};
    }
    
    
           
    /**
     * Calcula una métrica y el error de los datos utilizando ventanas deslizantes dado un umbral temporal.
     * 
     * @param time_interval valor entero que representa el valor de tiempo utilizado como umbral para la ventana. Debe ser mayor a cero.
     * @param time_unit valor tipo ChronoUnit que indica la unidad de tiempo en el que se encuentra time_interval. Sólo se aceptan HOURS, 
     * MINUTES y SECONDS como unidades de tiempo válidas para el análisis.
     * @param window_x valor entero que representa el número de ventanas que se utilizan para el cálculo del valor final.
     * @param min_accuracy valor flotante que representa la precisión mínima que debe tener un dato para ser considerado válido dentro del 
     * análisis. Debe estar entre 0 y 1, siendo 0 la precisión mínima y 1 la precisión máxima.
     * @param metric valor tipo RollingMetric que define cuál métrica se va a calcular en las ventanas.
     * @param data debe ser un objeto que implemente la interfaz Collection cuyo contenido sean elemento tipo TemporalData pertenecientes a 
     * esta librería con los atributos: 
     * timestamp, data, accuracy
     * donde accuracy debe ser un valor decimal entre 0 y 1 que represente la calidad o precisión del dato, siendo 0 la precisión mínima
     * y 1 la precisión máxima.
     * 
     * @return arreglo de 3 números decimales con los siguientes campos:
     * [promedio_final, error, codigo_salida]
     * donde promedio_final es el promedio de los resultados obtenidos de las {window_x} ventanas cuya desviación estándar sea mínima,
     * error es el promedio de las desviaciones estándar de las {window_x} ventanas escogidas, el código_salida indica si el algoritmo
     * culminó correctamente o tuvo algún error.
     * 
     * Códigos de salida:
     *   1    Algoritmo culminado sin errores.
     *  -1    Si la colección {data} está vacía. 
     *  -2    Si el argumento {time_interval} no es mayor que cero.
     *  -3    Si el argumento {min_accuracy} no está en el rango establecido [0,1].
     *  -4    Si el argumento {time_unit} no corresponde a los valores aceptados en el algoritmo (HOURS, MINUTES, SECONDS).
     *  -5    Si el número de ventanas a considerar para calcular el resultado final {window_x}, es mayor que el número de ventanas
     *        que se forman de la colección {data} con el umbral de tiempo establecido.
     *  -6    Si hubieron ventanas cuyos datos temporales no cumplían con el umbral de tiempo requerido. 
     * 
     */
    public static double[] rollingTemporal(int time_interval, ChronoUnit time_unit, int window_x, double min_accuracy, RollingMetric metric, Collection<TemporalGeoData> data){
        
        
        if(data.isEmpty()){ 
            return new double[]{0,0,-1}; 
        } 
        
        else if(time_interval < 1){  
            return new double[]{0,0,-2}; 
        }
        
        else if(min_accuracy < 0 || min_accuracy > 1){ 
            return new double[]{0,0,-3}; 
        }
        
        else if(!(time_unit.equals(ChronoUnit.HOURS)|| time_unit.equals(ChronoUnit.MINUTES)|| time_unit.equals(ChronoUnit.SECONDS))){ 
            return new double[]{0,0,-4}; 
        } 
        
        
        PriorityQueue<double[]> minHeap = new PriorityQueue<>(new Comparator<double[]>() {
            @Override
            public int compare(double[] o1, double[] o2) {
                return ((Double)o1[1]).compareTo(o2[1]);
            }
        });
        
        Queue<GeoData> window = new LinkedList<>();
        int cont_windows = 0;
        int empty_window = 0;
        
        Iterator<TemporalGeoData> iterator = data.iterator();
        
        while(iterator.hasNext()){
            
            TemporalGeoData value = iterator.next();
                    
            if(value.accuracy >= min_accuracy){
                
                window.offer(value);
                
                TemporalGeoData head = (TemporalGeoData) window.peek();
                
                long[] time_difference = UtilsRW.timestampDifference(head.timestamp, value.timestamp);
                int threshold = UtilsRW.meetsTimeThreshold(time_difference, time_interval, time_unit);
                
                if(threshold == 0){
                    
                    double result_metric;
                    switch(metric){
                        case MEAN:
                            result_metric = UtilsRW.getMovingMean(window);
                            break;
                        case MEDIAN:
                            result_metric = UtilsRW.getMovingMedian(window);
                            break;
                        default:
                            result_metric = UtilsRW.getMovingMean(window);
                            break;
                    }

                    double std = UtilsRW.getMovingStd(window);
                    minHeap.add(new double[]{result_metric, std});
                    window.poll();
                    cont_windows++;
                    
                }else if(threshold == 1){
                    window.poll();
                    empty_window++;
                }
            }
        }
        
        if(cont_windows < window_x){
            return new double[]{0,0,-5}; 
        } 
        
        double[] results_metric = new double[window_x];
        double[] stds = new double[window_x];
        int i = 0;

        while(i < window_x){
            double[] arreglo = minHeap.poll();
            results_metric[i] = arreglo[0];
            stds[i] = arreglo[1];
            i++;
        }
        
        double error = UtilsRW.calculateMean(stds);
        double promedio_final = UtilsRW.calculateMean(results_metric);
       
        if(empty_window > 0){
            return new double[]{promedio_final, error, -6};
        }
        
        return new double[]{promedio_final, error, 1};
    }
    
}
    
    
    
    

