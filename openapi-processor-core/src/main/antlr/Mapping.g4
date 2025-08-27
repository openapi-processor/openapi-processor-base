grammar Mapping;

/*
 * Parser Rules
 */


// root:
mapping
    : type | map | annotate | content | name
    ;

type
    : anyType
    ;

// basic type mapping
map
    : sourceType Arrow anyType
    ;

// content type mapping
content
    : contentType Arrow anyType
    ;

// annotation mapping
annotate
    : sourceType Annotate annotationType
    ;

// add, drop parameter
name
    : sourceType
    ;

anyType
    : plainType | primitiveType | targetType | FormatType | Primitive
    ;

plainType
    : Plain
    ;

primitiveType
    : Primitive | Primitive OpenArray CloseArray
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
    : (Identifier | Boolean | String | Number | QualifiedType | QualifiedTypeClass)
    ;

annotationParameterNamed
    : Identifier '=' (Identifier | Boolean | String | Number | QualifiedType | QualifiedTypeClass)
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
    : Identifier | ApiIdentifier | String | FormatType | Primitive
    ;

formatIdentifier
    : Identifier | ApiIdentifier | String | FormatType | Primitive
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

Primitive
    : 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' | 'boolean' | 'char'
    ;

Identifier
    : JavaLetter JavaLetterOrDigit* | FormatType | Primitive
    ;

QualifiedTypeClass
    : (Identifier) ('.' Identifier)* '.class'
    ;

QualifiedType
    : (Identifier | Package) ('.' Identifier)*
    ;

// Api types with optional format
FormatType
    : 'string' | 'integer' | 'number'
    ;

ApiIdentifier
  : ApiLetter (ApiLetterOrDigit | [ \t])+ ApiLetterOrDigit+ | FormatType | Primitive
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

fragment ApiLetter
    :  [a-zA-Z_-]
    ;

fragment ApiLetterOrDigit
    : [a-zA-Z0-9_-]
    ;
