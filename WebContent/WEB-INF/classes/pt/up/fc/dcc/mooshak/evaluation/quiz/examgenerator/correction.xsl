<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
	xmlns:q="http://mooshak.dcc.fc.up.pt/quiz" 
	xmlns:h="http://www.w3.org/1999/xhtml" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:fun="http://example.org/xslt/functions"
                version="2.0">

	<xsl:output method="xml" encoding="UTF-8" />	
	


 <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
</xsl:template>
    


<xsl:template match="q:config">

	<xsl:variable name="GroupVal" >
		<xsl:call-template name="Groups">
		 	<xsl:with-param name="group" select = "../q:group" />
		 </xsl:call-template>
 	</xsl:variable> 
 	
	<xsl:element name="{name()}">
        <xsl:copy-of select="@*" />
        
	<xsl:attribute name="mark">
     	<xsl:value-of select="sum($GroupVal/q:group/@score)"/>
     </xsl:attribute>    
     
     
       
     </xsl:element>

</xsl:template>     
    
<xsl:template match="q:group">


	<xsl:variable name="QAVal" >
		<xsl:call-template name="QAS">
		 	<xsl:with-param name="QA" select = "q:QA" />
		 </xsl:call-template>
 	</xsl:variable> 
 		
     <xsl:element name="{name()}">
     
       <xsl:copy-of select="@*" />
       
     <xsl:attribute name="score">
     	<xsl:value-of select="sum($QAVal/q:QA/q:answer/@score)"/>
     </xsl:attribute>
     
 		<xsl:copy-of select="$QAVal"></xsl:copy-of> 
     </xsl:element>
 
</xsl:template>

        
  
 <xsl:template name='Groups'>
	<xsl:param name = "group" /> 
	<xsl:apply-templates select="$group"/>
	 	
</xsl:template>          


        

<xsl:template name='QAS'>
	<xsl:param name = "QA" /> 
	
	<xsl:apply-templates select="$QA"/>
	 	
</xsl:template>        
        
<xsl:template match="q:QA">

      <xsl:variable name="AnswerVal" >
 			<xsl:call-template name="Answer">
 			 	<xsl:with-param name="answer_" select = "q:answer" />
 			 </xsl:call-template>
 		</xsl:variable> 
      
      <!--xsl:if test="@type='matching'"> <@type='single' or  @type='multiple' or @type='boolean' @type='shortAnswer' @type='numeric' -->
      		 <xsl:element name="{name()}">
        		<xsl:copy-of select="@*"/>
        		<xsl:attribute name="score"> 
				 	<xsl:value-of select="sum($AnswerVal//q:answer/@score)"/>
		    	</xsl:attribute>
		    
	      		<xsl:apply-templates select="q:question"/>
	      		<!--xsl:apply-templates select="q:answer"/-->
	      		<xsl:copy-of select="$AnswerVal"></xsl:copy-of> 
	      		 <!--GTY>
       				<xsl:value-of select="sum($AnswerVal//q:answer/@score)"/>
       			</GTY-->	
	      		<xsl:apply-templates select="q:response"/>
	      	
      		</xsl:element>
      
 
</xsl:template>        
 
 
 <xsl:template name='Answer'>
	<xsl:param name = "answer_" /> 
	
	<xsl:apply-templates select="q:answer"/>
	 	
</xsl:template>   
 
  
  
<xsl:template match="q:question">

	<xsl:copy-of select=" . | h:*"/>
	
</xsl:template>  


		

	<xsl:template match="h:*">
		<!--xsl:copy-of select="." copy-namespaces="no" /--> 
		<xsl:element name="h:{local-name(.)}">
			<xsl:copy-of select="@*|text()|node()" />
			
		</xsl:element>

	</xsl:template>

<xsl:template match="q:response">

	<xsl:copy-of select="."/>
	
</xsl:template>  
  
  
<xsl:template match="q:answer">

<xsl:variable name="type" select="../@type" as="xs:string"/> 
<xsl:variable name="score" select="@score" as="xs:integer"/> 

	
	<xsl:choose>
	<!-- test="$type='single' or $type='multiple' or $type='boolean' or $type='fillInTheBlank'" -->
		<xsl:when  test="$type='single' or $type='multiple' or $type='boolean' or $type='fillInTheBlank'" >
		
			  <xsl:element name="{name()}">
			 <xsl:attribute name="score"> 
			 <xsl:value-of select="sum(./q:choice[@selected='true']/@score)"></xsl:value-of>
		    </xsl:attribute>
		    
	        		<xsl:copy-of select="@*" />
	        		<xsl:apply-templates select="q:choice"/>
	        		
	        	</xsl:element>
		</xsl:when>
		
		<xsl:when test="$type='shortAnswer'">
		  	 <xsl:element name="{name()}">
		  	 
		  	  <xsl:variable name="response" select="../response/value/text()" /> 
		    <xsl:variable name="a" select="q:choice" as="node()*"/>
		  	 
		  	  <xsl:attribute name="score"> 
			 <xsl:value-of select="sum(fun:getScoreShort($a,$response))"></xsl:value-of>
		    </xsl:attribute>
		    
			 <xsl:copy-of select="@*|node()" />
       	 
       		
       	</xsl:element>
		</xsl:when>
		
		
		 <xsl:when test="$type='numeric'">
		  	 <xsl:element name="{name()}">
		  	 
			   <xsl:attribute name="score"> 
			   
				    <xsl:variable name="response" select="../q:response/q:value/text()" as="xs:integer"/> 
				    <xsl:variable name="a" select="q:choice" as="node()*"/>
		       		<xsl:value-of select="max(fun:getScoreNumber($a,$response))"/>
	
			    </xsl:attribute>
			    
				<xsl:copy-of select="@*" />
	       		<xsl:apply-templates select="q:choice" mode="numeric"/>
       		
       		</xsl:element>
       	 
		</xsl:when>
		
		
		 <xsl:when test="$type='essay' or $type='matching'">
		  	 <xsl:element name="{name()}">
		  	 
			   <xsl:attribute name="score">0</xsl:attribute>
			    
				<xsl:copy-of select="@* | node()" />
				
       		</xsl:element>
       	 
		</xsl:when>
		
		
		
		
		<!--xsl:otherwise>
			<xsl:copy-of select="."/>
		</xsl:otherwise-->	
	</xsl:choose>
	
	
	
