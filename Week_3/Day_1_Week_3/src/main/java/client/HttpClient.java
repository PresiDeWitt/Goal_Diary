package client;

import model.HttpResponseModel;
import utils.HttpReader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class HttpClient {
    /**
     * Realiza una petición HTTP GET a la URL especificada
     * @param urlString URL a la que se realizará la petición
     * @return Objeto HttpResponseModel con los datos de la respuesta
     */
    public static HttpResponseModel makeHttpRequest(String urlString) {
        HttpResponseModel response = new HttpResponseModel();
        HttpURLConnection connection = null;

        try {
            // Establecer la conexión
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            // Configurar método HTTP
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // Obtener código de estado
            int statusCode = connection.getResponseCode();
            response.setStatusCode(statusCode);

            // Obtener headers
            Map<String, List<String>> headers = connection.getHeaderFields();
            response.setHeaders(headers);

            // Leer el cuerpo de la respuesta
            if (statusCode >= 200 && statusCode < 300) {
                // Si es una respuesta exitosa
                response.setBody(HttpReader.readResponseBody(connection));
            } else {
                // Si es un error, intentar leer el cuerpo de error
                response.setBody(HttpReader.readErrorBody(connection));
            }

        } catch (MalformedURLException e) {
            response.setException("URL mal formada: " + e.getMessage());
        } catch (IOException e) {
            response.setException("Error de entrada/salida: " + e.getMessage());
        } catch (Exception e) {
            response.setException("Error inesperado: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return response;
    }
}