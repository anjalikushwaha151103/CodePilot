from app.intel.analyzer import ASTAnalyzer

class ComplexityEstimator:
    @staticmethod
    def estimate(analyzer: ASTAnalyzer):
        evidence = []
        issues = []
        strengths = []
        
        complexity = "O(1)"
        confidence = 0.5
        
        if analyzer.function_count > 0:
            strengths.append("Code is modularized into functions.")
        else:
            issues.append("Missing function definitions. Code might be purely script-level.")
            
        if analyzer.branch_count > 10:
            issues.append("High cyclomatic complexity detected (many branches).")
            
        if analyzer.max_loop_depth == 1:
            complexity = "O(n)"
            confidence = 0.7
            evidence.append("Single loop depth detected.")
        elif analyzer.max_loop_depth == 2:
            complexity = "O(n^2)"
            confidence = 0.8
            evidence.append("Nested loop (depth 2) detected.")
            issues.append("Nested loops detected; ensure dataset constraints permit O(n^2) or consider optimizing.")
        elif analyzer.max_loop_depth >= 3:
            complexity = f"O(n^{analyzer.max_loop_depth})"
            confidence = 0.9
            evidence.append(f"Deeply nested loops (depth {analyzer.max_loop_depth}) detected.")
            issues.append("Extremely deep nesting. Risk of Time Limit Exceeded (TLE).")
            
        if analyzer.recursion_detected:
            # We can't strictly know if it's O(2^n) or O(n) without master theorem context
            # So we degrade confidence or change estimate
            if analyzer.max_loop_depth > 0:
                # Recursion inside a loop or loop inside recursion
                complexity = "UNKNOWN"
                confidence = 0.4
                evidence.append("Both loops and recursion detected. Complexity is non-trivial.")
            else:
                # Pure recursion
                evidence.append("Direct recursion detected.")
                issues.append("Potential recursion depth risk. Ensure base cases are robust.")
                
        # If no loops and no recursion, it's highly likely O(1)
        if analyzer.max_loop_depth == 0 and not analyzer.recursion_detected:
            complexity = "O(1)"
            confidence = 0.8
            evidence.append("No loops or recursion found.")
            
        return {
            "complexityEstimate": complexity,
            "complexityConfidence": confidence,
            "evidence": evidence,
            "issues": issues,
            "strengths": strengths
        }
