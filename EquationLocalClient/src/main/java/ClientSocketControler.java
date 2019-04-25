import org.json.JSONObject;

public class ClientSocketControler {
    private Thread th;
    private ClientSocketThread socketThread;

    private String theServerAdress;
    private int thePortNumber;
    private Controller parent;

    public ClientSocketControler(String serverAdress, int portNumber, Controller theParent) {
        setTheServerAdress(serverAdress);
        setThePortNumber(portNumber);
        setParent(theParent);
        Connect();
    }

    public boolean isConnected() {
        return (socketThread!=null && socketThread.isConnected());
    }

    void Disconnect() {
        socketThread.disconnnect();
        socketThread = null;
        th.interrupt();
        th=null;
    }
    public void Connect(String serverAdress, int portNumber) {
        setTheServerAdress(serverAdress);
        setThePortNumber(portNumber);
        Connect();
    }
    private void Connect() {
        if(isConnected()==true) {
            Disconnect();
        }
        if(theServerAdress==null || thePortNumber<=1024) {
            System.out.println("System: The port should be larger then 1024\n");
            return;
        }
        socketThread = new ClientSocketThread(theServerAdress, thePortNumber, parent);
        th = new Thread(socketThread);
        th.start();
    }

    void SendData(String cmd, JSONObject data) {
        if(socketThread.WriteToSocket(ClientSocketThread.JsonDataWraper(cmd, data)))
        {
            parent.progressBar.setProgress(0.1);
        }
    }

    public String getTheServerAdress() {
        return theServerAdress;
    }
    public void setTheServerAdress(String theServerAdress) {
        this.theServerAdress = theServerAdress;
    }
    public int getThePortNumber() {
        return thePortNumber;
    }
    public void setThePortNumber(int thePortNumber) {
        this.thePortNumber = thePortNumber;
    }
    public Controller getParent() {
        return parent;
    }
    public void setParent(Controller parent) {
        this.parent = parent;
    }
}
