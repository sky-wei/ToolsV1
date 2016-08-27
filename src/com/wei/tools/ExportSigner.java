package com.wei.tools;

import java.io.File;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;

import com.wei.bean.SignerInfo;
import com.wei.imp.OutputImp;
import com.wei.tools.Tools.FileSizeException;

public class ExportSigner {
	
	private File exportDir;
	private SignerInfo signerInfo;
	
	private OutputImp outputImp;
	
	public ExportSigner(File exportDir, SignerInfo signerInfo) {
		this.exportDir = exportDir;
		this.signerInfo = signerInfo;
	}
	
	public boolean exportSigner() {
		
		if (exportDir == null
				|| !exportDir.isDirectory()) {
			outputInfoln("ExportDir No Exists!");
			return false;
		}
		
		if (!signerInfo.getFilePath().isFile()) {
			outputInfoln("Source File " + signerInfo.getFilePath() + " No Exists!");
			return false;
		}
		
		File signerFile = new File(exportDir, signerInfo.getSignerName() + ".keystore");
		File configFile = new File(exportDir, signerInfo.getSignerName() + "_config.xml");
		
		if (signerFile.isFile()) {
			outputInfoln("ExportDir " + signerFile + " Exists!");
			return false;
		}
		
		if (configFile.isFile()) {
			outputInfoln("ExportDir " + configFile + " Exists!");
			return false;
		}
		
		try {
			Tools.saveFile(signerFile, Tools.readFile(signerInfo.getFilePath()));
			
			Document document = DocumentHelper.createDocument();
			document.add(signerInfo.getElement());
			
			Tools.writeToFile(configFile, document);
			
			return true;
		} catch (IOException e) {
			outputInfoln("IOException", e);
		} catch (FileSizeException e) {
			outputInfoln("FileSizeException", e);
		}
		
		return false;
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
