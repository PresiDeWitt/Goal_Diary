package migration;

import app.DatabaseManager;
import config.DatabaseConfig;
import migration.result.MigracionResultado;
import model.UsuarioDay_2;
import model.UsuarioOracle;
import services.MigracionService;
import services.ReporteService;
import services.TransformacionService;

import services.UsuarioService; // Si el paquete es completo

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class Migracion {
    private final DatabaseConfig configOrigen;
    private final DatabaseConfig configDestino;
    private final UsuarioService usuarioService;
    private final MigracionService migracionService;
    private final ReporteService reporteService;
    private final TransformacionService transformacionService;

    public Migracion(DatabaseConfig configOrigen, DatabaseConfig configDestino) {
        this.configOrigen = configOrigen;
        this.configDestino = configDestino;
        this.usuarioService = new UsuarioService(); // Ahora apunta al de Proyect_1
        this.migracionService = new MigracionService();
        this.reporteService = new ReporteService();
        this.transformacionService = new TransformacionService();
    }

    public void ejecutarMigracion() {
        try (Connection connOrigen = DatabaseManager.conectar(configOrigen);
             Connection connDestino = DatabaseManager.conectar(configDestino)) {

            // 1. Obtener usuarios de Oracle
            List<UsuarioOracle> usuariosOracle = usuarioService.obtenerUsuariosOracle(connOrigen);

            // 2. Transformar a estructura MySQL
            List<UsuarioDay_2> usuariosTransformados = new ArrayList<>();
            for (UsuarioOracle usuarioOracle : usuariosOracle) {
                usuariosTransformados.add(transformacionService.transformarUsuario(usuarioOracle));
            }

            // 3. Validar usuarios
            List<UsuarioDay_2> usuariosValidos = usuarioService.validarUsuarios(usuariosTransformados);

            // 4. Migrar a MySQL
            MigracionResultado resultado = migracionService.migrarUsuarios(connDestino, usuariosValidos);

            // 5. Reporte
            reporteService.mostrarReporte(resultado);

        } catch (Exception e) {
            System.err.println("Error en la migración: " + e.getMessage());
            e.printStackTrace();
        }
    }
}