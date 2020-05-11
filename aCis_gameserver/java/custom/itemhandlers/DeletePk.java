package custom.itemhandlers;

import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.UserInfo;

/**
 * @author Jhonatan Nuss
 */

public class DeletePk implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
		{
			return;
		}
		Player player = (Player) playable;
		
		if (player.isAllSkillsDisabled())
		{
			
			player.sendPacket(ActionFailed.STATIC_PACKET);
			
			return;
		}
		if (player.isInOlympiadMode())
		{
			player.sendMessage("This item cannot be used on Olympiad Games.");
		}
		
		if (player.getPkKills() == 0)
		{
			
			player.sendMessage("You do not have PK's to be removed.");
		}
		else
		{
			
			player.setPkKills(0);
			playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
			player.sendMessage("Your PK's have been removed.");
			player.sendPacket(new UserInfo(player));
		}
	}
}
