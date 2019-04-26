import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

/**
 * @author khaledalkathiri
 */

/**
 * @author Chris Lashley
 * added intention handling, ability to break out of loop using time parameter, and step details
 */

public class Planner
{
	/* added 11/10/18 by Philip Deppen */
	OpenPrecondition threateningPrecondition;
	ArrayList <Literal> mostRecentMadeGoal = new ArrayList<Literal>();
	int currentGoal = 0;
	OpenPrecondition newOpenPrecondition;
	
	LinkedList <Step>  Actions = new LinkedList <Step>();
	ArrayList <CausalLink>  Links = new ArrayList <CausalLink>();
	
	LinkedList <OpenPrecondition> openPrecon = new LinkedList <OpenPrecondition>();

	Ordering<Step> graph = new Ordering<Step>();
	ArrayList <Literal> intent = new ArrayList <Literal>();

	protected ConflictParser parser;
	protected Literal literal;
	protected Binding binding;
	protected Step step;
	protected CausalLink causalLink;
	//	protected Order ordering;
	protected int key;
	//protected OpenPrecondition openPrecondition;
	protected int StepId;
	protected Ordering<Step> newOrdering;

	// to kick out a plan from being stuck in a loop (default .5s since 90%+ run in less than 0.001)
	protected long startTime;
	protected long currentTime;
	protected long duration;
	
	protected boolean restriction;

	/**
	 * create a plan using a selected parser and start time
	 * @param parser the domain/problem parser
	 * @param startTime system time in nanoseconds "System.nanoTime()"
	 * @throws FileNotFoundException
	 */
	public Planner(ConflictParser p, long startTime) throws FileNotFoundException
	{
		this.parser = p;
		binding = new Binding(p);
		this.StepId=0;
		Step goalState = p.getGoalState();
		goalState.setStepId(0);
		newOrdering = new Ordering<Step>();
		intent = p.IntentionsArray;
		this.startTime = startTime;
	}

	/**
	 * create a plan using a selected parser and start time
	 * @return true if successful plan found, false if not
	 * @throws FileNotFoundException
	 */
	public boolean search() throws FileNotFoundException
	{
		return true;
	}

	/**
	 * This function is to add preconditions to the openPreconditions queue
	 * @param step
	 */
	public void addPreconditions(Step step)
	{

	}

	/**
	 * This function is to add the goal preconditions to the openPrecondition array
	 * @throws FileNotFoundException
	 */
	public void addGoalOpenPrecondition() throws FileNotFoundException
	{
		//System.out.println("/*****In addGoalOpenPrecondition() --> Planner class*****/");
		
		//to get how many preconditions in the goal state
		for(int i = 0; i < parser.getProblemDomainPreconditionSize(); i++)
		{
			OpenPrecondition object = new OpenPrecondition(0,null);
			object.addOpenPrcondition(parser.getGoalPreconditions(i));
			object.addStep(0);
						
			openPrecon.addLast(object);

		}

	}
			
	/**
	 * Created 12/13/18 - use this in place of detectPotentialThreatOld
	 */
	public boolean detectPotentialThreat(OpenPrecondition condition, Step step)
	{
		boolean threat = false;
		
		ArrayList <CausalLink> threats = new ArrayList <CausalLink>();
		
		threats = this.getThreatenCausalLinks(condition);
		
		if (!threats.isEmpty()) {
			System.out.println("Restriction Added");
			threat = true;
			condition.getOpenPrecondtion().hasNegativeSign(true);
		}		
		
		threats = this.getThreatenCausalLinks(step);
		
		if (!threats.isEmpty()) {
			threat = true;
			System.out.println("Restriction Added");
			condition.getOpenPrecondtion().hasNegativeSign(true);
		}
					
//		if (threat) {
//			System.out.println("Restriction Added");	
//		}
		
//		return false;
		return threat;
		
	}
	
	/**
	 * Created by Philip Deppen (3/12/19, issue 11)
	 * @param condition
	 * @param step
	 * @return
	 */
	public boolean detectPotentialInitial(OpenPrecondition condition, Step step)
	{
		boolean threat = false;
		
		ArrayList <CausalLink> threats = new ArrayList <CausalLink>();
		
		threats = this.getThreatenCausalLinks(condition);
		
		if (!threats.isEmpty()) {
			threat = true;
			condition.getOpenPrecondtion().hasNegativeSign(true);
		}		
		
//		threats = this.getThreatenCausalLinks(step);
//		
//		if (!threats.isEmpty()) {
//			threat = true;
//			condition.getOpenPrecondtion().hasNegativeSign(true);
//		}
				
		
		return threat;
	}
	
