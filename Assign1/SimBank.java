//Simulation Bank class

import java.util.*;
import java.text.DecimalFormat;
import java.lang.Math;

public class SimBank
{	

	private int ntell;  //# of tellers
	private boolean single; // true if single
	private double hrs;   //hours to run sim
	private double arr_rate; //arrivals per hour
	private double t_min;  //transaction length
	private int maxq;    //max amount of customers allowed to wait
	private long seed;   //random seed to initialize randDist; also plants a tree

	private int arrivals = 0;     //counters for arrivals, cmopletions, waiters
	private int completions = 0;
	private int waiters = 0;

	private ArrayList< Queue<Customer> > queues = new ArrayList< Queue<Customer> >();    //queue of customers
	private PriorityQueue<SimEvent> fel = new PriorityQueue<SimEvent>();				//future event list

	private ArrayList<Customer> customers = new ArrayList<Customer>();	//arraylist storing cusomters waited on
	private ArrayList<Customer> leavers = new ArrayList<Customer>();	//arraylist storing customers who left the bank


	public SimBank(int tellers, boolean sing, double hours, double arriv_rate, double tr_min, int maxq1, long seed1)
	{
		ntell = tellers;
		single = sing;
		hrs = hours;
		arr_rate = arriv_rate;
		t_min = tr_min;
		maxq = maxq1;
		seed = seed1;
	} //constructor containing bank information

