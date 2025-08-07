package identify.mapper;

import identify.pojo.VpnLogs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface VpnMapper extends ElasticsearchRepository<VpnLogs.VpnLog, String> {
}
