package fi.danielz.hslbussin.presentation.directionselection.model

import android.os.Parcelable
import fi.danielz.hslbussin.RoutesQuery
import kotlinx.parcelize.Parcelize

/**
 * Single simplified direction to be presented in UI
 */
@Parcelize
data class DirectionData(
    val directionId: Int?,
    val name: String?
) : Parcelable {
    constructor(queryDataItem: RoutesQuery.Pattern) : this(
        queryDataItem.directionId,
        queryDataItem.headsign
    )
}

