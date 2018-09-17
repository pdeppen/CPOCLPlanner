import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JOptionPane;

/**
 * @author PhilipDeppen
 */
public class Restrictions {

	private FileWriter fileWriter;
	private PrintWriter printWriter;

	public void setUpWriters() throws IOException {
		 fileWriter = new FileWriter("printingOrderOfClassNames.txt");
		 printWriter = new PrintWriter(fileWriter);
	}
	
	public Restrictions(String s) {
		printClassNamesInOrder(s);
	}
	
	/**
	 * getting ordering of classes called
	 * not crucial to restrictions class
	 */
	public void printClassNamesInOrder(String s) {
//		System.out.println("/**********" + s + "**********/");
	}

}