	public void runSimulation()
	{
		Teller[] tellers = new Teller[ntell]; 

		for(int i =0; i <ntell; i++)
		{
			tellers[i] = new Teller(i);   //assigns tellers to index in array
		}

		if(single)
			queues.add(new LinkedList<Customer>() );  //creates queue for Oneline Bank

		else
			for(int i = 0; i < ntell; i++)
				queues.add(new LinkedList<Customer>() );   //creates queues for Bankkq

		double curr_time = 0.0;
		double minutes = hrs * 60; //calculating minutes
		double stop_time = curr_time + minutes;  //calculating stop time
		
		double arr_rate_min = arr_rate / 60;   //convert arrivals to minutes
		double serve_rate = 1/t_min;  //get service rate
		RandDist R = new RandDist(seed);  //seeding random distribution 


		double next_arr_min = R.exponential(arr_rate_min);  // calculating next arrival time through exponential distribution
		ArrivalEvent next_arrival = new ArrivalEvent(next_arr_min); //creating arrival event
		fel.offer(next_arrival); //adding event to queue

		SimEvent curr;  //current event

		//process events as long as there are events in queue. 
		while(fel.size() > 0)
		{
			curr = fel.poll();  
			curr_time = curr.get_e_time();

			if (curr instanceof ArrivalEvent)  //if next event is an arrival event
			{
				arrivals++; 

				Customer cust = new Customer(arrivals, curr_time);  //creating new customer

				double serve_time = R.exponential(serve_rate);   //calculate service time
				double finish_time = curr_time + serve_time;	//calculate finish time 

				cust.setServiceT(serve_time);  //setting service time

				boolean allBusy = true;
				for(int i = 0; i < ntell; i++)
				{
					if(!tellers[i].isBusy())
					{	
						allBusy = false;

						tellers[i].addCust(cust);
						cust.setTeller(i);
						cust.setStartT(curr_time);
						cust.setEndT(finish_time);
						CompletionLocEvent next_complete = new CompletionLocEvent(finish_time, i);
						fel.offer(next_complete); 

						break;  
						
					}

				} //Checking to see if any tellers are open; if they are, a customer is assigned to a teller

				
				int total = 0;

				if(allBusy) 
				{
					int shortest = queues.get(0).size();
					int index = 0;
					
					for(int j = 0; j< queues.size(); j++)
					{
						total += queues.get(j).size();
						if(queues.get(j).size() < shortest)
						{
							shortest = queues.get(j).size(); 
							index = j;
						}
					}

					if(total < maxq)
					{
						waiters++;
						queues.get(index).offer(cust);
						cust.setQueue(index);
					}
					else							
						leavers.add(cust);
				} //if all the tellers are busy, assign customer to the shortest queue. if bank is overloaded customer leaves.
				

				//get next arrival time; if it is before stop time, create new arrival event and add it to PQ.
				next_arr_min = curr_time + R.exponential(arr_rate_min);

				if (next_arr_min <= stop_time)
				{
					next_arrival = new ArrivalEvent(next_arr_min);
					fel.offer(next_arrival);
				}

			}

			else //if next event is completion event
			{
				completions++;

				int loc = ((CompletionLocEvent)curr).getLoc();  //get teller location.

				customers.add(tellers[loc].removeCust());   //removing customer from teller and adding them to customers arraylist

				Customer cust;
				if(!single)  
					cust = queues.get(loc).poll();  //if it is multiple queue set up, call customer from line corresponding to teller location
				
				else 
					cust = queues.get(0).poll();  //if it is single queue set up, call customer from only line
				

				if(cust != null)
				{
					tellers[loc].addCust(cust);

					cust.setTeller(loc);
					double finish_time = curr_time + cust.getServiceT();
					cust.setStartT(curr_time);
					cust.setEndT(finish_time);
					CompletionLocEvent next_complete = new CompletionLocEvent(finish_time, loc);
					fel.offer(next_complete);
					
				} //checking if customer is null (empty line); if not add customer to open teller, setting times and creating comp event.


			}

		}

	}//runSimulation()
	public void showResults()
	{	
		
		DecimalFormat f = new DecimalFormat("##.00"); //creating a decimal format
		System.out.printf("\n%-10s%-10s%-10s%-10s%-10s%-10s%-10s%-10s%-10s\n",
			"Customer", "Arrival", "Service" ,"Queue","Teller","Time Serv","Time Cust", "Time Serv","Time Spent");
		System.out.printf("%-10s%-10s%-10s%-10s%-10s%-10s%-10s%-10s%-10s\n",
			"Id","Time","Time","Loc","Loc","Begins","Waits","Ends", "in Sys");
		System.out.printf("%-10s%-10s%-10s%-10s%-10s%-10s%-10s%-10s%-10s\n",
			"-------","-------","--------","--------","--------","-------","-------","-------", "-------");
		for(int i = 0; i < customers.size(); i++)
		{
			System.out.printf("%-10s%-10s%-10s%-10s%-10s%-10s%-10s%-10s%-10s\n",
							customers.get(i).getId(), f.format(customers.get(i).getArrivalT() )
							,f.format(customers.get(i).getServiceT()) ,customers.get(i).getQueue() 
							,customers.get(i).getTeller() ,f.format(customers.get(i).getStartT())
							,f.format(customers.get(i).getWaitT()) ,f.format(customers.get(i).getEndT())
							,f.format(customers.get(i).getInSystem()));
			
		} //printing customer information for those who were processed
		System.out.println("\nCustomers who did not stay: \n");
		System.out.printf("%-10s%-10s%-10s\n", "Customer", "Arrival", "Service");
		System.out.printf("%-10s%-10s%-10s\n", "Id", "Time", "Time");
		System.out.printf("%-10s%-10s%-10s\n", "-------", "-------", "-------");

		for(int i = 0; i < leavers.size(); i++)
		{
			System.out.printf("%-10s%-10s%-10s\n", leavers.get(i).getId(), f.format(leavers.get(i).getArrivalT()) ,
							 f.format(leavers.get(i).getServiceT()) );
		} //printing customer info for those who left

		System.out.println("\nNumber of Tellers: "+ ntell);

		if(single)
			System.out.println("Number of Queues: 1" );
		else
			System.out.println("Number of Queues: " + ntell);

		System.out.println("Max number allowed to wait: " + maxq);
		System.out.println("Customer arrival rate (per hr):" + arr_rate);
		System.out.println("Customer service time (ave min):" + t_min);
		System.out.println("Number of customers arrived: " + arrivals);
		System.out.println("Number of customers served: " + completions);
		System.out.println("Num. turned away: " + leavers.size());
		System.out.println("Num. who waited: " + waiters);
		//printing bank information

		double totalWait = 0;
		double maxWait = 0;
		double y1 = 0;
		double totalInSystem = 0;
		double totalService = 0;

		for(int i = 0; i < customers.size(); i++)
		{
			totalWait += customers.get(i).getWaitT();

			if(customers.get(i).getWaitT() > maxWait)
				maxWait = customers.get(i).getWaitT();
			
			totalInSystem += customers.get(i).getInSystem();
			totalService += customers.get(i).getServiceT();
		} //calculating different total times and max wait

		double averageInSystem = totalInSystem / completions;
		double averageWait = totalWait / completions;
		double averageWaiterWait = totalWait / waiters;
		double averageService = totalService / completions;
		//calculating averages

		for(int i = 0; i < customers.size(); i++)
			y1 += Math.pow((customers.get(i).getWaitT() - averageWait), 2); //getting value standard Dev. calculation

		double standardDev = Math.sqrt(y1 / completions); //calculating standard dev.

		System.out.println("Average wait: "+ averageWait + " min.");
		System.out.println("Max Wait: " + maxWait + " min.");
		System.out.println("Std. Dev. Wait: " + standardDev);
		System.out.println("Average Service: " + averageService + " min.");
		System.out.println("Ave. Waiter Wait: "+ averageWaiterWait + " min.");
		System.out.println("Ave. in System: " + averageInSystem + " min.");
		//printing remaining bank information

	}//showResults()



}//SimBank