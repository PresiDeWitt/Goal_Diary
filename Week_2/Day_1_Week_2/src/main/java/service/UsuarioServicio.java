package service;

import dao.IUsuarioDAO;
import model.UsuarioDay_1;

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
        List<UsuarioDay_1> usuarios = usuarioDAO.getAllUsuarios();

        if (usuarios.isEmpty()) {
            System.out.println("No se encontraron usuarios en la base de datos.");
            return;
        }

        System.out.println("==== LISTA DE USUARIOS ====");
        for (UsuarioDay_1 usuario : usuarios) {
            System.out.println(usuario);
        }
        System.out.println("==========================");
    }

    /**
     * Método que devuelve la lista de todos los usuarios
     *
     * @return Lista de usuarios
     */
    public List<UsuarioDay_1> obtenerUsuarios() {
        return usuarioDAO.getAllUsuarios();
    }
}