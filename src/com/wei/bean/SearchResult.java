package com.wei.bean;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;


public class SearchResult {
	
	private File file;
	private List<SearchItem> results;
	
	public SearchResult() {}
	
	public SearchResult(File file, List<SearchItem> results) {
		this.file = file;
		this.results = results;
	}

	public File getFile() {
		return file;
	}

	public List<SearchItem> getResults() {
		return results;
	}
	
	public List<SearchItem> noRepeatResults() {
		
		Map<Integer, String> map = new HashMap<Integer, String>();
		List<SearchItem> newResult = new ArrayList<SearchItem>();
		
		for (int i = 0; i < results.size(); i++) {
			
			SearchItem item = results.get(i);
			if (!map.containsKey(item.getKey())) {
				map.put(item.getKey(), null);
				newResult.add(item);
			}
		}
		
		return newResult;
	}
	
	public Element getElement() {
		
		Element result = DocumentHelper.createElement("result");
		result.addAttribute("file", file.getPath());
		
		for (int i = 0; i < results.size(); i++) {
			Element item = DocumentHelper.createElement("item");
			SearchItem searchItem = results.get(i);
			String key = Integer.toString(searchItem.getKey());
			item.addAttribute("key", key);
			item.addAttribute("value", searchItem.getValue());
			result.add(item);
		}
		
		return result;
	}
	
	public void setElement(Element element) {
		
		String filePath = element.attributeValue("file");
		this.file = new File(filePath);
		
		Iterator<?> items = element.elementIterator("item");
		this.results = new ArrayList<SearchItem>();
		while (items.hasNext()) {
			Element item = (Element)items.next();
			String key = item.attributeValue("key");
			String value = item.attributeValue("value");
			SearchItem searchItem = new SearchItem(Integer.parseInt(key), value);
			this.results.add(searchItem);
		}
	}

	@Override
	public String toString() {
		StringBuilder info  = new StringBuilder("File: " + file + "\n");
		for (int i = 0; results != null && i < results.size(); i++) {
			info.append(results.get(i) + "\n");
		}
		return info.toString();
	}
}
