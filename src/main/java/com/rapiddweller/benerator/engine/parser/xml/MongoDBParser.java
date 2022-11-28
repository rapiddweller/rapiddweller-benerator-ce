package com.rapiddweller.benerator.engine.parser.xml;

import com.rapiddweller.benerator.BeneratorErrorIds;
import com.rapiddweller.benerator.engine.BeneratorRootStatement;
import com.rapiddweller.benerator.engine.Statement;
import com.rapiddweller.benerator.engine.parser.attr.CommonAttrs;
import com.rapiddweller.benerator.engine.parser.attr.ScriptableBooleanAttribute;
import com.rapiddweller.benerator.engine.parser.string.IdParser;
import com.rapiddweller.benerator.engine.parser.string.ScriptableParser;
import com.rapiddweller.benerator.engine.statement.MongoDBStatement;
import com.rapiddweller.benerator.engine.statement.IfStatement;
import com.rapiddweller.benerator.factory.BeneratorExceptionFactory;
import com.rapiddweller.common.Expression;
import com.rapiddweller.common.Validator;
import com.rapiddweller.common.parser.PositiveIntegerParser;
import com.rapiddweller.common.parser.StringParser;
import com.rapiddweller.format.xml.AttrInfo;
import com.rapiddweller.format.xml.AttrInfoSupport;
import org.w3c.dom.Element;

import static com.rapiddweller.benerator.BeneratorErrorIds.*;
import static com.rapiddweller.benerator.engine.DescriptorConstants.*;
import static com.rapiddweller.benerator.engine.parser.xml.DescriptorParserUtil.*;

public class MongoDBParser extends AbstractBeneratorDescriptorParser {

  private static final AttrInfo<String> ID_ATT_INFO = new AttrInfo<>(
      ATT_ID, true, SYN_MONGO_ID, new IdParser(), null);

  private static final AttrInfo<Expression<String>> ENVIRONMENT_ATT_INFO =
      CommonAttrs.environment(BeneratorErrorIds.SYN_DB_ENVIRONMENT, false);

  private static final AttrInfo<Expression<String>> SYSTEM_ATT_INFO =
      CommonAttrs.system(BeneratorErrorIds.SYN_DB_SYSTEM, false);

  private static final AttrInfo<Expression<String>> HOST_ATT_INFO = new AttrInfo<>(
      ATT_HOST, false, SYN_MONGO_HOST,
      new ScriptableParser<>(new StringParser("mongodb host")), null);

  private static final AttrInfo<Expression<Integer>> PORT_ATT_INFO = new AttrInfo<>(
      ATT_PORT, false, SYN_MONGO_PORT,
      new ScriptableParser<>(new PositiveIntegerParser()), null);

  private static final AttrInfo<Expression<String>> DATABASE_ATT_INFO = new AttrInfo<>(
      ATT_DATABASE, false, SYN_MONGO_DATABASE,
      new ScriptableParser<>(new StringParser("mongodb database name")), null);

  private static final AttrInfo<Expression<String>> USER_ATT_INFO = new AttrInfo<>(
      ATT_USER, false, SYN_MONGO_USER,
      new ScriptableParser<>(new StringParser("mongodb user")), null);

  private static final AttrInfo<Expression<String>> PASSWORD_ATT_INFO = new AttrInfo<>(
      ATT_PASSWORD, false, SYN_MONGO_PASSWORD,
      new ScriptableParser<>(new StringParser("mongodb password")), null);

  //mongo db auth db
  private static final AttrInfo<Expression<String>> AUTH_DB_ATT_INFO = new AttrInfo<>(
      ATT_AUTH_DB, false, SYN_MONGO_AUTH_DB,
      new ScriptableParser<>(new StringParser("mongodb auth db")), null);

  //mongo db auth mechanism
  private static final AttrInfo<Expression<String>> AUTH_MECHANISM_ATT_INFO = new AttrInfo<>(
      ATT_AUTH_MECHANISM, false, SYN_MONGO_AUTH_MECHANISM,
      new ScriptableParser<>(new StringParser("mongodb auth mechanism")), null);

  private static final AttrInfo<Expression<Boolean>> CLEAN_ATT_INFO = new ScriptableBooleanAttribute(
      ATT_CLEAN, false, SYN_MONGO_CLEAN, Boolean.TRUE);

  private static final AttrInfoSupport ATTR_INFO_SUPPORT =
      new AttrInfoSupport(SYN_MONGO_ILL_ATTR, new MongodbValidator(), ID_ATT_INFO, ENVIRONMENT_ATT_INFO, SYSTEM_ATT_INFO,
          HOST_ATT_INFO, PORT_ATT_INFO, DATABASE_ATT_INFO, USER_ATT_INFO, PASSWORD_ATT_INFO, CLEAN_ATT_INFO, AUTH_DB_ATT_INFO,
          AUTH_MECHANISM_ATT_INFO);

  public MongoDBParser() {
    super(EL_MONGO_DB, ATTR_INFO_SUPPORT, BeneratorRootStatement.class, IfStatement.class);
  }

  @Override
  public Statement doParse(Element element, Element[] parentXmlPath, Statement[] parentComponentPath, BeneratorParseContext context) {
    Expression<String> id = parseScriptableStringAttribute(ATT_ID, element);
    Expression<String> environment = ENVIRONMENT_ATT_INFO.parse(element);
    Expression<String> system = SYSTEM_ATT_INFO.parse(element);
    Expression<String> host = parseScriptableStringAttribute(ATT_HOST, element);
    Expression<Integer> port = parseIntAttribute(ATT_PORT, element);
    Expression<String> database = parseScriptableStringAttribute(ATT_DATABASE, element);
    Expression<String> user = parseScriptableStringAttribute(ATT_USER, element);
    Expression<String> password = parseScriptableStringAttribute(ATT_PASSWORD, element);
    //mongo db authdb
    Expression<String> authdb = parseScriptableStringAttribute(ATT_AUTH_DB, element);
    // mongo db authMechanism
    Expression<String> authMechanism = parseScriptableStringAttribute(ATT_AUTH_MECHANISM, element);
    Expression<Boolean> clean = parseBooleanExpressionAttribute(ATT_CLEAN, element);
    return new MongoDBStatement(id, environment, system, host, port, database, user, password, authdb, authMechanism, clean,
        context.getResourceManager());
  }

  static class MongodbValidator implements Validator<Element> {

    @Override
    public boolean valid(Element element) {
      boolean hasEnv = (ENVIRONMENT_ATT_INFO.parse(element) != null);
      boolean hasSys = (SYSTEM_ATT_INFO.parse(element) != null);
      if (hasEnv && !hasSys) {
        throw BeneratorExceptionFactory.getInstance().syntaxErrorForXmlElement(
            "environment specified but no system", null, SYN_MONGO_SYS, element);
      }
      if (!hasEnv && hasSys) {
        throw BeneratorExceptionFactory.getInstance().syntaxErrorForXmlElement(
            "system specified but no environment", null, SYN_MONGO_ENV, element);
      }
      return true;
    }
  }

}
