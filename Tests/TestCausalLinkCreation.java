import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.junit.jupiter.api.Test;

class TestCausalLinkCreation {
	
	String domainName = "BriefcaseDomain.txt";
	String problemName = "BriefcaseProblem.txt";
//	String domainName = "BirthdayDomain.txt";
//	String problemName = "BirthdayProblem.txt";
	
	ConflictParser parser = new ConflictParser();
	
	@Test
	void test() throws IOException {
		parser.parseDomain(domainName);
		parser.parseProblem(problemName);
		
		PriorityQueueMethod planner = new PriorityQueueMethod(parser, System.nanoTime());
		
		// change this to create different text files
		int id = 9;
		
		//planner.debugGetOpenPreconditions(id);
		planner.search();
		
		/* check that threatening preconditino is being saved correctly */
		assertEquals("paycheck Paycheck", planner.threateningPrecondition.getOpenPreconditionToString().toString());
		
		/* check that goals were saved correctly */
		assertEquals("location Paycheck Home", planner.mostRecentMadeGoal.get(0).toString());
		assertEquals("location Briefcase Office", planner.mostRecentMadeGoal.get(1).toString());
	}

}
