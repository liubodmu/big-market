package cn.bugstack.unit.strategy;

import cn.bugstack.domain.strategy.service.armory.IStrategyDispatch;
import cn.bugstack.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import cn.bugstack.domain.strategy.service.rule.chain.impl.DefaultLogicChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class DefaultLogicChainTest {

    private static final long STRATEGY_ID = 100006L;

    private final IStrategyDispatch strategyDispatch = Mockito.mock(IStrategyDispatch.class);
    private final DefaultLogicChain chain = new DefaultLogicChain();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(chain, "strategyDispatch", strategyDispatch);
    }

    @Test
    void default_chain_returns_dispatch_award() {
        when(strategyDispatch.getRandomAwardId(STRATEGY_ID)).thenReturn(101);

        DefaultChainFactory.StrategyAwardVO vo = chain.logic("unit_user", STRATEGY_ID);

        assertEquals(101, vo.getAwardId().intValue(),
                () -> "默认链应返回调度奖品，实际 " + vo.getAwardId());
        assertEquals("rule_default", vo.getLogicModel(),
                () -> "默认链应标记 rule_default，实际 " + vo.getLogicModel());
    }
}
