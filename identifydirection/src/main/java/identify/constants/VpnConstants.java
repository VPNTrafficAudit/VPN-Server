package identify.constants;

public class VpnConstants {

        public static final String MAPPING_TEMPLATE =
                "{\n" +
                        "  \"mappings\": {\n" +
                        "    \"properties\": {\n" +
                        "      \"packetType\": {\n" +
                        "        \"type\": \"keyword\"\n" +
                        "      },\n" +
                        "      \"downstreamBytes\": {\n" +
                        "        \"type\": \"integer\"\n" +
                        "      },\n" +
                        "      \"geoip\": {\n" +
                        "        \"type\": \"nested\",\n" +
                        "        \"properties\": {\n" +
                        "          \"city_name\": {\n" +
                        "            \"type\": \"text\"\n" +
                        "          },\n" +
                        "          \"country_iso_code\": {\n" +
                        "            \"type\": \"keyword\"\n" +
                        "          },\n" +
                        "          \"country_name\": {\n" +
                        "            \"type\": \"text\"\n" +
                        "          },\n" +
                        "          \"location\": {\n" +
                        "            \"type\": \"geo_point\"\n" +
                        "          }\n" +
                        "        }\n" +
                        "      },\n" +
                        "      \"disconnectTime\": {\n" +
                        "        \"type\": \"date\"\n" +
                        "      },\n" +
                        "      \"fiveTupleData\": {\n" +
                        "        \"type\": \"nested\",\n" +
                        "        \"properties\": {\n" +
                        "          \"userId\": {\n" +
                        "            \"type\": \"keyword\"\n" +
                        "          },\n" +
                        "          \"uuid\": {\n" +
                        "            \"type\": \"keyword\"\n" +
                        "          },\n" +
                        "          \"aud\": {\n" +
                        "            \"type\": \"keyword\"\n" +
                        "          }\n" +
                        "        }\n" +
                        "      },\n" +
                        "      \"downstreamPackets\": {\n" +
                        "        \"type\": \"integer\"\n" +
                        "      },\n" +
                        "      \"upstreamBytes\": {\n" +
                        "        \"type\": \"integer\"\n" +
                        "      },\n" +
                        "      \"connectTime\": {\n" +
                        "        \"type\": \"date\"\n" +
                        "      },\n" +
                        "      \"packetTime\": {\n" +
                        "        \"type\": \"date\"\n" +
                        "      },\n" +
                        "      \"upstreamPackets\": {\n" +
                        "        \"type\": \"integer\"\n" +
                        "      },\n" +
                        "      \"vpnsip\": {\n" +
                        "        \"type\": \"ip\"\n" +
                        "      }\n" +
                        "    }\n" +
                        "  }\n" +
                        "}";
    }

