/**
 * StationEntry.java
 *
 * Created on Mar 21, 2010, 6:03:52 PM
 *
 */
package JavaTron;

/**
 * Class that holds a radio station entry
 * @author Joe Culbreth
 */

public class StationEntry{
	private String category;
	private String title;
	private String location;
	private String format;
	private boolean valid;

	public StationEntry(){
		category=new String("");
		title=new String("");
		location=new String("");
		format=new String("");
		valid=false;
	}

	/**
	 * Copy Constructor
	 * @param se
	 */
	public StationEntry(StationEntry se){
		this.category=se.getCategory();
		this.title=se.getTitle();
		this.location=se.getLocation();
		this.format=se.getFormat();
		this.valid=se.isValid();
	}

	public StationEntry(String category_, String title_, String location_, String format_){
		category=category_;
		title=title_;
		location=location_;
		format=format_;
		valid=true;
	}

	public StationEntry(String category_, String title_, String location_, String format_, boolean valid_){
		category=category_;
		title=title_;
		location=location_;
		format=format_;
		valid=valid_;
	}

	public boolean setStation(String category_, String title_, String location_, String format_){
		category=category_;
		title=title_;
		location=location_;
		format=format_;
		valid=true;
		return true;
	}

	public boolean isValid(){
		return valid;
	}

	public String getCategory(){
		return category;
	}

	public String getTitle(){
		return title;
	}

	public String getLocation(){
		return location;
	}

	public String getFormat(){
		return format;
	}

	public void setCategory(String s){
		category=s;
	}

	public void setTitle(String s){
		title=s;
	}

	public void setLocation(String s){
		location=s;
	}

	public void setFormat(String s){
		format=s;
	}

	public void setValid(boolean val){
		valid=val;
	}

	public boolean Validate(){
		setValid(true);
		return true;
	}

	public String getXML(){
		String entry="<station><id></id>" +
				"<category>"+category+"</category>" +
				"<title>"+title+"</title>" +
				"<location>"+location+"</location>" +
				"<format>"+format+"</format>" +
				"<pa>null</pa></station>";
		return entry;
	}

	public int getXMLSize(){
		return getXML().length();
	}

	@Override
	public String toString(){
		return(category + ", " + title + ", " + location + "," + format + ", " + valid );
	}
} // End StationEntry Class
