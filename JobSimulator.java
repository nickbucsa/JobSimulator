//Juan Gonzalez
//Razmig Tanashian
//Nicolae Bucsa
//Patricia Cruz
import java.util.Scanner;
import java.lang.Exception;
class JobSimulator {
	public static void main( String[] args ) {
		int queues, customers, minTime = 0, maxTime = 0, minProc = 0, maxProc = 0;
		Scanner scan = new Scanner( System.in );
		while( true ) {
			try {
				System.out.println( "How many queues do you want to simulate?" );
				queues = scan.nextInt();
				checkInput( queues );
				System.out.println( "How many customers do you want to simulate?" );
				customers = scan.nextInt();
				checkInput( customers );
				System.out.println( "What is the minimum time between job arrivals?" );
				minTime = scan.nextInt();
				checkInput( minTime );
				System.out.println( "What is the maximum time between job arrivals?" );
				maxTime = scan.nextInt();
				checkInput( maxTime );
                checkInput2( minProc, maxProc );
				System.out.println( "What is the minimum processing time for a job?" );
				minProc = scan.nextInt();
				checkInput( minProc );
				System.out.println( "What is the maximum processing time for a job?" );
				maxProc = scan.nextInt();
				checkInput( maxProc );
                checkInput2( minProc, maxProc );
				break;
			} catch( Exception e ) {
				System.out.println( "Bad input." );
				scan = new Scanner( System.in );
			}
		}
		Store store = new Store( queues, customers, minTime, maxTime, minProc, maxProc );
	}
	private static void checkInput( int x ) throws Exception {
		if( x <= 0 ) {
			throw new Exception( "Bad input." );
		}
	}
    private static void checkInput2( int min, int max ) throws Exception {
        if( min > max ) {
            throw new Exception( "Garbage input." );
        }
    }
}
class Person {
	public int arrivalTime, departureTime, processingTime;
	public Person( int minProc, int maxProc, int arrival, int turnTime ) {
		arrivalTime = arrival;
		processingTime = minProc + (int)(Math.random() * ((maxProc - minProc) + 1));
        if( turnTime > arrivalTime ) {
            departureTime = turnTime + processingTime;
        } else {
            departureTime = arrivalTime + processingTime;
        }
	}
}	
class Queue {
	PeopleNode peopleHead, peopleTail;
	int clock = -1, eventIndex = 0, totalDelay = 0, queue;
	public Queue( int queue ) {
		this.queue = queue;
	}
	public void addPerson( Person person ) {
		if( peopleHead == null ) {
			peopleHead = new PeopleNode( person );
			peopleTail = peopleHead;
		} else if( peopleHead == peopleTail ) {
			peopleTail = new PeopleNode( person );
			peopleHead.next = peopleTail;
		} else {
			peopleTail.next = new PeopleNode( person );
			peopleTail = peopleTail.next;
		}
	}
	public int getFinalDeparture() {
		if( peopleTail != null ) {
			return peopleTail.person.departureTime;
		}
		return 0;
	}
	public void simulateQueue( int clock ) {
		PeopleNode current = peopleHead;
		if( peopleHead == null ) return;
		if( peopleHead == peopleTail ) {
			if( current.person.arrivalTime == clock ) {
				System.out.println( clock + " | Customer Arrival in Queue " + queue + " - Processing Time: " + current.person.processingTime );
			}
			if( current.person.departureTime == clock ) {
				System.out.println( clock + " | Customer Departure in Queue " + queue );
			}
		}
		while( current != null && peopleHead != peopleTail ) {
			if( current.person.arrivalTime == clock ) {
				System.out.println( clock + " | Customer Arrival in Queue " + queue + " - Processing Time: " + current.person.processingTime );
			}
			if( current.person.departureTime == clock ) {
				System.out.println( clock + " | Customer Departure in Queue " + queue );
			}
			current = current.next;
		}
	}
}
class PeopleNode {
	PeopleNode next;
	Person person;
	public PeopleNode( Person person ) {
		this.person = person;
		next = null;
	}
}
class Store {
	Queue[] queues;
	Person[] people;
	public Store( int numberOfQueues, int customers, int minTime, int maxTime, int minProc, int maxProc ) {
		this.queues = new Queue[numberOfQueues];
		for( int x = 0; x < numberOfQueues; x++ ) {
			queues[x] = new Queue( x + 1 );
		}
		people = new Person[customers];
		people[0] = new Person( minProc, maxProc, 0, 0 );
		queues[0].addPerson( people[0] );
        int arrival, totalDelay = 0;
		for( int y = 1; y < customers; y++ ) {
            arrival = people[y-1].arrivalTime + minTime + (int)(Math.random() * ((maxTime - minTime) + 1));
			people[y] = new Person( minProc, maxProc, arrival, queues[getBestQueue()].getFinalDeparture() );
            if( ( queues[getBestQueue()].getFinalDeparture() - arrival ) > 0 ) totalDelay += queues[getBestQueue()].getFinalDeparture() - arrival;
			queues[getBestQueue()].addPerson( people[y] );
		}
		int longestTime = queues[getLongestQueue()].getFinalDeparture();
        System.out.println( "Longest queue time: " + longestTime );
		for( int z = 0; z <= longestTime; z++ ) {
			for( int zz = 0; zz < numberOfQueues; zz++ ) {
				queues[zz].simulateQueue( z );
			}
		}
        System.out.println( "Total delay: " + totalDelay );
	}
	public int getBestQueue() {
		int best = 0;
		for( int x = 1; x < queues.length; x++ ) {
			if( queues[x-1].getFinalDeparture() > queues[x].getFinalDeparture() ) best = x;
		}
		return best;
	}
	public int getLongestQueue() {
		int longest = 0;
		for( int x = 1; x > queues.length; x++ ) {
			if( queues[x-1].getFinalDeparture() < queues[x].getFinalDeparture() ) longest = x;
		}
		return longest;
	}
}