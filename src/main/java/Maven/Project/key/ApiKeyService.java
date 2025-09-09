package Maven.Project.key;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class ApiKeyService {
    private final JdbcTemplate jdbc;

    public ApiKeyService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** 사용 가능한 키 1개를 '예약(+1)'해서 돌려준다. 없으면 예외 */
    @Transactional
    public ReservedKey reserveOne() {
        // 날짜 바뀌면 오늘 카운터 리셋
        jdbc.update("UPDATE api_keys SET used_today=0, last_reset=? WHERE last_reset<>?", LocalDate.now(), LocalDate.now());

        // 아직 30 미만인 키 하나를 잠금 SELECT (라운드로빈: id 오름차순)
        Long id = jdbc.query(con -> con.prepareStatement(
                "SELECT id FROM api_keys " +
                        "WHERE used_today < 30 " +
                        "ORDER BY id ASC " +
                        "LIMIT 1 FOR UPDATE"
        ), rs -> rs.next() ? rs.getLong(1) : null);

        if (id == null) {
            throw new IllegalStateException("오늘 사용할 수 있는 API 키가 없습니다.");
        }

        // 카운트 +1
        jdbc.update("UPDATE api_keys SET used_today = used_today + 1 WHERE id = ?", id);

        // 실제 키 값 반환
        String key = jdbc.queryForObject("SELECT api_key FROM api_keys WHERE id = ?", String.class, id);
        return new ReservedKey(id, key);
    }

    /** 공급자 레이트리밋 등에 걸렸을 때, 해당 키는 오늘 못 쓰게 바로 30으로 채운다(선택) */
    public void markExhaustedToday(long id) {
        jdbc.update("UPDATE api_keys SET used_today = 30 WHERE id = ?", id);
    }

    public record ReservedKey(long id, String apiKey) {}
}
