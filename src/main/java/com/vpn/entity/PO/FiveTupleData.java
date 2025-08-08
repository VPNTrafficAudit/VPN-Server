package com.vpn.entity.PO;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
@Data
public class FiveTupleData {
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

