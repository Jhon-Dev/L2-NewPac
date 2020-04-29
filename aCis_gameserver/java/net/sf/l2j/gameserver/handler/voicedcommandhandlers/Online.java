package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;

public class Online implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"online"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player player, String target)
	{
		if (command.equalsIgnoreCase("online"))
			player.sendMessage("There are " + World.getInstance().getAllPlayersCount() * 1 + " player(s) online.");
		return Config.ONLINE_PLAYER;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}