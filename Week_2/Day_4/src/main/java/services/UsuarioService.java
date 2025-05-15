package services;

import model.UsuarioDay_2;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para operaciones relacionadas con usuarios.
 * Proporciona métodos para obtener y validar usuarios desde una base de datos.
 */
public class UsuarioService {

    /**
     * Obtiene todos los usuarios desde la base de datos.
     *
     * @param conn Conexión a la base de datos
     * @return Lista de usuarios
     * @throws SQLException Si ocurre un error al acceder a la base de datos
     */
    public List<UsuarioDay_2> obtenerUsuarios(Connection conn) throws SQLException {
        List<UsuarioDay_2> usuarios = new ArrayList<>();
        String sql = "SELECT id, nombre, email, fecha_nacimiento FROM usuarios";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String email = rs.getString("email");
                Date fechaNacimiento = rs.getDate("fecha_nacimiento");
                LocalDate fechaNacimientoLocal = fechaNacimiento != null ? fechaNacimiento.toLocalDate() : null;

                usuarios.add(new UsuarioDay_2(id, nombre, email, fechaNacimientoLocal));
            }
        }

        System.out.println("Usuarios obtenidos: " + usuarios.size());
        return usuarios;
    }

    /**
     * Filtra una lista de usuarios, devolviendo solo los que son válidos.
     *
     * @param usuarios Lista de usuarios a validar
     * @return Lista de usuarios válidos
     */
    public List<UsuarioDay_2> validarUsuarios(List<UsuarioDay_2> usuarios) {
        List<UsuarioDay_2> usuariosValidos = new ArrayList<>();

        for (UsuarioDay_2 usuario : usuarios) {
            if (validarUsuario(usuario)) {
                usuariosValidos.add(usuario);
            }
        }

        System.out.println("Usuarios válidos: " + usuariosValidos.size());
        return usuariosValidos;
    }

    /**
     * Valida un usuario individual.
     *
     * @param usuario Usuario a validar
     * @return true si el usuario es válido, false en caso contrario
     */
    private boolean validarUsuario(UsuarioDay_2 usuario) {
        if (usuario.getNombre() == null || usuario.getEmail() == null || usuario.getFechaNacimiento() == null) {
            System.out.println("Usuario con ID " + usuario.getId() + " rechazado: campos nulos");
            return false;
        }

        if (!usuario.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            System.out.println("Usuario con ID " + usuario.getId() + " rechazado: formato de email inválido");
            return false;
        }

        return true;
    }
}