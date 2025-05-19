package model;
import java.time.LocalDate;

public class Usuario {
    private int id;
    private String nombre;
    private String email;
    private LocalDate fechaNacimiento;
    private boolean esValido;
    private String mensajeError;

    public Usuario(int id, String nombre, String email, LocalDate fechaNacimiento) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.fechaNacimiento = fechaNacimiento;
        this.esValido = true;
        this.mensajeError = "";
    }

    // Getters y setters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public boolean isEsValido() { return esValido; }
    public String getMensajeError() { return mensajeError; }

    public void setEsValido(boolean esValido) { this.esValido = esValido; }
    public void setMensajeError(String mensajeError) { this.mensajeError = mensajeError; }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", nombre='" + nombre + "', email='" + email +
                "', fechaNacimiento=" + fechaNacimiento + "}";
    }
}