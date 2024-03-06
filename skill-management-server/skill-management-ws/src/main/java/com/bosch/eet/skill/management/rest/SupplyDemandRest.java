package com.bosch.eet.skill.management.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bosch.eet.skill.management.common.Routes;
import com.bosch.eet.skill.management.dto.GenericResponseDTO;
import com.bosch.eet.skill.management.dto.request.CreateSupplyDemandRequestDTO;
import com.bosch.eet.skill.management.dto.request.UpdateSupplyDemandRequestDTO;
import com.bosch.eet.skill.management.dto.response.ChangeHistoryResponseDTO;
import com.bosch.eet.skill.management.dto.response.CreateInfoSupplyDemandResponseDTO;
import com.bosch.eet.skill.management.dto.response.SupplyDemandResponseDTO;
import com.bosch.eet.skill.management.exception.MessageCode;
import com.bosch.eet.skill.management.service.SupplyDemandService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RequestMapping
@RestController
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SupplyDemandRest {

    SupplyDemandService supplyDemandService;

    @GetMapping(value = Routes.URI_REST_SUPPLY_DEMAND_LIST_DEMAND)
    public GenericResponseDTO<List<SupplyDemandResponseDTO>> getAllSupplyDemand() {
        return GenericResponseDTO.<List<SupplyDemandResponseDTO>>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(supplyDemandService.getAllSupplyDemand())
                .build();
    }


    @PostMapping(value = Routes.URI_REST_SUPPLY_DEMAND_CREATE)
    @ResponseStatus(HttpStatus.CREATED)
    public GenericResponseDTO<List<SupplyDemandResponseDTO>> createSupplyDemand(@Valid @RequestBody CreateSupplyDemandRequestDTO request) {
        return GenericResponseDTO.<List<SupplyDemandResponseDTO>>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(supplyDemandService.createSupplyDemand(request))
                .build();
    }

    @PostMapping(value = Routes.URI_REST_SUPPLY_DEMAND_UPDATE)
    public GenericResponseDTO<SupplyDemandResponseDTO> updateASupplyDemand(@Valid @RequestBody UpdateSupplyDemandRequestDTO request,
                                                                           HttpServletRequest servletRequest) {
        String clientHref = servletRequest.getHeader("request_URL");
        return GenericResponseDTO.<SupplyDemandResponseDTO>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(supplyDemandService.updateASupplyDemand(request, clientHref))
                .build();
    }

    @PostMapping(value = Routes.URI_REST_SUPPLY_DEMAND_DELETE)
    public GenericResponseDTO<String> deleteASupplyDemand(@PathVariable("id") String id) {
        return GenericResponseDTO.<String>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(supplyDemandService.deleteSupplyDemand(id))
                .build();
    }

    @GetMapping(value = Routes.URI_REST_SUPPLY_DEMAND_CREATE_INFO)
    public GenericResponseDTO<CreateInfoSupplyDemandResponseDTO> getListSkill() {
        return GenericResponseDTO.<CreateInfoSupplyDemandResponseDTO>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(supplyDemandService.getInfoToCreateDemand())
                .build();
    }

    @GetMapping(value = Routes.URI_REST_SUPPLY_DEMAND_CHANGE_HISTORY)
    public GenericResponseDTO<List<ChangeHistoryResponseDTO>> getDemandChangeHistoryById(@PathVariable("id") String id) throws IllegalAccessException {
        return GenericResponseDTO.<List<ChangeHistoryResponseDTO>>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(supplyDemandService.getDemandChangeHistoryById(id))
                .build();
    }
    

    @GetMapping(value = Routes.URI_REST_SUPPLY_DEMAND_DEMAND_BY_SUBID)
    public GenericResponseDTO<SupplyDemandResponseDTO> getDemandBySubId(@PathVariable("subId") int subId){
        return GenericResponseDTO.<SupplyDemandResponseDTO>builder()
                .code(MessageCode.SUCCESS.toString())
                .data(supplyDemandService.getSupplyDemandBySubId(subId))
                .build();
    }
}


