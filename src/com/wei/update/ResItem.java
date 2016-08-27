package com.wei.update;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class ResItem {
	
	private String name;
	private String url;
	private String size;
	private String md5;
	
	public ResItem() {};
	
	public ResItem(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	
	public void setElement(Element element) {
		
		name = element.element("name").attributeValue("value");
		url = element.element("url").attributeValue("value");
		size = element.element("size").attributeValue("value");
		md5 = element.element("md5").attributeValue("value");
	}
	
	public Element getElement() {
		
		Element resItem = DocumentHelper.createElement("item");
		
		resItem.addElement("name").addAttribute("value", name);
		resItem.addElement("url").addAttribute("value", url);
		resItem.addElement("size").addAttribute("value", size);
		resItem.addElement("md5").addAttribute("value", md5);
		
		return resItem;
	}

	@Override
	public String toString() {
		return "name: " + name + ", url: " + url + ", size: " + size + ", md5: " + md5;
	}
}
