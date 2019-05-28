package com.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.service.MyJobService;

public class InitServlet implements ServletContextListener
{
    
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(InitServlet.class);

    private ApplicationContext app;
    
    
    private MyJobService myJob;

    public void init()
    {
        logger.info("System initializing start");
        myJob.init();
        logger.info("order job monitor init end.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void contextInitialized(ServletContextEvent event)
    {
        // TODO Auto-generated method stub
        app = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext()); // 获取spring上下文！
        myJob = (MyJobService)app.getBean("myJob");
        init();

    }

}
