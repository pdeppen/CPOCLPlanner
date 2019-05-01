import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;


/**
 * 
 * @author khaledalkathiri
 *
 * @param <T>
 */
public class Ordering<T>
{
    Map<T,List<T>> neighbors = new HashMap<T,List<T>>();
    Map<T, List<T>> restrictionNeighbors = new HashMap<T, List<T>>();
    Map<T, List<T>> temp = new HashMap<T, List<T>>();

    
    public int getSize()
    {
    	return neighbors.size();
    }
    
    public void createTemp()
    {
    		temp.clear();
		for (Map.Entry<T,List<T>> entry : neighbors.entrySet())
        {
            temp.put(entry.getKey(),
               // Or whatever List implementation you'd like here.
               (entry.getValue()));
        }
    }
    
    public void copyRestrictions()
    {
    		restrictionNeighbors.clear();
    		for (Map.Entry<T,List<T>> entry : temp.entrySet())
        {
            restrictionNeighbors.put(entry.getKey(),
               // Or whatever List implementation you'd like here.
               (entry.getValue()));
        }
//    		restrictionNeighbors.remove(neighbors.size());
    }
    
    public void resetRestrictions()
    {
//    		neighbors.putAll(restrictionNeighbors);
    		neighbors.clear();
		for (Map.Entry<T,List<T>> entry : restrictionNeighbors.entrySet())
        {
            neighbors.put(entry.getKey(),
               // Or whatever List implementation you'd like here.
               (entry.getValue()));
        }
    }
    
    /**
     * Add a vertex to the graph.  Nothing happens if vertex is already in graph.
     */
    public void add (T vertex) 
    {
        if (neighbors.containsKey(vertex)) 
        	return;
        neighbors.put(vertex, new ArrayList<T>());
    }
    
    /**
     * True iff graph contains vertex.
     */
    public boolean contains (T vertex) 
    {
        return neighbors.containsKey(vertex);
    }
    
    /**
     * Add an edge to the graph; if either vertex does not exist, it's added.
     * This implementation allows the creation of multi-edges and self-loops.
     */
    public void add (T from, T to)
    {
        this.add(from);
        this.add(to);
        neighbors.get(from).add(to);
    }
    
    /**
     * Remove an edge from the graph.  Nothing happens if no such edge.
     * @throws IllegalArgumentException if either vertex doesn't exist.
     */
    public void remove (T from, T to)
    {
        if (!(this.contains(from) && this.contains(to)))
            throw new IllegalArgumentException("Nonexistent vertex");
        neighbors.get(from).remove(to);
    }
    

    /**
     * Report (as a Map) the in-degree of each vertex.
     */
    public Map<T,Integer> inDegree () 
    {
        Map<T,Integer> result = new HashMap<T,Integer>();
        for (T v: neighbors.keySet())
        	result.put(v, 0);       // All in-degrees are 0
        
        for (T from: neighbors.keySet()) 		//to loop through all the steps
        {
            for (T to: neighbors.get(from))		//to loop through a specific step
            {
                result.put(to, result.get(to) + 1);           // Increment in-degree
            }
        }
        return result;
    }
    
    /**
     * Report (as a List) the topological sort of the vertices; null for no such sort.
     */
    public List<T> topSort () 
    {
        Map<T, Integer> degree = inDegree();
        
        //stack all vertices with zero in-degree
        Stack<T> zeroVerts = new Stack<T>();        
        for (T v: degree.keySet()) 
        {
            if (degree.get(v) == 0) 
            	zeroVerts.push(v);
        }
        
        // Determine the topological order
        List<T> result = new ArrayList<T>();
        while (!zeroVerts.isEmpty()) 
        {
            T v = zeroVerts.pop();                  // Choose a vertex with zero in-degree
            result.add(v);                          // Vertex v is next in topol order
            
            // "Remove" vertex v by updating its neighbors
            for (T neighbor: neighbors.get(v)) 
            {
                degree.put(neighbor, degree.get(neighbor) - 1);
                
                // Remember any vertices that now have zero in-degree
                if (degree.get(neighbor) == 0)
                	zeroVerts.push(neighbor);
            }
        }
        // Check that we have used the entire graph (if not, there was a cycle)
        if (result.size() != neighbors.size()) 
        	return null;
        
        return result;
    }
    
