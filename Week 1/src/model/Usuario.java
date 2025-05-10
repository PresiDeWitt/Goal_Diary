package model;

/**
 * Clase modelo que representa un usuario en la base de datos
 */
public class Usuario {
    private int id;
    private String nombre;
    private String email;
    private int edad;

    // Constructor sin ID para nuevos usuarios
    public Usuario(String nombre, String email, int edad) {
        this.nombre = nombre;
        this.email = email;
        this.edad = edad;
    }

    // Constructor completo para usuarios existentes
    public Usuario(int id, String nombre, String email, int edad) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.edad = edad;
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

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    @Override
    public String toString() {
        return "model.Usuario [id=" + id + ", nombre=" + nombre + ", email=" + email + ", edad=" + edad + "]";
    }
}