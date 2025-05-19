package services;

import model.UsuarioDay_2;
import model.UsuarioOracle;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UsuarioService {

    public List<UsuarioOracle> obtenerUsuariosOracle(Connection conn) throws SQLException {
        List<UsuarioOracle> usuarios = new ArrayList<>();

        // Consulta modificada: eliminamos TIPO_DOCUMENTO que no existe en la tabla
        String sql = "SELECT ID, NOMBRE, APELLIDO1, APELLIDO2, DNI FROM CIMDATA.USUARIOS_TEST";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                usuarios.add(new UsuarioOracle(
                        rs.getInt("ID"),
                        rs.getString("NOMBRE"),
                        rs.getString("APELLIDO1"),
                        rs.getString("APELLIDO2"),
                        rs.getString("DNI")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar usuarios de Oracle: " + e.getMessage());
            throw e;
        }

        System.out.println("Usuarios obtenidos de Oracle: " + usuarios.size());
        return usuarios;
    }

    /**
     * Obtiene todos los usuarios de la base de datos MySQL.
     *
     * @param conn Conexión a la base de datos
     * @return Lista de usuarios obtenidos
     * @throws SQLException Si ocurre un error al acceder a la base de datos
     */
    public List<UsuarioDay_2> obtenerUsuarios(Connection conn) throws SQLException {
        List<UsuarioDay_2> usuarios = new ArrayList<>();
        String sql = "SELECT id, nombre, email, fecha_nacimiento, tipo_documento FROM usuarios";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String email = rs.getString("email");
                Date fechaNacimiento = rs.getDate("fecha_nacimiento");
                String tipoDocumento = rs.getString("tipo_documento");

                LocalDate fechaNacimientoLocal = fechaNacimiento != null ? fechaNacimiento.toLocalDate() : null;

                usuarios.add(new UsuarioDay_2(id, nombre, email, fechaNacimientoLocal, tipoDocumento));
            }
        }

        System.out.println("Usuarios obtenidos de MySQL: " + usuarios.size());
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

        System.out.println("Usuarios válidos: " + usuariosValidos.size() + " de " + usuarios.size() + " procesados");
        return usuariosValidos;
    }

    /**
     * Valida un usuario individual.
     *
     * @param usuario Usuario a validar
     * @return true si el usuario es válido, false en caso contrario
     */
    private boolean validarUsuario(UsuarioDay_2 usuario) {
        // Modificado para adaptarse a la estructura actual (fecha_nacimiento puede ser null)
        if (usuario.getNombre() == null || usuario.getEmail() == null) {
            System.out.println("Usuario con ID " + usuario.getId() + " rechazado: campos nombre o email nulos");
            return false;
        }

        if (!usuario.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            System.out.println("Usuario con ID " + usuario.getId() + " rechazado: formato de email inválido");
            return false;
        }

        return true;
    }
}