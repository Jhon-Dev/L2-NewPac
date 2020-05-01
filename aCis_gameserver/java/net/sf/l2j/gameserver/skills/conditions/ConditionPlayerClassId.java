package net.sf.l2j.gameserver.skills.conditions;

import java.util.List;

import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.skills.L2Skill;

/**
 * @author Jhonatan Nuss
 */

public class ConditionPlayerClassId extends Condition
{
	private final List<Integer> _class;
	
	public ConditionPlayerClassId(List<Integer> race)
	{
		_class = race;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item)
	{
		return effector instanceof Player && _class.contains(((Player) effector).getClassId().getId());
	}
}