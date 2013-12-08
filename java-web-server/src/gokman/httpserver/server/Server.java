package gokman.httpserver.server;

import gokman.httpserver.client.ClientThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket; 
 
/**
 * Simple Web Server for learning purposes. Handles one client connection 
 * at a time and sends back a static HTML page as response.
 */
public class Server {
	
	 private ServerSocket socket;
	 private int port;
	 
	 public Server(int port){
		 
		 this.port=port;
		 
		 
	 }
	 /**
	  * Creates and returns server socket.
	  * @param port Server port.
	  * @return created server socket
	  * @throws Exception Exception thrown, if socket cannot be created.
	  */
    protected ServerSocket getServerSocket() throws Exception {
    	
        return new ServerSocket(port);
        
    }
 
    /**
     * Starts web server and handles web browser requests.
     * @param port Server port(ex. 80, 8080)
     * @throws Exception Exception thrown, if server fails to start.
     */
    public void runServer() throws Exception {
    	
    	socket = getServerSocket();
 
        while (true) {
        	
            try {
            	
                Socket serverSocket = socket.accept();
               
                new ClientThread(serverSocket);
                
            } catch(IOException e) {
            	
            	System.out.println("Failed to start server: " + e.getMessage());
            	System.exit(0);
            	return;
            	
            }
        }
    }   
   
}	