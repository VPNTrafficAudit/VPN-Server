package com.identify.controller;

import com.identify.pojo.Flag;
import com.identify.pojo.PageResult;
import com.identify.pojo.VpnLogs;
import com.identify.service.IdatapullService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/data")
public class datacontroller {
    @Autowired
    private IdatapullService datapullService;

    @GetMapping("/pullalldata")
    public PageResult<Flag> findByFiveTupleDataNotNullAndConnectTimeAfter(@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")String date, @RequestParam(value = "pageNum") int pageNum, @RequestParam(value = "pageSize") int pageSize, @RequestParam(value = "flag1") Integer flag1) throws ParseException {
        Pageable pageable = Pageable.ofSize(pageSize).withPage(pageNum);

        return datapullService.findByFiveTupleDataNotNullAndConnectTimeAfter(date, pageable,flag1);
    }



    }
