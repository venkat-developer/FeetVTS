<?xml version="1.0"?>
<!--
1. The XSLT File for Optimizing a built Skin a. Eliminates Empty
Elements b. Eliminates Duplication in Scripts c. Eliminates
Duplication in StyleSheets d. Eliminates Duplication in Templates
-->

<!--Author : N.Balaji-->

<!-- TODO :1. Add Attributes of all the elements -->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.w3.org/2005/04/xpath-functions">
    
    <!--Importing necessary files-->
    
    <xsl:import href="generictemplates.xslt"/>
    
    <!--Setting the output type-->
    
    <xsl:output method="xml" version="1.0" omit-xml-declaration="yes"
        standalone="yes" indent="no"/>
    
    <!--The Main Template-->
    
    <xsl:template name="main" match="/">
        <skin>
            <xsl:attribute name="name">
                <xsl:value-of select="//skin/@name"/>
            </xsl:attribute>
            <!--Generating the Views-->
            <xsl:call-template name="views"/>
            <!--Generating the Widgets-->
            <xsl:call-template name="widgets"/>
            <!--Generating the libraries-->
            <xsl:call-template name="libraries"/>
            
            <!-- Select the filters here -->
            <!--Applying the Default template to include any un processes Tags-->
            <xsl:for-each select="//skin">
                <xsl:apply-templates/>
            </xsl:for-each>
        </skin>
    </xsl:template>
    
    <!--The Views Template -->
    <!--For Populating all the widgets at the root level-->
    <xsl:template name="views">
        <views>
            
            <!--
            1. The following loop condition creates views with unique names ->
            The Logic within the loop "adds" any file in the depended views
            -->
            
            <xsl:for-each select="//view">
                
                
                <view>
                    <xsl:attribute name="name">
                        <xsl:value-of select="@name"/>
                    </xsl:attribute>
                    <xsl:attribute name="title">
                        <xsl:value-of select="@title"/>
                    </xsl:attribute>
                    
                    <!--Optimizing Scripts-->
                    <xsl:variable name="scriptcontent">
                        <xsl:value-of select="scripts/script"/>
                    </xsl:variable>
                    
                    <xsl:if test="$scriptcontent!=''">
                        <scripts>
                            <xsl:for-each select="scripts">
                                <xsl:for-each
                                    select="script[not(normalize-space(./text())= preceding-sibling::script/text() or ./text()= preceding-sibling::script/text())]">
                                    <script>
                                        <xsl:value-of select="."/>
                                    </script>
                                </xsl:for-each>
                            </xsl:for-each>
                            <!-- Adding the Filterset -->
                            <xsl:for-each select="./scripts">
                                <xsl:call-template name="addFilterSet"/>
                            </xsl:for-each>
                        </scripts>
                    </xsl:if>
                    
                    <!--Optimizing stylesheets-->
                    <xsl:variable name="stylesheetcontent">
                        <xsl:value-of select="stylesheets/stylesheet"/>
                    </xsl:variable>
                    
                    <xsl:if test="$stylesheetcontent!=''">
                        <stylesheets>
                            <xsl:for-each select="stylesheets">
                                <xsl:for-each
                                    select="stylesheet[not(normalize-space(./text())= preceding-sibling::stylesheet/text() or ./text()= preceding-sibling::stylesheet/text())]">
                                    <stylesheet>
                                        <xsl:value-of select="."/>
                                    </stylesheet>
                                </xsl:for-each>
                            </xsl:for-each>
                            <!-- Adding the Filterset -->
                            <xsl:for-each select="./stylesheets">
                                <xsl:call-template name="addFilterSet"/>
                            </xsl:for-each>
                        </stylesheets>
                    </xsl:if>
                    
                    <!--Optimizing templatess-->
                    <xsl:call-template name="optimizeSingularElement">
                        <xsl:with-param name="element" select="./template"/>
                        <xsl:with-param name="resultElementName" select="'template'"/>
                        <xsl:with-param name="addNameAttribute">
                            <xsl:value-of select="'false'"/>
                        </xsl:with-param>
                    </xsl:call-template>
                    
                    <!--Including all the data-->
                    <xsl:call-template name="optimizeSingularElement">
                        <xsl:with-param name="element" select="./data"/>
                        <xsl:with-param name="resultElementName" select="'data'"/>
                        <xsl:with-param name="addNameAttribute">
                            <xsl:value-of select="'false'"/>
                        </xsl:with-param>
                    </xsl:call-template>
                    
                    <!--Optimizing Widgetrefs-->
                    <xsl:variable name="widgetcontent">
                        <xsl:value-of select="widgetrefs/widgetref/@ref"/>
                    </xsl:variable>
                    
                    <xsl:if test="$widgetcontent!=''">
                        <widgetrefs>
                            <xsl:for-each select="widgetrefs">
                                <xsl:for-each
                                    select="widgetref[not(./@ref = preceding-sibling::widgetref[@ref]/@ref)]">
                                    <widgetref>
                                        <xsl:attribute name="ref">
                                            <xsl:value-of
                                                select="@ref"/>
                                        </xsl:attribute>
                                    </widgetref>
                                </xsl:for-each>
                            </xsl:for-each>
                            <!-- Adding the Filterset -->
                            <xsl:for-each select="./widgetrefs">
                                <xsl:call-template name="addFilterSet"/>
                            </xsl:for-each>
                        </widgetrefs>
                    </xsl:if>
                    
                    <!--Optimizing the Libraryrefs-->
                    <xsl:variable name="libcontent">
                        <xsl:value-of select="libraryrefs/libraryref/@ref"/>
                    </xsl:variable>
                    
                    <xsl:if test="$libcontent!=''">
                        <libraryrefs>
                            <xsl:for-each select="libraryrefs">
                                <xsl:for-each
                                    select="libraryref[not(./@ref = preceding-sibling::libraryref[@ref]/@ref)]">
                                    <libraryref>
                                        <xsl:attribute name="ref">
                                            <xsl:value-of
                                                select="@ref"/>
                                        </xsl:attribute>
                                    </libraryref>
                                </xsl:for-each>
                            </xsl:for-each>
                            <!-- Adding the Filterset -->
                            <xsl:for-each select="./libraryrefs">
                                <xsl:call-template name="addFilterSet"/>
                            </xsl:for-each>
                        </libraryrefs>
                    </xsl:if>
                    
                    <!-- Adding the Filterset -->
                    <xsl:call-template name="addFilterSet"/>
                    
                    <!--Applying the Default template to include any un processes Tags-->
                    <xsl:apply-templates/>
                </view>
                
            </xsl:for-each>
            <!-- Adding the Filterset -->
            <xsl:for-each select="//skin/views">
                <xsl:call-template name="addFilterSet"/>
            </xsl:for-each>
        </views>
    </xsl:template>
    
    <!--The Widgets template-->
    <!--For Populating all the widgets at the root level-->
    <xsl:template name="widgets">
        
        <widgetconfig>
            <xsl:for-each select="/skin/widgetconfig/widget">
                
                
                <widget>
                    <xsl:attribute name="name">
                        <xsl:value-of select="@name"/>
                    </xsl:attribute>
                    <xsl:attribute name="type">
                        <xsl:value-of select="@type"/>
                    </xsl:attribute>
                    <!--Including all the Scripts-->
                    
                    <xsl:variable name="scriptcontent">
                        <xsl:value-of select="scripts"/>
                    </xsl:variable>
                    <xsl:if test="$scriptcontent!=''">
                        <scripts>
                            
                            <!--Optimizing the scripts found here-->
                            <xsl:for-each select="scripts">
                                <xsl:for-each
                                    select="script[not(normalize-space(./text())= preceding-sibling::script/text() or ./text()= preceding-sibling::script/text())]">
                                    <script>
                                        <xsl:value-of select="."/>
                                    </script>
                                </xsl:for-each>
                            </xsl:for-each>
                            <!-- Adding the Filterset -->
                            <xsl:for-each select="./scripts">
                                <xsl:call-template name="addFilterSet"/>
                            </xsl:for-each>
                        </scripts>
                    </xsl:if>
                    
                    <!--Including all the Stylesheets-->
                    
                    <xsl:variable name="stylecontent">
                        <xsl:value-of select="stylesheets"/>
                    </xsl:variable>
                    <xsl:if test="$stylecontent!=''">
                        
                        <stylesheets>
                            
                            <!--Adding all the stylesheets found here-->
                            <xsl:for-each select="stylesheets">
                                <xsl:for-each
                                    select="stylesheet[not(normalize-space(./text())= preceding-sibling::stylesheet/text() or ./text()= preceding-sibling::stylesheet/text())]">
                                    <stylesheet>
                                        <xsl:value-of select="."/>
                                    </stylesheet>
                                </xsl:for-each>
                            </xsl:for-each>
                            
                            <!-- Adding the Filterset -->
                            <xsl:for-each select="./stylesheets">
                                <xsl:call-template name="addFilterSet"/>
                            </xsl:for-each>
                        </stylesheets>
                    </xsl:if>
                    
                    <!--Including all the template-->
                    <xsl:call-template name="optimizeSingularElement">
                        <xsl:with-param name="element" select="./template"/>
                        <xsl:with-param name="resultElementName" select="'template'"/>
                        <xsl:with-param name="addNameAttribute">
                            <xsl:value-of select="'false'"/>
                        </xsl:with-param>
                    </xsl:call-template>
                    
                    <!--Including all the data-->
                    <xsl:call-template name="optimizeSingularElement">
                        <xsl:with-param name="element" select="./data"/>
                        <xsl:with-param name="resultElementName" select="'data'"/>
                        <xsl:with-param name="addNameAttribute">
                            <xsl:value-of select="'false'"/>
                        </xsl:with-param>
                    </xsl:call-template>
                    
                    <!--Including all the Widgetrefs-->
                    
                    <xsl:variable name="widgetcontent">
                        <xsl:value-of select="widgetrefs/widgetref/@ref"/>
                    </xsl:variable>
                    <xsl:if test="$widgetcontent!=''">
                        <widgetrefs>
                            
                            <!--Adding all the widget refs found here-->
                            <xsl:for-each select="widgetrefs">
                                <xsl:for-each
                                    select="widgetref[not(./@ref = preceding-sibling::widgetref[@ref]/@ref)]">
                                    <widgetref>
                                        <xsl:attribute name="ref">
                                            <xsl:value-of
                                                select="@ref"/>
                                        </xsl:attribute>
                                    </widgetref>
                                </xsl:for-each>
                            </xsl:for-each>
                            <!-- Adding the Filterset -->
                            <xsl:for-each select="./widgetrefs">
                                <xsl:call-template name="addFilterSet"/>
                            </xsl:for-each>
                        </widgetrefs>
                    </xsl:if>
                    
                    <!--Including all the Libraryrefs-->
                    <xsl:variable name="libcontent">
                        <xsl:value-of select="libraryrefs/libraryref/@ref"/>
                    </xsl:variable>
                    <xsl:if test="$libcontent!=''">
                        <libraryrefs>
                            
                            <!--Adding all the Library refs found here-->
                            <xsl:for-each select="libraryrefs">
                                <xsl:for-each
                                    select="libraryref[not(./@ref = preceding-sibling::libraryref[@ref]/@ref)]">
                                    <libraryref>
                                        <xsl:attribute name="ref">
                                            <xsl:value-of
                                                select="@ref"/>
                                        </xsl:attribute>
                                    </libraryref>
                                </xsl:for-each>
                            </xsl:for-each>
                            
                            <!-- Adding the Filterset -->
                            <xsl:for-each select="./libraryrefs">
                                <xsl:call-template name="addFilterSet"/>
                            </xsl:for-each>
                            
                        </libraryrefs>
                    </xsl:if>
                    <!-- Adding the Filterset -->
                    <xsl:call-template name="addFilterSet"/>
                </widget>
                
            </xsl:for-each>
            <!-- Adding the Filterset -->
            <xsl:for-each select="//skin/widgetconfig">
                <xsl:call-template name="addFilterSet"/>
            </xsl:for-each>
        </widgetconfig>
    </xsl:template>
    
    
    <!--The Libraries template-->
    <!--For Populating all the Libraries at the root level-->
    <xsl:template name="libraries">
        <libraries>
            <xsl:for-each select="//library">
                <library>
                    <xsl:attribute name="name">
                        <xsl:value-of select="@name"/>
                    </xsl:attribute>
                    <xsl:attribute name="mergeAs">
                        <xsl:value-of select="@mergeAs"/>
                    </xsl:attribute>
                    <!--Including the Scripts-->
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
                    <!--Including the Stylesheets-->
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
            <!-- Adding the Filterset -->
            <xsl:for-each select="//skin/libraries">
                <xsl:call-template name="addFilterSet"/>
            </xsl:for-each>
        </libraries>
    </xsl:template>
    
    <!--
    Convienience template for adding a filterset
    -->
    
    <xsl:template name="addFilterSet">
        <xsl:for-each select="filterset[1]">
            <filterset>
                <xsl:for-each
                    select="filter[not(./@element=preceding-sibling::filter[@element]/@element and ./@removeby=preceding-sibling::filter[@removeby]/@removeby and ./@key=preceding-sibling::filter[@key]/@key)]">
                    <xsl:element name="filter">
                        <xsl:attribute name="element">
                            <xsl:value-of select="./@element"/>
                        </xsl:attribute>
                        <xsl:attribute name="removeby">
                            <xsl:value-of select="./@removeby"/>
                        </xsl:attribute>
                        <xsl:attribute name="key">
                            <xsl:value-of select="./@key"/>
                        </xsl:attribute>
                    </xsl:element>
                </xsl:for-each>
            </filterset>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template name="default" match="*">
        <xsl:for-each select=".">
            <xsl:variable name="elementName">
                <xsl:value-of select="name(.)"/>
            </xsl:variable>
            <xsl:if
                test="not(normalize-space(./text())=preceding-sibling::*[name()=name(current())]/text() or ./text()=preceding-sibling::*[name()=name(current())]/text())">
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