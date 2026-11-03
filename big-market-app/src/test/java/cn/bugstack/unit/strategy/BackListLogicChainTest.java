package cn.bugstack.unit.strategy;

import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.rule.chain.ILogicChain;
import cn.bugstack.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import cn.bugstack.domain.strategy.service.rule.chain.impl.BackListLogicChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BackListLogicChainTest {

    private static final long STRATEGY_ID = 100006L;

    private final IStrategyRepository repository = Mockito.mock(IStrategyRepository.class);
    private final BackListLogicChain chain = new BackListLogicChain();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(chain, "repository", repository);
    }

    @Test
    void blacklist_hit_returns_blacklist_award() {
        when(repository.queryStrategyRuleValue(STRATEGY_ID, "rule_blacklist"))
                .thenReturn("101:unit_user_a,unit_user_b");

        DefaultChainFactory.StrategyAwardVO vo = chain.logic("unit_user_a", STRATEGY_ID);

        assertEquals(101, vo.getAwardId().intValue(),
                () -> "黑名单用户应返回黑名单奖品，实际 " + vo.getAwardId());
        assertEquals("rule_blacklist", vo.getLogicModel(),
                () -> "黑名单接管应标记 logicModel，实际 " + vo.getLogicModel());
    }

    @Test
    void blacklist_miss_passes_to_next_chain() {
        when(repository.queryStrategyRuleValue(STRATEGY_ID, "rule_blacklist"))
                .thenReturn("101:unit_user_a,unit_user_b");
        ILogicChain nextChain = Mockito.mock(ILogicChain.class);
        DefaultChainFactory.StrategyAwardVO expected = DefaultChainFactory.StrategyAwardVO.builder()
                .awardId(202).logicModel("rule_weight").build();
        when(nextChain.logic("xiaofuge", STRATEGY_ID)).thenReturn(expected);
        chain.appendNext(nextChain);

        DefaultChainFactory.StrategyAwardVO actual = chain.logic("xiaofuge", STRATEGY_ID);

        assertSame(expected, actual, () -> "黑名单未命中应放行到下一链，实际 " + actual);
    }
}
