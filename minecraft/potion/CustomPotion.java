package clashsoft.cslib.minecraft.potion;

import clashsoft.cslib.util.CSArrays;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

/**
 * The Class CustomPotion.
 */
public class CustomPotion extends Potion
{
	/** The custom icon file. */
	private ResourceLocation	customIconFile;
	
	/** The instant. */
	private boolean				instant;
	
	/** The custom color. */
	private int					customColor;
	
	/** The bad. */
	private boolean				bad;
	
	public CustomPotion(String name, boolean bad, int color, boolean instant, String iconFile, int iconX, int iconY)
	{
		this(name, bad, color, instant, iconFile, iconX, iconY, -1);
	}
	
	public CustomPotion(String name, boolean bad, int color, boolean instant, ResourceLocation iconFile, int iconX, int iconY)
	{
		this(name, bad, color, instant, iconFile, iconX, iconY, -1);
	}
	
	public CustomPotion(String name, boolean bad, int color, boolean instant, String iconFile, int iconX, int iconY, int customColor)
	{
		this(name, bad, color, instant, new ResourceLocation(iconFile), iconX, iconY, customColor);
	}
	
	public CustomPotion(String name, boolean bad, int color, boolean instant, ResourceLocation iconFile, int iconX, int iconY, int customColor)
	{
		super(getNextFreeID(), bad, color);
		this.setPotionName(name);
		this.instant = instant;
		this.setIconIndex(iconX, iconY);
		this.customColor = customColor;
		this.bad = bad;
		this.customIconFile = iconFile;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getStatusIconIndex()
	{
		Minecraft.getMinecraft().renderEngine.bindTexture(this.customIconFile);
		return super.getStatusIconIndex();
	}
	
	/**
	 * Gets the custom color.
	 * 
	 * @return the custom color
	 */
	public int getCustomColor()
	{
		return this.customColor;
	}
	
	@Override
	public boolean isInstant()
	{
		return this.instant;
	}
	
	@Override
	public boolean isBadEffect()
	{
		return this.bad;
	}
	
	/**
	 * Gets the next free potion id.
	 * 
	 * @return the next free potion id
	 */
	public static int getNextFreeID()
	{
		int id = CSArrays.indexOf(potionTypes, null);
		if (id == -1)
			throw new IllegalStateException("No more empty potion IDs!");
		return id;
	}
}
