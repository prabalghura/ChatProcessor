package com.smartapp.nlp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.ml.AbstractTrainer;
import opennlp.tools.ml.naivebayes.NaiveBayesTrainer;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderFactory;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;


/**
 * @author rajendrag
 *This class generates a model using the training data provided and the openNLP TrainingAPI
 */
public class Trainer {
    private static final String TRAINING_DATA_FOLDER = "trainingData";
	private static final String UTF_8 = "UTF-8";
	private static final String SENTENCES_CSV = "sentences.csv";
	private static final String SENTENCE_ANALYSER_BIN = "/sentenceAnalyser.bin";
	// training data set
    static String trainingPath = "modelOut";

    public static void main(String[] args) throws IOException {
    	documentCategorizerTrainer();
        //nameFinderTrainer();
    }

	private static void nameFinderTrainer() throws IOException, FileNotFoundException {
		String[] entities = new String[]{"action"};
        String[] pathsOfTrainedFile = new String[]{"/action.bin"};
        for(int i = 0; i < entities.length; i++){
            final int j = i;
            InputStreamFactory isf = new InputStreamFactory() {
            	String[] pathsOfTraingFile = new String[]{"/action.train"};
                public InputStream createInputStream() throws IOException {
                    return new FileInputStream(trainingPath + pathsOfTraingFile[j]);
                }
            };
            Charset charset = Charset.forName(UTF_8);
            ObjectStream<String> lineStream = new PlainTextByLineStream(isf, charset);
            ObjectStream<NameSample> sampleStream = new NameSampleDataStream(lineStream);
            TokenNameFinderModel model;
            TokenNameFinderFactory nameFinderFactory = new TokenNameFinderFactory();
            try {
                model = NameFinderME.train("en", entities[i], sampleStream, TrainingParameters.defaultParams(),
                        nameFinderFactory);
            } finally {
                sampleStream.close();
            }
            BufferedOutputStream modelOut = null;
            try {
                modelOut = new BufferedOutputStream(new FileOutputStream(trainingPath + pathsOfTrainedFile[i]));
                model.serialize(modelOut);
            } finally {
                if (modelOut != null)
                    modelOut.close();
            }
        }
	}
    
    public static void documentCategorizerTrainer() {
    		MarkableFileInputStreamFactory factory;
    		String pathsOfTrainedFile = SENTENCE_ANALYSER_BIN;
    		DoccatModel model;
            DoccatFactory df = new DoccatFactory();
            Charset charset = Charset.forName(UTF_8);
            ObjectStream<DocumentSample> sampleStream = null;
            BufferedOutputStream modelOut = null;
			try {
				factory = new MarkableFileInputStreamFactory(
				     new File(TRAINING_DATA_FOLDER + File.separator + SENTENCES_CSV));
				ObjectStream<String> lineStream = new PlainTextByLineStream(factory, charset);
	            sampleStream = new DocumentSampleStream(lineStream);
	            TrainingParameters params = new TrainingParameters();
	            params.put(TrainingParameters.CUTOFF_PARAM, 0+"");
	            params.put(AbstractTrainer.ALGORITHM_PARAM, NaiveBayesTrainer.NAIVE_BAYES_VALUE);
	            model = DocumentCategorizerME.train("en", sampleStream, params,df);
	            modelOut = new BufferedOutputStream(new FileOutputStream(trainingPath + pathsOfTrainedFile));
                model.serialize(modelOut);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
                try {
					sampleStream.close();
					if (modelOut != null)
	                    modelOut.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
    }
}
