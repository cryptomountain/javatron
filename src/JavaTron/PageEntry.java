/**
 * Copyright (C) 2010 Joe Culbreth
 */

package JavaTron;

/**
 * Defines a Data Structure for finding songs on the playlist
 * Used to delete songs from the active playlist safely
 *
 * @author Joe Culbreth <joe@connexsmart.com>
 * @version $Revision: 1.0 $
 */
public class PageEntry{
	private int idx;
	private int page;
	private int webindex;

	/**
	 * PageEntry Constructor
	 *
	 * @param idx_ Index
	 * @param page_ page Number
	 * @param webindex_ webindex
	 */
	PageEntry(int idx_,int page_, int webindex_){
		idx=idx_;
		page=page_;
		webindex=webindex_;
	}

	/**
	 * Sets all the values of a PageEntry
	 * 
	 * @param idx_
	 * @param page_
	 * @param webindex_
	 */
	public void setPageEntry(int idx_, int page_, int webindex_){
		idx=idx_;
		page=page_;
		webindex=webindex_;
	}

	/**
	 * 
	 * @return An int[] idx,page,webindex
	 */
	public int[] getValues(){
		int[] values={this.idx,this.page,this.webindex};
		return values;
	}

	public int getIndex(){
		return idx;
	}

	public int getPage(){
		return page;
	}

	public int getWebIndex(){
		return webindex;
	}

	@Override
	public String toString(){
		String rpt=idx+", "+page+", "+webindex;
		return rpt;
	}
}
