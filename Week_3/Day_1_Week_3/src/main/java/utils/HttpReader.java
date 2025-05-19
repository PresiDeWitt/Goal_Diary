package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class HttpReader {
    /**
     * Lee el cuerpo de la respuesta HTTP
     * @param connection Conexión HTTP establecida
     * @return Contenido del cuerpo de respuesta como String
     */
    public static String readResponseBody(HttpURLConnection connection) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    /**
     * Lee el cuerpo de error de la respuesta HTTP
     * @param connection Conexión HTTP establecida
     * @return Contenido del cuerpo de error como String
     */
    public static String readErrorBody(HttpURLConnection connection) {
        StringBuilder errorContent = new StringBuilder();
        try (BufferedReader errorReader = new BufferedReader(
                new InputStreamReader(connection.getErrorStream()))) {
            String line;
            while ((line = errorReader.readLine()) != null) {
                errorContent.append(line).append("\n");
            }
        } catch (IOException | NullPointerException e) {
            return "No se pudo leer el cuerpo del error: " + e.getMessage();
        }
        return errorContent.toString();
    }
}