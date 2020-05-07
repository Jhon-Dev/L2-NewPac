package net.sf.l2j.gameserver.network.clientpackets;

import java.util.Collection;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.EnchantResult;
import net.sf.l2j.gameserver.network.serverpackets.InventoryUpdate;
import net.sf.l2j.gameserver.network.serverpackets.ItemList;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;
import net.sf.l2j.gameserver.network.serverpackets.StatusUpdate;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.skills.L2Skill;

public final class RequestEnchantItem extends AbstractEnchantPacket
{
	private int _objectId;
	
	@Override
	protected void readImpl()
	{
		_objectId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null || _objectId == 0)
			return;
		
		if (!player.isOnline() || getClient().isDetached())
		{
			player.setActiveEnchantItem(null);
			return;
		}
		
		if (player.isProcessingTransaction() || player.isInStoreMode())
		{
			player.sendPacket(SystemMessageId.CANNOT_ENCHANT_WHILE_STORE);
			player.setActiveEnchantItem(null);
			player.sendPacket(EnchantResult.CANCELLED);
			return;
		}
		
		ItemInstance item = player.getInventory().getItemByObjectId(_objectId);
		ItemInstance scroll = player.getActiveEnchantItem();
		
		if (item == null || scroll == null)
		{
			player.setActiveEnchantItem(null);
			player.sendPacket(SystemMessageId.ENCHANT_SCROLL_CANCELLED);
			player.sendPacket(EnchantResult.CANCELLED);
			return;
		}
		
		// template for scroll
		final EnchantScroll scrollTemplate = getEnchantScroll(scroll);
		if (scrollTemplate == null)
			return;
		
