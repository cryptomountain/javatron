
package JavaTron;

import java.io.*;
/**
 * 
 * @author Joe Culbreth
 * 03/03/2010
 */

/* ----------------------------------------------------------

   original C++ code by:
                        Gustav "Grim Reaper" Munkby
                        http://floach.pimpin.net/grd/
                        grimreaperdesigns@gmx.net

   modified and converted to C# by:
                        Robert A. Wlodarczyk
                        http://rob.wincereview.com:8080
                        rwlodarc@hotmail.com

	modified and converted to Java by:
 *						Joe Culbreth
 *						joeydanger_one@yahoo.com
 *		And really, all the hard work was done
 *		by the Gustav and Robert. All I did was
 *		'javaize' this.
 *

 ---------------------------------------------------------- */


public class Mp3Header
{
    // Public variables for storing the information about the MP3
    public int intBitRate;
    public String strFileName;
    public long lngFileSize;
    public int intFrequency;
    public String strMode;
    public int intLength;
    public String strLengthFormatted;

    // Private variables used in the process of reading in the MP3 files
    private long bithdr;
    private boolean booleanVBitRate;
    private int intVFrames;

    public boolean ReadMP3Information(String FileName)
    {
        try{
			File infile=new File(FileName);
			RandomAccessFile fs = new RandomAccessFile(infile,"r");
			// Set the filename not including the path information
			strFileName = infile.getName();

			// Replace ' with '' for the SQL INSERT statement
			strFileName = strFileName.replace("'", "''");

			// Set the file size
			lngFileSize = infile.length();

			byte[] bytHeader = new byte[4];
			byte[] bytVBitRate = new byte[12];
			int intPos = 0;

			// Keep reading 4 bytes from the header until we know for sure that in
			// fact it's an MP3
			do
			{
				//fs.Position = intPos;
				fs.seek(intPos);
				//fs.Read(bytHeader,0,4);
				try{
					fs.readFully(bytHeader);
				}catch(IOException e){
					e.printStackTrace();
				}

				intPos++;
				LoadMP3Header(bytHeader);
			}
			while(!IsValidHeader() && (fs.getFilePointer()!=fs.length()));

			// If the current file stream position is equal to the length,
			// that means that we've read the entire file and it's not a valid MP3 file
			if(fs.getFilePointer() != fs.length())
			{
				intPos += 3;

				if(getVersionIndex() == 3)    // MPEG Version 1
				{
					if(getModeIndex() == 3)    // Single Channel
					{
						intPos += 17;
					}
					else
					{
						intPos += 32;
					}
				}
				else                        // MPEG Version 2.0 or 2.5
				{
					if(getModeIndex() == 3)    // Single Channel
					{
						intPos += 9;
					}
					else
					{
						intPos += 17;
					}
				}

				// Check to see if the MP3 has a variable bitrate

				fs.seek(intPos);
				fs.readFully(bytVBitRate);
				booleanVBitRate = LoadVBRHeader(bytVBitRate);

				// Once the file's read in, then assign the properties of the file to the public variables
				intBitRate = getBitrate();
				intFrequency = getFrequency();
				strMode = getMode();
				intLength = getLengthInSeconds();
				strLengthFormatted = getFormattedLength();
				fs.close();
				return true;

			}
		}catch(IOException e){
			e.printStackTrace();
		}
        return false;
    }

    private void LoadMP3Header(byte[] c)
    {
        // this thing is quite interesting, it works like the following
        // c[0] = 00000011
        // c[1] = 00001100
        // c[2] = 00110000
        // c[3] = 11000000
        // the operator << means that we'll move the bits in that direction
        // 00000011 << 24 = 00000011000000000000000000000000
        // 00001100 << 16 =         000011000000000000000000
        // 00110000 << 24 =                 0011000000000000
        // 11000000       =                         11000000
        //                +_________________________________
        //                  00000011000011000011000011000000
        bithdr = (long)(((c[0] & 255) << 24) | ((c[1] & 255) << 16) | ((c[2] & 255) <<  8) | ((c[3] & 255)));
    }

    private boolean LoadVBRHeader(byte[] inputheader)
    {
        // If it's a variable bitrate MP3, the first 4 bytes will read 'Xing'
        // since they're the ones who added variable bitrate-edness to MP3s
        if(inputheader[0] == 88 && inputheader[1] == 105 &&
            inputheader[2] == 110 && inputheader[3] == 103)
        {
            int flags = (int)(((inputheader[4] & 255) << 24) | ((inputheader[5] & 255) << 16) | ((inputheader[6] & 255) <<  8) | ((inputheader[7] & 255)));
            if((flags & 0x0001) == 1)
            {
                intVFrames = (int)(((inputheader[8] & 255) << 24) | ((inputheader[9] & 255) << 16) | ((inputheader[10] & 255) <<  8) | ((inputheader[11] & 255)));
                return true;
            }
            else
            {
                intVFrames = -1;
                return true;
            }
        }
        return false;
    }

