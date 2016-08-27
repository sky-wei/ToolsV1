package com.wei.tools;

import java.io.File;
import java.util.Arrays;

import javax.swing.JFrame;

import com.wei.bean.ConfigInfo;
import com.wei.gui.ScheduleBar;
import com.wei.imp.OutputImp;

public class SignerFile {
	
	private File sourceFile;
	private File targetDir;
	
	private String fileName;
	private File workDirectory;
	private File workBinDirectory;
	
	private OutputImp outputImp;
	
	public SignerFile(File sourceFile, File targetDir) {
		
		this.sourceFile = sourceFile;
		this.targetDir = targetDir;
		
		init();
	}
	
	public SignerFile(String sourceFile, String targetDir) {
		
		this(new File(sourceFile), new File(sourceFile));
	}
	
	public boolean signerFile(JFrame jFrame) {
		
		if (!isApkFile()) {
			outputInfoln("Invalid Apk File!");
			return false;
		}
		
		String signerName = "Signer_" + fileName;
		File signerFile = new File(targetDir, signerName);
		
		// 删除以存在的签名Apk文件
		if (signerFile.isFile()) {
			outputInfoln("Delete Signer File...");
			signerFile.delete();
		}
		
		outputInfoln("Signer File...");
		
		ConfigInfo configInfo = Tools.getConfigInfo();
		File keystoreFile = configInfo.getKeystoreFile();
		if (keystoreFile == null || !keystoreFile.exists()) {
			outputInfoln("Keystore File Not Exist!");
			return false;
		}
		
		String javaHome = System.getenv("JAVA_HOME");
		File singerFile = new File(javaHome, "bin/jarsigner.exe");
		if (!singerFile.exists()) {
			outputInfoln(singerFile + " File Not Exist!");
			return false;
		}
		
		String[] cmd2 = {singerFile.getPath(), "-verbose", "-keystore", keystoreFile.getPath(), "-signedjar",
				signerFile.getPath(), sourceFile.getPath(), configInfo.getName(), "-storepass", configInfo.getKeystorePassword(), 
				"-keypass", configInfo.getPassword(), "-digestalg", "SHA1", "-sigalg", "MD5withRSA", "-sigfile", "CERT"};
		
		// 执行的cmd命令
		outputInfoln(Arrays.toString(cmd2));
		
		ExecRunnable execRunnable = new ExecRunnable(cmd2, workBinDirectory, outputImp);
		ScheduleBar scheduleBar = new ScheduleBar(jFrame, "Signer Apk!", execRunnable);
		scheduleBar.setVisible(true);
		
		return singerFile.exists();
	}
	
	/**
	 * 判断当前的文件名是否是一个正常的Apk文件
	 * @return true:正常的,false:非正常的
	 */
	private boolean isApkFile() {
		
		if (fileName != null 
				&& fileName.toLowerCase().endsWith(".apk")) {
			return true;
		}
		return false;
	}
	
	private void init() {
		
		fileName = sourceFile.getName();
		workDirectory = new File(System.getProperty("user.dir"));
		workBinDirectory = new File(workDirectory, "toolsBin");
		
		// 当前目标目录下时,创建目录
		Tools.createDir(targetDir);
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
}
