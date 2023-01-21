package fi.danielz.hslbussin.presentation.stopdisplay.model

interface StopSingleDepartureData {
    val departureTime: Long
}

interface StopDeparturesData {
    val departures: List<StopSingleDepartureData>
}