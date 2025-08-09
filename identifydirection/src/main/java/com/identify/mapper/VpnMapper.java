package com.identify.mapper;

import com.identify.pojo.VpnLogs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface VpnMapper extends ElasticsearchRepository<VpnLogs, String> {
    List<VpnLogs> findAll();
    Page<VpnLogs> findByFiveTupleDataNotNullAndConnectTimeAfter(String date, Pageable pageable,Integer flag1);
}
