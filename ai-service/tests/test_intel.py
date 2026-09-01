import pytest
from app.api.models import Language
from app.intel.parser import CodeParser
from app.intel.analyzer import ASTAnalyzer
from app.intel.complexity import ComplexityEstimator

def test_parse_valid_python():
    code = "def hello():\n    print('world')\n"
    tree = CodeParser.parse(code, Language.PYTHON)
    assert tree is not None
    assert tree.root_node.type == "module"

def test_ast_analyzer_complexity_o1():
    code = "def add(a, b): return a + b"
    tree = CodeParser.parse(code, Language.PYTHON)
    analyzer = ASTAnalyzer(tree, Language.PYTHON)
    analyzer.analyze()
    
    assert analyzer.function_count == 1
    assert analyzer.loop_count == 0
    assert analyzer.branch_count == 0
    assert analyzer.max_loop_depth == 0
    assert not analyzer.recursion_detected
    
    heuristics = ComplexityEstimator.estimate(analyzer)
    assert heuristics["complexityEstimate"] == "O(1)"

def test_ast_analyzer_complexity_on2():
    code = """
def find_duplicates(arr):
    for i in range(len(arr)):
        for j in range(i+1, len(arr)):
            if arr[i] == arr[j]:
                return True
    return False
"""
    tree = CodeParser.parse(code, Language.PYTHON)
    analyzer = ASTAnalyzer(tree, Language.PYTHON)
    analyzer.analyze()
    
    assert analyzer.function_count == 1
    assert analyzer.loop_count == 2
    assert analyzer.branch_count == 1
    assert analyzer.max_loop_depth == 2
    
    heuristics = ComplexityEstimator.estimate(analyzer)
    assert heuristics["complexityEstimate"] == "O(n^2)"

def test_ast_analyzer_recursion():
    code = """
def factorial(n):
    if n <= 1:
        return 1
    return n * factorial(n - 1)
"""
    tree = CodeParser.parse(code, Language.PYTHON)
    analyzer = ASTAnalyzer(tree, Language.PYTHON)
    analyzer.analyze()
    
    assert analyzer.function_count == 1
    assert analyzer.branch_count == 1
    assert analyzer.recursion_detected
    
    heuristics = ComplexityEstimator.estimate(analyzer)
    assert "Direct recursion detected." in heuristics["evidence"]
