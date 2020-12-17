/*
 * (c) Copyright 2009 by Volker Bergmann. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted under the terms of the
 * GNU General Public License.
 *
 * For redistributing this software or a derivative work under a license other
 * than the GPL-compatible Free Software License as defined by the Free
 * Software Foundation or approved by OSI, you must first obtain a commercial
 * license to this software product from Volker Bergmann.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * WITHOUT A WARRANTY OF ANY KIND. ALL EXPRESS OR IMPLIED CONDITIONS,
 * REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE
 * HEREBY EXCLUDED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

grammar Benerator;

options {
    output=AST;
    backtrack=true;
    memoize=true;
}

@header {
	package com.rapiddweller.benerator.script;
}

@lexer::header{ 
	package com.rapiddweller.benerator.script;
}

@lexer::members {
	@Override
	public Token nextToken() {
		while (true) {
			state.token = null;
			state.channel = Token.DEFAULT_CHANNEL;
			state.tokenStartCharIndex = input.index();
			state.tokenStartCharPositionInLine = input.getCharPositionInLine();
			state.tokenStartLine = input.getLine();
			state.text = null;
			if ( input.LA(1)==CharStream.EOF ) {
				return Token.EOF_TOKEN;
			}
			try {
				mTokens();
				if ( state.token==null ) {
					emit();
				}
				else if ( state.token==Token.SKIP_TOKEN ) {
					continue;
				}
				return state.token;
			}
			catch (RecognitionException re) {
				reportError(re);
				throw new RuntimeException(getClass().getSimpleName() + " error", re); // or throw Error
			}
		}
	}

}

@members {
protected void mismatch(IntStream input, int ttype, BitSet follow)
  throws RecognitionException
{
  throw new MismatchedTokenException(ttype, input);
}

public Object recoverFromMismatchedSet(IntStream input, RecognitionException e, BitSet follow)
  throws RecognitionException
{
  throw e;
}
}

@rulecatch {
catch (RecognitionException e) {
  throw e;
}
}


/********************************************************************************************
                          Parser section
*********************************************************************************************/

weightedLiteralList
    :   weightedLiteral (','! weightedLiteral)*;
	
weightedLiteral
    :   literal ('^'^ expression)?;

transitionList
    :   transition (','! transition)*;

transition
    :   literal '->'^ literal ('^'! expression)?;

beanSpecList
    :   beanSpec (','! beanSpec)*;

beanSpec
    :   expression -> ^(BEANSPEC expression);

expression 
    :   conditionalExpression
    |   assignment
    ;

assignment
    :   qualifiedName '='^ expression;

conditionalExpression 
    :   conditionalOrExpression ('?'^ expression ':'! conditionalExpression)?
    ;
    
conditionalOrExpression 
    :   conditionalAndExpression ('||'^ conditionalAndExpression )*
    ;

conditionalAndExpression 
    :   inclusiveOrExpression ('&&'^ inclusiveOrExpression)*
    ;

inclusiveOrExpression 
    :   exclusiveOrExpression ('|'^ exclusiveOrExpression)*
    ;

exclusiveOrExpression
    :   andExpression ('^'^ andExpression)*
    ;

andExpression
    :   equalityExpression ('&'^ equalityExpression)*
    ;

equalityExpression 
    :   relationalExpression (('==' | '!=')^ relationalExpression)*
    ;

relationalExpression 
    :   shiftExpression (('<=' | '>=' | '<' | '>')^ shiftExpression)*
    ;

shiftExpression 
    :   additiveExpression (('<<' | '>>>' | '>>')^ additiveExpression)*
    ;

additiveExpression 
    :   multiplicativeExpression (('+' | '-')^ multiplicativeExpression)*
    ;

multiplicativeExpression 
    :   unaryExpression (('*' | '/' | '%')^ unaryExpression)*
    ;

// NOTE: for '+' and '-', if the next token is int or long interal, then it's not a unary expression.
//       it's a literal with signed value. INTLITERAL AND LONG LITERAL are added here for this.
//

unaryExpression 
    :   '-' castExpression -> ^(NEGATION castExpression)
    |   '~'^ castExpression
    |   '!'^ castExpression
    |   castExpression
    ;

castExpression 
    :   '(' type ')' postfixExpression -> ^(CAST type postfixExpression)
    |   postfixExpression
    ;

type
    :   qualifiedName -> ^(TYPE qualifiedName);

