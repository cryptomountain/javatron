/* Copyright (C) 2001-2002 Taylor Gautier
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your 
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along 
 * with this program; if not, write to the Free Software Foundation, Inc., 
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id: GUIUtilities.java,v 1.1 2002/08/05 00:06:41 tgautier Exp $
 */
package JavaTron;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.border.*;

import java.util.*;
import java.lang.reflect.*;

/** 
 * Graphical User Interface Utilities class.  Contains a number
 * of handy implementations.
 *
 * @author Taylor Gautier
 * @version $Revision: 1.1 $
 */
public class GUIUtilities
{
  private static long docking = 0;
  private static boolean debug = false;
  // ui constants
  public static final float BUTTON_FONT_SIZE = (float) 10;
  
  // snap/dock constants  
  private static final int SNAP_DISTANCE = 20;  
  private static final long DOCK_TIME    = 100;
  
  public static final int NONE           = 0;
  public static final int TOP_LEFT       = 1;
  public static final int TOP_RIGHT      = 2;
  public static final int LEFT_TOP       = 3;
  public static final int LEFT_BOTTOM    = 4;
  public static final int BOTTOM_LEFT    = 5;
  public static final int BOTTOM_RIGHT   = 6;
  public static final int RIGHT_TOP      = 7;
  public static final int RIGHT_BOTTOM   = 8;
  
  /**
  * Check to see if component c1 should dock against component
  * c2.  C1 is the component that is being moved, while c2 is
  * sitting still.
  *
  * @param c1 the component that is moving
  * @param c2 the component that is sitting still
  *
  * @return an integer that represents the docked state of c1
  */ 
  public static int checkDock(Component c1, Component c2)  
  {
    Dimension c1Size = c1.getSize();
    Dimension c2Size = c2.getSize();    
    int snap;              
    int newX = c1.getX(), newY = c1.getY();
    int docked = NONE;
    
    // check bottom
    snap = c2.getY() + c2Size.height;
    if (c1.getY() >= snap - SNAP_DISTANCE &&
        c1.getY() <= snap + SNAP_DISTANCE) {
      newY = snap;
      
      // check left for dock
      if (Math.abs(c2.getX() - c1.getX()) <= SNAP_DISTANCE) {
          newX = c2.getX();
        docked = BOTTOM_LEFT;
      }
      // check right for dock
      if (Math.abs(c2.getX() + c2.getWidth() - c1.getX() - c1.getWidth())
          <= SNAP_DISTANCE) {
        newX = c2.getX() + c2.getWidth() - c1.getWidth();
        docked = BOTTOM_RIGHT;
      }
    }
    
    // check right
    snap = c2.getX() + c2Size.width;
    if (c1.getX() >= snap - SNAP_DISTANCE &&
        c1.getX() <= snap + SNAP_DISTANCE) {
      newX = snap;
      
      // check top for dock
      if (Math.abs(c2.getY() - c1.getY()) <= SNAP_DISTANCE) {
          newY = c2.getY();
        docked = RIGHT_TOP;
      }
      // check bottom for dock
      if (Math.abs(c2.getY() + c2.getHeight() - c1.getY() - c1.getHeight())
          <= SNAP_DISTANCE) {
        newY = c2.getY() + c2.getHeight() - c1.getHeight();
        docked = RIGHT_BOTTOM;
      }
    }
    
    // check top
    snap = c1.getY() + c1Size.height;
    if (snap <= c2.getY() + SNAP_DISTANCE &&
        snap >= c2.getY() - SNAP_DISTANCE) {
      newY = c2.getY() - c1Size.height;
      
      // check left for dock
      if (Math.abs(c2.getX() - c1.getX()) <= SNAP_DISTANCE) {
          newX = c2.getX();
        docked = TOP_LEFT;
      }
      // check right for dock
      if (Math.abs(c2.getX() + c2.getWidth() - c1.getX() - c1.getWidth())
          <= SNAP_DISTANCE) {
        newX = c2.getX() + c2.getWidth() - c1.getWidth();
        docked = TOP_RIGHT;
      }
    }
    
    // check left
    snap = c1.getX() + c1Size.width;
    if (snap <= c2.getX() + SNAP_DISTANCE &&
        snap >= c2.getX() - SNAP_DISTANCE) {
      newX = c2.getX() - c1Size.width;
      
      // check top for dock
      if (Math.abs(c2.getY() - c1.getY()) <= SNAP_DISTANCE) {
          newY = c2.getY();
        docked = LEFT_TOP;
      }
      // check bottom for dock
      if (Math.abs(c2.getY() + c2.getHeight() - c1.getY() - c1.getHeight())
          <= SNAP_DISTANCE) {
        newY = c2.getY() + c2.getHeight() - c1.getHeight();
        docked = LEFT_BOTTOM;
      }
    }                  
    
    if (newX != c1.getX() || newY != c1.getY()) {                
      c1.setLocation(newX, newY);
    }
    
    return docked;
  }  
  
