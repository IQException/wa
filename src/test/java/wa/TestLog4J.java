package wa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestLog4J {
    private static Logger log = LoggerFactory.getLogger(TestLog4J.class);
    public static void main(String[] args) {
        log.debug("debug");
        log.info("info");
        log.warn("warn");
        log.error("error");
    }
}
