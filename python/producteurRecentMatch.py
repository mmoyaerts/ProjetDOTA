from confluent_kafka import Producer
import requests
import json
import time
from datetime import datetime, timezone
import sys

#bootstrap_servers = "localhost:9092"
#topic = "recentGame"

#producer = Producer({"bootstrap.servers": bootstrap_servers})

def getVictory(matchId, heroId):
    url=f'https://api.opendota.com/api/matches/{matchId}'
    response = requests.get(url)
    
    if response.status_code == 200:
        data = response.json()
        picks_bans = data.get("picks_bans", [])
        
        for pick in picks_bans:
            if pick.get("hero_id") == heroId:
                is_radiant = pick.get("team") == 0  # 0 = Radiant, 1 = Dire
                return is_radiant   
        return None
    else:
        print(f"Erreur API: {response.status_code}")
        return None

def getStartMatch(start_time):
    dt_utc = datetime.fromtimestamp(start_time, tz=timezone.utc)
    return(dt_utc.strftime('%d-%m-%Y %H:%M:%S'))

def get_recent_matches(accountId):
    url = f'https://api.opendota.com/api/players/{accountId}/recentMatches'
    response = requests.get(url)
    if response.status_code == 200:
        data = response.json()  # Données JSON du match
        result = []
        for match in data:
            matchId = match.get("match_id")
            heroId = match.get("hero_id")
            kills = match.get("kills")
            deaths = match.get("deaths")
            assists = match.get("assists")
            gameMode = match.get("game_mode")
            radiantWin = match.get("radiant_win")
            team = getVictory(matchId, heroId)
            victory = (radiantWin == team)
            duration = match.get("duration")
            startTime = getStartMatch(match.get("start_time"))
            result.append({
                "match_id" : matchId,
                "hero_id": heroId,
                "kills": kills,
                "deaths" : deaths,
                "assists" : assists,
                "game_mode" : gameMode,
                "duration" : duration,
                "startTime" : startTime,
                "isRadian" : team,
                "victory" : victory})
        return result
    else:
        print(f"Erreur API: {response.status_code}")
        return None
    
#def send_to_kafka(data):
    #data_json = json.dumps(data)
    #producer.produce(topic, value=data_json)
    #producer.flush()

if len(sys.argv) < 2:
    print("❌ Usage: python script.py <accountId>")
    sys.exit(1)

try:
    accountId = int(sys.argv[1])
except ValueError:
    print("❌ accountId doit être un entier.")
    sys.exit(1)
seenMatches = set()

while True:
    try:
        games = get_recent_matches(accountId)
        if games:
            # Ne pas republier les anciens matchs déjà vus
            newGames = [g for g in games if g["match_id"] not in seenMatches]
            if newGames:
                #send_to_kafka(newGames)
                print(json.dumps(newGames))
                seenMatches.update(g["match_id"] for g in newGames)
                break
            else:
                continue
        else:
            print("Erreur ou aucun match trouvé.")
            break
    except Exception as e:
        print("Erreur pendant l'exécution :", e)

    time.sleep(5)