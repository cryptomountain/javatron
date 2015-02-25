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
 * $Id: Preferences.java,v 1.1 2002/08/05 00:06:41 tgautier Exp $
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

import java.io.*;
/**
 * Container for Configuration Panels
 *
 * @author Taylor Gautier
 */
public class Preferences extends GUIUtilities.DockedDialog
implements Configuration.ChangeListener
{
  private static final int   PREFS_HEIGHT           = 370;
  
  MyUIHandler uiHandler;
  GUIUtilities.SelectorPanel configPanel;
  
  JTree selector;
  DefaultMutableTreeNode root;
  TreePath selection;
   
  ConfigPanel[] panels;
  AudioTronState at;
  Properties props;
  JButton save;
  
  protected static interface ConfigPanel
  {
    public void setActive(boolean active);
    public JComponent getUI();
  }

  protected static abstract class AbstractConfigPanel implements ConfigPanel
  {
	@Override
    public void setActive(boolean active) { }
	@Override
    public abstract JComponent getUI();
  }
  
  public Preferences(AudioTronState at_, 
                     Frame owner,
                     String label,
                     JToggleButton toggle)
  {
    super(owner, label, toggle);
    at = at_;
    uiHandler = new MyUIHandler();
    
    panels = new ConfigPanel[13];
    save = uiHandler.createJButton("Save");
    
    // setup the content pane buffer
    GUIUtilities.setIndent(getContentPane(), AudioTronUI.INDENT_MAIN);
    
    root = new DefaultMutableTreeNode("");
    DefaultMutableTreeNode prefs = new DefaultMutableTreeNode("Preferences");
    
	DefaultMutableTreeNode first = new DefaultMutableTreeNode("General");
	prefs.add(first);
    prefs.add(new DefaultMutableTreeNode("Playlist Editor"));
	prefs.add(new DefaultMutableTreeNode("TV Mode"));
    root.add(prefs);

    DefaultMutableTreeNode util = new DefaultMutableTreeNode("Util");
    util.add(new DefaultMutableTreeNode("TOC"));
	util.add(new DefaultMutableTreeNode("Message Display"));
	util.add(new DefaultMutableTreeNode("Network Address"));
	util.add(new DefaultMutableTreeNode("Share Settings"));
	util.add(new DefaultMutableTreeNode("Radio"));
	util.add(new DefaultMutableTreeNode("History"));
    root.add(util);

	DefaultMutableTreeNode help = new DefaultMutableTreeNode("Help");
    help.add(new DefaultMutableTreeNode("About"));
    root.add(help);      
    Box left = Box.createVerticalBox();
    selector = new JTree(root);
    selector.setRootVisible(false);
    selector.setToggleClickCount(0);
    selector.setExpandsSelectedPaths(true);
    Object[] path = { root, prefs, first };
    
    // expand all the rows
    for (int i = root.getChildCount()-1; i >= 0; i--) {
      selector.expandRow(i);
    }
    
    selector.setSelectionPath(new TreePath(path));
    JScrollPane sp1 = new JScrollPane(selector);
    
    // create the right panel                        
    configPanel = new GUIUtilities.SelectorPanel();
    
    panels[1] = new GeneralConfigPanel();
    panels[2] = new EditorConfigPanel();
	panels[3] = new TVConfigPanel();
    panels[5] = new TOCConfigPanel();
	panels[6] = new MDisplayConfigPanel();
	panels[7] = new NetConfigPanel();
	panels[8] = new ShareConfigPanel();
	panels[9] = new RadioConfigPanel();
	panels[10]= new HistoryPanel();
    panels[12] = new AboutConfigPanel();
    
    configPanel.setCurrent(panels[1].getUI());
    selector.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    
    // create the split pane
    JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                   sp1,
                                   configPanel.getUI());
    // create dialog buttons
    Box bb = Box.createHorizontalBox();
    bb.add(Box.createHorizontalGlue());
    bb.add(save);
    bb.add(Box.createHorizontalStrut(5));
    bb.add(uiHandler.createJButton("Close"));
    
    // add dialog buttons
    Box vb = Box.createVerticalBox();      
    vb.add(Box.createVerticalStrut(5));
    vb.add(bb);
    
    // add all the ui to the dialog
    JPanel p = new JPanel(new BorderLayout());
    p.add(sp, "Center");
    p.add(vb, "South");
    getContentPane().add(p, "Center");
          
    pack();
    setSize(new Dimension(owner.getWidth()+20, PREFS_HEIGHT));
    sp.setDividerLocation((int) (selector.getWidth()*1.3));

    TreeSelectionListener tsl = new TreeSelectionListener() {
	  @Override
      public void valueChanged(TreeSelectionEvent e)
      {
        TreePath oldSelection = e.getOldLeadSelectionPath();
        if (e.getNewLeadSelectionPath() == null) {
          return;
        }
        selection = e.getNewLeadSelectionPath();
        if (selection.getPathCount() == 2) {
          selector.setSelectionPath(oldSelection);
          return;
        }
        int row = selector.getRowForPath(selection);
        if (oldSelection.getPathCount() != 2) {
          int oldRow = selector.getRowForPath(oldSelection);          
          panels[row].getUI().setSize(panels[oldRow].getUI().getSize());
          panels[oldRow].setActive(false);
        }
        configPanel.setCurrent(panels[row].getUI());
        panels[row].setActive(true);
      }
    };
    selector.addTreeSelectionListener(tsl);
    save.setEnabled(false);
    
    // Add ourselves to Configuration
    Configuration.addChangeListener(this);
  }
  
  public void propertyChanged(String key)
  {
    save.setEnabled(true);
  }
  
  protected boolean saveProperties()
  {
    boolean ret = Configuration.saveProperties();
    save.setEnabled(!ret);
    
    return ret;
  }
  
  protected class MyUIHandler extends GUIUtilities.UIHandler
  {
    public void close() { setVisible(false); }
    public void save() { saveProperties(); }
  }

  protected class GeneralConfigPanel extends AbstractConfigPanel
  {
    GUIUtilities.VerticalLayoutPanel ui;
    MyUIHandler uiHandler;
    JTextField server, username, base_m3u_file;
    JPasswordField password;

    
    public GeneralConfigPanel()
    {
      uiHandler = new MyUIHandler();
      ui = new GUIUtilities.VerticalLayoutPanel();
      
      GUIUtilities.setNewIndent(ui, AudioTronUI.INDENT_MAIN);
      ui.add(GUIUtilities.createJLabel("AudioTron Server:")); ui.br();
      ui.add((server = new JTextField(20))); 
      ui.verticalStrut(5);
      ui.add(GUIUtilities.createJLabel("Username:")); ui.br();
      ui.add((username = new JTextField(20)));
      ui.verticalStrut(5);
      ui.add(GUIUtilities.createJLabel("Password:")); ui.br();
      ui.add((password = new JPasswordField(20)));
	  ui.verticalStrut(5);
      ui.add(GUIUtilities.createJLabel("Default m3u file:")); ui.br();
      ui.add((base_m3u_file = new JTextField(20)));
      ui.glue();
      
      server.setMaximumSize(server.getPreferredSize());
      username.setMaximumSize(username.getPreferredSize());
      password.setMaximumSize(password.getPreferredSize());
	  base_m3u_file.setMaximumSize(base_m3u_file.getPreferredSize());
      
      server.setText(at.getServer());
      username.setText(at.getUsername());
      password.setText(at.getPassword());
	  base_m3u_file.setText(at.getBaseM3U());
      
      // add text listeners
      DocumentListener action = new DocumentListener() {
        public void changedUpdate(DocumentEvent e) { uiHandler.server(); }
        public void insertUpdate(DocumentEvent e) { uiHandler.server(); }
        public void removeUpdate(DocumentEvent e) { uiHandler.server(); }
      };
      server.getDocument().addDocumentListener(action);
      action = new DocumentListener() {
        public void changedUpdate(DocumentEvent e) { uiHandler.username(); }
        public void insertUpdate(DocumentEvent e) { uiHandler.username(); }
        public void removeUpdate(DocumentEvent e) { uiHandler.username(); }
      };
      username.getDocument().addDocumentListener(action);      
      action = new DocumentListener() {
        public void changedUpdate(DocumentEvent e) { uiHandler.password(); }
        public void insertUpdate(DocumentEvent e) { uiHandler.password(); }
        public void removeUpdate(DocumentEvent e) { uiHandler.password(); }
      };
      password.getDocument().addDocumentListener(action);
	  action = new DocumentListener() {
        public void changedUpdate(DocumentEvent e) { uiHandler.base_m3u_file(); }
        public void insertUpdate(DocumentEvent e) { uiHandler.base_m3u_file(); }
        public void removeUpdate(DocumentEvent e) { uiHandler.base_m3u_file(); }
      };
      base_m3u_file.getDocument().addDocumentListener(action);

    }
    
    public JComponent getUI() { return ui; }
    
    protected class MyUIHandler extends GUIUtilities.UIHandler
    {
      public void server() 
      {
        Configuration.setProperty(Configuration.KEY_SERVER, server.getText());
      }
      public void username()
      {
        Configuration.setProperty(Configuration.KEY_USERNAME, username.getText());
      }
      public void password()
      { 
		String p = new String(password.getPassword());
		String password = JTP.encrypt(p);
		Configuration.setProperty(Configuration.KEY_PASSWORD, password);
      }
	  
	  public void base_m3u_file(){
		  Configuration.setProperty(Configuration.KEY_BASE_M3U_FILE,
									new String(base_m3u_file.getText()));
	  }
    } // GeneralConfigPanel.MyUIHandler
  } // GeneralConfigPanel

  protected class MDisplayConfigPanel extends AbstractConfigPanel
  {
	  ATMessagePanel msg;
	  MDisplayConfigPanel(){
		  msg=new ATMessagePanel(at);
	  }

	  public JComponent getUI(){
		  return msg;
	  }
  }

  protected class NetConfigPanel extends AbstractConfigPanel
  {
	  ATNetConfigPanel netcfg;
	  NetConfigPanel(){
		  netcfg=new ATNetConfigPanel(at);
	  }

	  public JComponent getUI(){
		  return netcfg;
	  }
  }

  protected class RadioConfigPanel extends AbstractConfigPanel
  {
	  ATRadioPanel test;
	  RadioConfigPanel(){
		  test=new ATRadioPanel(at);
	  }

	  public JComponent getUI(){
		  return test;
	  }
  }

  protected class ShareConfigPanel extends AbstractConfigPanel
  {
	  ATNetFileConfigPanel a;
	  ShareConfigPanel(){
		  a=new ATNetFileConfigPanel(at);
	  }

	  public JComponent getUI(){
		  return a;
	  }
  }
  
  protected class TVConfigPanel extends AbstractConfigPanel
  {
	  ATTVModeConfigPanel tvp;
	  TVConfigPanel(){
		  tvp=new ATTVModeConfigPanel(at);
	  }

	  public JComponent getUI(){
		  return tvp;
	  }
  }

	protected class HistoryPanel extends AbstractConfigPanel{
		ATHistoryPanel thp;
		HistoryPanel(){
		  thp=new ATHistoryPanel(at);
	  }

	  public JComponent getUI(){
		  return thp;
	  }
	}

  protected class EditorConfigPanel extends AbstractConfigPanel
  {
    JPanel ui;
    MyUIHandler uiHandler;
    JRadioButton rb1, rb2;
	JCheckBox cb1;
    
    public EditorConfigPanel()
    {
      uiHandler = new MyUIHandler();
      ui = new JPanel();
      
      GUIUtilities.setIndent(ui, AudioTronUI.INDENT_MAIN);
      Box blayout = Box.createVerticalBox();
      ButtonGroup bg = new ButtonGroup();
      rb1 = uiHandler.createJRadioButton("Play Selected Now");
      rb2 = uiHandler.createJRadioButton("Enqueue Selected");
      rb1.setSelected(true);
	  cb1 = uiHandler.createJCheckBox("Confirm File Overwrite");
      bg.add(rb1); bg.add(rb2);
      blayout.add(GUIUtilities.createJLabel("Double click defaults to:"));
      blayout.add(rb1);
      blayout.add(rb2);
	  blayout.add(GUIUtilities.createJLabel(" "));
	  blayout.add(GUIUtilities.createJLabel("File Overwrite:"));
	  blayout.add(cb1);
	  blayout.add(Box.createVerticalGlue());

      ui.add(blayout, "Center");
      
      int val = 
        Configuration.getIntProperty(Configuration.KEY_DEFAULT_DOUBLE_CLICK);
      
      switch (val) {
        case AudioTronUI.PLAY_NOW:
          rb1.setSelected(true);
          break;
        case AudioTronUI.ENQUEUE:
          rb2.setSelected(true);
          break;
      }

	  cb1.setSelected(Configuration.getBoolProperty(Configuration.KEY_CONFIRM_OVERWRITE));
	  
    }

	public JComponent getUI() { return ui; }
    
    protected class MyUIHandler extends GUIUtilities.UIHandler
    {
      public void play_selected_now() 
      {
        Configuration.setIntProperty(Configuration.KEY_DEFAULT_DOUBLE_CLICK,
                                     AudioTronUI.PLAY_NOW);
      }
      
      public void enqueue_selected()
      {
        Configuration.setIntProperty(Configuration.KEY_DEFAULT_DOUBLE_CLICK,
                                     AudioTronUI.ENQUEUE);
      }

	  public void confirm_file_overwrite()
	  {
		  Configuration.setBoolProperty(Configuration.KEY_CONFIRM_OVERWRITE,
										cb1.isSelected());
	  }
    } // EditorConfigPanel.MyUIHandler
  } // EditorConfigPanel
  
  protected class TOCConfigPanel implements ConfigPanel
  {
    GUIUtilities.VerticalLayoutPanel ui;
    JList list;
    JButton toc;
    MyUIHandler uiHandler;
    String filename = "atrontc.vtc";
    PrintWriter writer;
    
    boolean initialized = false;
    
    public TOCConfigPanel() 
    {
      uiHandler = new MyUIHandler();
      
      ui = new GUIUtilities.VerticalLayoutPanel();
      GUIUtilities.setNewIndent(ui, AudioTronUI.INDENT_MAIN);
      list = new JList(at.getShareListModel());
      JScrollPane jsp = new JScrollPane(list);
      toc = uiHandler.createJButton("Save TOC");
      
      ui.add(GUIUtilities.createJLabel("Share List:"));
      ui.addSingleItem(jsp);
      ui.verticalStrut(5);
      ui.add(toc);
      ui.br();
      
      list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public JComponent getUI() { return ui; }
    
    public void setActive(boolean active)
    {
      if (!initialized) {
        initialized = true;
        at.getGlobalInfo();
      }
    }
    
    protected class MyUIHandler extends GUIUtilities.UIHandler
    {
      public void save_toc()
      {
        if (list.getSelectedIndex() < 0) {
          GUIUtilities.warning("Please select a value first");
          return;
        }
        
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int ret = chooser.showSaveDialog(Preferences.this);
        
        if (ret != JFileChooser.APPROVE_OPTION) {
          return;
        }
        
        File chosen = chooser.getSelectedFile();
        File f = new File(chosen, filename);
        
        if (f.exists()) {
          Object[] options = { "Yes", "No" };
          String msg = "Overwrite existing file (" + f + ") ?";
          int sel = 
            JOptionPane.showOptionDialog(null, 
                                         msg,
                                         "Warning",
                                         JOptionPane.DEFAULT_OPTION,
                                         JOptionPane.WARNING_MESSAGE,
                                         null,
                                         options,
                                         options[0]);        
          if (sel != 0) {
            return;
          }
        }
        
        // start toc
        try {
          FileOutputStream fos = new FileOutputStream(f);
          writer = new PrintWriter(fos);
          at.getTOC(list.getSelectedValue().toString(), new Parser());
          toc.setEnabled(false);
        } catch (Exception ioe) {          
          String msg = "Could not save to file: " + f;
          msg += "\n";
          msg += ioe.toString();
          GUIUtilities.warning(msg);
        }                                                                                 
      }
    } // TOCConfigPanel.MyUIHandler
    
    protected class Parser extends AudioTron.AbstractParser
    {
      public boolean parse(String content)
      {
        if (content.indexOf("[End TOC]") == 0) {
          return true;
        }
        
        writer.println(content);
        return true;
      }
      
      public void end(boolean error)
      {
        writer.close();
        toc.setEnabled(true);
      }
    }
  } // TOCConfigPanel
  
  protected class AboutConfigPanel extends AbstractConfigPanel
  {
    JPanel ui;

    public AboutConfigPanel()
    {
      uiHandler = new MyUIHandler();
      ui = new JPanel();
      
      GUIUtilities.setIndent(ui, AudioTronUI.INDENT_MAIN);
      Box blayout = Box.createVerticalBox();
      JPanel layout = new JPanel(new GridLayout(0, 1));
      AboutDialog.addLabels(layout);
      blayout.add(layout);
      blayout.add(Box.createVerticalStrut(5));
      AboutDialog.addText(blayout);
      ui.add(blayout, "Center");
    }
    
    public JComponent getUI() { return ui; }
  }
  
  protected static class TimeLabel 
  {
    int minutes;
    
    public TimeLabel(int minutes_)
    {
      minutes = minutes_;
    }
    
    public String toString()
    {
      StringBuffer b = new StringBuffer();      
      int hours = minutes/60;
      
      if (hours > 0) {
        b.append(hours);
        b.append(" hour");
        if (hours > 1) {
          b.append('s');
        }
      }
      
      int mins = minutes % 60;
      if (mins > 0) {
        if (hours > 0) {
          b.append(", ");
        }
        b.append(mins);
        b.append(" min");
      }
      
      return b.toString();
    }
  } // TimeLabel
  
  protected static class VolumeLabel
  {
    int volume;
    
    public VolumeLabel(int volume_)
    {
      volume = volume_;
    }
    
    public String toString()
    {
      StringBuffer b = new StringBuffer();      
      b.append(volume);
      b.append(" db");
      
      return b.toString();
    }
  } // VolumeLabel
}
