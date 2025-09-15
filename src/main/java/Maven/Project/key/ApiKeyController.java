package Maven.Project.key;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.Map;


@RestController
public class ApiKeyController {
    private final JdbcTemplate jdbcTemplate;
    public ApiKeyController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/keys/use")
    @Transactional
    public Map<String, Object> useKey() {
        Map<String, Object> row = jdbcTemplate.queryForMap(
                "SELECT id, current_api_key, " +
                        "(30 - IF(last_reset = CURDATE(), used_today, 0)) AS remaining_today " +
                        "FROM v_api_key LIMIT 1"
        );

        Long id = ((Number) row.get("id")).longValue();
        String apiKey = (String) row.get("current_api_key");

        int updated = jdbcTemplate.update(
                "UPDATE api_keys SET used_today = used_today + 1 WHERE id = ?",
                id
        );
        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용 가능한 키 없음");
        }

        Integer used = jdbcTemplate.queryForObject(
                "SELECT used_today FROM api_keys WHERE id = ?", Integer.class, id
        );

        return Map.of(
                "api_key", apiKey,
                "used_today", used,
                "remaining_today", 30 - used
        );
    }





}
