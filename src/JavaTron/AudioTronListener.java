/* Copyright (C) 2001 Taylor Gautier
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
 * $Id: AudioTronListener.java,v 1.1 2002/08/05 00:06:41 tgautier Exp $
 */
package JavaTron;

import java.util.*;

/**
 * AudioTronListener interface describes a notification mechanism
 * for state changes.
 *
 * @author Taylor Gautier
 * @version $Revision: 1.1 $
 */
public interface AudioTronListener
{  
  /**
   * Notification of mute state
   *
   * @param muted true if muted, false otherwise
   */
  public void mute(boolean muted);

  /**
   * Notification of repeat state
   *
   * @param repeat true if repeat is set, false otherwise
   */
  public void repeat(boolean repeat);

  /**
   * Notification of random state
   *
   * @param random true if random is set, false otherwise
   */
  public void random(boolean random);

  /**
   * Notification of playing state.
   *
   * @param state can be one of {@link #STATE_UNKNOWN}, 
                  {@link #STATE_PLAYING}, {@link #STATE_PAUSED},
                  {@link #STATE_STOPPED}
   */
  public void state(ATState state);

  /**
   * Notification of AudioTron version
   *
   * @param version The AudioTron version as reported by the AudioTron webpage
   */
  public void version(String version);

  /**
   * Notification of the details of the current song playing
   *
   * @param song the current song playing
   */
  public void currentSong(AudioTronSong song);
   
  /**
   * Notification of the details of the next song to be played
   *
   * @param song the next song to play
   */
  public void nextSong(AudioTronSong song);

  /**
   * Notification of the current song position
   *
   * @param song the position of the song
   */
  public void songPositionUpdated(AudioTronSong song);

  /**
   * Notification of the AudioTron "controller enginer" status.  This
   * value is suitable for display to the user.
   *
   * @param status a string that indicates what the "engine" is doing
   * @param longRunning this value is set if the current operation is long
   *                    running.  In the case that it is long running,
   *                    status will be called repeatedly (likely with
   *                    the same value) during the long running operation.
   *
   */
  public void status(String status, boolean longRunning);
}