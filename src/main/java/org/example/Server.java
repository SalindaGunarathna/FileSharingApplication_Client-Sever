package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    public static ArrayList<MyFile> myFiles = new ArrayList<>();

    public static void main(String[] args) throws IOException {


        int fileId  = 0;
        JFrame jFrame = new JFrame("Server");
        jFrame.setSize(500, 500);
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.X_AXIS));
        jPanel.setBorder(BorderFactory.createEmptyBorder(70, 0, 10, 0));
        jPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JScrollPane jScrollPane = new JScrollPane(jPanel);
        jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        JLabel jlTitle = new JLabel("File Receiver");
        jlTitle.setFont(new Font("Arial", Font.BOLD, 30));
        jlTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);


        jFrame.add(jlTitle);
        jFrame.add(jScrollPane);
        jFrame.setVisible(true);

        ServerSocket serverSocket = new ServerSocket(1234);

        while (true) {

            Socket socket = serverSocket.accept();


            try {

                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                int fileNameLength = dataInputStream.readInt();
                if(fileNameLength != 0){
                    byte[] fileNameBytes = new byte[fileNameLength];

                    dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);


                    String fileName = new String(fileNameBytes);

                    int fileContentLength = dataInputStream.readInt();

                    if (fileContentLength > 0) {

                        byte[] fileContentBytes = new byte[fileContentLength];

                        dataInputStream.readFully(fileContentBytes, 0, fileContentBytes.length);

                        JPanel jpfileRow = new JPanel();
                        jpfileRow.setLayout(new BoxLayout(jpfileRow, BoxLayout.X_AXIS));

                        JLabel jlFileName = new JLabel(fileName);
                        jlFileName.setFont(new Font("Arial", Font.BOLD, 20));
                        jpfileRow.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));


                        if(getFileExtension(fileName).equalsIgnoreCase("txt")){
                            jpfileRow.setName(String.valueOf(fileId));
                            jpfileRow.addMouseListener(getMyMouseListener());

                            jpfileRow.add(jlFileName);
                            jpfileRow.add(jpfileRow);
                            jFrame.validate();
                        }else {

                            jpfileRow.setName(String.valueOf(fileId));
                            jpfileRow.addMouseListener(getMyMouseListener());
                            jpfileRow.add(jlFileName);
                            jpfileRow.add(jpfileRow);
                            jFrame.validate();
                        }

                        myFiles.add(new MyFile(fileId, fileName, fileContentBytes, getFileExtension(fileName)));
                    }



                }

            }catch (IOException e){
                e.printStackTrace();

            }



        }





    }

    private static MouseListener getMyMouseListener() {

        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JPanel jPanel = (JPanel) e.getSource();
                int  fileId = Integer.parseInt(jPanel.getName());
                for (MyFile myFile : myFiles) {
                    if (myFile.getId() == fileId) {
                        JFrame jfPreview =  createFremae(myFile.getName(),myFile.getData(),myFile.getFileExtension());
                        jfPreview.setVisible(true);

                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        };

}


    private static String getFileExtension(String fileName) {

        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex >0) {
            return fileName.substring(dotIndex + 1);
        }else {
            return "No file extension found";
        }

    }

    private static JFrame createFremae(String fileName,byte[] fileData,String fileExtension) {

        JFrame jFrame = new JFrame("File Downloader");
        jFrame.setSize(400, 400);
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.X_AXIS));

        JLabel jlTitle = new JLabel("File Downloader");
        jlTitle.setFont(new Font("Arial", Font.BOLD, 30));
        jlTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel jIPrompt = new JLabel("Are you sure you want to download: " + fileName);
        jIPrompt.setFont(new Font("Arial", Font.BOLD, 20));
        jIPrompt.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        jIPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton jbYes = new JButton("Yes");
        jbYes.setPreferredSize(new Dimension(150, 75));
        jbYes.setFont(new Font("Arial", Font.BOLD, 20));


        JButton jbNo = new JButton("No");
        jbNo.setPreferredSize(new Dimension(150, 75));
        jbNo.setFont(new Font("Arial", Font.BOLD, 20));


        JLabel jlFileContent = new JLabel();
        jlFileContent.setFont(new Font("Arial", Font.BOLD, 20));
        jlFileContent.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        jlFileContent.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel jpButtons = new JPanel();
        jpButtons.setLayout(new BoxLayout(jpButtons, BoxLayout.X_AXIS));
        jpButtons.setBorder(BorderFactory.createEmptyBorder(70, 0, 10, 0));
        jpButtons.setAlignmentX(Component.CENTER_ALIGNMENT);

        jpButtons.add(jbYes);
        jpButtons.add(jbNo);

        if (fileExtension.equalsIgnoreCase("txt")) {
            jlFileContent.setText("<html>" + new String(fileData) + "</html>");
        } else {
            jlFileContent.setIcon(new ImageIcon(fileData));
        }

        jbYes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File fileToDownload = new File(fileName);
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);
                    fileOutputStream.write(fileData);
                    fileOutputStream.close();
                    jFrame.dispose();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }

        });

        jbNo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jFrame.dispose();
            }
        });

        jPanel.add(jlTitle);
        jPanel.add(jIPrompt);
        jPanel.add(jlFileContent);
        jPanel.add(jpButtons);
        jFrame.add(jPanel);
        return jFrame;

        }
}
