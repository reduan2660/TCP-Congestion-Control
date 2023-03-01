package org.main.Server;

import org.main.TCP.MessageFormat;
import org.main.TCP.MessageParse;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.Math.min;

public class ServerThread extends Thread {
    final DataInputStream dataInputStream;
    final DataOutputStream dataOutputStream;
    final Socket socket;

    int seqNo = 0, rwnd = 65535;
    int cwnd, ssthresh;
    int prevSeq, prevAck, dupAck;
    int ackNo, totalSendByte = 0, totalSendPacket = 0;
    String SLOW_START = "SLOW START", CONGESTION_AVOIDANCE = "CONGESTION AVOIDANCE";
    String selectedMode = SLOW_START;


    List<Integer> logSentPacket;
    private void sendSynAck() throws IOException {
        byte[] data = new byte[0];

        MessageFormat messageFormat = new MessageFormat(3000, this.socket.getPort(), seqNo, 0, true, true, false, rwnd, 1460, data);
        // seqNo += 1;

        this.dataOutputStream.write(messageFormat.segment);

        MessageParse synAckMessage = new MessageParse(messageFormat.segment);
        synAckMessage.print();
    }

    private void sendFin() throws IOException {
        byte[] data = new byte[0];

        MessageFormat messageFormat = new MessageFormat(3000, this.socket.getPort(), seqNo, 0, false, false, true, rwnd, 1460, data);
        seqNo += 1;

        this.dataOutputStream.write(messageFormat.segment);

        MessageParse finMessage = new MessageParse(messageFormat.segment);
        finMessage.print();
    }

    private void sendFile(int MSS, int rwnd)  throws Exception
    {
        String fileName = "image.jpg";
        String path = "C:\\Users\\red\\Pictures\\" + fileName;
        File f = new File(path);
        if(f.exists()) {

            int bytes = 0;

            File file = new File(path);
            FileInputStream fileInputStream = new FileInputStream(file);

            // Here we break file into chunks
            byte[] buffer = new byte[MSS];

            fileInputStream.skip(seqNo);

            logSentPacket.add(cwnd/MSS);

            for (int i=0; i<(cwnd/MSS); i++){
                if ((bytes = fileInputStream.read(buffer, 0, MSS)) != -1) {

                    byte[] data = new byte[min(bytes, MSS)];
                    System.arraycopy(buffer, 0, data, 0, data.length);

                    // Send the file to Server Socket
                    MessageFormat messageFormat = new MessageFormat(3000, this.socket.getPort(), seqNo, ackNo, false, false, false, rwnd, MSS, data);
                    seqNo += min(bytes, MSS);

                    totalSendByte += data.length;
                    totalSendPacket += 1;

                    this.dataOutputStream.write(messageFormat.segment, 0, messageFormat.segment.length);
                    this.dataOutputStream.flush();

                    MessageParse dataMessage = new MessageParse(messageFormat.segment);
                    dataMessage.print();
                }
                else{
//                    System.out.println("TOTAL SEND " + totalSendByte);
//                    System.out.println("TOTAL SEND PACKET " + totalSendPacket);
                    sendFin();
                    break;
                }
            }
            // close the file here
            fileInputStream.close();
        }
        else{
            dataOutputStream.writeLong(0);
        }
    }

    // Constructor
    public ServerThread(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream)
    {
        this.socket = socket;
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
    }

    @Override
    public void run()
    {
        try {

            while(true) {
                if(dataInputStream.available() > 0) {
                    byte[] packet = dataInputStream.readNBytes(dataInputStream.available());

                    MessageParse message = new MessageParse(packet);
                    ackNo = message.ackNo();

                    /* SYN */
                    if (message.isSyn()) {
                        message.print();

                        cwnd = message.mss(); /* Setting the congestion window (cwnd) to 1 Maximum Segment Size (MSS). */
                        ssthresh = message.rwnd(); /* Setting the slow start threshold (ssthresh) to a large value, e.g., the size of the receive window. */

                        dupAck = 0; /* Setting the duplicate ACK counter to 0 */
                        prevAck = message.ackNo();
                        prevSeq = message.seqNo();

                        logSentPacket = new ArrayList<>(); /* RESETTING LOG */
                        sendSynAck();
                    }

                    /* ACK */
                    if (message.isAck()){
                        if(prevSeq == message.seqNo() && prevAck == message.ackNo()){
                            message.printDupAck();
                            dupAck += 1;
                        }
                        else message.print();

                        if(dupAck < 3) {
                            if (cwnd >= ssthresh) selectedMode = CONGESTION_AVOIDANCE;
                            if (Objects.equals(selectedMode, SLOW_START)) cwnd += message.mss(); /* For each ACK received, increase cwnd by 1 MSS. */
                            if (Objects.equals(selectedMode, CONGESTION_AVOIDANCE)) cwnd += ((message.mss() * message.mss()) / cwnd); /* For each ACK received, increase cwnd by (MSS * MSS) / cwnd  */
                        }

                        else{
                            /* set ssthresh to cwnd/2, set cwnd to 1 */
//                            ssthresh = cwnd/2;
//                            cwnd = message.mss();

                            /* FAST RETRANSMIT */
                            ssthresh = cwnd/2;
                            cwnd = ssthresh + (3* message.mss());
                            /* RETRANSMIT FROM DUP ACKED SEQ */
                            seqNo = message.ackNo();
                            dupAck = 0;
                        }

                        prevAck = message.ackNo();
                        prevSeq = message.seqNo();

                        /* Data */
                        sendFile(message.mss(), message.rwnd());

                    }

                    /* FIN */
                    if (message.isFin()) break;
                }
            }

            System.out.println("CONNECTION CLOSE");
            System.out.println("LOGS ------------------");
            System.out.println("TOTAL PACKET SENT: " + totalSendPacket);
            System.out.println("TOTAL BYTE SENT: " + totalSendByte);
//            for (Integer sentPacket : logSentPacket) System.out.print( "( " + totalSendPacket + " , " + sentPacket + "), ");
            for(int i=0; i< logSentPacket.size(); i++ ){
                System.out.print( "( " + i + " , " + logSentPacket.get(i) + "), ");
            }
            // closing resources
            this.dataInputStream.close();
            this.dataOutputStream.close();

        }
        catch(IOException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
