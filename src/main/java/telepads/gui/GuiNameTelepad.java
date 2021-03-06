package telepads.gui;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;

import telepads.Telepads;
import telepads.block.TETelepad;
import telepads.packets.Serverpacket;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class GuiNameTelepad extends GuiScreen{

	private GuiTextField padNameField;

	public EntityPlayer thePlayer;
	public TETelepad te;

	public GuiNameTelepad(EntityPlayer player, TETelepad te){
		thePlayer = player;
		this.te = te;
	}

	@Override
	public void actionPerformed(GuiButton button) {
	}


	@Override
	public boolean doesGuiPauseGame(){
		return false;
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {

		int posX = (this.width ) / 2;
		int posY = (this.height ) / 2;
		try{

			String p = StatCollector.translateToLocal("enter.to.confirm");

			fontRendererObj.drawSplitString(p, (posX+1) -75, posY-1, 180 ,0x000000);
			fontRendererObj.drawSplitString(p, posX -75, posY, 180 ,0xffffff);

			String q = StatCollector.translateToLocal("name.your.telepad");

			fontRendererObj.drawSplitString(q + " : "+padNameField.getText(), (posX+1) -75, posY-1-20, 180 ,0x000000);
			fontRendererObj.drawSplitString(q + " : "+padNameField.getText(), posX   -75, posY  -20, 180 ,0xff0000);

		}finally{
			if(padNameField != null) {
				padNameField.drawTextBox();
			}
		}
	}

	@Override
	public void initGui() {

		int posX = (this.width ) / 2;
		int posY = (this.height ) / 2;
		this.buttonList.clear();

		padNameField = new GuiTextField(fontRendererObj, posX-(150/2) , posY-50, 150, 20);

		String padName = te.telepadname.equals("TelePad") ? te.getWorldObj().getBiomeGenForCoords(te.xCoord, te.zCoord).biomeName : te.telepadname;

		if(padNameField != null){
			padNameField.setText(padName);
			padNameField.setMaxStringLength(50);
		}
	}

	@Override
	protected void keyTyped(char c, int i)
	{
		super.keyTyped(c, i);
		if(i == Keyboard.KEY_RETURN) {
			sendPacket(padNameField.getText());
			te.isNamed = true;
			this.mc.thePlayer.closeScreen();
		}

		if(padNameField != null) {
			padNameField.textboxKeyTyped(c, i);
		}
	}


	@Override
	protected void mouseClicked(int i, int j, int k)
	{
		super.mouseClicked(i, j, k);
		if(padNameField != null) {
			padNameField.mouseClicked(i, j, k);
		}
	}

	public void sendPacket(String padName){

		ByteBuf buf = Unpooled.buffer();
		ByteBufOutputStream out = new ByteBufOutputStream(buf);

		try {
			out.writeInt(Serverpacket.SYNC_REGISTER);

			out.writeInt(te.xCoord);
			out.writeInt(te.yCoord);
			out.writeInt(te.zCoord);

			out.writeUTF(padName);

			Telepads.Channel.sendToServer(new FMLProxyPacket(buf, Telepads.packetChannel));
			out.close();
		} catch (Exception e) {
		}
	}
}
