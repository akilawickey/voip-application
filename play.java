import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class play extends Thread{
	static String str;
    @Override
    public void run() {
    	
    	DatagramPacket dgp;

    	try{

        InetAddress addr = InetAddress.getByName(str);
        DatagramSocket socket = new DatagramSocket();
        System.out.println(" .... Speak now .... ");    
        while (true) {
        int readCount;
//
        readCount = RecordPlayback.targetDataLine.read(RecordPlayback.tempBuffer, 0, RecordPlayback.tempBuffer.length);  //capture      sound into tempBuffer    
                      
        dgp = new DatagramPacket (RecordPlayback.tempBuffer,RecordPlayback.tempBuffer.length,addr,2127);

        socket.send(dgp);	
    	}	
    	}catch(Exception e){

        }	
    }

		public static void main(String args[]){		
		
        
		RecordPlayback rb = new RecordPlayback();
		rb.captureAudio();
            
        System.out.println("----------------------------------------------------------------------------");
        System.out.println("----------------------------VOIP APPLICATION--------------------------------");
        System.out.println("----------------------------------------------------------------------------");
        System.out.println();
        System.out.println("   Please Enter the IP Address  ");
        Scanner in = new Scanner(System.in);    //get the IP address from the user from the user 
        str = in.nextLine();
        
        Server s = new Server();
		play p= new play();
		s.start();    // starting the server thread to capture the data packets
		p.start();    // sending packets using a Thread
           
		
		}

	}
	