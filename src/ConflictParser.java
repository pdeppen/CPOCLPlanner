import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author khaledalkathiri
 */

/**
 * @author Chris Lashley
 * modified to use intentions and intentional agents for conflict
 */

public class ConflictParser
{
	/** just testing */
	Restrictions restrictions = new Restrictions("Conflict Parser");
	
	// instance variables for parsing the domain
	private String predicates = ":predicates ";
	private String action = ":action ";
	private String parameters = ":parameters ";
	private String precondition = ":precondition ";
	private String effect = ":effect ";
	private String agents = ":agents ";	// for consent
	private String delims = "and";
	private String[] ActionName = null;
	private String[] ParameterStyle = null;
	private String[] ActionPrecondition = null;
	private String[] ActionEffect = null;
	private String[] ActionAgents = null;	// for consent
	private String[] DomainPredicates = null;


	//instance variables for parsing the problem
	private String initialState = ":init";
	private String intention = ":intentions";
	private String goalState = ":goal";
	private String[] initialStateLiterals = null;
	private String[] intentionLiterals = null;
	private String[] goalStateLiterals = null;


	public ArrayList <Step> ActionsDomain = new ArrayList <Step>();
	public ArrayList <Step> ProblemDomain = new ArrayList <Step>();
	public ArrayList <Step> IntentionDomain = new ArrayList <Step>();
	private Step step;

	public ArrayList <Literal> PredicatesArray = new ArrayList <Literal>();
	public ArrayList <Literal> IntentionsArray = new ArrayList <Literal>();

	private String name;

	private Literal literal;

	private String problemName;
	private String domainName;

	public ConflictParser()
	{
		this.ActionsDomain = new ArrayList <Step>();
		this.ProblemDomain = new ArrayList <Step>();
		this.IntentionDomain = new ArrayList <Step>();

		this.PredicatesArray = new ArrayList <Literal>();
		this.IntentionsArray = new ArrayList <Literal>();


	}


	public void randomizeActions()
	{
		Collections.shuffle(ActionsDomain);
	}

	public String getDomainName()
	{
		return domainName;
	}

	public String getProblemName()
	{
		return problemName;
	}

	public void setDomainName(String domainName)
	{
		this.domainName = domainName;
	}

	public void setProblemName(String problemName)
	{
		this.problemName = problemName;
	}


	/**
	 * This function is to parse the domain file
	 * @param fileName
	 * @throws FileNotFoundException
	 */
	public void parseDomain(String fileName) throws FileNotFoundException
	{

		File text = new File(fileName);
		Scanner scan = new Scanner(text);

		while(scan.hasNextLine())
		{
			step = new Step(null, null, null, null, null, -1);

			String line = scan.nextLine();

			if(line.startsWith(";;;"))
				line = scan.nextLine();

			else if(line.contains(predicates))
			{
				DomainPredicates = line.split(predicates);

				Matcher predicate = Pattern.compile("\\(([^)]+)\\)").matcher(DomainPredicates[1]);
				while(predicate.find())
				{
					//adding the preconditions to the step
					literal = new Literal(null, null);
					Literal s = literal.parseStringToLiteral(predicate.group(1));

					PredicatesArray.add(s);
						//System.out.println(s.getLiteralParameters(0));
						//System.out.print("  \t");
				}
			}
			//System.out.print("  \n");

			else if(line.contains(action))
			{
				ActionName = line.split(action);
					//System.out.print(ActionName[1] +"  ");
				name = ActionName[1];

				//adding the action name to the step
				step.addStepName(name);

				//Action parameters
				String ActionLine = scan.nextLine();
				if(ActionLine.contains(parameters))
				{
					ParameterStyle = ActionLine.split(parameters);
					step.addParameter(parseParameters(ParameterStyle[1]));
					ActionLine = scan.nextLine();
				}

				//Action preconditions
				if(ActionLine.contains(precondition))
				{
					ActionPrecondition = ActionLine.split(delims);
						//System.out.println("Preconditions:  ");

					Matcher pre = Pattern.compile("\\(([^)]+)\\)").matcher(ActionPrecondition[1]);
					while(pre.find())
					{
						//adding the preconditions to the step
						//System.out.print(pre.group(1));
						//literal = new Literal(null,null);

						Literal s = literal.parseStringToLiteral(pre.group(1));
							//System.out.println(pre.group(1));
						step.addPreconditions(s);
							//System.out.print(s.getLiteralName());
							//System.out.println(s.getLiteralParameters(0));

							//System.out.print("  \t	");

					}
					ActionLine = scan.nextLine();
				}
				//System.out.println("");

				//Action Effects
				if(ActionLine.contains(effect))
				{
					ActionEffect = ActionLine.split(delims);
						//System.out.println("Effects:  ");

					Matcher ef = Pattern.compile("\\(([^)]+)\\)").matcher(ActionEffect[1]);
					while(ef.find())
					{
						//literal = new Literal(null,null);
						//System.out.println(ef.group(1));
						//adding the effects to the step
						Literal s = literal.parseStringToLiteral(ef.group(1));
						step.addEffects(s);
							//System.out.print(s.getLiteralParameters(1));
							//System.out.print("  \t");
					}
					ActionLine = scan.nextLine();
				}

				//Action parameters
				if(ActionLine.contains(agents))
				{
					ActionAgents = ActionLine.split(agents);
					step.addAgent(parseAgents(ActionAgents[1]));
//					ActionLine = scan.nextLine();
				}

				//adding the step to the arrayList of steps
				ActionsDomain.add(step);
					//System.out.println("\n");
			}
		}
		scan.close();
	}

