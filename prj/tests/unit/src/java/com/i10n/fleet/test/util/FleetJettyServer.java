package com.i10n.fleet.test.util;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * Helps the Application deployment based Test Cases to test test on the test
 * application deployed locally from the Jetty Server.Uses {@link Server} from
 * jetty library
 * 
 * @author sabarish
 * 
 */
public class FleetJettyServer {
    private FleetJettyServer() {
        PropertyLoader.load();
    }

    private static FleetJettyServer g_instance = null;

    private static final String PORT_PROP = "jetty.test.port";
    private static final String FLEET_PROP = "test.fleet.webapp";
    private static final String STATIC_PROP = "test.static.webapp";

    private Server m_jettyServer = new Server();
    private boolean m_started = false;

    /**
     * Returns the singleton instance of {@link FleetJettyServer}
     * 
     * @return
     */
    public synchronized static FleetJettyServer getInstance() {
        if (null == g_instance) {
            g_instance = new FleetJettyServer();
        }
        return g_instance;
    }

    /**
     * Starts the jetty server ans waits for the server to get completely
     * started up
     * 
     * @throws Exception
     */
    public synchronized void start() throws Exception {
        if (!m_started) {
            setupJettyServer();
            try {
                m_jettyServer.start();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            int seconds = 0;
            while (m_jettyServer.isStarting()) {
                if (seconds > 10) {
                    throw new JettyServerStartupException(
                            "Jetty Server took longer time than expected to start!");
                }
                Thread.sleep(1000);
                seconds++;
            }
            if (m_jettyServer.isFailed()) {
                throw new JettyServerStartupException("Jetty Server failed to startup!");
            }
            m_started = true;
        }
        else {
            throw new JettyServerStartupException("Server alredy started!");
        }
    }

    /**
     * Stops the jetty server
     * 
     * @throws Exception
     */
    public void stop() throws Exception {
        m_jettyServer.stop();
    }

    /**
     * Sets up the jetty server
     */
    private void setupJettyServer() {
        SocketConnector connector = new SocketConnector();
        connector.setPort(Integer.parseInt(PropertyLoader.getProperty(PORT_PROP)));
        m_jettyServer.setConnectors(new Connector[] { connector });

        WebAppContext appContext = new WebAppContext();
        appContext.setServer(m_jettyServer);
        appContext.setContextPath("/fleet");
        appContext.setWar(PropertyLoader.getProperty(FLEET_PROP));

        WebAppContext staticContext = new WebAppContext();
        staticContext.setServer(m_jettyServer);
        staticContext.setContextPath("/static");
        staticContext.setWar(PropertyLoader.getProperty(STATIC_PROP));

        m_jettyServer.addHandler(appContext);
        m_jettyServer.addHandler(staticContext);

        m_jettyServer.setStopAtShutdown(true);

    }
    
    @SuppressWarnings("unused")
    private class JettyServerStartupException extends Exception {

    	private static final long serialVersionUID = -156739869901948245L;

		public JettyServerStartupException() {
            super();
        }

        public JettyServerStartupException(Throwable cause) {
            super(cause);
        }

        public JettyServerStartupException(String msg, Throwable cause) {
            super(msg, cause);
        }

        public JettyServerStartupException(String msg) {
            super(msg);
        }
    }
}