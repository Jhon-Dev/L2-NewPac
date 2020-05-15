package net.sf.l2j.gameserver.handler.admincommandhandlers;

import java.util.Set;

import net.sf.l2j.commons.lang.StringUtil;

import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * This class handles following admin commands:
 * <ul>
 * <li>show_ivetory</li>
 * <li>delete_item</li>
 * </ul>
 * @author Zealar
 */

public class AdminInventory implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_show_inventory",
		"admin_delete_item"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if ((activeChar.getTarget() == null))
		{
			activeChar.sendMessage("Select a target");
			return false;
		}
		
		if (!(activeChar.getTarget() instanceof Player))
		{
			activeChar.sendMessage("Target need to be player");
			return false;
		}
		
		Player player = activeChar.getTarget().getActingPlayer();
		
		if (command.startsWith(ADMIN_COMMANDS[0]))
		{
			if (command.length() > ADMIN_COMMANDS[0].length())
			{
				String com = command.substring(ADMIN_COMMANDS[0].length() + 1);
				if (StringUtil.isDigit(com))
				{
					showItemsPage(activeChar, Integer.parseInt(com));
				}
			}
			
			else
			{
				showItemsPage(activeChar, 0);
			}
		}
		else if (command.contains(ADMIN_COMMANDS[1]))
		{
			String val = command.substring(ADMIN_COMMANDS[1].length() + 1);
			
			player.destroyItem("GM Destroy", Integer.parseInt(val), player.getInventory().getItemByObjectId(Integer.parseInt(val)).getCount(), null, true);
			showItemsPage(activeChar, 0);
		}
		
		return true;
	}
	
	private static void showItemsPage(Player activeChar, int page)
	{
		final Player target = activeChar.getTarget().getActingPlayer();
		
		final Set<ItemInstance> items = target.getInventory().getItems();
		
		int maxItemsPerPage = 16;
		int maxPages = items.size() / maxItemsPerPage;
		if (items.size() > (maxItemsPerPage * maxPages))
		{
			maxPages++;
		}
		
		if (page > maxPages)
		{
			page = maxPages;
		}
		
		int itemsStart = maxItemsPerPage * page;
		int itemsEnd = items.size();
		if ((itemsEnd - itemsStart) > maxItemsPerPage)
		{
			itemsEnd = itemsStart + maxItemsPerPage;
		}
		
		final NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
		adminReply.setFile("data/html/admin/inventory.htm");
		adminReply.replace("%PLAYER_NAME%", target.getName());
		
		StringBuilder sbPages = new StringBuilder();
		for (int x = 0; x < maxPages; x++)
		{
			int pagenr = x + 1;
			sbPages.append("<td><button value=\"" + String.valueOf(pagenr) + "\" action=\"bypass -h admin_show_inventory " + String.valueOf(x) + "\" width=14 height=14 back=\"sek.cbui67\" fore=\"sek.cbui67\"></td>");

		}
		
		adminReply.replace("%PAGES%", sbPages.toString());
		
		StringBuilder sbItems = new StringBuilder();
		
		for (ItemInstance item : items)
		{
			sbItems.append("<tr><td><button action=\"bypass -h admin_delete_item " + String.valueOf(item.getObjectId()) + "\" width=16 height=16 back=\"L2UI.bbs_delete\" fore=\"L2UI.bbs_delete\">" + "</td>");
			sbItems.append("<td width=60>" + item.getName() + "</td></tr><br>");
		}
		
		adminReply.replace("%ITEMS%", sbItems.toString());
		
		activeChar.sendPacket(adminReply);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}