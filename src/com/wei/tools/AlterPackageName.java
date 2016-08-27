package com.wei.tools;

import java.io.File;
import java.util.Arrays;

import javax.swing.JFrame;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import com.wei.bean.ConfigInfo;
import com.wei.bean.SimpleApkInfo;
import com.wei.gui.ScheduleBar;
import com.wei.imp.OutputImp;

public class AlterPackageName {

	private File sourceFile;

	private String apkName;
	private File workDirectory;
	private File workBinDirectory;
	private File decompileDirectory;
	private File decompileFileDirectory;
	private File compileDirectory;
	private File compileFileDirectory;
	private File signerDirectory;
	private File completeDirectory;
	
	private OutputImp outputImp;
	
	public AlterPackageName(String sourcePath) {
		
		this(new File(sourcePath));
	}
	
	public AlterPackageName(File sourceFile) {
		
		this.sourceFile = sourceFile;
		
		init();
	}
	
	public void init() {
		
		apkName = sourceFile.getName();
		workDirectory = new File(System.getProperty("user.dir"));
		workBinDirectory = new File(workDirectory, "toolsBin");
		decompileDirectory = new File(workDirectory, "Decompile");
		compileDirectory = new File(workDirectory, "Compile");
		signerDirectory = new File(workDirectory, "Signer");
		completeDirectory = new File(workDirectory, "Complete");
		
		// 去除.apk的扩展名
		String fileName = Tools.getNoExFileName(sourceFile);
		decompileFileDirectory = new File(decompileDirectory, fileName);
	}
	
	public boolean decompile(JFrame jFrame) {
		
		if (!isApkFile()) {
			outputInfoln("Invalid Apk File!");
			return false;
		}
		
		// 创建目录反编译目录和编译目录
		Tools.createDir(decompileDirectory);
		Tools.createDir(compileDirectory);
		Tools.createDir(signerDirectory);
		Tools.createDir(completeDirectory);
		
		// 反编译的文件目录存在,需要删除
		if (Tools.fileExists(decompileFileDirectory)) {
			outputInfoln("Delete Used File!\nFile: " + decompileFileDirectory);
			Tools.deleteAllFile(decompileFileDirectory);
			outputInfoln("Delete Used File Complete!");
		}
		
		// 输出工作目录相关信息
		outputInfoln("WorkDirectory: " + workDirectory);
		outputInfoln("WorkBinDirectory: " + workBinDirectory);
		outputInfoln("DecompileDirectory: " + decompileDirectory);
		outputInfoln("CompileDirectory: " + compileDirectory);
		outputInfoln("SignerDirectory: " + signerDirectory);
		outputInfoln("CompleteDirectory: " + completeDirectory);
		outputInfoln("ConfigInfo: " + Tools.getConfigInfo());
		
		String[] cmd = {"java", "-jar" , "apktool.jar", "d", sourceFile.getPath(), decompileFileDirectory.getPath()};
		
		// 输出cmd命令信息
		outputInfoln(Arrays.toString(cmd));
		
		ExecRunnable execRunnable = new ExecRunnable(cmd, workBinDirectory, outputImp);
		ScheduleBar scheduleBar = new ScheduleBar(jFrame, "Decompile Apk!", execRunnable);
		scheduleBar.setVisible(true);
		
		return true;
	}
	
	public SimpleApkInfo getSimpleApkInfo() {
		
		File manifestFile = new File(decompileFileDirectory, "/AndroidManifest.xml");
		
		if (!manifestFile.exists()) {
			return null;
		}
		
		try {
			Document document = Tools.readXmlFile(manifestFile);
			Element manifestEl = document.getRootElement();
			
			if (manifestEl != null) {
				SimpleApkInfo info = new SimpleApkInfo();
				info.setPackageName(manifestEl.attributeValue("package"));
				info.setVersionName(manifestEl.attributeValue("versionName"));
				info.setVersionCode(manifestEl.attributeValue("versionCode"));
				return info;
			}
		} catch (DocumentException e) {
			outputInfoln("DocumentException! File: " + manifestFile, e);
		}
		
		return null;
	}
	
	public void alterSimplePackageName(JFrame jFrame, SimpleApkInfo pastInfo, SimpleApkInfo alterInfo) {
		
		SimpleAlterInfoRunnable alterInfoRunnable = new SimpleAlterInfoRunnable(
				decompileFileDirectory, compileDirectory, pastInfo, alterInfo, outputImp);
		ScheduleBar scheduleBar = new ScheduleBar(jFrame, "Alter Package Info!", alterInfoRunnable);
		scheduleBar.setVisible(true);
		compileFileDirectory = alterInfoRunnable.getCompileFile();
	}
	
