package io.github.artynova.annotations.source;

import io.github.artynova.Promptable;
import io.github.artynova.annotations.runtime.PromptMessage;
import io.github.artynova.utils.NameUtils;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Processor of the {@link MakePromptable} and {@link MakePromptables}
 * source-retained annotations.
 */
@SupportedAnnotationTypes({
    "com.nova.prompter.annotations.source.MakePromptable",
    "com.nova.prompter.annotations.source.MakePromptables"})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public final class MakePromptableProcessor extends AbstractProcessor {
    /**
     * Stored reference to the {@link Messager} of the environment.
     */
    private Messager messager;
    /**
     * Stored reference to the {@link Elements} of the environment.
     */
    private Elements elementUtils;
    /**
     * Stored reference to the {@link Filer} of the environment.
     */
    private Filer filer;

    @Override
    public synchronized void init(final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations,
        final RoundEnvironment roundEnv) {
        Set<? extends Element> annotatedElements =
            roundEnv.getElementsAnnotatedWith(MakePromptable.class);
        for (Element annotatedElement : annotatedElements) {
            processElement(annotatedElement, false);
        }
        annotatedElements =
            roundEnv.getElementsAnnotatedWith(MakePromptables.class);
        for (Element annotatedElement : annotatedElements) {
            processElement(annotatedElement, true);
        }
        return true;
    }

    private void processElement(final Element element, final boolean array) {
        if (element.getKind() != ElementKind.PACKAGE) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                "@MakePromptable and @MakePromptables can only be"
                    + " applied to classes.",
                element);
        } else {
            try {
                generateCode((PackageElement) element, array);
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage(),
                    element);
            }
        }
    }

    private void generateCode(final PackageElement annotatedElement,
        final boolean array)
        throws IOException {
        String packageName = annotatedElement.getQualifiedName().toString();
        MakePromptable[] promptables =
            array ? annotatedElement.getAnnotation(MakePromptables.class)
                .value() // if repeated
                : new MakePromptable[] {annotatedElement.getAnnotation(
                    MakePromptable.class)}; // if not repeated
        for (MakePromptable promptable : promptables) {
            generatePromptable(packageName, promptable);
        }
    }

    private void generatePromptable(final String packageName,
        final MakePromptable annotation) throws IOException {
        TypeSpec classSpec = TypeSpec.classBuilder(annotation.name())
            .addModifiers(Modifier.PUBLIC).addSuperinterface(
                Promptable.class) // in case superclass is not Promptable
            .superclass(extractBaseTypeName(annotation))
            .addFields(generateFields(annotation.properties()))
            .addMethods(generateMethods(annotation.properties())).build();

        JavaFile javaFile =
            JavaFile.builder(packageName, classSpec).indent("    ").build();

        javaFile.writeTo(filer);
    }

    private List<FieldSpec> generateFields(final PromptProperty[] properties) {
        List<FieldSpec> fieldSpecs = new ArrayList<>();

        for (PromptProperty property : properties) {
            FieldSpec fieldSpec =
                FieldSpec.builder(extractPropertyTypeName(property),
                    property.name(), Modifier.PRIVATE).build();
            fieldSpecs.add(fieldSpec);
        }

        return fieldSpecs;
    }

    private List<MethodSpec> generateMethods(
        final PromptProperty[] properties) {
        List<MethodSpec> methodSpecs = new ArrayList<>();

        for (PromptProperty property : properties) {
            methodSpecs.add(getterMethod(property));
            methodSpecs.add(setterMethod(property));
        }

        return methodSpecs;
    }

    private MethodSpec getterMethod(final PromptProperty property) {
        String methodName =
            "get" + property.name().substring(0, 1).toUpperCase()
                + property.name().substring(1);
        return MethodSpec.methodBuilder(methodName)
            .addModifiers(Modifier.PUBLIC)
            .returns(extractPropertyTypeName(property))
            .addStatement("return $N", property.name()).build();
    }

    private MethodSpec setterMethod(final PromptProperty property) {
        String methodName =
            "set" + property.name().substring(0, 1).toUpperCase()
                + property.name().substring(1);
        MethodSpec.Builder setterSpecBuilder =
            MethodSpec.methodBuilder(methodName).addModifiers(Modifier.PUBLIC)
                .addAnnotation(generateMessageAnnotation(property))
                .returns(void.class)
                .addParameter(extractPropertyTypeName(property),
                    property.name());
        if (property.required()) {
            setterSpecBuilder.addStatement(
                "if ($N == null) throw new IllegalArgumentException(\"$N"
                    + " cannot be missing\")",
                property.name(), NameUtils.capitalizeFirstLetter(
                    NameUtils.humanReadableName(property.name())));
        }
        return setterSpecBuilder.addStatement("this.$N = $N", property.name(),
            property.name()).build();
    }

    private AnnotationSpec generateMessageAnnotation(
        final PromptProperty property) {
        return AnnotationSpec.builder(PromptMessage.class)
            .addMember("value", "$S", property.message()).build();
    }

    // MirroredTypeException provides the TypeMirror for the inaccessible Class
    private TypeName extractBaseTypeName(final MakePromptable promptable) {
        try {
            return TypeName.get(elementUtils.getTypeElement(
                    promptable.baseClass().getCanonicalName())
                .asType()); // throws because of promptable.baseClass() access
        } catch (MirroredTypeException e) {
            return TypeName.get(e.getTypeMirror());
        }
    }

    private TypeName extractPropertyTypeName(final PromptProperty property) {
        try {
            return TypeName.get(elementUtils.getTypeElement(
                    property.fieldClass().getCanonicalName())
                .asType()); // throws because of property.fieldClass() access
        } catch (MirroredTypeException e) {
            return TypeName.get(e.getTypeMirror());
        }
    }
}
