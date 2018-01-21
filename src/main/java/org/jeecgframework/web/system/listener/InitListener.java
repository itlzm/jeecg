package org.jeecgframework.web.system.listener;

import javax.servlet.ServletContextEvent;

import com.oracle.tools.packager.Log;
import org.jeecgframework.web.system.service.DynamicDataSourceServiceI;
import org.jeecgframework.web.system.service.MutiLangServiceI;
import org.jeecgframework.web.system.service.SystemService;
import org.quartz.Scheduler;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


/**
 * 系统初始化监听器,在系统启动时运行,进行一些初始化工作
 * @author laien
 *
 */
public class InitListener  implements javax.servlet.ServletContextListener {

	
	public void contextDestroyed(ServletContextEvent event) {
		try{
			Scheduler startQuartz = (Scheduler) WebApplicationContextUtils
					.getWebApplicationContext(event.getServletContext())
					.getBean("schedulerFactory");

			if(startQuartz != null){
				startQuartz.shutdown(true);
			}

			Thread.sleep(1000);//主线程睡眠1s
		}catch (Exception e){
			e.printStackTrace();
		}
		event.getServletContext().log("QuartzContextListener销毁成功！");
		System.out.println("QuartzContextListener销毁成功！");

	}

	
	public void contextInitialized(ServletContextEvent event) {
		WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
		SystemService systemService = (SystemService) webApplicationContext.getBean("systemService");
//		MenuInitService menuInitService = (MenuInitService) webApplicationContext.getBean("menuInitService");
		MutiLangServiceI mutiLangService = (MutiLangServiceI) webApplicationContext.getBean("mutiLangService");
		DynamicDataSourceServiceI dynamicDataSourceService = (DynamicDataSourceServiceI) webApplicationContext.getBean("dynamicDataSourceService");
		
		/**
		 * 第一部分：对数据字典进行缓存
		 */
		systemService.initAllTypeGroups();
		systemService.initAllTSIcons();
		
		
//		/**
//		 * 第二部分：自动加载新增菜单和菜单操作权限
//		 * 说明：只会添加，不会删除（添加在代码层配置，但是在数据库层未配置的）
//		 */
//		if("true".equals(ResourceUtil.getConfigByName("auto.scan.menu.flag").toLowerCase())){
//			menuInitService.initMenu();
//		}
		
		/**
		 * 第三部分：加载多语言内容
		 */
		mutiLangService.initAllMutiLang();
		
		/**
		 * 第四部分：加载配置的数据源信息
		 */
		dynamicDataSourceService.initDynamicDataSource();
	}

}
