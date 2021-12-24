package hello.proxy.proxyfactory;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ConcreteService;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
public class ProxyFactoryTest {

    @Test
    void 인터페이스가_있으면_JDK_동적_프록시_적용() {
        final ServiceInterface target = new ServiceImpl();
        final ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addAdvice(new TimeAdvice());
        final ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        proxy.save();

        assertAll(
                () -> assertThat(AopUtils.isAopProxy(proxy)).isTrue(),
                () -> assertThat(AopUtils.isJdkDynamicProxy(proxy)).isTrue(),
                () -> assertThat(AopUtils.isCglibProxy(proxy)).isFalse()
        );
    }

    @Test
    void 구체_클래스만_있다면_CGLIB_사용() {
        final ConcreteService target = new ConcreteService();
        final ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addAdvice(new TimeAdvice());
        final ConcreteService proxy = (ConcreteService)proxyFactory.getProxy();
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        proxy.call();

        assertAll(
                () -> assertThat(AopUtils.isAopProxy(proxy)).isTrue(),
                () -> assertThat(AopUtils.isJdkDynamicProxy(proxy)).isFalse(),
                () -> assertThat(AopUtils.isCglibProxy(proxy)).isTrue()
        );
    }

    @Test
    void ProxyTargetClass_옵션을_사용하면_인터페이스가_있어도_CGLIB을_사용한다() {
        final ServiceInterface target = new ServiceImpl();
        final ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.addAdvice(new TimeAdvice());
        final ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        proxy.save();

        assertAll(
                () -> assertThat(AopUtils.isAopProxy(proxy)).isTrue(),
                () -> assertThat(AopUtils.isJdkDynamicProxy(proxy)).isFalse(),
                () -> assertThat(AopUtils.isCglibProxy(proxy)).isTrue()
        );
    }
}
