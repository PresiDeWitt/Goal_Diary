package service;

import dao.IUsuarioDAO;
import model.Usuario;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase de servicio que implementa la lógica de negocio relacionada con los usuarios.
 * Actúa como intermediaria entre la capa de acceso a datos y la capa de presentación.
 */
public class UsuarioServicio {

    private final IUsuarioDAO usuarioDAO;

    /**
     * Constructor que recibe la implementación del DAO a utilizar
     *
     * @param usuarioDAO Implementación de IUsuarioDAO
     */
    public UsuarioServicio(IUsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * Método que obtiene y muestra la lista de todos los usuarios
     */
    public void mostrarUsuarios() {
        List<Usuario> usuarios = usuarioDAO.getAllUsuarios();

        if (usuarios.isEmpty()) {
            System.out.println("No se encontraron usuarios en la base de datos.");
            return;
        }

        System.out.println("==== LISTA DE USUARIOS ====");
        for (Usuario usuario : usuarios) {
            System.out.println(usuario);
        }
        System.out.println("==========================");
    }

    /**
     * Método que devuelve la lista de todos los usuarios
     *
     * @return Lista de usuarios
     */
    public List<Usuario> obtenerUsuarios() {
        return usuarioDAO.getAllUsuarios();
    }

    /**
     * Método que limpia y valida los datos de los usuarios
     * - Elimina espacios en blanco en los nombres con trim()
     * - Convierte los correos electrónicos a minúsculas con toLowerCase()
     * - Valida que tanto el nombre como el email no sean null
     */
    public List<Usuario> limpiarYValidarUsuarios() {
        List<Usuario> usuarios = usuarioDAO.getAllUsuarios();
        List<Usuario> usuariosLimpios = new ArrayList<>();

        System.out.println("==== LIMPIEZA Y VALIDACIÓN DE USUARIOS ====");
        for (Usuario usuario : usuarios) {
            boolean esValido = true;

            // Verificar si el nombre es null
            if (usuario.getNombre() == null) {
                System.out.println("Usuario ID " + usuario.getId() + ": INVÁLIDO - Nombre es null");
                esValido = false;
            } else {
                // Limpiar espacios en blanco del nombre
                usuario.setNombre(usuario.getNombre().trim());
            }

            // Verificar si el email es null
            if (usuario.getEmail() == null) {
                System.out.println("Usuario ID " + usuario.getId() + ": INVÁLIDO - Email es null");
                esValido = false;
            } else {
                // Convertir email a minúsculas
                usuario.setEmail(usuario.getEmail().toLowerCase());
            }

            if (esValido) {
                usuariosLimpios.add(usuario);
                System.out.println("Usuario " + usuario.getNombre() + " (" + usuario.getEmail() + "): VÁLIDO");
            }
        }
        System.out.println("==========================================");

        return usuariosLimpios;
    }
}