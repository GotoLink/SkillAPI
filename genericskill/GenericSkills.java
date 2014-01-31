package genericskill;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import skillapi.SkillRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = "genericskills", name = "Generic Skills Pack", version = "0.1", dependencies = "required-after:skillapi")
public class GenericSkills {
	public static final String[] skills = { "Creeper Blast", "Levitate", "Summon Wolf", "Super Jump", "Healing Breeze", "Binding Signet", "Unrelenting Force", "Barrage" };
	public static Item genSkillBook, heritageAmulet, manaPotion;
	private static int skillBookId = 7777, heritageAmuletId = 7778, manaPotionId = 7779;

	@EventHandler
	public void load(FMLInitializationEvent event) {
		SkillRegistry.registerSkill(new SkillCreeperBlast().setName(skills[0]).setTexture("creeperblast"));
		SkillRegistry.registerSkill(new SkillLevitate().setName(skills[1]).setTexture(skills[1].toLowerCase()));
		SkillRegistry.registerSkill(new SkillSummonWolf().setName(skills[2]).setTexture("summonwolf"));
		SkillRegistry.registerSkill(new SkillSuperJump().setName(skills[3]).setTexture("superjump"));
		SkillRegistry.registerSkill(new SkillHealingBreeze().setName(skills[4]).setTexture("healingbreeze"));
		SkillRegistry.registerSkill(new SkillBindingSignet().setName(skills[5]).setTexture("bindingsignet"));
		SkillRegistry.registerSkill(new SkillUnrelentingForce().setName(skills[6]).setTexture("unrelentingforce"));
		SkillRegistry.registerSkill(new SkillBarrage().setName(skills[7]).setTexture(skills[7].toLowerCase()));
		EntityRegistry.registerModEntity(EntityShockWave.class, "FusRoDah", 0, this, 20, 4, true);
        GameRegistry.addShapelessRecipe(new ItemStack(genSkillBook), Items.gold_ingot, Items.book);
        GameRegistry.addRecipe(new ItemStack(heritageAmulet), " S ", "S S", "GDG", Character.valueOf('S'), Items.string, Character.valueOf('G'), Items.gold_ingot, Character.valueOf('D'),
                Items.diamond);
        GameRegistry.addShapelessRecipe(new ItemStack(manaPotion), Items.glass_bottle, new ItemStack(Items.dye, 1, 4));
	}

    @EventHandler
    public void pre(FMLPreInitializationEvent event){
        CreativeTabs customTab = new CreativeTabs("GenericSkillPack") {
            @Override
            @SideOnly(Side.CLIENT)
            public Item getTabIconItem() {
                return GenericSkills.heritageAmulet;
            }
        };
        genSkillBook = new ItemSkillBook().addSkills(skills).setCreativeTab(customTab);
        GameRegistry.registerItem(genSkillBook, "Generic Skill Book");
        heritageAmulet = new ItemHeritageAmulet().setCreativeTab(customTab);
        GameRegistry.registerItem(heritageAmulet, "Heritage Amulet");
        manaPotion = (new ItemManaPotion(5)).setCreativeTab(customTab);
        GameRegistry.registerItem(manaPotion, "Mana Potion");
    }
}