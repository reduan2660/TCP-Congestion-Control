package org.example.Client;

import org.example.TCP.MessageFormat;
import org.example.TCP.MessageParse;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static java.lang.Math.min;

public class Client {
    static int SERVER_PORT = 3000, MSS = 1460, seqNo = 1, ackNo = 0, BUFFER_SIZE = 65536;
    static int RWND = MSS * 4;

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
            sendSyn(socket, dis, dos);

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

            /* WAITING FOR FILE */
            FileOutputStream fileOutputStream = new FileOutputStream("Received image.jpg");
            byte[] buffer = new byte[BUFFER_SIZE];

            boolean finFlag = false; int i = 0;
            while(true) {
                /* DATA RCV SESSION START */

                int available = dis.available();
                if(available > 0) {
//                    System.out.println("READING BYTES = " + min(available, MSS + 24));

//                    byte[] Packet = dis.readNBytes(dis.available());
                    byte[] packet = dis.readNBytes(min(available, MSS + 24));
                    MessageParse message = new MessageParse(packet);
                    message.print();

                    ackNo = message.seqNo();
                    if (message.isFin()) finFlag = true;

                   /* WRITE TO FILE */
                    fileOutputStream.write(message.data());


                    i = i+1;
                }

                /* ACK */
                if(finFlag){
                    // sendAck(socket, dis, dos);
                    /* FIN */
                    sendFin(socket, dis, dos);
                    break;
                }
                else if(i==4){
                    sendAck(socket, dis, dos);
                    seqNo += 1;
                    i=0;
                }
            }



            System.out.println("CONNECTION CLOSE");
            // closing resources
            scanner.close();
            dis.close();
            dos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
