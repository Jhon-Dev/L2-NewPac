/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author Jhonatan Nuss
 */

public class Stats implements IVoicedCommandHandler
{
	
	private static final String[] VOICED_COMMANDS =
	{
		"stats"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player player, String target)
	{
		if (command.equalsIgnoreCase("stats"))
		{
			if (player.getTarget() == null)
			{
				player.sendMessage("You have no one targeted.");
				return false;
			}
			if (!(player.getTarget() instanceof Player))
			{
				player.sendMessage("You can only get the info of a player.");
				
				return false;
			}
			
			NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
			Player targetp = (Player) player.getTarget();
			
			StringBuilder replyMSG = new StringBuilder("<html><title>L2JnCenter Info Player</title><body><center>");
			
			replyMSG.append("<br><table bgcolor=000000 width=330 height=17>");
			replyMSG.append("<tr>");
			replyMSG.append("<td><center>Stats Info Players in <font color=\"FFFF00\">L2JnCenter</font> Interlude</center></td>");
			replyMSG.append("</tr>");
			replyMSG.append("</table>");
			
			replyMSG.append("<br><br><img src=\"l2ui.bbs_lineage2\" height=16 width=80><br>");
			
			replyMSG.append("<td><img src=\"L2UI.SquareBlank\" width=40 height=2></td>");
			replyMSG.append("<center><img src=\"L2UI.SquareGray\" width=300 height=1></center>");
			replyMSG.append("<table bgcolor=000000 width=300 height=35><tr><td>");
			replyMSG.append("<tr>");
			replyMSG.append("<td>");
			replyMSG.append("<table>");
			replyMSG.append("<tr>");
			replyMSG.append("<td width=\"110\">PLayer:<font color=\"ae9977\"> " + targetp.getName() + "</font></td> <td width=\"100\">Level:<font color=\"ae9977\"> " + targetp.getLevel() + "</font></td>");
			replyMSG.append("</tr>");
			replyMSG.append("</table>");
			
			if (targetp.getClan() == null)
			{
				
				replyMSG.append("<table>");
				replyMSG.append("<tr>");
				replyMSG.append("<td width=\"110\">Clan:<font color=\"ae9977\"> None</font></td> <td width=\"100\">Alliance:<font color=\"ae9977\"> None</font></td>");
				replyMSG.append("</tr>");
				replyMSG.append("</table>");
				
			}
			else
			{
				
				replyMSG.append("<table>");
				replyMSG.append("<tr>");
				replyMSG.append("<td width=\"110\">Clan:<font color=\"ae9977\"> " + targetp.getClan().getName() + "</font></td> <td width=\"100\">Alliance:<font color=\"ae9977\"> " + targetp.getClan().getAllyName() + "</font></td>");
				replyMSG.append("</tr>");
				replyMSG.append("</table>");
				
			}
			
			replyMSG.append("<table>");
			replyMSG.append("<tr>");
			replyMSG.append("<td width=\"110\">P.Atk:<font color=\"ae9977\"> " + targetp.getPAtk(targetp) + "</font></td> <td width=\"100\">M.Atk:<font color=\"ae9977\"> " + targetp.getMAtk(targetp, null) + "</font></td>");
			replyMSG.append("</tr>");
			replyMSG.append("</table>");
			
			replyMSG.append("<table>");
			replyMSG.append("<tr>");
			replyMSG.append("<td width=\"110\">P.def:<font color=\"ae9977\"> " + targetp.getPDef(targetp) + "</font></td> <td width=\"100\">M.def:<font color=\"ae9977\"> " + targetp.getMDef(targetp, null) + "</font></td>");
			replyMSG.append("</tr>");
			replyMSG.append("</table>");
			
			replyMSG.append("<table>");
			replyMSG.append("<tr>");
			replyMSG.append("<td width=\"110\">Accuracy:<font color=\"ae9977\"> " + targetp.getAccuracy() + "</font></td> <td width=\"100\">Evasion:<font color=\"ae9977\"> " + targetp.getEvasionRate(targetp) + "</font></td>");
			replyMSG.append("</tr>");
			replyMSG.append("</table>");
			
			replyMSG.append("<table>");
			replyMSG.append("<tr>");
			replyMSG.append("<td width=\"110\">Crit.Rate:<font color=\"ae9977\"> " + targetp.getCriticalHit(targetp, null) + "</font></td> <td width=\"100\">Speed:<font color=\"ae9977\"> " + targetp.getMoveSpeed() + "</font></td>");
			replyMSG.append("</tr>");
			replyMSG.append("</table>");
			
			replyMSG.append("<table>");
			replyMSG.append("<tr>");
			replyMSG.append("<td width=\"110\">Atk.Spd:<font color=\"ae9977\"> " + targetp.getPAtkSpd() + "</font></td> <td width=\"100\">Casting Spd:<font color=\"ae9977\"> " + targetp.getMAtkSpd() + "</font></td>");
			replyMSG.append("</tr>");
			replyMSG.append("</table>");
			
			replyMSG.append("<table>");
			replyMSG.append("<tr>");
			replyMSG.append("<td width=\"110\">PvP kills:<font color=\"ae9977\"> " + targetp.getPvpKills() + "</font></td> <td width=\"100\">Pk kills:<font color=\"ae9977\"> " + targetp.getPkKills() + "</font></td>");
			replyMSG.append("</tr>");
			replyMSG.append("</table>");
			
			if (targetp.getActiveWeaponInstance() == null)
			{
				
				replyMSG.append("<table>");
				replyMSG.append("<tr>");
				replyMSG.append("<td width=\"110\">Weapon:<font color=\"ae9977\"> No.Equiped</font></td>");
				replyMSG.append("</tr>");
				replyMSG.append("</table>");
				
			}
			else
			{
				
				replyMSG.append("<table>");
				replyMSG.append("<tr>");
				replyMSG.append("<td width=\"110\">Weapon:<font color=\"ae9977\"> +" + targetp.getActiveWeaponInstance().getEnchantLevel() + "</font></td>");
				replyMSG.append("</tr>");
				replyMSG.append("</table>");
				
			}
			
			replyMSG.append("<table>");
			replyMSG.append("<tr>");
			replyMSG.append("<td width=\"110\">CP:<font color=\"ae9977\"> " + targetp.getMaxCp() + "</font></td> <td width=\"100\">HP:<font color=\"ae9977\"> " + targetp.getMaxHp() + "</font></td><td width=\"100\">MP:<font color=\"ae9977\"> " + targetp.getMaxMp() + "</font></td>");
			replyMSG.append("</tr>");
			replyMSG.append("</table>");
			
			replyMSG.append("</td>");
			replyMSG.append("</tr>");
			replyMSG.append("</table>");
			replyMSG.append("<center><img src=\"L2UI.SquareGray\" width=300 height=1></center>");
			replyMSG.append("<td><img src=\"L2UI.SquareBlank\" width=40 height=2></td>");
			
			replyMSG.append("<br><br><table bgcolor=000000 width=300 height=10>");
			replyMSG.append("<tr>");
			replyMSG.append("<td width=20><center>Vote for us : <font color=\"1C86EE\">www.L2JnCenter.com</font></center></td>");
			replyMSG.append("</tr>");
			replyMSG.append("</table>");
			replyMSG.append("</center></body></html>");
			
			adminReply.setHtml(replyMSG.toString());
			player.sendPacket(adminReply);
			
			adminReply = null;
			targetp = null;
			replyMSG = null;
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
	
}