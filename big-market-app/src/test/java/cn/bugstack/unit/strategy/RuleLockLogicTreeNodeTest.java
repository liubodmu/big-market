package cn.bugstack.unit.strategy;

import cn.bugstack.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.bugstack.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import cn.bugstack.domain.strategy.service.rule.tree.impl.RuleLockLogicTreeNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuleLockLogicTreeNodeTest {

    private final RuleLockLogicTreeNode node = new RuleLockLogicTreeNode();

    @Test
    void count_at_or_above_limit_allows() {
        DefaultTreeFactory.TreeActionEntity action = node.logic("unit_user", 100006L, 102, "5");

        assertEquals(RuleLogicCheckTypeVO.ALLOW, action.getRuleLogicCheckType(),
                () -> "用户 10 次超过阈值 5 应放行，实际 " + action.getRuleLogicCheckType());
        assertNull(action.getStrategyAwardVO(), () -> "放行不应携带奖品，实际 " + action.getStrategyAwardVO());
    }

    @Test
    void count_below_limit_takes_over() {
        DefaultTreeFactory.TreeActionEntity action = node.logic("unit_user", 100006L, 102, "15");

        assertEquals(RuleLogicCheckTypeVO.TAKE_OVER, action.getRuleLogicCheckType(),
                () -> "用户 10 次低于阈值 15 应拦截，实际 " + action.getRuleLogicCheckType());
        assertNull(action.getStrategyAwardVO(), () -> "拦截不应携带奖品，实际 " + action.getStrategyAwardVO());
    }

    @Test
    void invalid_rule_value_throws_config_error() {
        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> node.logic("unit_user", 100006L, 102, "abc"));
        assertTrue(thrown.getMessage().contains("次数锁"), () -> "异常信息应指向次数锁，实际 " + thrown.getMessage());
    }
}
