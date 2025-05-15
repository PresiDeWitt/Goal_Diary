package config;

public class DatabaseConfig {
    public static final String DEFAULT_DB_URL = "jdbc:mysql://localhost:3306/tl_database";
    public static final String DEFAULT_DB_USER = "root";
    public static final String DEFAULT_DB_PASSWORD = "Alejandro2004#";
    public static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    private String dbUrl;
    private String dbUser;
    private String dbPassword;

    public DatabaseConfig() {
        this(DEFAULT_DB_URL, DEFAULT_DB_USER, DEFAULT_DB_PASSWORD);
    }

    public DatabaseConfig(String dbUrl, String dbUser, String dbPassword) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    // Getters - consistent naming
    public String getDbUrl() { return dbUrl; }
    public String getDbUser() { return dbUser; }
    public String getDbPassword() { return dbPassword; }
}