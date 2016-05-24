package com.snu.adb.fpgrowh.tskim;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Node extends Item{
	Map<Integer, Node> childMap;
	public Node parent;

	public Node(int id, Node parent) {
		super(id, 1);
		this.parent = parent;
		this.childMap = new HashMap<Integer, Node>();
	}

	/*
	 * LinkedList
	 * */
	public void insertTrx(List<Item> toInsertList, Map<Integer, HeaderItem> fListMap) {
		if (toInsertList.size() == 0)
			return;
		
		Item toInsert = toInsertList.remove(0);
		int id = toInsert.getId();
		Node child = childMap.get(id);
		HeaderItem hItem;
		if (child == null) {
			child = new Node(id, this);
			childMap.put(id, child);
			child.setSupport(toInsert.support);
			hItem = fListMap.get(id);
			hItem.appendNewNode(child);
		} else {
			child.incrementSup(toInsert.support);
		}
		
		child.insertTrx(toInsertList, fListMap);
	}


	public Map<Integer, Node> getChildMap() {
		return childMap;
	}

	public void setChildMap(Map<Integer, Node> child) {
		this.childMap = child;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}
	
	public boolean hasSinglePath() {
		if (childMap.size() == 0) {
			return true;
		} else if (childMap.size() == 1) {
			Iterator<Node> iter = childMap.values().iterator();
			return iter.next().hasSinglePath();
		} else {
			return false;
		}
	}
	
	/**
	 * 
	 * @return projected trx
	 */
	public List<Item> project(int support) {
		List<Item> list = new LinkedList<Item>();
		Node cur;
		cur = this;
		
		while(cur.parent != null) {
			Item item = new Item(cur.id, support);
			list.add(item);
			cur = cur.parent;
		}
		
		return list;
	}
	
}
