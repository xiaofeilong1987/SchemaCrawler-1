/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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

package schemacrawler.tools.datatext;


import java.util.logging.Logger;

import javax.sql.DataSource;

import schemacrawler.crawl.SchemaCrawlerException;
import schemacrawler.execute.DataHandler;
import schemacrawler.execute.QueryExecutor;
import schemacrawler.tools.Executable;
import schemacrawler.tools.OutputOptions;

/**
 * Basic SchemaCrawler executor.
 * 
 * @author Sualeh Fatehi
 */
public class DataToolsExecutable
  extends Executable<DataTextFormatOptions>
{

  private static final Logger LOGGER = Logger
    .getLogger(DataToolsExecutable.class.getName());

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.Executable#execute(javax.sql.DataSource)
   */
  @Override
  public void execute(final DataSource dataSource)
    throws Exception
  {
    final DataHandler dataHandler = createDataHandler(toolOptions);
    final QueryExecutor executor = new QueryExecutor(dataSource, dataHandler);
    executor.executeSQL(toolOptions.getQuery().getQuery());
  }

  /**
   * Instantiates a text formatter type of DataHandler from the mnemonic
   * string.
   * 
   * @param options
   *        Options
   * @throws SchemaCrawlerException
   *         On an exception
   * @return CrawlHandler instance
   */
  public static DataHandler createDataHandler(final DataTextFormatOptions options)
    throws SchemaCrawlerException
  {

    if (options.getOutputOptions().getOutputFormat() == null)
    {
      return null;
    }

    DataHandler handler = null;
    final OutputOptions outputOptions = options.getOutputOptions();
    switch (outputOptions.getOutputFormat())
    {
      case text:
        handler = new DataPlainTextFormatter(options);
        break;
      case html:
        handler = new DataHTMLFormatter(options);
        break;
      case csv:
        handler = new DataCSVFormatter(options);
        break;
      default:
        throw new IllegalArgumentException("Unknown output format specified - "
                                           + outputOptions
                                             .getOutputFormatValue());
    }

    return handler;
  }

}
