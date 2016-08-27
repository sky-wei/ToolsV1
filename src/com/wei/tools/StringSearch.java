package com.wei.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wei.imp.SearchImp;

public class StringSearch implements SearchImp {
	
	private final static Pattern STRING_MATCHER = Pattern.compile("\"(((\\\\.?)*[^\\\\\"]*)*)\"");

	@Override
	public List<String> searchAnalysis(String content) {
		
		Matcher matcher = STRING_MATCHER.matcher(content);
		
		List<String> results = new ArrayList<String>();
		
		while (matcher.find()) {
			String result = matcher.group(1);
			if (result != null && result.trim().length() > 0) {
				results.add(result);
			}
		}
		
		return results;
	}
}
