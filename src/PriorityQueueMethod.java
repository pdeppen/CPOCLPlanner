import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;

public class PriorityQueueMethod extends Planner
{
	public PriorityQueueMethod(ConflictParser p, long startTime) throws FileNotFoundException
	{
		super(p, startTime);

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
				System.out.println("No Plan Found");
				//				break;
				return false;
			}

			this.updateCausalLinks();
			System.out.println("\n\n");

			if(!(this.CheckThreats()))
			{
				System.out.println("No Plan Found");
				//				break;
				return false;
			}
			
			this.printOutSolution();

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


	public boolean resolveOpenPrecondition() throws FileNotFoundException
	{



		OpenPrecondition precondition;

		//get the first open precondition in the queue
		precondition = this.getOpenPrecondition();
		System.out.println("The openPrecondition:	"+ precondition.getOpenPrecondtion());
		System.out.println("Action is "+ Actions.get(precondition.getStepID()).getStepName()+
				"	ActionID is "+precondition.getStepID());


		//search for an effect in the initial state to satisfy it (if there is)
		if(binding.isBounded(precondition.getOpenPrecondtion()))
		{
			boolean isFoundInIntialState = this.searchEffectInInitialState(precondition);
			System.out.println("In Initial State?	"+isFoundInIntialState);

			if(!(isFoundInIntialState))
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

				return true;


			}

			else
			{
				if((this.searchSimilarInEffects(precondition)))
				{
//					Step currentStep = Actions.get(precondition.getStepID());
//					printStepDetails(currentStep);

					return true;
				}
				else
				{
					//search for an action in the action domain to satisfy the open precondition
					//add the action to the plan
					boolean isFoundinActionDomain =this.searchEffectsInActionDomain(precondition);
					if(!(isFoundinActionDomain))
					{
						System.out.println("No Plan found");
						return false;
					}
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

		
		System.out.println("Printing Current Graph");
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
