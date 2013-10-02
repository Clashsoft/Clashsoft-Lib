package clashsoft.clashsoftapi.util;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import clashsoft.clashsoftapi.ClashsoftAPI;
import clashsoft.clashsoftapi.util.update.ModUpdate;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

/**
 * The Class CSUpdate.
 * 
 * This class adds several methods for updating mods.
 */
public class CSUpdate
{
	
	/** The found updates. */
	protected static Map<String, ModUpdate>	foundUpdates			= new HashMap();
	
	/** The Constant CURRENT_VERION. */
	public static final String				CURRENT_VERION			= "1.6.4";
	
	/** The Constant CLASHSOFT_ADFLY. */
	public static final String				CLASHSOFT_ADFLY			= "http://adf.ly/2175784/";
	
	/** The Constant CLASHSOFT_UPDATE_NOTES. */
	public static final String				CLASHSOFT_UPDATE_NOTES	= "https://dl.dropboxusercontent.com/s/pxm1ki6wbtxlvuv/update.txt";
	
	/**
	 * Checks if the website is available.
	 * 
	 * @param url
	 *            the url
	 * @return true, if available
	 */
	public static boolean checkWebsiteAvailable(String url)
	{
		try
		{
			URL url1 = new URL(url);
			
			HttpURLConnection.setFollowRedirects(false);
			HttpURLConnection con = (HttpURLConnection) url1.openConnection();
			con.setRequestMethod("HEAD");
			int response = con.getResponseCode();
			return response == HttpURLConnection.HTTP_OK;
		}
		catch (Exception ex)
		{
			return false;
		}
	}
	
	/**
	 * Reads a website or downloads its contents.
	 * 
	 * @param url
	 *            the url
	 * @return the string[]
	 */
	public static String[] readWebsite(String url)
	{
		try
		{
			URL url1 = new URL(url);
			HttpURLConnection.setFollowRedirects(true);
			HttpURLConnection con = (HttpURLConnection) url1.openConnection();
			con.setDoOutput(false);
			con.setReadTimeout(20000);
			con.setRequestProperty("Connection", "keep-alive");
			
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:16.0) Gecko/20100101 Firefox/20.0");
			con.setRequestMethod("GET");
			con.setConnectTimeout(5000);
			BufferedInputStream in = new BufferedInputStream(con.getInputStream());
			int responseCode = con.getResponseCode();
			StringBuffer buffer = new StringBuffer();
			int chars_read;
			while ((chars_read = in.read()) != -1)
			{
				char g = (char) chars_read;
				buffer.append(g);
			}
			return buffer.toString().split("\n");
		}
		catch (Exception ex)
		{
			return null;
		}
	}
	
	/**
	 * Creates a CLASHSOFT UNIQUE VERSION (e.g. v1.6.4-4)
	 * 
	 * @param rev
	 *            the rev
	 * @return the string
	 */
	public static String version(int rev)
	{
		return CURRENT_VERION + "-" + rev;
	}
	
	/**
	 * Checks if an update for Clashsoft mod is available.
	 * 
	 * @param modName
	 *            the mod name
	 * @param version
	 *            the current version
	 * @return the mod update
	 */
	public static ModUpdate checkForUpdate(String modName, String version)
	{
		return checkForUpdate(modName, CSString.getInitials(modName).toLowerCase(), version);
	}
	
	/**
	 * Checks if an update for Clashsoft mod is available.
	 * 
	 * @param modName
	 *            the mod name
	 * @param modInitials
	 *            the mod initials
	 * @param version
	 *            the version
	 * @return the mod update
	 */
	public static ModUpdate checkForUpdate(String modName, String modInitials, String version)
	{
		return checkForUpdate(modName, modInitials, version, readWebsite(CLASHSOFT_UPDATE_NOTES));
	}
	
	/**
	 * Checks if an update for a mod is available by reading the update file.
	 * 
	 * @param modName
	 *            the mod name
	 * @param modInitials
	 *            the mod initials
	 * @param version
	 *            the version
	 * @param updateFile
	 *            the update file
	 * @return the mod update
	 */
	public static ModUpdate checkForUpdate(String modName, String modInitials, String version, String[] updateFile)
	{
		String newVersion = version;
		String updateNotes = "";
		String updateUrl = "";
		for (int i = 0; i < updateFile.length; i++)
		{
			String s = updateFile[i];
			
			if (s.startsWith(modName) || s.startsWith(modInitials))
			{
				int i0 = s.indexOf(':');
				int i1 = s.indexOf('=');
				int i2 = s.indexOf('@');
				if (i0 == -1)
					break;
				
				newVersion = s.substring(i0 + 1, i1);
				if (i1 != -1)
					updateNotes = s.substring(i1 + 1, i2 == -1 ? s.length() : i2);
				if (i2 != -1)
					updateUrl = s.substring(i2 + 1);
				break;
			}
		}
		
		if (version != newVersion)
		{
			ModUpdate update = new ModUpdate(modName, modInitials, version, newVersion, updateNotes, updateUrl);
			
			if (update.isValid())
				foundUpdates.put(modName, update);
			
			return update;
		}
		return null;
	}
	
	/**
	 * Notifys a player about an update and installs it if {@link ClashsoftAPI.autoUpdate} is enabled.
	 * 
	 * @param player
	 *            the player
	 * @param modName
	 *            the mod name
	 * @param update
	 *            the update
	 */
	public static void notifyUpdate(EntityPlayer player, String modName, ModUpdate update)
	{
		if (update != null && update.isValid())
		{
			player.addChatMessage("A new " + modName + " version is available: " + EnumChatFormatting.GREEN + update.newVersion + EnumChatFormatting.RESET + ". You are using " + EnumChatFormatting.RED + update.version);
			if (!update.updateNotes.isEmpty())
				player.addChatMessage(EnumChatFormatting.RESET + "Update Notes: " + EnumChatFormatting.ITALIC + update.updateNotes);
			
			if (ClashsoftAPI.autoUpdate)
				update.install(player);
			else
				player.addChatMessage(EnumChatFormatting.RED + "Automatic updates disabled. Type \"@Update " + modName + "\" to manually install the update.");
		}
	}
	
	/**
	 * Installs an update.
	 * 
	 * @param player
	 *            the player
	 * @param modName
	 *            the mod name
	 */
	public static void update(EntityPlayer player, String modName)
	{
		ModUpdate update = foundUpdates.get(modName);
		if (update != null)
			update.install(player);
	}
	
	/**
	 * Compares two versions.
	 * 
	 * @param version1
	 *            the first version
	 * @param version2
	 *            the first version
	 * @return the comparation result
	 */
	public static int compareVersion(String version1, String version2)
	{
		int mcv1 = Integer.parseInt(version1.substring(0, version1.indexOf('-')).replace(".", ""));
		int mcv2 = Integer.parseInt(version2.substring(0, version2.indexOf('-')).replace(".", ""));
		int rev1 = Integer.parseInt(version1.substring(version1.indexOf('-') + 1, version1.length()));
		int rev2 = Integer.parseInt(version2.substring(version2.indexOf('-') + 1, version2.length()));
		
		int v1 = mcv1 << 4 | rev1;
		int v2 = mcv2 << 4 | rev2;
		
		return Integer.compare(v1, v2);
	}
}
