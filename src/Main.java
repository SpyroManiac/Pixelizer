import log.LogCreator;
import sun.security.util.Debug;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import static java.lang.Thread.sleep;


public class Main {
    private static boolean pixelizer = true;
    private static String pathString;
    private static String sourceString;
    private static JFrame frame;
    private static JTextField sourceField;
    private static JTextField pathField;
    private static JButton sourceSearchButton;
    private static JButton executeButton;
    private static JCheckBox replaceBox;
    private static JLabel sourceImgLabel;
    private static final LogCreator logCreator = new LogCreator();

    public static void main(String[] args) {
        makeFrame();
    }

    public static void makeFrame() {
        ImageIcon img = new ImageIcon(Objects.requireNonNull(Main.class.getResource("Img/Icon.png")));

        frame = new JFrame("pixelizer");
        frame.setIconImage(img.getImage());
        frame.setSize(500, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        JButton pathSearchButton;
        JLabel pathLabel;

        JMenuBar mb = new JMenuBar();
        JMenu m1 = new JMenu("Mode");
        JMenu m2 = new JMenu("Help");
        logCreator.setIconImage(img.getImage());

        JMenuItem m11 = new JMenuItem(new AbstractAction("pixelizer") {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh(frame, true);
            }
        });
        JMenuItem m12 = new JMenuItem(new AbstractAction("Replacer") {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh(frame, false);
            }
        });

