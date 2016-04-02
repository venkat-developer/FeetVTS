package com.i10n.fleet.providers.managers;

import org.junit.Test;
import org.w3c.dom.Document;

import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.managers.impl.SkinManager;
import com.i10n.fleet.skins.ISkinFactory;
import com.i10n.fleet.test.AbstractFleetTestCase;

/**
 * Test Cases for testing {@link SkinManager}
 * 
 * @author sabarish
 * 
 */
public class SkinManagerTest extends AbstractFleetTestCase {
    /**
     * Tests Data Providability of {@link SkinManager}
     */
    @Test
    public void testDataProvider() {
        SkinManager manager = new SkinManager();
        Dataset dataset = manager.getSkin();
        assertTrue(dataset.containsKey("default"));
    }

    /**
     * Tests {@link SkinManager#reload()}
     */
    @Test
    public void testReloadability() {
        ISkinFactory factory = new ISkinFactory() {
            public Dataset getSkins(Document doc) {
                Dataset result = new Dataset();
                result.put("default", null);
                return result;
            }
        };
        SkinManager manager = new SkinManager();
        manager.setSkinFactory(factory);
        assertEquals(factory, manager.getSkinFactory());
        assertTrue(manager.getSkin().containsKey("default"));
        factory = new ISkinFactory() {
            public Dataset getSkins(Document doc) {
                Dataset result = new Dataset();
                result.put("default", null);
                result.put("skin1", null);
                return result;
            }
        };
        manager.setSkinFactory(factory);
        manager.reload();
        assertTrue(manager.getSkin().containsKey("default"));
        assertTrue(manager.getSkin().containsKey("skin1"));

    }

}
