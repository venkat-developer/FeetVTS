<?xml version="1.0"?>
<!-- 1. The XSLT File for expanding the given XML File -->
<!--Author : N.Balaji-->

<!--TODO: 1. Remove Brs -->

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
            
            <!--Applying the Default template to include any un processes Tags-->
            <xsl:for-each select="//skin">
                <xsl:apply-templates/>
            </xsl:for-each>
        </skin>
    </xsl:template>
    
    <!--The Views Template - populates the views -->
    
    <xsl:template name="views">
        <xsl:for-each select="/skin/views">
            <views>
                <xsl:call-template name="view">
                </xsl:call-template>
            </views>
        </xsl:for-each>
    </xsl:template>
    
    <!--The View Template - Populates each view-->
    <!--Recursively resolves the necessary entities -->
    <!--Populates-->
    <!--1. Scripts = (js) files-->
    <!--2. Stylesheets-->
    <!--3. Template-->
    <!--4. Widgets-->
    
    <xsl:template name="view">
        <xsl:for-each select="/skin/views/view">
            <view>
                <xsl:attribute name="name">
                    <xsl:value-of select='@name'/>
                </xsl:attribute>
                <xsl:attribute name="title">
                    <xsl:value-of select='@title'/>
                </xsl:attribute>
                
                
                <!--Adding the Dependent Libraries as library Elements-->
                <libraries>
                    <!--Adding the Base Library Files-->
                    <xsl:call-template name="extractlibraries">
                        <xsl:with-param name="LibraryName">
                            <xsl:value-of select="'base'"/>
                        </xsl:with-param>
                    </xsl:call-template>
                    
                    <!--Adding all the  Libraires Found Here-->
                    <xsl:for-each select="./libraryrefs">
                        <xsl:for-each select="libraryref">
                            <!--Resolving the Libraries-->
                            <xsl:call-template name="extractlibraries">
                                <xsl:with-param name="LibraryName">
                                    <xsl:value-of select="@ref"/>
                                </xsl:with-param>
                            </xsl:call-template>
                        </xsl:for-each>
                    </xsl:for-each>
                    
                    <!--Adding all the  Libraires Found in the dependencies-->
                    <!--Resolving the Libraries-->
                    <xsl:for-each select="widgetrefs">
                        <xsl:for-each select="widgetref">
                            <xsl:call-template name="deReferenceWidgetLibraries">
                                <xsl:with-param name="WidgetName">
                                    <xsl:value-of select="@ref"/>
                                </xsl:with-param>
                                <xsl:with-param name="returnElement">
                                    <xsl:value-of select="'library'"/>
                                </xsl:with-param>
                            </xsl:call-template>
                        </xsl:for-each>
                    </xsl:for-each>
                </libraries>
                
                <!--Adding the Script Files-->
                
                <scripts>
                    <!--Adding all scripts found in the Dependencies-->
                    <!--Resolve the Dependencies of each of the Widget Refs recursively-->
                    <xsl:for-each select="widgetrefs">
                        <xsl:for-each select="widgetref">
                            <xsl:call-template name="deReferenceWidgetScripts">
                                <xsl:with-param name="WidgetName">
                                    <xsl:value-of select="@ref"/>
                                </xsl:with-param>
                            </xsl:call-template>
                        </xsl:for-each>
                    </xsl:for-each>
                    
                    
                    <!--Adding any script that is found here-->
                    <xsl:for-each select="scripts">
                        <xsl:for-each select="script">
                            <xsl:variable name="content">
                                <xsl:value-of select="."/>
                            </xsl:variable>
                            <xsl:if test="normalize-space($content)!=''">
                                <script>
                                    <xsl:value-of select="."/>
                                </script>
                            </xsl:if>
                        </xsl:for-each>
                    </xsl:for-each>
                </scripts>
                
                <stylesheets>
                    
                    <!--Adding all the stylesheets for in the dependencies-->
                    <xsl:for-each select="widgetrefs">
                        <xsl:for-each select="widgetref">
                            <xsl:call-template name="deReferenceWidgetStylesheets">
                                <xsl:with-param name="WidgetName">
                                    <xsl:value-of select="@ref"/>
                                </xsl:with-param>
                            </xsl:call-template>
                        </xsl:for-each>
                    </xsl:for-each>
                    
                    <!--Adding any stylesheet found here-->
                    <xsl:for-each select="stylesheets">
                        <xsl:for-each select="stylesheet">
                            <xsl:variable name="content">
                                <xsl:value-of select="."/>
                            </xsl:variable>
                            <xsl:if test="normalize-space($content)!=''">
                                <stylesheet>
                                    <xsl:value-of select="."/>
                                </stylesheet>
                            </xsl:if>
                        </xsl:for-each>
                    </xsl:for-each>
                </stylesheets>
                
                <!--Adding Any Template that is found here-->
                <xsl:call-template name="addSingularElement">
                    <xsl:with-param name="element" select="./template"/>
                    <xsl:with-param name="resultElementName" select="'template'"/>
                    <xsl:with-param name="prependedString">
                        <xsl:value-of select="''"/>
                    </xsl:with-param>
                    <xsl:with-param name="appendedString">
                        <xsl:value-of select="''"/>
                    </xsl:with-param>
                    <xsl:with-param name="applyPrefixToDefault">
                        <xsl:value-of select="'false'"/>
                    </xsl:with-param>
                    <xsl:with-param name="applySuffixToDefault">
                        <xsl:value-of select="'false'"/>
                    </xsl:with-param>
                    <xsl:with-param name="addNameAttribute">
                        <xsl:value-of select="'false'"/>
                    </xsl:with-param>
                </xsl:call-template>
                
                <!--Adding Any data that is found here-->
                <xsl:call-template name="addSingularElement">
                    <xsl:with-param name="element" select="./data"/>
                    <xsl:with-param name="resultElementName" select="'data'"/>
                    <xsl:with-param name="prependedString">
                        <xsl:value-of select="''"/>
                    </xsl:with-param>
                    <xsl:with-param name="appendedString">
                        <xsl:value-of select="''"/>
                    </xsl:with-param>
                    <xsl:with-param name="applyPrefixToDefault">
                        <xsl:value-of select="'false'"/>
                    </xsl:with-param>
                    <xsl:with-param name="applySuffixToDefault">
                        <xsl:value-of select="'false'"/>
                    </xsl:with-param>
                    <xsl:with-param name="addNameAttribute">
                        <xsl:value-of select="'false'"/>
                    </xsl:with-param>
                </xsl:call-template>
                
                <widgets>
                    <!--Resolve the Widgets with Dependencies-->
                    <xsl:for-each select="widgetrefs">
                        <xsl:for-each select="widgetref">
                            <xsl:call-template name="deReferenceWidgets">
                                <xsl:with-param name="WidgetName">
                                    <xsl:value-of select="@ref"/>
                                </xsl:with-param>
                            </xsl:call-template>
                        </xsl:for-each>
                    </xsl:for-each>
                </widgets>
                
                <!--Applying the Default template to include any un processes Tags-->
                <xsl:apply-templates/>
            </view>
        </xsl:for-each>
    </xsl:template>
    
    <!--Template for dereferencing widgets-->
    <xsl:template name="deReferenceWidgets">
        <xsl:param name="WidgetName"/>
        <!--Selecting the Widget-->
        <xsl:for-each select="//widgetconfig/widget">
            <xsl:if test="$WidgetName=@name">
                <xsl:call-template name="widget">
                    <xsl:with-param name="WidgetName">
                        <xsl:value-of select="$WidgetName"/>
                    </xsl:with-param>
                    <xsl:with-param name="Recurse" select="'true'"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    
    <!--Template for dereferencing libraries of Widgets inside Views-->
    <xsl:template name="deReferenceWidgetLibraries">
        <xsl:param name="WidgetName"/>
        <xsl:param name="returnElement"/>
        <!--Selecting the Widget-->
        <xsl:for-each select="//widgetconfig/widget">
            <xsl:if test="$WidgetName=@name">
                <xsl:call-template name="extractwidgetlibraries">
                    <xsl:with-param name="Recurse" select="'true'"/>
                    <xsl:with-param name="returnElement">
                        <xsl:value-of select="$returnElement"/>
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    
    <!--Template for dereferencing scripts of Widgets inside Views-->
    <xsl:template name="deReferenceWidgetScripts">
        <xsl:param name="WidgetName"/>
        <!--Selecting the Widget-->
        <xsl:for-each select="//widgetconfig/widget">
            <xsl:if test="$WidgetName=@name">
                <xsl:call-template name="extractwidgetscripts">
                    <xsl:with-param name="Recurse" select="'true'"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    
    <!--Template for dereferencing stylesheets of Widgets inside Views-->
    <xsl:template name="deReferenceWidgetStylesheets">
        <xsl:param name="WidgetName"/>
        <!--Selecting the Widget-->
        <xsl:for-each select="//widgetconfig/widget">
            <xsl:if test="$WidgetName=@name">
                <xsl:call-template name="extractwidgetstyles">
                    <xsl:with-param name="Recurse" select="'true'"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    
    <!--The Widgets template-->
    <!--For Populating all the widgets at the root level-->
    <xsl:template name="widgets">
        <widgets>
            <xsl:for-each select="/skin/widgetconfig/widget">
                <xsl:call-template name="widget">
                    <xsl:with-param name="WidgetName">
                        <xsl:value-of select="@name"/>
                    </xsl:with-param>
                    <xsl:with-param name="Recurse" select="'false'"/>
                </xsl:call-template>
            </xsl:for-each>
        </widgets>
    </xsl:template>
    
    
    <!--The Widget Template-->
    <!--Recursively resolves necessary entities-->
    <!--Populates a widget with-->
    <!--1. Scripts = (js) files-->
    <!--2. Stylesheets-->
    <!--3. Template-->
    <!--
    @Param recurse :- set to true to create a seperate widget element for
    each of the widgetrefs within a widget :- Used while populating the
    Views
    -->
    
    <xsl:template name="widget">
        <xsl:param name="WidgetName"/>
        <xsl:param name="Recurse"/>
        
        <widget>
            <xsl:attribute name="name">
                <xsl:value-of select='$WidgetName'/>
            </xsl:attribute>
            <xsl:attribute name="type">
                <xsl:value-of select="'ui'"/>
            </xsl:attribute>
            
            <!--Call the Scripts Template with resolution-->
            <scripts>
                
                <!-- Including all the Libraries-->
                <xsl:call-template name="deReferenceWidgetLibraries">
                    <xsl:with-param name="WidgetName">
                        <xsl:value-of select="$WidgetName"/>
                    </xsl:with-param>
                    <xsl:with-param name="returnElement">
                        <xsl:value-of select="'script'"/>
                    </xsl:with-param>
                </xsl:call-template>
                
                <!--Including the script files-->
                <xsl:call-template name="deReferenceWidgetScripts">
                    <xsl:with-param name="WidgetName">
                        <xsl:value-of select="$WidgetName"/>
                    </xsl:with-param>
                </xsl:call-template>
            </scripts>
            
            <!--Call the stylesheet Template with resolution-->
            <stylesheets>
                
                <!-- Including all the Libraries-->
                <xsl:call-template name="deReferenceWidgetLibraries">
                    <xsl:with-param name="WidgetName">
                        <xsl:value-of select="$WidgetName"/>
                    </xsl:with-param>
                    <xsl:with-param name="returnElement">
                        <xsl:value-of select="'stylesheet'"/>
                    </xsl:with-param>
                </xsl:call-template>
                
                <xsl:call-template name="deReferenceWidgetStylesheets">
                    <xsl:with-param name="WidgetName">
                        <xsl:value-of select="$WidgetName"/>
                    </xsl:with-param>
                    <xsl:with-param name="Recurse" select="'true'"/>
                </xsl:call-template>
            </stylesheets>
            
            <!--Adding Templates-->
            <xsl:call-template name="addSingularElement">
                <xsl:with-param name="element" select="./template"/>
                <xsl:with-param name="resultElementName" select="'template'"/>
                <xsl:with-param name="prependedString">
                    <xsl:value-of select="''"/>
                </xsl:with-param>
                <xsl:with-param name="appendedString">
                    <xsl:value-of select="''"/>
                </xsl:with-param>
                <xsl:with-param name="applyPrefixToDefault">
                    <xsl:value-of select="'false'"/>
                </xsl:with-param>
                <xsl:with-param name="applySuffixToDefault">
                    <xsl:value-of select="'false'"/>
                </xsl:with-param>
                <xsl:with-param name="addNameAttribute">
                    <xsl:value-of select="'false'"/>
                </xsl:with-param>
            </xsl:call-template>
            
            <!--Adding Any data that is found here-->
            <xsl:call-template name="addSingularElement">
                <xsl:with-param name="element" select="./data"/>
                <xsl:with-param name="resultElementName" select="'data'"/>
                <xsl:with-param name="prependedString">
                    <xsl:value-of select="''"/>
                </xsl:with-param>
                <xsl:with-param name="appendedString">
                    <xsl:value-of select="''"/>
                </xsl:with-param>
                <xsl:with-param name="applyPrefixToDefault">
                    <xsl:value-of select="'false'"/>
                </xsl:with-param>
                <xsl:with-param name="applySuffixToDefault">
                    <xsl:value-of select="'false'"/>
                </xsl:with-param>
                <xsl:with-param name="addNameAttribute">
                    <xsl:value-of select="'false'"/>
                </xsl:with-param>
            </xsl:call-template>
            
        </widget>
        
        <!--Resolving Dependent Widgets-->
        <xsl:if test="$Recurse='true'">
            <!--Select the reffered library files-->
            <xsl:for-each select="./widgetrefs">
                <xsl:for-each select="widgetref">
                    <xsl:call-template name="deReferenceWidgets">
                        <xsl:with-param name="WidgetName">
                            <xsl:value-of select="@ref"/>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:for-each>
            </xsl:for-each>
        </xsl:if>
    </xsl:template>
    
    <!--
    Convienience method for extracting all the script files recursively
    -->
    
    <xsl:template name="extractwidgetscripts">
        
        <xsl:param name="WidgetName"/>
        <xsl:param name="Recurse"/>
        
        <!--Also Extracts the Scripts of all of the depencies-->
        <!--Send the Name of this widget as a parameter-->
        <!--Select the reffered script files-->
        <xsl:if test="$Recurse='true'">
            <xsl:for-each select="./widgetrefs">
                <xsl:for-each select="widgetref">
                    <xsl:call-template name="deReferenceWidgetScripts">
                        <xsl:with-param name="WidgetName">
                            <xsl:value-of select="@ref"/>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:for-each>
            </xsl:for-each>
        </xsl:if>
        
        <!--Select the Script files-->
        <xsl:for-each select="./scripts">
            <xsl:for-each select="script">
                <xsl:variable name="content">
                    <xsl:value-of select="."/>
                </xsl:variable>
                <xsl:if test="normalize-space($content)!=''">
                    <script>
                        <xsl:value-of select="."/>
                    </script>
                </xsl:if>
            </xsl:for-each>
        </xsl:for-each>
        
    </xsl:template>
    
    
    <!--
    Convienience method for extracting all the Library files as a Library
    Elements or Script elements recursively
    -->
    
    <xsl:template name="extractwidgetlibraries">
        
        <xsl:param name="WidgetName"/>
        <xsl:param name="Recurse"/>
        <xsl:param name="returnElement"/>
        
        <!--Also Extracts the Scripts of all of the depencies-->
        <!--Send the Name of this widget as a parameter-->
        <!--Select the reffered library files-->
        <xsl:if test="$Recurse='true'">
            <xsl:for-each select="./widgetrefs">
                <xsl:for-each select="widgetref">
                    <xsl:call-template name="deReferenceWidgetLibraries">
                        <xsl:with-param name="WidgetName">
                            <xsl:value-of select="@ref"/>
                        </xsl:with-param>
                        <xsl:with-param name="returnElement">
                            <xsl:value-of select="$returnElement"/>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:for-each>
            </xsl:for-each>
        </xsl:if>
        
        <!--Adding the Base Library Files-->
        <xsl:if test="$returnElement='library'">
            <xsl:call-template name="extractlibraries">
                <xsl:with-param name="LibraryName">
                    <xsl:value-of select="'base'"/>
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
        <xsl:if test="$returnElement='script'">
            <xsl:call-template name="extractlibraryScripts">
                <xsl:with-param name="LibraryName">
                    <xsl:value-of select="'base'"/>
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
        <xsl:if test="$returnElement='stylesheet'">
            <xsl:call-template name="extractlibraryStylesheets">
                <xsl:with-param name="LibraryName">
                    <xsl:value-of select="'base'"/>
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
        
        
        <!--Adding all the Dependent Libraires-->
        <xsl:for-each select="./libraryrefs">
            <xsl:for-each select="libraryref">
                <!--Resolving the Libraries-->
                <xsl:if test="$returnElement='library'">
                    <xsl:call-template name="extractlibraries">
                        <xsl:with-param name="LibraryName">
                            <xsl:value-of select="@ref"/>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:if>
                <xsl:if test="$returnElement='script'">
                    <xsl:call-template name="extractlibraryScripts">
                        <xsl:with-param name="LibraryName">
                            <xsl:value-of select="@ref"/>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:if>
                <xsl:if test="$returnElement='stylesheet'">
                    <xsl:call-template name="extractlibraryStylesheets">
                        <xsl:with-param name="LibraryName">
                            <xsl:value-of select="@ref"/>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:if>
            </xsl:for-each>
        </xsl:for-each>
        
    </xsl:template>
    
    <!--
    Convienience method for extracting all the style files recursively
    -->
    
    <xsl:template name="extractwidgetstyles">
        
        <xsl:param name="WidgetName"/>
        <xsl:param name="Recurse"/>
        
        <!--Adding the Default Style Sheet-->
        <!--Also extracts the stylesheets of the dependencies-->
        <!--Send the Name of the Widget as a parameter-->
        <!--Select the reffered stylesheet files-->
        <xsl:if test="$Recurse='true'">
            <!--Recurse through Dependencies-->
            <xsl:for-each select="./widgetrefs">
                <xsl:for-each select="widgetref">
                    <xsl:call-template name="deReferenceWidgetStylesheets">
                        <xsl:with-param name="WidgetName">
                            <xsl:value-of select="@ref"/>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:for-each>
            </xsl:for-each>
        </xsl:if>
        
        <!--Select the StyleSheet files-->
        <xsl:for-each select="./stylesheets">
            <xsl:for-each select="stylesheet">
                <xsl:variable name="content">
                    <xsl:value-of select="."/>
                </xsl:variable>
                <xsl:if test="normalize-space($content)!=''">
                    <stylesheet>
                        <xsl:value-of select="."/>
                    </stylesheet>
                </xsl:if>
            </xsl:for-each>
        </xsl:for-each>
        
    </xsl:template>
    
    <!--
    Convienience method for extracting all the library files : returns
    Library Elements
    -->
    
    <xsl:template name="extractlibraries">
        <!--Todo : Remove brs-->
        <xsl:param name="LibraryName"/>
        <!--Also Extracts the Templates of the Dependencies-->
        <!--Send the Name of the widget as a parameter-->
        <xsl:for-each select="/skin/libraries/library">
            <xsl:if test="$LibraryName=@name">
                <library>
                    <xsl:attribute name="name">
                        <xsl:value-of select='@name'/>
                    </xsl:attribute>
                    <xsl:attribute name="mergeAs">
                        <xsl:value-of select="@mergeAs"/>
                    </xsl:attribute>
                    <xsl:for-each select="scripts">
                        <scripts>
                            <xsl:for-each select="script">
                                <xsl:variable name="content">
                                    <xsl:value-of select="."/>
                                </xsl:variable>
                                <xsl:if test="normalize-space($content)!=''">
                                    <script>
                                        <xsl:attribute name="merge">
                                            <xsl:value-of select="@merge"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="min">
                                            <xsl:value-of select="@min"/>
                                        </xsl:attribute>
                                        <xsl:value-of select="."/>
                                    </script>
                                </xsl:if>
                            </xsl:for-each>
                        </scripts>
                    </xsl:for-each>
                    <xsl:for-each select="stylesheets">
                        <stylesheets>
                            <xsl:for-each select="stylesheet">
                                <xsl:variable name="content">
                                    <xsl:value-of select="."/>
                                </xsl:variable>
                                <xsl:if test="normalize-space($content)!=''">
                                    <stylesheet>
                                        <xsl:value-of select="."/>
                                    </stylesheet>
                                </xsl:if>
                            </xsl:for-each>
                        </stylesheets>
                    </xsl:for-each>
                </library>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    
    
    <!--
    Convienience method for extracting all the global library script files
    : returns set of script files
    -->
    
    <xsl:template name="extractlibraryScripts">
        <!--Todo : Remove brs-->
        <xsl:param name="LibraryName"/>
        <!--Also Extracts the Templates of the Dependencies-->
        <!--Send the Name of the widget as a parameter-->
        <xsl:for-each select="/skin/libraries/library">
            <xsl:if test="$LibraryName=@name">
                <xsl:for-each select="scripts">
                    <xsl:for-each select="script">
                        <xsl:variable name="content">
                            <xsl:value-of select="."/>
                        </xsl:variable>
                        <xsl:if test="normalize-space($content)!=''">
                            <script>
                                <xsl:value-of select="."/>
                            </script>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:for-each>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    
    <!--
    Convienience method for extracting all the global library stylesheet
    files : returns set of stylesheet files
    -->
    
    <xsl:template name="extractlibraryStylesheets">
        <!--Todo : Remove brs-->
        <xsl:param name="LibraryName"/>
        <!--Also Extracts the Templates of the Dependencies-->
        <!--Send the Name of the widget as a parameter-->
        <xsl:for-each select="/skin/libraries/library">
            <xsl:if test="$LibraryName=@name">
                <xsl:for-each select="stylesheets">
                    <xsl:for-each select="stylesheet">
                        <xsl:variable name="content">
                            <xsl:value-of select="."/>
                        </xsl:variable>
                        <xsl:if test="normalize-space($content)!=''">
                            <stylesheet>
                                <xsl:value-of select="."/>
                            </stylesheet>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:for-each>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template name="default" match="*">
        <xsl:for-each select=".">
            <xsl:variable name="elementName">
                <xsl:value-of select="name(.)"/>
            </xsl:variable>
            <xsl:if test="./@autoInclude='true'">
                <xsl:element name="{$elementName}">
                    <xsl:attribute name="autoInclude">
                        <xsl:value-of select="./@autoInclude"/>
                    </xsl:attribute>
                    <xsl:value-of select="normalize-space(.)"/>
                </xsl:element>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>