package com.vpn.service;

import com.vpn.entity.PO.VpnLog;
import com.vpn.entity.VO.PageResult;
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface VpnRepositoryService {
    public PageResult<VpnLog> getAllVpnLog(Pageable pageable);

}
