package services;

import model.UsuarioDay_2;
import model.UsuarioOracle;

public class TransformacionService {

    public UsuarioDay_2 transformarUsuario(UsuarioOracle usuarioOracle) {
        String nombreCompleto = String.format("%s %s %s",
                usuarioOracle.getNombre(),
                usuarioOracle.getApellido1(),
                usuarioOracle.getApellido2()).trim();

        String tipoDocumento = validarDNI(usuarioOracle.getDocumento()) ? "DNI" : "PASAPORTE";

        return new UsuarioDay_2(
                usuarioOracle.getId(),
                nombreCompleto,
                generarEmail(usuarioOracle),
                null, // Fecha de nacimiento no disponible en Oracle
                tipoDocumento
        );
    }

    private boolean validarDNI(String documento) {
        // Implementar lógica de validación de DNI
        return documento != null && documento.matches("\\d{8}[A-Za-z]");
    }

    private String generarEmail(UsuarioOracle usuario) {
        return String.format("%s.%s@empresa.com",
                usuario.getNombre().toLowerCase(),
                usuario.getApellido1().toLowerCase());
    }
}