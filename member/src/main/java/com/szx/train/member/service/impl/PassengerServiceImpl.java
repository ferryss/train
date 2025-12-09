package com.szx.train.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szx.train.common.context.LoginMemberContext;
import com.szx.train.common.exception.BusinessException;
import com.szx.train.common.exception.BusinessExceptionEnum;
import com.szx.train.common.util.SnowUtil;
import com.szx.train.member.domain.dto.PassengerDTO;
import com.szx.train.member.domain.dto.PassengerQueryDTO;
import com.szx.train.member.domain.po.Passenger;
import com.szx.train.member.domain.vo.PassengerVO;
import com.szx.train.member.mapper.PassengerMapper;
import com.szx.train.member.service.IPassengerService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
        // 数量校验
        Long count = lambdaQuery()
                .eq(LoginMemberContext.getId() != null, Passenger::getMemberId, LoginMemberContext.getId())
                .count();
        if(count >= 50){
            throw new BusinessException(BusinessExceptionEnum.MEMBER_PASSENGER_COUNT_EXCEEDING);
        }


        passengerDTO.setMemberId(LoginMemberContext.getId());

        LocalDateTime now = LocalDateTime.now();

        Passenger passenger = BeanUtil.copyProperties(passengerDTO, Passenger.class);

        passenger.setId(SnowUtil.getSnowflakeNextId());
        passenger.setCreateTime(now);
        passenger.setUpdateTime(now);

        save(passenger);
    }

    @Override
    public void updatePassenger(PassengerDTO passengerDTO) {
        LocalDateTime now = LocalDateTime.now();

        Passenger passenger = BeanUtil.copyProperties(passengerDTO, Passenger.class);
        passenger.setUpdateTime(now);
        boolean bool = updateById(passenger);
        if(!bool){
            throw new RuntimeException("更新乘车人失败");
        }
    }

    @Override
    public IPage<PassengerVO> queryList(PassengerQueryDTO passengerQueryDTO) {

        IPage<Passenger> page = new Page<>(passengerQueryDTO.getPage(), passengerQueryDTO.getSize());

        IPage<Passenger> list = lambdaQuery()
                .eq(LoginMemberContext.getId() != null , Passenger::getMemberId, LoginMemberContext.getId())
                .orderByDesc(Passenger::getCreateTime)
                .page(page);

        if(list.getRecords().isEmpty()){
            return null;
        }

        List<PassengerVO> passengerVOList = list.getRecords().stream().map(item -> {
            PassengerVO passengerVO = BeanUtil.copyProperties(item, PassengerVO.class);
            String idCard = passengerVO.getIdCard();
            //身份证中间4位
            int length = idCard.length();
            String start = idCard.substring(0, 3);
            String end = idCard.substring(length - 4);
            StringBuilder masked = new StringBuilder(start);
            for (int i = 0; i < length - 7; i++) {
                masked.append('*');
            }
            masked.append(end);
            passengerVO.setIdCard(masked.toString());

            return passengerVO;
        }).toList();

        IPage<PassengerVO> passengerVOPage = new Page<>(passengerQueryDTO.getPage(), passengerQueryDTO.getSize());
        passengerVOPage.setTotal(list.getTotal());
        passengerVOPage.setRecords(passengerVOList);

        return passengerVOPage;

    }

    @Override
    public PassengerVO queryById(Long id) {

        Passenger byId = getById(id);
        if(byId == null){
            return null;
        }

        return BeanUtil.copyProperties(byId, PassengerVO.class);
    }
}
