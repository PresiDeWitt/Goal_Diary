package services;


import migration.result.MigracionResultado;

/**
 * Servicio para mostrar reportes de migración
 */
public class ReporteService {

    /**
     * Muestra un reporte detallado del resultado de la migración
     * @param resultado Objeto con los resultados de la migración
     */
    public void mostrarReporte(MigracionResultado resultado) {
        System.out.println("\n===== REPORTE DE MIGRACIÓN =====");
        System.out.println("Total de usuarios procesados: " + resultado.getTotalProcesados());
        System.out.println("Usuarios insertados: " + resultado.getInsertados());
        System.out.println("Usuarios duplicados: " + resultado.getDuplicados());
        System.out.println("Errores inesperados: " + resultado.getErrores());
        System.out.println("===============================");
    }
}