package com.wei.update;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class UpdateRes {
	
	public UpdateRes() {
		
	}
	
	public List<ResItem> getResList() throws DocumentException {
		
		SAXReader saxReader = new SAXReader();
		
		Document document = saxReader.read("http://localhost:8080/PineappleService/EelphantRes");
		Element list = document.getRootElement();
		
		List<ResItem> resItems = new ArrayList<ResItem>();
		
		if ("list".equals(list.getName())) {
			
			Iterator<?> iterator = list.elementIterator("item");
			
			while (iterator.hasNext()) {
				
				Element item = (Element)iterator.next();
				
				ResItem resItem = new ResItem();
				resItem.setElement(item);
				
				resItems.add(resItem);
			}
		}
		
		return resItems;
	}
	
	public String getResListContent() throws IOException {
		
		URL url = new URL("http://localhost:8080/PineappleService/EelphantRes");
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		
		if (conn.getResponseCode() == 200) {
			
			InputStream is = null;
			ByteArrayOutputStream baos = null;
			
			try {
				is = conn.getInputStream();
				baos = new ByteArrayOutputStream();
				
				byte[] buffer = new byte[10240];
				int len = 0;
				
				while ((len = is.read(buffer)) != -1) {
					baos.write(buffer, 0, len);
				}
				
				baos.flush();
				
				return new String(baos.toByteArray(), "UTF-8");
			} finally {
				if (baos != null) baos.close();
				if (is != null) is.close();
			}
		}
		
		return null;
	}
	
	public static void main(String[] args) throws IOException, DocumentException, InterruptedException {
		
//		UpdateRes updateRes = new UpdateRes();
//		List<ResItem> resItems = updateRes.getResList();
//		
////		for (int i = 0; i < resItems.size(); i++) {
////			
////			
////		}
//		
//		ResItem resItem = resItems.get(0);
//		File saveFile = new File("G:/" + resItem.getName());
//		
//		DownloadThread downloadThread = new DownloadThread(resItem.getUrl(), saveFile);
//		downloadThread.start();
//		downloadThread.join();
//		
//		System.out.println("下载完成...");
		
		File saveFile = new File("G:/Tool4.xRes_4.3002(Orange).zip");
		
		ZipFile zipFile = new ZipFile(saveFile);
		Enumeration<?> enumeration = zipFile.entries();
		
		while (enumeration.hasMoreElements()) {
			
//			ZipEntry entry = (ZipEntry);
			System.out.println(enumeration.nextElement());
		}
	}
}
