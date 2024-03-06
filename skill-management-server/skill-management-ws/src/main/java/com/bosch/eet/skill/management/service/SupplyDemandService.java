package com.bosch.eet.skill.management.service;

import java.util.List;

import com.bosch.eet.skill.management.dto.request.CreateSupplyDemandRequestDTO;
import com.bosch.eet.skill.management.dto.request.UpdateSupplyDemandRequestDTO;
import com.bosch.eet.skill.management.dto.response.ChangeHistoryResponseDTO;
import com.bosch.eet.skill.management.dto.response.CreateInfoSupplyDemandResponseDTO;
import com.bosch.eet.skill.management.dto.response.SupplyDemandResponseDTO;

public interface SupplyDemandService {
    CreateInfoSupplyDemandResponseDTO getInfoToCreateDemand();

    List<SupplyDemandResponseDTO> createSupplyDemand(CreateSupplyDemandRequestDTO request);

    List<SupplyDemandResponseDTO> getAllSupplyDemand();

    SupplyDemandResponseDTO updateASupplyDemand(UpdateSupplyDemandRequestDTO request, String href);

    List<ChangeHistoryResponseDTO> getDemandChangeHistoryById(String id) throws IllegalAccessException;

    String deleteSupplyDemand(String id);
    
    SupplyDemandResponseDTO getSupplyDemandBySubId(int subId);
}
