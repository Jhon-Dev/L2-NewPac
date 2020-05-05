package custom.handlers;

import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.skills.L2Skill;


/**
 * @author Jhonatan Nuss
 *
 */

   public class ClanFull implements IItemHandler
   {
       private final int reputation = 150000;
       private final byte level = 8;
  
       // id skills
       private final int[] clanSkills =
       {
           370,
           371,
           372,
           373,
           374,
           375,
           376,
           377,
           378,
           379,
           380,
           381,
           382,
           383,
           384,
           385,
           386,
           387,
           388,
           389,
           390,
           391
      };
  
       @Override
       public void useItem(Playable playable, ItemInstance item, boolean forceUse)
       {
           if (!(playable instanceof Player))
               return;
  
           Player player = (Player) playable;
  
           if (player.isClanLeader())
           {
               if (player.getClan().getLevel() == 8)
               {
                   player.sendMessage("Your clan is already maximum level!");
                   return;
               }
  
               player.getClan().changeLevel(level);
               player.getClan().addReputationScore(reputation);
  
               for (int s : clanSkills)
               {
                   L2Skill clanSkill = SkillTable.getInstance().getInfo(s, SkillTable.getInstance().getMaxLevel(s));
                   player.getClan().addNewSkill(clanSkill, forceUse);
               }
  
               player.sendSkillList();
               player.getClan().updateClanInDB();
               player.sendMessage("Your clan Level/Skills/Reputation has been updated!");
               playable.destroyItem("Consume", item.getObjectId(), 1, null, false);
               player.broadcastUserInfo();
           }
           else
               player.sendMessage("You are not the clan leader.");
  
           return;
       }
   }