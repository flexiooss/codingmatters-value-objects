package org.codingmatters.value.objects.php;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class RunPhpTest {

    public static final int COMPOSER_INSTALL_TIMEOUT_IN_MINUTES = 10;
    public static final int PHP_UNIT_TEST_TIMEOUT_IN_SECONDS = 20;

    private static ProcessBuilder processBuilder;

    @BeforeClass
    public static void setUp() throws Exception {
        String dir = System.getProperty("project.build.directory") + "/php-test";
        File directory = new File(dir);
        processBuilder = new ProcessBuilder();
        processBuilder.directory(directory);
        processBuilder.command("composer", "install");
        System.out.println("Running composer install...");
        Process process = processBuilder.start();
        assertTrue("composer install took too much time", process.waitFor(10, TimeUnit.MINUTES));
        if (process.exitValue() != 0) {
            printError(process);
        }
        assertThat(process.exitValue(), is(0));
    }

    private static void printError(Process process) throws IOException {
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (InputStream stream = process.getInputStream()) {
            while (stream.read(buffer) != -1) {
                out.write(buffer);
            }
            System.out.println("Out = " + new String(out.toByteArray()));
        }
        try (InputStream stream = process.getErrorStream()) {
            while (stream.read(buffer) != -1) {
                out.write(buffer);
            }
            System.out.println("Error = " + new String(out.toByteArray()));
        }
    }

    @Test
    public void test_01_emptyObject() throws IOException, InterruptedException {
        processBuilder.command("./vendor/bin/phpunit", "test/01_emptyObject.php");
        Process process = processBuilder.start();
        process.waitFor(PHP_UNIT_TEST_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
        if (process.exitValue() != 0) {
            printError(process);
        }
        assertThat(process.exitValue(), is(0));
    }

    @Test
    public void test_02_primitiveProps() throws InterruptedException, IOException {
        processBuilder.command("./vendor/bin/phpunit", "test/02_primitiveTypesTest.php");
        Process process = processBuilder.start();
        process.waitFor(PHP_UNIT_TEST_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
        if (process.exitValue() != 0) {
            printError(process);
        }
        assertThat(process.exitValue(), is(0));
    }

    @Test
    public void test_03_primitiveLists() throws IOException, InterruptedException {
        processBuilder.command("./vendor/bin/phpunit", "test/03_primitiveListTest.php");
        Process process = processBuilder.start();
        process.waitFor(PHP_UNIT_TEST_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
        if (process.exitValue() != 0) {
            printError(process);
        }
        assertThat(process.exitValue(), is(0));
    }

    @Test
    public void test_04_embeddedValueSpec() throws IOException, InterruptedException {
        processBuilder.command("./vendor/bin/phpunit", "test/04_embeddedValueSpecTest.php");
        Process process = processBuilder.start();
        process.waitFor(PHP_UNIT_TEST_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
        if (process.exitValue() != 0) {
            printError(process);
        }
        assertThat(process.exitValue(), is(0));
    }

    @Test
    public void test_05_enums() throws InterruptedException, IOException {
        processBuilder.command("./vendor/bin/phpunit", "test/05_inSpecEnumTest.php");
        Process process = processBuilder.start();
        process.waitFor(PHP_UNIT_TEST_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
        if (process.exitValue() != 0) {
            printError(process);
        }
        assertThat(process.exitValue(), is(0));
    }

    @Test
    public void test_07_externalValues() throws IOException, InterruptedException {
        processBuilder.command("./vendor/bin/phpunit", "test/07_externalValueObject.php");
        Process process = processBuilder.start();
        process.waitFor(PHP_UNIT_TEST_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
        if (process.exitValue() != 0) {
            printError(process);
        }
        assertThat(process.exitValue(), is(0));
    }

}
