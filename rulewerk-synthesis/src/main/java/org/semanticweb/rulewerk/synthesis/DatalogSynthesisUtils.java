package org.semanticweb.rulewerk.synthesis;

import java.util.ArrayList;
import java.util.List;

public class DatalogSynthesisUtils {
	public static <T> List<List<T>> split(List<T> list, int numberOfParts) {
		List<List<T>> numberOfPartss = new ArrayList<>(numberOfParts);
		int size = list.size();
		int sizePernumberOfParts = (int) Math.ceil(((double) size) / numberOfParts);
		int leftElements = size;
		int i = 0;
		while (i < size && numberOfParts != 0) {
			numberOfPartss.add(list.subList(i, i + sizePernumberOfParts));
			i = i + sizePernumberOfParts;
			leftElements = leftElements - sizePernumberOfParts;
			sizePernumberOfParts = (int) Math.ceil(((double) leftElements) / --numberOfParts);
		}
		return numberOfPartss;
	}
	
	public static <T> List<List<T>> split2(List<T> list, int numberOfParts) {
		List<List<T>> numberOfPartss = new ArrayList<>(numberOfParts);
		float avg = list.size() / (float) numberOfParts;
		float last = 0;
        while (last < list.size()) {
            numberOfPartss.add(list.subList((int) last,(int) (last+avg)));
            last += avg;
        }
		return numberOfPartss;
	}
}
