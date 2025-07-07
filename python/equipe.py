import requests

# URL de l'API Steam pour les matchs pro en direct
url = "https://api.steampowered.com/IDOTA2Match_570/GetLiveLeagueGames/v1/?key=DE92D8249135D52E0F232AB75B4C58D4"

# Appel à l'API
response = requests.get(url)
data = response.json()

# Extraction des équipes uniques
teams = {}
for match in data.get("result", {}).get("games", []):
    for team_side in ["radiant_team", "dire_team"]:
        team = match.get(team_side, {})
        team_id = team.get("team_id")
        team_name = team.get("team_name")
        if team_id and team_name:
            teams[team_id] = team_name.replace("'", "''")  # gestion des apostrophes SQL

# Génération des requêtes INSERT SQL PostgreSQL
print("-- Requêtes INSERT INTO team(id_team, name)\n")
for team_id, team_name in teams.items():
    print(f"INSERT INTO team(id_team, name) VALUES ({team_id}, '{team_name}') ON CONFLICT (id_team) DO NOTHING;")
