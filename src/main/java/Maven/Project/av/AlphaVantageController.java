package Maven.Project.av;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/av")
public class AlphaVantageController {
    private final AlphaVantageService av;

    public AlphaVantageController(AlphaVantageService av) {
        this.av = av;
    }

    // 예: GET /api/av/intraday?symbol=IBM&interval=5min&outputsize=compact
    @GetMapping("/intraday")
    public ResponseEntity<String> intraday(
            @RequestParam String symbol,
            @RequestParam String interval,
            @RequestParam(required = false) String outputsize
    ) {
        return ResponseEntity.ok(av.intraday(symbol, interval, outputsize));
    }
}
