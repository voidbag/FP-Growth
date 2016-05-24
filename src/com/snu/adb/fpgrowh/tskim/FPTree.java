package com.snu.adb.fpgrowh.tskim;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

public class FPTree {
	private Node treeRoot;
	private Map<Integer, HeaderItem> fListMap;
	private List<HeaderItem> fList;
	private Comparator<Item> gSupComparator;
	private static Comparator<Item> gIdComparator;
	private static Comparator<Pattern> gPatternComparator;
	private int minSup;
	private static Map<Integer, List<Pattern>> resultMap = new TreeMap<Integer, List<Pattern>>();
	
	private static String inputPath;
	private static String outputPath;
	private static int gMinSup;
	
	public static void main(String [] args) {
		FPTree.inputPath = "983.txt";
		//FPTree.inputPath = "test.txt";
		FPTree.outputPath = "output-983-support_28.txt";
		FPTree.gMinSup = 28;
		
		FPTree.gIdComparator = new Comparator<Item>() {
			@Override
			public int compare(Item o1, Item o2) {
				return o1.id - o2.id;
			}
		};
		
		FPTree.gPatternComparator = new Comparator<Pattern>() {
			@Override
			public int compare(Pattern o1, Pattern o2) {
				Iterator<Item> iter1, iter2;
				Item item1;
				Item item2;
				int ret;
				
				iter1 = o1.getItemSet().iterator();
				iter2 = o2.getItemSet().iterator();
				
				ret = 0;
				while(iter1.hasNext()) {
					item1 = iter1.next();
					item2 = iter2.next();
					
					ret = item1.id - item2.id;
					if (ret != 0)
						return ret;
				}
				return ret;
			}
		};
		
		
		long start = System.currentTimeMillis();
		
		try {
			doFPGrowth();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
		 System.out.println((writeAll(start) - start) + "(ms)");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static long writeAll(long start) throws IOException {
		PrintWriter bOut = new PrintWriter(new BufferedWriter(new FileWriter(FPTree.outputPath)));
		bOut.println("Data mining");
		bOut.println("[FP-Tree algorithm]");
		bOut.println("Taeksang Kim");
		bOut.println("Input file: "+ FPTree.inputPath);
		bOut.println("Minimum support: "+ FPTree.gMinSup);
		long t2;

		for (Entry<Integer, List<Pattern>> entry: resultMap.entrySet()) {
			bOut.println();
			bOut.println("====L" + entry.getKey() + "====");
			
			for (Pattern pattern : entry.getValue()) {
				pattern.getItemSet().sort(gIdComparator);
			}
			entry.getValue().sort(gPatternComparator);
			for (Pattern pattern : entry.getValue()) {
				bOut.println(pattern.toString());
			}
		}
		
		t2 = System.currentTimeMillis();
		bOut.println();
		bOut.println(" "+(t2-start)+"(ms)");
		
		bOut.flush();
		bOut.close();
		return t2;

	}
	
	private static void doFPGrowth() throws FileNotFoundException {
		FPTree mainTree;
		Scanner scanner, trxScanner;
		List<Item> trx;
		scanner = new Scanner(new BufferedInputStream(new FileInputStream(new File(FPTree.inputPath))));
		
		mainTree = new FPTree(FPTree.gMinSup);
		System.out.println("Building entire FP-tree...");
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			trxScanner = new Scanner(line);
			trx = new LinkedList<Item>();
			
			while(trxScanner.hasNextInt()) {
				trx.add(new Item(trxScanner.nextInt(), 1));
			}
			if (trx.size() > 0) {
				mainTree.addNewTrx(trx);
			}
		}
		
		mainTree.buildFListFinish();
		
		scanner.close();
		scanner = new Scanner(new BufferedInputStream(new FileInputStream(new File(FPTree.inputPath))));
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			trxScanner = new Scanner(line);
			trx = new LinkedList<Item>();
			
			while(trxScanner.hasNextInt()) {
				trx.add(new Item(trxScanner.nextInt(), 1));
			}
			
			if (trx.size() > 0) {
				mainTree.grow(trx);
			}
		}
		scanner.close();
		System.out.println("Building entire FP-tree completed!");
		
		System.out.println("Start mining!");
		fpTree(mainTree, new LinkedList<Item>());
	}
	
	public FPTree(int minSup) {
		this.minSup = minSup;
		this.treeRoot = new Node(0, null);
		this.fListMap = new TreeMap<Integer, HeaderItem>();
		this.gSupComparator = new Comparator<Item>() {
			@Override
			public int compare(Item o1, Item o2) {
				int ret;
				
				ret = o2.gSupport - o1.gSupport;
				if (ret == 0)
					ret = o1.id - o2.id;
				
				return ret;
			}
		};
		
	}
	
