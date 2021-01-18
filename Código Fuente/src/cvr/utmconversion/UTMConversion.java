/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cvr.utmconversion;

import com.bbn.openmap.proj.Ellipsoid;
import com.bbn.openmap.proj.ProjMath;
import com.bbn.openmap.proj.coords.UTMPoint;

/**
 * 
 * Clase con métodos estáticos que realizan la conversión de UTMPoint a LLPoint y
 * viceversa.
 * 
 * Siendo UTMPoint una representación de coordenadas UTM y LLPoint la representación
 * de coordenadas geográficas Latitud y Longitud.
 * 
 * @author Domenica Barreiro
 */
public class UTMConversion {
    
    // LatLon Point to UTMpoint
    public static UTMPoint convertLatLonToUTM(LLPoint point){
        double lat = point.latitud;
        double lon = point.longitud;
        
        int zoneNumber = getZoneNumber(lat, lon);
        boolean isnorthern = lat >= 0.0F;
        UTMPoint utmPoint = LLtoUTM(lat, lon, Ellipsoid.WGS_84, null, zoneNumber, isnorthern);
        return utmPoint;
    }

    private static UTMPoint LLtoUTM(double lat, double lon, Ellipsoid ellip, UTMPoint utmPoint, int zoneNumber, boolean isNorthern) {
        double a = ellip.radius;
        double k0 = 0.9996D;
        double eccSquared = ellip.eccsq;
        double eccPrimeSquared = eccSquared / (1.0D - eccSquared);
        double eccSquared2 = eccSquared * eccSquared;
        double eccSquared3 = eccSquared2 * eccSquared;
        double LatRad = ProjMath.degToRad(lat);
        double LongRad = ProjMath.degToRad(lon);
        double LongOrigin = (double)((zoneNumber - 1) * 6 - 180 + 3);
        double LongOriginRad = Math.toRadians(LongOrigin);
        double tanLatRad = Math.tan(LatRad);
        double sinLatRad = Math.sin(LatRad);
        double cosLatRad = Math.cos(LatRad);
        double N = a / Math.sqrt(1.0D - eccSquared * sinLatRad * sinLatRad);
        double T = tanLatRad * tanLatRad;
        double C = eccPrimeSquared * cosLatRad * cosLatRad;
        double A = cosLatRad * (LongRad - LongOriginRad);
        double M = a * ((1.0D - eccSquared / 4.0D - 3.0D * eccSquared2 / 64.0D - 5.0D * eccSquared3 / 256.0D) * LatRad - (3.0D * eccSquared / 8.0D + 3.0D * eccSquared2 / 32.0D + 45.0D * eccSquared3 / 1024.0D) * Math.sin(2.0D * LatRad) + (15.0D * eccSquared2 / 256.0D + 45.0D * eccSquared3 / 1024.0D) * Math.sin(4.0D * LatRad) - 35.0D * eccSquared3 / 3072.0D * Math.sin(6.0D * LatRad));
        double UTMEasting = k0 * N * (A + (1.0D - T + C) * A * A * A / 6.0D + (5.0D - 18.0D * T + T * T + 72.0D * C - 58.0D * eccPrimeSquared) * A * A * A * A * A / 120.0D) + 500000.0D;
        double UTMNorthing = k0 * (M + N * Math.tan(LatRad) * (A * A / 2.0D + (5.0D - T + 9.0D * C + 4.0D * C * C) * A * A * A * A / 24.0D + (61.0D - 58.0D * T + T * T + 600.0D * C - 330.0D * eccPrimeSquared) * A * A * A * A * A * A / 720.0D));
        if (!isNorthern) {
            UTMNorthing += 1.0E7D;
        }

        if (utmPoint == null) {
            utmPoint = new UTMPoint();
        }

        utmPoint.northing = UTMNorthing;
        utmPoint.easting = UTMEasting;
        utmPoint.zone_number = zoneNumber;
        utmPoint.zone_letter = (char)(isNorthern ? 78 : 83);
        return utmPoint;
    }

