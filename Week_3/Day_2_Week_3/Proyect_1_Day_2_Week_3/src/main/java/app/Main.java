package app;


import service.ApiService;
import service.PostService;
import util.GsonParser;
import util.JacksonParser;
import util.JsonParser;

import java.io.IOException;
import java.util.Scanner;

/**
 * Clase principal que inicia la aplicación.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== Lector de JSON de JSONPlaceholder ===");
        System.out.println("Seleccione la biblioteca para parsear JSON:");
        System.out.println("1. Gson (Google)");
        System.out.println("2. Jackson (FasterXML)");

        Scanner scanner = new Scanner(System.in);
        int option = getOption(scanner, 1, 2);

        // Crear el parser adecuado según la selección
        JsonParser jsonParser;
        if (option == 1) {
            jsonParser = new GsonParser();
            System.out.println("Utilizando Gson para el parsing de JSON");
        } else {
            jsonParser = new JacksonParser();
            System.out.println("Utilizando Jackson para el parsing de JSON");
        }

        // Crear servicios
        ApiService apiService = new ApiService();
        PostService postService = new PostService(apiService, jsonParser);

        try {
            // Mostrar todos los posts
            System.out.println("\nObteniendo posts desde la API...\n");
            postService.displayAllPosts();

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Obtiene una opción válida del usuario.
     *
     * @param scanner Scanner para leer la entrada
     * @param min Valor mínimo aceptable
     * @param max Valor máximo aceptable
     * @return Opción seleccionada
     */
    private static int getOption(Scanner scanner, int min, int max) {
        int option = -1;

        while (option < min || option > max) {
            System.out.print("Ingrese su opción (" + min + "-" + max + "): ");

            try {
                option = Integer.parseInt(scanner.nextLine().trim());

                if (option < min || option > max) {
                    System.out.println("Opción no válida. Intente de nuevo.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
            }
        }

        return option;
    }
}