    /**
     * This method returns null if there is any cycle in the graph
     */
    public boolean isDag ()
    {
        return topSort() != null;
    }


    /**
     * 
     * @param s1
     * @param s2
     * @return
     */
    public boolean containsOrdering(T s1, T s2)
    {

    	if(neighbors.containsKey(s1))
    	{
        	//System.out.println(s1.toString());
        	if(neighbors.get(s1).contains(s2))
        	{
        		return true;
        	}
        	
        	//if there is a nested relation
        	for(T neighbor: neighbors.get(s1))
        	{
        		int size = neighbors.get(s1).size();
        		for(int i=0;i<size;i++)
        		{
        			neighbor = neighbors.get(s1).get(i);
        			if(neighbors.containsKey(neighbor))
        			{
        				if(neighbors.get(neighbor).contains(s2))
        					return true;
        			}
        			
          			//more nested relation (useful only with the bage method)
        			for(T neighbor2: neighbors.get(neighbor))
        			{
            			int size2 = neighbors.get(neighbor).size();
            			for(int k=0;k<size2;k++)
            			{
            				neighbor2 = neighbors.get(neighbor).get(k);
            				if(neighbors.containsKey(neighbor2))
            				{
            					if(neighbors.get(neighbor2).contains(neighbor))
            					{
            						return true;
            					}
            				}

            			}
        			}
  
        		}
      
        	}
 
    	}
    	return false;
    	
    }

   
/**
 * This method is to update the ordering when there is a threat between the steps
 * @param graph
 * @param s2
 * @param s1
 * @return
 */
    public Ordering<T> updateOrdering(Ordering <T> graph, T s2, T s1)
    {
        for (T v: neighbors.keySet())		//loop through the steps
        {
        	if(neighbors.get(v).contains(s1))			//if a step connected to s1
        	{
            	if(!(neighbors.get(v).contains(s2)))		//if the step is not connected to s2 to prevent looping
            	{
            		System.out.println("Found this step "+v.toString());
            		graph.add(s2, v);			//add this step
            	}

        	}
        }

    	return graph;
    }
    

//    
//    /**
//     * Main program (for testing).
//     * @throws FileNotFoundException 
//     */
//    public static void main (String[] args) throws FileNotFoundException {
//        // Create a Graph with Integer nodes
//        Digraph<Step> graph = new Digraph<Step>();
//        
//		Parser parser = new Parser();
//		String domainName = "Domain.txt";
//		String problemName = "Problem.txt";
//		parser.parseDomain(domainName);
//		parser.parseProblem(problemName);
//		
//		Planner planner = new BagMethod(parser);
//		Binding binding = new Binding(parser);
//        
//        Step unload = parser.getAction(1);
//        Step load = parser.getAction(0);
//        Step fly = parser.getAction(2);
//        Step init = parser.getInitialState();
//        Step goal = parser.getGoalState();
//        
//        graph.add(goal, unload);
//        graph.add(unload, fly);
//        graph.add(unload, load);
//        graph.add(fly, init);
//        graph.add(load, init);
//        graph.add(goal, init);
//        graph.add(fly, load);
//        
//
//        System.out.println("The current graph: " + graph);
//        System.out.println("In-degrees: " + graph.inDegree());
//        System.out.println("Out-degrees: " + graph.outDegree());
//        System.out.println("A topological sort of the vertices: " + graph.topSort());
//        System.out.println("The graph " + (graph.isDag()?"is":"is not") + " a dag");
//
////        graph.add(4, 1);                                     // Create a cycle
////        System.out.println("Cycle created");
////        System.out.println("The current graph: " + graph);
////        System.out.println("A topological sort of the vertices: " + graph.topSort());
////        System.out.println("The graph " + (graph.isDag()?"is":"is not") + " a dag");
//    }
}
