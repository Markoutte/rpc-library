package me.markoutte.libs.rpc.processing;

import me.markoutte.libs.rpc.server.RpcServerConfiguration;
import org.openide.filesystems.annotations.LayerBuilder;
import org.openide.filesystems.annotations.LayerBuilder.File;
import org.openide.filesystems.annotations.LayerGeneratingProcessor;
import org.openide.filesystems.annotations.LayerGenerationException;
import org.openide.util.lookup.ServiceProvider;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ServiceProvider(service = Processor.class)
@SupportedAnnotationTypes({"me.markoutte.libs.rpc.server.RpcServerConfiguration"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class RpcServerConfigurationAnnotationProcessor extends LayerGeneratingProcessor {

    @Override
    protected boolean handleProcess(Set<? extends TypeElement> set, RoundEnvironment re) throws LayerGenerationException {

        Set<? extends Element> annotated = re.getElementsAnnotatedWith(RpcServerConfiguration.class);
        
        for (Element e : annotated) {
            RpcServerConfiguration annotation = e.getAnnotation(RpcServerConfiguration.class);
            if (annotation == null) {
                continue;
            }
            
            LayerBuilder builder = layer(e);
            String id = binaryName(e.asType()).replace(".", "-");
            File folder = builder.folder(String.format("%s/%s.instance", RpcServerConfigurationProperties.FOLDER, id));

            folder.stringvalue("configuration", annotation.configuration());
            folder.stringvalue("host", annotation.host());
            folder.intvalue("port", annotation.port());

            folder.write();

            // see http://hauchee.blogspot.com/2015/12/compile-time-annotation-processing-getting-class-value.html
            List<? extends AnnotationMirror> mirrors = e.getAnnotationMirrors();
            for (AnnotationMirror mirror : mirrors) {
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : mirror.getElementValues().entrySet()) {
                    switch (entry.getKey().getSimpleName().toString()) {
                        case "services":
                            List<? extends AnnotationValue> mrs = (List<? extends AnnotationValue>) entry.getValue().getValue();
                            for (AnnotationValue mr : mrs) {
                                File f = builder.file(String.format("%s/%s.instance", folder.getPath(), binaryName((TypeMirror) mr.getValue())));
                                f.write();
                            }
                    }
                }
            }
        }

        return true;
    }

    private String binaryName(TypeMirror t) {
        Element e = processingEnv.getTypeUtils().asElement(t);
        if (e != null && (e.getKind().isClass() || e.getKind().isInterface())) {
            return processingEnv.getElementUtils().getBinaryName((TypeElement) e).toString();
        } else {
            return t.toString(); // fallback - might not always be right
        }
    }
}
