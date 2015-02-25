/* Copyright (C) 2001-2002 Taylor Gautier
 * Portions Copyright (C) 2010 Joe Culbreth
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
 * $Id: AudioTronUI.java,v 1.1 2002/08/05 00:06:41 tgautier Exp $
 */
package JavaTron;



import org.jdesktop.application.Application;
import org.jdesktop.application.FrameView;

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
 * AudioTron user interface class.
 *
 * @author Taylor Gautier
 * @version $Revision: 1.1 $
 */
public class AudioTronUI extends org.jdesktop.application.FrameView
  implements ItemListener,
             AudioTronListener,
             ChangeListener
{
  // constants
  private static final String me = "JavaTron " + Version.version;
  private static final String EDITOR_TITLE = "JavaTron Playlist Editor";
  private static final String PREFS_TITLE = "JavaTron Preferences";
  private static final String ABOUT_TITLE = "About " + me;
  private static final String WEB_TITLE = "Audiotron Web Interface";
  
  // ui constants
  protected static final int   INDENT_MAIN            = 5;
  protected static final float BUTTON_FONT_SIZE       = (float) 10;
  protected static final float EDITOR_FONT_SIZE       = (float) 10;
  protected static final float EDITOR_BIG_FONT_SIZE	  = (float) 24;
  protected static final float EDITOR_TITLE_FONT_SIZE = (float) 11;
  protected static final float EDITOR_BIG_TITLE_FONT_SIZE = (float) 22;
  protected static final int   EDITOR_HEIGHT          = 400;

  protected static final int SS_MAX              = 1000;
  protected static final int SS_MAJOR            = SS_MAX/5;
  protected static final int SS_MINOR            = SS_MAX/50;
  
  protected static final Dimension EDITOR_MIN    = new Dimension(245, 50);
  protected static final Dimension EDITOR_PREF   = new Dimension(245, 200);
  protected static final Dimension SELECTOR_MIN  = new Dimension(180, 50);
  protected static final Dimension SELECTOR_PREF = new Dimension(230, 200);
  protected static final Dimension PROGRESS_SIZE = new Dimension(65, 14);
  
  protected static final int PLAY_NOW = 1;
  protected static final int ENQUEUE  = 2;
  
  // The AudioTron controller component
  private static AudioTronState at;
  private ATState state = ATState.UNKNOWN;
  private AudioTronSong currentSong, nextSong;
  
  // ui components
  private JFrame f;
  private GUIUtilities.DockedDialog editor, prefsDialog;
  private ATWebDisplayDlg atDlg;
  private JComboBox songChoice;
  protected static JList songChoice2;
  private JButton prev, stop, play, pause, next, search;
  private JCheckBox repeat, mute, random;
  private JLabel status, nextLabel, atclock;
  private JToggleButton pl, prefs, dlg;
  private JSlider songSlider;
  private JProgressBar progress;

  // ui vars
  private int height;
  private boolean isIconified = false;
  private boolean isBigFont = false;
  private GUIUtilities.DockedDialog currentDialog;
  private boolean songChoiceNotification = false;
  private boolean songSliderNotification = false;     
  private MyUIHandler uiHandler;

  // Clock ui
  private String time = "00:00:00";
  private String nowplaying = "01. Gerry Rafferty";
  // 1.4 specific stuff
  private Method progressMethod;
  
  /**
   * AudioTronUI constructor
   *
   * @param at_ an initialized AudioTron component
   *		app an Application Class
   */
  public AudioTronUI(AudioTronState at_, Application app)
  {
	super(app);
	  at = at_;
    
    // Initialize the ui handler (button clicks etc.)
    uiHandler = new MyUIHandler();

    // Initialize defaults
    setDefaults();
    
    // Initialize the song choice combo box
    songChoice = new JComboBox(at.getSongQueueModel());
    GUIUtilities.setFontType(songChoice, Font.PLAIN);
	GUIUtilities.setFontSize(songChoice,EDITOR_FONT_SIZE);
    songChoice.setDoubleBuffered(true);
    songChoice.setSelectedIndex(0);
    
    // Initialize the playlist song choice list
    songChoice2 = new JList(at.getSongQueueModel());
    GUIUtilities.setFontType(songChoice2, Font.PLAIN);
    GUIUtilities.setFontSize(songChoice2, EDITOR_FONT_SIZE);
    
    // Initialize the status bar
    status = GUIUtilities.createJLabel("Updating Status fo");
    status.setBorder(BorderFactory.createLoweredBevelBorder());    
    status.setDoubleBuffered(true);

    // Initialize next status label
    nextLabel = GUIUtilities.createJLabel("A very very very very very very very very very long song name");
    nextLabel.setDoubleBuffered(true);

	// Initialize the clock
	atclock = GUIUtilities.createJLabel("   "+time+"                                 "+nowplaying+"              ");
	atclock.setDoubleBuffered(true);

    try {
      Class[] args = { boolean.class };
      progressMethod = JProgressBar.class.getMethod("setIndeterminate", args);
    } catch (NoSuchMethodException nsme) {
      // ignore
    }
  }
  
  /**
   * Call this to initialize the UI and show it
   */
  public void go()
  {
    JPanel p;
    Box b, b2;
    
    // Initialize the main frame window
    f = new JFrame(me);

	f.setIconImage(new ImageIcon(getClass().getResource("resources/mainIcon.png")).getImage());
    f.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {System.exit(0);}
        public void windowIconified(WindowEvent e) { iconified(); }
        public void windowDeiconified(WindowEvent e) { deiconified(); }
    });
    f.getContentPane().setLayout(new BorderLayout());
    
    // Setup a grid panel that will layout the following components vertically
    Box windowContents = Box.createVerticalBox();
    JPanel buffer = new JPanel(new BorderLayout());
    GUIUtilities.setIndent(buffer, INDENT_MAIN);
    
    buffer.add(windowContents, "Center");   
    f.getContentPane().add(buffer, "Center");
    
    // Setup the song choice
    windowContents.add(songChoice);
    windowContents.add(Box.createVerticalStrut(5));
    
    // Setup the checkboxes for repeat,random,mute
    Box hb = Box.createHorizontalBox();
    Box vb1 = Box.createVerticalBox();
    Box vb2 = Box.createVerticalBox();
    
    random = uiHandler.createJCheckBox("Random");
    repeat = uiHandler.createJCheckBox("Repeat");
    mute = uiHandler.createJCheckBox("Mute");
    progress = new JProgressBar(JProgressBar.HORIZONTAL);
    progress.setAlignmentX(mute.getAlignmentX());
    progress.setStringPainted(true);
    progress.setFont(mute.getFont());
    progress.setMaximumSize(PROGRESS_SIZE);
    progress.setPreferredSize(PROGRESS_SIZE);
    vb1.add(random); vb1.add(repeat);
    vb1.add(Box.createVerticalGlue());
    vb2.add(mute);
    vb2.add(Box.createVerticalGlue());
    vb2.add(progress);
    vb2.add(Box.createVerticalStrut(5));
    
	// Setup the clock - JSC
	Box cb = Box.createHorizontalBox();
	cb.setBackground(Color.black);
	cb.setForeground(Color.green);
	cb.setOpaque(true);
	atclock.setFont(new Font("Arial",1,14));
	atclock.setOpaque(true);
	atclock.setBackground(Color.black);
	atclock.setForeground(Color.green);
	TitledBorder cborder=
			BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "");
	cb.setBorder(cborder);
	cb.add(atclock);

    // Setup the next label
    JPanel lb = new JPanel(new GridLayout(0, 1));
    Box lbb = Box.createVerticalBox();      
    TitledBorder tborder =
      BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                                       "Next Up");
    tborder.setTitleFont(mute.getFont());
    lb.setBorder(tborder);
    nextLabel.setFont(mute.getFont());
    nextLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
    lbb.add(nextLabel);
    lbb.add(Box.createVerticalGlue());
    lb.add(lbb);
    
    // add the checkboxes, progress bar and label
    hb.add(vb1);
    hb.add(vb2);
	hb.add(Box.createHorizontalGlue());
	hb.add(lb);
    //windowContents.add(cb);
	windowContents.add(hb);
    windowContents.add(Box.createVerticalStrut(5));
    
    // Setup the raised area
    JPanel rb = new JPanel(new GridLayout(0, 1));
    rb.setBorder(BorderFactory.createRaisedBevelBorder());
    Box rbb = Box.createVerticalBox();
    rb.add(rbb);

    // Setup the song slider
    Box ssb = Box.createHorizontalBox();
    songSlider = new JSlider(0, SS_MAX, 0);
    songSlider.setDoubleBuffered(true);
    songSlider.setMajorTickSpacing(SS_MAJOR);
    songSlider.setMinorTickSpacing(SS_MINOR);
    songSlider.setPaintTicks(true);
    songSlider.setPaintLabels(true);
    disableSongSlider();
    ssb.add(Box.createHorizontalStrut(4));      
    ssb.add(songSlider);
    ssb.add(Box.createHorizontalStrut(4));
    
    // Setup the play buttons      
    Box pb = Box.createHorizontalBox();
    prev = uiHandler.createJButton("|< Prev");
    stop = uiHandler.createJButton("O Stop"); 
    play = uiHandler.createJButton("Play >"); 
    pause = uiHandler.createJButton("|| Pause"); 
    next = uiHandler.createJButton("Next >|"); 
    pb.add(Box.createHorizontalStrut(4));
    pb.add(prev); pb.add(play);  
    pb.add(pause); pb.add(stop);  
    pb.add(next);      

    // add song slider and play buttons to the raised bar
    rbb.add(Box.createVerticalStrut(5));
    rbb.add(ssb);
    rbb.add(Box.createVerticalStrut(5));            
    rbb.add(pb);
    rbb.add(Box.createVerticalStrut(5));
    windowContents.add(rb);
                      
    // Setup PL Button
    b = Box.createHorizontalBox();
    pl = uiHandler.createJToggleButton("PlayList");
    b.add(pl);
    
    // Setup the Pref button
    prefs = uiHandler.createJToggleButton("Prefs");
    b.add(prefs);
    //b.add(Box.createHorizontalStrut(5));

	// Setup the Web viewer button
	dlg = uiHandler.createJToggleButton("View Web");
	//b.add(dlg);
	//b.add(Box.createHorizontalStrut(5));

	// Add the pl, pref buttons to button tray
    pb.add(Box.createHorizontalStrut(15));
    pb.add(Box.createHorizontalGlue());                  
    pb.add(b);
    
    // Pack and show the frame
    f.pack();        
    height = f.getHeight();
    // add just enough for 2 playlists
    f.setSize(new Dimension(f.getWidth()+10, height));
    f.setVisible(true);

    // add the component listener only after frame is shown...
    // or else we get message we aren't ready to handle
    f.addComponentListener(
      new ComponentAdapter() {
        public void componentMoved(ComponentEvent e) {
          if (currentDialog != null && currentDialog.isVisible()) {
            if (!GUIUtilities.dockDialog(f, currentDialog)) {
              currentDialog.docked = 
                GUIUtilities.invertDocked(GUIUtilities.checkDock(f, currentDialog));
            }
          }
        }        
        public void componentResized(ComponentEvent e) {
            f.setSize(f.getWidth(), height);
        }
      }
    );       
    
    // make sure labels stay the current size
    status.setPreferredSize(status.getSize());
    nextLabel.setPreferredSize(nextLabel.getSize());
    nextLabel.setText("");
        
    // Initialize the status    
    setStatus(at.getStatus());
    
    // Add ourselves as a listener to at
    at.addAudioTronListener(this);
  }
  
  private void iconified()
  {
    isIconified = true;
    setTitle();
  }
  
  private void deiconified() 
  {
    isIconified = false;
    setTitle();
  }
   
  private void setTitle()
  {
    if (isIconified && state.isPlaying() && currentSong != null) {
      f.setTitle(currentSong.getShortFormAdapter().toString());
    } else {
      f.setTitle(state.toString());
    }
  }

  public boolean isBigFont(){
	  return isBigFont;
  }

  private void toggleBigFont(){
	  setBigFont(!isBigFont);
  }
  private void setBigFont(boolean isbig){
	  isBigFont=isbig;
  }

  public void changeFontSize(float size){
	//GUIUtilities.setFontSize(f, size);
	//GUIUtilities.setFontSize(editor,
	//GUIUtilities.setFontSize(prefsDialog, size);
	//private ATWebDisplayDlg atDlg;
	height=isBigFont ? height-40 : height+40;
	GUIUtilities.setFontSize(songChoice, size+1);
	GUIUtilities.setFontSize(songChoice2, size+1);
	System.out.println("Choice Height:"+songChoice.getHeight());
	if(size > 20)
		songChoice.setSize(songChoice.getWidth(),33);
	else
		songChoice.setSize(songChoice.getWidth(),20);
	//private JButton prev, stop, play, pause, next, search;
	//private JCheckBox repeat, mute, random;
	//private JLabel status, atclock;
	System.out.println("NextLabel: "+nextLabel.getHeight());
	GUIUtilities.setFontSize(nextLabel, size);
	if(isBigFont)
		nextLabel.setSize(nextLabel.getWidth(),18);
	else
		nextLabel.setSize(nextLabel.getWidth(),15);
	//private JToggleButton pl, prefs, dlg;
	//GUIUtilities.setFontSize(songSlider, size);
	//GUIUtilities.setFontSize(progress, size);
	songChoice.updateUI();
	f.setSize(f.getWidth(), height);
	toggleBigFont();
  }

  protected class MyUIHandler extends GUIUtilities.UIHandler
  {
    public void ___prev() { at.prev(); }
    public void o_stop() { at.stop(); }
    public void play__() { at.play(); }
    public void ___pause() { at.pause(); }
    public void next___() { at.next(); }
    public void random() { at.random(); }
    public void repeat() { at.repeat(); }
    public void mute() { at.mute(); } 
    
    public void prefs()
    {      
      if (prefsDialog == null) {
        prefsDialog = new Preferences(at, f, PREFS_TITLE, prefs);
        prefsDialog.setDocked(GUIUtilities.BOTTOM_LEFT);
      }
      setCurrentDialog(prefsDialog);
    }
            
    public void playlist()
    {
      if (editor == null) {
        editor = new AudioTronPlaylistEditor(at, f, EDITOR_TITLE, pl);
        editor.setDocked(GUIUtilities.BOTTOM_LEFT);
      }  
      setCurrentDialog(editor);
    }

	public void view_web()
	{
		String wc="<html><head></head><body><h1>Retrieving Information...</h1><h3>Don't get too excited!</h3></body>";
		if(atDlg == null) {
			atDlg=new ATWebDisplayDlg(at,f,WEB_TITLE,dlg,wc);
			atDlg.setDocked(GUIUtilities.RIGHT_TOP);

		}
		setCurrentDialog(atDlg);
		atDlg.getInfo("/filecfg.asp");

	}

	public void search()
	{
	  at.search();
	}
  } // END OF MyUIHandler /********************************************************/
 
  void setCurrentDialog(GUIUtilities.DockedDialog dialog)
  {
    if (currentDialog == dialog) {
      if (currentDialog.isShowing()) {
        currentDialog.setVisible(false);
      } else {
        GUIUtilities.dockDialog(f, currentDialog);
        currentDialog.setVisible(true);
      }
      return;
    }
    
    if (currentDialog != null) {
      currentDialog.setVisible(false);
	  //f.setSize(f.getWidth(),height);
    }
    currentDialog = dialog;
    GUIUtilities.dockDialog(f, dialog);
    dialog.setVisible(true);
  }
  
  /**
   * An item state changed.  If it is selected, the user changed
   * the selection, so go to the new selection.
   *
   * @param e obj the object that is performing the action
   */ 
	@Override
  public void itemStateChanged(ItemEvent e)
  {
    if (e.getSource() == songChoice &&
        e.getStateChange() == ItemEvent.SELECTED) {
      at.gotoIndex(songChoice.getSelectedIndex());
	  if(songChoice.getFont().getSize() > 20)
		  songChoice.setSize(songChoice.getWidth(),33);
	  else
		  songChoice.setSize(songChoice.getWidth(),20);
    }
	System.out.println("SongChoice,State:"+songChoice.getFont().getSize());
	System.out.println("SongChoice,State:"+songChoice.getHeight());
  }
  /**
   * 
   * @see setStatus(String, boolean);
   */
  private void setStatus(String status)
  {
    setStatus(status, false);
  }

  /**
   * Sets the status for the application
   * 
   * @param status_ the string to set the status to
   */
  private void setStatus(String status_, boolean longRunning)
  { 
    progress.setString(status_);

    if (progressMethod != null) {
      try {
        Object[] args = { new Boolean(longRunning) };
        progressMethod.invoke(progress, args);
      } catch (IllegalAccessException iae) {
        // ignore
      } catch (InvocationTargetException ite) {
        // ignore
      }
    }
  }
  
  /**
   * ChangeListener implementation -- listen for changes to song slider.
   *
   */
  public void stateChanged(ChangeEvent e)
  {
    if (!songSlider.getValueIsAdjusting()) {
      int pct = Math.round(songSlider.getValue()*100/SS_MAX);
      at.setSongPosition(pct);
      currentSong.setEstimatedPosition((long) (pct*currentSong.total)*10-2000);      
    }
  }

  /**
   * Enable/Disable us as the action listener for the song chooser
   */
  private synchronized void songChoiceNotification(boolean enable)
  {
    if (!enable) {
      songChoice.removeItemListener(this);
    } else {
      if (!songChoiceNotification) {
        songChoice.addItemListener(this);
      }    
    }
    songChoiceNotification = enable;
  }

  private void setSongSliderLabels(int total)
  {            
    Hashtable labels = new Hashtable();
    for (int i = 0; i <= SS_MAX; i += SS_MAJOR) {
      labels.put(new Integer(i), 
                 GUIUtilities.createJLabel(AudioTronSong.time(total*i/SS_MAX)));
    }
    songSlider.setLabelTable(labels);
  }

  private void enableSongSlider()
  {
    if (currentSong != null) {      
      setSongSliderLabels(currentSong.total);
    }
    songSlider.setEnabled(true);
    songSliderNotification(true);
  }
  
  private void disableSongSlider()
  {
    songSliderNotification(false);
    songSlider.setValue(0);
    setSongSliderLabels(0);      
    songSlider.setEnabled(false);
  }  

  private synchronized void songSliderNotification(boolean enable)
  {
    if (!enable) {
      songSlider.removeChangeListener(this);
    } else {
      if (!songSliderNotification) {
        songSlider.addChangeListener(this);
      }
    }
    songSliderNotification = enable;
  } 
       
  private void setNextLabel()
  {
    if (nextSong == null) {
      nextLabel.setText("");
      return;
    }
    
    nextLabel.setText(" " + nextSong);
  }

  /******************************************************************/
  /* AudioTronListener interface                                    */
  /******************************************************************/
  public void mute(final boolean muted_)
  {
    Runnable setMute = new Runnable() {
      public void run() {      
        mute.setSelected(muted_);
      }
    };
    SwingUtilities.invokeLater(setMute);
  }
  
  public void repeat(final boolean repeat_)
  {
    Runnable setRepeat = new Runnable() {
      public void run() {        
        repeat.setSelected(repeat_);
      }
    };
    SwingUtilities.invokeLater(setRepeat);
  }
  
  public void random(final boolean random_)
  {
    Runnable setRandom = new Runnable() {
      public void run() {
        random.setSelected(random_);
      }
    };
    SwingUtilities.invokeLater(setRandom);
  }
  
  public void state(final ATState st)
  {
    Runnable changeState = new Runnable() {     
      public void run() {            
        if (state == ATState.UNKNOWN || state != st) {
          boolean enabled = st != ATState.ERROR;
          random.setEnabled(enabled);
          repeat.setEnabled(enabled);
          mute.setEnabled(enabled);
          songChoice.setEnabled(enabled);
          pl.setEnabled(enabled);
          if (editor != null && !enabled) {
            editor.setVisible(false);
          }
                              
          if (st.isPlaying()) {            
            stop.setEnabled(true);
            pause.setEnabled(true);
            prev.setEnabled(true);
            next.setEnabled(true);
            play.setEnabled(false);
            enableSongSlider();      
            setNextLabel();
          } else {
            nextLabel.setText("");
            play.setEnabled(enabled);
            stop.setEnabled(false);
            pause.setEnabled(false);
            prev.setEnabled(false);
            next.setEnabled(false);
            disableSongSlider();
          }
          
          /* this is a debatable feature -- current firmware makes
           * the behavior pretty bad because the AT hangs for a long time
           * and responds with "Error 6 - Play timeout"
           
           if (st == ATState.PAUSED) {
            play.setEnabled(true);
          }
          */
        }
        state = st;
        setTitle();
        f.setIconImage(state.getImage());
      }
    };
    SwingUtilities.invokeLater(changeState);
  }

  public void version(String version) {}
  
  public void currentSong(AudioTronSong song)
  {
    currentSong = song;
    
    Runnable setCurrentSong = new Runnable() {
      public void run() {
        setCurrentSong();
      }
    };
    SwingUtilities.invokeLater(setCurrentSong);
  }
  
  private synchronized void setCurrentSong()
  {
    // set the songChoice to the correct index
    songChoiceNotification(false);
    songChoice.setSelectedItem(currentSong);
    songChoice2.setSelectedIndex(0); // some weird JList bug...
    songChoice2.setSelectedValue(currentSong, true); 
    songChoiceNotification(true);
    if(isBigFont)
		songChoice.setSize(songChoice.getWidth(),33);
	else
		songChoice.setSize(songChoice.getWidth(),20);
    songChoice.paintImmediately(songChoice.getVisibleRect());
    
    setSongSliderLabels(currentSong.total);
  }

  public void nextSong(AudioTronSong song)
  {
    nextSong = song;
    
    Runnable setNextSong = new Runnable() {
      public void run() {
        setNextLabel();
      }   
    };
    SwingUtilities.invokeLater(setNextSong);
  }
  
  public void songPositionUpdated(AudioTronSong song)
  {
    // update the song slider pos
    
    // the currentSong total should never be 0, but there is a bug
    // in apigetstatus.asp that allows it to happen so add this 
    // guard    
    
    setTitle();
    
    if (currentSong == null || 
        song != currentSong) {
      return;
    }
    
    Runnable setSongPosition = new Runnable() {
      public void run() {
        setSongPosition();
      }
    };
    SwingUtilities.invokeLater(setSongPosition);
  }    
  
  private synchronized void setSongPosition()
  {
    // checks for currentSong.total == 0 are due to a bug in 
    // firmware 2.1.13
    if (!songSlider.getValueIsAdjusting()) {
      songSlider.setEnabled(currentSong.total > 0);
      
      int pos = currentSong.total == 0 ? 0 :
                (int) (currentSong.estimatedPos/currentSong.total*SS_MAX/1000);
      songSliderNotification(false);
      songSlider.setValue(pos);
      songSliderNotification(true);    
    }
  }
  
  /**
   * Set the status for the status bar
   *
   * @param status_ the status string
   * @param longRunning true if the status indicates a long-running process
   */     
  public void status(final String status_, final boolean longRunning)
  {
    Runnable setStatus = new Runnable() {
      public void run() {
        setStatus(status_, longRunning);
      }
    };
    SwingUtilities.invokeLater(setStatus);
  }

  /******************************************************************/
  /* End AudioTronListener interface                                */
  /******************************************************************/
  
  /******************************************************************/
  /* Preferences stuff                                              */
  /******************************************************************/
  
  protected void setDefaults()
  {
    Properties defaults = new Properties();
    defaults.setProperty(Configuration.KEY_DEFAULT_DOUBLE_CLICK, new Integer(PLAY_NOW).toString());
    
    Configuration.setDefault(defaults);
  }
  
  protected static int getDefaultDoubleClick()
  {
    return Configuration.getIntProperty(Configuration.KEY_DEFAULT_DOUBLE_CLICK);
  }
}
