package cn.dewro.remex.quartz.config;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.quartz.JobDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import cn.dewro.remex.exception.RemexException;

public class BaseQuartzSchedulerConfig {
protected Logger logger = LoggerFactory.getLogger(getClass());
	
	/** 默认的quartz配置项属性文件路径  */
	public final static String DEFAULT_QUARTZ_PROPERTIES_URL = "/quartz.properties";

	
	/**
	 * 创建任务调度类
	 * @param dataSource 数据源
	 * @return
	 */
	protected SchedulerFactoryBean buildScheduler(DataSource dataSource) {
		try {
			Properties quartzProperties = getQuartzProperties();
			
			SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
			//任务调度配置项
			factoryBean.setQuartzProperties(quartzProperties); 
			//数据源
			factoryBean.setDataSource(dataSource); 
			
			return factoryBean;
		} catch (Exception e) {
			throw new RemexException("quartz build scheduler error!", e);
		}
	}
	
	/**
	 * 创建任务调度明细
	 */
	protected JobDetailFactoryBean buildJobDetail(Class<?> jobClass) {
		JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
		factoryBean.setJobClass(jobClass);
		//任务完成之后是否依然保留到数据库
		factoryBean.setDurability(true);
		//可恢复属性，由正常节点恢复
		factoryBean.setRequestsRecovery(false);
		
		return factoryBean;
	}
	
	/**
	 * 创建任务调度触发器
	 * @throws ParseException 
	 */
	protected CronTriggerFactoryBean buildTrigger(JobDetail jobDetail) {
		CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
		factoryBean.setJobDetail(jobDetail);

		return factoryBean;
	}
	
	/**
	 * 任务调度配置项
	 * @return
	 * @throws IOException
	 */
	protected Properties getQuartzProperties() throws IOException {  
		PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
		propertiesFactoryBean.setLocation(new ClassPathResource(getQuartzPropertiesUrl()));
		propertiesFactoryBean.afterPropertiesSet();
		return propertiesFactoryBean.getObject(); 
	} 
	
	/**
	 * 取得quartz配置项属性文件路径
	 * @return
	 */
	protected String getQuartzPropertiesUrl() {
		return DEFAULT_QUARTZ_PROPERTIES_URL;
	}
}
