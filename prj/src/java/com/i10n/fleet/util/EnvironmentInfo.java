package com.i10n.fleet.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * This class is used to load Environment based properties. Helps is using
 * environmentalized properties and streams.
 * 
 * @author sabarish
 * 
 */
public final class EnvironmentInfo {

	public static final String ENVIROMENT_PROP_NAME = "FLEETCHK_ENV";
	public static final String DEFAULT_ENV = "default";

	private static Properties g_properties = new Properties();
	private static String g_environment = DEFAULT_ENV;
	private static boolean g_isLoaded = false;

	private static final Logger LOG = Logger.getLogger(EnvironmentInfo.class);

	private EnvironmentInfo() {
	}

	/**
	 * Some Configurable Environment Parameters
	 */
	public enum EnvironmentParams {
		DEFAULT_SKIN, CLUSTER_ENABLED, FREEMARKER_COMPRESSION_ENABLED, FREEMARKER_COMPRESSION_SINGLELINE_ENABLED
	}

	/**
	 * Clears current environment settings.
	 */
	public static void clear() {
		g_isLoaded = false;
		g_properties.clear();
		g_environment = DEFAULT_ENV;
	}

	/**
	 * Loads the properties based on the running environment.If already loaded ,
	 * it will try reloading again. The environment is decided based on the
	 * following basis. if FLEETCHK_ENV is set in System Properties then its
	 * value is taken. If not set it will try environment properties.if still
	 * not set , it will pick computer name if no such file exists then
	 * default.properties is picked
	 * 
	 * @param classLoaderObj
	 */
	public static void load(Object classLoaderObj) {
		InputStream propStream = null;
		if (!g_isLoaded) {
			try {
				String environment = System.getProperty(ENVIROMENT_PROP_NAME);
				if (null == environment) {
					try {
						environment = InetAddress.getLocalHost().getHostName();
					}
					catch (UnknownHostException e) {
						LOG.error(
								"Caught UnknownHostException while retrieving localhost",
								e);
					}
				}
				if (null == environment) {
					environment = DEFAULT_ENV;
				}
				propStream = classLoaderObj.getClass().getResourceAsStream(
						"/environment/" + environment + ".properties");
				if (null == propStream) {
					LOG.debug("Failing back to default properties as env :" + environment
							+ "was not found");
					propStream = classLoaderObj.getClass().getResourceAsStream(
							"/environment/" + DEFAULT_ENV + ".properties");
					environment = DEFAULT_ENV;
				}
				g_environment = environment;
				LOG.info("Using environment " + environment);
				g_properties.load(propStream);
				g_isLoaded = true;
			}
			catch (IOException e) {
				LOG.error("Error loading environment properties file", e);
			}
			finally {
				IOUtils.closeQuietly(propStream);
			}
		}
		else {
			propStream = classLoaderObj.getClass().getResourceAsStream(
					"/environment/" + g_environment + ".properties");
			try {
				g_properties.load(propStream);
			}
			catch (IOException e) {
				LOG.error("Error reloading environment properties file", e);
			}
			finally {
				IOUtils.closeQuietly(propStream);
			}
		}
	}

	/**
	 * Checks whether the EnvironmentInfo is loaded
	 * 
	 * @return
	 */
	public static boolean isLoaded() {
		return g_isLoaded;
	}

	/**
	 * Returns environment property set
	 * 
	 * @param propName
	 * @return
	 */
	public static String getProperty(String propName) {
		return g_properties.getProperty(propName);
	}

	/**
	 * Returns environment property set
	 * 
	 * @param propName
	 * @return
	 */
	public static String getProperty(EnvironmentParams propName) {
		return g_properties.getProperty(propName.toString());
	}

	/**
	 * Returns the current loaded environment;
	 * 
	 * @return
	 */
	public static String getEnvironment() {
		return g_environment;
	}

	/**
	 * Returns the environment property map in Map<String, String> format.
	 * 
	 * @return
	 */
	public static Map<String, String> getPropertyMap() {
		Map<String, String> result = new HashMap<String, String>();
		for (Entry<Object, Object> entry : g_properties.entrySet()) {
			result.put((String) entry.getKey(), (String) entry.getValue());
		}
		return result;
	}

	/**
	 * Returns detemplatized stream from the input stream passed based on the
	 * environment properties
	 * 
	 * @param stream
	 * @return
	 */
	public static InputStream getEnvironmentalizedStream(InputStream stream) {
		Template template = null;
		InputStream result = null;
		ByteArrayOutputStream out = null;
		try {
			template = new Template("environment", new InputStreamReader(stream), null);
			out = new ByteArrayOutputStream();
			Writer writer = new OutputStreamWriter(out);
			template.process(getPropertyMap(), writer);
			result = new ByteArrayInputStream(out.toByteArray());
		}
		catch (IOException e) {
			LOG.error("Caught IOException while loading stream for detemplatizing", e);
		}
		catch (TemplateException e) {
			LOG.error(
					"Caught TemplateException while detemplatizing the property stream ",
					e);
		}
		finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(stream);
		}
		return result;
	}
}
