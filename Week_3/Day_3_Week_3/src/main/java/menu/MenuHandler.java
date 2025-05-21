package menu;

import okhttp3.Response;
import java.util.Scanner;

import static extra.EjercicoExtra.realizarPeticionConGson;
import static services.HttpRequester.hacerPeticion;
import static services.Encabezado.printEncabezadp;
import static services.BodyPrinter.printBody;
;

public class MenuHandler {

    public static void iniciarMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            mostrarOpciones();
            int option = leerOpcion(scanner);

            switch (option) {
                case 1:
                    System.out.println("\nRealizando petición HTTP...");
                    Response response = hacerPeticion();
                    if (response != null) {
                        System.out.println("Código de estado: " + response.code());
                        printEncabezadp(response);
                        printBody(response);
                    }
                    break;
                case 2:
                    System.out.println("\nEjecutando ejercicio [Extra] con deserialización...");
                    realizarPeticionConGson();
                    break;
                case 3:
                    System.out.println("Saliendo...");
                    exit = true;
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }

        scanner.close();
    }

    private static void mostrarOpciones() {
        System.out.println("\nBienvenido al Explorador HTTP básico");
        System.out.println("Elige una opción:");
        System.out.println("1. Realizar una petición HTTP");
        System.out.println("2. Ejercicio [Extra]");
        System.out.println("3. Salir");
        System.out.print("Opción: ");
    }

    private static int leerOpcion(Scanner scanner) {
        int option = -1;
        try {
            option = scanner.nextInt();
            scanner.nextLine(); // limpiar salto
        } catch (Exception e) {
            System.out.println("Entrada inválida.");
            scanner.nextLine(); // limpiar entrada incorrecta
        }
        return option;
    }
}
