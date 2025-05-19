package view;

import model.HttpResponseModel;

import java.util.List;
import java.util.Map;

public class HttpResponsePrinter {
    /**
     * Muestra la respuesta HTTP de forma ordenada
     * @param response Objeto HttpResponseModel a mostrar
     */
    public static void displayHttpResponse(HttpResponseModel response) {
        System.out.println("\n======= RESPUESTA HTTP =======");

        // Muestra información sobre excepciones si las hay
        if (response.getException() != null) {
            System.out.println("ERROR: " + response.getException());
            return;
        }

        // Muestra el código de estado
        System.out.println("Código de estado: " + response.getStatusCode());

        // Muestra los headers
        System.out.println("\n--- HEADERS ---");
        Map<String, List<String>> headers = response.getHeaders();
        if (headers != null) {
            headers.forEach((key, values) -> {
                if (key != null) {
                    System.out.println(key + ": " + String.join(", ", values));
                }
            });
        }

        // Muestra el cuerpo de la respuesta
        System.out.println("\n--- CUERPO DE LA RESPUESTA ---");
        String body = response.getBody();
        if (body != null && !body.isEmpty()) {
            System.out.println(body);
        } else {
            System.out.println("El cuerpo de la respuesta está vacío");
        }

        System.out.println("=============================");
    }
}