    private static int getZoneNumber(double lat, double lon) {
        int zoneNumber = (int)((lon + 180.0D) / 6.0D) + 1;
        if (lon == 180.0D) {
            zoneNumber = 60;
        }

        if (lat >= 56.0D && lat < 64.0D && lon >= 3.0D && lon < 12.0D) {
            zoneNumber = 32;
        }

        if (lat >= 72.0D && lat < 84.0D) {
            if (lon >= 0.0D && lon < 9.0D) {
                zoneNumber = 31;
            } else if (lon >= 9.0D && lon < 21.0D) {
                zoneNumber = 33;
            } else if (lon >= 21.0D && lon < 33.0D) {
                zoneNumber = 35;
            } else if (lon >= 33.0D && lon < 42.0D) {
                zoneNumber = 37;
            }
        }
        return zoneNumber;
    }
    
    
    // UTM coordinates to LatLon coordinates
    
    public static LLPoint convertUTMToLatLon(UTMPoint point){
        return UTMtoLL(Ellipsoid.WGS_84, point.northing, point.easting, point.zone_number, point.zone_letter);
    }

    private static LLPoint UTMtoLL(Ellipsoid ellip, double UTMNorthing, double UTMEasting, int zoneNumber, char zoneLetter) {
        if (zoneNumber >= 0 && zoneNumber <= 60) {
            double k0 = 0.9996D;
            double a = ellip.radius;
            double eccSquared = ellip.eccsq;
            double e1 = (1.0D - Math.sqrt(1.0D - eccSquared)) / (1.0D + Math.sqrt(1.0D - eccSquared));
            double x = UTMEasting - 500000.0D;
            double y = UTMNorthing;
            if (zoneLetter == 'S') {
                y = UTMNorthing - 1.0E7D;
            }

            double LongOrigin = (double)((zoneNumber - 1) * 6 - 180 + 3);
            double eccPrimeSquared = eccSquared / (1.0D - eccSquared);
            double M = y / k0;
            double mu = M / (a * (1.0D - eccSquared / 4.0D - 3.0D * eccSquared * eccSquared / 64.0D - 5.0D * eccSquared * eccSquared * eccSquared / 256.0D));
            double phi1Rad = mu + (3.0D * e1 / 2.0D - 27.0D * e1 * e1 * e1 / 32.0D) * Math.sin(2.0D * mu) + (21.0D * e1 * e1 / 16.0D - 55.0D * e1 * e1 * e1 * e1 / 32.0D) * Math.sin(4.0D * mu) + 151.0D * e1 * e1 * e1 / 96.0D * Math.sin(6.0D * mu);
            double N1 = a / Math.sqrt(1.0D - eccSquared * Math.sin(phi1Rad) * Math.sin(phi1Rad));
            double T1 = Math.tan(phi1Rad) * Math.tan(phi1Rad);
            double C1 = eccPrimeSquared * Math.cos(phi1Rad) * Math.cos(phi1Rad);
            double R1 = a * (1.0D - eccSquared) / Math.pow(1.0D - eccSquared * Math.sin(phi1Rad) * Math.sin(phi1Rad), 1.5D);
            double D = x / (N1 * k0);
            double lat = phi1Rad - N1 * Math.tan(phi1Rad) / R1 * (D * D / 2.0D - (5.0D + 3.0D * T1 + 10.0D * C1 - 4.0D * C1 * C1 - 9.0D * eccPrimeSquared) * D * D * D * D / 24.0D + (61.0D + 90.0D * T1 + 298.0D * C1 + 45.0D * T1 * T1 - 252.0D * eccPrimeSquared - 3.0D * C1 * C1) * D * D * D * D * D * D / 720.0D);
            lat = ProjMath.radToDeg(lat);
            double lon = (D - (1.0D + 2.0D * T1 + C1) * D * D * D / 6.0D + (5.0D - 2.0D * C1 + 28.0D * T1 - 3.0D * C1 * C1 + 8.0D * eccPrimeSquared + 24.0D * T1 * T1) * D * D * D * D * D / 120.0D) / Math.cos(phi1Rad);
            lon = LongOrigin + ProjMath.radToDeg(lon);
            return new LLPoint(lat, lon);
        } else {
            return null;
        }
    }
}
