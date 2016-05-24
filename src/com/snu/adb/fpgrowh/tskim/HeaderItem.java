package com.snu.adb.fpgrowh.tskim;

import java.util.LinkedList;
import java.util.List;

public class HeaderItem extends Item{

	protected LinkedList<Node> list;
	public HeaderItem(int id, int support) {
		super(id, support);
		this.gSupport = support;
		this.list = new LinkedList<Node>();
	}
	public LinkedList<Node> getList() {
		return list;
	}
	public void setList(LinkedList<Node> list) {
		this.list = list;
	}
	
	public void appendNewNode(Node node) {
		list.add(node);
	}
	

	
	public List<List<Item>> project() {
		List<List<Item>> ret = new LinkedList<List<Item>>();
		
		for (Node node : this.list) {
			if (node.parent.parent != null) { 
				ret.add(node.parent.project(node.support));
			}
		}
		return ret;
	}
}
