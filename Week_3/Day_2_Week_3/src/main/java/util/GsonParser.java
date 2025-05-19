package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Implementación de JsonParser que utiliza la biblioteca Gson de Google.
 */
public class GsonParser implements JsonParser {

    private final Gson gson;

    public GsonParser() {
        // Configuración básica de Gson
        this.gson = new GsonBuilder()
                .setLenient()  // Ser más tolerante con JSON mal formado
                .create();
    }

    @Override
    public <T> T parseObject(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    @Override
    public <T> List<T> parseList(String json, Class<T> clazz) {
        // Utilizamos TypeToken para manejar la deserialización de una colección genérica
        Type listType = TypeToken.getParameterized(List.class, clazz).getType();
        return gson.fromJson(json, listType);
    }
}