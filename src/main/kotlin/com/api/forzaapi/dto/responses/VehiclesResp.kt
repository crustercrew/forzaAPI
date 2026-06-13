package com.api.forzaapi.dto.responses

data class VehiclesResp (
    val id:Int,
    val modelName: String,
    val productionyear: Int,
    val manufacturer: ManufacturerResp,
    val enginespec: String,
    val horsepower:Int,
    val torque:Int,
    val driveType: String,
    val drivetrain: String,
    val transmission:String,
    val weightkg:Int,
    val weightdistribution:String
)