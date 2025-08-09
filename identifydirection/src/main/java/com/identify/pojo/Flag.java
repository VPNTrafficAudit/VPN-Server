package com.identify.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flag {
    private String srcIp;
    private String dstIp;
    private Long srcPort;
    private Long dstPort;
    private Long protoId;

    // 对应的ID（VPN日志的ID）
    private String id;

    // Flag变量：0表示服务端，1表示客户端
    private int flag;

    // 构造函数（不包含flag，flag默认为0）
    public Flag(String srcIp, String dstIp, Long srcPort, Long dstPort, Long protoId, String id) {
        this.srcIp = srcIp;
        this.dstIp = dstIp;
        this.srcPort = srcPort;
        this.dstPort = dstPort;
        this.protoId = protoId;
        this.id = id;
        this.flag = 0; // 默认值
    }


}
