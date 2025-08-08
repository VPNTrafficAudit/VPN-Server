package com.vpn.entity.PO;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.util.Date;
import java.util.List;

@Data
@Document(indexName = "tzw_vpns_log")
public class VpnLog {
    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Keyword)
    private String aud;

    @Field(type = FieldType.Date,  pattern = "yyyy-MM-dd HH:mm:ss||epoch_second")
    private Date connectTime;

    @Field(type = FieldType.Date,  pattern = "yyyy-MM-dd HH:mm:ss||epoch_second")
    private Date disconnectTime;

    @Field(type = FieldType.Long)
    private Long downstreamBytes;

    @Field(type = FieldType.Long)
    private Long downstreamPackets;

    @Field(name = "fiveTupleData")
    private List<FiveTupleData> fiveTupleData;

    @Field(name = "geoip")
    private GeoIp geoip;

    @MultiField(
            mainField = @Field(type = FieldType.Text),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword, ignoreAbove = 256)
            }
    )
    private String message;

    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd HH:mm:ss||epoch_second")
    private Date packetTime;

    @Field(type = FieldType.Keyword)
    private String packetType;

    @Field(type = FieldType.Long)
    private Long upstreamBytes;

    @Field(type = FieldType.Long)
    private Long upstreamPackets;

    @Field(type = FieldType.Keyword)
    private String userId;

    @Field(type = FieldType.Keyword)
    private String uuid;

    @Field(type = FieldType.Keyword)
    private String vpnsIp;

    @Field(type = FieldType.Integer)
    private Integer vpnsPort;

}
