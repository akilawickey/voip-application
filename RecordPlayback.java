/*
' * CO 324 - Network and Web programming
 * Project I
 * Skeleton Code
 */


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;



public class RecordPlayback {

boolean stopCapture = false;
ByteArrayOutputStream byteArrayOutputStream;
AudioFormat audioFormat;
TargetDataLine targetDataLine;
AudioInputStream audioInputStream;
SourceDataLine sourceDataLine;
byte tempBuffer[] = new byte[500];
DatagramPacket dgp;
DatagramSocket dgs;

public static void main(String[] args) {
    
	System.out.println("Running...");
    RecordPlayback playback = new RecordPlayback();
    playback.captureAudio();

}




private AudioFormat getAudioFormat() {
    float sampleRate = 16000.0F;
    int sampleSizeInBits = 16;
    int channels = 2;
    boolean signed = true;
    boolean bigEndian = true;
    return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
}   



private void captureAudio() {
    
    try {
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

        audioFormat = getAudioFormat();     //get the audio format
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

        targetDataLine = (TargetDataLine) mixer.getLine(dataLineInfo);
        targetDataLine.open(audioFormat);
        targetDataLine.start();

        DataLine.Info dataLineInfo1 = new DataLine.Info(SourceDataLine.class, audioFormat);
        sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo1);
        sourceDataLine.open(audioFormat);
        sourceDataLine.start();
        
        //Setting the maximum volume
        FloatControl control = (FloatControl)sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
        control.setValue(control.getMaximum());

       captureAndPlay(); //playing the audio

    } catch (LineUnavailableException e) {
        System.out.println(e);
        System.exit(0);
    }
  
}

    private void captureAndPlay() {
        byteArrayOutputStream = new ByteArrayOutputStream();
        stopCapture = false;
        try {
            int readCount;
            while (!stopCapture) {
                readCount = targetDataLine.read(tempBuffer, 0, tempBuffer.length);  //capture sound into tempBuffer
                if (readCount > 0) {
                    byteArrayOutputStream.write(tempBuffer, 0, readCount);
                    sourceDataLine.write(tempBuffer, 0, 500);   //playing audio available in tempBuffer
                     DatagramSocket socket = new DatagramSocket();
                     dgp = new DatagramPacket(tempBuffer, 500, InetAddress.getByName("127.0.0.1"), 50005);
                      //White noise at server side

                        while (true) {
                             socket.send(dgp);
                        }



                    // InetAddress addr = InetAddress.getByName("127.0.0.1");
                    // DatagramSocket socket = new DatagramSocket();
                    // while (true) {
                    //        // Read the next chunk of data from the TargetDataLine.
                    //     //   numBytesRead =  line.read(data, 0, data.length);
                    //        // Save this chunk of data.
                    //        dgp = new DatagramPacket (tempBuffer,tempBuffer.length,addr,50005);

                    //        socket.send(dgp);
                    //     }

                }
            }
            byteArrayOutputStream.close();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }
    
}
