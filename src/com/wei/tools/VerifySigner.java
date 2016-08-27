package com.wei.tools;

import java.io.File;
import java.util.Arrays;

import javax.swing.JFrame;

import com.wei.gui.ScheduleBar;
import com.wei.imp.OutputImp;

public class VerifySigner {
	
	private File sourceFile;
	private OutputImp outputImp;
	
	private File workDirectory;
	private File workBinDirectory;
	
	public VerifySigner(File sourceFile) {
		this.sourceFile = sourceFile;
		
		init();
	}
	
	public void verifySigner(JFrame jFrame) {
		
		if (!isApkFile()) {
			outputInfoln("Invalid Apk File!");
			return ;
		}
		
		outputInfoln("Verify Signer...");
		
		String javaHome = System.getenv("JAVA_HOME");
		File sigerExeFile = new File(javaHome, "bin/jarsigner.exe");
		if (!sigerExeFile.exists()) {
			outputInfoln(sigerExeFile + " File Not Exist!");
			return ;
		}
		
		String[] cmd = {sigerExeFile.getPath(), "-verify", "-verbose", "-certs", sourceFile.getPath()};
		
		// 执行的cmd命令
		outputInfoln(Arrays.toString(cmd));
		
		ExecRunnable execRunnable = new ExecRunnable(cmd, workBinDirectory, outputImp);
		ScheduleBar scheduleBar = new ScheduleBar(jFrame, "Verify Signer!", execRunnable);
		scheduleBar.setVisible(true);
	}
	
	private void init() {
		
		workDirectory = new File(System.getProperty("user.dir"));
		workBinDirectory = new File(workDirectory, "toolsBin");
	}
	
	/**
	 * 判断当前的文件名是否是一个正常的Apk文件
	 * @return true:正常的,false:非正常的
	 */
	private boolean isApkFile() {
		
		if (sourceFile.getName() != null 
				&& sourceFile.getName().toLowerCase().endsWith(".apk")) {
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
}
