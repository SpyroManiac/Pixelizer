package log;

import pixelizer.Pixelizer;
import sun.security.util.Debug;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultCaret;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogCreator extends javax.swing.JFrame {
    public static final JTextArea area = new JTextArea();
    private static String log = "";
    private static DefaultCaret caret;
    private static JButton exit;
    private static JButton print;
    private static JScrollBar vertical;
    public static int pixelized = 0;
    private static updateThread thread;

    public LogCreator() {
        thread = new updateThread(this);
        this.setTitle("debug log");
        this.setSize(500, 400);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        area.setEditable(false);
        caret = (DefaultCaret) area.getCaret();
        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setBounds(10, 10, 465, 300);
        scrollPane.setAutoscrolls(true);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue( vertical.getMaximum() );

        print = new JButton(new AbstractAction("print log") {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fc = new JFileChooser();
                fc.setFileFilter(new FileNameExtensionFilter("text document", "txt"));
                int response = fc.showSaveDialog(null);
                if (response == JFileChooser.APPROVE_OPTION) {
                    String pathString = fc.getSelectedFile().toString();
                    if(pathString.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Please enter a valid name", "warning", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if(!pathString.endsWith(".txt")) {
                        pathString += ".txt";
                    }
                    Debug.println("debug log: ", pathString);
                    File myObj = new File(pathString);

                    try {
                        FileWriter writer = new FileWriter(myObj);
                        writer.write(area.getText());
                        writer.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                }
            }
        });
        print.setBounds(10,320,100,30);
        print.setEnabled(false);

        exit = new JButton(new AbstractAction("exit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeWindow();
            }
        });
        exit.setBounds(374,320,100,30);
        exit.setEnabled(false);

        this.setLayout(null);
        this.add(scrollPane);
        this.add(print);
        this.add(exit);
        thread.start();
    }

    public static void scrollMax() {
        vertical.setValue(vertical.getMaximum());
    }
    private void closeWindow() {
        area.setText("");
        setButtonsEnabled(false);
        this.setVisible(false);
    }

    public static void setButtonsEnabled(boolean enabled) {
        thread.endThread();
        print.setEnabled(enabled);
        exit.setEnabled(enabled);
    }

    public void dig(BufferedImage source, String path, boolean pixelizer, boolean replace) throws InterruptedException {
        File directoryPath = new File(path);
        File[] filesList = directoryPath.listFiles();

        for (File file : filesList) {
            String imagePath = file.getAbsolutePath();
            if (imagePath.toLowerCase().endsWith(".png") || imagePath.toLowerCase().endsWith(".jpg")) {
                Debug.println("image", imagePath);
                log += "image: " + imagePath + "\n";
                if (pixelizer) {
                    try {
                        long start = System.nanoTime();
                        Pixelizer.getAveragePixel(imagePath, replace);
                        long elapsedTime = System.nanoTime() - start;
                        log += "Time to complete: " + (double) elapsedTime / 1_000_000_000 +"s\n";
                        pixelized++;
                    } catch (IOException e) {
                        log += e.getMessage();
                        throw new RuntimeException(e);
                    }
                } else {
                    File f = new File(imagePath);
                    try {
                        long start = System.nanoTime();
                        long elapsedTime = System.nanoTime() - start;
                        ImageIO.write(source, "PNG", f);
                        log += "Time to complete: " + elapsedTime + "ns\n";
                        pixelized++;
                    } catch (IOException e) {
                        log += e.getMessage();
                        throw new RuntimeException(e);
                    }
                }
            } else {
                if (file.isDirectory()) {
                    Debug.println("folder", imagePath);
                    log += "folder" + imagePath + "\n";
                    dig(source, imagePath, pixelizer,replace);
                } else {
                    Debug.println("error", "Unknown file type");
                    log += "Unknown file type: " + imagePath + "\n";
                }
            }
        }
    }

    public static void stopCaret() {
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
    }

    public static void startCaret() {
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }

    public static String getLog() {
        return log;
    }

    public static void setLog(String newLog) {
        log = newLog;
    }

    public void setText(String text) {
        area.setText(text);
    }
}
