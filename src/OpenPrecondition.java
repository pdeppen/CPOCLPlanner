

/**
 * This class is for adding the openPrecondition so I can keep track of their steps
 * @author khaledalkathiri
 *
 */
public class OpenPrecondition
{

	private int stepID;
	private Literal openPrecondition;

	public OpenPrecondition(int stepID, Literal openPrecondition)
	{
		this.stepID = stepID;
		this.openPrecondition = openPrecondition;
	}

	/**
	 * This method adds a new StepId
	 * @param stepID
	 */
	public void addStep(int stepID)
	{
		this.stepID = stepID;
	}

	/**
	 * This method adds the open precondition to the object
	 * @param openPrecondition
	 */
	public void addOpenPrcondition(Literal openPrecondition)
	{
		this.openPrecondition = openPrecondition;
	}

	/**
	 * This function returns the openPrecondition
	 * @return
	 */
	public Literal getOpenPrecondtion()
	{
		return this.openPrecondition;
	}


	/**
	 * This function returns the step of the openPreoconditon
	 * @return
	 */
	public int getStepID()
	{
		return this.stepID;
	}



}
