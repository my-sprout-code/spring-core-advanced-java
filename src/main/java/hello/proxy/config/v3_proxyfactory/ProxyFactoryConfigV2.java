package hello.proxy.config.v3_proxyfactory;

import hello.proxy.app.v2.OrderControllerV2;
import hello.proxy.app.v2.OrderRepositoryV2;
import hello.proxy.app.v2.OrderServiceV2;
import hello.proxy.config.v3_proxyfactory.advice.LogTraceAdvice;
import hello.proxy.trace.logtrace.LogTrace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ProxyFactoryConfigV2 {

    @Bean
    public OrderControllerV2 orderControllerV2(final LogTrace logTrace) {
        final OrderControllerV2 orderControllerV2 = new OrderControllerV2(orderServiceV2(logTrace));
        final ProxyFactory proxyFactory = new ProxyFactory(orderControllerV2);
        proxyFactory.addAdvisor(getAdvisor(logTrace));

        final OrderControllerV2 proxy = (OrderControllerV2) proxyFactory.getProxy();
        log.info("ProxyFactory proxy={}, target={}", proxy.getClass(), orderControllerV2.getClass());
        return proxy;
    }

    @Bean
    public OrderServiceV2 orderServiceV2(final LogTrace logTrace) {
        final OrderServiceV2 orderServiceV2 = new OrderServiceV2(orderRepositoryV2(logTrace));
        final ProxyFactory proxyFactory = new ProxyFactory(orderServiceV2);
        proxyFactory.addAdvisor(getAdvisor(logTrace));

        final OrderServiceV2 proxy = (OrderServiceV2) proxyFactory.getProxy();
        log.info("ProxyFactory proxy={}, target={}", proxy.getClass(), orderServiceV2.getClass());
        return proxy;
    }

    @Bean
    public OrderRepositoryV2 orderRepositoryV2(final LogTrace logTrace) {
        final OrderRepositoryV2 orderRepositoryV2 = new OrderRepositoryV2();
        final ProxyFactory proxyFactory = new ProxyFactory(orderRepositoryV2);
        proxyFactory.addAdvisor(getAdvisor(logTrace));

        final OrderRepositoryV2 proxy = (OrderRepositoryV2) proxyFactory.getProxy();
        log.info("ProxyFactory proxy={}, target={}", proxy.getClass(), orderRepositoryV2.getClass());
        return proxy;
    }

    private Advisor getAdvisor(final LogTrace logTrace) {
        final NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedNames("request*", "order*", "save*");
        final LogTraceAdvice logTraceAdvice = new LogTraceAdvice(logTrace);
        return new DefaultPointcutAdvisor(pointcut, logTraceAdvice);
    }
}
