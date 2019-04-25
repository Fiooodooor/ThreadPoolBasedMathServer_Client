import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientSocket implements Runnable{
    private Socket socket;
    private BufferedReader br;
    private BufferedWriter bw;
    private LinkedList<String> userInfo;        // user << org << type << ver

    public ClientSocket(Socket theSocket) {
        userInfo = new LinkedList<>();
        this.setSocket(theSocket);
    }

    @Override
    public void run() {
        try {
            setBr(new BufferedReader(new InputStreamReader(socket.getInputStream())));
            setBw(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
            String temp;
            do {
                temp = getBr().readLine();
                if(temp == null)
                {
                    socket.close();
                }
                else
                {
                    messageDecode(temp);
                }
            } while(getSocket() != null && !getSocket().isClosed());
        }
        catch (SocketException e) {
            Logger.getLogger(ClientSocket.class.getName()).log(Level.INFO, "[INFO] Client disconnected." + e.getMessage());
        } catch (IOException e) {
            Logger.getLogger(ClientSocket.class.getName()).log(Level.WARNING, "[WARNING] IOExcecption in client socket. Data maybe lost." + e.getMessage());
        } catch (Exception other) {
            Logger.getLogger(ClientSocket.class.getName()).log(Level.WARNING, "[WARNING] Unknown error in client socket!" + other.getMessage());
        }
    }

    private boolean generateImage(JSONObject data) {
        TheChartGenerator chart = new TheChartGenerator(data);
        BufferedImage img = chart.getRenderedChart();

        try {
            JSONObject streamData, reply;
            float quality = 1;
            String size;
            ImageWriter writer;
            ImageWriteParam param;
            OutputStream os;
            ByteArrayOutputStream byteArrayOutputStream;
            ImageOutputStream ios;

            if(ImageIO.getImageWritersByFormatName("jpg").hasNext())
            {
                writer = ImageIO.getImageWritersByFormatName("jpg").next();

                byteArrayOutputStream = new ByteArrayOutputStream();
                ios = ImageIO.createImageOutputStream(byteArrayOutputStream);
                writer.setOutput(ios);

                param = writer.getDefaultWriteParam();
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(quality);
                writer.write(null, new IIOImage(img, null, null), param);
                size = Integer.toString(byteArrayOutputStream.size());

                streamData = new JSONObject();
                streamData.put("streamSize", size);
                streamData.put("streamType", "byteArrayOutputStream");
                streamData.put("fileType", "image/jpeg");
                streamData.put("compressQuality", quality);
                if(messageEncode("streamData", streamData, true))
                {
                    reply = new JSONObject(this.messageRead());
                    if(!reply.isEmpty() && reply.optString("cmd", "error").compareTo("startSending")==0 )
                    {
                        Logger.getLogger(ClientSocket.class.getName()).log(Level.FINE, "SENDING IMG TO: "
                                                                           + this.getSocket().getInetAddress().toString()
                                                                           + " :: " + byteArrayOutputStream.size() + "kb");
                        os = getSocket().getOutputStream();
                        byteArrayOutputStream.writeTo(os);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(ClientSocket.class.getName()).log(Level.WARNING, "ERROR: Somethin went wrong while sending chart. E:: " + e.toString());
        }
        return false;
    }

    private String messageRead() {
        String temp;
        try {
            temp = this.getBr().readLine();
            return temp;
        } catch (IOException e) {
            Logger.getLogger(ClientSocket.class.getName()).log(Level.WARNING, "Client 'messageRead()' IOException. Message:" + e.getMessage());
        }
        return null;
    }

    private void messageDecode(String data) {
        JSONObject wrapedData = new JSONObject(data);
        if(!wrapedData.isEmpty()) {
            String cmd;
            cmd = wrapedData.optString("cmd", "unknown");
            if(cmd.compareTo("generate") == 0) {
                generateImage(wrapedData.optJSONObject("data"));
            } else if (cmd.compareTo("userInfo") == 0) {
                setUserInfo(wrapedData.optJSONObject("data"));
            } else {
                return;
            }
        }
    }

    private boolean messageWrite(String plainText) {
        try {
            this.getBw().write(plainText);
            this.getBw().newLine();
            this.getBw().flush();
            return true;
        } catch (IOException e) {
            System.err.println(e.toString());
        }
        return false;
    }

    private boolean messageEncode(String cmd, JSONObject data, boolean status) {
        JSONObject wrapper = new JSONObject();
        wrapper.put("cmd", cmd);
        wrapper.put("success", status);
        wrapper.put("data", data);
        return this.messageWrite(wrapper.toString());
    }

    private void setUserInfo(JSONObject data) {
        setUserInfo(data.optString("user", "undefined"));
        setUserInfo(data.optString("org", "undefined"));
        setUserInfo(data.optString("type", "undefined"));
        setUserInfo(data.optString("ver", "undefined"));

        JSONObject wraped_data = new JSONObject();
        JSONArray functionName = new JSONArray();
        JSONArray functionArgs = new JSONArray();

        data = new JSONObject();
        data.put("type", "R->R function drawer");
        data.put("user", "root");
        data.put("ver", "100");

        wraped_data.put("size", SymbolMapper.TOKEN_FUNCTION.values().length);
        for (SymbolMapper.TOKEN_FUNCTION S : SymbolMapper.TOKEN_FUNCTION.values()) {
            functionName.put(S.toString());
            functionArgs.put(S.getVariablesNr());
        }
        wraped_data.put("namesArray", functionName);
        wraped_data.put("argumentsArray", functionArgs);
        data.put("data", wraped_data);

        messageEncode("serverInfo", data, true);
    }

    public Socket getSocket() {
        return socket;
    }
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public BufferedReader getBr() {
        return br;
    }
    public void setBr(BufferedReader br) {
        this.br = br;
    }

    public BufferedWriter getBw() {
        return bw;
    }
    public void setBw(BufferedWriter bw) {
        this.bw = bw;
    }

    public LinkedList<String> getUserInfo() {
        return userInfo;
    }
    public void setUserInfo(String userInfo) {
        this.userInfo.addLast(userInfo);
    }
}