	/**
	 * This method is to check the initial state if it satisfies the openPrecondition
	 * Works only with the bounded precondition
	 * @param precondition
	 * @return true if the precondition is found
	 * @return false if the precondition is not found
	 */
	//TODO: check threats before creating new one
	public boolean searchEffectInInitialState(OpenPrecondition openPrecondition)
	{
		//System.out.println("/*****In searchEffectInInitialState() --> Planner class*****/");

		Literal precondition = openPrecondition.getOpenPrecondtion();
		Step currentStep = Actions.get(openPrecondition.getStepID());
		
//		System.out.println("Current Step: " + currentStep.getStepName());
						
		int effSize = parser.getProblemDomainEffectsSize();
		for(int i=0; i< effSize;i++)
		{
			String temp = precondition.toString();
			
			if(temp.equals(parser.getIntialStateEffects(i).toString())) 
			{
				/* added print statement 12/10/18 */
				System.out.println(parser.getIntialStateEffects(i) + " is negative : " + parser.getIntialStateEffects(i).isNegative());
				if(!(parser.getIntialStateEffects(i).isNegative())) 
				{
					/* detecting potential threat when searching effects of actions */
//					if (this.detectPotentialInitial(openPrecondition, currentStep))
//						return false;
					
					System.out.println("Found In initial State");

					//this.applyNegation(currentStep);
					
					causalLink = new CausalLink(openPrecondition,parser.getInitialState(),parser.getIntialStateEffects(i));
					causalLink.getPrecondition().getOpenPrecondtion().setExcuted(true);
					Links.add(causalLink);

					//this.addOrdering(currentStep, parser.getInitialState());
					graph.add(currentStep, parser.getInitialState());
										
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Edited by Philip Deppen (12/3/18, issue 4)
	 * Edited by Philip Deppen (3/12/18, issue 11)
	 * This function searches for an effect in ActionDomain to solve an open precondition
	 * @param precondition
	 */
	public boolean searchEffectsInActionDomain(OpenPrecondition openPrecondition)
	{
	//	System.out.println("/*****In searchEffectsInActionDomain() --> Planner class*****/");

		Literal precondition= openPrecondition.getOpenPrecondtion();
		int id = openPrecondition.getStepID();

		Step currentStep = Actions.get(openPrecondition.getStepID());

		//ordering = new Order(null,null);

		int stepsNum = parser.getActionDomainSize();		//how many action in the domain
		for(int i=0; i< stepsNum;i++)
		{
			int effectNum = parser.getActionsDomainEffectSize(i);		//how many effects in every action
			for(int f=0; f< effectNum; f++)
			{
				//System.out.println(parser.getActionsEffects(i, f).getLiteralName());
				if(precondition.getLiteralName().equals(parser.getActionsEffects(i, f).getLiteralName()))
				{
					// Both literals are either + or -
					/* conditional made 12/3/18 
					 * edited on 12/4/18 - added "checkInitial" and "precondition.isNegative" which was a key part 
					 * TODO: make this statement work with all domains 
					 * TODO: add this else if statement to all methods that search for a literal 
					 */
					if((!parser.getActionsEffects(i, f).isNegative() && !precondition.isNegative()) || (parser.getActionsEffects(i, f).isNegative() && precondition.isNegative()))
					{
						step = parser.getAction(i);
												
						Step newStep = new Step(step);

///////////
///////////
///////////
// check intentions
///////////
///////////
///////////
//						checkIntentions();

						//bind the variables
						newStep = binding.bindLiterals(newStep, precondition , f);

						StepId+=1;
						newStep.setStepId(StepId);


						addPreconditions(newStep);
						
						// checks for potential threat
						if (this.detectPotentialThreat(openPrecondition, newStep))
							return false;
						
						Actions.addLast(newStep);
						//this.addOrdering(currentStep, newStep);
						graph.add(currentStep, newStep);

						//this.applyNegation(boundedStep);

						
						//add causal Link
						causalLink = new CausalLink(openPrecondition,newStep,newStep.getEffects(f));
						causalLink.getPrecondition().getOpenPrecondtion().setExcuted(true);
						Links.add(causalLink);
						//System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");

						return true;

					}
				}
			}
		}
		/** add restrictions here ?*/
		//Restrictions restriction = new Restrictions(precondition, id, currentStep);
		
		
		System.out.println("/********** RETURNING FALSE -> IN searchEffectsInActionDomain() at bottom Planner class");
		return false;
		//return true;
	}

	/**
	 * This method checks if there is any causal links between these two steps
	 * @param current
	 * @param next
	 * @return true if there is a connection
	 * @return false if there is no connection
	 * 
	 * NOT CALLED ANYWHERE
	 */
	public boolean stepsAreConnectedWithCausalLink(Step current, Step next)
	{
		//System.out.println("/*****In stepsAreConnectedWithCausalLink() --> Planner class*****/");

		int nextStepID = next.getStepId();

		//int effectSize = next.getEffectsSize();
		int effectSize = current.getEffectsSize();
		System.out.println(current.getEffects(0).toString());

		for(int i=0;i<effectSize;i++)
		{
			for(int f=0;f <Links.size();f++)
			{
				if(Links.get(f).getEffect().toString().equals(current.getEffects(i).toString()))
				{
					if((Links.get(f).getPrecondition().getStepID() == nextStepID)&&(Links.get(f).getStepName() == next))
					{
						System.out.println(Links.get(f).toString());
						System.out.println(next.toString()+"current" + current.toString()+"Steps are connected with CL \n\n\n\n\n\n\n\n\n");
						return true;
					}
				}
			}

		}

		return false;
	}


	/**
	 * This method searches for an effect in the Actions array
	 * @param openPrecondition
	 * @return
	 */
	public boolean searchInEffects(OpenPrecondition openPrecondition)
	{
		//System.out.println("/*****In searchInEffects() --> Planner class*****/");

		Literal precondition= openPrecondition.getOpenPrecondtion();
		Step currentStep = Actions.get(openPrecondition.getStepID());

		int sizeActions = Actions.size();
		//bc the first action is goal
		for(int i=1;i<sizeActions;i++)
		{
			if((openPrecondition.getStepID() != i))		//not to search in the same Action's effect
			{
				int sizeEffects = Actions.get(i).getEffectsSize();
				for(int x=0;x<sizeEffects;x++)
				{
					Literal effect = Actions.get(i).getEffects(x);
					// precondition and effect are both positive or negative
					if(	(!effect.isNegative() && !precondition.isNegative() && effect.toString().equals(precondition.toString())) || (effect.isNegative() && precondition.isNegative() && effect.toString().equals(precondition.toString())))
					{

						if(!(graph.containsOrdering(Actions.get(i), currentStep )))
						{
							System.out.println("Found in Effects");
							//this.addOrdering(currentStep, Actions.get(i));
							
							if (this.detectPotentialThreat(openPrecondition, currentStep))
								return false;

							graph.add(currentStep, Actions.get(i));


							//add causal Link
							causalLink= new CausalLink(openPrecondition,Actions.get(i),effect);
							causalLink.getPrecondition().getOpenPrecondtion().setExcuted(true);
							Links.add(causalLink);

							return true;
						}




						//						if(!(this.stepsAreConnectedWithCausalLink(currentStep, Actions.get(i))))
						//						{
						//							System.out.println("Found in Effects");
						//							//this.addOrdering(currentStep, Actions.get(i));
						//
						//
						//							graph.add(currentStep, Actions.get(i));
						//
						//
						//							//add causal Link
						//							causalLink= new CausalLink(openPrecondition,Actions.get(i),effect);
						//							causalLink.getPrecondition().getOpenPrecondtion().setExcuted(true);
						//							Links.add(causalLink);
						//
						//							return true;
						//						}
					}
				}
			}
		}

		return false;
	}




	/**
	 * This function searches for any literal that can resolve the open Precondition
	 * Edited by Philip Deppen (12/3/18, issue 4)
	 * Edited by Philip Deppen (1/9/19, issue 10)
	 */
	public boolean searchAnyInInitialState(OpenPrecondition openPrecondition, ArrayList<Literal> array)
	{
		//System.out.println("/*****In searchAnyInitialState() --> Planner class*****/");

		Literal precondition = openPrecondition.getOpenPrecondtion();
		Step currentStep = Actions.get(openPrecondition.getStepID());

		/** TODO: Comment out to have static plan */
		Collections.shuffle(array);
		
		Literal literal;
		
		for (int a = 0; a < array.size(); a++)
		{
			literal = array.get(a);
			
			int paraNotBounded = binding.checkParaNotBounded(precondition);
			
			if(!(array.get(a).isNegative()) && !currentStep.getSelectedLiteralExists(literal.getLiteralParameters(paraNotBounded)))
			{
				String groundLetter = precondition.getLiteralParameters(paraNotBounded);
				String newVariable;
				newVariable = array.get(a).getLiteralParameters(paraNotBounded);
				currentStep.setSelectedLiteral(newVariable);
				
				System.out.println("newVariablex: " + newVariable);

				////////////////
				String tempPreCond = groundLetter;
				System.out.println("groundLetter: " + tempPreCond);
				System.out.println("newVariable: " + newVariable);
				////////////////

				//to bind the dequeued precondition with the new parameters
				binding.bindPrecondtion(precondition, groundLetter, newVariable);
				binding.bindStep(currentStep, groundLetter, newVariable);

// print details & check intentions
				printStepDetails(currentStep);
				if (checkIntentions(currentStep) == true)
				{
					causalLink = new CausalLink(openPrecondition,parser.getInitialState(),array.get(0));

					causalLink.getPrecondition().getOpenPrecondtion().setExcuted(true);
					Links.add(causalLink);
					//this.addOrdering(currentStep, parser.getInitialState());
					graph.add(currentStep, parser.getInitialState());

					//				System.out.println("*********************" + precondition.toString());

					return true;
				}
				else
				{
					return false;
				}

			}
		}
		return false;

	}

	/**
	 * This methods searches for a literal in the intial state that is similar to it but not fully bounded.
	 * Edited by Philip Deppen (12/3/18, issue 4)
	 * @param precondition
	 * @return
	 */
	public boolean searchSimilarInInitialState(OpenPrecondition openPrecondition)
	{
		//System.out.println("/*****In searchSimilarInInitialState() --> Planner class*****/");

		Literal precondition = openPrecondition.getOpenPrecondtion();
		Step currentStep = Actions.get(openPrecondition.getStepID());


		ArrayList <Literal> array = new ArrayList <Literal>();
		int sizeEffectInitailState = parser.getProblemDomainEffectsSize();

		int index=0;
		int y;
		for( y =0; y< sizeEffectInitailState;y++)		//The size of effect in initial state
		{
			/* added second conditional 11/16/18 issue #4 */
			if(precondition.getLiteralName().equals(parser.getIntialStateEffects(y).getLiteralName()))
			{
					array.add(index,parser.getIntialStateEffects(y));
					index++;
		
			}
		}


		//loop through the array to check if there is a match in the initial State
		for(int f=0; f< array.size();f++)
		{
			int paraBounded = binding.checkParaBounded(precondition);
			if(paraBounded == -1)
			{
				if((searchAnyInInitialState(openPrecondition,array)))
				{
					return true;
				}
				/** add restrictions here ? */
				System.out.println("/********** RETURNING FALSE -> IN searchSimilarInInitialState() 1st conditional Planner class");
				return false;
			}

			Literal temp = array.get(f);	//CargoAt C1 SFO & CargoAt C2 JFK
			if(	((temp.isNegative() && precondition.isNegative()) || (!temp.isNegative() && !precondition.isNegative()))
					&& (precondition.getLiteralParameters(paraBounded).equals(temp.getLiteralParameters(paraBounded))))
			{
//////////////////////
//				printStepDetails(currentStep);
//////////////////////
//				boolean intends = checkIntentions();
//////////////////////
//				System.out.println("intends " + intends);

				int paraNotBounded = binding.checkParaNotBounded(precondition);
				String groundLetter = precondition.getLiteralParameters(paraNotBounded);
				String newVariable = temp.getLiteralParameters(paraNotBounded);
				
				// TODO: check here
				currentStep.setSelectedLiteral(newVariable);
				
				//to bind the dequeued precondition with the new parameters
				binding.bindPrecondtion(precondition, groundLetter, newVariable);
				binding.bindStep(currentStep, groundLetter, newVariable);

				/*
				 *  TODO: add this for all methods
				 *  added 12/13/18 
				 */
				if (this.detectPotentialThreat(openPrecondition, currentStep)) 
				{
					/* if it is not found in initial - search action effects */
					return this.searchEffectsInActionDomain(openPrecondition);
				}
				
				causalLink = new CausalLink(openPrecondition,parser.getInitialState(),temp);
				causalLink.getPrecondition().getOpenPrecondtion().setExcuted(true);
				Links.add(causalLink);

				//this.addOrdering(currentStep, parser.getInitialState());
				graph.add(currentStep, parser.getInitialState());

//				printStepDetails(currentStep);
//				checkIntentions(currentStep);


					
				return true;
			}
		}
		/** don't think this ever gets hit */
		System.out.println("/********** RETURNING FALSE -> IN searchSimilarInInitialState() bottom of method Planner class");
		return false;
	}


	/**
	 *
	 * @param openPrecondition
	 * @param arry
	 * @return
	 */
	public boolean searchAnyEffect(OpenPrecondition openPrecondition, Map<Integer, Literal> arry)
	{
		//System.out.println("/*****In searchAnyEffect() --> Planner class*****/");

		Literal precondition = openPrecondition.getOpenPrecondtion();
		Step currentStep = Actions.get(openPrecondition.getStepID());

//////////////////////
//	printStepDetails(currentStep);
//////////////////////
//	boolean intends = checkIntentions();
//////////////////////
//	System.out.println("intends " + intends);

		for(int mapkey: arry.keySet())
		{
			Literal literal = arry.get(mapkey);

			if(!(literal.isNegative()))
			{
				if(!(graph.containsOrdering(Actions.get(mapkey), currentStep )))
				{
					int paraNotBounded = binding.checkParaNotBounded(precondition);
					String groundLetter = precondition.getLiteralParameters(paraNotBounded);
					String newVariable = literal.getLiteralParameters(paraNotBounded);

					//to bind the dequeued precondition with the new parameters
					binding.bindPrecondtion(precondition, groundLetter, newVariable);
					binding.bindStep(currentStep, groundLetter, newVariable);
					
					if (this.detectPotentialThreat(openPrecondition, currentStep))
						return false;
					
					causalLink= new CausalLink(openPrecondition,Actions.get(mapkey),literal);

					causalLink.getPrecondition().getOpenPrecondtion().setExcuted(true);
					Links.add(causalLink);
					graph.add(currentStep, Actions.get(mapkey));

					System.out.println(currentStep.toString() + Actions.get(mapkey).toString());


					return true;
				}
			}
		}

		return false;
	}

	/**
	 *
	 * @param openPrecondition
	 * @return
	 */
	public boolean searchSimilarInEffects(OpenPrecondition openPrecondition)
	{
		//System.out.println("/*****In searchSimilarInEffects() --> Planner class*****/");

		Literal precondition= openPrecondition.getOpenPrecondtion();
		Step currentStep = Actions.get(openPrecondition.getStepID());

		Map <Integer, Literal> arry = new HashMap <Integer, Literal>();

		//ArrayList <Literal> arry = new ArrayList <Literal>();
		int sizeActions = Actions.size();
		for(int i=1;i<sizeActions;i++)
		{
			int sizeEffects = Actions.get(i).getEffectsSize();
			for(int y =0; y< sizeEffects;y++)
			{
				//CargoAt == CargoAt
				System.out.println(precondition.getLiteralName().toString());
				System.out.println(Actions.get(i).getEffects(y).getLiteralName());
				if((precondition.getLiteralName().equals(Actions.get(i).getEffects(y).getLiteralName()))
						&&(!(Actions.get(i).getEffects(y).isNegative())))
				{

					arry.put(Actions.get(i).getStepId(), Actions.get(i).getEffects(y));
					//arry.add(index,Actions.get(i).getEffects(y));
					//index++;

				}
			}
		}

		for(int key: arry.keySet())
		{
			int paraBounded = binding.checkParaBounded(precondition);
			if(paraBounded == -1)
			{
				int numOfPara = precondition.sizeLiteralParameters();
				if(numOfPara ==1)
				{
					//call that function
					if((this.searchAnyEffect(openPrecondition, arry)))
					{

	//////////////////////
//						printStepDetails(currentStep);
		//////////////////////
//						boolean intends = checkIntentions();
		//////////////////////
//						System.out.println("intends " + intends);

						return true;
					}
				}
				return false;
			}

			Literal temp = arry.get(key);	//CargoAt C1 SFO & CargoAt C2 JFK
			// issue 14
			if(	(precondition.getLiteralParameters(paraBounded).equals(temp.getLiteralParameters(paraBounded)))
					&&  (!temp.isNegative() && !precondition.isNegative()) || (temp.isNegative() && precondition.isNegative()))
				// old code
//				(!(temp.isNegative())))

			{
				System.out.println(Actions.get(key).toString() + key);
				System.out.println(currentStep.toString());

				//if(!(this.stepsAreConnectedWithCausalLink(currentStep, Actions.get(key))))
				if(!(graph.containsOrdering(Actions.get(key), currentStep )))
				{

					System.out.println("Found in similar effects");
					int paraNotBounded = binding.checkParaNotBounded(precondition);
					String groundLetter = precondition.getLiteralParameters(paraNotBounded);
					String newVariable = temp.getLiteralParameters(paraNotBounded);

					//to bind the dequeued precondition with the new parameters
					binding.bindPrecondtion(precondition, groundLetter, newVariable);
					binding.bindStep(currentStep, groundLetter, newVariable);

					if (this.detectPotentialThreat(openPrecondition, currentStep))
						return false;

					causalLink = new CausalLink(openPrecondition,Actions.get(key),temp);
					causalLink.getPrecondition().getOpenPrecondtion().setExcuted(true);
					Links.add(causalLink);

					graph.add(currentStep, Actions.get(key));
					System.out.println(currentStep.toString() + Actions.get(key).toString());

					return true;
				}
			}
		}

		return false;
	}




	/**
	 * This function updates the variables between the causalLinks
	 * @param causalLink
	 */
	public void updateCausalLinks()
	{
		//System.out.println("/*****In updateCausalLinks() --> Planner class*****/");
		
		Step temp = null;
		for(int i=0;i<Links.size();i++)
		{
			CausalLink causalLink = Links.get(i);
			//to bind variables between a step and an effect
			int oldStepId = causalLink.getPrecondition().getStepID();
			Step oldStep = Actions.get(oldStepId);
			Literal effect = causalLink.getEffect();
			binding.bindStepByPrecondition(oldStep, effect);

			//to bind variables between a precondition and a step
			Step newStep = causalLink.getStepName();
			Literal precondition = causalLink.getPrecondition().getOpenPrecondtion();
			binding.bindStepByPrecondition(newStep, precondition);
						
			temp = newStep;
		}
		
		printStepDetails(temp);

	}







	public boolean bothContainsOrdering(Step s1, Step s2)
	{
		//System.out.println("/*****In bothContainsOrdering() --> Planner class*****/");

		if(	(graph.containsOrdering(s1, s2)) || (graph.containsOrdering(s2, s1))	)
		{
			return true;
		}
		return false;
	}


	/**
	 * Edited by Philip Deppen (issue 12, 4/3/19)
	 * This function searches in the arrayList of threats and then checks if there is no ordering between
	 * the the steps, it adds an ordering constraint between them.
	 * This function applies promotion and demotion
	 * @param threats
	 * @return
	 */
	public boolean hasNoOrdering(ArrayList<CausalLink> threats)
	{
		//System.out.println("/*****In hasNoOrdering() --> Planner class*****/");

		for(int i=0;i<threats.size();i++)
		{
			Literal effect = threats.get(i).getEffect();
			Step s1 = Actions.get(threats.get(i).getPrecondition().getStepID());

			for(int f=0;f<threats.size();f++)
			{
				Step s2 = Actions.get(threats.get(f).getPrecondition().getStepID());
				if(!(s1.equals(s2)))
				{

					if(!(this.bothContainsOrdering(s1, s2)))
					{
						System.out.println(s1.toString() + s2.toString());
						if(	(this.isPreconditionNegateOld(s1, effect)) && (this.isPreconditionNegateOld(s2, effect))	)
						{
							Literal precondition = threats.get(i).getPrecondition().getOpenPrecondtion();
							Literal newEffect = this.getNewEffect(s1, effect);
							Links.remove(threats.get(i));	//remove the link

							//System.out.println(precondition.toString());
							//System.out.println(newEffect.toString());
							//System.out.println(effect.toString());

							if((newEffect.getLiteralName().equals(effect.getLiteralName())))
							{
								System.out.println("no ordering");
								binding.bindStepByChangingLetters(s2, newEffect, precondition);
								
							}
						}
						else
						{
//							System.out.println("/********** in hasNoOrdering(); -> Planner class **********/");

							if(this.isPreconditionNegateOld(s1,effect))
							{
								System.out.println("s1: " + s1.toString());
								System.out.println("s2: " + s2.toString());

								//either way it will be the same precondition
								Literal precondition = threats.get(i).getPrecondition().getOpenPrecondtion();
								//Literal newEffect = this.getNewEffect(s1, effect);
								Literal newEffect = this.getNewEffect(s1, effect);

								Links.remove(threats.get(i));	//remove the link

								System.out.println(precondition.toString());
								System.out.println(newEffect.toString());
								System.out.println(effect.toString());

								if((newEffect.getLiteralName().equals(effect.getLiteralName())))
								{
									System.out.println("swapping step order");
//									binding.bindStepByChangingLetters(s2, newEffect, precondition);
									binding.bindStepByChangingLetters(s2, precondition, newEffect);									
								}

								graph.add(s1, s2);    //this was changed to from s1 to s2
//								graph.add(s2, s1);
//								graph.updateOrdering(graph, s2, s1);
								return true;

							}
							else if(this.isPreconditionNegateOld(s2, effect))
							{
								Literal precondition = threats.get(i).getPrecondition().getOpenPrecondtion();
								Literal newEffect = this.getNewEffect(s2, effect);

								Links.remove(threats.get(i));	//remove the link

								if((newEffect.getLiteralName().equals(effect.getLiteralName())))
								{
									binding.bindStepByChangingLetters(s1, newEffect, precondition);
								}

//								graph.add(s1, s2);
								graph.updateOrdering(graph, s1, s2);

								return true;
							}
						}
					}
				}
			}
		}

		return false;
	}




	/**
	 * THis function returns the new effect after it has been negated
	 * @param s
	 * @param effect
	 * @return the new effect
	 */
	public Literal getNewEffect(Step s, Literal effect)
	{
		//System.out.println("/*****In getNewEffect() --> Planner class*****/");


		int sizeEffect = s.getEffectsSize();

		for(int i=0; i<sizeEffect;i++)
		{
			if(	(effect.equals(s.getEffects(i)))  || (!(s.getEffects(i).isNegative()))	)
			{
				return s.getEffects(i);
			}
		}


		return effect;
	}

	/**
	 *
	 * @param threats
	 * @return
	 */
	public boolean hasOrdering(ArrayList<CausalLink> threats)
	{
		//System.out.println("/*****In hasOrdering() --> Planner class*****/");

		OpenPrecondition TempOpenPrecon = new OpenPrecondition(-1,null);


		for(int i=0;i<threats.size();i++)
		{

			Literal effect = threats.get(i).getEffect();
			Step s1 = Actions.get(threats.get(i).getPrecondition().getStepID());

			for(int f=0;f<threats.size();f++)
			{
				Step s2 = Actions.get(threats.get(f).getPrecondition().getStepID());
				if(!(s1.equals(s2)))
				{
					if((graph.containsOrdering(s1,s2)))
					{
						TempOpenPrecon.addOpenPrcondition(threats.get(i).getPrecondition().getOpenPrecondtion());
						TempOpenPrecon.addStep(s1.getStepId());
						System.out.println(TempOpenPrecon.getStepID() + TempOpenPrecon.getOpenPrecondtion().toString());

						openPrecon.add(TempOpenPrecon);		//add the new open precondition
						Links.remove(threats.get(i));		//remove the link
						effect.hasNegativeSign(true);

						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * This function checks if there is a threat between variables
	 */
	public boolean CheckThreats()
	{
		ArrayList <CausalLink> threats = new ArrayList <CausalLink>();
		
		threats = this.getThreatenCausalLinks();

		//When there is no threats
		if(threats.isEmpty()) {
			return true;
		}
		
		//if there is no ordering between the threatened steps then we order the step
		if(this.hasNoOrdering(threats))
		{
			System.out.println(threats.toString());
			System.out.println("The threat has been resolved by reording the steps" );
//			System.out.println("/**********In first conditional in CheckThreats() -> Planner**********"); 
			return true;
		}

		//if there is an ordering between the threatened steps we add a new step
		if(this.hasOrdering(threats))
		{
			System.out.println("The threat has been resolved by adding a new step");
//			System.out.println("/**********In second conditional in CheckThreats() -> Planner**********"); 
			return true;
		}

		else
		{
			System.out.println("The threat can not be resolved");
//			System.out.println("/**********In third conditional in CheckThreats() -> Planner**********"); 
			return false;

		}
	}


	/**
	 * Original getThreatenCausalLink() that was modified by Girard 12/11/18
	 * This function searches for causalLinks that are threatened by other causalLinks
	 * @return an arrayList of threatened CausalLinks
	 */
	// TODO: modify
//	public ArrayList<CausalLink> getThreatenCausalLinks()
//	{
//		//System.out.println("/*****In getThreatenCausalLinks() --> Planner class*****/");
//
//		ArrayList <CausalLink> threats = new ArrayList <CausalLink>();
//
//		int sizeLinks = Links.size();
//		for(int i =0;i<sizeLinks;i++)
//		{
//			CausalLink link1 = Links.get(i);
//			Literal thisEffect = link1.getEffect();
//			for(int x=0; x<sizeLinks;x++)
//			{
//				Literal effect = Links.get(x).getEffect();
//				
//				// 12/11/18 - following 3 lines edited by Girard
//				//CausalLink link2 = Links.get(x);
//				//Literal effect = link2.getEffect();
//				if(thisEffect.toString().equals(effect.toString()) && (!(Links.get(i).equals(Links.get(x)))))
//				{
//					//System.out.println(Links.get(i));
//					//System.out.println(Links.get(x));
//					if(Links.get(i).getStepName() == Links.get(x).getStepName())
//					{
//						int stepid = Links.get(i).getPrecondition().getStepID();
//						Step s = Actions.get(stepid);
//						if((isPreconditionNegateOld(s,thisEffect)))
//						{
//							//System.out.println(Links.get(i));
//							//System.out.println(Links.get(x));
//
////							System.out.println("/**********In getThreatenCausalLinks -> Planner class**********");
//							threats.add(Links.get(i));
//							threats.add(Links.get(x));
//						}
//					}
//
//				}
//			}
//
//		}
//
//		return threats;
//	}
	
	/**
	 * This is the original getThreatenCausalLink()
	 * This function searches for causalLinks that are threatened by other causalLinks
	 * @return an arrayList of threatened CausalLinks
	 */
	public ArrayList<CausalLink> getThreatenCausalLinks()
	{
		ArrayList <CausalLink> threats = new ArrayList <CausalLink>();

		int sizeLinks = Links.size();
		for(int i =0;i<sizeLinks;i++)
		{
			Literal thisEffect = Links.get(i).getEffect();
			for(int x=0; x<sizeLinks;x++)
			{
				Literal effect = Links.get(x).getEffect();
				if((thisEffect.toString().equals(effect.toString()) && (Links.get(i) != Links.get(x))))
				{
					if(Links.get(i).getStepName() == Links.get(x).getStepName())
					{
						int stepid = Links.get(i).getPrecondition().getStepID();
						Step s = Actions.get(stepid);
						if((isPreconditionNegateOld(s,thisEffect)))
						{
							threats.add(Links.get(i));
							threats.add(Links.get(x));
						}
					}

				}
			}

		}

		return threats;
	}
	
	/**
	 * Created 12/12/18 - Girard
	 * This function searches for causalLinks that are threatened by other causalLinks.
	 * This should only be used if the Causal Link connects a Literal from the initial
	 * state to the goal state.
	 * @return an arrayList of threatened CausalLinks
	 */
	public ArrayList<CausalLink> getThreatenCausalLinks(OpenPrecondition condition)
	{
		/* threatened links are saved here */
		ArrayList <CausalLink> threats = new ArrayList <CausalLink>();
		
		/* size of links array */
		int sizeLinks = Links.size();
		
		/* loop for each link */
		for(int i =0;i<sizeLinks;i++)
		{
			/* get a link */
			CausalLink link1 = Links.get(i);
			/* get effect of link */
			Literal thisEffect = link1.getEffect();
			/* get current open precondition */
			Literal threat = condition.getOpenPrecondtion();
			
			/* checks if either currentPrecondition or link effect are not and .toString's are the same */
			if((isPreconditionNegate(threat,thisEffect)))
			{
				System.out.println(link1);
				/* add the lnk to threats */
				threats.add(link1);
			}
			
		}

		return threats;
	}

	
	/**
	 * Created 12/12/18 - Girard
	 * This function searches for causalLinks that are threatened by other causalLinks
	 * This should only be used if the new causal link connects to one or more action/steps.
	 * 
	 * Pass in the Step that the causal link connects to.  If the Causal Link connects 
	 * two action/states then should check each state individually.
	 * @return an arrayList of threatened CausalLinks
	 */
	public ArrayList<CausalLink> getThreatenCausalLinks(Step s)
	{
		/* threatened links are saved here */
		ArrayList <CausalLink> threats = new ArrayList <CausalLink>();
		
		/* size of links array */
		int sizeLinks = Links.size();
		
		/* loop for each link */
		for(int i =0;i<sizeLinks;i++)
		{
			/* get a link */
			CausalLink link1 = Links.get(i);
			/* get effect of link */
			Literal thisEffect = link1.getEffect();
			/* get current open precondition */
			
			/* if step != null */
			if (s != link1.getStepName())
			{
				// Need to have all Literals updated for that Step
				
				
				/* loop for each effect in current step */
				for (int x=0;x<s.getEffectsSize();x++)
				{
					/* threat = current step effect */
					Literal threat = s.getEffects(x);
					if((isPreconditionNegate(threat,thisEffect)))
					{
						System.out.println(link1);
						threats.add(link1);
					}
					
				}
				
				/* loop for each precondition in current step */
				for (int x=0;x<s.getPreconditionSize();x++)
				{
					/* threat = current step precondition */
					Literal threat = s.getPreconditions(x);
					if((isPreconditionNegate(threat,thisEffect)))
					{
						System.out.println(link1);
						threats.add(link1);
					}
				}
			}
			
		}

		return threats;
	}


	/**
	 * Created 12/11/18 - Girard
	 * This function checks if the step negates a satisfied precondition
	 * @param s the step of the precondition
	 * @param effect the effect that connects the openprecondition
	 * @return
	 */
	public boolean isPreconditionNegate(Literal threat, Literal effect)
	{
        if(threat.toString().equals(effect.toString()))
		{
			if(threat.isNegative() == !effect.isNegative())
			{
					//System.out.println("threat: " + threat.toString() + " is negative: " + threat.isNegative());
					//System.out.println("effect: " + effect.toString() + " is negative: " + effect.isNegative());
					return true;
			}
		}
		return false;
	}

	/**
	 * 12/11/18 - Method from old planner. Change return to true for briefcase plan to work
	 * Edited by Philip Deppen (issue 15, 4/3/19)
	 * This function checks if the step negates a satisfied precondition
	 * @param s the step of the precondition
	 * @param effect the effect that connects the openprecondition
	 * @return
	 */
	public boolean isPreconditionNegateOld(Step s, Literal effect)
	{
		//System.out.println("/*****In isPreconditionNegate() --> Planner class*****/");

		int sizeEffect = s.getEffectsSize();
		for(int i=0;i<sizeEffect;i++)
		{
			if(s.getEffects(i).toString().equals(effect.toString()))
			{
				// call new isPreconditionNegate
				if(this.isPreconditionNegate(effect, s.getEffects(i)))
				{
//					System.out.println("Step: " + s.getStepName());
//					System.out.println("Step effect: " + s.getEffects(i).toString());
//					System.out.println("Step effect: is negative: " + s.getEffects(i).isNegative());
//					System.out.println("Effect: " + effect.toString());
//					System.out.println("Effect is negative: " + effect.isNegative());
//					JOptionPane.showMessageDialog(null, "here");
					return true;
				}
			}
		}
		return false;
	}



	//
	//	/**
	//	 * This function checks how many times the effect is connected with a causal Link
	//	 * @param link
	//	 * @return
	//	 */
	//	public int hasCausalLink(CausalLink link)
	//	{
	//		Literal effect = link.getEffect();
	//		int sizeCausalLinks = Links.size();
	//		int counter =0;
	//		for(int i=0;i<sizeCausalLinks;i++)
	//		{
	//			if(Links.get(i).getEffect().equals(effect))
	//			{
	//				counter++;
	//			}
	//		}
	//		return counter;
	//	}


	public CausalLink getCausalLink(int index)
	{
		//System.out.println("/*****In getCausalLink() --> Planner class*****/");

		return Links.get(index);
	}

	//	/**
	//	 * This function applies the negation after the step has been chosen
	//	 * @param step
	//	 */
	//	public void applyNegation()
	//	{
	//		int sizeActions = Actions.size();
	//		for(int i=0;i<sizeActions;i++)
	//		{
	//			int sizeEffects = Actions.get(i).getEffectsSize();
	//			for(int f=0;f<sizeEffects;f++)
	//			{
	//				if((Actions.get(i).getEffects(f).isNegative()))
	//				{
	//					Literal negativeEffect = Actions.get(i).getEffects(f);
	//
	//					for(int j=0; j< sizeActions;j++)
	//					{
	//						int numOfEffects = Actions.get(j).getEffectsSize();
	//						for(int k=0;k<numOfEffects; k++)
	//						{
	//							if(Actions.get(j).getEffects(k).toString().equals(negativeEffect.toString()))
	//							{
	//								Actions.get(j).getEffects(k).hasNegativeSign(true);
	//							}
	//						}
	//					}
	//
	//					int sizeEffectsInIntitial = parser.getProblemDomainEffectsSize();
	//					for(int y=0;y<sizeEffectsInIntitial;y++)
	//					{
	//						if(parser.getIntialStateEffects(y).toString().equals(negativeEffect.toString()))
	//						{
	//							parser.getIntialStateEffects(y).hasNegativeSign(true);
	//						}
	//					}
	//
	//				}
	//			}
	//		}
	//	}



	public OpenPrecondition getOpenPrecondition()
	{
		//System.out.println("/*****In getOpenPrecondition() --> Planner class*****/");

		OpenPrecondition openprecondition = openPrecon.getFirst();
		this.removeOpenPrecondition();
		return openprecondition;
	}

	public OpenPrecondition removeOpenPrecondition()
	{
		//System.out.println("/*****In removeOpenPrecondition() --> Planner class*****/");

		return openPrecon.removeFirst();
	}

	public void printStepDetails(Step newStep)
	{
		//System.out.println("/*****In printStepDetails() --> Planner class*****/");

		System.out.println("Step Name: " + newStep.toString());
		for (int a = 0; a < newStep.getAgentsSize(); a++)
		{
			System.out.println("Agent: " + newStep.getAgent(a));
			System.out.print("Preconditions:");
			for (int temp = 0; temp < newStep.getPreconditionSize(); temp++)
			{
				System.out.print(" (" + newStep.getPreconditions(temp) + ")");
			}
			System.out.println("");
			System.out.print("Effects:");
			for (int temp = 0; temp < newStep.getEffectsSize(); temp++)
			{
				System.out.print(" (" + newStep.getEffects(temp) + ")");
			}
			System.out.println("");
		}
	}


	public boolean checkIntentions(Step newStep)
	{
		//System.out.println("/*****In checkIntentions() --> Planner class*****/");

		boolean broken;
		int numAgents = newStep.agents.size();
		System.out.println("Number of Agents: " + numAgents);
		if (numAgents != 0)
		{
			for (int a = 0; a < intent.size(); a++)
			{
				if (intent.get(a).getLiteralParameters(0).equals(newStep.agents.get(0)))
				{
					System.out.println("intender: " + intent.get(a).getLiteralParameters(0) + " Agent: " + newStep.agents.get(0));
					System.out.println("agent has intention frame");
					System.out.println("Intention: " + intent.get(a).getLiteralParameters(1) + " " + intent.get(a).getLiteralParameters(2));
					for (int b = 0; b < newStep.getEffectsSize(); b++)
					{
						if (newStep.getEffects(b).isNegative())
						{
							System.out.println("effect: " + newStep.getEffects(b) + " is negative");
							if (intent.get(a).getLiteralParameters(1).equals(newStep.getEffects(b).getLiteralParameters(0)))
							{
								if (intent.get(a).getLiteralParameters(2).equals(newStep.getEffects(b).getLiteralParameters(1)))
								{
									System.out.println("breaks intention");
									return false;
								}
							}
						}
						else
						{
							if (intent.get(a).getLiteralParameters(1).equals(newStep.getEffects(b).getLiteralParameters(0)))
							{
								if (intent.get(a).getLiteralParameters(2).equals(newStep.getEffects(b).getLiteralParameters(1)))
								{
									System.out.println("direct intention");
									return true;
								}
							}
						}
					}
				}
			}
		}
		System.out.println("does not break intention");
		return true;
	}
}