package migration.result;

public class MigracionResultado {
    private int insertados;
    private int duplicados;
    private int errores;

    public MigracionResultado() {
        this.insertados = 0;
        this.duplicados = 0;
        this.errores = 0;
    }

    public void incrementarInsertados() { insertados++; }
    public void incrementarDuplicados() { duplicados++; }
    public void incrementarErrores() { errores++; }

    public int getInsertados() { return insertados; }
    public int getDuplicados() { return duplicados; }
    public int getErrores() { return errores; }
}
