package uk.co.ljd.ksparkmvc.processor

import uk.co.ljd.ksparkmvc.annotations.KSparkApplication
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import kotlin.reflect.KClass

class ExampleProcessor : AbstractProcessor() {

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
			val simpleName = element.simpleName.toString()
			val generatedJavaClassName = generatedFilePrefix.capitalize() + simpleName.capitalize() + generatedFileSuffix

			filer.createSourceFile(packageName + '.' + generatedJavaClassName).openWriter().use { with(it) {
				appendln("package $packageName;")
				appendln("public final class $generatedJavaClassName {}")
			}}

			if (generateKotlinCode && kotlinGenerated != null && element.kind == ElementKind.CLASS) {
				File(kotlinGenerated, "$simpleName.kt").writer().buffered().use {
					it.appendln("package $packageName")
					it.appendln("fun $simpleName.customToString() = \"$generatedJavaClassName: \" + toString()")
				}
			}
		}

		if (options[GENERATE_ERROR] == "true") {
			processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Error from annotation processor!")
		}
	}

	override fun getSupportedSourceVersion() = SourceVersion.RELEASE_8

	override fun getSupportedAnnotationTypes() = ANNOTATION_TO_PREFIX.keys.map { it.java.canonicalName }.toSet()

	override fun getSupportedOptions() = setOf(SUFFIX_OPTION, GENERATE_KOTLIN_CODE_OPTION, GENERATE_ERROR)
}