</xsl:template>   
  
  
  <xsl:template match="q:choice">
  
   <xsl:element name="{name()}">
			 
			 <xsl:if test="@selected='true' and @score>0 ">
			 <xsl:attribute name="correct"> 
			  	<xsl:text>true</xsl:text>
		    </xsl:attribute>
		    </xsl:if>
		    
		    <xsl:if test="@selected='true' and @score &lt;= 0 ">
			 <xsl:attribute name="correct"> 
			  	<xsl:text>false</xsl:text>
		    </xsl:attribute>
		    </xsl:if>
		    
			  		<!--xsl:attribute name="score"><xsl:value-of select="$score"/></xsl:attribute-->
        		<xsl:copy-of select="@*|node()|text()" />
        	
        	</xsl:element>
 
  </xsl:template>
  
  
  
  <xsl:template match="q:choice" mode="numeric">
  
     <xsl:element name="{name()}">
			 
			 <xsl:variable name="response" select="../../q:response/q:value/text()" as="xs:integer"/>
			 <xsl:attribute name="correct">
			 <xsl:choose>
			 	 <xsl:when test="$response &lt;=  @maximumValue and  $response &gt;= @minimumValue">
			 	 	<xsl:text>true</xsl:text>
			 	 </xsl:when>
			 	 
			 	 <xsl:when test="$response &lt;=  @high and  $response &gt;= @low">
			 	 	<xsl:text>true</xsl:text>
			 	 </xsl:when>
			 	 
			 	 <xsl:when test="$response &lt;= (@value + @tolerance) and $response &gt;= (@value - @tolerance)">
			 	 	<xsl:text>true</xsl:text>
			 	 </xsl:when>
			 	 
			 	 <xsl:otherwise>
			 	 	<xsl:text>false</xsl:text>
			 	 </xsl:otherwise>
		 	 
		 	</xsl:choose>
			 </xsl:attribute>
			
	        <xsl:copy-of select="@*|node()|text()" />
	        	
	</xsl:element>
  
  </xsl:template>
  
  
  <xsl:template match="q:choice" mode="shortAnswer">
  		<xsl:copy-of select="@*|node()|text()" />
  </xsl:template>
  
  
  
 
  <xsl:function name="fun:getScore">
    <xsl:param name="choices"  /> 
  <xsl:for-each select="$choices">
	
		<xsl:if test="$choices/@selected = true()">
		<xsl:value-of select="@score"/>
		</xsl:if>
	    <!--tr>
	      <td><xsl:value-of select="@score"/></td>
	      <td><xsl:value-of select="@selected"/></td>
	    </tr-->
    </xsl:for-each>
  </xsl:function>
  
  
  
  <xsl:function name="fun:getScoreShort">
    <xsl:param name="choices"  /> 
    <xsl:param name="response"  /> 
    
   
  <xsl:for-each select="$choices">
		
		 <xsl:variable name="choice" select="." as="xs:string"/>
		<xsl:if test="$choice=$response">
				
					<xsl:value-of select="@score"/>
				
				</xsl:if>
    </xsl:for-each>
  </xsl:function>
  
  
    
  <xsl:function name="fun:getScoreNumber">
    <xsl:param name="choices"  /> 
    <xsl:param name="response" as="xs:integer"/> 
    <!--xsl:variable name="res" select="../../q:response/q:value/text()" as="xs:integer"/-->
    
     

  <xsl:for-each select="$choices">
		 
		<xsl:choose>
		 	 <xsl:when test="$response &lt;=  @maximumValue and  $response &gt;= @minimumValue">
		 	 	<xsl:value-of select="@score"/>
		 	 </xsl:when>
		 	 
		 	 <xsl:when test="$response &lt;=  @high and  $response &gt;= @low">
		 	 	<xsl:value-of select="@score"/>
		 	 </xsl:when>
		 	 
		 	 <xsl:when test="$response &lt;= (@value + @tolerance) and $response &gt;= (@value - @tolerance)">
		 	 	<xsl:value-of select="@score"/>
		 	 </xsl:when>
		 	 
		 </xsl:choose>
		 
		 
				
    </xsl:for-each>
  </xsl:function>
  
  
  </xsl:stylesheet> 
  








  <!--xsl:for-each select="q:choice">
				<xsl:if test="@selected = true()">
				<xsl:value-of select="@score"/>
				</xsl:if>
		    </xsl:for-each-->
		    
		    <!--xsl:for-each select="q:choice">
		<xsl:if test="@selected = true()">
		<xsl:value-of select="@score"/>
		</xsl:if>
    </xsl:for-each-->

	<!--q:AAAA>	<xsl:value-of select="sum(./q:choice[@selected='true']/@score)"></xsl:value-of> </q:AAAA-->
		    