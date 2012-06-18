package es.ctic.tabels

object columnConverter {
	
	def alphaToInt(col : String) : Int = {
	  var num :Double = 0
	  var index :Int = col.length()
	  
	  col.foreach(c  =>{if(index>1) num += scala.math.pow(26 , index-1)*(c - 'A'+1); 
	  					else num += (c - 'A')
	  					index-=1;})
	  					
	  return num.toInt
	}
	
	def intToAlpha(i : Int) : String = {
			
		var value : Double = (i / 26)-1
		var resto : Double = i % 26  
		var alphaCol : String = ""
		  
		while(value >= 0)
		{
			
			alphaCol += (resto + 'A').toChar.toString
			resto = value % 26
			value = (value / 26)-1
		}
		alphaCol += (resto + 'A').toChar.toString
		
		return alphaCol.reverse
	}
  
}

