package com.smartapp.nlp;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;

public class NLPWrapper {
	
	private static final String EN_NER_DATE_BIN = "en-ner-date.bin";
	private static final String SENTENCE_ANALYSER_BIN = "sentenceAnalyser.bin";
	private static final String EVENT = "EVENT";
	private static final String QUERY = "QUERY";
	private static final String EN_POS_MAXENT_BIN = "en-pos-maxent.bin";
	private static final String UTF_8 = "UTF-8";
	private static final String SENT = "sent";
	private static final String EN_POS_PERCEPTRON_BIN = "en-pos-perceptron.bin";
	private static final String EN_SENT_BIN = "en-sent.bin";
	private static final String EN_TOKEN_BIN = "en-token.bin";
	private static final String ACTION_MODEL_BIN = "actionModel.bin";
	
	/**
	 * @param paragraph
	 * @return
	 * @throws InvalidFormatException
	 * @throws IOException
	 * This method splits a paragraph into sentences using the sentenceDegection model provided by OpenNLP
	 */
	public static String[] sentenceDetect(String paragraph) throws InvalidFormatException,IOException {
		InputStream is = new FileInputStream(EN_SENT_BIN);
		SentenceModel model = new SentenceModel(is);
		SentenceDetectorME sdetector = new SentenceDetectorME(model);
		String sentences[] = sdetector.sentDetect(paragraph);
		return sentences;
	}
	
