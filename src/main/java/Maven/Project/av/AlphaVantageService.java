package Maven.Project.av;

import Maven.Project.key.ApiKeyService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AlphaVantageService {
    private final ApiKeyService keys;
    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper om = new ObjectMapper();

    public AlphaVantageService(ApiKeyService keys) {
        this.keys = keys;
    }

    // 예: TIME_SERIES_INTRADAY 호출
    public String intraday(String symbol, String interval, String outputsize) {
        // 키 수보다 약간 넉넉하게 재시도
        for (int attempt = 0; attempt < 15; attempt++) {
            var rk = keys.reserveOne(); // 여기서 이미 +1 카운트됨
            try {
                String url = "https://www.alphavantage.co/query"
                        + "?function=TIME_SERIES_INTRADAY"
                        + "&symbol=" + symbol
                        + "&interval=" + interval
                        + (outputsize != null ? "&outputsize=" + outputsize : "")
                        + "&apikey=" + rk.apiKey();

                ResponseEntity<String> res = rest.getForEntity(url, String.class);
                String body = res.getBody() == null ? "" : res.getBody();

                // 레이트리밋/공지 메시지 감지 시 다음 키로 스위치
                if (res.getStatusCode().value() == 429 || isRateLimited(body)) {
                    keys.markExhaustedToday(rk.id());
                    continue; // 다음 키로 재시도
                }
                return body; // 정상 응답
            } catch (Exception e) {
                // 네트워크 오류 등 → 다음 키로 시도
            }
        }
        throw new IllegalStateException("Alpha Vantage 호출 실패(키 소진 또는 연속 오류)");
    }

    private boolean isRateLimited(String json) {
        try {
            JsonNode n = om.readTree(json);
            return n.has("Note") || n.has("Information"); // AV가 한도/지연 안내할 때 나오는 필드
        } catch (Exception ignore) {
            return false;
        }
    }
}
