package services;

import okhttp3.Response;
import okhttp3.Headers;

public class Encabezado {
    public static void printEncabezadp(Response response) {
        System.out.println("\nENCABEZADOS DE RESPUESTA:");
        Headers headers = response.headers();
        for (String name : headers.names()) {
            System.out.println(name + ": " + headers.get(name));
        }
    }
}
