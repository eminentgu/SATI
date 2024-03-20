package importPaper;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import genericOperation.GenericCreateTable;

public class CreatePaperInfo { // 该类用于根据用户选择的题录文件创建Paper Information数据表

	public static void importPaperInfo(GenericCreateTable table, List<File> files) { // fileNames列表存储了所有用户选择的题录文件名

		for (File file : files) {
			try {
				Reader in = new FileReader(file);
				BufferedReader br = new BufferedReader(in); // 创建缓冲流进行读文件
				String line; // line是从文件中读出来的每一行内容
				StringBuffer rawInfoBlock = new StringBuffer(); // rawInfoBlock包含一篇论文的所有信息
				while ((line = br.readLine()) != null) {
					if (line.equals("")) { // 如果line为空，说明一篇论文已经读完，需要将其插入到table中
						String[] infoBlock = getInfo(rawInfoBlock, "\\{([^\\}]+)\\}:\\s*([^\\{]+)"); // 调用静态方法getInfo获取一篇论文的infoBlock
						table.insertToTable(infoBlock); // 将infoBlock插入表中
						rawInfoBlock = new StringBuffer(); // rawInfoBlock重新实例化，接收下一篇论文的信息
					} else {
						rawInfoBlock.append(line); // 在rawInfoBlock后追加新读入的行
						if (rawInfoBlock.charAt(rawInfoBlock.length() - 1) != ';') { // 为了以后更容易进行查询，在每个字段值读入后都会加上分号
							rawInfoBlock.append(";");
						}
					}
				}
				br.close();
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private static String[] getInfo(StringBuffer rawString, String regex) {// 该静态方法利用正则表达式，捕获rawInfoBlock中的论文相关信息
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(rawString);
		LinkedHashMap<String, String> infoMap = new LinkedHashMap<>(); // infoMap用于存储该论文每个字段对应的字段值
		String[] infoKeys = { "Reference Type", "Title", "Author", "Author Address", "Journal", "Year", "Volume",
				"Issue", "Pages", "Keywords", "Abstract", "ISBN/ISSN", "Notes", "URL", "DOI", "Database Provider" };
		// infoKeys记录了Paper Information数据表中的所有可能的字段名
		for (String infoKey : infoKeys) {
			infoMap.put(infoKey, "");
		}
		while (matcher.find()) {
			String infoKey = matcher.group(1); // 捕获的字段名
			String infoValue = matcher.group(2).trim(); // 捕获的字段值，应该去除首尾的无效空格
			infoMap.put(infoKey, infoValue);
		}
		String[] infoBlock = new String[16];
		int i = 0;
		for (String infoValue : infoMap.values()) {
			infoBlock[i++] = infoValue;
		}
		return infoBlock; // infoBlock数组的每个元素代表相应字段值，如果该字段值不存在，用""存储
	}

}