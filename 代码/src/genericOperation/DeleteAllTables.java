package genericOperation;

import java.sql.*;

public class DeleteAllTables {  // 该类用于删除所有数据表
	
	public static void delete() {
		Connection con;
		Statement stmt;
		try {
			EstablishConnection.loadDriver();
			con=EstablishConnection.connectDatebase("D:/Temp/Database.accdb");  // 调用EstablishConnection类中的connectDatabase静态方法建立连接
			DatabaseMetaData metaData = con.getMetaData();
			ResultSet resultSet = metaData.getTables(null, null, null, new String[]{"TABLE"});  // resultSet包含数据库中所有数据表的名称
	        while (resultSet.next()) {
	        	String tableName = resultSet.getString("TABLE_NAME");
	        	if (tableName.contains(" ") || tableName.contains("/")) {  // 表名中可能含有空格和正斜杠，需要将其格式化，否则SQL语句解析异常
	    			tableName = "[" + tableName + "]";
	    		}
            	String sql = "drop table " + tableName;  // 创建SQL语句删除表
            	stmt = con.createStatement();
                stmt.executeUpdate(sql);  // 执行该语句，数据表被删除
            }
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
}
