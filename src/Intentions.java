
public class Intentions
{
	private String owner;
	private String victim;
	private String detail;
	private Literal literal;
	
	public Intentions (Literal literal)
	{
		this.literal = literal;
		owner = literal.getLiteralParameters(0);
		victim = literal.getLiteralParameters(1);
		detail = literal.getLiteralParameters(2);
	}
}
