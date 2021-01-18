/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cvr.models;

/**
 * Modelo del objeto GeoData, estos objetos se utilizan como dato fundamental en
 * el análisis de las ventanas deslizantes.
 * 
 * Atributos:
 * 
 * data - valor tipo double que representa un dato geográfico. Se utiliza para los 
 * cálculos internos de las ventanas deslizantes.
 * 
 * accuracy - valor tipo double que representa la calidad o precisión del dato. Debe 
 * ser un valor entre 0 y 1, siendo 0 la precisión mínima y 1 la precisión máxima.
 * 
 * @author Domenica Barreiro
 */
public class GeoData {
    
    public double data;
    public double accuracy;
    
    public GeoData(double data, double accuracy) {
        this.data = data;
        this.accuracy = accuracy;
    }
}
