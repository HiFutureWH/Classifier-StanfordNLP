import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

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


public class TechnologyDetector {
	
	public static void main(String[] args) throws InvalidFormatException, IOException{
//        String path = args[0];
		File file = null;
        WordExtractor extractor = null;
        // read .doc file line by line
        try{
            file = new File("job-requirements\\job1.doc");
            String outputfile = "job1-a.txt";
            FileInputStream fis = new FileInputStream(file.getAbsolutePath());
            HWPFDocument document = new HWPFDocument(fis);
            extractor = new WordExtractor(document);
            // fileData is the original text requirement in one single string
            String fileData = extractor.getText();
            String[] sentence = Tokenize(fileData);
//            for(String s:sentence)
//            	System.out.println(s);
            
            //read in the list of technologies
            String[] alltechs = readTXT("Tech.txt");
            // intersect alltechs and thistechs
            //TODO address the the upper case and lower case problem
            //TODO identify similar match, such as "hadoop" and "apache hadoop"
            //TODO identify tech name longer than one word. such as "machine learning"
            String[] thistechs = intersect(sentence, alltechs);
            
        	System.out.println("-----------------TECHS FOUND--------------------");            
            for(String s:thistechs){
            	System.out.println(s);
            }
            
            System.out.println("-----------------NOUNS in original TEXT---------");
            POSTag(sentence); //show only nouns from the original text

        	
        	//annotate the original file(fileData) with the tech array we got(thistechs)
        	//example <START:tech> Python <END>
        	// ".*(^|[^a-zA-Z])name([^a-zA-Z]|$).*"
        	
            System.out.println("-----------------Annotated TEXT-----------------");
        	String annotxt = new String(fileData);
        	for(String tech:thistechs){
        		String annotation = String.format(" <START:tech> %s <END> ", tech);
        		annotxt = annotxt.replaceAll("\\b" + tech + "\\b", annotation);
        	}
        	String[] sents = SentenceDetect(annotxt);
        	for(String s:sents) System.out.println(s);
        	
        	//save sentences(sents) to txt file
        	savetxt(sents, outputfile);
        	


//                	System.out.print(n + "\t");
//                    System.out.println(fileData[i]);
            }
        catch (Exception exep){
            exep.printStackTrace();
        }
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
				System.out.println(line);
				
			perfMon.incrementCounter();
		}
		perfMon.stopAndPrintFinalResult();
	}
	
	//TODO: find location name
	
	public static String[] readTXT(String filename) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(filename));
		List<String> lines = new ArrayList<String>();
		while(sc.hasNextLine()){
			lines.add(sc.nextLine());
		}
		sc.close();
		String[] arr = lines.toArray(new String[0]);
		
		return arr;
		
	}
	
    public static String[] intersect(String[] arr1, String[] arr2){
        List<String> l = new LinkedList<String>();
        Set<String> common = new HashSet<String>();                  
        for(String str:arr1){
            if(!l.contains(str)){
                l.add(str);
            }
        }
        for(String str:arr2){
            if(l.contains(str)){
                common.add(str);
            }
        }
        String[] result={};
        return common.toArray(result);
    }
    
    public static void savetxt(String[] inputarray, String outputfile){
 // write sentences into a txt file.
		try
		{
		    PrintWriter pr = new PrintWriter(outputfile);    
	
		    for (int i=0; i<inputarray.length ; i++)
		    {
		        pr.println(inputarray[i]);
		    }
		    pr.close();
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		    System.out.println("No such file exists.");
		}
    }
}
