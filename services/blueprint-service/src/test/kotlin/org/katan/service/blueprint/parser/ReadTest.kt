package org.katan.service.blueprint.parser

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import org.junit.Test
import kotlin.test.assertEquals

class ReadTest {

    @Test
    fun `nested struct`() {
        val input = """
            root {
                nested = 1
            }
        """.trimIndent()

        val result = withParserTest(
            input,
            Property(
                qualifiedName = "root",
                kind = PropertyKind.Struct
            ),
            Property(
                qualifiedName = "root.nested",
                kind = PropertyKind.Numeric
            )
        )

        assertEquals(
            expected = buildJsonObject {
                putJsonObject("root") {
                    put("nested", 1)
                }
            },
            actual = result
        )
    }

    @Test
    fun `multi-level nested struct`() {
        val input = """
            root {
                nested-1 = 1
                
                nested-2 {
                     nested-2-1 = "hello world"
                }
            }
        """.trimIndent()

        val result = withParserTest(
            input,
            Property(
                qualifiedName = "root",
                kind = PropertyKind.Struct
            ),
            Property(
                qualifiedName = "root.nested-1",
                kind = PropertyKind.Numeric
            ),
            Property(
                qualifiedName = "root.nested-2",
                kind = PropertyKind.Struct
            ),
            Property(
                qualifiedName = "root.nested-2.nested-2-1",
                kind = PropertyKind.Literal
            )
        )

        assertEquals(
            expected = buildJsonObject {
                putJsonObject("root") {
                    put("nested-1", 1)
                    putJsonObject("nested-2") {
                        put("nested-2-1", "hello world")
                    }
                }
            },
            actual = result
        )
    }

    @Test
    fun `number in list of numeric`() {
        val input = """
            root = [1, 2, 3, 4]
        """.trimIndent()

        val result = withParserTest(
            input,
            Property("root", PropertyKind.Multiple(PropertyKind.Numeric))
        )

        assertEquals(
            expected = buildJsonObject {
                putJsonArray("root") {
                    for (i in 1..4)
                        add(i)
                }
            },
            actual = result
        )
    }

    @Test
    fun `string in list of literals`() {
        val input = """
            root = ["a", "b", "c"]
        """.trimIndent()

        val result = withParserTest(
            input,
            Property("root", PropertyKind.Multiple(PropertyKind.Literal))
        )

        assertEquals(
            expected = buildJsonObject {
                putJsonArray("root") {
                    add("a")
                    add("b")
                    add("c")
                }
            },
            actual = result
        )
    }

    @Test
    fun `null in list of nulls`() {
        val input = """
            root = [null, null, null]
        """.trimIndent()

        val result = withParserTest(
            input,
            Property("root", PropertyKind.Multiple(PropertyKind.Null))
        )

        assertEquals(
            expected = buildJsonObject {
                @OptIn(ExperimentalSerializationApi::class)
                putJsonArray("root") {
                    add(null)
                    add(null)
                    add(null)
                }
            },
            actual = result
        )
    }

    @Test
    fun `number in mixed of numeric and literal`() {
        val input = """
            value = 1
        """.trimIndent()

        val result = withParserTest(
            input,
            Property(
                "value",
                PropertyKind.Mixed(
                    PropertyKind.Numeric,
                    PropertyKind.Literal
                )
            )
        )

        assertEquals(
            expected = buildJsonObject {
                put("value", 1)
            },
            actual = result
        )
    }

    @Test
    fun `string in mixed of numeric and literal`() {
        val input = """
            value = "1"
        """.trimIndent()

        val result = withParserTest(
            input,
            Property(
                "value",
                PropertyKind.Mixed(
                    PropertyKind.Numeric,
                    PropertyKind.Literal
                )
            )
        )

        assertEquals(
            expected = buildJsonObject {
                put("value", "1")
            },
            actual = result
        )
    }

    @Test
    fun `multiple of struct with mixed kind`() {
        val input = """
            data = [{
                a = "test 1"
                b = 1
            }, {
                a = "test 2",
                b = "2"
            }, {
                a = "test 3",
                b {
                  c = true
                }
            }]
        """.trimIndent()

        val result = withParserTest(
            input,
            Property(
                qualifiedName = "data",
                kind = PropertyKind.Multiple(PropertyKind.Struct)
            ),
            Property(
                qualifiedName = "data.a",
                kind = PropertyKind.Literal
            ),
            Property(
                qualifiedName = "data.b",
                kind = PropertyKind.Mixed(
                    PropertyKind.Numeric,
                    PropertyKind.Literal,
                    PropertyKind.Struct
                )
            ),
            Property(
                qualifiedName = "data.b.c",
                kind = PropertyKind.TrueOrFalse
            )
        )

        assertEquals(
            expected = buildJsonObject {
                putJsonArray("data") {
                    add(
                        buildJsonObject {
                            put("a", "test 1")
                            put("b", 1)
                        }
                    )
                    add(
                        buildJsonObject {
                            put("a", "test 2")
                            put("b", "2")
                        }
                    )
                    add(
                        buildJsonObject {
                            put("a", "test 3")
                            put(
                                "b",
                                buildJsonObject {
                                    put("c", true)
                                }
                            )
                        }
                    )
                }
            },
            actual = result
        )
    }

    @Test
    fun `unnamed struct in struct of structs`() {
        val input = """
            data {
                "A" {
                    key = 1
                }
            }
        """.trimIndent()

        val result = withParserTest(
            input,
            Property("data", PropertyKind.Struct),
            Property("data.*", PropertyKind.Struct),
            Property("data.*.key", PropertyKind.Literal)
        )
        assertEquals(
            expected = buildJsonObject {
                put(
                    "A",
                    buildJsonObject {
                        put("key", 1)
                    }
                )
            },
            actual = result
        )
    }
}
