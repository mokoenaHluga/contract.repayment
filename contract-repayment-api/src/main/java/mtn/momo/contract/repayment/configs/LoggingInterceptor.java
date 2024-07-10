package mtn.momo.contract.repayment.configs;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    /**
     * This method is called before the actual handler is executed.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @param handler  chosen handler to execute, for type and/or instance evaluation
     * @return true if the execution chain should proceed with the next interceptor or the handler itself.
     *         Else, DispatcherServlet assumes that this interceptor has already dealt with the response itself.
     * @throws Exception in case of errors
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestId = request.getHeader("X-Request-Id");
        MDC.put("X-Request-Id", requestId != null ? requestId : "SYSTEM");
        return true;
    }

    /**
     * This method is called after the handler is executed, allowing for cleanup.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @param handler  the handler (or the last interceptor in the chain)
     * @param ex       exception thrown on handler execution, if any
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        MDC.clear();
    }
}
