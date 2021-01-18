/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cvr.models;

import java.time.LocalDateTime;

/**
 * Modelo del objeto TemporalGeoData, estos objetos se utilizan como dato fundamental en
 * el análisis de las ventanas deslizantes temporales. Hereda de la clase GeoData.
 * 
 * Atributos:
 * 
 * timestamp - objeto tipo LocalDateTime que representa la marca temporal en la cual
 * se ha recolectado este dato.
 * 
 * @author Doménica Barreiro
 */
public class TemporalGeoData extends GeoData {
    
    public LocalDateTime timestamp;
    
    public TemporalGeoData(LocalDateTime timestamp, double data, double accuracy) {
        super (data, accuracy);
        this.timestamp = timestamp;
    }
    
}
