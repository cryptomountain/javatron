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
 * $Id: AudioTronPlaylistEditor.java,v 1.2 2002/08/05 04:50:30 tgautier Exp $
 */
 package JavaTron;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import javax.swing.JFileChooser;
import javax.swing.filechooser.*;

import java.util.*;
import java.lang.reflect.*;
import java.io.*;

/**
* Implements the Playlist Editor dialog box.
*/
public class AudioTronPlaylistEditor extends GUIUtilities.DockedDialog
{
  public static final String ADD_DIALOG_TITLE ="Add to Library";
  public static final String CLEAR_MSG =
    "Are you sure you want to clear the AudioTron playlist?";
  public static final String CLEAR_PLAYLIST_MSG =
    "Are you sure you want to clear this playlist?";

  boolean showExtra = false;
  boolean bigFont=false;

  Frame owner;
  Hashtable trees = new Hashtable();
  Hashtable editorLists = new Hashtable();

  MyUIHandler uiHandler;
  JPopupMenu treePopup;
  JTabbedPane itemSelector, editorSelector;

  int playlistIndex = 1;
  GUIUtilities.SelectorPanel editorSelectorPanel, itemSelectorPanel;
  Box isbbb1, isbbb2, isbbb3, esbbb1;

  JScrollPane sp1;
  JButton createNewPlaylist, addToLibrary;
  JToggleButton setTV;

  AudioTronState at;

  private Method setTabLayoutPolicyMethod;
  private Field tabLayoutField;
  private GUIUtilities.DockedDialog addSongDialog;

