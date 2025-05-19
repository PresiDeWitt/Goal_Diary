package com.jsonreader.service;

import com.jsonreader.model.Post;
import com.jsonreader.util.JsonParser;

import java.io.IOException;
import java.util.List;

/**
 * Servicio para gestionar la obtención y procesamiento de Posts.
 */
public class PostService {

    private static final String API_URL = "https://jsonplaceholder.typicode.com/posts";

    private final ApiService apiService;
    private final JsonParser jsonParser;

    /**
     * Constructor que permite inyectar las dependencias.
     *
     * @param apiService Servicio para realizar peticiones HTTP
     * @param jsonParser Parser JSON a utilizar
     */
    public PostService(ApiService apiService, JsonParser jsonParser) {
        this.apiService = apiService;
        this.jsonParser = jsonParser;
    }

    /**
     * Obtiene todos los posts desde la API.
     *
     * @return Lista de objetos Post
     * @throws IOException Si hay errores en la conexión o el parsing
     */
    public List<Post> getAllPosts() throws IOException {
        // Obtener respuesta como String
        String jsonResponse = apiService.get(API_URL);

        // Convertir a lista de Posts
        return jsonParser.parseList(jsonResponse, Post.class);
    }

    /**
     * Obtiene un post específico por su ID.
     *
     * @param id ID del post a buscar
     * @return Objeto Post
     * @throws IOException Si hay errores en la conexión o el parsing
     */
    public Post getPostById(int id) throws IOException {
        String postUrl = API_URL + "/" + id;
        String jsonResponse = apiService.get(postUrl);

        return jsonParser.parseObject(jsonResponse, Post.class);
    }

    /**
     * Imprime todos los posts en un formato legible.
     *
     * @throws IOException Si hay errores en la obtención de datos
     */
    public void displayAllPosts() throws IOException {
        List<Post> posts = getAllPosts();

        System.out.println("Se han encontrado " + posts.size() + " publicaciones:");
        System.out.println();

        for (Post post : posts) {
            System.out.println(post);
        }
    }
}