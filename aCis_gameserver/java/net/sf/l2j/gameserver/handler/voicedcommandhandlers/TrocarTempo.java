package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import java.math.BigDecimal;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.InventoryUpdate;

/**
 * @author Jhonatan Nuss
 */
public class TrocarTempo implements IVoicedCommandHandler
{
	private static String[] VOICED_COMMANDS =
	{
		"trocar_tempo",
		"tempo_online"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		int _calcule = (int) arredondaValor(1, activeChar.getOnlineTime() / 3600);
		
		if (Config.ONLINETIME)
			
		if (command.startsWith("trocar_tempo"))
		{
			if ((_calcule >= 1) && (activeChar.getPvpKills() >= Config.MIN_PVP))
			{
				InventoryUpdate iu = new InventoryUpdate();
				activeChar.addItem("Squash Coins", Config.ID_REWARD, _calcule, activeChar, true);
				activeChar.sendPacket(iu);
				activeChar.setOnlineTime(0);
			}
			else
			{
				if (activeChar.getPvpKills() < Config.MIN_PVP)
				{
					activeChar.sendMessage("Sao necessarios " + Config.MIN_PVP + " pvp's para proceder a troca.Voce tem apenas" + activeChar.getPvpKills() + " PVP'S");
				}
				if (_calcule < 1)
				{
					activeChar.sendMessage("Voce nao tem 1 hora online atualmente.");
				}
			}
		}
		else if (command.startsWith("tempo_online"))
		{
			if (_calcule >= 1)
			{
				activeChar.sendMessage("Voce tem atualmente " + _calcule + " horas online.");
			}
			else if (_calcule < 1)
			{
				activeChar.sendMessage("Voce tem atualmente " + activeChar.getOnlineTime() / 60 + " minutos online.");
				
			}
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public static double arredondaValor(int casasDecimais, double valor)
	{
		BigDecimal decimal = new BigDecimal(valor);
		return decimal.setScale(casasDecimais, 3).doubleValue();
	}
	
	public boolean useVoicedCommand(String command, String all, Player activeChar, String text)
	{
		return false;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}