  /**
   * Move the dialog to the appropriate position depending on the value
   * of the docked flag stored by the dialog.
   *
   * @param f the frame window that the dialog is docked against
   * @param dialog the dialog to move
   *
   * @return true if the dialog is moved, false if not. It is moved
   *              if the docked constant is not equal to NONE.
   */
  public static boolean dockDialog(Frame f, DockedDialog dialog)
  {              
    switch (dialog.docked) {
      case TOP_LEFT:
        dialog.docking();
        dialog.setLocation(f.getX(), f.getY() - dialog.getHeight());
        break;         
      case TOP_RIGHT:
        dialog.docking();
        dialog.setLocation(f.getX()+f.getWidth() - dialog.getWidth(), f.getY() - dialog.getHeight());
        break;
          
      case RIGHT_TOP:
        dialog.docking();
        dialog.setLocation(f.getX() + f.getWidth(), f.getY());
        break;         
      case RIGHT_BOTTOM:
        dialog.docking();
        dialog.setLocation(f.getX() + f.getWidth(), f.getY() + f.getHeight() - dialog.getHeight());
        break;
              
      case BOTTOM_LEFT:
        dialog.docking();
        dialog.setLocation(f.getX(), f.getY() + f.getHeight());
        break;
      case BOTTOM_RIGHT:
        dialog.docking();
        dialog.setLocation(f.getX() + f.getWidth() - dialog.getWidth(), f.getY() + f.getHeight());
        break;
                  
      case LEFT_TOP:
        dialog.docking();
        dialog.setLocation(f.getX() - dialog.getWidth(), f.getY());
        break;
      case LEFT_BOTTOM:
        dialog.docking();
        dialog.setLocation(f.getX() - dialog.getWidth(), f.getY() + f.getHeight() - dialog.getHeight());
        break;
      
      default:
        return false;
    }
    
    return true;
  }
  
  /**
   * Invert a docked value.  This is useful for the main window which
   * might dock against a dockable dialog.  Usually the docked variable
   * is relative from the dockedDialog to the frame window, so if a 
   * frame window computes the dock constant, it will be opposite the
   * value that the DockedDialog should receive.
   *
   * @param docked the value to invert
   *
   * @return the inverted value
   */
  public static int invertDocked(int docked)
  {
    switch (docked) {
      case TOP_LEFT: return BOTTOM_LEFT;
      case TOP_RIGHT: return BOTTOM_RIGHT;
      case LEFT_TOP: return RIGHT_TOP;
      case LEFT_BOTTOM: return RIGHT_BOTTOM;
      case BOTTOM_LEFT: return TOP_LEFT;
      case BOTTOM_RIGHT: return TOP_RIGHT;
      case RIGHT_TOP: return LEFT_TOP;
      case RIGHT_BOTTOM: return LEFT_BOTTOM;
    }
    
    return docked;
  }
  
