package com.snu.adb.fpgrowh.tskim;


public class Item {
	
	protected int id;
	protected int support;
	protected int gSupport;
	
	public Item(int id, int support) {
		this.id = id;
		this.support = support;
	
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSupport() {
		return support;
	}
	public void setSupport(int support) {
		this.support = support;
	}
	public void incrementSup() {
		this.support++;
	}
	public void decrementSup() {
		this.support--;
	}
	public void incrementSup(int delta) {
		this.support += delta;
	}
	public void decrementSup(int delta) {
		this.support -= delta;
	}
	public int getgSupport() {
		return gSupport;
	}
	public void setgSupport(int gSupport) {
		this.gSupport = gSupport;
	}
	
	public String toString() {
		return "[" +Integer.toString(this.gSupport)+", "+ this.id +"]";
	}
	
}
