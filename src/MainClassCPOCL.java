import java.io.FileNotFoundException;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * @author Chris Lashley
 * runner for gathering times, average time, number of fails, and standard deviations
 */

public class MainClassCPOCL
{
//	static String domainName = "DeathMatchDomain.txt";
//	static String problemName = "DeathMatchProblem.txt";
//	static String domainName = "SimpleDeathMatchDomain.txt";
//	static String problemName = "SimpleDeathMatchProblem.txt";

//	static String domainName = "CryingBabyDomain.txt";
//	static String problemName = "CryingBabyProblem.txt";
	static String domainName = "SimpleCryingBabyDomain.txt";
	static String problemName = "SimpleCryingBabyProblem.txt";
//	static String domainName = "Reorder_and_Restriction_Domain.txt";
//	static String problemName = "Reorder_and_Restriction_Problem.txt";
	
//	static String domainName = "BirthdayDomain.txt";
//	static String problemName = "BirthdayProblem.txt";
//	static String domainName = "SimpleBirthdayDomain.txt";
//	static String problemName = "SimpleBirthdayProblem.txt";

//	static String domainName = "MarriageDomain.txt";
//	static String problemName = "MarriageProblem.txt";
//	static String domainName = "SimpleMarriageDomain.txt";
//	static String problemName = "SimpleMarriageProblem.txt";

//	static String domainName = "DinnerDomain.txt";
//	static String problemName = "DinnerProblem.txt";
//	static String domainName = "SimpleDinnerDomain.txt";
//	static String problemName = "SimpleDinnerProblem.txt";
	
/** 
 * Domain and Problem added by Philip Deppen
 */
//static String domainName = "BriefcaseDomain.txt";
//static String problemName = "BriefcaseProblem.txt";




	public static void main(String [] args) throws FileNotFoundException
	{
		int numPlans = 100;
		boolean success;

		double bagDev;
		double priorityDev;
		double queueDev;
		double bagAvg = 0.0;
		double priorityAvg = 0.0;
		double queueAvg = 0.0;
		long bagTotal = 0;
		long priorityTotal = 0;
		long queueTotal = 0;

		long[] bagTimes = new long[numPlans];
		long[] priorityTimes = new long[numPlans];
		long[] queueTimes = new long[numPlans];

		int bagFails = 0;
		int priorityFails = 0;
		int queueFails = 0;

		long start;
		long end;
		long time;
		double temp;


		ConflictParser parser = new ConflictParser();

		parser.parseDomain(domainName);
		parser.parseProblem(problemName);
		Planner planner;

//		for (int i = 0; i < bagTimes.length; i++)
//		{
//			success = false;
//			while (!success)
//			{
//				start = System.nanoTime();
////				start = System.currentTimeMillis();
//				planner = new BagMethod(parser, start);
//				success = planner.search();
//				end = System.nanoTime();
//				time = (end - start) / 1000;
////				time = System.currentTimeMillis() - start;
//
//				bagTimes[i] = time;
//				if (!success)
//					bagFails++;
//			}
//		}

		for (int i = 0; i < queueTimes.length; i++)
		{
			success = false;
			while (!success)
			{
				start = System.nanoTime();
//				start = System.currentTimeMillis();
				planner = new QueueMethod(parser, start);
				success = planner.search();
				end = System.nanoTime();
				time = (end - start) / 1000;
//				time = System.currentTimeMillis() - start;
				queueTimes[i] = time;
				if (!success)
					queueFails++;
			}
		}

		for (int i = 0; i < priorityTimes.length; i++)
		{
			success = false;
			while (!success)
			{
				start = System.nanoTime();
//				start = System.currentTimeMillis();
				planner = new PriorityQueueMethod(parser, start);
				success = planner.search();
				end = System.nanoTime();
//				time = System.currentTimeMillis() - start;
				time = (end - start) / 1000;
				priorityTimes[i] = time;
				if(!success)
					priorityFails++;
			}
		}


		DecimalFormat df = new DecimalFormat("##.##");
		df.setRoundingMode(RoundingMode.HALF_EVEN);

		System.out.println("\n\n\n\n\n");
		System.out.println(domainName + " B");
		for (int i = 0; i < numPlans; i++)
		{
//			System.out.println("B" + (i+1) + ": " + bagTimes[i]);
			System.out.println(bagTimes[i]);
		}
		System.out.println("\n\n\n\n\n");
		System.out.println(domainName + " Q");
		for (int i = 0; i < numPlans; i++)
		{
//			System.out.println("Q" + (i+1) + ": " + queueTimes[i]);
			System.out.println(queueTimes[i]);
		}
		System.out.println("\n\n\n\n\n");
		System.out.println(domainName + " P");
		for (int i = 0; i < numPlans; i++)
		{
//			System.out.println("P" + (i+1) + ": " + priorityTimes[i]);
			System.out.println(priorityTimes[i]);
		}



		for (int i = 0; i < bagTimes.length; i++)
		{
			bagTotal += bagTimes[i];
		}
		bagAvg = (bagTotal / (numPlans + 0.0));
		temp = 0.0;
		for (int i = 0; i < bagTimes.length; i++)
		{
			temp += ((bagTimes[i] - bagAvg) * (bagTimes[i] - bagAvg));
		}
		bagDev = (Math.sqrt(temp) / (numPlans + 0.0));
		System.out.println("\nB fails");
		System.out.println(bagFails);
		System.out.println("B Avg");
		System.out.println(df.format(bagAvg));
		System.out.println("B SDV");
		System.out.println(df.format(bagDev));

		for (int i = 0; i < queueTimes.length; i++)
		{
//			System.out.println(i + ": " + queueTimes[i]);
			queueTotal += queueTimes[i];
		}
		queueAvg = (queueTotal / (numPlans + 0.0));
		temp = 0.0;
		for (int i = 0; i < queueTimes.length; i++)
		{
			temp += ((queueTimes[i] - queueAvg) * (queueTimes[i] - queueAvg));
		}
		queueDev = (Math.sqrt(temp) / (numPlans + 0.0));
		System.out.println("Q fails");
		System.out.println(queueFails);
		System.out.println("Q Avg");
		System.out.println(df.format(queueAvg));
		System.out.println("Q SDV");
		System.out.println(df.format(queueDev));

		for (int i = 0; i < priorityTimes.length; i++)
		{
//			System.out.println(i + ": " + priorityTimes[i]);
			priorityTotal += priorityTimes[i];
		}
		priorityAvg = (priorityTotal / (numPlans + 0.0));
		temp = 0.0;
		for (int i = 0; i < priorityTimes.length; i++)
		{
			temp += ((priorityTimes[i] - priorityAvg) * (priorityTimes[i] - priorityAvg));
		}
		priorityDev = (Math.sqrt(temp) / (numPlans + 0.0));
		System.out.println("P fails");
		System.out.println(priorityFails);
		System.out.println("P Avg");
		System.out.println(df.format(priorityAvg));
		System.out.println("P SDV");
		System.out.println(df.format(priorityDev));
	}
}