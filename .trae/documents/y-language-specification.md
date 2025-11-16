# Y Language Specification Document

## 1. Language Philosophy

Y language is designed to be the most human-readable programming language ever created. Every construct reads like natural English while maintaining the precision and power of traditional programming languages. The language serves as a universal intermediate representation that eliminates ambiguity and ensures consistent translation across different AI models and compilers.

## 2. Core Syntax Principles

### 2.1 Natural Language Structure
- Sentences start with action verbs
- Parameters are introduced with "with parameters"
- Return types are specified with "that returns"
- Code blocks are delimited with "begin" and "end"
- Conditions use "if" and "otherwise" instead of "else"

### 2.2 Type System
Y language supports both explicit and implicit typing:
- Explicit: `create variable name as string equals "John"`
- Implicit: `create variable name equals "John"` (Yummy: type inferred as string)

### 2.3 Decorations (Yummy Syntax)
Technical specifications that cannot be expressed naturally use `(Yummy: description)` format:
```y
create function genericIdentity with parameters value as any (Yummy: generic type T) that returns any
begin
    return value
end
```

## 3. Language Constructs

### 3.1 Variable Declarations

**Y Language:**
```y
create variable message as string equals "Hello World"
create variable count as number equals 42
create variable isActive as boolean equals true
create variable items as list of string equals empty list
create variable mapping as map of string to number equals empty map
```

**Rust Translation:**
```rust
let message: String = String::from("Hello World");
let count: i64 = 42;
let is_active: bool = true;
let items: Vec<String> = Vec::new();
let mapping: HashMap<String, i64> = HashMap::new();
```

**TypeScript Translation:**
```typescript
const message: string = "Hello World";
const count: number = 42;
const isActive: boolean = true;
const items: string[] = [];
const mapping: Record<string, number> = {};
```

### 3.2 Function Declarations

**Y Language:**
```y
create function addNumbers with parameters a as number and b as number that returns number
begin
    return a plus b
end

create function greetUser with parameters name as string and age as number (Yummy: optional parameter) that returns string
begin
    return "Hello " plus name plus ", you are " plus age plus " years old"
end

create function processData with parameters data as list of number and callback as function that takes number and returns number that returns list of number
begin
    return data map with callback
end
```

**Rust Translation:**
```rust
pub fn add_numbers(a: i64, b: i64) -> i64 {
    return a + b;
}

pub fn greet_user(name: String, age: Option<i64>) -> String {
    return format!("Hello {}, you are {} years old", name, age.unwrap_or(0));
}

pub fn process_data(data: Vec<i64>, callback: fn(i64) -> i64) -> Vec<i64> {
    return data.into_iter().map(callback).collect();
}
```

**TypeScript Translation:**
```typescript
function addNumbers(a: number, b: number): number {
    return a + b;
}

function greetUser(name: string, age?: number): string {
    return `Hello ${name}, you are ${age || 0} years old`;
}

function processData(data: number[], callback: (item: number) => number): number[] {
    return data.map(callback);
}
```

### 3.3 Control Flow

**Y Language:**
```y
create function checkAge with parameters age as number that returns string
begin
    if age is greater than or equal to 18
    begin
        return "Adult"
    end
    otherwise
    begin
        return "Minor"
    end
end

create function findMax with parameters numbers as list of number that returns number
begin
    create variable max as number equals numbers at 0
    loop through each number in numbers
    begin
        if number is greater than max
        begin
            set max equals number
        end
    end
    return max
end
```

**Rust Translation:**
```rust
pub fn check_age(age: i64) -> String {
    if age >= 18 {
        return String::from("Adult");
    } else {
        return String::from("Minor");
    }
}

pub fn find_max(numbers: Vec<i64>) -> i64 {
    let mut max = numbers[0];
    for number in numbers {
        if number > max {
            max = number;
        }
    }
    return max;
}
```

**TypeScript Translation:**
```typescript
function checkAge(age: number): string {
    if (age >= 18) {
        return "Adult";
    } else {
        return "Minor";
    }
}

function findMax(numbers: number[]): number {
    let max = numbers[0];
    for (const number of numbers) {
        if (number > max) {
            max = number;
        }
    }
    return max;
}
```

### 3.4 Pattern Matching

