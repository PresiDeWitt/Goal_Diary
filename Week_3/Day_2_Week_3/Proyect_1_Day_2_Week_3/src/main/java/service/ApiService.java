package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Servicio para realizar peticiones HTTP a APIs REST.
 */
public class ApiService {

    private static final int TIMEOUT_MS = 5000;

    /**
     * Realiza una petición GET a la URL especificada y devuelve la respuesta como String.
     *
     * @param urlString URL a la que se realiza la petición
     * @return String con la respuesta de la API
     * @throws IOException Si hay problemas de conexión o lectura
     */
    public String get(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            // Configuración de la conexión
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIMEOUT_MS);
            connection.setReadTimeout(TIMEOUT_MS);

            // Verificar código de respuesta
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("Error al obtener datos. Código de respuesta: " + responseCode);
            }

            // Leer la respuesta
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()))) {

                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                return response.toString();
            }
        } finally {
            // Asegurarse de cerrar la conexión
            connection.disconnect();
        }
    }
}