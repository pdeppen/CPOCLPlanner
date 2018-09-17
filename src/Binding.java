import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Binding
{
	/** just testing */
	Restrictions restrictions = new Restrictions("Binding");
	
	private Step variable;
	private Literal precondition;
//	Parser parser;
	ConflictParser parser;

//	public Binding(Parser parser)
	public Binding(ConflictParser parser)
	{
		this.parser = parser;
		this.variable = variable;
		this.precondition = precondition;
	}


	/**
	 * This function bound the current precondition with the next step
	 * @param variable
	 * @param precondition
	 * @param effectNum
	 */
	public Step bindLiterals(Step variable, Literal precondition, int effectNum)
	{
		Step newStep = variable;


		int x = parser.countParaInPredicate(precondition);

		//to hold the letters
		String GroundArry[] = new String[x];
		String  arry[] = new String[x];


		for (int i = 0;i< x; i++)
		{
			Literal predicate = parser.getPredicate(precondition);
			//adding the ground letters to array
			//GroundArry[i]= predicate.getLiteralParameters(i);
			GroundArry[i]= variable.getEffects(effectNum).getLiteralParameters(i);
			arry[i]= precondition.getLiteralParameters(i);

			//System.out.println(GroundArry[i]+"   "+ arry[i]);

		}

		//bind the step
		for (int f =0; f< arry.length; f++)
		{
			newStep = bindStep(variable,GroundArry[f],arry[f]);

		}

		return newStep;
	}


	/**
	 * This function returns a step with the new binding letters
	 * @param variable
	 * @param groundLetter
	 * @param newVariable
	 * @return
	 */
	public Step bindStep(Step variable,String groundLetter,String newVariable)
	{

		int preconditionsSize = variable.getPreconditionSize();
		int effectsSize = variable.getEffectsSize();
		int parameterSize = variable.getParameterSize();
		int agentSize = variable.getAgentsSize();

		//preconditions
		for(int i=0;i<preconditionsSize; i++)
		{
			int sizePara = variable.getPreconditions(i).sizeLiteralParameters();
			for(int f =0; f<sizePara;f++)
			{
				if(variable.getPreconditions(i).getLiteralParameters(f).equals(groundLetter))
					variable.getPreconditions(i).setLiteralParameters(f, newVariable);
			}
		}


		//effects
		for(int y=0;y<effectsSize; y++)
		{
			int sizePara = variable.getEffects(y).sizeLiteralParameters();
			for(int f =0; f<sizePara;f++)
			{
				if(variable.getEffects(y).getLiteralParameters(f).equals(groundLetter))
					variable.getEffects(y).setLiteralParameters(f, newVariable);
			}
		}

		//parameters
		for(int x=0; x<parameterSize;x++)
		{
			if(variable.getParameter(x).equals(groundLetter))
				variable.setParameter(x, newVariable);
		}

		//agents
		for(int z=0; z<agentSize;z++)
		{
			if(variable.getAgent(z).equals(groundLetter))
				variable.setAgent(z, newVariable);
		}

		return variable;
	}


	/**
	 * This function is to bind the precondition with the new variables
	 * @param precondition
	 * @param groundLetter
	 * @param newVariable
	 * @return
	 */
	public  Literal bindPrecondtion(Literal precondition, String groundLetter, String newVariable)
	{
		int precondtionParaSize = precondition.sizeLiteralParameters();
		for(int i=0;i<precondtionParaSize;i++)
		{
			if(precondition.getLiteralParameters(i).equals(groundLetter))
				precondition.setLiteralParameters(i, newVariable);

		}
		return precondition;
	}


	/**
	 * This function binds the current step when the precondition is bounded with the next step
	 * @param step
	 * @param precondition
	 * @return
	 */
	public Step bindStepByPrecondition (Step step,Literal precondition)
	{
		int x = parser.countParaInPredicate(precondition);

		String GroundArry[] = new String[x];
		String  arry[] = new String[x];

		for (int i = 0;i< x; i++)
		{
			Literal predicate = parser.getPredicate(precondition);
			//adding the ground letters to array
			GroundArry[i]= predicate.getLiteralParameters(i);
			arry[i]= precondition.getLiteralParameters(i);

		}

		//bind the step
		for (int f =0; f< arry.length; f++)
		{
			bindStep(step,GroundArry[f],arry[f]);

		}

		return step;
	}


	/**
	 * This method is used mostly when we have a threat and we changed the ordering
	 * @param step
	 * @param precondition that has the new letters
	 * @param precondition that has old values
	 * @return
	 */
	public Step bindStepByChangingLetters(Step step, Literal newPrecondition, Literal precondition)
	{
		int x = parser.countParaInPredicate(precondition);

		String GroundArry[] = new String[x];
		String  arry[] = new String[x];

		for (int i = 0;i< x; i++)
		{
			//adding the ground letters to array
			GroundArry[i]= precondition.getLiteralParameters(i);
			arry[i]= newPrecondition.getLiteralParameters(i);

		}

		for (int f =0; f< arry.length; f++)
		{
			bindStep(step,GroundArry[f],arry[f]);

		}

		return step;
	}


//	/**
//	 * This method is to bind next literals to allow the openPrecondition to be dequeued
//	 * becuase it can't be dequeued unless it is fully bounded
//	 * @param variable
//	 * @param precondition
//	 * @return
//	 */
//	public boolean bindNextLiterals(Step variable, Literal precondition)		//Variable (LOAD) 	precondition( IN C1 p)
//	{
//		//Literal newPrecondition = precondition;
//		boolean flag =false;
//		int sizeEffectInitailState = parser.getProblemDomainEffectsSize();
//		int sizeStepPrecondtion = variable.getPreconditionSize();
//
//		for(int x=0;x<sizeStepPrecondtion;x++ )		//The size of preconditions in the action
//		{
//
//
//			Literal literal = variable.getPreconditions(x);
//			ArrayList <Literal> arry = new ArrayList <Literal>();
//
//			if(!(this.isBounded(literal)))
//			{
//
//				//This to add the steps that have the same names to the temp array to check them
//				int index=0;
//				for(int y =0; y< sizeEffectInitailState;y++)		//The size of effect in initial state
//				{
//
//					//CargoAt == CargoAt
//					if(literal.getLiteralName().equals(parser.getIntialStateEffects(y).getLiteralName()))
//					{
//						arry.add(index,parser.getIntialStateEffects(y));
//						index++;
//
//					}
//				}
//
//
//				//loop through the array to check if there is a match in the initial State
//				for(int f=0; f< arry.size();f++)
//				{
//					int paraBounded = this.checkParaBounded(literal);
//					String para = literal.getLiteralParameters(paraBounded);
//					Literal temp = arry.get(f);	//CargoAt C1 SFO & CargoAt C2 JFK
//					if(para.equals(temp.getLiteralParameters(paraBounded)))
//					{
//
//						int paraNotBounded = this.checkParaNotBounded(literal);
//						String groundLetter = literal.getLiteralParameters(paraNotBounded);
//						String newVariable = temp.getLiteralParameters(paraNotBounded);
//
//						variable = bindStep(variable,groundLetter,newVariable);
//						precondition = bindPrecondtion(precondition, groundLetter, newVariable);
//						//variable = boundedStep;
//						flag = true;
//						arry.clear();
//					}
//
//				}
//				if(flag = false)
//					return false;
//			}
//		}
//		return true;
//	}






	/**
	 * This function is to check if the literal is fully bounded or not
	 * @param literal
	 * @return true if the literal is fully bounded
	 */
	public boolean isBounded(Literal literal)
	{
		int size = literal.sizeLiteralParameters();
		for(int i =0; i< size;i++)
		{
			if(literal.getLiteralParameters(i).contains("?")) {
				System.out.println("/********** RETURNING FALSE -> IN isBounded() Binding class");
				return false;
			}
		}
		return true;
	}


	/**
	 * This function is to check whether the  parameter is bounded or not
	 * @param para
	 * @return false if the parameter empty
	 */
	public boolean isParaBounded(String para)
	{
		if(para.contains("?"))
			return false;
		else
			return true;
	}


	/**
	 * This function is to check which parameter is not bounded
	 * @param literal
	 * @return the index of the parameter
	 */
	public int checkParaNotBounded(Literal literal)
	{
		int size = literal.sizeLiteralParameters();
		for(int i =0; i< size;i++)
		{
			if(literal.getLiteralParameters(i).contains("?"))
				return i;
		}
		return -1;
	}


	/**
	 * This function is to check which parameter is bounded
	 * @param literal
	 * @return the index of the parameter
	 */
	public int checkParaBounded(Literal literal)
	{
		int size = literal.sizeLiteralParameters();
		for(int i =0;i<size; i++)
		{
			if(!(literal.getLiteralParameters(i).contains("?")))
				return i;
		}
		return -1;
	}


}
