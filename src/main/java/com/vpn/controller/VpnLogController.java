package com.vpn.controller;


import com.vpn.entity.PO.VpnLog;
import com.vpn.entity.VO.PageResult;
import com.vpn.service.VpnRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vpnLog")
public class VpnLogController {
    @Autowired
    private VpnRepositoryService vpnRepositoryService;


    @GetMapping("/search/All")
    public PageResult<VpnLog> getAllVpnLogs(
            @RequestParam(value = "pageNumber") int pageNumber,
            @RequestParam(value = "pageSize") int pageSize
    ){
        Pageable pageable = PageRequest.of(pageNumber-1, pageSize);
        return vpnRepositoryService.getAllVpnLog(pageable);
    }

    

}
