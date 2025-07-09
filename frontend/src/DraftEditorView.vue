<template>
  <div class="editor-container">
    <header class="editor-header">
      <h1>Éditeur de Draft pour {{ proName }}</h1>
      <div class="top-buttons">
        <button @click="logout" class="btn-logout small-btn">Déconnexion</button>
      </div>
    </header>

    <!-- Radiant -->
    <section class="team-section radiant">
      <h2>{{ radiantTeamName }}</h2>
      <!-- Bans -->
      <div class="bans">
        <img
          v-for="bid in bansRadiant"
          :key="bid"
          :src="`/heroes/${bid}.png`"
          class="ban-icon"
          :title="`Ban ID ${bid}`"
        />
      </div>
      <div class="slots">
        <div
          v-for="(slot, idx) in slotsRadiant"
          :key="idx"
          class="hero-slot"
        >
          <div class="slot-border">
            <div class="slot-content">
              <template v-if="slot.id !== null">
                <img
                  :src="`/heroes/${slot.id}.png`"
                  :alt="slot.name"
                  class="slot-image"
                />
                <div class="slot-info">
                  <span class="slot-name">{{ slot.name }}</span>
                  <span class="slot-winrate">({{ slot.winrate }} %)</span>
                </div>
              </template>
              <button
                v-else
                @click="openModal('Radiant', idx)"
                class="btn-import small-btn"
                v-if="!matchId"
              >
                Importer Héros
              </button>
            </div>
          </div>
          <p class="slot-owner">{{ slot.playerName }}</p>
        </div>
      </div>
    </section>

    <!-- Dire -->
    <section class="team-section dire">
      <h2>{{ direTeamName }}</h2>
      <!-- Bans -->
      <div class="bans">
        <img
          v-for="bid in bansDire"
          :key="bid"
          :src="`/heroes/${bid}.png`"
          class="ban-icon"
          :title="`Ban ID ${bid}`"
        />
      </div>
      <div class="slots">
        <div
          v-for="(slot, idx) in slotsDire"
          :key="idx"
          class="hero-slot"
        >
          <div class="slot-border">
            <div class="slot-content">
              <template v-if="slot.id !== null">
                <img
                  :src="`/heroes/${slot.id}.png`"
                  :alt="slot.name"
                  class="slot-image"
                />
                <div class="slot-info">
                  <span class="slot-name">{{ slot.name }}</span>
                  <span class="slot-winrate">({{ slot.winrate }} %)</span>
                </div>
              </template>
              <button
                v-else
                @click="openModal('Dire', idx)"
                class="btn-import small-btn"
                v-if="!matchId"
              >
                Importer Héros
              </button>
            </div>
          </div>
          <p class="slot-owner">{{ slot.playerName }}</p>
        </div>
      </div>
    </section>

    <button @click="sendDraft" class="btn-send small-btn">Envoyer à l’IA</button>

    <!-- Modal de sélection de héros -->
    <div v-if="showModal" class="modal-backdrop">
      <div class="modal">
        <h3>Sélectionner un héros ({{ currentTeam }} slot {{ currentSlot + 1 }})</h3>
        <select v-model="selectedHero">
          <option disabled value="">-- Choisissez un héros --</option>
          <option v-for="name in availableHeroes" :key="name" :value="name">
            {{ name }}
          </option>
        </select>
        <div class="modal-actions">
          <button @click="confirmImport" class="btn-confirm small-btn">Valider</button>
          <button @click="closeModal" class="btn-cancel small-btn">Annuler</button>
        </div>
      </div>
    </div>

    <!-- Popup résumé winrates -->
    <div v-if="showSummary" class="modal-backdrop">
      <div class="modal">
        <h3>Résumé IA</h3>
        <p>Winrate Radiant : {{ radiantSummary }} %</p>
        <p>Winrate Dire    : {{ direSummary }} %</p>
        <div class="modal-actions">
          <button @click="closeSummary" class="btn-cancel small-btn">Fermer</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter, useRoute }            from 'vue-router'
import axios                              from 'axios'

// --- Router & params ---
const router  = useRouter()
const route   = useRoute()
const proId   = route.params.proId
const matchId = route.params.matchId ? Number(route.params.matchId) : null
const token   = localStorage.getItem('jwt_token') || ''

// --- State ---
const proName         = ref('…')
const heroes          = ref([])
const slotsRadiant    = ref(Array(5).fill().map(() => ({ id: null, name: '', winrate: 0, playerName: '' })))
const slotsDire       = ref(Array(5).fill().map(() => ({ id: null, name: '', winrate: 0, playerName: '' })))
const playersRadiant  = ref([])
const playersDire     = ref([])
const bansRadiant     = ref([])
const bansDire        = ref([])
const radiantTeamName = ref('Radiant')
const direTeamName    = ref('Dire')

const showModal    = ref(false)
const currentTeam  = ref('')
const currentSlot  = ref(0)
const selectedHero = ref('')

const showSummary    = ref(false)
const radiantSummary = ref(0)
const direSummary    = ref(0)

// --- Helpers ---
function randomWinrate() {
  return Math.floor(Math.random() * 101)
}
function heroName(id) {
  const h = heroes.value.find(h => h.id === id)
  return h ? h.name : `ID ${id}`
}

// --- Computed pour sélection manuelle ---
const availableHeroes = computed(() => {
  const taken = (currentTeam.value === 'Radiant' ? slotsRadiant.value : slotsDire.value)
    .map(s => s.name)
    .filter(Boolean)
  return heroes.value.map(h => h.name).filter(n => !taken.includes(n))
})

