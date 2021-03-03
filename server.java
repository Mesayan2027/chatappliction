package com.company;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class server {
    static Vector<Clienthandler> clients=new Vector<>();
    static Vector<image_server> img_clients=new Vector<>();
    public static void main(String[] args) throws IOException {
        server ser =new server();
        ser.start();
    }
    public void start() throws IOException {
        ServerSocket ss = new ServerSocket(8080);
        //principle server
        ServerSocket ims = new ServerSocket(8081);
        //image server
        while(true){
            Socket s = new Socket();
            s = ss.accept();
            DataInputStream din=new DataInputStream(s.getInputStream());
            DataOutputStream dou=new DataOutputStream(s.getOutputStream());
            String logon="a";
            Clienthandler hn=new Clienthandler(din,dou,s,logon);
            clients.add(hn);
            System.out.println(clients);
            Thread t=new Thread(hn);
            t.start();
            Socket is = ims.accept();
            String imlogon="a";
            image_server in=new image_server(is,imlogon);
            Thread t1=new Thread(in);
            t1.start();
            img_clients.add(in);
            System.out.println(img_clients);
        }
    }
}
class Clienthandler implements Runnable {
    private DataInputStream din;
    private DataOutputStream dou;
    private String logon;
    static Socket s;

    public Clienthandler(DataInputStream din, DataOutputStream dou, Socket s, String logon) {
        this.din = din;
        this.dou = dou;
        this.logon = logon;
    }

    @Override
    public void run() {
        String te;
        while (true) {
            try {
                te = din.readUTF();
                System.out.println(te);
                for (Clienthandler mc : server.clients) {
                    System.out.println(1);
                    System.out.println(mc.logon);
                    if (mc.logon.equals("a")) {
                        System.out.println(mc.logon);
                        System.out.println(mc);
                        mc.dou.writeUTF(te);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        try {
            this.din.close();
            this.dou.close();
            this.logon = "b";
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
class image_server implements Runnable{
    private Socket is;
    private String imlogon;

    public image_server(Socket is,String imlogon) throws IOException {
        this.is=is;
        this.imlogon=imlogon;
    }
    @Override
    public void run() {
        while (true) {
            try {
                //System.out.println("msg for img is"+msgforimg);
                BufferedImage img = ImageIO.read(ImageIO.createImageInputStream(is.getInputStream()));
                if(img!=null) {
                    for (image_server ic : server.img_clients) {
                        if (ic.imlogon.equals("a")) {
                            System.out.println(ic);
                            ImageIO.write(img, "PNG", ic.is.getOutputStream());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        this.imlogon = "b";
    }
}
