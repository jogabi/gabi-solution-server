package Maven.Project.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class ApiKeyTestController {

    private final JdbcTemplate jdbcTemplate;

    public ApiKeyTestController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/keys")
    public List<Map<String, Object>> getAllKeys() {
        return jdbcTemplate.queryForList("SELECT * FROM api_keys");
    }
}
