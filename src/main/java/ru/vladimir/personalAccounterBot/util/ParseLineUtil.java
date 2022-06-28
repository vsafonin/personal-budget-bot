package ru.vladimir.personalAccounterBot.util;

import java.util.ArrayList;
import java.util.List;

import ru.vladimir.personalAccounterBot.exception.ParseMessageExp;

public class ParseLineUtil {

	public static List<String> parse(String lineOfString) throws ParseMessageExp{
		
		List<String> wordList = new ArrayList<>();
		
		if (lineOfString.contains("\"")) {
			//count qoutes
			long qoutes = lineOfString.chars().filter(ch -> ch == '"').count();
			if((qoutes % 2) != 0) {
				throw new ParseMessageExp();
			}
			String[] words = lineOfString.split("[\"]");
			int i = 0;
			for (String word: words) {
				
				if(word.isBlank()) {
					i++;
					continue;
				}
				if (i++ == words.length - 1) {
					for (String subWord: word.split("[ ]")) {
						if(subWord.isBlank()) {
							continue;
						}
						wordList.add(subWord);
					}
				}
				else {
					wordList.add(word);
				}
				
			}
			
		}
		else {
			for (String word: lineOfString.split("[ ]")) {
				wordList.add(word);
			}
		}
		return wordList;
	}
}
