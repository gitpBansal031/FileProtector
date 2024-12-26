import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class main extends JFrame {
    private JTextField keyField;
    private JLabel imageLabel;
    private JPanel filesPanel;
    private File[] selectedFiles;
    private JRadioButton encryptButton;
    private JRadioButton decryptButton;
    private JCheckBox overrideCheckBox;
    private JProgressBar progressBar;
    private JDialog progressDialog;

    public main() {
        setTitle("Image Search GUI");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());

        JLabel keyLabel = new JLabel("Key:");
        keyField = new JTextField(16);
        JButton randomKeyButton = new JButton("Randomize Key");
        JButton selectFilesButton = new JButton("Select Files");
        JButton operateButton = new JButton("Operate");

        encryptButton = new JRadioButton("Encrypt");
        decryptButton = new JRadioButton("Decrypt");
        ButtonGroup group = new ButtonGroup();
        group.add(encryptButton);
        group.add(decryptButton);
        encryptButton.setSelected(true);

        overrideCheckBox = new JCheckBox("Override Existing Files");

        filesPanel = new JPanel();
        filesPanel.setLayout(new FlowLayout());

        topPanel.add(keyLabel);
        topPanel.add(keyField);
        topPanel.add(randomKeyButton);
        topPanel.add(selectFilesButton);
        topPanel.add(encryptButton);
        topPanel.add(decryptButton);
        topPanel.add(overrideCheckBox);
        topPanel.add(operateButton);

        add(topPanel, BorderLayout.NORTH);
        add(filesPanel, BorderLayout.CENTER);

        imageLabel = new JLabel();
        add(new JScrollPane(imageLabel), BorderLayout.EAST);

        randomKeyButton.addActionListener(e -> keyField.setText(generateRandomKey()));

        selectFilesButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                selectedFiles = fileChooser.getSelectedFiles();
                displaySelectedFiles();
            }
        });

        operateButton.addActionListener(e -> {
            String key = keyField.getText();
            if (key.length() != 16) {
                JOptionPane.showMessageDialog(null, "Key must be 16 characters long", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            showProgressDialog();
            new Thread(() -> operate(key, encryptButton.isSelected(), overrideCheckBox.isSelected())).start();
        });
    }

    private void displaySelectedFiles() {
        filesPanel.removeAll();
        for (File file : selectedFiles) {
            JLabel fileLabel = new JLabel(file.getName());
            if (file.isDirectory()) {
                fileLabel.setIcon(UIManager.getIcon("FileView.directoryIcon"));
            } else {
                fileLabel.setIcon(UIManager.getIcon("FileView.fileIcon"));
            }
            filesPanel.add(fileLabel);
        }
        filesPanel.revalidate();
        filesPanel.repaint();
    }

    private void displayImage(File file) {
        try {
            ImageIcon imageIcon = new ImageIcon(file.getAbsolutePath());
            imageLabel.setIcon(imageIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void operate(String key, boolean encrypt, boolean override) {
        try {
            byte[] keyBytes = key.getBytes();
            int totalFiles = countFiles(selectedFiles);
            int processedFiles = 0;

            for (File file : selectedFiles) {
                if (file.isDirectory()) {
                    File[] filesInDir = file.listFiles();
                    if (filesInDir != null) {
                        for (File f : filesInDir) {
                            processFile(f, keyBytes, encrypt, override);
                            processedFiles++;
                            updateProgress(processedFiles, totalFiles);
                        }
                    }
                } else {
                    processFile(file, keyBytes, encrypt, override);
                    processedFiles++;
                    updateProgress(processedFiles, totalFiles);
                }
            }

            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(progressDialog, "Operation completed successfully");
                progressDialog.dispose();
            });
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(progressDialog, "Decryption failed: Incorrect key", "Error", JOptionPane.ERROR_MESSAGE);
                progressDialog.dispose();
            });
            e.printStackTrace();
        }
    }

    private int countFiles(File[] files) {
        int count = 0;
        for (File file : files) {
            if (file.isDirectory()) {
                File[] filesInDir = file.listFiles();
                if (filesInDir != null) {
                    count += filesInDir.length;
                }
            } else {
                count++;
            }
        }
        return count;
    }

    private void processFile(File file, byte[] keyBytes, boolean encrypt, boolean override) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[fis.available()];
        fis.read(data);
        fis.close();

        byte[] result;
        if (encrypt) {
            result = encrypt(data, keyBytes);
        } else {
            result = decrypt(data, keyBytes);
        }

        File outputFile = file;
        if (!override) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(file.getParent(), "output_" + file.getName()));
            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                outputFile = fileChooser.getSelectedFile();
                // Ensure the correct file extension
                if (!outputFile.getName().contains(".")) {
                    String extension = getFileExtension(file);
                    outputFile = new File(outputFile.getAbsolutePath() + extension);
                }
            }
        }

        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(result);
        fos.close();
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }

    private void updateProgress(int processedFiles, int totalFiles) {
        int progress = (int) ((double) processedFiles / totalFiles * 100);
        SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
    }

    private void showProgressDialog() {
        progressDialog = new JDialog(this, "Progress", true);
        progressDialog.setLayout(new BorderLayout());
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(Color.BLUE);
        progressDialog.add(progressBar, BorderLayout.CENTER);
        progressDialog.setSize(300, 100);
        progressDialog.setLocationRelativeTo(this);

        // Create a new thread to update the progress bar
        new Thread(() -> {
            for (int i = 0; i <= 100; i++) {
                final int progress = i;
                SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
                try {
                    Thread.sleep(50); // Adjust the sleep time for smoother animation
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            SwingUtilities.invokeLater(() -> progressDialog.dispose());
        }).start();

        progressDialog.setVisible(true);
    }

    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    public static String generateRandomKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128); // 128-bit key
            SecretKey secretKey = keyGen.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded()).substring(0, 16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ImageSearchGUI().setVisible(true));
    }
}