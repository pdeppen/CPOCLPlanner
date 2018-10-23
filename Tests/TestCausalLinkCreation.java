import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;

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
	}

}
