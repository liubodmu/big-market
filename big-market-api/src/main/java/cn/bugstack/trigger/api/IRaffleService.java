package cn.bugstack.trigger.api;

import cn.bugstack.trigger.api.dto.RaffleAwardListRequestDTO;
import cn.bugstack.trigger.api.dto.RaffleAwardListResponseDTO;
import cn.bugstack.trigger.api.dto.RaffleRequestDTO;
import cn.bugstack.trigger.api.dto.RaffleResponseDTO;
import cn.bugstack.types.model.Response;


import java.util.List;

public interface IRaffleService {
    Response<Boolean> strategyAromry(Long strategyId);
    Response<List<RaffleAwardListResponseDTO>> queryRaffleAwardList(RaffleAwardListRequestDTO requestDTO);
    Response<RaffleResponseDTO> randomRaffle(RaffleRequestDTO requestDTO);
}
