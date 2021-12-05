import java.net.*;
import java.io.*;
import java.util.*;

public class Bidder{

    private Random rnd;

    private ServerSocket in_ss;
    private Socket in_soc;
    private Socket out_soc;

    String	localhost = "127.0.0.1";

    int in_port;
    int out_port;
	int the_bid;

	Auctioneer auctioneer;


    public Bidder (int inPor, int outPor){
	
		rnd = new Random();
		in_port = inPor;
		out_port = outPor;

		// Creates new Auctioneer object
		auctioneer = new Auctioneer();

		// Create the server socket with the current port
		try {
			in_ss = new ServerSocket(in_port);

			// Print status
			System.out.println("Bidder  : " +in_port+ " of distributed lottery is active ....");
		}
		catch (IOException e){
			System.out.println("Could not create Server Socket for Bidder (Port: "+in_port+"): "+e);
		}


		// Wait for the token and receive it from a socket listening on the in_port;
		try {
			// Accept the server socket and the token
			in_soc = in_ss.accept();

			// Confirm that the token has been received
			System.out.println("Bidder (Port: " + in_port + ") has received the token.");
		}
		catch (IOException e){
			System.out.println("Connection could not be made: "+e);
		}

		// Call to Auctioneer to read current bid on file
		try {
			the_bid = auctioneer.getThe_bid();

			// Print status
			System.out.println("Node: "+in_port+" is reading the bid file");
		}
		catch (Exception e){
			System.out.println("Could not read file bid.txt: "+e);
		}

		// To bid or not to bid...
		try {
			// Generate a random integer of either 0 or 1
			int randNum = rnd.nextInt(2);

			if (randNum == 1){
				the_bid += 10;
				auctioneer.setThe_bid(the_bid); // Make a bid

				// Print confirmation of bid
				System.out.println("Node "+in_port+":  my bid is "+the_bid);
			}
			else {
				System.out.println("Node "+in_port+": no bid");
			}
		}
		catch (IllegalArgumentException e){
			System.out.println("Could not generate random number: "+e);
		}

		// Have a pause before closing the socket
	    try{
			Thread.sleep(1000);
		}
		catch (InterruptedException e){
			System.out.println("Sleep interrupted: "+e);
		}
		catch (IllegalArgumentException e){
			System.out.println("Incorrect value entered: "+e);
		}

		// Close the current socket
		try {
			in_soc.close();
		}
		catch (IOException e){
			System.out.println("Socket failed to close: "+e);
		}

		// Create a new socket to send the token to
		try {
			out_soc = new Socket(localhost, out_port);

			// Confirm status
			System.out.println("Bidder node: "+in_port+" now releasing the token");
		}
		catch (IOException e){
			System.out.println("Token could not be released: "+e);
		}

		// Check that the connection was successful
		try {
			if (out_soc.isConnected()){
				System.out.println("Socket to Node "+out_port+" connected okay");
			}
		}
		catch (Exception e){
			System.out.println("Socket to Node "+out_port+" failed to connect");
		}

		// Have a pause before closing the socket
		try{
			Thread.sleep(1000);
		}
		catch (InterruptedException e){
			System.out.println("Sleep interrupted: "+e);
		}
		catch (IllegalArgumentException e){
			System.out.println("Incorrect value entered: "+e);
		}

		// Close the socket (pass the token)
		try {
			out_soc.close();
		}
		catch (IOException e){
			System.out.println("Socket failed to close: "+e);
		}

		// Have another pause
		try{
			Thread.sleep(1000);
		}
		catch (InterruptedException e){
			System.out.println("Sleep interrupted: "+e);
		}
		catch (IllegalArgumentException e){
			System.out.println("Incorrect value entered: "+e);
		}

		// Check that the connection was successfully closed
		try {
			if (out_soc.isClosed()){
				System.out.println("Socket to Node "+out_port+" was closed successfully");
				System.out.println("Bidder  : " +in_port+ " - forwarded token to "+out_port);
			}
		}
		catch (Exception e) {
	    System.out.println("Connection could not be closed. Token could not be passed: "+e);
	    System.exit(1);	
		}
    }

    public static void main (String[] args){
	
	String n_host_name = "";
	
	// receive own port and next port in the ring at launch time
	if (args.length != 2) {
	    System.out.print("Usage: Bidder [port number] [forward port number]");
	    System.exit(1);
	}
	
	// get the IP address of the node  - might be useful on multi-computer runs 
 	try{ 
	    InetAddress n_inet_address =  InetAddress.getLocalHost() ;
	    n_host_name = n_inet_address.getHostName();
	    System.out.println ("node hostname is " +n_host_name+":"+n_inet_address);
    	}
    	catch (java.net.UnknownHostException e){
	    System.out.println(e);
	    System.exit(1);
    	} 
	
    	Bidder b = new Bidder(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
    }
}

