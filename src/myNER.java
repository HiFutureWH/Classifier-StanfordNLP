import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

public class myNER {
	public static void main(String[] args){
		//TODO
		try {
			String paragraph = readDoc("../job-requirements/job2.doc");
//			System.out.println(paragraph);
			findName(paragraph);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void findName(String paragraph) throws IOException{
		InputStream is = new FileInputStream("en-ner-tech.bin");
		TokenNameFinderModel model = new TokenNameFinderModel(is);
		is.close();
		
		NameFinderME nameFinder = new NameFinderME(model);
		String[] lines = SentenceDetect(paragraph);
		for(String line:lines){
			String[] sentence = Tokenize(line);

			Span nameSpans[] = nameFinder.find(sentence);
//			for(Span s:nameSpans)
//				System.out.println(s);
			
	    	//find probabilities for techs
	    	double[] spanProbs = nameFinder.probs(nameSpans);
	    	
	    	//3. print techs
	    	for( int i = 0; i<nameSpans.length; i++) {
	    		if(spanProbs[i] > 0.9){
//	    			System.out.println("Span: "+nameSpans[i].toString());
	    			System.out.println("tech: "+sentence[nameSpans[i].getStart()] + " " + sentence[nameSpans[i].getStart()+1]);
//	    			System.out.println("Prob: "+spanProbs[i].round());
	    		}
	    	}	
		}
/*	    String[] sentence = new String[]{
	            "Mike",
	            "experience",
	            "Python",
	            "Numpy",
	            "Java",
	            "SQL",
	            "person"
	            };
	    Span nameSpans[] = nameFinder.find(sentence);
	    for(Span s: nameSpans)
	        System.out.println(s.toString());  */

	}
	
	public static String readDoc(String inputfile) throws IOException{
		File file = new File(inputfile);
        FileInputStream fis = new FileInputStream(file.getAbsolutePath());
        HWPFDocument document = new HWPFDocument(fis);
        WordExtractor extractor = new WordExtractor(document);
        // fileData is the original text requirement in one single string
        String fileData = extractor.getText();
        extractor.close();
        return fileData;
	}
	
	//SentenceDetect, break down a paragraph into sentences. 
	public static String[] SentenceDetect(String paragraph) throws InvalidFormatException, 
		IOException{
		//	String paragraph = "Hi. How are you? This is Mike.";
		InputStream is = new FileInputStream("./nlpmodel/en-sent.bin");
		SentenceModel model = new SentenceModel(is);
		SentenceDetectorME sdetector = new SentenceDetectorME(model);
	
		String[] sentence = sdetector.sentDetect(paragraph);
		is.close();
		return sentence;
//		for(String s:sentence){
//			System.out.println(s);
//		}
		//	System.out.println(sentence[0]);
		//	System.out.println(sentence[1]);
	}
	
	//Tokenizer, break down sentences into single words
	public static String[] Tokenize(String paragraph) throws InvalidFormatException,
			IOException{
		InputStream is = new FileInputStream("./nlpmodel/en-token.bin");
		TokenizerModel model = new TokenizerModel(is);
		Tokenizer tokenizer = new TokenizerME(model);
		String tokens[] = tokenizer.tokenize(paragraph);
//		for(String a:tokens)
//			System.out.println(a);
		is.close();
		return tokens;
//		System.out.println("-------------2-----------------");
	}
}
