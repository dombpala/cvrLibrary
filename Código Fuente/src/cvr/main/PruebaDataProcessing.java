/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cvr.main;

import cvr.dataprocessing.RollingMetric;
import cvr.dataprocessing.RollingWindow;
import cvr.models.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 *
 * @author Domenica Barreiro
 */
public class PruebaDataProcessing {
    
    public static void main(String[] args) {
        int window_k = 20;
        int window_x = 6;
        String path = "src/cvr/main/datos_prueba.txt";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy");
        //Fri Nov 13 12:10:43 GMT-05:00 2020
        
        
        ArrayList<GeoData> data = new ArrayList<>();
        ArrayList<TemporalGeoData> dataTemporal = new ArrayList<>();
        
        try{
            BufferedReader bf = new BufferedReader(new FileReader(path));
            String head = bf.readLine();
            
            String line = bf.readLine();
            while(line != null){
                String[] campos = line.split(",");
                if(!campos[4].equals("null")){
                    
                    LocalDateTime timestamp = LocalDateTime.parse(campos[4],formatter);
                    dataTemporal.add(new TemporalGeoData(timestamp,Double.valueOf(campos[1]), 1));
                    data.add(new GeoData(Double.valueOf(campos[1]), 1));
                    
                }
               line = bf.readLine();
            }
            bf.close();
            
        }catch(IOException e){
            System.out.println("Fail in reading/writing file: "+e.getMessage());
        }
        
        
        double[] results = RollingWindow.rolling(window_k, window_x, 1, RollingMetric.MEAN, data);
        System.out.println("--- RESULTADOS DE PRUEBA VENTANA DESLIZANTE (ROLLING)---\n"
                          +"Resultado        :  "+results[0]
                        +"\nDesv.Est. (error):  "+results[1]
                        +"\nCódigo de Salida :  "+results[2]);
        
        
        double[] resultsTemporal = RollingWindow.rollingTemporal(1, ChronoUnit.MINUTES, window_x, 0, RollingMetric.MEAN, dataTemporal);
        System.out.println("\n--- RESULTADOS DE PRUEBA VENTANA DESLIZANTE TEMPORAL (ROLLING TEMPORAL)---\n"
                          +"Resultado        :  "+resultsTemporal[0]
                        +"\nDesv.Est. (error):  "+resultsTemporal[1]
                        +"\nCódigo de Salida :  "+resultsTemporal[2]);
        
         
    }
}
