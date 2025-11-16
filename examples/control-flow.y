// Example 2: Control Flow and Conditionals
// This example demonstrates if-else statements and control flow

create function checkGrade with parameters score as number that returns string
begin
    if score is greater than or equal to 90
    begin
        return "A"
    end
    otherwise if score is greater than or equal to 80
    begin
        return "B"
    end
    otherwise if score is greater than or equal to 70
    begin
        return "C"
    end
    otherwise
    begin
        return "F"
    end
end

create function maxOfThree with parameters a as number and b as number and c as number that returns number
begin
    create variable max as number equals a
    if b is greater than max
    begin
        set max equals b
    end
    if c is greater than max
    begin
        set max equals c
    end
    return max
end

print checkGrade with 85
print checkGrade with 92
print maxOfThree with 10 and 25 and 15