import socket

# Define host IP and port
HOST = '127.0.0.1'
PORT = 8080

# Define buffer size
BUFFER_SIZE = 1024

# Open file in binary mode and read contents
with open('sample.pdf', 'rb') as f:
    file_contents = f.read()

# Create a TCP socket and connect to the server
client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

client_socket.connect((HOST, PORT))

# Receive the file contents from the client using a buffer
file_contents = b''
while True:
    data = client_socket.recv(BUFFER_SIZE)
    if not data:
        break
    file_contents += data

# Open a new file in binary mode and write the received contents to it
with open('received_file.pdf', 'wb') as f:
    f.write(file_contents)

# Close the socket
client_socket.close()

