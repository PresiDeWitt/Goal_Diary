package Week_1.src.app;

import Week_1.src.config.DatabaseConnection;
import Week_1.src.dao.UsuarioDAO;
import Week_1.src.dao.UsuarioDAOImpl;
import Week_1.src.model.Usuario;
import Week_1.src.utils.Validaciones;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Aplicación de consola para gestionar usuarios en la base de datos
 */
public class UsuarioApp {

    private final UsuarioDAO usuarioDAO;
    private final Scanner scanner;

    public UsuarioApp() {
        this.usuarioDAO = new UsuarioDAOImpl();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Muestra el menú principal y procesa la opción seleccionada
     */
    public void mostrarMenu() {
        int opcion;
        do {
            System.out.println("\n===== GESTIÓN DE USUARIOS =====");
            System.out.println("1. Listar todos los usuarios");
            System.out.println("2. Buscar usuario por ID");
            System.out.println("3. Añadir nuevo usuario");
            System.out.println("4. Actualizar usuario");
            System.out.println("5. Eliminar usuario");
            System.out.println("6. Salir");
            System.out.print("\nSeleccione una opción: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine());
                procesarOpcion(opcion);
            } catch (NumberFormatException e) {
                System.out.println("Por favor, introduzca un número válido.");
                opcion = 0;
            }
        } while (opcion != 6);
    }

    /**
     * Procesa la opción seleccionada del menú
     * @param opcion Número de opción seleccionada
     */
    private void procesarOpcion(int opcion) {
        try {
            switch (opcion) {
                case 1:
                    listarUsuarios();
                    break;
                case 2:
                    buscarUsuarioPorId();
                    break;
                case 3:
                    agregarUsuario();
                    break;
                case 4:
                    actualizarUsuario();
                    break;
                case 5:
                    eliminarUsuario();
                    break;
                case 6:
                    System.out.println("¡Hasta pronto!");
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        } catch (SQLException e) {
            System.err.println("Error en la operación: " + e.getMessage());
        }
    }

    /**
     * Lista todos los usuarios de la base de datos
     * @throws SQLException Si ocurre un error en la base de datos
     */
    private void listarUsuarios() throws SQLException {
        List<Usuario> usuarios = usuarioDAO.getAllUsuarios();

        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }

        System.out.println("\n--- LISTA DE USUARIOS ---");
        for (Usuario usuario : usuarios) {
            System.out.println(usuario);
        }
    }

    /**
     * Busca y muestra un usuario por su ID
     * @throws SQLException Si ocurre un error en la base de datos
     */
    private void buscarUsuarioPorId() throws SQLException {
        System.out.print("Introduzca el ID del usuario: ");
        String idStr = scanner.nextLine();

        if (!Validaciones.validarEdadString(idStr)) {
            System.out.println("Por favor, introduzca un ID válido (número entero positivo).");
            return;
        }

        int id = Integer.parseInt(idStr);
        Usuario usuario = usuarioDAO.getUsuarioById(id);

        if (usuario != null) {
            System.out.println("\n--- USUARIO ENCONTRADO ---");
            System.out.println(usuario);
        } else {
            System.out.println("No se encontró ningún usuario con ID " + id);
        }
    }

    /**
     * Añade un nuevo usuario a la base de datos
     * @throws SQLException Si ocurre un error en la base de datos
     */
    private void agregarUsuario() throws SQLException {
        System.out.println("\n--- AÑADIR NUEVO USUARIO ---");

        // Validar nombre
        String nombre;
        do {
            System.out.print("Nombre: ");
            nombre = scanner.nextLine();
            String error = Validaciones.mensajeErrorNombre(nombre);
            if (error != null) {
                System.out.println(error);
            } else {
                break;
            }
        } while (true);

        // Validar email
        String email;
        do {
            System.out.print("Email: ");
            email = scanner.nextLine();
            String error = Validaciones.mensajeErrorEmail(email);
            if (error != null) {
                System.out.println(error);
            } else {
                break;
            }
        } while (true);

        // Validar edad
        int edad;
        do {
            System.out.print("Edad: ");
            String edadStr = scanner.nextLine();
            String error = Validaciones.mensajeErrorEdad(edadStr);
            if (error != null) {
                System.out.println(error);
            } else {
                edad = Integer.parseInt(edadStr);
                break;
            }
        } while (true);

        Usuario nuevoUsuario = new Usuario(nombre, email, edad);
        int id = usuarioDAO.insertUsuario(nuevoUsuario);

        if (id > 0) {
            System.out.println("Usuario añadido correctamente con ID: " + id);
        } else {
            System.out.println("No se pudo añadir el usuario.");
        }
    }

    /**
     * Actualiza los datos de un usuario existente
     * @throws SQLException Si ocurre un error en la base de datos
     */
    private void actualizarUsuario() throws SQLException {
        System.out.print("Introduzca el ID del usuario a actualizar: ");
        String idStr = scanner.nextLine();

        if (!Validaciones.validarEdadString(idStr)) {
            System.out.println("Por favor, introduzca un ID válido (número entero positivo).");
            return;
        }

        int id = Integer.parseInt(idStr);
        Usuario usuario = usuarioDAO.getUsuarioById(id);

        if (usuario == null) {
            System.out.println("No se encontró ningún usuario con ID " + id);
            return;
        }

        System.out.println("\n--- ACTUALIZAR USUARIO ---");
        System.out.println("Datos actuales: " + usuario);

        // Actualizar nombre
        System.out.print("Nuevo nombre (" + usuario.getNombre() + "): ");
        String nombre = scanner.nextLine();
        if (!nombre.isEmpty()) {
            String error = Validaciones.mensajeErrorNombre(nombre);
            if (error != null) {
                System.out.println(error + ". Se mantiene el nombre actual.");
            } else {
                usuario.setNombre(nombre);
            }
        }

        // Actualizar email
        System.out.print("Nuevo email (" + usuario.getEmail() + "): ");
        String email = scanner.nextLine();
        if (!email.isEmpty()) {
            String error = Validaciones.mensajeErrorEmail(email);
            if (error != null) {
                System.out.println(error + ". Se mantiene el email actual.");
            } else {
                usuario.setEmail(email);
            }
        }

        // Actualizar edad
        System.out.print("Nueva edad (" + usuario.getEdad() + "): ");
        String edadStr = scanner.nextLine();
        if (!edadStr.isEmpty()) {
            String error = Validaciones.mensajeErrorEdad(edadStr);
            if (error != null) {
                System.out.println(error + ". Se mantiene la edad actual.");
            } else {
                usuario.setEdad(Integer.parseInt(edadStr));
            }
        }

        boolean actualizado = usuarioDAO.updateUsuario(usuario);
        if (actualizado) {
            System.out.println("Usuario actualizado correctamente.");
        } else {
            System.out.println("No se pudo actualizar el usuario.");
        }
    }

    /**
     * Elimina un usuario de la base de datos
     * @throws SQLException Si ocurre un error en la base de datos
     */
    private void eliminarUsuario() throws SQLException {
        System.out.print("Introduzca el ID del usuario a eliminar: ");
        String idStr = scanner.nextLine();

        if (!Validaciones.validarEdadString(idStr)) {
            System.out.println("Por favor, introduzca un ID válido (número entero positivo).");
            return;
        }

        int id = Integer.parseInt(idStr);

        // Verificar si el usuario existe
        Usuario usuario = usuarioDAO.getUsuarioById(id);
        if (usuario == null) {
            System.out.println("No se encontró ningún usuario con ID " + id);
            return;
        }

        System.out.println("¿Está seguro de que desea eliminar a " + usuario.getNombre() + "? (S/N): ");
        String confirmacion = scanner.nextLine();

        if (confirmacion.equalsIgnoreCase("S")) {
            boolean eliminado = usuarioDAO.deleteUsuario(id);
            if (eliminado) {
                System.out.println("Usuario eliminado correctamente.");
            } else {
                System.out.println("No se pudo eliminar el usuario.");
            }
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    /**
     * Metodo principal para iniciar la aplicación
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        DatabaseConnection dbConnection = new DatabaseConnection();

        System.out.println("Probando conexión a la base de datos...");
        if (dbConnection.testConnection()) {
            System.out.println("¡Conexión exitosa a la base de datos!");
            UsuarioApp app = new UsuarioApp();
            app.mostrarMenu();
        } else {
            System.err.println("No se pudo conectar a la base de datos. Verifique la configuración.");
        }
    }
}