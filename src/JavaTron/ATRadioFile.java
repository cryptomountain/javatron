/**
 * ATRadioFile.java
 *
 * Created on Mar 21, 2010, 6:03:52 PM
 * 
 */
package JavaTron;

import java.io.*;
import java.nio.*;
import java.util.*;
import java.net.MalformedURLException;
import jcifs.smb.*;
import jcifs.smb.SmbException;

/**
 * Class to work with Radio.txt files for the Audiotron
 *
 * @author Joe Culbreth
 */
public class ATRadioFile {
	/**
	 * Empty Constructor
	 */
	ATRadioFile(){
		timestamp=(int)System.currentTimeMillis()/1000;
		stationList=new ArrayList<StationEntry>();
		fullData=new String();
	}
	/**
	 * Constructs an ATRadioFile given a filename
	 * @param filename
	 */
	ATRadioFile(String filename)throws NotRadioFileException, MalformedRadioFileException{
		stationList=new ArrayList<StationEntry>();
		fullData=new String();
		try{
			readRadioFile(filename);
		}catch(NotRadioFileException nre){
			nre.printStackTrace();
			throw new NotRadioFileException();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}catch(MalformedRadioFileException mre){
			mre.printStackTrace();
			throw new MalformedRadioFileException();
		}

	}

	/**
	 * Constructs an ATRadioFile given a byte array of data
	 * @param data
	 */
	ATRadioFile(byte[] data_){
		stationList=new ArrayList<StationEntry>();
		fullData=new String(data_);
		parseFullData();

	}

	/**
	 * Constructs an ATRadioFile given a network share and filename with auth credentials
	 *
	 * @param host_ Hostname of the device hosting the radio definition file
	 * @param sharename The Windows share radio.txt is on
	 * @param netUserName Username to use to connect to the share
	 * @param password Password for the share
	 * @param filename The filename to read (Not necessarily radio.txt!)
	 */
	ATRadioFile(String host_, String sharename, String netUserName_, String password, String filename){
		host=host_;
		netUserName=netUserName_;
		netPassword=password;
		radioURL="smb://"+host+"/"+sharename+"/"+filename;
		WinFiles wf=new WinFiles();
		fullData=new String();
		stationList=new ArrayList<StationEntry>();
		errorMessage="OK";
		try{
			long begin=System.currentTimeMillis();
			wf.login(host, netUserName, netPassword);
			long end=System.currentTimeMillis();
			System.out.println("Authentication took " +(end-begin)+ " milliseconds");
			String path=radioURL;
			begin=System.currentTimeMillis();
			if(!wf.isExist(path)){
				errorMessage="Can't Find Radio File";
				System.out.println("Can't Find Radio file!");
				return;
			}
			end=System.currentTimeMillis();
			System.out.println("Existance check took "+(end-begin)+" milliseconds");
			begin=System.currentTimeMillis();
			int size=wf.getFileSize(path);
			end=System.currentTimeMillis();
			System.out.println("Radio is "+size+" bytes long.");
			System.out.println("It took "+(end-begin)+ " milliseconds to figure that out.");
			byte[] inbuff=new byte[size];
			begin=System.currentTimeMillis();
			inbuff=wf.getFile(path);
			end=System.currentTimeMillis();
			System.out.println("File Retrieved in "+ (end - begin) +" milliseconds.");
			begin=System.currentTimeMillis();
			fullData=new String(inbuff);
			end=System.currentTimeMillis();
			System.out.println("File Conversion took "+(end-begin)+" milliseconds.");
			fullData=fullData.trim();
			System.out.println("DATA: Got the file");
		}catch(SmbException se){
			System.err.println(se.getMessage());
			errorMessage=se.getMessage();
			//throw new SmbException("message",se.getCause());
		}catch(Exception e){
			errorMessage=e.getMessage();
			e.printStackTrace();
		}

		parseFullData();

	}

	/**
	 * Adds a station to the list
	 * @param category
	 * @param title
	 * @param location
	 * @param format
	 */
	public void addStation(String category,String title,String location,String format){
		StationEntry se=new StationEntry(category,title,location,format);
		addStation(se);
	}

	public void addStation(StationEntry se){
		se.Validate();
		stationList.add(se);
	}

	/**
	 * removes a station from the list
	 * @param i the index of the station to be removed
	 */
	public void deleteStation(int i){
		stationList.remove(i);
	}

	/**
	 * retrieves the index of the station with the given title
	 * @param title
	 * @return the index of the station matching the title
	 */
	public int getIndexFromName(String title){
		int i=0;
		while(!stationList.get(i).getTitle().equals(title))
			i++;
		return(i);
	}

	/**
	 * Refreshes the XML header
	 */
	private void updateHeader(boolean includeInvalid){
		length=0;
		length=getDataLength(includeInvalid);
		setTimestamp();
		String init="<TBSTATIONS><length>";
		String subheader="</length><username>"+tbUsername+"</username><ttl>7</ttl>"+
				"<updateavail>false</updateavail><stationcount>"+getStationCount()+"</stationcount>" +
				"<time>"+timestamp+"</time>";
		String tmp=init+subheader+footer;
		length+=tmp.length();
		Integer l=length;
		length+=l.toString().length();

		header=init+length+subheader;
		//System.out.println("UpHeader: "+ header);
	}

	/**
	 * Gets the length of the XML data portion of the Station list
	 * @return the length in bytes
	 */
	private int getDataLength(){
		return getDataLength(true);
	}
	
