import java.net.*;
import java.io.*;
import java.util.*;

public class Auctioneer{

	// Used to determine if current node makes a bid
    private Random rnd;

    private ServerSocket in_ss;
    private Socket in_soc; // Current socket
    private Socket out_soc; // Socket of next node (first bidder)

    String	localhost = "127.0.0.1";
    String hostName;
    int in_port; // Current port number
    int out_port; // Port number of next node (first bidder)
	int the_bid;
	FileWriter bidFile; // Variable used for bid.txt file
	PrintWriter printWriter; // Variable used for Print Writer
	int current_bid;

	public Auctioneer(){

	}

	public Auctioneer (int inPor, int outPor){
	
		rnd = new Random();
		in_port = inPor;
		out_port = outPor;
		the_bid = 15; // This is used to set the initial price

		System.out.println("Auctioneer: " +in_port+ " of distributed lottery is active ....");

		createBidFile(); // Call to create and instantiate file

		System.out.println("Auctioneer: " +in_port+ " -  STARTING AUCTION  with price = "+the_bid);

		letsPause();
		createNodeSockOut();
		letsPause();
		closeNodeSockOut();
		createServerSock();
		acceptServerSock();
		letsPause();
		closeServerSock();
	 }

	 // Create bid.txt file
	private void createBidFile(){
		// Create and instantiate the file named bid.txt with the initial price (the_bid)
		try {
			// Create the file
			bidFile = new FileWriter("bid.txt", false);

			// Create the Print Writer
			PrintWriter printWriter = new PrintWriter(bidFile, true);

			// Print the current bid as int
			printWriter.print(the_bid);

			// Close the Print Writer stream
			printWriter.close();

			// Close the File Writer stream
			bidFile.close();
		}
		catch (IOException e) {
			System.out.println("Cannot create the file "+e);
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

	// Call from Bidder to read the current bid on file
	public synchronized int getThe_bid(){
		try {
			BufferedReader br = new BufferedReader (new FileReader("bid.txt"));
			current_bid = Integer.parseInt(br.readLine());
		}
		catch (FileNotFoundException e){
			System.out.println("Could not access file bid.txt: "+e);
		}
		catch (IOException e){
			System.out.println("Could not read file bid.txt: "+e);
		}
		return current_bid;
	}

	 // Call from bidder to update the current bid on file
	public synchronized void setThe_bid(int newBid){
		 try {
			 this.the_bid = newBid;

			 // Create File Writer object again
			 bidFile = new FileWriter("bid.txt", false);

			 // Create the Print Writer again
			 printWriter = new PrintWriter(bidFile, true);

			 // Update the file to the new value of the_bid
			 printWriter.println(the_bid);

			 // Close the Print Writer stream
			 printWriter.close();

			 // Close the File Writer stream
			 bidFile.close();

			 try {
				 Thread.sleep(1000);
			 }
			 catch (InterruptedException e){
				 System.out.println("Sleep interrupted: "+e);
			 }
			 catch (IllegalArgumentException e){
				 System.out.println("Incorrect value entered: "+e);
			 }
		 }
		 catch (IOException e){
			System.out.println("Could not update the current bid value: "+e);
		 }
	}

	// Get the IP address and the port number of the current  node
	private void getIPandPort(){
		try{
			InetAddress n_inet_address =  InetAddress.getLocalHost() ;
			hostName = n_inet_address.getHostName();
			System.out.println ("node hostname is " +hostName+":"+n_inet_address);
		}
		catch (java.net.UnknownHostException e){
			System.out.println(e);
			System.exit(1);
		}
	}
    
    public static void main (String[] args){
		if (args.length != 2) {
	    	System.out.print("Usage: Auctioneer [port number] [forward port number]");
	    	System.exit(1);
		}
    	Auctioneer a = new Auctioneer(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
    }
}

