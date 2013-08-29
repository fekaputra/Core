package cz.cuni.xrg.intlib.frontend;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.Notification;
import cz.cuni.xrg.intlib.commons.app.auth.AuthenticationContextService;
import cz.cuni.xrg.intlib.commons.app.communication.Client;
import cz.cuni.xrg.intlib.commons.app.conf.AppConfig;
import cz.cuni.xrg.intlib.commons.app.conf.ConfigProperty;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUFacade;
import cz.cuni.xrg.intlib.commons.app.execution.log.LogFacade;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineFacade;
import cz.cuni.xrg.intlib.commons.app.scheduling.ScheduleFacade;
import cz.cuni.xrg.intlib.commons.app.user.UserFacade;
import cz.cuni.xrg.intlib.frontend.auxiliaries.IntlibHelper;
import cz.cuni.xrg.intlib.frontend.auxiliaries.IntlibNavigator;
import cz.cuni.xrg.intlib.frontend.gui.MenuLayout;
import cz.cuni.xrg.intlib.frontend.gui.ViewNames;
import cz.cuni.xrg.intlib.frontend.gui.views.*;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Frontend application entry point. Also provide access to the application
 * services like database connection. To access the class use
 * ((AppEntry)UI.getCurrent()).
 *
 * @author Petyr
 *
 */
@Push(PushMode.AUTOMATIC)
@Theme("IntLibTheme")
public class AppEntry extends com.vaadin.ui.UI {

	private static final Logger LOG = LoggerFactory.getLogger(AppEntry.class);
	
	/**
	 * Used to resolve url request and select active view.
	 */
	private com.vaadin.navigator.Navigator navigator;
	
	/**
	 * Spring application context.
	 */
	private ApplicationContext context;
	
	private MenuLayout main;
	
	private Date lastAction = null;
	
	private Thread backendStatusThread;
	
	private Client backendClient;
		
	/**
	 * Add a single view to {@link #navigator}.
	 *
	 * @param view Name of the view.
	 */
	private void initNavigatorAddSingle(ViewNames view) {
		this.navigator.addView(view.getUrl(), ViewsFactory.create(view));
	}
	
	/**
	 * Add url-view association into navigator.
	 */
	private void initNavigator() {
		initNavigatorAddSingle(ViewNames.INITIAL);
		// TODO: check rights !!
		initNavigatorAddSingle(ViewNames.ADMINISTRATOR);
		initNavigatorAddSingle(ViewNames.DATA_BROWSER);
		initNavigatorAddSingle(ViewNames.DPU);
		initNavigatorAddSingle(ViewNames.EXECUTION_MONITOR);
		initNavigatorAddSingle(ViewNames.PIPELINE_LIST);
		initNavigatorAddSingle(ViewNames.PIPELINE_EDIT);
		initNavigatorAddSingle(ViewNames.SCHEDULER);
		initNavigatorAddSingle(ViewNames.LOGIN);

		/* You can create new views dynamically using a view provider
		 * that implements the  ViewProvider interface.
		 * A provider is registered in Navigator with  addProvider().
		 */

		/* View Change Listeners
		 * You can handle view changes also by implementing a  ViewChangeListener
		 * and adding it to a Navigator. When a view change occurs, a listener receives
		 * a ViewChangeEvent object, which has references to the old and the activated view,
		 * the name of the activated view, as well as the fragment parameters.
		 */
	}

	@Override
	protected void init(com.vaadin.server.VaadinRequest request) {

		// create Spring context, always the first after application init,
		// so that all needed beans are ready.
		context = new ClassPathXmlApplicationContext(
				"frontend-context.xml",
				"commons-app-context-security.xml");

		// create main application uber-view and set it as app. content
		// in panel, for possible vertical scrolling
		main = new MenuLayout();
		//Panel mainPanel = new Panel();
		//mainPanel.setContent(main);
		setContent(main);

		// create a navigator to control the views
		this.navigator = new IntlibNavigator(this, main.getViewLayout());

		// add vaadin to export package list
		ModuleFacade modules = (ModuleFacade) context.getBean("moduleFacade");
		modules.start();

		this.addDetachListener(new DetachListener() {
			@Override
			public void detach(DetachEvent event) {
				getModules().stop();
				if (backendStatusThread != null) {
					backendStatusThread.interrupt();
				}
				if (backendClient != null) {
					backendClient.close();
				}

			}
		});

		initNavigator();

		// Configure the error handler for the UI
		this.setErrorHandler(new DefaultErrorHandler() {
			@Override
			public void error(com.vaadin.server.ErrorEvent event) {
				Throwable cause = IntlibHelper.findFinalCause(event.getThrowable());
				if (cause != null) {
					// Display the error message in a custom fashion
					String text = String.format("Exception: %s, Message: %s, Source: %s", cause.getClass().getName(), cause.getMessage(), cause.getStackTrace().length > 0 ? cause.getStackTrace()[0].toString() : "unknown");
					Notification.show("Uncaught exception appeared in system!", text, Notification.Type.ERROR_MESSAGE);
					// and log ...
					LOG.error("Uncaught exception", cause);
				} else {
					// Do the default error handling (optional)
					doDefault(event);
				}
			}
		});
		
		/**
		 * Checking user every time request is made.
		 */
		this.getNavigator().addViewChangeListener(new ViewChangeListener() {
			@Override
			public boolean beforeViewChange(ViewChangeListener.ViewChangeEvent event) {
				if (!event.getViewName().equals(ViewNames.LOGIN.getUrl()) && !checkAuthentication()) {
					getNavigator().navigateTo(ViewNames.LOGIN.getUrl());
					getMain().refreshUserBar();
					return false;
				}
				setActive();
				return true;
			}

			@Override
			public void afterViewChange(ViewChangeListener.ViewChangeEvent event) {
			}
		});

		AppConfig config = getAppConfiguration();
		backendClient = new Client(
				config.getString(ConfigProperty.BACKEND_HOST),
				config.getInteger(ConfigProperty.BACKEND_PORT));

	}

