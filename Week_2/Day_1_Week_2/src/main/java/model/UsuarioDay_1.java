package model;

/**
 * Clase que representa la entidad Usuario en el sistema.
 * Corresponde a la capa de modelo en la arquitectura en capas.
 */
public class UsuarioDay_1 {
    private int id;
    private String nombre;
    private String email;

    /**
     * Constructor vacío
     */
    public UsuarioDay_1() {
    }

    /**
     * Constructor con todos los campos
     *
     * @param id Identificador único del usuario
     * @param nombre Nombre del usuario
     * @param email Correo electrónico del usuario
     */
    public UsuarioDay_1(int id, String nombre, String email) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
    }

    // Getters y setters

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
        return "Usuario [id=" + id + ", nombre=" + nombre + ", email=" + email + "]";
    }
}