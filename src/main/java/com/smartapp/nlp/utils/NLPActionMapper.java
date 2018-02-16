package com.smartapp.nlp.utils;

import java.util.ArrayList;
import java.util.List;
import com.smartapp.nlp.action.BaseAction;

public class NLPActionMapper {
	
	public static List<BaseAction> actionList() {
		List<BaseAction> list = new ArrayList<BaseAction>();
		
		list.add(new BaseAction(97162, 97234, NLPActionType.LEAVETYPE));
		
		return list;
	}
}
