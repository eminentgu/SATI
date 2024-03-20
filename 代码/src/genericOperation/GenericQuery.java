package genericOperation;

import java.sql.*;

public class GenericQuery { // 通用查询类，给定一个SQL查询语句（形如select * from tableName where ），获得语句的查询结果

	private String queryStr; // 需执行的SQL查询语句
	private String[][] info; // 查询结果，n条记录，m列字段

	public GenericQuery(String queryStr) { // 用SQL查询语句来构造实例
		this.queryStr = queryStr;
		info = null; // info被初始化为null
	}

	public String[][] fetchInfo() { // 类的实例通过调用fetchInfo方法即可获得查询结果info
		Query();
		return info;
	}

	private void Query() { // 根据SQL查询语句进行查询，并将结果记录在info二维String数组中
		Connection con;
		Statement sql;
		ResultSet rs; // 结果集
		try {
			EstablishConnection.loadDriver();
			con = EstablishConnection.connectDatebase("D:/Temp/Database.accdb"); // 调用EstablishConnection类中的connectDatabase静态方法建立连接
			sql = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY); // rs的游标可以上下移动，更容易实现查询操作
			rs = sql.executeQuery(queryStr); // 执行该SQL查询语句
			ResultSetMetaData rsMetaData = rs.getMetaData(); // 结果集rs调用getMetaData方法返回一个结果集的元数据对象rsMetaData，rsMetaData可以获得结果集的列数
			rs.last(); // 将游标移动到结果集尾端，再调用rs.getRow()即可获得结果集的行数
			int rowCount = rs.getRow(), colCount = rsMetaData.getColumnCount(), id = 0;
			if (rowCount != 0) { // 如果结果集存在记录，就将查询结果赋值给info，否则info仍为null
				info = new String[rowCount][colCount];
			}
			rs.beforeFirst(); // 将rs游标移动到第一条记录之前
			while (rs.next()) {
				for (int i = 0; i < colCount; i++) {
					info[id][i] = rs.getString(i + 1);  // 取出rs指向的记录每一个字段的取值并赋值给info
				}
				id++;
			}
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
