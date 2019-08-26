package dev.onload.rocketmq;

import dev.onload.rocketmq.consumer.service.HandleService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.List;


/**
 * @author huang
 * @date 2019-07-28
 * @desc spi核心自动配置
 */
@Configuration
@ConditionalOnClass(HandleService.class)
public class MyAutoConfiguration implements
        EnvironmentAware, ApplicationContextAware, BeanDefinitionRegistryPostProcessor {

    private Environment environment;
    private ApplicationContext applicationContext;

    /**
     * 后置处理bean定义注册
     *
     * @param beanDefinitionRegistry
     * @throws BeansException
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        //从spring.factories加载HandleService的所有实现
        List<HandleService> helloServices = SpringFactoriesLoader.loadFactories(HandleService.class, this.getClass().getClassLoader());
        //然后用BeanDefinitionRegistry注册到BeanDefinitions
        helloServices.forEach(helloService -> {
            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
            beanDefinition.setBeanClass(helloService.getClass());
            beanDefinition.setLazyInit(false);
            beanDefinition.setAbstract(false);
            beanDefinition.setAutowireCandidate(true);
            beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
            beanDefinitionRegistry.registerBeanDefinition(helloService.getClass().getSimpleName(), beanDefinition);
        });

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
