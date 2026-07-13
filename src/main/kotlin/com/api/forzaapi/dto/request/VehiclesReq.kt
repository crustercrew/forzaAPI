package com.api.forzaapi.dto.request
import com.api.forzaapi.enumerates.DriveType
import com.api.forzaapi.enumerates.Drivetrain
import jakarta.validation.constraints.*

data class VehiclesReq(
    @field:NotBlank(message = "Model name is required")
    val modelName: String,

    @field:Min(value = 1900, message = "production year not valid")
    val productionyear: Int,

    @field:NotBlank(message = "manufacturer name is required")
    val manufacturerName: String,

    val enginespec: String,

    @field:Min(0)
    val horsepower: Int,
    val torque: Int,

    val driveType: DriveType,
    val drivetrain: Drivetrain,

    val transmission: String,
    val weightlbs: Int,
    val weightdistribution: String,
    val description: String,
    val images: List<String>?
)
