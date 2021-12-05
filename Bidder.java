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
		createServerSock();
		acceptServerSock();
		letsPause();
		bidding();
		letsPause();
		closeServerSock();
		createNodeSockOut();
		letsPause();
		closeNodeSockOut();
    }

	// Create server socket
	private void createServerSock(){
		// Create the server socket with the current port
		try {
			in_ss = new ServerSocket(in_port);

			// Print status
			System.out.println("Auctioneer's socket (Local Host: "+localhost+", Port number: "+in_port+") is listening....");
		}
		catch (IOException e){
			System.out.println("Socket could not be opened: "+e);
		}
		catch (IllegalArgumentException e){
			System.out.println("Incorrect port number: "+e);
		}
	}

	// Accept server socket
	private void acceptServerSock(){
		// Accept the server socket and the token
		try {
			in_soc = in_ss.accept();

			// Confirm that token has been received
			System.out.println("Auctioneer ("+in_port+ ") has received the token back");
		}
		catch (IOException e){
			System.out.println("Connection could not be made: "+e);
		}
	}

	// Have a wee pause before the next bit
	private void letsPause(){
		try{
			Thread.sleep(1000);
		}
		catch (InterruptedException e){
			System.out.println("Sleep interrupted: "+e);
		}
		catch (IllegalArgumentException e){
			System.out.println("Incorrect value entered: "+e);
		}
	}

	// Reading bid.txt and deciding to bid or not
	private void bidding(){
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
	}

	// Close the server socket
	private void closeServerSock(){
		// Close the current socket
		try {
			in_ss.close();

			// Confirm that Auctioneer has the token back
			System.out.println("Auctioneer: " +in_port+ " :: received token back");
		}
		catch (IOException e){
			System.out.println("Cannot close Server Socket: "+e);
		}
		letsPause();
	}

	// Create the socket to the next node
	private void createNodeSockOut(){
		// Create a new socket to send the token to
		try {
			out_soc = new Socket(localhost, out_port);
		}
		catch (UnknownHostException e){
			System.out.println("Invalid IP address provided: "+e);
		}
		catch (IOException e){
			System.out.println("Socket creation failed: "+e);
		}
		catch (IllegalArgumentException e){
			System.out.println("Incorrect Port number: "+e);
		}

		// Check success
		sockConnectSuccess();
	}

	// Check that the connection was successful
	private void sockConnectSuccess(){
		try {
			if (out_soc.isConnected()){
				// Confirm that connection was accepted
				System.out.println("Auctioneer: " +in_port+ " :: sent token to "+out_port);
			}
		}
		catch (Exception e){
			System.out.println("Could not connect to "+out_port+": Token not sent.");
		}
	}

	// Close new socket
	private void closeNodeSockOut(){
		// Close the new socket (pass the token)
		try {
			out_soc.close();
		}
		catch (IOException e){
			System.out.println("Socket failed to close: "+e);
		}
		// call to pause
		letsPause();

		// Call to check success
		sockCloseSuccess();
	}

	// Check closed successfully
	private void sockCloseSuccess(){
		// Check that the connection was successfully closed
		try {
			if (out_soc.isClosed()){
				System.out.println("Socket to bidder "+out_port+" is now closed.");
				System.out.println("Token has been passed successfully.");
			}
		}
		catch (Exception e){
			System.out.println("** Socket to first bidder "+out_port+" is still open **");
		}
	}

    public static void main (String[] args){
	// receive own port and next port in the ring at launch time
	if (args.length != 2) {
	    System.out.print("Usage: Bidder [port number] [forward port number]");
	    System.exit(1);
	}
    	Bidder b = new Bidder(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
    }
}

