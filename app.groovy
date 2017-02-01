@Grab('groovy-all')
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.CopyOnWriteArrayList
import java.time.*
import groovy.json.*

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
  public String create(@RequestBody String payload) {
    try{
      def value = new JsonSlurper().parseText(payload)
      def cpuTemp = value["payload"]["data"]["cpuTemp"][0]
      if (cpuTemp) {
        logger.info("post request: cpuTemp=[${cpuTemp}]")
        dataList.add([date:LocalDateTime.now(), value:cpuTemp, payload:payload])
      } else {
        logger.info("invalid post request: ${payload}")
      }
    } catch(Exception e) {
      logger.error(e)
    }
  }
  @RequestMapping(value = "/messages/clear")
  public String clear() {
    logger.info("CLEAR: clear messages.")
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