  /**
  * Defines a class that is dockable against a frame window.
  */
  public static class DockedDialog extends JDialog
  {
    public int docked;
    private long docking = 0;
    
    /**
     * Constructor
     *
     * @param owner the owner of this dialog
     * @param label the label of this dialog
     * @param toggle (optional - can be null) a toggle button that controls
     *               whether we are visible or not
     */
    public DockedDialog(final Frame owner, 
                        String label,
                        final JToggleButton toggle)
    {
      super(owner, label);
      
      addComponentListener(new ComponentAdapter() {
        public void componentShown(ComponentEvent e) 
        {
          if (toggle != null) toggle.setSelected(true);
        }
        public void componentHidden(ComponentEvent e) 
        {
          if (toggle != null) toggle.setSelected(false);
        }
        public void componentMoved(ComponentEvent e)
        {
          if (System.currentTimeMillis() < docking) {
            return;
          }
          docked = checkDock(DockedDialog.this, owner);
        }
      });
    }
    
    /**
     * Set our docked flag which indicates where we are docked
     */
    public void setDocked(int d) { docked = d; }
    
    /**
     * Tell the dialog that a docking operation is occurring (it is 
     * being moved to a dock/snap to a location, i.e. a user is NOT 
     * the source of the move message that will result.
     */
    public void docking() { docking = System.currentTimeMillis() + DOCK_TIME; }
  }
  
  /**
  * Defines a class that automatically handles Action events
  * using functions of the same name.  In essence this class
  * creates an instance of a customized Action object.  This object
  * has a label (as all Action objects do) and it uses that
  * label to dynamically link to the method of the same name 
  * in its parent class (the derived instance of UIHandler).
  * <p>
  * This class also provides shortcut methods to create widgets 
  * with a specific look and attach them to Actions.
  */
  public static class UIHandler
  {    
    public Action createAction(String label)
    { return createAction(label, label); }
    
    public Action createAction(String label, String method)
    { return new ActionHandler(label, method); }
    
    public Action createAction(String label, String method, Class[] args)
    { 
//		System.out.print("Create Action: "+label+", "+method+", ");
//		for(int i=0;i<args.length;i++)
//			System.out.print(args[i].toString()+", ");
//		System.out.println();
		
		return new ActionHandler(label, method, args);
	}
    
    class ActionHandler extends AbstractAction
    {
      WrappedMethod wm;
      
      public ActionHandler(String label) { this(label, label); }
      public ActionHandler(String label, String method)
      { this(label, method, null); }
      public ActionHandler(String label, Class[] args)
      { this(label, label, args); }
      public ActionHandler(String label, String method, Class[] args)
      {
        super(label);
        wm = getWrappedMethod(method, args);
      }
      
      public void actionPerformed(ActionEvent e)
      { 
		if(debug){System.out.println("action:"+e.paramString()+"|"+e.getActionCommand());}

		if(e.getActionCommand().length() > 15){
			String ac=e.getActionCommand().substring(0,16);
			if(debug){System.out.println("Action: "+ac+"*");}
			if(e.getActionCommand().substring(0,16).equals("Add To Playlist ")){
				if(debug){System.out.println("Action: detected fubar'd action");}
				String playlistNum = e.getActionCommand().substring(16);
				String playlist="Playlist "+playlistNum;
				if(debug){System.out.println(playlist);}
				System.out.println("New argument list...");
				wm.args=new Object[1];
				wm.args[0]=playlist;

			}
		}
		try {
          wm.invoke();
        } catch (IllegalAccessException iae) {
          iae.printStackTrace();
        } catch (InvocationTargetException ite) {
          ite.printStackTrace();
        }
      }
    }
    
    WrappedMethod getWrappedMethod(String method, Class[] args)
    {
      try {
        Method m;      
        m = getClass().getDeclaredMethod(makeMethodName(method), args);
        return new WrappedMethod(this, m, null);
      } catch (NoSuchMethodException nsme) {
        nsme.printStackTrace();
      }
      
      return null;
    }  
    
