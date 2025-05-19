package app;

import config.DatabaseConfig;
import migration.result.MigracionResultado;
import model.UsuarioDay_2;
import services.MigracionService;
import services.ReporteService;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Configuración para base de datos destino (MySQL/MariaDB)
        DatabaseConfig configDestino = new DatabaseConfig("mysql_destino");

        Connection conn = null;
        try {
            // Establecer conexión con la base de datos destino
            conn = DatabaseManager.conectar(configDestino);

            // Crear lista de usuarios de prueba para simular datos ya transformados y validados
            List<UsuarioDay_2> usuariosValidos = crearUsuariosPrueba();

            // Insertar en la base de datos destino
            MigracionService migracionService = new MigracionService();
            MigracionResultado resultado = migracionService.migrarUsuarios(conn, usuariosValidos);

            // Generar reporte
            ReporteService reporteService = new ReporteService();
            reporteService.mostrarReporte(resultado);

        } catch (SQLException e) {
            System.err.println("Error en la conexión a la base de datos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Cerrar la conexión
            DatabaseManager.cerrarConexion(conn);
        }
    }

    /**
     * Crea una lista de usuarios de prueba para la demostración.
     * En un caso real, estos datos vendrían de la base de datos origen.
     */
    private static List<UsuarioDay_2> crearUsuariosPrueba() {
        List<UsuarioDay_2> usuarios = new ArrayList<>();

        // Crear algunos usuarios de ejemplo (simula datos ya transformados y validados)
        usuarios.add(new UsuarioDay_2(1, "Ana López García", "ana.lopez@empresa.com",
                LocalDate.of(1990, 5, 15), "DNI"));

        usuarios.add(new UsuarioDay_2(2, "Carlos Martínez Ruiz", "carlos.martinez@empresa.com",
                LocalDate.of(1985, 10, 22), "DNI"));

        usuarios.add(new UsuarioDay_2(3, "María Sánchez Pérez", "maria.sanchez@empresa.com",
                LocalDate.of(1992, 3, 8), "PASAPORTE"));

        usuarios.add(new UsuarioDay_2(4, "Javier Rodríguez Gómez", "javier.rodriguez@empresa.com",
                LocalDate.of(1988, 7, 30), "DNI"));

        // Usuario duplicado para demostrar el manejo de errores (supongamos que ya existe en la BD)
        usuarios.add(new UsuarioDay_2(1, "Ana López García (Duplicado)", "ana.lopez@empresa.com",
                LocalDate.of(1990, 5, 15), "DNI"));

        System.out.println("Usuarios de prueba creados: " + usuarios.size());
        return usuarios;
    }
}