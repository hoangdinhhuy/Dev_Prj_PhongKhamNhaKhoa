package connectDB;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Noi DUY NHAT cap Connection cho toan bo ung dung. Doc cau hinh tu /db.properties. */
public class ConnectDB {
    // CFG khai bao TRUOC INSTANCE: static khoi tao theo thu tu khai bao,
    // constructor cua INSTANCE can CFG da san sang.
    private static final Properties CFG = new Properties();
    private static final ConnectDB INSTANCE = new ConnectDB();
    private static Connection con;

    private ConnectDB() {
        try (InputStream is = ConnectDB.class.getResourceAsStream("/db.properties")) {
            if (is == null) {
                throw new RuntimeException(
                        "Thiếu file db.properties trong src/main/resources/ (copy từ db.properties.example).");
            }
            CFG.load(is);
        } catch (IOException ex) {
            throw new RuntimeException("Không đọc được db.properties: " + ex.getMessage(), ex);
        }
    }

    public static ConnectDB getInstance() {
        return INSTANCE;
    }

    public void connect() {
        try {
            if (con == null || con.isClosed()) {
                con = DriverManager.getConnection(
                        CFG.getProperty("db.url"),
                        CFG.getProperty("db.user"),
                        CFG.getProperty("db.password"));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Không kết nối được SQL Server: " + ex.getMessage(), ex);
        }
    }

    public Connection getConnection() {
        connect();
        return con;
    }
}
