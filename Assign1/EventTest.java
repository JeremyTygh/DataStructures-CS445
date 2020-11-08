// CS 0445 Spring 2020
// Simple demonstration of the basic idea of an event-driven simulation.  See
// Recitation 2 document for more information on this type of simulation.  This
// handout will be very helpful for Assignment 1.  Read the comments very
// carefully and run the program several times to make sure you understand what
// is going on.

// The idea of this program is that entities will have two times associated with
// them:
//		1) Their arrival times
//		2) Their service times
//	Both of these times will be generated randomly using an exponential distribution
//  (see below).  An entity will initially be given an arrival time and it will
//  arrive into the system at that time.  It will then be given a service time (i.e.
//  once it has arrived into the system, how long will it take for the entity to
//  complete its transaction)?  The entity will then exit the system after its
//  service time has completed.

//  The events in this system will be ArrivalEvents and CompletionEvents, as
//  explained below.  See also the files:
//  	SimEvent.java, ArrivalEvent.java, CompletionEvent.java

import java.util.*;
public class EventTest
{
	public static void main(String [] args)
	{
		// The events will be stored in a priority queue, prioritized by
		// the timestamp of the events.  
		PriorityQueue<SimEvent> eventPQ = new PriorityQueue<SimEvent>();
		SimEvent curr;
		Scanner S = new Scanner(System.in);
		
		double curr_time = 0.0;
		
		// Set arrival rate and average service times
		System.out.print("Arrival rate (per hour) > ");
		double arr_rate = S.nextDouble();
		
		System.out.print("Service time (average, in min.) > ");
		double ave_serv = S.nextDouble();
		System.out.print("How long to run (in hrs) > ");
		double hrs = S.nextDouble();
		double min = hrs * 60;
		double stop_time = curr_time + min;
		
		// Keep track of how many arrivals and completions have occurred.
		int arrivals = 0, completions = 0;
		
		// Note that the two values above are different in two ways, and must
		// be normalized in order to be used with the exponential distribution:
		// 1) The arrivals are a rate (number per unit time) while the service is
		//    the average value (duration).  These two values have an inverse
		//    relationship.  For more on this see RandTest.java
		// 2) The arrivals are per hour while the service is in minutes.  The time
		//    reference should be the same for both.
		double arr_rate_min = arr_rate/60;  // convert arrivals to minutes
		double serve_rate = 1/ave_serv;		// get service rate
		
		System.out.print("Enter a seed: > ");
		long seed = S.nextLong();
		RandDist R = new RandDist(seed);  // Seeds for random number generators can
			  // be arbitrary but and can be used to generate consistent data in multiple
			  // runs of a program.  Try running this program several times - using the
			  // same seed and different seeds.  You should see identical output for
			  // each run with the same seed.
			  
		// Generate time for first arrival event, create the event and put it into the
		// PQ.  This will start the process, which will continue as long as events 
		// remain in the PQ.  The time for the arrival of this event will be based on
		// the exponential distribution.  You don't need to know the details of this
		// distribution, but it does a reasonable job of modeling real-life arrivals.
		// For more information on this, see:
		
		// https://en.wikipedia.org/wiki/Exponential_distribution#Occurrence_and_applications
		
		// For more information on the RandDist class and how it is utilized, see the
		// following handouts:
		//		RandDist.java
		//		RandTest.java
		// These are both available on the CS 0445 Handouts page.
		
		double next_arr_min = R.exponential(arr_rate_min);
		ArrivalEvent next_arrival = new ArrivalEvent(next_arr_min);
		eventPQ.offer(next_arrival);
		
		// Keep processing as long as there are still events remaining to be processed.
		while (eventPQ.size() > 0)
		{
			// Get the next event from the PQ
			curr = eventPQ.poll();
			curr_time = curr.get_e_time(); // Move clock up to time of next 
						// event.  The idea is that the clock moves in chunks
						// of time -- up to the time when the next event will
						// occur. The time in between can be skipped because
						// nothing is overtly occurring / changing during this
						// time.
			System.out.printf("\tEvent at %6.2f ", curr.get_e_time());
			
			// Check to see the type of event using instanceof operator
			if (curr instanceof ArrivalEvent)
			{
				arrivals++;
				System.out.print(" is ArrivalEvent");  
				// Get the service time for the object that has just arrived.
				// Then create a CompletionEvent for the object with a time
				// equal to the current clock plus the service time.  Then add the
				// CompletionEvent to the PQ so that it can be considered with the
				// other events.  Note that since the events are being placed into
				// a PQ, the order that they are removed may not match the order that
				// they are inserted.
				double serve_time = R.exponential(serve_rate);
				System.out.printf(": Service time: %6.2f\n", serve_time);
				double finish_time = curr_time + serve_time;
				CompletionEvent next_complete = new CompletionEvent(finish_time);
				System.out.printf("\t\tAdding CompletionEvent for time: %6.2f \n", finish_time);
				eventPQ.offer(next_complete);  // Add CompletionEvent to PQ
				
				// Once the arrival has been processed, we consider when the next
				// arrival will occur.  This can be done by again using the exponential
				// distribution.  We get next arrival time, and if it is before the stop 
				// time, create a new ArrivalEvent and add it to the PQ.
				next_arr_min = curr_time + R.exponential(arr_rate_min);
				if (next_arr_min <= stop_time)
				{
					next_arrival = new ArrivalEvent(next_arr_min);
					System.out.printf("\t\tAdding next ArrivalEvent for time: %6.2f \n", next_arr_min);
					eventPQ.offer(next_arrival);
				}
			}  
			else
			{
				completions++;
				System.out.println(" is a CompletionEvent");
			}
		}
		System.out.printf("\nSystem is complete at time %6.2f \n", curr_time);
		System.out.println("\nThere were a total of: ");
		System.out.println("\t" + arrivals + " arrivals");
		System.out.println("\t" + completions + " completions");
	}
}