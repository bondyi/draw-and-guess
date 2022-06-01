package by.bondarik.drawandguess.model.network;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TCPConnection implements Closeable {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public TCPConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    public TCPConnection(String address, int port) throws IOException {
        this.socket = new Socket(address, port);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    public Socket getSocket() {
        return socket;
    }

    public boolean isActive() {
        return socket.isConnected();
    }

    public void send(Message message) throws IOException {
        synchronized (this.out) {
            out.writeObject(message);
        }
    }

    public Message receive() throws IOException, ClassNotFoundException {
        synchronized (this.in) {
            return (Message) in.readObject();
        }
    }

    @Override
    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    @Override
    public String toString() {
        return socket.getRemoteSocketAddress().toString();
    }
}
