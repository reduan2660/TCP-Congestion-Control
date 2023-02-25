package org.example.Server;

import org.example.TCP.MessageFormat;
import org.example.TCP.MessageParse;

import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread {
    final DataInputStream dataInputStream;
    final DataOutputStream dataOutputStream;
    final Socket socket;

    int seqNo = 0, rwnd = 65535;
    int ackNo;

    private void sendSynAck() throws IOException {
        byte[] data = new byte[0];

        MessageFormat messageFormat = new MessageFormat(3000, this.socket.getPort(), seqNo, 0, true, true, false, rwnd, 1460, data);
        seqNo += 1;

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
            for (int i=0; i<(rwnd/MSS); i++){

                if ((bytes = fileInputStream.read(buffer)) != -1) {

//                fileInputStream.skip(seqNo*MSS);
//                if(fileInputStream.read(buffer, seqNo*MSS, MSS) != -1) {
                    // Send the file to Server Socket
                    MessageFormat messageFormat = new MessageFormat(3000, this.socket.getPort(), seqNo, ackNo, false, false, false, rwnd, MSS, buffer);
                    seqNo += MSS; fileInputStream.skip(MSS);

                    this.dataOutputStream.write(messageFormat.segment, 0, messageFormat.segment.length);
//                    this.dataOutputStream.flush();

                    MessageParse dataMessage = new MessageParse(messageFormat.segment);
                    dataMessage.print();
                }
                else{
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
                    message.print();


                    /* SYN */
                    if (message.isSyn()) {
                        sendSynAck();
                    }

                    /* ACK */
                    if (message.isAck()){
                        /* Data */
                        sendFile(message.mss(), message.rwnd());

                        /* FIN */
                        // sendFin();
                    }

                    /* FIN */
                    if (message.isFin()) break;
                }
            }

            System.out.println("CONNECTION CLOSE");
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
