package top.bearcabbage.syncsignnotice;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 数据库配置类
 */
@Environment(EnvType.CLIENT)
public class DatabaseConfig {
    public String host;
    public String user;
    public String password;
    public String database;
    public int port;
    
    public DatabaseConfig(String host, String user, String password, String database, int port) {
        this.host = host;
        this.user = user;
        this.password = password;
        this.database = database;
        this.port = port;
    }
}