	public void addNewTrx(List<Item> trx) {
		HeaderItem hItem;
		
		for (Item item : trx) {
			hItem = this.fListMap.get(item.id); 
			if (hItem == null) {
				hItem = new HeaderItem(item.id, item.support);
				this.fListMap.put(item.id, hItem);
			} else {
				hItem.incrementSup(item.support);
				hItem.setgSupport(hItem.support);
			}
		}
	}
	
	public void buildFListFinish() {
		int stopIdx;
		ArrayList<HeaderItem> localFList = new ArrayList<HeaderItem>();
		List<HeaderItem> toRemove;
		
		localFList.addAll(fListMap.values());
		localFList.sort(gSupComparator);
		
		stopIdx = localFList.size();//bug
		for(int i = 0; i < localFList.size(); i++) {
			HeaderItem item = localFList.get(i);
			if (item.support < this.minSup) {
				stopIdx = i;
				break;
			}
		}
		
		this.fList = localFList.subList(0, stopIdx);
	
		toRemove = localFList.subList(stopIdx, localFList.size());
		
		for (HeaderItem item : toRemove) {
			fListMap.remove(item.id);
		}
		
	}
	
	/**
	 * 
	 * @param list must be LinkedList
	 */
	public void grow(List<Item> trx) {
		HeaderItem hItem;
		Item item;
		Iterator<Item> iter = trx.iterator();
		
		while(iter.hasNext()) {
			item = iter.next();
			hItem = fListMap.get(item.id);
			
			if (hItem != null && hItem.support >= this.minSup) { //redundant	
				item.gSupport = hItem.support;	
			} else {
				iter.remove(); /*prune*/
			}
		}
		
		trx.sort(gSupComparator);
		treeRoot.insertTrx(trx, fListMap);
	}
	
	
	public boolean hasSinglePath() {	
		return this.treeRoot.hasSinglePath();
	}
	
	public List<Item> getSingleNodeList() {
		List<Item> singleList = new LinkedList<Item>();
		Node node = this.treeRoot;
		
		while(node.childMap.values().size() == 1) {
			node = node.childMap.values().iterator().next();
			singleList.add(node);
		}
		return singleList;
	}
	
	private static void generateSubSetPattern(List<Item> input, List<Item> alpha) {
		LinkedList<Item> baseList = new LinkedList<Item>(input);
		LinkedList<Pattern> ret = new LinkedList<Pattern>(); 
		LinkedList<Pattern> tmpList;
		
		for (Item item : baseList) {
			Pattern newPattern;
			tmpList = new LinkedList<Pattern>();
			for (Pattern pattern : ret){
				newPattern = pattern.newPattern(item);
				tmpList.add(newPattern);
			}
			ret.addAll(tmpList);
			ret.add(new Pattern(item));
		}
		
		for (Pattern pattern : ret) {
			pattern.naiveAdd(alpha);
			generatePattern(pattern);
		}
	}
	
	public static void generatePattern(Pattern pattern) {
		List<Pattern> list = resultMap.get(pattern.itemSet.size());
		
		if (list == null) {
			list = new LinkedList<Pattern>();
			resultMap.put(pattern.itemSet.size(), list);
		}
		
		list.add(pattern);
	}
	
	/**
	 * 
	 * @param fpTree
	 * @param alpha not null
	 */
	public static void fpTree(FPTree fpTree, List<Item> alpha) {
		if (fpTree.hasSinglePath()){
			if (fpTree.treeRoot.childMap.size() != 0) {
				/*generate every subset*/
				/*project */
				
				List<Item> trx = fpTree.getSingleNodeList();
				generateSubSetPattern(trx, alpha);
			}
			
		} else {
			ListIterator<HeaderItem> iter =  fpTree.fList.listIterator(fpTree.fList.size());
			while(iter.hasPrevious()) {
				HeaderItem hItem = iter.previous();
				List<Item> beta = new ArrayList<Item>(alpha);
				beta.add(hItem);
				
				Pattern pattern = new Pattern();
				pattern.setItemSet(beta);
				pattern.setSup(hItem.support);
				generatePattern(pattern); //
				
				/*generate beta pattern with support hItem.support*/
				FPTree betaTree = new FPTree(fpTree.minSup);
				
				List<List<Item>> trxList = hItem.project();
				for (List<Item> trx : trxList)
					betaTree.addNewTrx(trx);
				
				betaTree.buildFListFinish();
				
				for (List<Item> trx : trxList) {
					betaTree.grow(trx);
				}
				
				FPTree.fpTree(betaTree, beta);
			}			
		}
	}
	
	public Node getTreeRoot() {
		return this.treeRoot;
	}
}