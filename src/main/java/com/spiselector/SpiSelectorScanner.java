package com.spiselector;

import com.spiselector.annotation.SpiInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Arrays;
import java.util.Set;

public class SpiSelectorScanner extends ClassPathBeanDefinitionScanner {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    SpiSelectorScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        if (beanDefinitions.isEmpty()) {
            this.logger.warn("No spi Interface was found in {} package. Please check your configuration.", Arrays.toString(basePackages));
        } else {
            this.logger.info("find total {} spi interface", beanDefinitions.size());
            beanDefinitions.forEach(this::assemblyBeanDefinition);
        }

        return beanDefinitions;
    }

    private void assemblyBeanDefinition(BeanDefinitionHolder holder) {
        GenericBeanDefinition definition = (GenericBeanDefinition) GenericBeanDefinition.class.cast(holder.getBeanDefinition());
        String spiInterface = definition.getBeanClassName();
        this.logger.info("register the spi interface is {}", spiInterface);
        definition.setBeanClass(SpiSelectorProxyFactory.class);
        definition.getConstructorArgumentValues().addGenericArgumentValue(spiInterface);
        definition.setAutowireMode(2);
        definition.setPrimary(true);
        this.logger.info("register the spi interface {} success", spiInterface);
    }

    void registerFilters() {
        this.addIncludeFilter(new AnnotationTypeFilter(SpiInterface.class));
        this.addExcludeFilter((reader, factory) -> {
            return reader.getClassMetadata().getClassName().endsWith("package-info");
        });
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }
}
