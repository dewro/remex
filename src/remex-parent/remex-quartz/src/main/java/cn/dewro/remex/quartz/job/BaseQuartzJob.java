package cn.dewro.remex.quartz.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * job基础类
 * 
 * @author Qiaoxin.Hong
 *
 */
public abstract class BaseQuartzJob implements Job {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * job业务实际处理
	 * @param context
	 */
	public abstract void handle(JobExecutionContext context);

	/**
	 * job业务处理
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		long curTime = System.currentTimeMillis();
		String className = getClass().getSimpleName();
		
		try {
			logger.info("[sid = {}] satrt quartz [job = {}] execute", curTime, className);
			
			//job业务实际处理
			handle(context);
			
		} catch (Exception e) {
			logger.error("quartz job - {} execute error!", className, e);
		} finally {
			logger.info("[sid = {}] end quartz [job = {}] execute [callTime = {}]", curTime, className
					, System.currentTimeMillis() - curTime);
		}
	}
}
