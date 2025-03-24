package com.example.apiproject_assignment_2.models

//EPIC APIRequest model (returned JSON)
data class EPICRequest (
    var identifier: String,
    var caption: String,
    var image: String,
    var version: String,
    var centroid_coordinates : Coordinates,
    var dscovr_j2000_position : Position,
    var sun_j2000_position : Position,
    var attitude_quaternions : AttitudeQuaternions,
    var date : String,
    var coords : Coords

)

data class Coordinates(
    val lat: Double,
    val lon: Double
)

data class Position(
    val x: Double,
    val y: Double,
    val z: Double
)

data class AttitudeQuaternions(
    val q0: Double,
    val q1: Double,
    val q2: Double,
    val q3: Double
)

data class Coords(
    val centroid_coordinates: Coordinates,
    val dscovr_j2000_position: Position,
    val lunar_j2000_position: Position,
    val sun_j2000_position: Position,
    val attitude_quaternions: AttitudeQuaternions
)