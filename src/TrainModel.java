
//train your custom model using the annotated text(annotxt)
// - step1: detect sentences, annotxt will be in the form of one sentence per line.
// - step2: train the model with annotated files.
// training sample: job1-a.txt

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

public class TrainModel {
	static String onlpModelPath = "en-ner-tech.bin";
	// training data set
	static String trainingDataFilePath = "../job-requirements/job1-anno.txt";

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException {
		Charset charset = Charset.forName("UTF-8");
		ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStream(trainingDataFilePath), charset);
		ObjectStream<NameSample> sampleStream = new NameSampleDataStream(lineStream);
		TokenNameFinderModel model;
		
		try {
			model = NameFinderME.train("en", "tech", sampleStream, Collections.<String, Object>emptyMap());
		} 
		finally {
			sampleStream.close();
		}
		BufferedOutputStream modelOut = null;
		try {
			modelOut = new BufferedOutputStream(new FileOutputStream(onlpModelPath));
			model.serialize(modelOut);
		} finally {
			if (modelOut != null)
				modelOut.close();
		}
	}
}