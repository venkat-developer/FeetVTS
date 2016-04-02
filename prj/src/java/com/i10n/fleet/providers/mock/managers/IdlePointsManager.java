package com.i10n.fleet.providers.mock.managers;

/**
 * Mock : Mock Data Manager for Idle Points Data. This class will be removed in
 * future.
 * 
 * @author Sabarish
 * 
 */
public class IdlePointsManager extends AbstractViolationsManager implements
        IIdlePointsManager {
    private static final String FILE_DOCUMENT = "/mock/idlepoints.xml";

    /**
     * @see AbstractDataManager#getDocumentName()
     */
    protected String getDocumentName() {
        return FILE_DOCUMENT;
    }

}
