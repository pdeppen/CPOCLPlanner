import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author khaledalkathiri
 */

/**
 * @author Chris Lashley
 * added intention handling, ability to break out of loop using time parameter, and step details
 */

public class Planner
{

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
	 * This method is to check the initial state if it satisfies the openPrecondition
	 * Works only with the bounded precondition
	 * @param precondition
	 * @return true if the precondition is found
	 * @return false if the precondition is not found
	 */
	public boolean searchEffectInInitialState(OpenPrecondition openPrecondition)
	{

		Literal precondition = openPrecondition.getOpenPrecondtion();
		Step currentStep = Actions.get(openPrecondition.getStepID());
		int effSize = parser.getProblemDomainEffectsSize();
		for(int i=0; i< effSize;i++)
		{
			String temp = precondition.toString();
			if(temp.equals(parser.getIntialStateEffects(i).toString()))
			{

				if(!(parser.getIntialStateEffects(i).isNegative()))
				{
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
	 * This function searches for an effect in ActionDomain to solve an open precondition
	 * @param precondition
	 */
	public boolean searchEffectsInActionDomain(OpenPrecondition openPrecondition)
	{
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
					//The literal is not negative
					if(!(parser.getActionsEffects(i, f).isNegative()))
					{
						//System.out.println(parser.getActionsEffects(i, f).getLiteralParameters(0));
						//To get the step
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


						Actions.addLast(newStep);
						//this.addOrdering(currentStep, newStep);
						graph.add(currentStep, newStep);

						//this.applyNegation(boundedStep);


						//add causal Link
						causalLink = new CausalLink(openPrecondition,newStep,newStep.getEffects(f));
						causalLink.getPrecondition().getOpenPrecondtion().setExcuted(true);
						Links.add(causalLink);

						return true;

					}
				}
			}
		}
		return false;
	}

	/**
	 * This method checks if there is any causal links between these two steps
	 * @param current
	 * @param next
	 * @return true if there is a connection
	 * @return false if there is no connection
	 */
	public boolean stepsAreConnectedWithCausalLink(Step current, Step next)
	{
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
					if(	(!(effect.isNegative()))	&&(effect.toString().equals(precondition.toString())))
					{

						if(!(graph.containsOrdering(Actions.get(i), currentStep )))
						{
							System.out.println("Found in Effects");
							//this.addOrdering(currentStep, Actions.get(i));


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
	 *
	 */
	public boolean searchAnyInInitialState(OpenPrecondition openPrecondition, ArrayList<Literal> array)
	{
		Literal precondition = openPrecondition.getOpenPrecondtion();
		Step currentStep = Actions.get(openPrecondition.getStepID());

//		Collections.shuffle(array);

		for (int a = 0; a < array.size(); a++)
		{
			Literal literal = array.get(a);

			if(!(array.get(a).isNegative()))
			{
				int paraNotBounded = binding.checkParaNotBounded(precondition);
				String groundLetter = precondition.getLiteralParameters(paraNotBounded);
				String newVariable = array.get(0).getLiteralParameters(paraNotBounded);
				System.out.println(newVariable);

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
	 * @param precondition
	 * @return
	 */
	public boolean searchSimilarInInitialState(OpenPrecondition openPrecondition)
	{
		Literal precondition = openPrecondition.getOpenPrecondtion();
		Step currentStep = Actions.get(openPrecondition.getStepID());


		ArrayList <Literal> array = new ArrayList <Literal>();
		int sizeEffectInitailState = parser.getProblemDomainEffectsSize();

		int index=0;
		int y;
		for( y =0; y< sizeEffectInitailState;y++)		//The size of effect in initial state
		{

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
				return false;
			}

			Literal temp = array.get(f);	//CargoAt C1 SFO & CargoAt C2 JFK
			if(	(precondition.getLiteralParameters(paraBounded).equals(temp.getLiteralParameters(paraBounded)))
					&& (!(temp.isNegative())))
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

				//to bind the dequeued precondition with the new parameters
				binding.bindPrecondtion(precondition, groundLetter, newVariable);
				binding.bindStep(currentStep, groundLetter, newVariable);


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
			if(	(precondition.getLiteralParameters(paraBounded).equals(temp.getLiteralParameters(paraBounded)))
					&& (!(temp.isNegative())))
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
		if(	(graph.containsOrdering(s1, s2)) || (graph.containsOrdering(s2, s1))	)
		{
			return true;
		}
		return false;
	}


	/**
	 * This function searches in the arrayList of threats and then checks if there is no ordering between
	 * the the steps, it adds an ordering constraint between them.
	 * This function applies promotion and demotion
	 * @param threats
	 * @return
	 */
	public boolean hasNoOrdering(ArrayList<CausalLink> threats)
	{
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
						if(	(this.isPreconditionNegate(s1, effect)) && (this.isPreconditionNegate(s2, effect))	)
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
							if(this.isPreconditionNegate(s1,effect))
							{


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
									binding.bindStepByChangingLetters(s2, newEffect, precondition);
								}

								//graph.add(s1, s2);    //this was changed to from s1 to s2
								//graph.add(s2, s1);

								graph.updateOrdering(graph, s2, s1);
								return true;

							}
							else if(this.isPreconditionNegate(s2, effect))
							{
								Literal precondition = threats.get(i).getPrecondition().getOpenPrecondtion();
								Literal newEffect = this.getNewEffect(s2, effect);

								Links.remove(threats.get(i));	//remove the link

								if((newEffect.getLiteralName().equals(effect.getLiteralName())))
								{
									binding.bindStepByChangingLetters(s1, newEffect, precondition);
								}

								//graph.add(s1, s2);
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
		if(threats.isEmpty())
			return true;

		//if there is no ordering between the threatened steps then we order the step
		if(this.hasNoOrdering(threats))
		{
			System.out.println(threats.toString());
			System.out.println("The threat has been resolved by reording the steps" );
			return true;
		}


		//if there is an ordering between the threatened steps we add a new step
		if(this.hasOrdering(threats))
		{
			System.out.println("The threat has been resolved by adding a new step");
			return true;
		}

		else
		{
			System.out.println("The threat can not be resolved");
			return false;

		}
	}


	/**
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
						if((isPreconditionNegate(s,thisEffect)))
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
	 * This function checks if the step negates a satisfied precondition
	 * @param s the step of the precondition
	 * @param effect the effect that connects the openprecondition
	 * @return
	 */
	public boolean isPreconditionNegate(Step s, Literal effect)
	{
		int sizeEffect = s.getEffectsSize();
		for(int i=0;i<sizeEffect;i++)
		{
			if(s.getEffects(i).toString().equals(effect.toString()))
			{
				if(s.getEffects(i).isNegative())
				{
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
		OpenPrecondition openprecondition = openPrecon.getFirst();
		this.removeOpenPrecondition();
		return openprecondition;
	}

	public OpenPrecondition removeOpenPrecondition()
	{

		return openPrecon.removeFirst();
	}

	public void printStepDetails(Step newStep)
	{
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