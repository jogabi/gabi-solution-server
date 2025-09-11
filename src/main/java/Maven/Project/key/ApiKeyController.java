package Maven.Project.key;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@RestController
public class ApiKeyController {

    private final JdbcTemplate jdbcTemplate;

    public ApiKeyController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/keys/current")
    public Map<String, Object> getCurrentKey() {
        return jdbcTemplate.queryForMap("SELECT * FROM v_api_key LIMIT 1");
    }
}
