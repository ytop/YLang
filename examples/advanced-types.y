// Example 4: Advanced Types and Generics
// This example demonstrates more advanced type features

create function createPair with parameters first as any and second as any that returns map of string to any
begin
    return map with "first" to first and "second" to second
end

create function processEither with parameters value as either string or number that returns string
begin
    return value
end

create function handleReference with parameters ref as reference to number that returns number
begin
    return ref
end

create variable pair as map of string to any equals createPair with "hello" and 42
create variable eitherValue as either string or number equals "test"
create variable numRef as reference to number equals 100

print pair
print processEither with eitherValue
print handleReference with numRef