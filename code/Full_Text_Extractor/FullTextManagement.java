package Crawler.Full_Text_Extractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.jsoup.Jsoup;

import eus.ixa.ixa.pipe.seg.RuleBasedSegmenter;

public class FullTextManagement {
	
	public static String cleanHTML(String text)
	{
		String cleanText = Jsoup.parse(text).text();
		
		return cleanText;
	}
	
	public static List<String> splitSentences(String paragraph)
	{		
		Properties properties;
		properties = new Properties();
		properties.setProperty("language", "es");
		properties.setProperty("normalize", "default");
		properties.setProperty("untokenizable", "no");
		properties.setProperty("hardParagraph", "no");
		
		RuleBasedSegmenter segmenter = new RuleBasedSegmenter(paragraph, properties);
		String[] sentenceArray = segmenter.segmentSentence();
		
		List<String> sentences = new ArrayList<String>(Arrays.asList(sentenceArray));
		
		return sentences;
	}
	
	// add additional methods here
}
