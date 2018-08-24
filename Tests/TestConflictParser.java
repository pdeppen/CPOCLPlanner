
import static org.junit.Assert.*;



import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;

import org.junit.Test;


public class TestConflictParser
{
	@Test
	public void testParsingDomain() throws FileNotFoundException
	{
    	String domainName = "TestDomain2.txt";

		ConflictParser p = new ConflictParser();
		p.parseDomain(domainName);


		//First Action
		assertEquals("reload",p.ActionsDomain.get(0).getStepName());
		//Parameters
		assertEquals("?killer", p.ActionsDomain.get(0).getParameter(0).toString());
		//Preconditions
		assertEquals("person ?killer", p.ActionsDomain.get(0).getPreconditions(0).toString());
		assertEquals("status ?killer Healthy", p.ActionsDomain.get(0).getPreconditions(1).toString());
		assertEquals("weapon ?killer Unloaded", p.ActionsDomain.get(0).getPreconditions(2).toString());
		//Effects
		assertEquals("weapon ?killer Loaded", p.ActionsDomain.get(0).getEffects(0).toString());
		assertEquals("weapon ?killer Unloaded", p.ActionsDomain.get(0).getEffects(1).toString());
		assertFalse(p.ActionsDomain.get(0).getEffects(0).isNegative());				//is the effect negative
		assertTrue(p.ActionsDomain.get(0).getEffects(1).isNegative());				//is the effect negative
		//Agents
		assertEquals("?killer", p.ActionsDomain.get(0).getAgent(0).toString());
	}


	@Test
	public void testParsingProblem() throws FileNotFoundException
	{
    	String ProblemName = "TestProblem2.txt";

		ConflictParser p = new ConflictParser();
		p.parseProblem(ProblemName);

		//intial State
		assertEquals(":init",p.ProblemDomain.get(0).getStepName());
		assertEquals("status Timmy Healthy",p.ProblemDomain.get(0).getEffects(0).toString());
		assertEquals("status Hank Healthy",p.ProblemDomain.get(0).getEffects(1).toString());
		assertEquals("status Carl Healthy",p.ProblemDomain.get(0).getEffects(2).toString());
		assertEquals("status William Healthy",p.ProblemDomain.get(0).getEffects(3).toString());
		assertEquals("person Timmy",p.ProblemDomain.get(0).getEffects(4).toString());
		assertEquals("person Hank",p.ProblemDomain.get(0).getEffects(5).toString());
		assertEquals("person Carl",p.ProblemDomain.get(0).getEffects(6).toString());
		assertEquals("person William",p.ProblemDomain.get(0).getEffects(7).toString());
		assertEquals("weapon Timmy Unloaded",p.ProblemDomain.get(0).getEffects(8).toString());
		assertEquals("weapon Hank Unloaded",p.ProblemDomain.get(0).getEffects(9).toString());
		assertEquals("weapon Carl Unloaded",p.ProblemDomain.get(0).getEffects(10).toString());
		assertEquals("weapon William Unloaded",p.ProblemDomain.get(0).getEffects(11).toString());

		//intentions
		assertEquals("intends Timmy Timmy Healthy",p.IntentionDomain.get(0).getEffects(0).toString());
		assertEquals("intends Hank Hank Healthy",p.IntentionDomain.get(0).getEffects(1).toString());
		assertEquals("intends Carl Carl Healthy",p.IntentionDomain.get(0).getEffects(2).toString());
		assertEquals("intends William William Healthy",p.IntentionDomain.get(0).getEffects(3).toString());

		//Goal State
		assertEquals(":goal", p.ProblemDomain.get(1).getStepName());
		assertEquals("weapon Timmy Loaded",p.ProblemDomain.get(1).getPreconditions(0).toString());
		assertEquals("weapon William Loaded",p.ProblemDomain.get(1).getPreconditions(1).toString());
		assertEquals("status Carl Dead",p.ProblemDomain.get(1).getPreconditions(2).toString());
		assertEquals("status Hank Dead",p.ProblemDomain.get(1).getPreconditions(3).toString());
	}


	@Test
	public void testParsingPredicates() throws FileNotFoundException
	{
		String domainName = "TestDomain2.txt";

		ConflictParser p = new ConflictParser();
		p.parseDomain(domainName);

		//predicates
		assertEquals("status ?person ?status", p.PredicatesArray.get(0).toString());
		assertEquals("intends ?intender ?person ?status", p.PredicatesArray.get(1).toString());
		assertEquals("person ?person", p.PredicatesArray.get(2).toString());
		assertEquals("weapon ?person ?loaded", p.PredicatesArray.get(3).toString());
	}