	public static void main(String[] args) {
		try {
			String[] message = new String[]{"How did you come up with this crazy idea?"};
			documentCategorizer(message);
			parseMessage("The butler hit the intruder with a hammer");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void parseMessage(String message)
	{
		List<String> actions = new ArrayList<>();
		try {
			actions = parseMessageForActions(message);
			for(String action : actions) {
				boolean isQuery = determineIfQueryWithPerceptronPOSTTagging(message,action);
				determineIfQueryWithmaxentPOSTTagging(message,action);
				if(isQuery) {
					System.out.println(QUERY);
				}else {
					System.out.println(EVENT);	
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * @param message
	 * @throws IOException
	 * This method processes the messages from the chat window to find the action that fits a particular use case
	 */
	public static List<String> parseMessageForActions(String message) throws IOException {
		List<String> actions = new ArrayList<>();
 		String[] sentence = tokenize(message);
		InputStream is = new FileInputStream(ACTION_MODEL_BIN);
		TokenNameFinderModel model = new TokenNameFinderModel(is);
		NameFinderME nameFinder = new NameFinderME(model);
		Span nameSpans[] = nameFinder.find(sentence);
		is.close();
		for(Span s: nameSpans)
			actions.add(sentence[s.getStart()]);
		return actions;
	}
	
	/**
	 * @param paragraph
	 * @return
	 * @throws IOException
	 * This method tokenizes a sentence in to tokens using the English token model provided by OpenNLP
	 */
	public static String[] tokenize(String paragraph) throws IOException {
		
		InputStream is = new FileInputStream(EN_TOKEN_BIN);
		TokenizerModel model = new TokenizerModel(is);
		Tokenizer tokenizer = new TokenizerME(model);
		String tokens[] = tokenizer.tokenize(paragraph);
		is.close();
		return tokens;
		
	}
	
	/**
	 * @param message
	 * @throws IOException
	 * This method tags an English sentence with the POS using the perceptron model
	 */
	public static boolean determineIfQueryWithPerceptronPOSTTagging(String message, String action) throws IOException {
		boolean isQuery = true;
		POSModel model = new POSModelLoader().load(new File(EN_POS_PERCEPTRON_BIN));
		PerformanceMonitor perfMon = new PerformanceMonitor(System.err, SENT);
		POSTaggerME tagger = new POSTaggerME(model);
		InputStreamFactory isf = new InputStreamFactory() {
            public InputStream createInputStream() throws IOException {
                return new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8.name()));
            }
        };
		ObjectStream<String> lineStream = new PlainTextByLineStream(isf,UTF_8);
		perfMon.start();
		String line;
		while ((line = lineStream.read()) != null) {
			String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE.tokenize(line);
			String[] tags = tagger.tag(whitespaceTokenizerLine);
			POSSample sample = new POSSample(whitespaceTokenizerLine, tags);
			String[] sentences = sample.getSentence();
			String[] posTags = sample.getTags();
			boolean actionFound = false;
			for(int i=0; i<sentences.length ; i++) {
				if(sentences[i].equals(action)) {
					if(posTags[i].contains("NN")) {
						actionFound = true;
					}
				}
			}
			if(actionFound) {
				for(int i=0;i<tags.length;i++) {
					if(tags[i].contains("VB")) {
						isQuery = false;
					}
				}
			}
			System.out.println(sample.toString());
			perfMon.incrementCounter();
		}
		perfMon.stopAndPrintFinalResult();
		return isQuery;
	}
	
	public static void dateFinder() throws IOException {
		String []sentence = new String[]{
			    "Taking a leave on. 19.1.2018.",
			    "a",
			    "on",
			    ".",
			    "19.1.2018.",
			    };
		InputStream is = new FileInputStream(EN_NER_DATE_BIN);
		TokenNameFinderModel model = new TokenNameFinderModel(is);
		is.close();
		NameFinderME nameFinder = new NameFinderME(model);
		Span nameSpans[] = nameFinder.find(sentence);
		for(Span s: nameSpans)
			System.out.println(s.toString());
	}
	
	public static boolean determineIfQueryWithmaxentPOSTTagging(String message, String action) throws IOException {
		boolean isQuery = true;
		POSModel model = new POSModelLoader().load(new File(EN_POS_MAXENT_BIN));
		PerformanceMonitor perfMon = new PerformanceMonitor(System.err, SENT);
		POSTaggerME tagger = new POSTaggerME(model);
		InputStreamFactory isf = new InputStreamFactory() {
            public InputStream createInputStream() throws IOException {
                return new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8.name()));
            }
        };
		ObjectStream<String> lineStream = new PlainTextByLineStream(isf,UTF_8);
		perfMon.start();
		String line;
		while ((line = lineStream.read()) != null) {
			String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE.tokenize(line);
			String[] tags = tagger.tag(whitespaceTokenizerLine);
			POSSample sample = new POSSample(whitespaceTokenizerLine, tags);
			String[] sentences = sample.getSentence();
			String[] posTags = sample.getTags();
			boolean actionFound = false;
			for(int i=0; i<sentences.length ; i++) {
				if(sentences[i].equals(action)) {
					if(posTags[i].contains("NN")) {
						actionFound = true;
					}
				}
			}
			if(actionFound) {
				for(int i=0;i<tags.length;i++) {
					if(tags[i].contains("VB")) {
						isQuery = false;
					}
				}
			}
			System.out.println(sample.toString());
			perfMon.incrementCounter();
		}
		perfMon.stopAndPrintFinalResult();
		return isQuery;
	}
	
	public static void documentCategorizer(String[] chat) {
		InputStream is;
		try {
			is = new FileInputStream(SENTENCE_ANALYSER_BIN);
			DoccatModel model = new DoccatModel(is);
			DocumentCategorizerME dcm = new DocumentCategorizerME(model);
			double[] outcomes = dcm.categorize(chat);
			dcm.getCategory(1);
			String category = dcm.getBestCategory(outcomes);
			System.out.println(category);
		}catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
//	public static void test1(String message) throws Exception {
//		String[] sentence = tokenize("Will take a leave tomorrow.");
//	    final String modelPath = "../socialnet/models/tag.bin.gz";
//	    final String dictFile = "/home/rajendrag/Desktop/nlptest";
//	    InputStream is = new FileInputStream(dictFile);
//	    Dictionary dict = Dictionary.parseOneEntryPerLine(new InputStreamReader(is));
//	    StringList stlist = new StringList(sentence);
//	    boolean present = dict.contains(stlist);
//	    System.out.println(present);
//	}

}
