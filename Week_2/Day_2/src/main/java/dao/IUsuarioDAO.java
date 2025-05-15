package dao;

import model.UsuarioDay_2;
import java.util.List;

/**
 * Interfaz para el acceso a datos de usuarios
 */
public interface IUsuarioDAO {

    /**
     * Obtiene todos los usuarios de la base de datos
     * @return Lista de usuarios
     */
    List<UsuarioDay_2> getAllUsuarios();

    /**
     * Obtiene un usuario por su ID
     * @param id ID del usuario
     * @return Usuario encontrado o null si no existe
     */
    UsuarioDay_2 getUsuarioById(int id);

    /**
     * Inserta un nuevo usuario en la base de datos
     *
     * @param usuario Usuario a insertar
     * @return true si se insertó correctamente, false en caso contrario
     */
    int insertUsuario(UsuarioDay_2 usuario);

    /**
     * Actualiza un usuario existente en la base de datos
     *
     * @param usuario Usuario con los datos actualizados
     * @return true si se actualizó correctamente, false en caso contrario
     */
    int updateUsuario(UsuarioDay_2 usuario);

    /**
     * Elimina un usuario de la base de datos
     * @param id ID del usuario a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    boolean deleteUsuario(int id);
}