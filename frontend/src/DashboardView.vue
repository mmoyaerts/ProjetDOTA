<template>
  <div class="dashboard-container">
    <header class="dashboard-header">
      <h1>Tableau de bord</h1>
      <div class="top-buttons">
        <button @click="logout" class="btn small-btn">Déconnexion</button>
        <button @click="goToDraft" class="btn small-btn">Éditeur de Draft</button>
      </div>
    </header>

    <!-- Sélection du pro -->
    <section class="pro-select">
      <h2>Choisissez un pro</h2>
      <ul class="pro-list">
        <li v-for="pro in pros" :key="pro.id">
          <router-link
            :to="{ name: 'ProMatches', params: { id: pro.id } }"
            class="btn-pro"
          >
            {{ pro.name }}
          </router-link>
        </li>
      </ul>
      <p v-if="pros.length === 0" class="no-pros">Aucun pro trouvé…</p>
    </section>

    <!-- Flux live WebSocket -->
    <section class="live-section">
      <h2>Matchs live</h2>
      <div class="table-wrapper" v-if="liveMatches.length">
        <table class="live-table">
          <thead>
            <tr>
              <th># Match</th>
              <th>Radiant</th>
              <th>Dire</th>
              <th>Picks R</th>
              <th>Picks D</th>
              <th>Bans R</th>
              <th>Bans D</th>
              <th>Joueurs R</th>
              <th>Joueurs D</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="m in liveMatches"
              :key="m.match_id"
              @click="goToDraftWithMatch(m)"
              class="live-row"
            >
              <td>{{ m.match_id }}</td>
              <td>{{ m.radiant_team.team_name }}</td>
              <td>{{ m.dire_team.team_name }}</td>
              <td class="picks-cell">
                <img
                  v-for="id in m.radiant_picks"
                  :key="`rpick-${m.match_id}-${id}`"
                  :src="`/heroes/${id}.png`"
                  class="pick-icon"
                />
              </td>
              <td class="picks-cell">
                <img
                  v-for="id in m.dire_picks"
                  :key="`dpick-${m.match_id}-${id}`"
                  :src="`/heroes/${id}.png`"
                  class="pick-icon"
                />
              </td>
              <td class="bans-cell">
                <img
                  v-for="id in m.radiant_bans"
                  :key="`rban-${m.match_id}-${id}`"
                  :src="`/heroes/${id}.png`"
                  class="ban-icon"
                />
              </td>
              <td class="bans-cell">
                <img
                  v-for="id in m.dire_bans"
                  :key="`dban-${m.match_id}-${id}`"
                  :src="`/heroes/${id}.png`"
                  class="ban-icon"
                />
              </td>
              <td>
                <ul class="players-list">
                  <li
                    v-for="p in m.players.filter(pl => pl.team === 'Radiant')"
                    :key="`rplayer-${p.account_id}`"
                  >
                    <img
                      :src="`/heroes/${p.hero_id}.png`"
                      class="player-icon"
                    />
                    {{ p.name }}
                  </li>
                </ul>
              </td>
              <td>
                <ul class="players-list">
                  <li
                    v-for="p in m.players.filter(pl => pl.team === 'Dire')"
                    :key="`dplayer-${p.account_id}`"
                  >
                    <img
                      :src="`/heroes/${p.hero_id}.png`"
                      class="player-icon"
                    />
                    {{ p.name }}
                  </li>
                </ul>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <p v-else class="no-matches">Pas de match live pour le moment.</p>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router      = useRouter()
const pros        = ref([])
const matches     = ref([])
const liveMatches = ref([])

let prodSocket = null
let liveSocket = null

onMounted(async () => {
  // Liste des pros
  try {
  const token = localStorage.getItem('jwt_token') // récupère le token JWT

  const res = await axios.get('http://localhost:8080/pros', {
    headers: {
      Authorization: `Bearer ${token}`
    },
    withCredentials: true
  })

    pros.value = res.data.map(p => ({ id: p.idSteam, name: p.pseudo }))
  } catch (e) {
    console.error('❌ Erreur récupération des pros :', e)
  }

  // WebSocket producteur (feed liveMatches)
  prodSocket = new WebSocket('ws://localhost:8080/ws/match-stream?script=producteurCurrentMatch.py')
  prodSocket.onopen  = () => console.log('✅ WS producteur connecté')
  prodSocket.onerror = err => console.error('❌ WS producteur erreur :', err)
  prodSocket.onclose = () => console.log('🔌 WS producteur fermé')

  // WebSocket consumer pour liveMatches
  liveSocket = new WebSocket('ws://localhost:8080/ws/match-stream?script=consumerCurrentMatch.py')
  liveSocket.onopen    = () => console.log('✅ WS live connecté')
  liveSocket.onerror   = err => console.error('❌ WS live erreur :', err)
  liveSocket.onclose   = () => console.log('🔌 WS live fermé')
  liveSocket.onmessage = event => {
    try {
      liveMatches.value = JSON.parse(event.data)
    } catch {
      console.warn('⚠️ Donnée live non-JSON :', event.data)
    }
  }
})

