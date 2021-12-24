package hello.proxy.advisor;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import java.lang.reflect.Method;

public class AdvisorTest {

    @Test
    void advisorTest() {
        final ServiceInterface target = new ServiceImpl();
        final ProxyFactory proxyFactory = new ProxyFactory(target);
        final DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(Pointcut.TRUE, new TimeAdvice());

        proxyFactory.addAdvisor(advisor);
        final ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        proxy.save();
        proxy.find();
    }

    @Test
    void 직접_만든_포인트컷이_사용되는_어드바이저() {
        final ServiceInterface target = new ServiceImpl();
        final ProxyFactory proxyFactory = new ProxyFactory(target);
        final DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(new MyPointCut(), new TimeAdvice());

        proxyFactory.addAdvisor(advisor);
        final ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        proxy.save();
        proxy.find();
    }

    static class MyPointCut implements Pointcut {

        @Override
        public ClassFilter getClassFilter() {
            return ClassFilter.TRUE;
        }

        @Override
        public MethodMatcher getMethodMatcher() {
            return new MyMethodMatcher();
        }
    }

    @Slf4j
    static class MyMethodMatcher implements MethodMatcher {


        public static final String MATCH_NAME = "save";

        @Override
        public boolean matches(final Method method, final Class<?> targetClass) {
            final boolean result = method.getName().equals(MATCH_NAME);
            log.info("포인트 컷 호출 method={}, targetClass={}", method.getName(), targetClass);
            log.info("포인트 컷 결과 result={}", result);
            return result;
        }

        @Override
        public boolean isRuntime() {
            return false;
        }

        @Override
        public boolean matches(final Method method, final Class<?> targetClass, final Object... args) {
            return false;
        }
    }

    @Test
    void 스프링제공_포인트컷이_사용되는_어드바이저() {
        final ServiceInterface target = new ServiceImpl();
        final ProxyFactory proxyFactory = new ProxyFactory(target);
        final NameMatchMethodPointcut nameMatchMethodPointcut = new NameMatchMethodPointcut();
        nameMatchMethodPointcut.setMappedName("save");
        final DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(nameMatchMethodPointcut, new TimeAdvice());

        proxyFactory.addAdvisor(advisor);
        final ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        proxy.save();
        proxy.find();
    }
}
