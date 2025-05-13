package Week_2.Day_1.src.main.java.service;


import Week_2.Day_1.src.main.java.dao.IUsuarioDAO;
import Week_2.Day_1.src.main.java.model.Usuario;

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
}