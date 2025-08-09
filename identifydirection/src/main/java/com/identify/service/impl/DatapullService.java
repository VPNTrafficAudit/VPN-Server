package com.identify.service.impl;

import com.identify.pojo.Flag;
import com.identify.pojo.PageResult;
import com.identify.pojo.VpnLogs;
import com.identify.service.IdatapullService;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;

import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;

@Slf4j
@Service
public class DatapullService implements IdatapullService {

        @Autowired
        private ElasticsearchOperations elasticsearchOperations;



        @Override
        public PageResult<Flag> findByFiveTupleDataNotNullAndConnectTimeAfter(String date, Pageable pageable,Integer flag1) throws ParseException {

            //创建查询条件
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date formattedDate = simpleDateFormat.parse(date);
  //          String format = simpleDateFormat.format(formattedDate);
//            String formattedDate = dateFormat.format(date);
            log.info("查询时间：{}", date);
  //          log.info("aaa{}",date.getTime());
            NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.boolQuery()
                            .must((QueryBuilders.existsQuery("fiveTupleData")))
                            .must(QueryBuilders.rangeQuery("connectTime").gt(formattedDate.getTime()/1000))
                    )
                    .withPageable(pageable)
                    .withSort(Sort.by(Sort.Direction.DESC, "connectTime"));
            log.info("查询条件：{}", searchQueryBuilder.build().getQuery());
//

            // 执行查询
            Query searchQuery = searchQueryBuilder
                    .withPageable(PageRequest.of(0, 10000))  // 设置返回10000条
                    .build();

            SearchHits<VpnLogs> searchHits = elasticsearchOperations.search(searchQuery, VpnLogs.class);
            List<VpnLogs> data = searchHits.getSearchHits().stream().map(SearchHit::getContent).toList();


            //日志查询结果数量
            log.info("查询结果数量：{}", data.size());

            //计算分页结果


//            long total = searchHits.getTotalHits();






            Map<VpnLogs.FiveTupleData, String> fiveTupleDataIdMap = new HashMap<>();
            for (VpnLogs vpnLogs : data) {
                for (VpnLogs.FiveTupleData fiveTupleData : vpnLogs.getFiveTupleData()) {
                    fiveTupleDataIdMap.put(fiveTupleData, vpnLogs.getId());
                }
            }
            //输出五元组数
            log.info("五元组数：{}", fiveTupleDataIdMap.size());
            for (Map.Entry<VpnLogs.FiveTupleData, String> entry : fiveTupleDataIdMap.entrySet()) {
                VpnLogs.FiveTupleData fiveTupleData = entry.getKey();
                String id = entry.getValue();
                log.info("五元组：{}", fiveTupleData);
                log.info("id：{}", id);
            }
//            Map<Map<VpnLogs.FiveTupleData, String>,Integer> flagMap = new HashMap<>();
//            //创建flagmap,key为五元组，value为
//            for (Map.Entry<VpnLogs.FiveTupleData, String> entry : fiveTupleDataIdMap.entrySet()) {
//                Map<VpnLogs.FiveTupleData, String> key = new HashMap<>();
//                key.put(entry.getKey(), entry.getValue());
//                flagMap.put(key,1);
//            }





            //定义一个新List,实体类为Flag
            List<Flag> flagList = new ArrayList<>();
            //将fiveTupleDataIdMap中的每个五元组及其对应id存入flaglist,并设置flag为1
            for (Map.Entry<VpnLogs.FiveTupleData, String> entry : fiveTupleDataIdMap.entrySet()) {
                flagList.add(new Flag(entry.getKey().getSrc_ip(), entry.getKey().getDst_ip(), entry.getKey().getSrc_port(), entry.getKey().getDst_port(), entry.getKey().getProto_id(), entry.getValue()));
                flagList.get(flagList.size()-1).setFlag(1);
            }
            System.out.println("初始数据量: " + flagList.size());

// 添加调试统计
            Map<String, Integer> portDistribution = new HashMap<>();
            Map<String, Integer> ipPatternStats = new HashMap<>();
            int serverCount = 0;
            int clientCount = 0;

