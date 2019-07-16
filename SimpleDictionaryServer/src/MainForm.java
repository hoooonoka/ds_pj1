import java.awt.EventQueue;
import javax.swing.JFrame;
import java.awt.GridLayout;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainForm {
	private static Dictionary dictionary;
	private static Thread t;
	private JFrame frame;

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
		frame.getContentPane().setLayout(new GridLayout(1, 0, 0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Control", null, panel_1, null);
		panel_1.setLayout(null);
		dictionary=new Dictionary(System.getProperty("user.dir")+"/src/dic.txt");
		
		JLabel wordCountLabel = new JLabel("<html>Word Count<br>"+dictionary.dictionaryLength()+"</html>");
		
		wordCountLabel.setBounds(21, 41, 109, 163);
		panel_1.add(wordCountLabel);
		JTextArea logText = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(logText);
		scrollPane.setSize(240, 163);
		scrollPane.setLocation(142, 41);
		panel_1.add(scrollPane);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Dictionary", null, panel, null);
		panel.setLayout(null);
		
		JTextArea dictionaryText = new JTextArea();
		JScrollPane scrollPane_1 = new JScrollPane(dictionaryText);
		scrollPane_1.setSize(336, 177);
		scrollPane_1.setLocation(42, 31);
		panel.add(scrollPane_1);
		
		JLabel lblCurrentDictionary = new JLabel("Current Dictionary");
		lblCurrentDictionary.setBounds(42, 6, 124, 16);
		panel.add(lblCurrentDictionary);
		dictionaryText.setText(dictionary.printWords());
		t = new Thread(() -> listen(wordCountLabel,dictionaryText,logText));
		t.start();
		
	}
	
	private static void listen(JLabel wordCountLabel, JTextArea dictionaryText, JTextArea logText)
	{
		ServerSocket listeningSocket=null;
		SimpleThreadPool pool=new SimpleThreadPool(10,wordCountLabel,dictionary,dictionaryText,logText);
		try
		{
    		listeningSocket = new ServerSocket(4444);
			//Listen for incoming connections for ever 
			while (true) 
			{
				try 
				{
						Socket client = listeningSocket.accept();
						// Start a new thread for a connection
						pool.putRequest(client);
						
//						Thread t = new Thread(() -> serveClient(client));
//						t.start();
				}
				catch(SocketException e) 
				{
					System.out.println("closed...");
				}
				
			}
		}
    	catch (SocketException ex) 
    	{
			ex.printStackTrace();
		}
    	catch (IOException e) 
    	{
			e.printStackTrace();
		} 
		finally 
		{
			if(listeningSocket != null) 
			{
				try 
				{
					listeningSocket.close();
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	
	synchronized public static void changeCounts(JLabel wordCountLabel)
	{
		wordCountLabel.setText("<html>Word Count<br>"+dictionary.dictionaryLength()+"</html>");
	}
	
	synchronized public static void changeDictionary(JTextArea dictionaryText)
	{
		dictionaryText.setText(dictionary.printWords());
	}
	
	synchronized public static void changeLog(JTextArea logText,String result)
	{
		String log=logText.getText();
		logText.setText(log+"\n"+result);
	}
}

