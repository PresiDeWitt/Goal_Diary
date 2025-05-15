package migration;

import config.DatabaseConfig;
import model.UsuarioDay_2;

import java.sql.Connection;
import migration.result.MigracionResultado;
import services.DatabaseManager;
import services.MigracionService;
import services.ReporteService;
import services.UsuarioService;


import java.util.List;

public class Migracion{
    private final DatabaseConfig configOrigen;
    private final DatabaseConfig configDestino;
    private final UsuarioService usuarioService;
    private final MigracionService migracionService;
    private final ReporteService reporteService;

    public Migracion(DatabaseConfig configOrigen, DatabaseConfig configDestino) {
        this.configOrigen = configOrigen;
        this.configDestino = configDestino;
        this.usuarioService = new UsuarioService();
        this.migracionService = new MigracionService();
        this.reporteService = new ReporteService();
    }

    public void ejecutarMigracion() {
        try {
            // Establecer conexiones
            Connection connOrigen = DatabaseManager.conectar(configOrigen);
            Connection connDestino = DatabaseManager.conectar(configDestino);

            // Obtener y validar usuarios
            List<UsuarioDay_2> usuarios = usuarioService.obtenerUsuarios(connOrigen);
            List<UsuarioDay_2> usuariosValidos = usuarioService.validarUsuarios(usuarios);

            // Realizar migración
            MigracionResultado resultado = migracionService.migrarUsuarios(connDestino, usuariosValidos);

            // Mostrar reporte
            reporteService.mostrarReporte(resultado);

            // Cerrar conexiones
            DatabaseManager.cerrarConexion(connOrigen);
            DatabaseManager.cerrarConexion(connDestino);

        } catch (Exception e) {
            System.err.println("Error en la migración: " + e.getMessage());
            e.printStackTrace();
        }
    }
}