package org.katan.event

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

data class TestEvent<T>(val value: T)

@ExperimentalCoroutinesApi
class EventScopeTest {

    @Test
    fun `listen to a publication of a primitive type`() = runTest {
        val eventScope: EventScope = EventScopeImpl()
        val received = mutableListOf<Int>()

        eventScope.listen<Int>().onEach {
            received.add(it)
        }.launchIn(TestScope(UnconfinedTestDispatcher()))

        assertTrue(received.isEmpty())
        eventScope.dispatch(3)

        assertEquals(listOf(3), received)
    }

    @Test
    fun `listen to a publication of a data class`() = runTest {
        val eventScope: EventScope = EventScopeImpl()
        val received = mutableListOf<TestEvent<String>>()

        eventScope.listen<TestEvent<String>>().onEach {
            received.add(it)
        }.launchIn(TestScope(UnconfinedTestDispatcher()))

        assertTrue(received.isEmpty())
        eventScope.dispatch(TestEvent("abc"))

        assertEquals(listOf(TestEvent("abc")), received)
    }

    @Test
    fun `ignore publication of non-listened type`() = runTest {
        val eventScope: EventScope = EventScopeImpl()
        val received = mutableListOf<String>()

        eventScope.listen<String>().onEach {
            received.add(it)
        }.launchIn(TestScope(UnconfinedTestDispatcher()))

        assertTrue(received.isEmpty())
        eventScope.dispatch(TestEvent("abc"))

        assertTrue(received.isEmpty())
    }
}