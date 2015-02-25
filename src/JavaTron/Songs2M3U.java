/**
 * Songs2M3U class
 * Writes AT songlist to an M3U playlist file
 * Copyright (C) 2010 Joe Culbreth
 */
package JavaTron;

import java.io.*;
import java.util.*;
import javax.swing.*;

/**
 * Writes a .m3u Playlist file from the current PlayQ
 *
 * @author Joe Culbreth <joe@connexsmart.com>
 */
public class Songs2M3U {

	/**
	 * Public Constructor: takes an AudiotronState Object argument
	 * @param at_ An initialized AudioTronState Object
	 */
	public Songs2M3U(AudioTronState at_){
		at=at_;
		fname=at.getBaseM3U();
		confirmOverwrite=Configuration.getBoolProperty(Configuration.KEY_CONFIRM_OVERWRITE);
		atshares=new ArrayList<String>();
	}

	public void showBuffer(){
		System.out.println(data.toString());
	}

	/**
	 * Reads the PlayQ and generates a playlist into Songs2M3U.data
	 */
	public void makeM3U(){
		AudioTronSong s;
		this.getShareList();
		data=new StringBuffer();
		data.append("#EXTM3U\r\n");
		Enumeration<AudioTronSong> queue=at.getSongQueue();
		while(queue.hasMoreElements()){
			 s = queue.nextElement();
			 if(showExtra){System.out.println("Title: " + s.getTitleAdapter().toString());}
			 data.append("#EXTINF:-,");
			 data.append(s.title + "\r\n");
			 //data.append(s.sourceLocation.replace("\\\\MEDIA\\MEDIA", "") + "\r\n");
			 data.append(s.sourceLocation + "\r\n");
		}
	}

	/**
	 * Creates an m3u file from a list of AudioTronSong entries in the buffer 'data'
	 * @param list List of AudioTronSong types
	 */
	public void makeM3U(ArrayList<AudioTronSong> list){
		AudioTronSong s;
		this.getShareList();
		data=new StringBuffer();
		data.append("#EXTM3U\r\n");
		for(int i=0;i < list.size(); i++){
			s=list.get(i);
			if(showExtra){System.out.println("Title: " + s.getTitleAdapter().toString());}
			data.append("#EXTINF:-,");
			data.append(s.title + "\r\n");
			 //data.append(s.sourceLocation.replace("\\\\MEDIA\\MEDIA", "") + "\r\n");
			 data.append(s.sourceLocation + "\r\n");
		}
	}

	/**
	 * Saves the m3u buffer to a file. This version uses fname as a filename,
	 * but will not overwrite an existing file. It saves to the first
	 * filename.X.m3u it finds that doesn't exist.
	 *
	 * @return boolean true on success, false on failure
	 */
	public boolean saveToFile(){
		File f=new File(fname);
		int i=1;
		while(f.exists()){
			String[] ele;
			fname=fname.replace(".m3u","");
			ele=fname.split("\\.");
			if(showExtra){System.out.println("ele: "+ele.length+", "+ele.toString());}
			if(ele.length == 1)
				fname+=".1.m3u";
			else
				fname=ele[0]+"."+Integer.toString(i) + ".m3u";
			f=new File(fname);
			i++;
		}
		return saveToFile(fname);
	}

	/**
	 * Saves the m3u buffer to the file given in filename. This version will
	 * overwrite with gusto!
	 *
	 * @param filename A String representing the filename (full path)
	 * @return boolean true on success, false on failure.
	 */
	public boolean saveToFile(String filename){
		
		if(data.length()==0){
			System.out.println("No Data in buffer.");
			return false;
		}

		if(filename.isEmpty()){
			System.out.println("Filename is Empty.");
			return false;
		}

		//showBuffer();
		String outbuff=data.toString();
		if(showExtra){System.out.println("Data: "+ outbuff);}
		File outfile=new File(filename);
		if(outfile.exists() && confirmOverwrite){
			if(!confirm("File Exists.\r\nOverwrite?")){
				System.out.println("Refusing to overwrite");
				return false;
			}
		}

		// Ok...try to write the damn file
		try{
			FileOutputStream fw=new FileOutputStream(outfile);
			fw.write(outbuff.getBytes());
		}catch(IOException e){
			System.out.println("Couldn't write to " + filename);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public String getFname(){
		return fname;
	}

	public void getShareList(){

		System.out.println("Share list...");
		Enumeration shares=at.getShares();
		String share;
		while(shares.hasMoreElements()){
			share=shares.nextElement().toString();
			atshares.add(share);
			System.out.println("SHARE: "+share);
		}
		
	}

	public void setFname(String filename){
		fname=filename;
	}

	/**
	 * Sets debug mode. Debug mode prints information about what its doing
	 *
	 * @param debug A boolean. True for debug messages, False for none.
	 */
	public void setDebug(boolean debug){
		showExtra=debug;
	}

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
	/**
	 * Private Variables
	 */
	private AudioTronState at;
	private StringBuffer data;
	private boolean isBuilt =false;
	private boolean confirmOverwrite=true;
	private String fname;
	private ArrayList<String> atshares;
	private boolean showExtra =false;

	// Not used yet
	
}
