/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cvr.utmconversion;

/**
 * Modelo del objeto LLPoint, estos objetos se utilizan para la representación de
 * un punto geográfico con coordenadas Latitud y Longitud.
 * 
 * @author Domenica Barreiro
 */
public class LLPoint {
    
    public double latitud;
    public double longitud;

    public LLPoint(double latitud, double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }
    
}

