package com.vpn.entity.PO;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@Data
public class GeoIp {
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
