import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

public class main {
    // Encrypt or Decrypt file using AES
    static void operateAES(String key, boolean isEncrypt) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.showOpenDialog(null);
        File file = fileChooser.getSelectedFile();

        try {
            // Generate AES key
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");

            // Initialize cipher for encryption or decryption
            Cipher cipher = Cipher.getInstance("AES");
            if (isEncrypt) {
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, secretKey);
            }

            // Read file data
            FileInputStream fis = new FileInputStream(file);
            byte[] fileData = fis.readAllBytes();
            fis.close();

            // Perform encryption or decryption
            byte[] outputData = cipher.doFinal(fileData);

            // Write back the processed data
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(outputData);
            fos.close();

            JOptionPane.showMessageDialog(null, "Operation completed!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("Hello world");
        JFrame frame = new JFrame();
        frame.setTitle("Image Operation");
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Font
        Font font = new Font("Roboto", Font.BOLD, 20);

        // Buttons
        JButton encryptButton = new JButton("Encrypt Image");
        encryptButton.setFont(font);

        JButton decryptButton = new JButton("Decrypt Image");
        decryptButton.setFont(font);

        // Text field
        JTextField keyField = new JTextField(16); // AES key must be 16 characters (128 bits)
        keyField.setFont(font);

        encryptButton.addActionListener(e -> {
            System.out.println("Encrypt button clicked");
            String key = keyField.getText();
            if (key.length() != 16) {
                JOptionPane.showMessageDialog(null, "Key must be 16 characters long!");
                return;
            }
            operateAES(key, true);
        });

        decryptButton.addActionListener(e -> {
            System.out.println("Decrypt button clicked");
            String key = keyField.getText();
            if (key.length() != 16) {
                JOptionPane.showMessageDialog(null, "Key must be 16 characters long!");
                return;
            }
            operateAES(key, false);
        });

        frame.setLayout(new FlowLayout());
        frame.add(new JLabel("Enter AES Key:"));
        frame.add(keyField);
        frame.add(encryptButton);
        frame.add(decryptButton);

        frame.setVisible(true);
    }
}
