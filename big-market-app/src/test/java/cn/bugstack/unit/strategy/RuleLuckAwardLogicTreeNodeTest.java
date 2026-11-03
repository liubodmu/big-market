package cn.bugstack.unit.strategy;

import cn.bugstack.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.bugstack.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import cn.bugstack.domain.strategy.service.rule.tree.impl.RuleLuckAwardLogicTreeNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RuleLuckAwardLogicTreeNodeTest {

    private final RuleLuckAwardLogicTreeNode node = new RuleLuckAwardLogicTreeNode();

    @Test
    void luck_award_without_desc_returns_empty_desc() {
        DefaultTreeFactory.TreeActionEntity action =
                node.logic("unit_user", 100006L, 104, "101");

        assertEquals(RuleLogicCheckTypeVO.TAKE_OVER, action.getRuleLogicCheckType(),
                () -> "兜底奖品应接管，实际 " + action.getRuleLogicCheckType());
        assertEquals(101, action.getStrategyAwardVO().getAwardId().intValue(),
                () -> "兜底应返回配置奖品 101，实际 " + action.getStrategyAwardVO().getAwardId());
        assertEquals("", action.getStrategyAwardVO().getAwardRuleValue(),
                () -> "无描述时应返回空串，实际 " + action.getStrategyAwardVO().getAwardRuleValue());
    }

    @Test
    void luck_award_with_desc_returns_desc() {
        DefaultTreeFactory.TreeActionEntity action =
                node.logic("unit_user", 100006L, 104, "101:感谢参与");

        assertEquals(101, action.getStrategyAwardVO().getAwardId().intValue(),
                () -> "兜底奖品 ID 应保持 101，实际 " + action.getStrategyAwardVO().getAwardId());
        assertEquals("感谢参与", action.getStrategyAwardVO().getAwardRuleValue(),
                () -> "兜底规则值应携带描述，实际 " + action.getStrategyAwardVO().getAwardRuleValue());
    }

    @Test
    void luck_award_empty_config_throws_raw_number_format_exception() {
        assertThrows(NumberFormatException.class, () -> node.logic("unit_user", 100006L, 104, ""),
                () -> "现状：空兜底配置抛裸 NumberFormatException（guard 死代码，已记缺陷库）");
    }
}