    /**
    * Create a method name from the input label
    *
    * @param label the label to turn into a method name
    */
    String makeMethodName(String label)
    {
      label = label.toLowerCase();
      label = label.replace(' ', '_');
      label = label.replace('|', '_');
      label = label.replace('<', '_');
      label = label.replace('>', '_');
      if(debug){System.out.println("makeMethodName: "+label);}
      return label;
    }
    
    /**
    * Create a customized JButton
    */
    public JButton createJButton(String label) 
    { return createJButton(createAction(label)); }
    
    public JButton createJButton(Action action)
    { return formatJButton(new JButton(action)); }
    
    public JButton formatJButton(JButton b)
    {
      setFontSize(b, BUTTON_FONT_SIZE);
      return b;
    }
    
    /**
    * Create a customized JToggleButton
    */
    public JToggleButton createJToggleButton(String label) 
    { return createJToggleButton(createAction(label)); }
    
    public JToggleButton createJToggleButton(Action action)
    { return formatJToggleButton(new JToggleButton(action)); }
    
    public JToggleButton formatJToggleButton(JToggleButton b)
    {
      setFontSize(b, BUTTON_FONT_SIZE);
      return b;
    }    
    
    /**
    * Create a customized JCheckBox
    */
    public JCheckBox createJCheckBox(String label)
    { return createJCheckBox(createAction(label)); }
    
    public JCheckBox createJCheckBox(Action action)
    { return formatJCheckBox(new JCheckBox(action)); }
    
    public JCheckBox formatJCheckBox(JCheckBox c)
    {
      setFontSize(c, BUTTON_FONT_SIZE);  
      return c;
    }
    
    /**
    * Create a customized JMenuItem
    */
    public JMenuItem createJMenuItem(String label)
    { return createJMenuItem(createAction(label)); }   
    
    public JMenuItem createJMenuItem(Action action)
    { return formatJMenuItem(new JMenuItem(action)); }
    
    public JMenuItem formatJMenuItem(JMenuItem m)
    {
      setFontSize(m, BUTTON_FONT_SIZE);
      setFontType(m, Font.PLAIN);
      
      return m;
    }
    
    /**
    * Create a customized JRadioButton
    */
    public JRadioButton createJRadioButton(String label)
    { return createJRadioButton(createAction(label)); }
    
    public JRadioButton createJRadioButton(Action action)
    { return formatJRadioButton(new JRadioButton(action)); }
    
    public JRadioButton formatJRadioButton(JRadioButton r)
    {
      setFontSize(r, BUTTON_FONT_SIZE);
      
      return r;
    }
  }
  
  
  /**
   * A class that is a fixed width and height component
   */
  public static class Spacer extends JComponent
  {
    /**
     * Constructor
     *
     * @param width the fixed width of this component
     * @param height the fixed height of this component
     */
    public Spacer(int width, int height)
    {
      super();
      setSize(new Dimension(width, height));
      setMinimumSize(new Dimension(width, height));
      setMaximumSize(new Dimension(width, height));
      setPreferredSize(new Dimension(width, height));
    }
  }

  /**
   * A panel that provides the ability to different panels inside
   * of it at runtime
   */
  public static class SelectorPanel
  {
    Container current;
    JPanel box = new JPanel(new GridLayout(0,1));
    Box bbox = Box.createHorizontalBox();
    
    public SelectorPanel()
    {
      box.add(bbox); 
      box.setMaximumSize(new Dimension(Integer.MAX_VALUE,1));
    }
    
    /**
     * Return the UI component of the panel
     *
     * @return the ui component of the panel
     */
    public JComponent getUI() { return box; }
    
