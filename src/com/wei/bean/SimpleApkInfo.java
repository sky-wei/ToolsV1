package com.wei.bean;

public class SimpleApkInfo {
	
	private String packageName;
	private String versionName;
	private String versionCode;
	
	public SimpleApkInfo() {};
	
	public SimpleApkInfo(String packageName, String versionName, String versionCode) {
		
		this.packageName = packageName;
		this.versionName = versionName;
		this.versionCode = versionCode;
	}
	
	
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	public String getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (obj instanceof SimpleApkInfo) {
			SimpleApkInfo info = (SimpleApkInfo)obj;
			if (info.getPackageName().equals(this.packageName)
					&& info.getVersionCode().equals(this.versionCode)
					&& info.getVersionName().equals(this.versionName)) {
				return true;
			}
		}
		
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return "PackageName: " + packageName + "\nVersionName: " + versionName + "\nVersionCode: " + versionCode;
	}
}