	/**
	 * Checks if there is logged in user and if its session is still valid.
	 *
	 * @return true if user and its session are valid, false otherwise
	 */
	private boolean checkAuthentication() {
		return getAuthCtx().isAuthenticated();
	}

	/**
	 * Returns facade, which provides services for managing pipelines.
	 *
	 * @return pipeline facade
	 */
	public PipelineFacade getPipelines() {
		return (PipelineFacade) context.getBean("pipelineFacade");
	}

	/**
	 * Return application navigator.
	 *
	 * @return application navigator
	 */
	@Override
	public Navigator getNavigator() {
		return this.navigator;
	}

	/**
	 * Return facade, which provide services for manipulating with modules.
	 *
	 * @return modules facade
	 */
	public ModuleFacade getModules() {
		return (ModuleFacade) context.getBean("moduleFacade");
	}

	/**
	 * Return facade, which provide services for manipulating with DPUs.
	 *
	 * @return dpus facade
	 */
	public DPUFacade getDPUs() {
		return (DPUFacade) context.getBean("dpuFacade");
	}

	/**
	 * Return facade, which provide services for manipulating with Schedules.
	 *
	 * @return schedules facade
	 */
	public ScheduleFacade getSchedules() {
		return (ScheduleFacade) context.getBean("scheduleFacade");
	}

	/**
	 * Return facade, which provide services for manipulating with Schedules.
	 *
	 * @return schedules facade
	 */
	public UserFacade getUsers() {
		return (UserFacade) context.getBean("userFacade");
	}

	/**
	 * Return facade, which provide services for manipulating with Logs.
	 *
	 * @return log facade
	 */
	public LogFacade getLogs() {
		return (LogFacade) context.getBean("logFacade");
	}

	/**
	 * Return application configuration class.
	 *
	 * @return
	 */
	public AppConfig getAppConfiguration() {
		return (AppConfig) context.getBean("configuration");
	}

	/**
	 * Fetches spring bean.
	 *
	 * @param name
	 * @return bean
	 * @deprecated use {@link #getBean(java.lang.Class) instead
	 */
	@Deprecated
	public Object getBean(String name) {
		return context.getBean(name);
	}

	/**
	 * Fetches spring bean.
	 *
	 * @param type
	 * @return bean
	 */
	public <T extends Object> T getBean(Class<T> type) {
		return context.getBean(type);
	}

	public MenuLayout getMain() {
		return main;
	}

	/**
	 * Helper method for retrieving authentication context.
	 *
	 * @return authentication context for current user session
	 */
	public AuthenticationContextService getAuthCtx() {
		return getBean(AuthenticationContextService.class);
	}

	/**
	 * Sets last action date to current time.
	 */
	public void setActive() {
		lastAction = new Date();
	}

	/**
	 * Gets time of last action.
	 *
	 * @return Time of last action.
	 */
	public Date getLastAction() {
		return lastAction;
	}

	public void setupBackendStatusChecking() {
		main.refreshBackendStatus(false);
		if (backendStatusThread == null) {
			backendStatusThread = new Thread() {
				private boolean isRunning = false;

				@Override
				public void run() {
					boolean lastStatus = false;
					//boolean isRunning = false;
					while (true) {
						isRunning = getBackendClient().checkStatus();
						if (lastStatus != isRunning) {
							lastStatus = isRunning;
							main.getUI().access(new Runnable() {
								@Override
								public void run() {
									main.refreshBackendStatus(isRunning);
								}
							});
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							break;
						}
					}
				}
			};
			backendStatusThread.start();
		}
	}

	public Client getBackendClient() {
		return backendClient;
	}
}
