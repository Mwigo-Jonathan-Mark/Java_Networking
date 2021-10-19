/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UDP_CONNECTIONS.UDP_SERVER_1;

import java.io.IOException;
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
public class ServerMain
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        Udp_Server_1.createServer("localhost", 12300);
        
    }
    
}

class Udp_Server_1
{
    public static void createServer(String ip, int port)
    {
        try {
            DatagramSocket dSocket = new DatagramSocket(port, InetAddress.getByName(ip));
            System.out.println("UDP SERVER STARTED!\n\tServer is Running at "+ dSocket.getLocalSocketAddress());
            
            while(true)
            {
                DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
                
                System.out.println("\t\tWaiting for Packets from clients.....");
                
                dSocket.receive(packet);
                System.out.println("\t\t\tPacket Received from "+ packet.getAddress() + ":"+ packet.getPort());
                displayPacket(packet);
                dSocket.send(packet);
            }
            
        } catch (SocketException ex) {
            Logger.getLogger(Udp_Server_1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Udp_Server_1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Udp_Server_1.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    private static void displayPacket(DatagramPacket packet)
    {
        byte[] message = packet.getData();
        int length = packet.getLength();
        int offset = packet.getOffset();
        int remotePort = packet.getPort();
        InetAddress remoteIp = packet.getAddress();
        
        System.out.println("\t\t\t\tPacket(s) Received\n\t\t\t\t\t"+ remoteIp +":"+ remotePort +" Has Sent: "+ new String(message, offset, length));
    }
}
