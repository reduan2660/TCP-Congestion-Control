package org.example.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException
    {
        ServerSocket ss = new ServerSocket(3000);
        System.out.println("Server started ");

        // client request
        while (true)
        {
            Socket socket = null;

            try
            {
                socket = ss.accept();

                System.out.println("A new client is connected : " + socket);

                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                // create a new thread object
                Thread t = new ServerThread(socket, dis, dos);

                // Invoking the start() method
                t.start();

            }
            catch (Exception e){
                socket.close();
                e.printStackTrace();
            }
        }
    }
}
