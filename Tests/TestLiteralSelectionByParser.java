import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

class TestLiteralSelectionByParser {
	
	ConflictParser parser = new ConflictParser();

	String domainName = "DeathMatchDomain.txt";
	String problemName = "DeathMatchProblem.txt";
	
	/**
	 * Testing to find actions selected for PQmethod by conflict parser
	 * @throws FileNotFoundException 
	 */
	@Test
	void testParser() throws FileNotFoundException {
		
		//System.out.println("/********** DEBUGGING DOMAIN PARSER **********/");
		parser.parseDomain(domainName);
		//System.out.println("/********** DEBUGGING PROBLEM PARSER **********/");
		parser.parseProblem(problemName);
	}
	
	/**
	 * Testing addGoalOpenPrecondition()
	 */
	@Test
	void testAddGoalOpenPrecondition() throws FileNotFoundException {
		
//		//System.out.println("/********** DEBUGGING DOMAIN PARSER **********/");
//		parser.parseDomain(domainName);
//		//System.out.println("/********** DEBUGGING PROBLEM PARSER **********/");
//		parser.parseProblem(problemName);
//		
//		Planner planner = new PriorityQueueMethod(parser, System.nanoTime());
//		
//		planner.addGoalOpenPrecondition();
		
	}
	
	/**
	 * Testing searchEffectInInitialState()
	 */
	@Test
	void testSearchEffecetInInitialState() throws FileNotFoundException {
		
		//System.out.println("/********** DEBUGGING DOMAIN PARSER **********/");
		parser.parseDomain(domainName);
		//System.out.println("/********** DEBUGGING PROBLEM PARSER **********/");
		parser.parseProblem(problemName);
		
		Planner planner = new PriorityQueueMethod(parser, System.nanoTime());
		
		//get the first open precondition in the queue
		System.out.println("/********** DEBUGGING searchEffectInInitialState() **********/");
		
		//get the first open precondition in the queue
		OpenPrecondition precondition = planner.getOpenPrecondition();

		planner.searchEffectInInitialState(precondition);
	}

	

}
