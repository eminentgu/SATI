package statisticalFunction;

import java.text.Collator;
import java.util.Locale;
import java.util.*;

class WordsFreq implements Comparable<WordsFreq> {  // 同分析功能中的WordsFreq类，用于实现自定义的排序功能
	
	String word;
	int freq;
	
	WordsFreq(){}
	
	WordsFreq(String word, int freq){
		this.word = word;
		this.freq = freq;
	}
	
	public int compareTo(WordsFreq wf) {
		int res = wf.freq - freq;  // 首先按频次排序
		if (res == 0) {
			Collator collator = Collator.getInstance(Locale.CHINA);  // 如果频次一致，就按汉语拼音升序排列
			res = collator.compare(word, wf.word);
		}
		return res;
	}
	
}

class GetResult {
	
	static String[][] getTopN(String[][] info, int n){  // 相比分析功能中的TopN查询，由于这里创建了数据表，因此程序会简洁一些
		List<WordsFreq> freqList = new LinkedList<>();
		for (String[] item: info) {  // item[0]是一篇论文的关键词，item[1]是它对应的词频
			WordsFreq wordsFreq = new WordsFreq(item[0], Integer.parseInt(item[1]));
			freqList.add(wordsFreq);
		}
		Collections.sort(freqList);
		Iterator<WordsFreq> iter = freqList.iterator();
		int cnt = 0;
		String[][] result = new String[n][2];  // 将结果存入result后返回
		while(iter.hasNext()) {
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
	
}