postfixExpression
    :   (primary -> primary)
        (
            '[' expression ']' -> ^(INDEX $postfixExpression expression)
        |   '.' IDENTIFIER arguments-> ^(SUBINVOCATION $postfixExpression IDENTIFIER arguments)
        |   '.' IDENTIFIER -> ^(FIELD $postfixExpression IDENTIFIER)
        )*
    ;

primary 
    :   '('! expression ')'!
    |   literal
    |	creator
    |   qualifiedName arguments -> ^(INVOCATION qualifiedName arguments)
    |   qualifiedName
    ;

creator
    :   'new' qualifiedName arguments -> ^(CONSTRUCTOR qualifiedName arguments)
    |   'new' qualifiedName '{' assignment (',' assignment)* '}' -> ^(BEAN qualifiedName assignment*)
    ;

arguments
    :   '(' (expression (',' expression)*)? ')' -> ^(ARGUMENTS expression*);
    
qualifiedName
	:   IDENTIFIER ('.' IDENTIFIER)* -> ^(QUALIFIEDNAME IDENTIFIER*)
	;

literal 
    :   INTLITERAL
    |   DECIMALLITERAL
    |   STRINGLITERAL
    |   BOOLEANLITERAL
    |   NULL
    ;

/********************************************************************************************
                  Lexer section
*********************************************************************************************/

fragment TYPE:;
fragment NEGATION:;
fragment INDEX:;
fragment FIELD:;
fragment ARGUMENTS:;
fragment CAST:;
fragment CONSTRUCTOR:;
fragment INVOCATION:;
fragment SUBINVOCATION:;
fragment QUALIFIEDNAME:;
fragment BEAN:;
fragment BEANSPEC:;

BOOLEANLITERAL
    :   'true'
    |   'false'
    ;
    
INTLITERAL
    :   '0' 
    |   '1'..'9' ('0'..'9')*    
    |   '0' ('0'..'7')+         
    |   HexPrefix HexDigit+        
    ;

fragment
HexPrefix
    :   '0x';
        
fragment
HexDigit
    :   ('0'..'9'|'a'..'f'|'A'..'F')
    ;

DECIMALLITERAL
    :   ('0' .. '9')+ '.' ('0' .. '9')* Exponent?  
    |   ('0' .. '9')+ Exponent  
    ;
        
fragment 
Exponent    
    :   ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ 
    ;

STRINGLITERAL
    :   '\'' ( EscapeSequence | ~( '\'' | '\\' | '\r' | '\n' ))* '\''
    ; 

fragment
EscapeSequence 
    :   '\\' (
                 'b' 
             |   't' 
             |   'n' 
             |   'f' 
             |   'r' 
             |   '\"' 
             |   '\'' 
             |   '\\' 
             |   ('0'..'3') ('0'..'7') ('0'..'7')
             |   ('0'..'7') ('0'..'7') 
             |   ('0'..'7')
             )          
;     

WS  :   ( ' ' | '\r' | '\t' | '\u000C' | '\n' ) 
            {
                skip();
            }          
    ;
    
COMMENT:   '/*' (options {greedy=false;} : . )* '*/';
    
LINE_COMMENT
    :   '//' ~('\n'|'\r')*  ('\r\n' | '\r' | '\n') 
            {
                skip();
            }
    |   '//' ~('\n'|'\r')*     // a line comment could appear at the end of the file without CR/LF
            {
                skip();
            }
    ;

NULL:     'null';
LPAREN:   '(';
RPAREN:   ')';
LBRACE:   '{';
RBRACE:   '}';
LBRACKET: '[';
RBRACKET: ']';
SEMI:   ';';
COMMA:  ',';
DOT :   '.';
EQ  :   '=';
BANG:   '!';
TILDE:  '~';
QUES:   '?';
COLON:  ':';
EQEQ:   '==';
AMPAMP: '&&';
BARBAR: '||';
PLUS:   '+';
SUB :   '-';
STAR:   '*';
SLASH:   '/';
AMP :   '&';
BAR :   '|';
CARET:   '^';
PERCENT: '%';
MONKEYS_AT: '@';
BANGEQ: '!=';
GT  :   '>';
SHIFT_RIGHT:   '>>';
SHIFT_RIGHT2:   '>>>';
SHIFT_LEFT:   '<<';
GE  :   '>=';
LT  :   '<';
LE  :   '<=';
ARROW:   '->';
              
IDENTIFIER
    :   IdentifierStart IdentifierPart*
    ;

fragment
IdentifierStart
    :   'A'..'Z'
    |	'a'..'z'
    |   '_'
    ;                
                       
fragment 
IdentifierPart
    :   'A'..'Z'
    |	'a'..'z'
    |   '_'
    |	'0'..'9'
    ;
