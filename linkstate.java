// Christian Toro
// UFID: 4169-4846
// CNT4007C - Computer Network Fundamentals
// Project Assignment 3

import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class linkstate {

	// Prints a step
	private static void printStep(Node n, int step, int size)
	{
		String toPrint = "" + step;
		for(int i = 0; i < size; i++)
		{
			if(n.getANodeParent(i) == -1) // If distance is infinite and parent unknown
			{
				toPrint += ",i,?";
			}
			else // There's a parent and distance between them
			{
				toPrint += "," + n.getANodeDistance(i) + "," + (n.getANodeParent(i));
			}
		}
		System.out.println(toPrint);
	}

	// Dijkstra's Algorithm
	private static void dijkstraAlgorithm(ArrayList<Node> a, int index)
	{
		// Keeps track of visited nodes
		ArrayList<Node> visited = new ArrayList<Node>();

		// List of nodes to visit, initialized to the node we start at from index
		ArrayList<Node> toVisit = new ArrayList<Node>();
		toVisit.add(a.get(index));
		
		/// Variables for keeping track of current node and current step
		Node current;
		int step = 0;

		while(!toVisit.isEmpty())
		{
			// Pop toVisit list for node
			current = toVisit.get(0);
			
			if(!visited.contains(current)) // If not visited
			{
				// For each neighbor we will add to toVisit array and then perform some actions
				for(int i = 0; i < current.getNumberOfNeighbors(); i++)
				{
					toVisit.add(a.get(current.getNeighbor(i) - 1));
					if(a.get(index).getANodeParent(current.getNeighbor(i) - 1) == -1) // No parent and distance infinite
					{
						// Since it hasn't been visited before, simply get the distance and set it as current as the parent node
						int distance = current.getNeighborDistance(i) + a.get(index).getANodeDistance(current.getNodeID() - 1);
						a.get(index).setANodeDistance(current.getNeighbor(i) - 1, distance);
						a.get(index).setANodeParent(current.getNeighbor(i) - 1, current.getNodeID());
					}
					else // Need to compare distances
					{
						// Same as before except it has been visited
						int distanceToCompare = current.getNeighborDistance(i) + a.get(index).getANodeDistance(current.getNodeID() - 1);
						Node temp = current;

						if(distanceToCompare < a.get(index).getANodeDistance(current.getNeighbor(i) - 1)) // new distance is shorter than old distance
						{
							a.get(index).setANodeDistance(current.getNeighbor(i) - 1, distanceToCompare);
							a.get(index).setANodeParent(current.getNeighbor(i) - 1, current.getNodeID());
						}
					}
				}
				// Add current to visited, print step, increment step
				visited.add(current);
				printStep(a.get(index), step, a.size());
				step++;
			}
			// Pop off the node we just visited
			toVisit.remove(0);
		}
	}

	// Function prints the header in accordance to number of nodes
	private static void printStart(ArrayList<Node> a)
	{
		String toPrint = "Step";
		for(int i = 0; i < a.size(); i++)
		{
			toPrint += ",D" + (i+1) + ",P" + (i+1);
		}

		System.out.println(toPrint);
	}

	// Creates nodes and fills the arraylist
	private static boolean initializeNode(ArrayList<Node> a, String[] x)
	{
		try
		{
			Node temp;
			int[] nodesToAdd = new int[x.length];

			// Each node is greater than 0, so subtract by 1 for index and increment for however many times it is encountered
			for(int i = 0; i < x.length; i += 3)
			{
				nodesToAdd[(int) Integer.parseInt(x[i]) - 1]++;
			}

			// If their is a node at that index, create node and then add to arraylist
			for(int i = 0; i < nodesToAdd.length; i++)
			{
				if(nodesToAdd[i] > 0)
				{
					temp = new Node(i + 1, nodesToAdd.length, nodesToAdd[i] - 1);
					a.add(temp);
				}
			}

			int currentNode = 1;
			int currentDistance = 0;
			int neighborNode = 1;

			// Add a node's neighbors to node object
			for(int i = 0; i < x.length; i++)
			{
				if(i % 3 == 0) // If a node
				{
					currentNode = (int) Integer.parseInt(x[i]) - 1;
				}
				else if((i - 1) % 3 == 0 && a.get(currentNode).getNodeID() != (int) Integer.parseInt(x[i])) // If a node neighbor
				{
					a.get(currentNode).setNeightbor((int) Integer.parseInt(x[i]));
				}
				else if((i - 2) % 3 == 0 && a.get(currentNode).getNodeID() != (int) Integer.parseInt(x[i - 1])) // If a distance between neighbor and node
				{
					a.get(currentNode).setNeightborDistance((int) Integer.parseInt(x[i]));
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private static StringBuilder getNetworkContents(FileReader f, BufferedReader b)
	{
		// Initialize variable for building string
		StringBuilder toReturn = new StringBuilder();
		String fileLine = "";

		try
		{
			// Append file contents while their is a next line
			while((fileLine = b.readLine()) != null)
			{
				toReturn.append(fileLine + " ");
			}
		}
		catch(Exception e)
		{
			// If error then terminate program
			e.printStackTrace();
			System.exit(1);
		}

		return toReturn;
	}

	public static void main(String[] args) throws Exception
	{
		// String for filename
		String fileName;
		
		// Variable for reading from file
		File file;
		FileReader fileReader;
		BufferedReader bufferedFileReader;

		// Variables for creating nodes
		String[] numberList;
		ArrayList<Node> theNodes = new ArrayList<Node>();

		try
		{
			// Get the filename
			fileName = args[0];
			
			// Create reader for file
			file = new File(fileName);
			fileReader = new FileReader(file);
			bufferedFileReader = new BufferedReader(fileReader);

			// Get the text from the file which is essentially strings separated by space
			// Close streams
			StringBuilder contents = getNetworkContents(fileReader, bufferedFileReader);
			fileReader.close();
			bufferedFileReader.close();

			// Splits the content into an array by space delimmited from string
			String splitter = contents.toString();
			String[] numberListTemp = splitter.split("\\s");

			// Remove any indeces with blank text
			List<String> temp = new ArrayList<String>(Arrays.asList(numberListTemp));
			temp.removeAll(Arrays.asList(""));

			// Get the content again in proper format
			numberList = new String[temp.size()];
			for(int i = 0; i < numberList.length; i++)
			{
				numberList[i] = temp.get(i);
			}

			if(numberList.length % 3 == 0) // Content formatted correctly and has all data
			{
				boolean cont = initializeNode(theNodes, numberList);
				
				if(cont && theNodes.size() > 0)
				{
					printStart(theNodes);
					// for(int i = 0; i < theNodes.size(); i++)
					// {
						dijkstraAlgorithm(theNodes, 0);
					// }
				}
				else // Error from converting string to int, possible non numeric input
				{
					throw new Exception("Network format incorrect");
				}
			}
			else // Content is not in groups of 3
			{
				throw new Exception("Network format incorrect");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	// Node class for creating list of nodes in network
	static class Node
	{
		private int nodeID;	// ID of Node, index in array is always nodeID - 1
		private int[][] nodeDistances; // 2D array where the index is the node and the pair of data is the parent, distance
		private int numberOfNeighbors; // Number of neighbors
		private int[][] neighbors; // 2D array of the neighbors where the pair of data is the node and distance
		private int neighborIndex; // Used for filling above array

		// Constructor
		Node(int nodeID, int numberOfNodes, int numberOfNeighbors)
		{
			this.nodeID = nodeID;
			this.nodeDistances = new int[numberOfNodes][2];
			this.neighbors = new int[numberOfNeighbors][2];
			this.numberOfNeighbors = numberOfNeighbors;
			this.neighborIndex = 0;

			for(int i = 0; i < numberOfNodes; i++)
			{
				if(i == this.nodeID - 1) // Distance to self is 0
				{
					this.nodeDistances[i][0] = this.nodeID;
					this.nodeDistances[i][1] = 0;
				}
				else // Init to unknown parent and infinite distance
				{
					this.nodeDistances[i][0] = -1;
					this.nodeDistances[i][1] = -2;
				}
			}

			for(int i = 0; i < numberOfNeighbors; i++)
			{
				this.neighbors[i][0] = -1;
				this.neighbors[i][1] = -2;
			}
		}

		int getNodeID() // Return node id (index in array is nodeID - 1)
		{
			return this.nodeID;
		}

		int getNumberOfNeighbors() // Returns number of neighbors
		{
			return this.numberOfNeighbors;
		}

		int getNeighbor(int index) // Gets a neighbor's nodeID
		{
			return this.neighbors[index][0];
		}

		void setNeightbor(int node) // Set this node's neighbor's nodeID
		{
			this.neighbors[this.neighborIndex][0] = node;
		}

		int getNeighborDistance(int index) // get this node's neighbor's distance
		{
			return this.neighbors[index][1];
		}

		void setNeightborDistance(int distance) // set this node's neighbor's distance
		{
			this.neighbors[this.neighborIndex][1] = distance;
			this.neighborIndex++;
		}

		int getANodeDistance(int index) // get node's distance to current
		{
			return this.nodeDistances[index][1];
		}

		void setANodeDistance(int index, int distance) // set node's distance to current
		{
			this.nodeDistances[index][1] = distance;
		}

		int getANodeParent(int index) // get the parent of this node's index
		{
			return this.nodeDistances[index][0];
		}

		void setANodeParent(int index, int node) // set the parent of this node's index
		{
			this.nodeDistances[index][0] = node;
		}
	}
}