	@Test
	public void testSettingLiteralPara() throws FileNotFoundException
	{
    	String domainName = "TestDomain2.txt";

		ConflictParser p = new ConflictParser();
		p.parseDomain(domainName);

		assertEquals("person ?killer", p.ActionsDomain.get(0).getPreconditions(0).toString());
		assertEquals("status ?killer Healthy", p.ActionsDomain.get(0).getPreconditions(1).toString());

		Literal literal = p.ActionsDomain.get(0).getPreconditions(0);
		// predicate w/ 1 parameter
		assertEquals("person ?killer", literal.toString());
		assertEquals(true,p.predicateArrayHas(literal));	// person is a predicate
		assertEquals(1,p.countParaInPredicate(literal));	// 1 parameter for person
		literal.setLiteralParameters(0, "Timmy");			// set parameter to Timmy
		assertEquals("person Timmy",literal.toString());

		// predicate w/ 2 parameters
		literal = p.ActionsDomain.get(0).getPreconditions(1);
		assertEquals("status ?killer Healthy",literal.toString());
		assertEquals(true,p.predicateArrayHas(literal));	// status is a predicate
		assertEquals(2,p.countParaInPredicate(literal));	// 2 parameters for status
		literal.setLiteralParameters(0, "Timmy");			// set first parameter
		assertEquals("status Timmy Healthy",literal.toString());
		literal.setLiteralParameters(1, "Dead");			// set second parameter
		assertEquals("status Timmy Dead",literal.toString());

		// action
		assertEquals("?killer", p.ActionsDomain.get(0).getParameter(0));
		assertEquals("kill[?killer, ?victim]", p.ActionsDomain.get(1).toString());
	}


	@Test
	public void testGettingOpenPrecondition() throws FileNotFoundException, CloneNotSupportedException
	{
		ConflictParser parser = new ConflictParser();
		String domainName = "TestDomain2.txt";
		String problemName = "TestProblem2.txt";
		parser.parseDomain(domainName);
		parser.parseProblem(problemName);

		Planner planner = new QueueMethod(parser, System.nanoTime());
		planner.addGoalOpenPrecondition();


		Literal firstprecon = planner.getOpenPrecondition().getOpenPrecondtion();
		assertEquals("weapon Timmy Loaded",firstprecon.toString());
		OpenPrecondition precon = new OpenPrecondition(0,firstprecon);
		planner.searchEffectsInActionDomain(precon);

		Literal secondprecon = planner.getOpenPrecondition().getOpenPrecondtion();
		assertEquals("weapon William Loaded",secondprecon.toString());
		precon = new OpenPrecondition(0,secondprecon);
		planner.searchEffectsInActionDomain(precon);

		Literal thirdprecon = planner.getOpenPrecondition().getOpenPrecondtion();
		assertEquals("status Carl Dead",thirdprecon.toString());
		precon = new OpenPrecondition(0,thirdprecon);
		planner.searchEffectsInActionDomain(precon);

		Literal fourthprecon = planner.getOpenPrecondition().getOpenPrecondtion();
		assertEquals("status Hank Dead",fourthprecon.toString());
		precon = new OpenPrecondition(0,fourthprecon);
		planner.searchEffectsInActionDomain(precon);

		assertEquals("weapon Timmy Loaded",planner.getOpenPrecondition().getOpenPrecondtion().toString());
		assertEquals("weapon William Loaded",planner.getOpenPrecondition().getOpenPrecondtion().toString());
		assertEquals("status Carl Dead",planner.getOpenPrecondition().getOpenPrecondtion().toString());
		assertEquals("status Hank Dead",planner.getOpenPrecondition().getOpenPrecondtion().toString());
		assertEquals("person Timmy",planner.getOpenPrecondition().getOpenPrecondtion().toString());
		assertEquals("status Timmy Healthy",planner.getOpenPrecondition().getOpenPrecondtion().toString());
		assertEquals("weapon Timmy Unloaded",planner.getOpenPrecondition().getOpenPrecondtion().toString());
		assertEquals("person William",planner.getOpenPrecondition().getOpenPrecondtion().toString());
		assertEquals("status William Healthy",planner.getOpenPrecondition().getOpenPrecondtion().toString());
		assertEquals("weapon William Unloaded",planner.getOpenPrecondition().getOpenPrecondtion().toString());
		assertEquals("person ?killer",planner.getOpenPrecondition().getOpenPrecondtion().toString());
		assertEquals("person Carl",planner.getOpenPrecondition().getOpenPrecondtion().toString());
		assertEquals("status ?killer Healthy",planner.getOpenPrecondition().getOpenPrecondtion().toString());
		assertEquals("status Carl Healthy",planner.getOpenPrecondition().getOpenPrecondtion().toString());

		precon = new OpenPrecondition(0,secondprecon);
		planner.searchEffectsInActionDomain(precon);

		assertEquals("weapon ?killer Loaded",planner.getOpenPrecondition().getOpenPrecondtion().toString());
		assertEquals("person ?killer",planner.getOpenPrecondition().getOpenPrecondtion().toString());
		assertEquals("person Hank",planner.getOpenPrecondition().getOpenPrecondtion().toString());
		assertEquals("status ?killer Healthy",planner.getOpenPrecondition().getOpenPrecondtion().toString());
		assertEquals("status Hank Healthy",planner.getOpenPrecondition().getOpenPrecondtion().toString());
		assertEquals("weapon ?killer Loaded",planner.getOpenPrecondition().getOpenPrecondtion().toString());
		assertEquals("person William",planner.getOpenPrecondition().getOpenPrecondtion().toString());
		assertEquals("status William Healthy",planner.getOpenPrecondition().getOpenPrecondtion().toString());
		assertEquals("weapon William Unloaded",planner.getOpenPrecondition().getOpenPrecondtion().toString());





	}


