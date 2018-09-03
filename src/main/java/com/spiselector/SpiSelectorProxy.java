package com.spiselector;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public class SpiSelectorProxy<T> implements InvocationHandler {

    private Class<T> spiInterface;
    private Map<String, T> beans;

    SpiSelectorProxy(Class<T> spiInterface, Map<String, T> beans) {
        this.beans = beans;
        this.spiInterface = spiInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return Object.class.equals(method.getDeclaringClass()) ? method.invoke(this, args) : method.invoke(this.beans.getOrDefault(SpiApplicationContext.getSpiCode(), null), args);
    }

    T getSpiProxy() {
        return this.spiInterface.cast(Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{this.spiInterface}, this));
    }
}
