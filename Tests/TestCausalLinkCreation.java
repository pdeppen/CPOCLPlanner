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
		int id = 10;
		
		//planner.debugGetOpenPreconditions(id);
		planner.search();
		
		/* check that threatening preconditino is being saved correctly */
		assertEquals("paycheck Paycheck", planner.threateningPrecondition.getOpenPreconditionToString().toString());
		
		/* check that goals were saved correctly */
		assertEquals("location Paycheck Home", planner.mostRecentMadeGoal.get(0).toString());
		assertEquals("location Briefcase Office", planner.mostRecentMadeGoal.get(1).toString());
		
		/* check that new literal = not paycheck Paycheck */
		//assertEquals("paycheck Paychfd", )
		
//		int stepsNum = parser.getActionDomainSize();		//how many action in the domain
//		for(int i=0; i< stepsNum;i++)
//		{
//			int effectNum = parser.getActionsDomainEffectSize(i);		//how many effects in every action
//			for(int f=0; f< effectNum; f++)
//			{
//				System.out.println(parser.getActionsEffects(i, f).getLiteralName());
//			}
//		}
		
	}

}
