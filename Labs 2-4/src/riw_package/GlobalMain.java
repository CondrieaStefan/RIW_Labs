package riw_package;

import java.io.File;
import java.util.HashMap;

public class GlobalMain {
	
	private HashMap<String, Integer> wordsException;
	private HashMap<String, Integer> stopWords;
	
	
	public void setStopWords(File stopWordsFile, String separator)
	{
		String text = Text.getStringFromFile(stopWordsFile);
		this.stopWords = Text.createWordsFrequencyByText(text, separator);
	}
	
	public void setWordsException(File wordsExeptionFile, String separator)
	{
		String text = Text.getStringFromFile(wordsExeptionFile);
		this.wordsException = Text.createWordsFrequencyByText(text, separator);
	}
	

	public HashMap<String, Integer> getStopWords(){
		return this.stopWords;
	}
	public HashMap<String, Integer> getExceptionWords(){
		return this.wordsException;
	}
	

	
	
	public static void main(String[] args)
	{
		/* ------------------  Create direct index --------------------*/
		
		GlobalMain globalMain = new GlobalMain();
		globalMain.setStopWords(new File("stopwords_en.txt"), " ");
		globalMain.setWordsException(new File("exceptionWords.txt"), " ");
		
		
			FolderParser folderParser = new FolderParser();
						 folderParser.setStopWords(globalMain.getStopWords());
						 folderParser.setExceptionWords(globalMain.getExceptionWords());
			
			String inputFolderName = "TestFolder";
			String outputFolderName = "DirectIndexes";
			
			File outputFile = new File(outputFolderName + "/frequency.txt");
			FolderParser.deleteContent(outputFile);
			
						folderParser.setOuputFile(outputFile);
						
			folderParser.parseFolder(new File(inputFolderName));
			
		
			
		
		/*-------------- Create inverse index -----------------*/
		
		System.out.println("\nCreating invers indexes...");
		HashMap<String, HashMap<String,Integer>> hGlobal = new HashMap<String, HashMap<String,Integer>>();
		FolderParser.INDEX_INVERS(hGlobal, new File("DirectIndexes"));
		
		System.out.println("End!");
	}
	
	
}
