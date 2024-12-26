import javax.swing.*;
import java.awt.*;
import java.io.*;
public class main {
    static void operate(int key){
        JFileChooser fileChooser=new JFileChooser();
        fileChooser.showOpenDialog(null);
        File file=fileChooser.getSelectedFile();
        try{
            FileInputStream fis=new FileInputStream(file);
            byte[] data=new byte[fis.available()];
            fis.read(data);
            int i=0;
            for(byte b:data){
                System.out.println(b);
                data[i]=(byte)(b^key);
                i++;
            }
            FileOutputStream fos=new FileOutputStream(file);
            fos.write(data);
            fos.close();
            fis.close();
            JOptionPane.showMessageDialog(null,"Done");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        System.out.println("Hello world");
        JFrame frame=new JFrame();
        frame.setTitle("Image Operation");
        frame.setSize(400,400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //font
        Font font=new Font("Roboto",Font.BOLD,25);

        //button
        JButton button=new JButton();   
        button.setText("Open image");
        button.setFont(font);

        //text field
        JTextField textField=new JTextField(10);
        textField.setFont(font);

        button.addActionListener(e->{
            System.out.println("Button clicked");
            String str=textField.getText();
            int key=Integer.parseInt(str);
            operate(key);
        });

        frame.setLayout(new FlowLayout());
        frame.add(button);
        frame.add(textField);

        frame.setVisible(true);
    }
}  