        JMenuItem m21 = new JMenuItem(new AbstractAction("Help") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog jDialog = newDialog(1050, 410, "Help");
                jDialog.setResizable(false);
                JLabel helpDialog = new JLabel("<html><h2>&nbsp !!WARNING!!</H2>" +
                        "&nbsp &nbsp &nbsp This program will go through the source folder, and any subfolders inside that source folder, and either pixelizes or replaces the images inside said folder.<br>" +
                        "&nbsp &nbsp &nbsp this process can not be undone, please be sure you select the right folder, and that you have backups of said folder before you start." +
                        "<h2>&nbsp selecting modes</H2>" +
                        "&nbsp &nbsp &nbsp You can change modes between the pixelizer and the replacer by going into the mode menu<br>" +
                        "&nbsp &nbsp &nbsp \"mode > pixelizer\" for the pixelizer<br>" +
                        "&nbsp &nbsp &nbsp \"mode > replacer\" for the replacer<br>" +
                        "<H2>&nbsp Pixelizer</H2>" +
                        "&nbsp &nbsp &nbsp To start, select the source folder you want to start pixelizing from.<br>" +
                        "&nbsp &nbsp &nbsp The pixelizer will load in all images, read the value of every pixel from said images, and generate and pixel image that is the average of the RGB values of said image<br>" +
                        "&nbsp &nbsp &nbsp If you checked the replace image folder, it will replace the source images with the newly created pixel image, otherwise it will make a copy called (your original picture_pixel.png)<br>" +
                        "<H2>&nbsp Replacer</h2>" +
                        "&nbsp &nbsp &nbsp To start, select a source folder you want to replace all images from.<br>" +
                        "&nbsp &nbsp &nbsp Then select the source image you want to replace the others with.<br>" +
                        "&nbsp &nbsp &nbsp It will then go through all folders and replace the images inside with the source image." +
                        "</html>");
                jDialog.add(helpDialog);
                jDialog.setVisible(true);

            }
        });
        JMenuItem m22 = new JMenuItem(new AbstractAction("About") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog jDialog = newDialog(400, 150, "About");
                jDialog.setResizable(false);
                JLabel about = new JLabel("<html><h2>This program was brought to you by <b>Kage</b></h2><html>");
                about.setForeground(new Color(138, 60, 220));
                about.setBounds(10, 10, 600, 30);
                JLabel twitter = new JLabel("twitter: ");
                twitter.setBounds(10, 35, 300, 30);
                JLabel twitterLink = Hyperlink("TokageArt", "https://twitter.com/TokageArt");
                twitterLink.setBounds(50, 35, 300, 30);
                JLabel email = new JLabel("Email: kagehikaruart@gmail.com");
                email.setBounds(10, 55, 300, 30);
                JLabel version = new JLabel("Version: 1.1.3");
                version.setBounds(10, 75, 300, 30);

                jDialog.setLayout(null);
                jDialog.add(about);
                jDialog.add(twitter);
                jDialog.add(twitterLink);
                jDialog.add(email);
                jDialog.add(version);
                jDialog.setVisible(true);
            }
        });

        m1.add(m11);
        m1.add(m12);
        mb.add(m1);

        m2.add(m21);
        m2.add(m22);
        mb.add(m2);
        mb.setBounds(0, 0, 500, 20);

        // label target
        pathLabel = new JLabel("target folder:");
        pathLabel.setBounds(10, 30, 100, 30);

        // path textfield
        pathField = new JTextField("target folder");
        pathField.setBounds(105, 30, 340, 30);

        // path search button
        pathSearchButton = new JButton(new AbstractAction("...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userDir = System.getProperty("user.home");
                final JFileChooser fc = new JFileChooser(userDir + "/Desktop");
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int response = fc.showOpenDialog(null);
                if (response == JFileChooser.APPROVE_OPTION) {
                    pathString = fc.getSelectedFile().toString();
                    pathField.setText(pathString);
                }
            }
        });
        pathSearchButton.setBounds(445, 30, 29, 29);

        // label target
        sourceImgLabel = new JLabel("Source Image:");
        sourceImgLabel.setBounds(10, 70, 100, 30);

        // source image textbox
        sourceField = new JTextField("source image");
        sourceField.setBounds(105, 70, 340, 30);

        // source image search button
        sourceSearchButton = new JButton(new AbstractAction("...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userDir = System.getProperty("user.home");
                final JFileChooser fc = new JFileChooser(userDir + "/Desktop");
                fc.setFileFilter(new FileNameExtensionFilter("Image file", "jpg", "jpeg", "png"));
                int response = fc.showOpenDialog(null);
                if (response == JFileChooser.APPROVE_OPTION) {
                    sourceString = fc.getSelectedFile().toString();
                    sourceField.setText(sourceString);
                }
            }
        });
        sourceSearchButton.setBounds(445, 70, 29, 29);

        //replacement checkbox
        replaceBox = new JCheckBox("Replace original?", false);
        replaceBox.setBounds(7, 70, 150, 30);

        //execute button
        executeButton = new JButton(new AbstractAction("Start") {
            @Override
            public void actionPerformed(ActionEvent e) {

                Debug.println("Info", "target: " + pathString);
                Debug.println("Info", "source: " + sourceString);
                Debug.println("Info", "pixelizer?: " + pixelizer);
                Debug.println("Info", "Replace: " + replaceBox.isSelected());

                if (pathString == null) {
                    if (new File(pathField.getText()).isDirectory()) {
                        pathString = pathField.getText();
                    } else {
                        JOptionPane.showMessageDialog(null, "Please select a target folder", "warning", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
                if (!pixelizer && sourceString == null) {
                    if (new File(sourceField.getText()).isFile()) {
                        sourceString = sourceField.getText();
                        if (!sourceString.endsWith(".jpg") && !sourceString.endsWith(".jepg") && !sourceString.endsWith(".png")) {
                            JOptionPane.showMessageDialog(null, "Please select a valid image", "warning", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Please select a source image", "warning", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }

                run();
            }
        });
        executeButton.setBounds(374, 70, 100, 30);

        frame.setLayout(null);

        //add all items to frame
        frame.add(pathLabel);
        frame.add(pathField);
        frame.add(pathSearchButton);
        frame.add(replaceBox);
        frame.add(sourceImgLabel);
        frame.add(sourceField);
        frame.add(sourceSearchButton);
        frame.add(executeButton);
        frame.add(mb);

        m11.doClick();

        frame.setVisible(true);
    }

    private static void run() {
        int result = JOptionPane.showConfirmDialog(null, "You are about to change every image in the folder (" + pathString + ").\nThis action can't be stopped, nor reverted!\nAre you sure you want to continue?", "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            Debug.println("Info", "starting program");
            pathString = pathString.replace("\\", "/");


            Thread thread = new Thread(() -> {
                BufferedImage image = null;
                if (sourceString != null) {
                    sourceString = sourceString.replace("\\", "/");
                    try {
                        image = ImageIO.read(new File(sourceString));
                    } catch (IOException ex) {
                        Debug.println("error", ex.getMessage());
                    }
                }

                LogCreator.startCaret();
                try {
                    LogCreator.setLog("");
                    LogCreator.pixelized = 0;
                    logCreator.setVisible(true);
                    logCreator.dig(image, pathString, pixelizer, replaceBox.isSelected());
                    LogCreator.setLog(LogCreator.getLog() + "\n\nEnd of operation!\nFiles affected: " + LogCreator.pixelized);
                    LogCreator.setButtonsEnabled(true);
                    try
                    {
                        sleep(200);
                    }
                    catch (InterruptedException e)
                    {
                        //e.printStackTrace();
                    }
                    LogCreator.scrollMax();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                LogCreator.stopCaret();
            });
            thread.start();


        } else {
            Debug.println("Info", "Operation canceled");
        }
    }

    private static JLabel Hyperlink(String text, String hyperlink) {
        String hyperlinkText = "<html><a href=\"\">" + text + "</a></html>";
        JLabel link = new JLabel(hyperlinkText);
        link.setForeground(Color.BLUE.darker());
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(hyperlink));
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                link.setText(hyperlinkText);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                link.setText(text);
            }
        });
        return link;
    }

    private static JDialog newDialog(int width, int height, String title) {
        JDialog f = new JDialog(frame, title);
        f.setSize(width, height);
        f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        f.setLocationRelativeTo(null);
        return f;
    }

    private static void refresh(JFrame frame, boolean pixelate) {
        pathField.setText("target folder");
        sourceImgLabel.setVisible(!pixelate);
        sourceField.setVisible(!pixelate);
        sourceField.setText("source image");
        sourceSearchButton.setVisible(!pixelate);
        replaceBox.setVisible(pixelate);
        pixelizer = pixelate;
        pathString = null;
        sourceString = null;

        if (pixelate) {
            executeButton.setBounds(374, 70, 100, 30);
            frame.setTitle("Pixelizer");
            frame.setSize(500, 150);
        } else {
            executeButton.setBounds(374, 110, 100, 30);
            frame.setTitle("Replacer");
            frame.setSize(500, 200);
        }
    }
}