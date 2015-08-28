package skillapi.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;
import skillapi.*;
import skillapi.packets.LearnSkillPacket;
import skillapi.packets.SkillPacket;

import java.util.Iterator;

public final class HudSkills extends Gui{
	private final Minecraft game;
	private EntityPlayer thePlayer;
	private PlayerSkills player = null;
	public int skillGetTimer = 50;

	public HudSkills() {
		this.game = Minecraft.getMinecraft();
	}

	public void drawHUD(int scaledWidth, int scaledHeight, float partialTicks) {
		if (thePlayer != game.getRenderViewEntity()) {
			thePlayer = (EntityPlayer) game.getRenderViewEntity();
			player = PlayerSkills.get(thePlayer);
		}
		if (player != null) {
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(770, 771);
			drawManaBar(scaledWidth, scaledHeight);
			drawSkillBar(scaledWidth, scaledHeight);
			drawChargeup(scaledWidth, scaledHeight);
			drawActiveSkills(scaledWidth, scaledHeight);
			drawSkillGet(scaledWidth, scaledHeight);
			GlStateManager.disableBlend();
			bind(Gui.icons);
		}
	}

	private void drawManaBar(int scaledWidth, int scaledHeight) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		bind(GuiKnownSkills.GUI);
		for (int manaPip = 0; manaPip < 10; manaPip++) {
			int posY = scaledHeight - 39 - 10;
			int posX = (scaledWidth / 2 + 10) + manaPip * 8;
			drawTexturedModalRect(posX, posY, 104, 143, 9, 9); //BG
			if (player.getMana() < player.getPrevMana()) {
				if (manaPip * 2 + 1 < player.getPrevMana())
					drawTexturedModalRect(posX, posY, 131, 143, 9, 9); //DMG Full pip
				if (manaPip * 2 + 1 == player.getPrevMana())
					drawTexturedModalRect(posX, posY, 140, 143, 9, 9); //DMG Half pip
			}
			if (manaPip * 2 + 1 < player.getMana())
				drawTexturedModalRect(posX, posY, 113, 143, 9, 9); //Full pip
			if (manaPip * 2 + 1 == player.getMana())
				drawTexturedModalRect(posX, posY, 122, 143, 9, 9); //Half pip
		}
	}

	private void drawSkillBar(int scaledWidth, int scaledHeight) {
		boolean isBarEmpty = true;
		for (Skill skill : player.skillBar)
			if (skill != null) {
				isBarEmpty = false;
				break;
			}
		if (isBarEmpty)
			return;
		int posX = (scaledWidth - 22), posY = ((scaledHeight - 102) / 2);
		drawTexturedModalRect(posX, posY, 218, 0, 22, 102);
		Skill skill;
		for (int i = 0; i < player.skillBar.length; i++) {
			skill = player.skillBar[i];
			if (skill != null) {
				bind(skill.getTexture());
				GL11.glScalef(0.0625F, 0.0625F, 1F);
				if (!skill.canPlayerUseSkill(thePlayer))
					GL11.glColor4f(0.4F, 0.4F, 0.4F, 1.0F);
				drawTexturedModalRect((posX + 3) * 16, (posY + 3 + (20 * i)) * 16, 0, 0, 256, 256);
				GL11.glScalef(16F, 16F, 1F);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				if (skill.cooldownFrame >= 1 && skill.getCooldownTime(thePlayer) > 0) {
					bind(GuiKnownSkills.GUI);
					drawTexturedModalRect(posX + 3, posY + 3 + (20 * i), 240, (int) (skill.cooldownFrame * 8D / (skill.getCooldownTime(thePlayer) * SkillTickHandler.mult)) * 16, 16, 16);
				}
			}
		}
	}

	private void drawChargeup(int scaledWidth, int scaledHeight) {
		if (player.chargingSkill != null) {
			bind(GuiKnownSkills.GUI);
			drawTexturedModalRect((scaledWidth - 104) / 2, scaledHeight - 70, 0, 134, 104, 10);
			drawTexturedModalRect((scaledWidth - 100) / 2, scaledHeight - 69, 0, 144,
					(int) (player.chargingSkill.charge * 104 / (player.chargingSkill.getChargeupTime(thePlayer) * SkillTickHandler.mult)), 8);
			game.fontRendererObj.drawStringWithShadow(player.chargingSkill.getName(), (scaledWidth - game.fontRendererObj.getStringWidth(player.chargingSkill.getName())) / 2, scaledHeight - 69,
					player.chargingSkill.getNameColour());
		}
	}

	private void drawActiveSkills(int scaledWidth, int scaledHeight) {
		if (player.activeSkills.isEmpty())
			return;
		int xOffset;
		Skill skill;
		Iterator<String> itr = player.activeSkills.iterator();
		for (int i = 0; i < player.activeSkills.size(); i++) {
			xOffset = ((scaledWidth - 22 - ((player.activeSkills.size() - 1) * 22)) / 2) + (22 * i);
			skill = SkillRegistry.get(itr.next());
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			bind(GuiKnownSkills.GUI);
			drawTexturedModalRect(xOffset, 0, 218, 102, 22, 20);
			bind(skill.getTexture());
			GL11.glScalef(0.0625F, 0.0625F, 1F);
			drawTexturedModalRect((xOffset + 3) * 16, 2 * 16, 0, 0, 256, 256);
			GL11.glScalef(16F, 16F, 1F);
			game.fontRendererObj.drawStringWithShadow(String.valueOf(skill.timeLeft), xOffset + 19 - game.fontRendererObj.getStringWidth(String.valueOf(skill.timeLeft)), 10, 0xFFFFFF);
		}
	}

	private void drawSkillGet(int scaledWidth, int scaledHeight) {
		if (player.skillJustLearnt != null) {
			if (skillGetTimer-- > 0) {
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.8F);
				bind(GuiKnownSkills.GUI);
				drawTexturedModalRect(scaledWidth - 150, (scaledHeight - 32) / 2 + (skillGetTimer % 50) - 50, 0, 152, 104, 32);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				bind(player.skillJustLearnt.getTexture());
				GL11.glScalef(0.0625F, 0.0625F, 1F);
				drawTexturedModalRect((scaledWidth - 140) * 16, ((scaledHeight - 16) / 2 + (skillGetTimer % 50) - 50) * 16, 0, 0, 256, 256);
				GL11.glScalef(16F, 16F, 1F);
				game.fontRendererObj.drawStringWithShadow("Skill Acquired:", scaledWidth - 120, (scaledHeight - 17) / 2 + (skillGetTimer % 50) - 50, -256);
				game.fontRendererObj.drawStringWithShadow(player.skillJustLearnt.getName(), scaledWidth - 120, (scaledHeight + 3) / 2 + (skillGetTimer % 50) - 50, player.skillJustLearnt.getNameColour());
			} else {
				player.skillJustLearnt = null;
                SkillPacket pkt = new LearnSkillPacket(thePlayer.getEntityId(), null);
				SkillAPI.channels.get(pkt.getChannel()).sendToServer(pkt.getPacket(Side.SERVER));
				skillGetTimer = 50;
			}
		}
	}

	private void bind(ResourceLocation texture){
		game.getTextureManager().bindTexture(texture);
	}
}
