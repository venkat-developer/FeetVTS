package com.i10n.fleet.util;

import java.util.logging.Level;

import org.apache.log4j.Logger;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.HTMLActionHandler;
import org.eclipse.birt.report.engine.api.HTMLEmitterConfig;
import org.eclipse.birt.report.engine.api.HTMLServerImageHandler;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;

/**
 * This Class defines the BIRT Engine its properties and initializations It also
 * provides the functionalities to createTask,shutdownTask exportReport and
 * Shutting Down the BIRT and Report Engine
 * 
 * @author Vignesh
 */
@SuppressWarnings({ "deprecation", "unchecked" })
public class BIRTEngine {

	private static String BIRTPATH = "/home/joshua/apache-tomcat-6.0.20/webapps/birt-runtime-2_2_1_1";
	private static Logger LOG = Logger.getLogger(BIRTEngine.class);
	private static IReportEngine engine = null;
	private static IRunAndRenderTask task = null;
	private static EngineConfig config = null;
	private static String ENGINE_CONFIG_TYPE = null;

	/**
	 *  BIRT Engine constructor initializes the BIRT and Report Engine 
	 */
	private BIRTEngine(){
		initializeBIRTAndReportEngine();
	}

	/**
	 * This method initializes the BIRT Engine which is used
	 * throughout the application for report Generation 
	 */
	public static void initializeBIRTAndReportEngine() {
		try {
			LOG.debug("Initialising BIRT Engine !!" + BIRTPATH);
			config = new EngineConfig();									// Instantiating an EngineConfig
			config.setEngineHome(BIRTPATH + "/ReportEngine");
			config.setLogConfig(BIRTPATH + "/log", Level.FINE);
			Platform.startup(config);
			IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
			engine = factory.createReportEngine(config);
			engine.changeLogLevel(Level.WARNING);
			LOG.debug("BIRT and Report Service Started Successfully!!");

		}catch (Exception exception) {

		}
	}

	/**
	 * This Method is used to create Task by using the following parameters
	 * @param rptFile
	 * @param configType
	 * @return IRunAndRenderTask
	 */
	public static  IRunAndRenderTask createTask(String rptFile, String configType) {
		try {
			if (engine != null&& config != null) {
				IReportRunnable design = engine.openReportDesign(BIRTPATH+ "/reports/" + rptFile + ".rptdesign");
				if(configType != null){
					LOG.debug("ConfigType Existss");
					ENGINE_CONFIG_TYPE = configType;

					// Configure the emitter to handle actions and images
					HTMLEmitterConfig emitterConfig = new HTMLEmitterConfig();
					emitterConfig.setActionHandler(new HTMLActionHandler());
					HTMLServerImageHandler imageHandler = new HTMLServerImageHandler();
					emitterConfig.setImageHandler(imageHandler);
					config.getEmitterConfigs().put(configType,emitterConfig); //$NON-NLS-1$
				}
				task = engine.createRunAndRenderTask(design);
				return task;
			} else {
				return null;
			}
		}catch (Exception e){
			shutDownTask(task);
			return null;
		}
	}

	/**
	 * This method is used for shuttingDown tasks
	 * This Method not only closes the task but it also
	 * looks if there are any Emitter configurations set
	 * If set it will remove the configuration and then it will close the Task
	 * @param localTask
	 */
	public static void shutDownTask(IRunAndRenderTask localTask) {
		if (null != localTask) {
			if (config.getEmitterConfigs().containsKey(ENGINE_CONFIG_TYPE)) {
				config.getEmitterConfigs().remove(ENGINE_CONFIG_TYPE);
			}
			localTask.close();
		}
	}

	/**
	 * This Method Shuts Down the BIRT Engine by 
	 * calling engine.destroy and Platform.shutdown
	 */
	public static void shutdownBIRTAndReportEngine() {
		if (null != engine) {
			engine.destroy();
		}
		Platform.shutdown();
		LOG.warn("BIRT and Report Service Shutdown");
	}
}