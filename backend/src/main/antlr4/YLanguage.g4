grammar YLanguage;

// Parser Rules
program : statement+ EOF ;

statement : functionDeclaration
          | variableDeclaration
          | assignment
          | controlFlow
          | loopStatement
          | returnStatement
          | expressionStatement
          | moduleDeclaration
          | traitDeclaration
          | structureDeclaration
          | importStatement
          | tryStatement
          | matchStatement
          ;

functionDeclaration : 'create' 'function' identifier 'with' 'parameters' parameterList 'that' 'returns' type block
                    | 'create' 'async' 'function' identifier 'with' 'parameters' parameterList 'that' 'returns' type block
                    ;

parameterList : parameter ('and' parameter)* | 'nothing' ;

parameter : identifier 'as' type ;

block : 'begin' statement+ 'end' ;

variableDeclaration : 'create' 'variable' identifier 'as' type 'equals' expression ;

assignment : 'set' identifier 'equals' expression ;

controlFlow : 'if' expression block ('otherwise' block)? ;

loopStatement : 'loop' 'through' 'each' identifier 'in' expression block
               | 'loop' 'with' 'variable' identifier 'equals' expression 'while' expression 'increment' identifier block
               ;

returnStatement : 'return' expression ;

tryStatement : 'try' block ('catch' identifier 'as' type block)* ;

matchStatement : 'match' expression 'begin' matchCase+ 'end' ;

matchCase : 'case' matchPattern block ;

matchPattern : identifier 'as' type
               | identifier 'as' type 'with' identifier
               | identifier
               | 'range' expression 'to' expression
               | 'guard' expression 'where' expression
               ;

moduleDeclaration : 'module' identifier 'begin' statement+ 'end' ;

traitDeclaration : 'define' 'trait' identifier 'begin' functionSignature+ 'end' ;

structureDeclaration : 'create' 'structure' identifier ('with' 'generic' 'type' identifier)? ('that' 'implements' identifier)? 'begin' structureMember+ 'end' ;

structureMember : 'create' 'field' identifier 'as' type ';' ;

functionSignature : 'create' 'function' identifier 'with' 'parameters' parameterList 'that' 'returns' type ';' ;

importStatement : 'import' identifier ;

expressionStatement : expression ';' ;

expression : conditionalExpression ;

conditionalExpression : logicalOrExpression ('if' logicalOrExpression 'otherwise' conditionalExpression)? ;

logicalOrExpression : logicalAndExpression ('or' logicalAndExpression)* ;

logicalAndExpression : equalityExpression ('and' equalityExpression)* ;

equalityExpression : relationalExpression (('equals' | 'not' 'equals') relationalExpression)* ;

relationalExpression : additiveExpression (('is' 'greater' 'than' | 'is' 'less' 'than' | 'is' 'greater' 'than' 'or' 'equals' | 'is' 'less' 'than' 'or' 'equals') additiveExpression)* ;

additiveExpression : multiplicativeExpression (('plus' | 'minus') multiplicativeExpression)* ;

multiplicativeExpression : unaryExpression (('times' | 'divided' 'by' | 'modulo') unaryExpression)* ;

unaryExpression : 'not' unaryExpression
                | 'minus' unaryExpression
                | postfixExpression
                ;

postfixExpression : primaryExpression (memberAccess | functionCall | typeCast)* ;

primaryExpression : literal
                   | identifier
                   | listExpression
                   | mapExpression
                   | parenthesizedExpression
                   ;

literal : STRING
         | NUMBER
         | BOOLEAN
         | 'empty' 'list'
         | 'empty' 'map'
         ;

functionCall : identifier 'with' argumentList
             | identifier 'with' 'parameters' argumentList
             ;

argumentList : expression ('and' expression)* | 'nothing' ;

memberAccess : expression 'at' expression
             | expression 'dot' identifier
             ;

listExpression : 'list' 'of' expression ('and' expression)* ;

mapExpression : 'map' 'with' keyValuePair ('and' keyValuePair)* ;

keyValuePair : expression 'to' expression ;

typeCast : expression 'to' 'string'
         | expression 'to' 'number'
         | expression 'to' 'boolean'
         ;

parenthesizedExpression : '(' expression ')' ;

type : 'string'
     | 'number'
     | 'boolean'
     | 'nothing'
     | 'any'
     | 'list' 'of' type
     | 'map' 'of' type 'to' type
     | 'either' type 'or' type
     | 'function' 'that' 'takes' type 'and' 'returns' type
     | 'reference' 'to' type
     | 'box' 'of' type
     | 'lifetime' 'of' type
     | identifier
     | genericType
     ;

genericType : type '(' 'Yummy:' 'generic' 'type' identifier ')' ;

identifier : IDENTIFIER ;

lifetime : '\'' IDENTIFIER ;

// Lexer Rules
IDENTIFIER : [a-zA-Z_][a-zA-Z0-9_]* ;
STRING : '"' (~["\r\n])* '"' ;
NUMBER : '-'? [0-9]+ ('.' [0-9]+)? ;
BOOLEAN : 'true' | 'false' ;

// Keywords
CREATE : 'create' ;
FUNCTION : 'function' ;
WITH : 'with' ;
PARAMETERS : 'parameters' ;
THAT : 'that' ;
RETURNS : 'returns' ;
BEGIN : 'begin' ;
END : 'end' ;
VARIABLE : 'variable' ;
AS : 'as' ;
EQUALS : 'equals' ;
SET : 'set' ;
IF : 'if' ;
OTHERWISE : 'otherwise' ;
LOOP : 'loop' ;
THROUGH : 'through' ;
EACH : 'each' ;
IN : 'in' ;
WHILE : 'while' ;
INCREMENT : 'increment' ;
RETURN : 'return' ;
TRY : 'try' ;
CATCH : 'catch' ;
MATCH : 'match' ;
CASE : 'case' ;
MODULE : 'module' ;
DEFINE : 'define' ;
TRAIT : 'trait' ;
STRUCTURE : 'structure' ;
IMPORT : 'import' ;
ASYNC : 'async' ;
AWAIT : 'await' ;
EMPTY : 'empty' ;
LIST : 'list' ;
MAP : 'map' ;
NOT : 'not' ;
AND : 'and' ;
OR : 'or' ;
PLUS : 'plus' ;
MINUS : 'minus' ;
TIMES : 'times' ;
DIVIDED : 'divided' ;
BY : 'by' ;
MODULO : 'modulo' ;
IS : 'is' ;
GREATER : 'greater' ;
THAN : 'than' ;
LESS : 'less' ;
TO : 'to' ;
STRING_TYPE : 'string' ;
NUMBER_TYPE : 'number' ;
BOOLEAN_TYPE : 'boolean' ;
NOTHING_TYPE : 'nothing' ;
ANY_TYPE : 'any' ;
EITHER : 'either' ;

TAKES : 'takes' ;
REFERENCE : 'reference' ;
BOX : 'box' ;
OF : 'of' ;
YUMMY : 'Yummy' ;
GENERIC : 'generic' ;
TYPE : 'type' ;
DOT : 'dot' ;
AT : 'at' ;
BORROWS : 'borrows' ;
OWNS : 'owns' ;
LIFETIME : 'lifetime' ;
FIELD : 'field' ;
RANGE : 'range' ;
GUARD : 'guard' ;
WHERE : 'where' ;

// Whitespace and Comments
WS : [ \t\r\n]+ -> skip ;
COMMENT : '//' ~[\r\n]* -> skip ;
BLOCK_COMMENT : '/*' .*? '*/' -> skip ;
YUMMY_COMMENT : '(' 'Yummy:' (~[)])* ')' -> skip ;