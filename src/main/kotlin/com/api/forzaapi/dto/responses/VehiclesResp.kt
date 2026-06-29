package com.api.forzaapi.dto.responses

import com.api.forzaapi.dto.responses.manufacturers.ManufacturerResp
import tools.jackson.databind.ser.Serializers

data class VehiclesResp (
    val id:Int,
    val modelName: String,
    val productionyear: Int,
    val manufacturer: ManufacturerResp,
    val enginespec: String,
    val horsepower:Int? = 0,
    val torque:Int? = 0,
    val driveType: String,
    val drivetrain: String,
    val transmission:String,
    val weightlbs:Int,
    val weightdistribution:String,
    val description: String?,
    val images: List<String>?
): Serializers