	@Test
	public void testCausalLink() throws FileNotFoundException
	{
		ConflictParser parser = new ConflictParser();
		// domain/problem for testing
		String domainName = "TestDomain2.txt";
		String problemName = "TestProblem2.txt";

		parser.parseDomain(domainName);
		parser.parseProblem(problemName);

//		Planner planner = new QueueMethod(parser);
//		Planner planner = new PriorityQueue(parser);
		Planner planner = new BagMethod(parser, System.nanoTime());

		Binding binding = new Binding(parser);

		planner.search();
	}


	@Test
	public void testGettingPreconditions() throws FileNotFoundException
	{
    	String domainName = "TestDomain2.txt";
		String problemName = "TestProblem2.txt";



		//Planner planner = new Planner(domainName,problemName);
		ConflictParser p = new ConflictParser();
		p.setDomainName(domainName);
		p.setProblemName(problemName);

		p.parseDomain(domainName);
		p.parseProblem(problemName);

		//Planner planner = new Planner(p);
		Planner planner = new PriorityQueueMethod(p, System.nanoTime());


		planner.search();
		assertEquals(":goal",planner.Actions.get(0).getStepName());
		assertEquals("reload",planner.Actions.get(1).getStepName());


		assertEquals("weapon Timmy Loaded", planner.Actions.get(0).getPreconditions(0).toString());
		assertEquals("weapon William Loaded", planner.Actions.get(0).getPreconditions(1).toString());
		assertEquals("status Carl Dead", planner.Actions.get(0).getPreconditions(2).toString());
		assertEquals("status Hank Dead", planner.Actions.get(0).getPreconditions(3).toString());
	}


	@Test
	public void bindStepByPrecondition()throws FileNotFoundException
	{
		ConflictParser parser = new ConflictParser();
		String domainName = "TestDomain2.txt";
		String problemName = "TestProblem2.txt";
		parser.parseDomain(domainName);
		parser.parseProblem(problemName);

		Planner planner = new Planner(parser, System.nanoTime());
		Binding binding = new Binding(parser);

		Literal openPreocondition = parser.getGoalPreconditions(2);
		assertEquals("status Carl Dead", openPreocondition.toString());


		Step kill = parser.ActionsDomain.get(1);
		binding.bindLiterals(kill, openPreocondition, 0);

		assertEquals("kill",kill.getStepName());
		assertEquals("person ?killer", kill.getPreconditions(0).toString());
		assertEquals("person Carl", kill.getPreconditions(1).toString());
		assertEquals("status ?killer Healthy", kill.getPreconditions(2).toString());
		assertEquals("status Carl Healthy", kill.getPreconditions(3).toString());
		assertEquals("weapon ?killer Loaded", kill.getPreconditions(4).toString());

		assertEquals("status Carl Dead", kill.getEffects(0).toString());
		assertEquals("status Carl Healthy", kill.getEffects(1).toString());
		assertTrue(kill.getEffects(1).isNegative());		//is the effect negative

		assertEquals("status Carl Dead", openPreocondition.toString());
		binding.bindStepByPrecondition(kill, openPreocondition);

		assertEquals("kill",kill.getStepName());
		assertEquals("person ?killer", kill.getPreconditions(0).toString());
		assertEquals("person Carl", kill.getPreconditions(1).toString());
		assertEquals("status ?killer Healthy", kill.getPreconditions(2).toString());
		assertEquals("status Carl Healthy", kill.getPreconditions(3).toString());
	}


