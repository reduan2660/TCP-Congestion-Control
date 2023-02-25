package org.example.TCP;

public class MessageFormat {
/* TCP SEGMENT STRUCTURE


       0                   1 1 1 1 1 1 1 1 1 1 2 2 2 2 2 2 2 2 2 2 3 3
       0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
      |           Source Port          |       Destination Port       |
      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
      |                        Sequence Number                        |
      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
      |                    Acknowledgment Number                      |
      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
      |  Data |           |U|A|P|R|S|F|                               |
      | Offset| Reserved  |R|C|S|S|Y|I|            Window             |
      |       |           |G|K|H|T|N|N|                               |
      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
      |           Checksum            |         Urgent Pointer        |
      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
      |                    Options (if any)                           |
      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
      |                             Data                              |
      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
*/

    /* OPTION (MSS)

     0                   1 1 1 1 1 1 1 1 1 1 2 2 2 2 2 2 2 2 2 2 3 3
     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |      Kind     |     Length    | Data (variable)
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
*/
    public byte[] segment;

    public MessageFormat(int _sourcePort, int _destinationPort, int _seq_no, int _ack_no, boolean _isAck, boolean _isSyn, boolean _isFin, int _rwnd, int _mss, byte[] _data){

        /* Source Port
            0                   1 1 1 1 1 1 1 1 1 1 2 2 2 2 2 2 2 2 2 2 3 3
           0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
          +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
          |           Source Port         |       Destination Port        |
          +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
         */

        byte[] sourcePort = new byte[2];
        /* Converting integer id to 2 byte array */
        sourcePort[1] = (byte) (_sourcePort & 0xFF);
        sourcePort[0] = (byte) ((_sourcePort >> 8) & 0xFF);

        byte[] destinationPort = new byte[2];
        /* Converting integer id to 2 byte array */
        destinationPort[1] = (byte) (_destinationPort & 0xFF);
        destinationPort[0] = (byte) ((_destinationPort >> 8) & 0xFF);

        /*

       0                   1 1 1 1 1 1 1 1 1 1 2 2 2 2 2 2 2 2 2 2 3 3
       0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
      |                        Sequence Number                        |
      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
         */

        byte[] seq_no = new byte[4];
        /* Converting integer id to 4 byte array */
        seq_no[3] = (byte) (_seq_no & 0xFF);
        seq_no[2] = (byte) ((_seq_no >> 8) & 0xFF);
        seq_no[1] = (byte) ((_seq_no >> 16) & 0xFF);
        seq_no[0] = (byte) ((_seq_no >> 24) & 0xFF);


        /*

       0                   1 1 1 1 1 1 1 1 1 1 2 2 2 2 2 2 2 2 2 2 3 3
       0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
      |                     Acknowledgment Number                     |
      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
         */

        byte[] ack_no = new byte[4];
        /* Converting integer id to 4 byte array */
        ack_no[3] = (byte) (_ack_no & 0xFF);
        ack_no[2] = (byte) ((_ack_no >> 8) & 0xFF);
        ack_no[1] = (byte) ((_ack_no >> 16) & 0xFF);
        ack_no[0] = (byte) ((_ack_no >> 24) & 0xFF);

        /*
           0                   1 1 1 1 1 1 1 1 1 1 2 2 2 2 2 2 2 2 2 2 3 3
           0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
          +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
          |  Data |           |U|A|P|R|S|F|                               |
          | Offset| Reserved  |R|C|S|S|Y|I|            Window             |
          |       |           |G|K|H|T|N|N|                               |
          +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
         */
        byte[] flag = new byte[2];
        if(_isAck) flag[1] |= 1<<4;
        if(_isSyn) flag[1] |= 1<<1;
        if(_isFin) flag[1] |= 1<<0;

        byte[] window = new byte[2];
        /* Converting integer id to 2 byte array */
        window[1] = (byte) ((_rwnd) & 0xFF);
        window[0] = (byte) ((_rwnd >> 8) & 0xFF);

        /*
          +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
          |           Checksum            |         Urgent Pointer        |
          +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
         */

        byte[] checksum = new byte[2];
        byte[] urgentPointer = new byte[2];


        /*
      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
      |                    Options (if any)                           |
      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

        (MSS)

         0                   1 1 1 1 1 1 1 1 1 1 2 2 2 2 2 2 2 2 2 2 3 3
         0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
        |    Kind (2)   |     Length    | Data (variable)
        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    */

        byte[] kind = new byte[1];
        /* Converting integer id to 1 byte array */
        kind[0] = (byte) ((2) & 0xFF);

        byte[] length = new byte[1];
        /* Converting integer id to 1 byte array */
        length[0] = (byte) ((2) & 0xFF);

        byte[] mss = new byte[2];
        /* Converting integer id to 2 byte array */
        mss[1] = (byte) ((_mss) & 0xFF);
        mss[0] = (byte) ((_mss >> 8) & 0xFF);


        /* Building Header */
        byte[] header = new byte[20];
        int byteIndex = 0;

        System.arraycopy(sourcePort, 0, header, byteIndex, sourcePort.length);              byteIndex += sourcePort.length;
        System.arraycopy(destinationPort, 0, header, byteIndex, destinationPort.length);    byteIndex += destinationPort.length;
        System.arraycopy(seq_no, 0, header, byteIndex, seq_no.length);                      byteIndex += seq_no.length;
        System.arraycopy(ack_no, 0, header, byteIndex, ack_no.length);                      byteIndex += ack_no.length;
        System.arraycopy(flag, 0, header, byteIndex, flag.length);                          byteIndex += flag.length;
        System.arraycopy(window, 0, header, byteIndex, window.length);                      byteIndex += window.length;
        System.arraycopy(checksum, 0, header, byteIndex, checksum.length);                  byteIndex += checksum.length;
        System.arraycopy(urgentPointer, 0, header, byteIndex, urgentPointer.length);        byteIndex += urgentPointer.length;

        /* Building Option */
        byte[] option = new byte[4];
        byteIndex = 0;

        System.arraycopy(kind, 0, option, byteIndex, kind.length);      byteIndex += kind.length;
        System.arraycopy(length, 0, option, byteIndex, length.length);  byteIndex += length.length;
        System.arraycopy(mss, 0, option, byteIndex, mss.length);        byteIndex += mss.length;

        /* Building Segment */
        byte[] segment = new byte[header.length + option.length + _data.length];
        byteIndex = 0;
        System.arraycopy(header, 0, segment, byteIndex, header.length); byteIndex += header.length;
        System.arraycopy(option, 0, segment, byteIndex, option.length); byteIndex += option.length;
        System.arraycopy(_data, 0, segment, byteIndex, _data.length);     byteIndex += _data.length;


        this.segment = segment;


//        for (byte b : this.segment)
//            System.out.print(String.format("%8s", Integer.toBinaryString((b + 256) % 256)).replace(' ', '0') + " ");
//        System.out.println();
    }

    public void print() {
        MessageParse parsedMessage = new MessageParse(segment);
        parsedMessage.print();
    }
}
