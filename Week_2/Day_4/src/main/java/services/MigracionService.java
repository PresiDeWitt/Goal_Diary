package services;

import model.UsuarioDay_2;
import migration.result.MigracionResultado;

import java.sql.*;
import java.util.List;

/**
 * Servicio para migrar usuarios a una base de datos.
 */
public class MigracionService {

    // MySQL duplicate key error code
    private static final int DUPLICATE_KEY_ERROR_CODE = 1062;

    /**
     * Migra una lista de usuarios a la base de datos.
     *
     * @param conn     Conexión a la base de datos (no se cierra en este método)
     * @param usuarios Lista de usuarios a migrar
     * @return Resultado de la migración con conteos de operaciones
     * @throws SQLException Si ocurre un error grave de base de datos
     */
    public MigracionResultado migrarUsuarios(Connection conn, List<UsuarioDay_2> usuarios) throws SQLException {
        MigracionResultado resultado = new MigracionResultado();
        String sql = "INSERT INTO usuarios (id, nombre, email, fecha_nacimiento) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (UsuarioDay_2 usuario : usuarios) {
                try {
                    pstmt.setInt(1, usuario.getId());
                    pstmt.setString(2, usuario.getNombre());
                    pstmt.setString(3, usuario.getEmail());

                    // Convert LocalDate to java.sql.Date
                    pstmt.setDate(4, usuario.getFechaNacimiento() != null ?
                            Date.valueOf(usuario.getFechaNacimiento()) : null);

                    pstmt.executeUpdate();
                    resultado.incrementarInsertados();

                } catch (SQLIntegrityConstraintViolationException e) {
                    if (e.getErrorCode() == DUPLICATE_KEY_ERROR_CODE) {
                        System.out.println("Usuario con ID " + usuario.getId() +
                                " no insertado: ya existe en la base de datos.");
                        resultado.incrementarDuplicados();
                    } else {
                        resultado.incrementarErrores();
                        System.err.println("Error de integridad al insertar usuario ID " +
                                usuario.getId() + ": " + e.getMessage());
                    }
                } catch (SQLException e) {
                    resultado.incrementarErrores();
                    System.err.println("Error al insertar usuario ID " + usuario.getId() +
                            ": " + e.getMessage());
                }
            }
        }

        return resultado;
    }
}