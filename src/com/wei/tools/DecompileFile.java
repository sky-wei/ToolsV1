package com.wei.tools;

import java.io.File;
import java.util.Arrays;

import javax.swing.JFrame;

import com.wei.gui.ScheduleBar;
import com.wei.imp.OutputImp;

public class DecompileFile {
	
	private File decompileFile;
	
	private File workDirectory;
	private File workBinDirectory;
	private File decompileFileDirectory;
	
	private OutputImp outputImp;
	
	public DecompileFile(File decompileFile) {
		this.decompileFile = decompileFile;
		
		init();
	}
	
	public void init() {
		
		workDirectory = new File(System.getProperty("user.dir"));
		workBinDirectory = new File(workDirectory, "toolsBin");
		
		// 去除.apk的扩展名
		String fileName = Tools.getNoExFileName(decompileFile);
		decompileFileDirectory = new File(decompileFile.getParentFile(), fileName);
	}
	
	public boolean decompile(JFrame jFrame) {
		
		// 输出工作目录相关信息
		outputInfoln("WorkDirectory: " + workDirectory);
		outputInfoln("WorkBinDirectory: " + workBinDirectory);
		
		// 反编译的文件目录存在,需要删除
		if (Tools.fileExists(decompileFileDirectory)) {
			outputInfoln("Delete Used File!\nFile: " + decompileFileDirectory);
			Tools.deleteAllFile(decompileFileDirectory);
			outputInfoln("Delete Used File Complete!");
		}
		
		String[] cmd = {"java", "-jar" , "apktool.jar", "d", decompileFile.getPath(), decompileFileDirectory.getPath()};
		
		// 输出cmd命令信息
		outputInfoln(Arrays.toString(cmd));
		
		ExecRunnable execRunnable = new ExecRunnable(cmd, workBinDirectory, outputImp);
		ScheduleBar scheduleBar = new ScheduleBar(jFrame, "Decompile Apk!", execRunnable);
		scheduleBar.setVisible(true);
		
		return true;
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
