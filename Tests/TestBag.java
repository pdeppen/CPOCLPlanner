import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import org.junit.Test;

public class TestBag {

	@Test
	public void testFindPlanBag() throws FileNotFoundException
	{
		ConflictParser parser = new ConflictParser();

//		String domainName = "BriefcaseDomain.txt";
//		String problemName = "BriefcaseProblem.txt";
		
//		String domainName = "DeathMatchDomain.txt";
//		String problemName = "DeathMatchProblem.txt";
//		String domainName = "SimpleDeathMatchDomain.txt";
//		String problemName = "SimpleDeathMatchProblem.txt";

//		String domainName = "CryingBabyDomain.txt";
//		String problemName = "CryingBabyProblem.txt";
//		String domainName = "SimpleCryingBabyDomain.txt";
//		String problemName = "SimpleCryingBabyProblem.txt";

//		String domainName = "BirthdayDomain.txt";
//		String problemName = "BirthdayProblem.txt";
		String domainName = "SimpleBirthdayDomain.txt";
		String problemName = "SimpleBirthdayProblem.txt";

//		String domainName = "MarriageDomain.txt";
//		String problemName = "MarriageProblem.txt";
//		String domainName = "SimpleMarriageDomain.txt";
//		String problemName = "SimpleMarriageProblem.txt";

//		String domainName = "DinnerDomain.txt";
//		String problemName = "DinnerProblem.txt";
//		String domainName = "SimpleDinnerDomain.txt";
//		String problemName = "SimpleDinnerProblem.txt";

		parser.parseDomain(domainName);
		parser.parseProblem(problemName);

//		boolean success;
//		for (int i = 30; i > 0; i--)
//		{
//			success = false;
//			while (!success)
//			{
				Planner planner = new BagMethod(parser, System.nanoTime());
//				success = planner.search();
				planner.search();
//			}
//		}
	}
}