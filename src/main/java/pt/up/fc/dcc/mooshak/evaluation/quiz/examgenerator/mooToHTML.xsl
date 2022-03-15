<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
	xmlns:q="http://mooshak.dcc.fc.up.pt/quiz" 
	xmlns:h="http://www.w3.org/1999/xhtml" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                version="2.0">

	<xsl:output method="html" encoding="UTF-8" indent="yes"/>	
	

	
<xsl:template match="q:quiz">


   <div id="container-quizzes">
   <div id="quiz">
		<!--xsl:apply-templates select="QA"/-->
		  
		 <!--h1><xsl:value-of select="q:group/q:QA[2]/q:question"/></h1-->
		 
		 
		  <div id="quiz-head">
		   <!--button class="btn-submit" onclick="submit()">Submit</button>
		  
		  <div id="progress-bar">
        <progress id="_progress" value="22" max="100"></progress>
        </div>
        
        <div id="contenedor">
			<div class="reloj" id="Horas">00</div>
			<div class="reloj" id="Minutos">:00</div>
			<div class="reloj" id="Segundos">:00</div>
		</div-->
		
		 <div id="question-line">
         </div>
        
        </div> 		  
         		 
		 
		<div id="groups">
		    
			  <xsl:apply-templates select="q:group"/>
		</div>

	
	===============And of Question=================
	</div>   
  </div>	

</xsl:template>	



<xsl:template match="q:group">


	<div class="group" id="{@id}" >
		<p class="group-name">
			
			===============<xsl:value-of select="@name" /> ===============
		</p>


	<xsl:if test="q:description">
		<p class="description-group">
			
			Description of Group:
			<xsl:value-of select="q:description" />
			
		</p>
	 </xsl:if>
	


	<xsl:apply-templates select="q:QA">
		<xsl:with-param name="idGroup" select="@id" />
	</xsl:apply-templates>

	</div>


</xsl:template>


<xsl:template match="q:QA">
	<xsl:variable name="idQuestion" select="text()"/>
	<div class="qa {@type}" id="{@id}" >
		<xsl:if test="q:description">
			<p class="description-question">
				Description:
				<xsl:value-of select="q:description" />
			</p>
		</xsl:if>

		<xsl:if test="q:question">

			<div class="question-text" >
				
					Q<xsl:value-of select="position()" />:
				
				<xsl:apply-templates select="q:question" />
				
				<xsl:if test="@maxScore">
					[
					<xsl:value-of select="@score" /> <!-- @maxScore -->
					pts]
			   </xsl:if>
			   <b/>
			</div>


		</xsl:if>

		<xsl:if test="q:answer">
			<xsl:apply-templates select="q:answer" />
		</xsl:if>

	</div>
</xsl:template>



 

<xsl:template match="q:question">

  <xsl:apply-templates select="q:missingWord | text() | h:*" />
		
</xsl:template>


	<xsl:template match="h:*">
		<!--xsl:copy-of select="." copy-namespaces="no" /--> 
		<xsl:element name="{local-name(.)}">
			<xsl:copy-of select="@*|text()|node()" />
			
		</xsl:element>

	</xsl:template>

 <xsl:template match="*">
        <xsl:element name="{local-name(.)}">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="@*">
        <xsl:copy/>
    </xsl:template>


<xsl:template match="text()" />

 
  <!-- override rule: copy any text node beneath description  -->
 
<xsl:template match="q:question//text()">
    <xsl:copy-of select="." />
</xsl:template>




