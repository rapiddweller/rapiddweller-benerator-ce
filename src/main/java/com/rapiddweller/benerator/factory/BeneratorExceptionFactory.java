/* (c) Copyright 2021 by Volker Bergmann. All rights reserved. */

package com.rapiddweller.benerator.factory;

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.BeneratorFactory;
import com.rapiddweller.benerator.BeneratorUtil;
import com.rapiddweller.benerator.IllegalGeneratorStateException;
import com.rapiddweller.common.StringUtil;
import com.rapiddweller.common.cli.CLIIllegalArgumentException;
import com.rapiddweller.common.cli.CLIIllegalOptionException;
import com.rapiddweller.common.cli.CLIIllegalOptionValueException;
import com.rapiddweller.common.cli.CLIMissingOptionValueException;
import com.rapiddweller.common.exception.ApplicationException;
import com.rapiddweller.common.exception.ComponentInitializationFailure;
import com.rapiddweller.common.exception.ConnectFailedException;
import com.rapiddweller.common.exception.ExceptionFactory;
import com.rapiddweller.common.exception.ExitCodes;
import com.rapiddweller.common.exception.SyntaxError;
import com.rapiddweller.common.file.FileResourceNotFoundException;
import com.rapiddweller.format.xml.XMLElementParserFactory;
import com.rapiddweller.task.Task;
import com.rapiddweller.task.TaskUnavailableException;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

/**
 * Extends {@link ExceptionFactory} with Benerator-specific factory methods.<br/><br/>
 * Created: 18.11.2021 06:25:06
 * @author Volker Bergmann
 * @since 2.1.0
 */
public class BeneratorExceptionFactory extends ExceptionFactory {

  public static final String ATTRIBUTE_ILLEGAL_FOR_ELEMENT = "Illegal attribute for element";

  public static BeneratorExceptionFactory getInstance() {
    ExceptionFactory result = ExceptionFactory.getInstance();
    if (!(result instanceof BeneratorExceptionFactory)) {
      result = BeneratorFactory.getInstance().createExceptionFactory();
      ExceptionFactory.setInstance(result);
    }
    return (BeneratorExceptionFactory) result;
  }

  public FileResourceNotFoundException beneratorFileNotFound(String uri) {
      return new FileResourceNotFoundException(BeneratorErrorIds.BEN_FILE_NOT_FOUND,
          "Benerator file not found: " + uri);
  }

  public IllegalGeneratorStateException illegalGeneratorState(String message) {
    return illegalGeneratorState(message, null);
  }

  public IllegalGeneratorStateException illegalGeneratorState(String message, Exception cause) {
    return new IllegalGeneratorStateException(message, cause);
  }

  public TaskUnavailableException taskUnavailable(Task task, Long requiredInvocations, long actualInvocations) {
    return new TaskUnavailableException(task, requiredInvocations, actualInvocations);
  }

  @Override
  public CLIIllegalArgumentException illegalCommandLineArgument(String message) {
    return new CLIIllegalArgumentException(message, BeneratorErrorIds.CLI_ILLEGAL_ARGUMENT);
  }

  @Override
  public CLIIllegalOptionException illegalCommandLineOption(String optionName) {
    return new CLIIllegalOptionException(optionName, BeneratorErrorIds.CLI_ILLEGAL_OPTION);
  }

  @Override
  public CLIMissingOptionValueException missingCommandLineOptionValue(String name) {
    return new CLIMissingOptionValueException(name, BeneratorErrorIds.CLI_MISSING_OPTION_VALUE);
  }

  @Override
  public CLIIllegalOptionValueException illegalCommandLineOptionValue(String name, String value) {
    if ("--mode".equals(name)) {
      return new CLIIllegalOptionValueException(name, value, BeneratorErrorIds.CLI_ILLEGAL_MODE,
          "Illegal mode value: " + value + ". Allowed values are lenient, strict and turbo");
    } else if ("--list".equals(name)) {
      return new CLIIllegalOptionValueException(name, value, BeneratorErrorIds.CLI_ILLEGAL_LIST,
          "Illegal value for list option: " + value + ". Allowed values are db and kafka");
    } else {
      return new CLIIllegalOptionValueException(name, value, BeneratorErrorIds.CLI_ILLEGAL_OPTION_VALUE, null);
    }
  }

  public ComponentInitializationFailure componentInitializationFailed(String componentName, Throwable cause, String errorId) {
     return new ComponentInitializationFailure(errorId, componentName, cause);
  }

  @Override
  public SyntaxError syntaxErrorForUri(String message, Throwable cause, String uri, int line, int column) {
    if (cause != null) {
      if (message.startsWith("Premature end of file")  && line == -1 && column == -1) {
        if (BeneratorUtil.isDescriptorFilePath(uri)) {
          return SyntaxError.forUri("Empty Benerator file", uri, BeneratorErrorIds.SYN_EMPTY_BEN_FILE);
        } else {
          return SyntaxError.forUri("Empty XML file", uri, BeneratorErrorIds.SYN_EMPTY_XML_FILE);
        }
      } else if (message.startsWith("Content is not allowed in prolog")) {
        return SyntaxError.forUri("File does not start with <?xml...?> or a tag", uri,
            BeneratorErrorIds.SYN_NO_XML_FILE);
      }
      message = StringUtil.removeSuffixIfPresent(".", message);
      return SyntaxError.forUri(message, uri, null);
    }
    return SyntaxError.forUri("Syntax error", uri, null);
  }

  @Override
  public SyntaxError syntaxErrorForXmlElement(String message, Throwable cause, String errorId, Element element) {
    if (errorId == null || !errorId.startsWith("BEN-")) {
      if (message.startsWith(XMLElementParserFactory.ILLEGAL_ROOT_ELEMENT)) {
        errorId = BeneratorErrorIds.SYN_ILLEGAL_ROOT;
      } else if (message.startsWith(XMLElementParserFactory.ILLEGAL_ELEMENT)) {
        errorId = BeneratorErrorIds.SYN_ILLEGAL_ELEMENT;
      } else if (message.startsWith(XMLElementParserFactory.ILLEGAL_CHILD_ELEMENT)) {
        errorId = BeneratorErrorIds.SYN_MISPLACED_ELEMENT;
      } else if (message.startsWith(ATTRIBUTE_ILLEGAL_FOR_ELEMENT)) {
        errorId = BeneratorErrorIds.SYN_ILLEGAL_ELEMENT;
      }
    }
    return SyntaxError.forXmlElement(message, cause, errorId, element);
  }

  @Override
  public SyntaxError illegalXmlAttributeValue(String message, Throwable cause, String errorId, Attr attribute) {
    return SyntaxError.forXmlAttribute(message, cause, errorId, attribute);
  }

  @Override
  public ConnectFailedException connectFailed(String message, Throwable cause) {
    return new ConnectFailedException(message, cause, BeneratorErrorIds.DB_CONNECT_FAILED);
  }

  @Override
  public ApplicationException outOfMemory(Throwable e) {
    return new ApplicationException(BeneratorErrorIds.OUT_OF_MEMORY, ExitCodes.MISCELLANEOUS_ERROR, "Out of memory", e);
  }

}
