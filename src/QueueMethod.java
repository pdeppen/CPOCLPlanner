import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;




public class QueueMethod extends Planner
{
	public QueueMethod(ConflictParser p, long startTime) throws FileNotFoundException
	{
		super(p, startTime);

		this.key=1;
		Actions = new LinkedList <Step>();
		Links = new ArrayList <CausalLink>();
		openPrecon = new LinkedList <OpenPrecondition>();


		Step goalState = p.getGoalState();
		goalState.setStepId(0);

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
		//StepId+=1;
		int stepId = step.getStepId();

		int preconditionsNum = step.getPreconditionSize();
		for(int i=0; i<preconditionsNum;i++)
		{

			//openPrecondition.addStep(StepId);

			OpenPrecondition object = new OpenPrecondition(stepId,null);

			object.addOpenPrcondition(step.getPreconditions(i));
			object.addStep(stepId);

			openPrecon.addLast(object);

			//System.out.print(step.getPreconditions(i).toString() +"\t" + step.getStepId());

		}
	}


	public boolean search() throws FileNotFoundException
	{
//		Collections.shuffle(openPrecon);

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
		}

		//to print out the solution if it exists
		if(openPrecon.isEmpty())
		{
			System.out.println("The Following plan has been found:");
			//			this.tempmethod();
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
					boolean isFoundinActionDomain =this.searchEffectsInActionDomain(precondition);
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
				System.out.println("Found similar");
				System.out.println(precondition.getOpenPrecondtion());
				return true;

			}

			else
			{
				if((this.searchSimilarInEffects(precondition)))
				{
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
				}
			}
		}

		return true;

	}


	public void tempmethod()
	{
		for(CausalLink pre :Links)
		{
			System.out.println(pre.getPrecondition().getOpenPrecondtion().isNegative()+ "--->"+pre.toString());
		}

		System.out.println(Links.size());


//		//to print out the preconditions in every action
//		for(int i =0; i< Actions.size();i++)
//		{
//			int sizeprecon = Actions.get(i).getPreconditionSize();
//			for(int f=0; f< sizeprecon;f++)
//			{
//				System.out.println(Actions.get(i).getPreconditions(f)+"  ");
//			}
//			System.out.println("\n");
//		}
		this.printOutSolution();
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

		int size = graph.topSort().size();
		for(int i= size -1;i>=0;i--)
		{
			System.out.println(graph.topSort().get(i));
		}
	}
}
