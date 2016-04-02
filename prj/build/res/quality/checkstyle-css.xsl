<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" omit-xml-declaration="no"/>

	<xsl:template match="/">
		<html>
			<head>
				<title>CSS Checkstyle Results</title>
				<link rel="stylesheet" type="text/css" href="checkstyle-css.css"/>
			</head>
			<body>
				<h1>CSS Checkstyle Results</h1>
				<div class="quick-links">
					<xsl:apply-templates select="results" mode="quick-links"/>
				</div>
				<div class="messages">
					<xsl:apply-templates select="*"/>
				</div>
			</body>
		</html>
	</xsl:template>	

	<xsl:template match="results" mode="quick-links">
		<ul>
			<xsl:apply-templates select="scope/scope" mode="quick-links"/>
		</ul>
	</xsl:template>
	
	<xsl:template match="scope" mode="quick-links">
		<li><a><xsl:attribute name="href"><xsl:text>#</xsl:text><xsl:value-of select="translate(@css, '/', '-')"/></xsl:attribute><xsl:value-of select="substring-after(@css, 'BrandSites/prj/')"/></a></li>
	</xsl:template>
	
	<xsl:template match="scope">
		<ul>
			<xsl:if test="@skin and not(@css)">
				<xsl:attribute name="class">scope skin</xsl:attribute>
				
				<a><xsl:attribute name="name"><xsl:value-of select="@skin"/></xsl:attribute></a>
				<li class="scope skin"><xsl:value-of select="@skin"/></li>
			</xsl:if>
			<xsl:if test="@css and not(@media) and not(@selector)">
				<xsl:attribute name="class">scope css</xsl:attribute>
				
				<a><xsl:attribute name="name"><xsl:value-of select="translate(@css, '/', '-')"/></xsl:attribute></a>
				<li class="scope css"><xsl:value-of select="substring-after(@css, 'BrandSites/prj/')"/></li>
			</xsl:if>
			<xsl:if test="@media and not(@selector)">
				<xsl:attribute name="class">scope media</xsl:attribute>
				<li class="scope media"><xsl:text>@media ( </xsl:text><xsl:value-of select="@media"/><xsl:text> ) </xsl:text></li>
			</xsl:if>
			<xsl:if test="@selector">
				<xsl:attribute name="class">scope selector</xsl:attribute>
				<li class="scope selector"><xsl:value-of select="@selector"/></li>
			</xsl:if>
			
			<xsl:if test="message">
				<li>
					<ul class="messages">
						<xsl:apply-templates select="message"/>
					</ul>
				</li>
			</xsl:if>
			<li>
				<xsl:apply-templates select="scope"/>
			</li>
		</ul>
	</xsl:template>
	
	<xsl:template match="message">
		<li><xsl:attribute name="class">message <xsl:value-of select="@level"/></xsl:attribute><span class="indicator">X</span><span class="text"><xsl:value-of select="."/></span></li>
	</xsl:template>
</xsl:stylesheet>
