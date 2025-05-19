package model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

/**
 * Clase modelo que representa una publicación de la API JSONPlaceholder.
 * Incluye anotaciones para compatibilidad con Gson y Jackson.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Post {

    @SerializedName("userId")
    @JsonProperty("userId")
    private int userId;

    @SerializedName("id")
    @JsonProperty("id")
    private int id;

    @SerializedName("title")
    @JsonProperty("title")
    private String title;

    @SerializedName("body")
    @JsonProperty("body")
    private String body;

    // Constructor vacío necesario para la deserialización
    public Post() {
    }

    // Constructor completo
    public Post(int userId, int id, String title, String body) {
        this.userId = userId;
        this.id = id;
        this.title = title;
        this.body = body;
    }

    // Getters y Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title != null ? title : "Sin título";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body != null ? body : "Sin contenido";
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Post #" + id + ":\n" +
                "Título: " + getTitle() + "\n" +
                "Cuerpo: " + getBody() + "\n" +
                "-----";
    }
}