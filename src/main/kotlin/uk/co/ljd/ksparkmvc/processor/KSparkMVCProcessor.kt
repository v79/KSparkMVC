package uk.co.ljd.ksparkmvc.processor

import uk.co.ljd.ksparkmvc.annotations.KSparkApplication
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import kotlin.reflect.KClass

class KSparkMVCProcessor : AbstractProcessor() {

	private companion object {
		val ANNOTATION_TO_PREFIX = mapOf(
				KSparkApplication::class to "SourceAnnotated")

		val SUFFIX_OPTION = "suffix"
		val GENERATE_KOTLIN_CODE_OPTION = "generate.kotlin.code"
		val GENERATE_ERROR = "generate.error"
		val KAPT_KOTLIN_GENERATED_OPTION = "kapt.kotlin.generated"
	}

	override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
		for ((annotation, prefix) in ANNOTATION_TO_PREFIX) {
			processAnnotation(roundEnv, annotation, prefix)
		}

		return true
	}

	private fun <T : Annotation> processAnnotation(roundEnv: RoundEnvironment, annotationClass: KClass<T>, generatedFilePrefix: String) {
		val elements = roundEnv.getElementsAnnotatedWith(annotationClass.java)

		val elementUtils = processingEnv.elementUtils
		val filer = processingEnv.filer

		val options = processingEnv.options
		val generatedFileSuffix = options[SUFFIX_OPTION] ?: "Generated"
		val generateKotlinCode = "true" == options[GENERATE_KOTLIN_CODE_OPTION]
		val kotlinGenerated = options[KAPT_KOTLIN_GENERATED_OPTION]

		for (element in elements) {
			val packageName = elementUtils.getPackageOf(element).qualifiedName.toString()
			val simpleName = element.simpleName.toString() + "Wibble"
			val generatedJavaClassName = generatedFilePrefix.capitalize() + simpleName.capitalize() + generatedFileSuffix

//			filer.createSourceFile(packageName + '.' + generatedJavaClassName).openWriter().use { with(it) {
//				appendln("package $packageName;")
//				appendln("public final class $generatedJavaClassName {}")
//			}}

//			if (generateKotlinCode && kotlinGenerated != null && element.kind == ElementKind.CLASS) {

//			val serverClass = ClassName(packageName,simpleName)
//			val kotlinFile = KotlinFile.builder(packageName,simpleName)
//					.addType(TypeSpec.classBuilder(simpleName).build())
//							.addProperty(PropertySpec.builder("logger",Logger::class)
//									.initializer(CodeBlock.of("LoggerFactory.getLogger(%s)",simpleName).toBuilder().build()).build())
//					.addProperty(PropertySpec.builder("thisPackage",Package::class).initializer(CodeBlock.of("this.javaClass.`package`").toBuilder().build()).build()
//					).build()
//			kotlinFile.writeTo(System.out)

			val builder: SparkServerClassBuilder = SparkServerClassBuilder(packageName, classSimpleName = simpleName)
			File(kotlinGenerated, "$simpleName.kt").writer().buffered().use {
				it.append(builder.buildCompleteClass())
			}


//				File(kotlinGenerated, "$simpleName.kt").writer().buffered().use {
//					it.appendln("package $packageName")
//					it.appendln("fun $simpleName.customToString() = \"$generatedJavaClassName: \" + toString()")
//				}
//			}
		}

		if (options[GENERATE_ERROR] == "true") {
			processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Error from annotation processor!")
		}
	}

		fun Element.toTypeElementOrNull(): TypeElement? {
			if (this !is TypeElement) {
				processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Invalid element type, class expected", this)
				return null
			}

			return this
		}

	override fun getSupportedSourceVersion() = SourceVersion.RELEASE_8

	override fun getSupportedAnnotationTypes() = ANNOTATION_TO_PREFIX.keys.map { it.java.canonicalName }.toSet()

	override fun getSupportedOptions() = setOf(SUFFIX_OPTION, GENERATE_KOTLIN_CODE_OPTION, GENERATE_ERROR)
	}



	/*lateinit var filer: Filer

	override fun init(processingEnvironment: ProcessingEnvironment) {
		super.init(processingEnv)
		filer = processingEnvironment.filer
	}

	override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {

		for (typeElement in annotations) {
			for (element in roundEnv.getElementsAnnotatedWith(KSparkApplication::class.java)) {

				try {
					processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "found KSparkApplication annotation on ", element)

					val className: String = element.simpleName.toString() + "Wibble"

					val kotlinFile = KotlinFile.builder("", className)
							.addType(TypeSpec.classBuilder(className)
									.build())
							.build()


				} catch (e: IOException) {
					error(typeElement, "It's all gone tits up, sorry")
					return false
				}
			}
		}

		return true
	}




	override fun getSupportedSourceVersion(): SourceVersion {
		return SourceVersion.RELEASE_8
	}

	override fun getSupportedAnnotationTypes(): Set<String> {
		val annotationTypes = HashSet<String>()
		annotationTypes.add(KSparkApplication::class.java.canonicalName)

		return annotationTypes
	}

	private fun error(element: Element, errorMessage: String, vararg args: Any) {
		var message = errorMessage
		if (args.size > 0) {
			message = String.format(message, *args)
		}
		processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, message, element)
	}*/
