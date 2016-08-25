import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class InformationGain {
	HashMap<String, ArrayList<String>> attrMapData;
	HashMap<String, Double> attrInfoGainMapData;
	
	public HashMap<String, ArrayList<String>> mapAttributesData(ArrayList<ArrayList<String>> dataSet) {
		HashMap<String, ArrayList<String>> attrMap = new HashMap<String, ArrayList<String>>();
		
		ArrayList<String> attributeSet = dataSet.get(0);
		
		for(int i = 0; i < attributeSet.size(); i++) {
			for(int j = 1; j < dataSet.size(); j++) {
				if(attrMap.containsKey(attributeSet.get(i))) {
					attrMap.get(attributeSet.get(i)).add(dataSet.get(j).get(i));
				}
				else {
					ArrayList<String> val = new ArrayList<String>();
					val.add(dataSet.get(j).get(i));
					attrMap.put(attributeSet.get(i), val);
				}
			}
		}
		return attrMap;
	}
	
	public double computeEntropyUsingVarianceImpurity(double posCount, double negCount) {
		
		if(posCount == negCount) {
			return 1;
		}
		
		else if(posCount == 0 || negCount == 0) {
			return 0;
		}
		
		else {
			double posProb = posCount / (posCount + negCount);
			double negProb = negCount / (posCount + negCount);
			
			double entropy = posProb * negProb;
			return entropy;
		}
	}
	
	public double computeEntropy(double posCount, double negCount) {
		
		if(posCount == negCount) {
			return 1;
		}
		
		else if(posCount == 0 || negCount == 0) {
			return 0;
		}
		
		else {
			double posProb = posCount / (posCount + negCount);
			double negProb = negCount / (posCount + negCount);
			
			double entropy = ((-posProb) * (Math.log(posProb) / Math.log(2))) + ((-negProb) * (Math.log(negProb) / Math.log(2)));
			return entropy;
		}
	}
	
	public double getInformationGainForAttribute(double posClass, double negClass, double zeroClassPos, double zeroClassNeg, double oneClassPos, double oneClassNeg, boolean isVarianceImpurity) {
		double infoGain;
		
		double classCount = posClass + negClass;
		double zeroClassCount = zeroClassPos + zeroClassNeg;
		double oneClassCount = oneClassPos + oneClassNeg;
		double classEntropy;
		double zeroClassEntropy;
		double oneClassEntropy;
		
		if(isVarianceImpurity == false) {
			 classEntropy = computeEntropy(posClass, negClass);
			 zeroClassEntropy = computeEntropy(zeroClassPos, zeroClassNeg);
			 oneClassEntropy = computeEntropy(oneClassPos, oneClassNeg);
		}
		else {
			 classEntropy = computeEntropyUsingVarianceImpurity(posClass, negClass);
			 zeroClassEntropy = computeEntropyUsingVarianceImpurity(zeroClassPos, zeroClassNeg);
			 oneClassEntropy = computeEntropyUsingVarianceImpurity(oneClassPos, oneClassNeg);
		}
		
		infoGain = classEntropy - (((zeroClassCount / classCount) * zeroClassEntropy) +
				((oneClassCount / classCount) * oneClassEntropy));
		
		return infoGain;
	}
	
	public String mostLikelyAttribute(ArrayList<ArrayList<String>> data, ArrayList<String> attributeList, boolean isVarianceImpurity) {
		String bestAttr = "";
		attrInfoGainMapData = new HashMap<String, Double>();
		attrMapData = mapAttributesData(data);
		
		double posClass = 0; 
		double negClass = 0;
		
		for(String str : attrMapData.get("Class")) {
			if(str.equalsIgnoreCase("1")) {
				posClass++;
			}
			else {
				negClass++;
			}
		}
		
		//excluding the class attribute from the attribute list		
		for(String attr : attributeList.subList(0, attributeList.size() - 1)) {
			ArrayList<String> dataSetOfAttr = attrMapData.get(attr);
			double zeroClassPos = 0;
			double oneClassPos = 0;
			double zeroClassNeg = 0;
			double oneClassNeg = 0;
			
			for(int i = 0; i < dataSetOfAttr.size(); i++) {
				if(dataSetOfAttr.get(i).equalsIgnoreCase("0")) {
					if(attrMapData.get("Class").get(i).equalsIgnoreCase("1")) {
						zeroClassPos++;
					} else {
						zeroClassNeg++;
					}
				} else {
					if(attrMapData.get("Class").get(i).equalsIgnoreCase("1")) {
						oneClassPos++;
					} else {
						oneClassNeg++;
					}
				}
			}
			
			Double attrInformationGain = getInformationGainForAttribute(posClass, negClass, zeroClassPos, zeroClassNeg, oneClassPos, oneClassNeg, isVarianceImpurity);
			attrInfoGainMapData.put(attr, attrInformationGain);
		}
		
		ArrayList<Double> attrValuesList = new ArrayList<Double>(attrInfoGainMapData.values());
		Collections.sort(attrValuesList);
		Collections.reverse(attrValuesList);
		
		for(String key : attrInfoGainMapData.keySet()) {
			if(attrValuesList.get(0).equals(attrInfoGainMapData.get(key))) {
				bestAttr = key;
				break;
			}
		}
		return bestAttr;
	}
	
	public HashMap<String, ArrayList<ArrayList<String>>> getMapDataForBestAttr(String bestAttr, ArrayList<ArrayList<String>> dataSet) {
		HashMap<String, ArrayList<ArrayList<String>>> bestAttrMapData = new HashMap<String, ArrayList<ArrayList<String>>>();
		int bestAttrIdx = dataSet.get(0).indexOf(bestAttr);
		
		// Arranging the dataset corresponding the best attribute's zero class and one class
		for(int i = 1; i < dataSet.size(); i++) {
			if(dataSet.get(i).get(bestAttrIdx).equalsIgnoreCase("0")) {
				if(bestAttrMapData.containsKey("0")) {
					bestAttrMapData.get("0").add(dataSet.get(i));
				}
				else {
					ArrayList<ArrayList<String>> dataList = new ArrayList<ArrayList<String>>();
					dataList.add(dataSet.get(0));
					dataList.add(dataSet.get(i));
					bestAttrMapData.put("0", dataList);
				}
			}
			else {
				if(bestAttrMapData.containsKey("1")) {
					bestAttrMapData.get("1").add(dataSet.get(i));
				}
				else {
					ArrayList<ArrayList<String>> dataList = new ArrayList<ArrayList<String>>();
					dataList.add(dataSet.get(0));
					dataList.add(dataSet.get(i));
					bestAttrMapData.put("1", dataList);
				}
			}
		}
		
		return bestAttrMapData;
	}

}
