/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UDP_CONNECTIONS.UDP_CLIENT_1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
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
        UdpClient1.createClient("localhost", 12300);
    }
    
}

class UdpClient1
{
    public static void createClient(String serverIp, int port)
    {
        try {
            DatagramSocket dSocket = new DatagramSocket();
            
            System.out.println("UDP client running at: "+ dSocket.getLocalSocketAddress());
            String message;
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            
            String prompt = "Type a Message.(Bye to exit.)";
            System.out.println(prompt);
            
            while((message = consoleReader.readLine()) != null)
            {
                if(message.equalsIgnoreCase("bye"))
                {
                    dSocket.close();
                    break;
                }
                
                DatagramPacket packet = UdpClient1.getPacket(message, serverIp, port);
                
                dSocket.send(packet);
                dSocket.receive(packet);
                
                readPacket(packet);
                
                System.out.println(prompt);
                
            }
         
        }   catch (UnknownHostException ex) {
            Logger.getLogger(UdpClient1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SocketException ex) {
            Logger.getLogger(UdpClient1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UdpClient1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static DatagramPacket getPacket(String message, String destIp, int destPort)
    {
        final int MAX_LENGTH = 1024;
        byte[] msg = message.getBytes();
        int length = msg.length;
        if(length > MAX_LENGTH) length = MAX_LENGTH;
        
        DatagramPacket packet = new DatagramPacket(msg, length);
        
        try {
            packet.setAddress(InetAddress.getByName(destIp));
            packet.setPort(destPort);
        } catch (UnknownHostException ex) {
            Logger.getLogger(UdpClient1.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        return packet;
    }

    private static void readPacket(DatagramPacket packet)
    {
        byte[] message = packet.getData();
        int offset = packet.getOffset();
        int length = packet.getLength();
        
        System.out.println("Pinged Message: "+ new String(message, offset, length));
    }
}
