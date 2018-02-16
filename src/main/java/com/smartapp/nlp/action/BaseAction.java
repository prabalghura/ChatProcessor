package com.smartapp.nlp.action;

import com.smartapp.nlp.utils.NLPActionType;

public class BaseAction {
	
	private double warnLevel;
	
	private double confirmLevel;
	
	private double actionLevel;
	
	private int firstDocNo;
	
	private int lastDocNo;
	
	private NLPActionType type;

	public BaseAction(double warnLevel, double confirmLevel, double actionLevel, int firstDocNo, int lastDocNo,
			NLPActionType type) {
		super();
		this.warnLevel = warnLevel;
		this.confirmLevel = confirmLevel;
		this.actionLevel = actionLevel;
		this.firstDocNo = firstDocNo;
		this.lastDocNo = lastDocNo;
		this.type = type;
	}

	public BaseAction(int firstDocNo, int lastDocNo, NLPActionType type) {
		super();
		this.warnLevel = 80.0;
		this.confirmLevel = 90.0;
		this.actionLevel = 95.0;
		this.firstDocNo = firstDocNo;
		this.lastDocNo = lastDocNo;
		this.type = type;
	}

	public double getWarnLevel() {
		return warnLevel;
	}

	public void setWarnLevel(double warnLevel) {
		this.warnLevel = warnLevel;
	}

	public double getConfirmLevel() {
		return confirmLevel;
	}

	public void setConfirmLevel(double confirmLevel) {
		this.confirmLevel = confirmLevel;
	}

	public double getActionLevel() {
		return actionLevel;
	}

	public void setActionLevel(double actionLevel) {
		this.actionLevel = actionLevel;
	}

	public int getFirstDocNo() {
		return firstDocNo;
	}

	public void setFirstDocNo(int firstDocNo) {
		this.firstDocNo = firstDocNo;
	}

	public int getLastDocNo() {
		return lastDocNo;
	}

	public void setLastDocNo(int lastDocNo) {
		this.lastDocNo = lastDocNo;
	}

	public NLPActionType getType() {
		return type;
	}

	public void setType(NLPActionType type) {
		this.type = type;
	}
}
