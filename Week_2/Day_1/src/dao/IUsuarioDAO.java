package Week_2.Day_1.src.dao;


import Week_2.Day_1.src.model.Usuario;

import java.util.List;

/**
 * Interfaz que define el contrato para las operaciones de acceso a datos
 * relacionadas con la entidad Usuario.
 */
public interface IUsuarioDAO {

    /**
     * Obtiene todos los usuarios de la base de datos.
     *
     * @return Lista de todos los usuarios encontrados
     */
    List<Usuario> getAllUsuarios();
}