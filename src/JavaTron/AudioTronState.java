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
 * $Id: AudioTronState.java,v 1.1 2002/08/05 00:06:41 tgautier Exp $
 */
package JavaTron;

import java.net.*;
import java.io.*;

import java.lang.*;
import java.lang.reflect.*;

import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

/** 
 * AudioTron stateful interface class.  This class maintains state by
 * updating at periodic intervals.  It uses a priority queue to all commands
 * to supercede any update requests.  If a command is sent during an update, 
 * the update is canceled and requeued.
 * <p>
 * To get useful information about the state of the AudioTron, register an
 * AudioTronListener with this object.  The AudioTronListener will then get
 * notifications wrt to state changes.
 *
 * @author Taylor Gautier
 * @version $Revision: 1.1 $
 */
public class AudioTronState extends AudioTron
{
  private static final int STATUS = LAST_STATUS+1;
  private static final int PLAYQ  = STATUS+1;
  
  Updater updater;
  
  // state variables
  private boolean isMute, isRepeat, isRandom;
  private SongQueue songQueue;
  private ShareList shareList;
  private AudioTronSong currentSong, nextSong;
  protected ATState state;
  
  private int dirty = 0;

  // Parse stuff
  StatusParser statusParser;
  PlayQueueParser playQueueParser;  
    
  // The set of AudioTronListener's
  Vector listeners = new Vector();
  
  /**
   * Zero parameter version of constructor -- default settings for address,
   * username, and password.  Sets up the thread and other initializations.
   */
  public AudioTronState()
  {
    this(null);
  }
  /**
   * One parameter version of constructor -- allows setting of AudioTron
   * address (default username and password)
   *
   * @param server_ the AudioTron ip address (defaults to 192.168.0.10)
   */
  public AudioTronState(String server_)
  {
    this(server_, null);
  }
  /**
   * Two parameter version of constructor -- allows setting of AudioTron
   * address and username (default password).
   *
   * @param server_ the AudioTron ip address (defaults to 192.168.0.10) 
   * @param username_ the username to use when accessing AudioTron pages
   *                  (defaults to 'admin')
   */
  public AudioTronState(String server_, String username_)
  {
    this(server_, username_, null);
  }  
  /**
   * Three parameter version of constructor -- allows setting of AudioTron
   * address, username, and password.
   *
   * @param server_ the AudioTron ip address (defaults to 192.168.0.10) 
   * @param username_ the username to use when accessing AudioTron pages
   *                  (defaults to 'admin')
   * @param password_ the password to use when accessing the AudioTron pages
   *                  (defaults to 'admin')
   */
  public AudioTronState(String server_, String username_, String password_)
  {
    super(server_, username_, password_);

    try {
      // hack -- set song listeners
      AudioTronSong.at = this;
      
      // Setup song queue
      songQueue = new SongQueue();
      resetSongQueue(0);

      shareList = new ShareList();
      
      // Refresh status to make sure everything is good
      refreshStatus();

      // Initialize the parse methods
      statusParser = new StatusParser();
      playQueueParser = new PlayQueueParser();
      
      // Initialize status map
      statusMap.put(new Integer(STATUS), "Updating");
      statusMap.put(new Integer(PLAYQ), "PlayQ");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Start the update thread
   */
  @Override
  public void start()
  {
    super.start();  

    // Initialize the updater
    updater= new Updater();
  }
  
  /**
   * Add an AudioTronListener to this instance
   *
   * @param listener the listener to add
   */
  public void addAudioTronListener(AudioTronListener listener)
  {
    listeners.addElement(listener);
  }

  /**
   * Remove an AudioTronListener from this instance
   *
   * @param listener the listener to remove
   */
  public void removeAudioTronListener(AudioTronListener listener)
  {
    listeners.removeElement(listener);
  }
  
  /**
   * Return an enumeration of the song queue songs.  (Each element should
   * be of type AudioTronSong.
   */
  public Enumeration getSongQueue()
  {
    return songQueue.elements();
  }

  /**
   * Update song information in our queue and notify any listeners.
   * This will only update if the new song info is "newer" than the existing
   * info.
   *
   * @param song the song to update
   *
   * @returns the song in the playlist that corresponds to the one passed in 
   */
  private AudioTronSong updateSong(AudioTronSong song)
  {
    if (song == null) {
      return null;
    }
    
    AudioTronSong current = 
      (AudioTronSong) songQueue.getElementAt(song.index-1);
    if (current.shouldUpdate(song)) {
      current.update(song);
      songQueue.updated(song.index-1);
    }
    
    return current;
  }
  
  /**
   * Clear the song queue
   */
  private void resetSongQueue(int total)
  {
    songQueue.reset(total);
    currentSong = AudioTronSong.NULL_SONG;
    nextSong = null;
  }

  /**
   * Generate a new song queue with the specified total
   * number of songs and notify any listeners.
   *
   * @param total the total number of songs to put in the new queue
   */
  private void newSongQueue(int total)
  {
    resetSongQueue(total);
    updater.setUpdateSongQueue(true);
  }

  // overridden
  @Override
  public void clear(boolean update)
  {
    super.clear(update);
    playQueueParser.resetProgress();

    if (update) {
      updater.setUpdateSongQueue(true);
    }
  }

  // overridden
	@Override
  public void gotoIndex(int index)
  {
    super.gotoIndex(index);
   
    if (!state.isPlaying()) {
      play();
    }
  }
  
  public void getGlobalInfo()
  {
    getGlobalInfo(new GlobalParser());
  }

  /**
   * Get a listing of the shares (each element will of ???
   * type)
   *
   * @return an Enumeration of the network share the Audiotron is using
   *
   */
  public Enumeration getShares(){
	  int i=0;
	  if(shareList.getSize() < 1){
		  while( shareList.getSize() < 1  &&  i < 10){
			  System.out.println(i+" #Shares = "+shareList.getSize());
		  	  getGlobalInfo();
			  try{
				  Thread.sleep(200);
			  }catch(Exception e){

			  }
			  i++;
		  }
		  //JOptionPane.showMessageDialog(null,"Shares not Enumerated!");
  	  }
	  return shareList.elements();
  }
  /**
   * Add song information to the song queue.
   *
   * @param song the song to add to the queue
   */
  private void addSong(AudioTronSong song)
  {
    songQueue.addElement(song);
  }
 
  /** 
   * Update the current song playing, if an update is made, notify any 
   * listeners.  This will only update if the current song index is 
   * different than the passed in song index.
   *
   * @param song the song to update the current song with
   */
  private void updateCurrentSong(AudioTronSong song)
  {    
    // second term is a workaround for apigetstatus.asp TotalTime bug
    // (although it's probably not a bad idea overall)
    if (!currentSong.shouldUpdate(song) && currentSong.index == song.index) {
      return;
    }
    
    AudioTronSong newSong = updateSong(song);
    if (newSong != currentSong) {
      if (currentSong != AudioTronSong.NULL_SONG) {
        currentSong.setPlaying(false);
      }
    } 
    currentSong = newSong;
    currentSong.setPlaying(true);    
    
    AudioTronListener listener;
    for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
      listener = (AudioTronListener) e.nextElement();
      listener.currentSong(currentSong);
    }
  }
 
  /** 
   * Update the next song playing, if an update is made, notify any 
   * listeners.  This will only update if the next song index is 
   * different than the passed in song index.
   *
   * @param song the song to update the next song with
   */
  private void updateNextSong(AudioTronSong song)
  {
    if (song != null &&
        nextSong != null &&
        !nextSong.shouldUpdate(song) && 
        nextSong.index == song.index) {
      return;
    }

    nextSong = updateSong(song);

    AudioTronListener listener;
    for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
      listener = (AudioTronListener) e.nextElement();
      listener.nextSong(nextSong);
    }
  }

