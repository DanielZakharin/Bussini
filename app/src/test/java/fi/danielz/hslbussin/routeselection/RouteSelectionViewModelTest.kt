package fi.danielz.hslbussin.routeselection

import com.apollographql.apollo3.api.Error
import fi.danielz.hslbussin.presentation.directionselection.model.DirectionData
import fi.danielz.hslbussin.presentation.routeselection.RouteSelectionViewModel
import fi.danielz.hslbussin.presentation.routeselection.model.RouteData
import fi.danielz.hslbussin.presentation.routeselection.model.RoutesDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

val mockRoute = object : RouteData {
    override val gtfsId: String = "test"
    override val name: String = "test route"
    override val directions: List<DirectionData>
        get() = emptyList()
}

val mockError = Error("Uh oh!", null, null, null, null)

val mockHappyDataSource = object : RoutesDataSource {
    override val routes: Flow<List<RouteData>> = flow {
        emit(listOf(mockRoute))
    }

    // should never emit
    override val errors: Flow<List<Error>> = flow {}
}

val mockUnHappyDataSource = object : RoutesDataSource {
    // should never emit
    override val routes: Flow<List<RouteData>> = flow {}

    override val errors: Flow<List<Error>> = flow {
        emit(listOf(mockError))
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class RouteSelectionViewModelTest {
    @Test
    fun `Happy Path`() = runTest {

        val viewModel =
            RouteSelectionViewModel(mockHappyDataSource)

        val items = viewModel.routes.first()

        assertEquals(listOf(mockRoute), items)
        viewModel.errors.testFlowIsEmpty()
    }

    @Test
    fun `UnHappy Path`() = runTest {

        val viewModel =
            RouteSelectionViewModel(mockUnHappyDataSource)

        val errors = viewModel.errors.first()

        assertEquals(listOf(mockError), errors)
        viewModel.routes.testFlowIsEmpty()
    }
}

suspend fun Flow<*>.testFlowIsEmpty() {
    val isEmpty = try {
        first()
        false
    } catch (_: NoSuchElementException) {
        true
    }
    assertEquals(true, isEmpty)
}