package ch.heigvd.res.lab01.impl.transformers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.apache.commons.io.FileUtils;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Olivier Liechti
 */
public class FileTransformerTest {
  
  @Test
  public void itShouldDuplicateATextFile() throws IOException {
    FileUtils.deleteDirectory(new File("./tmp"));
    new File("./tmp").mkdir();
    FileTransformer ft = new NoOpFileTransformer();
    File inputFile = new File("./tmp/test.txt");
    File outputFile = new File("./tmp/test.txt.out");
    OutputStreamWriter writer = new OutputStreamWriter( new FileOutputStream(inputFile), "UTF-8" );
    writer.write("Les bons élèves sont tous très assidus.\nLes bons maîtres sont appliqués.");
    writer.flush();
    writer.close();
    ft.visit(inputFile);
      System.out.println(FileUtils.checksumCRC32(inputFile));
      System.out.println(FileUtils.checksumCRC32(outputFile));
    assertTrue( FileUtils.contentEquals(inputFile, outputFile) );
    FileUtils.deleteDirectory(new File("./tmp"));
  }
  
}
