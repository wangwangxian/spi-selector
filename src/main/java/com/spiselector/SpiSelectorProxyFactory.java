package com.spiselector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SpiSelectorProxyFactory<T> implements FactoryBean<T>, InitializingBean, ApplicationContextAware {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Class<T> spiInterface;
    private ApplicationContext applicationContext;
    private Map<String, T> beans;

    SpiSelectorProxyFactory(Class<T> spiInterface) {
        this.spiInterface = spiInterface;
    }

    @Override
    public T getObject() {
        this.logger.info("SpiSelectorProxyFactory create the JDK Dynamic Proxy class ,{}", this.spiInterface.getName());
        return (T) (new SpiSelectorProxy(this.spiInterface, this.beans)).getSpiProxy();
    }

    @Override
    public Class<?> getObjectType() {
        return this.spiInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() {
        this.beans = new HashMap();
        this.applicationContext.getBeansOfType(this.spiInterface).values().forEach((bean) -> {
            SpiInterface spiInterface = this.assemblySpiInterface(bean);
            if (spiInterface != null) {
                Arrays.stream(spiInterface.code()).forEach((code) -> {
                    this.beans.put(code, bean);
                });
            }

        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private SpiInterface assemblySpiInterface(Object obj) {
        for (Class clazz = obj.getClass(); clazz != null && !clazz.equals(Object.class); clazz = clazz.getSuperclass()) {
            SpiInterface spiInterface = (SpiInterface) clazz.getAnnotation(SpiInterface.class);
            if (spiInterface != null) {
                return spiInterface;
            }
        }

        return null;
    }
}
