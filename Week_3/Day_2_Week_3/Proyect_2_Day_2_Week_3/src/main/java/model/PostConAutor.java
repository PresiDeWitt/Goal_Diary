package model;

/**
 * Clase extendida de Post que incluye la información del autor.
 * Hereda todos los atributos y métodos de Post y añade funcionalidad de usuario.
 */
public class PostConAutor extends Post {

    private Usuario autor;

    // Constructor vacío necesario para la deserialización
    public PostConAutor() {
        super();
    }

    // Constructor que acepta un Post y un Usuario
    public PostConAutor(Post post, Usuario autor) {
        super(post.getUserId(), post.getId(), post.getTitle(), post.getBody());
        this.autor = autor;
    }

    // Constructor completo
    public PostConAutor(int userId, int id, String title, String body, Usuario autor) {
        super(userId, id, title, body);
        this.autor = autor;
    }

    // Getter y Setter para el autor
    public Usuario getAutor() {
        return autor;
    }

    public void setAutor(Usuario autor) {
        this.autor = autor;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Post #").append(getId()).append(":\n");
        sb.append("Título: ").append(getTitle()).append("\n");
        sb.append("Cuerpo: ").append(getBody()).append("\n");

        if (autor != null) {
            sb.append("Autor: ").append(autor.getName());
            sb.append(" (").append(autor.getEmail()).append(")");
        } else {
            sb.append("Autor: Usuario no encontrado");
        }

        sb.append("\n-----");
        return sb.toString();
    }
}