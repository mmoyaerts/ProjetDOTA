<template>
     <router-link to="/dashboard" class="btn-dashboard">
    Aller au dashboard
  </router-link>
  <div class="editor-container">
    <header class="editor-header">
      <h1>Éditeur de Draft</h1>
      <div class="top-buttons">
        <button @click="logout" class="btn-logout small-btn">Déconnexion</button>
      </div>
    </header>

    <!-- Édition manuelle ou live selon matchId -->
    <section class="team-section radiant">
      <h2>{{ radiantTeamName }}</h2>
      <div class="bans">
        <img
          v-for="bid in bansRadiant"
          :key="`rban-${bid}`"
          :src="`/heroes/${bid}.png`"
          class="ban-icon"
          :title="`Ban ID ${bid}`"
        />
      </div>
      <div class="slots">
        <div v-for="(slot, idx) in slotsRadiant" :key="idx" class="hero-slot">
          <div class="slot-border">
            <div class="slot-content">
              <template v-if="slot.id !== null">
                <img :src="`/heroes/${slot.id}.png`" :alt="slot.name" class="slot-image" />
                <div class="slot-info">
                  <span class="slot-name">{{ slot.name }}</span>
                  <span class="slot-winrate">({{ slot.winrate }} %)</span>
                </div>
              </template>
              <button
                v-else
                @click="openModal('Radiant', idx)"
                class="btn-import small-btn"
                v-if="!isLive"
              >
                Importer Héro
              </button>
            </div>
          </div>
          <p class="slot-owner">{{ slot.playerName }}</p>
        </div>
      </div>
    </section>

    <section class="team-section dire">
      <h2>{{ direTeamName }}</h2>
      <div class="bans">
        <img
          v-for="bid in bansDire"
          :key="`dban-${bid}`"
          :src="`/heroes/${bid}.png`"
          class="ban-icon"
          :title="`Ban ID ${bid}`"
        />
      </div>
      <div class="slots">
        <div v-for="(slot, idx) in slotsDire" :key="idx" class="hero-slot">
          <div class="slot-border">
            <div class="slot-content">
              <template v-if="slot.id !== null">
                <img :src="`/heroes/${slot.id}.png`" :alt="slot.name" class="slot-image" />
                <div class="slot-info">
                  <span class="slot-name">{{ slot.name }}</span>
                  <span class="slot-winrate">({{ slot.winrate }} %)</span>
                </div>
              </template>
              <button
                v-else
                @click="openModal('Dire', idx)"
                class="btn-import small-btn"
                v-if="!isLive"
              >
                Importer Héro
              </button>
            </div>
          </div>
          <p class="slot-owner">{{ slot.playerName }}</p>
        </div>
      </div>
    </section>

    <!-- Bouton IA pour manuel -->
    <button
      v-if="!isLive"
      @click="openWinrateModal"
      class="btn-send small-btn"
    >
      Envoyer à l’IA
    </button>

    <!-- Modal IA -->
    <div v-if="showWinrateModal" class="modal-backdrop">
      <div class="modal">
        <h3>Winrate des équipes</h3>
        <p><strong>{{ radiantTeamName }} :</strong> {{ radiantTeamWinrate }} %</p>
        <p><strong>{{ direTeamName }} :</strong> {{ direTeamWinrate }} %</p>
        <button @click="closeWinrateModal" class="btn-cancel small-btn">Fermer</button>
      </div>
    </div>

    <!-- Modal import héros -->
    <div v-if="showModal" class="modal-backdrop">
      <div class="modal">
        <button class="modal-close" @click="closeModal">×</button>
        <h3>Importer pour {{ currentTeam }} slot {{ currentSlot + 1 }}</h3>
        <ul class="hero-palette">
          <li
            v-for="hero in filteredHeroes"
            :key="hero.id"
            class="palette-item"
            @click="selectHero(hero)"
          >
            <img :src="`/heroes/${hero.id}.png`" :alt="hero.name" />
            <span>{{ hero.name }}</span>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const route = useRoute()
const matchId = route.params.matchId ? Number(route.params.matchId) : null
const isLive = !!matchId
const token = localStorage.getItem('jwt_token') || ''

// State
const heroes = ref([])
const slotsRadiant = ref(Array(5).fill().map(() => ({ id: null, name: '', winrate: 0, playerName: '' })))
const slotsDire  = ref(Array(5).fill().map(() => ({ id: null, name: '', winrate: 0, playerName: '' })))
const bansRadiant = ref([])
const bansDire    = ref([])
const radiantTeamName = ref('Radiant')
const direTeamName    = ref('Dire')

// Modals
const showModal = ref(false)
const currentTeam = ref('')
const currentSlot = ref(0)
const showWinrateModal = ref(false)

// Helpers
function randomWinrate() {
  return Math.floor(Math.random() * 101)
}
function heroName(id) {
  const h = heroes.value.find(h => h.id === id)
  return h ? h.name : `ID ${id}`
}

// Filter palette
const filteredHeroes = computed(() => {
  const taken = (currentTeam.value === 'Radiant' ? slotsRadiant.value : slotsDire.value)
    .map(s => s.name).filter(Boolean)
  return heroes.value.filter(h => !taken.includes(h.name))
})

// Winrate calculate
const radiantTeamWinrate = computed(() => {
  const arr = slotsRadiant.value.map(s => s.winrate)
  return arr.length ? Math.round(arr.reduce((a,b)=>a+b,0)/arr.length) : 0
})
const direTeamWinrate = computed(() => {
  return 100 - radiantTeamWinrate.value
})

