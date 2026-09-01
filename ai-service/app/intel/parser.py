import tree_sitter_cpp
import tree_sitter_java
import tree_sitter_python
import tree_sitter_javascript
from tree_sitter import Language as TSLanguage, Parser
from app.api.models import Language

_LANGUAGES = {
    Language.CPP: TSLanguage(tree_sitter_cpp.language()),
    Language.JAVA: TSLanguage(tree_sitter_java.language()),
    Language.PYTHON: TSLanguage(tree_sitter_python.language()),
    Language.JAVASCRIPT: TSLanguage(tree_sitter_javascript.language()),
}

class CodeParser:
    @staticmethod
    def parse(code: str, language: Language):
        if language not in _LANGUAGES:
            raise ValueError(f"Language {language} is not supported.")
        
        parser = Parser(_LANGUAGES[language])
        tree = parser.parse(bytes(code, "utf8"))
        return tree
