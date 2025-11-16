// Example: Advanced Y Language Features
// This example demonstrates Rust and TypeScript specific features in Y language

// Rust-specific: Enum with pattern matching
create enum Result with generic type T and E
begin
    create variant Ok with field value as T
    create variant Err with field error as E
end

// TypeScript-specific: Interface declaration
create interface UserProfile
begin
    create field name as string
    create field age as number that is optional
    create field email as string that is readonly
end

// TypeScript-specific: Type alias
create type alias JSONValue equals either string or number or boolean or map of string to JSONValue or list of JSONValue

// Rust-specific: Implementation block
create implementation for Result that implements Display
create function fmt with parameters self as reference to Result and f as reference to Formatter that returns Result of fmt and Error
begin
    match self
    begin
        case Ok with value
        begin
            write f with "Ok: {}" and value
        end
        case Err with error  
        begin
            write f with "Error: {}" and error
        end
    end
end

// Rust-specific: Lifetime annotations
create function process_data with parameters data as reference to string that borrows lifetime 'a that returns string that borrows lifetime 'a
begin
    return concatenate data with " processed"
end

// Rust-specific: Ownership and borrowing
create function take_ownership with parameters value as string that returns string
begin
    return value
end

create function borrow_value with parameters value as reference to string that returns number
begin
    return length of value
end

// Advanced pattern matching with guards
create function classify_number with parameters n as number that returns string
begin
    match n
    begin
        case guard where n is less than 0
        begin
            return "negative"
        end
        case range 0 to 10
        begin
            return "small positive"
        end
        case guard where n is greater than 100
        begin
            return "large positive"
        end
        case default
        begin
            return "medium positive"
        end
    end
end

// Decorator/Attribute support (Yummy syntax)
create function important_function with parameters data as string that returns nothing (Yummy: This function is marked as #[deprecated] in Rust and @deprecated in TypeScript)
begin
    print "This function is deprecated"
end

// Test the features
create variable result as Result of string and string equals Ok with "Hello World"
create variable user as UserProfile equals map with "name" to "John" and "age" to 30 and "email" to "john@example.com"
create var json as JSONValue equals map with "type" to "user" and "data" to user

print classify_number with 5
print classify_number with 150
print borrow_value with "test"