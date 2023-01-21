package org.katan.service.blueprint.parser

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TopLevelPropertiesParserTest {

    @Test
    fun `missing property 'name'`() {
        val input = """
            version = "test"
        """.trimIndent()

        val exception = withParserTest {
            assertFailsWith<RequiredPropertyException> {
                parse(input)
            }
        }
        assertEquals(Properties.Name, exception.property)
    }

    @Test
    fun `missing property 'version'`() {
        val input = """
            name = "test"
        """.trimIndent()

        val exception = withParserTest {
            assertFailsWith<RequiredPropertyException> {
                parse(input)
            }
        }
        assertEquals(Properties.Version, exception.property)
    }

    @Test
    fun `blank property 'name'`() {
        val input = """
            name = ""
            version = "0.0.0"
        """.trimIndent()

        val exception = withParserTest {
            assertFailsWith<BlankPropertyException> {
                parse(input)
            }
        }
        assertEquals(Properties.Name, exception.property)
    }

    @Test
    fun `blank property 'version'`() {
        val input = """
            name = "test"
            version = ""
        """.trimIndent()

        val exception = withParserTest {
            assertFailsWith<BlankPropertyException> {
                parse(input)
            }
        }
        assertEquals(Properties.Version, exception.property)
    }
}
