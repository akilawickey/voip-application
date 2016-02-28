
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

public class Mic 
{
    public byte[] buffer;
    private int port;
    static AudioInputStream ais;

    public static void main(String[] args)
    {
        TargetDataLine line;
        DatagramPacket dgp; 

        AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
        float sampleRate = 16000.0F;
        int channels = 2;
        int sampleSizeInBits = 16;
        boolean bigEndian = true;
        boolean signed = true;
        InetAddress addr;


        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
        Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();    //get available mixers
        System.out.println("Available mixers:");
        Mixer mixer = null;

        for (int cnt = 0; cnt < mixerInfo.length; cnt++) {
            System.out.println(cnt + " " + mixerInfo[cnt].getName());
            mixer = AudioSystem.getMixer(mixerInfo[cnt]);

            Line.Info[] lineInfos = mixer.getTargetLineInfo();
            if (lineInfos.length >= 1 && lineInfos[0].getLineClass().equals(TargetDataLine.class)) {
                System.out.println(cnt + " Mic is supported!");
                break;
            }
        }
        
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            System.out.println("Line matching " + info + " not supported.");
            return;
        }

        try
        {
            line = (TargetDataLine) AudioSystem.getLine(info);

            int buffsize = line.getBufferSize()/5;
            buffsize += 512; 

            line.open(format);

            line.start();   

            int numBytesRead;
            byte[] data = new byte[buffsize];

            addr = InetAddress.getByName("127.0.0.1");
            DatagramSocket socket = new DatagramSocket();
            while (true) {
                   // Read the next chunk of data from the TargetDataLine.
                   numBytesRead =  line.read(data, 0, data.length);
                   // Save this chunk of data.
                   dgp = new DatagramPacket (data,data.length,addr,50005);

                   socket.send(dgp);
                }

        }catch (LineUnavailableException e) {
            e.printStackTrace();
        }catch (UnknownHostException e) {
            // TODO: handle exception
        } catch (SocketException e) {
            // TODO: handle exception
        } catch (IOException e2) {
            // TODO: handle exception
        }
    }
}