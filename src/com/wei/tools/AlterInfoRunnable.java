package com.wei.tools;

import java.io.File;
import java.io.IOException;
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

public class AlterInfoRunnable implements Runnable {

	private File alterFile;
	private File targetDir;
	private File compileFile;
	private File complieRawResFile;
	private File complieResFile;
	private File complieSmaliFile;
	private SimpleApkInfo pastInfo;
	private SimpleApkInfo alterInfo;
	private OutputImp outputImp;
	
	public AlterInfoRunnable(File alterFile, File targetDir, SimpleApkInfo pastInfo, 
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
			complieRawResFile = new File(compileFile, "RawRes");
			complieResFile = new File(compileFile, "res");
			complieSmaliFile = new File(compileFile, "smali");
			
			if (Tools.fileExists(compileFile)) {
				outputInfoln("Delete Used File!\nFile: " + compileFile);
				Tools.deleteAllFile(compileFile);
				outputInfoln("Delete Used File Complete!");
			}
			
			FileHandle fileHandle = new FileHandle(alterFile, targetDir);
			fileHandle.copyAll();
			
			outputInfoln("Alter External Res...");
			alterExternalRes();
			outputInfoln("Replace Res File...");
			alterResFile();
			outputInfoln("Replace Smali File...");
			replaceSmaliFile();
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
		
		ManifestReplace manifestReplace = new ManifestReplace(pastInfo, alterInfo);
		ReplaceFile replaceFile = new ReplaceFile(searchResult, manifestReplace);
		replaceFile.replaceAll();
		
		Tools.deleteAllFile(searchResult);
	}
	
	private void replaceSmaliFile() throws IOException {
		
		File searchResult = new File(compileFile, "searchResult.xml");
		LPackageNameSearch smaliSearch = new LPackageNameSearch(pastInfo.getPackageName());
		SearchFile searchFile = new SearchFile(complieSmaliFile, searchResult, smaliSearch);
		searchFile.searchAll();
		
		LPackageNameReplace smaliReplace = new LPackageNameReplace(alterInfo.getPackageName());
		ReplaceFile replaceFile = new ReplaceFile(searchResult, smaliReplace);
		replaceFile.replaceAll();
		
		String packageName = pastInfo.getPackageName();
		String packagePath = packageName.replace(".", "/");
		File smaliPath = new File(complieSmaliFile, packagePath);
		
		if (Tools.fileExists(smaliPath)) {
			
			packageName = alterInfo.getPackageName();
			packagePath = packageName.replace(".", "/");
			File alterSmaliPath = new File(complieSmaliFile, packagePath);
			File tempAlterSmaliPath = new File(compileFile, "/tempSmali/" + packageName);
			if (Tools.createDir(tempAlterSmaliPath)) {
				
				FileHandle fileHandle = new FileHandle(smaliPath, tempAlterSmaliPath);
				fileHandle.setRmSourceDir(true);
				fileHandle.copyAll();
				
				Tools.deleleAllFileAndNullDir(smaliPath);
				Tools.createDir(alterSmaliPath);
				
				fileHandle = new FileHandle(tempAlterSmaliPath, alterSmaliPath);
				fileHandle.setRmSourceDir(true);
				fileHandle.copyAll();
				
				Tools.deleleAllFileAndNullDir(tempAlterSmaliPath);
			}
		}
		
		Tools.deleteAllFile(searchResult);
	}
	
	private void alterResFile() {
		
		File searchResult = new File(compileFile, "searchResult.xml");
		PackageNameSearch xmlSearch = new PackageNameSearch(pastInfo.getPackageName());
		SearchFile searchFile = new SearchFile(complieResFile, searchResult, xmlSearch);
		searchFile.setFilter(new FileNameExtensionFilter("xml"));
		searchFile.searchAll();
		
		PackageNameReplace xmlReplace = new PackageNameReplace(alterInfo.getPackageName());
		ReplaceFile replaceFile = new ReplaceFile(searchResult, xmlReplace);
		replaceFile.replaceAll();
		
		Tools.deleteAllFile(searchResult);
	}
	
