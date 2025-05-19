package util;

import java.io.IOException;
import java.util.List;

/**
 * Interfaz genérica para parsear JSON a objetos Java.
 * Permite implementaciones intercambiables de diferentes bibliotecas.
 */
public interface JsonParser {

    /**
     * Convierte un String JSON en un objeto de tipo T
     *
     * @param <T> Tipo de objeto a crear
     * @param json String con formato JSON
     * @param clazz Clase del tipo a deserializar
     * @return Objeto del tipo especificado
     * @throws IOException Si hay errores en el proceso de deserialización
     */
    <T> T parseObject(String json, Class<T> clazz) throws IOException;

    /**
     * Convierte un String JSON en una lista de objetos de tipo T
     *
     * @param <T> Tipo de objeto a crear
     * @param json String con formato JSON
     * @param clazz Clase del tipo a deserializar
     * @return Lista de objetos del tipo especificado
     * @throws IOException Si hay errores en el proceso de deserialización
     */
    <T> List<T> parseList(String json, Class<T> clazz) throws IOException;
}