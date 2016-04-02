package com.i10n.tools.skins.beans;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;

import com.i10n.tools.skins.SkinConstants;

public class Skin {
    private String m_name = "";
    private Skin m_baseSkin = null;
    private Map<String, View> m_views = new LinkedHashMap<String, View>();
    private Map<String, ILibrary> m_libraries = new LinkedHashMap<String, ILibrary>();
    private Document m_document = null;

    public Skin(String name) {
        m_name = name;
    }

    public Skin(String name, Document document) {
        m_name = name;
        m_document = document;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    public Skin getBaseSkin() {
        return m_baseSkin;
    }

    public void setBaseSkin(Skin skin) {
        m_baseSkin = skin;
    }

    public List<View> getViews() {
        return new ArrayList<View>(m_views.values());
    }

    public void addViews(Map<String, View> views) {
        m_views.putAll(views);
    }

    public Document getDocument() {
        return m_document;
    }

    public void setDocument(Document document) {
        m_document = document;
    }

    public List<ILibrary> getLibraries() {
        return new ArrayList<ILibrary>(m_libraries.values());
    }

    public ILibrary getLibrary(String name) {
        return m_libraries.get(name);
    }

    public void addLibraries(Map<String, ILibrary> libraries) {
        m_libraries.putAll(libraries);
    }

    public String getNamespace() {
        return "skins" + SkinConstants.DELIMITER + m_name;
    }

}
