package org.springframework.example;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;


/**
 *
 */
public class CustomBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	public String dbFileName = "db.properties";

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// 场景一：修改已存在的 BeanDefinition
		try {
			// 获取指定 Bean 的定义
			BeanDefinition beanDefinition = beanFactory.getBeanDefinition("student");

			// 修改 BeanDefinition 属性信息
			MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();
			propertyValues.addPropertyValue("name", "薛伟");

			// 修改作用域
			beanDefinition.setScope(BeanDefinition.SCOPE_PROTOTYPE);

			// 快速初始化 Bean
			Object student = beanFactory.getBean("student");
			System.out.println(student);

		} catch (Exception e) {
			System.err.println("修改 Student 的 BeanDefinition 属性失败！" + e);
		}

		// 场景二：动态注册 BeanDefinition（Spring 与 MyBatis 整合就是使用的这种）
		// 这里使用了一个强转,动态注册bean，可以实现另一个接口
		DefaultListableBeanFactory factory = (DefaultListableBeanFactory) beanFactory;
		GenericBeanDefinition definition = new GenericBeanDefinition();
		definition.setBeanClass(Teacher.class);
		definition.getPropertyValues().addPropertyValue("name", "XUEW");
		factory.registerBeanDefinition("teacher", definition);

		// 场景三：加载外部配置文件
//		try {
//			URL resource = ClassUtils.getDefaultClassLoader().getResource(dbFileName);
//			File file = ResourceUtils.getFile(resource);
//			if (file.exists()) {
//				Properties props = PropertiesLoaderUtils.loadAllProperties(dbFileName);
//				for (String key : props.stringPropertyNames()) {
//					System.setProperty(key, props.getProperty(key));
//				}
//			}
//		} catch (IOException e) {
//			throw new RuntimeException("Failed to load properties file", e);
//		}

		// 场景四：条件性修改 Bean 定义
		String os = System.getProperty("os.name");
		if ("Mac OS X".equals(os)) {
			BeanDefinition osInfo = beanFactory.getBeanDefinition("osInfo");
			MutablePropertyValues infoPropertyValues = osInfo.getPropertyValues();
			infoPropertyValues.addPropertyValue("name", os);
			infoPropertyValues.addPropertyValue("desc", "苹果系统");
		}

		// 场景五：快速初始化 Bean
		System.out.println(beanFactory.getBean("student"));
		System.out.println(beanFactory.getBean("teacher"));


		// 设置当前环境
		System.setProperty("environment.active", "prod");
	}

}
