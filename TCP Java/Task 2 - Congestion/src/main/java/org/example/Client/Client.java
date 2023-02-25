package org.example.Client;

import org.example.TCP.MessageFormat;
import org.example.TCP.MessageParse;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

import static java.lang.Math.min;

public class Client {
    static int SERVER_PORT = 3000, MSS = 1460, seqNo = 0, ackNo = 0, BUFFER_SIZE = 32000;
    static int RWND = BUFFER_SIZE;

    private static final int packetLossThreshold = 10; /* in (%) */

    private static int rrandom(){
        Random random = new Random();
        int min = 1;
        int max = 100;
        return random.nextInt((max - min) + 1) + min;
    }
    private static void sendSyn(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream) throws IOException {
        byte[] data = new byte[0];

        MessageFormat messageFormat = new MessageFormat(socket.getLocalPort(), socket.getPort(), seqNo, ackNo, false, true, false, RWND, MSS, data);
        dataOutputStream.write(messageFormat.segment);

        messageFormat.print();
    }

    private static void sendAck(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream) throws IOException {
        byte[] data = new byte[0];

        MessageFormat messageFormat = new MessageFormat(socket.getLocalPort(), socket.getPort(), seqNo, ackNo, true, false, false, RWND, MSS, data);
        dataOutputStream.write(messageFormat.segment);

        messageFormat.print();
    }

    private static void sendDupAck(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream) throws IOException {
        byte[] data = new byte[0];

        MessageFormat messageFormat = new MessageFormat(socket.getLocalPort(), socket.getPort(), seqNo, ackNo, true, false, false, RWND, MSS, data);
        dataOutputStream.write(messageFormat.segment);

        messageFormat.printDupAck();
    }


    private static void sendFin(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream) throws IOException {
        byte[] data = new byte[0];

        MessageFormat messageFormat = new MessageFormat(socket.getLocalPort(), socket.getPort(), seqNo, ackNo, false, false, true, RWND, MSS, data);
        dataOutputStream.write(messageFormat.segment);

        messageFormat.print();
    }
    public static void main(String[] args) throws IOException
    {
        try
        {
            Scanner scanner = new Scanner(System.in);
            Socket socket = new Socket("localhost", SERVER_PORT);
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            /* SENDING SYN */
            long startTime = System.nanoTime();
            sendSyn(socket, dis, dos);
            seqNo += 1;

            /* WAITING FOR SYN ACK */
            while(true) {
                if(dis.available() > 0) {

                    byte[] SynAckPacket = dis.readNBytes(dis.available());
                    MessageParse SynAck = new MessageParse(SynAckPacket); SynAck.print();

                    ackNo = SynAck.seqNo();
                    break;
                }
            }

            /* SENDING ACK */
            sendAck(socket, dis, dos);
            seqNo += 1;

            /* WAITING FOR FILE */
            FileOutputStream fileOutputStream = new FileOutputStream("Received image.jpg");
            byte[] buffer = new byte[BUFFER_SIZE];

            boolean finFlag = false, acked=true, packetLoss = false;
            int totalRecievedByte = 0, totalRcvdPacket = 0;
            int nextSequenceNumber = ackNo;

            while(true) {
                /* DATA RCV SESSION START */
                int available = dis.available();
                if(available > 0) {

                    byte[] packet = dis.readNBytes(min(available, MSS + 24));


                    if (rrandom() > packetLossThreshold) { /* PACKET LOSS */
                        MessageParse message = new MessageParse(packet);

                        /* SEND DUP ACK */
                        if(message.seqNo() != nextSequenceNumber){
                            ackNo = nextSequenceNumber;
                            packetLoss = true;
                        }
                        else {

                            message.print();
                            nextSequenceNumber += message.data().length;

                            ackNo = message.seqNo() + message.data().length;
                            if (message.isFin()) finFlag = true;

                            else {
                                /* WRITE TO FILE */
                                fileOutputStream.write(message.data());

                                acked = false;
                                /* LOG */
                                totalRecievedByte += message.data().length;
                                totalRcvdPacket += 1;
                            }
                        }
                    }
                    else{
                        packetLoss = true;
                        System.out.println("PACKET LOSS");

                    }

                }

                if(packetLoss){
                    /* DUP ACK */
                    ackNo = nextSequenceNumber;
                    sendDupAck(socket, dis, dos);
                    packetLoss = false;
                }

                /* FIN */
                else if(finFlag){
                    fileOutputStream.close();
                    /* FIN */
                    sendFin(socket, dis, dos);
                    seqNo += 1;
                    break;
                }

                /* ACK */
                else if(available == 0 && !acked){
                    sendAck(socket, dis, dos);
                    seqNo += 1;

                    acked = true;
                }
            }

            System.out.println("CONNECTION CLOSE");
            System.out.println("LOGS _____________");
            long endTime = System.nanoTime();
            long duration = (endTime - startTime);
            double seconds = (double)duration / 1_000_000_000.0;

            System.out.println("Time difference in seconds: " + seconds);
            System.out.println("TOTAL RCVD BYTE "  + totalRecievedByte);
            System.out.println("TOTAL RCVD PACKET "  + totalRcvdPacket);

            double throughput = totalRecievedByte / (seconds * 1_000_000)  ;
            System.out.println("AVERAGE THROUGHPUT "  + throughput);
            // closing resources
            scanner.close();
            dis.close();
            dos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