                for (Flag flag : flagList) {
                    try {
                        // 调试信息：统计端口分布
                        String portRange = getPortRange(flag.getSrcPort());
                        portDistribution.put(portRange, portDistribution.getOrDefault(portRange, 0) + 1);

                        // 获取IP类型
                        boolean srcIsPrivate = isPrivateIP(flag.getSrcIp());
                        boolean dstIsPrivate = isPrivateIP(flag.getDstIp());

                        String ipPattern = getIpPattern(srcIsPrivate, dstIsPrivate);
                        ipPatternStats.put(ipPattern, ipPatternStats.getOrDefault(ipPattern, 0) + 1);

                        // 修改后的判断逻辑 - 针对VPN场景优化
                        boolean isServer = false;

                        // 规则1: 源端口在知名端口范围 (更宽松的范围)
                        if (flag.getSrcPort() != null && flag.getSrcPort() <= 1024) {
                            isServer = true;
                        }
                        // 规则2: 常见的服务端端口（即使>1024）
                        else if (flag.getSrcPort() != null && isCommonServerPort(flag.getSrcPort())) {
                            isServer = true;
                        }
                        // 规则3: VPN服务端常用端口
                        else if (flag.getSrcPort() != null && isVpnServerPort(flag.getSrcPort())) {
                            isServer = true;
                        }
                        // 规则4: 端口比较 - 如果源端口明显小于目标端口
                        else if (flag.getSrcPort() != null && flag.getDstPort() != null) {
                            if (flag.getSrcPort() < 10000 && flag.getDstPort() > 30000) {
                                isServer = true;
                            }
                        }
                        // 规则5: IP模式判断 - 外网到内网通常是服务访问
                        else if (!srcIsPrivate && dstIsPrivate) {
                            isServer = true;
                        }
                        // 规则6: 对于VPN场景，如果目标端口是VPN客户端端口
                        else if (flag.getDstPort() != null && isVpnClientPort(flag.getDstPort())) {
                            isServer = true;  // 源端是VPN服务端
                        }

                        // 设置flag
                        if (isServer) {
                            flag.setFlag(0);
                            serverCount++;
                        } else {
                            flag.setFlag(1);
                            clientCount++;
                        }

                    } catch (Exception e) {
                        System.err.println("处理Flag时出错: " + e.getMessage());
                        e.printStackTrace();
                        // 出错时保持默认值（客户端）
                        clientCount++;
                    }
                    // 输出调试信息
                    System.out.println("处理后数据量: " + flagList.size());
                    System.out.println("服务端数量: " + serverCount);
                    System.out.println("客户端数量: " + clientCount);
                    System.out.println("端口分布: " + portDistribution);
                    System.out.println("IP模式分布: " + ipPatternStats);


            }
            //计算分页结果，只计算flag值符合flag1
            long total = flagList.stream().filter(flag -> flag.getFlag() == flag1).count();
            int pageSize = pageable.getPageSize();
            int currentPage = pageable.getPageNumber() + 1;
            int pages = (int) Math.ceil((double) total / pageSize);

