package org.katan.service.blueprint.parser

import org.junit.Test
import kotlin.test.assertFailsWith

// TODO validate property kinds and mixed properties too
class ParserTest {

    @Test
    fun `no matching value for mixed property`() {
        val input = "version = test"
        val property = Property(
            qualifiedName = "version",
            kind = PropertyKind.Mixed(PropertyKind.Numeric)
        )

        assertFailsWith<NoMatchesForMixedProperty> {
            withParserTest(input, property)
        }
    }
}
