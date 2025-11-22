package com.szx.train.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szx.train.common.util.SnowUtil;
import com.szx.train.member.domain.dto.PassengerDTO;
import com.szx.train.member.domain.po.Passenger;
import com.szx.train.member.mapper.PassengerMapper;
import com.szx.train.member.service.IPassengerService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * 乘车人 服务实现类
 * </p>
 *
 * @author Ferry
 * @since 2025-11-19
 */
@Service
public class PassengerServiceImpl extends ServiceImpl<PassengerMapper, Passenger> implements IPassengerService {

    @Override
    public void savePassenger(PassengerDTO passengerDTO) {
        LocalDateTime now = LocalDateTime.now();

        Passenger passenger = BeanUtil.copyProperties(passengerDTO, Passenger.class);

        passenger.setId(SnowUtil.getSnowflakeNextId());
        passenger.setCreateTime(now);
        passenger.setUpdateTime(now);

        save(passenger);
    }
}