	private int getDataLength(boolean includeInvalid){
		int dlength=0;
		for(int i=0;i<stationList.size();i++){
			if(includeInvalid){
				dlength+=stationList.get(i).getXMLSize();
			}else{
				if(stationList.get(i).isValid())
					dlength+=stationList.get(i).getXMLSize();
			}
		}
		System.out.println("DataLength: "+dlength);
		return dlength;
	}

	public String getRadioText(){
		String out=new String();
		updateHeader(true);
		out=header;
		for(int i=0;i<stationList.size();i++){
			if(stationList.get(i).isValid())
				out+=stationList.get(i).getXML();
		}
		out+=footer;
		return out;
	}

	/**
	 * Gets the StationEntry at index i
	 * @param i the index of the record to retrieve
	 * @return a StationEntry Object
	 */
	private StationEntry getStationEntryAt(int i){
		return stationList.get(i);
	}

	/**
	 * Gets a StationEntry in an ArrayList format
	 * @param i the index of the record to retrieve
	 * @return ArrayList of the station(category, title, location, format, "true" or "false" validation)
	 */
	public ArrayList<String> getStationEntryTextAt(int i){
		StationEntry se=stationList.get(i);
		ArrayList<String> sl=new ArrayList<String>();
		sl.add(se.getCategory());
		sl.add(se.getTitle());
		sl.add(se.getLocation());
		sl.add(se.getFormat());
		sl.add(se.isValid()?"true":"false");
		return sl;
	}

	public String getXMLStationEntryAt(int i){
		return stationList.get(i).getXML();
	}

	public int getStationCount(){
		return stationList.size();
	}
	
	public String getTbUsername(){
		return tbUsername;
	}

	public String getUsername(){
		return netUserName;
	}

	public String getPassword(){
		return netPassword;
	}

	public String getHost(){
		return host;
	}

	public String getRadioURL(){
		return radioURL;
	}

	public ArrayList<StationEntry> getStationList(){
		return stationList;
	}

	public void setStationList(ArrayList<StationEntry> list){
		stationList=list;
	}

	public void parseFullData(){
		fullData=fullData.replace("<station>", "\n<station>");
		fullData=fullData.replace("</TBSTATIONS>","");
		if(debug){System.out.print(fullData);}
		String category, title, location, format;
		StationEntry se;
		String[] entries=fullData.split("\\n");
		stationCount=entries.length - 1;
		header=entries[0];
		for(int i=1;i<entries.length;i++){
			category=entries[i].substring(entries[i].indexOf("<category>")+10,entries[i].indexOf("</category>"));
			title=entries[i].substring(entries[i].indexOf("<title>")+7,entries[i].indexOf("</title>"));
			location=entries[i].substring(entries[i].indexOf("<location>")+10,entries[i].indexOf("</location>"));
			format=entries[i].substring(entries[i].indexOf("<format>")+8,entries[i].indexOf("</format>"));
			//se=new StationEntry(category,title,location,format,true);
			//System.out.println("PARSE: " + se.toString());
			addStation(category,title,location,format);
			
		}
	}

	public void setAuth(String host_, String user, String pass){
		host=host_;
		netUserName=user;
		netPassword=pass;
	}

	public void setHeader(String s){
		header=s;
	}

	public void setHost(String s){
		host=s;
	}

	public void setRadioURL(String url){
		radioURL=url;
	}

	public void setPassword(String pass){
		netPassword=pass;
	}

	public void setTimestamp(){
		timestamp=System.currentTimeMillis()/1000;
	}

	public void setTimestamp(long i){
		timestamp=i;
	}

	public void setTbUsername(String user){
		tbUsername=user;
	}

	public void setUsername(String user){
		netUserName=user;
	}

	public String getLastError(){
		return errorMessage;
	}

	public boolean readRadioFile(String filename) throws IOException,
														NotRadioFileException,
														MalformedRadioFileException
	{
		try{
			File infile=new File(filename);
			byte[] buff=new byte[(int)infile.length()];
			FileInputStream ifs=new FileInputStream(filename);
			ifs.read(buff);
			fullData=new String(buff);
		}catch(Exception e){
			System.out.println("Couldn't read file "+filename);
			e.printStackTrace();
			throw new IOException();
		}
		if(!fullData.substring(0,12).equalsIgnoreCase("<TBSTATIONS>")){
			System.out.println(fullData.substring(0,11));
			throw new NotRadioFileException();
		}
		parseFullData();
		return true;
	}
	
	public boolean writeRadioFile()throws SmbException, MalformedURLException{
		String radioBuffer=getRadioText();
		WinFiles wf=new WinFiles();
		try{
			wf.login(host, netUserName, netPassword);
			System.out.println("wf.radioURL : "+radioURL);
			wf.putFile(radioURL, radioBuffer);

		}catch(SmbException se){
			se.printStackTrace();
			throw new SmbException(se.getNtStatus(),false);
		}catch(Exception e){
			e.printStackTrace();
			throw new MalformedURLException();
		}
		return true;
	}

	public static class NotRadioFileException extends Exception{

	}

	public static class MalformedRadioFileException extends Exception{

	}

	private String fullData, stationData, header, tbUsername, errorMessage;
	private String netUserName, netPassword, host, radioURL;
	private String footer = "</TBSTATIONS>";
	private int length;
	private int stationCount;
	private ArrayList<StationEntry> stationList;
	private long timestamp;
	private boolean debug = false;
	
	

}
