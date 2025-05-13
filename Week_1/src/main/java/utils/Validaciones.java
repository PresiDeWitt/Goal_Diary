package utils;

import java.util.regex.Pattern;

/**
 * Clase de utilidad para validar campos de entrada de usuario
 */
public class Validaciones {

    // Patrón para validar email según RFC 5322
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"
    );

    // Constantes para validación de edad
    private static final int EDAD_MINIMA = 0;
    private static final int EDAD_MAXIMA = 120;

    // Longitudes para validación del nombre
    private static final int NOMBRE_LONGITUD_MINIMA = 2;
    private static final int NOMBRE_LONGITUD_MAXIMA = 50;

    /**
     * Valida que el nombre cumpla con los requisitos
     * @param nombre Nombre a validar
     * @return true si el nombre es válido, false si no lo es
     */
    public static boolean validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }

        // Verificar longitud del nombre
        int longitud = nombre.trim().length();
        if (longitud < NOMBRE_LONGITUD_MINIMA || longitud > NOMBRE_LONGITUD_MAXIMA) {
            return false;
        }

        // Verificar que solo contiene caracteres válidos (letras, espacios y algunos caracteres especiales)
        return nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s'-]+$");
    }

    /**
     * Devuelve un mensaje explicando por qué el nombre no es válido
     * @param nombre Nombre a validar
     * @return Mensaje de error o null si el nombre es válido
     */
    public static String mensajeErrorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "El nombre no puede estar vacío";
        }

        int longitud = nombre.trim().length();
        if (longitud < NOMBRE_LONGITUD_MINIMA) {
            return "El nombre debe tener al menos " + NOMBRE_LONGITUD_MINIMA + " caracteres";
        }

        if (longitud > NOMBRE_LONGITUD_MAXIMA) {
            return "El nombre no puede tener más de " + NOMBRE_LONGITUD_MAXIMA + " caracteres";
        }

        if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s'-]+$")) {
            return "El nombre solo puede contener letras, espacios, apóstrofes y guiones";
        }

        return null; // El nombre es válido
    }

    /**
     * Valida que el email cumpla con el formato estándar
     * @param email Email a validar
     * @return true si el email es válido, false si no lo es
     */
    public static boolean validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Devuelve un mensaje explicando por qué el email no es válido
     * @param email Email a validar
     * @return Mensaje de error o null si el email es válido
     */
    public static String mensajeErrorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "El email no puede estar vacío";
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return "El formato del email no es válido";
        }

        return null; // El email es válido
    }

    /**
     * Valida que la edad esté dentro de un rango aceptable
     * @param edad Edad a validar
     * @return true si la edad es válida, false si no lo es
     */
    public static boolean validarEdad(int edad) {
        return (edad >= EDAD_MINIMA && edad <= EDAD_MAXIMA);
    }

    /**
     * Intenta convertir un string a entero y valida que sea una edad válida
     * @param edadStr String que representa la edad
     * @return true si se puede convertir a un entero y es una edad válida
     */
    public static boolean validarEdadString(String edadStr) {
        try {
            int edad = Integer.parseInt(edadStr);
            return validarEdad(edad);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Devuelve un mensaje explicando por qué la edad no es válida
     * @param edadStr String que representa la edad
     * @return Mensaje de error o null si la edad es válida
     */
    public static String mensajeErrorEdad(String edadStr) {
        if (edadStr == null || edadStr.trim().isEmpty()) {
            return "La edad no puede estar vacía";
        }

        try {
            int edad = Integer.parseInt(edadStr);

            if (edad < EDAD_MINIMA) {
                return "La edad no puede ser negativa";
            }

            if (edad > EDAD_MAXIMA) {
                return "La edad no puede ser mayor a " + EDAD_MAXIMA + " años";
            }

            return null; // La edad es válida
        } catch (NumberFormatException e) {
            return "La edad debe ser un número entero";
        }
    }

    /**
     * Valida todos los campos de un usuario
     * @param nombre El nombre del usuario
     * @param email El email del usuario
     * @param edadStr La edad como string
     * @return Un mensaje de error o null si todos los campos son válidos
     */
    public static String validarTodosLosCampos(String nombre, String email, String edadStr) {
        String errorNombre = mensajeErrorNombre(nombre);
        if (errorNombre != null) {
            return errorNombre;
        }

        String errorEmail = mensajeErrorEmail(email);
        if (errorEmail != null) {
            return errorEmail;
        }

        String errorEdad = mensajeErrorEdad(edadStr);
        if (errorEdad != null) {
            return errorEdad;
        }

        return null; // Todos los campos son válidos
    }
}