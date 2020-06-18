package riw_package;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FolderParser {

	private HashMap<String, Integer> exceptionWords;
	private HashMap<String, Integer> stopWords;
	private File outputFile = null;
	
	public void setStopWords(HashMap<String, Integer> stopWords){
		this.stopWords = stopWords;
	}
	public void setExceptionWords(HashMap<String, Integer> exceptionWords){
		this.exceptionWords = exceptionWords;
	}
	
	public void setOuputFile(String fileName){
		this.outputFile = new File(fileName);
	}
	public void setOuputFile(File outputFile){
		this.outputFile = outputFile;
	}
	
	
	public void parseFolder(File folder)
	{
		if(folder.isDirectory())
		{	
			File[] listOfFiles = folder.listFiles();
			for(File file : listOfFiles)
			{
				parseFolder(file);
			}
		}
		else if(folder.isFile())
		{
			if(this.outputFile == null)
			{
				this.outputFile = new File("frequency.txt");
			}
			else if(this.outputFile.length()/1024 > 512)	//if file is more 512 KB
			{
				int nrFiles = outputFile.getParentFile().listFiles().length;
				this.outputFile = new File(outputFile.getAbsolutePath().replaceFirst("[.][^.]+$", "") +(nrFiles+1) + ".txt");
			}
			parseFile(folder, this.outputFile);
			
		}
	}
	
	
	public long parseFile(File file, File directIndexes)
	{
		System.out.print("Parsing " + file.getName() + " -> " + directIndexes.getName() + "(" + (directIndexes.length()) + " B -> ");
		
		String text = Text.getStringFromFile(file);	//get text from file

		text = text.trim();
		text = text.toLowerCase();
		text = text.replaceAll("[^a-zA-Z0-9-' \n]", "");	//remove non alpha-numeric characters
		text = text.replaceAll("\\s{2,}", " ");		//remove duplicate white-space
		
		
		try {
			
			PrintWriter printWriter = new PrintWriter(new FileWriter(directIndexes, true));
			printWriter.println(file.getAbsolutePath());
			
			if(text.length() > 0)
			{
				HashMap<String, Integer> wordsFrecvency = Text.createWordsFrequencyByText(text, " ");
				wordsFrecvency = Text.removeStopWords(wordsFrecvency, stopWords, exceptionWords);
				printWriter.println(wordsFrecvency.toString()+"\n");
			}
			else
			{
				printWriter.println("{}\n");
			}
			printWriter.close();
			System.out.println(directIndexes.length() + " Bytes)");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return directIndexes.length();
	}
	
	
	public static void deleteContent(File file)
	{
		if(file.exists())
		{
			try {
				PrintWriter printWriter = new PrintWriter(file);
				printWriter.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	public static void INDEX_INVERS(HashMap<String, HashMap<String, Integer>> globalHashMap, File folderDirectIndex)
	{
		File[] listOfFiles = folderDirectIndex.listFiles();
		for(File file : listOfFiles)
		{
			if(file.isFile())
			{
				System.out.println("Fisier: " + file.getName()+ "...");
				INDEX_INVERS_FISIER(globalHashMap, file);
			}
			else if(file.isDirectory())
			{
				INDEX_INVERS(globalHashMap, folderDirectIndex);
			}
		}
		
		try {
			PrintWriter pW = new PrintWriter("indexInvers.txt");
			

			for(Map.Entry<String, HashMap<String, Integer>> entry : globalHashMap.entrySet())
			{
				pW.println(entry.getKey());
				pW.println(entry.getValue().toString() + "\n");
			}
			pW.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	public static void INDEX_INVERS_FISIER(HashMap<String, HashMap<String, Integer>> globalHashMap, File fileDirectIndex)
	{
		String text = Text.getTextFromFile(fileDirectIndex);
		
		String[] lines = text.split("\n");
		for(int i = 0; i<lines.length; ++i)
		{
			String line = lines[i];
			if(line.startsWith("{") && !line.equals("{}"))
			{
				String fileName = lines[i-1];
				createHashMapByString(globalHashMap, line, fileName);
			}
		}
		
		
	}
	
	
	
	public static void createHashMapByString(HashMap<String, HashMap<String, Integer>> globalHashMap, String hashMapString, String fileName)
	{
		String[] stringsByText =  hashMapString.split(", ");
		stringsByText[0] = stringsByText[0].substring(1, stringsByText[0].length());	//elimin acolada dinainte
		stringsByText[stringsByText.length - 1] = stringsByText[stringsByText.length - 1].substring(0, stringsByText[stringsByText.length - 1].length() - 1);	//elimin acolada de dupa
		
		
		for(int i=0; i < stringsByText.length; ++i)
		{
			String[] keyVal = stringsByText[i].split("=");
			String word = keyVal[0];
			String freq = keyVal[1];
			
			if(globalHashMap.containsKey(word))
			{
				HashMap<String, Integer> fileFrequency = globalHashMap.get(word);
				fileFrequency.put(fileName, Integer.parseInt(freq));
				
				globalHashMap.replace(keyVal[0], fileFrequency);
			}
			else
			{
				HashMap<String, Integer> fileFrequency = new HashMap<String, Integer>();
				fileFrequency.put(fileName, Integer.parseInt(freq));
				globalHashMap.put(word, fileFrequency);
			}
		}
		
	}
	
	
	
	
	
	public static void main(String[] args)
	{
		HashMap<String, String> h1 = new HashMap<String, String>();
		HashMap<String, String> h2 = new HashMap<String, String>();
		
		
		h1.put("ana", "2");
		h1.put("are", "1");
		h1.put("mere", "3");
		

		h2.put("ana", "4");
		h2.put("are", "1");
		h2.put("pere", "3");
		
		HashMap<String, Integer> h3 = new HashMap<String, Integer>();
		//h3.putAll(h1);
		//h3.putAll(h2);
		
		System.out.println("h1: " + h1.toString());
		System.out.println("h2: " + h2.toString());
		System.out.println("h3: " + h3.toString());
		//h1.putAll(h2);
		
		//h1.forEach((k, v) -> h2.merge(k, v, (v1, v2) -> v1+v2));
		
		System.out.println("h2: " + h2.toString());
		
		
		
		HashMap<String, ArrayList<String>> hh1 = new HashMap<String, ArrayList<String>>();
		ArrayList<String> l1 = new ArrayList<String>();
		l1.add("fis1:1");
		l1.add("fis2:4");
		
		hh1.put("ana", l1);
		
		
		//HashMap<String, ArrayList<String>> hh1 = new HashMap<String, ArrayList<String>>();
		
		//h1.forEach((k, v) -> hh1.merge(k, v, (v1, v2) -> (v2.add()));
		
		
		
		 Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	        map.put(1, 20);
	        map.put(2, 20);
	        map.put(3, 10);
	        map.put(4, 30);
	        map.put(5, 20);
	        map.put(6, 10);

	      //  Set<Integer> result = map.entrySet().stream().flatMap(e -> Stream.of(e.getKey(), e.getValue()))
	        //    .collect(Collectors.toSet());

	        Set<Integer> result = map.entrySet().stream().flatMap(e->Stream.of(e.getKey(), e.getValue())).collect(Collectors.toSet());
	        System.out.println(result);
		
		
	}
	
}
