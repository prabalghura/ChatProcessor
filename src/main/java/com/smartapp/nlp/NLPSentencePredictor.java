package com.smartapp.nlp;

import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache;
import org.deeplearning4j.text.documentiterator.LabelsSource;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartapp.nlp.action.BaseAction;
import com.smartapp.nlp.utils.NLPActionMapper;
import com.smartapp.nlp.utils.NLPMatchLevel;
import com.smartapp.nlp.utils.NLPResponseObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Prabal Ghura
 */
public class NLPSentencePredictor {

	public static final String ACTIONMODEL = "actionmodel";
	
	public static final String filePath = ACTIONMODEL + File.separator + "raw_sentences.txt";
	
    private static final Logger log = LoggerFactory.getLogger(NLPSentencePredictor.class);
    
    private ParagraphVectors vec;
    
	public NLPSentencePredictor() {
		File jointTrainingFile = new File(filePath);
		InputStream targetStream = null;
		try {
			targetStream = new FileInputStream(jointTrainingFile);
		} catch (FileNotFoundException e) {
			log.error("File not found");
		}
		
		SentenceIterator iter = new BasicLineIterator(targetStream);
		
		AbstractCache<VocabWord> cache = new AbstractCache<>();
		
		TokenizerFactory t = new DefaultTokenizerFactory();
		t.setTokenPreProcessor(new CommonPreprocessor());
		
		LabelsSource source = new LabelsSource("DOC_");
		
		this.vec = new ParagraphVectors.Builder()
				.minWordFrequency(1)
				.iterations(5)
				.seed(5)
				.epochs(1)
				.layerSize(100)
				.learningRate(0.025)
				.labelsSource(source)
				.windowSize(5)
				.iterate(iter)
				.trainWordVectors(false)
				.vocabCache(cache)
				.tokenizerFactory(t)
				.sampling(0)
				.build();
		
		this.vec.fit();
	}
	
	@SuppressWarnings("deprecation")
	public List<NLPResponseObject> response(String rawText) {
		List<BaseAction> actionList = NLPActionMapper.actionList();
		List<NLPResponseObject> responseList = new ArrayList<NLPResponseObject>();
		
		for (BaseAction action : actionList)
		{
			double maxSimilarity = 0.0;
			int maxDoc = action.getFirstDocNo();
			for(int i = action.getFirstDocNo(); i <= action.getLastDocNo(); i++)
			{
				double similarity = this.vec.similarityToLabel(rawText, "DOC_"+i);
				if(similarity>maxSimilarity)
				{
					maxSimilarity = similarity;
					maxDoc = i;
				}
			}
			
			NLPMatchLevel level = NLPMatchLevel.NONE;
			maxSimilarity*=100;
			
			if(maxSimilarity>action.getActionLevel())
				level = NLPMatchLevel.ACTION;
			else if(maxSimilarity>action.getConfirmLevel())
				level = NLPMatchLevel.CONFIRM;
			else if(maxSimilarity>action.getWarnLevel())
				level = NLPMatchLevel.WARN;
			
			if(level != NLPMatchLevel.NONE)
			{
				String bestMatch = getLine(maxDoc);
				NLPResponseObject response = new NLPResponseObject(action.getType(), level, rawText, bestMatch);
				responseList.add(response);
			}
		}
		
		return responseList;
	}
	
	private String getLine(int lineNo) {
		String line = null;
		try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
		    line = lines.skip(lineNo).findFirst().get();
		} catch (IOException e) {
			log.error("Error line not found");
		}
		return line;
	}
	
}
