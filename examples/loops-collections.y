// Example 3: Loops and Collections
// This example demonstrates loops and working with collections

create function sumList with parameters numbers as list of number that returns number
begin
    create variable total as number equals 0
    loop through each number in numbers
    begin
        set total equals total plus number
    end
    return total
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

create function filterPositive with parameters numbers as list of number that returns list of number
begin
    create variable result as list of number equals empty list
    loop through each number in numbers
    begin
        if number is greater than 0
        begin
            set result equals result plus list of number
        end
    end
    return result
end

create variable numbers as list of number equals list of 5 and -2 and 8 and 3 and -1 and 7
print sumList with numbers
print findMax with numbers
print filterPositive with numbers