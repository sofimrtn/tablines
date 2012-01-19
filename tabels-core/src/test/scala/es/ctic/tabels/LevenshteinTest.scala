package es.ctic.tabels

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._

class LevenshteinTest extends JUnitSuite{
	
  @Test def stringDistanceTest{
     assert( Levenshtein.stringDistance(   "",    "") == 0 )
   	 assert( Levenshtein.stringDistance(  "a",    "") == 1 )
  	 assert( Levenshtein.stringDistance(   "",   "a") == 1 )
  	 assert( Levenshtein.stringDistance("abc",    "") == 3 )
  	 assert( Levenshtein.stringDistance(   "", "abc") == 3 )
  	 assert( Levenshtein.stringDistance(   "",    "") == 0 )
  	 assert( Levenshtein.stringDistance(  "a",   "a") == 0 )
  	 assert( Levenshtein.stringDistance("abc", "abc") == 0 )
  	 assert( Levenshtein.stringDistance(   "",   "a") == 1 )
  	 assert( Levenshtein.stringDistance(  "a",  "ab") == 1 )
  	 assert( Levenshtein.stringDistance(  "b",  "ab") == 1 )
  	 assert( Levenshtein.stringDistance( "ac", "abc") == 1 )
  	 assert( Levenshtein.stringDistance("abcdefg", "xabxcdxxefxgx") == 6 )
  	 assert( Levenshtein.stringDistance(  "a",    "") == 1 )
  	 assert( Levenshtein.stringDistance( "ab",   "a") == 1 )
  	 assert( Levenshtein.stringDistance( "ab",   "b") == 1 )
  	 assert( Levenshtein.stringDistance("abc",  "ac") == 1 )
  	 assert( Levenshtein.stringDistance("xabxcdxxefxgx", "abcdefg") == 6 )
  	 assert( Levenshtein.stringDistance(  "a",   "b") == 1 )
  	 assert( Levenshtein.stringDistance( "ab",  "ac") == 1 )
  	 assert( Levenshtein.stringDistance( "ac",  "bc") == 1 )
  	 assert( Levenshtein.stringDistance("abc", "axc") == 1 )
  	 assert( Levenshtein.stringDistance("xabxcdxxefxgx", "1ab2cd34ef5g6") == 6 )
  	 assert( Levenshtein.stringDistance("example", "samples") == 3 )
  	 assert( Levenshtein.stringDistance("sturgeon", "urgently") == 6 )
  	 assert( Levenshtein.stringDistance("levenshtein", "frankenstein") == 6 )
  	 assert( Levenshtein.stringDistance("distance", "difference") == 5 )
  	 assert( Levenshtein.stringDistance("java was neat", "scala is great") == 7 )
  }
}