package statisticalFunction;

import java.util.*;
import genericOperation.*;

public class FrequencyCount {  // 该类实现了统计功能中的频次统计
	
	private String colName, formatColName, tableName, formatTableName;  // colName是频次统计的字段名，tableName是要创建的频次统计表的表名
	
	public FrequencyCount(String colName) {
		this.colName = colName;
		tableName = colName + " Frequency";
		formatTableName = tableName + " Frequency";
		if (tableName.contains(" ") || tableName.contains("/")) {  // 表名中可能含有空格和正斜杠，需要将其格式化，否则SQL语句解析异常
			formatTableName = "[" + tableName + "]";
		}
		formatColName = colName;
		if (colName.contains(" ") || colName.contains("/")) {
			formatColName = "[" + colName + "]";
		}
	}
	
	public void createFreqTables() {  // 根据成员遍历colName创建频次统计表
		String[] newInfoNames = {colName, "Frequency"};
		GenericQuery getInfo = new GenericQuery("select " + formatColName + " from [Paper Information]");  // 新建GenericQuery实例getInfo，用于获取要统计的字段的所有信息
		String[][] info = getInfo.fetchInfo();
		HashMap<String, Integer>freq = new HashMap<>();  // 储存统计结果
		for (String[] item: info) {
			for (String splitItem: item[0].split(";")) {
				freq.put(splitItem, freq.getOrDefault(splitItem, 0) + 1);
			}
		}
		GenericCreateTable table = new GenericCreateTable(tableName, newInfoNames);
		if (table.initialTable()) {  // table.initialTable()返回结果为true表明表不存在，需要创建，否则无需创建
			for (String key: freq.keySet()) {  // 创建词频统计表并插入每一个统计信息
				if (! key.equals("")) {
					String[] infoBlock = {key, String.valueOf(freq.get(key))};
					table.insertToTable(infoBlock);	
				}
			}
		}	
	}
	
	public String[][] freqInquiry(String word) {  // 获取单个词语word的词频
		word = word.trim();
		GenericQuery getInfo = new GenericQuery("select " + formatColName + ", Frequency from " + formatTableName + " where " + formatColName + " = '" + word + "'");
		String[][] info = getInfo.fetchInfo();
		return info;
	}
	
	public String[][] freqInquiry(int n) {  // 获取TOP N词频查询结果
		GenericQuery getInfo = new GenericQuery("select " + formatColName + ", Frequency from " + formatTableName);
		String[][] info = getInfo.fetchInfo();
		return GetResult.getTopN(info, n);
	}
	
}