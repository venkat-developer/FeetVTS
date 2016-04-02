package com.i10n.fleet.util;

import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import com.i10n.fleet.datasets.IDataset;


/**
 * 
 * This class has static methods used for debugging purposes
 * 
 * @author vijaybharath
 *
 */
public class Helper {

	private static Logger LOG = Logger.getLogger(Helper.class);
	
	public static void printDataSet(IDataset dataSet){
		printDataSetWithIndent(0, dataSet);
	}
	static void printDataSetWithIndent(int indentCnt, IDataset dataSet){
		if(dataSet == null) return;
		Set<String> keySet = dataSet.keySet();
		Iterator<String> keyIterator = keySet.iterator();

		while(keyIterator.hasNext()){
			String key = keyIterator.next();
			for(int i = 0; i < indentCnt; i++){
				LOG.debug("\t");
			}
			Object value = dataSet.get(key);
			if(value instanceof IDataset){
				LOG.debug(key + ":");
				printDataSetWithIndent(indentCnt + 1, (IDataset)value);
			}
			else{
				LOG.debug(key + ":" + value);
			}
		}

	}
}

