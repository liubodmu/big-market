package cn.bugstack.unit;

import cn.bugstack.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import cn.bugstack.domain.activity.model.entity.ActivityAccountDayEntity;
import cn.bugstack.domain.activity.model.entity.ActivityAccountEntity;
import cn.bugstack.domain.activity.model.entity.ActivityAccountMonthEntity;
import cn.bugstack.domain.activity.model.entity.ActivityEntity;
import cn.bugstack.domain.activity.model.entity.PartakeRaffleActivityEntity;
import cn.bugstack.domain.activity.model.entity.UserRaffleOrderEntity;
import cn.bugstack.domain.activity.model.valobj.ActivityStateVO;
import cn.bugstack.domain.activity.model.valobj.UserRaffleOrderStateVO;
import cn.bugstack.domain.activity.repository.IActivityRepository;
import cn.bugstack.domain.activity.service.partake.RaffleActivityPartakeService;
import cn.bugstack.types.exception.AppException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PartakeQuotaRulesTest {

    private static final String USER_ID = "unit_user";
    private static final long ACTIVITY_ID = 100301L;
    private static final long STRATEGY_ID = 100006L;

    private final IActivityRepository repository = Mockito.mock(IActivityRepository.class);
    private final RaffleActivityPartakeService service = new RaffleActivityPartakeService(repository);

    private ActivityEntity stubActivity(ActivityStateVO state, Date begin, Date end) {
        ActivityEntity activity = ActivityEntity.builder()
                .activityId(ACTIVITY_ID)
                .activityName("unit-activity")
                .strategyId(STRATEGY_ID)
                .state(state)
                .beginDateTime(begin)
                .endDateTime(end)
                .build();
        when(repository.queryRaffleActivityByActivityId(ACTIVITY_ID)).thenReturn(activity);
        return activity;
    }

    private PartakeRaffleActivityEntity request() {
        return PartakeRaffleActivityEntity.builder()
                .userId(USER_ID)
                .activityId(ACTIVITY_ID)
                .build();
    }

    private ActivityEntity validOpenActivity() {
        long dayMillis = 24L * 60 * 60 * 1000;
        return stubActivity(ActivityStateVO.open,
                new Date(System.currentTimeMillis() - dayMillis),
                new Date(System.currentTimeMillis() + dayMillis));
    }

    private ActivityAccountEntity mainAccount(int counts) {
        return ActivityAccountEntity.builder()
                .userId(USER_ID)
                .activityId(ACTIVITY_ID)
                .totalCount(counts)
                .totalCountSurplus(counts)
                .dayCount(counts)
                .dayCountSurplus(counts)
                .monthCount(counts)
                .monthCountSurplus(counts)
                .build();
    }

    @Test
    void state_not_open_throws_biz_003() {
        long dayMillis = 24L * 60 * 60 * 1000;
        stubActivity(ActivityStateVO.close,
                new Date(System.currentTimeMillis() - dayMillis),
                new Date(System.currentTimeMillis() + dayMillis));

        AppException thrown = assertThrows(AppException.class, () -> service.createOrder(request()));
        assertEquals("ERR_BIZ_003", thrown.getCode(), () -> "活动未 open 应返回 003，实际 " + thrown.getCode());
        verify(repository, never()).saveCreatePartakeOrderAggregate(any());
    }

    @Test
    void activity_out_of_date_throws_biz_004() {
        long dayMillis = 24L * 60 * 60 * 1000;
        stubActivity(ActivityStateVO.open,
                new Date(System.currentTimeMillis() - 2 * dayMillis),
                new Date(System.currentTimeMillis() - dayMillis));

        AppException thrown = assertThrows(AppException.class, () -> service.createOrder(request()));
        assertEquals("ERR_BIZ_004", thrown.getCode(), () -> "活动过期应返回 004，实际 " + thrown.getCode());
    }

    @Test
    void unused_order_is_reused_without_saving() {
        validOpenActivity();
        UserRaffleOrderEntity existing = UserRaffleOrderEntity.builder()
                .userId(USER_ID)
                .activityId(ACTIVITY_ID)
                .orderId("000111222333")
                .orderState(UserRaffleOrderStateVO.create)
                .build();
        when(repository.queryNoUsedRaffleOrder(any())).thenReturn(existing);

        UserRaffleOrderEntity actual = service.createOrder(request());

        assertSame(existing, actual, () -> "未使用订单应原样复用，实际 " + actual);
        verify(repository, never()).queryActivityAccountByUserId(anyString(), anyLong());
        verify(repository, never()).saveCreatePartakeOrderAggregate(any());
    }

    @Test
    void missing_main_account_throws_biz_006() {
        validOpenActivity();
        when(repository.queryActivityAccountByUserId(anyString(), anyLong())).thenReturn(null);

        AppException thrown = assertThrows(AppException.class, () -> service.createOrder(request()));
        assertEquals("ERR_BIZ_006", thrown.getCode(), () -> "缺失账户应返回 006，实际 " + thrown.getCode());
    }

    @Test
    void zero_total_surplus_throws_biz_006() {
        validOpenActivity();
        when(repository.queryActivityAccountByUserId(anyString(), anyLong())).thenReturn(mainAccount(0));

        AppException thrown = assertThrows(AppException.class, () -> service.createOrder(request()));
        assertEquals("ERR_BIZ_006", thrown.getCode(), () -> "总额度为 0 应返回 006，实际 " + thrown.getCode());
    }

    @Test
    void month_surplus_zero_throws_biz_007() {
        validOpenActivity();
        when(repository.queryActivityAccountByUserId(anyString(), anyLong())).thenReturn(mainAccount(5));
        when(repository.queryActivityAccountMonthByUserId(anyString(), anyLong(), anyString()))
                .thenReturn(ActivityAccountMonthEntity.builder()
                        .userId(USER_ID).activityId(ACTIVITY_ID).month("2026-11")
                        .monthCount(5).monthCountSurplus(0).build());

        AppException thrown = assertThrows(AppException.class, () -> service.createOrder(request()));
        assertEquals("ERR_BIZ_007", thrown.getCode(), () -> "月额度为 0 应返回 007，实际 " + thrown.getCode());
    }

    @Test
    void day_surplus_zero_throws_biz_008() {
        validOpenActivity();
        when(repository.queryActivityAccountByUserId(anyString(), anyLong())).thenReturn(mainAccount(5));
        when(repository.queryActivityAccountMonthByUserId(anyString(), anyLong(), anyString())).thenReturn(null);
        when(repository.queryActivityAccountDayByUserId(anyString(), anyLong(), anyString()))
                .thenReturn(ActivityAccountDayEntity.builder()
                        .userId(USER_ID).activityId(ACTIVITY_ID).day("2026-11-03")
                        .dayCount(5).dayCountSurplus(0).build());

        AppException thrown = assertThrows(AppException.class, () -> service.createOrder(request()));
        assertEquals("ERR_BIZ_008", thrown.getCode(), () -> "日额度为 0 应返回 008，实际 " + thrown.getCode());
    }

    @Test
    void happy_path_builds_and_saves_one_aggregate() {
        validOpenActivity();
        when(repository.queryNoUsedRaffleOrder(any())).thenReturn(null);
        when(repository.queryActivityAccountByUserId(anyString(), anyLong())).thenReturn(mainAccount(5));
        when(repository.queryActivityAccountMonthByUserId(anyString(), anyLong(), anyString())).thenReturn(null);
        when(repository.queryActivityAccountDayByUserId(anyString(), anyLong(), anyString())).thenReturn(null);

        UserRaffleOrderEntity created = service.createOrder(request());

        assertNotNull(created, () -> "正常参与应返回订单，实际 null");
        assertEquals(USER_ID, created.getUserId(), () -> "订单应归属调用方，实际 " + created.getUserId());
        assertEquals(ACTIVITY_ID, created.getActivityId().longValue(),
                () -> "订单应挂在指定活动，实际 " + created.getActivityId());
        assertSame(UserRaffleOrderStateVO.create, created.getOrderState(),
                () -> "新订单应为 create 状态，实际 " + created.getOrderState());

        ArgumentCaptor<CreatePartakeOrderAggregate> captor =
                ArgumentCaptor.forClass(CreatePartakeOrderAggregate.class);
        verify(repository, times(1)).saveCreatePartakeOrderAggregate(captor.capture());
        CreatePartakeOrderAggregate saved = captor.getValue();
        assertSame(created, saved.getUserRaffleOrderEntity(), () -> "聚合应持有待保存订单，实际 " + saved);
        assertNotNull(saved.getActivityAccountEntity(),
                () -> "聚合应携带账户实体，实际 " + saved.getActivityAccountEntity());
    }
}
