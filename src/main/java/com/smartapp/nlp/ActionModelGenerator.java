package com.smartapp.nlp;

import java.io.File;

import opennlp.addons.modelbuilder.DefaultModelBuilderUtil;

/**
 * @author rajendrag
 *This class uses the OpenNLP model generator add-on to generate a model based
 *on the annotated sentences and known entities proviced in the text/input files.
 *The model generated will be more efficient with larger training data.
 */
public class ActionModelGenerator {

	private static final String ACTION = "action";
	private static final String ACTIONMODEL = "actionmodel";

	public static void main(String[] args) {

		/**
	     * establish a file to put sentences in
	     */
	    File sentences = new File(ACTIONMODEL + File.separator + "sentences.txt");

	    /**
	     * establish a file to put your NER hits in (the ones you want to keep based on prob)
	     */
	    File knownEntities = new File(ACTIONMODEL + File.separator + "knownEntities.txt");

	    /**
	     * establish a BLACKLIST file to put your bad NER hits in (also can be based on prob)
	     */
	    File blacklistedentities = new File(ACTIONMODEL + File.separator + "actionblentities.txt");

	    /**
	     * establish a file to write your annotated sentences to
	     */
	    File annotatedSentences = new File(ACTIONMODEL + File.separator + "annotatedSentences.txt");

	    /**
	     * establish a file to write your model to
	     */
	    File theModel = new File(ACTIONMODEL + File.separator + "actionModel.bin");

	    /**
	     * THIS IS WHERE THE ADDON IS GOING TO USE THE FILES (AS IS) TO CREATE A NEW MODEL. YOU SHOULD NOT HAVE TO RUN THE FIRST PART AGAIN AFTER THIS RUNS, JUST NOW PLAY WITH THE
	     * KNOWN ENTITIES AND BLACKLIST FILES AND RUN THE METHOD BELOW AGAIN UNTIL YOU GET SOME DECENT RESULTS (A DECENT MODEL OUT OF IT).
	     */
	    DefaultModelBuilderUtil.generateModel(sentences, knownEntities, blacklistedentities, theModel, annotatedSentences, ACTION, 3);
	

	}

}
