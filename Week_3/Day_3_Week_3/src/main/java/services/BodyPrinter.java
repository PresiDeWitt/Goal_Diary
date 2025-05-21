package services;

import okhttp3.Response;
import java.io.IOException;

public class BodyPrinter {
    public static void printBody(Response response) {
        System.out.println("\nCUERPO DE LA RESPUESTA (JSON):");
        try {
            System.out.println(response.body().string());
        } catch (IOException e) {
            System.err.println("Error al leer el cuerpo de la respuesta: " + e.getMessage());
        }
    }
}
