package services;

import model.UsuarioDay_2;
import model.UsuarioOracle;
import java.util.Locale;

/**
 * Servicio para transformar usuarios de la estructura Oracle a la estructura MySQL
 */
public class TransformacionService {

    private static final String DOMINIO_EMAIL = "empresa.com";
    private static final String FORMATO_DNI = "\\d{8}[A-Za-z]";
    private static final int LONGITUD_MAXIMA_EMAIL = 100;

    /**
     * Transforma un usuario de Oracle a la estructura de MySQL
     * @param usuarioOracle Usuario en formato Oracle
     * @return Usuario transformado en formato MySQL
     */
    public UsuarioDay_2 transformarUsuario(UsuarioOracle usuarioOracle) {
        if (usuarioOracle == null) {
            throw new IllegalArgumentException("El usuario Oracle no puede ser nulo");
        }

        return new UsuarioDay_2(
                usuarioOracle.getId(),
                construirNombreCompleto(usuarioOracle),
                generarEmail(usuarioOracle),
                usuarioOracle.getFechaNacimiento(),
                determinarTipoDocumento(usuarioOracle.getDocumento())
        );
    }

    /**
     * Construye el nombre completo a partir de los componentes del nombre
     */
    private String construirNombreCompleto(UsuarioOracle usuario) {
        String nombre = usuario.getNombre() != null ? usuario.getNombre().trim() : "";
        String apellido1 = usuario.getApellido1() != null ? usuario.getApellido1().trim() : "";
        String apellido2 = usuario.getApellido2() != null ? usuario.getApellido2().trim() : "";

        return String.join(" ", nombre, apellido1, apellido2).trim();
    }

    /**
     * Determina el tipo de documento basado en su formato
     */
    private String determinarTipoDocumento(String documento) {
        if (documento == null || documento.trim().isEmpty()) {
            return null;
        }
        return validarDNI(documento) ? "DNI" : "PASAPORTE";
    }

    /**
     * Valida si un documento tiene formato de DNI español
     */
    private boolean validarDNI(String documento) {
        return documento != null && documento.matches(FORMATO_DNI);
    }

    /**
     * Genera un email corporativo basado en el nombre y apellido
     */
    private String generarEmail(UsuarioOracle usuario) {
        String nombreBase = normalizarTexto(usuario.getNombre());
        String apellidoBase = normalizarTexto(usuario.getApellido1());

        // Si no hay nombre o apellido válido, usar el ID
        if (nombreBase.isEmpty() && apellidoBase.isEmpty()) {
            return String.format("usuario.%d@%s", usuario.getId(), DOMINIO_EMAIL);
        }

        String email = String.format("%s.%s@%s", nombreBase, apellidoBase, DOMINIO_EMAIL);

        // Asegurar que no exceda la longitud máxima
        if (email.length() > LONGITUD_MAXIMA_EMAIL) {
            email = email.substring(0, LONGITUD_MAXIMA_EMAIL);
        }

        return email;
    }

    /**
     * Normaliza texto para usarlo en emails (elimina acentos, caracteres especiales)
     */
    private String normalizarTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return "";
        }

        // Convertir a minúsculas y normalizar caracteres
        String normalizado = texto.toLowerCase(Locale.US)
                .replaceAll("[^a-z0-9]", "")  // Eliminar caracteres no alfanuméricos
                .replaceAll("[áàäâ]", "a")
                .replaceAll("[éèëê]", "e")
                .replaceAll("[íìïî]", "i")
                .replaceAll("[óòöô]", "o")
                .replaceAll("[úùüû]", "u")
                .replaceAll("ñ", "n");

        return normalizado.isEmpty() ? "user" : normalizado;
    }
}