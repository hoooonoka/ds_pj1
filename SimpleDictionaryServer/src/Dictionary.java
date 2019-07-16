import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
public class Dictionary 
{
	private static Map<String, String> words;
	private static String path;
	public Dictionary(String filePath)
	{
		words=readDictionaryFile(filePath);
		path=filePath;
	}
	public Map<String, String> readDictionaryFile(String path)
	{
		Map<String, String> words=new HashMap<String, String>();
		// read file
		String fileName=path;
        String line="";
        try
        {
                BufferedReader in=new BufferedReader(new FileReader(fileName));
                line=in.readLine();
                while (line!=null)
                {
                        String[] aWord=line.split(":");
                        words.put(aWord[0], aWord[1]);
                        line=in.readLine();
                }
                in.close();
        } catch (IOException e)
        {
                e.printStackTrace();
        }
        return words;
	}
	
	public synchronized String addWord(String word,String defination)
	{
		if(words.containsKey(word))
		{
			// raise error: duplicate words
			System.out.println("here");
			return "Existed";
		}
		else
		{
			words.put(word, defination);
			if(changeDictionaryFile()=="Success")
			{
				return "Success";
			}
			else
			{
				return "Fail to add the word in the Dictionary File";
			}
		}
	}
	public synchronized String deleteWord(String word)
	{
		if(words.containsKey(word))
		{
			words.remove(word);
			if(changeDictionaryFile()=="Success")
			{
				return "Success";
			}
			else
			{
				return "Fail to delete the word from the Dictionary File";
			}
		}
		else
		{
			// raise error: no such word
			return "Not Found";
		}
	}
	
	public synchronized String searchWord(String word)
	{
		if(words.containsKey(word))
		{
			return "*"+words.get(word);
		}
		else
		{
			// raise error: no such word
			return "Not Found";
		}
	}
	
	public synchronized String changeDictionaryFile()
	{
	    FileWriter writer;
		try 
		{
			writer = new FileWriter(path);
			writer.write(this.printWords());
			writer.close();
			return "Success";
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			return "Fail";
		}
	    
	}
	
	public Map<String,String> getWords()
	{
		return words;
	}
	
	public String printWords()
	{
		String s="";
		for (Map.Entry<String, String> entry : words.entrySet()) 
		{ 
			s+=entry.getKey() + ": " + entry.getValue() + "\n"; 
		}
		return s;
	}
	
	public int dictionaryLength()
	{
		return words.size();
	}
}

