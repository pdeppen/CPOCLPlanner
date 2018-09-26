import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.jupiter.api.Test;

class TestLiteralSelectionByParser {
	
	ConflictParser parser = new ConflictParser();

	String domainName = "DeathMatchDomain.txt";
	String problemName = "DeathMatchProblem.txt";
	
	/**
	 * Testing to find actions selected for PQmethod by conflict parser
	 * NEED TO UNCOMMENT METHODS AT BOTTOM OF PARSER TO TEST 
	 */
	@Test
	void testParser() throws FileNotFoundException {
		
//		System.out.println("/********** DEBUGGING DOMAIN PARSER **********/");
//		parser.parseDomain(domainName);
//		System.out.println("/********** DEBUGGING PROBLEM PARSER **********/");
//		parser.parseProblem(problemName);
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
	 * DEBUG THROUGH TESTPRIORITYQUEUE INSTEAD
	 */
	@Test
	void testSearchEffecetInInitialState() throws FileNotFoundException {
		
//		//System.out.println("/********** DEBUGGING DOMAIN PARSER **********/");
//		parser.parseDomain(domainName);
//		//System.out.println("/********** DEBUGGING PROBLEM PARSER **********/");
//		parser.parseProblem(problemName);
//		
//		Planner planner = new PriorityQueueMethod(parser, System.nanoTime());
//		
//		//get the first open precondition in the queue
//		System.out.println("/********** DEBUGGING searchEffectInInitialState() **********/");
//		
//		//get the first open precondition in the queue
//		OpenPrecondition precondition = planner.getOpenPrecondition();
//
//		planner.searchEffectInInitialState(precondition);
	}
	
	@Test
	void testGetOpenPrecondition() throws IOException {
		parser.parseDomain(domainName);
		parser.parseProblem(problemName);
		
		PriorityQueueMethod planner = new PriorityQueueMethod(parser, System.nanoTime());
		
		// change this to create different text files
		int id = 3;
		
		planner.debugGetOpenPreconditions(id);
		
	}
	

	

}
