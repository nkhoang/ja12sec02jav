package com.nkhoang.gae.listener;

import com.nkhoang.gae.dao.AppConfigDao;
import com.nkhoang.gae.manager.UserManager;
import com.nkhoang.gae.model.AppConfig;
import com.nkhoang.gae.model.Role;
import com.nkhoang.gae.model.User;
import com.nkhoang.gae.service.impl.AppCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;

/**
 * <p/>
 * StartupListener class used to initialize and database settings and populate
 * any application-wide drop-downs.
 * <p/>
 * <p/>
 * Keep in mind that this listener is executed outside of
 * OpenSessionInViewFilter, so if you're using Hibernate you'll have to
 * explicitly initialize all loaded data at the GenericDao or service level to
 * avoid LazyInitializationException. Hibernate.initialize() works well for
 * doing this.
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class StartupListener implements ServletContextListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(StartupListener.class);
    // the context param compressMode in web.xml
    private static final String COMPRESS_MODE_CONFIG = "compressMode";
    // the variable name to get the appConfig.
    private static final String APP_CONFIG = "appConfig";

    @SuppressWarnings({"unchecked"})
    public void contextInitialized(ServletContextEvent event) {
        LOGGER.debug("Initializing context...");

        ServletContext context = event.getServletContext();

        Map<String, Object> config = (HashMap<String, Object>) context.getAttribute(APP_CONFIG);

        if (config == null) {
            config = new HashMap<String, Object>();
        }

        if (context.getInitParameter(COMPRESS_MODE_CONFIG) != null) {
            config.put(COMPRESS_MODE_CONFIG, context.getInitParameter(COMPRESS_MODE_CONFIG));
        }

        context.setAttribute(APP_CONFIG, config);

        setupContext(context);
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

    /**
     * This method uses the LookupManager to lookup available roles from the
     * data layer.
     *
     * @param context The servlet context
     */
    public static void setupContext(ServletContext context) {
        LOGGER.debug("Check default user ...");
        ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(context);

        UserManager userService = (UserManager) ctx.getBean("userManager");
        AppConfigDao appConfigDao = (AppConfigDao) ctx.getBean("appConfigDao");
        AppCache appCache = (AppCache) ctx.getBean("appCache");
        PasswordEncoder passwordEncoder = (PasswordEncoder) ctx.getBean("passwordEncoder");

        userService.clearAll();

        List<User> users = userService.listAll();
        if (users != null && users.size() > 0) {
            LOGGER.debug("Default users existed");
        } else {
            LOGGER.debug("Creating default users...");
            User admin = new User();
            admin.setEnabled(true);
            admin.setUsername("admin");
            admin.setFirstName("Hoang");
            admin.setLastName("Nguyen");
            admin.setMiddleName("Khanh");
            admin.setEmail("nkhoang.it@gmail.com");
            admin.setIssueDate(new Date());
            admin.setBirthDate(new Date());
            admin.setGender(User.CustomerGender.FEMALE.name());
            admin.setPersonalId("0238679999");
            admin.setPersonalIdType(User.PersonalIdType.VISA.name());
            admin.setIssuePlace("HCM");
            admin.setPhoneNumber("2342432423423");
            List<String> roles = new ArrayList<String>(0);
            roles.add(Role.UserRole.ROLE_ADMIN.name());
            roles.add(Role.UserRole.ROLE_USER.name());
            // set roles
            admin.setRoleNames(roles);
            admin.setPassword(passwordEncoder.encodePassword("admin", null));

            userService.save(admin);

            if (admin.getId() != null) {
                LOGGER.debug("Saving default users [ok]");
            }

            // save default dictionary
            if (appConfigDao.getAppConfigByLabel("dictionary") == null) {
                AppConfig appConfig = new AppConfig();
                appConfig.setLabel("dictionary");
                appConfig.setValue("vdict, oxford");
                appConfig = appConfigDao.save(appConfig);
                if (appConfig.getId() != null) {
                    LOGGER.debug("Save appConfig successfully.");
                    appCache.addProperty(appConfig.getLabel(), Arrays.asList(appConfig.getValue().split(",")));
                }
            }
        }
    }
}
