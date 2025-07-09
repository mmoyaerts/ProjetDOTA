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
              <button
                v-else
                @click="openModal('Radiant', idx)"
                class="btn-import small-btn"
              >
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
              <button
                v-else
                @click="openModal('Dire', idx)"
                class="btn-import small-btn"
              >
                Importer Héro
              </button>
            </div>
          </div>
          <p class="slot-label">HÉROS {{ idx + 1 }}</p>
        </div>
      </div>
    </section>

    <button @click="sendDraft" class="btn-send small-btn">Envoyer à l'IA</button>

    <!-- Modal d’import manuel -->
    <div v-if="showModal" class="modal-backdrop">
      <div class="modal">
        <h3>Sélectionner un héros ({{ currentTeam }} slot {{ currentSlot+1 }})</h3>
        <select v-model="selectedHero">
          <option disabled value="">-- Choisissez un héros --</option>
          <option v-for="hero in availableHeroes" :key="hero" :value="hero">
            {{ hero }}
          </option>
        </select>
        <div class="modal-actions">
          <button @click="confirmImport" class="btn-confirm small-btn">Valider</button>
          <button @click="closeModal"   class="btn-cancel small-btn">Annuler</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const route  = useRoute()
const matchId       = route.params.matchId
const initialMatch  = route.state?.match

// Liste de héros statique (ou à charger via REST)
const heroes = ref([
  { id: 1, name: 'Invoker' },
  { id: 2, name: 'Pudge' },
  { id: 3, name: 'Phantom Assassin' },
  { id: 4, name: 'Snapfire' },
  { id: 5, name: 'Earthshaker' },
])

// Draft slots
const slotsRadiant = ref(Array(5).fill().map(() => ({ hero:'', winrate:null })))
const slotsDire    = ref(Array(5).fill().map(() => ({ hero:'', winrate:null })))

const showModal    = ref(false)
const currentTeam  = ref('')
const currentSlot  = ref(0)
const selectedHero = ref('')

// Filtre des héros non pris
const availableHeroes = computed(() => {
  const taken = (currentTeam.value === 'Radiant'
    ? slotsRadiant.value
    : slotsDire.value
  ).map(s => s.hero).filter(Boolean)
  return heroes.value.map(h => h.name).filter(n => !taken.includes(n))
})

// Met à jour les slots d’après un objet match
function updateDraftFromMatch(data) {
  if (data.radiant_picks) {
    data.radiant_picks.slice(0,5).forEach((id,i) => {
      slotsRadiant.value[i] = { hero: heroName(id), winrate: null }
    })
  }
  if (data.dire_picks) {
    data.dire_picks.slice(0,5).forEach((id,i) => {
      slotsDire.value[i] = { hero: heroName(id), winrate: null }
    })
  }
}

onMounted(async () => {
  if (initialMatch) {
    // on a reçu l’objet complet via route.state
    updateDraftFromMatch(initialMatch)
  } else {
    // fallback REST si on a seulement l’ID
    try {
      const res = await axios.get(`http://localhost:8080/matches/${matchId}`)
      updateDraftFromMatch(res.data)
    } catch (e) {
      console.error('❌ Erreur chargement match via REST :', e)
    }
  }
})

// Modal & interactions
function openModal(team, idx) {
  currentTeam.value  = team
  currentSlot.value  = idx
  selectedHero.value = ''
  showModal.value    = true
}
function closeModal() {
  showModal.value = false
}
function confirmImport() {
  if (!selectedHero.value) return
  const winrate = Math.floor(Math.random()*101)
  const slotObj = { hero: selectedHero.value, winrate }
  if (currentTeam.value==='Radiant') slotsRadiant.value[currentSlot.value]=slotObj
  else                               slotsDire.value[ currentSlot.value]=slotObj
  closeModal()
}
function sendDraft() {
  console.log('Draft envoyé →', {
    Radiant: slotsRadiant.value,
    Dire:    slotsDire.value
  })
}
function logout() {
  router.push({ name: 'Login' })
}

// Helpers pour nom et icône
function heroName(id) {
  const h = heroes.value.find(h=>h.id===id)
  return h ? h.name : `ID ${id}`
}
function heroLogo(name) {
  return `/assets/heroes/${name.toLowerCase().replace(/\s+/g,'-')}.png`
}
</script>

<style scoped>
.editor-container { padding:1rem; background:#f3f4f6; min-height:100vh; }
.editor-header { display:flex; align-items:center; margin-bottom:1.5rem; }
.editor-header h1 { flex:1; text-align:center; }
.top-buttons { display:flex; gap:.5rem; }
.btn-logout { background:#ef4444; color:#fff; border:none; cursor:pointer; }

/* Sections Radiant / Dire */
.team-section { border:2px solid #9ca3af; padding:1rem; margin-bottom:1rem; }
.slots { display:flex; gap:.5rem; }
.hero-slot { flex:1; text-align:center; }
.slot-border { border:2px dashed #6b7280; height:150px; display:flex; justify-content:center; align-items:center; }
.slot-image { width:50px; height:50px; border-radius:4px; }
.slot-label { margin-top:.5rem; font-size:.75rem; }
.btn-import, .btn-confirm, .btn-cancel, .btn-send { font-size:.75rem; padding:.25rem .5rem; cursor:pointer; }
.btn-send { background:#374151; color:#fff; border:none; margin-top:1rem; }

/* Modal */
.modal-backdrop { position:fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.5); display:flex; justify-content:center; align-items:center; }
.modal { background:#fff; padding:1rem; border-radius:8px; width:280px; }
.modal-actions { display:flex; justify-content:flex-end; gap:.5rem; margin-top:1rem; }
.btn-confirm { background:#10b981; color:#fff; border:none; }
.btn-cancel  { background:#ef4444; color:#fff; border:none; }
</style>
