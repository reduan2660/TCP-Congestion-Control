

class Segment:
    segment = bytearray()

    def __init__(self, source_port=None, dest_port=None, seq_no=None, ack_no=None, ack=None, syn=None, fin=None, rwnd=None, mss=None, data=None, setSeg=False, segment=None):
        
        if setSeg:
            self.segment = segment
        else:
        
            seg = bytearray()
            
            sp = bytearray([(source_port >> 8) & 0xFF, source_port & 0xFF])
            seg = seg + sp
            
            dp = bytearray([(dest_port >> 8) & 0xFF, dest_port & 0xFF])
            seg = seg + dp

            sqn = bytearray([(seq_no >> 24) & 0xFF,(seq_no >> 16) & 0xFF,(seq_no >> 8) & 0xFF, seq_no & 0xFF])
            seg = seg + sqn

            ackn = bytearray([(ack_no >> 24) & 0xFF,(ack_no >> 16) & 0xFF,(ack_no >> 8) & 0xFF, ack_no & 0xFF])
            seg = seg + ackn

            f = bytearray([0x00, 0x00])

            if(ack):
                f[1] |= 1 << 4;
            if(syn):
                f[1] |= 1 << 1;
            if(fin):
                f[1] |= 1 << 0;
            seg = seg + f
            
            rw = bytearray([(rwnd >> 8) & 0xFF, rwnd & 0xFF])
            seg = seg + rw
        
            chop = bytearray([0x00, 0x00, 0x00, 0x00])
            seg = seg + chop

            option = bytearray([0x02, 0x02, 0x00, 0x00])

            option[2] = (mss >> 8) & 0xFF
            option[3] = (mss) & 0xFF

            seg = seg + option

            seg = seg + data

            self.segment = seg


    def sp(self):
        return int.from_bytes(self.segment[0:2], byteorder='big')
    
    def dp(self):
        return int.from_bytes(self.segment[2:4], byteorder='big')
    
    def seqn(self):
        return int.from_bytes(self.segment[4:8], byteorder='big')
    
    def ackn(self):
        return int.from_bytes(self.segment[8:12], byteorder='big')
    
    def isAck(self):
        return ((self.segment[13] & (1 << 4)) >> 4) == 1

    def isSyn(self):
        return ((self.segment[13] & (1 << 1)) >> 1) == 1
    
    def isFin(self):
        return (self.segment[13] & 1) == 1
    
    def rwnd(self):
        return int.from_bytes(self.segment[14:16], byteorder='big')
    
    def mss(self):
        return int.from_bytes(self.segment[22:24], byteorder='big')
    
    def data(self):
        return self.segment[24:]
    
    def __str__(self):
        s = f"Source: {self.sp()} Dest: {self.dp()} |"
        

        if (self.isSyn()):
            s += " SYN"
        if (self.isAck()):
            s += " ACK"
        if (self.isFin()):
            s += " FIN"

        if(self.isSyn() or self.isAck() or self.isFin()):
            s += f" | Receive Window: {self.rwnd()} MSS : {self.mss()}"
        else:
            s += f" Data: {len(self.data())}"

        
        return s