  /**
   * AudioTronPlaylistEditor Constructor
   *
   * @param at_ AudiotronState Object
   * @param owner_ Frame Object
   * @param label String Object
   * @param toggle JToggleButton Object
   */
  public AudioTronPlaylistEditor(AudioTronState at_,
                                 Frame owner_,
                                 String label,
                                 JToggleButton toggle)
  {
    super(owner_, label, toggle);
	owner = owner_;
    at = at_;
    uiHandler = new MyUIHandler();

    try {
    Class[] args = { int.class };
      setTabLayoutPolicyMethod =
        JTabbedPane.class.getMethod("setTabLayoutPolicy", args);
      try {
        tabLayoutField = JTabbedPane.class.getField("SCROLL_TAB_LAYOUT");
      } catch (NoSuchFieldException nsfe) {
        setTabLayoutPolicyMethod = null;
      }
    } catch (NoSuchMethodException nsme) {
      // ignore
    }

    // setup the content pane buffer
    GUIUtilities.setIndent(getContentPane(), AudioTronUI.INDENT_MAIN);

    Action playNow, enqueue, getinfo;
    playNow = uiHandler.createAction("Play Now");
    enqueue = uiHandler.createAction("Enqueue");
	getinfo = uiHandler.createAction("Get Info");

    treePopup = new JPopupMenu();
	//Font f = treePopup.getFont();
	//f=f.deriveFont((float)60);
	//System.out.println(treePopup.getFont().toString());
	treePopup.add(uiHandler.createJMenuItem(playNow));
    treePopup.add(uiHandler.createJMenuItem(enqueue));
	treePopup.add(uiHandler.createJMenuItem(getinfo));
    treePopup.add(new JSeparator());


    Box b2;

    JPanel contents = new JPanel(new BorderLayout());

    Box isb = Box.createVerticalBox();
    itemSelector = new JTabbedPane(JTabbedPane.TOP);
    if (setTabLayoutPolicyMethod != null) {
      try {
        Object[] args = { new Integer(tabLayoutField.getInt(itemSelector)) };
        setTabLayoutPolicyMethod.invoke(itemSelector, args);
      } catch (InvocationTargetException ite) {
        ite.printStackTrace();
      } catch (IllegalAccessException iae) {
        iae.printStackTrace();
      }
    }
    GUIUtilities.setFontSize(itemSelector, AudioTronUI.EDITOR_TITLE_FONT_SIZE);
    itemSelector.setMinimumSize(AudioTronUI.SELECTOR_MIN);
    itemSelector.setPreferredSize(AudioTronUI.SELECTOR_PREF);
    itemSelector.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        JTabbedPane tp = (JTabbedPane) e.getSource();
        at.fillTree(tp.getTitleAt(tp.getSelectedIndex()));
      }
    }
    );
    itemSelector.add("Genre", new JScrollPane(getJTree("Genre")));
    itemSelector.add("Album", new JScrollPane(getJTree("Album")));
    itemSelector.add("Artist", new JScrollPane(getJTree("Artist")));
    itemSelector.add("Fav", new JScrollPane(getJTree("Fav")));
    itemSelector.add("List", new JScrollPane(getJTree("List")));
    itemSelector.add("Title", new JScrollPane(getJTree("Title")));
    itemSelector.add("Web", new JScrollPane(getJTree("Web")));

    isb.add(itemSelector);
    isb.add(Box.createVerticalStrut(5));

    itemSelectorPanel = new GUIUtilities.SelectorPanel();
    isbbb1 = Box.createHorizontalBox();
    isbbb1.add(new GUIUtilities.Spacer(5, 0));  // weird Swing bug -- can't use Strut?!?!
    isbbb1.add(uiHandler.createJButton(playNow));
    isbbb1.add(Box.createHorizontalGlue());
    isbbb1.add(uiHandler.createJButton(enqueue));
    isbbb1.add(new GUIUtilities.Spacer(5, 0));

    isbbb2 = Box.createHorizontalBox();
    isbbb2.add(new GUIUtilities.Spacer(5, 0));
    isbbb2.add(uiHandler.createJButton("Add To Playlist"));
	isbbb2.add(Box.createHorizontalGlue());

	isbbb3 = Box.createHorizontalBox();
	isbbb3.add(new GUIUtilities.Spacer(5, 0));
    isbbb3.add(uiHandler.createJToggleButton("TV"));
	isbbb3.add(Box.createHorizontalGlue());

    isb.add(itemSelectorPanel.getUI());
    isb.add(Box.createVerticalStrut(5));

    Box esb = Box.createVerticalBox();
    editorSelector = new JTabbedPane(JTabbedPane.TOP);
    if (setTabLayoutPolicyMethod != null) {
      try {
        Object[] args = { new Integer(tabLayoutField.getInt(editorSelector)) };
        setTabLayoutPolicyMethod.invoke(editorSelector, args);
      } catch (InvocationTargetException ite) {
        ite.printStackTrace();
      } catch (IllegalAccessException iae) {
        iae.printStackTrace();
      }
    }
    GUIUtilities.setFontSize(editorSelector, AudioTronUI.EDITOR_TITLE_FONT_SIZE);
    editorSelector.setMinimumSize(AudioTronUI.EDITOR_MIN);
    editorSelector.setPreferredSize(AudioTronUI.EDITOR_PREF);
    esb.add(editorSelector);
    esb.add(Box.createVerticalStrut(5));

    // The current playlist ui
    Action playSelected, copyToPlaylist, removeSelected, getInfo, clear;
    playSelected = uiHandler.createAction("Play Selected");
	copyToPlaylist = uiHandler.createAction("Make into Playlist");
	removeSelected = uiHandler.createAction("Remove Selected");
	getInfo=uiHandler.createAction("Get Song Info");
	clear = uiHandler.createAction("Clear");
    JPopupMenu popup = new JPopupMenu();
    popup.add(uiHandler.createJMenuItem(playSelected));
	popup.add(uiHandler.createJMenuItem(removeSelected));
	popup.add(uiHandler.createJMenuItem(getInfo));
    popup.add(new JSeparator());
    popup.add(uiHandler.createJMenuItem(clear));
    sp1 = new JScrollPane(AudioTronUI.songChoice2);
    editorSelector.add("AT Play Queue", sp1);
    MouseListener ml = new PopupMouseAdapter(popup)
    {
	  @Override
      Component getComponent(MouseEvent e) { return AudioTronUI.songChoice2; }
      int getSelectedRow(MouseEvent e)
      {
        int row = AudioTronUI.songChoice2.locationToIndex(new Point(e.getX(), e.getY()));
        if (row != -1) {
          AudioTronUI.songChoice2.setSelectedIndex(row);
        }

        return row;
      }

	  int[] getSelectedRows(MouseEvent e){
		  int[] a=new int[1];
		  a[0]=0;
		  return(a);
	  }

      void doubleClick(MouseEvent e)
      {
        at.gotoIndex(AudioTronUI.songChoice2.getSelectedIndex());
      }
    };
    AudioTronUI.songChoice2.addMouseListener(ml);

    editorSelectorPanel = new GUIUtilities.SelectorPanel();
    esbbb1 = Box.createHorizontalBox();
    esbbb1.add(new GUIUtilities.Spacer(5, 0));
    esbbb1.add(uiHandler.createJButton(playSelected));
	esbbb1.add(uiHandler.createJButton(copyToPlaylist));
    esbbb1.add(Box.createHorizontalGlue());
    esbbb1.add(uiHandler.createJButton(clear));
    esbbb1.add(new GUIUtilities.Spacer(5, 0));
    esb.add(editorSelectorPanel.getUI());
    esb.add(Box.createVerticalStrut(5));

    // add a default playlist
    uiHandler.create_new_playlist();

    // setup and add the split plane to the window contents
    JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, isb, esb);
    sp.resetToPreferredSizes();
    contents.add(sp, "Center");

    // create the main editor  buttons
    Box bb = Box.createVerticalBox();
    bb.add(Box.createVerticalStrut(10));
    b2 = Box.createHorizontalBox();
    createNewPlaylist = uiHandler.createJButton("Create New Playlist");
	addToLibrary = uiHandler.createJButton("Add to Library");
	setTV = uiHandler.createJToggleButton("TV");

    b2.add(createNewPlaylist);
	b2.add(addToLibrary);
	b2.add(setTV);
    ButtonGroup bg = new ButtonGroup();
    JRadioButton r1, r2;
    r1 = uiHandler.createJRadioButton("Set Play");
    r2 = uiHandler.createJRadioButton("Set Enqueue");
    b2.add(Box.createHorizontalGlue());
    b2.add(uiHandler.createJButton("Close"));
    bb.add(b2);
    contents.add(bb, "South");

    // Add the completed contents window to the frame
    getContentPane().add(contents, "Center");

    // Add editor tabbed pane selector changelistener
    editorSelector.addChangeListener(new ChangeListener() {
    public void stateChanged(ChangeEvent e) { setButtons(); }
    });

    setButtons();

    pack();
    setSize(new Dimension(owner.getWidth(), AudioTronUI.EDITOR_HEIGHT));
    sp.setDividerLocation(AudioTronUI.SELECTOR_PREF.width);
  }

  /**
   * Sets up the Buttons
   */
  protected void setButtons()
  {
    if (editorSelector.getSelectedIndex() > 0) {
      itemSelectorPanel.setCurrent(isbbb2);
      editorSelectorPanel.setCurrent(getEditorList().getButtonBox());
    } else {
      itemSelectorPanel.setCurrent(isbbb1);
      editorSelectorPanel.setCurrent(esbbb1);
    }
  }

  /**
   * Method to retrieve the selected EditorList object
   * @return EditorList Object
   */
  public EditorList getEditorList()
  {
    String title =
		editorSelector.getTitleAt(editorSelector.getSelectedIndex());
    return getEditorList(title);
  }

  /**
   * Method to retrieve an EditorList object given a title
   * @param title A String Object
   * @return An Editorlist Object
   */
  public EditorList getEditorList(String title)
  {
    return (EditorList) editorLists.get(title);
  }

  public void setTVFont(boolean big){
	  /** FIXME **/
	  float size = big ? AudioTronUI.EDITOR_BIG_FONT_SIZE : AudioTronUI.EDITOR_FONT_SIZE;
	  Color fg = big ? Color.CYAN : Color.BLACK;
	  Color bg = big ? Color.black : Color.WHITE;
	  //Color sc = big ? Color.blue : Color.blue;

	  if(big){
		  String fgcolor=Configuration.getProperty(Configuration.KEY_TV_FONT_COLOR);
		  String bgcolor=Configuration.getProperty(Configuration.KEY_TV_BGCOLOR);
		  String fsize=Configuration.getProperty(Configuration.KEY_TV_FONT_SIZE);
		  String ffont=Configuration.getProperty(Configuration.KEY_TV_FONT);

		  if(fgcolor.equals("Yellow")){fg=Color.YELLOW;}
		  else if(fgcolor.equals("Black")){fg=Color.BLACK;}
		  else if(fgcolor.equals("Cyan")){fg=Color.CYAN;}
		  else if(fgcolor.equals("Green")){fg=Color.GREEN;}
		  else if(fgcolor.equals("Magenta")){fg=Color.MAGENTA;}
		  else if(fgcolor.equals("Light Gray")){fg=Color.LIGHT_GRAY;}
		  else if(fgcolor.equals("Dark Gray")){fg=Color.DARK_GRAY;}
		  else fg=JTP.parseRGB(fgcolor);

		  if(bgcolor.equals("White")){bg=Color.WHITE;}
		  else if(bgcolor.equals("Black")){bg=Color.BLACK;}
		  else if(bgcolor.equals("Blue")){bg=Color.BLUE;}
		  else if(bgcolor.equals("Yellow")){bg=Color.YELLOW;}
		  else if(bgcolor.equals("Green")){bg=Color.GREEN;}
		  else if(bgcolor.equals("Light Gray")){bg=Color.LIGHT_GRAY;}
		  else if(bgcolor.equals("Dark Gray")){bg=Color.DARK_GRAY;}
		  else bg=JTP.parseRGB(bgcolor);

		  size=(float)Integer.valueOf(fsize);

	  }
	  //Font tvFont=new Font(ffont,Font.PLAIN,Integer.valueOf(fsize));
	  GUIUtilities.setFontSize(itemSelector, size);
	  GUIUtilities.setFontSize(editorSelector, size);

	  int num = itemSelector.getTabCount();
	  for(int i =0; i< num ; i++){
		 JTree tree = getJTree(itemSelector.getTitleAt(i));
		 //tree.setFont(tvFont);
		 GUIUtilities.setFontSize(tree, size);
		 tree.setRowHeight(0);
		 tree.setForeground(fg);
		 tree.setBackground(bg);
		 //GUIUtilities.setFontSize(uiHandler.getPopup(),size);
		 DefaultTreeCellRenderer cellRenderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
		 cellRenderer.setBackgroundNonSelectionColor(bg);
		 //cellRenderer.setBackgroundSelectionColor(sc);
		 cellRenderer.setTextNonSelectionColor(fg);
		 //GUIUtilities.setBackground(tree, bg);
		 //System.out.println("CELL: "+ cellRenderer.getBackgroundNonSelectionColor().toString());

	  }

	  GUIUtilities.setFontSize(AudioTronUI.songChoice2,size);
	  AudioTronUI.songChoice2.setForeground(fg);
	  AudioTronUI.songChoice2.setBackground(bg);
	  //GUIUtilities.setFontSize(getEditorList("AT Play Queue").popup,size);

	  num = editorSelector.getTabCount();
	  for(int i=1; i<num; i++){
		  JList list = getEditorList(editorSelector.getTitleAt(i)).getJList();
		  list.setBackground(bg);
		  list.setForeground(fg);
		  GUIUtilities.setFontSize(list,size);
		  //JPopupMenu j=getEditorList(editorSelector.getTitleAt(i)).getPopup();
		  //j.setFont(j.getFont().deriveFont(size));
		  //GUIUtilities.setFontSize(j,size);

	  }
	  JavaTron j=JavaTron.getApplication();
	  AudioTronUI u=j.getUI();
	  u.changeFontSize(size);
	  bigFont=big;
	}

  /**
   * Class which defines custom UI Handlers
   */
  protected class MyUIHandler extends GUIUtilities.UIHandler
  {
    /**
	 *
	 */

	public class IndexedPlayableNode implements AudioTron.Playable
    {
      AudioTron.Playable node;
      ListModel model;
      Object stringObject;
      boolean useType = true;
      String type;

      /**
	   * IndexedPlayableNode Constructor
	   * @param node_ AudioTron.Playable
	   * @param model_ ListModel
	   */
	  public IndexedPlayableNode(AudioTron.Playable node_, ListModel model_)
      {
        node = node_;
        model = model_;

        if (node instanceof AudioTronState.IndexedPlayableTreeNode) {
          type = "Song";
          stringObject = ((AudioTronState.IndexedPlayableTreeNode)node).getUserObject();
          if (stringObject instanceof Adapter) {
            AudioTronSong song =
            (AudioTronSong) ((Adapter) stringObject).getSource();
            stringObject = song.getLongFormAdapter();
			System.out.println(stringObject);
          }
        } else {
          type = getType();
          stringObject = node;
        }
      }

	  /**
	   * Method to return the node's type
	   * @return A String Object
	   */
	  public String getType() { return node.getType(); }
	  /**
	   * Method to return the node's File
	   * @return A String Object
	   */
      public String getFile() { return node.getFile(); }
      /**
	   * Method to return whether a node is Playable or not
	   * @return true on Playable, false if not Playable
	   */
	  public boolean isPlayable() { return node.isPlayable(); }
	  /**
	   * Method to convert this to a song if possible
	   * @return An AudioTrongSong object or null if conversion fails
	   */
	  public AudioTronSong toSong(){
		  AudioTronSong s=new AudioTronSong();
		  if( node instanceof AudioTronState.IndexedPlayableTreeNode) {
				type = "Song";
				stringObject = ((AudioTronState.IndexedPlayableTreeNode)node).getUserObject();
				if (stringObject instanceof Adapter) {
					s =	(AudioTronSong) ((Adapter) stringObject).getSource();
					//System.out.println(stringObject);
					}
		  }
          return s;
	  }

      public String toString()
      {
        StringBuffer str = new StringBuffer();
        int i;

        for (i = 0; i < model.getSize(); i++) {
          if (model.getElementAt(i) == this) {
            break;
          }
        }
        if (i < model.getSize()) {
          AudioTronSong.appendIndex(str, i + 1);
          str.append(". ");
        }
        str.append(type);
        str.append(" : ");
        str.append(stringObject.toString());
        return str.toString();
      }
    } // End IndexedPlayableNode


	public void set_play() { }
    public void set_enqueue() { }

    public void close() { setVisible(false); }
    public void clear()
    {
      if (confirm(CLEAR_MSG)) {
        at.clear();
      }
    }

	public void tv(){
		if(AudioTronPlaylistEditor.this.bigFont) {
			AudioTronPlaylistEditor.this.bigFont = false;
		}else{
			AudioTronPlaylistEditor.this.bigFont = true;
		}
		AudioTronPlaylistEditor.this.setTVFont(AudioTronPlaylistEditor.this.bigFont);
	}

    public void play_selected()
    {
      at.gotoIndex(AudioTronUI.songChoice2.getSelectedIndex());
    }

	/**
	 * * JSC ***
	 * Remove Selected Songs from Playlist<br><br>
	 *
	 * This function needs to build a replica of the playlist in the format
	 * that is produced by the web server, get the index information that the
	 * web server will understand, then call the URL /goform/webQuePlayForm
	 * with POST data.<br>
	 *
	 * post info:<br>
	 *		currlist: 0 >>The current listing offset (see currurl)<br>
	 *		currurl: i.e. "/queue.asp?entries=50&index=0"<br>
	 *		currpage: "/queue.asp"<br>
	 *		QueDel: "Delete+From+Queue"<br>
	 *		entry5: 5	>>entry is base 10 index relative to the displayed page,<br>
	 *					>>value is the absolute list index in hex<br>
	 *					>>form.<br>
	 *					>>if there are multiple entries, there will be one of these<br>
	 *					>>per entry for each page, which means we'll have to do a seperate<br>
	 *					>>request for each "page" of selections<br>
	 *					>>WHY WASN'T THIS IN THE API?!?!???<br>
	 *					>><br><br>
	 *
	 * Example:<br>
	 *	assumption: there are 150 songs in the queue, and its displaying 50 per page<br>
	 *	assumption: we want to remove the 63rd and 64th<br>
	 *		then<br>
	 *	entry13: 3f								<< entry(63-50): 63base10 = 3Fbase16 ><br>
	 *  entry14: 40								<< entry(64-50): 64base10 = 40base16 ><br>
	 *	currlist: 1<br>
	 *	currurl: /queue.asp?entries=50&index=50	<< Offset of 50, 2nd page @ 50 per page ><br>
	 *	currpage: /queue.asp<br>
	 *  QueDel: Delete+From+Queue<br>
	 *
	 * TODO - Need to bulletproof multiple song deletions
	 *        Multiples are done with repetitive 'entryXX' entries per page
	 *
	 * 1. build a list of selected indexes divided by page
	 * 2. delete each page group starting from the last page
	 *    and working backwards.
	 * 3. need to do a check for the current song and toss it out, including
	 *    a check done just prior to the page containing the current song.?
	 *
	 *
	 */

	public void remove_selected(){
		EditorList pq=getEditorList("AT Play Queue");
		HashMap t=new HashMap(); // final POST information
		ArrayList<Integer> pagecount=new ArrayList<Integer>();
		int[] idxs=AudioTronUI.songChoice2.getSelectedIndices();
		// There's got to be a better way in java....
		// Bust 'em up - get a list of pages that contain objects
		// marked for removal, building the pages list as we go
		PageEntry[] pages=new PageEntry[idxs.length+1];
		for(int i=0;i<idxs.length;i++){
			pages[i]=new PageEntry(i,idxs[i]/50,idxs[i]);
			if(!pagecount.contains(new Integer(idxs[i]/50)))
				pagecount.add(idxs[i]/50);
			if(showExtra){System.out.println(pages[i].toString());}
		}

		// Start at the last page and remove everything from that one
		// Work our way backwards to the first page
		for(int i=pagecount.size()-1;i>-1;i--){
			int entrypage=pagecount.get(i).intValue();
			for(int x=0;x<pages.length-1;x++){
				if(showExtra){System.out.println(
							"X: "+x+
							", pages.page: "+ pages[x].getPage()+
							" entry page: "+entrypage
							);}
				if(pages[x].getPage()== entrypage){
					System.out.println("Adding " + pages[x].toString() + " to the delete list...");
					t.put("entry" + (pages[x].getWebIndex()-(entrypage*50)),
							Integer.toHexString(pages[x].getWebIndex())
						  );
				}
			}
			t.put("currpage", "/queue.asp");
			t.put("currlist", Integer.toString(entrypage) );
			t.put("currurl", "/queue.asp&entries=50&index="+entrypage*50);
			t.put("QueDel", "Delete From Queue");

			if(showExtra){
				System.out.println("===========================");
				System.out.println("nDeleting songs on page # "+entrypage);
			}
			Set set=t.entrySet();
			Iterator idx=set.iterator();
			while(idx.hasNext()) {
				Map.Entry me = (Map.Entry)idx.next();
				if(showExtra){
					System.out.print(me.getKey() + ": ");
					System.out.println(me.getValue());
				}
			}
			System.out.println("Removing...");
			at.dequeueFiles(t);
			t.clear();
		}
	}

	/**
	 * Pops up a Message Pane displaying song information
	 * Only works on first in a list right now
	 * This method is specific for querying from the Play Queue
	 * TODO: return information on multiple selections
	 *		needs a serious check for requests from the
	 *		'Web' tab. These are currently returning the ENTIRE
	 *		audiotron library.
	 *
	 */
	public void get_song_info(){
		String out=new String();
		int idx=AudioTronUI.songChoice2.getSelectedIndex();
		Object[] selection=AudioTronUI.songChoice2.getSelectedValues();
		String[] info=((AudioTronSong)selection[0]).getSongInfo();
		for(int i=0;i<info.length;i++){
			System.out.println( info[i]);
			out+="      "+info[i].replace(":", " : ").replace("null","") + "     \n";
		}
//		JOptionPane j=new JOptionPane(info[0],JOptionPane.INFORMATION_MESSAGE);
//		if(bigFont){
//			String fsize=Configuration.getProperty(Configuration.KEY_TV_FONT_SIZE);
//			float size=(float)Integer.valueOf(fsize);
//			j.setOpaque(true);
//			j.setFont( j.getFont().deriveFont(size) );
//		}
//
//		j.setVisible(true);
		JOptionPane.showMessageDialog(AudioTronPlaylistEditor.this, out,
           "   "+info[0], JOptionPane.INFORMATION_MESSAGE);

	}

	/**
	 * JSC - Report the Song Information
	 * TODO: Iron out error conditions
	 */

	public void get_info()
	{
		Object[] selectedItems=getSelectedItems();
		get_info(selectedItems);

	}

	// "This needs to stop BLOCKING!" - Leaving this comment because it was
	//									such a HASSLE finding info on this.
	// Ok..I don't know what's up with the Runnable Class, but it sure wasn't
	// doing the job.
	// PROBLEM: Blocking!
	// SOLUTION: Use a real inner thread class
	//
	// I'm using Object[] in anticipation of implementing multiple selection
	// For now...get the first one and show the file info.

	// Possible types by tab...
	// WEB: class JavaTron.AudioTronState$AudioTronTree$SongListTopLevelTreeNode
	// FAV: class JavaTron.AudioTronState$FavPlayableTreeNode
	// LIST: class JavaTron.AudioTronState$AudioTronTree$SongListTopLevelTreeNode
	// ARTIST: class JavaTron.AudioTronState$IndexedPlayableTreeNode
	// GENRE: class JavaTron.AudioTronState$IndexedPlayableTreeNode
	// TITLE: class JavaTron.AudioTronState$PlayableTreeNode
	// ALBUM: class JavaTron.AudioTronState$IndexedPlayableTreeNode
	//
	protected void get_info(final Object[] selected){
		class InfoGetter extends Thread {
			@Override
			public void run() {
				boolean doit=true;
				// check to make sure we're clicking on a Song!!!
				// There's got to be another way to check this.
				Object[] selectedItems=selected;
				System.out.println(selectedItems[0].getClass().toString());
				if(	   selectedItems[0].getClass().toString().equals("class JavaTron.AudioTronState$IndexedPlayableTreeNode")
					|| selectedItems[0].getClass().toString().equals("class JavaTron.AudioTronState$PlayableTreeNode")
					)
				{
					System.out.println("InfoGetter: PASSED");
				}else if(selectedItems[0].getClass().toString().equals("class JavaTron.AudioTronState$AudioTronTree$SongListTopLevelTreeNode")){
					DefaultMutableTreeNode tree=(DefaultMutableTreeNode)selectedItems[0];
					if(tree == null){
						System.out.println("NULL value");
						return;
					}
					if(tree.getParent().toString().equals("List")){
						System.out.println("I'm a List");
						JOptionPane.showMessageDialog(null,"Info for Lists not Implemented yet.");
						return;
					}else{
						JOptionPane.showMessageDialog(null,"NOT A Song");
						return;
					}
				}else if(selectedItems[0].getClass().toString().equals("class JavaTron.AudioTronState$FavPlayableTreeNode")){
						JOptionPane.showMessageDialog(null,"Info for Favorites not Implemented yet.");
						return;
				}else{
					System.out.println("InfoGetter: FAILED");
					JOptionPane.showMessageDialog(null,"NOT A Song");
					return;
				}
				/*
				if(selectedItems[0] instanceof DefaultMutableTreeNode){
					if(showExtra){System.out.println("TREE...");}
					DefaultMutableTreeNode tree=(DefaultMutableTreeNode)selectedItems[0];
					System.out.println(selectedItems[0].getClass() + ", " + getJTree("Genre").getClass());
					doit=tree.isLeaf()?true:false;
					if(doit){
						System.out.println("I came from the " + tree.getParent().getParent() + " tab");
					}
					if(showExtra){if(doit){System.out.println("...LEAF...");}else{ System.out.println("...NODE...");}}
				}
				 *
				 */
				String f=((AudioTron.Playable)selectedItems[0]).getFile();
				System.out.println("Getting info for "+f);

				if( ((AudioTron.Playable)selectedItems[0]).isPlayable() ){
					System.out.println("Passed isPlayable()...");
					if( doit ){
						String info[] = at.getSongInfoFromTitle(f);
						String info1=new String();
						for(int i=0;i<info.length-2;i++){
							if(info[i]!=null){
								info[i]=info[i].replace(":"," : ");
								info1+=info[i].replace("null", "")+"\n";
							}
						}
						JOptionPane.showMessageDialog(AudioTronPlaylistEditor.this, info1,
											f, JOptionPane.INFORMATION_MESSAGE);
					}
				}else
					JOptionPane.showMessageDialog(null,"NOT A Song");
			}

		} // end InfoGetter

		InfoGetter r=new InfoGetter();
		r.start();

	}

	/**
	 * Retrieves the selected Items in the song library lists
	 * @return an Object[]
	 */
	protected Object[] getSelectedItems()
    {
      JTree tree = getJTree(itemSelector.getTitleAt(itemSelector.getSelectedIndex()));
      TreePath[] paths = tree.getSelectionPaths();
      Object[] selectedItems = new Object[paths.length];

      for (int i = 0; i < paths.length; i++) {
        selectedItems[i] = paths[i].getLastPathComponent();
		System.out.println(selectedItems[i].getClass());
      }

      return selectedItems;
    }

    /**
	 * Adds Songs to an EditorList
	 * @param list EditorList
	 * @param items Object[]
	 */
	void addItems(EditorList list, Object[] items)
    {
      for (int i = 0; i < items.length; i++) {
        if (items[i] instanceof AudioTron.Playable) {

          items[i] =
			new IndexedPlayableNode((AudioTron.Playable) items[i], list);
        }

        try {
          if (!((AudioTron.Playable) items[i]).isPlayable()) {
            continue;
          }
        } catch (ClassCastException cce) {
          // ignore
        }

        list.addElement(items[i]);
      }
    }

    public void play_now() { play_now(getSelectedItems()); }
    protected void play_now(Object item)
    {
      Object[] items = { item };
      play_now(items);
    }

    public void play_now(Object[] items)
    {
      Vector list = new Vector();
      for (int i = 0; i < items.length; i++) {
        list.addElement(items[i]);
      }

      at.playFile(list.elements());
    }

    public void add_to_playlist()
    {
      EditorList el=getEditorList();
	  Object[] si=getSelectedItems();
	  //System.out.println("add_to_playlist():"+el.toString());
	  //add_to_playlist(el);
	  addItems(el,si);

    }

	public void add_to_playlist(EditorList list)
	{
		addItems(list,getSelectedItems());
	}

	public void add_to_playlist(String playlistTitle){
		EditorList el=getEditorList(playlistTitle);
		add_to_playlist(el);
	}

	public void enqueue() { enqueue(getSelectedItems()); }
    public void enqueue(Object item)
    {
      Object[] items = { item };
      enqueue(items);
    }
    public void enqueue(Object[] items) { at.queueFile(items); }

    /**
	 * Creates a new playlist, it's corresponding tab and popup Menu entry
	 */
	public void create_new_playlist()
    {
      EditorList editorList = new EditorList();
      editorSelector.add(editorList.getTitle(), editorList.getUI());

      //Class[] args = { editorList.getClass() };
	  Class[] args = { String.class };
	  // TODO: Fix this - JSC
	  System.out.println(args[0].toString());
      Action a = uiHandler.createAction("Add To " + editorList.getTitle(),
                                       "Add To Playlist",
                                       args);

      treePopup.add(uiHandler.createJMenuItem(a));

    }

	public JPopupMenu getPopup(){
		return treePopup;
	}
	/**
	 * Implements apiaddfile.asp
	 *
	 * @param fileURL
	 */
	public void add_to_library(String fileURL)
	{
		System.out.println("Adding a song?");
		JOptionPane.showMessageDialog(null, "Feature Not Implemented");
		//JFileChooser jfc=new JFileChooser();

	}

	public void make_into_playlist()
	{
		EditorList list=getEditorList("Playlist 1");
		try{
			make_into_playlist(list);
		}catch(NullPointerException e){
			if(list==null)
				System.out.println("List is NULL");
			e.printStackTrace();
		}
	}

	/**
	 * Reads current PlayQ, Saves list as M3U playlist file and copies
	 * the PlayQ to an EditorList
	 *
	 * @param list an EditorList
	 */
	public void make_into_playlist(EditorList list)
	{
		AudioTronSong s;
		File savefile;
		String filename;
		Enumeration<AudioTronSong> queue=at.getSongQueue();
		while(queue.hasMoreElements()){
			 s = queue.nextElement();
			 System.out.println("Title: " + s.getTitleAdapter().toString());
			 list.addElement(s);
		}
		Songs2M3U playlist=new Songs2M3U(at);
		playlist.makeM3U();
		JFileChooser chooser=new JFileChooser();
		chooser.setDialogTitle("Save M3U file");
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		//FileSystemView v=chooser.getFileSystemView();
		//chooser.setFileHidingEnabled(false);

		int returnVal = chooser.showDialog(AudioTronPlaylistEditor.this,"Save");
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			savefile=chooser.getSelectedFile();
			System.out.println("You chose to save to this file: " +
				chooser.getSelectedFile().getName());
			filename=savefile.toString();
			if(filename.endsWith((".M3U")) )
				filename=filename.substring(0,filename.length()-4);
			if(!filename.toString().endsWith(".m3u"))
				filename+=".m3u";
			if(playlist.saveToFile(filename)){
				System.out.println("File Written");
			}

		}else{
			System.out.println("Save Cancelled");
		}
		//playlist.showBuffer();


	}
  } // End MyUIHandler Class
