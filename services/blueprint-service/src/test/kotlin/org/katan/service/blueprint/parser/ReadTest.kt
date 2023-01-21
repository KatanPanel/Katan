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

        val nested = Property(
            qualifiedName = "root.nested",
            kind = PropertyKind.Numeric
        )
        val root = Property(
            qualifiedName = "root",
            kind = PropertyKind.Struct
        )

        val result = withParserTest(listOf(root, nested)) {
            read(input)
        }

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
    fun `deep nested struct`() {
        val input = """
            root {
                nested-1 = 1
                
                nested-2 {
                     nested-2-1 = "hello world"
                }
            }
        """.trimIndent()

        val result = withParserTest(
            listOf(
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
        ) {
            read(input)
        }

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
    fun `accept number in numeric list`() {
        val input = """
            root = [1, 2, 3, 4]
        """.trimIndent()

        val result = withParserTest(
            listOf(
                Property(
                    qualifiedName = "root",
                    kind = PropertyKind.Multiple(PropertyKind.Numeric)
                )
            )
        ) {
            read(input)
        }

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
    fun `accept string in literals list`() {
        val input = """
            root = ["a", "b", "c"]
        """.trimIndent()

        val result = withParserTest(
            listOf(
                Property(
                    qualifiedName = "root",
                    kind = PropertyKind.Multiple(PropertyKind.Literal)
                )
            )
        ) {
            read(input)
        }

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
    fun `accept null in null list`() {
        val input = """
            root = [null, null, null]
        """.trimIndent()

        val result = withParserTest(
            listOf(
                Property(
                    qualifiedName = "root",
                    kind = PropertyKind.Multiple(PropertyKind.Null)
                )
            )
        ) {
            read(input)
        }

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
    fun `accept number in mixed numeric and literal`() {
        val input = """
            value = 1
        """.trimIndent()

        val result = withParserTest(
            listOf(
                Property(
                    qualifiedName = "value",
                    kind = PropertyKind.Mixed(PropertyKind.Numeric, PropertyKind.Literal)
                )
            )
        ) {
            read(input)
        }

        assertEquals(
            expected = buildJsonObject {
                put("value", 1)
            },
            actual = result
        )
    }

    @Test
    fun `accept string in mixed numeric and literal`() {
        val input = """
            value = "1"
        """.trimIndent()

        val result = withParserTest(
            listOf(
                Property(
                    qualifiedName = "value",
                    kind = PropertyKind.Mixed(PropertyKind.Numeric, PropertyKind.Literal)
                )
            )
        ) {
            read(input)
        }

        assertEquals(
            expected = buildJsonObject {
                put("value", "1")
            },
            actual = result
        )
    }

    @Test
    fun `accept list in multiple of struct with mixed kind`() {
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
            listOf(
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
                    kind = PropertyKind.Mixed(PropertyKind.Numeric, PropertyKind.Literal, PropertyKind.Struct)
                ),
                Property(
                    qualifiedName = "data.b.c",
                    kind = PropertyKind.TrueOrFalse
                )
            )
        ) {
            read(input)
        }

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
}
