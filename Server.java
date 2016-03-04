import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Server extends Thread {

     DatagramSocket socket = null;
     boolean listen = true;


    public void run() {
        byte[] buf = new byte[100];

        try{
        DatagramSocket socket= new DatagramSocket(2127);    
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        
        while (listen) {
            try {
                // receive request
                socket.receive(packet);
               // System.out.println(packet.getData());  //checking the sound
                RecordPlayback.sourceDataLine.write(packet.getData(), 0, 100);   //playing audio available in tempBuffer
                
            } catch (Exception e) {
                e.printStackTrace();
                listen = false;
            }
        }
        socket.close();

    }catch(Exception e){

    	
    }
    }
}