  /**
   * Broadcast the new current state
   */
  private void updateState()
  {
    if (currentSong != AudioTronSong.NULL_SONG) {
      currentSong.setPlaying(state.isPlaying());
    }
    if (!state.isPlaying()) {
      currentSong = AudioTronSong.NULL_SONG;
    }
    AudioTronListener listener;
    for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
      listener = (AudioTronListener) e.nextElement();
      listener.state(state);
    }
  }
    
  /**
   * Update state of muted and notify listeners if state is changed
   *
   * @param state new state to set muted to
   */
  private void updateMute(boolean state)
  {
    isMute = state;
    
    AudioTronListener listener;
    for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
      listener = (AudioTronListener) e.nextElement();
      listener.mute(isMute);
    }
  }

  /**
   * Update state of repeat and notify listeners if state is changed
   *
   * @param state new state to set repeat to
   */
  private void updateRepeat(boolean state)
  {
    isRepeat = state;
    
    AudioTronListener listener;
    for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
      listener = (AudioTronListener) e.nextElement();
      listener.repeat(state);
    }
  }

  /**
   * Update state of random and notify listeners if state is changed
   *
   * @param state new state to set random to
   */
  private void updateRandom(boolean state)
  {
    isRandom = state;

    AudioTronListener listener;
    for (Enumeration e = listeners.elements(); e.hasMoreElements();) {
      listener = (AudioTronListener) e.nextElement();
      listener.random(state);
    }
  }

  /** 
   * Return a text string describing the status of the AudioTron
   * 'engine' status.
   *
   * @return string describing internal state
   */
  public String getStatus()
  {
    String status = super.getStatus();
    if (status.equals("") && dirty > 0) {
      return "Dirty";
    }

    return status;
  }

  public void setATMessage(String line1, String line2, int timeout){
	  Vector commandArgs = new Vector();
	  commandArgs.add("line1");
	  commandArgs.add(line1);
	  commandArgs.add("line2");
	  commandArgs.add(line2);
	  if(timeout > 0){
		commandArgs.add("timeout");
		commandArgs.add(timeout);
	  }

	  GetCommand c=new GetCommand("/apimsg.asp",commandArgs,null,MSG);
	  c.invoke();

  }

