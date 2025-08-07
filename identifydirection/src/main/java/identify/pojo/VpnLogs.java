package identify.pojo;


import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
public class VpnLogs {
    @Document(indexName = "tzw_vpn_log")
    public class VpnLog {

        @Id
        private String id;

        @Field(type = FieldType.Keyword)
        private String packetType;

        @Field( type = FieldType.Text)
        private Integer downstreamBytes;

        @Field(type = FieldType.Text)
        private String geoipCityName;

        @Field(type = FieldType.Keyword)
        private String geoipCountryIsoCode;

        @Field(type = FieldType.Text)
        private String geoipCountryName;

        @Field(type = FieldType.Object)
        private GeoIpLocation geoipLocation;

        @Field(type = FieldType.Date)
        private java.util.Date disconnectTime;

        @Field(type = FieldType.Object)
        private FiveTupleData fiveTupleData;

        @Field(type = FieldType.Integer)
        private Integer downstreamPackets;

        @Field(type = FieldType.Integer)
        private Integer upstreamBytes;

        @Field(type = FieldType.Date)
        private java.util.Date connectTime;

        @Field(type = FieldType.Date)
        private java.util.Date packetTime;

        @Field(type = FieldType.Keyword)
        private String vpnsip;

        // 嵌套对象的定义
        public static class GeoIpLocation {
            @Field(type = FieldType.Double)
            private Double lon;

            @Field(type = FieldType.Double)
            private Double lat;

            // 构造方法
            public GeoIpLocation(Double lon, Double lat) {  }
        }

        public static class FiveTupleData {
            @Field(type = FieldType.Keyword)
            private String userId;

            @Field(type = FieldType.Keyword)
            private String uuid;

            @Field(type = FieldType.Keyword)
            private String aud;

            // 构造方法、getter/setter 省略
        }

        // 构造方法、getter/setter 省略
    }
}