            //创建新的PageResult,根据
            PageResult<Flag> result = new PageResult<>();
            result.setTotal(total);
            result.setPages(pages);
            result.setPageSize(pageSize);
            result.setCurrentPage(currentPage);
            //设置数据,flagList中flag为参数flag1的
            result.setData(flagList.stream().filter(flag -> flag.getFlag() == flag1).collect(Collectors.toList()));
            return result;

        }
    // 调试方法：获取端口范围描述
    private static String getPortRange(Long port) {
        if (port == null) return "null";
        if (port <= 1024) return "知名端口(≤1024)";
        if (port <= 10000) return "用户端口(1025-10000)";
        if (port <= 32767) return "用户端口(10001-32767)";
        if (port <= 49151) return "动态端口(32768-49151)";
        return "临时端口(≥49152)";
    }

    // 调试方法：获取IP模式描述
    private static String getIpPattern(boolean srcPrivate, boolean dstPrivate) {
        if (srcPrivate && dstPrivate) return "内网->内网";
        if (srcPrivate && !dstPrivate) return "内网->外网";
        if (!srcPrivate && dstPrivate) return "外网->内网";
        return "外网->外网";
    }

    // 判断是否为常见服务端端口
    private static boolean isCommonServerPort(Long port) {
        if (port == null) return false;

        Set<Long> commonServerPorts = Set.of(
                1433L,  // SQL Server
                1521L,  // Oracle
                3306L,  // MySQL
                5432L,  // PostgreSQL
                6379L,  // Redis
                27017L, // MongoDB
                8080L,  // HTTP备用
                8443L,  // HTTPS备用
                8000L,  // HTTP开发
                9000L,  // 通用服务
                3389L,  // RDP
                5060L,  // SIP
                1194L,  // OpenVPN
                4500L,  // IPSec
                500L    // IPSec
        );

        return commonServerPorts.contains(port);
    }

    // 判断是否为VPN服务端口
    private static boolean isVpnServerPort(Long port) {
        if (port == null) return false;

        Set<Long> vpnServerPorts = Set.of(
                1194L,  // OpenVPN默认端口
                1723L,  // PPTP
                4500L,  // IPSec NAT-T
                500L,   // IPSec IKE
                1701L,  // L2TP
                443L,   // SSL VPN
                993L,   // SSTP
                51820L  // WireGuard
        );

        return vpnServerPorts.contains(port);
    }

    // 判断是否为VPN客户端端口（高位临时端口）
    private static boolean isVpnClientPort(Long port) {
        if (port == null) return false;
        // VPN客户端通常使用临时端口
        return port >= 49152 && port <= 65535;
    }

    // 判断是否为私有IP
    private static boolean isPrivateIP(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }

        String actualIp = extractIPv4FromMapped(ip);

        try {
            String[] parts = actualIp.split("\\.");
            if (parts.length != 4) {
                return false;
            }

            int first = Integer.parseInt(parts[0]);
            int second = Integer.parseInt(parts[1]);

            // 10.0.0.0/8
            if (first == 10) return true;

            // 172.16.0.0/12
            if (first == 172 && second >= 16 && second <= 31) return true;

            // 192.168.0.0/16
            if (first == 192 && second == 168) return true;

            // 127.0.0.0/8
            if (first == 127) return true;

            return false;
        } catch (NumberFormatException e) {
            System.err.println("IP地址格式错误: " + ip + " -> " + actualIp);
            return false;
        }
    }
    private static String extractIPv4FromMapped(String ip) {
        if (ip == null || ip.isEmpty()) {
            return ip;
        }

        // 处理 ffff:x.x.x.x 格式
        if (ip.startsWith("ffff:")) {
            String ipv4Part = ip.substring(5); // 去掉 "ffff:" 前缀
            if (isValidIPv4Format(ipv4Part)) {
                return ipv4Part;
            }
        }

        // 处理标准IPv4-mapped IPv6格式 ::ffff:x.x.x.x
        if (ip.contains("::ffff:")) {
            int index = ip.indexOf("::ffff:");
            String ipv4Part = ip.substring(index + 7); // 去掉 "::ffff:" 前缀
            if (isValidIPv4Format(ipv4Part)) {
                return ipv4Part;
            }
        }

        // 处理 ::ffff:xxxx:xxxx 十六进制格式
        if (ip.contains("::ffff:") && !ip.contains(".")) {
            // 这种情况需要将十六进制转换为IPv4格式
            return convertHexToIPv4(ip);
        }

        // 如果不是特殊格式，直接返回原IP
        return ip;
    }

    // 验证IPv4格式是否有效
    private static boolean isValidIPv4Format(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }

        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }

        try {
            for (String part : parts) {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // 将十六进制格式的IPv4-mapped IPv6转换为标准IPv4
    private static String convertHexToIPv4(String ip) {
        try {
            // 处理 ::ffff:xxxx:xxxx 格式
            if (ip.contains("::ffff:")) {
                String hexPart = ip.substring(ip.indexOf("::ffff:") + 7);
                String[] hexGroups = hexPart.split(":");

                if (hexGroups.length == 2) {
                    // 解析两个十六进制组
                    int group1 = Integer.parseInt(hexGroups[0], 16);
                    int group2 = Integer.parseInt(hexGroups[1], 16);

                    // 转换为IPv4格式
                    int octet1 = (group1 >> 8) & 0xFF;
                    int octet2 = group1 & 0xFF;
                    int octet3 = (group2 >> 8) & 0xFF;
                    int octet4 = group2 & 0xFF;

                    return octet1 + "." + octet2 + "." + octet3 + "." + octet4;
                }
            }
        } catch (Exception e) {
            System.err.println("转换十六进制IPv4失败: " + ip);
        }

        return ip; // 转换失败时返回原值
    }

    }

//
//@Autowired
//private VpnMapper elasticRepositoryerride;
//public PageResult<VpnLogs>pullData() {
//
//            int maxRetries = 3;
//            int retryCount = 0;
//
//@Override
//    public PageResult<VpnLog> findByFiveTupleDataNotNullAndConnectTimeAfter(String date, Pageable pageable) throws ParseException {
//
//        //创建查询条件
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date formattedDate = simpleDateFormat.parse(date);
/// /        String format = simpleDateFormat.format(formattedDate);
//        log.info("查询时间：{}", formattedDate);
//        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder()
//                .withQuery(QueryBuilders.boolQuery()
//                        .must((QueryBuilders.existsQuery("fiveTupleData")))
//                        .must(QueryBuilders.rangeQuery("connectTime").gt(formattedDate.getTime()/1000))
//                )
//                .withPageable(pageable)
//                .withSort(Sort.by(Sort.Direction.DESC, "connectTime"));
//        log.info("查询条件：{}", searchQueryBuilder.build().getQuery());
////
//        SearchHits<VpnLog> searchHits = elasticsearchOperations.search(searchQueryBuilder.build(), VpnLog.class);
//        log.info("查询结果：{}", searchHits);
////            SearchHits<VpnLogs> searchHits = elasticsearchOperations.search(query, VpnLogs.class);
//
//        //提取结果
//        List<VpnLog> data = searchHits.getSearchHits().stream()
//                .map(SearchHit::getContent)
//                .collect(Collectors.toList());
//        log.info("查询结果：{}", data);
//
//        //计算分页结果
//
//
//        long total = searchHits.getTotalHits();
//        int pageSize = pageable.getPageSize();
//        int currentPage = pageable.getPageNumber() + 1;
//        int pages = (int) Math.ceil((double) total / pageSize);
//
//        PageResult<VpnLog> result = new PageResult<>();
//        result.setTotal(total);
//        result.setPages(pages);
//        result.setPageSize(pageSize);
//        result.setCurrentPage(currentPage);
//        result.setData(data);
//
//        return result;
//    }
//            while (retryCount < maxRetries) {
//                try {
//                    // 尝试执行查询
//                    return executeQuery();
//                } catch (ElasticsearchStatusException e) {
//                    retryCount++;
//                    log.warn("查询失败，正在进行第 {} 次重试", retryCount, e);
//
//                    if (retryCount >= maxRetries) {
//                        log.error("查询重试 {} 次后仍然失败", maxRetries, e);
//                        return new ArrayList<>(); // 返回空列表
//                    }
//
//                    // 等待一段时间再重试
//                    try {
//                        Thread.sleep(1000 * retryCount); // 递增等待时间
//                    } catch (InterruptedException ie) {
//                        Thread.currentThread().interrupt();
//                        return new ArrayList<>();
//                    }
//                }
//            }
//
//            return new ArrayList<>();
//        }
//    private List<VpnLogs>executeQuery() {
//        // 实际的查询逻辑
////        Pageable pageable = PageRequest.of(0, 5); // 第0页，每页5条
////        Page<VpnLogs> pageResult = elasticRepositoryerride.findAll(pageable);
////        return pageResult.getContent(); // 返回前5条记录
//        return elasticRepositoryerride.findAll();
//    }
//
//
//    }

//        List<VpnLogs> allResults = new ArrayList<>();
//        int page = 0;
//        int size = 10000; // Elasticsearch 默认最大窗口大小
//        boolean hasMoreData = true;
//
//        try {
//            while (hasMoreData) {
//                // 创建分页请求
//                Pageable pageable = PageRequest.of(page, size);
//
//                // 注意：这需要你的 VpnMapper 支持分页查询
//                // 如果不支持，你需要修改 VpnMapper 接口添加分页方法
//                List<VpnLogs> pageResults = elasticRepositoryerride.findAll(); // 临时使用，需要修改为分页查询
//
//                // 这里只是示例，实际需要修改 elasticRepositoryerride 的实现
//                if (pageResults.size() < size) {
//                    hasMoreData = false;
//                }
//
//                allResults.addAll(pageResults);
//                page++;
//
//                // 防止无限循环
//                if (page > 1000) break;
//            }
//
//            log.info("查询完成，总共获取到 {} 条记录", allResults.size());
//
//        } catch (Exception e) {
//            log.error("查询出错: ", e);
//        }
//
//        return allResults;
//    }
//

        //查询五元组数据不空的数据，返回一个新的map,只包含五元组信息
//        List<VpnLogs.FiveTupleData> fiveTupleData = null;
//        for (VpnLogs vpnLogs : results) {
//            if (vpnLogs.getFiveTupleData() != null) {
//                fiveTupleData = (List<VpnLogs.FiveTupleData>) vpnLogs;
//            }
//        }
//        //并判断源ip是服务端还是客户端，用flag变量0表示服务端，1表示客户端
//        int flag = 0;
//        for (VpnLogs.FiveTupleData fiveTupleData1 : fiveTupleData) {
//            if (fiveTupleData1.getSrc_ip().equals("192.168.1.1")) {
//                flag = 1;
//            }
//            if (fiveTupleData1.getDst_ip().equals("192.168.1.1")) {
//                flag = 0;
//            }
//            if (flag == 1) {
//            }
//            if (flag == 0) {
//            }
//        }







