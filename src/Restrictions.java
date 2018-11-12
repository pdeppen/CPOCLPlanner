import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JOptionPane;

/**
 * @author PhilipDeppen
 */
public class Restrictions {


	public Restrictions(Literal openPrecondition, int id, Step step) {
		System.out.println("*******************\n"
						 + "IN RESTRICTIONS CLASS\n"
						 + "*******************\n");
		
		System.out.println("Open Precondtion: " + openPrecondition + " is negated: " + openPrecondition.isNegative());
		System.out.println("ID: " + id);
		System.out.println("Step: " + step.toString());
		
		
		Literal newOpenPrecondition = openPrecondition;
		
		newOpenPrecondition.setExcuted(false);
		newOpenPrecondition.hasNegativeSign(true);
		
		System.out.println("New OpenPrecondition: " + newOpenPrecondition.toString() + " is negated: " + newOpenPrecondition.isNegative()); 
		step.addPreconditions(newOpenPrecondition);
	}
	
}
