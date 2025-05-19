package services;

import migration.result.MigracionResultado;

public class ReporteService {
    public void mostrarReporte(MigracionResultado resultado) {
        System.out.println("\n===== REPORTE DE MIGRACIÓN =====");
        System.out.println("Usuarios insertados: " + resultado.getInsertados());
        System.out.println("Usuarios duplicados: " + resultado.getDuplicados());
        System.out.println("Errores inesperados: " + resultado.getErrores());
        System.out.println("===============================");
    }
}