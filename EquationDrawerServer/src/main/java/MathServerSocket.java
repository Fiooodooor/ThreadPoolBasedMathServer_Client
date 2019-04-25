import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MathServerSocket extends ThreadPool{
    private ServerSocket server;
    private int portNumber;

    public MathServerSocket(int listeningPortNumber, int socketThreadsNumber) {
        super(socketThreadsNumber);
        if(listeningPortNumber<=1024) {
            System.out.println("[SERVER] Port number was less then 1024. For stability reasons setting it to default value 7755");
            listeningPortNumber = 7755;
        }
        this.setPortNumber(listeningPortNumber);
    }

    public void start() {
        System.out.println("######################################################################");
        System.out.println("##          Nicolaus Copernicus University, NSI, 02.2019            ##");
        System.out.println("######################################################################");
        System.out.println("##  Milosz Linkiewicz - 212.589  ::  Threadpool based math server.  ##");
        System.out.println("##                                                                  ##");
        System.out.println("##   Programming II - JAVA Studies with dr. Blazej Zyglarski        ##");
        System.out.println("######################################################################");
        System.out.println();
        System.out.println("[SERVER] Starting the R-‐>R equation solving and drawing server.");
        System.out.println("[SERVER] Adress: localhost, Port: " + String.valueOf(getPortNumber())
                                          + " worker-threads: " + String.valueOf(getThreadsNumber()));

        try {
            server = new ServerSocket(portNumber);
            while(true) {
                Socket klient = server.accept();
                System.out.println("[SERVER] Client connected: " + klient.getRemoteSocketAddress().toString());
                addJob(new ClientSocket(klient));
            }
        }
        catch (IOException e) {
            Logger.getLogger(MathServerSocket.class.getName()).log(Level.WARNING, "Unknown server IOException. Message:" + e.getMessage());
        }
    }

    private ServerSocket getServer() {
        return server;
    }
    private void setServer(ServerSocket server) {
        this.server = server;
    }
    public int getPortNumber() {
        return portNumber;
    }
    private void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }
}