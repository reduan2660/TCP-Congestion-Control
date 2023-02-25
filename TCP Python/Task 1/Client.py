import socket
from Segment.Segment import Segment


# Define host IP and port
HOST = '127.0.0.1'
PORT = 8080
CLIENT_PORT = 1234

# Define buffer size
BUFFER_SIZE = 1024

# Create a TCP socket and connect to the server
client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client_socket.connect((HOST, PORT))

seqn = 0
ackn = 0
rwnd = 64000
mss = 1020

def syn():
    packet = Segment(
        source_port=CLIENT_PORT, 
        dest_port=PORT, 
        seq_no=seqn, 
        ack_no=ackn, 
        ack=False, 
        syn=True, 
        fin=False, 
        rwnd=rwnd, 
        mss=mss, 
        data=bytearray(0))
    print(packet)
    client_socket.send(packet.segment)


def ack():
    packet = Segment(
        source_port=CLIENT_PORT, 
        dest_port=PORT, 
        seq_no=seqn, 
        ack_no=ackn, 
        ack=True, 
        syn=False, 
        fin=False, 
        rwnd=rwnd, 
        mss=mss, 
        data=bytearray(0))
    print(packet)
    client_socket.send(packet.segment)


def main():
    
    syn()

    segment = client_socket.recv(BUFFER_SIZE)
    packet = Segment(setSeg=True, segment=segment)
    print(packet)

    ack()

    # Receive the file contents from the client using a buffer
    file_contents = b''
    while True:
        segment = client_socket.recv(min(mss + 24, BUFFER_SIZE))
        packet = Segment(setSeg=True, segment=segment)

        if not segment:
            break
        file_contents += packet.data()
    
    
    # Open a new file in binary mode and write the received contents to it
    with open('received_file.pdf', 'wb') as f:
        f.write(file_contents)

    client_socket.close()
    

if __name__ == '__main__':
    main()

