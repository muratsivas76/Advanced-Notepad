package net.dilmerkezi.listeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

/**
 * Listener responsible for encrypting and saving .enc files.
 * Includes mandatory overwrite confirmation and AES encryption.
 * 
 * Focus: Highly stabilized to return focus after multiple dialog chains.
 *
 * Licensed under GPL v3.
 */
public class SaveEncryptedFileActionListener implements ActionListener {
  
  private final JTextPane txt;
  private final JLabel statusLabel;
  private final JFileChooser encjfc;
  private final Component parent;
  
  public SaveEncryptedFileActionListener(JTextPane txt, JLabel statusLabel, JFileChooser encjfc, Component parent) {
    this.txt = txt;
    this.statusLabel = statusLabel;
    this.encjfc = encjfc;
    this.parent = parent;
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    statusLabel.setText("");
    encjfc.setDialogTitle("Save Encrypted File");
    
    if (encjfc.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
      File selectedFile = encjfc.getSelectedFile();
      String filePath = selectedFile.getAbsolutePath();
      
      if (!filePath.toLowerCase().endsWith(".enc")) {
        filePath = filePath + ".enc";
        selectedFile = new File(filePath);
      }
      
      final File finalFile = selectedFile;
      
      // 1. Overwrite Confirmation
      if (finalFile.exists()) {
        String confirmMsg = "<html><body><font color=\"#FF5722\" size=\"5\">"
        + "This file already exists!<br><br>"
        + "Enter <b>e/y/t/yes/ok</b> to Overwrite:"
        + "</font></body></html>";
        
        String input = JOptionPane.showInputDialog(parent, confirmMsg);
        
        if (input == null || !isUserConfirmed(input.trim().toLowerCase())) {
          statusLabel.setText("Overwrite cancelled by user.");
          forceFocus();
          return;
        }
      }
      
      // 2. Security credentials
      final String fullPassword = askForPassword();
      if (fullPassword == null || fullPassword.isEmpty()) {
          forceFocus();
          return;
      }
      
      // 3. Background encryption process
      Thread encryptionThread = new Thread(new Runnable() {
          @Override
          public void run() {
            try {
              SecretKey key = getSecretKey(fullPassword);
              byte[] data = txt.getText().getBytes("UTF-8");
              byte[] encryptedData = encrypt(data, key);
              
              writeBytesToFile(finalFile, encryptedData);
              
              SwingUtilities.invokeLater(new Runnable() {
                  @Override
                  public void run() {
                    String successMsg = "<html><body><font color=\"#4CAF50\" size=\"5\">"
                    + "Encrypted file saved successfully:<br>" + finalFile.getName()
                    + "</font></body></html>";
                    JOptionPane.showMessageDialog(parent, successMsg);
                    txt.requestFocus(); // Focus after success dialog
                  }
              });
              
              } catch (final Exception ex) {
              SwingUtilities.invokeLater(new Runnable() {
                  @Override
                  public void run() {
                    String errorMsg = "<html><body><font color=\"red\" size=\"5\">"
                    + "Encryption failed:<br>" + ex.getMessage()
                    + "</font></body></html>";
                    JOptionPane.showMessageDialog(parent, errorMsg);
                    txt.requestFocus(); // Focus after error dialog
                  }
              });
            }
          }
      });
      encryptionThread.start();
    } else {
      forceFocus();
    }
  }
  
  private boolean isUserConfirmed(String input) {
    return input.equals("e") || input.equals("y") || input.equals("t")
    || input.equals("yes") || input.equals("ok");
  }
  
  private String askForPassword() {
    String passMsg = "<html><body><font color=\"blue\" size=\"5\">Enter password for encryption:</font></body></html>";
    return JOptionPane.showInputDialog(parent, passMsg);
  }
  
  private SecretKey getSecretKey(String password) throws Exception {
    byte[] key = password.getBytes("UTF-8");
    MessageDigest sha = MessageDigest.getInstance("SHA-256");
    key = sha.digest(key);
    key = Arrays.copyOf(key, 16);
    return new SecretKeySpec(key, "AES");
  }
  
  private byte[] encrypt(byte[] data, SecretKey key) throws Exception {
    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(Cipher.ENCRYPT_MODE, key);
    return cipher.doFinal(data);
  }
  
  private void writeBytesToFile(File file, byte[] data) throws IOException {
    FileOutputStream fos = new FileOutputStream(file);
    try {
      fos.write(data);
      fos.flush();
      } finally {
      fos.close();
    }
  }

  /**
   * Guaranteed focus recovery after any dialog close or cancellation.
   */
  private void forceFocus() {
    SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          txt.requestFocus();
        }
    });
  }
  
}
