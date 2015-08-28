package skillapi.client;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientEventHandler {
    public static final ClientEventHandler INSTANCE = new ClientEventHandler();
	private final HudSkills skillsHUD = new HudSkills();
    private ClientEventHandler(){}

	@SubscribeEvent
	public void renderHUD(RenderGameOverlayEvent.Post event) {
		if (event.type == ElementType.AIR) {
			skillsHUD.drawHUD(event.resolution.getScaledWidth(), event.resolution.getScaledHeight(), event.partialTicks);
		}
	}
}
