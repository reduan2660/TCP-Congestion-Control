package org.example;

import org.example.TCP.MessageFormat;
import org.example.TCP.MessageParse;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        int BUFFER = 1;
        byte[] data = new byte[BUFFER];
        data[0] = (byte) 69;
        MessageFormat messageFormat = new MessageFormat(3000, 6000, 1200000, 600, true, false, false, 64000, 1460, data);

        MessageParse parsedMessage = new MessageParse(messageFormat.segment);
        System.out.println("Source Port = " + parsedMessage.sourcePort());
        System.out.println("Dest Port = " + parsedMessage.destPort());
        System.out.println("Seq = " + parsedMessage.seqNo());
        System.out.println("ackNo = " + parsedMessage.ackNo());
        System.out.println("isAck = " + parsedMessage.isAck());
        System.out.println("isSyn = " + parsedMessage.isSyn());
        System.out.println("isFin = " + parsedMessage.isFin());
        System.out.println("checksum = " + parsedMessage.checksum());
        System.out.println("mss = " + parsedMessage.mss());
        System.out.println("data = " + Arrays.toString(parsedMessage.data()));

    }
}

