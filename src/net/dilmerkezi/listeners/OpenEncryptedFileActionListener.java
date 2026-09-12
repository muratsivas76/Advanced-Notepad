package net.dilmerkezi.listeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.StyledEditorKit;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * Listener responsible for opening and decrypting .enc files.
 * Uses AES decryption in a background thread and optimized streaming for UI updates.
 * Compatible with Java 6/7/8.
 *
 * Licensed under GPL v3.
 */
public class OpenEncryptedFileActionListener implements ActionListener {
  
  private final JTextPane txt;
  private final JLabel statusLabel;
  private final JFileChooser encjfc;
  private final Component parent;
  
  public OpenEncryptedFileActionListener(JTextPane txt, JLabel statusLabel, JFileChooser encjfc, Component parent) {
    this.txt = txt;
    this.statusLabel = statusLabel;
    this.encjfc = encjfc;
    this.parent = parent;
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    statusLabel.setText("");
    encjfc.setDialogTitle("Open Encrypted File");
    
    // 1. UI interaction (File Chooser) on EDT
    if (encjfc.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
      final File selectedFile = encjfc.getSelectedFile();
      final String fileName = selectedFile.getName();
      
      // Validation check
      if (!fileName.toLowerCase().endsWith(".enc")) {
        JOptionPane.showMessageDialog(parent, "This is not an encrypted file (.enc)!");
        return;
      }
      
      // Ask for password
      final String fullPassword = askForPassword();
      if (fullPassword == null || fullPassword.isEmpty()) return;
      
      // 2. Heavy processing in background thread
      Thread decryptionThread = new Thread(new Runnable() {
          @Override
          public void run() {
            try {
              SecretKey key = getSecretKey(fullPassword);
              
              // Read and decrypt bytes
              byte[] encryptedData = readAllBytes(selectedFile);
              byte[] decryptedData = decrypt(encryptedData, key);
              
              // Convert to String using UTF-8
              final String content = new String(decryptedData, "UTF-8");
              // Use StringReader for efficient streaming into JTextPane
              final StringReader sr = new StringReader(content);
              
              // 3. UI Update on EDT
              SwingUtilities.invokeLater(new Runnable() {
                  @Override
                  public void run() {
                    try {
                      txt.setEditorKit(new StyledEditorKit());
                      
                      /**
                       * PERFORMANCE OPTIMIZATION:
                       * Using txt.read() with a Reader is more efficient than txt.setText().
                       * It prevents long freezes during document rendering.
                       */
                      txt.read(sr, "DecryptedContent");
                      
                      /**
                       * Attach listener after loading.
                       * Prevents the initial load from being stored in the Undo buffer.
                       */
                      if (UndoRedoConstants.ulis != null) {
                        txt.getDocument().addUndoableEditListener(UndoRedoConstants.ulis);
                      }
                      
                      txt.setCaretPosition(0);
                      txt.requestFocus();
                      statusLabel.setText("Opened and Decrypted: " + fileName);
                      
                      } catch (IOException ex) {
                      statusLabel.setText("Error displaying decrypted content.");
                      } finally {
                      sr.close();
                    }
                  }
              });
              
              } catch (final Exception ex) {
              SwingUtilities.invokeLater(new Runnable() {
                  @Override
                  public void run() {
                    JOptionPane.showMessageDialog(parent, "Decryption failed: " + ex.getMessage());
                    statusLabel.setText("Decryption error.");
                  }
              });
            }
          }
      });
      decryptionThread.start();
    }
  }
  
  private String askForPassword() {
    return JOptionPane.showInputDialog(parent, "<html><body><font color=\"blue\" size=\"5\">Enter password for decryption:</font></body></html>");
  }
  
  /**
   * Derives a 128-bit AES key from the password string using SHA-256.
   */
  private SecretKey getSecretKey(String password) throws Exception {
    byte[] key = password.getBytes("UTF-8");
    MessageDigest sha = MessageDigest.getInstance("SHA-256");
    key = sha.digest(key);
    key = Arrays.copyOf(key, 16); // 128-bit AES key
    return new SecretKeySpec(key, "AES");
  }
  
  /**
   * AES decryption logic.
   */
  private byte[] decrypt(byte[] data, SecretKey key) throws Exception {
    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(Cipher.DECRYPT_MODE, key);
    return cipher.doFinal(data);
  }
  
  /**
   * Java 6/7 compatible method to read all bytes from a file.
   */
  private byte[] readAllBytes(File file) throws IOException {
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(file);
      byte[] data = new byte[(int) file.length()];
      int bytesRead = 0;
      while (bytesRead < data.length) {
        int result = fis.read(data, bytesRead, data.length - bytesRead);
        if (result == -1) break;
        bytesRead += result;
      }
      return data;
      } finally {
      if (fis != null) fis.close();
    }
  }
  
}
