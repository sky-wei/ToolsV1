package com.wei.bean;

public class ReplaceItem {

	private String target;
	private String replacement;
	
	public ReplaceItem() {};
	
	public ReplaceItem(String target, String replacement) {
		this.target = target;
		this.replacement = replacement;
	}
	
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getReplacement() {
		return replacement;
	}
	public void setReplacement(String replacement) {
		this.replacement = replacement;
	}
}
