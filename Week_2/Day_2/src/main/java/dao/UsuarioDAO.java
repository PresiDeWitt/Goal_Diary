package dao;

import config.DatabaseConnection;
import model.UsuarioDay_2;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de IUsuarioDAO que utiliza JDBC para acceder a la base de datos MySQL.
 */
public class UsuarioDAO implements IUsuarioDAO {

    private static final String TABLE_NAME = "usuarios";
    private static final String DB_CONFIG = "mysql_origen"; // Verificar que esto coincide con database.properties

    // Método auxiliar para mapear ResultSet a UsuarioDay_2
    private UsuarioDay_2 mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Date fechaNacimientoSQL = rs.getDate("fecha_nacimiento");
        LocalDate fechaNacimiento = (fechaNacimientoSQL != null) ? fechaNacimientoSQL.toLocalDate() : null;

        return new UsuarioDay_2(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("email"),
                fechaNacimiento,
                rs.getString("tipo_documento")
        );
    }

    // Método auxiliar para establecer parámetros en PreparedStatement
    private void setUsuarioParameters(PreparedStatement stmt, UsuarioDay_2 usuario) throws SQLException {
        stmt.setString(1, usuario.getNombre());
        stmt.setString(2, usuario.getEmail());

        if (usuario.getFechaNacimiento() != null) {
            stmt.setDate(3, Date.valueOf(usuario.getFechaNacimiento()));
        } else {
            stmt.setNull(3, Types.DATE);
        }

        stmt.setString(4, usuario.getTipoDocumento());
    }

    @Override
    public List<UsuarioDay_2> getAllUsuarios() {
        List<UsuarioDay_2> usuarios = new ArrayList<>();
        String query = String.format("SELECT id, nombre, email, fecha_nacimiento, tipo_documento FROM %s", TABLE_NAME);

        try (Connection conn = DatabaseConnection.getConnection(DB_CONFIG);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en getAllUsuarios: " + e.getMessage());
            e.printStackTrace();
        }
        return usuarios;
    }

    @Override
    public UsuarioDay_2 getUsuarioById(int id) {
        String query = String.format("SELECT id, nombre, email, fecha_nacimiento, tipo_documento FROM %s WHERE id = ?", TABLE_NAME);

        try (Connection conn = DatabaseConnection.getConnection(DB_CONFIG);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en getUsuarioById para ID " + id + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int insertUsuario(UsuarioDay_2 usuario) {
        String query = String.format(
                "INSERT INTO %s (nombre, email, fecha_nacimiento, tipo_documento) VALUES (?, ?, ?, ?)",
                TABLE_NAME
        );

        try (Connection conn = DatabaseConnection.getConnection(DB_CONFIG);
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            setUsuarioParameters(stmt, usuario);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("No se insertó ningún registro.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("No se obtuvo el ID generado.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en insertUsuario: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int updateUsuario(UsuarioDay_2 usuario) {
        String query = String.format(
                "UPDATE %s SET nombre = ?, email = ?, fecha_nacimiento = ?, tipo_documento = ? WHERE id = ?",
                TABLE_NAME
        );

        try (Connection conn = DatabaseConnection.getConnection(DB_CONFIG);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            setUsuarioParameters(stmt, usuario);
            stmt.setInt(5, usuario.getId());

            return stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error en updateUsuario para ID " + usuario.getId() + ": " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public boolean deleteUsuario(int id) {
        String query = String.format("DELETE FROM %s WHERE id = ?", TABLE_NAME);

        try (Connection conn = DatabaseConnection.getConnection(DB_CONFIG);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en deleteUsuario para ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}