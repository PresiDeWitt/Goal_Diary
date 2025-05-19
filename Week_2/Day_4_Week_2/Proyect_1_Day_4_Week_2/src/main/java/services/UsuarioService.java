package services;

import model.UsuarioDay_2;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.UsuarioOracle;

/**
 * Servicio para operaciones relacionadas con usuarios
 */
public class UsuarioService {

    /**
     * Obtiene una lista de usuarios desde la base de datos fuente
     * @param conn Conexión a la base de datos
     * @return Lista de usuarios obtenida
     * @throws SQLException Si ocurre un error al consultar la base de datos
     */
    public List<UsuarioDay_2> obtenerUsuarios(Connection conn) throws SQLException {
        List<UsuarioDay_2> usuarios = new ArrayList<>();
        String sql = "SELECT id, nombre, email, fecha_nacimiento FROM usuarios";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                UsuarioDay_2 usuario = new UsuarioDay_2();
                usuario.setId(rs.getInt("id"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setEmail(rs.getString("email"));

                java.sql.Date fechaSql = rs.getDate("fecha_nacimiento");
                if (fechaSql != null) {
                    usuario.setFechaNacimiento(fechaSql.toLocalDate());
                }

                usuarios.add(usuario);
            }
        }

        return usuarios;
    }

    /**
     * Valida una lista de usuarios según reglas de negocio
     * @param usuarios Lista de usuarios a validar
     * @return Lista de usuarios que pasaron la validación
     */
    public List<UsuarioDay_2> validarUsuarios(List<UsuarioDay_2> usuarios) {
        List<UsuarioDay_2> usuariosValidos = new ArrayList<>();

        for (UsuarioDay_2 usuario : usuarios) {
            // Reglas de validación básicas
            boolean esValido = true;

            // ID debe ser mayor que cero
            if (usuario.getId() <= 0) {
                System.out.println("Usuario inválido: ID " + usuario.getId() + " debe ser mayor que cero");
                esValido = false;
            }

            // Nombre no puede ser nulo o vacío
            if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
                System.out.println("Usuario inválido: ID " + usuario.getId() + " debe tener un nombre");
                esValido = false;
            }

            // Email debe tener formato válido (verificación simple)
            if (usuario.getEmail() == null || !usuario.getEmail().contains("@")) {
                System.out.println("Usuario inválido: ID " + usuario.getId() + " debe tener un email válido");
                esValido = false;
            }

            // Fecha de nacimiento no puede ser futura
            if (usuario.getFechaNacimiento() != null &&
                    usuario.getFechaNacimiento().isAfter(LocalDate.now())) {
                System.out.println("Usuario inválido: ID " + usuario.getId() + " tiene fecha de nacimiento futura");
                esValido = false;
            }

            if (esValido) {
                usuariosValidos.add(usuario);
            }
        }

        System.out.println("Usuarios validados: " + usuariosValidos.size() + " de " + usuarios.size());
        return usuariosValidos;
    }

    public List<UsuarioOracle> obtenerUsuariosOracle(Connection connOrigen) throws SQLException {
        List<UsuarioOracle> usuarios = new ArrayList<>();

        // Consulta a la tabla USUARIOS_TEST de Oracle
        String sql = "SELECT ID, NOMBRE, APELLIDO1, APELLIDO2, DNI, TIPO_DOCUMENTO FROM CIMDATA.USUARIOS_TEST";

        try (Statement stmt = connOrigen.createStatement();
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
}