package uk.co.ljd.ksparkmvc.processor

/**
 * Created by Liam Davison on 05/07/2017.
 */
class SparkServerClassBuilder(packageName: String, classSimpleName: String) {

	val packageName = packageName
	val className = classSimpleName
	var defaultPort: Int? = null

	fun buildCompleteClass(): String {
		var sb: StringBuilder = StringBuilder()

		// the package
		sb.append("package ").append(packageName).append("\n")
		sb.append("\n")

		// the imports
		buildImports(sb)

		// the documentation comment
		buildCommentDocs(sb)

		// the class
		buildClass(sb,className, "SparkApplication")

		// add properties/fields
		buildProperties(sb, className)

		// constructor
		buildConstructor(sb, defaultPort)

		// build init function
		buildInitFunction(sb)

		// close
		sb.append("}").append("\n")

		return sb.toString()
	}

	private fun buildImports(sb: StringBuilder) {
		sb.append("import org.reflections.Reflections").append("\n")
		sb.append("import org.reflections.scanners.MethodAnnotationsScanner").append("\n")
		sb.append("import org.reflections.scanners.SubTypesScanner").append("\n")
		sb.append("import org.reflections.scanners.TypeAnnotationsScanner").append("\n")
		sb.append("import org.slf4j.LoggerFactory").append("\n")
		sb.append("import spark.kotlin.get").append("\n")
		sb.append("import spark.kotlin.port").append("\n")
		sb.append("import spark.kotlin.staticFiles").append("\n")
		sb.append("import spark.servlet.SparkApplication").append("\n")
		sb.append("import uk.co.liamjdavison.annotations.SparkController").append("\n")
		sb.append("import uk.co.ljd.ksparkmvc.annotations.KSparkApplication").append("\n")
		sb.append("import java.util.*").append("\n").append("\n")
	}

	private fun buildCommentDocs(sb: StringBuilder) {
		sb.append("""/**
 * Root class representing our embedded Jetty SparkJava server. The server is initialised, and each controller is constructed.
 * The server port number defaults to 4567; to change it, set the JAVA environment variable server.port (e.g. -Dserver.port=8000).
 * Individual controllers are responsible for their own routes.
 */""").append("\n")
	}

	private fun buildClass(sb: StringBuilder, className: String, superClasses: String?) {
		sb.append("class ").append(className)
		if(superClasses != null) {
			sb.append(" : ").append(superClasses)
		}
		sb.append(" {").append("\n")

	}

	private fun buildProperties(sb: StringBuilder, className: String) {
		sb.append("\t").append("val logger = LoggerFactory.getLogger(")
		sb.append(className)
		sb.append("::class.java)").append("\n")
		sb.append("\t").append("val thisPackage = this.javaClass.`package`").append("\n")
		sb.append("\n")
	}

	private fun buildConstructor(sb: StringBuilder, port: Int?) {
		sb.append("\t").append("constructor(args: Array<String>) {").append("\n")
		sb.append("\t\t").append("val portNumber: String? = System.getProperty(\"server.port\")").append("\n")
		sb.append("\t\t").append("port(number = portNumber?.toInt() ?: ")
		if(port != null) { sb.append(port)	 } else sb.append("4567")
		sb.append(")")
		sb.append("\n").append("\n")

		sb.append("\t\t").append("staticFiles.location(\"/public\")").append("\n").append("\n")

		sb.append("\t\t").append("// initialize controllers").append("\n")
		sb.append("\t\t").append("val reflections = Reflections(thisPackage.name, MethodAnnotationsScanner(), TypeAnnotationsScanner(), SubTypesScanner())").append("\n")
		sb.append("\t\t").append("val controllers = reflections.getTypesAnnotatedWith(SparkController::class.java)").append("\n")
		sb.append("\t\t").append("controllers.forEach { it.newInstance() }").append("\n")
		sb.append("\t").append("}").append("\n").append("\n")
	}

	private fun buildInitFunction(sb: StringBuilder) {
		sb.append("\t").append("override fun init() {").append("\n")
		sb.append("\t\t").append("TODO(\"not implemented\") //To change body of created functions use File | Settings | File Templates.").append("\n")
		sb.append("\t").append("}").append("\n")
	}
}