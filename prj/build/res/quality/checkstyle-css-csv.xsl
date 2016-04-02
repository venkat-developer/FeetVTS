<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="text" version="1.0" encoding="UTF-8" indent="yes" omit-xml-declaration="no"/>

	<xsl:template match="message">
		<xsl:variable name="scope" select="ancestor::scope[1]"/>
		
		<xsl:value-of select="@level"/>
		<xsl:text>	</xsl:text>

		<xsl:value-of select="."/>
		<xsl:text>	</xsl:text>

		<xsl:if test="./@format">
			<xsl:value-of select="@format"/>
			<xsl:text>	</xsl:text>
		</xsl:if>

		<xsl:value-of select="$scope/@selector"/>
		<xsl:text>	</xsl:text>

		<xsl:value-of select="$scope/@media"/>
		<xsl:text>	</xsl:text>

		<xsl:value-of select="substring-after($scope/@css, 'BrandSites/')"/>
		<xsl:text>	</xsl:text>

		<xsl:value-of select="$scope/@skin"/>
		<xsl:text>
</xsl:text>
	</xsl:template>
	
	<xsl:template match="@*|text()"/>
</xsl:stylesheet>
