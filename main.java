
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
    private File selectedFile;
    private JRadioButton encryptButton;
    private JRadioButton decryptButton;
    private JCheckBox overrideCheckBox;

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
        JButton selectFileButton = new JButton("Select File");
        JButton operateButton = new JButton("Operate");
        JButton loadKeyButton = new JButton("Load Key");

        encryptButton = new JRadioButton("Encrypt");
        decryptButton = new JRadioButton("Decrypt");
        ButtonGroup group = new ButtonGroup();
        group.add(encryptButton);
        group.add(decryptButton);
        encryptButton.setSelected(true);

        overrideCheckBox = new JCheckBox("Override Existing File");

        topPanel.add(keyLabel);
        topPanel.add(keyField);
        topPanel.add(randomKeyButton);
        topPanel.add(selectFileButton);
        topPanel.add(encryptButton);
        topPanel.add(decryptButton);
        topPanel.add(overrideCheckBox);
        topPanel.add(operateButton);
        topPanel.add(loadKeyButton);

        add(topPanel, BorderLayout.NORTH);

        imageLabel = new JLabel();
        add(new JScrollPane(imageLabel), BorderLayout.CENTER);

        randomKeyButton.addActionListener(e -> keyField.setText(generateRandomKey()));

        selectFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                displayImage(selectedFile);
            }
        });

        operateButton.addActionListener(e -> {
            String key = keyField.getText();
            if (key.length() != 16) {
                JOptionPane.showMessageDialog(null, "Key must be 16 characters long", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            operate(key, encryptButton.isSelected(), overrideCheckBox.isSelected());
            displayImage(selectedFile);
        });

        loadKeyButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File keyFile = fileChooser.getSelectedFile();
                try (BufferedReader br = new BufferedReader(new FileReader(keyFile))) {
                    keyField.setText(br.readLine());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
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
            FileInputStream fis = new FileInputStream(selectedFile);
            byte[] data = new byte[fis.available()];
            fis.read(data);
            fis.close();

            byte[] result;
            byte[] keyBytes = key.getBytes();
            if (encrypt) {
                result = encrypt(data, keyBytes);
                saveKeyToFile(key);
            } else {
                result = decrypt(data, keyBytes);
            }

            File outputFile = selectedFile;
            if (!override) {
                JFileChooser saveFileChooser = new JFileChooser();
                saveFileChooser.setSelectedFile(new File(selectedFile.getParent(), "output_" + selectedFile.getName()));
                if (saveFileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    outputFile = saveFileChooser.getSelectedFile();
                }
            }

            FileOutputStream fos = new FileOutputStream(outputFile);
            fos.write(result);
            fos.close();

            JOptionPane.showMessageDialog(null, "Done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveKeyToFile(String key) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("key.txt"));
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File keyFile = fileChooser.getSelectedFile();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(keyFile))) {
                bw.write(key);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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