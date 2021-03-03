package com.company;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class writer implements Runnable{
    private static JTextArea l1;
    private final static String nl = "\n";
    static int i;
    static String msg;
    public static void main(String ee){
         msg=ee;
         writer w=new writer();
         Thread t=new Thread(w);
         t.start();
         //thread for writing massages in text area
    }
    public void writere(JFrame f){
         JPanel viewpanel1 = new JPanel();
         //this panel consists the text from the others
         viewpanel1.setBorder(new TitledBorder(new EtchedBorder(), "your text"));
         l1 = new JTextArea(5, 20);
         //the text area containing massage
         l1.setEditable(false);
         JScrollPane scrollPane = new JScrollPane(l1);
         //scrollpane for scroll
         scrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
         viewpanel1.add(scrollPane);
         f.add(viewpanel1, BorderLayout.NORTH);
         f.setVisible(true);
         f.setSize(400, 400);
         f.setResizable(true);
    }
    @Override
        public void run() {
           l1.append(msg + nl);
           msg=null;
    }
}
