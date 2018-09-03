package com.spiselector;

import com.spiselector.annotation.SpiScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;

public class SpiSelectorRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        String[] packages = new String[]{"com"};

        try {
            AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(SpiScan.class.getName(), true));
            packages = annoAttrs.getStringArray("value");
        } catch (Exception var7) {
            this.logger.warn("get the boot SpiScan error", var7);
        }

        if (packages != null) {
            Arrays.stream(packages).forEach((str) -> {
                this.logger.info("spi will scan package {}", str);
            });
        }

        this.logger.info("scan the spi interface auto proxy");
        SpiSelectorScanner scanner = new SpiSelectorScanner(registry);
        scanner.setResourceLoader(this.resourceLoader);

        try {
            scanner.registerFilters();
            scanner.doScan(packages);
        } catch (Exception var6) {
            this.logger.error("Could not determine auto-configuration package, spi interface scanning disabled.", var6);
        }

    }

}
