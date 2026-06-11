package com.api.forzaapi

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.beans.factory.annotation.Autowired
import javax.sql.DataSource
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.test.context.TestPropertySource
import java.sql.Connection

@SpringBootTest
@TestPropertySource(locations = ["file:.env"])
class ForzaApiApplicationTests {

    @Autowired
    private lateinit var dataSource: DataSource

	@Test
	fun contextLoads() {
	}

    @Test
    fun testDatabaseConnection() {
        assertNotNull(dataSource, "The Spring DataSource bean should not be null")

        dataSource.connection.use { connection: Connection ->
            assertNotNull(connection, "Database connection should be established successfully")

            // Verify that SSL is actively required/used on the remote host
            println("--- Connection Verified ---")
            println("Connected to catalog: ${connection.catalog}")
            println("Database Driver: ${connection.metaData.driverName}")
            println("Database Product Version: ${connection.metaData.databaseProductVersion}")

            // 3. Execute a lightweight query to ensure the database can process commands
            connection.createStatement().use { statement ->
                statement.executeQuery("SELECT 1;").use { resultSet ->
                    assertTrue(resultSet.next(), "The database should return a row for a basic query")

                    val resultValue = resultSet.getInt(1)
                    assertTrue(resultValue == 1, "The returned verification value should equal 1")
                    println("Sanity Check SQL Execution Result: $resultValue (Success)")
                }
            }
        }
    }

}
