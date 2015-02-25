/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
* Implements the Playlist Editor dialog box.
*/

/**
 * Skelton for External Dialog Classes
 * @author Joe Culbreth
 */
public class AudioTronDlg extends GUIUtilities.DockedDialog{

	public static AudioTronState at;
	public static Frame owner;
	public static String label;

	MyUIHandler uiHandler;

	/**
	 * AudioTronDlg Constructor
	 * @param at_ An AudioTronState Object
	 * @param owner_ A Frame Object
	 * @param label A String
	 * @param toggle A JToggleButton
	 */
	public AudioTronDlg(AudioTronState at_,
                                 Frame owner_,
                                 String label,
                                 JToggleButton toggle)
  {
    super(owner_, label, toggle);
	owner = owner_;
    at = at_;
    uiHandler = new MyUIHandler();
	}



	protected class MyUIHandler extends GUIUtilities.UIHandler{

	}
}
