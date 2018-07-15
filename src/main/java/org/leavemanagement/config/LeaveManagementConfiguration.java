package org.leavemanagement.config;

import org.leavemanagement.status.LeaveProperty;
import org.leavemanagement.status.LeavePropertyImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Locale;

/**
 * Created by atul_saurabh on 19/11/17.
 */

@EnableAutoConfiguration
@Component
@ComponentScan
@PropertySource("classpath:leave.properties")
public class LeaveManagementConfiguration extends WebMvcConfigurerAdapter
{
    @Autowired
    private Environment environment;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        registry.addResourceHandler("/static/css/**").addResourceLocations("/static/css/");
        registry.addResourceHandler("/static/images/**").addResourceLocations("/static/images/");
        registry.addResourceHandler("/static/js/**").addResourceLocations("/static/js/");
    }

    @Bean
    public LocaleResolver localeResolver()
    {
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        sessionLocaleResolver.setDefaultLocale(Locale.US);
        return sessionLocaleResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor()
    {
      LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
      localeChangeInterceptor.setParamName("lang");
      return localeChangeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    /*@Bean
    @Primary
    @ConfigurationProperties(prefix = "datasource")
    public DataSource primaryDataSource()
    {
        return DataSourceBuilder.create().build();
    }*/

    @Bean
    public LeaveProperty leaveProperty()
    {
        LeaveProperty leaveProperty = new LeavePropertyImpl(environment);
        return leaveProperty;
    }

   @Bean
    public HibernateJpaSessionFactoryBean sessionFactory(EntityManagerFactory factory)
    {
        HibernateJpaSessionFactoryBean factoryBean = new HibernateJpaSessionFactoryBean();
        factoryBean.setEntityManagerFactory(factory);
        return factoryBean;
    }
}
