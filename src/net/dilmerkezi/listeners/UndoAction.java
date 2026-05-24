package net.dilmerkezi.listeners;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.undo.UndoManager;

/**
 * Action responsible for the Undo operation.
 */
public class UndoAction extends AbstractAction {
  
  private final UndoManager umngr;
  
  public UndoAction(UndoManager umngr) {
    super("Undo");
    this.umngr = umngr;
    setEnabled(false);
  }
  
  public void actionPerformed(ActionEvent evt) {
    try {
      if (umngr.canUndo()) {
        umngr.undo();
      }
      } catch (Exception uex) {
      System.err.println("Undo Error: " + uex.getMessage());
    }
    // updateActions call will be handled by the listener or manually
    again();
  }
  
  public void again() {
    boolean canu = umngr.canUndo();
    setEnabled(canu);
    putValue(Action.NAME, canu ? umngr.getUndoPresentationName() : "Undo");
  }
  
}
