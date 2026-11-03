package cn.bugstack.unit.strategy;

import cn.bugstack.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.bugstack.domain.strategy.model.valobj.StrategyAwardStockKeyVO;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.armory.IStrategyDispatch;
import cn.bugstack.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import cn.bugstack.domain.strategy.service.rule.tree.impl.RuleStockLogicTreeNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RuleStockLogicTreeNodeTest {

    private static final long STRATEGY_ID = 100006L;
    private static final int AWARD_ID = 102;

    private final IStrategyDispatch strategyDispatch = Mockito.mock(IStrategyDispatch.class);
    private final IStrategyRepository strategyRepository = Mockito.mock(IStrategyRepository.class);
    private final RuleStockLogicTreeNode node = new RuleStockLogicTreeNode();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(node, "strategyDispatch", strategyDispatch);
        ReflectionTestUtils.setField(node, "strategyRepository", strategyRepository);
    }

    @Test
    void stock_deducted_takes_over_and_sends_delay_queue() {
        when(strategyDispatch.subtractionAwardStock(STRATEGY_ID, AWARD_ID)).thenReturn(true);

        DefaultTreeFactory.TreeActionEntity action =
                node.logic("unit_user", STRATEGY_ID, AWARD_ID, "tree_lock");

        assertEquals(RuleLogicCheckTypeVO.TAKE_OVER, action.getRuleLogicCheckType(),
                () -> "库存扣减成功应接管，实际 " + action.getRuleLogicCheckType());
        assertEquals(AWARD_ID, action.getStrategyAwardVO().getAwardId().intValue(),
                () -> "接管应携带原奖品，实际 " + action.getStrategyAwardVO().getAwardId());
        assertEquals("tree_lock", action.getStrategyAwardVO().getAwardRuleValue(),
                () -> "接管应携带规则值，实际 " + action.getStrategyAwardVO().getAwardRuleValue());

        ArgumentCaptor<StrategyAwardStockKeyVO> captor = ArgumentCaptor.forClass(StrategyAwardStockKeyVO.class);
        verify(strategyRepository).awardStockConsumeSendQueue(captor.capture());
        assertEquals(STRATEGY_ID, captor.getValue().getStrategyId().longValue(),
                () -> "延迟队列应携带策略 ID，实际 " + captor.getValue().getStrategyId());
        assertEquals(AWARD_ID, captor.getValue().getAwardId().intValue(),
                () -> "延迟队列应携带奖品 ID，实际 " + captor.getValue().getAwardId());
    }

    @Test
    void stock_not_deducted_allows_pass() {
        when(strategyDispatch.subtractionAwardStock(STRATEGY_ID, AWARD_ID)).thenReturn(false);

        DefaultTreeFactory.TreeActionEntity action =
                node.logic("unit_user", STRATEGY_ID, AWARD_ID, "tree_lock");

        assertEquals(RuleLogicCheckTypeVO.ALLOW, action.getRuleLogicCheckType(),
                () -> "库存不足应放行给后续节点，实际 " + action.getRuleLogicCheckType());
        assertNull(action.getStrategyAwardVO(), () -> "放行不应携带奖品信息，实际 " + action.getStrategyAwardVO());
        verify(strategyRepository, never()).awardStockConsumeSendQueue(any());
    }
}
