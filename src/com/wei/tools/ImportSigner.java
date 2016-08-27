package com.wei.tools;

import java.io.File;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import com.wei.bean.SignerInfo;
import com.wei.db.SignerDB;
import com.wei.imp.OutputImp;
import com.wei.tools.Tools.FileSizeException;

public class ImportSigner {
	
	private File importFile;
	private File targetDir;
	private SignerDB signerDB;
	
	private OutputImp outputImp;
	
	public ImportSigner(SignerDB signerDB, File importFile, File targetDir) {
		this.signerDB = signerDB;
		this.importFile = importFile;
		this.targetDir = targetDir;
	}
	
	public boolean importSigner() {
		
		try {
			outputInfoln("ImportSignerFile: " + importFile);
			
			SignerInfo signerInfo = resolveSigner();
			
			outputInfoln("ResolveSigner: " + signerInfo);
			
			// 这里不对签名信息进行校验了,节约时间
			if (signerInfo != null) {
				
				String fileName = getNoExtendName(importFile.getName());
				File saveFile = getSaveKeystoreFile(fileName);
				
				Tools.saveFile(saveFile, Tools.readFile(importFile));
				
				signerInfo.setFilePath(saveFile);
				
				return signerDB.insertSignerInfo(signerInfo);
			}
		} catch (DocumentException e) {
			outputInfoln("解析签名配置信息出错了", e);
		} catch (IOException e) {
			outputInfoln("读取的签名文件时出错了", e);
		} catch (FileSizeException e) {
			outputInfoln("读取的签名文件过大", e);
		}
		
		return false;
	}
	
	public SignerInfo resolveSigner() throws DocumentException {
		
		String fileName = getNoExtendName(importFile.getName());
		
		if (fileName == null) return null;
		
		File configFile = new File(importFile.getParentFile(), fileName + "_config.xml");
		
		if (!configFile.isFile()) return null;
		
		Document document = Tools.readXmlFile(configFile);
		Element element = document.getRootElement();
		
		if ("signer".equals(element.getName())) {
			
			SignerInfo signerInfo = new SignerInfo(-1);
			signerInfo.setElement(element);
			
			return signerInfo;
		}
		
		return null;
	}
	
	private String getNoExtendName(String fileName) {
		
		if (fileName == null
				|| fileName.trim().length() <= 0) {
			return null;
		}
		
		int index = fileName.lastIndexOf(".");
		
		if (index != -1 && index != 0) {
			return fileName.substring(0, index);
		}
		
		return null;
	}
	
	private File getSaveKeystoreFile(String fileName) {
		
		int i = 1;
		File saveFile = new File(targetDir, fileName + ".keystore");
		
		while (true) {
			if (!saveFile.isFile()) {
				return saveFile;
			}
			saveFile = new File(targetDir, fileName + "_" + i + ".keystore");
			i++;
		}
	}
	
	public OutputImp getOutputImp() {
		return outputImp;
	}
	
	public void setOutputImp(OutputImp outputImp) {
		this.outputImp = outputImp;
	}
	
	private void outputInfoln(String info) {
		if (this.outputImp != null) {
			this.outputImp.outputInfoln(info);
		}
	}
	
	private void outputInfoln(String msg, Throwable throwable) {
		if (outputImp != null) {
			outputImp.outputInfoln(msg, throwable);
		}
	}
}