// Update draft from live
function updateDraftFromMatch(data) {
  radiantTeamName.value = data.radiant_team.team_name
  direTeamName.value    = data.dire_team.team_name
  bansRadiant.value = data.radiant_bans.slice(0,7)
  bansDire.value    = data.dire_bans.slice(0,7)
  const playersRad = data.players.filter(p => p.team === 'Radiant').slice(0,5)
  const playersDi  = data.players.filter(p => p.team === 'Dire').slice(0,5)
  data.radiant_picks.slice(0,5).forEach((id,i) => {
    slotsRadiant.value[i] = {
      id,
      name: heroName(id),
      winrate: randomWinrate(),
      playerName: playersRad[i]?.name || ''
    }
  })
  data.dire_picks.slice(0,5).forEach((id,i) => {
    slotsDire.value[i] = {
      id,
      name: heroName(id),
      winrate: randomWinrate(),
      playerName: playersDi[i]?.name || ''
    }
  })
}

// Fetch heroes
async function fetchAllHeroes() {
  try {
    const res = await axios.get('http://localhost:8080/heroes', {
      headers: { Authorization: `Bearer ${token}` },
      withCredentials: true
    })
    const list = res.data._embedded?.heroes || []
    console.log(list)
    heroes.value = list.map(h => {
      const href = h._links?.self?.href || ''
      const id = h.id != null ? h.id : Number(href.split('/').pop())
      return { id, name: h.name }
    })
  } catch (e) {
    console.error('Erreur chargement héros :', e)
  }
}

// WebSockets producer & consumer
let producerSocket, consumerSocket
onMounted(async () => {
  await fetchAllHeroes()
  if (isLive) {
    producerSocket = new WebSocket(`ws://localhost:8080/ws/match-stream?script=producteurCurrentMatch.py`)
    consumerSocket = new WebSocket(`ws://localhost:8080/ws/match-stream?script=consumerCurrentMatch.py`)
    consumerSocket.onmessage = evt => {
      try {
        console.log("ok")
        const d = JSON.parse(evt.data)
        const msg = Array.isArray(d)
          ? d.find(m => m.match_id === matchId)
          : d.match_id === matchId && d
        if (msg) updateDraftFromMatch(msg)
      } catch {}
    }
  }
})
onBeforeUnmount(() => {
  producerSocket?.close()
  consumerSocket?.close()
})

// Actions
function openModal(team, idx) {
  currentTeam.value = team
  currentSlot.value = idx
  showModal.value = true
}
function closeModal() { showModal.value = false }
function selectHero(hero) {
  const slot = {
    id: hero.id,
    name: hero.name,
    winrate: randomWinrate(),
    playerName: ''
  }
  if (currentTeam.value === 'Radiant') slotsRadiant.value[currentSlot.value] = slot
  else slotsDire.value[currentSlot.value] = slot
  closeModal()
}
function openWinrateModal() { showWinrateModal.value = true }
function closeWinrateModal() { showWinrateModal.value = false }
function logout() { router.push({ name: 'Login' }) }
</script>

<style scoped>
.editor-container { padding: 1rem; background: #f3f4f6; min-height: 100vh; color: #000 }
.editor-header { display: flex; align-items: center; margin-bottom: 1.5rem }
.editor-header h1 { flex: 1; text-align: center }
.top-buttons .small-btn { background: #ef4444; color: #fff; border: none; padding: .5rem; cursor: pointer }
.team-section { border: 2px solid #9ca3af; padding: 1rem; margin-bottom: 1rem }
.team-section.radiant { border-color: #3b82f6 }
.team-section.dire { border-color: #ef4444 }
.team-section h2 { margin: 0 0 .5rem }
.bans { display: flex; gap: 4px; margin-bottom: .5rem }
.ban-icon { width: 24px; height: 24px; border-radius: 4px; cursor: help }
.slots { display: flex; gap: .5rem }
.hero-slot { flex: 1; text-align: center }
.slot-border { border: 2px dashed #6b7280; height: 150px; display: flex; align-items: center; justify-content: center }
.slot-image { width: 50px; height: 50px; border-radius: 4px }
.slot-info { margin-top: .5rem }
.slot-name { font-weight: bold }
.slot-winrate { font-size: .85rem; color: #555 }
.slot-owner { margin-top: .25rem; font-size: .75rem; color: #000 }
.btn-import, .btn-send, .btn-cancel { font-size: .75rem; padding: .25rem .5rem; cursor: pointer; border: none }
.btn-send { background: #374151; color: #fff; margin-top: 1rem }
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.modal {
  background: #fff;
  padding: 1rem;
  border-radius: 8px;
  /* Agrandir la fenêtre */
  width: 80vw;
  max-width: 800px;
  /* Limiter la hauteur et ajouter un ascenseur vertical */
  max-height: 80vh;
  overflow-y: auto;
  text-align: center;
  z-index: 1001;
}.modal-close { position: absolute; top: 8px; right: 8px; border: none; background: transparent; font-size: 1.25rem; cursor: pointer }
.hero-palette { list-style: none; padding: 0; display: grid; grid-template-columns: repeat(auto-fill,minmax(100px,1fr)); gap: .5rem; margin-top: 1rem }
.palette-item { border: 1px solid #ccc; border-radius: 4px; padding: .5rem; cursor: pointer; display: flex; flex-direction: column; align-items: center }
.palette-item img { width: 48px; height: 48px; margin-bottom: .25rem }
.palette-item span { font-size: .75rem; text-align: center }
</style>