**Y Language:**
```y
create function processResult with parameters result as either string or number that returns string
begin
    match result
    begin
        case value as string
        begin
            return "String: " plus value
        end
        case value as number
        begin
            return "Number: " plus value to string
        end
    end
end

create function handleStatus with parameters status as Status enum that returns string
begin
    match status
    begin
        case Status Pending
            return "Processing..."
        case Status Success
            return "Completed!"
        case Status Error with message
            return "Failed: " plus message
    end
end
```

### 3.5 Error Handling

**Y Language:**
```y
create function divideNumbers with parameters a as number and b as number that returns either number or string
begin
    try
    begin
        if b equals 0
        begin
            throw "Division by zero error"
        end
        return a divided by b
    end
    catch error as string
    begin
        return "Error: " plus error
    end
end

create function readFile with parameters filename as string that returns either string or FileError (Yummy: Result type)
begin
    try
    begin
        create variable content as string equals read file filename
        return content
    end
    catch error as FileError
    begin
        return error
    end
end
```

### 3.6 Generic Programming

**Y Language:**
```y
create function identity with parameters value as any (Yummy: generic type T) that returns any (Yummy: returns T)
begin
    return value
end

create function filterList with parameters items as list of any (Yummy: generic type T) and predicate as function that takes T and returns boolean that returns list of any (Yummy: returns list of T)
begin
    return items where predicate
end

create structure Container with generic type T
begin
    create variable value as T
    
    create function get that returns T
    begin
        return value
    end
    
    create function set with parameters newValue as T
    begin
        set value equals newValue
    end
end
```

### 3.7 Trait/Interface Definitions

**Y Language:**
```y
define trait Drawable
begin
    create function draw that returns nothing
    create function area that returns number
end

define interface Comparable with generic type T
begin
    create function compare with parameters other as T that returns number
end

create structure Circle that implements Drawable
begin
    create variable radius as number
    
    create function draw that returns nothing
    begin
        print "Drawing circle with radius " plus radius
    end
    
    create function area that returns number
    begin
        return 3.14159 times radius times radius
    end
end
```

### 3.8 Module System

**Y Language:**
```y
module MathUtilities
begin
    export create function add with parameters a as number and b as number that returns number
    begin
        return a plus b
    end
    
    export create constant PI as number equals 3.14159
    
    create function helper that returns nothing (Yummy: private function)
    begin
        print "Helper function"
    end
end

import MathUtilities

create function calculateCircleArea with parameters radius as number that returns number
begin
    return MathUtilities PI times radius times radius
end
```

### 3.9 Async Programming

**Y Language:**
```y
create async function fetchData with parameters url as string that returns string
begin
    create variable response as string equals await make request to url
    return response
end

create function processAsync with parameters urls as list of string that returns list of string
begin
    create variable promises as list of promise of string equals empty list
    
    loop through each url in urls
    begin
        add fetchData with url to promises
    end
    
    return await all promises
end
```

### 3.10 Memory Management

**Y Language:**
```y
create function createBox with parameters value as number that returns box of number (Yummy: Box<i32> in Rust, reference in TypeScript)
begin
    return box of value
end

create function useReference with parameters value as reference to number that returns nothing (Yummy: &i32 in Rust)
begin
    print "Value: " plus value
end

create function transferOwnership with parameters data as vector of string that returns vector of string (Yummy: demonstrates move semantics)
begin
    return data
end
```

## 4. Advanced Features

### 4.1 Macro System

**Y Language:**
```y
define macro repeat with parameters count as number and action as block
begin
    loop with variable i equals 0 while i is less than count increment i
    begin
        execute action
    end
end

create function example that returns nothing
begin
    repeat 3 times
    begin
        print "Hello"
    end
end
```

### 4.2 Lifetime Annotations (Rust-specific)

**Y Language:**
```y
create function longestString with parameters first as reference to string (Yummy: lifetime 'a) and second as reference to string (Yummy: lifetime 'a) that returns reference to string (Yummy: lifetime 'a)
begin
    if length of first is greater than length of second
    begin
        return first
    end
    otherwise
    begin
        return second
    end
end
```

### 4.3 Advanced TypeScript Features

