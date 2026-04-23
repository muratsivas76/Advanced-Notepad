package net.dilmerkezi.defter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A utility dialog that provides an extended character map for selecting Unicode characters,
 * including symbols like telephone, arrows, and mathematical operators.
 */
public class XCharacterMap extends JDialog {
  private JTable charTable;
  private final JTextPane targetTextPane;
  
  public XCharacterMap(JTextPane targetTextPane) {
    super(SwingUtilities.getWindowAncestor(targetTextPane), "Extended Character Map", ModalityType.MODELESS);
    this.targetTextPane = targetTextPane;
    initializeUI();
  }
  
  private void initializeUI() {
    setLayout(new BorderLayout());
    setSize(600, 500);
    setAlwaysOnTop(true);
    setLocationRelativeTo(targetTextPane);
    
    // 1. Prepare Table Model (16 columns for hex-like grid)
    String[] columnNames = new String[16];
    for (int i = 0; i < 16; i++) {
      columnNames[i] = Integer.toHexString(i).toUpperCase();
    }
    
    DefaultTableModel model = new DefaultTableModel(null, columnNames) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    
    // 2. Define Unicode blocks to include
    int[][] blocks = {
      {0x0020, 0x017F}, // Basic Latin & Latin Extended-A (Standard letters)
      {0x0370, 0x03FF}, // Greek and Coptic
      {0x2000, 0x206F}, // General Punctuation
      {0x2100, 0x214F}, // Letterlike Symbols
      {0x2190, 0x21FF}, // Arrows
      {0x2200, 0x22FF}, // Mathematical Operators
      {0x25A0, 0x25FF}, // Geometric Shapes
      {0x2600, 0x26FF}, // Miscellaneous Symbols (Phone ☎, Sun ☀, Cloud ☁, etc.)
      {0x2700, 0x27BF}  // Dingbats (Scissors ✂, Pencil ✎, Stars ★)
    };
    
    // Fill the table with characters from defined blocks
    for (int[] block : blocks) {
      for (int rowBase = block[0]; rowBase <= block[1]; rowBase += 16) {
        Object[] rowData = new Object[16];
        for (int i = 0; i < 16; i++) {
          int charCode = rowBase + i;
          if (charCode <= block[1]) {
            rowData[i] = String.valueOf((char) charCode);
            } else {
            rowData[i] = "";
          }
        }
        model.addRow(rowData);
      }
    }
    
    // 3. Configure JTable
    charTable = new JTable(model);
    
    // "Dialog" or "Segoe UI Symbol" works best for showing symbols on most systems
    charTable.setFont(new Font("Dialog", Font.PLAIN, 20));
    //charTable.setForeground(Color.BLUE);
    charTable.setRowHeight(35);
    charTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    charTable.getTableHeader().setReorderingAllowed(false);
    charTable.setCellSelectionEnabled(true);
    
    // 4. Add Mouse Listener for Double-Click action
    charTable.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          if (e.getClickCount() == 2) {
            int row = charTable.getSelectedRow();
            int col = charTable.getSelectedColumn();
            if (row != -1 && col != -1) {
              Object value = charTable.getValueAt(row, col);
              if (value != null && !value.toString().isEmpty()) {
                insertCharacter(value.toString());
              }
            }
          }
        }
    });
    
    // 5. Layout assembly
    JScrollPane scrollPane = new JScrollPane(charTable);
    add(scrollPane, BorderLayout.CENTER);
    
    JLabel footerLabel = new JLabel(" Double-click a symbol to insert it into your document.");
    footerLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
    footerLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
    add(footerLabel, BorderLayout.SOUTH);
  }
  
  private void insertCharacter(String character) {
    try {
      int caretPosition = targetTextPane.getCaretPosition();
      targetTextPane.getDocument().insertString(caretPosition, character, null);
      targetTextPane.requestFocusInWindow();
      } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  
}
