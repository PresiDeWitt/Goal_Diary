package service;

import model.Usuario;
import util.JsonParser;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar la obtención y procesamiento de Usuarios.
 * Extiende las funcionalidades para trabajar con el endpoint de usuarios.
 */
public class UsuarioService {

    private static final String USERS_API_URL = "https://jsonplaceholder.typicode.com/users";

    private final ApiService apiService;
    private final JsonParser jsonParser;

    /**
     * Constructor que permite inyectar las dependencias.
     *
     * @param apiService Servicio para realizar peticiones HTTP
     * @param jsonParser Parser JSON a utilizar
     */
    public UsuarioService(ApiService apiService, JsonParser jsonParser) {
        this.apiService = apiService;
        this.jsonParser = jsonParser;
    }

    /**
     * Obtiene todos los usuarios desde la API.
     *
     * @return Lista de objetos Usuario
     * @throws IOException Si hay errores en la conexión o el parsing
     */
    public List<Usuario> getAllUsuarios() throws IOException {
        String jsonResponse = apiService.get(USERS_API_URL);
        return jsonParser.parseList(jsonResponse, Usuario.class);
    }

    /**
     * Obtiene un usuario específico por su ID.
     *
     * @param id ID del usuario a buscar
     * @return Objeto Usuario
     * @throws IOException Si hay errores en la conexión o el parsing
     */
    public Usuario getUsuarioById(int id) throws IOException {
        String userUrl = USERS_API_URL + "/" + id;
        String jsonResponse = apiService.get(userUrl);
        return jsonParser.parseObject(jsonResponse, Usuario.class);
    }

    /**
     * Obtiene un mapa de usuarios indexado por ID para búsquedas rápidas.
     *
     * @return Map con ID como clave y Usuario como valor
     * @throws IOException Si hay errores en la obtención de datos
     */
    public Map<Integer, Usuario> getUsuariosMap() throws IOException {
        List<Usuario> usuarios = getAllUsuarios();
        return usuarios.stream()
                .collect(Collectors.toMap(Usuario::getId, usuario -> usuario));
    }

    /**
     * Imprime todos los usuarios en un formato legible.
     *
     * @throws IOException Si hay errores en la obtención de datos
     */
    public void displayAllUsuarios() throws IOException {
        List<Usuario> usuarios = getAllUsuarios();

        System.out.println("Se han encontrado " + usuarios.size() + " usuarios:");
        System.out.println();

        for (Usuario usuario : usuarios) {
            System.out.println(usuario);
        }
    }
}