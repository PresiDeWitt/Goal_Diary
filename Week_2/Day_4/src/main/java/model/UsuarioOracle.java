package model;

import lombok.Data;
@Data
public class UsuarioOracle {
    // Getters...

    private int id;
    private String nombre;
    private String apellido1;
    private String apellido2;
    private String documento;
    private String tipoDocumento;

    // Constructor, getters y setters
    public UsuarioOracle(int id, String nombre, String apellido1, String apellido2,
                         String documento, String tipoDocumento) {
        this.id = id;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.documento = documento;
        this.tipoDocumento = tipoDocumento;
    }
}