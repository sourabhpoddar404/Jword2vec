package org.aksw.word2vecrestful.word2vec;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.aksw.word2vecrestful.utils.Word2VecMath;

/**
 *
 * @author MichaelRoeder
 *
 */
public class Word2VecModel {

  public Map<String, float[]> word2vec;
  public int vectorSize;
  public TreeSet<VectorDimension> sortedVecDimns;
  public int[] dimRngIndxDesc;
  public Word2VecModel(final Map<String, float[]> word2vec, final int vectorSize) {
    this.word2vec = word2vec;
    this.vectorSize = vectorSize;
    this.sortedVecDimns = new TreeSet<>();
    //fetch vector dimension details
    fetchVectorDimensions();
  }
  private void fetchVectorDimensions() {
	  float[] dimVals;
	  VectorDimension vectorDimension;
	  //for each dimension
	  for(int i=0;i<vectorSize;i++) {
		  dimVals = new float[word2vec.size()];
		  //fetch all the values
		  int j=0;
		  for(float[] entryVec : word2vec.values()) {
			  dimVals[j++] =  entryVec[i];
		  }
		  //init and set dimension det
		  vectorDimension = new VectorDimension(i, Word2VecMath.getMin(dimVals), Word2VecMath.getMax(dimVals));
		  this.sortedVecDimns.add(vectorDimension);
	  }
	  this.dimRngIndxDesc = new int[vectorSize];
	  Iterator<VectorDimension> descIt = sortedVecDimns.descendingIterator();
	  int k = 0;
	  while(descIt.hasNext()) {
		  this.dimRngIndxDesc[k++] = descIt.next().getId();
	  }
  }
  public Map<String, float[]> getClosestEntry(float[] inpvec){
	  Map<String, float[]> resMap = new HashMap<>();
	  double minDist = -2;
	  String minWord = null;
	  float[] minVec = null;
	  double tempDist;
	  for(Entry<String, float[]> wordEntry : word2vec.entrySet()) {
		  String word = wordEntry.getKey();
		  float[] wordvec = wordEntry.getValue();
		  tempDist = getEucDistSqr(inpvec, wordvec, minDist);
		  if(tempDist != -1) {
			  minWord = word;
			  minVec = wordvec;
			  minDist = tempDist;
		  }
	  }
	  resMap.put(minWord, minVec);
	  return resMap;
  }
  
  private double getEucDistSqr(float[] arr1, float[] arr2, double minDist) {
	  double dist = 0;
	  for(int i: dimRngIndxDesc) {
		  dist+= Math.pow(arr1[i]-arr2[i], 2);
		  if(minDist!=-2 && dist>minDist)
			  return -1;
	  }
	  return dist;
  }
}
