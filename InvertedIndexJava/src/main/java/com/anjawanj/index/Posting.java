package com.anjawanj.index;
import java.io.Serializable;
import java.util.ArrayList;

public class Posting implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer docID;
	private Integer termFrequency;
	private String documentName;
	private ArrayList<Integer> lineNos = new ArrayList<>();
	
	public ArrayList<Integer> getLineNos() {
		return lineNos;
	}
	public void setLineNos(Integer lineNo) {
		this.lineNos.add(lineNo);
	}
	public String getDocumentName() {
		return documentName;
	}
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}
	public Integer getDocID() {
		return docID;
	}
	public void setDocID(Integer docID) {
		this.docID = docID;
	}
	public Integer getTermFrequency() {
		return termFrequency;
	}
	public void setTermFrequency(Integer termFrequency) {
		this.termFrequency = termFrequency;
	}

}
