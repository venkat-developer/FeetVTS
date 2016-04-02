<?xml version="1.0"?>
<!--
1. The XSLT File that contains genric templates that can be reused
-->

<!--Author : N.Balaji-->


<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:exsl="http://exslt.org/common"
    xmlns:fn="http://www.w3.org/2005/04/xpath-functions">
    
    <!--
    1. Generic template for adding a single element to an xml document
    -->
    <xsl:template name="addSingularElement">
        <xsl:param name="element"/>
        <xsl:param name="resultElementName"/>
        <xsl:param name="defaultValue"/>
        <xsl:param name="prependedString"/>
        <xsl:param name="appendedString"/>
        <xsl:param name="applyPrefixToDefault"/>
        <xsl:param name="applySuffixToDefault"/>
        <xsl:param name="addNameAttribute"/>
        <!--Adding any template that is found here-->
        <xsl:for-each select="exsl:node-set($element)">
            <!--Checking for an empty element-->
            <xsl:variable name="content">
                <xsl:value-of select="."/>
            </xsl:variable>
            <xsl:variable name="nameAttribute">
                <xsl:value-of select="./@name"/>
            </xsl:variable>
            <xsl:if test="normalize-space($content)!=''">
                
                <xsl:element name="{$resultElementName}">
                    <xsl:if test="$addNameAttribute='true'">
                        <xsl:attribute name="name">
                            <xsl:if test="normalize-space($nameAttribute)!=''">
                                <xsl:value-of select="$nameAttribute"/>
                            </xsl:if>
                            <xsl:if test="normalize-space($nameAttribute)=''">
                                <xsl:value-of select="normalize-space(.)"/>
                            </xsl:if>
                        </xsl:attribute>
                    </xsl:if>
                    <xsl:if test="$prependedString!=''">
                        <xsl:value-of select="$prependedString"/>
                    </xsl:if>
                    
                    <xsl:value-of select="normalize-space(.)"/>
                    
                    <xsl:if test="$appendedString!=''">
                        <xsl:value-of select="$appendedString"/>
                    </xsl:if>
                </xsl:element>
                
            </xsl:if>
        </xsl:for-each>
        
        <!--Adding the default Element-->
        
        <xsl:if test="$defaultValue!=''">
            
            <xsl:element name="{$resultElementName}">
                <xsl:if test="$addNameAttribute='true'">
                    <xsl:attribute name="name">
                        <xsl:value-of select="$defaultValue"/>
                    </xsl:attribute>
                </xsl:if>
                <xsl:if test="$applyPrefixToDefault='true'">
                    <xsl:value-of select="$prependedString"/>
                </xsl:if>
                
                <xsl:value-of select="$defaultValue"/>
                
                <xsl:if test="$applySuffixToDefault='true'">
                    <xsl:value-of select="$appendedString"/>
                </xsl:if>
            </xsl:element>
            
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="optimizeSingularElement">
        <xsl:param name="element"/>
        <xsl:param name="resultElementName"/>
        <xsl:param name="addNameAttribute"/>
        
        <!--Adding any template that is found here-->
        <xsl:for-each select="exsl:node-set($element)">
            <xsl:if
                test="not(normalize-space(./text())=preceding-sibling::*[name()=name(current())]/text() or ./text()=preceding-sibling::*[name()=name(current())]/text())">
                
                <xsl:variable name="content">
                    <xsl:value-of select="."/>
                </xsl:variable>
                <xsl:if test="normalize-space($content)!=''">
                    
                    <xsl:element name="{$resultElementName}">
                        <xsl:if test="$addNameAttribute='true'">
                            <xsl:attribute name="name">
                                <xsl:value-of select="./@name"/>
                            </xsl:attribute>
                        </xsl:if>
                        <xsl:value-of select="normalize-space(.)"/>
                    </xsl:element>
                    
                </xsl:if>
                
            </xsl:if>
        </xsl:for-each>
        
    </xsl:template>
    
</xsl:stylesheet>