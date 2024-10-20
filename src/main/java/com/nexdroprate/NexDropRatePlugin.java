package com.nexdroprate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Nex Drop Rate",
	description = "Tracks average drop rates and contribution at Nex",
	tags = {"combat", "droprate", "tracking"}
)

public class NexDropRatePlugin extends Plugin
{
	private final int[] MOB_IDs = {
		NpcID.NEX,
		NpcID.NEX_11279,
		NpcID.NEX_11280,
		NpcID.NEX_11281,
		NpcID.NEX_11282,
		NpcID.BLOOD_REAVER_11294,
		NpcID.CRUOR,
		NpcID.GLACIES,
		NpcID.FUMUS,
		NpcID.UMBRA
	};
	private int[] damageTotal;
	private List<Double> dropRates;
	private List<Double> contributions;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private NexDropRateOverlay overlay;

	@Getter
	private double averageDropRate;

	@Getter
	private double averageContribution;

	private void dropRate()
	{
		this.dropRates.add(((double) 1 / (43 * ((double) damageTotal[1] / damageTotal[0]))) * 100);
	}

	private void averageDropRate()
	{
		this.averageDropRate = dropRates.stream().mapToDouble(Double::doubleValue).sum() / dropRates.size();
	}

	private void contribution()
	{
		this.contributions.add(((double) damageTotal[0] / damageTotal[1]) * 100);
	}

	private void averageContribution()
	{
		this.averageContribution = contributions.stream().mapToDouble(Double::doubleValue).sum() / contributions.size();
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied event)
	{
		if (event.getActor() instanceof NPC)
		{
			int nex = ((NPC) event.getActor()).getComposition().getId();
			if (IntStream.of(MOB_IDs).anyMatch(id -> id == nex))
			{
				int damage = event.getHitsplat().getAmount();
				if (damage == 0)
				{
					return;
				}
				damageTotal[1] += damage;
				if (event.getHitsplat().isMine())
				{
					damageTotal[0] += damage;
				}
			}
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		int nex = event.getNpc().getId();
		if (nex == NpcID.NEX_11282)
		{
			dropRate();
			contribution();
			averageDropRate();
			averageContribution();
			damageTotal = new int[2];
		}
		if (nex == NpcID.NEX)
		{
			damageTotal = new int[2];
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		int nex = event.getNpc().getId();
		if (nex == NpcID.NEX_11279)
		{
			damageTotal = new int[2];
		}
	}
	@Override
	protected void startUp() throws Exception
	{

		overlayManager.add(overlay);

		this.damageTotal = new int[2];
		this.dropRates = new ArrayList<>();
		this.contributions = new ArrayList<>();
		this.averageDropRate = 0;
	}

	@Override
	protected void shutDown()
	{

		overlayManager.remove(overlay);

		this.damageTotal = new int[2];
		this.dropRates = new ArrayList<>();
		this.averageDropRate = 0;
	}
}
