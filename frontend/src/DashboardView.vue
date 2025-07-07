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

    <!-- Matchs en cours -->
    <section class="matches-section">
      <h2>Matchs en cours</h2>
      <ul class="match-list">
        <li v-for="match in matches" :key="match.id" class="match-item">
          <router-link :to="{ name: 'DraftEditor', params: { matchId: match.id } }">
            <p><strong>#{{ match.id }}</strong> – Début : {{ formatDate(match.startTime) }}</p>
            <p>Durée actuelle : {{ formatDuration(match.duration) }}</p>
          </router-link>
        </li>
      </ul>
      <p v-if="matches.length === 0" class="no-matches">Pas de match en cours.</p>
    </section>

    <!-- Flux live WebSocket -->
    <section class="live-section">
      <h2>Mises à jour en direct</h2>
      <pre class="live-output">{{ liveUpdate }}</pre>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router     = useRouter()
const pros       = ref([])
const matches    = ref([])
const liveUpdate = ref('')
let socket       = null

onMounted(async () => {
  // 1) Charger la liste des pros (JSON simple via proxy Vite)
  try {
    const res = await axios.get('/pros')
    pros.value = res.data.map(p => ({
      id:   p.idSteam,
      name: p.pseudo
    }))
    console.log('Pros chargés :', pros.value)
  } catch (e) {
    console.error('❌ Erreur récupération des pros :', e)
  }

  // 2) Charger les matchs en cours
  try {
    const res = await axios.get('/matches/ongoing')
    const body = res.data
    matches.value = Array.isArray(body.content)
      ? body.content
      : (Array.isArray(body) ? body : [])
  } catch (e) {
    console.error('❌ Erreur récupération des matchs :', e)
  }

  // 3) Connexion WebSocket pour le live
  socket = new WebSocket('ws://localhost:8080/ws/match-stream?script=producteurCurrentMatch.py')
  socket.onopen    = () => console.log('✅ WS connecté')
  socket.onmessage = e => { liveUpdate.value = e.data }
  socket.onerror   = err => console.error('❌ WS erreur :', err)
  socket.onclose   = () => console.log('🔌 WS fermé')
})

onBeforeUnmount(() => {
  socket && socket.close()
})

// Navigation et actions
function logout() {
  router.push({ name: 'Login' })
}
function goToDraft() {
  router.push({ name: 'DraftEditor' })
}

// Helpers de format
function formatDate(iso) {
  return new Date(iso).toLocaleString()
}
function formatDuration(sec) {
  return `${Math.floor(sec / 60)}m`
}
</script>

<style scoped>
.dashboard-container {
  padding: 1rem;
  max-width: 800px;
  margin: 0 auto;
  color: #111;
}
.dashboard-header {
  display: flex;
  align-items: center;
  position: relative;
  margin-bottom: 2rem;
}
.dashboard-header h1 {
  flex: 1;
  text-align: center;
}
.top-buttons {
  position: absolute;
  top: 0;
  right: 0;
  display: flex;
  gap: 0.5rem;
}
.btn {
  font-size: 0.75rem;
  padding: 0.25rem 0.5rem;
}

/* Sélection du pro */
.pro-select {
  margin-bottom: 1.5rem;
}
.pro-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  list-style: none;
  padding: 0;
}
.btn-pro {
  background: #2563eb;
  color: #fff;
  border: none;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  cursor: pointer;
  text-decoration: none;
}
.no-pros {
  margin-top: 0.5rem;
  color: #888;
  font-style: italic;
}

/* Matchs en cours */
.matches-section {
  margin-bottom: 1.5rem;
}
.match-list {
  list-style: none;
  padding: 0;
}
.match-item {
  background: #f3f4f6;
  padding: 1rem;
  margin-bottom: 0.5rem;
  border-radius: 4px;
}
.no-matches {
  margin-top: 0.5rem;
  color: #888;
  font-style: italic;
}

/* Flux live */
.live-section {
  margin-bottom: 1.5rem;
}
.live-output {
  background: #e5e7eb;
  padding: 1rem;
  border-radius: 4px;
  min-height: 100px;
  overflow: auto;
  white-space: pre-wrap;
}
</style>
