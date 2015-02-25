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
 * $Id: MSNTPDaemon.java,v 1.1 2002/08/05 00:06:41 tgautier Exp $
 */

package JavaTron;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Implements a mini-SNTP daemon (listens for a UDP NTP packet and responds)
 *
 * @author Taylor Gautier
 * @version $Revision: 1.1 $ 
 */
public class MSNTPDaemon implements Runnable
{
  static final int WORD = 4;
  static final int SNTP_PACKET_SIZE = 12 * WORD;
  static final int DAY = 60*60*24;
  
  public MSNTPDaemon()
  {      
    Thread t = new Thread(this);
    t.setDaemon(true);
    t.start();
  }
  
  void fillBuffer(byte[] buffer, int offset, byte[] values)
  {
    for (int i = 0; i < values.length; i++) {
      buffer[offset+i] = values[i];
    }
  }
  
  void fillBuffer(byte[] buffer, int offset, int value1, int value2)
  {
    buffer[offset] = (byte) (value1 >> 8);
    buffer[offset+1] = (byte) (value1);
    buffer[offset+2] = (byte) (value2 >> 8);
    buffer[offset+3] = (byte) (value2);
  }
  
  void fillBuffer(byte[] buffer, int offset, int value)
  {
    buffer[offset] = (byte) (value >> 24);
    buffer[offset+1] = (byte) (value >> 16);
    buffer[offset+2] = (byte) (value >> 8);
    buffer[offset+3] = (byte) (value);
  }
  
  /**
  * Setup SNTP header
  */
  void sntpHeader(byte[] buffer, byte[] packet)
  {
    byte b;
    
    try {
      buffer[0] = (byte) ((0 << 6) | (3 << 3) | 4);
      buffer[1] = (byte) 3;  // stratum
      buffer[2] = (byte) 0;  // poll
      buffer[3] = (byte) 0; // precision
      fillBuffer(buffer, 1*WORD, 0, 0); // root delay
      fillBuffer(buffer, 2*WORD, 0, 0); // root dispersion
      fillBuffer(buffer, 3*WORD, InetAddress.getLocalHost().getAddress()); // reference id
      fillBuffer(buffer, 4*WORD, 0);  // reference timestamp
      fillBuffer(buffer, 5*WORD, 0);  // reference timestamp
      System.arraycopy(packet, 10*WORD, buffer, 6*WORD, 2*WORD);  // originate timestamp
      fillBuffer(buffer, 7*WORD, 0);  // originate timestamp
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  /**
  * Compute an SNTP date -- 64 bit value starting 
  */
  void sntpDate(byte[] buffer, int offset)
  {
    Date d = new Date();
    long seconds = d.getTime()/1000 + (365*70 + 70/4)*DAY;
    fillBuffer(buffer, offset, (int) seconds);
    fillBuffer(buffer, offset+WORD, 10000);
  } 
  
  public void run() {
    DatagramSocket sd = null;
    byte[] rcvBuffer = new byte[SNTP_PACKET_SIZE];
    byte[] sndBuffer = new byte[SNTP_PACKET_SIZE];
    DatagramPacket receive = new DatagramPacket(rcvBuffer, rcvBuffer.length);
    DatagramPacket send;
    
    try {
      // create the listen socket
      sd = new DatagramSocket(123);
      
      for (;;) {
        try {  
          // wait for message from client
          sd.receive(receive);
          
          sntpHeader(sndBuffer, rcvBuffer);
          sntpDate(sndBuffer, 8*WORD);
          sntpDate(sndBuffer, 10*WORD);
          send = new DatagramPacket(sndBuffer,
          SNTP_PACKET_SIZE,
          receive.getAddress(),
          receive.getPort());
          
          
          // send the message back to the client
          sd.send(send);
        } catch (Exception e) {
          // keep going if there is some exception other than a
          // read exception
        }
      }
    } catch (Exception e) {
      // e.printStackTrace();  -- be quiet already - don't want to bother
      // UNIX users with the machinations required of windoze users
    }      
  }
}
