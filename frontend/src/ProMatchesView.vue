<template>
  <div class="container">
    <h1 class="title">Historique de {{ playerName }}</h1>

    <p v-if="games.length === 0" class="no-games">
      Aucun match trouvé pour ce joueur.
    </p>

    <RecentMatches
      v-if="playerId"
      :account-id="playerId"
      script-name="producteurRecentMatch.py"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute }    from 'vue-router'
import axios           from 'axios'
import RecentMatches   from './components/MatchHistory.vue'  // <- importe ton composant

const route      = useRoute()
const playerId   = route.params.id
const playerName = ref(`#${playerId}`)
const games      = ref([])

onMounted(async () => {
  if (!playerId) return

  try {
    const proRes = await axios.get(`/pros/${playerId}`)
    playerName.value = proRes.data.pseudo
  } catch (err) {
    console.error('Erreur récupération du pro :', err)
  }

  try {
    const pmRes = await axios.get(`/playermatches/pro/${playerId}/recent`)
    const body = pmRes.data
    games.value = Array.isArray(body.content) ? body.content : []
  } catch (err) {
    console.error('Erreur récupération des matchs :', err)
    games.value = []
  }
})

function formatDuration(sec) {
  return Math.floor(sec / 60)
}
function heroLogo(name) {
  return `/assets/heroes/${name
    .toLowerCase()
    .replace(/\s+/g, '-')
    .replace(/[^a-z0-9-]/g, '')}.png`
}
</script>

<style scoped>
.container    { max-width:800px; margin:2rem auto; padding:0 1rem; }
.title        { text-align:center; margin-bottom:1.5rem; font-size:2rem; }
.no-games     { text-align:center; color:#888; font-style:italic; }
.game-list    { list-style:none; padding:0; display:flex; flex-direction:column; gap:1rem; }
.game-item    { display:flex; align-items:center; padding:1rem; border-radius:8px; }
.game-item.win  { background:#dcfce7; }
.game-item.lose { background:#ffe4e6; }
.game-logo img   { width:60px; height:60px; border-radius:6px; margin-right:1rem; }
.game-info      { flex:1; display:flex; flex-wrap:wrap; gap:1rem; }
.game-id        { font-weight:bold; }
.game-result    { min-width:6rem; text-align:center; font-weight:bold; }
.game-item.win .game-result  { color:#059669; }
.game-item.lose .game-result { color:#dc2626; }
</style>
