package analyticalFunction;

import java.text.Collator;
import java.util.*;

class WordsFreq implements Comparable<WordsFreq> {
// 创建该类的目的是实现自定义排序（先按频次降序再按拼音升序），实现了Comparable<T>泛型接口，从而可以使用Collection类的sort方法对列表进行排序	
	String word;
	int freq;
	
	WordsFreq(){}
	
	WordsFreq(String word, int freq){
		this.word = word;
		this.freq = freq;
	}
	
	public int compareTo(WordsFreq wf) {
		int res = wf.freq - freq;  // 先比较词频
		if (res == 0) {  // 如果词频一致，则按拼音升序
			Collator collator = Collator.getInstance(Locale.CHINA);
			res = collator.compare(word, wf.word);
		}
		return res;
	}
	
}

class GetResult {  // 该类定义两个静态方法，getTopN用于获取TopN结果，getOne用于获取单个查询的结果
	
	static String[][] getTopN(String[][] info, int n, int colIdx, boolean isCooccurrence){
	// isCooccurrence判断本次查询结果是否为共现查询，如果是共现查询，colIdx直接被指定为0（因为info中只存在查询字段的信息）；如果是分布查询，colIdx由TOPNDistribution类的静态成员指定
		HashMap<String, Integer> freqMap = new HashMap<>();
		for (String[] item: info) {
			String[] words = item[colIdx].split(";");  
			int len = words.length;
			if (isCooccurrence) {  // 共现查询的处理方式
				for (int i = 0; i < len - 1; i ++) {
					for (int j = i + 1; j < len; j ++) {
						String word1 = words[i].trim();  // 去除words[i]首尾的无效空格
						String word2 = words[j].trim();  // 去除words[j]首尾的无效空格
						String word12 = (word1.compareTo(word2) < 0) ? word1 + ";" + word2 : word2 + ";" + word1;
						freqMap.put(word12, freqMap.getOrDefault(word12, 0) + 1);
					}
				}
			}
			else {  // 分布查询的处理方式
				for(int i = 0; i < len; i ++) {
					String word = words[i];
					if (! word.equals("")) {
						freqMap.put(word, freqMap.getOrDefault(word, 0) + 1);
					}					
				}
			}
			
		}
		List<WordsFreq> freqList = new LinkedList<>();
		for (String key: freqMap.keySet()) {  // 将freqMap的元素转移到freqList中，从而可以进行自定义的排序
			WordsFreq wordsFreq = new WordsFreq(key, freqMap.get(key));
			freqList.add(wordsFreq);
		}
		Collections.sort(freqList);  // 按词频降序，拼音升序排序
		Iterator<WordsFreq> iter = freqList.iterator();
		int cnt = 0;
		String[][] result = new String[n][2];
		while(iter.hasNext()) {  // 用迭代器遍历freqList，将结果赋值给result
			if (cnt < n) {
				WordsFreq wordFreq = iter.next();
				result[cnt][0] = wordFreq.word;
				result[cnt][1] = String.valueOf(wordFreq.freq);
				cnt ++;
			}
			else {
				break;
			}
		}
		return result;
	}
	
	static String[][] getOne(String[][] info, String inquiryWord1, String inquiryWord2){  // 根据传入的两个共现词来查询对应的共现频次，具体做法与getN一致
		HashMap<String, Integer> freqMap = new HashMap<>();
		for (String[] item: info) {
			String[] words = item[0].split(";");
			int len = words.length;
			for (int i = 0; i < len - 1; i ++) {
				for (int j = i + 1; j < len; j ++) {
					String word1 = words[i].trim();
					String word2 = words[j].trim();
					String word12 = (word1.compareTo(word2) < 0) ? word1 + ";" + word2 : word2 + ";" + word1;  // 词语共现与先后次序无关，需要进行处理
					freqMap.put(word12, freqMap.getOrDefault(word12, 0) + 1);
				}
			}
		}
		String[][] result = new String[1][2];
		String inquiryWord12 = inquiryWord1 + ";" + inquiryWord2;
		result[0][0] =  inquiryWord12;
		result[0][1] = String.valueOf(freqMap.getOrDefault(inquiryWord12, null));
		return result;
	}
	
}
