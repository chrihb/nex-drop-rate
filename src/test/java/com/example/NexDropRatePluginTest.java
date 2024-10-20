package com.example;

import com.nexdroprate.NexDropRatePlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class NexDropRatePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(NexDropRatePlugin.class);
		RuneLite.main(args);
	}
}