import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.PriorityQueue;
import java.util.Queue;

import javax.swing.JLabel;
import javax.swing.JTextArea;

public class SimpleThreadPool
{
	private static final int MAX_REQUEST = 100;
    private final Queue<Socket> requestQueue;
    private int count;
    private final Thread[] threadPool;
    public SimpleThreadPool(int size,JLabel wordCountLabel,Dictionary dictionary,JTextArea dictionaryText, JTextArea logText) 
    {
        requestQueue = new PriorityQueue<>(MAX_REQUEST);
        count = 0;
        threadPool = new Thread[size];
        for (int i = 0; i < size; i++)
        {
        	Thread t = new Thread(() -> serverClient(dictionary,wordCountLabel,this,dictionaryText,logText));
        	t.start();
        	threadPool[i]=t;
        }
           
    }
    
    public synchronized void putRequest(Socket request)
    {
        while (count >= MAX_REQUEST)
        {
            try
            {
                wait();
            }
            catch (InterruptedException e) 
            {
                e.printStackTrace();
            }
        }
        requestQueue.add(request);
        count++;
        notifyAll();
    }
    
    public synchronized static void repaint(JLabel wordCountLabel,JTextArea dictionaryText,JTextArea logText,String result)
    {
    	MainForm.changeCounts(wordCountLabel);
	    MainForm.changeDictionary(dictionaryText);
	    MainForm.changeLog(logText,result);
    }
 
    public synchronized Socket takeRequest()
    {
        while (count <= 0)
        {
            try 
            {
                wait();
            }
            catch (InterruptedException e) 
            {
                e.printStackTrace();
            }
        }
        Socket request = requestQueue.poll();
        count--;
        notifyAll();
        return  request;
    }
    
    private static void serverClient(Dictionary dictionary, JLabel wordCountLabel,SimpleThreadPool pool,JTextArea dictionaryText, JTextArea logText) 
	{
    	while(true)
    	{
    		Socket client=pool.takeRequest();
    		try(Socket clientSocket = client)
    		{
    			DataInputStream input = new DataInputStream(clientSocket.getInputStream());
    		    DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
    			String command=input.readUTF();
     			System.out.println("CLIENT: "+command);
    		    String[] words=command.split("\\*");
    		    String result="";
    		    String log="";
    		    if(words[0].equals("add"))
    		    {
    		    	result=dictionary.addWord(words[1], words[2]);
    		    	if(result.equals("Success"))
    		    	log="user "+client.getInetAddress().getHostName()+" add a word:"+words[1];
    		    	else
    		    	log="user "+client.getInetAddress().getHostName()+" wanted to add a word:"+words[1]+"; however, failed";
    		    }
    		    else if(words[0].equals("delete"))
    		    {
    		    	result=dictionary.deleteWord(words[1]);
    		    	if(result.equals("Success"))
        		    log="user "+client.getInetAddress().getHostName()+" delete a word:"+words[1];
        		   	else
        		   	log="user "+client.getInetAddress().getHostName()+" wanted to delete a word:"+words[1]+"; however, failed";
    		    }
    		    else if(words[0].equals("search"))
    		    {
    			    result=dictionary.searchWord(words[1]);
    			    if(!result.equals("Not Found"))
        		    log="user "+client.getInetAddress().getHostName()+" search a word:"+words[1];
        		   	else
        		   	log="user "+client.getInetAddress().getHostName()+" wanted to search a word:"+words[1]+"; however, failed";
    		    }
    		    
    		    repaint(wordCountLabel,dictionaryText,logText,log);
    		    System.out.println(result);
    		    output.writeUTF(result);
    		    output.flush();
    		    client.close();
    		}
    		catch (IOException e) 
    		{
    			e.printStackTrace();
    		}
    	}
		
    }



}
