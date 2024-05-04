package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {

    public static void main(String[] args) {

        final File[] fileToSend = new File[1];

        JFrame jFrame = new JFrame("Client");
        jFrame.setSize(500, 500);
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));

        JLabel  jlTitle = new JLabel("File Sender");
        jlTitle.setFont(new Font("Arial", Font.BOLD, 30));
        jlTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel jlFileName = new JLabel("Choose a file to send:");
        jlFileName.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
        jlFileName.setAlignmentX(Component.CENTER_ALIGNMENT);
        jlFileName.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel jpButtons = new JPanel();
        jpButtons.setLayout(new BoxLayout(jpButtons, BoxLayout.X_AXIS));
        jpButtons.setBorder(BorderFactory.createEmptyBorder(70, 0, 10, 0));
        jpButtons.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton jbSendFile = new JButton("Send File");
        jbSendFile.setPreferredSize(new Dimension(150, 75));
        jbSendFile.setFont(new Font("Arial", Font.BOLD, 20));

        JButton jbChooseFile = new JButton("Choose File");
        jbChooseFile.setPreferredSize(new Dimension(150, 75));
        jbChooseFile.setFont(new Font("Arial", Font.BOLD, 20));

        jpButtons.add(jbSendFile);
        jpButtons.add(jbChooseFile);

        jbChooseFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setDialogTitle("Choose File");
                if(jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
                    fileToSend[0] = jFileChooser.getSelectedFile();

                    jlFileName.setText("File name: " + fileToSend[0].getName());

                }
           }


        }
        );

        jbSendFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(fileToSend[0] != null){
                    jlFileName.setText("Pleace Choose File");
                }else{

                    try{
                        FileInputStream fileInputStream = new FileInputStream(fileToSend[0].getAbsolutePath());
                        Socket socket =  new Socket("localhost", 1234);

                        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

                        String fileName = fileToSend[0].getName();
                        byte[] filenameByte = fileName.getBytes();

                        byte[] fileContentsBytes = new byte[(int)fileToSend[0].length()];
                        fileInputStream.read(filenameByte);

                        dataOutputStream.writeInt(filenameByte.length);
                        dataOutputStream.write(filenameByte);

                        dataOutputStream.writeInt(fileContentsBytes.length);
                        dataOutputStream.write(fileContentsBytes);

                    }catch (IOException e1){
                        e1.printStackTrace();


                    }



                }
            }
        });

        jFrame.add(jlTitle);
        jFrame.add(jlFileName);
        jFrame.add(jpButtons);
        jFrame.setVisible(true);






        System.out.println("Hello world!");
    }


}
