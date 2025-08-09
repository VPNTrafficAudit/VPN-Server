package com.identify.service;

import com.identify.pojo.Flag;
import com.identify.pojo.PageResult;
import org.springframework.data.domain.Pageable;

import java.text.ParseException;

public interface IdatapullService{




    //PageResult<VpnLogs> findByFiveTupleDataNotNullAndConnectTimeAfter(Date date, Pageable pageable);

    PageResult<Flag> findByFiveTupleDataNotNullAndConnectTimeAfter(String date, Pageable pageable,Integer flag1) throws ParseException;
}
