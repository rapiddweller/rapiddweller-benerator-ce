package com.rapiddweller.benerator.engine.parser.xml;

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.engine.BeneratorRootStatement;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.parser.string.IdParser;
import com.rapiddweller.benerator.engine.parser.string.ScriptableParser;
import com.rapiddweller.benerator.engine.statement.DefineStorageSystemStatement;
import com.rapiddweller.benerator.engine.statement.IfStatement;
import com.rapiddweller.common.Expression;
import com.rapiddweller.common.exception.ExceptionFactory;
import com.rapiddweller.common.exception.ParseException;
import com.rapiddweller.common.parser.StringParser;
import com.rapiddweller.format.xml.AttrInfo;
import com.rapiddweller.format.xml.AttrInfoSupport;
import com.rapiddweller.platform.nosql.CustomStorageSystem;
import com.rapiddweller.platform.nosql.mongo.MongoDBSystem;
import com.rapiddweller.script.expression.ConstantExpression;
import com.rapiddweller.script.expression.ExpressionUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.rapiddweller.benerator.engine.DescriptorConstants.*;
import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.*;
import static java.lang.String.format;

public class StorageSystemParser extends AbstractBeneratorDescriptorParser {

    private static final ExceptionFactory EXCEPTION_FACTORY = ExceptionFactory.getInstance();

    private static final ScriptableParser<String> PARAMETER_PARSER =
            new ScriptableParser<>(new StringParser("parameter"));

    private final Map<String, Class<? extends CustomStorageSystem>> storageSystemClasses = Map.of(
            "mongodb", MongoDBSystem.class
    );

    private static final AttrInfo<String> ID_ATT_INFO = new AttrInfo<>(
            ATT_ID, true, BeneratorErrorIds.SYN_SYSTEM_STORAGE_ID, new IdParser(), null);

    private static final AttrInfoSupport ATTR_INFO_SUPPORT =
            new AttrInfoSupport(BeneratorErrorIds.SYN_SYSTEM_STORAGE_ATTR, ID_ATT_INFO);

    protected StorageSystemParser() {
        super(EL_STORAGE_SYSTEM, ATTR_INFO_SUPPORT, BeneratorRootStatement.class, IfStatement.class);
    }

    @Override
    public Statement doParse(Element element, Element[] parentXmlPath, Statement[] parentComponentPath, BeneratorParseContext context) {
        Expression<String> id = parseScriptableStringAttribute(ATT_ID, element);
        Expression<Class<? extends CustomStorageSystem>> clazz = getClass(element, id.evaluate(context));
        return new DefineStorageSystemStatement(id, clazz, getClassParams(element), context.getResourceManager());
    }

    private Expression<Class<? extends CustomStorageSystem>> getClass(Element element, String id) {
        Node classNode = element.getElementsByTagName("class").item(0);
        if (Objects.nonNull(classNode)) {
            String className = classNode.getTextContent();
            return ExpressionUtil.constant(getClassFromJava(className, id));
        } else {
            Node typeNode = element.getElementsByTagName("type").item(0);
            String className = typeNode.getTextContent();
            return ExpressionUtil.constant(getClassFromMap(className, id));
        }
    }

    private Class<? extends CustomStorageSystem> getClassFromJava(String className, String id) {
        try {
            Class<?> clazz = Class.forName(className);
            if (CustomStorageSystem.class.isAssignableFrom(clazz)) {
                return (Class<? extends CustomStorageSystem>) clazz;
            } else {
                throw classNotSuperclass(className);
            }
        } catch (ClassNotFoundException e) {
            throw notClassNotFoundException(className, id);
        }
    }

    private Class<? extends CustomStorageSystem> getClassFromMap(String className, String id) {
        if (storageSystemClasses.containsKey(className)) {
            return storageSystemClasses.get(className);
        } else {
            throw notClassNotFoundException(className, id);
        }
    }

    private static ParseException notClassNotFoundException(String className, String id) {
        throw EXCEPTION_FACTORY.parsingError(format("Cannot find class %s for storage system %s.", className, id));
    }

    private static ParseException classNotSuperclass(String className) {
        String message = "Cannot create storage system from class %s, because it is not a superclass of DefaultStoreSystem";
        throw EXCEPTION_FACTORY.parsingError(format(message, className));
    }

    private Expression<Map<String, Expression<String>>> getClassParams(Element element) {
        Map<String, Expression<String>> classParams = new HashMap<>();
        Node paramsNode = element.getElementsByTagName("params").item(0);
        if (paramsNode != null) {
            NodeList paramNodes = paramsNode.getChildNodes();
            for (int i = 0; i < paramNodes.getLength(); i++) {
                Node paramNode = paramNodes.item(i);
                if (paramNode.getAttributes() != null) {
                    String paramName = paramNode.getAttributes().getNamedItem("name").getNodeValue();
                    Expression<String> paramValue = PARAMETER_PARSER.parse(paramNode.getTextContent());
                    classParams.put(paramName, paramValue);
                }
            }
        }
        return new ConstantExpression<>(classParams);
    }

}
