import java.io.*;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;


public class sentDetect {
	
	public static void main(String[] args){
//        String path = args[0];
		File file = null;
        WordExtractor extractor = null;
        // read .doc file line by line
        try{
            file = new File("../job-requirements/job1.doc");
            FileInputStream fis = new FileInputStream(file.getAbsolutePath());
            HWPFDocument document = new HWPFDocument(fis);
            extractor = new WordExtractor(document);
            String fileData = extractor.getText();
            SentenceDetect(fileData);
//            POSTag(sentence);
//                	System.out.print(n + "\t");
//                    System.out.println(fileData[i]);
            }
        catch (Exception exep){
            exep.printStackTrace();
        }
	}
	
	//SentenceDetect, break down a paragraph into sentences. 
	public static void SentenceDetect(String paragraph) throws InvalidFormatException, 
		IOException{
		//	String paragraph = "Hi. How are you? This is Mike.";
		InputStream is = new FileInputStream("./nlpmodel/en-sent.bin");
		SentenceModel model = new SentenceModel(is);
		SentenceDetectorME sdetector = new SentenceDetectorME(model);
	
		String[] sentence = sdetector.sentDetect(paragraph);
		
		for(String s:sentence)
			System.out.println(s);
		
		is.close();
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
	
	//POSTagging, identify words as noun or verb, adj, adv, etc
	public static void POSTag(String paragraph[]) throws IOException{
		POSModel model = new POSModelLoader().load(new File("./nlpmodel/en-pos-maxent.bin"));
		PerformanceMonitor perfMon = new PerformanceMonitor(System.err,"sent");
		POSTaggerME tagger = new POSTaggerME(model);
		
//		String input = "Hi. How are you? This is Mike.";
//		@SuppressWarnings("deprecation")
//		ObjectStream<String> lineStream = new PlainTextByLineStream(new StringReader(paragraph));
		
		perfMon.start();
//		String line;
//		while((line = lineStream.read()) != null){
		for(String line:paragraph){
//			String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE.tokenize(line);
			String tag = tagger.tag(line);
			if(tag.matches("[A-Za-z0-9_]+/NN.?"))
//			POSSample sample = new POSSample(line, tag);
				System.out.println(tag);
				
			perfMon.incrementCounter();
		}
		perfMon.stopAndPrintFinalResult();
	}
	
	//TODO: find location name
}
