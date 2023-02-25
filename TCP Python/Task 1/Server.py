import socket

from Segment.Segment import Segment

# Define host IP and port
HOST = '127.0.0.1'
PORT = 8080

CLIENT_PORT = 1234


# Define buffer size
BUFFER_SIZE = 1024

seqn = 0
ackn = 0
rwnd = 0
mss = 0

def synack():
    packet = Segment(
        source_port=PORT, 
        dest_port=CLIENT_PORT, 
        seq_no=seqn, 
        ack_no=ackn, 
        ack=True, 
        syn=True, 
        fin=False, 
        rwnd=rwnd, 
        mss=mss, 
        data=bytearray(0))
    print(packet)
    
    client_socket.send(packet.segment)

def fin():
    packet = Segment(
        source_port=PORT, 
        dest_port=CLIENT_PORT, 
        seq_no=seqn, 
        ack_no=ackn, 
        ack=False, 
        syn=False, 
        fin=True, 
        rwnd=rwnd, 
        mss=mss, 
        data=bytearray(0))
    print(packet)
    
    client_socket.send(packet.segment)


# Create a TCP socket and bind to the host and port
server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.bind((HOST, PORT))

# Listen for incoming connections
server_socket.listen()

# Accept a connection from a client
client_socket, client_address = server_socket.accept()

def main():

    segment = client_socket.recv(BUFFER_SIZE)
    packet = Segment(setSeg=True, segment=segment)
    print(packet)

    mss = packet.mss()
    rwnd = packet.rwnd()

    if(packet.isSyn()):
        synack()

    segment = client_socket.recv(BUFFER_SIZE)
    packet = Segment(setSeg=True, segment=segment)
    print(packet)

    if(packet.isAck()):
        # Open file in binary mode and read contents
        with open('sample.pdf', 'rb') as f:
            file_contents = f.read()
        # Send the file contents to the server using a buffer
        bytes_sent = 0
        while bytes_sent < len(file_contents):
            # Calculate the end position of the buffer
            end_pos = bytes_sent + mss

            packet = Segment(
                source_port=PORT, 
                dest_port=CLIENT_PORT, 
                seq_no=seqn, 
                ack_no=ackn, 
                ack=False, 
                syn=False, 
                fin=False, 
                rwnd=rwnd, 
                mss=mss, 
                data=file_contents[bytes_sent:end_pos])
            print(packet)

            # Send the next chunk of data to the server
            client_socket.sendall(packet.segment)

            # Update the bytes sent counter
            bytes_sent = end_pos

    fin()
    # Close the client socket and server socket
    client_socket.close()
    server_socket.close()

# # Open file in binary mode and read contents
# with open('sample.pdf', 'rb') as f:
#     file_contents = f.read()
# # Send the file contents to the server using a buffer
# bytes_sent = 0
# while bytes_sent < len(file_contents):
#     # Calculate the end position of the buffer
#     end_pos = bytes_sent + BUFFER_SIZE

#     # Send the next chunk of data to the server
#     client_socket.sendall(file_contents[bytes_sent:end_pos])

#     # Update the bytes sent counter
#     bytes_sent = end_pos


if __name__ == '__main__':
    main()


