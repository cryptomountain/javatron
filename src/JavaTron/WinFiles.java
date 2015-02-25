
package JavaTron;

/**
 * Class to access the windows shares that the Audiotron uses.
 * Adapted from SmbFunction JCIFS example code
 * Hey Samba Guys! GREAT WORK!
 * http://jcifs.samba.org
 *
 * @author Reza, Joe Culbreth
 */
import java.util.LinkedList;
import java.io.*;
import jcifs.UniAddress;
import jcifs.smb.*;



public class WinFiles
{
    private UniAddress domain;
    private NtlmPasswordAuthentication authentication;

    public WinFiles()
    {

    }

    /**
     *
     * @param address
     * @param username
     * @param password
     * @throws java.lang.Exception
     */
    public void login(String address, String username, String password) throws Exception
    {

        setDomain(UniAddress.getByName(address));
        setAuthentication(new NtlmPasswordAuthentication(address, username, password));
        SmbSession.logon(getDomain(), authentication);

    }

    /**
     *
     * @param path
     * @return
     * @throws java.lang.Exception
     */
    public LinkedList<String> getList(String path) throws Exception
    {
        LinkedList<String> fList = new LinkedList<String>();
        SmbFile f = new SmbFile(path,authentication);
        SmbFile[] fArr = f.listFiles();

        for(int a = 0; a < fArr.length; a++)
        {
            fList.add(fArr[a].getName());
            System.out.println(fArr[a].getName());
        }

        return fList;
    }

    /**
     *
     * @param path
     * @return
     * @throws java.lang.Exception
     */
    public boolean checkDirectory(String path) throws Exception
    {
        if(!isExist(path))
        {
            System.out.println(path + " not exist");
            return false;
        }

        if(!isDir(path))
        {
            System.out.println(path + " not a directory");
            return false;
        }

        return true;
    }

    /**
     *
     * @param path
     * @return
     * @throws java.lang.Exception
     */
    public boolean isExist(String path) throws Exception
    {
        SmbFile sFile = new SmbFile(path, authentication);

        return sFile.exists();
    }

    /**
     *
     * @param path
     * @return
     * @throws java.lang.Exception
     */
    public boolean isDir(String path) throws Exception
    {
        SmbFile sFile = new SmbFile(path, authentication);

        return sFile.isDirectory();
    }

    /**
     *
     * @param path
     * @throws java.lang.Exception
     */
    public void createDir(String path) throws Exception
    {
       SmbFile sFile = new SmbFile(path, authentication);

       sFile.mkdir();
    }

    /**
     *
     * @param path
     * @throws java.lang.Exception
     */
    public void delete(String path) throws Exception
    {
        SmbFile sFile = new SmbFile(path, authentication);
        sFile.delete();
    }

    /**
     *
     * @param path
     * @return
     * @throws java.lang.Exception
     */
    public long size(String path) throws Exception
    {
        SmbFile sFile = new SmbFile(path, authentication);

        return sFile.length();
    }

    /**
     *
     * @param path
     * @return
     * @throws java.lang.Exception
     */
    public String getFileName(String path) throws Exception
    {
        SmbFile sFile = new SmbFile(path, authentication);

        return sFile.getName();
    }

	public int getFileSize(String path) throws Exception
	{
		SmbFile sFile = new SmbFile(path,authentication);
		if(sFile.isFile()){
			return((int)sFile.length());
		}else{
			System.out.println("NOT a file");
			return(0);
		}
	}

	public byte[] getFile(String path) throws Exception
	{
		SmbFile sFile = new SmbFile(path, authentication);
		int size=(int)sFile.length();
		byte[] out=new byte[size];
		int i=0;
		InputStream fs=sFile.getInputStream();
		Reader r=new InputStreamReader(fs);
		int data = r.read();
		while(data != -1){
			out[i] = (byte)data;
			data = r.read();
			i++;
		}
		r.close();
		return out;
	}

	public boolean putFile(String path, String data) throws Exception, SmbException
	{

		try{
			SmbFile sFile = new SmbFile( path, authentication);
			int size=data.length();
			byte[] b=new byte[size];
			b=data.getBytes();
			SmbFileOutputStream os=new SmbFileOutputStream(sFile);
			os.write(b);
			os.flush();
			os.close();
		}catch(SmbException se){
			se.printStackTrace();
			throw new SmbException(se.getNtStatus(),true);
			//return false;
		}catch(IOException ioe){
			ioe.printStackTrace();
			throw new IOException();
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception();
			//return false;
		}
		return true;
	}
    /**
     * @return the domain
     */
    public UniAddress getDomain()
    {
        return domain;
    }

    /**
     * @param domain the domain to set
     */
    public void setDomain(UniAddress domain)
    {
        this.domain = domain;
    }

    /**
     * @return the authentication
     */
    public NtlmPasswordAuthentication getAuthentication()
    {
        return authentication;
    }

    /**
     * @param authentication the authentication to set
     */
    public void setAuthentication(NtlmPasswordAuthentication authentication)
    {
        this.authentication = authentication;
    }

}

