package com.wei.tools;

import java.io.File;
import java.util.Arrays;

import javax.swing.JFrame;

import com.wei.gui.ScheduleBar;
import com.wei.imp.OutputImp;

public class CompileFile {
	
	private File compileFile;
	
	private File workDirectory;
	private File workBinDirectory;
	
	private OutputImp outputImp;
	
	public CompileFile(File compileFile) {
		this.compileFile = compileFile;
		
		init();
	}
	
	public void init() {
		
		workDirectory = new File(System.getProperty("user.dir"));
		workBinDirectory = new File(workDirectory, "toolsBin");
	}
	
	public void compileApk(JFrame jFrame) {
		
		String[] cmd = {"java", "-jar" , "apktool.jar", "b", compileFile.getPath()};
		
		// 执行的cmd命令
		outputInfoln(Arrays.toString(cmd));
		
		System.out.println(workBinDirectory);
		ExecRunnable execRunnable = new ExecRunnable(cmd, workBinDirectory, outputImp);
		ScheduleBar scheduleBar = new ScheduleBar(jFrame, "Compile Apk!", execRunnable);
		scheduleBar.setVisible(true);
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
