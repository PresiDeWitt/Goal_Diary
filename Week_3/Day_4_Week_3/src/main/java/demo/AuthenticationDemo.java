package demo;
/**
 * Demuestra diferentes tipos de autenticación
 */
public class AuthenticationDemo {

    public static void explainAuthenticationTypes() {
        System.out.println("\nEXPLICACIÓN: Tipos de Autenticación\n");
        System.out.println("API KEY:");
        System.out.println("- Clave estática que identifica la aplicación");
        System.out.println("- Se envía en headers personalizados (X-API-Key)");
        System.out.println("- No expira automáticamente");
        System.out.println("- Ejemplo: X-API-Key: sk-1234567890abcdef");

        System.out.println("\nBEARER TOKEN:");
        System.out.println("- Token dinámico obtenido tras autenticación");
        System.out.println("- Se envía en header Authorization");
        System.out.println("- Puede tener fecha de expiración");
        System.out.println("- Ejemplo: Authorization: Bearer eyJhbGciOiJIUzI1NiIs...");

        System.out.println("\nMANEJO DE EXPIRACIÓN:");
        System.out.println("1. Detectar error 401 con mensaje de token expirado");
        System.out.println("2. Usar refresh token para obtener nuevo access token");
        System.out.println("3. Reintentar la petición original con nuevo token");
        System.out.println("4. Implementar renovación automática antes de expiración");
    }

    public static void demonstrateTokenRefresh() {
        System.out.println("\nEJEMPLO: Renovación de Tokens\n");
        String expiredToken = "expired-token-123";
        String refreshToken = "refresh-token-456";
        String newToken = "new-token-789";

        System.out.println("1. Token actual: " + maskToken(expiredToken));
        System.out.println("2. Detectar expiración (401 Unauthorized)");
        System.out.println("3. Usar refresh token: " + maskToken(refreshToken));
        System.out.println("4. Obtener nuevo access token");
        System.out.println("5. Nuevo token: " + maskToken(newToken));
        System.out.println("6. Reintentar petición original");
    }

    private static String maskToken(String token) {
        if (token == null || token.length() <= 8) return "***";
        return token.substring(0, 6) + "***" + token.substring(token.length() - 3);
    }
}