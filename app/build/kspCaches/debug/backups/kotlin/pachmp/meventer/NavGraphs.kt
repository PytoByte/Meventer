package pachmp.meventer

import com.ramcosta.composedestinations.spec.*
import pachmp.meventer.destinations.*

/**
 * Class generated if any Composable is annotated with `@Destination`.
 * It aggregates all [TypedDestination]s in their [NavGraph]s.
 */
public object NavGraphs {

    public val register: NavGraph = NavGraph(
        route = "register",
        startRoute = RegisterScreenDestination,
        destinations = listOf(
            RegisterScreenDestination,
			CodeScreenDestination,
			CreateUserScreenDestination
        )
    )
}