package model;

/**
 * Clase que representa la entidad Usuario en el sistema.
 * Corresponde a la capa de modelo en la arquitectura en capas.
 */

import java.sql.Date;
import java.time.LocalDate;

public class UsuarioDay_2 {
    private int id;
    private String nombre;
    private String email;
    private LocalDate fechaNacimiento;
    private String tipoDocumento;
    // Constructores
    public UsuarioDay_2() {}

    public UsuarioDay_2(int id, String nombre, String email, LocalDate fechaNacimiento) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.fechaNacimiento = fechaNacimiento;
    }
    // Constructor actualizado para incluir tipoDocumento
    public UsuarioDay_2(int id, String nombre, String email, LocalDate fechaNacimiento, String tipoDocumento) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.fechaNacimiento = fechaNacimiento;
        this.tipoDocumento = tipoDocumento;
    }

    public UsuarioDay_2(int id, String nombreCompleto, String s, Object o, String tipoDocumento) {
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }
    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    // Getters y setters
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {

        this.fechaNacimiento = fechaNacimiento;
    }

    // Resto de getters y setters...
    // Getters y setters
    //fechaNacimiento


    // Otros atributos
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Usuario [id=" + id + ", nombre=" + nombre + ", email=" + email +
                ", fechaNacimiento=" + fechaNacimiento + "]";
    }
}

