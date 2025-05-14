package etl.Day_3.src.main.java.validator;

import model.Usuario;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class UsuarioValidator {
    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static boolean validarUsuario(Usuario usuario) {
        // Validar email
        if (!validarEmail(usuario.getEmail())) {
            System.out.println("Usuario " + usuario.getNombre() + " rechazado: Email inválido");
            return false;
        }

        // Validar edad (18+ años)
        if (!validarEdad(usuario.getFechaNacimiento())) {
            System.out.println("Usuario " + usuario.getNombre() + " rechazado: Menor de 18 años");
            return false;
        }

        System.out.println("Usuario " + usuario.getNombre() + " aceptado. Edad: " +
                calcularEdad(usuario.getFechaNacimiento()) + " años");
        return true;
    }

    public static boolean validarEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean validarEdad(LocalDate fechaNacimiento) {
        return fechaNacimiento != null && calcularEdad(fechaNacimiento) >= 18;
    }

    public static int calcularEdad(LocalDate fechaNacimiento) {
        LocalDate hoy = LocalDate.now();
        return Period.between(fechaNacimiento, hoy).getYears();
    }

    public static LocalDate parseFecha(String fechaStr) {
        try {
            return LocalDate.parse(fechaStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            System.err.println("Error al parsear fecha: " + fechaStr + " - Formato esperado: yyyy-MM-dd");
            return null;
        }
    }
}