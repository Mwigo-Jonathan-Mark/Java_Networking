/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NON_BLOCKING_CONNECTIONS.NON_BLOCKING_SERVER;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
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
public class ServerMain
{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        NonBlockingTcpServer.createServer("localhost", 12300);
    }
    
}

class NonBlockingTcpServer
{
    public static void createServer(String hostIp, int port)
    {
        try {
            Selector selector = Selector.open();
            ServerSocketChannel ssChannel = ServerSocketChannel.open();
            
            ssChannel.configureBlocking(false);
            ssChannel.bind(new InetSocketAddress(InetAddress.getByName(hostIp), port));
            ssChannel.register(selector, SelectionKey.OP_ACCEPT);
            
            System.out.println("SERVER STARTED!\n\tServer Running at: "+ ssChannel.getLocalAddress() +"\n\t\tServer listenning for In-Coming requests....");
            
            while(true)
            {
                if(selector.select() <= 0) continue;
                processReadySet(selector.selectedKeys());
            }
            
        } catch (IOException ex) {
            Logger.getLogger(NonBlockingTcpServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void processReadySet(Set<SelectionKey> readySet) throws IOException
    {
        SelectionKey key;
        Iterator iterator = readySet.iterator();
        
        while(iterator.hasNext())
        {
            key = (SelectionKey) iterator.next();
            iterator.remove();
            
            if(key.isAcceptable())
            {
                System.out.println("\t\t\tConnection request detected!\n\t\t\tProcessing request....");
                processAccept(key);
            }
            
            if(key.isReadable())
            {
                String message = processRead(key);
                if(message != null && message.length() > 0) echoMessage(key, message);
                
            }
        }
        
    }

    private static void processAccept(SelectionKey key) throws IOException
    {
        ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();
        SocketChannel sChannel = ssChannel.accept();
        sChannel.configureBlocking(false);
        System.out.println("\t\t\t\tConnection from "+ sChannel.getRemoteAddress() +" is ready!");

        sChannel.register(key.selector(), SelectionKey.OP_READ);
           
    }

    private static String processRead(SelectionKey key) throws IOException
    {
        SocketChannel sChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int byteCount = sChannel.read(buffer);
        String message = null;
        
        if(byteCount > 0)
        {
            buffer.flip();
            Charset charSet = Charset.forName("UTF-8");
            CharsetDecoder decoder = charSet.newDecoder();
            CharBuffer charBuffer = decoder.decode(buffer);
            
            message = charBuffer.toString();
            System.out.println(sChannel.getRemoteAddress() +": "+ message);
        }
        return message;
        
    }

    private static void echoMessage(SelectionKey key, String message) throws IOException
    {
        SocketChannel sChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        System.out.println("Echoing Message to sender....");
        sChannel.write(buffer);
    }
}