    /**
     * Set the current container to show in the panel
     *
     * @param current_ the container to set as the current container
     */
    public void setCurrent(Container current_)
    {
      if (current != null) {
        bbox.remove(current);
      }
      current = current_;
      bbox.add(current);
      current.validate();
      box.paintImmediately(box.getVisibleRect());
    }          
  }
  
  /**
   * Set the font size for a component (leaves other properties of
   * the current font intact)
   *
   * @param size the new size of the font
   */
  public static void setFontSize(Component c, float size)
  {
    c.setFont(c.getFont().deriveFont(size));
  }
  
  /**
   * Set the font type for a component (leaves other properties of
   * the current font intact)
   *
   * @param type the new type for the font
   */
  public static void setFontType(Component c, int type)
  {
    c.setFont(c.getFont().deriveFont(type));
  }

  public static void setBackground(Component c, Color color)
  {
	  if(c instanceof JTree){
		  JTree j=(JTree)c;
		  DefaultTreeCellRenderer dcr=(DefaultTreeCellRenderer)j.getCellRenderer();
		  dcr.setBackgroundNonSelectionColor(color);
	  }
	  c.setBackground(color);
  }
  /**
  * Create a JLabel but make the font plain instead of super ugly BOLD
  *
  * @param mlabel the label to initialie the JLabel to
  *
  * @return an initialized JLabel with a PLAIN font
  */
  public static JLabel createJLabel(String mlabel)
  {
    JLabel l = new JLabel(mlabel, SwingConstants.LEFT);
    setFontType(l, Font.PLAIN);
    
    return l;
  }
  
  /**
   * Sets up a layout in the container to implement an indent
   *
   * @param c the container to add the indent to
   * @param indent the amount of indentation
   */
  public static void setIndent(Container c, int indent)
  {        
    c.setLayout(new BorderLayout());   
    c.add(Box.createVerticalStrut(indent), "North");
    c.add(Box.createVerticalStrut(indent), "South");
    c.add(Box.createHorizontalStrut(indent), "East");
    c.add(Box.createHorizontalStrut(indent), "West");
  }
  
  public static void setNewIndent(JComponent c, int indent)
  {
    Border b = BorderFactory.createEmptyBorder(indent, indent, indent, indent);
    c.setBorder(b);
  }
  
  /**
  * A useful debugging method - sets a red border on a JComponent
  *
  * @param c the JComponent  
  */
  public static void showBorder(JComponent c) { showBorder(c, Color.red); }
  public static void showBorder(JComponent c, Color color)
  {
    c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color),
               c.getBorder()));
  }
  
  public static class VerticalLayoutPanel extends JPanel
  {
    Box hbl;
    boolean first = true;
    
    public VerticalLayoutPanel()
    {
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      setAlignmentX(0);
      setAlignmentY(0);
    }
    
    public void addSingleItem(Component c)
    {
      br();
      super.add(c);
    }
    
    public Component add(Component c)
    {
      if (hbl == null)
      {
        hbl = Box.createHorizontalBox();
        super.add(hbl);
      }
      hbl.add(c);
      return c;
    }
    
    public void br() { br(false); }
    public void br(boolean hard)
    {
      if (hbl != null) {
        if (!hard) {
          hbl.add(Box.createHorizontalGlue());
        }
        hbl = null;
      }
    }
    
    public void horizontalStrut(int size)
    {
      add(new Spacer(size, 0));
    }
    
    public void verticalStrut(int size)
    {
      addSingleItem(new Spacer(0, size));
    }
    
    public void glue()
    {
      addSingleItem(Box.createVerticalGlue());
    }
  } // VerticalLayoutPanel
  
  public static void warning(String msg)
  {
    Object[] options = { "Ok" };
    JOptionPane.showOptionDialog(null, 
                                 msg,
                                 "Warning",
                                 JOptionPane.DEFAULT_OPTION,
                                 JOptionPane.WARNING_MESSAGE,
                                 null,
                                 options,
                                 options[0]);
  }// End

}
