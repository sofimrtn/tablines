package tabels.web



import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.web.GroovyPageUnitTestMixin} for usage instructions
 */
@TestFor(ColumnConverterTagLib)
class ColumnConverterTagLibTests {

    void testIntToAlphaAux() {
        def tag = new ColumnConverterTagLib()
        assertEquals("A", tag.intToAlphaAux(0))
        assertEquals("AC", tag.intToAlphaAux(28))
        assertEquals("YZ", tag.intToAlphaAux(675))
        assertEquals("ZA", tag.intToAlphaAux(676))
        assertEquals("AAD", tag.intToAlphaAux(705))
    }
}
