package Week_1.src.dao;

import Week_1.src.model.Usuario;

import java.sql.SQLException;
import java.util.List;

/**
 * Interfaz que define las operaciones CRUD para model.Usuario
 */
public interface UsuarioDAO {

    /**
     * Obtiene todos los usuarios de la base de datos
     * @return Lista de objetos model.Usuario
     * @throws SQLException Si ocurre un error en la base de datos
     */
    List<Usuario> getAllUsuarios() throws SQLException;

    /**
     * Obtiene un usuario por su ID
     * @param id El ID del usuario a buscar
     * @return El usuario encontrado o null si no existe
     * @throws SQLException Si ocurre un error en la base de datos
     */
    Usuario getUsuarioById(int id) throws SQLException;

    /**
     * Inserta un nuevo usuario en la base de datos
     * @param usuario El usuario a insertar (sin ID)
     * @return El ID generado para el nuevo usuario
     * @throws SQLException Si ocurre un error en la base de datos
     */
    int insertUsuario(Usuario usuario) throws SQLException;

    /**
     * Actualiza un usuario existente
     * @param usuario El usuario con los datos actualizados
     * @return true si se actualizó correctamente, false si no
     * @throws SQLException Si ocurre un error en la base de datos
     */
    boolean updateUsuario(Usuario usuario) throws SQLException;

    /**
     * Elimina un usuario por su ID
     * @param id El ID del usuario a eliminar
     * @return true si se eliminó correctamente, false si no
     * @throws SQLException Si ocurre un error en la base de datos
     */
    boolean deleteUsuario(int id) throws SQLException;
}