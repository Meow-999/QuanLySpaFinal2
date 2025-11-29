package Data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class DataConnection {
    // Sử dụng thư mục hiện tại (nơi chứa file EXE)
    private static final String APP_DIR = System.getProperty("user.dir");
    private static final String DB_DIR = APP_DIR + File.separator + "database";
    private static final String DB_FILE_NAME = "DB_SPA.accdb";
    private static final String DB_PATH = DB_DIR + File.separator + DB_FILE_NAME;
    private static final String DB_URL = "jdbc:ucanaccess://" + DB_PATH;
    
    // Đường dẫn cho bill và thongke trong thư mục app
    private static final String BILL_PATH = APP_DIR + File.separator + "bill";
    private static final String THONGKE_PATH = APP_DIR + File.separator + "thongke";

    private static void initializeDatabase() {
        try {
            // Tạo các thư mục cần thiết
            createDirectories();
            
            File dbFile = new File(DB_PATH);
            if (!dbFile.exists()) {
                System.out.println("📁 Đang copy database từ resources...");
                
                // Copy database từ resources (trong JAR) ra thư mục bên ngoài
                copyDatabaseFromResources();
            } else {
                System.out.println("✅ Database đã tồn tại tại: " + DB_PATH);
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi khởi tạo database: " + e.getMessage());
            createEmptyDatabase();
        }
    }

    private static void createDirectories() {
        try {
            // Tạo thư mục database
            File dbDir = new File(DB_DIR);
            if (!dbDir.exists()) {
                dbDir.mkdirs();
                System.out.println("✅ Đã tạo thư mục database: " + DB_DIR);
            }
            
            // Tạo thư mục bill
            File billDir = new File(BILL_PATH);
            if (!billDir.exists()) {
                billDir.mkdirs();
                System.out.println("✅ Đã tạo thư mục bill: " + BILL_PATH);
            }
            
            // Tạo thư mục thongke
            File thongkeDir = new File(THONGKE_PATH);
            if (!thongkeDir.exists()) {
                thongkeDir.mkdirs();
                System.out.println("✅ Đã tạo thư mục thongke: " + THONGKE_PATH);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tạo thư mục: " + e.getMessage());
        }
    }

    private static void copyDatabaseFromResources() {
        try {
            // Sử dụng ClassLoader để lấy resource từ JAR
            InputStream inputStream = DataConnection.class.getClassLoader()
                    .getResourceAsStream("database/" + DB_FILE_NAME);
            
            if (inputStream != null) {
                File dbFile = new File(DB_PATH);
                Files.copy(inputStream, dbFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("✅ Đã copy database đến: " + DB_PATH);
            } else {
                System.err.println("❌ Không tìm thấy database trong resources, tạo database trống...");
                createEmptyDatabase();
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi copy database từ resources: " + e.getMessage());
            createEmptyDatabase();
        }
    }

    private static void createEmptyDatabase() {
        try {
            // Tạo database Access trống
            String emptyDbUrl = "jdbc:ucanaccess://" + DB_PATH + ";newdatabaseversion=V2016";
            Connection conn = DriverManager.getConnection(emptyDbUrl, "", "");
            conn.close();
            System.out.println("✅ Đã tạo database trống tại: " + DB_PATH);
        } catch (Exception e) {
            System.err.println("❌ Lỗi tạo database trống: " + e.getMessage());
        }
    }

    public static Connection getConnection() {
        Connection conn = null;
        try {
            initializeDatabase();
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            
            File dbFile = new File(DB_PATH);
            if (!dbFile.exists()) {
                System.err.println("❌ File database không tồn tại");
                return null;
            }
            
            conn = DriverManager.getConnection(DB_URL, "", "");
            System.out.println("✅ Kết nối Access thành công! Database: " + DB_PATH);
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("❌ Lỗi kết nối Access: " + e.getMessage());
        }
        return conn;
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
            }
        }
    }
    
    // Thêm phương thức để lấy đường dẫn các thư mục
    public static String getBillPath() {
        return BILL_PATH + File.separator;
    }
    
    public static String getThongkePath() {
        return THONGKE_PATH + File.separator;
    }
    
    public static String getDatabasePath() {
        return DB_DIR + File.separator;
    }
    
    public static String getAppPath() {
        return APP_DIR + File.separator;
    }
}