	/**
	 * This function is to parse the problem file
	 * @param fileName
	 * @throws FileNotFoundException
	 */
	public void parseProblem(String fileName) throws FileNotFoundException
	{
		//fileName = problemName;

		File text = new File(fileName);
		Scanner scan = new Scanner(text);

		while(scan.hasNextLine())
		{
			String line = scan.nextLine();

			// new need to test
			if(line.startsWith(";;;"))
				line = scan.nextLine();
			// end new

			else if(line.contains(initialState))
			{
				step = new Step(null, null, null, null, null, -1);
				literal = new Literal(null,null);

				//System.out.println("Initial State: ");

				initialStateLiterals = line.split(initialState);

				step.addStepName(initialState);
				Matcher literals = Pattern.compile("\\(([^)]+)\\)").matcher(initialStateLiterals[1]);
				while(literals.find())
				{
					//step.addEffects(litrals.group(1));
						//int numOfPara =literal.searchInPredicateArray(this,litrals.group(1));
						Literal s = literal.parseStringToLiteral(literals.group(1));
						//System.out.println(s.getLiteralParameters(0));
						step.addEffects(s);

				}
				line = scan.nextLine();
				ProblemDomain.add(0, step);
			}

// add intentions here
			else if(line.contains(intention))
			{
				step = new Step(null, null, null, null, null, -1);
				Intentions intent;
				literal = new Literal(null,null);
//
//				//System.out.println("Intentions: ");
//
				intentionLiterals = line.split(intention);
//
				step.addStepName(intention);
				Matcher literals = Pattern.compile("\\(([^)]+)\\)").matcher(intentionLiterals[1]);
				while(literals.find())
				{
//					//step.addEffects(litrals.group(1));
//						//int numOfPara =literal.searchInPredicateArray(this,litrals.group(1));
						Literal s = literal.parseStringToLiteral(literals.group(1));
//						//System.out.println(s.getLiteralParameters(0));
						step.addEffects(s);
//
						intent = new Intentions(s);
						IntentionsArray.add(s);
				}
				line = scan.nextLine();

				IntentionDomain.add(0, step);
			}

			else if(line.contains(goalState))
			{
				step = new Step(null, null, null, null, null, 0);

//				goalStateLiterals = line.split(delims);
				goalStateLiterals = line.split(goalState);
					//System.out.println("\nGoal: ");
				step.addStepName(goalState);
				Matcher literals = Pattern.compile("\\(([^)]+)\\)").matcher(goalStateLiterals[1]);
				while(literals.find())
				{
					Literal s = literal.parseStringToLiteral(literals.group(1));
					step.addPreconditions(s);
					//step.addPreconditions(litrals.group(1));
						//System.out.println(litrals.group(1));
				}
				ProblemDomain.add(1, step);

			}

		}
		scan.close();
	}

	//Problem File
	/**
	 * This function is to get the goal preconditions in the ProblemDomain
	 * @param index
	 * @return
	 */
	public Literal getGoalPreconditions(int index)
	{
		return ProblemDomain.get(1).getPreconditions(index);
	}



