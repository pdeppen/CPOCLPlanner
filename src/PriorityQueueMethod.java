import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

public class PriorityQueueMethod extends Planner
{
    FileWriter fileWriter;
    PrintWriter printWriter = null;
        
	int tempCounter = 0;
	OpenPrecondition precondition;
    
	public PriorityQueueMethod(ConflictParser p, long startTime) throws FileNotFoundException
	{
		super(p, startTime);

		try {
			fileWriter = new FileWriter("textFiles/orderOfOpenPreconditions10.txt");
		    printWriter = new PrintWriter(fileWriter);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.key=1;
		Actions = new LinkedList <Step>();
		Links = new ArrayList <CausalLink>();
		openPrecon = new LinkedList <OpenPrecondition>();



		Step goalState = p.getGoalState();
		goalState.setStepId(0);
		//ordering.addStep(goalState);
		//order.put(0, ordering);
				
		Actions.add(0, parser.getGoalState());
		//openPrecondition= new OpenPrecondition(0,null);
		this.addGoalOpenPrecondition();

	}



	/**
	 * This function is to add preconditions to the openPreconditions queue
	 * @param step
	 */
	public void addPreconditions(Step step)
	{
		//increment the step Id by one
		int stepId = step.getStepId();

		LinkedList <OpenPrecondition>  temp = new LinkedList <OpenPrecondition>();

		int preconditionsNum = step.getPreconditionSize();


		for(int f=0;f<preconditionsNum;f++)
		{
			//openPrecondition.addStep(StepId);
			OpenPrecondition object = new OpenPrecondition(stepId,null);

			object.addOpenPrcondition(step.getPreconditions(f));
			object.addStep(stepId);
			temp.addFirst(object);
		}

		for(int i=0; i<preconditionsNum;i++)
		{

			OpenPrecondition object = new OpenPrecondition(stepId,null);
			object = temp.getFirst();
			openPrecon.addFirst(object);
			temp.removeFirst();
		}



		temp.clear();
	}


	public boolean search() throws FileNotFoundException
	{
		while(!(openPrecon.isEmpty()))
		{
			currentTime = System.nanoTime();
			duration = (currentTime - startTime) / 100000000;
			if (duration > 5)	// more than half a second
			{
				System.out.println("*****\n*****\n*****\n*****\n*****\n");
				return false;
			}
			
			if(!(this.resolveOpenPrecondition()))
			{
				if (!this.restrictionOpenPrecons.isEmpty())
				{
					this.Links = new ArrayList<CausalLink>(this.RestrictionLinks);
			        this.openPrecon = (LinkedList)this.restrictionOpenPrecons.clone(); 
					this.restrictionPrecondition = this.getOpenPrecondition();
					System.out.println("Old Graph: " + graph.neighbors.toString());
					
//					this.graph = this.restrictionGraph;
//					this.graph = new Ordering<Step>();
//					this.graph = this.restrictionGraph;
//					this.graph.neighbors.putAll(restrictionGraph.neighbors);
//					this.graph.neighbors = this.restrictionGraph.neighbors;
		        		System.out.println("Restriction Graph: " + graph.restrictionNeighbors.toString());
		        		graph.resetRestrictions();
					System.out.println("New Graph: " + graph.neighbors.toString());

					System.out.println("Possible Restriction");
					System.out.println("Making precondition: " + this.restrictionPrecondition.getOpenPrecondtion() + " negative");
					this.restrictionPrecondition.getOpenPrecondtion().hasNegativeSign(true);
					if (!(this.resolveWithRestriction()))
							return false;
				}
				else {
					System.out.println("No Plan Found -> !(this.resolvedOpenPrecondition()");
				//				break;
					return false;
			
				}
			}
			
			this.updateCausalLinks();
			System.out.println("\n\n");

			if(!(this.CheckThreats()))
			{
				if (!this.restrictionOpenPrecons.isEmpty())
				{
					this.Links = new ArrayList<CausalLink>(this.RestrictionLinks);
			        this.openPrecon = (LinkedList)this.restrictionOpenPrecons.clone(); 
					this.restrictionPrecondition = this.getOpenPrecondition();
					this.graph = new Ordering<Step>();
					this.graph = this.restrictionGraph;
					
					System.out.println("Possible Restriction");
					System.out.println("Making precondition: " + this.restrictionPrecondition.getOpenPrecondtion() + " negative");
					this.restrictionPrecondition.getOpenPrecondtion().hasNegativeSign(true);
					if (!(this.resolveWithRestriction()))
							return false;

				}
				else {
					System.out.println("No Plan Found -> !(this.resolvedOpenPrecondition()");
				//				break;
					return false;			
				}

			}
			this.possibleRestriction = false;

			
		}

		//to print out the solution if it exists
		if(openPrecon.isEmpty())
		{
			System.out.println("The Following plan has been found:");
			//			this.notused();
//			this.printOutSolution();
			return true;
		}
		return true;
	}
	
	public boolean resolveWithRestriction()
	{
//		for (int i = 0; i < this.openPrecon.size(); i++)
//			System.out.println("Open Preconditions left: " + this.openPrecon.get(i).getOpenPreconditionToString() + " action: "  + Actions.get(this.openPrecon.get(i).getStepID()).getStepName());
//	
//		/* added 12/12/18 - prints causal links created so far */
//		System.out.println("\nLinks Created so far: ");
//		for (int i = 0; i < this.Links.size(); i++)
//			System.out.println(this.Links.get(i));
		
		System.out.println("The openPrecondition:	"+ this.restrictionPrecondition.getOpenPrecondtion());
		System.out.println("Action is "+ Actions.get(restrictionPrecondition.getStepID()).getStepName()+
				"	ActionID is "+restrictionPrecondition.getStepID());
					
		
		System.out.println("Action is negative: " + restrictionPrecondition.getOpenPrecondtion().isNegative());
							
		//search for an effect in the initial state to satisfy it (if there is)
		if(binding.isBounded(restrictionPrecondition.getOpenPrecondtion()))
		{
			boolean isFoundInIntialState = this.searchEffectInInitialState(restrictionPrecondition); // true
			System.out.println("In Initial State?	"+isFoundInIntialState);
			
			//boolean isFoundInActionDomain = this.searchEffectsInActionDomain(precondition);

			if(!(isFoundInIntialState)) // || isFoundInActionDomain))
			{
				if(!( this.searchInEffects(restrictionPrecondition)))
				{
					boolean isFoundinActionDomain = this.searchEffectsInActionDomain(restrictionPrecondition);
					if(!(isFoundinActionDomain))
					{
						return false;
					}

				}
			}
		}

		else
		{
			boolean isFoundSimilarInInitialStat = this.searchSimilarInInitialState(restrictionPrecondition);
			System.out.println("Similar In Initial State?	"+isFoundSimilarInInitialStat);
			
			if((isFoundSimilarInInitialStat))
			{
				System.out.println(restrictionPrecondition.getOpenPrecondtion());
				Step currentStep = Actions.get(restrictionPrecondition.getStepID());
				printStepDetails(currentStep);
				System.out.println("/********** RETURNING TRUE -> IN resolveOpenPreconditions() 1st condition in the else PQM class");

				return true;


			}

			else
			{
				/** never gets reached */
				if((this.searchSimilarInEffects(restrictionPrecondition)))
				{
//					Step currentStep = Actions.get(precondition.getStepID());
//					printStepDetails(currentStep);
					System.out.println("/********** RETURNING TRUE -> IN resolveOpenPreconditions() 2nd condition in the else PQM class");

					return true;
				}
				else
				{
					//search for an action in the action domain to satisfy the open precondition
					//add the action to the plan
					boolean isFoundinActionDomain =this.searchEffectsInActionDomain(restrictionPrecondition);
					/** here for last check before plan finally fails */
					if(!(isFoundinActionDomain))
					{
						System.out.println("No Plan found -> resolveOpenPreconditions() in PQM class");
						return false;
					}
					System.out.println("/*********** isFoundInActionDomain = true");
					System.out.println("Action is ");
				}
			}
		}

		return true;
	}

	/** conditions that must be met for an action to take place */
	public boolean resolveOpenPrecondition() throws FileNotFoundException
	{
//		/* added 12/10/18 - prints remaining preconditions */
//		for (int i = 0; i < this.openPrecon.size(); i++)
//			System.out.println("Open Preconditions left: " + this.openPrecon.get(i).getOpenPreconditionToString() + " action: "  + Actions.get(this.openPrecon.get(i).getStepID()).getStepName());
//		
//		/* added 12/12/18 - prints causal links created so far */
//		System.out.println("\nLinks Created so far: ");
//		for (int i = 0; i < this.Links.size(); i++)
//			System.out.println(this.Links.get(i));
			
		/* print blank line */
		System.out.println("");
		LinkedList <OpenPrecondition> temp = (LinkedList)this.openPrecon.clone();
//		graph.createTemp();

//		OpenPrecondition precondition;

		//get the first open precondition in the queue
		
		/**
		 * This is where preconditions differ each time 
		 */
//		if (this.restrictionPath)
//			precondition = this.restrictionPrecondition;
		
		precondition = this.getOpenPrecondition();
		
		System.out.println("The openPrecondition:	"+ precondition.getOpenPrecondtion());
		System.out.println("Action is "+ Actions.get(precondition.getStepID()).getStepName()+
				"	ActionID is "+precondition.getStepID());
					
		
		System.out.println("Action is negative: " + precondition.getOpenPrecondtion().isNegative());
								

		//search for an effect in the initial state to satisfy it (if there is)
		if(binding.isBounded(precondition.getOpenPrecondtion()))
		{
			boolean isFoundInIntialState = this.searchEffectInInitialState(precondition); // true
			System.out.println("In Initial State?	"+isFoundInIntialState);
			
			//boolean isFoundInActionDomain = this.searchEffectsInActionDomain(precondition);

			if(!(isFoundInIntialState)) // || isFoundInActionDomain))
			{
				if(!( this.searchInEffects(precondition)))
				{
					boolean isFoundinActionDomain = this.searchEffectsInActionDomain(precondition);
					if(!(isFoundinActionDomain))
					{
						return false;
					}

				}
			}
		}

		else
		{
			boolean isFoundSimilarInInitialStat = this.searchSimilarInInitialState(precondition);
			System.out.println("Similar In Initial State?	"+isFoundSimilarInInitialStat);
			
			if((isFoundSimilarInInitialStat))
			{
				System.out.println(precondition.getOpenPrecondtion());
				Step currentStep = Actions.get(precondition.getStepID());
				printStepDetails(currentStep);
				System.out.println("/********** RETURNING TRUE -> IN resolveOpenPreconditions() 1st condition in the else PQM class");

				return true;


			}

			else
			{
				/** never gets reached */
				if((this.searchSimilarInEffects(precondition)))
				{
//					Step currentStep = Actions.get(precondition.getStepID());
//					printStepDetails(currentStep);
					System.out.println("/********** RETURNING TRUE -> IN resolveOpenPreconditions() 2nd condition in the else PQM class");

					return true;
				}
				else
				{
					//search for an action in the action domain to satisfy the open precondition
					//add the action to the plan
					boolean isFoundinActionDomain =this.searchEffectsInActionDomain(precondition);
					/** here for last check before plan finally fails */
					if(!(isFoundinActionDomain))
					{
						System.out.println("No Plan found -> resolveOpenPreconditions() in PQM class");
						return false;
					}
					System.out.println("/*********** isFoundInActionDomain = true");
					System.out.println("Action is ");
				}
			}
		}
		
		if (this.possibleRestriction)
		{	
			this.RestrictionLinks = new ArrayList<CausalLink>(this.Links);
//			this.restrictionOpenPrecons = new LinkedList<OpenPrecondition>(this.openPrecon);
	        this.restrictionOpenPrecons = (LinkedList)temp.clone(); 
	        graph.copyRestrictions();
//        		System.out.println("Temp Graph: " + tempGraph.neighbors.toString());
//	        	this.restrictionGraph = tempGraph;
//        		restrictionGraph = tempGraph;
	        	System.out.println("Restriction Graph: " + graph.restrictionNeighbors.toString());
		}
		this.possibleRestriction = false;
		
		return true;

	}



	/**
	 * This function prints out the solution set
	 */
	public void printOutSolution()
	{
		for(CausalLink pre :Links)
		{
			System.out.println(pre.getPrecondition().getOpenPrecondtion().isNegative()+ "--->"+pre.toString());
		}
		System.out.println(Links.size());

		//to print out the preconditions in every action
		for(int i =0; i< Actions.size();i++)
		{
			int sizeprecon = Actions.get(i).getPreconditionSize();
			for(int f=0; f< sizeprecon;f++)
			{
				System.out.println(Actions.get(i).getPreconditions(f)+"  ");
			}
			System.out.println("\n");
		}

		int size = graph.topSort().size();
		for(int i= size -1;i>=0;i--)
		{
			System.out.println(graph.topSort().get(i));
		}
		System.out.println("\n");
	}



	public void notused()
	{


		for(CausalLink pre :Links)
		{
			System.out.println(pre.getPrecondition().getOpenPrecondtion().isNegative()+ "--->"+pre.toString());
		}
		System.out.println(Links.size());



		//to print out the preconditions in every action
		for(int i =0; i< Actions.size();i++)
		{
			int sizeprecon = Actions.get(i).getPreconditionSize();
			for(int f=0; f< sizeprecon;f++)
			{
				System.out.println(Actions.get(i).getPreconditions(f)+"  ");
			}
			System.out.println("\n");
		}
	}
}
