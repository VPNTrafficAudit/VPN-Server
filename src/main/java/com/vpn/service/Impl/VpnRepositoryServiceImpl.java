package com.vpn.service.Impl;

import com.vpn.entity.PO.VpnLog;
import com.vpn.entity.VO.PageResult;
import com.vpn.service.VpnRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VpnRepositoryServiceImpl implements VpnRepositoryService {
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Override
    public PageResult<VpnLog> getAllVpnLog(Pageable pageable) {
        Criteria criteria = new Criteria();
        CriteriaQuery query = new CriteriaQuery(criteria);
        query.setPageable(pageable);
        SearchHits<VpnLog> searchHits = elasticsearchOperations.search(query, VpnLog.class);
        List<VpnLog> data = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .toList();

        long total = searchHits.getTotalHits();
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber() + 1;
        int pages = (int) Math.ceil((double) total / pageSize);

        PageResult<VpnLog> result = new PageResult<>();
        result.setTotal(total);
        result.setPages(pages);
        result.setPageSize(pageSize);
        result.setCurrentPage(currentPage);
        result.setData(data);

        return result;

    }
}
