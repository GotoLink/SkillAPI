package genericskill;

import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import skillapi.SkillRegistry;

@Mod(modid = "genericskills", name = "Generic Skills Pack", version = "$version", dependencies = "required-after:skillapi")
public final class GenericSkills {
	public static final String[] skills = { "Creeper Blast", "Levitate", "Summon Wolf", "Super Jump", "Healing Breeze", "Binding Signet", "Unrelenting Force", "Barrage" };
	public static Item genSkillBook, heritageAmulet, manaPotion;

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
        GameRegistry.addRecipe(new ItemStack(heritageAmulet), " S ", "S S", "GDG", 'S', Items.string, 'G', Items.gold_ingot, 'D',
                Items.diamond);
        GameRegistry.addShapelessRecipe(new ItemStack(manaPotion), Items.glass_bottle, new ItemStack(Items.dye, 1, 4));
        if(event.getSide().isClient()){
            registerRenders();
        }
	}

    @SideOnly(Side.CLIENT)
    private void registerRenders() {
        ItemModelMesher mesher = FMLClientHandler.instance().getClient().getRenderItem().getItemModelMesher();
        mesher.register(genSkillBook, 0, new ModelResourceLocation("genericskills:GenericSkillBook", "inventory"));
        mesher.register(heritageAmulet, 0, new ModelResourceLocation("genericskills:HeritageAmulet", "inventory"));
        mesher.register(manaPotion, 0, new ModelResourceLocation("genericskills:ManaPotion", "inventory"));
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
        GameRegistry.registerItem(genSkillBook, "GenericSkillBook");
        heritageAmulet = new ItemHeritageAmulet().setCreativeTab(customTab);
        GameRegistry.registerItem(heritageAmulet, "HeritageAmulet");
        manaPotion = (new ItemManaPotion(5)).setCreativeTab(customTab);
        GameRegistry.registerItem(manaPotion, "ManaPotion");
        if(event.getSourceFile().getName().endsWith(".jar") && event.getSide().isClient()){
            try {
                Class.forName("mods.mud.ModUpdateDetector").getDeclaredMethod("registerMod", ModContainer.class, String.class, String.class).invoke(null,
                        FMLCommonHandler.instance().findContainerFor(this),
                        "https://raw.github.com/GotoLink/SkillAPI/master/Pack_update.xml",
                        "https://raw.github.com/GotoLink/SkillAPI/master/Pack_changelog.md"
                );
            } catch (Throwable e) {
            }
        }
    }

    @EventHandler
    public void remap(FMLMissingMappingsEvent event){
        for(FMLMissingMappingsEvent.MissingMapping missingMapping:event.get()){
            switch(missingMapping.type){
                case ITEM:
                    missingMapping.remap(GameData.getItemRegistry().getObject(missingMapping.name.replace(" ", "")));
                    break;
            }
        }
    }
}