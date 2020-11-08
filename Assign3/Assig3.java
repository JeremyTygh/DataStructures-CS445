import java.util.*;
import java.io.*;

public class Assig3
{
	public static void main(String[] args)
	{
		new Assig3();
	}

	public Assig3()
	{
		Scanner inScan = new Scanner(System.in);
		Scanner fReader;
		File fName;
        String fString = "";
        String phrase = "";
       
       	// Making sure the file name is valid
        while (true)
        {
           try
           {
               System.out.println("Please enter grid filename:");
               fString = inScan.nextLine();
               fName = new File(fString);
               fReader = new Scanner(fName);
              
               break;
           }
           catch (IOException e)
           {
               System.out.println("Problem " + e);
           }
        }

		// Parse input file to create 2-d grid of characters
		String [] dims = (fReader.nextLine()).split(" ");
		int rows = Integer.parseInt(dims[0]);
		int cols = Integer.parseInt(dims[1]);
		
		char [][] theBoard = new char[rows][cols];

		for (int i = 0; i < rows; i++)
		{
			String rowString = fReader.nextLine();
			for (int j = 0; j < rowString.length(); j++)
			{
				theBoard[i][j] = Character.toLowerCase(rowString.charAt(j));
			}
		}

		// Show user the grid
		for (int i = 0; i < rows; i++)
		{
			for (int j = 0; j < cols; j++)
			{
				System.out.print(theBoard[i][j] + " ");
			}
			System.out.println();
		}

		System.out.println("Please enter the phrase (separated by spaces):");
        phrase = (inScan.nextLine()).toLowerCase();
		while (!(phrase.equals("")))
		{
			String[] words = phrase.split(" ");
			int num = words.length;
			String[] i = new String[(num*2)];  
			System.out.println("Looking for: " + phrase);
			System.out.println("containing " + num + " words");

			boolean found = false;
			for (int r = 0; (r < rows && !found); r++)
			{
				for (int c = 0; (c < cols && !found); c++)
				{
					found = findPhrase(r, c, words, 0, i, theBoard);
				}
			}  //calling findPhrase to search grid

			if (found)
			{
				System.out.println("The phrase: " + phrase);
				System.out.println("was found: ");

				for(int j = 0; j < num; j++)
				{
					System.out.println(words[j] +": " + i[j*2] + " to " + i[(j*2)+1]);
				}

				for (int h = 0; h < rows; h++)
				{
					for (int j = 0; j < cols; j++)
					{
						System.out.print(theBoard[h][j] + " ");
						theBoard[h][j] = Character.toLowerCase(theBoard[h][j]);
					}
					System.out.println();
				}
			} //printing info in the case that the phrase is found
			else
			{
				System.out.println("The phrase: " + phrase);
				System.out.println("was not found");
			}//print info if phrase is not found
			
			System.out.println("Please enter the phrase to search for: ");
        	phrase = (inScan.nextLine()).toLowerCase();
		}
			
	}

	private boolean findWord(int row, int col, String[]words, int loc, int dir, 
								int num, String[] index, char[][] board)
	{

		if(loc == 0 )
			index[(num*2)] = "(" + row + "," + col + ")";
		if(row >= board.length || row < 0 || col >= board[0].length || col < 0)   //checking boundaries
			return false;
		else if(board[row][col] != words[num].charAt(loc)) //checking for matching character
			return false;                   
		else
		{
			board[row][col] = Character.toUpperCase(board[row][col]);  //converting letter to uppercase

			boolean answer = false;

			if( num == words.length-1 && loc == words[words.length-1].length() - 1)
			{
				answer = true;
				index[num*2 + 1] = "(" + row + "," + col + ")";
			} //this is the base case for the recursive method

			else if(loc == words[num].length()-1)
			{
				index[num*2 + 1] = "(" + row + "," + col + ")";

				if(!answer) //checking right
					answer = findWord(row, col+1, words, 0, 0, num+1, index, board);
				if(!answer)  //checking left
					answer = findWord(row+1, col, words, 0, 1, num+1, index, board);
				if(!answer) //checking down
					answer = findWord(row, col-1, words, 0, 2, num+1, index, board);
				if(!answer)// checking up
					answer = findWord(row-1, col, words, 0, 3, num+1, index, board);
				if(!answer)
					board[row][col] = Character.toLowerCase(board[row][col]);
			}		//otherwise, change letter to lower case

			else
			{
				if((!answer && dir==0) || (!answer && loc==0 && num ==0)) //back right
					answer = findWord(row, col+1, words, loc+1, 0, num, index, board);
				if((!answer && dir==1) || (!answer && loc==0 && num ==0))//back down
					answer = findWord(row+1, col, words, loc+1, 1, num, index, board);
				if((!answer && dir==2) || (!answer && loc==0 && num ==0))//back left
					answer = findWord(row, col-1, words, loc+1, 2, num, index, board);
				if((!answer && dir == 3) || (!answer && loc==0 && num == 0)) //back up
					answer = findWord(row-1, col, words, loc+1, 3, num, index, board);
				if(!answer)
					board[row][col] = Character.toLowerCase(board[row][col]);
					//backtracking, so also have to convert character to lower case

			}

			return answer;
		}


	}


	private boolean findPhrase(int row, int col, String[] words, int num, String[] index, char[][] board)
	{
		boolean found = false;

		for(int j = 0; j < 4; j++)
		{
			found = findWord(row, col, words, 0, j, 0, index, board);
			if(found) break;
		}      //looking for words to form a phrase

		return found;
	}

}