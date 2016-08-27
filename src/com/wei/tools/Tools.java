package com.wei.tools;

import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.wei.bean.ConfigInfo;
import com.wei.imp.OutputImp;

public class Tools {
	
	public static final OutputFormat OUTPUT_FORMAT = OutputFormat.createPrettyPrint();
	public static final SimpleDateFormat DATA_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static final Random RANDOM = new Random();
	
	private static ConfigInfo configInfo;
	
	public static final Logger log = Logger.getLogger("ToolsLog");
	
	{
		PropertyConfigurator.configure("log4j.properties");
	}
	
	public static boolean createDir(File dir) {
		
		if (dir == null) {
			throw new NullPointerException();
		}
		
		if (!dir.exists()) {
			return dir.mkdirs();
		}
		
		return true;
	}
	
	public static boolean deleteAllFile(File dir) {
		
		if (dir == null) {
			throw new NullPointerException();
		}
		
		if (dir.isDirectory()) {
			
			File[] files = dir.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					deleteAllFile(files[i]);
				}
			}
		}
		
		return dir.delete();
	}
	
	public static void deleteNullDir(File dir) {
		
		if (dir == null) {
			throw new NullPointerException();
		}
		
		if (dir.isDirectory()) {
			
			File[] files = dir.listFiles();
			if (files == null || files.length <= 0) {
				File parent = dir.getParentFile();
				dir.delete();
				if (parent != null) {
					deleteNullDir(parent);
				}
			}
		}
	}
	
	public static void deleleAllFileAndNullDir(File dir) {
		
		deleteAllFile(dir);
		// 为上一级成特别做的处理
		createDir(dir);
		deleteNullDir(dir);
	}
	
	public static boolean fileExists(File file) {
		
		if (file == null) {
			throw new NullPointerException();
		}
		
		return file.exists();
	}
	
	public static String getNoExFileName(File file) {
		
		if (file == null || !file.exists() || !file.isFile()) {
			return null;
		}
		
		String name = file.getName();
		int lastIndex = name.lastIndexOf(".");
		
		if (lastIndex != -1) {
			return name.substring(0, lastIndex);
		}
		
		return name;
	}
	
	public static Document readXmlFile(File file) throws DocumentException {
		
		if (file == null || !file.exists()) {
			throw new NullPointerException();
		}
		
		SAXReader saxReader = new SAXReader();
		
		return saxReader.read(file);
	}
	
	public static boolean writeToFile(File file, Document document) throws IOException {
		
		if (file == null || document == null) {
			throw new NullPointerException();
		}
		
		OutputStream out = null;
		XMLWriter xmlWriter = null;
		
		try {
			
			out = new FileOutputStream(file);
			xmlWriter = new XMLWriter(out, OUTPUT_FORMAT);
			xmlWriter.write(document);
		} finally {
			if (xmlWriter != null) xmlWriter.close();
			if (out != null) out.close();
		}
		
		return true;
	}
	
	public static ConfigInfo getConfigInfo() {
		
		if (Tools.configInfo == null) {
			
			File configFile = new File("res/Config.xml");
			
			try {
				Tools.configInfo = loadConfigInfo(configFile);
			} catch (DocumentException e) {
				Tools.log.error("Load ConfigInfo Exception!", e);
			}
			
			if (Tools.configInfo == null) {
				Tools.configInfo = getDefaultConfigInfo();
			}
		}
		
		return Tools.configInfo;
	}
	
	public static boolean saveConfigInfo(ConfigInfo configInfo) {
		
		if (configInfo == null) {
			return false;
		}
		
		// 保存给当前
		Tools.configInfo = configInfo;
		
		Document document = DocumentHelper.createDocument();
		document.add(configInfo.getElement());
		
		File configFile = new File("res/Config.xml");
		
		try {
			return writeToFile(configFile, document);
		} catch (IOException e) {
			Tools.log.error("Save ConfigInfo Exception!", e);
		}
		
		return false;
	}
	
	private static ConfigInfo loadConfigInfo(File configFile) throws DocumentException {
		
		if (configFile == null || !configFile.exists()) {
			return null;
		}
		
		Document document = readXmlFile(configFile);
		Element config = document.getRootElement();
		
		ConfigInfo configInfo = new ConfigInfo();
		configInfo.setElement(config);
		
		return configInfo;
	}
	
	private static ConfigInfo getDefaultConfigInfo() {
		
		String userDir = System.getProperty("user.dir");
		File keystoreFile = new File(userDir + "/res/Android.keystore");
		
		ConfigInfo configInfo = new ConfigInfo();
		configInfo.setKeystoreFile(keystoreFile);
		configInfo.setName("jingcai.wei");
		configInfo.setPassword("jingcai1314");
		
		return configInfo;
	}
	
	public static String readFileContent(File file, String encoding) throws IOException {
		
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
	
	public static byte[] readFile(File file) throws IOException, FileSizeException {
		
		if (file == null || !file.exists() || !file.isFile()) {
			return null;
		}
		
		// 文件不能大于10MB
		if (file.length() > 1024 * 1024 * 10) {
			throw new FileSizeException("File > 10MB!");
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
			
			return baos.toByteArray();
		} finally {
			if (baos != null) baos.close();
			if (bis != null) bis.close();
			if (fis != null) fis.close();
		}
	}
	
	public static boolean saveFile(File file, byte[] content) throws IOException {
		
		if (file == null || content == null) {
			return false;
		}
		
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		
		try {
			fos = new FileOutputStream(file, false);
			bos = new BufferedOutputStream(fos);
			
			bos.write(content);
			bos.flush();
			
			return true;
		} finally {
			if (bos != null) bos.close();
			if (fos != null) fos.close();
		}
	}
	
	public static boolean saveFileContent(File file, String content, String encoding) throws IOException {
		
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
	
	public static class FileSizeException extends Exception {
		
		private static final long serialVersionUID = -7781640647453151160L;

		public FileSizeException() {};
		
		public FileSizeException(String messages) {
			super(messages);
		}
		
		public FileSizeException(String messages, Throwable t) {
			super(messages, t);
		}
	}
	
	 
	public static String bytesToHexString(byte[] src){
       
		if (src == null || src.length <= 0) {
    	   	return null;
       	}
       	
       	StringBuilder stringBuilder = new StringBuilder("");
       	
       	for (int i = 0; i < src.length; i++) {
    	   	
           	String value = Integer.toHexString(src[i] & 0xFF);
           	if (value.length() < 2) {
        	   	stringBuilder.append(0);
           	}
           	stringBuilder.append(value);
       	}
       	
       	return stringBuilder.toString();
	}

	public static byte[] hexStringToBytes(String hexString) {
		
		if (hexString == null || hexString.trim().length() <= 0) {
    	   	return null;
       	}
       
       	hexString = hexString.toUpperCase();
       	int length = hexString.length() / 2;
       	char[] hexChars = hexString.toCharArray();
       	byte[] d = new byte[length];
       
       	for (int i = 0; i < length; i++) {
           	int pos = i * 2;
           	d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
       	}
       
       	return d;
	}

	private static byte charToByte(char c) {
       	return (byte) "0123456789ABCDEF".indexOf(c);
   	}
	
	/**
	 * 获取指定文件的图片
	 * @param file 图片路径
	 * @return Image
	 */
	public static Image getImage(File file) {
		
		if (file == null || !file.exists()) {
			return null;
		}
		
		try {
			return ImageIO.read(file);
		} catch (IOException e) {
			Tools.log.error("IOException!", e);
		}
		
		return null;
	}
   
	public static byte[] getRandomKey() {
		
		byte[] key = new byte[16];
		
		for (int i = 0; i < key.length; i++) {
			
			key[i] = (byte)RANDOM.nextInt(Byte.MAX_VALUE);
		}
		
		return key;
	}
	
	public static void outputInfoln(OutputImp outputImp, String msg) {
		
		if (outputImp == null) {
			System.out.println(msg);
			return ;
		}
		outputImp.outputInfoln(msg);
	}
	
	public static void outputInfoln(OutputImp outputImp, String msg, Throwable t) {
		
		if (outputImp == null) {
			System.out.println(msg);
			t.printStackTrace();
			return ;
		}
		outputImp.outputInfoln(msg, t);
	}
}