/**
 * Fetches the song information for a song with a selected Title
 * This should never return a list of songs, but the information for
 * a single song.
 *
 * This is COMPLETLY WRONG...it BLOCKS...but right now it works.
 * I'll fix it when I figure out what Taylor is doing with the commands
 * or when I understand threading a little better.
 * -JSC
 *
 * @param Title The Song Title (String) from the tree list
 * @return A String[] containing the Song information
 */
  public String[] getSongInfoFromTitle(String Title){

	  InfoParser parser=new InfoParser();
	  parser.listName="Song List";
	  getFilteredInfo("title",Title,parser);
	  // Wait for the server to return the information (yea...I'm blocking)
	  // (yea...It needs to be fixed) (yea...it really sucks)
	  while(parser.s==null){}
	  for(int i=0;i<parser.s.getSongInfo().length;i++)
		System.out.println(parser.s.getSongInfo()[i]);

	  return parser.s.getSongInfo();
  }

  /**
   * Implements Parsing a Song entry from the Library
   * NOTE!! This doesn't conform to Taylor's design (Its close). I'm still trying
   * to figure out what the returns are for....
   * TODO: redesign for consistency
   *
   * JSC
   */
  protected class InfoParser extends SongListParser{
	  public AudioTronSong s;
	  public void test(){
		  System.out.println("InfoParser.test");
	  }

	  @Override
	  protected boolean handleParse(String content){
		
		if (content.indexOf("Error=") == 0) {
			return true;
		}

		if(content.indexOf("[Song List]") == 0){
			return true;
		}

		if(content.indexOf("[Song]") == 0){
			currentParseSong=new AudioTronSong();
			return true;
		}

		if (content.indexOf("[End Song]") == 0) {
			if(currentParseSong == null)
				System.out.println("handleParse: currentParseSong == NULL");

			return songParsed();
		}

		if (content.indexOf("[End Song List]")== 0){
			return(listDone());
		}

		if (super.handleParse(content)) {
			return true;
		}
		return false;

	  }

	  @Override
	  protected boolean songParsed(){

		  String[] info=currentParseSong.getSongInfo();
		  for(int i=0;i<info.length;i++){
			  System.out.println("SongParsed: "+info[i]);
		  }
		  s=new AudioTronSong();
		  if(currentParseSong == null)
			  System.out.println("SongParsed: currentParseSong == null");
		  else
			s.update(currentParseSong);
		  if(s == null)
				System.out.println("SongParsed: s == null");
		  return true;
	  }

	  public boolean listDone(){
		  return true;
	  }

  }

  /**
   * Implements parsing the apigetinfo.asp?type=global page
   */
  protected class GlobalParser extends DynamicFieldParser
  {
    public void begin(String error)
    {
      if (error == null) {
        shareList.clear();
      }
    }
    
    public boolean fieldShare(String content)
    {
      shareList.addElement(content);
      return true;
    }
    
    protected boolean handleParse(String content)
    {
      if (content.indexOf("[Global Info]") == 0) {
        return true;
      }
      if (content.indexOf("[End Global Info]") == 0) {
        return true;
      }
      if (content.indexOf("[Share List]") == 0) {
        return true;
      }
      if (content.indexOf("[End Share List]") == 0) {
        return true;
      }
      if (content.indexOf("[Host List]") == 0) {
        return true;
      }
      if (content.indexOf("[Host]") == 0) {
        return true;
      }
      if (content.indexOf("[End Host]") == 0) {
        return true;
      }
      if (content.indexOf("[End Host List]") == 0) {
        return true;
      }
      
      return super.handleParse(content);
    }
  }
   
  /**
   * Implements parsing the apigetstatus.asp page
   */ 
  protected class StatusParser extends DynamicFieldParser
  {
    private AudioTronSong currentParseSong;
    private int songPos;
    private int queueTotal;
    private boolean next;
    private long playQUpdateTime, lastPlayQUpdateTime = 0;
    
    protected boolean fieldCurrPlayTime(String arg)
    {
      songPos = new Integer(arg).intValue();
      return true;
    }
    
    protected boolean fieldPlayQUpdateTime(String arg)
    {
      playQUpdateTime = new Long(arg).longValue();
      return true;
    }
    
    protected boolean fieldRANDOM_LED(String arg)
    {
      updateRandom(arg.equals("1"));
      
      return true;
    }
    
    protected boolean fieldMUTE_LED(String arg)
    {
      updateMute(arg.equals("1"));
      
      return true;
    }
    
    protected boolean fieldREPEAT_LED(String arg)
    {
      updateRepeat(arg.equals("1"));
      return true;
    }
    
    protected boolean fieldState(String newstate)
    {
      state = ATState.parse(newstate);
      
      updateState();
      
      return true;
    }
    
    
    protected boolean fieldQueueLen(String arg)
    {
      if (playQUpdateTime > lastPlayQUpdateTime) {
        int size = new Integer(arg).intValue();

        lastPlayQUpdateTime = playQUpdateTime;
        newSongQueue(size);       
      }
      
      return true;
    }
    
    protected boolean fieldCurrIndex(String arg)
    {
      currentParseSong = new AudioTronSong();
      currentParseSong.index = new Integer(arg).intValue()+1;
      return true;
    }
    
    protected boolean fieldTotalTime(String arg)
    {
      currentParseSong.total = new Integer(arg).intValue();
      
      // this is a bug with firmware 2.1.13 -- it sometimes returns
      // 0.  When it does the current position is also screwed so
      // set it to 0
      if (currentParseSong.total == 0) {
        songPos = 0;
      }
      return true;
    }
    
    protected boolean fieldTitle(String arg)
    {
      currentParseSong.title = arg;
      return true;
    }
    
    protected boolean fieldArtist(String arg)
    {
      currentParseSong.artist = arg;
      return true;
    }
    
    protected boolean fieldAlbum(String arg)
    {
      currentParseSong.album = arg;
      return true;
    }
    
    protected boolean fieldGenre(String arg)
    {
      currentParseSong.genre = arg;
      return true;
    }
    
    protected boolean fieldSource(String arg)
    {
      currentParseSong.source = arg;
      return true;
    }
    
    protected boolean fieldSourceID(String arg)
    {
      currentParseSong.sourceLocation = arg;
      
      // this is a bug in the current 2.1.13 software
      if (currentParseSong.index <= 0) {
        return true;
      }
      
      // hack -- this is the end of current song parsing
      updateCurrentSong(currentParseSong);
      
      return true;
    }
    
    // this happens if there is no next song
    protected boolean fieldNextItem(String arg)
    {
      updateNextSong(null);
      return true;
    }
    
    protected boolean fieldNextIndex(String arg)
    {
      currentParseSong = new AudioTronSong();
      currentParseSong.index = new Integer(arg).intValue()+1;
      return true;
    }
    
    protected boolean fieldNextTitle(String arg)
    {
      currentParseSong.title = arg;
      return true;
    }
    
    protected boolean fieldNextArtist(String arg)
    {
      currentParseSong.artist = arg;
      return true;
    }
    
    protected boolean fieldNextAlbum(String arg)
    {
      currentParseSong.album = arg;
      return true;
    }
  
    protected boolean fieldNextGenre(String arg)
    {
      currentParseSong.genre = arg;
      return true;
    }
  
    protected boolean fieldNextSource(String arg)
    {
      currentParseSong.source = arg;
      return true;
    }
  
    protected boolean fieldNextSourceID(String arg)
    {
      currentParseSong.sourceLocation = arg;
      
      // hack -- this is the end of the next song parsing
      updateNextSong(currentParseSong);
      
      return true;
    }
    
    /**
     * Parse the returned data from the api "apigetstatus.asp" page.
     * This is a parse callback method.
     *
     * @param content line-by-line content of the data returned
     *
     * @return true if the parse is successful, false otherwise
     */
    protected boolean handleParse(String content)
    {
      if (content.indexOf("Error=") == 0) {
        return true;
      }
      if (content.indexOf("[Status]") == 0) {
        next = false;
        return true;
      }
      if (content.indexOf("[End Status]") == 0) {
        if (currentSong != AudioTronSong.NULL_SONG) {
          currentSong.setPosition(songPos);
        }
  
        return true;
      }
      
      return super.handleParse(content);
    }
  }
  
  /**
   * Implements parsing the playback queue
   */
  protected class PlayQueueParser extends SongListParser
  {
    private int progress = 0;

    public PlayQueueParser()
    {
      listName = "Play Queue";
    }
    
    protected boolean handleParse(String content)
    {
      if (content.indexOf("empty") >= 0) {
        resetSongQueue(0);
        return true;
      }
      if (content.indexOf("[Play Queue]") == 0) {
        return true;
      }
      if (content.indexOf("[Song") == 0) {
        currentParseSong = new AudioTronSong();
        int index = content.indexOf(" ");
        int index2 = content.indexOf("]");
        currentParseSong.index =
          new Integer(content.substring(index+1, index2)).intValue()+1;
        return true;
      }
      
      return super.handleParse(content);
    }
    
    protected boolean songParsed()
    {
      if (currentParseSong.index <= songQueue.getSize()) {
        updateSong(currentParseSong);
      } else {
        addSong(currentParseSong);
      }
      progress = currentParseSong.index;
      
      return true;
    }
    
    public int getProgress() { return progress; }
    protected void resetProgress() { progress = 0; }
    
	@Override
    protected boolean listDone()
    {
      resetProgress();
      return true;
    }
  }
    
  /**
   * Implements parsing a list of songs
   */      
  protected abstract class SongListParser extends ListParser 
  {
    protected AudioTronSong currentParseSong;
    
    protected boolean fieldTitle(String arg)
    {
      currentParseSong.title = arg;
      return true;
    }
    
    protected boolean fieldArtist(String arg)
    {
      currentParseSong.artist = arg;
      return true;
    }
    
    protected boolean fieldAlbum(String arg)
    {
      currentParseSong.album = arg;
      return true;
    }
    
    protected boolean fieldGenre(String arg)
    {
      currentParseSong.genre = arg;
      return true;
    }
    
    protected boolean fieldID(String arg)
    {
      //System.out.println("FieldID = "+arg);
	  currentParseSong.sourceLocation = arg;
      return true;
    }
    
    protected abstract boolean songParsed();
    
		@Override
    protected boolean handleParse(String content)
    {
      if (super.handleParse(content)) {
        return true;
      }
      
      if (content.indexOf("[End Song") == 0) {
        return songParsed();
      }
      
      return false;
    }
  }
  
  /**
   * Generic list parser class
   */
  protected abstract class ListParser extends DynamicFieldParser
  {
    protected String listName = "";
    
    protected abstract boolean listDone();
    
    protected boolean fieldCurrTime(String str) { return true; }
    protected boolean fieldLastChangeTime(String str) { return true; }
    protected boolean fieldListCount(String str) { return true; }
    
		@Override
    protected boolean handleParse(String content)
    {
      if (content.indexOf("Error=") == 0) {
        return true;
      }
      if (content.indexOf("Start=") == 0) {
        return true;
      }
      if (content.indexOf("[End " + listName + "]") == 0) {
        return listDone();
      }
      
      return super.handleParse(content);
    }
  }
 /*****************************************************************************************/
  /**
   * Generic field parse class.
   * <p>
   * The parse method will take the input data, split it into
   * it's constituent field and value, generate a dynamic method
   * name for the field and invoke that method name on itself.
   * <p>
   * Sub classes are expected to provide the field parse methods.
   */
  protected abstract static class DynamicFieldParser extends AbstractParser
  {
    /**
     * Based on the two arguments, the field and its arguments, generates
     * the field parse method name and calls it with it's parameter.
     *
     * @param field the field name
     * @param argument the argument to the field
     *
     * @return true if success, false otherwise
     */
    protected boolean callFieldMethod(String field, String argument)
    {
      StringBuffer methodName = new StringBuffer();
      
      methodName.append("field");
      methodName.append(field);
      
      Object[] args = new Object[1];
      args[0] = argument;
      
      try {
        Class functionArgs[] = new Class[1];
        functionArgs[0] = String.class;
        Class current = getClass();
        Method m = null;
        
        while (current != null && m == null) {
          try {
            m = current.getDeclaredMethod(methodName.toString(), 
                                          functionArgs);
          } catch (NoSuchMethodException nsme) {
            // silently ignore non existent methods
          }
          current = current.getSuperclass();
        }
        
        if (m != null) {
          return ((Boolean) m.invoke(this, args)).booleanValue();
        }
        return true;
      } catch (Exception e) {
        System.err.println(field + " : " + argument);
        e.printStackTrace();
      }
      
      return false;
    }
    
    /**
     * Parses a 'field' into it's constituent parts.  A field
     * is a string of the form :
     * <pre>
     * field=argument
     * </pre>
     *
     * where field is the field and argument is the argument to 
     * the field
     */
    private void parseField(String content, String[] fields)
    {
      // assert fields.length == 2;
         
      try {
        int index = content.indexOf("=");
        fields[0] = content.substring(0, index);
        fields[1] = content.substring(index+1);
        
      } catch (Exception e) {
        System.err.println("Bad string [" + content + "]");
      }
    }
    
    protected boolean handleParse(String content) { return false; }
    
		@Override
    public final boolean parse(String content)
    {
      if (handleParse(content)) {
        return true;
      }

      if (content.trim().equals("")) {
        return true;
      }
      
      try {
        String[] fields = new String[2];
        
        parseField(content, fields);
        callFieldMethod(fields[0], fields[1]);
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }
      
      return true;
    }
  } // End Dynamic Field Parser

  /**
   * Updater Class
   *
   */
  private class Updater implements Timer.Alarm
  {
    private final long refreshDelay = 1750;
    private final long updateDelay = 3000;
    private long refresh = System.currentTimeMillis() + updateDelay;
    
    private boolean updateSongQueue = false;
    boolean songQueueInitialized = false;
    
    // update methods
    Command updateStatusCommand = new UpdateStatusCommand();
    Command updateSongQueueCommand = new UpdateSongQueueCommand();
    
    /**
	 * Empty Constructor
	 */
	public Updater()
    {
      Timer.addAlarm(this);
    }
    
    // Timer.Alarm interface
	@Override
    public long getAlarm() { return refresh; }

	@Override
    public void tick()
    {
      addCommand(updateStatusCommand, 2);
    }
    
    protected class UpdateStatusCommand extends Command
    {
      public UpdateStatusCommand() { super(STATUS); }
      
      public void invoke()
      {
        boolean success = false;
        
        try {
          success = update("/apigetstatus.asp", statusParser);
        } catch (IOException ioe) {
          // ignore
        }
        
        // To ensure there is only ever one alarm, this is the only place
        // we add ourselves back to the Timer event queue.
        //
        // Since startCommand/endCommand are called from the same thread
        // that invoked us, we are guaranteed to get an endCommand for
        // the currently executing command, and then a startCommand
        // for the newly executing command.
        //
        // Because of this we always set the refresh to be the updateDelay.
        // If another command starts executing that causes the AT to be
        // dirty, then startCommand increments dirty and resets the
        // refresh to be the refreshDelay.
        if (success) {
          dirty = 0;
        }
        refresh = System.currentTimeMillis() + updateDelay;
        
        // Init songqueue if not initialized
        if (!songQueueInitialized) {
          setUpdateSongQueue(true);
        }
        
        // add ourselves back to the Timer event queue (see above for
        // explanation)
        Timer.addAlarm(Updater.this);
      }
    }
    
    protected class UpdateSongQueueCommand extends Command
    {
      public UpdateSongQueueCommand() { super(PLAYQ); }
      
      public void invoke()
      {
        try {
          if (update("/apigetinfo.asp?type=playq&this=" + 
                     playQueueParser.getProgress(), 
                     playQueueParser)) {
            songQueueInitialized = true;
          } 
          setUpdateSongQueue(false);
        } catch (IOException ioe) {
          // interrupted -- requeue command
          addCommand(updateSongQueueCommand, 2);
        }
      }
    }

   /** 
     * Update our state with the state from the remote AT.
     * 
     * @return true if the update was successful, false if
     *              it was not successful.
     *
     * @throws IOException if the process was interrupted
     */
    private boolean update(String page, Parser p)
    throws IOException
    {
      String ret = null;
      
      ret = get(page, null, p);
      
      if (ret != null) {
        state = ATState.ERROR;
        state.setStateString(ret);
        updateState();
      }
      
      return ret == null;
    }
    
    /**
     * Tells us that a command finished
     */ 
    protected void endCommand(Command command)
    {
      if (command.status == COMMAND) {
        dirty++;
        refresh = System.currentTimeMillis() + refreshDelay;
        Timer.alarmUpdated(this);
      }
    }
    
    /**
     * Indicate that we should update Song Queue Information
     */
    public synchronized void setUpdateSongQueue(boolean update)
    { 
      if (!updateSongQueue && update) {
        addCommand(updateSongQueueCommand, 2);
      }
      updateSongQueue = update;
    }
  } // End Updater
      
  protected void startCommand(Command command)
  {
    super.startCommand(command);
    refreshStatus();
  }
  
  protected void endCommand(Command command)
  {
    super.endCommand(command);
    updater.endCommand(command);
    refreshStatus();
  }
  
  /**
   * Update listener(s) with the current status
   */
  protected void refreshStatus()
  {
    AudioTronListener listener;
    String status = getStatus();
    boolean longRunning = getCommandStatus() != NONE; 
    
    if (longRunning) {
      status += "...";
    }
    
    for (Enumeration e = listeners.elements(); e.hasMoreElements();) {      
      listener = (AudioTronListener) e.nextElement();
      listener.status(status, longRunning); 
    }
  }

  public ComboBoxModel getSongQueueModel() { return songQueue; }
  public ComboBoxModel getShareListModel() { return shareList; }
  
  private class ShareList extends VectorListModel implements ComboBoxModel
  {
    Object selectedItem = null;
    
    public Object getSelectedItem() { return selectedItem; }
    public void setSelectedItem(Object anItem) { selectedItem = anItem; }

  }
    
  private class SongQueue extends VectorListModel implements ComboBoxModel
  {
    Object selectedItem = null;
                  
    public Object getSelectedItem() { return selectedItem; }    
    public void setSelectedItem(Object anItem) { selectedItem = anItem; }    
            
    protected void reset(int total)
    {        
      this.clear(false);
            
      if (total == 0) {
        AudioTronSong s = new AudioTronSong(-1);
        s.title = "<No Song>";
        addElement(s, false);
      } else {
        for (int i = 0; i < total; i++) {
          addElement(new AudioTronSong(i+1), false);
        }
      }

      updated(0, getSize() - 1);
    }
  }

  Hashtable trees = new Hashtable();
  
  /**
   * Fetch a tree for a particular "file type" which presents a TreeModel 
   * of that file type that is displayable in a JTree.
   * <p>
   * Case is insensitive.
   *
   * @param type the file type ('genre', 'album', 'artist' etc.)
   */
  public AudioTronTree getTree(String type)
  {
    String lc = type.toLowerCase();
    AudioTronTree tree = (AudioTronTree) trees.get(lc);
    if (tree == null) {
      tree = new AudioTronTree(type, lc);
      trees.put(lc, tree);
    }
    
    return tree;
  }
  
  /**
   * Tells us to retrieve data to fill the tree of the specified
   * filetype.
   * <p>
   * Case is insensitive.
   *
   * @param type the key specifying which tree to fill
   */
  public void fillTree(String type)
  {
    getTree(type).fill();
  }    
    
  /**
   * Implements the TreeModel for the different types of files 
   * (genre, album, artist etc.)
   */  
  public class AudioTronTree implements TreeModel, TreeExpansionListener
  {
    Vector listeners = new Vector();
    boolean filterable = false;
    FillableTreeNode root;
    
    public AudioTronTree(Object label, String lc)
    {
      // list, title and web are not filterable
      if (lc.equals("title") || lc.equals("artist") || lc.equals("album") ||
          lc.equals("genre")) {
        filterable = true;
      }
      if (lc.equals("title")) {
        root = new AudioTronTree.SegmentedRootTreeNode(label, 9);
      } else if (lc.equals("fav")) {
        root = new FavRootTreeNode(label);
      } else {
        root = new RootTreeNode(label);
      }
    }
    
    public void treeCollapsed(TreeExpansionEvent event) { }  
    public void treeExpanded(TreeExpansionEvent event)
    {
      try {
        FillableTreeNode node = 
        (FillableTreeNode) event.getPath().getLastPathComponent();
        if (node != null) {
          node.fill();
        }
      } catch (ClassCastException cce) {
        cce.printStackTrace();
      }
    }
    
    public synchronized void addTreeModelListener(final TreeModelListener l)
    {
      listeners.addElement(l);
    }
    
    public synchronized void removeTreeModelListener(TreeModelListener l)
    {
      listeners.removeElement(l);
    }
    
		@Override
    public void valueForPathChanged(TreePath path, Object newValue) { }
    
    
		@Override
    public int getChildCount(Object parent)
    {
      TreeNode t;
      
      for (Enumeration e = root.breadthFirstEnumeration();
           e.hasMoreElements(); ) {
        t = (TreeNode) e.nextElement();
        if (t == parent) {
          return t.getChildCount();
        }
      }
      
      return 0;
    }
    
		@Override
    public int getIndexOfChild(Object parent, Object child)
    {
      TreeNode t;
      
      for (Enumeration e = root.breadthFirstEnumeration();
           e.hasMoreElements(); ) {
        t = (TreeNode) e.nextElement();
        if (t == parent) {
          return t.getIndex((TreeNode) child);
        }
      }
      
      return 0;
    }
    
    public boolean isLeaf(Object node)
    {
      if (node == this) {
        return false;
      }
      
      TreeNode t;
      
      for (Enumeration e = root.breadthFirstEnumeration();
           e.hasMoreElements(); ) {
        t = (TreeNode) e.nextElement();
        if (t == node) {
          return t.isLeaf();
        }
      }
      
      return true;
    }
    
	@Override
    public Object getRoot() { return root; }    
    
    protected synchronized void treeNodesInserted(final TreeModelEvent event)
    {
      Runnable r = new Runnable() {
		@Override
        public void run() {
          TreeModelListener l;
          for (Enumeration e = listeners.elements(); e.hasMoreElements(); ) {
            l = (TreeModelListener) e.nextElement();
            l.treeNodesInserted(event);
            
          }
        }
      };
      SwingUtilities.invokeLater(r);
    }
    
    protected synchronized void treeNodesRemoved(final TreeModelEvent event)
    {
      Runnable r = new Runnable() {
        public void run() {
          TreeModelListener l;        
          for (Enumeration e = listeners.elements(); e.hasMoreElements(); ) {
            l = (TreeModelListener) e.nextElement();
            l.treeNodesRemoved(event);
          }
        }
      };
      SwingUtilities.invokeLater(r);
    }
    
    protected synchronized void treeNodesChanged(final TreeModelEvent event)
    {
      Runnable r = new Runnable() {
        public void run() {  
          TreeModelListener l;
          
          for (Enumeration e = listeners.elements(); e.hasMoreElements(); ) {
            l = (TreeModelListener) e.nextElement();
            l.treeNodesChanged(event);
          }
        }
      };
      SwingUtilities.invokeLater(r);
    }
    
    public Object getChild(Object parent, int index)
    { 
      TreeNode t;
      
      for (Enumeration e = root.breadthFirstEnumeration(); 
           e.hasMoreElements(); ) {
        t = (TreeNode) e.nextElement();
        if (t == parent) {
          return t.getChildAt(index);
        }
      }
      
      return null;  
    } // AudioTronTree
    
    protected void fill() { root.fill(); }
    
    protected class SegmentedRootTreeNode extends RootTreeNode
    {
      String current = null;
      boolean restarted = false;
      Parser parser = new Parser();
      SegmentedTreeNode currentNode;
      Character request = null;
      Character z = new Character('Z');
      
      public SegmentedRootTreeNode(Object object, int segments)
      {
        super(object, true);
        
        int div = Math.round((float) 26/(float) segments);
        SegmentedTreeNode node;
        int i;
        for (i = div; i <= 26; i += div)
        {
          node = new SegmentedTreeNode(i-div, i-1);
          add(node);
        }
        if ((i - div) < 26) {
          node = new SegmentedTreeNode(i-div, 25);
          add(node);
        }
        
        currentNode = (SegmentedTreeNode) getChildAt(0);
      } 

      public synchronized void requestFill(Character c)
      {
        if (request == null || request.compareTo(c) < 0) {
          request = c;
        }
        
        // if count is 0, not fetching, so start
        if (parser.count == 0) {
			System.out.println("1499");
          fetch();
        }
      }
      
      public void fetch()
      {
        // initialize count to 1 (fetching if > 0)
        parser.count = 1;
        
        getInfo(toString(), current, 50, parser);
      }
      
	  /**
	   * Parser Class
	   */
      protected class Parser extends ListParser
      {
        int count = 0;
        
        public Parser()
        {
          listName = "Title List";
        }
        
        protected boolean listDone()
        {
          // JSC - changed check to if(count<51)(probaby count == 2 is a better test)
			// still has a bug where the last of each group is also returned as the
			// first of the next group.
			System.out.println("Count = " + count);
			// total song list is done if count is 1
          if(count < 51){
		  //if (count == 1 || count == 2) {
            return true;
          }
          
          // reset count to 0 (not fetching)
          count = 0;
          
          Character c = new Character(Character.toUpperCase(current.charAt(0)));
          System.out.println("Request: "+ request.toString());
		  System.out.println("C: "+ c);
		  if (c.compareTo(request) <= 0) {

            System.out.println("1545");
			fetch();
            return true;
          }
          
          return true;
        }
        
			@Override
        public void end(boolean error)
        {
          if (error) {
            restarted = true;
            System.out.println("1557");
			fetch();
          }
        }
        
		@Override
        protected boolean handleParse(String content)
        {
          if (content.indexOf("[Title List]") == 0) {
            return true;
          }
          
          return super.handleParse(content);
        }
        
        protected boolean fieldItem(String content)
        {
          // ignore first result of a resumed search
          if (restarted) {
            restarted = false;
            return true;
          }
          
          count++;
          current = content;
          
          Character c = new Character(Character.toUpperCase(content.charAt(0)));
          if (c.compareTo(currentNode.finish) > 0) {
            if (c.compareTo(z) <= 0) { 
              currentNode.filled();
              
              int index = getIndex(currentNode);            
              currentNode = (SegmentedTreeNode) getChildAt(index+1);
            }
          }
          
          PlayableTreeNode node = new PlayableTreeNode(content);
          currentNode.newNode(node);
          
          return true;
        }
      } // Parser
    } // SegmentedRootTreeNode

	/****************************************************************************************/
	protected class FavRootTreeNode extends FillableTreeNode
    {
      Parser parser = new Parser();
      int parsed = 0;
      int count = 0;

      public FavRootTreeNode(Object object) { super(object, false); }
      
      protected void startFilling()
      {
        getInfo(toString().toLowerCase(), parser);

      }
      
      protected class Parser extends ListParser
      {
        protected AudioTronFavorite currentParseFav;
        
        public Parser() { listName = "Favorite List"; }
        
        protected boolean fieldFavType(String arg)
        {
          currentParseFav.type = arg;
          return true;
        }
        
        protected boolean fieldFavValue(String arg)
        {
          currentParseFav.value = arg;
          return true;
        }
        
        protected boolean favoriteParsed()
        {
          currentParseFav.index = count++;
          if (count > parsed) {
            parsed++;
            PlayableTreeNode node =
            new FavPlayableTreeNode(currentParseFav);
            
            newNode(node);
          }
          
          return true;
        }
        
        protected boolean listDone()
        {
          filled();
          return true;
        }

        public void end(boolean error)
        {
          if (error) {
            count = 0;
            startFilling();
          }
        }
        
        protected boolean handleParse(String content)
        {
          if (super.handleParse(content)) {
            return true;
          }
          
          if (content.indexOf("[Favorite List]") == 0) {
            return true;
          }
          
          if (content.indexOf("[Favorite ") == 0) {
            currentParseFav = new AudioTronFavorite();
            return true;
          }
          if (content.indexOf("[End Favorite ") == 0) {
            return favoriteParsed();
          }
          
          return false;
        }
      } 
    }
    
    protected class RootTreeNode extends FillableTreeNode
    {
      Parser parser = new Parser();
      String current = null;
      boolean restarted = false;
      
      public RootTreeNode(Object object) { this(object, false); }
      
      public RootTreeNode(Object object, boolean filled)
      {
        super(object, filled);
      }
      
      protected void startFilling()
      {
        getInfo(toString().toLowerCase(), current, parser);
      }
      
      protected class Parser extends ListParser
      {
        public boolean listDone()
        {
          current = null;
          filled();
          return true;
        }
        
        public void end(boolean error)
        {
          if (error) {
            restarted = true;
            startFilling();
          }
        }
        
        protected boolean handleParse(String content)
        {
          if (content.indexOf(" List]") >= 0) {
            return true;
          }
          
          return super.handleParse(content);
        }
        
        protected boolean fieldItem(String str)
        {
          // ignore first result of a resumed search
          if (restarted) {
            restarted = false;
            return true;
          }

          current = str;
          TopLevelTreeNode node = new SongListTopLevelTreeNode(str, 
                                                               !filterable);
          newNode(node);
          
          return true;
        }
      }
      
      public boolean isPlayable() { return false; }
    } // RootTreeNode
    
    private class SegmentedTreeNode extends TopLevelTreeNode
    {
      Character start;
      Character finish;
      
      public SegmentedTreeNode(int begin, int end)
      {
        super("");
        
        start = new Character((char) (begin + (int) 'A'));
        finish = new Character((char) (end + (int) 'A'));
        String title = start + " - " + finish;
        setUserObject((Object) title);
      }
      
			@Override
      protected void startFilling()
      {
        SegmentedRootTreeNode root = (SegmentedRootTreeNode) getParent();
        root.requestFill(finish);
      }
      
			@Override
      public boolean isPlayable() { return false; }
    }  // SegmentedTreeNode
    
    private class SongListTopLevelTreeNode extends TopLevelTreeNode
    {
      Parser parser = new Parser();
      String current = null;
      boolean restarted = false;
      
      public SongListTopLevelTreeNode(Object object)
      {
        this(object, false);
      }
      
      public SongListTopLevelTreeNode(Object object, boolean filled)
      {
        super(object, filled);
      }
      
			@Override
      protected void startFilling()
      {
        getFilteredInfo(this.getRoot().toString(),
                        toString(),
                        current,
                        parser);
      }
      
      protected class Parser extends SongListParser
      {
        public Parser()
        {
          listName = "Song List";
        }

		@Override
        public void end(boolean error)
        {
          if (error) {
            restarted = true;
            startFilling();
          }
        }
        
		@Override
        public boolean handleParse(String content)
        {
          if (content.indexOf("[Song List]") >= 0) {
            return true;
          }                
          if (content.indexOf("[Song]") == 0) {
            currentParseSong = new AudioTronSong();
            return true;
          }
          
          return super.handleParse(content);
        }
        
		@Override
        public boolean songParsed()
        {
          // ignore first result of a resumed search
          if (restarted) {
            restarted = false;
            return true;
          }
          
          current = currentParseSong.sourceLocation;
          PlayableTreeNode node = 
            new IndexedPlayableTreeNode(currentParseSong.getTitleAdapter());
          
          newNode(node);
          return true;
        }
        
		@Override
        public boolean listDone()
        {
          if (first) {
            replace(0, new PlayableTreeNode("No data?!?!?", false));
          }
          
          filled();
          
          return true;
        }
      }
    } // SongListTopLevelTreeNode

    protected abstract class TopLevelTreeNode extends FillableTreeNode
    implements Playable
    {
      public TopLevelTreeNode(Object object)
      { 
        this(object, false);
      }      
      public TopLevelTreeNode(Object object, boolean filled)
      { 
        super(object, filled);
      }
      
			@Override
      public boolean isPlayable() { return true; }
			@Override
      public String getType() { return getParent().toString(); }
    } // TopLevelTreeNode
    
    protected abstract class FillableTreeNode extends PlayableTreeNode
    {
      boolean isFilling = false;
      boolean isFilled;
      boolean first = true;
      
      public FillableTreeNode(Object object, boolean filled_)
      {
        super(object);
        isFilled = filled_;
        if (!isFilled) {
          super.add(new PlayableTreeNode("Retrieving info...", false));
        }
      }
      
      public void replace(int index, MutableTreeNode node)
      {
        if (index < 0 || index >= this.getChildCount()) {
          return;
        }
        super.remove(index);
        super.insert(node, index);
        int[] indices = { index };
        Object[] children = { node };
        TreeModelEvent e = new TreeModelEvent(this, 
                                              getPath(),
                                              indices,
                                              children);
        AudioTronTree.this.treeNodesChanged(e);
      }
      
			@Override
      public void add(MutableTreeNode node)
      {
        super.add(node);
        int[] indices = { this.getChildCount() - 1 };
        Object[] children = { node };
        TreeModelEvent e = new TreeModelEvent(this, 
                                              getPath(),
                                              indices,
                                              children);
        AudioTronTree.this.treeNodesInserted(e);
      }
      
			@Override
      public void remove(MutableTreeNode node)
      {
        remove(getIndex(node));
      }
      
			@Override
      public void remove(int index)
      {
        if (index < 0 || index >= this.getChildCount()) {
          return;
        }
        Object[] children = { getChildAt(index) };
        int[] indices = { index };
        super.remove(index);
        TreeModelEvent e = new TreeModelEvent(this, 
                                              getPath(),
                                              indices,
                                              children);
        AudioTronTree.this.treeNodesRemoved(e);
      }
      
		@Override
      public boolean isLeaf()
      {
        return isFilled ? super.isLeaf() : false;
      }

      protected void newNode(MutableTreeNode node)
      {
        if (first) {
          first = false;
          replace(0, node);
        } else {
		  add(node);
        }
      }
      
      protected synchronized void fill()
      {
        if (!isFilled && !isFilling) {
          isFilling = true;        
          startFilling();      
        }
      }
      
      protected synchronized void filled()
      {
        isFilling = false;
        isFilled = true;
      }    
      
      protected abstract void startFilling();
    }  // FillableTreeNode
  }  // AudioTronTree
  
  protected static class FavPlayableTreeNode extends PlayableTreeNode
  {
    public FavPlayableTreeNode(AudioTronFavorite fav)
    {
      super(fav);
    }
    
	@Override
    public String getType() { return "Fav"; }

	@Override
    public String getFile()
    {
      return ((AudioTronFavorite) getUserObject()).index + "";
    }

	@Override
    public boolean isPlayable()
    {
      return ((AudioTronFavorite) getUserObject()).isPlayable();
    }
  }
  
  protected static class IndexedPlayableTreeNode extends PlayableTreeNode
  {
    public IndexedPlayableTreeNode(Object object) { super(object); }
    

	@Override
	public String toString()
    {
      int index = getParent().getIndex(this);
      
      StringBuffer str = new StringBuffer();
      AudioTronSong.appendIndex(str, index + 1);
      str.append(". ");
      str.append(super.toString());
      
      return str.toString();
    }
    

	@Override
	public String getFile() { return getUserObject().toString(); }
  } // IndexedPlayableTreeNode
  
  protected static class PlayableTreeNode extends DefaultMutableTreeNode
  implements Playable
  {
    boolean playable;
    
    public PlayableTreeNode(Object object) { this(object, true); }
    public PlayableTreeNode(Object object, boolean playable_)
    {
      super(object);
      playable = playable_;
    }
    

    public boolean isPlayable() { return playable; }
    public String getType() { return "Title"; }
    public String getFile() { return toString(); }
  } // PlayableTreeNode
  
  protected static class AudioTronFavorite
  {
    String type;
    String value;
    int index;
    
    public String toString() 
    {
      StringBuffer b = new StringBuffer();
      b.append("Fav ");
      if (index > 15) {
        char c = (char) ((int) 'A' + index - 16);
        b.append(c);
      } else {
        b.append(index+1);
      }
      b.append(" : {");
      b.append(type);
      if (isPlayable()) {
        b.append(", ");
        b.append(value);
      }
      b.append("}");
      return b.toString();
    }
    
    public boolean isPlayable() { return !type.equals("Not Configured"); }
  } // AudioTronFavorite
} // AudioTronState
