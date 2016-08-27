package com.wei.bean;

import java.io.File;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class SignerInfo {
	
	private int id;
	private String keystorePassword;
	private int term;
	private String signerName;
	private String signerPassword;
	private String name;
	private String organization;
	private String city;
	private String province;
	private String code;
	private File filePath;
	private String createTime;
	
	public SignerInfo() {};
	
	public SignerInfo(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKeystorePassword() {
		return keystorePassword;
	}

	public void setKeystorePassword(String keystorePassword) {
		this.keystorePassword = keystorePassword;
	}

	public int getTerm() {
		return term;
	}

	public void setTerm(int term) {
		this.term = term;
	}

	public String getSignerName() {
		return signerName;
	}

	public void setSignerName(String signerName) {
		this.signerName = signerName;
	}

	public String getSignerPassword() {
		return signerPassword;
	}

	public void setSignerPassword(String signerPassword) {
		this.signerPassword = signerPassword;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public File getFilePath() {
		return filePath;
	}

	public void setFilePath(File filePath) {
		this.filePath = filePath;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
	public Element getElement() {
		
		Element signer = DocumentHelper.createElement("signer");
		
		signer.addAttribute("storepass", keystorePassword);
		signer.addAttribute("validity", Integer.toString(term));
		signer.addAttribute("alias", signerName);
		signer.addAttribute("keypass", signerPassword);
		signer.addAttribute("cn", name);
		signer.addAttribute("ou", organization);
		signer.addAttribute("o", organization);
		signer.addAttribute("l", city);
		signer.addAttribute("st", province);
		signer.addAttribute("c", code);
		signer.addAttribute("createTime", createTime);
		
		return signer;
	}
	
	public void setElement(Element element) {
		
		keystorePassword = element.attributeValue("storepass");
		term = Integer.parseInt(element.attributeValue("validity"));
		signerName = element.attributeValue("alias");
		signerPassword = element.attributeValue("keypass");
		name = element.attributeValue("cn");
		organization = element.attributeValue("ou");
		organization = element.attributeValue("o");
		city = element.attributeValue("l");
		province = element.attributeValue("st");
		code = element.attributeValue("c");
		createTime = element.attributeValue("createTime");
	}
	
	public String getOtherInfo() {
		
		return "validity:" + term + ",cn:" + name + ",ou:" + organization + ",o:" + organization
				+ ",l:" + city + ",st:" + province + ",c:" + code;
	}

	@Override
	public String toString() {
		return "id: " + id + ", storepass: " + keystorePassword + ", alias: " + signerName
				+ ", keypass: " + signerPassword + ", validity: " + term;
	}
}
