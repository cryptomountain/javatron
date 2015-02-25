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
 * $Id: Timer.java,v 1.1 2002/08/05 00:06:41 tgautier Exp $
 */
package JavaTron;

import java.util.*;

/**
* Implements an event queue based timer
*/
public class Timer extends Thread
{    
  static Vector alarms = new Vector();
  static Vector ticks = new Vector();
  static AlarmComparator comparator = new AlarmComparator();  
  static Thread t;
  static Timer timer = new Timer();
 
  private static class AlarmComparator implements Comparator
  {
    @Override
	public int compare(Object o1, Object o2)
    {
      Alarm a1 = (Alarm) o1;
      Alarm a2 = (Alarm) o2;
      
      if (a1.getAlarm() == a2.getAlarm()) {
        return 0;
      }
      
      return (a1.getAlarm() < a2.getAlarm() ? -1 : 1); 
    }
    
	@Override
    public boolean equals(Object obj)
    {
      return obj.getClass() == getClass();
    }

	@Override
	public int hashCode() {
		int hash = 5;
		return hash;
	}
  }
  
  private Timer()
  {
    
    Runnable r = new Runnable() {
      public void run() 
      {
        Alarm alarm = null;
        long wake = 0;
        long remaining = 0;
        
        for (;;) {
          synchronized (t) {
            // loop until an alarm expires
            do {
              try {
                if (alarms.size() == 0) {
                  t.wait();
                } else {
                  alarm = (Alarm) alarms.elementAt(0);              
                  remaining = alarm.getAlarm() - System.currentTimeMillis();
                  if (remaining > 0) {
                    t.wait(remaining);
                  }
                }
              } catch (InterruptedException ie) {
                // should never be interrupted
                ie.printStackTrace();
              }
            } while (alarm == null || remaining > 0);

            // remove the current alarm, it expired so add it to the 
            // ticks pending queue
            alarms.remove(0);
          }
          
          synchronized(Timer.this) {
            ticks.addElement(alarm);
            Timer.this.notify();
          }

          // reset the alarm and remaining values
          alarm = null;
          remaining = 0;
        }
      }
    };
    
    setDaemon(true);
    start();
    
    t = new Thread(r);
    t.setDaemon(true);
    t.setPriority(Thread.MAX_PRIORITY);
    t.start();
  }
  
  /**
   * Implements the Runnable interface -- you should not call this method.
   */
  public void run()
  {
    for (;;) {      
      synchronized (this) { 
        while (ticks.size() <= 0) {
          try {
            wait();
          } catch (InterruptedException ie) {
            // should never be interrupted
            ie.printStackTrace();
          }          
        }
        
        for (Enumeration e = ticks.elements(); e.hasMoreElements(); ) {
          Alarm a = (Alarm) e.nextElement();
          a.tick();
        }
        ticks.clear();
      }
    }
  }
  
  /**
  * Insert this alarm into the timer event queue.  If it already exists
  * this call is ignored.  If you want to update the timer call 
  * @link{alarmUpdated}.
  *
  * @param alarm the alarm to insert
  */
  public static void addAlarm(Alarm alarm)
  {
    synchronized (timer.t) {
      if (alarms.contains(alarm)) {
        return;
      }
      
      insertAlarm(alarm);
    }
  }
  
  /**
   * Call this method if your alarm time has been updated
   *
   * @param alarm the alarm that was updated
   *
   * @return true if the alarm is in the queue and the update was successful,
   *         false otherwise
   */
  public static boolean alarmUpdated(Alarm alarm)
  {
    synchronized (timer.t) {
      if (!alarms.remove(alarm)) {
        return false;
      }
      
      insertAlarm(alarm);     
      return true;
    }    
  }

  /**
   * Insert an alarm into the queue - uses insertion sort since the 
   * alarm queue is always kept in sorted order.  (Assumes timer.t
   * lock is held)
   */
  private static void insertAlarm(Alarm alarm)
  {
    // insertion sort
    int i;
    for (i = 0; i < alarms.size(); i++) {
      if (comparator.compare(alarm, alarms.elementAt(i)) < 0) {
        break;
      }
    }

    alarms.insertElementAt(alarm, i);
    timer.t.notify();      
  }  
  
  /**
   * Defines the interface that must be implemented to get timer notifications
   */
  public static interface Alarm
  {
    /**
     * This is an absolute time at which you want to receive notification
     * (via @link{tick}).
     *
     * @return an absolute time in milliseconds using the same base
     *         as System.currentTimeMillis() 
     */
    public long getAlarm();
    
    /**
    * This method will be called after the time indicated by @link{getAlarm}
    * has expired.
    */
    public void tick();
  }
}
