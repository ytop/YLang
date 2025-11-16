package com.ylang.backend.service;

import com.ylang.backend.dto.CompileRequest;
import com.ylang.backend.dto.CompileResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CompilationServiceTest {

    @Autowired
    private CompilationService compilationService;

    @Test
    void testBasicCompilationToTypeScript() {
        String yCode = "create function greet with parameters name as string that returns string\n" +
                      "begin\n" +
                      "    return \"Hello \" plus name\n" +
                      "end";

        CompileResponse response = compilationService.compile(yCode, "typescript", "test");
        
        assertTrue(response.isSuccess(), "Compilation should succeed");
        assertNotNull(response.getCompiledCode(), "Compiled code should not be null");
        assertTrue(response.getCompiledCode().contains("function"), "Should contain function keyword");
        assertTrue(response.getCompiledCode().contains("greet"), "Should contain function name");
    }

    @Test
    void testBasicCompilationToRust() {
        String yCode = "create function addNumbers with parameters a as number and b as number that returns number\n" +
                      "begin\n" +
                      "    return a plus b\n" +
                      "end";

        CompileResponse response = compilationService.compile(yCode, "rust", "test");
        
        assertTrue(response.isSuccess(), "Compilation should succeed");
        assertNotNull(response.getCompiledCode(), "Compiled code should not be null");
        assertTrue(response.getCompiledCode().contains("fn"), "Should contain fn keyword");
        assertTrue(response.getCompiledCode().contains("add_numbers"), "Should contain function name");
    }

    @Test
    void testInvalidSyntax() {
        String invalidCode = "this is not valid Y language syntax";
        
        CompileResponse response = compilationService.compile(invalidCode, "typescript", "test");
        
        assertFalse(response.isSuccess(), "Compilation should fail for invalid syntax");
        assertNotNull(response.getErrors(), "Should have errors");
        assertFalse(response.getErrors().isEmpty(), "Should have at least one error");
    }

    @Test
    void testValidation() {
        String validCode = "create variable x as number equals 42";
        
        var response = compilationService.validate(validCode);
        
        assertTrue(response.isValid(), "Validation should succeed for valid code");
    }
}