<template>
  <div class="container">
    <h1 class="title">Historique de {{ playerName }}</h1>

    <p v-if="games.length === 0" class="no-games">
      Aucun match trouvé pour ce joueur.
    </p>

    <ul v-else class="game-list">
      <li
        v-for="pm in games"
        :key="pm.id.matchId"
        :class="['game-item', pm.win ? 'win' : 'lose']"
      >
        <div class="game-logo">
          <img
            :src="heroLogo(pm.hero.name)"
            :alt="pm.hero.name"
          />
        </div>
        <div class="game-info">
          <span class="game-id">#{{ pm.id.matchId }}</span>
          <span class="game-hero">{{ pm.hero.name }}</span>
          <span class="game-kda">
            KDA : {{ pm.kill }}/{{ pm.deaths }}/{{ pm.assists }}
          </span>
          <span class="game-duration">
            Durée : {{ formatDuration(pm.match.duration) }}m
          </span>
        </div>
        <div class="game-result">
          {{ pm.win ? 'VICTOIRE' : 'DÉFAITE' }}
        </div>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute }    from 'vue-router'
import axios           from 'axios'

// (Proxy Vite déjà en place pour /playermatches → localhost:8080)
const route      = useRoute()
const playerId   = route.params.id

const playerName = ref(`#${playerId}`)
const games      = ref([])

onMounted(async () => {
  if (!playerId) return

  // 1) récupérer le pseudo du pro
  try {
    const proRes = await axios.get(`/pros/${playerId}`)
    playerName.value = proRes.data.pseudo
  } catch (err) {
    console.error('Erreur récupération du pro :', err)
  }

  // 2) récupérer les PlayerMatch récents
  try {
    const pmRes = await axios.get(`/playermatches/pro/${playerId}/recent`)
    const body = pmRes.data
    games.value = Array.isArray(body.content)
      ? body.content
      : []
  } catch (err) {
    console.error('Erreur récupération des matchs du pro :', err)
    games.value = []
  }
})

function formatDuration(sec) {
  return Math.floor(sec / 60)
}

function heroLogo(name) {
  return `/assets/heroes/${
    name.toLowerCase()
        .replace(/\s+/g, '-')
        .replace(/[^a-z0-9-]/g, '')
  }.png`
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
