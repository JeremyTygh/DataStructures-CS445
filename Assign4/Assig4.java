import java.util.*;

public class Assig4
{
	public static Random R = new Random();

	private ArrayList<Sorter<Integer>> sorts;
	private Integer [] A;	
	private int size;
	private int trials;
	private boolean presort;

	public void fillArray()
	{
		for (int i = 0; i < A.length; i++)
		{
			// Values will be 0 <= X < 1 billion
			if(!presort)
				A[i] = new Integer(R.nextInt(1000000000));  //random data
			else 
				A[i] = i;   //sorted data
		}
	}


	public Assig4(String sz, String runs, String sOr)
	{	
		size = Integer.parseInt(sz);
		trials = Integer.parseInt(runs);
		presort = Boolean.parseBoolean(sOr);


		sorts = new ArrayList<Sorter<Integer>>();
		sorts.add(new QuickSort<Integer>(new SimplePivot<Integer>()));
		sorts.add(new QuickSort<Integer>(new MedOfThree<Integer>()));
		sorts.add(new QuickSort<Integer>(new RandomPivot<Integer>()));
		sorts.add(new MergeSort<Integer>());   //adding sorts to array list

		A = new Integer[size];  //creating array of indicated size

		long start, finish;
		//long[] times = new long[trials];
		//long[] avgs = new long[sorts.size()];
		long[] bestRecs = new long[sorts.size()];
		long[] worstRecs = new long[sorts.size()];
		double[] bestAvgs = new double[sorts.size()];
		double[] worstAvgs = new double[sorts.size()];
		double sum;
		double avg;

		for (int i = 0; i < sorts.size(); i++)//iterating through array list
		{
			R.setSeed(123456);  // This will enable all sorts to use the same data.  If
					// you have multiple runs with the same algorithm you should only
					// set this one time for each algorithm so that the different runs
					// will have different data.
			int recurseMin = 3;
			double minTime = 0, maxTime = 0;
			int bestRec = 0, worstRec = 0;

			for(int x = 0; x < 14; x++) // for iterating through setMin()
			{

				sum = 0;
				avg = 0;
				sorts.get(i).setMin(recurseMin);

				for(int j = 0; j < trials; j++)
				{
			

					fillArray();
						
					// Get the current Sorter<T> object, set the min and sort the data
					Sorter<Integer> mySort = sorts.get(i);

					start = System.nanoTime();

					mySort.sort(A, A.length);
						
					finish = System.nanoTime(); 

					sum += (finish - start);//adding time to sum

				}
				

				avg = sum / trials;

				if(x == 0)    //speical case
				{
					maxTime = avg;
					minTime = avg;
					bestRecs[i] = recurseMin;
					worstRecs[i] = recurseMin;
					bestAvgs[i] = avg;
					worstAvgs[i] = avg;
				}

				else
				{
					if(avg < minTime)
					{
						minTime = avg;
						bestRecs[i] = recurseMin;    //comparing times to get best per algorithm
						bestAvgs[i] = avg;
					}
					if(avg > maxTime)
					{
						maxTime = avg;
						worstRecs[i] = recurseMin;
						worstAvgs[i] = avg;
					}

				}

				recurseMin += 5;

			}//end min 

			
		}

		String[] sortNames = {"Simple Pivot QuickSort", "Median of Three QuickSort", "Random Pivot QuickSort", "MergeSort"};

		double goodAvg = bestAvgs[0];
		double badAvg = worstAvgs[0];
		long goodRec = bestRecs[0];
		long badRec = worstRecs[0];        //initializing variables
		String goodSort = sortNames[0];
		String badSort = sortNames[0];


		for(int i = 0; i < sorts.size(); i++)
		{
			if(bestAvgs[i] < goodAvg)
			{
				goodAvg = bestAvgs[i];
				goodRec = bestRecs[i];		//finding best overall avg time out of all times
				goodSort = sortNames[i];
			}
			if(worstAvgs[i] > badAvg)
			{
				badAvg = worstAvgs[i];
				badRec = worstRecs[i];  	//finding worst overall avg time out of all times
				badSort = sortNames[i];
			}

		}


		String rand;

		if(presort)
			rand = "Presorted";
		else
			rand = "Random";


		System.out.println("Initialization information: \n \tArray size: " + size + "\n \tNumber of runs per test: " + trials + "\n \tInitial Data: " + rand);

		System.out.println("\nAfter the tests, here is the best setup: \n\t Algorithm: " + goodSort + "\n\t Data Status: " + rand + "\n\t Min Recurse: " + 
			goodRec + "\n\t Average: " + goodAvg/1000000000);
		System.out.println("\nAfter the tests, here is the worst setup: \n\t Algorithm: " + badSort + "\n\t Data Status: " + rand + "\n\t Min Recurse: " + 
			badRec + "\n\t Average: " + badAvg/1000000000);

		System.out.println("\nHere are the per algorithm results: ");

		System.out.println("Algorithm: Simple Pivot QuickSort \n \t Best Result: \n \t\t Min Recurse: " + bestRecs[0] + "\n\t\t Average: " + bestAvgs[0]/1000000000 + 
			" sec \n\t Worst Result: \n\t\t Min Recurse: " + worstRecs[0] + "\n\t\t Average: " + worstAvgs[0]/1000000000 + " sec");
		System.out.println("\nAlgorithm: Median of Three QuickSort \n \t Best Result: \n \t\t Min Recurse: " + bestRecs[1] + "\n\t\t Average: " + bestAvgs[1]/1000000000 + 
			" sec \n\t Worst Result: \n\t\t Min Recurse: " + worstRecs[1] + "\n\t\t Average: " + worstAvgs[1]/1000000000 + " sec");
		System.out.println("\nAlgorithm: Random Pivot QuickSort \n \t Best Result: \n \t\t Min Recurse: " + bestRecs[2] + "\n\t\t Average: " + bestAvgs[2]/1000000000 + 
			" sec \n\t Worst Result: \n\t\t Min Recurse: " + worstRecs[2] + "\n\t\t Average: " + worstAvgs[2]/1000000000 + " sec");
		System.out.println("\nAlgorithm: MergeSort \n \t Best Result: \n \t\t Min Recurse: " + bestRecs[3] + "\n\t\t Average: " + bestAvgs[3]/1000000000 + 
			" sec \n\t Worst Result: \n\t\t Min Recurse: " + worstRecs[3] + "\n\t\t Average: " + worstAvgs[3]/1000000000 + " sec");
		//printing output
	}

	public static void main(String [] args)
	{
		new Assig4(args[0], args[1], args[2]);
	}
	

}