	@Test
	public void bindNextVariable()throws FileNotFoundException
	{
		ConflictParser parser = new ConflictParser();
		String domainName = "TestDomain2.txt";
		String problemName = "TestProblem2.txt";
		parser.parseDomain(domainName);
		parser.parseProblem(problemName);

		Planner planner = new Planner(parser, System.nanoTime());
		Binding binding = new Binding(parser);
		OpenPrecondition openprecon = new OpenPrecondition(0, null);
		openprecon.addOpenPrcondition(parser.getGoalPreconditions(2));
		openprecon.addStep(parser.getGoalState().getStepId());

		assertEquals("status Carl Dead", parser.getGoalPreconditions(2).toString());
		Literal openPrecondition = parser.getGoalPreconditions(1);


		Step kill = parser.ActionsDomain.get(1);
		Literal precondition = parser.getGoalPreconditions(2);
		assertEquals("status Carl Dead", precondition.toString());

		assertEquals("person ?killer", kill.getPreconditions(0).toString());
		assertEquals("person ?victim", kill.getPreconditions(1).toString());
		assertEquals("status ?killer Healthy",kill.getPreconditions(2).toString());
		assertEquals("status ?victim Healthy", kill.getPreconditions(3).toString());
		assertEquals("weapon ?killer Loaded", kill.getPreconditions(4).toString());
		assertEquals("status ?victim Dead", kill.getEffects(0).toString());
		assertEquals("status ?victim Healthy", kill.getEffects(1).toString());
		assertTrue(kill.getEffects(1).isNegative());		//is the effect negative

		//bind the variables
		binding.bindLiterals(kill, precondition, 0);

		assertEquals("kill",kill.getStepName());
		assertEquals("person ?killer", kill.getPreconditions(0).toString());
		assertEquals("person Carl", kill.getPreconditions(1).toString());
		assertEquals("status ?killer Healthy",kill.getPreconditions(2).toString());
		assertEquals("status Carl Healthy", kill.getPreconditions(3).toString());
		assertEquals("weapon ?killer Loaded", kill.getPreconditions(4).toString());
		assertEquals("status Carl Dead", kill.getEffects(0).toString());
		assertEquals("status Carl Healthy", kill.getEffects(1).toString());
		assertTrue(kill.getEffects(1).isNegative());		//is the effect negative



		//The variables should be bounded right now but not fully bounded.
		assertEquals(true, binding.isBounded(openPrecondition));
		assertEquals("kill",kill.getStepName());
		assertEquals("person ?killer", kill.getPreconditions(0).toString());
		assertEquals("person Carl",kill.getPreconditions(1).toString());
		assertEquals("status ?killer Healthy", kill.getPreconditions(2).toString());
		assertEquals("status Carl Healthy", kill.getPreconditions(3).toString());
		assertEquals("status Carl Dead", kill.getEffects(0).toString());
		assertEquals("status Carl Healthy", kill.getEffects(1).toString());
		assertTrue(kill.getEffects(1).isNegative());		//is the effect negative

		//grab the next openPrecondition
		openPrecondition = kill.getPreconditions(0);
		assertEquals("person ?killer",openPrecondition.toString());

		//The new openPrecondition is not yet bounded.
		assertEquals(false, binding.isBounded(openPrecondition));
		assertEquals("kill",kill.getStepName());
		assertEquals("person ?killer", kill.getPreconditions(0).toString());
		assertEquals("person Carl",kill.getPreconditions(1).toString());
		assertEquals("status ?killer Healthy", kill.getPreconditions(2).toString());
		assertEquals("status Carl Healthy", kill.getPreconditions(3).toString());
		assertEquals("status Carl Dead", kill.getEffects(0).toString());
		assertEquals("status Carl Healthy", kill.getEffects(1).toString());
		assertTrue(kill.getEffects(1).isNegative());		//is the effect negative

		//bind the new openPrecondition
		binding.bindStep(kill, "?killer", "William");
		assertEquals("person William", openPrecondition.toString());

		//The variables should be fully bounded now
		assertEquals(true, binding.isBounded(openPrecondition));
		assertEquals("kill",kill.getStepName());
		assertEquals("person William", kill.getPreconditions(0).toString());
		assertEquals("person Carl",kill.getPreconditions(1).toString());
		assertEquals("status William Healthy", kill.getPreconditions(2).toString());
		assertEquals("status Carl Healthy", kill.getPreconditions(3).toString());
		assertEquals("status Carl Dead", kill.getEffects(0).toString());
		assertEquals("status Carl Healthy", kill.getEffects(1).toString());
		assertTrue(kill.getEffects(1).isNegative());		//is the effect negative
	}


