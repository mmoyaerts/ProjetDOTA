<template>
  <div class="dashboard-container">
    <header class="dashboard-header">
      <h1>Tableau de bord</h1>
      <div class="top-buttons">
        <button @click="logout" class="btn-destructive">Déconnexion</button>
        <button @click="goToDraft" class="btn-primary">Éditeur de Draft</button>
      </div>
    </header>

    <section class="pro-grid-section">
      <h2>Choisir un pro</h2>
      <div class="pro-grid">
        <div
          v-for="pro in filteredPros"
          :key="pro.idSteam"
          class="pro-card"
          @click="goToPro(pro.idSteam)"
        >
          <h3 class="pro-name">{{ pro.pseudo }}</h3>
          <p class="pro-team">Équipe: {{ pro.teamName }}</p>
          <button class="btn-primary btn-small">Voir matchs</button>
        </div>
      </div>
      <p v-if="!filteredPros.length" class="no-pros">Aucun pro trouvé…</p>
    </section>

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
              class="clickable-row"
            >
              <td>{{ m.match_id }}</td>
              <td>{{ m.radiant_team.team_name }}</td>
              <td>{{ m.dire_team.team_name }}</td>
              <td class="picks-cell">
                <img
                  v-for="(id, i) in m.radiant_picks"
                  :key="`rpick-${m.match_id}-${i}`"
                  :src="`/heroes/${id}.png`"
                  class="pick-icon"
                />
              </td>
              <td class="picks-cell">
                <img
                  v-for="(id, i) in m.dire_picks"
                  :key="`dpick-${m.match_id}-${i}`"
                  :src="`/heroes/${id}.png`"
                  class="pick-icon"
                />
              </td>
              <td class="bans-cell">
                <img
                  v-for="(id, i) in m.radiant_bans"
                  :key="`rban-${m.match_id}-${i}`"
                  :src="`/heroes/${id}.png`"
                  class="ban-icon"
                />
              </td>
              <td class="bans-cell">
                <img
                  v-for="(id, i) in m.dire_bans"
                  :key="`dban-${m.match_id}-${i}`"
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
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const pros = ref([])
const search = ref('')
const liveMatches = ref([])
let prodSocket = null
let liveSocket = null
const token = localStorage.getItem('jwt_token') || ''

onMounted(async () => {
  // Liste des pros
  try {
    const res = await axios.get('http://localhost:8080/pros', {
      headers: { Authorization: `Bearer ${token}` },
      withCredentials: true
    })
    pros.value = res.data.map(p => ({
      idSteam: p.idSteam,
      pseudo: p.pseudo,
      teamName: p.team?.name || ''
    }))
  } catch (e) {
    console.error('Erreur récupération des pros :', e)
  }

  // WS producteur (non utilisé ici)
  prodSocket = new WebSocket('ws://localhost:8080/ws/match-stream?script=producteurCurrentMatch.py')
  prodSocket.onerror = err => console.error('WS producteur erreur', err)

  // WS consumer pour récupérer liveMatches
  liveSocket = new WebSocket('ws://localhost:8080/ws/match-stream?script=consumerCurrentMatch.py')
  liveSocket.onmessage = evt => {
    try {
      liveMatches.value = JSON.parse(evt.data)
    } catch {
      console.warn('Flux live non JSON', evt.data)
    }
  }
})

onBeforeUnmount(() => {
  prodSocket?.close()
  liveSocket?.close()
})

// Filtrage des pros
const filteredPros = computed(() =>
  pros.value.filter(p =>
    p.pseudo.toLowerCase().includes(search.value.toLowerCase())
  )
)

// Navigation
function goToPro(idSteam) {
  router.push({ name: 'ProMatches', params: { id: idSteam } })
}
function goToDraft() {
  router.push({ name: 'DraftEditor' })
}
function logout() {
  router.push({ name: 'Login' })
}
function goToDraftWithMatch(match) {
  router.push({
    name: 'DraftEditor',
    params: { matchId: match.match_id },
    state: { match }
  })
}

</script>

<style scoped>
h1 {
  color : white;

}

h2 {
  color: white;
}
.dashboard-container { padding:1rem; max-width:1200px; margin:0 auto; color:#111; }
.dashboard-header { display:flex; align-items:center; margin-bottom:2rem; }
.dashboard-header h1 { flex:1; text-align:center; }
.top-buttons { display:flex; gap:0.5rem; }
.btn-primary { background:#3b82f6; color:#fff; border:none; padding:.5rem 1rem; border-radius:.25rem; cursor:pointer; }
.btn-secondary { background:#6b7280; color:#fff; border:none; padding:.25rem .75rem; border-radius:.25rem; cursor:pointer; }
.btn-destructive { background:#ef4444; color:#fff; border:none; padding:.5rem 1rem; border-radius:.25rem; cursor:pointer; }
.btn-small { font-size:.75rem; padding:.25rem .5rem; }
.card { background:#fff; border-radius:.5rem; padding:1rem; margin-bottom:2rem; box-shadow:0 2px 8px rgba(0,0,0,0.05); }
.no-pros, .no-matches { margin-top:1rem; color:#888; font-style:italic; }
.pro-search { width:100%; padding:.5rem; margin-bottom:.75rem; border:1px solid #d1d5db; border-radius:.25rem; }
.pro-list-wrapper { max-height:300px; overflow-y:auto; }
.pro-list { list-style:none; margin:0; padding:0; }
.pro-item { display:flex; justify-content:space-between; align-items:center; padding:.75rem; border-bottom:1px solid #e5e7eb; cursor:pointer; transition:background .2s; }
.pro-item:hover { background:#f9fafb; }
.pro-name { font-weight:500; }
.pro-team { font-size:.75rem; color:#6b7280; }
.pro-grid { display:grid; grid-template-columns:repeat(auto-fill,minmax(180px,1fr)); gap:1rem; }
.pro-card { text-align:center; padding:1rem; border-radius:.5rem; background:#fff; box-shadow:0 2px 8px rgba(0,0,0,0.05); cursor:pointer; transition:transform .2s, box-shadow .2s; }
.pro-card:hover { transform:translateY(-4px); box-shadow:0 4px 12px rgba(0,0,0,0.1); }
.pro-avatar-wrapper { width:64px; height:64px; margin:0 auto .5rem; }
.pro-avatar { width:100%; height:100%; border-radius:50%; object-fit:cover; }
.table-wrapper { overflow-x:auto; margin-top:1rem; border-radius:8px; box-shadow:0 2px 8px rgba(0,0,0,0.05); }
.live-table { width:100%; border-collapse:collapse; }
.live-table th, .live-table td { padding:.75rem; border-bottom:1px solid #e2e8f0; text-align:left; vertical-align:top; }
.live-table th { background:#f5f5f5; position:sticky; top:0; z-index:2; font-weight:600; text-transform:uppercase; font-size:.85rem; color:#555; }
.pick-icon, .ban-icon { width:32px; height:32px; object-fit:contain; margin-right:4px; }
.players-list { list-style:none; margin:0; padding:0; }
.players-list li { display:flex; align-items:center; margin-bottom:4px; }
.player-icon { width:24px; height:24px; object-fit:contain; margin-right:6px; }
.clickable-row { cursor:pointer; transition:background .2s; }
.clickable-row:hover { background:#f0f0f0; }
</style>
