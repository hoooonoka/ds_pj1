import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.GridLayout;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Pattern;
import java.awt.event.ActionEvent;

public class MainForm {

	private JFrame frame;
	private JTextField searchWordText;
	private JTextField deleteWordText;
	private JTextField addWordText;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainForm window = new MainForm();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainForm() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(1, 1, 0, 0));
		
		JTabbedPane tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane_1);
		
		JPanel panel = new JPanel();
		tabbedPane_1.addTab("Search", null, panel, null);
		panel.setLayout(null);
		
		searchWordText = new JTextField();
		searchWordText.setBounds(139, 51, 130, 26);
		panel.add(searchWordText);
		searchWordText.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Word");
		lblNewLabel.setBounds(35, 56, 61, 16);
		panel.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Meaning");
		lblNewLabel_1.setBounds(35, 157, 61, 16);
		panel.add(lblNewLabel_1);
		
		JButton searchButton = new JButton("Search");
		searchButton.setBounds(298, 51, 84, 29);
		panel.add(searchButton);
		
		JTextArea queryMeaningText = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(queryMeaningText);
		scrollPane.setSize(243, 59);
		scrollPane.setLocation(139, 142);
		panel.add(scrollPane);
		
		JLabel searchStatus = new JLabel("");
		searchStatus.setBounds(45, 86, 367, 44);
		panel.add(searchStatus);
		
		JPanel panel_2 = new JPanel();
		tabbedPane_1.addTab("Add", null, panel_2, null);
		panel_2.setLayout(null);
		
		addWordText = new JTextField();
		addWordText.setBounds(141, 29, 221, 26);
		panel_2.add(addWordText);
		addWordText.setColumns(10);
		
		JLabel lblNewLabel_4 = new JLabel("Word");
		lblNewLabel_4.setBounds(35, 34, 61, 16);
		panel_2.add(lblNewLabel_4);
		
		JLabel lblNewLabel_5 = new JLabel("Defination");
		lblNewLabel_5.setBounds(35, 67, 76, 16);
		panel_2.add(lblNewLabel_5);
		
		JButton submitButton = new JButton("Submit");
		submitButton.setBounds(35, 178, 88, 29);
		panel_2.add(submitButton);
		
		JLabel addStatus = new JLabel("");
		addStatus.setBounds(141, 183, 282, 43);
		panel_2.add(addStatus);
		
		JTextArea addMeaningText = new JTextArea();
		
		JScrollPane scrollPane_1 = new JScrollPane(addMeaningText);
		scrollPane_1.setSize(221, 87);
		scrollPane_1.setLocation(141, 67);
		panel_2.add(scrollPane_1);
		
		JPanel panel_1 = new JPanel();
		tabbedPane_1.addTab("Delete", null, panel_1, null);
		panel_1.setLayout(null);
		
		JLabel lblNewLabel_2 = new JLabel("Word");
		lblNewLabel_2.setBounds(30, 64, 61, 16);
		panel_1.add(lblNewLabel_2);
		
		deleteWordText = new JTextField();
		deleteWordText.setBounds(90, 59, 157, 26);
		panel_1.add(deleteWordText);
		deleteWordText.setColumns(10);
		
		JButton deleteButton = new JButton("Delete");
		deleteButton.setBounds(275, 59, 117, 29);
		panel_1.add(deleteButton);
		
		JLabel deleteStatus = new JLabel("");
		deleteStatus.setBounds(40, 97, 360, 58);
		panel_1.add(deleteStatus);
		JTabbedPane tabbedPane = new JTabbedPane();
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Socket socket = new Socket();
        		try 
        		{
        			String word=deleteWordText.getText();
        			String result=checkDelete(word);
        			if(result.equals("True"))
        			{
        				deleteStatus.setText("deleting...");
            			socket.connect(new InetSocketAddress("localhost",4444),5000);
            			System.out.println("Connection established");

            			// Get the input/output streams for reading/writing data from/to the socket
            			DataInputStream in = new DataInputStream(socket.getInputStream());
            			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            			// Send the input string to the server by writing to the socket output stream
            			out.writeUTF("delete*"+deleteWordText.getText());
            			out.flush();
            				
            			String received = in.readUTF(); // This method blocks until there
            											 // is something to read from the
            											 // input stream
            			System.out.println("Message received: " + received);
            			if(received.equals("Not Found"))
            			{
            				deleteStatus.setText("<html>Word Not Exist: Delete Fail</html>");
            			}
            			else if(received.equals("<html>Fail to delete the word from the Dictionary File</html>"))
            			{
            				deleteStatus.setText("<html>Fail to delete the word from the Dictionary File</html>");
            			}
            			else
            			{
            				deleteStatus.setText("<html>Word Found: Delete Success</html>");
            			}
        			}
        			else
        			{
        				deleteStatus.setText(result);
        			}
        			
        		}
        		catch(InterruptedIOException e1)
        		{
        			// timeout
        			deleteStatus.setText("<html>Connection Timeout:<br>Maybe the Internet connection failed</html>");
        		}
        		catch (UnknownHostException e1) 
        		{
        			deleteStatus.setText("<html>Server Connecting Error:<br>Cannot find the server</html>");
        		}
        		catch (IOException e1) 
        		{
        			deleteStatus.setText("<html>Socket Opening Error:<br>Problems Occur when Opening Socket</html>");
        		}
        		finally 
        		{
        			// Close the socket
        			if (socket != null) 
        			{
        				try 
        				{
        					socket.close();
        				} 
        				catch (IOException e1) 
        				{
        					deleteStatus.setText("<html>Socket Closing Error:<br>Problems Occur when Closing Socket/<html>");
        				}
        			}
        		}
			}
		});
		
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Socket socket = new Socket();
        		try 
        		{
        			String word=searchWordText.getText();
        			String result=checkQuery(word);
        			System.out.print(result);
        			if(result.equals("True"))
        			{
        				queryMeaningText.setText("");
            			searchStatus.setText("Searching...");
            			socket.connect(new InetSocketAddress("localhost",4444),5000);
            			System.out.println("Connection established");

            			// Get the input/output streams for reading/writing data from/to the socket
            			DataInputStream in = new DataInputStream(socket.getInputStream());
            			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            			// Send the input string to the server by writing to the socket output stream
            			out.writeUTF("search*"+searchWordText.getText());
            			System.out.println("search*"+searchWordText.getText());
            			out.flush();
            			while(true)
            			{
            				String received = in.readUTF(); // This method blocks until there
    						 // is something to read from the
    						 // input stream
    						System.out.println("Message received: " + received);
    						if(received.equals("Not Found"))
    						{
	    						searchStatus.setText("<html>Word Not Existed in Dictionary</html>");
	    						break;
    						}
    						else
    						{
	    						searchStatus.setText("<html>Word Found: Search Success</html>");
	    						queryMeaningText.setText(received.split("\\*")[1]);
	    						break;
    						}
            			}
        			}
        			else
        			{
        				searchStatus.setText(result);
        			}
        		}
        		catch(InterruptedIOException e1)
        		{
        			// timeout
        			searchStatus.setText("<html>Connection Timeout:<br>Maybe the Internet connection failed</html>");
        		}
        		catch (UnknownHostException e1) 
        		{
        			searchStatus.setText("<html>Server Connecting Error:<br>Cannot find the server</html>");
        		}
        		catch (IOException e1) 
        		{
        			searchStatus.setText("<html>Socket Opening Error:<br>Problems Occur when Opening Socket</html>");
        		}
        		finally 
        		{
        			// Close the socket
        			if (socket != null) 
        			{
        				try 
        				{
        					socket.close();
        				} 
        				catch (IOException e1) 
        				{
        					searchStatus.setText("<html>Socket Closing Error:<br>Problems Occur when Closing Socket</html>");
        				}
        			}
        		}
			}
		});
		
		submitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Socket socket = new Socket();
        		try 
        		{
        			
        			String word=addWordText.getText();
        			String meaning=addMeaningText.getText();
        			String result=checkAdd(word,meaning);
        			if(result.equals("True"))
        			{
        				addStatus.setText("Adding...");
            			socket.connect(new InetSocketAddress("localhost",4444),5000);
            			System.out.println("Connection established");
        				// Get the input/output streams for reading/writing data from/to the socket
            			DataInputStream in = new DataInputStream(socket.getInputStream());
            			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            			// Send the input string to the server by writing to the socket output stream
            			out.writeUTF("add*"+addWordText.getText()+"*"+addMeaningText.getText());
            			System.out.println("add*"+addWordText.getText()+"*"+addMeaningText.getText());
            			out.flush();
            				
            			String received = in.readUTF(); // This method blocks until there
            											 // is something to read from the
            											 // input stream
            			System.out.println("Message received: " + received);
            			if(received.equals("Existed"))
            			{
            				addStatus.setText("<html>Word Already Existed in Dictionary</html>");
            			}
            			else if(received.equals("Fail to add the word in the Dictionary File"))
            			{
            				addStatus.setText("<html>Fail to add the word in the Dictionary File</html>");
            			}
            			else
            			{
            				addStatus.setText("<html>Word added</html>");
            			}
        			}
        			else
        			{
        				addStatus.setText(result);
        			}
        			
        		}
        		catch(InterruptedIOException e1)
        		{
        			// timeout
        			addStatus.setText("<html>Connection Timeout:<br>Maybe the Internet connection failed</html>");
        		}
        		catch (UnknownHostException e1) 
        		{
        			addStatus.setText("<html>Server Connecting Error:<br>Cannot find the server</html>");
        		}
        		catch (IOException e1) 
        		{
        			addStatus.setText("<html>Socket Opening Error:<br>Problems Occur when Opening Socket</html>");
        		}
        		finally 
        		{
        			// Close the socket
        			if (socket != null) 
        			{
        				try 
        				{
        					socket.close();
        				} 
        				catch (IOException e1) 
        				{
        					addStatus.setText("<html>Socket Closing Error:<br>Problems Occur when Closing Socket</html>");
        				}
        			}
        		}
			}
		});  
	}
	
	private String checkQuery(String word)
	{
	    if(!Pattern.matches("^\\s*$", word))
	    {
	    	if(Pattern.matches("^[A-Za-z\\s]+$", word))
	    	return "True";
	    	else
	    	return "Input should only be letters or space";
	    }
	    else
	    	return "input word is empty or only space";
	}
	
	private String checkAdd(String word,String meaning)
	{
		if((!Pattern.matches("^\\s*$", word))&&(!Pattern.matches("^\\s*$", meaning)))
	    {
	    	if(Pattern.matches("^[a-zA-Z\\s]+$", word))
	    	return "True";
	    	else
	    	return "Input should only be letters or space";
	    }
	    else
	    	return "input is empty or only space";
		
	}
	
	private String checkDelete(String word)
	{
		if(!Pattern.matches("^\\s*$", word))
	    {
	    	if(Pattern.matches("^[a-zA-Z\\s]+$", word))
	    	return "True";
	    	else
	    	return "Input should only be letters or space";
	    }
	    else
	    	return "input word is empty or only space";
	}
}
