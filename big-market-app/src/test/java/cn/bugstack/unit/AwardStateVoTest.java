package cn.bugstack.unit;

import cn.bugstack.domain.award.model.valobj.AwardStateVO;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AwardStateVoTest {

    @Test
    void create_code_maps_to_create_enum() {
        AwardStateVO actual = AwardStateVO.of("create");
        assertSame(AwardStateVO.create, actual, () -> "create 应映射到 create 枚举，实际 " + actual);
    }

    @Test
    void complete_code_maps_to_complete_enum() {
        AwardStateVO actual = AwardStateVO.of("complete");
        assertSame(AwardStateVO.complete, actual, () -> "complete 应映射到 complete 枚举，实际 " + actual);
    }

    @Test
    void fail_code_maps_to_fail_enum() {
        AwardStateVO actual = AwardStateVO.of("fail");
        assertSame(AwardStateVO.fail, actual, () -> "fail 应映射到 fail 枚举，实际 " + actual);
    }

    @Test
    void legacy_completed_code_maps_to_complete_enum() {
        AwardStateVO actual = AwardStateVO.of("completed");
        assertSame(AwardStateVO.complete, actual, () -> "completed 存量值应兼容映射到 complete，实际 " + actual);
    }

    @Test
    void unknown_code_throws_app_exception() {
        AppException thrown = assertThrows(AppException.class, () -> AwardStateVO.of("bogus"));
        assertEquals(ResponseCode.UN_ERROR.getCode(), thrown.getCode(),
                () -> "未知状态值应命中通用错误码，实际 " + thrown.getCode());
    }
}
