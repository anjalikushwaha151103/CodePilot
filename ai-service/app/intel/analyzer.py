from collections import deque
from app.api.models import Language
from tree_sitter import Node

class ASTAnalyzer:
    def __init__(self, tree, language: Language):
        self.tree = tree
        self.language = language
        self.function_count = 0
        self.branch_count = 0
        self.loop_count = 0
        self.max_loop_depth = 0
        self.recursion_detected = False
        
        # Internal state for traversal
        self.current_function_name = None
        self.function_names = set()

    def analyze(self):
        self._find_function_names()
        self._walk(self.tree.root_node, loop_depth=0)

    def _find_function_names(self):
        # Quick pass to find all defined function names for recursion detection
        def traverse(node):
            if node.type in ["function_definition", "method_declaration", "function_declaration"]:
                for child in node.children:
                    if child.type == "identifier":
                        self.function_names.add(child.text.decode("utf8"))
            for child in node.children:
                traverse(child)
        traverse(self.tree.root_node)

    def _walk(self, node: Node, loop_depth: int):
        is_loop = node.type in [
            "for_statement", "while_statement", "do_statement", 
            "enhanced_for_statement", "for_in_statement"
        ]
        
        is_branch = node.type in [
            "if_statement", "switch_statement", "case_statement", "match_statement", "elif_clause"
        ]

        is_function = node.type in [
            "function_definition", "method_declaration", "function_declaration"
        ]
        
        is_call = node.type in ["call_expression", "method_invocation", "call"]

        if is_function:
            self.function_count += 1
            # Try to identify function name
            for child in node.children:
                if child.type == "identifier":
                    self.current_function_name = child.text.decode("utf8")
                    break

        if is_loop:
            self.loop_count += 1
            loop_depth += 1
            if loop_depth > self.max_loop_depth:
                self.max_loop_depth = loop_depth

        if is_branch:
            self.branch_count += 1

        if is_call and self.current_function_name:
            # Check if calling self
            for child in node.children:
                if child.type == "identifier" and child.text.decode("utf8") == self.current_function_name:
                    self.recursion_detected = True
                    break
                elif child.type == "field_access" or child.type == "attribute":
                    # For recursive methods like this.foo() or self.foo()
                    for sub in child.children:
                        if sub.type == "identifier" and sub.text.decode("utf8") == self.current_function_name:
                            self.recursion_detected = True
                            break

        for child in node.children:
            self._walk(child, loop_depth)

        if is_function:
            self.current_function_name = None
