# FileProtector

FileProtector is a Java-based file encryption and decryption application. It provides a simple and secure way to protect sensitive files by encrypting them and decrypting them when needed. The project features a user-friendly graphical interface and uses robust encryption algorithms for data security.

---

## Features

- **File Encryption**: Securely encrypt files to protect sensitive data.
- **File Decryption**: Easily decrypt encrypted files using the correct key.
- **User-Friendly GUI**: A simple and intuitive graphical interface for ease of use.
- **Cross-Platform**: Developed in Java, making it platform-independent.

---

## Technologies Used

- **Programming Language**: Java (version 19.0.2)
- **GUI Library**: Swing (for user interface design)
- **Encryption Algorithm**: AES (Advanced Encryption Standard)

---

## Prerequisites

- Java 19.0.2 or later installed on your system.

---

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/gitpBansal031/FileProtector.git
2. Navigate to the project directory:
   ```bash
   cd FileProtector
3. Compile the Java files:
   ```bash
   javac -d bin -sourcepath src src/com/fileprotector/Main.java
4. Run the application:
   ```bash
   java -cp bin com.fileprotector.Main
5. The application will open with a graphical user interface (GUI).

6. Use the following steps to interact with the application:
   - **To Encrypt a File**:
     1. Click the "Browse" button to select the file you want to encrypt.
     2. Enter a secure encryption key in the designated field.
     3. Click the "Encrypt" button to encrypt the file.
     4. Save the encrypted file to your desired location.

   - **To Decrypt a File**:
     1. Click the "Browse" button to select the encrypted file.
     2. Enter the same encryption key used during encryption.
     3. Click the "Decrypt" button to decrypt the file.
     4. Save the decrypted file to your desired location.
