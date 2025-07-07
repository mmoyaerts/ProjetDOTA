<template>
  <div class="editor-container">
    <header class="editor-header">
      <h1>Éditeur de Draft</h1>
      <div class="top-buttons">
        <button @click="logout" class="btn-logout small-btn">Déconnexion</button>
      </div>
    </header>

    <section class="team-section radiant">
      <h2>RADIANT</h2>
      <div class="slots">
        <div v-for="(slot, idx) in slotsRadiant" :key="idx" class="hero-slot">
          <div class="slot-border">
            <div class="slot-content">
              <template v-if="slot.hero">
                <img :src="heroLogo(slot.hero)" class="slot-image" />
                <p class="slot-label">{{ slot.hero }} ({{ slot.winrate }}%)</p>
              </template>
              <button v-else @click="openModal('Radiant', idx)" class="btn-import small-btn">
                Importer Héro
              </button>
            </div>
          </div>
          <p class="slot-label">HÉROS {{ idx + 1 }}</p>
        </div>
      </div>
    </section>

    <section class="team-section dire">
      <h2>DIRE</h2>
      <div class="slots">
        <div v-for="(slot, idx) in slotsDire" :key="idx" class="hero-slot">
          <div class="slot-border">
            <div class="slot-content">
              <template v-if="slot.hero">
                <img :src="heroLogo(slot.hero)" class="slot-image" />
                <p class="slot-label">{{ slot.hero }} ({{ slot.winrate }}%)</p>
              </template>
              <button v-else @click="openModal('Dire', idx)" class="btn-import small-btn">
                Importer Héro
              </button>
            </div>
          </div>
          <p class="slot-label">HÉROS {{ idx + 1 }}</p>
        </div>
      </div>
    </section>

    <button @click="sendDraft" class="btn-send small-btn">Envoyer à l'IA</button>

    <div v-if="showModal" class="modal-backdrop">
      <div class="modal">
        <h3>Sélectionner un héros ({{ currentTeam }} slot {{ currentSlot + 1 }})</h3>
        <select v-model="selectedHero">
          <option disabled value="">-- Choisissez un héros --</option>
          <option v-for="hero in availableHeroes" :key="hero" :value="hero">{{ hero }}</option>
        </select>
        <div class="modal-actions">
          <button @click="confirmImport" class="btn-confirm small-btn">Valider</button>
          <button @click="closeModal" class="btn-cancel small-btn">Annuler</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'

// Router
const router = useRouter()
const route = useRoute()

// Match ID from route
const matchId = route.params.matchId || null

// Hero list (static or fetched)
const heroes = ref([
  { id: 1, name: 'Invoker' },
  { id: 2, name: 'Pudge' },
  { id: 3, name: 'Phantom Assassin' },
  { id: 4, name: 'Snapfire' },
  { id: 5, name: 'Earthshaker' }
])

// Draft slots state
const slotsRadiant = ref(Array(5).fill().map(() => ({ hero: '', winrate: null })))
const slotsDire    = ref(Array(5).fill().map(() => ({ hero: '', winrate: null })))

// Modal state
const showModal    = ref(false)
const currentTeam  = ref('')
const currentSlot  = ref(0)
const selectedHero = ref('')

// Compute available heroes per team
const availableHeroes = computed(() => {
  const taken = currentTeam.value === 'Radiant'
    ? slotsRadiant.value.map(s => s.hero).filter(Boolean)
    : slotsDire.value.map(s => s.hero).filter(Boolean)
  return heroes.value.map(h => h.name).filter(n => !taken.includes(n))
})

// WebSocket instance
let socket = null

// Function to update slots from match data
function updateOrAddScript(message) {
  if (!message.radiant_picks || !message.dire_picks) {
    console.warn('Pas de picks dans le message', message)
    return
  }
  message.radiant_picks.slice(0, 5).forEach((id, i) => {
    slotsRadiant.value[i] = { hero: heroName(id), winrate: null }
  })
  message.dire_picks.slice(0, 5).forEach((id, i) => {
    slotsDire.value[i] = { hero: heroName(id), winrate: null }
  })
}

// Lifecycle: on mount
onMounted(async () => {
  // Initial load via REST if matchId
  if (matchId) {
    try {
      const response = await axios.get(`/matches/${matchId}`)
      updateOrAddScript(response.data)
    } catch (err) {
      console.error('Erreur récupération match initial :', err)
    }
  }

  // Init WebSocket if matchId
  if (matchId) {
  const wsUrl = `/ws/match-stream?script=producteurCurrentMatch.py`
    socket = new WebSocket(wsUrl)
    socket.onmessage = event => {
      try {
        const msg = JSON.parse(event.data)
        if (msg.match_id === matchId) updateOrAddScript(msg)
      } catch (e) {
        console.error('❌ Erreur parsing JSON :', e)
      }
    }
    socket.onopen = () => console.log('✅ WS connecté pour match', matchId)
    socket.onerror = e => console.error('❌ WS erreur :', e)
    socket.onclose = () => console.log('🔌 WS fermé')
  }
})

// Lifecycle: before unmount
onBeforeUnmount(() => {
  socket && socket.close()
})

// User interactions
function openModal(team, idx) {
  currentTeam.value = team
  currentSlot.value = idx
  selectedHero.value = ''
  showModal.value = true
}
function closeModal() { showModal.value = false }
function confirmImport() {
  if (!selectedHero.value) return
  const winrate = Math.floor(Math.random() * 101)
  const slotObj = { hero: selectedHero.value, winrate }
  if (currentTeam.value === 'Radiant') slotsRadiant.value[currentSlot.value] = slotObj
  else slotsDire.value[currentSlot.value] = slotObj
  closeModal()
}
function sendDraft() {
  console.log('Draft envoyé', { Radiant: slotsRadiant.value, Dire: slotsDire.value })
}
function logout() { router.push({ name: 'Login' }) }

// Helpers
function heroName(id) {
  const h = heroes.value.find(h => h.id === id)
  return h ? h.name : `ID ${id}`
}
function heroLogo(name) {
  return `/assets/heroes/${name.toLowerCase().replace(/\s+/g,'-')}.png`
}
</script>

<style scoped>
.editor-container { padding: 1rem; background: #e5e7eb; min-height: 100vh }
.editor-header { display: flex; align-items: center; position: relative }
.editor-header h1 { flex:1; text-align:center; margin:0 }
.top-buttons { position:absolute; top:1rem; right:1rem; display:flex; gap:.5rem }
.team-section { border:2px solid #9ca3af; padding:1rem; margin:1rem 0 }
.slots { display:flex; gap:.5rem }
.hero-slot { flex:1; text-align:center }
.slot-border { border:2px dashed #6b7280; height:150px; display:flex; justify-content:center; align-items:center }
.slot-image { width:50px; height:50px; border-radius:4px }
.slot-label { margin-top:.5rem; font-size:.75rem }
.btn-import, .btn-confirm, .btn-cancel, .btn-send, .btn-logout { font-size:.75rem; padding:.25rem .25rem; }
.btn-send { background:#374151; color:#fff; border:none; cursor:pointer }
.modal-backdrop { position:fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.5); display:flex; justify-content:center; align-items:center }
.modal { background:#fff; padding:1.5rem; border-radius:8px; width:280px }
.modal-actions { display:flex; justify-content:flex-end; gap:.5rem; margin-top:1rem }
.btn-confirm { background:#10b981; color:#fff; border:none; cursor:pointer }
.btn-cancel { background:#ef4444; color:#fff; border:none; cursor:pointer }
</style>
