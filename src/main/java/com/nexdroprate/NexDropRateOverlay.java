package com.nexdroprate;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

class NexDropRateOverlay extends OverlayPanel
{
	private final NexDropRatePlugin plugin;

	@Inject
	private NexDropRateOverlay(NexDropRatePlugin plugin)
	{
		super(plugin);
		setPosition(OverlayPosition.TOP_LEFT);
		this.plugin = plugin;
		addMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Nex Drop Rate Overlay"); // Check
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		String[] dropRate = { fraction(plugin.getAverageDropRate()), percent(plugin.getAverageContribution()) };
		String[] type = { "Drop Rate:", "Contribution:" };

		panelComponent.getChildren().add(TitleComponent.builder()
			.text("Average")
			.color(Color.ORANGE)
			.build());

		for (int i = 0; i < dropRate.length; i++)
		{
			panelComponent.getChildren().add(LineComponent.builder()
				.left(type[i])
				.right(dropRate[i])
				.leftColor(Color.WHITE)
				.rightColor(Color.WHITE)
				.build());
		}

		return super.render(graphics);
	}

	private String percent(double droprate)
	{
		return String.format("%.1f%%", droprate);
	}

	private String fraction(double droprate)
	{
		if (droprate == 0)
		{
			return "N/A";
		}
		int denominator = (int) (100 / droprate);

		return "1/" + denominator;
	}
}
