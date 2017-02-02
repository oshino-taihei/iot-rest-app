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
      def sentTime = value["sentTime"][0]
      def cpuTemp = value["payload"]["data"]["cpuTemp"][0]
      if (cpuTemp) {
        logger.info("post request: date=[${sentTime}],cpuTemp=[${cpuTemp}]")
        dataList.add([date:sentTime, value:cpuTemp])
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

  @RequestMapping(value = "/messages/dummy")
  public String dummy() {
    logger.info("CREATE: dummy messages.")
    dataList.add([date:Instant.now().toEpochMilli(), value:45])
    dataList.add([date:Instant.now().plusSeconds(5).toEpochMilli(), value:50])
    dataList.add([date:Instant.now().plusSeconds(10).toEpochMilli(), value:55])
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
