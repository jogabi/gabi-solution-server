// src/main/java/com/example/demo/DbPingController.java
package Maven.Project;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.sql.DataSource;

@RestController
public class DbPingController {
    private final DataSource ds;
    public DbPingController(DataSource ds) { this.ds = ds; }

    @GetMapping("/db-ping")
    public String ping() throws Exception {
        try (var c = ds.getConnection();
             var st = c.createStatement();
             var rs = st.executeQuery("SELECT 1")) {
            rs.next();
            return "OK " + rs.getInt(1);
        }
    }
}
