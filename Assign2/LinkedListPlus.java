// CS 0445 Spring 2020
// LinkedListPlus<T> class partial implementation

// See the commented methods below.  You must complete this class by
// filling in the method bodies for the remaining methods.  Note that you
// may NOT add any new instance variables, but you may use method variables
// as needed.

import java.lang.Math.*;

public class LinkedListPlus<T> extends A2LList<T>
{
	// Default constructor simply calls super()
	public LinkedListPlus()
	{
		super();
	}
	
	// Copy constructor.  This is a "deepish" copy so it will make new
	// Node objects for all of the nodes in the old list.  However, it
	// is not totally deep since it does NOT make copies of the objects
	// within the Nodes -- rather it just copies the references.  The
	// idea of this method is as follows:  If oldList has at least one
	// Node in it, create the first Node for the new list, then iterate
	// through the old list, appending a new Node in the new list for each
	// Node in the old List.  At the end, link the Nodes around to make sure
	// that the list is circular.
	public LinkedListPlus(LinkedListPlus<T> oldList)
	{
		super();
		if (oldList.getLength() > 0)
		{
			// Special case for first Node since we need to set the
			// firstNode instance variable.
			Node temp = oldList.firstNode;
			Node newNode = new Node(temp.data);
			firstNode = newNode;
			
			// Now we traverse the old list, appending a new Node with
			// the correct data to the end of the new list for each Node
			// in the old list.  Note how the loop is done and how the
			// Nodes are linked.
			Node currNode = firstNode;
			temp = temp.next;
			int count = 1;
			while (count < oldList.getLength())
			{
				newNode = new Node(temp.data);
				currNode.next = newNode;
				newNode.prev = currNode;
				temp = temp.next;
				currNode = currNode.next;
				count++;
			}
			currNode.next = firstNode;  // currNode is now at the end of the list.
			firstNode.prev = currNode;	// link to make the list circular
			numberOfEntries = oldList.numberOfEntries;
		}			
	}

	// Make a StringBuilder then traverse the nodes of the list, appending the
	// toString() of the data for each node to the end of the StringBuilder.
	// Finally, return the StringBuilder as a String.  Note that since the list
	// is circular, we cannot look for null.  Rather we must count the Nodes as
	// we progress down the list.
	public String toString()
	{
		StringBuilder b = new StringBuilder();
		Node curr = firstNode;
		int i = 0;
		while (i < this.getLength())
		{
			b.append(curr.data.toString());
			b.append(" ");
			curr = curr.next;
			i++;
		}
		return b.toString();
	}
	
	// Remove num items from the front of the list
	public void leftShift(int num)
	{	
		int length = this.getLength();

		if(num > 0 && num < length)
		{	
			Node lastNode = firstNode.getPrevNode();

			for(int i = 1; i <=num; i++)
			{
				firstNode = firstNode.next;    //if num is greater than 0, shift all elements left
				numberOfEntries--;
			}

			lastNode.setNextNode(firstNode);
			firstNode.setPrevNode(lastNode);

		}

		else if(num >= length)
		{
			this.clear();          //clear list if num is greater than or equal to length
		}
	}//leftShift

	// Remove num items from the end of the list
	public void rightShift(int num)
	{	
		int length = this.getLength();

		if(num > 0 && num < length)
		{
			Node lastNode = firstNode.getPrevNode();

			for(int i = 1; i <= num; i++)
			{
				lastNode = lastNode.getPrevNode();   //if num greater than 0, shift all elements left
				numberOfEntries--;
			}

			lastNode.setNextNode(firstNode);  
			firstNode.setPrevNode(lastNode);
		}

		else if(num >= length)
		{
			this.clear(); //clear list if num is greater than or equal to length
		}
	}//rightShift

	// Rotate to the left num locations in the list.  No Nodes
	// should be created or destroyed.
	public void leftRotate(int num)
	{
		if(num > 0)
		{
			for(int i = 1; i <= num; i++)   //if num is greater than 0, point change firstNode to rotate left
				firstNode = firstNode.next;
		}

		else if(num < 0)                
			rightRotate(Math.abs(num));  //if num is less than 0, call a right rotate
	}//leftRotate

	// Rotate to the right num locations in the list.  No Nodes
	// should be created or destroyed.
	public void rightRotate(int num)
	{
		if(num > 0)
		{
			int length = this.getLength();
			
			for(int i = 1; i <= num; i++)
				firstNode = firstNode.prev;        //repoint firstNode to rotate right

		}

		else if(num < 0)
			leftRotate(Math.abs(num));   //call leftrotate if num is less than 0
	}//rightRotate
	
	// Reverse the nodes in the list.  No Nodes should be created
	// or destroyed.
	public void reverse()
	{
		Node curr = firstNode;
		Node temp;

		do
		{	
			temp = curr.next;
			curr.next = curr.prev;
			curr.prev = temp;
			curr = temp;

		}while(curr != firstNode);    //iterating through list, switching pointers to reverse. 

		firstNode = firstNode.next; //repoint firstNode
	}//reverse				
}//LinkedListPlus
