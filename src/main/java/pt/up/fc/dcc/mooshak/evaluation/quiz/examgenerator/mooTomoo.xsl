<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
	xmlns:q="http://mooshak.dcc.fc.up.pt/quiz" 
	xmlns:h="http://www.w3.org/1999/xhtml" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
                version="2.0">

	<xsl:output method="xml" encoding="UTF-8" />	
	

 <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
</xsl:template>
    
 
 <xsl:template match="q:config">

	<xsl:element name="{name()}">
        <xsl:copy-of select="@*" />
       
     </xsl:element>


</xsl:template>      
    
<xsl:template match="q:group">

     <xsl:element name="{name()}">
        <xsl:copy-of select="@*" />
    
	   	<xsl:choose>
	   		<xsl:when test="@shuffle = true()">
	   			<xsl:call-template name="pick-random-item">
	          	<xsl:with-param name="items" select="q:QA"/>
	           	<xsl:with-param name="val" select="@numberQuestion"/>
	      		</xsl:call-template>
	   		</xsl:when>
	   		
	   		<xsl:otherwise>
	   			<xsl:apply-templates select="q:QA"/>
	   		</xsl:otherwise>
	   	</xsl:choose>
       
     </xsl:element>
      

</xsl:template>
        
        
        
        
<xsl:template match="q:QA">

	<!--xsl:copy>
      <xsl:call-template name="pick-random-item">
          <xsl:with-param name="items" select="q:QA"/>
      </xsl:call-template>
   </xsl:copy-->
   
      <!-- xsl:for-each select="q:QA[position()&lt;@numberQuestion]">
        <xsl:copy-of select="."/>
      </xsl:for-each-->
      
       <xsl:variable name="nodePosition" select="position()" as="xs:integer"/> 
       <xsl:variable name="numberQuestion" select="../@numberQuestion" as="xs:integer"/> 
      <!--a>	<xsl:value-of select="../@numberQuestion" /> </a>
      	<b><xsl:value-of select="position()" /></b-->
      	
      	<!-- Eliminar o IF?? para o caso de não for "shuffle" -->
      	<xsl:if test="$nodePosition &lt; $numberQuestion+1">
      		<!--  xsl:copy-of select="."/!-->
      		 <xsl:element name="{name()}">
        		<xsl:copy-of select="@*" />
	      		<xsl:apply-templates select="q:question"/>
	      		<xsl:apply-templates select="q:answer"/>
	      		<xsl:apply-templates select="q:response"/>
      		</xsl:element>
      	</xsl:if>
      	
      
  

</xsl:template>        
  
  
  
<xsl:template match="q:question">

	<xsl:copy-of select=" . | h:*"/>
	
</xsl:template>  


<xsl:template match="h:*">
		<!--xsl:copy-of select="." copy-namespaces="no" /--> 
		<xsl:element name="{local-name(.)}">
			<xsl:copy-of select="@*|text()|node()" />
			
		</xsl:element>

	</xsl:template>
  
 <xsl:template match="q:response">

	<xsl:copy-of select="."/>
	
</xsl:template>   
  
<xsl:template match="q:answer">

<xsl:variable name="type" select="../@type" as="xs:string"/> 

	<xsl:choose>
		<xsl:when test="$type='single' or $type='multiple' or $type='boolean' ">
			  <xsl:element name="{name()}">
	        		<xsl:copy-of select="@*" />
					<xsl:call-template name="shuffle-answer">
		            <xsl:with-param name="items" select="q:choice"/>
		        	</xsl:call-template>
	        	</xsl:element>
		</xsl:when>
		
		<!--xsl:when test="$type='boolean'">
		  	 <xsl:element name="{name()}">
	        	 <xsl:copy-of select="@*" />
				 <xsl:call-template name="shuffle-answer">
	             <xsl:with-param name="items" select="q:booleanChoice"/>
	        	 </xsl:call-template>
        	 </xsl:element>
		</xsl:when-->
		
		<xsl:otherwise>
			<xsl:copy-of select="."/>
		</xsl:otherwise>	
	</xsl:choose>
	
	
</xsl:template>   
  
  
  
  
  
    
        
<xsl:template name="pick-random-item">
    <xsl:param name="items" />
    <xsl:param name="val" as="xs:integer" />
    
     <xsl:variable name="currenttime" select="current-dateTime()" as="xs:dateTime"/> 
    <xsl:variable name="seed"  select="number(format-dateTime($currenttime,'[f]'))"/>


    <xsl:if test="$items">
        <!-- generate a random number using the "linear congruential generator" algorithm -->
        <xsl:variable name="a" select="16645257"/>
        <xsl:variable name="c" select="1013904223"/>
        <xsl:variable name="m" select="4294967296"/>
        <xsl:variable name="random" select="($a * $seed + $c) mod $m"/>
        <!-- scale random to integer 1..n -->
        <xsl:variable name="i" select="floor($random div $m * count($items)) + 1"/>
        <!-- write out the corresponding item -->
        <!--xsl:copy-of select="$items[$i]"/-->
        <!-- recursive call with the remaining items -->
        
         <!--xsl:variable name="b" select="count($items)"/>
         <b> <xsl:value-of select="$val"></xsl:value-of></b-->
       
        <xsl:apply-templates select="$items[$i]"/>
        
        <xsl:if test="($val - 1) > 0">
        
        <xsl:call-template name="pick-random-item">
            <xsl:with-param name="items" select="$items[position()!=$i]"/>
             <xsl:with-param name="val" select="$val - 1"/>
            
        </xsl:call-template>
        
        </xsl:if>
    </xsl:if>
</xsl:template>    



<xsl:template name="shuffle-answer">
    <xsl:param name="items" />
    
     <xsl:variable name="currenttime" select="current-dateTime()" as="xs:dateTime"/> 
    <xsl:variable name="seed"  select="number(format-dateTime($currenttime,'[f]'))"/>

    <xsl:if test="$items">
        <!-- generate a random number using the "linear congruential generator" algorithm -->
        <xsl:variable name="a" select="16645257"/>
        <xsl:variable name="c" select="1013904223"/>
        <xsl:variable name="m" select="4294967296"/>
        <xsl:variable name="random" select="($a * $seed + $c) mod $m"/>
        <!-- scale random to integer 1..n -->
        <xsl:variable name="i" select="floor($random div $m * count($items)) + 1"/>
        <!-- write out the corresponding item -->
        <xsl:copy-of select="$items[$i]"/>
        <!-- recursive call with the remaining items -->
        <xsl:call-template name="shuffle-answer">
            <xsl:with-param name="items" select="$items[position()!=$i]"/>
            
        </xsl:call-template>
    </xsl:if>
</xsl:template>    



</xsl:stylesheet>



