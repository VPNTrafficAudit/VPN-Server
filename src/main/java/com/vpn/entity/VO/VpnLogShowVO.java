package com.vpn.entity.VO;

import com.vpn.entity.PO.FiveTupleData;
import com.vpn.entity.PO.GeoIp;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class VpnLogShowVO {

    private String id;

    private String aud;

    private Date connectTime;

    private Date disconnectTime;

    private Long downstreamBytes;

    private Long downstreamPackets;

    private List<FiveTupleData> fiveTupleData;

    private GeoIp geoip;

    private String message;

    private Date packetTime;

    private String packetType;


    private Long upstreamBytes;

    private Long upstreamPackets;

    private String userId;


    private String uuid;


    private String vpnsIp;


    private Integer vpnsPort;

}