	private void alterExternalRes() throws IOException {
		
		String packageName = pastInfo.getPackageName();
		String packagePath = packageName.replace(".", "/");
		File pastExternalRes = new File(complieRawResFile, packagePath);
		
		if (Tools.fileExists(pastExternalRes)) {
			
			String alterPackageName = alterInfo.getPackageName();
			String alterPackagePath = alterPackageName.replace(".", "/");
			File alterExternalRes = new File(complieRawResFile, alterPackagePath);
			File tempAlterResPath = new File(compileFile, "tempRes/" + alterPackageName);
			if (Tools.createDir(tempAlterResPath)) {
				
				FileHandle fileHandle = new FileHandle(pastExternalRes, tempAlterResPath);
				fileHandle.setRmSourceDir(true);
				fileHandle.copyAll();
				
				Tools.deleleAllFileAndNullDir(pastExternalRes);
				Tools.createDir(alterExternalRes);
				
				fileHandle = new FileHandle(tempAlterResPath, alterExternalRes);
				fileHandle.setRmSourceDir(true);
				fileHandle.copyAll();
				
				Tools.deleleAllFileAndNullDir(tempAlterResPath);
			}
		}
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
	
	class ManifestSearch implements SearchImp {

		private final Pattern packageNameForMatcher;
		private final Pattern versionNameForMatcher;
		private final Pattern versionCodeForMatcher;
		
		public ManifestSearch(SimpleApkInfo targetInfo) {
			
			String packageNameTag = targetInfo.getPackageName();
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
			
			return result;
		}
	}
	
	class ManifestReplace implements ReplaceImp {

		private SimpleApkInfo pastInfo;
		private SimpleApkInfo alterInfo;
		
		public ManifestReplace(SimpleApkInfo pastInfo, SimpleApkInfo alterInfo) {
			this.pastInfo = pastInfo;
			this.alterInfo = alterInfo;
		}
		
		@Override
		public ReplaceItem replace(SearchItem item) {
			
			String value = item.getValue();
			
			if (value.equals(pastInfo.getPackageName())) {
				return new ReplaceItem(value, alterInfo.getPackageName());
			} else if (value.equals("android:versionCode=\"" + pastInfo.getVersionCode() + "\"")) {
				return new ReplaceItem(value, "android:versionCode=\"" + alterInfo.getVersionCode() + "\"");
			} else if (value.equals("android:versionName=\"" + pastInfo.getVersionName() + "\"")) {
				return new ReplaceItem(value, "android:versionName=\"" + alterInfo.getVersionName() + "\"");
			}
			
			return new ReplaceItem("", "");
		}
	}
	
	class LPackageNameSearch implements SearchImp {

		private final String treatName;
		private final Pattern forMatcher;
		
		public LPackageNameSearch(String packageName) {
			
			this.treatName = "L" + packageName.replace(".", "/");
			this.forMatcher = Pattern.compile(this.treatName);
		}
		
		@Override
		public List<String> searchAnalysis(String content) {
			
			Matcher matcher = forMatcher.matcher(content);
			List<String> result = new ArrayList<String>();
			
			if (matcher.find()) {
				result.add(matcher.group());
			}
			
			return result;
		}
	}
	
	class LPackageNameReplace implements ReplaceImp {

		private final String treatName;
		
		public LPackageNameReplace(String packageName) {
			
			this.treatName = "L" + packageName.replace(".", "/");
		}
		
		@Override
		public ReplaceItem replace(SearchItem item) {
			
			return new ReplaceItem(item.getValue(), treatName);
		}
	}
	
	class PackageNameSearch implements SearchImp {

		private final Pattern forMatcher;
		
		public PackageNameSearch(String packageName) {
			
			this.forMatcher = Pattern.compile(packageName);
		}
		
		@Override
		public List<String> searchAnalysis(String content) {
			
			Matcher matcher = forMatcher.matcher(content);
			List<String> result = new ArrayList<String>();
			
			if (matcher.find()) {
				result.add(matcher.group());
			}
			
			return result;
		}
	}
	
	class PackageNameReplace implements ReplaceImp {

		private final String packageName;
		
		public PackageNameReplace(String packageName) {
			
			this.packageName = packageName;
		}
		
		@Override
		public ReplaceItem replace(SearchItem item) {
			
			return new ReplaceItem(item.getValue(), packageName);
		}
	}
}
