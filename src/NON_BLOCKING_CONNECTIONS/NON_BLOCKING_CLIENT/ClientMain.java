/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NON_BLOCKING_CONNECTIONS.NON_BLOCKING_CLIENT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.Set;
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
        Non_BlockingClient.createClient("localhost", 12300);
    }
    
}

class Non_BlockingClient
{
    private static BufferedReader consoleReader = null;
    public static void createClient(String serverIp, int port)
    {
        try {
            Selector selector = Selector.open();
            SocketChannel sChannel = SocketChannel.open();
            sChannel.configureBlocking(false);
            sChannel.connect(new InetSocketAddress(InetAddress.getByName(serverIp), port));
            sChannel.register(selector, SelectionKey.OP_CONNECT|SelectionKey.OP_READ|SelectionKey.OP_WRITE);

            System.out.println("CLIENT STARTED!\n\tClient running at: "+ sChannel.getLocalAddress());

            consoleReader = new BufferedReader(new InputStreamReader(System.in));

            while(true)
            {
                if(selector.select() > 0)
                {
                    boolean connectStatus = processReady(selector.selectedKeys());
                    if(connectStatus)
                    {
                        break;
                    }
                }
            }
            sChannel.close();
            
        } catch (IOException ex) {
            Logger.getLogger(Non_BlockingClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static boolean processReady(Set<SelectionKey> readySet) throws IOException
    {
        SelectionKey key;
        Iterator iterator = readySet.iterator();
        
        while(iterator.hasNext())
        {
            key = (SelectionKey) iterator.next();
            iterator.remove();
            
            if(key.isConnectable())
            {
                boolean connected = processConnect(key);
                if(!connected) return true; //Exit while loop.
            }
            if(key.isReadable())
            {
                System.out.println("\t\t\t\tPinged Message: "+ processRead(key));
            }
            if(key.isWritable())
            {
                String message = getUserInput();
                if(message.equalsIgnoreCase("bye")) return true; //Exit while loop.
                else processWrite(key, message);
            }
        }
        return false; //Not done yet rerun function. in while loop.
    }

    private static boolean processConnect(SelectionKey key)
    {
        try {
            SocketChannel sChannel = (SocketChannel) key.channel();
            
            while(sChannel.isConnectionPending()) sChannel.finishConnect();
            System.out.println("\t\tConnection ready!\n" + "\t\t\tClient connected to:"+ sChannel.getRemoteAddress());
        } catch (IOException ex) {
            key.cancel();
            Logger.getLogger(Non_BlockingClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    private static String processRead(SelectionKey key) throws IOException
    {
        SocketChannel sChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int byteCount = sChannel.read(buffer);
        String message = "";
        
        if(byteCount > 0)
        {
            buffer.flip();
            Charset charSet = Charset.forName("UTF-8");
            CharsetDecoder decoder = charSet.newDecoder();
            CharBuffer cBuffer = decoder.decode(buffer);
            message = cBuffer.toString();
        }
        return message;
    }

    private static void processWrite(SelectionKey key, String message) throws IOException
    {
        SocketChannel sChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        sChannel.write(buffer);
    }

    private static String getUserInput() throws IOException
    {
        String prompt = "\t\t\t\tType your message Here.(\"bye\" toexit.)";
        System.out.println(prompt);
        String userMessage = consoleReader.readLine();
        return userMessage;
    }
}
