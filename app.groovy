import org.slf4j.Logger
import org.slf4j.LoggerFactory

@RestController
class RestApp {
    @RequestMapping("/")
    String home() {
      "hello,world"
    }
}
