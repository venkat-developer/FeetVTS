package com.i10n.fleet.datasets;

import java.util.LinkedList;

import org.junit.Test;

import com.i10n.fleet.datasets.impl.NamedDataset;
import com.i10n.fleet.datasets.impl.SkinSetDataset;
import com.i10n.fleet.test.AbstractFleetTestCase;

/**
 * Test Cases for testing {@link SkinSetDataset}
 * 
 * @author subramaniam
 * 
 */
public class SkinSetDatasetTest extends AbstractFleetTestCase {
    /**
     * Tests {@link SkinSetDataset#hasSkin(String)}
     */
    @Test
    public void testHasSkin() {
        NamedDataset dataset = new NamedDataset();
        dataset.setName("skin");
        dataset.put("hello", "hello");
        SkinSetDataset skinset = new SkinSetDataset();
        skinset.addNamedDataset(dataset);
        assertTrue(skinset.hasSkin("skin"));
    }

    /**
     * Tests {@link SkinSetDataset#getSupportedSkins()}
     */
    @Test
    public void testGetSupportedSkins() {
        NamedDataset skinDataset = new NamedDataset();
        skinDataset.setName("skin");
        skinDataset.put("views", "listviews");
        NamedDataset skinDataset1 = new NamedDataset();
        skinDataset1.setName("skin1");
        skinDataset1.put("views", "listviews");
        LinkedList<NamedDataset> list = new LinkedList<NamedDataset>();
        list.add(skinDataset);
        list.add(skinDataset1);
        SkinSetDataset skinset = new SkinSetDataset();
        skinset.addAllNamedDatasets(list);
        assertTrue(skinset.getSupportedSkins().contains(skinDataset.getName()));
        assertTrue(skinset.getSupportedSkins().contains(skinDataset1.getName()));
    }

}
