import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JOptionPane;

public class PriorityQueueMethod extends Planner
{
    FileWriter fileWriter;
    PrintWriter printWriter = null;
        
	int tempCounter = 0;

    
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
				System.out.println("No Plan Found -> !(this.resolvedOpenPrecondition()");
				//				break;
				return false;
			}
			
			this.updateCausalLinks();
			System.out.println("\n\n");
									
//			if(!(this.CheckThreats()))
//			{
//				System.out.println("No Plan Found -> !(this.CheckThreats()");
//				//				break;
//				return false;
//			}
			
		}

		//to print out the solution if it exists
		if(openPrecon.isEmpty())
		{
			System.out.println("The Following plan has been found:");
			//			this.notused();
			this.printOutSolution();
			return true;
		}
		return true;
	}

	/** conditions that must be met for an action to take place */
	public boolean resolveOpenPrecondition() throws FileNotFoundException
	{
	    
		/* added 12/10/18 - prints remaining preconditions */
		for (int i = 0; i < this.openPrecon.size(); i++)
			System.out.println("Open Preconditions left: " + this.openPrecon.get(i).getOpenPreconditionToString() + " action: "  + Actions.get(this.openPrecon.get(i).getStepID()).getStepName());
		
		/* added 12/12/18 - prints causal links created so far */
		System.out.println("\nLinks Created so far: ");
		for (int i = 0; i < this.Links.size(); i++)
			System.out.println(this.Links.get(i));
			
		/* print blank line */
		System.out.println("");

		OpenPrecondition precondition;

		//get the first open precondition in the queue
		
		/**
		 * This is where preconditions differ each time 
		 */
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
