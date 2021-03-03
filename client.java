package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

public class client {
    public static void main(String[] args) throws IOException {
            gui g=new gui();
            g.nameinput();
    }
}
class gui extends JPanel implements Runnable {
    private static JTextArea l1;
    private final static  String nl="\n";
    static Socket s;
    static DataInputStream din;
    static String name;
    static String pat;
    static Socket imso;
    public void nameinput(){
        JFrame fn = new JFrame();
        JButton bu = new JButton("enter");
        bu.setSize(80, 20);
        JTextField tf = new JTextField();
        tf.setSize( 50, 20);
        tf.setBackground(Color.BLUE);
        JLabel l=new JLabel("*enter your name you want people to know you as!!*");
        JPanel pa=new JPanel(new BorderLayout());
        pa.add(bu,BorderLayout.NORTH);
        pa.add(tf,BorderLayout.CENTER);
        pa.add(l,BorderLayout.SOUTH);
        fn.add(pa);
        fn.setVisible(true);
        fn.setSize(600, 200);
        bu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                name = tf.getText();
                if (!name.equals("")) {
                    gui g=new gui();
                    try {
                        g.ui(name);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    fn.dispose();
                }
                else{
                    JOptionPane.showMessageDialog(fn, "Please enter a valid name!", "Swing Tester", JOptionPane.ERROR_MESSAGE);
                    tf.setText("");
                }
            }
        });
    }
    public void ui(String name) throws IOException {
        Socket s=new Socket("localhost",8080);
        DataInputStream din=new DataInputStream(s.getInputStream());
        gui.s =s;
        gui.din =din;
        gui g=new gui();
        Thread th=new Thread(g);
        th.start();
        JFrame frame = new JFrame(name);
        //frame
        frame.setLayout(new GridLayout());
        JButton sendb = new JButton("enter");
        //send button -sendb
        JButton imgb=new JButton("send image");
        //image send button -imgb
        JTextField tyour=new JTextField();
        //text field for input of massage -tyour
        tyour.setBounds(10,10,30,30);
        JPanel viewpanel=new JPanel();
        //panel input text and show client text
        viewpanel.setBorder(new TitledBorder(new EtchedBorder(),"your text"));
        l1=new JTextArea(5,20);
        l1.setEditable(false);
        JScrollPane scrollPane=new JScrollPane(l1);
        //scrollbar
        scrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        writer w=new writer();
        w.writere(frame);
        //contains the chat reading instance
        imso=new Socket("localhost",8081);
            sendb.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String d = tyour.getText();
                    if(!d.equals("")) {
                        try {
                            String m=name+">>"+d;
                            wri(m, s);
                            //writes the massages
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        l1.append(d + nl);
                        tyour.setText("");
                    }
                }
            });
            //send image button functionality
            imgb.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    float FACTOR=1;
                    try {
                        FileDialog fd = new FileDialog(new JFrame());
                        fd.setVisible(true);
                        File[] f = fd.getFiles();
                        if (f.length > 0) {
                            pat = fd.getFiles()[0].getAbsolutePath();
                        }
                        BufferedImage img = ImageIO.read(new File(pat));
                        int scaleX = (int) (img.getWidth() * FACTOR);
                        int scaleY = (int) (img.getHeight() * FACTOR);
                        Image image = img.getScaledInstance(scaleX, scaleY, Image.SCALE_SMOOTH);
                        BufferedImage buffered = new BufferedImage(scaleX, scaleY, 1);
                        buffered.getGraphics().drawImage(image, 0, 0, null);
                        System.out.println(buffered);
                        JFrame msgwimg=new JFrame();
                        msgwimg.setSize(100,200);
                        JTextField msgwimgte=new JTextField();
                        msgwimgte.setSize(20,80);
                        JButton msgwimgbu=new JButton("enter");
                        msgwimgbu.setSize(20,80);
                        msgwimg.setVisible(true);
                        msgwimg.add(msgwimgte);
                        msgwimg.add(msgwimgbu);
                        //image sending
                        msgwimgbu.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                String msgforimg=msgwimgte.getText();
                                try {
                                    ImageIO.write(buffered,"PNG",imso.getOutputStream());
                                    msgwimg.dispose();
                                } catch (IOException ioException) {
                                    ioException.printStackTrace();
                                }
                            }
                        });
                    }
                    catch (IOException ex){
                        ex.printStackTrace();
                    }
                }
            });
            imread threader=new imread(imso,name);
            Thread tr=new Thread(threader);
            tr.start();
            viewpanel.add(scrollPane,BOTTOM_ALIGNMENT);
            viewpanel.add(sendb,BOTTOM_ALIGNMENT);
            viewpanel.add(tyour,CENTER_ALIGNMENT);
            viewpanel.add(imgb);
            frame.add(viewpanel,BorderLayout.SOUTH);
            frame.setVisible(true);
            frame.setSize(400, 400);
            frame.setResizable(true);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
    static void wri(String d,Socket s) throws IOException {
        DataOutputStream dou=new DataOutputStream(s.getOutputStream());
        dou.writeUTF(d);
    }
    //reading massages of client
    @Override
    public void run() {
        while(true) {
            try {
                String ee = din.readUTF();
                writer.main(ee);
                //reads the massage and sends to write it in chat reader(l1)
                System.out.println(ee);
            } catch (IOException ioException) {
                ioException.printStackTrace();
                break;
            }
        }
    }
}
//image reading
class imread implements Runnable{
    static Socket imso;
    static String name;
    public imread(Socket imso,String name){
        imread.imso =imso;
        imread.name=name;
    }
    @Override
    public void run() {
        while(true){
            BufferedImage img;
            try {
                    img = ImageIO.read(ImageIO.createImageInputStream(imso.getInputStream()));
                    if (img != null) {
                        System.out.println("works");
                        JFrame frame = new JFrame("user "+name);
                        JLabel im = new JLabel(new ImageIcon(img));
                        frame.getContentPane().add(im);
                        frame.pack();
                        frame.setVisible(true);
                        //ImageIO.write(img,"png", new File("D:\\"));
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}

