package com.szx.train.batch.service.impl;

import com.szx.train.batch.domain.QrtzLocks;
import com.szx.train.batch.mapper.QrtzLocksMapper;
import com.szx.train.batch.service.IQrtzLocksService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Ferry
 * @since 2025-11-29
 */
@Service
public class QrtzLocksServiceImpl extends ServiceImpl<QrtzLocksMapper, QrtzLocks> implements IQrtzLocksService {

}
