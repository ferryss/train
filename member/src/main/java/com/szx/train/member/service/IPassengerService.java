package com.szx.train.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szx.train.member.domain.dto.PassengerDTO;
import com.szx.train.member.domain.po.Passenger;
import com.szx.train.member.domain.vo.PassengerVO;

import java.util.List;

/**
 * <p>
 * 乘车人 服务类
 * </p>
 *
 * @author Ferry
 * @since 2025-11-19
 */
public interface IPassengerService extends IService<Passenger> {

    void savePassenger(PassengerDTO passengerDTO);

    void updatePassenger(PassengerDTO passengerDTO);

    List<PassengerVO> queryList();

    PassengerVO queryById(Long id);
}