	/**
	 * This function will return the size of the precondition(Goal state) in the problem domain
	 * @param index
	 * @return
	 */
	public int getProblemDomainPreconditionSize()
	{
		return ProblemDomain.get(1).getPreconditionSize();
	}


	/**
	 * This function will return the size of the effects(initial state) in the problem domain
	 * @param index
	 * @return
	 */
	public int getProblemDomainEffectsSize()
	{
		return ProblemDomain.get(0).getEffectsSize();
	}

	/**
	 * This function is to get the effects of the initialState
	 * @param index
	 * @return
	 */
	public Literal getIntialStateEffects(int index)
	{
		return ProblemDomain.get(0).getEffects(index);
	}

	/**
	 * This function is to return the initial state
	 * @return
	 */
	public Step getInitialState()
	{
		return ProblemDomain.get(0);
	}

	/**
	 * This function is to return the goal state
	 * @return
	 */
	public Step getGoalState()
	{
		return ProblemDomain.get(1);

	}

	//Preconditions
	/**
	 * This function will return the size of the preconditions in the ActionDomain
	 * @param index
	 * @return
	 */
	public int getActionsDomainPreconditionSize(int index)
	{
		return ActionsDomain.get(index).getPreconditionSize();
	}


	/**
	 * This function is to get the preconditions in the ActionDomain
	 * @param step
	 * @param index
	 * @return
	 */
	public Literal getActionsPreconditions(int step, int index)
	{
		return ActionsDomain.get(step).getPreconditions(index);
	}


	//Effects
	/**
	 * This function will return the size of the effects in the ActionDomain
	 * @param index
	 * @return
	 */
	public int getActionsDomainEffectSize(int index)
	{
		return ActionsDomain.get(index).getEffectsSize();
	}

	/**
	 * This function is to get the effects in the ActionDomain
	 * @param step
	 * @param index
	 * @return
	 */
	public Literal getActionsEffects(int step, int index)
	{
		return ActionsDomain.get(step).getEffects(index);
	}


	//Actions
	/**
	 * This function is to get the size of the ActionDomain
	 * @return
	 */
	public int getActionDomainSize()
	{
		return ActionsDomain.size();
	}


	public Step getAction(int index)
	{
		return ActionsDomain.get(index);
	}

	//Parameter
	public ArrayList<String> parseParameters(String parameter)
	{
		ArrayList <String> paraArray = new ArrayList<String>();
		String[] delim;
		delim = parameter.split("\\s+");
		for(int i=0;i<delim.length;i++)
		{
			if(delim[i].contains("?"))
			{
				paraArray.add(delim[i]);
			}

		}
		return paraArray;
	}

	// new for consent
	//Agents
	public ArrayList<String> parseAgents(String agent)
	{
		ArrayList <String> agentArray = new ArrayList<String>();
		String[] delim;
		delim = agent.split("\\s+");
		for(int i=0;i<delim.length;i++)
		{
			if(delim[i].contains("?"))
			{
				agentArray.add(delim[i]);
			}

		}
		return agentArray;
	}
	// end new

	//Predicates
	public Literal getPredicates(int index)
	{
		return PredicatesArray.get(index);
	}

	public int SizePredicatesArray()
	{
		return PredicatesArray.size();
	}

	/**
	 *
	 * @param lit
	 * @return
	 */
	public boolean predicateArrayHas(Literal lit)
	{
		int i = this.SizePredicatesArray();
		for(int f =0; f<i; f++)
		{
			String predicate = PredicatesArray.get(f).getLiteralName();
			if(predicate.equals(lit.getLiteralName()))
				return true;
		}

		return false;
	}
	/**
	 * This method is to return the size of parameters in each method
	 * @param name
	 * @return
	 */
	public int countParaInPredicate(Literal name)
	{
		int size=0;
		if(this.predicateArrayHas(name))
		{

			size = name.sizeLiteralParameters();
		}
		return size;
	}

	/**
	 * This method return the predicate with the grounded letters
	 * @param literal
	 * @return
	 */
	public Literal getPredicate(Literal literal)
	{

		int i = this.SizePredicatesArray();
		for(int f =0; f<i; f++)
		{
			String predicate = PredicatesArray.get(f).getLiteralName();
			if(predicate.equals(literal.getLiteralName()))
				return PredicatesArray.get(f);
		}

		return null;
	}
}