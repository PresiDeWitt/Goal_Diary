package etl.Day_3.src.main.java.app;

import etl.Day_3.src.main.java.validator.UsuarioValidator;
import model.Usuario;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== VALIDACIÓN DE USUARIOS ===");

        // Crear lista de usuarios de prueba con fechas de nacimiento como String
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.add(new Usuario(1, "Juan", "juan@example.com",
                UsuarioValidator.parseFecha("1990-05-15")));
        usuarios.add(new Usuario(2, "María", "maria@example.com",
                UsuarioValidator.parseFecha("2010-08-20")));
        usuarios.add(new Usuario(3, "Pedro", "pedro@malformado",
                UsuarioValidator.parseFecha("1985-11-30")));
        usuarios.add(new Usuario(4, "Ana", "ana@example.com",
                UsuarioValidator.parseFecha("2005-03-10")));
        usuarios.add(new Usuario(5, "Carlos", "carlos@example.com",
                UsuarioValidator.parseFecha("1995-12-25")));

        System.out.println("\n=== RESULTADOS DE VALIDACIÓN ===");
        List<Usuario> usuariosValidos = new ArrayList<>();

        for (Usuario usuario : usuarios) {
            if (UsuarioValidator.validarUsuario(usuario)) {
                usuariosValidos.add(usuario);
            }
        }

        System.out.println("\n=== RESUMEN ===");
        System.out.println("Total usuarios procesados: " + usuarios.size());
        System.out.println("Usuarios válidos: " + usuariosValidos.size());
        System.out.println("Usuarios rechazados: " + (usuarios.size() - usuariosValidos.size()));
    }

}