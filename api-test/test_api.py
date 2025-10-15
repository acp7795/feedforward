import requests
from datetime import datetime, timedelta
import json

BASE_URL = "http://localhost:8080"

# --- Create a session to persist cookies ---
session = requests.Session()


# ==============================
# AUTH ENDPOINTS
# ==============================
def register_user():
    data = {
        "username": "testuser",
        "email": "testuser@example.com",
        "password": "TestPassword123!",
        "firstName": "Test",
        "lastName": "User",
    }
    print("\n📌 Registering user...")
    response = session.post(f"{BASE_URL}/api/auth/register", json=data)
    try:
        resp_json = response.json()
    except ValueError:
        resp_json = response.text
    print(f"Status Code: {response.status_code}")
    print(f"Response: {json.dumps(resp_json, indent=4)}")
    return response


def login_user():
    data = {"username": "testuser", "password": "TestPassword123!"}
    print("\n🔑 Logging in...")
    response = session.post(f"{BASE_URL}/api/auth/login", json=data)
    try:
        resp_json = response.json()
    except ValueError:
        resp_json = response.text
    print(f"Status Code: {response.status_code}")
    print(f"Response: {json.dumps(resp_json, indent=4)}")
    return response


def get_current_user():
    print("\n👤 Fetching current user profile...")
    response = session.get(f"{BASE_URL}/api/auth/me")
    try:
        resp_json = response.json()
    except ValueError:
        resp_json = response.text
    print(f"Status Code: {response.status_code}")
    print(f"Response: {json.dumps(resp_json, indent=4)}")
    return response


def logout_user():
    print("\n🚪 Logging out...")
    response = session.post(f"{BASE_URL}/api/auth/logout")
    try:
        resp_json = response.json()
    except ValueError:
        resp_json = response.text
    print(f"Status Code: {response.status_code}")
    print(f"Response: {json.dumps(resp_json, indent=4)}")
    return response


# ==============================
# PLAY SESSION ENDPOINTS
# ==============================
def get_all_play_sessions():
    print("\n🎮 Retrieving all play sessions...")
    response = session.get(f"{BASE_URL}/api/play-sessions")
    try:
        resp_json = response.json()
    except ValueError:
        resp_json = response.text
    print(f"Status Code: {response.status_code}")
    print(f"Response: {json.dumps(resp_json, indent=4)}")
    return response


def get_play_session_by_id(session_id):
    print(f"\n🎮 Retrieving play session by ID: {session_id}...")
    response = session.get(f"{BASE_URL}/api/play-sessions/{session_id}")
    try:
        resp_json = response.json()
    except ValueError:
        resp_json = response.text
    print(f"Status Code: {response.status_code}")
    print(f"Response: {json.dumps(resp_json, indent=4)}")
    return response


def get_play_sessions_by_user(user_id):
    print(f"\n👤 Retrieving play sessions for user: {user_id}...")
    response = session.get(f"{BASE_URL}/api/play-sessions/user/{user_id}")
    try:
        resp_json = response.json()
    except ValueError:
        resp_json = response.text
    print(f"Status Code: {response.status_code}")
    print(f"Response: {json.dumps(resp_json, indent=4)}")
    return response


def get_play_sessions_by_game(game_id):
    print(f"\n🎮 Retrieving play sessions for game: {game_id}...")
    response = session.get(f"{BASE_URL}/api/play-sessions/game/{game_id}")
    try:
        resp_json = response.json()
    except ValueError:
        resp_json = response.text
    print(f"Status Code: {response.status_code}")
    print(f"Response: {json.dumps(resp_json, indent=4)}")
    return response


def get_play_sessions_between(start_datetime, end_datetime):
    print(
        f"\n⏱ Retrieving play sessions between {start_datetime} and {end_datetime}..."
    )
    params = {"start": start_datetime.isoformat(), "end": end_datetime.isoformat()}
    response = session.get(f"{BASE_URL}/api/play-sessions/between", params=params)
    try:
        resp_json = response.json()
    except ValueError:
        resp_json = response.text
    print(f"Status Code: {response.status_code}")
    print(f"Response: {json.dumps(resp_json, indent=4)}")
    return response


def create_play_session(user_id, game_id):
    print(f"\n➕ Creating new play session for user {user_id}, game {game_id}...")
    data = {
        "userId": user_id,
        "gameId": game_id,
        "datetimeOpened": datetime.now().isoformat(),
        "datetimeClosed": None,
        "durationMinutes": 0,
    }
    response = session.post(f"{BASE_URL}/api/play-sessions", json=data)
    try:
        resp_json = response.json()
    except ValueError:
        resp_json = response.text
    print(f"Status Code: {response.status_code}")
    print(f"Response: {json.dumps(resp_json, indent=4)}")
    return response


def delete_play_session(session_id):
    print(f"\n🗑 Deleting play session ID: {session_id}...")
    response = session.delete(f"{BASE_URL}/api/play-sessions/{session_id}")
    try:
        resp_json = response.json()
    except ValueError:
        resp_json = response.text
    print(f"Status Code: {response.status_code}")
    print(f"Response: {json.dumps(resp_json, indent=4)}")
    return response


# ==============================
# EXECUTION FLOW
# ==============================
if __name__ == "__main__":
    # Auth flow
    reg = register_user()
    login = login_user()
    me = get_current_user()

    # Play sessions flow
    user_id = me.json()["data"]["userId"] if me.status_code == 200 else "testuser123"
    new_session = create_play_session(user_id=user_id, game_id="game123")
    all_sessions = get_all_play_sessions()
    get_play_session_by_id(
        new_session.json()["data"]["id"] if new_session.status_code == 201 else "1"
    )
    get_play_sessions_by_user(user_id)
    get_play_sessions_by_game("game123")

    # Between dates example
    start = datetime.now() - timedelta(days=7)
    end = datetime.now()
    get_play_sessions_between(start, end)

    # Delete play session
    if new_session.status_code == 201:
        delete_play_session(new_session.json()["data"]["id"])

    # Logout
    logout_user()

    print("\n✅ All API tests completed successfully!")
