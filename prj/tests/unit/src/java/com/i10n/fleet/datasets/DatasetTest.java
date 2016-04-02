package com.i10n.fleet.datasets;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.junit.Test;

import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.datasets.impl.NamedDataset;
import com.i10n.fleet.test.AbstractFleetTestCase;

/**
 * Test Cases for testing {@link Dataset}
 * 
 * @author suppi
 * 
 */
public class DatasetTest extends AbstractFleetTestCase {
    /**
     * Tests the empty dataset addition
     */
    @Test
    public void testEmptyPut() {
        IDataset dataset = new Dataset();
        dataset.put("test.path.value", "Value");
        assertEquals(dataset.get("test.path.value"), "Value");
        assertEquals(dataset.getDataset("test").getValue("path.value"), "Value");
    }

    /**
     * Tests replacing addition to {@link Dataset}
     */
    @Test
    public void testReplacePut() {
        IDataset dataset = new Dataset();
        dataset.put("test.path.value", "Value");
        assertEquals(dataset.get("test.path.value"), "Value");
        // Replaces test.path = value{"Value"} to test.path = "Path"
        dataset.put("test.path", "Path");
        assertEquals(dataset.get("test.path"), "Path");
    }

    /**
     * Tests addition where half of path is full rest is not added
     */
    @Test
    public void testAddPut() {
        IDataset dataset = new Dataset();
        dataset.put("test.path.value", "Value");
        assertEquals(dataset.get("test.path.value"), "Value");
        dataset.put("test.path.value.subval.value", "SubValue");
        assertNotSame(dataset.get("test.path.value"), "Value");
        assertEquals(dataset.get("test.path.value.subval.value"), "SubValue");
    }

    /**
     * Tests Constructor
     */
    @Test
    public void testConstructor() {
        Map<String, Object> testMap = new LinkedHashMap<String, Object>();
        testMap.put("path", "Path");
        IDataset dataset = new Dataset(testMap);
        assertEquals(dataset.get("path"), "Path");
    }

    /**
     * Tests {@link Dataset#addNamedDataset(INamedDataset)}
     */
    @Test
    public void testAddNamedDataset() {
        Dataset dataset = new Dataset();
        NamedDataset namedDataset = new NamedDataset();
        namedDataset.setName("testmap");
        namedDataset.put("test", "test");
        dataset.addNamedDataset(namedDataset);
        assertEquals(dataset.getValue("testmap.test"), "test");
    }

    /**
     * Tests {@link Dataset#addAllNamedDatasets(java.util.List)}
     */
    @Test
    public void testAddAllNamedDatasets() {
        LinkedList<NamedDataset> list = new LinkedList<NamedDataset>();
        NamedDataset namedDataset = new NamedDataset();
        namedDataset.setName("testmap");
        namedDataset.put("test", "test");
        NamedDataset namedDataset1 = new NamedDataset();
        namedDataset1.setName("testmap1");
        namedDataset1.put("test1", "test1");
        list.add(namedDataset);
        list.add(namedDataset1);
        Dataset dataset = new Dataset();
        dataset.addAllNamedDatasets(list);
        assertEquals(dataset.getDataset("testmap").get("test"), "test");
    }

    /**
     * Tests {@link Dataset#copy()}
     */
    @Test
    public void testDatasetCopy() {
        Dataset data = new Dataset();
        data.put("x.y.z", "x.y.z");
        data.put("x.y.a", "x.y.a");
        IDataset copy = data.copy();
        assertNotSame(copy, data);
        assertTrue("Copy is the same reference, not a copy", copy != data);
        assertTrue("inner data same reference, not a copy", copy.get("x") != data
                .get("x"));
        assertTrue("inner data is the same reference, not a copy",
                copy.get("x.y") != data.get("x.y"));
        assertEquals("Data is not the same", copy.getValue("x.y.z"), data
                .getValue("x.y.z"));
        assertEquals("Data is not the same", copy.getValue("x.y.a"), data
                .getValue("x.y.a"));
        assertEquals("Data size is not the same", copy.getDataset("x").size(), data
                .getDataset("x").size());
        assertEquals("Data size is not the same", copy.getDataset("x.y").size(), data
                .getDataset("x.y").size());
    }

    /**
     * Tests {@link Dataset#getBoolean(String)}
     */
    @Test
    public void testGetBoolean() {
        Dataset data = new Dataset();
        data.put("x.y", true);
        data.put("x.x", false);
        assertTrue(data.getBoolean("x.y"));
        assertFalse(data.getBoolean("x.x"));
        assertFalse(data.getBoolean("x.z"));
    }

}
