import requests

# URL de l'API Steam pour les matchs pro en direct
url = "https://api.steampowered.com/IDOTA2Match_570/GetLiveLeagueGames/v1/?key=DE92D8249135D52E0F232AB75B4C58D4"

# Appel à l'API
response = requests.get(url)
data = response.json()

sql_inserts = []

# La structure principale contient une liste de parties en cours dans data['result']['games']
games = data.get("result", {}).get("games", [])

for game in games:
    # Chaque game a potentiellement une équipe radiant et dire, avec leurs joueurs
    # Extraction des joueurs radiant
    radiant_team_id = game.get("radiant_team", {}).get("team_id")
    dire_team_id = game.get("dire_team", {}).get("team_id")
    if radiant_team_id=="none":
        radiant_team_id=0
    if dire_team_id=="none":
        dire_team_id=0
        
    players = game.get("players", [])
    for player in players:
        id_steam = player.get("account_id")
        pseudo = player.get("name") or 'Unknown'
        team = player.get("team")
        id_team=0
        if team==0:
            id_team=radiant_team_id
        if team ==1:
            id_team=dire_team_id

        if id_team!=0:
            pseudo_escaped = pseudo.replace("'", "''")
            sql = f"INSERT INTO pro (id_steam, pseudo, id_team) VALUES ({id_steam}, '{pseudo_escaped}', {id_team}) ON CONFLICT (id_steam) DO UPDATE SET pseudo = EXCLUDED.pseudo, id_team = EXCLUDED.id_team;"
            sql_inserts.append(sql)
            

# Affiche ou sauvegarde les requêtes
for query in sql_inserts:
    print(query)