    private boolean IsValidHeader()
    {
        return (((getFrameSync()      & 2047)==2047) &&
                ((getVersionIndex()   &    3)!=   1) &&
                ((getLayerIndex()     &    3)!=   0) &&
                ((getBitrateIndex()   &   15)!=   0) &&
                ((getBitrateIndex()   &   15)!=  15) &&
                ((getFrequencyIndex() &    3)!=   3) &&
                ((getEmphasisIndex()  &    3)!=   2)    );
    }

    private int getFrameSync()
    {
        return (int)((bithdr>>21) & 2047);
    }

    private int getVersionIndex()
    {
        return (int)((bithdr>>19) & 3);
    }

    private int getLayerIndex()
    {
        return (int)((bithdr>>17) & 3);
    }

    private int getProtectionBit()
    {
        return (int)((bithdr>>16) & 1);
    }

    private int getBitrateIndex()
    {
        return (int)((bithdr>>12) & 15);
    }

    private int getFrequencyIndex()
    {
        return (int)((bithdr>>10) & 3);
    }

    private int getPaddingBit()
    {
        return (int)((bithdr>>9) & 1);
    }

    private int getPrivateBit()
    {
        return (int)((bithdr>>8) & 1);
    }

    private int getModeIndex()
    {
        return (int)((bithdr>>6) & 3);
    }

    private int getModeExtIndex()
    {
        return (int)((bithdr>>4) & 3);
    }

    private int getCoprightBit()
    {
        return (int)((bithdr>>3) & 1);
    }

    private int getOrginalBit()
    {
        return (int)((bithdr>>2) & 1);
    }

    public int getEmphasisIndex()
    {
        return (int)(bithdr & 3);
    }

    public double getVersion()
    {
        double[] table = {2.5, 0.0, 2.0, 1.0};
        return table[getVersionIndex()];
    }

    public int getLayer()
    {
        return (int)(4 - getLayerIndex());
    }

    public int getBitrate()
    {
        // If the file has a variable bitrate, then we return an integer average bitrate,
        // otherwise, we use a lookup table to return the bitrate
        if(booleanVBitRate)
        {
            double medFrameSize = (double)lngFileSize / (double)getNumberOfFrames();
            return (int)((medFrameSize * (double)getFrequency()) / (1000.0 * ((getLayerIndex()==3) ? 12.0 : 144.0)));
        }
        else
        {
            int[][][] table =   {
                                { // MPEG 2 & 2.5
                                    {0,  8, 16, 24, 32, 40, 48, 56, 64, 80, 96,112,128,144,160,0}, // Layer III
                                    {0,  8, 16, 24, 32, 40, 48, 56, 64, 80, 96,112,128,144,160,0}, // Layer II
                                    {0, 32, 48, 56, 64, 80, 96,112,128,144,160,176,192,224,256,0}  // Layer I
                                },
                                { // MPEG 1
                                    {0, 32, 40, 48, 56, 64, 80, 96,112,128,160,192,224,256,320,0}, // Layer III
                                    {0, 32, 48, 56, 64, 80, 96,112,128,160,192,224,256,320,384,0}, // Layer II
                                    {0, 32, 64, 96,128,160,192,224,256,288,320,352,384,416,448,0}  // Layer I
                                }
                              };

            return table[getVersionIndex() & 1][getLayerIndex()-1][getBitrateIndex()];
        }
    }

    public int getFrequency()
    {
        int[][] table =    {
                            {32000, 16000,  8000}, // MPEG 2.5
                            {    0,     0,     0}, // reserved
                            {22050, 24000, 16000}, // MPEG 2
                            {44100, 48000, 32000}  // MPEG 1
                        };

        return table[getVersionIndex()][getFrequencyIndex()];
    }

    public String getMode()
    {
        switch(getModeIndex())
        {
            default:
                return "Stereo";
            case 1:
                return "Joint Stereo";
            case 2:
                return "Dual Channel";
            case 3:
                return "Single Channel";
        }
    }

    public int getLengthInSeconds()
    {
        // "intKilBitFileSize" made by dividing by 1000 in order to match the "Kilobits/second"
        int intKiloBitFileSize = (int)((8 * lngFileSize) / 1000);
        return (int)(intKiloBitFileSize/getBitrate());
    }

    public String getFormattedLength()
    {
        // Complete number of seconds
        int s  = getLengthInSeconds();

        // Seconds to display
        int ss = s%60;

        // Complete number of minutes
        int m  = (s-ss)/60;

        // Minutes to display
        int mm = m%60;

        // Complete number of hours
        int h = (m-mm)/60;

        // Make "hh:mm:ss"
        return h + ":" + mm + ":" + ss;
    }

    private int getNumberOfFrames()
    {
        // Again, the number of MPEG frames is dependant on whether it's a variable bitrate MP3 or not
        if (!booleanVBitRate)
        {
            double medFrameSize = (double)(((getLayerIndex()==3) ? 12 : 144) *((1000.0 * (float)getBitrate())/(float)getFrequency()));
            return (int)(lngFileSize/medFrameSize);
        }
        else
            return intVFrames;
    }

	public boolean isProtected(){
		return ( this.getProtectionBit() == 1 );
	}

	public boolean tester(){
		return false;
	}
}
