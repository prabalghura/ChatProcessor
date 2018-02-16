package com.smartapp.nlp;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartapp.nlp.utils.NLPResponseObject;

public class NLPRun {
	
	private static final Logger log = LoggerFactory.getLogger(NLPRun.class);

	public static void main(String[] args) {
		NLPSentencePredictor predictor = new NLPSentencePredictor();
		
		List<NLPResponseObject> responseList = predictor.response("Require a Leave on the 5 Nov .");
        
		if(responseList.isEmpty())
			log.info("kuchh nahi mila");
        for(NLPResponseObject response: responseList) {
        	log.info(response.toString());
        }
        
        responseList = predictor.response("Where's the party tonight .");
        
        if(responseList.isEmpty())
			log.info("kuchh nahi mila");
        for(NLPResponseObject response: responseList) {
        	log.info(response.toString());
        }
	}

}
