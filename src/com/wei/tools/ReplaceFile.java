package com.wei.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.wei.bean.ReplaceItem;
import com.wei.bean.SearchItem;
import com.wei.bean.SearchResult;
import com.wei.imp.OutputImp;
import com.wei.imp.ReplaceImp;

public class ReplaceFile {
	
	private File replaceFile;
	private ReplaceImp replaceImp;
	private OutputImp outputImp;
	private String encoding;
	
	public ReplaceFile(String filePath, ReplaceImp replaceImp) {
		this(new File(filePath), replaceImp);
	}
	
	public ReplaceFile(File replaceFile, ReplaceImp replaceImp) {
		this.replaceFile = replaceFile;
		this.replaceImp = replaceImp;
		this.encoding = System.getProperty("file.encoding");
	}

	public File getReplaceFile() {
		return replaceFile;
	}
	
	public ReplaceImp getReplaceImp() {
		return replaceImp;
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

	public boolean replaceAll() {
		
		Document document = null;
		try {
			document = readFile(replaceFile);
		} catch (DocumentException e) {
			outputInfoln("DocumentException!", e);
			return false;
		}
		
		List<SearchResult> searchResults = analysisReplaceData(document);
		if (searchResults == null || searchResults.isEmpty()) {
			outputInfoln("Analysis File Error!");
			return false;
		}
		
		for (int i = 0; i < searchResults.size(); i++) {
			
			SearchResult searchResult = searchResults.get(i);
			try {
				replaceFile(searchResult.getFile(), searchResult.noRepeatResults());
			} catch (IOException e) {
				outputInfoln("IOException!", e);
				return false;
			}
		}
		
		return true;
	}
	
	private void replaceFile(File file, List<SearchItem> searchItems) throws IOException {
		
		String content = readFileContent(file);
		outputInfoln("Start ReplaceFile: " + file);
		
		for (int i = 0; i < searchItems.size(); i++) {
			
			SearchItem searchItem = searchItems.get(i);
			ReplaceItem replaceItem = replaceImp.replace(searchItem);
			content = content.replace(replaceItem.getTarget(), replaceItem.getReplacement());
			outputInfoln(replaceItem.getTarget() + " --> " + replaceItem.getReplacement());
		}
		
		outputInfoln("End ReplaceFile: " + file);
		
		if (saveFileContent(file, content)) {
			outputInfoln("Save ReplaceFile: " + file + "\n");
		}
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
	
	private boolean saveFileContent(File file, String content) throws IOException {
		
		if (file == null || content == null) {
			return false;
		}
		
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		
		try {
			fos = new FileOutputStream(file, false);
			bos = new BufferedOutputStream(fos);
			
			bos.write(content.getBytes(encoding));
			bos.flush();
			
			return true;
		} finally {
			if (bos != null) bos.close();
			if (fos != null) fos.close();
		}
	}
	
	private List<SearchResult> analysisReplaceData(Document doc) {
		
		if (!isValidDocument(doc)) {
			return null;
		}
		
		List<SearchResult> searchResults = new ArrayList<SearchResult>();
		
		Element root = doc.getRootElement();
		Iterator<?> results = root.elementIterator("result");
		while(results.hasNext()) {
			Element result = (Element)results.next();
			SearchResult searchResult = new SearchResult();
			searchResult.setElement(result);
			searchResults.add(searchResult);
		}
		
		return searchResults;
	}
	
	private boolean isValidDocument(Document doc) {
		
		if (doc == null) {
			return false;
		}
		
		Element root = doc.getRootElement();
		String name = root.getName();
		if ("root".equals(name)) {
			String startKey = root.attributeValue("startKey");
			String endKey = root.attributeValue("endKey");
			if (startKey != null && startKey.trim().length() > 0
					&& endKey != null && endKey.trim().length() > 0) {
				return true;
			}
		}
		return false;
	}
	
	
	
	private Document readFile(File file) throws DocumentException {
		
		if (file == null || !file.exists()) {
			return null;
		}
		
		SAXReader saxReader = new SAXReader();
		
		return saxReader.read(file);
	}
	
	private void outputInfoln(String msg) {
		if (this.outputImp != null) {
			this.outputImp.outputInfoln(msg);
		}
	}
	
	private void outputInfoln(String msg, Throwable throwable) {
		if (this.outputImp != null) {
			this.outputImp.outputInfoln(msg, throwable);
		}
	}
}
