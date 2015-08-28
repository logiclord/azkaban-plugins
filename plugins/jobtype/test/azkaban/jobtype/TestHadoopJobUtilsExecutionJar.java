package azkaban.jobtype;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import azkaban.jobtype.HadoopSparkJob;
import azkaban.jobtype.SparkJobArg;
import azkaban.utils.Props;

public class TestHadoopJobUtilsExecutionJar {
  Props jobProps = null;

  Logger logger = Logger.getRootLogger();

  String workingDirString = "/tmp/TestHadoopSpark";

  File workingDirFile = new File(workingDirString);

  File libFolderFile = new File(workingDirFile, "lib");

  String executionJarName = "hadoop-spark-job-test-execution-x.y.z-a.b.c.jar";

  File executionJarFile = new File(libFolderFile, "hadoop-spark-job-test-execution-x.y.z-a.b.c.jar");

  File libraryJarFile = new File(libFolderFile, "library.jar");

  String delim = SparkJobArg.delimiter;

  @BeforeTest
  public void beforeTest() throws IOException {

  }

  @BeforeMethod
  public void beforeMethod() throws IOException {
    if (workingDirFile.exists())
      FileUtils.deleteDirectory(workingDirFile);
    workingDirFile.mkdirs();
    libFolderFile.mkdirs();
    executionJarFile.createNewFile();
    libraryJarFile.createNewFile();

  }

  // nothing should happen
  @Test
  public void testNoLibFolder() throws IOException {
    FileUtils.deleteDirectory(libFolderFile);
    String retval = HadoopJobUtils.resolveWildCardForJarSpec(workingDirString, "./lib/*", logger);

    Assert.assertEquals(retval, "");
  }
  
  @Test
  public void testOneLibFolderExpansion() throws IOException {
    
    String retval = HadoopJobUtils.resolveWildCardForJarSpec(workingDirString, "./lib/*", logger);

    Assert.assertEquals(retval, "/tmp/TestHadoopSpark/./lib/library.jar,/tmp/TestHadoopSpark/./lib/hadoop-spark-job-test-execution-x.y.z-a.b.c.jar");
  }
  
  @Test
  public void testTwoLibFolderExpansion() throws IOException {
    File lib2FolderFile = new File(workingDirFile, "lib2");
    lib2FolderFile.mkdirs();
    File lib2test1Jar = new File(lib2FolderFile, "test1.jar");
    lib2test1Jar.createNewFile();
    File lib2test2Jar = new File(lib2FolderFile, "test2.jar");
    lib2test2Jar.createNewFile();
    String retval = HadoopJobUtils.resolveWildCardForJarSpec(workingDirString, "./lib/*,./lib2/*", logger);

    Assert.assertEquals(retval, "/tmp/TestHadoopSpark/./lib/library.jar,/tmp/TestHadoopSpark/./lib/hadoop-spark-job-test-execution-x.y.z-a.b.c.jar,/tmp/TestHadoopSpark/./lib2/test1.jar,/tmp/TestHadoopSpark/./lib2/test2.jar");
  }
}