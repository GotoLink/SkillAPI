package genericskill;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SkillCreativeTab extends CreativeTabs{

	public SkillCreativeTab() {
		super("GenericSkillPack");
		LanguageRegistry.instance().addStringLocalization("itemGroup.GenericSkillPack", "GenericSkillPack");
	}
	@Override
	@SideOnly(Side.CLIENT)
    public Item getTabIconItem()
    {
        return GenericSkills.heritageAmulet;
    }
}
