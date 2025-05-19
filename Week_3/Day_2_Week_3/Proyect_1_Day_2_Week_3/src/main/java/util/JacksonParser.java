package util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

/**
 * Implementación de JsonParser que utiliza la biblioteca Jackson.
 */
public class JacksonParser implements JsonParser {

    private final ObjectMapper mapper;

    public JacksonParser() {
        this.mapper = new ObjectMapper();
        // Configuración para ignorar propiedades desconocidas
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public <T> T parseObject(String json, Class<T> clazz) throws IOException {
        return mapper.readValue(json, clazz);
    }

    @Override
    public <T> List<T> parseList(String json, Class<T> clazz) throws IOException {
        // Utilizamos TypeReference para manejar la deserialización de una colección genérica
        return mapper.readValue(json,
                mapper.getTypeFactory().constructCollectionType(List.class, clazz));
    }
}