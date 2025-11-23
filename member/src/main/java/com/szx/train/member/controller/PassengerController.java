package com.szx.train.member.controller;


import com.szx.train.common.resp.CommonResp;
import com.szx.train.member.domain.dto.PassengerDTO;
import com.szx.train.member.domain.vo.PassengerVO;
import com.szx.train.member.service.IPassengerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 乘车人 前端控制器
 * </p>
 *
 * @author Ferry
 * @since 2025-11-19
 */
@RestController
@RequestMapping("/passenger")
@RequiredArgsConstructor
public class PassengerController {

    private final IPassengerService passengerService;

    @PostMapping ("/save")
    public CommonResp<Object> savePassenger(@RequestBody @Valid PassengerDTO passengerDTO){
        passengerService.savePassenger(passengerDTO);
        return new CommonResp<>();
    }

    @PostMapping ("/update")
    public CommonResp<Object> updatePassenger(@RequestBody PassengerDTO passengerDTO){
        passengerService.updatePassenger(passengerDTO);
        return new CommonResp<>();
    }

    @GetMapping("/list")
    public CommonResp<List<PassengerVO>> queryList(){
        return new CommonResp<>(passengerService.queryList());
    }

    @DeleteMapping("/{id}")
    public CommonResp<Object> deletePassenger(@PathVariable Long id){
        return new CommonResp<>(passengerService.removeById(id));
    }

    @GetMapping("/{id}")
    public CommonResp<PassengerVO> queryById(@PathVariable Long id){
        return new CommonResp<>(passengerService.queryById(id));
    }
}
