from confluent_kafka import Producer
import requests
import json
import time

STEAM_API_KEY = "DE92D8249135D52E0F232AB75B4C58D4"
bootstrap_servers = "localhost:9092"
topic = "currentGame"

producer = Producer({"bootstrap.servers": bootstrap_servers})

def get_live_matches():
    url = "https://api.steampowered.com/IDOTA2Match_570/GetLiveLeagueGames/v1/"
    params = {"key": STEAM_API_KEY}
    r = requests.get(url, params=params)
    r.raise_for_status()
    return r.json().get("result", {}).get("games", [])

def send_to_kafka(data):
    data_json = json.dumps(data)
    producer.produce(topic, value=data_json)
    producer.flush()

def formaterEnvoyer():
    matches = get_live_matches()
    if not matches:
        print("Aucun match live trouvé.")
        return
    
    all_matches_data = []

    for match in matches:
        match_data = {}
        match_data['match_id'] = match['match_id']

        radiant_team = match.get("radiant_team", {})
        dire_team = match.get("dire_team", {})

        match_data['radiant_team'] = {
            'team_name': radiant_team.get("team_name", "Radiant"),
            'team_id': radiant_team.get("team_id", None)
        }
        match_data['dire_team'] = {
            'team_name': dire_team.get("team_name", "Dire"),
            'team_id': dire_team.get("team_id", None)
        }

        players = match.get("players", [])
        players_data = []
        for player in players:
            if player.get("name")=="FACEIT.com":
                continue
            else :
                player_info = {
                    'account_id': player.get("account_id", None),
                    'name': player.get("name", None),
                    'hero_id': player.get("hero_id", 0),
                    'team': "Radiant" if player.get("team") == 0 else "Dire"
                }
                players_data.append(player_info)

        match_data['players'] = players_data

        scoreboard = match.get("scoreboard", {})

        radiant_side = scoreboard.get("radiant", {})
        dire_side = scoreboard.get("dire", {})

        radiant_picks = [p.get("hero_id") for p in radiant_side.get("picks", [])]
        radiant_bans = [b.get("hero_id") for b in radiant_side.get("bans", [])]

        dire_picks = [p.get("hero_id") for p in dire_side.get("picks", [])]
        dire_bans = [b.get("hero_id") for b in dire_side.get("bans", [])]

        # Ajoute au match_data
        match_data["radiant_picks"] = radiant_picks
        match_data["radiant_bans"] = radiant_bans
        match_data["dire_picks"] = dire_picks
        match_data["dire_bans"] = dire_bans

        all_matches_data.append(match_data)
    send_to_kafka(all_matches_data)

while True:
    try:
        formaterEnvoyer()
    except Exception as e:
        print("Erreur pendant l'exécution :", e)

    time.sleep(10)