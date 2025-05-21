package app;

import client.HttpClient;
import model.HttpResponseModel;
import view.HttpResponsePrinter;

import java.util.Scanner;

public class HttpExplorer {

    private static final String DEFAULT_URL = "https://jsonplaceholder.typicode.com/posts";

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Bienvenido al Explorador HTTP básico");
            System.out.println("URL por defecto: " + DEFAULT_URL);
            System.out.print("¿Desea utilizar otra URL? (s/n): ");

            String response = scanner.nextLine().trim().toLowerCase();
            String urlToConnect = DEFAULT_URL;

            if (response.equals("s")) {
                System.out.print("Introduzca la nueva URL: ");
                urlToConnect = scanner.nextLine().trim();
            }

            HttpResponseModel httpResponse = HttpClient.makeHttpRequest(urlToConnect);
            HttpResponsePrinter.displayHttpResponse(httpResponse);
            scanner.close();
        } catch (Exception e) {
            System.err.println("Error principal: " + e.getMessage());
            e.printStackTrace();
        }
    }
}