package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Service gérant l'intégration avec l'API tierce Open-Meteo.
 * <p>
 * Il permet de récupérer les conditions météorologiques actuelles pour une localisation
 * donnée en deux étapes : géocodage (nom vers coordonnées GPS) puis requête météo.
 */
@Service
public class MeteoService {

    private final RestTemplate restTemplate;

    public MeteoService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Récupère et formate la météo actuelle pour une ville ou localisation donnée.
     *
     * @param localisation Le nom de la ville ou de la région (ex: "Paris", "Lyon")
     * @return Une chaîne de caractères formatée décrivant la météo (ex: "Ciel dégagé, 22.5°C"),
     * ou un message d'erreur si la localisation est introuvable ou l'API indisponible.
     */
    public String getMeteoParLocalisation(String localisation) {
        if (localisation == null || localisation.isEmpty()) {
            return "Non disponible";
        }

        try {
            // Étape 1 : Géocodage (conversion du nom de la ville en latitude/longitude)
            String geocodingUrl = "https://geocoding-api.open-meteo.com/v1/search?name=" 
                    + java.net.URLEncoder.encode(localisation, "UTF-8") + "&count=1&language=fr";
            
            GeocodingResponse geoResponse = restTemplate.getForObject(geocodingUrl, GeocodingResponse.class);
            
            if (geoResponse == null || geoResponse.getResults() == null || geoResponse.getResults().isEmpty()) {
                return "Localisation non trouvée";
            }
            
            double latitude = geoResponse.getResults().get(0).getLatitude();
            double longitude = geoResponse.getResults().get(0).getLongitude();
            
            // Étape 2 : Récupération des données météorologiques via les coordonnées
            String weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude 
                    + "&longitude=" + longitude + "&current_weather=true&timezone=auto";
            
            WeatherResponse weatherResponse = restTemplate.getForObject(weatherUrl, WeatherResponse.class);
            
            if (weatherResponse != null && weatherResponse.getCurrent_weather() != null) {
                return formaterMeteo(weatherResponse.getCurrent_weather());
            }
            
            return "Météo non disponible";
            
        } catch (Exception e) {
            return "Erreur météo";
        }
    }

    /**
     * Formate l'objet de réponse météo en une chaîne lisible par l'utilisateur.
     *
     * @param weather L'objet contenant les données brutes (température et code météo)
     * @return La description formatée
     */
    private String formaterMeteo(CurrentWeather weather) {
        int code = weather.getWeathercode();
        String description = getWeatherDescription(code);
        return description + ", " + weather.getTemperature() + "°C";
    }

    /**
     * Convertit les codes WMO (World Meteorological Organization) retournés par l'API Open-Meteo
     * en une description textuelle en français.
     *
     * @param code Le code météo WMO
     * @return La description en français
     */
    private String getWeatherDescription(int code) {
        switch (code) {
            case 0: return "Ciel dégagé";
            case 1: case 2: case 3: return "Partiellement nuageux";
            case 45: case 48: return "Brouillard";
            case 51: case 53: case 55: return "Bruine";
            case 56: case 57: return "Bruine verglaçante";
            case 61: case 63: case 65: return "Pluie";
            case 66: case 67: return "Pluie verglaçante";
            case 71: case 73: case 75: return "Neige";
            case 77: return "Grains de neige";
            case 80: case 81: case 82: return "Averses de pluie";
            case 85: case 86: return "Averses de neige";
            case 95: return "Orage";
            case 96: case 99: return "Orage avec grêle";
            default: return "Temps variable";
        }
    }

    // ===================================================================================
    // Classes internes DTO (Data Transfer Objects) pour désérialiser le JSON de l'API tierce
    // ===================================================================================

    private static class GeocodingResponse {
        private java.util.List<GeocodingResult> results;
        public java.util.List<GeocodingResult> getResults() { return results; }
        public void setResults(java.util.List<GeocodingResult> results) { this.results = results; }
    }

    private static class GeocodingResult {
        private String name;
        private double latitude;
        private double longitude;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }
        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }
    }

    private static class WeatherResponse {
        private CurrentWeather current_weather;
        public CurrentWeather getCurrent_weather() { return current_weather; }
        public void setCurrent_weather(CurrentWeather current_weather) { this.current_weather = current_weather; }
    }

    private static class CurrentWeather {
        private double temperature;
        private int weathercode;
        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }
        public int getWeathercode() { return weathercode; }
        public void setWeathercode(int weathercode) { this.weathercode = weathercode; }
    }
}