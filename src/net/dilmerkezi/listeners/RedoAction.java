package net.dilmerkezi.listeners;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.undo.UndoManager;

/**
 * Action responsible for the Redo operation.
 */
public class RedoAction extends AbstractAction {
  
  private final UndoManager umngr;
  
  public RedoAction(UndoManager umngr) {
    super("Redo");
    this.umngr = umngr;
    setEnabled(false);
  }
  
  public void actionPerformed(ActionEvent evt) {
    try {
      if (umngr.canRedo()) {
        umngr.redo();
      }
      } catch (Exception uex) {
      System.err.println("Redo Error: " + uex.getMessage());
    }
    again();
  }
  
  public void again() {
    boolean canu = umngr.canRedo();
    setEnabled(canu);
    putValue(Action.NAME, canu ? umngr.getRedoPresentationName() : "Redo");
  }
  
}
