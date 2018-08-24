import java.util.HashMap;
import java.util.Map;

public class CausalLink
{

	private Step newStep;
	private OpenPrecondition preconditionObj;
	private boolean excuted;
	private Literal effect;



	public CausalLink(OpenPrecondition preconditionObj, Step newStep, Literal effect)
	{
		this.newStep = newStep;
		this.preconditionObj = preconditionObj;
		this.effect=effect;
		this.excuted = false;
	}

//	public Literal getEffect(CausalLink causalLink)
//	{
//		int sizeEffects = causalLink.getStepName().getEffectsSize();
//		Literal openPrecondition = causalLink.getPrecondition().getOpenPrecondtion();
//	
//
//		for(int i=0; i<sizeEffects; i++)
//		{
//			if(!(causalLink.getStepName().getEffects(i).isNegative()))
//			{
//				if(causalLink.getStepName().getEffects(i).getLiteralName().equals(openPrecondition.getLiteralName()))
//				{
//					int sizeParameter = causalLink.getStepName().getEffects(i).sizeLiteralParameters();
//					for(int x=0;x<sizeParameter;x++)
//					{
//						if(causalLink.getStepName().getEffects(i).getLiteralParameters(x).equals(openPrecondition.getLiteralParameters(x)))
//							openPrecondition = causalLink.getStepName().getEffects(i);
//
//							
//					}
//				}
//			}
//
//		}
//		return openPrecondition;
//	}
	

	
	/**
	 * This function searches if there is an executed precondition in the step, so we can bind its
	 * preconditions and effects
	 * @param step
	 * @return
	 */
	public Literal searchExcutedPreocon(Step step)
	{
		int sizePrecon = step.getPreconditionSize();
		for(int i=0;i<sizePrecon;i++)
		{

			if(step.getPreconditions(i).isExcuted())
			{
				Literal literal = step.getPreconditions(i);
				return literal;
			}

		}
		return null;
	}



	public void addStep(Step newStep)
	{
		this.newStep = newStep; 
	}

	public void addPrecondition(OpenPrecondition literal)
	{
		this.preconditionObj = literal;
	}

	public Step getStepName()
	{
		return this.newStep;
	}

	public OpenPrecondition getPrecondition()
	{
		return this.preconditionObj;
	}
	
	public Literal getEffect()
	{
		return this.effect;
	}


	public String toString()
	{

		return this.getPrecondition().getOpenPrecondtion() + " -->" + this.getStepName().getStepName() +"-->"+ this.getEffect().toString();
	}




}
