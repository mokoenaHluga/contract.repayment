package mtn.momo.contract.repayment.util;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.slf4j.MDC;

public class LoggerUtil extends ClassicConverter {
    @Override
    public String convert(ILoggingEvent event) {
        return "[" + MDC.getCopyOfContextMap().getOrDefault("X-Request-Id", "SYSTEM") + "]";
    }
}
