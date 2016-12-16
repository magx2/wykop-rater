package pl.grzeslowski.wykop.scrapper;

import com.google.common.base.Preconditions;

import java.io.File;

public class TestUtils {
    private TestUtils() {

    }

    @SuppressWarnings("ConstantConditions")
    public static File loadFileFromClassLoader(String name) {
        final String path = TestUtils.class
                .getClassLoader()
                .getResource(name)
                .getFile();
        final File file = new File(path);
        Preconditions.checkArgument(file.exists());
        return file;
    }
}
