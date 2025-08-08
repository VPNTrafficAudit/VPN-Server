package com.vpn.repository;


import com.vpn.entity.PO.VpnLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VpnRepository extends ElasticsearchRepository<VpnLog,String> {



} 