		// first validation check
		if (!scrollTemplate.isValid(item) || !isEnchantable(item))
		{
			player.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITION);
			player.setActiveEnchantItem(null);
			player.sendPacket(EnchantResult.CANCELLED);
			return;
		}
		
		// attempting to destroy scroll
		scroll = player.getInventory().destroyItem("Enchant", scroll.getObjectId(), 1, player, item);
		if (scroll == null)
		{
			player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
			player.setActiveEnchantItem(null);
			player.sendPacket(EnchantResult.CANCELLED);
			return;
		}
		
		if (player.getActiveTradeList() != null)
		{
			player.cancelActiveTrade();
			player.sendPacket(SystemMessageId.TRADE_ATTEMPT_FAILED);
			return;
		}
		
		synchronized (item)
		{
			int chance = scrollTemplate.getChance(item);
			
			// last validation check
			if (item.getOwnerId() != player.getObjectId() || !isEnchantable(item) || chance < 0)
			{
				player.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITION);
				player.setActiveEnchantItem(null);
				player.sendPacket(EnchantResult.CANCELLED);
				return;
			}
			
			// success
			if (Rnd.get(100) < chance)
			{
				// announce the success
				SystemMessage sm;
				
				if (item.getEnchantLevel() == 0)
				{
					sm = SystemMessage.getSystemMessage(SystemMessageId.S1_SUCCESSFULLY_ENCHANTED);
					sm.addItemName(item.getItemId());
					player.sendPacket(sm);
				}
				else
				{
					sm = SystemMessage.getSystemMessage(SystemMessageId.S1_S2_SUCCESSFULLY_ENCHANTED);
					sm.addNumber(item.getEnchantLevel());
					sm.addItemName(item.getItemId());
					player.sendPacket(sm);
				}
				
				item.setEnchantLevel(item.getEnchantLevel() + 1);
				item.updateDatabase();
				player.sendPacket(EnchantResult.SUCCESS);
				
				// If item is equipped, verify the skill obtention (+4 duals, +6 armorset).
				if ((item.isItemList1() && item.getEnchantLevel() >= item.enchantBonusItemList1() + 13) || (item.isItemList2() && item.getEnchantLevel() >= item.enchantBonusItemList2() + 16) || (item.isItemList3() && item.getEnchantLevel() >= item.enchantBonusItemList3() + 18) || (item.isItemList4() && item.getEnchantLevel() >= item.enchantBonusItemList4() + 35) || (item.isItemList5() && item.getEnchantLevel() >= item.enchantBonusItemList5()))
				{
					final Collection<Player> pls = World.getInstance().getPlayers();
					for (Player onlinePlayer : pls)
					{
						if (onlinePlayer == null)
							continue;
						
						onlinePlayer.sendPacket(SystemMessage.sendString(player.getName() + " has successfuly enchanted a +" + item.getEnchantLevel() + " " + item.getName() + " with " + scroll.getName() + "."));
					}
				}
				L2Skill skill = SkillTable.FrequentSkill.LARGE_FIREWORK.getSkill();
				player.broadcastPacket(new MagicSkillUse(player, skill.getId(), skill.getLevel(), skill.getHitTime(), skill.getReuseDelay()));
				player.broadcastPacket(new SocialAction(player, 3));
			}
			else
			{
				// unequip item on enchant failure to avoid item skills stack
				if (item.isEquipped())
				{
					if (item.getEnchantLevel() > 0)
					{
						SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.EQUIPMENT_S1_S2_REMOVED);
						sm.addNumber(item.getEnchantLevel());
						sm.addItemName(item.getItemId());
						player.sendPacket(sm);
					}
					else
					{
						SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISARMED);
						sm.addItemName(item.getItemId());
						player.sendPacket(sm);
					}
					
					// Remove skill bestowed by +4 duals.
					ItemInstance[] unequiped = player.getInventory().unEquipItemInSlotAndRecord(item.getLocationSlot());
					InventoryUpdate iu = new InventoryUpdate();
					for (ItemInstance itm : unequiped)
						iu.addModifiedItem(itm);
					
					player.sendPacket(iu);
					player.broadcastUserInfo();
				}
				
				if ((item.isItemList1() && item.getEnchantLevel() >= item.enchantBonusItemList1() + 13) || (item.isItemList2() && item.getEnchantLevel() >= item.enchantBonusItemList2() + 16) || (item.isItemList3() && item.getEnchantLevel() >= item.enchantBonusItemList3() + 20) || (item.isItemList4() && item.getEnchantLevel() >= item.enchantBonusItemList4() + 35) || (item.isItemList5() && item.getEnchantLevel() >= item.enchantBonusItemList5()))
				{
					final Collection<Player> pls = World.getInstance().getPlayers();
					for (Player onlinePlayer : pls)
					
					{
						if (onlinePlayer == null)
							continue;
						
						onlinePlayer.sendPacket(SystemMessage.sendString(player.getName() + " has failed enchanted a +" + item.getEnchantLevel() + " " + item.getName() + " with " + scroll.getName() + "."));
					}
				}
				player.broadcastPacket(new SocialAction(player, 13));
				
			}
			
			if (scrollTemplate.isBlessed())
			{
				// blessed enchant - clear enchant value
				if (item.getEnchantLevel() > Config.ENCHANT_SAFE_MAX)
				{
					player.sendMessage("Failed in Blessed Enchant. The enchant value of the item reduced 1.");
					item.setEnchantLevel(item.getEnchantLevel() - 1);
				}
				else if (item.getEnchantLevel() == Config.ENCHANT_SAFE_MAX)
				{
					player.sendMessage("Failed in Blessed Enchant. The enchant value of the item became " + Config.ENCHANT_SAFE_MAX + ".");
					item.setEnchantLevel(Config.ENCHANT_SAFE_MAX);
				}
				
				item.updateDatabase();
				player.sendPacket(EnchantResult.UNSUCCESS);
				
			}
			else if (scrollTemplate.isCrystal())
			{
				// crystal enchant - clear enchant value
				player.sendMessage("Failed in Crystal Enchant. The enchant value of the item became " + Config.ENCHANT_SAFE_MAX + ".");
				
				item.setEnchantLevel(Config.ENCHANT_SAFE_MAX);
				item.updateDatabase();
				player.sendPacket(EnchantResult.UNSUCCESS);
				
			}
			else
			{
				if (!item.isItemList1() && !item.isItemList2() && !item.isItemList3() && !item.isItemList4() && !item.isItemList5())
				{
					
					// enchant - clear enchant value
					player.sendMessage("Failed in Enchant. The enchant value of the item became " + Config.ENCHANT_SAFE_MAX + ".");
					
					item.setEnchantLevel(Config.ENCHANT_SAFE_MAX);
					item.updateDatabase();
					player.sendPacket(EnchantResult.UNSUCCESS);
				}
				else
				{
					// enchant failed, destroy item
					int crystalId = item.getItem().getCrystalItemId();
					int count = item.getCrystalCount() - (item.getItem().getCrystalCount() + 1) / 2;
					if (count < 1)
						count = 1;
					
					ItemInstance destroyItem = player.getInventory().destroyItem("Enchant", item, player, null);
					if (destroyItem == null)
					{
						player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
						player.setActiveEnchantItem(null);
						player.sendPacket(EnchantResult.CANCELLED);
						return;
					}
					ItemInstance crystals = null;
					if (crystalId != 0)
					{
						crystals = player.getInventory().addItem("Enchant", crystalId, count, player, destroyItem);
						
						SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S);
						sm.addItemName(crystals.getItemId());
						sm.addItemNumber(count);
						player.sendPacket(sm);
					}
					
					if (!Config.FORCE_INVENTORY)
					{
						InventoryUpdate iu = new InventoryUpdate();
						if (destroyItem.getCount() == 0)
							iu.addRemovedItem(destroyItem);
						else
							iu.addModifiedItem(destroyItem);
						
						if (crystals != null)
							iu.addItem(crystals);
						
						player.sendPacket(iu);
					}
					else
						player.sendPacket(new ItemList(player, true));
					
					World world = World.getInstance();
					world.removeObject(destroyItem);
					if (crystalId == 0)
						player.sendPacket(EnchantResult.UNK_RESULT_4);
					else
						player.sendPacket(EnchantResult.UNK_RESULT_1);
				}
				
			}
		}
		StatusUpdate su = new StatusUpdate(player);
		su.addAttribute(StatusUpdate.CUR_LOAD, player.getCurrentLoad());
		player.sendPacket(su);
		
		player.sendPacket(new ItemList(player, false));
		player.broadcastUserInfo();
		player.setActiveEnchantItem(null);
		
	}
}