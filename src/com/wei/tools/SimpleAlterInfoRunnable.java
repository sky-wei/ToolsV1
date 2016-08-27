package com.wei.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wei.bean.ReplaceItem;
import com.wei.bean.SearchItem;
import com.wei.bean.SimpleApkInfo;
import com.wei.imp.OutputImp;
import com.wei.imp.ReplaceImp;
import com.wei.imp.SearchImp;

public class SimpleAlterInfoRunnable implements Runnable {
	
	public static final Pattern NAME_FOR_MATCHER = Pattern.compile("android:name=\"([\\w|\\.|$]+)\"");

	private File alterFile;
	private File targetDir;
	private File compileFile;
	private SimpleApkInfo pastInfo;
	private SimpleApkInfo alterInfo;
	private OutputImp outputImp;
	
	public SimpleAlterInfoRunnable(File alterFile, File targetDir, SimpleApkInfo pastInfo, 
			SimpleApkInfo alterInfo, OutputImp outputImp) {
		
		this.alterFile = alterFile;
		this.targetDir = targetDir;
		this.pastInfo = pastInfo;
		this.alterInfo = alterInfo;
		this.outputImp = outputImp;
	}
	
	public File getCompileFile() {
		return compileFile;
	}

	@Override
	public void run() {

		try {
			outputInfoln("Start Alter Package Info! ...");
			outputInfoln("PastInfo:\n" + pastInfo);
			outputInfoln("AlterInfo:\n" + alterInfo);
			
			compileFile = new File(targetDir, alterFile.getName());
			
			if (Tools.fileExists(compileFile)) {
				outputInfoln("Delete Used File!\nFile: " + compileFile);
				Tools.deleteAllFile(compileFile);
				outputInfoln("Delete Used File Complete!");
			}
			
			FileHandle fileHandle = new FileHandle(alterFile, targetDir);
			fileHandle.copyAll();
			
			outputInfoln("Replace Manifest File...");
			replaceManifestFile();
			
			outputInfoln("End Alter Package Info! ...");
		} catch (Exception e) {
			outputInfoln("Alter Package Exception!", e);
		}
	}
	
	private void replaceManifestFile() {
		
		File searchResult = new File(compileFile, "searchResult.xml");
		ManifestSearch manifestSearch = new ManifestSearch(pastInfo);
		SearchFile searchFile = new SearchFile(new File(compileFile, "AndroidManifest.xml"), searchResult, manifestSearch);
		searchFile.searchAll();
		
		ManifestReplace manifestReplace = new ManifestReplace(pastInfo, alterInfo, new File(compileFile, "smali"));
		ReplaceFile replaceFile = new ReplaceFile(searchResult, manifestReplace);
		replaceFile.replaceAll();
		
		Tools.deleteAllFile(searchResult);
	}
	
	private void outputInfoln(String msg) {
		if (this.outputImp != null) {
			this.outputImp.outputInfoln(msg);
		}
	}
	
	private void outputInfoln(String msg, Throwable throwable) {
		if (this.outputImp != null) {
			this.outputInfoln(msg, throwable);
		}
	}
	
	/*
	public static void main(String[] args) {
		
		File searchResult = new File(new File("G:\\"), "searchResult.xml");
		SimpleApkInfo simpleApkInfo = new SimpleApkInfo("com.polarbit.rthunder2play", "1.0.11", "1013");
		ManifestSearch manifestSearch = new ManifestSearch(simpleApkInfo);
		SearchFile searchFile = new SearchFile(new File(new File("F:\\Test\\19"), "AndroidManifest.xml"), searchResult, manifestSearch);
		searchFile.searchAll();
		
		SimpleApkInfo alterInfo = new SimpleApkInfo("com.polarbit.rthunder2play1", "1.0.11", "1013");
		ManifestReplace manifestReplace = new ManifestReplace(simpleApkInfo, alterInfo, new File("F:\\Test\\19\\smali"));
		ReplaceFile replaceFile = new ReplaceFile(searchResult, manifestReplace);
		replaceFile.replaceAll();
		
		Tools.deleteAllFile(searchResult);
	}
	*/
	
