package org.katan

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
class EventsDispatcherTest {

    @Test
    fun `listen to a publication of a primitive type`() = runTest {
        val eventsDispatcher: EventsDispatcher = EventsDispatcherImpl()
        val received = mutableListOf<Int>()

        eventsDispatcher.listen<Int>().onEach {
            received.add(it)
        }.launchIn(TestScope(UnconfinedTestDispatcher()))

        assertTrue(received.isEmpty())
        eventsDispatcher.dispatch(3)

        assertEquals(listOf(3), received)
    }

    @Test
    fun `listen to a publication of a data class`() = runTest {
        val eventsDispatcher: EventsDispatcher = EventsDispatcherImpl()
        val received = mutableListOf<TestEvent<String>>()

        eventsDispatcher.listen<TestEvent<String>>().onEach {
            received.add(it)
        }.launchIn(TestScope(UnconfinedTestDispatcher()))

        assertTrue(received.isEmpty())
        eventsDispatcher.dispatch(TestEvent("abc"))

        assertEquals(listOf(TestEvent("abc")), received)
    }

    @Test
    fun `ignore publication of non-listened type`() = runTest {
        val eventsDispatcher: EventsDispatcher = EventsDispatcherImpl()
        val received = mutableListOf<String>()

        eventsDispatcher.listen<String>().onEach {
            received.add(it)
        }.launchIn(TestScope(UnconfinedTestDispatcher()))

        assertTrue(received.isEmpty())
        eventsDispatcher.dispatch(TestEvent("abc"))

        assertTrue(received.isEmpty())
    }
}
