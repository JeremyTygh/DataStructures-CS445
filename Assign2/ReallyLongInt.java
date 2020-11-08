// CS 0445 Spring 2020
// This is a partial implementation of the ReallyLongInt class.  You need to
// complete the implementations of the remaining methods.  Also, for this class
// to work, you must complete the implementation of the LinkedListPlus class.
// See additional comments below.

public class ReallyLongInt 	extends LinkedListPlus<Integer> 
							implements Comparable<ReallyLongInt>
{
	private ReallyLongInt()
	{
		super();
	}

	// Data is stored with the LEAST significant digit first in the list.  This is
	// done by adding all digits at the front of the list, which reverses the order
	// of the original string.  Note that because the list is doubly-linked and 
	// circular, we could have just as easily put the most significant digit first.
	// You will find that for some operations you will want to access the number
	// from least significant to most significant, while in others you will want it
	// the other way around.  A doubly-linked list makes this access fairly
	// straightforward in either direction.
	public ReallyLongInt(String s)
	{
		super();
		char c;
		int digit = -1;
		// Iterate through the String, getting each character and converting it into
		// an int.  Then make an Integer and add at the front of the list.  Note that
		// the add() method (from A2LList) does not need to traverse the list since
		// it is adding in position 1.  Note also the the author's linked list
		// uses index 1 for the front of the list.
		for (int i = 0; i < s.length(); i++)
		{
			c = s.charAt(i);
			if (('0' <= c) && (c <= '9'))
			{
				digit = c - '0';
				// Do not add leading 0s
				if (!(digit == 0 && this.getLength() == 0)) 
					this.add(1, new Integer(digit));
			}
			else throw new NumberFormatException("Illegal digit " + c);
		}
		// If number is all 0s, add a single 0 to represent it
		if (digit == 0 && this.getLength() == 0)
			this.add(1, new Integer(digit));
	}

	public ReallyLongInt(ReallyLongInt rightOp)
	{
		super(rightOp);
	}
	
	// Method to put digits of number into a String.  Note that toString()
	// has already been written for LinkedListPlus, but you need to
	// override it to show the numbers in the way they should appear.
	public String toString()
	{
		StringBuilder b = new StringBuilder();
		Node curr = firstNode.prev;
		int i = 0;
		while (i < this.getLength())
		{
			b.append(curr.data.toString());  //adding digits to string
			
			curr = curr.prev;    //iterating curr
			i++;
		}
		return b.toString();
	}//toString

	// See notes in the Assignment sheet for the methods below.  Be sure to
	// handle the (many) special cases.  Some of these are demonstrated in the
	// RLITest.java program.

	// Return new ReallyLongInt which is sum of current and argument
	public ReallyLongInt add(ReallyLongInt rightOp)
	{
		int carry = 0;
		int result = 0;
		int count;
		int smallCount;
		Node longest; 
		Node shortest; 
		ReallyLongInt newInt = new ReallyLongInt();

		if(this.getLength() > rightOp.getLength())
		{
			longest = this.firstNode;
			shortest = rightOp.firstNode;     //finding longest and shortest numbers
			count = this.getLength();
			smallCount = rightOp.getLength();
		}
		else 
		{
			longest = rightOp.firstNode;
			shortest = this.firstNode;
			count = rightOp.getLength();
			smallCount = this.getLength();   //assigning longest and shortest based on list length

		}

		for(int i = 1; i <= count; i++)
		{

			if(i > smallCount)
				result = longest.data + carry;    //if we are past length of smaller list, ignore it when adding
			else
				result = longest.data + shortest.data + carry;   //if we are within shorter length, account for it when adding

			if(result >= 10)
			{
				carry = 1;
				result = result % 10;   //accounting for carry if adding results in a num 10 or greater
			}

			else 
				carry = 0;

			newInt.add(result);
			longest = longest.next;    //iterating longest and shortest
			shortest = shortest.next;

		}

		if(carry == 1)
		{
			newInt.add(1); //accounting for carry at end
			
		}

		return newInt;
	}//add
	
	// Return new ReallyLongInt which is difference of current and argument
	public ReallyLongInt subtract(ReallyLongInt rightOp)
	{	
		int borrow = 0;
		int result = 0;
		ReallyLongInt newInt = new ReallyLongInt();
		
		Node longest = this.firstNode;
		Node shortest = rightOp.firstNode;
		int count = this.getLength();
		int smallCount = rightOp.getLength();


		if(this.compareTo(rightOp) == -1)
		{
			throw new ArithmeticException(); //throw arithmetic exception if rightOP is greater than "this"
		}


		for(int i = 1; i <= count; i++)
		{	
			if(i > smallCount)
				result = longest.data - borrow; //if we are past length of smaller list, ignore it when subtracting
			else
				result = longest.data - shortest.data - borrow; //if we are within shorter length, account for it

			if(result < 0 )
			{
				borrow = 1;
				result += 10;  //accounting for borrows
			}

			else
				borrow = 0;

			newInt.add(result);
			longest = longest.next;
			shortest = shortest.next;   //iterating through longest and shortest

		}
		

		Node lastNode = newInt.firstNode.prev;
		while(lastNode.data == 0 && newInt.numberOfEntries > 1)
		{
			lastNode = lastNode.prev;
			newInt.numberOfEntries--;    //getting rid of extra zeroes.

		}
		lastNode.setNextNode(newInt.firstNode);  
		newInt.firstNode.setPrevNode(lastNode);

		
		return newInt;
	}//subtract

	// Return -1 if current ReallyLongInt is less than rOp
	// Return 0 if current ReallyLongInt is equal to rOp
	// Return 1 if current ReallyLongInt is greater than rOp
	public int compareTo(ReallyLongInt rOp)
	{
		if(this.getLength() > rOp.getLength())  // return 1 if current is grater than rOp
			return 1;
		else if(this.getLength() < rOp.getLength())
			return -1;          //return -1 if current is less than rOp

		Node lastNode = this.firstNode.prev;
		Node lastNodeO = rOp.firstNode.prev; 
		int count = 0;

		while(count != this.getLength()) //iterating through lists, comparing digit for digit
		{

			if(lastNode.data > lastNodeO.data)
			{
				return 1; //current is greater than rOp
			}
			else if(lastNode.data < lastNodeO.data)  
			{
				return -1; //current is less than rOp
			}

			lastNode = lastNode.prev;
			lastNodeO = lastNodeO.prev;
			count++;

		}
		return 0; //return 0 if current and rOp are equal



	}

	// Is current ReallyLongInt equal to rightOp?
	public boolean equals(Object rightOp)
	{
		ReallyLongInt rOp = (ReallyLongInt)rightOp;
		int result = compareTo(rOp);

		if(result == 0)
			return true; //if current equals rightOp, return true
		else
			return false; //otherwise, return false
	}

	// Mult. current ReallyLongInt by 10^num
	public void multTenToThe(int num)
	{	
		if(this.firstNode.data != 0)//if number is not 0, multiply
		{
			for(int i = 1; i <= num; i++)
			{
				this.add(1, 0);        //adding zeroes to list for num iterations
			}
		}
	}

	// Divide current ReallyLongInt by 10^num
	public void divTenToThe(int num)
	{

		int length = this.getLength();

		if(num < length) //if num is less than length subtract
		{

			Node lastNode = firstNode.getPrevNode();

			for(int i = 1; i <=num; i++)
			{
				firstNode = firstNode.next;     //iterate through, taking away digits, "num" of times
				numberOfEntries--;
			}

			lastNode.setNextNode(firstNode);
			firstNode.setPrevNode(lastNode);
		}

		else
		{
			firstNode.data = 0;
			firstNode.next = firstNode;
			firstNode.prev = firstNode; //if num is greater than or equal to length, return 0;
			numberOfEntries = 1;

		}

	
	}
	
}