	static class ManifestSearch implements SearchImp {
		
		private final Pattern packageNameForMatcher;
		private final Pattern versionNameForMatcher;
		private final Pattern versionCodeForMatcher;
		
		public ManifestSearch(SimpleApkInfo targetInfo) {
			
			String packageNameTag = "package=\"" + targetInfo.getPackageName() + "\"";
			this.packageNameForMatcher = Pattern.compile(packageNameTag);
			String versionCodeTag = "android:versionCode=\"" + targetInfo.getVersionCode() + "\"";
			this.versionCodeForMatcher = Pattern.compile(versionCodeTag);
			String versionNameTag = "android:versionName=\"" + targetInfo.getVersionName() + "\"";
			this.versionNameForMatcher = Pattern.compile(versionNameTag);
		}
		
		@Override
		public List<String> searchAnalysis(String content) {
			
			Matcher matcher = packageNameForMatcher.matcher(content);
			List<String> result = new ArrayList<String>();
			
			if (matcher.find()) {
				result.add(matcher.group());
			}
			
			matcher = versionCodeForMatcher.matcher(content);
			
			if (matcher.find()) {
				result.add(matcher.group());
			}
			
			matcher = versionNameForMatcher.matcher(content);
			
			if (matcher.find()) {
				result.add(matcher.group());
			}
			
			matcher = NAME_FOR_MATCHER.matcher(content);
			
			while (matcher.find()) {
				result.add(matcher.group());
			}
			
			return result;
		}
	}
	
	static class ManifestReplace implements ReplaceImp {

		private final Pattern CLASSNAME_FOR_MATCHER = Pattern.compile("[\\w|$]+");
		
		private SimpleApkInfo pastInfo;
		private SimpleApkInfo alterInfo;
		private File packageDir;
		
		public ManifestReplace(SimpleApkInfo pastInfo, SimpleApkInfo alterInfo, File smaliFileDir) {
			this.pastInfo = pastInfo;
			this.alterInfo = alterInfo;
			this.packageDir = getPackageDir(smaliFileDir, pastInfo.getPackageName());
		}
		
		@Override
		public ReplaceItem replace(SearchItem item) {
			
			String value = item.getValue();
			
			if (value.startsWith("package=")
					&& value.equals("package=\"" + pastInfo.getPackageName() + "\"")) {
				return new ReplaceItem(value, "package=\"" + alterInfo.getPackageName() + "\"");
			} else if (value.startsWith("android:versionCode=")
					&& value.equals("android:versionCode=\"" + pastInfo.getVersionCode() + "\"")) {
				return new ReplaceItem(value, "android:versionCode=\"" + alterInfo.getVersionCode() + "\"");
				
			} else if (value.startsWith("android:versionName=")
					&& value.equals("android:versionName=\"" + pastInfo.getVersionName() + "\"")) {
				return new ReplaceItem(value, "android:versionName=\"" + alterInfo.getVersionName() + "\"");
			}
			
			Matcher matcher = NAME_FOR_MATCHER.matcher(value);
			
			if (matcher.find()) {
				
				String name = matcher.group(1);
				String replacement = analysisName(name);
				if (replacement != null && !"".equals(replacement)) {
					return new ReplaceItem(value, "android:name=\"" + replacement + "\"");
				}
			}
			
			return new ReplaceItem("", "");
		}
		
		private String analysisName(String name) {
			
			if (name.startsWith(".")) {
				File file = new File(packageDir, name.replace(".", "/") + ".smali");
				if (file.exists()) {
					return pastInfo.getPackageName() + name;
				}
			}
			
			Matcher matcher = CLASSNAME_FOR_MATCHER.matcher(name);
			
			if (matcher.find() && name.equals(matcher.group())) {
				File file = new File(packageDir, name + ".smali");
				if (file.exists()) {
					return pastInfo.getPackageName() + "." + name;
				}
			}
			
			return null;
		}
		
		private File getPackageDir(File rootDir, String packageName) {
			
			String relativelyPath = packageName.replace(".", "/");
			
			return new File(rootDir, relativelyPath);
		}
	}
}
