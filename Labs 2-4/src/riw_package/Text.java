package riw_package;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class Text {

	public static String getStringFromFile(File file)
	{
		FileReader fileReader;
		try {
			fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String text = "";
			String line = "";
			while((line = bufferedReader.readLine()) != null)
			{
				text += line.trim()+ " ";
			}
			bufferedReader.close();
			fileReader.close();
			return text;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Unable to open file '" + file.getName() + "'");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error reading file '" + file.getName() + "'");
			e.printStackTrace();
		}
		
		
		return "";
	}
	
	public static String getTextFromFile(File file)
	{
		FileReader fileReader;
		try {
			fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String text = "";
			String line = "";
			while((line = bufferedReader.readLine()) != null)
			{
				text += line+ "\n";
			}
			bufferedReader.close();
			fileReader.close();
			return text;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Unable to open file '" + file.getName() + "'");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error reading file '" + file.getName() + "'");
			e.printStackTrace();
		}
		
		
		return "";
	}

	public static HashMap<String, Integer> createWordsFrequencyByText(String text, String separator)
	{
		HashMap<String,Integer> wordsFrecvency = new HashMap<String,Integer>();
		
		String words[] = text.split(separator);
		for(String word : words)
		{
			if(wordsFrecvency.containsKey(word))
			{
				wordsFrecvency.replace(word, wordsFrecvency.get(word) + 1);
			}
			else
			{
				wordsFrecvency.put(word, 1);
			}	
		}
		return wordsFrecvency;
	}
	
	public static HashMap<String, Integer> removeStopWords(HashMap<String, Integer> wordsFrecvency, HashMap<String, Integer> stopWords, HashMap<String, Integer> exceptionWords)
	{
		for(Map.Entry<String, Integer> stopWord : stopWords.entrySet())
		{
			if(!exceptionWords.containsKey(stopWord.getKey()))
			{
				wordsFrecvency.remove(stopWord.getKey());
			}
		}
		return wordsFrecvency;
	}
	
	
	
	public static HashMap<String, Integer> createHashMapByString(String hashMapText)
	{
		String[] stringsByText =  hashMapText.split(", ");
		stringsByText[0] = stringsByText[0].substring(1, stringsByText[0].length());	//elimin acolada dinainte
		stringsByText[stringsByText.length - 1] = stringsByText[stringsByText.length - 1].substring(0, stringsByText[stringsByText.length - 1].length() - 1);	//elimin acolada de dupa
		
		HashMap<String, Integer> hashMapByString = new HashMap<String, Integer>();
		
		
		for(int i = 0; i < stringsByText.length; ++i)
		{
			String[] keyVal = stringsByText[i].split("=");
			hashMapByString.put(keyVal[0], Integer.parseInt(keyVal[1]));
		}
		return hashMapByString;
	}
	
	public static void main(String[] args)
	{
		File f = new File("f.txt");
		System.out.println(f.getName());
		
		String name = f.getName();
		name = name.replaceFirst("[.][^.]+$", "");
		
		System.out.println(name);
		try {
			
			System.out.println("Bytes: " + f.length());
			PrintWriter pW = new PrintWriter(f);
			
			pW.print("Ana are mere mari");
			pW.close();
			
			System.out.println("Bytes: " + f.length());
			System.out.println("Free space: " + f.getTotalSpace());
			System.out.println("Totl space: " + f.getTotalSpace());
			System.out.println("Usab space: " + f.getUsableSpace());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