/*
 * ****************************************************************************************
 */
  /**
   * Class that can Edit a Playlist
   *
   */
  protected class EditorList extends VectorListModel
  {
    JList list;
    String title;
    Box editorContents;
    Box editorButtons;
    MyUIHandler uiHandler;
    JPopupMenu popup;
    Action up, down, remove, clear, save;


	public EditorList() {
      super();

      uiHandler = new MyUIHandler();

      list = new JList(this);
	  //list.setDragEnabled(true);
	  GUIUtilities.setFontSize(list, AudioTronUI.EDITOR_FONT_SIZE);
      GUIUtilities.setFontType(list, Font.PLAIN);

      up = uiHandler.createAction("Move Up");
      down = uiHandler.createAction("Move Down");
      remove = uiHandler.createAction("Remove");
      clear = uiHandler.createAction("Clear", "Clear Playlist");
	  save = uiHandler.createAction("Save M3U");

      popup = new JPopupMenu();
      popup.add(uiHandler.createJMenuItem(up));
      popup.add(uiHandler.createJMenuItem(down));
      popup.add(new JSeparator());
      popup.add(uiHandler.createJMenuItem(remove));
      popup.add(new JSeparator());
      popup.add(uiHandler.createJMenuItem(clear));

      editorContents = Box.createVerticalBox();
      editorContents.add(new JScrollPane(list));
      editorButtons = Box.createHorizontalBox();
      editorButtons.add(new GUIUtilities.Spacer(5, 0));
      editorButtons.add(uiHandler.createJButton(remove));
      editorButtons.add(uiHandler.createJButton(clear));
	  editorButtons.add(new GUIUtilities.Spacer(5, 0));
	  editorButtons.add(uiHandler.createJButton(up));
	  editorButtons.add(uiHandler.createJButton(down));
      editorButtons.add(Box.createHorizontalGlue());
      editorButtons.add(uiHandler.createJButton("Play Now"));
	  editorButtons.add(uiHandler.createJButton(save));
      editorButtons.add(new GUIUtilities.Spacer(5, 0));

	  // add editor list mouse adapter to remove items when double-clicked
      // and to activate popup menu
      MouseListener ml = new PopupMouseAdapter(popup) {
        Component getComponent(MouseEvent e) { return list; }
        int getSelectedRow(MouseEvent e)
        {
          int row = list.locationToIndex(new Point(e.getX(), e.getY()));
          if (row != -1) {
            list.setSelectedIndex(row);
            up.setEnabled(row != 0);
            down.setEnabled(row != list.getModel().getSize() - 1);
          }

          return row;
        }

		int[] getSelectedRows(MouseEvent e){
			int[] a=new int[1];
			a[0]=0;
			return(a);
		}

        void doubleClick(MouseEvent e)
        {
          uiHandler.remove();
        }
      };
      list.addMouseListener(ml);

      title = "Playlist " + (editorLists.size()+1);
      editorLists.put(title, this);
    }

	/**
	 * Adds an element to the List
	 * @param element An Object
	 */
    public void addElement(Object element) {
      super.addElement(element);

      if (getSize() == 1) {
        list.setSelectedIndex(0);
      }
    }

    public Container getUI() { return editorContents; }
    public Container getButtonBox() { return editorButtons; }
    public JList getJList() { return list; }
    public String getTitle() { return title; }
	public JPopupMenu getPopup(){ return popup; }

    /**
	 * Custom UIHandler for an EditorList
	 */
	protected class MyUIHandler extends GUIUtilities.UIHandler
    {
      public void move_up()
      {
        moveElementUp(list.getSelectedIndex());
        list.setSelectedIndex(list.getSelectedIndex()-1);
      }

      public void move_down()
      {
        moveElementDown(list.getSelectedIndex());
        list.setSelectedIndex(list.getSelectedIndex()+1);
      }

      // editor buttons
      public void remove()
      {
        int[] indices = list.getSelectedIndices();
        for (int i = 0; i < indices.length; i++) {
          removeElementAt(indices[i] - i);
        }

        if (getSize() > 0) {
          if (indices[0] < getSize()) {
            list.setSelectedIndex(indices[0]);
          } else {
            list.setSelectedIndex(indices[0]-1);
          }
        }
      }

      public void clear_playlist()
      {
        if (confirm(CLEAR_PLAYLIST_MSG)) {
          clear();
        }
      }

      public void play_now() { at.playFile(elements()); }

	  public void save_m3u(){
		  String filename;
		  File savefile;
		  AudioTronSong s=new AudioTronSong();

		  ArrayList<AudioTronSong> songList = new ArrayList<AudioTronSong>(list.getModel().getSize());
		  for(int i=0;i < list.getModel().getSize();i++){
			  System.out.println(i+": "+list.getModel().getElementAt(i).getClass());
			  if(list.getModel().getElementAt(i) instanceof
									AudioTronPlaylistEditor.MyUIHandler.IndexedPlayableNode )
			  {
				  s=new AudioTronSong();
				  AudioTronPlaylistEditor.MyUIHandler.IndexedPlayableNode x=
						(AudioTronPlaylistEditor.MyUIHandler.IndexedPlayableNode)list.getModel().getElementAt(i);
				  if(x.getType().equalsIgnoreCase("Song") || x.getType().equalsIgnoreCase("Title")){
					  System.out.println(i+": IPN Converted...");
					  s=x.toSong();
				  }else if(x.getType().equalsIgnoreCase("List")){
					  System.out.println(i+": List Detected....Thats over my head!");
					  //TODO: need to expand list and create Songs
				  }else{
					  System.out.println(i+": "+x.getType()+" Detected... Ouch");
				  }
			  }else{
				  System.out.println(i+": Direct Conversion...");
				  s=(AudioTronSong)list.getModel().getElementAt(i);
			  }
			  System.out.println("FINAL : "+s.toString());
			  if(!s.toString().equalsIgnoreCase("Unknown"))
				songList.add(s);
		  }
		  Songs2M3U m3u=new Songs2M3U(at);
		  m3u.makeM3U(songList);

		  JFileChooser chooser=new JFileChooser();
		  chooser.setDialogTitle("Save M3U file");
		  chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		  //chooser.setFileHidingEnabled(false);
		  int returnVal = chooser.showDialog(AudioTronPlaylistEditor.this,"Save");
		  if(returnVal == JFileChooser.APPROVE_OPTION) {
			savefile=chooser.getSelectedFile();
			System.out.println("You chose to save to this file: " +
				chooser.getSelectedFile().getName());
			filename=savefile.toString();
			if(filename.endsWith((".M3U")) )
				filename=filename.substring(0,filename.length()-4);
			if(!filename.toString().endsWith(".m3u"))
				filename+=".m3u";
			if(m3u.saveToFile(filename)){
				System.out.println("File Written");
			}

		}else{
			System.out.println("Save Cancelled");
		}
	  }
    }
  } // End editorList Class

  /******************************************************************************************/
  protected boolean confirm(String msg)
  {
    Object[] options = { "Yes", "No" };

    int sel =
      JOptionPane.showOptionDialog(null,
                                   msg,
                                   "Warning",
                                   JOptionPane.DEFAULT_OPTION,
                                   JOptionPane.WARNING_MESSAGE,
                                   null,
                                   options,
                                   options[0]);

    return (sel == 0);
  }

  protected JTree getJTree(String label)
  {
    JTree tree = (JTree) trees.get(label);
	if (tree == null) {

      tree = new JTree(at.getTree(label));
      GUIUtilities.setFontSize(tree, AudioTronUI.EDITOR_FONT_SIZE);
      //tree.setShowsRootHandles(true);
      tree.setDoubleBuffered(true);
      tree.setToggleClickCount(0);
      tree.addTreeExpansionListener(at.getTree(label));
      MouseListener ml = new PopupMouseAdapter(treePopup)
      {
        Component getComponent(MouseEvent e) { return (JTree) e.getSource(); }
        int getSelectedRow(MouseEvent e)
        {
          JTree t = (JTree) e.getSource();
          int row = t.getRowForLocation(e.getX(), e.getY());
          if (row != -1) {
            t.setSelectionRow(row);
          }

          return row;
        }

		//Testing a theory -JSC

		int[] getSelectedRows(MouseEvent e){
			JTree t = (JTree)e.getSource();
			int[] rows = t.getSelectionRows();
			return rows;
		}

        void doubleClick(MouseEvent e)
        {
          JTree t = (JTree) e.getSource();
          int row = t.getRowForLocation(e.getX(), e.getY());
          if (row != -1) {
            TreePath path = t.getPathForLocation(e.getX(), e.getY());
            if (editorSelector.getSelectedIndex() > 0) {
              uiHandler.add_to_playlist();
            } else {
              switch (AudioTronUI.getDefaultDoubleClick()) {
                case AudioTronUI.PLAY_NOW:
                  uiHandler.play_now();
                  break;
                case AudioTronUI.ENQUEUE:
                  uiHandler.enqueue();
                  break;
              }
            }
          }
        }
      };
      tree.addMouseListener(ml);
      TreeSelectionListener tsl = new TreeSelectionListener() {
        public void valueChanged(TreeSelectionEvent e) {
          JTree tree = (JTree) e.getSource();
          tree.scrollPathToVisible(e.getNewLeadSelectionPath());
        }
      };
      tree.addTreeSelectionListener(tsl);
      trees.put(label, tree);
    }

    return tree;
  }

  protected abstract static class PopupMouseAdapter extends MouseAdapter
  {
    JPopupMenu popup;

    public PopupMouseAdapter(JPopupMenu popup_) { popup = popup_; }

    abstract Component getComponent(MouseEvent e);
    abstract int getSelectedRow(MouseEvent e);
	abstract int[] getSelectedRows(MouseEvent e);

    void doubleClick(MouseEvent e) { }

    int popup(MouseEvent e)
    {
      /*
	  int row = getSelectedRow(e);
	  if (row >= 0 && popup != null) {
        popup.show(getComponent(e), e.getX(), e.getY());
      }
      return row;
      */

		/*
		 * JSC
		 */
		System.out.println("popup!");
		int[] rows=getSelectedRows(e);
		if(rows[0]>=0 && popup != null){
			popup.show(getComponent(e),e.getX(),e.getY());
			//System.out.println("Boing!");

		}
		return rows[0];

    }

    public void mousePressed(MouseEvent e)
    {
      if (e.isPopupTrigger()) {
        popup(e);
        return;
      }
      if (e.getClickCount() == 2) {
        doubleClick(e);
      }
    }

    public void mouseReleased(MouseEvent e)
    {
      if (e.isPopupTrigger() && popup(e) < 0 && popup != null) {
        popup.setVisible(false);
      }
    }
  } // PopupMouseAdapter
}
