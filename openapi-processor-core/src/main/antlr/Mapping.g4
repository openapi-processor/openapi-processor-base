grammar Mapping;

/*
 * Parser Rules
 */


// root:
mapping
    : type | map | annotate | content
    ;

type
    : anyType
    ;

map
    : sourceType Arrow anyType
    ;

content
    : contentType Arrow anyType
    ;

annotate
    : sourceType Annotate annotationType
    ;

anyType
    : plainType | primitiveType | targetType
    ;

plainType
    : Plain
    ;

primitiveType
    : primitiveValue | primitiveValue OpenArray CloseArray
    ;

sourceType
    : sourceIdentifier (':' formatIdentifier)?
    ;

targetType
    : (annotationType)? qualifiedTargetType
    ;

annotationType
    : qualifiedType ('(' (annotationParameters)? ')')?
    ;

contentType
    : ContentType
    ;

annotationParameters
    : annotationParameterUnnamed | annotationParameterNamed (',' annotationParameterNamed)*
    ;

annotationParameterUnnamed
    : (Identifier | Boolean | String | Number | QualifiedTypeClass)
    ;

annotationParameterNamed
    : Identifier '=' (Identifier | Boolean | String | Number | QualifiedTypeClass)
    ;

qualifiedTargetType
    : qualifiedType
    ;

qualifiedType
    : QualifiedType ('<' genericParameters '>')?
    ;

genericParameters
    : (genericParameter | genericParameterAny) (',' genericParameter)*
    ;

genericParameter
    : QualifiedType ('<' genericParameters '>')?
    ;

genericParameterAny
    : GenericAny
    ;

sourceIdentifier
    :  Identifier | String
    ;

formatIdentifier
    : Identifier | Format | String | primitiveValue
    ;

primitiveValue
    : 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' | 'boolean' | 'char'
    ;


/*
 * Lexer Rules
 */

Arrow: '=>';
Annotate: '@';

Plain: 'plain';
Boolean: 'true' | 'false';
Package: '{package-name}';

DoubleQuote: '"';
GenericAny: '?';
OpenArray: '[';
CloseArray: ']';

Whitespace
  : [ \t] -> skip
  ;

Identifier
    : JavaLetter JavaLetterOrDigit*
    ;

QualifiedTypeClass
    : (Identifier) ('.' Identifier)* '.class'
    ;

QualifiedType
    : (Identifier | Package) ('.' Identifier)*
    ;

Format
    : FormatLetter FormatLetterOrDigit*
    ;

String
    : DoubleQuote ( ~["\\] | '\\' [\t\\"] )* DoubleQuote
    ;

MimeType
    : [a-zA-Z_] ([a-zA-Z0-9\\._-])*
    ;

MimeSubType
    : [a-zA-Z_] ([a-zA-Z0-9\\._-])*
    ;

ContentType
    : MimeType '/' MimeSubType
    ;

// "any" number format (we only want to split the parameters list)
Number
    : ([a-fA-FlLxX0-9\\._])+
    ;

fragment JavaLetter
    : [a-zA-Z$_]
    ;

fragment JavaLetterOrDigit
    : [a-zA-Z0-9$_]
    ;

fragment FormatLetter
    :  [a-zA-Z_-]
    ;

fragment FormatLetterOrDigit
    : [a-zA-Z0-9_-]
    ;
