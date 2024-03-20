package genericOperation;

import java.sql.*;

public class EstablishConnection {  // 该类用于Java与Access数据库建立连接
	
	public static void loadDriver() {
		try {
			Class.forName("com.hxtt.sql.access.AccessDriver");  // 加载JDBC-Access数据库驱动程序
		}
		catch (ClassNotFoundException e) {}
	}
	
	public static Connection connectDatebase(String databasePath) {  // dataPath指定数据库文件路径，调用该静态方法即可建立连接
		Connection con = null;
		try {
			con = DriverManager.getConnection("jdbc:access:/" + databasePath);
		} 
		catch (SQLException e) {}
		return con;
	}
}