<xsl:template match="q:missingWord | text()">

		<xsl:variable name="idQuestion" select="text()"/>
		<xsl:variable name="type" select="../../@type"/>
		

	<xsl:if test="$type ='fillInTheBlank'">

		<select onclick="inputfillInTheBlank(this)" id="{text()}">
			<xsl:for-each select="../../q:answer[@id=$idQuestion]/q:choice">
				<xsl:element name="option">
					<xsl:attribute name="value"> <xsl:value-of select="text()" /> </xsl:attribute>
					<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
					<xsl:value-of select="text()" />
				</xsl:element>
			</xsl:for-each>
		</select>

	
	</xsl:if> 
	
	
	<xsl:if test="$type ='shortAnswer'">

		
		<xsl:element name="input">
			<xsl:attribute name="type">text</xsl:attribute>
			<xsl:attribute name="onblur">inputShort(this)</xsl:attribute>
			 <xsl:attribute name="id"> <xsl:value-of select="@id" /> </xsl:attribute>
			<xsl:attribute name="name">
				<xsl:value-of select="text()" /> 
			</xsl:attribute>
			<xsl:attribute name="id">
				<xsl:value-of select="text()" /> 
			</xsl:attribute>
		</xsl:element>

	</xsl:if> 
	
		

 
		
</xsl:template>




 
<xsl:template match="q:answer">

		<!-- p> <xsl:value-of select="@type" /></p-->
	
		
		
		
		
		
		 <xsl:if test="../@type = 'matching'">
		 	
		</xsl:if>
		
		<xsl:choose>
			<xsl:when test="../@type = 'single' or ../@type = 'multiple'">
			
				<xsl:apply-templates select="q:choice"  mode = "single" />
			</xsl:when>
			<xsl:when test="../@type = 'numeric'">
				<xsl:apply-templates select="q:choice"  mode = "numeric" />
			</xsl:when>
			<xsl:when test="../@type = 'boolean'">
				<xsl:apply-templates select="q:choice" mode="boolean"/>
			</xsl:when>
			
			<xsl:when test="../@type = 'essay'">
				<xsl:apply-templates select="q:choice" mode="essay" />
			</xsl:when>
			<xsl:when test="../@type = 'matching'">
			
				<table style="width:'100px'" >
			  <tr>
			    <th>Option 1</th>
			    <th>Option 2</th> 
			  </tr>
				<xsl:apply-templates select="q:choice" mode="matching" />
				</table>
			</xsl:when>
		
		</xsl:choose>
		
		
		
		
	
</xsl:template>


<xsl:template match="q:choice" mode = "single">


  <xsl:variable name="type" select="../../@type"/>


   
	    <!--input type="radio" name="$idGroup" value="option"> <xsl:value-of select="text()" /></input-->
	    
	    
	    <xsl:if test="($type = 'single') or ($type = 'multiple')">
	    
	     <xsl:element name="input">
	
			<xsl:choose>
				
				<xsl:when test="$type = 'single'">
					<xsl:attribute name="type">radio</xsl:attribute>
					<xsl:attribute name="class">w3-radio</xsl:attribute>
					<xsl:attribute name="value">false</xsl:attribute>
					<xsl:attribute name="onclick">inputSingle(this)</xsl:attribute>
				</xsl:when>
				
				<xsl:when test="$type = 'multiple'">
					<xsl:attribute name="type">checkbox</xsl:attribute>
					<xsl:attribute name="class">w3-check</xsl:attribute>
					<xsl:attribute name="onclick">inputMultiple(this)</xsl:attribute>
					
				</xsl:when>
			
			</xsl:choose>
	     	 
	     	 
	      
	     	 
	     	 <xsl:attribute name="name"> <xsl:value-of select="../../@id" /> </xsl:attribute>
	     	  <xsl:attribute name="id"> <xsl:value-of select="@id" /> </xsl:attribute>
	     	 <!--xsl:attribute name="value"> <xsl:value-of select="../../@id" /> </xsl:attribute>
	         <xsl:attribute name="doc:style"
	       	 namespace="http://www.java2s.com/documents">classic</xsl:attribute>
	     	 <xsl:apply-templates /-->

			<xsl:apply-templates mode="copy" select="h:* | text()"></xsl:apply-templates>
  			
    	</xsl:element>
    	 <br/>
    	</xsl:if>	
	    
	   
	    <!--  p>  <xsl:value-of select="q:feedback" /> </p-->
		
		
	

