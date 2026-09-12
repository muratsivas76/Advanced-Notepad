package net.dilmerkezi.listeners;

import java.io.Serializable;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;

public class UndoRedoConstants implements Serializable {
  public static final UndoManager umngr = new UndoManager();
  public static final UndoAction uaction = new UndoAction(umngr);
  public static final RedoAction raction = new RedoAction(umngr);
  public static final UndoableEditListener ulis = new OwnUndoListener(umngr, uaction, raction);
}
