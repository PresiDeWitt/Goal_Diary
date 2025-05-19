package service;

import model.Post;
import model.PostConAutor;
import model.Usuario;
import util.JsonParser;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio extendido para gestionar Posts con información de sus autores.
 * Extiende PostService para incluir funcionalidad de usuarios.
 */
public class PostConAutorService extends PostService {

    private final UsuarioService usuarioService;

    /**
     * Constructor que permite inyectar las dependencias.
     *
     * @param apiService Servicio para realizar peticiones HTTP
     * @param jsonParser Parser JSON a utilizar
     * @param usuarioService Servicio para gestionar usuarios
     */
    public PostConAutorService(ApiService apiService, JsonParser jsonParser, UsuarioService usuarioService) {
        super(apiService, jsonParser);
        this.usuarioService = usuarioService;
    }

    /**
     * Obtiene todos los posts con información de sus autores.
     *
     * @return Lista de objetos PostConAutor
     * @throws IOException Si hay errores en la conexión o el parsing
     */
    public List<PostConAutor> getAllPostsConAutor() throws IOException {
        // Obtener posts y usuarios
        List<Post> posts = super.getAllPosts();
        Map<Integer, Usuario> usuariosMap = usuarioService.getUsuariosMap();

        // Combinar posts con sus autores
        return posts.stream()
                .map(post -> {
                    Usuario autor = usuariosMap.get(post.getUserId());
                    return new PostConAutor(post, autor);
                })
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un post específico con información de su autor.
     *
     * @param id ID del post a buscar
     * @return Objeto PostConAutor
     * @throws IOException Si hay errores en la conexión o el parsing
     */
    public PostConAutor getPostConAutorById(int id) throws IOException {
        Post post = super.getPostById(id);
        Usuario autor = usuarioService.getUsuarioById(post.getUserId());
        return new PostConAutor(post, autor);
    }

    /**
     * Imprime todos los posts con autores en un formato legible.
     *
     * @throws IOException Si hay errores en la obtención de datos
     */
    public void displayAllPostsConAutor() throws IOException {
        List<PostConAutor> postsConAutor = getAllPostsConAutor();

        System.out.println("Se han encontrado " + postsConAutor.size() + " publicaciones con autores:");
        System.out.println();

        for (PostConAutor postConAutor : postsConAutor) {
            System.out.println(postConAutor);
        }
    }

    /**
     * Imprime posts filtrados por un autor específico.
     *
     * @param autorId ID del autor por el cual filtrar
     * @throws IOException Si hay errores en la obtención de datos
     */
    public void displayPostsByAutor(int autorId) throws IOException {
        List<PostConAutor> postsConAutor = getAllPostsConAutor();

        List<PostConAutor> postsDelAutor = postsConAutor.stream()
                .filter(post -> post.getUserId() == autorId)
                .collect(Collectors.toList());

        if (postsDelAutor.isEmpty()) {
            System.out.println("No se encontraron posts para el autor con ID: " + autorId);
            return;
        }

        Usuario autor = postsDelAutor.get(0).getAutor();
        System.out.println("Posts del autor: " +
                (autor != null ? autor.getName() : "Usuario ID " + autorId));
        System.out.println();

        for (PostConAutor post : postsDelAutor) {
            System.out.println(post);
        }
    }
}
