import org.junit.Assert
import org.junit.Test
import uk.co.ljd.ksparkmvc.processor.SparkServerClassBuilder

/**
 * Created by Liam Davison on 05/07/2017.
 */
class SparkServerClassBuilderTest {

	@Test
	fun testCreationOfSparkApplicationServerClass() {
		// setup
		val builder: SparkServerClassBuilder = SparkServerClassBuilder("uk.co.liamjdavison.kotlinsparkroutes","SparkServer")
		val expected = """package uk.co.liamjdavison.kotlinsparkroutes

import org.reflections.Reflections
import org.reflections.scanners.MethodAnnotationsScanner
import org.reflections.scanners.SubTypesScanner
import org.reflections.scanners.TypeAnnotationsScanner
import org.slf4j.LoggerFactory
import spark.kotlin.get
import spark.kotlin.port
import spark.kotlin.staticFiles
import spark.servlet.SparkApplication
import uk.co.liamjdavison.annotations.SparkController
import uk.co.ljd.ksparkmvc.annotations.KSparkApplication
import java.util.*

/**
 * Root class representing our embedded Jetty SparkJava server. The server is initialised, and each controller is constructed.
 * The server port number defaults to 4567; to change it, set the JAVA environment variable server.port (e.g. -Dserver.port=8000).
 * Individual controllers are responsible for their own routes.
 */
class SparkServer : SparkApplication {
	val logger = LoggerFactory.getLogger(SparkServer::class.java)
	val thisPackage = this.javaClass.`package`

	constructor(args: Array<String>) {
		val portNumber: String? = System.getProperty("server.port")
		port(number = portNumber?.toInt() ?: 4567)

		staticFiles.location("/public")

		// initialize controllers
		val reflections = Reflections(thisPackage.name, MethodAnnotationsScanner(), TypeAnnotationsScanner(), SubTypesScanner())
		val controllers = reflections.getTypesAnnotatedWith(SparkController::class.java)
		controllers.forEach { it.newInstance() }
	}

	override fun init() {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}
"""
		// execute
		val result: String = builder.buildCompleteClass()

		// verify
		Assert.assertEquals(expected,result)
	}

	@Test
	fun testCreationOfSparkApplicationServerClass_WithDifferentDefaultPort() {
		// setup
		val builder: SparkServerClassBuilder = SparkServerClassBuilder("uk.co.liamjdavison.kotlinsparkroutes","SparkServer")
		builder.defaultPort = 1234
		val expected = """package uk.co.liamjdavison.kotlinsparkroutes

import org.reflections.Reflections
import org.reflections.scanners.MethodAnnotationsScanner
import org.reflections.scanners.SubTypesScanner
import org.reflections.scanners.TypeAnnotationsScanner
import org.slf4j.LoggerFactory
import spark.kotlin.get
import spark.kotlin.port
import spark.kotlin.staticFiles
import spark.servlet.SparkApplication
import uk.co.liamjdavison.annotations.SparkController
import uk.co.ljd.ksparkmvc.annotations.KSparkApplication
import java.util.*

/**
 * Root class representing our embedded Jetty SparkJava server. The server is initialised, and each controller is constructed.
 * The server port number defaults to 4567; to change it, set the JAVA environment variable server.port (e.g. -Dserver.port=8000).
 * Individual controllers are responsible for their own routes.
 */
class SparkServer : SparkApplication {
	val logger = LoggerFactory.getLogger(SparkServer::class.java)
	val thisPackage = this.javaClass.`package`

	constructor(args: Array<String>) {
		val portNumber: String? = System.getProperty("server.port")
		port(number = portNumber?.toInt() ?: 1234)

		staticFiles.location("/public")

		// initialize controllers
		val reflections = Reflections(thisPackage.name, MethodAnnotationsScanner(), TypeAnnotationsScanner(), SubTypesScanner())
		val controllers = reflections.getTypesAnnotatedWith(SparkController::class.java)
		controllers.forEach { it.newInstance() }
	}

	override fun init() {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}
"""
		// execute
		val result: String = builder.buildCompleteClass()

		// verify
		Assert.assertEquals(expected,result)
	}

}