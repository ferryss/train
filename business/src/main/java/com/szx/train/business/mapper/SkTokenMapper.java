package com.szx.train.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szx.train.business.domain.SkToken;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * <p>
 * 秒杀令牌 Mapper 接口
 * </p>
 *
 * @author Ferry
 * @since 2026-01-22
 */
public interface SkTokenMapper extends BaseMapper<SkToken> {

    int decrease(@Param("date") Date date, @Param("trainCode") String trainCode);
}
