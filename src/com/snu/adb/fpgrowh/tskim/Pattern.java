package com.snu.adb.fpgrowh.tskim;

import java.util.LinkedList;
import java.util.List;

public class Pattern {
	List<Item> itemSet;
	int sup;
	
	public Pattern() {
		
	}
	
	public Pattern(List<Item> itemSet) {
		init(itemSet);
	}
	
	public Pattern(Item item) {
		List<Item> itemSet = new LinkedList<Item>();
		itemSet.add(item);
		init(itemSet);
	}
	
	private void init(List<Item> itemSet) {
		this.itemSet = itemSet;
		this.sup = Integer.MAX_VALUE;
		for (Item item : itemSet) {
			if (item.support < this.sup) {
				this.sup = item.support; 
			}
		}
	}
	
	public Pattern newPattern(Item item) {
		List<Item> newItemSet = new LinkedList<Item>();
		newItemSet.addAll(itemSet);
		newItemSet.add(item);
		Pattern pattern = new Pattern(newItemSet);
		
		return pattern;
	}
	public void naiveAdd(List<Item> itemSet) {
		this.itemSet.addAll(itemSet);
	}
	public List<Item> getItemSet() {
		return itemSet;
	}
	public void setItemSet(List<Item> itemSet) {
		this.itemSet = itemSet;
	}
	public int getSup() {
		return sup;
	}
	public void setSup(int sup) {
		this.sup = sup;
	}
	
	@Override
	public String toString() {
		String ret;
		String list;
		list ="[";
		int cnt = 0;
		
		
		for (Item item: itemSet) {
			if (cnt == 0)
				list += item.getId();
			else
				list += ", " + item.getId();
			cnt++;
		}
		list += "]";
		
		ret = "Support: " + this.sup + " Set: "+list;
		
		return ret;
	}
}