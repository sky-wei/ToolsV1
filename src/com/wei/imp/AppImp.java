package com.wei.imp;

import java.io.File;
import java.util.List;

import javax.swing.JFrame;

import com.wei.bean.SignerInfo;
import com.wei.bean.SimpleApkInfo;
import com.wei.bean.UiProperty;

public interface AppImp {
	
	JFrame getCurFrame();
	
	SimpleApkInfo open(File file);
	
	void alterPackageName(SimpleApkInfo pastInfo, SimpleApkInfo alterInfo);
	
	void compileApk(SimpleApkInfo alterApkInfo);
	
	void keystoreSettings();
	
	void setAlterMode(int mode);
	
	void quit();
	
	UiProperty loadUiProperty();
	
	boolean saveUiProperty(UiProperty uiProperty);
	
	void signerFile(File file);
	
	void batchSignerFile(File fileDir);
	
	void verifySigner(File file);

	File buildSignerFile(final SignerInfo signerInfo);
	
	boolean saveSignerInfo(final SignerInfo signerInfo);
	
	List<SignerInfo> getAllSignerInfo();
	
	boolean deleteSignerInfo(final SignerInfo signerInfo);
	
	void setDefaultSigner(final SignerInfo signerInfo);
	
	boolean exportSigner(File exportDir, final SignerInfo signerInfo);
	
	boolean importSigner(File importFile);
	
	boolean decompileFile(File decompileFile);
	
	void compileFile(File compileFile);
}
