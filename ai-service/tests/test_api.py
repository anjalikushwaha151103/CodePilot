from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)

def test_analyze_valid_code():
    response = client.post("/api/v1/analyze", json={
        "language": "python",
        "code": "def hello(): pass"
    })
    assert response.status_code == 200
    data = response.json()
    assert data["language"] == "python"
    assert data["parseSuccessful"] is True
    assert data["complexityEstimate"] == "O(1)"
    assert data["functionCount"] == 1

def test_analyze_invalid_language():
    response = client.post("/api/v1/analyze", json={
        "language": "rust",
        "code": "fn main() {}"
    })
    assert response.status_code == 422 # Pydantic validation error

def test_tutor_endpoint_mock_provider():
    # Because LLM_PROVIDER is mock by default
    response = client.post("/api/v1/tutor", json={
        "language": "python",
        "code": "def hello(): pass",
        "hintLevel": 1
    })
    assert response.status_code == 200
    data = response.json()
    assert data["hintLevel"] == 1
    assert data["message"] == "This is a mock tutoring response."

def test_tutor_endpoint_timeout():
    response = client.post("/api/v1/tutor", json={
        "language": "python",
        "code": "def timeout(): pass", # Triggers mock provider timeout
        "hintLevel": 1
    })
    assert response.status_code == 504

def test_tutor_endpoint_malformed():
    response = client.post("/api/v1/tutor", json={
        "language": "python",
        "code": "def malformed(): pass", # Triggers mock provider ValueError
        "hintLevel": 1
    })
    assert response.status_code == 502
