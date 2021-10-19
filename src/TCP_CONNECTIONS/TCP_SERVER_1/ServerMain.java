/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TCP_CONNECTIONS.TCP_SERVER_1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author MWIGO-JON-MARK
 */
public class ServerMain
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
     Tcp_Server_1.createServer("localhost", 12300, 70);
        
    }
    
}

class Tcp_Server_1
{
    public static void createServer(String ip, int port, int socket_queue)
    {
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            ServerSocket sSocket = new ServerSocket(port, socket_queue, ipAddress);
            System.out.println("SERVER STARTED!\nServer running at: "+ sSocket.getInetAddress().getHostAddress());
            
            do
            {
                System.out.println("Waiting for connection requests.....");
                Socket socket = sSocket.accept();
                System.out.println("Request recieved from "+ socket.getInetAddress().getHostAddress());
                System.out.println("Processing Request....");
                
                Runnable run = ()->handleClients(socket);
                
                new Thread(run).start();
                System.out.println("Client "+ socket.getInetAddress().getHostAddress() +" is successfully connected.");
                
            } while(true);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Tcp_Server_1.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch (IOException ex) {
            Logger.getLogger(Tcp_Server_1.class.getName()).log(Level.SEVERE, null, ex);
            
        }
    }

    private static void handleClients(Socket socket)
    {
        BufferedReader socketReader;
        BufferedWriter socketWriter;
        
        try {
            socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            
            String message;
            while((message = socketReader.readLine()) != null)
                
            {
                String ping = message;
                socketWriter.write("Your Message Has been Successfully recieved by the Server.\n");
                socketWriter.write(ping + "\n");
                socketWriter.flush();
                
            }
            
        } catch (IOException ex) {
            Logger.getLogger(Tcp_Server_1.class.getName()).log(Level.SEVERE, null, ex);
            
        } finally{
            try {
                socket.close();
                
            } catch (IOException ex) {
                Logger.getLogger(Tcp_Server_1.class.getName()).log(Level.SEVERE, null, ex);
                
            }
            
        }
    }
}
