package genericskill;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import skillapi.SkillRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "genericskills", name = "Generic Skills Pack", version = "0.1", dependencies = "required-after:skillapi")
@NetworkMod(clientSideRequired = true)
public class GenericSkills {
	public static final String[] skills = { "Creeper Blast", "Levitate", "Summon Wolf", "Super Jump", "Healing Breeze", "Binding Signet", "Unrelenting Force", "Barrage" };
	public static Item genSkillBook, heritageAmulet, manaPotion;
	private static int skillBookId = 7777, heritageAmuletId = 7778, manaPotionId = 7779;

	@EventHandler
	public void configLoad(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		skillBookId = config.getItem("skillBookId", skillBookId).getInt();
		heritageAmuletId = config.getItem("heritageAmuletId", heritageAmuletId).getInt();
		manaPotionId = config.getItem("manaPotionId", manaPotionId).getInt();
		if (config.hasChanged())
			config.save();
	}

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
		SkillCreativeTab customTab = new SkillCreativeTab();
		genSkillBook = new ItemSkillBook(skillBookId).addSkills(skills).setCreativeTab(customTab);
		LanguageRegistry.addName(genSkillBook, "Generic Skill Book");
		GameRegistry.addShapelessRecipe(new ItemStack(genSkillBook), new Object[] { Item.ingotGold, Item.book });
		GameRegistry.registerItem(genSkillBook, "Generic Skill Book");
		heritageAmulet = new ItemHeritageAmulet(heritageAmuletId).setCreativeTab(customTab);
		LanguageRegistry.addName(heritageAmulet, "Heritage Amulet");
		GameRegistry.addRecipe(new ItemStack(heritageAmulet), new Object[] { " S ", "S S", "GDG", Character.valueOf('S'), Item.silk, Character.valueOf('G'), Item.ingotGold, Character.valueOf('D'),
			Item.diamond });
		GameRegistry.registerItem(heritageAmulet, "Heritage Amulet");
		manaPotion = (new ItemManaPotion(manaPotionId, 5)).setCreativeTab(customTab);
		LanguageRegistry.addName(manaPotion, "Mana Potion");
		GameRegistry.addShapelessRecipe(new ItemStack(manaPotion), new Object[] { Item.glassBottle, new ItemStack(Item.dyePowder, 1, 4) });
		GameRegistry.registerItem(manaPotion, "Mana Potion");
		EntityRegistry.registerModEntity(EntityShockWave.class, "FusRoDah", 0, this, 20, 4, true);
	}
}