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
 * $Id: Configuration.java,v 1.1 2002/08/05 00:06:41 tgautier Exp $
 */
package JavaTron;

import java.util.*;
import java.io.*;

/**
 * Implements the ability to load and save a properties file for persistent
 * configuration capabitilities.
 *
 * @author Taylor Gautier
 * @version $Revision: 1.1 $
 */
public class Configuration
{
  static final String PROPERTIES_FILENAME     = "javatron.props";
  
  // AudioTron keys
  public static final String KEY_USERNAME				= "username";
  public static final String KEY_SERVER					= "server";
  public static final String KEY_PASSWORD				= "password";
  public static final String KEY_BASE_M3U_FILE			= "base_m3u_file";
  public static final String KEY_DEFAULT_PLAYLIST_PATH	= "default_playlist_loc";
  
  // AudioTronPlayListEditor keys
  public static final String KEY_DEFAULT_DOUBLE_CLICK	= "default_double_click";
  public static final String KEY_CONFIRM_OVERWRITE		= "confirm_overwrite";

  // RadioEditorPanel keys
  public static final String KEY_RADIO_USERNAME		= "rad_username";
  public static final String KEY_NET_USERNAME		= "net_username";
  public static final String KEY_NET_PASSWORD		= "net_password";
  public static final String KEY_RADIO_HOST			= "rad_host";
  public static final String KEY_RADIO_SHARE		= "rad_share";

  // NetConfig keys
  public static final String KEY_NET_MASK		= "net_mask";
  public static final String KEY_NET_GATEWAY	= "net_gateway";
  public static final String KEY_NET_DNS		= "net_dns";
  public static final String KEY_NET_DHCP		= "dhcp";

  // NetFilePanel keys
  public static final String KEY_WIN_USERNAME	= "win_username";
  public static final String KEY_WIN_PASSWORD	= "win_password";
  public static final String KEY_WIN_98PASSWORD	= "win_98password";

  //TV Mode keys
  public static final String KEY_TV_FONT		= "tv_font";
  public static final String KEY_TV_FONT_SIZE	= "tv_font_size";
  public static final String KEY_TV_FONT_COLOR	= "tv_font_color";
  public static final String KEY_TV_BGCOLOR		= "tv_bgcolor";

  static Properties props = new Properties();

  // Listeners
  static Vector listeners = new Vector();
  
  static {
    loadProperties();
  }
    
  public static String getProperty(String key)
  {
    return props.getProperty(key);
  }
  public static void setProperty(String key, String value)
  {
    props.setProperty(key, value);
    for (Enumeration e = listeners.elements(); e.hasMoreElements(); ) {
      ((ChangeListener) e.nextElement()).propertyChanged(key);
    }
  }
  public static int getIntProperty(String key)
  {
    String s = getProperty(key);
    if (s != null) { 
      return new Integer(s).intValue();
    }
    
    return 0;
  }
  public static void setIntProperty(String key, int value)
  {
    setProperty(key, new Integer(value).toString());
  }

  public static boolean getBoolProperty(String key){
	  String s = getProperty(key);
	  if (s != null){
		  if(s.toLowerCase().equalsIgnoreCase("true")
				  || s.equalsIgnoreCase("t")
				  || s.equalsIgnoreCase("1")
				  || s.equalsIgnoreCase("Y")
				  ){
			return true;
		  }else{
			  return false;
		  }
	  }
	  return false;
  }

  public static void setBoolProperty(String key, boolean value){
	  if(value){
		  setProperty(key,"true");
	  }else{
		  setProperty(key,"false");
	  }

  }

  public static Properties getProperties() { return props; }
  
  public static boolean loadProperties()
  {
    File f = new File(PROPERTIES_FILENAME);
    FileInputStream fis = null;
    
    try {
      fis = new FileInputStream(f);
      
      props.load(fis);
    } catch (FileNotFoundException fnfe) {
      return false;
    } catch (IOException ioe) {
      ioe.printStackTrace();
      return false;
    } finally {
      try {
        fis.close();
      } catch (Exception e) {
        // ignore
      }
    }
    
    return true;
  }
  
  public static boolean saveProperties()
  {
    File f = new File(PROPERTIES_FILENAME);
    FileOutputStream fos = null;
    
    try {
      fos = new FileOutputStream(f);
      
      props.store(fos, "JavaTron Properties");
    } catch (FileNotFoundException fnfe) {
      fnfe.printStackTrace();
      return false;
    } catch (IOException ioe) {
      ioe.printStackTrace();
      return false;
    } finally {
      try {
        fos.close();
      } catch (Exception e) {
        // ignore
      }
    }
    
    return true;
  }
  
  public static void setDefault(Properties defaultProperties)
  {
    String key;
    
    for (Enumeration e = defaultProperties.keys(); e.hasMoreElements(); ) {
      key = e.nextElement().toString();
      if (getProperty(key) == null) {
        setProperty(key, defaultProperties.getProperty(key));
      }
    }
  }
  
  public static interface ChangeListener
  {
    public void propertyChanged(String key);
  }
  
  public static void addChangeListener(ChangeListener listener)
  {
    listeners.add(listener);
  }
  
  public static void removeChangeListener(ChangeListener listener)
  {
    listeners.remove(listener);
  }
  
  private Configuration() { }
}
