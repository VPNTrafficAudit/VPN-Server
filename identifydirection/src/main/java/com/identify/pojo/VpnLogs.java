package com.identify.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import javax.xml.stream.Location;
import java.util.Date;
import java.util.List;

@Data
    @Document(indexName = "tzw_vpns_log")
    public class VpnLogs {
        @Id
        @Field(type = FieldType.Keyword)
        private String id;

        @Field(type = FieldType.Keyword)
        private String aud;

        @Field(type = FieldType.Date, format = DateFormat.epoch_second)
        private Date connectTime;

        @Field(type = FieldType.Date, format = DateFormat.epoch_second)
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

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class FiveTupleData {
            @MultiField(
                    mainField = @Field(type = FieldType.Text),
                    otherFields = {
                            @InnerField(suffix = "keyword", type = FieldType.Keyword, ignoreAbove = 256)
                    }
            )
            private String dst_ip;

            @Field(type = FieldType.Long)
            private Long dst_port;

            @Field(type = FieldType.Long)
            private Long proto_id;

            @MultiField(
                    mainField = @Field(type = FieldType.Text),
                    otherFields = {
                            @InnerField(suffix = "keyword", type = FieldType.Keyword, ignoreAbove = 256)
                    }
            )
            private String src_ip;

            @Field(type = FieldType.Long)
            private Long src_port;


        }

        @Data
        public static class GeoIp {
            @Field(type = FieldType.Keyword)
            private String city_name;

            @Field(type = FieldType.Keyword)
            private String country_iso_code;

            @MultiField(
                    mainField = @Field(type = FieldType.Text),
                    otherFields = {
                            @InnerField(suffix = "keyword", type = FieldType.Keyword, ignoreAbove = 256)
                    }
            )
            private String country_name;

            @Field(type = FieldType.Ip)
            private String ip;

            @Field(type = FieldType.Half_Float)
            private Float latitude;

            @GeoPointField
            private GeoPoint location;

            @Field(type = FieldType.Half_Float)
            private Float longitude;

            @Field(type = FieldType.Keyword)
            private String region_name;
        }

    }

