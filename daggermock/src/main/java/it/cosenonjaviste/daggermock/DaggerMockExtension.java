package it.cosenonjaviste.daggermock;

import java.lang.reflect.Field;
import java.util.*;

import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.util.ReflectionUtils;

public class DaggerMockExtension implements BeforeEachCallback {

    @SuppressWarnings("unchecked")
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        context.getTestInstance().ifPresent(testInstance -> {
            DaggerMockTest annotation = testInstance.getClass().getDeclaredAnnotation(DaggerMockTest.class);

            ArrayList<Object> modules = new ArrayList<>();

            List<Field> fields = AnnotationSupport.findAnnotatedFields(testInstance.getClass(),
                    DaggerMockModules.class);

            for (Field field : fields) {
                try {
                    modules.addAll((Collection<Object>) ReflectionUtils.tryToReadFieldValue(field, testInstance).get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //TODO Extract helper class from Rule to remove junit 4 dependency
            DaggerMockRule<?> rule = new DaggerMockRule<>(annotation.value(), modules.toArray());

            try {
                rule.initMocks(testInstance);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
    }
}