	public void alterPackageName(JFrame jFrame, SimpleApkInfo pastInfo, SimpleApkInfo alterInfo) {
		
		AlterInfoRunnable alterInfoRunnable = new AlterInfoRunnable(
				decompileFileDirectory, compileDirectory, pastInfo, alterInfo, outputImp);
		ScheduleBar scheduleBar = new ScheduleBar(jFrame, "Alter Package Info!", alterInfoRunnable);
		scheduleBar.setVisible(true);
		compileFileDirectory = alterInfoRunnable.getCompileFile();
	}
	
	public void compileApk(JFrame jFrame, SimpleApkInfo alterApkInfo) {
		
		if (compileFileDirectory == null
				|| !compileFileDirectory.exists()) {
			outputInfoln("Compile File Directory Not Exist!");
			return ;
		}
		
		String[] cmd = {"java", "-jar" , "apktool.jar", "b", compileFileDirectory.getPath()};
		
		// 执行的cmd命令
		outputInfoln(Arrays.toString(cmd));
		
		ExecRunnable execRunnable = new ExecRunnable(cmd, workBinDirectory, outputImp);
		ScheduleBar scheduleBar = new ScheduleBar(jFrame, "Compile Apk!", execRunnable);
		scheduleBar.setVisible(true);
		
		File signerApk = new File(signerDirectory, alterApkInfo.getPackageName() + ".apk");
		File compileFile = new File(compileFileDirectory, "dist/" + apkName);
		
		// 删除之前的签名文件...
		Tools.deleteAllFile(signerApk);
		
		if (!Tools.fileExists(compileFile)) {
			outputInfoln("Compile File Not Exist!");
			return ;
		}
		
		outputInfoln("Signer Apk...");
		
		ConfigInfo configInfo = Tools.getConfigInfo();
		File keystoreFile = configInfo.getKeystoreFile();
		if (keystoreFile == null || !keystoreFile.exists()) {
			outputInfoln("Keystore File Not Exist!");
			return ;
		}
		
		String javaHome = System.getenv("JAVA_HOME");
		File singerFile = new File(javaHome, "bin/jarsigner.exe");
		if (!singerFile.exists()) {
			outputInfoln(singerFile + " File Not Exist!");
			return ;
		}
		
		String[] cmd2 = {singerFile.getPath(), "-verbose", "-keystore", keystoreFile.getPath(), "-signedjar",
				signerApk.getPath(), compileFile.getPath(), configInfo.getName(), "-storepass", configInfo.getKeystorePassword(), 
				"-keypass", configInfo.getPassword(), "-digestalg", "SHA1", "-sigalg", "MD5withRSA", "-sigfile", "CERT"};
		
		// 执行的cmd命令
		outputInfoln(Arrays.toString(cmd2));
		
		execRunnable = new ExecRunnable(cmd2, workBinDirectory, outputImp);
		scheduleBar = new ScheduleBar(jFrame, "Signer Apk!", execRunnable);
		scheduleBar.setVisible(true);
		
		if (signerApk.exists()) {
			outputInfoln("Signer Success!");
			outputInfoln("Optimization Apk...");
			
			File completeFile = new File(completeDirectory, signerApk.getName());
			// 如果存在,删除之前的文件
			if (completeFile.exists()) completeFile.delete();
			
			File optimFile = new File(workBinDirectory, "zipalign.exe");
			String[] cmd3 = {optimFile.getPath(), "-v", "4", signerApk.getPath(), completeFile.getPath()};
			
			// 执行的cmd命令
			outputInfoln(Arrays.toString(cmd3));
			
			execRunnable = new ExecRunnable(cmd3, workBinDirectory, outputImp);
			scheduleBar = new ScheduleBar(jFrame, "Optimization Apk!", execRunnable);
			scheduleBar.setVisible(true);
			
			if (completeFile.exists()) {
				outputInfoln("Optimization Success!\nComplete Apk File: " + completeFile);
			}
			return ;
		}
		
		outputInfoln("Signer Apk Failure!");
	}
	
	private boolean isApkFile() {
		
		if (apkName != null 
				&& apkName.toLowerCase().endsWith(".apk")) {
			return true;
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
	
	private void outputInfoln(String info, Throwable throwable) {
		if (this.outputImp != null) {
			this.outputImp.outputInfoln(info, throwable);
		}
	}
}
