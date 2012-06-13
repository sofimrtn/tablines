package tabels.web

class ColumnConverterTagLib {
    
    def  intToAlpha = { args, body ->
        out << intToAlphaAux(args.col)
    }
    
    def intToAlphaAux(Integer value) {
        String alphaCol = ""
        Integer resto = value % 26  
          
        alphaCol += (('A' as char).plus(resto) as char).toString()
        while (value >= 26)
        {
            value = (value / 26) - 1      
            resto = value % 26
            alphaCol += (('A' as char).plus(resto) as char).toString()
        }

        return alphaCol.reverse()
        
    }

}
