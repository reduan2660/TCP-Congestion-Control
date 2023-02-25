package org.example.TCP;

public class MessageParse {
    byte[] segment;
    public MessageParse(byte[] _segment){
        this.segment = _segment;
    }

    public static long unsigned(long signed){
        return signed & 0xffL;
    }

    public int sourcePort(){
        long bit1 = unsigned(segment[0]); // UNSIGNED
        long bit2 = unsigned(segment[1]);
        return (int) (bit2 + bit1*256);
    }
    public int destPort(){
        long bit1 = unsigned(segment[2]);
        long bit2 = unsigned(segment[3]);
        return (int) (bit2 + bit1*256);
    }
    public int seqNo(){
        long bit1 = unsigned(segment[4]);
        long bit2 = unsigned(segment[5]);
        long bit3 = unsigned(segment[6]);
        long bit4 = unsigned(segment[7]);
        return (int) (bit4 + bit3 * (1<<8) + bit2 * (1<<16) + bit1*(1<<24));
    }
    public int ackNo(){
        long bit1 = unsigned(segment[8]);
        long bit2 = unsigned(segment[9]);
        long bit3 = unsigned(segment[10]);
        long bit4 = unsigned(segment[11]);
        return (int) (bit4 + bit3 * (1<<8) + bit2 * (1<<16) + bit1*(1<<24));
    }

    public boolean isAck(){
        return (((segment[13] & (1 << 4)) >> 4) == 1);
    }
    public boolean isFin(){
        return (((segment[13] & (1 << 0)) >> 0) == 1);
    }
    public boolean isSyn(){
        return (((segment[13] & (1 << 1)) >> 1) == 1);
    }
    public int rwnd(){
        long bit1 = unsigned(segment[14]);
        long bit2 = unsigned(segment[15]);
        return (int) (bit2 + bit1*256);
    }
    public int checksum(){
        long bit1 = unsigned(segment[16]);
        long bit2 = unsigned(segment[17]);
        return (int) (bit2 + bit1*256);
    }
    public int mss(){
        long bit1 = unsigned(segment[22]);
        long bit2 = unsigned(segment[23]);
        return (int) (bit2 + bit1*256);
    }
    public byte[] data(){
        byte[] data = new byte[segment.length - 20 - 4]; // - header - option
        System.arraycopy(segment, 24, data, 0, data.length);
        return data;
    }


    public void print() {

        System.out.print(sourcePort() + " -> " + destPort() + " | ");
        if(isSyn()) System.out.print(" [SYN] ");
        if(isAck()) System.out.print(" [ACK] ");
        if(isFin()) System.out.print(" [FIN] ");
        System.out.print("| ");
        System.out.print( "LEN = " + segment.length  + " SEQ = " + seqNo() + "  ACK = " + ackNo() + " RWND = " + rwnd());

        System.out.println();
    }
}
