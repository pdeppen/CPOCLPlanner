import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JOptionPane;

/**
 * @author PhilipDeppen
 */
public class Restrictions {


	public Restrictions(Literal openPrecondition, int id) {
		System.out.println("*******************\n"
						 + "IN RESTRICTIONS CLASS\n"
						 + "*******************\n");
		
		System.out.println("Open Precondtion: " + openPrecondition);
		System.out.println("ID: " + id);
	}
	
}
