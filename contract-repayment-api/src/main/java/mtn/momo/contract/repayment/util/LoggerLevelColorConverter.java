package mtn.momo.contract.repayment.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.color.ANSIConstants;
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase;

public class LoggerLevelColorConverter extends ForegroundCompositeConverterBase<ILoggingEvent> {

    /**
     * Determines the appropriate foreground color based on the log level.
     * @param event The logging event which contains details like the log level.
     * @return The ANSI color code as a String.
     */
    @Override
    protected String getForegroundColorCode(ILoggingEvent event) {
        Level level = event.getLevel();
        switch (level.toInt()) {
            case Level.ERROR_INT -> { return ANSIConstants.RED_FG; }    // Red for ERROR messages
            case Level.WARN_INT -> { return ANSIConstants.YELLOW_FG; }  // Yellow for WARN messages
            case Level.INFO_INT -> { return ANSIConstants.GREEN_FG; }   // Green for INFO messages
            case Level.DEBUG_INT -> { return ANSIConstants.BLUE_FG; }   // Blue for DEBUG messages
            default -> { return ANSIConstants.DEFAULT_FG; }             // Default color for other levels
        }
    }
}