// --- Chargement de tous les héros ---
async function fetchAllHeroes() {
  try {
    const res = await axios.get('http://localhost:8080/heroes', {
      headers: { Authorization: `Bearer ${token}` },
      withCredentials: true
    })
    const embedded = res.data._embedded
    heroes.value = embedded.heroes.map(h => {
      const href = h._links.self.href
      const id   = parseInt(href.split('/').pop(), 10)
      return { id, name: h.name }
    })
  } catch (e) {
    console.error('Erreur chargement héros :', e)
  }
}

// --- Chargement du pro via son ID ---
async function fetchPro(id) {
  try {
    const res = await axios.get(`http://localhost:8080/pros/${id}`, {
      headers: { Authorization: `Bearer ${token}` },
      withCredentials: true
    })
    proName.value = res.data.pseudo
  } catch (e) {
    console.error('Erreur chargement pro :', e)
  }
}

// --- Mise à jour des slots, bans et joueurs à partir du match ---
function updateDraftFromMatch(data) {
  // équipes
  radiantTeamName.value = data.radiant_team.team_name
  direTeamName.value    = data.dire_team.team_name
  // bans
  bansRadiant.value = data.radiant_bans.slice(0,7)
  bansDire.value    = data.dire_bans.slice(0,7)
  // joueurs
  playersRadiant.value = data.players.filter(p => p.team === 'Radiant').slice(0,5)
  playersDire.value    = data.players.filter(p => p.team === 'Dire').slice(0,5)
  // picks
  data.radiant_picks.slice(0,5).forEach((id, i) => {
    slotsRadiant.value[i] = {
      id,
      name: heroName(id),
      winrate: randomWinrate(),
      playerName: playersRadiant.value[i]?.name || ''
    }
  })
  data.dire_picks.slice(0,5).forEach((id, i) => {
    slotsDire.value[i] = {
      id,
      name: heroName(id),
      winrate: randomWinrate(),
      playerName: playersDire.value[i]?.name || ''
    }
  })
}

// --- WebSockets producer & consumer ---
let producerSocket = null
let consumerSocket = null

onMounted(async () => {
  await fetchAllHeroes()
  producerSocket = new WebSocket(`ws://localhost:8080/ws/match-stream?script=producteurCurrentMatch.py`)
  consumerSocket = new WebSocket(`ws://localhost:8080/ws/match-stream?script=consumerCurrentMatch.py`)
  consumerSocket.onmessage = evt => {
    try {
      const data = JSON.parse(evt.data)
      const msg = Array.isArray(data)
        ? data.find(m => m.match_id === matchId)
        : data.match_id === matchId && data
      if (msg) updateDraftFromMatch(msg)
    } catch (err) {
      console.warn('Erreur parsing WS :', err)
    }
  }
})

onBeforeUnmount(() => {
  producerSocket?.close()
  consumerSocket?.close()
})

// --- UI Actions ---
function openModal(team, idx) {
  currentTeam.value   = team
  currentSlot.value   = idx
  selectedHero.value  = ''
  showModal.value     = true
}
function closeModal() { showModal.value = false }
function confirmImport() {
  const heroObj = heroes.value.find(h => h.name === selectedHero.value)
  if (!heroObj) return
  const slot = { id: heroObj.id, name: heroObj.name, winrate: randomWinrate(), playerName: proName.value }
  if (currentTeam.value === 'Radiant') slotsRadiant.value[currentSlot.value] = slot
  else slotsDire.value[currentSlot.value] = slot
  closeModal()
}

function sendDraft() {
  radiantSummary.value = slotsRadiant.value.reduce((s, x) => s + x.winrate, 0)
  direSummary.value    = slotsDire.value.reduce((s, x) => s + x.winrate, 0)
  showSummary.value    = true
}
function closeSummary() { showSummary.value = false }

function logout() { router.push({ name: 'Login' }) }
</script>

<style scoped>
.editor-container { padding: 1rem; background: #f3f4f6; min-height: 100vh; color: #000; }
.editor-header { display: flex; align-items: center; margin-bottom: 1.5rem; }
.editor-header h1 { flex: 1; text-align: center; }
.top-buttons .small-btn { color: #fff !important; }
.team-section { border: 2px solid #9ca3af; padding: 1rem; margin-bottom: 1rem; }
.team-section.radiant { border-color: #3b82f6; }
.team-section.dire    { border-color: #ef4444; }
.team-section h2     { color: #000; }
.bans { display: flex; gap: 4px; margin-bottom: 0.5rem; }
.ban-icon { width: 24px; height: 24px; border-radius: 4px; cursor: help; }
.slots { display: flex; gap: 0.5rem; }
.hero-slot { flex: 1; text-align: center; }
.slot-border { border: 2px dashed #6b7280; height: 150px; display: flex; align-items: center; justify-content: center; }
.slot-image { width: 50px; height: 50px; border-radius: 4px; }
.slot-info { margin-top: 0.5rem; }
.slot-name { font-weight: bold; }
.slot-winrate { font-size: 0.85rem; color: #555; }
.slot-owner { margin-top: 0.5rem; color: #000; font-size: 0.75rem; }
.btn-import, .btn-confirm, .btn-cancel, .btn-send { font-size: 0.75rem; padding: 0.25rem 0.5rem; cursor: pointer; }
.btn-send { background: #374151; color: #fff; border: none; margin-top: 1rem; }
.modal-backdrop { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; }
.modal { background: #fff; padding: 1rem; border-radius: 8px; width: 300px; color: #000; }
.modal-actions { display: flex; justify-content: flex-end; gap: 0.5rem; margin-top: 1rem; }
.btn-confirm { background: #10b981; color: #fff; border: none; }
.btn-cancel  { background: #ef4444; color: #fff; border: none; }
</style>
