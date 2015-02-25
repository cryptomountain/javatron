/* Copyright (C) 2010  Joe Culbreth
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
 * $Id: Version.java,v 1.6 2010/03/01 20:46:45 jculbreth Exp $
 */

package JavaTron;

import java.awt.Color;
import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.commons.codec.binary.Base64;
/**
 * Utiltiy Class to make specific parsers available
 * @author Joe Culbreth
 */
public class JTP {
	/**
	 * A Parser to pass raw output from a web request
	 *
	 */
	public static class HtmlParser implements AudioTron.Parser{
		private boolean showRaw = false;
		private boolean showOut = true;
		private StringBuffer output;
		/**
		 * HtmlParser Constructor
		 * @param out A StringBuffer to recieve the parsed output
		 */
		public HtmlParser(StringBuffer out){
			output=out;
		}
		public void begin(String error){

		}
		
		public void end(boolean error){
			parse(output.toString());
		}

		/**
		 * Real kludgey formatter for AT Web pages
		 * Primarily strips out buttons and images but also
		 * re-arranges the layout for display in an Javatron
		 * WebEdit Display
		 *
		 * @param content The web content returned from the AudioTron
		 * @return Always returns true
		 */
		public boolean parse(String content){
			if(showRaw){
				System.out.println(content);
			}
			content=content.replaceAll("<img.*?>", "");
			content=content.replaceAll("<input type=\"submit\".*?>","[BUTTON]");
			content=content.replaceAll("<table width=\"500\"","<table width=\"800\"");
			content=content.replaceAll(" <td width=\"5%\""," <td width=\"1%\"");
			content=content.replaceAll(" <td width=\"72%\""," <td width=\"100%\"");
			content=content.replaceAll(" <td width=\"84%\"","<td width=\"5%\"");
			if(showOut){
				System.out.println("JTP: " + content);
			}
			output.append(content);
			return(true);
		}
		
	}

	/**
	 * Class to extend AudioTronSong to implement Playable Interface
	 * Already depricated, folded this into AudioTronSong class.
	 */
	public static class ATPlayableSong extends AudioTronSong implements AudioTron.Playable{

		public ATPlayableSong(AudioTronSong as_){
			update(as_);
		}

		public boolean isPlayable(){
			return(true);
		}

		public String getType(){
			return(new String("Song"));
		}

		public String getFile(){
			return(new String("Title"));
		}
	}

	/**
	 * Converts a CSV style string to a Color Object
	 * @param rgb A String in the form of R,G,B i.e. '0,255,127'
	 * @return A Color Object
	 */
	public static Color parseRGB(String rgb){
		String[] RGB=rgb.split(",");
		int red=Integer.valueOf(RGB[0]);
		int green=Integer.valueOf(RGB[1]);
		int blue=Integer.valueOf(RGB[2]);
		return new Color(red,green,blue);
	}

	/**
	 * Parses a Color type to a CSV type RGB string
	 * @param c A Color Object
	 * @return A String in the form of R,G,B i.e '0,255,127'
	 */
	public static String parseColor(Color c){
		int red=c.getRed();
		int green=c.getGreen();
		int blue=c.getBlue();
		String csv=Integer.toString(red)+","+Integer.toString(green)+","+Integer.toString(blue);
		return csv;
	}

	/**
	 * Encryption for storing passwords in javatron.props file
	 */
	private static final char[] PASSWORD = "enfldsgbnlsngdlksdsgm".toCharArray();
	private static final byte[] SALT = {
		(byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
		(byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
	};

	/**
	 * Encrypts a string
	 * @param property
	 * @return An encrypted string
	 */
	public static String encrypt(String property){
		String p = new String();
		try{
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
			SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
			Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
			pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
			p = base64Encode(pbeCipher.doFinal(property.getBytes()));
		}catch(Exception e){
			e.printStackTrace();
		}
		return p;
	}

	private static String base64Encode(byte[] bytes) {
	  byte[] t=Base64.encodeBase64(bytes);
	  return new String(t);
	}

	/**
	 * Decrypts an encrypted string
	 * @param property
	 * @return A plaintext string
	 */
	public static String decrypt(String property) {
		String p= new String();
		try{
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
			SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
			Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
			pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
			p = new String(pbeCipher.doFinal(base64Decode(property)));
		}catch(Exception e){
			e.printStackTrace();
		}
		return p;
	}

	private static byte[] base64Decode(String property) throws IOException {
		byte[] bytes=Base64.decodeBase64(property.getBytes());
		return bytes;
	}

}
