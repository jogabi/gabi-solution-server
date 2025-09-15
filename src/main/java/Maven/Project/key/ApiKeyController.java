package Maven.Project.key;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.Map;
import org.springframework.dao.EmptyResultDataAccessException;


@RestController
public class ApiKeyController {
    private final JdbcTemplate jdbcTemplate;

    public ApiKeyController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @PostMapping("/keys/use")
    @Transactional
    public Map<String, Object> useKey() {
        try {
            Map<String, Object> row = jdbcTemplate.queryForMap(
                    "SELECT id, current_api_key, remaining_today " +
                            "FROM gabiSolution.v_api_key LIMIT 1"
            );

            Long id = ((Number) row.get("id")).longValue();
            String apiKey = (String) row.get("current_api_key");

            int updated = jdbcTemplate.update(
                    "UPDATE gabiSolution.api_keys SET used_today = used_today + 1 WHERE id = ?",
                    id
            );
            if (updated == 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "업데이트 실패");
            }

            Integer used = jdbcTemplate.queryForObject(
                    "SELECT used_today FROM gabiSolution.api_keys WHERE id = ?",
                    Integer.class, id
            );

            int remaining = 30 - (used != null ? used : 0);

            return Map.of(
                    "api_key", apiKey,
                    "used_today", used,
                    "remaining_today", remaining
            );
        } catch (EmptyResultDataAccessException e) {
            // v_api_key 결과 없음 → 이미 30회 다 사용
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS, "오늘 사용할 수 있는 API 키가 모두 소진되었습니다. (30/30)"
            );
        }
    }





}




