package dao;

import model.UsuarioDay_2;

import java.util.List;

/**
 * Interfaz que define las operaciones de acceso a datos para la entidad Usuario.
 */
public interface IUsuarioDAO {
    /**
     * Obtiene todos los usuarios de la base de datos
     * @return Lista de usuarios
     */
    List<UsuarioDay_2> getAllUsuarios();

    /**
     * Obtiene un usuario por su ID
     * @param id ID del usuario a buscar
     * @return Usuario encontrado o null si no existe
     */
    UsuarioDay_2 getUsuarioById(int id);

    /**
     * Inserta un nuevo usuario en la base de datos
     * @param usuario Usuario a insertar
     * @return ID generado o -1 si hubo error
     */
    int insertUsuario(UsuarioDay_2 usuario);

    /**
     * Actualiza un usuario existente
     * @param usuario Usuario con los datos actualizados
     * @return Número de filas afectadas
     */
    int updateUsuario(UsuarioDay_2 usuario);

    /**
     * Elimina un usuario por su ID
     * @param id ID del usuario a eliminar
     * @return true si se eliminó, false en caso contrario
     */
    boolean deleteUsuario(int id);
}