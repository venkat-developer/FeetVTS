<?xml version="1.0"?>
<!--
The Following XSLT Performs two post processing Tasks 1. Eliminates
all the Duplicated Widgets inside each view 2. Eliminates all the
duplicated scripts inside each view , widget 3. Eliminates all the
duplicated stylesheets inside each view , widget
-->

<!--
Author : N.Balaji
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.w3.org/2005/04/xpath-functions">
    
    <!--Importing necessary files-->
    
    <xsl:import href="generictemplates.xslt"/>
    
    
    <!--Setting the output type-->
    
    <xsl:output method="xml" version="1.0" omit-xml-declaration="yes"
        standalone="yes"/>
    
    <!--The Main Widget-->
    
    <xsl:template match="/">
        <!-- Populate the skin -->
        <skin>
            <xsl:attribute name="name">
                <xsl:value-of select="//skin/@name"/>
            </xsl:attribute>
            
            <!--Populate the Views-->
            <views>
                
                <!--Populate Each View-->
                <xsl:for-each select="/skin/views/view">
                    <xsl:call-template name="populateview">
                        <xsl:with-param name="ViewName">
                            <xsl:value-of select="@name"/>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:for-each>
            </views>
            
            <!--Populate the Widgets-->
            <widgets>
                <xsl:call-template name="populatewidgets"/>
            </widgets>
            
            <!--Applying the Default template to include any un processes Tags-->
            <xsl:for-each select="//skin">
                <xsl:apply-templates/>
            </xsl:for-each>
        </skin>
    </xsl:template>
    
    <!--The Following template populates all the views-->
    
    <xsl:template name="populateview">
        <xsl:param name="ViewName"/>
        
        <xsl:for-each select="/skin/views/view">
            <xsl:if test="$ViewName=@name">
                
                
                <view>
                    <xsl:attribute name="name">
                        <xsl:value-of select="@name"/>
                    </xsl:attribute>
                    <xsl:attribute name="title">
                        <xsl:value-of select="@title"/>
                    </xsl:attribute>
                    
                    <!--Populate Libraries-->
                    <libraries>
                        <xsl:for-each select="libraries">
                            <xsl:for-each
                                select="library[not(./@name= preceding-sibling::library[@name]/@name)]">
                                <library>
                                    <xsl:attribute name="name">
                                        <xsl:value-of
                                            select='@name'/>
                                    </xsl:attribute>
                                    <xsl:attribute name="mergeAs">
                                        <xsl:value-of select="@mergeAs"/>
                                    </xsl:attribute>
                                    <xsl:for-each select="scripts">
                                        <scripts>
                                            <xsl:for-each select="script">
                                                <script>
                                                    <xsl:attribute name="merge">
                                                        <xsl:value-of select="@merge"/>
                                                    </xsl:attribute>
                                                    <xsl:attribute name="min">
                                                        <xsl:value-of select="@min"/>
                                                    </xsl:attribute>
                                                    <xsl:value-of select="."/>
                                                </script>
                                            </xsl:for-each>
                                        </scripts>
                                    </xsl:for-each>
                                    <xsl:for-each select="stylesheets">
                                        <stylesheets>
                                            <xsl:for-each select="stylesheet">
                                                <stylesheet>
                                                    <xsl:value-of select="."/>
                                                </stylesheet>
                                            </xsl:for-each>
                                        </stylesheets>
                                    </xsl:for-each>
                                </library>
                            </xsl:for-each>
                        </xsl:for-each>
                    </libraries>
                    
                    <!--Populate Scripts-->
                    <xsl:call-template name="populateScripts"/>
                    
                    
                    <!--Populate Stylesheets-->
                    <xsl:call-template name="populateStylesheets"/>
                    
                    <!--Populate templates-->
                    <xsl:call-template name="optimizeSingularElement">
                        <xsl:with-param name="element" select="./template"/>
                        <xsl:with-param name="resultElementName" select="'template'"/>
                        <xsl:with-param name="addNameAttribute">
                            <xsl:value-of select="'false'"/>
                        </xsl:with-param>
                    </xsl:call-template>
                    
                    <!--Populate data-->
                    <xsl:call-template name="optimizeSingularElement">
                        <xsl:with-param name="element" select="./data"/>
                        <xsl:with-param name="resultElementName" select="'data'"/>
                        <xsl:with-param name="addNameAttribute">
                            <xsl:value-of select="'false'"/>
                        </xsl:with-param>
                    </xsl:call-template>
                    
                    <!--Populating the Widgets-->
                    <widgets>
                        <xsl:call-template name="populatewidgetsinview">
                            <xsl:with-param name="ViewName">
                                <xsl:value-of select="@name"/>
                            </xsl:with-param>
                        </xsl:call-template>
                    </widgets>
                    
                    <!--Applying the Default template to include any un processes Tags-->
                    <xsl:apply-templates/>
                </view>
            </xsl:if>
        </xsl:for-each>
        
    </xsl:template>
    
    <!--
    Uitility template for populating each widget that is particular to a
    view
    -->
    
    <xsl:template name="populatewidgetsinview">
        <xsl:param name="ViewName"/>
        
        <!--Selecting the Correct View-->
        <xsl:for-each select="/skin/views/view">
            <xsl:if test="$ViewName=@name">
                <xsl:for-each select="./widgets">
                    <xsl:for-each
                        select="./widget [not(./@name= preceding-sibling::widget[@name]/@name)]">
                        <widget>
                            <xsl:attribute name="name">
                                <xsl:value-of select='@name'/>
                            </xsl:attribute>
                            <xsl:attribute name="type">
                                <xsl:value-of select="'ui'"/>
                            </xsl:attribute>
                            
                            <!--Populate Scripts-->
                            <xsl:call-template name="populateScripts"/>
                            
                            <!--Populate Stylesheets-->
                            <xsl:call-template name="populateStylesheets"/>
                            
                            <!--Populate templates-->
                            <xsl:call-template name="optimizeSingularElement">
                                <xsl:with-param name="element" select="./template"/>
                                <xsl:with-param name="resultElementName" select="'template'"/>
                                <xsl:with-param name="addNameAttribute">
                                    <xsl:value-of select="'false'"/>
                                </xsl:with-param>
                            </xsl:call-template>
                            
                            <!--Populate data-->
                            <xsl:call-template name="optimizeSingularElement">
                                <xsl:with-param name="element" select="./data"/>
                                <xsl:with-param name="resultElementName" select="'data'"/>
                                <xsl:with-param name="addNameAttribute">
                                    <xsl:value-of select="'false'"/>
                                </xsl:with-param>
                            </xsl:call-template>
                        </widget>
                    </xsl:for-each>
                </xsl:for-each>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    
    <!--Template for populating the Global widget declarations-->
    
    <xsl:template name="populatewidgets">
        <xsl:for-each select="/skin/widgets">
            <xsl:for-each
                select="./widget [not(./@name= preceding-sibling::widget[@name]/@name)]">
                <widget>
                    
                    <xsl:attribute name="name">
                        <xsl:value-of select='@name'/>
                    </xsl:attribute>
                    <xsl:attribute name="type">
                        <xsl:value-of select="'ui'"/>
                    </xsl:attribute>
                    
                    <!--Populate Scripts-->
                    <xsl:call-template name="populateScripts"/>
                    
                    <!--Populate Stylesheets-->
                    <xsl:call-template name="populateStylesheets"/>
                    
                    <!--Populate templates-->
                    <xsl:call-template name="optimizeSingularElement">
                        <xsl:with-param name="element" select="./template"/>
                        <xsl:with-param name="resultElementName" select="'template'"/>
                        <xsl:with-param name="addNameAttribute">
                            <xsl:value-of select="'false'"/>
                        </xsl:with-param>
                    </xsl:call-template>
                    
                    <!--Populate data-->
                    <xsl:call-template name="optimizeSingularElement">
                        <xsl:with-param name="element" select="./data"/>
                        <xsl:with-param name="resultElementName" select="'data'"/>
                        <xsl:with-param name="addNameAttribute">
                            <xsl:value-of select="'false'"/>
                        </xsl:with-param>
                    </xsl:call-template>
                </widget>
            </xsl:for-each>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template name="populateScripts">
        <scripts>
            <xsl:for-each select="./scripts">
                <xsl:for-each
                    select="./script[not(normalize-space(./text())= preceding-sibling::script/text() or ./text()= preceding-sibling::script/text())]">
                    <script>
                        <xsl:value-of select="."/>
                    </script>
                </xsl:for-each>
            </xsl:for-each>
        </scripts>
    </xsl:template>
    
    <xsl:template name="populateStylesheets">
        <stylesheets>
            <xsl:for-each select="./stylesheets">
                <xsl:for-each
                    select="stylesheet[not(normalize-space(./text())= preceding-sibling::stylesheet/text() or ./text()= preceding-sibling::stylesheet/text())]">
                    <stylesheet>
                        <xsl:value-of select="."/>
                    </stylesheet>
                </xsl:for-each>
            </xsl:for-each>
        </stylesheets>
    </xsl:template>
    
    <xsl:template name="default" match="*">
        <xsl:for-each select=".">
            <xsl:variable name="elementName">
                <xsl:value-of select="name(.)"/>
            </xsl:variable>
            <xsl:if
                test="not(normalize-space(./text())= preceding-sibling::*[name()=name(current())]/text() or ./text()= preceding-sibling::*[name()=name(current())]/text())">
                <xsl:if test="./@autoInclude='true'">
                    <xsl:element name="{$elementName}">
                        <xsl:attribute name="autoInclude">
                            <xsl:value-of select="./@autoInclude"/>
                        </xsl:attribute>
                        <xsl:value-of select="normalize-space(.)"/>
                    </xsl:element>
                </xsl:if>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>