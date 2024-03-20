package analyticalFunction;

import genericOperation.*;

public class TopNDistribution {  // 该类实现了分析功能中的分布查询

	static int authorAddress = 0, keywords = 0, author = 1, journal = 2;  // 静态成员将要查询分布的字段名映射为索引
	private String tableName, formatTableName, formatWordName;  // 在tableName列筛选含有wordName的论文信息，再进行分布查询
	private String[][] info;
	
	public TopNDistribution(String tableName, String wordName) {  //根据tableName和wordName来构造实例，具体要查询什么分布，用户要调用DistributionInquiry方法
		this.tableName = tableName;
		formatTableName = tableName;
		if (tableName.contains(" ") || tableName.contains("/")) {  // 表名中可能含有空格和正斜杠，需要将其格式化，否则SQL语句解析异常
			formatTableName = "[" + tableName + "]";
		}
		formatWordName = wordName;
		if (wordName.contains(" ") || wordName.contains("/")) {  // 词语名中也可能含有空格和正斜杠，同样需要进行格式化
			formatWordName = "[" + wordName + "]";
		}
		init();  // 初始化，获得tableName列值为wordName的所有分布信息
	}
	
	private void init() {
		if (tableName.equals("Keywords")) {  // 如果tableName是关键词，用户可能查询分布的字段为机构、作者、期刊
			String queryStr = "select [Author Address], Author, Journal from [Paper Information] where " + formatTableName + " like '%" + formatWordName + ";%'";  // wordName后要加分号表示整个词语，否则查询到的记也可能以wordName为前缀
			GenericQuery getInfo = new GenericQuery(queryStr);  // 使用genericOperation包中的GenericQuery类进行含有该词语的题录信息获取，存储在二维数组info中
			info = getInfo.fetchInfo();
		}
		else {  // 如果tableName是机构，用户可能查询分布的字段为关键词、作者、期刊
			String queryStr = "select Keywords, Author, Journal from [Paper Information] where " + formatTableName + " like '%" + formatWordName + ";%'";
			GenericQuery getInfo = new GenericQuery(queryStr);
			info = getInfo.fetchInfo();
		}
	}
	
	public boolean infoNotNull() {  // 判断tableName列值为wordName的记录是否存在，如果不存在返回false，否则返回true
		if (info == null) {
			return false;
		}
		return true;
	}
	
	public String[][] distributionInquiry(String colName) { // 根据tableName和colName，调用GetResult的静态方法getTopN获得分布查询结果
		if (tableName.equals("Keywords")) {
			switch(colName) {
				case "Author":
					return GetResult.getTopN(info, 10, author, false);
				case "Journal":
					return GetResult.getTopN(info, 10, journal, false);
				default:  // default其实代表 case "机构"
					return GetResult.getTopN(info, 10, authorAddress, false);
			}
		}
		else {
			switch(colName) {
			case "Author":
				return GetResult.getTopN(info, 10, author, false);
			case "Journal":
				return GetResult.getTopN(info, 10, journal, false);
			default:  // default其实代表 case "关键词"
				return GetResult.getTopN(info, 30, keywords, false);  // 题目要求关键词要查询TOP 30
			}
		}
	}
	
}
