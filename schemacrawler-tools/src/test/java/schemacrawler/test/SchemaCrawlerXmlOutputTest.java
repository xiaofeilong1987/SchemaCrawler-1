/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.test;


import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.text.operation.Operation;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import schemacrawler.utility.TestDatabase;
import sf.util.TestUtility;

public class SchemaCrawlerXmlOutputTest
{

  private static class LocalEntityResolver
    implements EntityResolver
  {

    public InputSource resolveEntity(final String publicId,
                                     final String systemId)
      throws SAXException, IOException
    {
      final String localResource = "/xhtml1"
                                   + systemId.substring(systemId
                                     .lastIndexOf('/'));
      final InputStream entityStream = LocalEntityResolver.class
        .getResourceAsStream(localResource);
      if (entityStream == null)
      {
        throw new IOException("Could not load " + localResource);
      }
      return new InputSource(entityStream);
    }

  }

  private static TestDatabase testUtility = new TestDatabase();

  @AfterClass
  public static void afterAllTests()
  {
    testUtility.shutdownDatabase();
  }

  @BeforeClass
  public static void beforeAllTests()
    throws Exception
  {
    TestDatabase.initializeApplicationLogging();
    testUtility.createMemoryDatabase();
    XMLUnit.setControlEntityResolver(new LocalEntityResolver());
  }

  @Test
  public void countOperatorValidXMLOutput()
    throws Exception
  {
    checkValidXmlOutput(Operation.count.name());
  }

  @Test
  public void dumpOperatorValidXMLOutput()
    throws Exception
  {
    checkValidXmlOutput(Operation.dump.name());
  }

  @Test
  public void list_objectsValidXMLOutput()
    throws Exception
  {
    checkValidXmlOutput(SchemaTextDetailType.list_objects.name());
  }

  @Test
  public void standard_schemaValidXMLOutput()
    throws Exception
  {
    checkValidXmlOutput(SchemaTextDetailType.standard_schema.name());
  }

  @Test
  public void verbose_schemaValidXMLOutput()
    throws Exception
  {
    checkValidXmlOutput(SchemaTextDetailType.verbose_schema.name());
  }

  private void checkValidXmlOutput(final String command)
    throws IOException, Exception, SchemaCrawlerException
  {
    final String referenceFile = command + ".html";
    final File testOutputFile = File.createTempFile("schemacrawler."
                                                        + referenceFile + ".",
                                                    ".test");

    final OutputOptions outputOptions = new OutputOptions(OutputFormat.html
      .name(), testOutputFile);
    outputOptions.setNoHeader(false);
    outputOptions.setNoFooter(false);
    outputOptions.setNoInfo(false);

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);

    final SchemaCrawlerOptions options = executable.getSchemaCrawlerOptions();
    options.setSchemaInfoLevel(SchemaInfoLevel.minimum());
    options.setShowStoredProcedures(true);

    executable.setOutputOptions(outputOptions);
    executable.execute(testUtility.getConnection());

    final List<String> failures = new ArrayList<String>();

    TestUtility.compareOutput("xml_output/" + referenceFile,
                              testOutputFile,
                              OutputFormat.html,
                              failures);

    if (failures.size() > 0)
    {
      fail(failures.toString());
    }
  }

}
