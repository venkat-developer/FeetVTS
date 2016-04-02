<?xml version="1.0"?>
<!--
1. The XSLT File for expanding the given XML File
-->

<!--Author : N.Balaji-->

<!--
Todo
1. Add Attributes of all the elements
-->
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
                <!--The First skin will be the current skin-->
                <xsl:value-of select="//skin[1]/@name"/>
            </xsl:attribute>
            <!--Generating the Views-->
            <xsl:call-template name="views">
                <xsl:with-param name="childSkinName">
                    <xsl:value-of select="//skin[1]/@name"/>
                </xsl:with-param>
            </xsl:call-template>
            <!--Generating the Widgets-->
            <xsl:call-template name="widgets">
                <xsl:with-param name="childSkinName">
                    <xsl:value-of select="//skin[1]/@name"/>
                </xsl:with-param>
            </xsl:call-template>
            <!--Generating the libraries-->
            <xsl:call-template name="libraries">
                <xsl:with-param name="childSkinName">
                    <xsl:value-of select="//skin[1]/@name"/>
                </xsl:with-param>
            </xsl:call-template>
            
            <!-- Include the filter list here -->
            <!--Applying the Default template to include any un processes Tags-->
            <xsl:for-each select="//skin">
                <xsl:apply-templates/>
            </xsl:for-each>
        </skin>
    </xsl:template>
    
    <!--The Views Template -->
    <!--For Populating all the widgets at the root level-->
    <xsl:template name="views">
        <xsl:param name="childSkinName"/>
        <views>
            
            <!--
            1. The following loop condition creates views with unique names ->
            The Logic within the loop "adds" any file in the depended views
            -->
            
            <xsl:for-each select="//view[not(./@name = preceding::view[@name]/@name)]">
                <xsl:variable name="firstviewname">
                    <xsl:value-of select="@name"/>
                </xsl:variable>
                
                <view>
                    <xsl:attribute name="name">
                        <xsl:value-of select="@name"/>
                    </xsl:attribute>
                    <xsl:attribute name="title">
                        <xsl:value-of select="@title"/>
                    </xsl:attribute>
                    
                    <scripts>
                        
                        <!--Adding all the scripts found in this view-->
                        <xsl:call-template name="getScripts">
                            <xsl:with-param name="prependedPath">
                                <xsl:value-of select="'com/i10n/fleet/views/'"/>
                                <xsl:value-of select="@name"/>
                                <xsl:value-of select="'/'"/>
                            </xsl:with-param>
                        </xsl:call-template>
                        
                        <!--Adding all the scripts found in the base views-->
                        <xsl:for-each select="following::view[./@name=$firstviewname]">
                            <!--Adding the script files-->
                            <xsl:call-template name="getScripts">
                                <xsl:with-param name="prependedPath">
                                    <xsl:value-of select="'com/i10n/fleet/views/'"/>
                                    <xsl:value-of select="@name"/>
                                    <xsl:value-of select="'/'"/>
                                </xsl:with-param>
                            </xsl:call-template>
                        </xsl:for-each>
                        
                        <!-- Adding filtersets -->
                        <filterset>
                            <!-- Adding the Filterset Found here-->
                            <xsl:for-each select="./scripts">
                                <xsl:call-template name="addFilterSet"/>
                            </xsl:for-each>
                            
                            <!-- Adding the filtersets found in the base views -->
                            <xsl:for-each select="following::view[./@name=$firstviewname]">
                                <xsl:for-each select="./scripts">
                                    <xsl:call-template name="addFilterSet"/>
                                </xsl:for-each>
                            </xsl:for-each>
                        </filterset>
                    </scripts>
                    
                    <!--Including all the Stylesheets-->
                    
                    <stylesheets>
                        
                        <!-- Adding stylesheets found in the base views -->
                        <xsl:for-each select="following::view[./@name=$firstviewname]">
                            <xsl:call-template name="getStylesheets">
                                <xsl:with-param name="prependedPath">
                                    <xsl:value-of select="'com/i10n/fleet/views/'"/>
                                    <xsl:value-of select="@name"/>
                                    <xsl:value-of select="'/'"/>
                                </xsl:with-param>
                            </xsl:call-template>
                        </xsl:for-each>
                        
                        <!--Adding any stylesheet that is found in this View-->
                        <xsl:call-template name="getStylesheets">
                            <xsl:with-param name="prependedPath">
                                <xsl:value-of select="'com/i10n/fleet/views/'"/>
                                <xsl:value-of select="@name"/>
                                <xsl:value-of select="'/'"/>
                            </xsl:with-param>
                        </xsl:call-template>
                        
                        <!-- Adding filtersets -->
                        <filterset>
                            <!-- Adding the Filterset Found here-->
                            <xsl:for-each select="./stylesheets">
                                <xsl:call-template name="addFilterSet"/>
                            </xsl:for-each>
                            
                            <!-- Adding the filtersets found in the base views -->
                            <xsl:for-each select="following::view[./@name=$firstviewname]">
                                <xsl:for-each select="./stylesheets">
                                    <xsl:call-template name="addFilterSet"/>
                                </xsl:for-each>
                            </xsl:for-each>
                        </filterset>
                    </stylesheets>
                    
                    <!--Adding the Templates-->
                    
                    <xsl:call-template name="addSingularElement">
                        <xsl:with-param name="element" select="./template"/>
                        <xsl:with-param name="resultElementName" select="'template'"/>
                        <xsl:with-param name="defaultValue">
                            <xsl:value-of select="'com/i10n/fleet/views/'"/>
                            <xsl:value-of select="@name"/>
                            <xsl:value-of select="'/'"/>
                            <xsl:value-of select="./@name"/>
                            <xsl:value-of select="'.ftm'"/>
                        </xsl:with-param>
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
                    
                    <!--Adding all the templates found in the base views-->
                    <xsl:for-each select="following::view[./@name=$firstviewname]">
                        <!--Adding any template that is found in this view-->
                        <xsl:call-template name="addSingularElement">
                            <xsl:with-param name="element" select="./template"/>
                            <xsl:with-param name="resultElementName" select="'template'"/>
                            <xsl:with-param name="defaultValue">
                                <xsl:value-of select="'com/i10n/fleet/views/'"/>
                                <xsl:value-of select="@name"/>
                                <xsl:value-of select="'/'"/>
                                <xsl:value-of select="./@name"/>
                                <xsl:value-of select="'.ftm'"/>
                            </xsl:with-param>
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
                    </xsl:for-each>
                    
                    <!--Adding Data-->
                    
                    <xsl:call-template name="addSingularElement">
                        <xsl:with-param name="element" select="./data"/>
                        <xsl:with-param name="resultElementName" select="'data'"/>
                        <xsl:with-param name="defaultValue">
                            <xsl:value-of select="'com/i10n/fleet/views/'"/>
                            <xsl:value-of select="@name"/>
                            <xsl:value-of select="'/'"/>
                            <xsl:value-of select="./@name"/>
                            <xsl:value-of select="'.ftd'"/>
                        </xsl:with-param>
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
                    
                    <!--Adding all the data found in the base views-->
                    <xsl:for-each select="following::view[./@name=$firstviewname]">
                        <!--Adding any template that is found in this view-->
                        <xsl:call-template name="addSingularElement">
                            <xsl:with-param name="element" select="./data"/>
                            <xsl:with-param name="resultElementName" select="'data'"/>
                            <xsl:with-param name="defaultValue">
                                <xsl:value-of select="'com/i10n/fleet/views/'"/>
                                <xsl:value-of select="@name"/>
                                <xsl:value-of select="'/'"/>
                                <xsl:value-of select="./@name"/>
                                <xsl:value-of select="'.ftd'"/>
                            </xsl:with-param>
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
                    </xsl:for-each>
                    
                    <!--Including all the Widgetrefs-->
                    
                    <!--Adding all the widget refs found here-->
                    <widgetrefs>
                        <xsl:for-each select="widgetrefs">
                            <xsl:for-each select="widgetref">
                                <widgetref>
                                    <xsl:attribute name="ref">
                                        <xsl:value-of
                                            select="@ref"/>
                                    </xsl:attribute>
                                </widgetref>
                            </xsl:for-each>
                        </xsl:for-each>
                        
                        <!--Adding the widgetrefs in the base views-->
                        <xsl:for-each select="following::view[./@name=$firstviewname]">
                            <xsl:for-each select="widgetrefs">
                                <xsl:for-each select="widgetref">
                                    <widgetref>
                                        <xsl:attribute name="ref">
                                            <xsl:value-of
                                                select="@ref"/>
                                        </xsl:attribute>
                                    </widgetref>
                                </xsl:for-each>
                            </xsl:for-each>
                        </xsl:for-each>
                        
                        <!-- Adding filtersets -->
                        <filterset>
                            <!-- Adding the Filterset Found here-->
                            <xsl:for-each select="./widgetrefs">
                                <xsl:call-template name="addFilterSet"/>
                            </xsl:for-each>
                            
                            <!-- Adding the filtersets found in the base views -->
                            <xsl:for-each select="following::view[./@name=$firstviewname]">
                                <xsl:for-each select="./widgetrefs">
                                    <xsl:call-template name="addFilterSet"/>
                                </xsl:for-each>
                            </xsl:for-each>
                        </filterset>
                    </widgetrefs>
                    
                    <!--Including all the Libraryrefs-->
                    
                    <!--Adding all the Library refs found here-->
                    <libraryrefs>
                        <xsl:for-each select="libraryrefs">
                            <xsl:for-each select="libraryref">
                                <libraryref>
                                    <xsl:attribute name="ref">
                                        <xsl:value-of
                                            select="@ref"/>
                                    </xsl:attribute>
                                </libraryref>
                            </xsl:for-each>
                        </xsl:for-each>
                        
                        <!--Adding the libraryrefs in the base views-->
                        <xsl:for-each select="following::view[./@name = $firstviewname]">
                            <xsl:for-each select="libraryrefs">
                                <xsl:for-each select="libraryref">
                                    <libraryref>
                                        <xsl:attribute name="ref">
                                            <xsl:value-of
                                                select="@ref"/>
                                        </xsl:attribute>
                                    </libraryref>
                                </xsl:for-each>
                            </xsl:for-each>
                        </xsl:for-each>
                        
                        <!-- Adding filtersets -->
                        <filterset>
                            <!-- Adding the Filterset Found here-->
                            <xsl:for-each select="./libraryrefs">
                                <xsl:call-template name="addFilterSet"/>
                            </xsl:for-each>
                            
                            <!-- Adding the filtersets found in the base views -->
                            <xsl:for-each select="following::view[./@name=$firstviewname]">
                                <xsl:for-each select="./libraryrefs">
                                    <xsl:call-template name="addFilterSet"/>
                                </xsl:for-each>
                            </xsl:for-each>
                        </filterset>
                    </libraryrefs>
                    
                    <!-- Adding filtersets -->
                    <filterset>
                        <!-- Adding the Filterset Found here-->
                        <xsl:call-template name="addFilterSet"/>
                        <!-- Adding the filtersets found in the base views -->
                        <xsl:for-each select="following::view[./@name=$firstviewname]">
                            <xsl:call-template name="addFilterSet"/>
                        </xsl:for-each>
                    </filterset>
                    
                    <!--Applying the Default template to include any un processes Tags-->
                    <xsl:apply-templates/>
                </view>
            </xsl:for-each>
            
            <!-- Adding filterset-->
            <filterset>
                <xsl:for-each select="//skin/views">
                    <xsl:call-template name="addFilterSet"/>
                </xsl:for-each>
            </filterset>
        </views>
    </xsl:template>
    
    <!--The Widgets template-->
    <!--For Populating all the widgets at the root level-->
    <xsl:template name="widgets">
        <xsl:param name="childSkinName"/>
        <xsl:variable name="lower">
            abcdefghijklmnopqrstuvwxyz
        </xsl:variable>
        <xsl:variable name="upper">
            ABCDEFGHIJKLMNOPQRSTUVWXYZ
        </xsl:variable>
        <widgetconfig>
            <xsl:for-each
                select="//widget[not(./@name = preceding::widget[@name]/@name)]">
                <xsl:variable name="firstwidgetname">
                    <xsl:value-of select="@name"/>
                </xsl:variable>
                
                <widget>
                    <xsl:attribute name="name">
                        <xsl:value-of select="@name"/>
                    </xsl:attribute>
                    <xsl:attribute name="type">
                        <xsl:value-of select="@type"/>
                    </xsl:attribute>
                    <!--Including all the Scripts-->
                    <scripts>
                        
                        <!--Adding any script that is found in this Widget-->
                        <xsl:call-template name="getScripts">
                            <xsl:with-param name="prependedPath">
                                <xsl:value-of select="'com/i10n/fleet/widgets/ui/'"/>
                                <xsl:value-of select="translate(@name,$upper,$lower)"/>
                                <xsl:value-of select="'/'"/>
                            </xsl:with-param>
                        </xsl:call-template>
                        
                        <!--Adding all the scripts found in the base Widgets-->
                        <xsl:for-each select="following::widget[./@name=$firstwidgetname]">
                            <!--Adding any script that is found this Widget-->
                            <xsl:call-template name="getScripts">
                                <xsl:with-param name="prependedPath">
                                    <xsl:value-of select="'com/i10n/fleet/widgets/ui/'"/>
                                    <xsl:value-of select="translate(@name,$upper,$lower)"/>
                                    <xsl:value-of select="'/'"/>
                                </xsl:with-param>
                            </xsl:call-template>
                        </xsl:for-each>
                        
                        <!-- Adding filtersets -->
                        <filterset>
                            <!-- Adding the Filterset Found here-->
                            <xsl:for-each select="./scripts">
                                <xsl:call-template name="addFilterSet"/>
                            </xsl:for-each>
                            
                            <!-- Adding the filtersets found in the base widgets -->
                            <xsl:for-each select="following::widget[./@name=$firstwidgetname]">
                                <xsl:for-each select="./scripts">
                                    <xsl:call-template name="addFilterSet"/>
                                </xsl:for-each>
                            </xsl:for-each>
                        </filterset>
                    </scripts>
                    
                    <!--Including all the Stylesheets-->
                    <stylesheets>
                        
                        <!-- Adding all the stylesheets in the base widgets -->
                        <xsl:for-each select="following::widget[./@name=$firstwidgetname]">
                            <xsl:call-template name="getStylesheets">
                                <xsl:with-param name="prependedPath">
                                    <xsl:value-of select="'com/i10n/fleet/widgets/ui/'"/>
                                    <xsl:value-of select="translate(@name,$upper,$lower)"/>
                                    <xsl:value-of select="'/'"/>
                                </xsl:with-param>
                            </xsl:call-template>
                        </xsl:for-each>
                        
                        <!--Adding any stylesheet that is found here-->
                        <xsl:call-template name="getStylesheets">
                            <xsl:with-param name="prependedPath">
                                <xsl:value-of select="'com/i10n/fleet/widgets/ui/'"/>
                                <xsl:value-of select="translate(@name,$upper,$lower)"/>
                                <xsl:value-of select="'/'"/>
                            </xsl:with-param>
                        </xsl:call-template>
                        
                        <!-- Adding filtersets -->
                        <filterset>
                            <!-- Adding the Filterset Found here-->
                            <xsl:for-each select="./stylesheets">
                                <xsl:call-template name="addFilterSet"/>
                            </xsl:for-each>
                            
                            <!-- Adding the filtersets found in the base views -->
                            <xsl:for-each select="following::widget[./@name=$firstwidgetname]">
                                <xsl:for-each select="./stylesheets">
                                    <xsl:call-template name="addFilterSet"/>
                                </xsl:for-each>
                            </xsl:for-each>
                        </filterset>
                    </stylesheets>
                    
                    <!--Including all the template-->
                    <!--Adding any template that is found here-->
                    <xsl:call-template name="addSingularElement">
                        <xsl:with-param name="element" select="./template"/>
                        <xsl:with-param name="resultElementName" select="'template'"/>
                        <xsl:with-param name="defaultValue">
                            <xsl:value-of select="'com/i10n/fleet/widgets/ui/'"/>
                            <xsl:value-of select="translate(@name,$upper,$lower)"/>
                            <xsl:value-of select="'/'"/>
                            <xsl:value-of select="./@name"/>
                            <xsl:value-of select="'.ftm'"/>
                        </xsl:with-param>
                        <xsl:with-param name="prependedString">
                            <xsl:value-of select="''"/>
                        </xsl:with-param>
                        <xsl:with-param name="appendedString">
                            <xsl:value-of select="'.ftm'"/>
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
                    
                    <!--Adding all the templates found in the base widgets-->
                    <xsl:for-each select="following::widget[./@name=$firstwidgetname]">
                        <!--Adding any template that is found in this Widget-->
                        <xsl:call-template name="addSingularElement">
                            <xsl:with-param name="element" select="./template"/>
                            <xsl:with-param name="resultElementName" select="'template'"/>
                            <xsl:with-param name="defaultValue">
                                <xsl:value-of select="'com/i10n/fleet/widgets/ui/'"/>
                                <xsl:value-of select="translate(@name,$upper,$lower)"/>
                                <xsl:value-of select="'/'"/>
                                <xsl:value-of select="./@name"/>
                                <xsl:value-of select="'.ftm'"/>
                            </xsl:with-param>
                            <xsl:with-param name="prependedString">
                                <xsl:value-of select="''"/>
                            </xsl:with-param>
                            <xsl:with-param name="appendedString">
                                <xsl:value-of select="'.ftm'"/>
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
                    </xsl:for-each>
                    
                    <!--Adding Data-->
                    
                    <xsl:call-template name="addSingularElement">
                        <xsl:with-param name="element" select="./data"/>
                        <xsl:with-param name="resultElementName" select="'data'"/>
                        <xsl:with-param name="defaultValue">
                            <xsl:value-of select="'com/i10n/fleet/widgets/ui/'"/>
                            <xsl:value-of select="translate(@name,$upper,$lower)"/>
                            <xsl:value-of select="'/'"/>
                            <xsl:value-of select="./@name"/>
                            <xsl:value-of select="'.ftd'"/>
                        </xsl:with-param>
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
                    
                    <!--Adding all the data found in the base views-->
                    <xsl:for-each select="following::widget[./@name=$firstwidgetname]">
                        <!--Adding any template that is found in this Widget-->
                        <xsl:call-template name="addSingularElement">
                            <xsl:with-param name="element" select="./data"/>
                            <xsl:with-param name="resultElementName" select="'data'"/>
                            <xsl:with-param name="defaultValue">
                                <xsl:value-of select="'com/i10n/fleet/widgets/ui/'"/>
                                <xsl:value-of select="translate(@name,$upper,$lower)"/>
                                <xsl:value-of select="'/'"/>
                                <xsl:value-of select="./@name"/>
                                <xsl:value-of select="'.ftd'"/>
                            </xsl:with-param>
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
                    </xsl:for-each>
                    
                    <!--Including all the Widgetrefs-->
                    <widgetrefs>
                        <!--Adding all the widget refs found here-->
                        <xsl:for-each select="widgetrefs">
                            <xsl:for-each select="widgetref">
                                <widgetref>
                                    <xsl:attribute name="ref">
                                        <xsl:value-of
                                            select="@ref"/>
                                    </xsl:attribute>
                                </widgetref>
                            </xsl:for-each>
                        </xsl:for-each>
                        
                        <!--Adding the widgetrefs in the base widgets-->
                        <xsl:for-each select="following::widget[./@name = $firstwidgetname]">
                            <xsl:for-each select="widgetrefs">
                                <xsl:for-each select="widgetref">
                                    <widgetref>
                                        <xsl:attribute name="ref">
                                            <xsl:value-of
                                                select="@ref"/>
                                        </xsl:attribute>
                                    </widgetref>
                                </xsl:for-each>
                            </xsl:for-each>
                        </xsl:for-each>
                        
                        <!-- Adding filtersets -->
                        <filterset>
                            <!-- Adding the Filterset Found here-->
                            <xsl:for-each select="./widgetrefs">
                                <xsl:call-template name="addFilterSet"/>
                            </xsl:for-each>
                            
                            <!-- Adding the filtersets found in the base views -->
                            <xsl:for-each select="following::widget[./@name=$firstwidgetname]">
                                <xsl:for-each select="./widgetrefs">
                                    <xsl:call-template name="addFilterSet"/>
                                </xsl:for-each>
                            </xsl:for-each>
                        </filterset>
                    </widgetrefs>
                    
                    <!--Including all the Libraryrefs-->
                    <libraryrefs>
                        
                        <!--Adding all the Library refs found here-->
                        <xsl:for-each select="libraryrefs">
                            <xsl:for-each select="libraryref">
                                <libraryref>
                                    <xsl:attribute name="ref">
                                        <xsl:value-of
                                            select="@ref"/>
                                    </xsl:attribute>
                                </libraryref>
                            </xsl:for-each>
                        </xsl:for-each>
                        
                        <!--Adding the libraryrefs in the base widgets-->
                        <xsl:for-each select="following::widget[./@name = $firstwidgetname]">
                            <xsl:for-each select="libraryrefs">
                                <xsl:for-each select="libraryref">
                                    <libraryref>
                                        <xsl:attribute name="ref">
                                            <xsl:value-of
                                                select="@ref"/>
                                        </xsl:attribute>
                                    </libraryref>
                                </xsl:for-each>
                            </xsl:for-each>
                        </xsl:for-each>
                        
                        <!-- Adding filtersets -->
                        <filterset>
                            <!-- Adding the Filterset Found here-->
                            <xsl:for-each select="./libraryrefs">
                                <xsl:call-template name="addFilterSet"/>
                            </xsl:for-each>
                            
                            <!-- Adding the filtersets found in the base views -->
                            <xsl:for-each select="following::widget[./@name=$firstwidgetname]">
                                <xsl:for-each select="./libraryrefs">
                                    <xsl:call-template name="addFilterSet"/>
                                </xsl:for-each>
                            </xsl:for-each>
                        </filterset>
                    </libraryrefs>
                    
                    <!-- Adding filtersets -->
                    <filterset>
                        <!-- Adding the Filterset Found here-->
                        <xsl:call-template name="addFilterSet"/>
                        
                        <!-- Adding the filtersets found in the base views -->
                        <xsl:for-each select="following::widget[./@name=$firstwidgetname]">
                            <xsl:call-template name="addFilterSet"/>
                        </xsl:for-each>
                    </filterset>
                </widget>
            </xsl:for-each>
            
            <!-- Adding the Filterset -->
            <filterset>
                <xsl:for-each select="//skin/widgetconfig">
                    <xsl:call-template name="addFilterSet"/>
                </xsl:for-each>
            </filterset>
        </widgetconfig>
    </xsl:template>
    
    <!--
    1. Convienience Template For extracting scripts and adding the default
    scripts of the view or widget entites 2. The following is the pattern
    for the default scripts added for the entities <path
    supplied><entityname><.js> 3. Makes no assumpitions about the path -
    so Path argument is a must
    -->
    <xsl:template name="getScripts">
        <xsl:param name="prependedPath"/>
        <!--Adding any script that is found here-->
        <xsl:for-each select="./scripts/script">
            <script>
                <xsl:value-of select="normalize-space(.)"/>
            </script>
        </xsl:for-each>
        
        <!--Adding the default script-->
        <script>
            <xsl:value-of select="$prependedPath"/>
            <xsl:value-of select="./@name"/>
            <xsl:value-of select="'.js'"/>
        </script>
    </xsl:template>
    
    <!--
    1. Convienience Template For extracting stylesheets and adding the
    default stylesheets of the view or widget entites 2. The following is
    the pattern for the default stylesheets added for the entities <path
    supplied><entityname><.css> 3. Makes no assumpitions about the path -
    so Path argument is a must
    -->
    <xsl:template name="getStylesheets">
        <xsl:param name="prependedPath"/>
        
        <!--Adding any stylesheet that is found here-->
        <xsl:for-each select="./stylesheets/stylesheet">
            <stylesheet>
                <xsl:value-of select="normalize-space(.)"/>
            </stylesheet>
        </xsl:for-each>
        
        <!--Adding the default stylesheet-->
        <stylesheet>
            <xsl:value-of select="$prependedPath"/>
            <xsl:value-of select="./@name"/>
            <xsl:value-of select="'.css'"/>
        </stylesheet>
    </xsl:template>
    
    
    <!--The Libraries template-->
    <!--For Populating all the Libraries at the root level-->
    <xsl:template name="libraries">
        <xsl:param name="childSkinName"/>
        <libraries>
            <xsl:for-each
                select="//library[not(./@name = preceding::library[@name]/@name)]">
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
                                    <xsl:variable name="scriptFile">
                                        <xsl:value-of select="normalize-space(.)"/>
                                    </xsl:variable>
                                    <xsl:attribute name="merge">
                                        <xsl:value-of select="@merge"/>
                                    </xsl:attribute>
                                    <xsl:attribute name="min">
                                        <xsl:value-of select="@min"/>
                                    </xsl:attribute>
                                    <xsl:if test="not(starts-with($scriptFile,'http://'))">
                                    <xsl:value-of select="'@STATIC_DIR@/'"/>
                                    </xsl:if>
                                    <xsl:value-of select="$scriptFile"/>
                                </script>
                            </xsl:for-each>
                        </scripts>
                    </xsl:for-each>
                    <xsl:for-each select="stylesheets">
                        <stylesheets>
                            <xsl:for-each select="stylesheet">
                                <stylesheet>
                                    <xsl:value-of select="'@STATIC_DIR@/'"/>
                                    <xsl:value-of select="normalize-space(.)"/>
                                </stylesheet>
                            </xsl:for-each>
                        </stylesheets>
                    </xsl:for-each>
                </library>
            </xsl:for-each>
            <!-- Adding the Filterset -->
            <filterset>
                <xsl:for-each select="//skin/libraries">
                    <xsl:call-template name="addFilterSet"/>
                </xsl:for-each>
            </filterset>
        </libraries>
    </xsl:template>
    
    <!--
    Convienience template for adding a filterset
    -->
    
    <xsl:template name="addFilterSet">
        <xsl:for-each select="filterset[1]">
            <xsl:for-each select="filter">
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