package com.szx.train.member.controller;


import com.szx.train.common.resp.CommonResp;
import com.szx.train.member.domain.dto.PassengerDTO;
import com.szx.train.member.service.IPassengerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
