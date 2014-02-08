package clashsoft.cslib.minecraft.network;

import java.util.EnumMap;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class CSPacketHandler
{
	protected EnumMap<Side, FMLEmbeddedChannel>	channels;
	
	protected CSCodec							codec;
	protected CSMessageHandler					messageHandler;
	
	public CSPacketHandler(String name)
	{
		this.codec = this.createCodec();
		
		this.channels = NetworkRegistry.INSTANCE.newChannel(name, this.codec);
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT)
		{
			addClientHandler();
		}
	}
	
	public abstract CSCodec createCodec();
	
	public abstract CSMessageHandler createMessageHandler();
	
	@SideOnly(Side.CLIENT)
	private void addClientHandler()
	{
		this.messageHandler = this.createMessageHandler();
		
		FMLEmbeddedChannel clientChannel = this.channels.get(Side.CLIENT);
		String codec = clientChannel.findChannelHandlerNameForType(this.codec.getClass());
		clientChannel.pipeline().addAfter(codec, "ClientHandler", this.messageHandler);
	}
	
	public FMLEmbeddedChannel getClientChannel()
	{
		return this.channels.get(Side.CLIENT);
	}
	
	public FMLEmbeddedChannel getServerChannel()
	{
		return this.channels.get(Side.SERVER);
	}
}