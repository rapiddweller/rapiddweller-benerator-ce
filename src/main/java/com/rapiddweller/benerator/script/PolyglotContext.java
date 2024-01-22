package com.rapiddweller.benerator.script;

import com.rapiddweller.common.Context;
import com.rapiddweller.common.exception.ScriptException;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.platform.map.Entity2MapConverter;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;

class PolyglotContext {

  private static final Logger logger = LoggerFactory.getLogger(PolyglotContext.class);
  private String previousMissingObject;
  private final org.graalvm.polyglot.Context polyglotCtx;

  public void updatePolyglotLocalFromGlobal(Context context, String language) {
    for (String entry : polyglotCtx.getBindings(language).getMemberKeys()) {
      migrateBeneratorContext2GraalVM(context, language, entry);
    }
  }

  public void migrateBeneratorContext2GraalVM(Context context, String language, String valueKey) {
    Object valueType;
    try {
      Object obj = context.get(valueKey);
      if (obj != null) {
        valueType = obj.getClass();
        // check if Entity Object
        if (Entity.class.equals(valueType)) {
          logger.debug("Entity found : {}", valueKey);
          Map<String, Object> map = new Entity2MapConverter().convert((Entity) obj);
          // to access items of map in polyglotCtx it is nessesary to create an ProxyObject
          ProxyObject proxy = ProxyObject.fromMap(map);
          polyglotCtx.getBindings(language).putMember(valueKey, proxy);
        } else {
          logger.debug("{} found : {}", valueType.getClass(), valueKey);
          polyglotCtx.getBindings(language).putMember(valueKey, obj);
        }
      }
    } catch (NullPointerException e) {
      logger.error("Context {} was NULL, this should not happen!", context);
    }

  }

  synchronized public Value evalScript(Context context, String text, String language) throws ScriptException {
    final int MAX_ATTEMPTS = 3;  // Maximum attempts for the same missing object
    Value returnValue = null;

    try {
      this.updatePolyglotLocalFromGlobal(context, language);
      returnValue = polyglotCtx.eval(language, text);
    } catch (org.graalvm.polyglot.PolyglotException e) {
      String missingObject = extractMissingObjectName(e.getMessage());
      if (missingObject != null) {
        if (!Objects.equals(previousMissingObject, missingObject)) {
          previousMissingObject = missingObject;
          missingObjectAttempts = 1;  // Reset the attempt count for a new missing object
          this.migrateBeneratorContext2GraalVM(context, language, missingObject);
          returnValue = evalScript(context, text, language);
        } else if (missingObjectAttempts < MAX_ATTEMPTS) {
          missingObjectAttempts++;  // Increment attempt count for the same missing object
          this.migrateBeneratorContext2GraalVM(context, language, missingObject);
          returnValue = evalScript(context, text, language);
        } else {
          throw new ScriptException("This object couldn't be found: " + missingObject, null);
        }
      } else {
        throw new ScriptException(e.getMessage(), null);
      }
    }
    return returnValue;
  }

  private String extractMissingObjectName(String errorMessage) {
    if (errorMessage.contains("ReferenceError: ")) {
      return errorMessage.replace("ReferenceError: ", "").replace(" is not defined", "");
    } else if (errorMessage.contains("NameError: ")) {
      return errorMessage.replace("NameError: name '", "").replace("' is not defined", "");
    }
    return null;
  }

  private int missingObjectAttempts = 0;

  public PolyglotContext() {

    if (isGraalVM()) {
      this.polyglotCtx = org.graalvm.polyglot.Context
          .newBuilder("js", "python")
          .allowIO(true)
          .option("python.ForceImportSite", "true")
          .allowAllAccess(true).build();
    } else {
      this.polyglotCtx = org.graalvm.polyglot.Context
          .newBuilder("js")
          .allowIO(true)
          .allowAllAccess(true).build();
    }
  }

  private boolean isGraalVM() {
    String javaVmName = System.getProperty("org.graalvm.home");
    return javaVmName != null;
  }
}
