package com.wei.update;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.wei.tools.Tools;


public class DownloadThread extends Thread {

	private static final int CONN_TIMEOUT = 1000 * 120;
	private static final int READ_TIMEOUT = 1000 * 60;
	private static final int BUFFER_SIZE = 1024 * 4;
	
	private String url;
	private long downSize;
	private File tempFile;
	private int consumeTime;
	
	/**
	 * 构造一个新的线程下载文件
	 * @param url 下载文件的url
	 * @param tempFile 下载保存的临时文件
	 */
	public DownloadThread(String url, File tempFile) {
		
		this.url = url;
		this.tempFile = tempFile;
		this.downSize = getTempFileSize();
	}
	
	@Override
	public void run() {
		super.run();
		
		RandomAccessFile raf = null;
		BufferedInputStream bis = null;
		
		Tools.log.debug("Start Down File!");
		Tools.log.debug("FileUrl: " + url);
		
		// 下载的开始时间...
		long startTime = System.currentTimeMillis();
		
		try {
			URL url = new URL(this.url);
			URLConnection conn = url.openConnection();
			
			// 设置连接的参数,超时时间等
			conn.setConnectTimeout(CONN_TIMEOUT);
			conn.setReadTimeout(READ_TIMEOUT);
			conn.setAllowUserInteraction(true);
			
			String property = "bytes=" + downSize + "-";
			Tools.log.debug("RANGE: " + property);
			conn.setRequestProperty("RANGE", property);
			
			raf = new RandomAccessFile(this.tempFile, "rw");
			raf.seek(downSize);
			Tools.log.debug("Open File!,seek:" + downSize);
			
			InputStream is = conn.getInputStream();
			bis = new BufferedInputStream(is, BUFFER_SIZE * 3);
			byte[] buffer = new byte[BUFFER_SIZE];
			int len = -1;
			
			while ((len = bis.read(buffer)) != -1) {
				raf.write(buffer, 0, len);
			}
		} catch (MalformedURLException e) {
			Tools.log.error("MalformedURLException", e);
		} catch (IOException e) {
			Tools.log.error("IOException", e);
		} catch (Exception e) {
			Tools.log.error("Exception", e);
		} finally {
			try {
				if (bis != null) bis.close();
				if (raf != null) raf.close();
			} catch (IOException e) {
			}
		}
		
		// 结束时间...
		long endTime = System.currentTimeMillis();
		consumeTime = (int)((endTime - startTime) / 1000);
		Tools.log.debug("Down Time(second):" + consumeTime);
		
		Tools.log.debug("Exit Down File!");
	}
	
	/**
	 * 返回下载到本地临时文件的大小
	 * @return 下载的临时文件大小
	 */
	public long getTempFileSize() {
		
		if (tempFile.exists() && tempFile.isFile()) {
			return tempFile.length();
		}
		
		return 0;
	}

	/**
	 * 返回当前下载资源所消耗的时间
	 * @return 下载所消耗的时间
	 */
	public int getConsumeTime() {
		return consumeTime;
	}
}