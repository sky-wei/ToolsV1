package com.wei.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import com.wei.imp.OutputImp;

public class FileHandle {
	
	private File sourceDir;
	private File targetDir;
	private OutputImp outputImp;
	private boolean rmSourceDir;
	
	public FileHandle(String sourceDir, String targetDir) {
		
		this.sourceDir = new File(sourceDir);
		this.targetDir = new File(targetDir);
		this.rmSourceDir = false;
	}
	
	public FileHandle(File sourceDir, File targetDir) {
		
		this.sourceDir = sourceDir;
		this.targetDir = targetDir;
		this.rmSourceDir = false;
	}

	public File getSourceDir() {
		return sourceDir;
	}

	public File getTargetDir() {
		return targetDir;
	}

	public OutputImp getOutputImp() {
		return outputImp;
	}

	public void setOutputImp(OutputImp outputImp) {
		this.outputImp = outputImp;
	}
	
	public boolean isRmSourceDir() {
		return rmSourceDir;
	}

	public void setRmSourceDir(boolean rmSourceDir) {
		this.rmSourceDir = rmSourceDir;
	}

	public boolean copyAll() throws IOException {
		
		if (sourceDir == null || !sourceDir.exists()
				|| targetDir == null) {
			return false;
		}
		
		if (sourceDir.isFile()) {
			String fileName = sourceDir.getName();
			if (dirExists(targetDir, true)) {
				File targetFile = new File(targetDir.getPath() + "/" + fileName);
				return copyFile(sourceDir, targetFile);
			}
			return false;
		}
		
		if (sourceDir.getPath().equals(targetDir.getPath())) {
			return true;
		}
		
		if (sourceDir.isDirectory()) {
			
			File newFilePath = new File(targetDir.getPath() + "/" + sourceDir.getName());
			
			if (rmSourceDir) {
				newFilePath = new File(targetDir.getPath());
			}
			
			if (sourceDir.compareTo(newFilePath) == 0) {
				return true;
			}
			dirExists(newFilePath, true);
			
			// 获取源目录下的所有文件或目录
			Queue<File> queue = listDir(sourceDir);
			
			while (queue != null && !queue.isEmpty()) {
				
				File file = queue.poll();
				if (file != null) {
					File filePath = getCopyTargetPath(file);
					
					if (file.isDirectory()) {
						outputCopyFile(file, filePath);
						
						dirExists(filePath, true);
						Queue<File> queue1 = listDir(file);
						if (queue1 != null) {
							queue.addAll(queue1);
						}
					} else if (file.isFile()) {
						outputCopyFile(file, filePath);
						
						copyFile(file, filePath);
					}
				}
			}
			return true;
		}
		
		return false;
	}
	
	private File getCopyTargetPath(File copyFile) {
		
		if (copyFile != null) {
			
			// 获取源目录的目录名称
			String sourceDirName = sourceDir.getName();
			String copyFilePath = copyFile.getPath();
			
			String relativelyPath = copyFilePath.replace(sourceDir.getPath(), "");
			
			if (rmSourceDir) {
				return new File(targetDir, relativelyPath);
			}
			
			return new File(targetDir, sourceDirName + relativelyPath);
		}
		
		return null;
	}
	
	private void outputCopyFile(File source, File target) {
		if (outputImp != null) {
			outputImp.outputInfoln(source.getPath() + "\nCopy To >>> " + target.getPath());
		}
	}
	
	private boolean dirExists(File dir, boolean create) {
		
		if (dir == null) {
			return false;
		}
		
		if (!dir.exists()) {
			if (create) {
				return dir.mkdirs();
			}
			return false;
		}
		
		return true;
	}
	
	private Queue<File> listDir(File dir) {
		
		if (dir == null || !dir.exists() || !dir.isDirectory()) {
			return null;
		}
		
		File[] files = dir.listFiles();
		if (files != null && files.length > 0) {
			Queue<File> queue = new LinkedList<File>();
			for (int i = 0; i < files.length; i++) {
				queue.add(files[i]);
			}
			return queue;
		}
		
		return null;
	}
	
	private boolean copyFile(File sourceFile, File targetFile) throws IOException {
		
		if (sourceFile == null || !sourceFile.exists() || !sourceFile.isFile()) {
			throw new FileNotFoundException("源文件不存在!!! sourceFile: " + sourceFile);
		}
		
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		
		try {
			byte[] buffer = new byte[10240];
			int len = 0;
			
			fis = new FileInputStream(sourceFile);
			bis = new BufferedInputStream(fis);
			fos = new FileOutputStream(targetFile);
			bos = new BufferedOutputStream(fos);
			
			while ((len = bis.read(buffer)) != -1) {
				bos.write(buffer, 0, len);
			}
			bos.flush();
			
			return true;
		} finally {
			if (bos != null) bos.close();
			if (fos != null) fos.close();
			if (bis != null) bis.close();
			if (fis != null) fis.close();
		}
	}
}
