package Week_2.src.app;


import dao.IUsuarioDAO;
import dao.UsuarioDAO;
import service.UsuarioServicio;

/**
 * Clase principal que contiene el método main para probar la implementación del patrón DAO
 * y la estructura en capas del proyecto.
 */
public class Main {

    public static void main(String[] args) {
        try {
            System.out.println("Iniciando aplicación...");

            // Crear instancia del DAO
            IUsuarioDAO usuarioDAO = new UsuarioDAO();

            // Crear instancia del servicio pasándole el DAO
            UsuarioServicio usuarioServicio = new UsuarioServicio(usuarioDAO);

            // Utilizar el servicio para mostrar los usuarios
            usuarioServicio.mostrarUsuarios();

            System.out.println("Aplicación finalizada correctamente.");
        } catch (Exception e) {
            System.err.println("Error en la aplicación: " + e.getMessage());
            e.printStackTrace();
        }
    }
}