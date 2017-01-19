import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.CopyOnWriteArrayList
import java.time.*

@Controller
class RestApp {
  private static final Logger logger = LoggerFactory.getLogger(RestApp.class)
  private static List<Map> dataList = new CopyOnWriteArrayList<>()

  @RequestMapping(value = "/")
  public String home() {
    "redirect:/messages"
  }
  @RequestMapping(value = "/messages", method = RequestMethod.GET)
  @ResponseBody
  public String index() {
    render('index')
  }
  @RequestMapping(value = "/messages", method = RequestMethod.POST)
  @ResponseBody
  public String create(@RequestParam(required = false) String value) {
    if (value != null) {
      dataList.add([date:LocalDateTime.now(), value:value])
    }
  }
  @RequestMapping(value = "/messages/clear")
  public String clear() {
    dataList.clear()
    "redirect:/messages"
  }

  private String render(String templateName) {
    def f = new File("view/${templateName}.template")
    def engine = new groovy.text.SimpleTemplateEngine()
    def binding = ['dataList': dataList]
    def template = engine.createTemplate(f).make(binding)
    template.toString()
  }
}
