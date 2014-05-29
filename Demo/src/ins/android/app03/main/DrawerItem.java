package ins.android.app03.main;
/**
 * Navigation Drawer item includes icon and title
 * @author cxphong
 *
 */

public class DrawerItem {
	private String mTtitle;
	private int mIcon;
	
	public DrawerItem(String title , int icon)
	{
		this.mTtitle = title;
		this.mIcon = icon;
	}

	/**
	 * @return the mTtitle
	 */
	public String getmTtitle() {
		return mTtitle;
	}

	/**
	 * @return the mIcon
	 */
	public int getmIcon() {
		return mIcon;
	}

}
