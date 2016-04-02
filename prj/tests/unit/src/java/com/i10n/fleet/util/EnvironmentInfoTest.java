package com.i10n.fleet.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.i10n.fleet.test.AbstractFleetTestCase;
import com.i10n.fleet.test.util.PropertyLoader;

/**
 * Tests various functionalities if EnvironmentInfo
 * 
 * @author sabarish
 * 
 */
public class EnvironmentInfoTest extends AbstractFleetTestCase {

	private String m_environment = null;

	private static final String KEY_TEST_OUT_DIR = "env.out.dir";
	private static final String DEFAULT_SYSPROP_PROP = "systemtestproperty";
	private static final String DEFAULT_SYSNAME_PROP = "systemname";
	private static final String RELOAD_SYSNAME_PROP = "systemnamereload";
	private static final String DEFAULT_STREAM_TEST_FILE = "/environment/stream.test.txt";
	private static final String DEFAULT_STREAM_RESULT_FILE = "/environment/stream.expected.txt";
	private static final String TEST_PROP_NAME = "TEST_PROP_NAME";

	private static Logger LOG = Logger.getLogger(EnvironmentInfoTest.class);

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		PropertyLoader.load();
		m_environment = System.getProperty(EnvironmentInfo.ENVIROMENT_PROP_NAME);
		EnvironmentInfo.clear();
		System.clearProperty(EnvironmentInfo.ENVIROMENT_PROP_NAME);
	}

	/**
	 * Tests fail back mechanism of getting back to default properties
	 */
	@Test
	public void testFailBack() {
		EnvironmentInfo.load(this);
		assertEquals("Failed on failback", EnvironmentInfo.DEFAULT_ENV, EnvironmentInfo
				.getEnvironment());
	}

	/**
	 * Tests EnvironmentInfo if it correctly loads properties based on system
	 * property.
	 */
	@Test
	public void testSystemProperty() {
		System.setProperty(EnvironmentInfo.ENVIROMENT_PROP_NAME, DEFAULT_SYSPROP_PROP);
		EnvironmentInfo.load(this);
		assertEquals(DEFAULT_SYSPROP_PROP, EnvironmentInfo.getEnvironment());
		assertEquals("systemprop", EnvironmentInfo.getProperty(TEST_PROP_NAME));
	}

	/**
	 * Tests EnvironmentInfo if it correctly loads properties based on system
	 * name.
	 */
	@Test
	public void testSystemNameProperty() {
		String systemName = getSystemName();
		File tempPropertyFile = createSystemPropertyFile(systemName, DEFAULT_SYSNAME_PROP);
		if (null != tempPropertyFile) {
			EnvironmentInfo.load(this);
			assertEquals(systemName, EnvironmentInfo.getEnvironment());
			assertEquals("systemname", EnvironmentInfo.getProperty(TEST_PROP_NAME));
			tempPropertyFile.delete();
		}
		else {
			fail("Error creating system property file with name = " + systemName);
		}
	}

	/**
	 * Tests EnvironmentInfo if it correctly detemplatizes evironment based
	 * streams
	 */
	@Test
	public void testEnvironmentStream() {
		System.setProperty(EnvironmentInfo.ENVIROMENT_PROP_NAME, DEFAULT_SYSPROP_PROP);
		EnvironmentInfo.load(this);
		InputStream testStream = null;
		InputStream resultStream = null;
		InputStream outStream = null;
		try {
			testStream = EnvironmentInfoTest.class
					.getResourceAsStream(DEFAULT_STREAM_TEST_FILE);
			resultStream = EnvironmentInfoTest.class
					.getResourceAsStream(DEFAULT_STREAM_RESULT_FILE);
			outStream = EnvironmentInfo.getEnvironmentalizedStream(testStream);
			assertTrue(IOUtils.contentEquals(resultStream, outStream));
		}
		catch (IOException e) {
			LOG.error("Caught IOException while testing environmentalized stream", e);
			fail("Caught IOException while testing environmentalized stream :: \n"
					+ e.getMessage());
		}

	}

	/**
	 * Tests EnvironmentInfo if it correctly reloads environment properties.
	 */
	@Test
	public void testReload() {
		testSystemNameProperty();
		String systemName = getSystemName();
		File tempPropertyFile = createSystemPropertyFile(systemName, RELOAD_SYSNAME_PROP);
		EnvironmentInfo.load(this);
		assertEquals(systemName, EnvironmentInfo.getEnvironment());
		assertEquals("systemnamereload", EnvironmentInfo.getProperty(TEST_PROP_NAME));
		tempPropertyFile.delete();
	}

	private File createSystemPropertyFile(String systemfile, String templatefile) {
		File result = null;
		String testDir = PropertyLoader.getProperty(KEY_TEST_OUT_DIR);
		if (null != testDir && null != systemfile) {
			result = new File(testDir + "/" + systemfile + ".properties");
			InputStream in = null;
			OutputStream out = null;
			try {
				in = new FileInputStream(testDir + "/" + templatefile + ".properties");
				out = new FileOutputStream(result);
				IOUtils.copy(in, out);
			}
			catch (IOException e) {
				LOG.error("Caught IOException while copying systemname property file", e);
				result = null;
			}
			finally {
				IOUtils.closeQuietly(in);
				IOUtils.closeQuietly(out);
			}
		}
		return result;
	}

	private String getSystemName() {
		String systemName = null;
		try {
			systemName = InetAddress.getLocalHost().getHostName();
		}
		catch (UnknownHostException e) {
			LOG.error("Caught UnknownHostException while retrieving local computername",
					e);
		}
		return systemName;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		EnvironmentInfo.clear();
		if (null != m_environment) {
			System.setProperty(EnvironmentInfo.ENVIROMENT_PROP_NAME, m_environment);
		}
		else {
			System.clearProperty(EnvironmentInfo.ENVIROMENT_PROP_NAME);
		}
	}

}
