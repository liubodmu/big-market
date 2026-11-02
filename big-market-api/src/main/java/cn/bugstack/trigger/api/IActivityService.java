package cn.bugstack.trigger.api;

import cn.bugstack.trigger.api.dto.ActivityAccountRequestDTO;
import cn.bugstack.trigger.api.dto.ActivityAccountResponseDTO;
import cn.bugstack.trigger.api.dto.ActivityPartakeRequestDTO;
import cn.bugstack.trigger.api.dto.ActivityPartakeResponseDTO;
import cn.bugstack.trigger.api.dto.UserAwardRecordRequestDTO;
import cn.bugstack.trigger.api.dto.UserAwardRecordResponseDTO;
import cn.bugstack.types.model.Response;

import java.util.List;

public interface IActivityService {

    Response<ActivityPartakeResponseDTO> partakeActivity(ActivityPartakeRequestDTO requestDTO);

    Response<ActivityAccountResponseDTO> queryActivityAccount(ActivityAccountRequestDTO requestDTO);

    Response<List<UserAwardRecordResponseDTO>> queryUserAwardRecordList(UserAwardRecordRequestDTO requestDTO);

}
