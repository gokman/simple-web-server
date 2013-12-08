package gokman.httpserver.client;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Date;

import gokman.httpserver.utils.Constants;

public class ClientThread extends Thread{
	
	private Socket soc;

	
	public ClientThread(Socket socket){
		
		super("thread");
		System.out.println(new Date());
		soc=socket;
		start();
	}
	public void run(){
		handleRequest(soc);
	}
	
	 /**
     * Handles web browser requests and returns a static web page to browser.
     * @param s socket connection between server and web browser.
     */
    public void handleRequest(Socket s) {
        BufferedReader is;     // inputStream from web browser
        PrintStream os;        // outputStream to web browser
        String request;        // Request from web browser
 
        try {
        	
            String webServerAddress = s.getInetAddress().toString();
            is = new BufferedReader(new InputStreamReader(s.getInputStream()));
            OutputStream out=new BufferedOutputStream(s.getOutputStream());
            os = new PrintStream(out, true);
            request = is.readLine();
            
            //after read request, we will analyse it
            
            if (!request.startsWith("GET") || request.length()<14 ||
                    !(request.endsWith("HTTP/1.0") || request.endsWith("HTTP/1.1"))) {
                    // bad request
                    errorReport(os, s, "400", "Bad Request", 
                                "Your browser sent a request that " + 
                                "this server could not understand.");
            }
            //request is valid
            else{
            	
            	String req = request.substring(4, request.length()-9).trim();
            	String path=Constants.ROOT_PATH+req;
            	File file=new File(path);
            	if (file.isDirectory() && !path.endsWith("/")) {
                    // redirect browser if referring to directory without final '/'
                    os.print("HTTP/1.0 301 Moved Permanently\r\n" +
                               "Location: http://" + 
                               s.getLocalAddress().getHostAddress() + ":" +
                               s.getLocalPort() + "/" + req + "/\r\n\r\n");
                } else {
                    if (file.isDirectory()) { 
                        // if directory, implicitly add 'index.html'
                        path = path + "index.html";
                        file = new File(path);
                    }
                    try { 
                        // send file
                        InputStream inputFile = new FileInputStream(file);
                        os.print("HTTP/1.0 200 OK\r\n" +
                                   "Content-Type: " + getContentType(path) + "\r\n" +
                                   "Date: " + new Date() + "\r\n" +
                                   "Server: FileServer 1.0\r\n\r\n");
                        sendFile(inputFile, out); // send raw file 
                        
                    } catch (FileNotFoundException e) { 
                        // file not found
                        errorReport(os, s, "404", "Not Found",
                                    "The requested URL was not found on this server.");
                    }
                }
            }
            
            os.close();
            s.close();
            
        } catch (IOException e) {
        	
            System.out.println("Failed to send response to client: " + e.getMessage());
            
        } finally {
        	
        	if(s != null) {
        		
        		try {
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
        return;
    }
    
    private static void sendFile(InputStream file, OutputStream out)
    {
        try {
        	
            byte[] buffer = new byte[1000];
            
            while (file.available()>0) 
                out.write(buffer, 0, file.read(buffer));
        } catch (IOException e) { 
        	
        	System.err.println(e); 
        	
        }
    }
    
    private static String getContentType(String path)
    {
        if (path.endsWith(".html") || path.endsWith(".htm")) 
            return "text/html";
        else if (path.endsWith(".txt") || path.endsWith(".java")) 
            return "text/plain";
        else if (path.endsWith(".gif")) 
            return "image/gif";
        else if (path.endsWith(".class"))
            return "application/octet-stream";
        else if (path.endsWith(".jpg") || path.endsWith(".jpeg"))
            return "image/jpeg";
        else    
            return "text/plain";
    }
    
    private static void errorReport(PrintStream pout, Socket connection,
            String code, String title, String msg)
	{
		pout.print("HTTP/1.0 " + code + " " + title + "\r\n" +
		"\r\n" +
		"<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">\r\n" +
		"<TITLE>" + code + " " + title + "</TITLE>\r\n" +
		"</HEAD><BODY>\r\n" +
		"<H1>" + title + "</H1>\r\n" + msg + "<P>\r\n" +
		"<HR><ADDRESS>FileServer 1.0 at " + 
		connection.getLocalAddress().getHostName() + 
		" Port " + connection.getLocalPort() + "</ADDRESS>\r\n" +
		"</BODY></HTML>\r\n");
	}
}