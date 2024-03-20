package genericOperation;

import java.sql.*;

public class GenericCreateTable {  // 该类用于创建数据表
	
	private String tableName, formatTableName;  // tableName是表名，formatTableName指格式化后的表名（表名中如果有空格等字符，需要将表名放在中括号，否则SQL语句无法正常解析）
	private String[] infoNames;  // infoNames指数据表的字段名称
	
	public GenericCreateTable(String tableName, String[] infoNames){  // 以表名和字段名来构造GenericCreateTable实例
		this.tableName=tableName;
		formatTableName = tableName;
		if (tableName.contains(" ") || tableName.contains("/")) {  // 表名中可能含有空格和正斜杠，需要将其格式化，否则SQL语句解析异常
			formatTableName = "[" + tableName + "]";
		}
		this.infoNames=infoNames;
	}
	
	public boolean initialTable() {  // 创建数据表，如果表已存在，则会返回false，否则返回true
		Connection con;
		Statement statement;
		StringBuffer formatInfoNames=new StringBuffer();
		try {
			EstablishConnection.loadDriver();
			con=EstablishConnection.connectDatebase("D:/Temp/Database.accdb");  // 调用EstablishConnection类中的connectDatabase静态方法建立连接
			DatabaseMetaData metaData = con.getMetaData();
			ResultSet rs = metaData.getTables(null, null, tableName, null);
            if (rs.next()) {  // 说明表已存在，不会进行任何操作
            	return false;
            }
            statement = con.createStatement();
            for (String infoName: infoNames) {  // 遍历infoNames中的每一个字段名构造SQL语句，与表名一样，字段名也需要格式化
            	if (infoName.contains(" ") || infoName.contains("/")) {
            		formatInfoNames.append(", [" + infoName + "] VARCHAR(100)");
            	}
            	else {
            		if (infoName.equals("Frequency")) {  // 频次列的字段类型为整型int
            			formatInfoNames.append(", " + infoName + " int");
            		}
            		else{
            			formatInfoNames.append(", " + infoName + " VARCHAR(100)");
            		}
            	}
            }
            statement.executeUpdate("create table " + formatTableName + " (ID int primary key" + formatInfoNames + ")");  // 执行SQL语句，创建相应表
            con.close();
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public void insertToTable(String[] infoBlock) {  // 该实例方法用于将infoBlock（代表一行数据）插入数据表中；即每次插入一条记录
		Connection con;
		StringBuffer formatInsertion=new StringBuffer();
		try {
			EstablishConnection.loadDriver();
			con=EstablishConnection.connectDatebase("D:/Temp/Database.accdb");  // 调用EstablishConnection类中的connectDatabase静态方法建立连接
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select count(*) from " + formatTableName);
			rs.next();
			int id = rs.getInt(1) + 1;  // rs.getInt(1)获得的就是当前数据表最后一个记录的ID字段值，将它加1就是现在待插入的记录的ID字段值
			for(int i=0; i<infoBlock.length; i++) {
				formatInsertion.append(", ?");
			}
			String sqlStr="insert into " + formatTableName + " values (?" + formatInsertion + ")";
			PreparedStatement preparedStmt = con.prepareStatement(sqlStr);  // 使用预处理语句进行记录插入
			preparedStmt.setInt(1, id);
			int location=2;
			for (String infoValue:infoBlock) {  // 遍历infoBlock，在相应位置插入字段值
				preparedStmt.setString(location, infoValue);
				location+=1;
			}
			preparedStmt.executeUpdate();
            con.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
