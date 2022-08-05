package com.rapiddweller.benerator.script;

import com.rapiddweller.common.Context;
import com.rapiddweller.common.exception.ScriptException;
import com.rapiddweller.model.data.Entity;
import com.rapiddweller.platform.map.Entity2MapConverter;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                    // TODO: might should create an Entity2ProxyObjectConverter in 3.0.0
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

        Value returnValue = null;
        try {
            this.updatePolyglotLocalFromGlobal(context, language);
            returnValue = polyglotCtx.eval(language, text);
        } catch (org.graalvm.polyglot.PolyglotException e) {
            if (e.getMessage().contains("is not defined")) {
                String missingObject = e.getMessage().replace("ReferenceError: ", "").replace(" is not defined", "");
                if (!Objects.equals(previousMissingObject, missingObject)) {
                    this.migrateBeneratorContext2GraalVM(context, language, missingObject);
                    returnValue = evalScript(context, text, language);
                    previousMissingObject = missingObject;
                }
            } else {
                throw new ScriptException(e.getMessage(), null);
            }
        }
        return returnValue;

    }

    public PolyglotContext() {
        this.polyglotCtx = org.graalvm.polyglot.Context
                .newBuilder("js", "python")
                .allowAllAccess(true).build();
    }
}
