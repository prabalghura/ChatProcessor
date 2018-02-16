package com.smartapp.nlp.utils;

public class NLPResponseObject {
	private NLPActionType type;
	
	private NLPMatchLevel matchLevel;
	
	private String input;
	
	private String bestMatch;

	@Override
	public String toString() {
		return "NLPResponseObject [type=" + type + ", matchLevel=" + matchLevel + ", input=" + input + ", bestMatch="
				+ bestMatch + "]";
	}

	public NLPResponseObject(NLPActionType type, NLPMatchLevel matchLevel, String input, String bestMatch) {
		this.type = type;
		this.matchLevel = matchLevel;
		this.input = input;
		this.bestMatch = bestMatch;
	}
	
	public NLPActionType getType() {
		return type;
	}

	public void setType(NLPActionType type) {
		this.type = type;
	}

	public NLPMatchLevel getMatchLevel() {
		return matchLevel;
	}

	public void setMatchLevel(NLPMatchLevel matchLevel) {
		this.matchLevel = matchLevel;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getBestMatch() {
		return bestMatch;
	}

	public void setBestMatch(String bestMatch) {
		this.bestMatch = bestMatch;
	}
}