**Y Language:**
```y
create function createMappedType with parameters keys as union of string literals that returns object with keys as boolean
begin
    return create object from keys with value true
end

create type UserProfile equals object with properties
begin
    name as string
    age as number
    email as string (Yummy: template literal type)
    isActive as boolean
end
```

## 5. Grammar Specification (ANTLR 4)

```antlr
grammar YLanguage;

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
          ;

functionDeclaration : 'create' 'function' identifier 'with' 'parameters' parameterList 'that' 'returns' type block ;

parameterList : parameter ('and' parameter)* ;

parameter : identifier 'as' type ('(' 'Yummy:' description ')')? ;

block : 'begin' statement+ 'end' ;

variableDeclaration : 'create' 'variable' identifier 'as' type 'equals' expression ;

assignment : 'set' identifier 'equals' expression ;

controlFlow : 'if' expression block ('otherwise' block)? ;

loopStatement : 'loop' 'through' 'each' identifier 'in' expression block
               | 'loop' 'with' 'variable' identifier 'equals' expression 'while' expression 'increment' identifier block ;

returnStatement : 'return' expression ;

expression : literal
           | identifier
           | functionCall
           | binaryExpression
           | unaryExpression
           | conditionalExpression
           | matchExpression
           | tryExpression
           ;

binaryExpression : expression operator expression ;

operator : 'plus' | 'minus' | 'times' | 'divided by' | 'equals' | 'is greater than' | 'is less than' | 'and' | 'or' ;

literal : STRING | NUMBER | BOOLEAN | 'empty list' | 'empty map' ;

type : 'string' | 'number' | 'boolean' | 'list' 'of' type | 'map' 'of' type 'to' type | 'any' | 'nothing' | 'either' type 'or' type ;

identifier : IDENTIFIER ;

IDENTIFIER : [a-zA-Z_][a-zA-Z0-9_]* ;
STRING : '"' (~["\r\n])* '"' ;
NUMBER : '-'? [0-9]+ ('.' [0-9]+)? ;
BOOLEAN : 'true' | 'false' ;

WS : [ \t\r\n]+ -> skip ;
```

## 6. Translation Rules

### 6.1 Type Mapping

| Y Language | Rust | TypeScript |
|------------|------|------------|
| string | String | string |
| number | i64/f64 | number |
| boolean | bool | boolean |
| list of T | Vec<T> | T[] |
| map of K to V | HashMap<K, V> | Record<K, V> |
| either A or B | Result<A, B> / enum | A \| B |
| any | impl Trait / Box<dyn Any> | any |
| nothing | () | void |

### 6.2 Operator Mapping

| Y Language | Rust | TypeScript |
|------------|------|------------|
| plus | + | + |
| minus | - | - |
| times | * | * |
| divided by | / | / |
| equals | == | === |
| is greater than | > | > |
| is less than | < | < |
| and | && | && |
| or | \|\| | \|\| |

### 6.3 Control Flow Mapping

| Y Language | Rust | TypeScript |
|------------|------|------------|
| if/otherwise | if/else | if/else |
| loop through each | for x in iter | for (const x of iter) |
| loop with while | while | while |
| match | match | switch |
| try/catch | Result<T, E> / panic! | try/catch |

## 7. Error Handling Strategy

### 7.1 Lexical Errors
- Invalid tokens detected by lexer
- Unclosed strings or comments
- Invalid number formats

### 7.2 Syntax Errors
- Missing keywords or delimiters
- Invalid expression structures
- Type declaration errors

### 7.3 Semantic Errors
- Undefined variables or functions
- Type mismatches
- Invalid operations on types
- Scope violations

### 7.4 Translation Errors
- Unsupported language features
- Platform-specific limitations
- Performance optimization conflicts

## 8. Performance Considerations

### 8.1 Compilation Speed
- Incremental parsing for large files
- Parallel translation for multiple targets
- Caching of intermediate representations

### 8.2 Runtime Performance
- Zero-cost abstractions where possible
- Efficient memory layout
- Minimal runtime overhead

### 8.3 Memory Usage
- Streaming compilation for large projects
- Garbage collection optimization hints
- Memory pool allocation strategies

This specification provides the foundation for implementing a complete Y language compiler that can accurately translate human-readable code to production-ready Rust and TypeScript while maintaining all the advanced features of both languages.