onBeforeUnmount(() => {
  prodSocket && prodSocket.close()
  liveSocket && liveSocket.close()
})

// navigation
function logout() {
  router.push({ name: 'Login' })
}
function goToDraft() {
  router.push({ name: 'DraftEditor' })
}
// clique sur ligne live → envoie match complet
function goToDraftWithMatch(match) {
  router.push({
    name: 'DraftEditor',
    params:   { matchId: match.match_id },
    state:    { match }
  })
}

// Helpers
function formatDate(iso) {
  return new Date(iso).toLocaleString()
}
function formatDuration(sec) {
  return `${Math.floor(sec / 60)}m`
}
</script>

<style scoped>
.dashboard-container { padding:1rem; max-width:1200px; margin:0 auto; color:#111; }
.dashboard-header { display:flex; align-items:center; margin-bottom:2rem; }
.dashboard-header h1 { flex:1; text-align:center; }
.top-buttons { display:flex; gap:0.5rem; }
.btn { font-size:0.75rem; padding:0.25rem 0.5rem; }

/* … pro-select, matches-section existants … */

/* Live table */
.table-wrapper { overflow-x:auto; margin-top:1rem; border-radius:8px; box-shadow:0 2px 8px rgba(0,0,0,0.05); }
.live-table { width:100%; border-collapse:separate; border-spacing:0 10px; }
.live-table th, .live-table td { padding:0.75rem; text-align:left; vertical-align:top; }
.live-table th {
  background:#333; color:#fff;
  position:sticky; top:0; z-index:2;
  font-weight:600; text-transform:uppercase; font-size:0.85rem;
}
.live-row { cursor:pointer; }
.picks-cell img, .bans-cell img   { width:32px; height:32px; margin-right:4px; }
.player-icon                       { width:24px; height:24px; margin-right:6px; }
.players-list { list-style:none; margin:0; padding:0; }
.players-list li { display:flex; align-items:center; margin-bottom:4px; }
.no-matches { margin-top:1rem; color:#888; font-style:italic; }


.dashboard-container {
  padding: 1rem;
  max-width: 1200px;
  margin: 0 auto;
  color: #111;
}
.dashboard-header {
  display: flex;
  align-items: center;
  margin-bottom: 2rem;
}
.dashboard-header h1 {
  flex: 1;
  text-align: center;
}
.top-buttons {
  display: flex;
  gap: 0.5rem;
}
.btn {
  font-size: 0.75rem;
  padding: 0.25rem 0.5rem;
}

/* Pro-select & matches-section existants… */

.table-wrapper {
  overflow-x: auto;
  margin-top: 1rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}
.live-table {
  width: 100%;
  border-collapse: collapse;
}
.live-table th,
.live-table td {
  padding: 0.75rem;
  border-bottom: 1px solid #e2e8f0;
  text-align: left;
  vertical-align: top;
}
.live-table th {
  background: #f5f5f5;
  position: sticky;
  top: 0;
  z-index: 2;
  font-weight: 600;
  text-transform: uppercase;
  font-size: 0.85rem;
  color: #555;
}
.picks-cell img,
.bans-cell img {
  width: 32px;
  height: 32px;
  object-fit: contain;
  margin-right: 4px;
}
.player-icon {
  width: 24px;
  height: 24px;
  object-fit: contain;
  margin-right: 6px;
}
.players-list {
  list-style: none;
  margin: 0;
  padding: 0;
}
.players-list li {
  display: flex;
  align-items: center;
  margin-bottom: 4px;
}
.clickable-row {
  cursor: pointer;
  transition: background 0.2s;
}
.clickable-row:hover {
  background: #f0f0f0;
}
.no-matches, .no-pros {
  margin-top: 1rem;
  color: #888;
  font-style: italic;
}
</style>
