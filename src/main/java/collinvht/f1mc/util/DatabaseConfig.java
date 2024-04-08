package collinvht.f1mc.util;

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

@Getter
public class DatabaseConfig {
    private final String host;
    private final int port;
    private final String database;
    private final String user;
    private final String password;

    public DatabaseConfig(String host, int port, String database, String user, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    public static DatabaseConfig fromYaml(YamlConfiguration databaseCFG) {
        String host = databaseCFG.getString("host");
        int port = databaseCFG.getInt("port");
        String database = databaseCFG.getString("database");
        String user = databaseCFG.getString("user");
        String password = databaseCFG.getString("password");
        return new DatabaseConfig(host,port,database,user,password);
    }
}
