from Segment.Segment import Segment

seg = bytearray(b"K")
s = Segment(
    source_port=3000, 
    dest_port=6000, 
    seq_no=12314, 
    ack_no=1, 
    ack=False, 
    syn=True, 
    fin=False, 
    rwnd=1234, 
    mss=1460, 
    data=seg)

print(s)

