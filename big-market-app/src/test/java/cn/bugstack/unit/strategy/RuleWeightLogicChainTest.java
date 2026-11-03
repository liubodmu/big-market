package cn.bugstack.unit.strategy;

import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.armory.IStrategyDispatch;
import cn.bugstack.domain.strategy.service.rule.chain.ILogicChain;
import cn.bugstack.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import cn.bugstack.domain.strategy.service.rule.chain.impl.RuleWeightLogicChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RuleWeightLogicChainTest {

    private static final long STRATEGY_ID = 100006L;

    private final IStrategyRepository repository = Mockito.mock(IStrategyRepository.class);
    private final IStrategyDispatch strategyDispatch = Mockito.mock(IStrategyDispatch.class);
    private final RuleWeightLogicChain chain = new RuleWeightLogicChain();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(chain, "repository", repository);
        ReflectionTestUtils.setField(chain, "strategyDispatch", strategyDispatch);
    }

    private void stubWeightRules(String ruleValue) {
        when(repository.queryStrategyRuleValue(STRATEGY_ID, "rule_weight")).thenReturn(ruleValue);
    }

    @Test
    void weight_picks_largest_threshold_at_or_below_user_score() {
        stubWeightRules("4000:102,103 5000:102,103,104 6000:102,103,104,105");
        chain.userScore = 5000L;
        when(strategyDispatch.getRandomAwardId(STRATEGY_ID, "5000:102,103,104")).thenReturn(104);

        DefaultChainFactory.StrategyAwardVO vo = chain.logic("unit_user", STRATEGY_ID);

        assertEquals(104, vo.getAwardId().intValue(),
                () -> "恰达 5000 分应命中 5000 档，实际 " + vo.getAwardId());
        assertEquals("rule_weight", vo.getLogicModel(),
                () -> "权重接管应带 logicModel，实际 " + vo.getLogicModel());
    }

    @Test
    void weight_picks_largest_threshold_below_user_score() {
        stubWeightRules("4000:102,103 5000:102,103,104 6000:102,103,104,105");
        chain.userScore = 5900L;
        when(strategyDispatch.getRandomAwardId(STRATEGY_ID, "5000:102,103,104")).thenReturn(104);

        DefaultChainFactory.StrategyAwardVO vo = chain.logic("unit_user", STRATEGY_ID);

        assertEquals(104, vo.getAwardId().intValue(),
                () -> "5900 分应命中 5000 档而非 6000 档，实际 " + vo.getAwardId());
    }

    @Test
    void weight_not_reached_passes_to_next_chain() {
        stubWeightRules("4000:102,103 5000:102,103,104");
        chain.userScore = 100L;
        ILogicChain nextChain = Mockito.mock(ILogicChain.class);
        DefaultChainFactory.StrategyAwardVO expected = DefaultChainFactory.StrategyAwardVO.builder()
                .awardId(202).logicModel("rule_default").build();
        when(nextChain.logic("unit_user", STRATEGY_ID)).thenReturn(expected);
        chain.appendNext(nextChain);

        DefaultChainFactory.StrategyAwardVO actual = chain.logic("unit_user", STRATEGY_ID);

        assertSame(expected, actual, () -> "分数不足应放行到下一链，实际 " + actual);
    }

    @Test
    void weight_empty_rule_value_passes_to_next_chain() {
        stubWeightRules("");
        chain.userScore = 5000L;
        ILogicChain nextChain = Mockito.mock(ILogicChain.class);
        DefaultChainFactory.StrategyAwardVO expected = DefaultChainFactory.StrategyAwardVO.builder()
                .awardId(203).logicModel("rule_default").build();
        when(nextChain.logic("unit_user", STRATEGY_ID)).thenReturn(expected);
        chain.appendNext(nextChain);

        DefaultChainFactory.StrategyAwardVO actual = chain.logic("unit_user", STRATEGY_ID);

        assertSame(expected, actual, () -> "权重规则值为空应对外放行，实际 " + actual);
    }

    @Test
    void weight_invalid_format_throws_raw_exception() {
        stubWeightRules("102,103");
        chain.userScore = 5000L;

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> chain.logic("unit_user", STRATEGY_ID));
        assertNotNull(thrown.getMessage(), () -> "权重格式错误应有异常消息，实际 " + thrown);
    }
}
