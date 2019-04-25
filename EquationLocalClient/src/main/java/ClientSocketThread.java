import javafx.embed.swing.SwingFXUtils;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;

public class ClientSocketThread implements Runnable
{
    private int portNumber;
    private String hostAdress;
    private Socket socketHolder;
    private BufferedWriter bw;
    private BufferedReader br;
    private Controller parent;

    private String serverType;
    private String serverUser;
    private String serverVersion;
    private Map<String,Integer> avalibleSymbols;

    ClientSocketThread(String hostAdress, int portNumber, Controller theParent)  {
        setPortNumber(portNumber);
        setHostAdress(hostAdress);
        parent = theParent;
    }

    public void run() {
        if(CreateConnection()) try {
            parent.debugText.appendText("Connection established. ");
            WriteToSocket(JsonDataWraper("userInfo", new JSONObject()));
            ThreadMainLoop();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        else {
            parent.disconnectButton.setDisable(true);
            parent.connectButton.setDisable(false);
        }
    }
    private void ThreadMainLoop() {
        try {
            while(socketHolder != null && socketHolder.isConnected()) {
                ReadFromSocket();
            }
        } catch(Exception e) {
            parent.debugText.appendText("INFO: Socket closing connection... ");
        }
        parent.debugText.appendText("INFO: Exiting main loop. OK!\n");
    }

    boolean WriteToSocket(JSONObject theRequest) {
        if(theRequest!=null && !theRequest.isEmpty()) {
            try {
                bw.write(theRequest.toString());
                bw.newLine();
                bw.flush();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void ReadFromSocket() throws Exception {
        String temp = br.readLine();
        System.out.println(temp);
        if(temp != null)    // If we will read null, that means the socket was disconnected
        {
            JSONObject respObject = new JSONObject(temp);
            if(respObject.optString("cmd", "error").compareTo("serverInfo")==0
            && respObject.has("data") )
            {
                JSONObject unwraped = respObject.optJSONObject("data");
                setServerType(unwraped.optString("type", "unknown"));
                setServerUser(unwraped.optString("user", "unknown"));
                setServerVersion(unwraped.optString("ver", "unknown"));
                if(unwraped.has("data") && !unwraped.isNull("data"))
                {
                    JSONObject fdata = unwraped.getJSONObject("data");
                    if(fdata.optInt("size", -1)>0 &&
                        fdata.has("namesArray") && fdata.has("argumentsArray"))
                    {
                        avalibleSymbols = new LinkedHashMap<String,Integer>();
                        for (int i=0; i<fdata.optInt("size", -1); i++) {
                            avalibleSymbols.put(fdata.getJSONArray("namesArray").getString(i), fdata.getJSONArray("argumentsArray").getInt(i));    // is there a clearer way?
                        }
                        System.out.println(getServerUser() + " : serwer wita i informuje że obsluguje nastepujace funkcje matematyczne:");
                        System.out.println(avalibleSymbols);
                        System.out.println("Nalezy to rozumiec jako NazwaFunkcji=1 oznacza, że funkcja przyjmuje postac z 1 arg. NazwaFunkcji(a) ");

                        parent.debugText.appendText("\n" + getServerUser() + " : serwer wita i informuje że obsluguje nastepujace funkcje matematyczne: \n");
                        parent.debugText.appendText(avalibleSymbols.toString());
                        parent.debugText.appendText("\nNalezy to rozumiec jako NazwaFunkcji=1 oznacza, że funkcja przyjmuje postac z 1 arg. NazwaFunkcji(a) \n");
                    }
                }
            }
            else if(respObject.optString("cmd", "error").compareTo("streamData")==0 &&
                    respObject.optBoolean("success", false) && respObject.has("data"))
            {
                JSONObject data;
                data = respObject.getJSONObject("data");
                int size = data.optInt("streamSize", -1);
                data = new JSONObject();
                data.put("cmd", "startSending");
                data.put("success", true);

                if(WriteToSocket(data) && size != -1) {
                    ReadImage(size);
                }
            }
        } else {
            socketHolder.close();
            return;
        }
    }

    private boolean ReadImage(int theSize) {
        try {
            parent.progressBar.setProgress(0.05);
            InputStream is;
            ByteArrayInputStream byteArrayInputStream;
            int bytesLoaded, bytesRead, bytesAvalible;

            parent.debugText.appendText("Image generated, size: " + theSize + " Kb. Reading. \n");

            is = socketHolder.getInputStream();
            byte[] imageBuffer = new byte[theSize];
            bytesLoaded = 0;

            while(socketHolder.isConnected() && bytesLoaded < theSize) {
                if(is.available() > 0) {
                    bytesAvalible = is.available();
                    bytesRead = is.read(imageBuffer, bytesLoaded, bytesAvalible);
                    if(bytesRead != -1) {
                        bytesLoaded += bytesRead;
                        parent.progressBar.setProgress(bytesLoaded/(double) theSize);
                    }
                }
            }
            byteArrayInputStream = new ByteArrayInputStream(imageBuffer);
            BufferedImage downloadedImage = ImageIO.read(byteArrayInputStream);
            ImageIO.write(downloadedImage, "jpg", new File(System.getProperty("user.dir") + "\\theGraphFile.jpg"));

            parent.theCanvas.setImage(SwingFXUtils.toFXImage(downloadedImage, null));
            parent.sendButtonsSetter(false);
            return true;
        } catch (Exception e) {
            System.out.println("RI:ERROR! " + e.toString()) ;
            parent.sendButtonsSetter(false);
            return false;
        }
    }
    static public JSONObject JsonDataWraper(String command, JSONObject data) {
        long idGen = System.currentTimeMillis();
        JSONObject jsonTemplate = new JSONObject();
        jsonTemplate.put("id", idGen);
        jsonTemplate.put("cmd", command);
        if(command.compareTo("userInfo")==0) {
            JSONObject dataTemplate = new JSONObject();
            dataTemplate.put("user", "fiooodooor");
            dataTemplate.put("org", "UMK_Torun");
            dataTemplate.put("type", "ClientSocket");
            dataTemplate.put("ver", "1000");
            jsonTemplate.put("data", dataTemplate);
            return jsonTemplate;
        } else if(command.compareTo("generate")==0) {
            jsonTemplate.put("data", data);
            return jsonTemplate;
        }
        return null;
    }

    private boolean CreateConnection() {
        InetSocketAddress addressHolder;
        try {
            socketHolder = new Socket();
            addressHolder = new InetSocketAddress(getHostAdress(), getPortNumber());
            socketHolder.connect(addressHolder, 4000);
            if(socketHolder.isConnected()) {
                bw = new BufferedWriter(new OutputStreamWriter(socketHolder.getOutputStream()));
                br = new BufferedReader(new InputStreamReader(socketHolder.getInputStream()));
            }
            return true;
        } catch (Exception e) {
            parent.debugText.appendText("Error: Could not create new connection. Try again.\n");
            parent.debugText.appendText("Error: " + e.getMessage() + "\n");
            return false;
        }
    }

    boolean isConnected() {
        return this.socketHolder.isConnected();
    }
    void disconnnect() {
        try {
            System.out.println("Disconnecting...\n");
            this.socketHolder.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.socketHolder = null;
    }

    private int getPortNumber() {
        return portNumber;
    }
    private void setPortNumber(int thePortNumber) {
        this.portNumber = thePortNumber;
    }
    private String getHostAdress() {
        return hostAdress;
    }
    private void setHostAdress(String theHostAdress) {
        this.hostAdress = theHostAdress;
    }
    public String getServerType() {
        return serverType;
    }
    private void setServerType(String theServerType) {
        this.serverType = theServerType;
    }
    public String getServerUser() {
        return serverUser;
    }
    private void setServerUser(String theServerUser) {
        this.serverUser = theServerUser;
    }
    public String getServerVersion() {
        return serverVersion;
    }
    private void setServerVersion(String theServerVersion) {
        this.serverVersion = theServerVersion;
    }
}