	@Test
	public void testBinding() throws FileNotFoundException
	{
		ConflictParser parser = new ConflictParser();

		String domainName = "TestDomain2.txt";
		String problemName = "TestProblem2.txt";
		parser.parseDomain(domainName);
		parser.parseProblem(problemName);

		Planner planner = new Planner(parser, System.nanoTime());
		Binding binding = new Binding(parser);

		Step kill = parser.ActionsDomain.get(1);
		Literal precondition = parser.getGoalPreconditions(2);
		assertEquals("status Carl Dead", precondition.toString());

		assertEquals("person ?killer", kill.getPreconditions(0).toString());
		assertEquals("person ?victim", kill.getPreconditions(1).toString());
		assertEquals("status ?killer Healthy",kill.getPreconditions(2).toString());
		assertEquals("status ?victim Healthy", kill.getPreconditions(3).toString());
		assertEquals("weapon ?killer Loaded", kill.getPreconditions(4).toString());
		assertEquals("status ?victim Dead", kill.getEffects(0).toString());
		assertEquals("status ?victim Healthy", kill.getEffects(1).toString());
		assertTrue(kill.getEffects(1).isNegative());		//is the effect negative

		//bind the variables
		binding.bindLiterals(kill, precondition, 0);

		assertEquals("kill",kill.getStepName());
		assertEquals("person ?killer", kill.getPreconditions(0).toString());
		assertEquals("person Carl", kill.getPreconditions(1).toString());
		assertEquals("status ?killer Healthy",kill.getPreconditions(2).toString());
		assertEquals("status Carl Healthy", kill.getPreconditions(3).toString());
		assertEquals("weapon ?killer Loaded", kill.getPreconditions(4).toString());
		assertEquals("status Carl Dead", kill.getEffects(0).toString());
		assertEquals("status Carl Healthy", kill.getEffects(1).toString());
		assertTrue(kill.getEffects(1).isNegative());		//is the effect negative

		//To check if it is fully bounded or not
		assertTrue(binding.isBounded(precondition));
		assertFalse(binding.isBounded(kill.getPreconditions(0)));	//In C1 ?p
		assertTrue(binding.isBounded(kill.getPreconditions(1)));	//Cargo C1

		//To check which parameter is not bounded
		assertEquals(0,binding.checkParaNotBounded(kill.getPreconditions(0))); //In C1 ?p


		//This is to test binding literals "Literals that are not fully bounded"
		assertEquals("status Carl Dead", precondition.toString());
		precondition.setLiteralParameters(0, "?victim");
		assertEquals("status ?victim Dead", precondition.toString());
		assertFalse(binding.isBounded(precondition));


		binding.bindLiterals(kill, precondition, 0);

		assertEquals("kill",kill.getStepName());
		assertEquals("person ?killer", kill.getPreconditions(0).toString());
		assertEquals("person ?victim",kill.getPreconditions(1).toString());
		assertEquals("status ?killer Healthy", kill.getPreconditions(2).toString());
		assertEquals("status ?victim Healthy", kill.getPreconditions(3).toString());
		assertEquals("status ?victim Dead", kill.getEffects(0).toString());
		assertEquals("status ?victim Healthy", kill.getEffects(1).toString());
		assertTrue(kill.getEffects(1).isNegative());		//is the effect negative
	}


	@Test
	public void testPlanner() throws FileNotFoundException
	{
		String domainName = "TestDomain2.txt";
		String problemName = "TestProblem2.txt";

		String name = "GoalState";
		ArrayList<Literal> preconditions = null; //= new ArrayList <Literal>();
		ArrayList<Literal> effects = null;

		ConflictParser parser = new ConflictParser();
		parser.parseDomain(domainName);
		parser.parseProblem(problemName);
		Planner planner = new Planner(parser, System.nanoTime());


		Literal literal = new Literal(null, null);
		literal.addLiteralName("status");
		literal.addLiteralParameters("?person");
		literal.addLiteralParameters("?status");
		assertEquals("status ?person ?status", literal.toString());

		literal.setLiteralParameters(0, "Timmy");
		literal.setLiteralParameters(1, "Healthy");
		assertEquals("status Timmy Healthy", literal.toString());
	}
}