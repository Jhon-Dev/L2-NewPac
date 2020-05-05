package custom.handlers;

import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;

/**
 * @author Jhonatan Nuss
 */

public class ItemNobles implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		Player player = (Player) playable;
		
		if (player.isNoble())
		{
			player.sendMessage("You are already a noblesse.");
		}
		else
		{
			player.broadcastPacket(new MagicSkillUse(player, 5103, 1, 1000, 0));
			player.sendPacket(new ExShowScreenMessage("Congratulations! You are a Noble!", 8000));
			player.setNoble(true, true);
			player.getInventory().addItem("Tiara", 7694, 1, player, null);
			player.sendMessage("You are now a Noble, Check your Skills!");
			playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
			player.broadcastUserInfo();
		}
		player = null;
	}
}