import socket

# Define host IP and port
HOST = '127.0.0.1'
PORT = 8080

# Define buffer size
BUFFER_SIZE = 1024

# Create a TCP socket and bind to the host and port
server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.bind((HOST, PORT))

# Listen for incoming connections
server_socket.listen()

# Accept a connection from a client
client_socket, client_address = server_socket.accept()



# Open file in binary mode and read contents
with open('sample.pdf', 'rb') as f:
    file_contents = f.read()
# Send the file contents to the server using a buffer
bytes_sent = 0
while bytes_sent < len(file_contents):
    # Calculate the end position of the buffer
    end_pos = bytes_sent + BUFFER_SIZE

    # Send the next chunk of data to the server
    client_socket.sendall(file_contents[bytes_sent:end_pos])

    # Update the bytes sent counter
    bytes_sent = end_pos



# Close the client socket and server socket
client_socket.close()
server_socket.close()

