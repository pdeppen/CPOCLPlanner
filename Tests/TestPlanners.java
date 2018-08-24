import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import org.junit.Test;

public class TestPlanners {

//	String domainName = "DeathMatchDomain.txt";
//	String problemName = "DeathMatchProblem.txt";
//	String domainName = "SimpleDeathMatchDomain.txt";
//	String problemName = "SimpleDeathMatchProblem.txt";

//	String domainName = "CryingBabyDomain.txt";
//	String problemName = "CryingBabyProblem.txt";
//	String domainName = "SimpleCryingBabyDomain.txt";
//	String problemName = "SimpleCryingBabyProblem.txt";

//	String domainName = "BirthdayDomain.txt";
//	String problemName = "BirthdayProblem.txt";
//	String domainName = "SimpleBirthdayDomain.txt";
//	String problemName = "SimpleBirthdayProblem.txt";

//	String domainName = "MarriageDomain.txt";
//	String problemName = "MarriageProblem.txt";
//	String domainName = "SimpleMarriageDomain.txt";
//	String problemName = "SimpleMarriageProblem.txt";

	String domainName = "DinnerDomain.txt";
	String problemName = "DinnerProblem.txt";
//	String domainName = "SimpleDinnerDomain.txt";
//	String problemName = "SimpleDinnerProblem.txt";

	boolean success;
	int numTimes = 30;

	long[] bagTimes = new long[numTimes];
	long[] priorityTimes = new long[numTimes];
	long[] queueTimes = new long[numTimes];

	@Test
	public void testFindPlanBag() throws FileNotFoundException
	{
		ConflictParser parser = new ConflictParser();

		parser.parseDomain(domainName);
		parser.parseProblem(problemName);

		for (int i = 0; i < bagTimes.length; i++)
		{
			success = false;
			while (!success)
			{
				Planner planner = new BagMethod(parser, System.nanoTime());
				success = planner.search();
			}
		}
	}

	@Test
	public void testFindPlanQueue() throws FileNotFoundException
	{
		ConflictParser parser = new ConflictParser();

		parser.parseDomain(domainName);
		parser.parseProblem(problemName);

		for (int i = 0; i < queueTimes.length; i++)
		{
			success = false;
			while (!success)
			{
				Planner planner = new QueueMethod(parser, System.nanoTime());
				success = planner.search();
			}
		}
	}

	@Test
	public void testFindPlanPriority() throws FileNotFoundException
	{
		ConflictParser parser = new ConflictParser();

		parser.parseDomain(domainName);
		parser.parseProblem(problemName);

		for (int i = 0; i < priorityTimes.length; i++)
		{
			success = false;
			while (!success)
			{
				Planner planner = new PriorityQueueMethod(parser, System.nanoTime());
				success = planner.search();
			}
		}
	}
}