package analyticalFunction;

import genericOperation.*;

public class CooccurrenceCount {  // 该类实现了分析功能中的共现查询
	
	public static String[][] cooccurrenceInquiry(String colName, String word1, String word2) {  // 查询word1和word2的共现次数，colName表征word1和word2来源于哪个字段
		word1 = word1.trim();  // 去除word1首尾的无效空格
		word2 = word2.trim();  // 去除word2首尾的无效空格
		String formatColName = new String(colName);
		if (colName.contains(" ") || colName.contains("/")) {
			formatColName = new String("[" + colName + "]");
		}
		String queryStr = "select " + formatColName + " from [Paper Information]";
		GenericQuery getInfo = new GenericQuery(queryStr);
		String[][]  info = getInfo.fetchInfo();  // 从Paper Information中先获得所有论文在colName字段的值，info为n行1列
		if (word1.compareTo(word2) < 0) {
			return GetResult.getOne(info, word1, word2);  // 调用GetResult的静态方法getOne获得查询结果
		}
		else {
			return GetResult.getOne(info, word2, word1);
		}
	}
	
	public static String[][] cooccurrenceInquiry(String colName, int n) {  // 实现了cooccurrenceInquiry方法的重载，用于获得TOP N的共现结果
		String formatColName = new String(colName);
		if (colName.contains(" ") || colName.contains("/")) {
			formatColName = new String("[" + colName + "]");
		}
		String queryStr = "select " + formatColName + " from [Paper Information]";
		GenericQuery getInfo = new GenericQuery(queryStr);
		String[][]  info = getInfo.fetchInfo();
		return GetResult.getTopN(info, n, 0, true);  // 调用GetResult的静态方法getN获得查询结果
	}
	
}