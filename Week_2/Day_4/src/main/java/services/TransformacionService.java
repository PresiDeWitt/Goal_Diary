package services;

import model.UsuarioDay_2;
import model.UsuarioOracle;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TransformacionService {

    public UsuarioDay_2 transformarUsuario(UsuarioOracle usuarioOracle) {
        String nombreCompleto = String.format("%s %s %s",
                usuarioOracle.getNombre(),
                usuarioOracle.getApellido1(),
                usuarioOracle.getApellido2()).trim();

        // Determinar el tipo de documento basado en el formato de DNI
        String tipoDocumento = null;
        if (usuarioOracle.getDocumento() != null) {
            tipoDocumento = validarDNI(usuarioOracle.getDocumento()) ? "DNI" : "PASAPORTE";
        }

        // Generar el email basado en nombre y apellido
        String email = generarEmail(usuarioOracle);

        // Crear y devolver el usuario transformado
        return new UsuarioDay_2(
                usuarioOracle.getId(),
                nombreCompleto,
                email,
                null,  // Fecha de nacimiento no disponible en la transformación
                tipoDocumento
        );
    }

    private boolean validarDNI(String documento) {
        // Implementar lógica de validación de DNI
        // Un DNI español tiene formato: 8 dígitos + 1 letra
        return documento != null && documento.matches("\\d{8}[A-Za-z]");
    }

    private String generarEmail(UsuarioOracle usuario) {
        // Asegurarse de que el nombre y apellido no sean nulos
        String nombre = usuario.getNombre() != null ? usuario.getNombre().toLowerCase() : "";
        String apellido = usuario.getApellido1() != null ? usuario.getApellido1().toLowerCase() : "";

        // Eliminar espacios y caracteres especiales
        nombre = nombre.replaceAll("[^a-z]", "");
        apellido = apellido.replaceAll("[^a-z]", "");

        // Si alguno está vacío después de la limpieza, usar un valor por defecto
        if (nombre.isEmpty()) nombre = "usuario";
        if (apellido.isEmpty()) apellido = "apellido";

        return String.format("%s.%s@empresa.com", nombre, apellido);
    }
}