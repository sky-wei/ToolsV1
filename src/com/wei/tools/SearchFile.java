package com.wei.tools;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.wei.bean.SearchItem;
import com.wei.bean.SearchResult;
import com.wei.imp.OutputImp;
import com.wei.imp.SearchImp;

public class SearchFile {
	
	public final static OutputFormat OUT_FORMAT = OutputFormat.createPrettyPrint();
	
	private File sourceFile;
	private File saveFile;
	private FileNameExtensionFilter filter;
	private SearchImp searchImp;
	private OutputImp outputImp;
	private int key;
	private String encoding;
	
	public SearchFile(String sourcePath, String saveFilePath, SearchImp searchImp) {
		this(new File(sourcePath), new File(saveFilePath), searchImp);
	}
	
	public SearchFile(File sourceFile, File saveFile, SearchImp searchImp) {
		this.sourceFile = sourceFile;
		this.saveFile = saveFile;
		this.searchImp = searchImp;
		this.encoding = System.getProperty("file.encoding");
	}

	public File getSourceFile() {
		return sourceFile;
	}

	public File getSaveFile() {
		return saveFile;
	}
	
	public FileNameExtensionFilter getFilter() {
		return filter;
	}

	public void setFilter(FileNameExtensionFilter filter) {
		this.filter = filter;
	}
	
	public SearchImp getSearchImp() {
		return searchImp;
	}

	public OutputImp getOutputImp() {
		return outputImp;
	}

	public void setOutputImp(OutputImp outputImp) {
		this.outputImp = outputImp;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public boolean searchAll() {
		
		if (sourceFile == null || !sourceFile.exists()
				|| saveFile == null) {
			return false;
		}
		
		Queue<File> searchFiles = searchAllFile();
		List<SearchResult> searchResults = new ArrayList<SearchResult>();
		Map<Integer, String> map = new HashMap<Integer, String>();
		Map<String, Integer> map_ii = new HashMap<String, Integer>();
		
		while (searchFiles != null && !searchFiles.isEmpty()) {
			
			File file = searchFiles.poll();
			try {
				String content = readFileContent(file);
				outputInfoln("\nStart Search " + file.getPath());
				
				if (content != null && content.trim().length() > 0) {
					
					List<String> result = searchImp.searchAnalysis(content);
					if (result != null && result.size() > 0) {
						
						List<SearchItem> searchItems = new ArrayList<SearchItem>();
						for (int i = 0; i < result.size(); i++) {
							String value = result.get(i);
							if (!map_ii.containsKey(value)) {
								map_ii.put(value, key);
								map.put(key, value);
								searchItems.add(new SearchItem(key, value));
								key ++;
							} else {
								int usedKey = map_ii.get(value);
								searchItems.add(new SearchItem(usedKey, value));
							}
						}
						
						SearchResult searchResult = new SearchResult(file, searchItems);
						outputInfoln(searchResult.toString());
						searchResults.add(searchResult);
					}
				}
				
				outputInfoln("End Search " + file.getPath());
			} catch (IOException e) {
				outputInfoln("IOException!", e);
				return false;
			}
		}
		
		return saveToFile(searchResults);
	}
	
	private boolean saveToFile(List<SearchResult> searchResults) {
		
		if (searchResults == null || searchResults.isEmpty()) {
			outputInfoln("SearchResults is Null!!!");
			return false;
		}
		
		Document document = DocumentHelper.createDocument();
		
		Element root = document.addElement("root");
		root.addAttribute("startKey", Integer.toString(0));
		root.addAttribute("endKey", Integer.toString(key));
		for (int i = 0; i < searchResults.size(); i++) {
			root.add(searchResults.get(i).getElement());
		}
		
		try {
			return writeToFile(saveFile, document);
		} catch (IOException e) {
			outputInfoln("IOException!", e);
			return false;
		}
	}
	
	private boolean writeToFile(File filePath, Document document) throws IOException {
		
		if (filePath == null || document == null) {
			return false;
		}
		
		OutputStream out = null;
		XMLWriter xmlWriter = null;
		
		try {
			
			out = new FileOutputStream(filePath);
			xmlWriter = new XMLWriter(out, OUT_FORMAT);
			xmlWriter.write(document);
		} finally {
			if (xmlWriter != null) xmlWriter.close();
			if (out != null) out.close();
		}
		
		return true;
	}
	
	private String readFileContent(File file) throws IOException {
		
		if (file == null || !file.exists() || !file.isFile()) {
			return null;
		}
		
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		ByteArrayOutputStream baos = null;
		
		try {
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			baos = new ByteArrayOutputStream(10240);
			
			int len = 0;
			byte[] buffer = new byte[10240];
			
			while((len = bis.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}
			baos.flush();
			
			return new String(baos.toByteArray(), encoding);
		} finally {
			if (baos != null) baos.close();
			if (bis != null) bis.close();
			if (fis != null) fis.close();
		}
	}
	
	private Queue<File> searchAllFile() {
		
		Queue<File> searchResult = new LinkedList<File>();
		
		if (sourceFile.isFile()) {
			if (extensionFilter(sourceFile)) {
				searchResult.add(sourceFile);
				return searchResult;
			}
		}
		
		if (sourceFile.isDirectory()) {
			
			// 获取源目录下的所有文件或目录
			Queue<File> searchQueue = listDir(sourceFile);
			
			while (searchQueue != null && !searchQueue.isEmpty()) {
				
				File file = searchQueue.poll();
				if (file != null) {
					
					if (file.isDirectory()) {
						Queue<File> queue1 = listDir(file);
						if (queue1 != null) {
							searchQueue.addAll(queue1);
						}
					} else if (file.isFile()) {
						if (extensionFilter(file)) {
							searchResult.add(file);
						}
					}
				}
			}
		}
		
		return searchResult;
	}
	
	private Queue<File> listDir(File dir) {
		
		if (dir == null || !dir.exists() || !dir.isDirectory()) {
			return null;
		}
		
		File[] files = dir.listFiles();
		if (files != null && files.length > 0) {
			Queue<File> queue = new LinkedList<File>();
			for (int i = 0; i < files.length; i++) {
				queue.add(files[i]);
			}
			return queue;
		}
		
		return null;
	}
	
	private void outputInfoln(String msg) {
		if (outputImp != null) {
			outputImp.outputInfoln(msg);
		}
	}
	
	private void outputInfoln(String msg, Throwable throwable) {
		if (outputImp != null) {
			outputImp.outputInfoln(msg, throwable);
		}
	}
	
	private boolean extensionFilter(File file) {
		if (filter != null) {
			return filter.accept(file);
		}
		return true;
	}
}
