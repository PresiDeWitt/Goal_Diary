package validator;

import config.DatabaseConnection;
import model.UsuarioDay_2;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase para leer usuarios de la base de datos y validarlos
 */
public class UsuarioValidatorDB {

    /**
     * Lee todos los usuarios de la base de datos y los valida
     * @return Lista de resultados de validación
     */
    public List<ResultadoValidacion> leerYValidarUsuarios() {
        List<ResultadoValidacion> resultados = new ArrayList<>();
        String sql = "SELECT id, nombre, email, fecha_nacimiento FROM usuarios";

        try (Connection conn = DatabaseConnection.getConnection("mysql");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("Conectado a la base de datos. Leyendo usuarios...");

            while (rs.next()) {
                // Extraer datos del resultado
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String email = rs.getString("email");
                LocalDate fechaNacimiento = null;

                // Manejar posibles valores nulos en fecha_nacimiento
                Date fechaSQL = rs.getDate("fecha_nacimiento");
                if (fechaSQL != null) {
                    fechaNacimiento = fechaSQL.toLocalDate();
                }

                // Crear usuario
                UsuarioDay_2 usuario = new UsuarioDay_2(id, nombre, email, fechaNacimiento);

                // Silenciar la salida por consola durante la validación
                boolean esValido = silenciarYValidar(usuario);
                String motivo = "";

                // Si no es válido, determinar la razón
                if (!esValido) {
                    if (!UsuarioValidator.validarEmail(email)) {
                        motivo = "Email inválido";
                    } else if (fechaNacimiento != null && !UsuarioValidator.validarEdad(fechaNacimiento)) {
                        motivo = "Menor de 18 años";
                    } else if (fechaNacimiento == null) {
                        motivo = "Fecha de nacimiento no válida";
                    } else {
                        motivo = "Error de validación desconocido";
                    }
                }

                // Crear resultado de validación y añadirlo incluso si no es válido
                ResultadoValidacion resultado = new ResultadoValidacion(usuario, esValido, motivo);
                resultados.add(resultado);
            }

        } catch (SQLException e) {
            System.err.println("Error al leer usuarios de la base de datos: " + e.getMessage());
        }

        return resultados;
    }

    /**
     * Valida un usuario sin imprimir mensajes en consola
     * @param usuario Usuario a validar
     * @return Resultado de la validación
     */
    private boolean silenciarYValidar(UsuarioDay_2 usuario) {
        boolean emailValido = UsuarioValidator.validarEmail(usuario.getEmail());
        boolean edadValida = UsuarioValidator.validarEdad(usuario.getFechaNacimiento());
        return emailValido && edadValida;
    }

    /**
     * Genera un reporte detallado de la validación de usuarios
     */
    public void generarReporteValidacion() {
        List<ResultadoValidacion> resultados = leerYValidarUsuarios();

        if (resultados.isEmpty()) {
            System.out.println("No hay usuarios en la base de datos para validar.");
            return;
        }

        System.out.println("\n===== REPORTE DE VALIDACIÓN DE USUARIOS =====");
        System.out.println("Total de usuarios en base de datos: " + resultados.size());

        int usuariosValidos = 0;
        int usuariosInvalidos = 0;

        System.out.println("\n----- DETALLE DE VALIDACIÓN -----");
        for (ResultadoValidacion resultado : resultados) {
            UsuarioDay_2 u = resultado.getUsuario();
            if (resultado.isValido()) {
                usuariosValidos++;
                System.out.println("VÁLIDO - ID: " + u.getId() + ", Nombre: " + u.getNombre() +
                        ", Email: " + u.getEmail() + ", Edad: " +
                        (u.getFechaNacimiento() != null ? UsuarioValidator.calcularEdad(u.getFechaNacimiento()) : "N/A") +
                        " años");
            } else {
                usuariosInvalidos++;
                System.out.println("INVÁLIDO - ID: " + u.getId() + ", Nombre: " + u.getNombre() +
                        ", Email: " + u.getEmail() + ", Fecha Nac.: " +
                        (u.getFechaNacimiento() != null ? u.getFechaNacimiento() : "N/A") +
                        " - Motivo: " + resultado.getMotivo());
            }
        }

        System.out.println("\n----- RESUMEN -----");
        System.out.println("Usuarios válidos: " + usuariosValidos + " (" +
                (resultados.size() > 0 ? (usuariosValidos * 100 / resultados.size()) : 0) + "%)");
        System.out.println("Usuarios inválidos: " + usuariosInvalidos + " (" +
                (resultados.size() > 0 ? (usuariosInvalidos * 100 / resultados.size()) : 0) + "%)");
    }

    /**
     * Clase interna para almacenar el resultado de una validación
     */
    public static class ResultadoValidacion {
        private UsuarioDay_2 usuario;
        private boolean valido;
        private String motivo;

        public ResultadoValidacion(UsuarioDay_2 usuario, boolean valido, String motivo) {
            this.usuario = usuario;
            this.valido = valido;
            this.motivo = motivo;
        }

        public UsuarioDay_2 getUsuario() {
            return usuario;
        }

        public boolean isValido() {
            return valido;
        }

        public String getMotivo() {
            return motivo;
        }
    }
}