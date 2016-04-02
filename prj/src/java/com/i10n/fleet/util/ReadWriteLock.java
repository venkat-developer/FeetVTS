package com.i10n.fleet.util;

import org.apache.log4j.Logger;

/**
 * Utility class for Lock mangement
 */
public class ReadWriteLock {

    /**
     * Logger for this class
     */
    private static final Logger LOG = Logger.getLogger(ReadWriteLock.class);

    /**
     * Lock Object
     */
    private Object m_lockObject;

    /**
     * # of read locks given
     */
    private int m_totalReadLocksGiven;

    /**
     * If <code>true</code> then a write lock is issued and would cause any
     * read/write to wait
     */
    private boolean m_writeLockIssuedFlag;

    /**
     * Number of thread waiting for the write lock
     */
    private int m_threadsWaitingForWriteLock;

    /**
     * Constructor
     */
    public ReadWriteLock() {
        m_lockObject = new Object();
        m_writeLockIssuedFlag = false;
    }

    /**
     * A read lock can be issued if there is no currently issued write lock and
     * there is no thread(s) currently waiting for the write lock
     */
    public void getReadLock() {
        synchronized(m_lockObject) {
            while((m_writeLockIssuedFlag) || (m_threadsWaitingForWriteLock != 0)) {
                try {
                    m_lockObject.wait();
                }catch(InterruptedException e) {
                    // do nothing
                }
            }
            m_totalReadLocksGiven++;
        }
    }

    /**
     * A write lock can be issued if there is no currently issued read or write
     * lock
     */
    public void getWriteLock() {
        synchronized(m_lockObject) {
            m_threadsWaitingForWriteLock++;

            while((m_totalReadLocksGiven != 0) || (m_writeLockIssuedFlag)) {
                try {
                    m_lockObject.wait();
                }catch(InterruptedException e) {
                    // ignore
                }
            }
            m_threadsWaitingForWriteLock--;
            m_writeLockIssuedFlag = true;
        }
    }

    /**
     * Release either the write lock or decrement the read lock count
     */
    public void done() {
        synchronized(m_lockObject) {

            // check for errors
            if((m_totalReadLocksGiven == 0) && (!m_writeLockIssuedFlag)) {
                LOG.debug("Invalid call to release the lock [No read/write aquired before]");
                return;
            }
            if(m_writeLockIssuedFlag) {
                m_writeLockIssuedFlag = false;
            }else {
                m_totalReadLocksGiven--;
            }
            m_lockObject.notifyAll();
        }
    }
}