</xsl:template>



<xsl:template match="q:choice" mode="numeric">

		<xsl:if test="position()=1">
		 <xsl:element name="input">
	      
	     	 <xsl:attribute name="type">text</xsl:attribute>
	     	 <xsl:attribute name="name"> <xsl:value-of select="../../@id" /> </xsl:attribute>
	     	  <xsl:attribute name="id"> <xsl:value-of select="@id" /> </xsl:attribute>
	     	 <xsl:attribute name="onblur">inputNumeric(this)</xsl:attribute>
	     	 
	         <!--xsl:attribute name="doc:style"
	       	 namespace="http://www.java2s.com/documents">classic</xsl:attribute>
	     	 <xsl:apply-templates /-->
	     	<xsl:value-of select="text()" />
    	</xsl:element>
    	
    	</xsl:if>

</xsl:template>


<xsl:template match="q:choice" mode="boolean">


	 
		
	
	<xsl:choose>
				
				<xsl:when test="@radio = 'true'">
					 <select id="@id">
					  <option value="true"> True </option>
					  <option value="false">False</option>	  
					</select >
				</xsl:when>
				
				<xsl:otherwise>
				
				<xsl:element name="input">
			
					<xsl:attribute name="type">radio</xsl:attribute>
					<xsl:attribute name="name"> <xsl:value-of select="../../@id" /> </xsl:attribute>
					<xsl:attribute name="id"> <xsl:value-of select="@id" /> </xsl:attribute>
					<xsl:attribute name="class">w3-radio</xsl:attribute>
					<xsl:attribute name="onclick">inputBoolean(this)</xsl:attribute>
					
					<xsl:apply-templates mode="copy" select="h:* | text()"></xsl:apply-templates>
		  			
					<br />
				</xsl:element>
			
				<!--xsl:element name="input">
			
					<xsl:attribute name="type">radio</xsl:attribute>
					<xsl:attribute name="name"> <xsl:value-of select="../../@id" /> </xsl:attribute>
					<xsl:attribute name="class">w3-radio</xsl:attribute>
					False
					<br />
				</xsl:element-->
				</xsl:otherwise>
			</xsl:choose>
	
</xsl:template>

<xsl:template match="q:choice" mode="essay">


	<xsl:value-of select="text()" />
	
	<xsl:variable name="test" select="@rows" />	
	
	<!--textarea rows="$rows" cols="@cols" id="@id"-->
	
	<textarea class="w3-input w3-border essay-textarea" rows="10" cols="20" id="{@id}" onblur="inputEssayChoice(this)" >
		
	</textarea>
</xsl:template>

<xsl:template match="q:choice" mode="matching">

	
	  <tr>
	    <td>
	    <input type="text" name="mappedValue" class="input-macthing" onblur="inputMatching(this)" id="{@id}"></input> 
	     <xsl:value-of select="@mappedValue"/>
	    </td>
	    <td>
	    	<xsl:value-of select="position()"/>. 
	    	<xsl:value-of select="@mapKey"/>
	    </td>
	   
	   
	  </tr>
	
	   
	
</xsl:template>




<xsl:template match="text()"  mode="copy">
    
       <xsl:value-of select="."/>
</xsl:template>


<xsl:template match="h:*"  mode="copy">
    
        <xsl:apply-templates select="."></xsl:apply-templates>
</xsl:template>


</xsl:stylesheet>

<!--xsl:template match="Group">
	
	<div id="{generate-id()}">
		<h1><xsl:value-of select="question"/></h1>
	
	</div>
</xsl:template>





<xsl:for-each-group select="*" group-starting-with="group"> 
		<div>
			<xsl:apply-templates select="current-group()" />
		</div>
	</xsl:for-each-group-->
