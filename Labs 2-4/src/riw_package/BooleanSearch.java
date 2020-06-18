package riw_package;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class BooleanSearch {
	

	public HashMap<String, HashMap<String, Integer>> parseText(String text)
	{
		HashMap<String, HashMap<String, Integer>> indexInvers = new HashMap<String, HashMap<String, Integer>>();
	

		text = text.trim();
		String[] lines = text.split("\n");
		
		for(int i = 0 ; i < lines.length; ++i)
		{
			String line = lines[i];
			if(line.startsWith("{"))
			{
				String word = lines[i-1];
				HashMap<String, Integer> fileNames = getDocumentsName(line);
				
				indexInvers.put(word, fileNames);
			}
		}
		
		return indexInvers;
	}
	
	
	public static HashMap<String, Integer> getDocumentsName(String string)
	{
		HashMap<String, Integer> fileList = new HashMap<String, Integer>();
		string  = string.substring(1, string.length() - 1);
		
		String[] fileNameList = string.split(", ");
		
		for(String fileName : fileNameList)
		{
			String[] fileInfo = fileName.split("=");
			fileList.put(fileInfo[0], Integer.parseInt(fileInfo[1]));
		}
		
		return fileList;
	}
	
	
	public static ArrayList<String> search(HashMap<String, HashMap<String, Integer>> globalHashMap, String query)
	{
		query = query.trim();
		query = query.toLowerCase();
		
		
		if(globalHashMap.containsKey(query))
		{
			HashMap<String, Integer> hQuery = globalHashMap.get(query);
			
			ArrayList<String> fileNames = new ArrayList<String>();
			for(Map.Entry<String, Integer> entry : hQuery.entrySet())
			{
				fileNames.add(entry.getKey());
			}
			return fileNames;
		}
		return null;
	}
	
//	public static ArrayList<String> AND(HashMap<String, HashMap<String, Integer>> globalHashMap, String query)
//	{
//		query = query.trim();
//		query = query.toLowerCase();
//		
//		String[] queries = query.split(" si ");
//		
//		ArrayList<String> coomunFiles = new ArrayList<String>();
//		
//		if(globalHashMap.containsKey(queries[0]) && globalHashMap.containsKey(queries[1]))
//		{
//			HashMap<String, Integer> h1 = globalHashMap.get(queries[0]);
//			HashMap<String, Integer> h2 = globalHashMap.get(queries[1]);
//			
//			
//			coomunFiles = getCoomunFiles(h1., list2);
//		}
//	}
	
	public static ArrayList<String> getCoomunFiles(ArrayList<String> list1, ArrayList<String> list2)
	{
		ArrayList<String> commonFiles = new ArrayList<String>();
		
		for(String word : list1)
		{
			for(String word2 : list2)
			{
				if(word.equals(word2))
				{
					commonFiles.add(word);
				}
			}
		}
		return commonFiles;
	}
	
	public static void main(String[] args)
	{
		System.out.println("Starting...");
		File indexInvers = new File("indexInvers.txt");
		String textInput = Text.getTextFromFile(indexInvers);
		
		
		System.out.println("Read already! Start creating hGlobal...");
		BooleanSearch bS = new BooleanSearch();
		
		HashMap<String, HashMap<String, Integer>> hGlobal = bS.parseText(textInput);
		
		Scanner keyboard = new Scanner(System.in);
		
		while(true)
		{
			System.out.println("insert your search : ");
			String query = keyboard.nextLine();
			
			ArrayList<String> results= BooleanSearch.search(hGlobal, query);
			if(results!= null)
			{
				System.out.println("\'" + query + "' se gaseste in " + results.toString());
			}
			else
			{
				System.out.println("'" + query + "' nu se gaseste!");
			}
			//keyboard.close();
		}
		

		
	}
	
}
