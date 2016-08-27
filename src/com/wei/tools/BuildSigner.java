package com.wei.tools;

import java.io.File;
import java.util.Arrays;

import javax.swing.JFrame;

import com.wei.bean.SignerInfo;
import com.wei.gui.ScheduleBar;
import com.wei.imp.OutputImp;

public class BuildSigner {
	
	private SignerInfo signerInfo;
	private File saveDir;
	private File workDirectory;
	private File workBinDirectory;
	
	private OutputImp outputImp;
	
	public BuildSigner(SignerInfo signerInfo, File saveDir) {
		this.signerInfo = signerInfo;
		this.saveDir = saveDir;
		
		init();
	}
	
	public void init() {
		
		workDirectory = new File(System.getProperty("user.dir"));
		workBinDirectory = new File(workDirectory, "toolsBin");
	}
	
	public File buildSigner(JFrame jFrame) {
		
		if (!saveDir.isDirectory()) {
			saveDir.mkdirs();
		}
		
		String javaHome = System.getenv("JAVA_HOME");
		File keyToolFile = new File(javaHome, "bin/keytool.exe");
		if (!keyToolFile.exists()) {
			outputInfoln(keyToolFile + " File Not Exist!");
			return null;
		}
		
		outputInfoln("Build Signer...");
		
		// 获取保存文件的路径
		File saveFile = getSaveKeystoreFile();
		
		String[] cmd = {keyToolFile.getPath() , "-genkey", "-dname", "cn=" + signerInfo.getName() +
				", ou=" + signerInfo.getOrganization() + ", o=" + signerInfo.getOrganization() + 
				", l=" + signerInfo.getCity() + ", st=" + signerInfo.getProvince() + ", c=" + signerInfo.getCode(),
				"-alias", signerInfo.getSignerName(), "-keypass", signerInfo.getSignerPassword(),
				"-keystore", saveFile.getAbsolutePath(), "-keyalg", "RSA", "-sigalg", "MD5withRSA",
				"-storepass", signerInfo.getKeystorePassword(), "-validity", Integer.toString(signerInfo.getTerm() * 12)};
		
		// 执行的cmd命令
		outputInfoln(Arrays.toString(cmd));
		
		ExecRunnable execRunnable = new ExecRunnable(cmd, workBinDirectory, outputImp);
		ScheduleBar scheduleBar = new ScheduleBar(jFrame, "Build Signer!", execRunnable);
		scheduleBar.setVisible(true);
		
		return saveFile.isFile() ? saveFile : null;
	}
	
	private File getSaveKeystoreFile() {
		
		int i = 1;
		File saveFile = new File(saveDir, signerInfo.getSignerName() + ".keystore");
		
		while (true) {
			if (!saveFile.isFile()) {
				return saveFile;
			}
			saveFile = new File(saveDir, signerInfo.getSignerName() + "_" + i + ".keystore");
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
}
