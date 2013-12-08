package gokman.httpserver.main;

import gokman.httpserver.server.Server;
import gokman.httpserver.utils.Constants;

public class Main {
	/**
	 * Main Method
	 */
	public static void main(String[] args) {
		
	        Server webServer = new Server(Constants.PORT);
	        
	    try {
	    	
			webServer.runServer();
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}  
	    
	}
}