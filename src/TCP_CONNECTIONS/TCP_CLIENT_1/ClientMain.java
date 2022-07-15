/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TCP_CONNECTIONS.TCP_CLIENT_1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author MWIGO-JON-MARK
 */
public class ClientMain
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        Tcp_Client_1.createClient("localhost", 12300);
    }
    
}

class Tcp_Client_1
{
    public static void createClient(String server_ip, int server_port)
    {
        Socket cSocket;
        BufferedReader socketReader;
        BufferedWriter socketWriter;
        BufferedReader consoleReader;

        try {
            cSocket = new Socket(server_ip, server_port);
            System.out.println("CLIENT STARTED!\nCilent Running at: "+ cSocket.getLocalSocketAddress());
            
            socketReader = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));
            socketWriter = new BufferedWriter(new OutputStreamWriter(cSocket.getOutputStream()));
            
            consoleReader = new BufferedReader(new InputStreamReader(System.in));
            
            String prompt = "Type Your Message Here.(Bye to exit.)";
            System.out.println(prompt);
            String message;
            while((message = consoleReader.readLine()) != null)
            {
                if(message.equalsIgnoreCase("bye")){
                    cSocket.close();
                    break;
                    
                }
                
                socketWriter.write(message+ "\n");
                socketWriter.flush();
                
                System.out.println("In-Coming Server Message: "+ socketReader.readLine());
                System.out.println("Pinged Message: "+ socketReader.readLine() +"\n");
                
                System.out.println(prompt);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(Tcp_Client_1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
