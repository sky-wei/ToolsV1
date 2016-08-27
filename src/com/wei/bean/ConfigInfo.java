package com.wei.bean;

import java.io.File;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class ConfigInfo {
	
	private File keystoreFile;
	private String keystorePassword;
	private String name;
	private String password;
	
	public ConfigInfo() {}
	
	public File getKeystoreFile() {
		return keystoreFile;
	}

	public void setKeystoreFile(File keystoreFile) {
		this.keystoreFile = keystoreFile;
	}

	public String getKeystorePassword() {
		return keystorePassword;
	}

	public void setKeystorePassword(String keystorePassword) {
		this.keystorePassword = keystorePassword;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public Element getElement() {
		
		Element config = DocumentHelper.createElement("config");
		
		config.addAttribute("keystoreFile", keystoreFile.getPath());
		config.addAttribute("keystorePassword", keystorePassword);
		config.addAttribute("name", name);
		config.addAttribute("password", password);
		
		return config;
	}
	
	public void setElement(Element element) {
		
		keystoreFile = new File(element.attributeValue("keystoreFile"));
		keystorePassword = element.attributeValue("keystorePassword");
		name = element.attributeValue("name");
		password = element.attributeValue("password");
	}

	@Override
	public String toString() {
		return "KeystoreFile: " + keystoreFile + ",KeystorePassword: " + keystorePassword + ",Name: " + name + ",Password: " + password;
	}
}
