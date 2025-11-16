// Example 1: Basic Functions and Variables
// This example demonstrates basic function definitions and variable usage

create function greet with parameters name as string that returns string
begin
    return "Hello, " plus name plus "!"
end

create function addNumbers with parameters a as number and b as number that returns number
begin
    return a plus b
end

create function isAdult with parameters age as number that returns boolean
begin
    return age is greater than or equal to 18
end

create variable message as string equals greet with "World"
create variable sum as number equals addNumbers with 5 and 3
create variable adult as boolean equals isAdult with 25

print message
print sum
print adult