package net.dilmerkezi.listeners;

import javax.swing.SwingUtilities;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;

/**
 * Custom UndoableEditListener to handle document changes.
 *
 * Licensed under GPL v3.
 */
public class OwnUndoListener implements UndoableEditListener {
  
  private final UndoManager umngr;
  private final UndoAction uaction;
  private final RedoAction raction;
  
  public OwnUndoListener(UndoManager umngr, UndoAction uaction, RedoAction raction) {
    this.umngr = umngr;
    this.uaction = uaction;
    this.raction = raction;
  }
  
  public void undoableEditHappened(final UndoableEditEvent e) {
    SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          umngr.addEdit(e.getEdit());
          updateActions();
        }
    });
  }
  
  protected void updateActions() {
    if (uaction != null) uaction.again();
    if (raction != null) raction.again();
  }
  
}
