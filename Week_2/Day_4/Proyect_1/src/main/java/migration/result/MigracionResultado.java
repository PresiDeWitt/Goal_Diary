package migration.result;

/**
 * Clase que representa el resultado de un proceso de migración de datos.
 * Mantiene estadísticas de registros insertados, duplicados y errores.
 */
public class MigracionResultado {
    private int insertados;
    private int duplicados;
    private int errores;

    /**
     * Constructor que inicializa todos los contadores a cero.
     */
    public MigracionResultado() {
        this.insertados = 0;
        this.duplicados = 0;
        this.errores = 0;
    }

    /**
     * Incrementa el contador de registros insertados correctamente.
     */
    public void incrementarInsertados() {
        insertados++;
    }

    /**
     * Incrementa el contador de registros duplicados (no insertados por ya existir).
     */
    public void incrementarDuplicados() {
        duplicados++;
    }

    /**
     * Incrementa el contador de errores inesperados durante la migración.
     */
    public void incrementarErrores() {
        errores++;
    }

    /**
     * Obtiene el número de registros insertados correctamente.
     * @return Número de insertados (siempre >= 0)
     */
    public int getInsertados() {
        return Math.max(insertados, 0);
    }

    /**
     * Obtiene el número de registros duplicados encontrados.
     * @return Número de duplicados (siempre >= 0)
     */
    public int getDuplicados() {
        return Math.max(duplicados, 0);
    }

    /**
     * Obtiene el número de errores inesperados durante la migración.
     * @return Número de errores (siempre >= 0)
     */
    public int getErrores() {
        return Math.max(errores, 0);
    }

    /**
     * Obtiene el total de registros procesados (insertados + duplicados + errores).
     * @return Total de registros procesados
     */
    public int getTotalProcesados() {
        return getInsertados() + getDuplicados() + getErrores();
    }

    /**
     * Obtiene el porcentaje de éxito de la migración.
     * @return Porcentaje de registros insertados correctamente (0-100)
     */
    public double getPorcentajeExito() {
        if (getTotalProcesados() == 0) return 0;
        return (getInsertados() * 100.0) / getTotalProcesados();
    }

    /**
     * Combina este resultado con otro resultado de migración.
     * @param otro Resultado a combinar
     * @return Este objeto con los valores acumulados
     */
    public MigracionResultado combinar(MigracionResultado otro) {
        if (otro != null) {
            this.insertados += otro.getInsertados();
            this.duplicados += otro.getDuplicados();
            this.errores += otro.getErrores();
        }
        return this;
    }

    /**
     * Reinicia todos los contadores a cero.
     */
    public void reiniciar() {
        this.insertados = 0;
        this.duplicados = 0;
        this.errores = 0;
    }

    /**
     * Representación textual del resultado de migración.
     * @return String con el resumen de la migración
     */
    @Override
    public String toString() {
        return String.format(
                "Migración: %d procesados | %d insertados (%.1f%%) | %d duplicados | %d errores",
                getTotalProcesados(),
                getInsertados(),
                getPorcentajeExito(),
                getDuplicados(),
                getErrores()
        );
    }
}